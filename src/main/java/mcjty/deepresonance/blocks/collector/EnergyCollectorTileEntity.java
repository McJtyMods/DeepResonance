package mcjty.deepresonance.blocks.collector;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mcjty.deepresonance.blocks.ModBlocks;
import mcjty.deepresonance.blocks.crystals.ResonatingCrystalConfiguration;
import mcjty.deepresonance.blocks.crystals.ResonatingCrystalTileEntity;
import mcjty.deepresonance.blocks.generator.GeneratorConfiguration;
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

    // Minimum power before we stop using a crystal.
    public static final float CRYSTAL_MIN_POWER = .00001f;

    // Relative coordinates of active crystals.
    private Set<Coordinate> crystals = new HashSet<Coordinate>();
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
                        int newEnergy = network.getEnergy() + calculateRF();
                        int maxEnergy = network.getRefcount() * GeneratorConfiguration.rfPerGeneratorBlock;
                        if (newEnergy > maxEnergy) {
                            newEnergy = maxEnergy;
                        }
                        if (network.getEnergy() != newEnergy) {
                            network.setEnergy(newEnergy);
                            DRGeneratorNetwork generatorNetwork = DRGeneratorNetwork.getChannels(worldObj);
                            generatorNetwork.save(worldObj);
                        }
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
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);

            findCrystals();
        }
    }

    private int calculateRF() {
        Set<Coordinate> tokeep = new HashSet<Coordinate>();
        boolean dirty = false;
        int rf = 0;
        for (Coordinate coordinate : crystals) {
            TileEntity te = worldObj.getTileEntity(coordinate.getX() + xCoord, coordinate.getY() + yCoord, coordinate.getZ() + zCoord);
            if (te instanceof ResonatingCrystalTileEntity) {
                ResonatingCrystalTileEntity resonatingCrystalTileEntity = (ResonatingCrystalTileEntity) te;
                if (resonatingCrystalTileEntity.getPower() > CRYSTAL_MIN_POWER) {
                    resonatingCrystalTileEntity.setGlowing(lasersActive);
                    tokeep.add(coordinate);

                    float power = resonatingCrystalTileEntity.getPower();
                    if (power < resonatingCrystalTileEntity.getPowerPerTick()) {
                        // We are empty.
                        resonatingCrystalTileEntity.setPower(0);
                        // Crystal will be removed from the list of active crystals next tick.
                    } else {
                        power -= resonatingCrystalTileEntity.getPowerPerTick();
                        resonatingCrystalTileEntity.setPower(power); // @@@ No block update on crystal yet!
                        rf += resonatingCrystalTileEntity.getRfPerTick();
                    }
                } else {
                    resonatingCrystalTileEntity.setGlowing(false);
                    dirty = true;
                }
            } else {
                dirty = true;
            }
        }
        if (dirty) {
            crystals = tokeep;
            markDirty();
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        }
        return rf;
    }

    private void findCrystals() {
        Set<Coordinate> newCrystals = new HashSet<Coordinate>();

        for (int y = yCoord - 1 ; y <= yCoord + 1 ; y++) {
            if (y >= 0 && y < worldObj.getHeight()) {
                for (int x = xCoord - 10 ; x <= xCoord + 10 ; x++) {
                    for (int z = zCoord - 10 ; z <= zCoord + 10 ; z++) {
                        if (worldObj.getBlock(x, y, z) == ModBlocks.resonatingCrystalBlock) {
                            TileEntity te = worldObj.getTileEntity(x, y, z);
                            if (te instanceof ResonatingCrystalTileEntity) {
                                ResonatingCrystalTileEntity resonatingCrystalTileEntity = (ResonatingCrystalTileEntity) te;
                                if (resonatingCrystalTileEntity.getPower() > CRYSTAL_MIN_POWER) {
                                    newCrystals.add(new Coordinate(x - xCoord, y - yCoord, z - zCoord));
                                    resonatingCrystalTileEntity.setGlowing(lasersActive);
                                } else {
                                    resonatingCrystalTileEntity.setGlowing(false);
                                }
                            }
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
