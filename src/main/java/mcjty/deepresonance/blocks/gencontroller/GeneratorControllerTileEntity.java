package mcjty.deepresonance.blocks.gencontroller;

import elec332.core.world.WorldHelper;
import mcjty.deepresonance.blocks.generator.GeneratorConfiguration;
import mcjty.deepresonance.blocks.generator.GeneratorSetup;
import mcjty.deepresonance.blocks.generator.GeneratorTileEntity;
import mcjty.deepresonance.generatornetwork.DRGeneratorNetwork;
import mcjty.deepresonance.varia.Broadcaster;
import mcjty.lib.entity.GenericTileEntity;
import mcjty.lib.varia.BlockTools;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.HashSet;
import java.util.Set;

public class GeneratorControllerTileEntity extends GenericTileEntity implements ITickable {
    private ControllerSounds controllerSounds;

    private int startup = 0;
    private int shutdown = 0;
    private boolean active = false;

    public GeneratorControllerTileEntity() {
        super();
    }

    private ControllerSounds getControllerSounds() {
        if (controllerSounds == null) {
            controllerSounds = new ControllerSounds();
        }
        return controllerSounds;
    }


    @Override
    public void invalidate() {
        super.invalidate();
        if (worldObj.isRemote) {
            stopSounds();
        }
    }

    private void stopSounds() {
        getControllerSounds().stopStartup();
        getControllerSounds().stopLoop();
        getControllerSounds().stopShutdown();
    }

    @Override
    public void update() {
        if (!worldObj.isRemote){
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
        ControllerSounds sounds = getControllerSounds();
        if (startup != 0) {
            sounds.stopLoop();
            sounds.stopShutdown();
            if (!sounds.isStartupPlaying()) {
                sounds.playStartup(worldObj, pos);
            }
        } else if (shutdown != 0) {
            sounds.stopLoop();
            sounds.stopStartup();
            if (!sounds.isShutdownPlaying()) {
                sounds.playShutdown(worldObj, pos);
            }
        } else if (active) {
            sounds.stopShutdown();
            sounds.stopStartup();
            if (!sounds.isLoopPlaying()) {
                sounds.playLoop(worldObj, pos);
            }
        } else {
            stopSounds();
        }
    }

    protected void checkStateServer() {
        //TODO: McJty: Redstone checking with meta?
        //int meta = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
        boolean active = true;//BlockTools.getRedstoneSignalIn(meta);

        // @todo optimize this?
        boolean dirty = false;
        Set<Integer> networks = new HashSet<Integer>();
        for (EnumFacing direction : EnumFacing.VALUES) {
            BlockPos newC = getPos().offset(direction);
            Block b = WorldHelper.getBlockAt(worldObj, newC);
            if (b == GeneratorSetup.generatorBlock) {
                GeneratorTileEntity generatorTileEntity = (GeneratorTileEntity) WorldHelper.getTileAt(worldObj, newC);
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
                                Broadcaster.broadcast(worldObj, getPos().getX(), getPos().getY(), getPos().getZ(), "There is no energy collector on this generator!", 100);
                            } else {
                                Broadcaster.broadcast(worldObj, getPos().getX(), getPos().getY(), getPos().getZ(), "There are too many energy collectors on this generator!!", 100);
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
            DRGeneratorNetwork generatorNetwork = DRGeneratorNetwork.getChannels(worldObj);
            generatorNetwork.save(worldObj);
        }
    }

    private boolean handleActivate(int id, BlockPos coordinate) {
        DRGeneratorNetwork generatorNetwork = DRGeneratorNetwork.getChannels(worldObj);
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
            GeneratorTileEntity generatorTileEntity = (GeneratorTileEntity) WorldHelper.getTileAt(worldObj, coordinate);
            generatorTileEntity.activate(true);
        }
        active = network.isActive();
        shutdown = 0;
        network.setShutdownCounter(0);
        network.setStartupCounter(startup);
        markDirty();
        worldObj.markBlockForUpdate(pos);
        return true;
    }

    private boolean handleDeactivate(int id, BlockPos coordinate) {
        DRGeneratorNetwork generatorNetwork = DRGeneratorNetwork.getChannels(worldObj);
        DRGeneratorNetwork.Network network = generatorNetwork.getOrCreateNetwork(id);
        if ((!network.isActive()) && network.getShutdownCounter() == 0 && network.getStartupCounter() == 0) {
            if (network.getShutdownCounter() != shutdown || network.getStartupCounter() != startup || (network.isActive() != active)) {
                shutdown = network.getShutdownCounter();
                startup = network.getStartupCounter();
                active = network.isActive();
                markDirty();
                worldObj.markBlockForUpdate(getPos());
            }
            return false;   // Nothing to do.
        }

        shutdown = network.getShutdownCounter();
        if (network.isActive() || network.getStartupCounter() != 0) {
            shutdown = GeneratorConfiguration.shutdownTime;
            GeneratorTileEntity generatorTileEntity = (GeneratorTileEntity) WorldHelper.getTileAt(worldObj, coordinate);
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
        worldObj.markBlockForUpdate(getPos());

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
    public void writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);
        tagCompound.setInteger("startup", startup);
        tagCompound.setInteger("shutdown", shutdown);
        tagCompound.setBoolean("active", active);
    }
}
