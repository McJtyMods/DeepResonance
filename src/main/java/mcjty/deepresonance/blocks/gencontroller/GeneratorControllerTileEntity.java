package mcjty.deepresonance.blocks.gencontroller;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mcjty.deepresonance.blocks.generator.GeneratorConfiguration;
import mcjty.deepresonance.blocks.generator.GeneratorSetup;
import mcjty.deepresonance.blocks.generator.GeneratorTileEntity;
import mcjty.deepresonance.generatornetwork.DRGeneratorNetwork;
import mcjty.entity.GenericTileEntity;
import mcjty.varia.BlockTools;
import mcjty.varia.Coordinate;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.HashSet;
import java.util.Set;

public class GeneratorControllerTileEntity extends GenericTileEntity {
    private GeneratorStartupSound generatorStartupSound;
    private GeneratorLoopSound generatorLoopSound;
    private GeneratorShutdownSound generatorShutdownSound;

    private int startup = 0;
    private int shutdown = 0;
    private boolean active = false;

    public GeneratorControllerTileEntity() {
        super();
    }

    @SideOnly(Side.CLIENT)
    private void playStartup() {
        EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
        generatorStartupSound = new GeneratorStartupSound(player, worldObj, xCoord, yCoord, zCoord);
        Minecraft.getMinecraft().getSoundHandler().playSound(generatorStartupSound);
    }

    @SideOnly(Side.CLIENT)
    private void playLoop() {
        EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
        generatorLoopSound = new GeneratorLoopSound(player, worldObj, xCoord, yCoord, zCoord);
        Minecraft.getMinecraft().getSoundHandler().playSound(generatorLoopSound);
    }

    @SideOnly(Side.CLIENT)
    private void playShutdown() {
        EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
        generatorShutdownSound = new GeneratorShutdownSound(player, worldObj, xCoord, yCoord, zCoord);
        Minecraft.getMinecraft().getSoundHandler().playSound(generatorShutdownSound);
    }

    @Override
    public void invalidate() {
        super.invalidate();
        if (worldObj.isRemote) {
            stopSounds();
        }
    }

    @SideOnly(Side.CLIENT)
    private void stopSounds() {
        stopStartup();
        stopLoop();
        stopShutdown();
    }

    private void stopShutdown() {
        if (generatorShutdownSound != null) {
            Minecraft.getMinecraft().getSoundHandler().stopSound(generatorShutdownSound);
            generatorShutdownSound = null;
        }
    }

    private void stopLoop() {
        if (generatorLoopSound != null) {
            Minecraft.getMinecraft().getSoundHandler().stopSound(generatorLoopSound);
            generatorLoopSound = null;
        }
    }

    private void stopStartup() {
        if (generatorStartupSound != null) {
            Minecraft.getMinecraft().getSoundHandler().stopSound(generatorStartupSound);
            generatorStartupSound = null;
        }
    }


    @Override
    public boolean canUpdate() {
        return true;
    }

    @Override
    protected void checkStateClient() {
        if (GeneratorConfiguration.baseGeneratorVolume < 0.01f) {
            // No sounds.
            return;
        }
        if (startup != 0) {
            stopLoop();
            stopShutdown();
            if (generatorStartupSound == null) {
                playStartup();
            }
        } else if (shutdown != 0) {
            stopLoop();
            stopStartup();
            if (generatorShutdownSound == null) {
                playShutdown();
            }
        } else if (active) {
            stopShutdown();
            stopStartup();
            if (generatorLoopSound == null) {
                playLoop();
            }
        } else {
            stopSounds();
        }
    }


    @Override
    protected void checkStateServer() {
        int meta = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
        boolean active = BlockTools.getRedstoneSignalIn(meta);

        // @todo optimize this?
        boolean dirty = false;
        Set<Integer> networks = new HashSet<Integer>();
        Coordinate thisCoord = new Coordinate(xCoord, yCoord, zCoord);
        for (ForgeDirection direction : ForgeDirection.values()) {
            if (!direction.equals(ForgeDirection.UNKNOWN)) {
                Coordinate newC = thisCoord.addDirection(direction);
                Block b = worldObj.getBlock(newC.getX(), newC.getY(), newC.getZ());
                if (b == GeneratorSetup.generatorBlock) {
                    GeneratorTileEntity generatorTileEntity = (GeneratorTileEntity) worldObj.getTileEntity(newC.getX(), newC.getY(), newC.getZ());
                    int networkId = generatorTileEntity.getNetworkId();
                    if (networkId != -1 && !networks.contains(networkId)) {
                        networks.add(networkId);
                        if (active) {
                            if (handleActivate(networkId, newC)) {
                                dirty = true;
                            }
                        } else {
                            if (handleDeactivate(networkId, newC)) {
                                dirty = true;
                            }
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

    private boolean handleActivate(int id, Coordinate coordinate) {
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
            GeneratorTileEntity generatorTileEntity = (GeneratorTileEntity) worldObj.getTileEntity(coordinate.getX(), coordinate.getY(), coordinate.getZ());
            generatorTileEntity.activate(true);
        }
        active = network.isActive();
        shutdown = 0;
        network.setShutdownCounter(0);
        network.setStartupCounter(startup);
        markDirty();
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        return true;
    }

    private boolean handleDeactivate(int id, Coordinate coordinate) {
        DRGeneratorNetwork generatorNetwork = DRGeneratorNetwork.getChannels(worldObj);
        DRGeneratorNetwork.Network network = generatorNetwork.getOrCreateNetwork(id);
        if ((!network.isActive()) && network.getShutdownCounter() == 0 && network.getStartupCounter() == 0) {
            if (network.getShutdownCounter() != shutdown || network.getStartupCounter() != startup || (network.isActive() != active)) {
                shutdown = network.getShutdownCounter();
                startup = network.getStartupCounter();
                active = network.isActive();
                markDirty();
                worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
            }
            return false;   // Nothing to do.
        }

        shutdown = network.getShutdownCounter();
        if (network.isActive() || network.getStartupCounter() != 0) {
            shutdown = GeneratorConfiguration.shutdownTime;
            GeneratorTileEntity generatorTileEntity = (GeneratorTileEntity) worldObj.getTileEntity(coordinate.getX(), coordinate.getY(), coordinate.getZ());
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
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);

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
