package mcjty.deepresonance.modules.generator.block;

import mcjty.deepresonance.modules.generator.GeneratorModule;
import mcjty.deepresonance.modules.generator.data.DRGeneratorNetwork;
import mcjty.deepresonance.modules.generator.data.GeneratorBlob;
import mcjty.deepresonance.modules.generator.sound.GeneratorSoundController;
import mcjty.deepresonance.modules.generator.util.GeneratorConfig;
import mcjty.lib.multiblock.MultiblockDriver;
import mcjty.lib.multiblock.MultiblockSupport;
import mcjty.lib.tileentity.TickingTileEntity;
import mcjty.lib.varia.Broadcaster;
import mcjty.lib.varia.OrientationTools;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Set;

public class GeneratorControllerTileEntity extends TickingTileEntity {

    private int startup = 0;
    private int shutdown = 0;
    private boolean active = false;

    // Client-only for sound
    enum PlayingSound {
        NONE,
        STARTUP,
        ACTIVE,
        SHUTDOWN
    }
    private PlayingSound clientSound = PlayingSound.NONE;

    public GeneratorControllerTileEntity(BlockPos pos, BlockState state) {
        super(GeneratorModule.TYPE_GENERATOR_CONTROLLER.get(), pos, state);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket packet) {
        boolean working = isPowered();
        super.onDataPacket(net, packet);

        if (level.isClientSide) {
            // If needed send a render update.
            if (isPowered() != working) {
                BlockState state = getBlockState();
                level.sendBlockUpdated(worldPosition, state, state, Block.UPDATE_ALL);
            }
        }
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        if (level.isClientSide) {
            stopSounds();
        }
    }

    @Override
    protected void tickServer() {
        boolean active1 = powerLevel > 0;

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
        if (networks.isEmpty()) {
            shutdown = 0;
        }
        if (dirty) {
            DRGeneratorNetwork generatorNetwork = DRGeneratorNetwork.getNetwork(level);
            generatorNetwork.save();
        }

        PlayingSound newsound = getPlayingSound();
        if (newsound != clientSound) {
            clientSound = newsound;
            markDirtyClient();
        }
    }

    @Override
    protected void tickClient() {
        if (GeneratorConfig.BASE_GENERATOR_VOLUME.get() < 0.01f) {
            // No sounds.
            return;
        }
        switch (clientSound) {
            case NONE:
                stopSounds();
                break;
            case STARTUP:
                if (!GeneratorSoundController.isStartupPlaying(level, worldPosition)) {
                    GeneratorSoundController.playStartup(level, worldPosition);
                }
                break;
            case ACTIVE:
                if (!GeneratorSoundController.isLoopPlaying(level, worldPosition)) {
                    GeneratorSoundController.playLoop(level, worldPosition);
                }
                break;
            case SHUTDOWN:
                if (!GeneratorSoundController.isShutdownPlaying(level, worldPosition)) {
                    GeneratorSoundController.playShutdown(level, worldPosition);
                }
                break;
        }
    }

    private PlayingSound getPlayingSound() {
        if (startup != 0) {
            return PlayingSound.STARTUP;
        } else if (shutdown != 0) {
            return PlayingSound.SHUTDOWN;
        } else if (active) {
            return PlayingSound.ACTIVE;
        } else {
            return PlayingSound.NONE;
        }
    }

    private void stopSounds() {
        GeneratorSoundController.stopSound(level, getBlockPos());
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
    public void saveAdditional(@Nonnull CompoundTag tagCompound) {
        tagCompound.putInt("startup", startup);
        tagCompound.putInt("shutdown", shutdown);
        tagCompound.putBoolean("active", active);
        tagCompound.putInt("playingSound", clientSound.ordinal());
        super.saveAdditional(tagCompound);
    }

    @Override
    public void load(CompoundTag tagCompound) {
        startup = tagCompound.getInt("startup");
        shutdown = tagCompound.getInt("shutdown");
        active = tagCompound.getBoolean("active");
        clientSound = PlayingSound.values()[tagCompound.getInt("playingSound")];
        super.load(tagCompound);
    }

    @Override
    public void saveClientDataToNBT(CompoundTag tagCompound) {
        tagCompound.putInt("playingSound", clientSound.ordinal());
    }

    @Override
    public void loadClientDataFromNBT(CompoundTag tagCompound) {
        clientSound = PlayingSound.values()[tagCompound.getInt("playingSound")];
    }
}
