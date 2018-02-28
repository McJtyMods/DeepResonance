package mcjty.deepresonance.blocks.gencontroller;

import mcjty.deepresonance.blocks.generator.GeneratorConfiguration;
import mcjty.deepresonance.blocks.generator.GeneratorSetup;
import mcjty.deepresonance.blocks.generator.GeneratorTileEntity;
import mcjty.deepresonance.client.sound.GeneratorSoundController;
import mcjty.deepresonance.generatornetwork.DRGeneratorNetwork;
import mcjty.lib.entity.GenericTileEntity;
import mcjty.lib.varia.Broadcaster;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.HashSet;
import java.util.Set;

public class GeneratorControllerTileEntity extends GenericTileEntity implements ITickable {
    private int startup = 0;
    private int shutdown = 0;
    private boolean active = false;
    private boolean rsControlled = true;
    private boolean activated = false;

    public GeneratorControllerTileEntity() {
        super();
    }

    public boolean isPowered() {
        return powerLevel > 0;
    }


    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
        boolean working = isPowered();

        super.onDataPacket(net, packet);

        if (getWorld().isRemote) {
            // If needed send a render update.
            if (isPowered() != working) {
                getWorld().markBlockRangeForRenderUpdate(getPos(), getPos());
            }
        }
    }


    @Override
    public void invalidate() {
        super.invalidate();
        if (getWorld().isRemote) {
            stopSounds();
        }
    }

    @SideOnly(Side.CLIENT)
    private void stopSounds() {
        GeneratorSoundController.stopSound(getWorld(), getPos());
    }

    @Override
    public void update() {
        if (!getWorld().isRemote){
            checkStateServer();
        } else {
            checkStateClient();
        }
    }

    @SideOnly(Side.CLIENT)
    protected void checkStateClient() {
        if (GeneratorConfiguration.baseGeneratorVolume < 0.01f) {
            // No sounds.
            return;
        }
        if (startup != 0) {
            if (!GeneratorSoundController.isStartupPlaying(getWorld(), pos)) {
                GeneratorSoundController.playStartup(getWorld(), pos);
            }
        } else if (shutdown != 0) {
            if (!GeneratorSoundController.isShutdownPlaying(getWorld(), pos)) {
                GeneratorSoundController.playShutdown(getWorld(), pos);
            }
        } else if (active) {
            if (!GeneratorSoundController.isLoopPlaying(getWorld(), pos)) {
                GeneratorSoundController.playLoop(getWorld(), pos);
            }
        } else {
            stopSounds();
        }
    }

    protected void checkStateServer() {
        boolean active = (rsControlled && powerLevel > 0) || (!rsControlled && activated);

        // @todo optimize this?
        boolean dirty = false;
        Set<Integer> networks = new HashSet<Integer>();
        for (EnumFacing direction : EnumFacing.VALUES) {
            BlockPos newC = getPos().offset(direction);
            Block b = getWorld().getBlockState(newC).getBlock();
            if (b == GeneratorSetup.generatorBlock) {
                GeneratorTileEntity generatorTileEntity = (GeneratorTileEntity) getWorld().getTileEntity(newC);
                int networkId = generatorTileEntity.getNetworkId();
                if (networkId != -1 && !networks.contains(networkId)) {
                    networks.add(networkId);
                    if (active) {
                        // Only activate with sufficient energy collectors.
                        int countCollectors = generatorTileEntity.getNetwork().getCollectorBlocks();
                        if (countCollectors == 1) {
                            if (handleActivate(networkId, newC)) {
                                dirty = true;
                            }
                        } else {
                            if (countCollectors < 1) {
                                Broadcaster.broadcast(getWorld(), getPos().getX(), getPos().getY(), getPos().getZ(), "There is no energy collector on this generator!", 100);
                            } else {
                                Broadcaster.broadcast(getWorld(), getPos().getX(), getPos().getY(), getPos().getZ(), "There are too many energy collectors on this generator!!", 100);
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
            DRGeneratorNetwork generatorNetwork = DRGeneratorNetwork.getChannels(getWorld());
            generatorNetwork.save(getWorld());
        }
    }

    public void activate() {
        this.rsControlled = false;
        this.activated = true;
        this.markDirtyClient();
    }

    public void deactivate() {
        this.activated = false;
        this.markDirtyClient();
    }

    public void setRsControlled(boolean f) {
        this.rsControlled = f;
    }

    private boolean handleActivate(int id, BlockPos coordinate) {
        DRGeneratorNetwork generatorNetwork = DRGeneratorNetwork.getChannels(getWorld());
        DRGeneratorNetwork.Network network = generatorNetwork.getOrCreateNetwork(id);
        if (network.isActive() && network.getShutdownCounter() == 0) {
            return false; // Nothing to do.
        }
        startup = network.getStartupCounter();
        if (startup == 0) {
            startup = GeneratorConfiguration.startupTime;
        }
        startup--;
        if (startup <= 0) {
            startup = 0;
            GeneratorTileEntity generatorTileEntity = (GeneratorTileEntity) getWorld().getTileEntity(coordinate);
            generatorTileEntity.activate(true);
        }
        active = network.isActive();
        shutdown = 0;
        network.setShutdownCounter(0);
        network.setStartupCounter(startup);
        markDirty();
        IBlockState state = getWorld().getBlockState(pos);
        getWorld().notifyBlockUpdate(pos, state, state, 3);
        return true;
    }

    private boolean handleDeactivate(int id, BlockPos coordinate) {
        IBlockState state = getWorld().getBlockState(getPos());
        DRGeneratorNetwork generatorNetwork = DRGeneratorNetwork.getChannels(getWorld());
        DRGeneratorNetwork.Network network = generatorNetwork.getOrCreateNetwork(id);
        if ((!network.isActive()) && network.getShutdownCounter() == 0 && network.getStartupCounter() == 0) {
            if (network.getShutdownCounter() != shutdown || network.getStartupCounter() != startup || (network.isActive() != active)) {
                shutdown = network.getShutdownCounter();
                startup = network.getStartupCounter();
                active = network.isActive();
                markDirty();

                getWorld().notifyBlockUpdate(getPos(), state, state, 3);
            }
            return false;   // Nothing to do.
        }

        shutdown = network.getShutdownCounter();
        if (network.isActive() || network.getStartupCounter() != 0) {
            shutdown = GeneratorConfiguration.shutdownTime;
            GeneratorTileEntity generatorTileEntity = (GeneratorTileEntity) getWorld().getTileEntity(coordinate);
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
        markDirty();
        getWorld().notifyBlockUpdate(getPos(), state, state, 3);

        return true;
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);
        startup = tagCompound.getInteger("startup");
        shutdown = tagCompound.getInteger("shutdown");
        active = tagCompound.getBoolean("active");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);
        tagCompound.setInteger("startup", startup);
        tagCompound.setInteger("shutdown", shutdown);
        tagCompound.setBoolean("active", active);
        return tagCompound;
    }
}
