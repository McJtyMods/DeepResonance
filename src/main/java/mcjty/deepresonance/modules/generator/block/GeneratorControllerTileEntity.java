package mcjty.deepresonance.modules.generator.block;

import mcjty.deepresonance.modules.generator.GeneratorModule;
import mcjty.deepresonance.modules.generator.data.DRGeneratorNetwork;
import mcjty.deepresonance.modules.generator.data.GeneratorBlob;
import mcjty.deepresonance.modules.generator.util.GeneratorConfig;
import mcjty.lib.multiblock.MultiblockDriver;
import mcjty.lib.multiblock.MultiblockSupport;
import mcjty.lib.tileentity.TickingTileEntity;
import mcjty.lib.varia.Broadcaster;
import mcjty.lib.varia.OrientationTools;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Set;

public class GeneratorControllerTileEntity extends TickingTileEntity {

    private int startup = 0;
    private int shutdown = 0;
    private boolean active = false;
    private boolean rsControlled = true;
    private boolean activated = false;

    public GeneratorControllerTileEntity() {
        super(GeneratorModule.TYPE_GENERATOR_CONTROLLER.get());
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket packet) {
        boolean working = isPowered();
        super.onDataPacket(net, packet);

        if (level.isClientSide()) {
            // If needed send a render update.
            if (isPowered() != working) {
                BlockState state = getBlockState();
                level.sendBlockUpdated(worldPosition, state, state, Constants.BlockFlags.DEFAULT);
            }
        }
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        if (level.isClientSide()) {
            stopSounds();
        }
    }

    @Override
    protected void tickServer() {
        boolean active1 = (rsControlled && powerLevel > 0) || (!rsControlled && activated);

        // @todo optimize this?
        boolean dirty = false;
        Set<Integer> networks = new HashSet<Integer>();
        for (Direction direction : OrientationTools.DIRECTION_VALUES) {
            BlockPos newC = getBlockPos().relative(direction);
            Block b = level.getBlockState(newC).getBlock();
            // @TODO WHAT IF WE HAVE MULTIPLE SEPARATE NETWORKS ADJACENT TO THE CONTROLLER? DON'T ALLOW!?
            if (b == GeneratorModule.GENERATOR_PART_BLOCK.get()) {
                GeneratorPartTileEntity generatorTileEntity = (GeneratorPartTileEntity) level.getBlockEntity(newC);
                int networkId = generatorTileEntity.getMultiblockId();
                if (networkId != -1 && !networks.contains(networkId)) {
                    networks.add(networkId);
                    if (active1) {
                        // Only activate with sufficient energy collectors.
                        int countCollectors = getCollectorBlocks(generatorTileEntity.getMultiblockId(), generatorTileEntity.getBlob(), newC);
                        if (countCollectors == 1) {
                            if (handleActivate(networkId, newC)) {
                                dirty = true;
                            }
                        } else {
                            if (countCollectors < 1) {
                                Broadcaster.broadcast(level, getBlockPos().getX(), getBlockPos().getY(), getBlockPos().getZ(), "There is no energy collector on this generator!", 100);
                            } else {
                                Broadcaster.broadcast(level, getBlockPos().getX(), getBlockPos().getY(), getBlockPos().getZ(), "There are too many energy collectors on this generator!!", 100);
                            }
                            if (handleDeactivate(networkId, newC)) {
                                dirty = true;
                            }
                        }
                    } else {
                        if (handleDeactivate(networkId, newC)) {
                            dirty = true;
                        }
                    }
                }
            }
        }
        if (dirty) {
            DRGeneratorNetwork generatorNetwork = DRGeneratorNetwork.getNetwork(level);
            generatorNetwork.save();
        }
    }

    @Override
    protected void tickClient() {
        // @todo 1.16
//        if (GeneratorConfiguration.baseGeneratorVolume < 0.01f) {
//            // No sounds.
//            return;
//        }
//        if (startup != 0) {
//            if (!GeneratorSoundController.isStartupPlaying(level, pos)) {
//                GeneratorSoundController.playStartup(level, pos);
//            }
//        } else if (shutdown != 0) {
//            if (!GeneratorSoundController.isShutdownPlaying(level, pos)) {
//                GeneratorSoundController.playShutdown(level, pos);
//            }
//        } else if (active) {
//            if (!GeneratorSoundController.isLoopPlaying(level, pos)) {
//                GeneratorSoundController.playLoop(level, pos);
//            }
//        } else {
//            stopSounds();
//        }
    }

