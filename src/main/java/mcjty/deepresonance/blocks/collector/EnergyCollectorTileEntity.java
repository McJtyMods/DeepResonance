package mcjty.deepresonance.blocks.collector;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mcjty.deepresonance.blocks.ModBlocks;
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
    private Set<Coordinate> crystals = new HashSet<>();
    private int crystalTimeout = 20;

    public EnergyCollectorTileEntity() {
        super();
    }

    @Override
    protected void checkStateServer() {
        if (worldObj.getBlock(xCoord, yCoord-1, zCoord) == GeneratorSetup.generatorBlock) {
            TileEntity te = worldObj.getTileEntity(xCoord, yCoord-1, zCoord);
            if (te instanceof GeneratorTileEntity) {
                DRGeneratorNetwork.Network network = ((GeneratorTileEntity) te).getNetwork();
                if (network != null) {
                    // @todo temporary code.
                    network.setEnergy(network.getEnergy()+1);
                    DRGeneratorNetwork generatorNetwork = DRGeneratorNetwork.getChannels(worldObj);
                    generatorNetwork.save(worldObj);
                }
            }
        }

        // @todo temporary!
        crystalTimeout--;
        if (crystalTimeout <= 0) {
            crystalTimeout = 20;
            findCrystals();
        }
    }

    private void findCrystals() {
        Set<Coordinate> newCrystals = new HashSet<>();

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
        }
    }

    public Set<Coordinate> getCrystals() {
        return crystals;
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
