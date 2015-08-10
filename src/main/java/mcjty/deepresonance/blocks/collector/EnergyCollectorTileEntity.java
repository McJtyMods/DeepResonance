package mcjty.deepresonance.blocks.collector;

import com.google.common.collect.Sets;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mcjty.deepresonance.blocks.ModBlocks;
import mcjty.deepresonance.blocks.crystals.ResonatingCrystalTileEntity;
import mcjty.deepresonance.blocks.generator.GeneratorSetup;
import mcjty.deepresonance.blocks.generator.GeneratorTileEntity;
import mcjty.deepresonance.generatornetwork.DRGeneratorNetwork;
import mcjty.entity.GenericTileEntity;
import mcjty.varia.Coordinate;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;

import java.util.HashSet;
import java.util.Set;

public class EnergyCollectorTileEntity extends GenericTileEntity {

    // Relative coordinates of crystals.
    private Set<Coordinate> crystals = new HashSet<Coordinate>();
    private int crystalTimeout = 20;
    private boolean lasersActive = false;
    private int laserStartup = 0;        // A mirror (for the client) of the network startup counter.

    public EnergyCollectorTileEntity() {
        super();
    }

    @Override
    protected void checkStateServer() {
        boolean active = false;
        int startup = 0;
        if (worldObj.getBlock(xCoord, yCoord-1, zCoord) == GeneratorSetup.generatorBlock) {
            TileEntity te = worldObj.getTileEntity(xCoord, yCoord-1, zCoord);
            if (te instanceof GeneratorTileEntity) {
                DRGeneratorNetwork.Network network = ((GeneratorTileEntity) te).getNetwork();
                if (network != null) {
                    if (network.isActive()) {
                        // @todo temporary code.
                        network.setEnergy(network.getEnergy() + calculateRF());
                        DRGeneratorNetwork generatorNetwork = DRGeneratorNetwork.getChannels(worldObj);
                        generatorNetwork.save(worldObj);
                        active = true;
                    }
                    startup = network.getStartupCounter();
                }
            }
        }

        if (active != lasersActive || startup != laserStartup) {
            lasersActive = active;
            laserStartup = startup;
            markDirty();
            updateCrystalState();
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        }

        // @todo temporary!
        crystalTimeout--;
        if (crystalTimeout <= 0) {
            crystalTimeout = 20;
            findCrystals();
        }
    }

    private int calculateRF(){
        return 1;
    }

    private void updateCrystalState() {
        for (Coordinate coordinate : crystals) {
            TileEntity te = worldObj.getTileEntity(coordinate.getX() + xCoord, coordinate.getY() + yCoord, coordinate.getZ() + zCoord);
            if (te instanceof ResonatingCrystalTileEntity) {
                ResonatingCrystalTileEntity resonatingCrystalTileEntity = (ResonatingCrystalTileEntity) te;
                resonatingCrystalTileEntity.setGlowing(lasersActive);
            }
        }

    }

    private void findCrystals() {
        Set<Coordinate> newCrystals = new HashSet<Coordinate>();

        for (int y = yCoord - 1 ; y <= yCoord + 1 ; y++) {
            if (y >= 0 && y < worldObj.getHeight()) {
                for (int x = xCoord - 10 ; x <= xCoord + 10 ; x++) {
                    for (int z = zCoord - 10 ; z <= zCoord + 10 ; z++) {
                        if (worldObj.getBlock(x, y, z) == ModBlocks.resonatingCrystalBlock) {
                            newCrystals.add(new Coordinate(x-xCoord, y-yCoord, z-zCoord));
                        }
                    }
                }
            }
        }
        if (!newCrystals.equals(crystals)) {
            crystals = newCrystals;
            markDirty();
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
            updateCrystalState();
        }
    }

    public Set<Coordinate> getCrystals() {
        return crystals;
    }

    public boolean areLasersActive() {
        return lasersActive;
    }

    public int getLaserStartup() {
        return laserStartup;
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);
        byte[] crystalX = tagCompound.getByteArray("crystalsX");
        byte[] crystalY = tagCompound.getByteArray("crystalsY");
        byte[] crystalZ = tagCompound.getByteArray("crystalsZ");
        crystals.clear();
        for (int i = 0 ; i < crystalX.length ; i++) {
            crystals.add(new Coordinate(crystalX[i], crystalY[i], crystalZ[i]));
        }
        lasersActive = tagCompound.getBoolean("lasersActive");
        laserStartup = tagCompound.getInteger("laserStartup");
    }

    @Override
    public void writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);
        byte[] crystalX = new byte[crystals.size()];
        byte[] crystalY = new byte[crystals.size()];
        byte[] crystalZ = new byte[crystals.size()];
        int i = 0;
        for (Coordinate crystal : crystals) {
            crystalX[i] = (byte) crystal.getX();
            crystalY[i] = (byte) crystal.getY();
            crystalZ[i] = (byte) crystal.getZ();
            i++;
        }

        tagCompound.setByteArray("crystalsX", crystalX);
        tagCompound.setByteArray("crystalsY", crystalY);
        tagCompound.setByteArray("crystalsZ", crystalZ);
        tagCompound.setBoolean("lasersActive", lasersActive);
        tagCompound.setInteger("laserStartup", laserStartup);
    }

    @Override
    public boolean shouldRenderInPass(int pass) {
        return pass == 1;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return AxisAlignedBB.getBoundingBox(xCoord - 7, yCoord - 1, zCoord - 7, xCoord + 8, yCoord + 2, zCoord + 8);
    }

}