    private void stopSounds() {
        // @todo 1.16
//        GeneratorSoundController.stopSound(level, getBlockPos());
    }

    private MultiblockDriver<GeneratorBlob> getDriver() {
        return DRGeneratorNetwork.getNetwork(level).getDriver();
    }


    private int getCollectorBlocks(int id, GeneratorBlob network, BlockPos p) {
        if (network.getCollectorBlocks() <= 0) {
            Set<BlockPos> positions = MultiblockSupport.findMultiblock(level, p, getDriver());
            int cnt = 0;
            for (BlockPos pos : positions) {
                if (level.getBlockEntity(pos.above()) instanceof EnergyCollectorTileEntity) {
                    cnt++;
                }
            }
            int finalCnt = cnt;
            getDriver().modify(id, holder -> holder.getMb().setCollectorBlocks(finalCnt));
        }
        return network.getCollectorBlocks();
    }

    private boolean handleActivate(int id, BlockPos coordinate) {
        DRGeneratorNetwork generatorNetwork = DRGeneratorNetwork.getNetwork(level);
        GeneratorBlob network = generatorNetwork.getOrCreateBlob(id);
        if (network.isActive() && network.getShutdownCounter() == 0) {
            return false; // Nothing to do.
        }
        startup = network.getStartupCounter();
        if (startup == 0) {
            startup = GeneratorConfig.STARTUP_TIME.get();
        }
        startup--;
        if (startup <= 0) {
            startup = 0;
            GeneratorPartTileEntity generatorTileEntity = (GeneratorPartTileEntity) level.getBlockEntity(coordinate);
            generatorTileEntity.activate(true);
        }
        active = network.isActive();
        shutdown = 0;
        network.setShutdownCounter(0);
        network.setStartupCounter(startup);
        setChanged();
        return true;
    }

    private boolean handleDeactivate(int id, BlockPos coordinate) {
        BlockState state = level.getBlockState(getBlockPos());
        DRGeneratorNetwork generatorNetwork = DRGeneratorNetwork.getNetwork(level);
        GeneratorBlob network = generatorNetwork.getOrCreateBlob(id);
        if ((!network.isActive()) && network.getShutdownCounter() == 0 && network.getStartupCounter() == 0) {
            if (network.getShutdownCounter() != shutdown || network.getStartupCounter() != startup || (network.isActive() != active)) {
                shutdown = network.getShutdownCounter();
                startup = network.getStartupCounter();
                active = network.isActive();
                setChanged();
            }
            return false;   // Nothing to do.
        }

        shutdown = network.getShutdownCounter();
        if (network.isActive() || network.getStartupCounter() != 0) {
            shutdown = GeneratorConfig.SHUTDOWN_TIME.get();
            GeneratorPartTileEntity generatorTileEntity = (GeneratorPartTileEntity) level.getBlockEntity(coordinate);
            generatorTileEntity.activate(false);
        }
        shutdown--;
        if (shutdown <= 0) {
            shutdown = 0;
        }
        startup = 0;
        active = network.isActive();
        network.setStartupCounter(0);
        network.setShutdownCounter(shutdown);
        setChanged();

        return true;
    }


    @Override
    public void setPowerInput(int powered) {
        boolean changed = powerLevel != powered;
        super.setPowerInput(powered);
        // @todo 1.16 check, do we need this?
        if (changed) {
            setChanged();
        }
    }

    public boolean isPowered() {
        return powerLevel > 0;
    }

    @Override
    protected boolean needsRedstoneMode() {
        return true;
    }

    @Override
    public void saveAdditional(@Nonnull CompoundNBT tagCompound) {
        tagCompound.putInt("startup", startup);
        tagCompound.putInt("shutdown", shutdown);
        tagCompound.putBoolean("active", active);
        super.saveAdditional(tagCompound);
    }

    @Override
    public void load(CompoundNBT tagCompound) {
        startup = tagCompound.getInt("startup");
        shutdown = tagCompound.getInt("shutdown");
        active = tagCompound.getBoolean("active");
        super.load(tagCompound);
    }

}
