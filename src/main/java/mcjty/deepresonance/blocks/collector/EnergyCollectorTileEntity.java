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
import mcjty.deepresonance.radiation.DRRadiationManager;
import mcjty.deepresonance.radiation.RadiationConfiguration;
import mcjty.entity.GenericTileEntity;
import mcjty.varia.Coordinate;
import mcjty.varia.GlobalCoordinate;
import mcjty.varia.Logging;
import net.minecraft.entity.player.EntityPlayer;
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
        DRGeneratorNetwork.Network network = null;
        int startup = 0;
        if (worldObj.getBlock(xCoord, yCoord-1, zCoord) == GeneratorSetup.generatorBlock) {
            TileEntity te = worldObj.getTileEntity(xCoord, yCoord-1, zCoord);
            if (te instanceof GeneratorTileEntity) {
                network = ((GeneratorTileEntity) te).getNetwork();
                if (network != null) {
                    if (network.isActive()) {
                        int rfPerTick = calculateRF();
                        network.setLastRfPerTick(rfPerTick);
                        int newEnergy = network.getEnergy() + rfPerTick;
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
                    } else {
                        network.setLastRfPerTick(0);
                    }
                    startup = network.getStartupCounter();
                }
            }
        }

        if (active != lasersActive || startup != laserStartup) {
            boolean doFind = lasersActive != active;
            lasersActive = active;
            laserStartup = startup;
            markDirty();
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);

            if (doFind) {
                findCrystals(network);
            }
        }
    }

    private int calculateRF() {
        Set<Coordinate> tokeep = new HashSet<Coordinate>();
        boolean dirty = false;

        float radiationRadius = 0;
        float radiationStrength = 0;

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
                        int rfPerTick = resonatingCrystalTileEntity.getRfPerTick();
                        rf += rfPerTick;

                        float purity = resonatingCrystalTileEntity.getPurity();
                        if (purity < 99.0f) {
                            float radius = RadiationConfiguration.minRadiationRadius + ((float) rfPerTick / ResonatingCrystalConfiguration.maximumRFtick)
                                    * (RadiationConfiguration.maxRadiationRadius - RadiationConfiguration.minRadiationRadius);
                            radius += radius * (100.0f-purity) * .005f;

                            if (radius > radiationRadius) {
                                radiationRadius = radius;
                            }

                            float strength = RadiationConfiguration.minRadiationStrength + resonatingCrystalTileEntity.getStrength() / 100.0f
                                    * (RadiationConfiguration.maxRadiationStrength - RadiationConfiguration.minRadiationStrength);
                            strength += strength * (100.0f-purity) * .005f;
                            radiationStrength += strength;
                        }
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

        if (radiationRadius > 0.1f) {
            DRRadiationManager radiationManager = DRRadiationManager.getManager(worldObj);
            GlobalCoordinate thisCoordinate = new GlobalCoordinate(new Coordinate(xCoord, yCoord, zCoord), worldObj.provider.dimensionId);
            if (radiationManager.getRadiationSource(thisCoordinate) == null) {
                Logging.log("Created radiation source with radius " + radiationRadius + " and strength " + radiationStrength);
            }
            DRRadiationManager.RadiationSource radiationSource = radiationManager.getOrCreateRadiationSource(thisCoordinate);
            radiationSource.update(radiationRadius, radiationStrength);
            radiationManager.save(worldObj);
        }

        return rf;
    }

    private void findCrystals(DRGeneratorNetwork.Network network) {
        Set<Coordinate> newCrystals = new HashSet<Coordinate>();

        int maxSupportedRF = network.getRefcount() * GeneratorConfiguration.maxRFInputPerBlock;
        int maxSupportedCrystals = network.getRefcount() * GeneratorConfiguration.maxCrystalsPerBlock;

        boolean tooManyCrystals = false;
        boolean tooMuchPower = false;

        for (int y = yCoord - 1 ; y <= yCoord + 1 ; y++) {
            if (y >= 0 && y < worldObj.getHeight()) {
                for (int x = xCoord - 10 ; x <= xCoord + 10 ; x++) {
                    for (int z = zCoord - 10 ; z <= zCoord + 10 ; z++) {
                        if (worldObj.getBlock(x, y, z) == ModBlocks.resonatingCrystalBlock) {
                            TileEntity te = worldObj.getTileEntity(x, y, z);
                            if (te instanceof ResonatingCrystalTileEntity) {
                                ResonatingCrystalTileEntity resonatingCrystalTileEntity = (ResonatingCrystalTileEntity) te;
                                if (resonatingCrystalTileEntity.getPower() > CRYSTAL_MIN_POWER) {
                                    if (newCrystals.size() >= maxSupportedCrystals) {
                                        resonatingCrystalTileEntity.setGlowing(false);
                                        tooManyCrystals = true;
                                    } else if (resonatingCrystalTileEntity.getRfPerTick() > maxSupportedRF) {
                                        resonatingCrystalTileEntity.setGlowing(false);
                                        tooMuchPower = true;
                                    } else {
                                        maxSupportedRF -= resonatingCrystalTileEntity.getRfPerTick();
                                        newCrystals.add(new Coordinate(x - xCoord, y - yCoord, z - zCoord));
                                        resonatingCrystalTileEntity.setGlowing(lasersActive);
                                    }
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

        if (lasersActive && (tooManyCrystals || tooMuchPower)) {
            // @todo This should be put in the Logging class as a broadcast message
            for (Object p : worldObj.playerEntities) {
                EntityPlayer player = (EntityPlayer)p;
                double sqdist = player.getDistanceSq(xCoord+.5, yCoord+.5, zCoord+.5);
                if (sqdist < 100) {
                    if (tooManyCrystals) {
                        Logging.warn(player, "There are too many crystals for this size generator!");
                    }
                    if (tooMuchPower) {
                        Logging.warn(player, "Some crystals are too powerful for this size generator!!");
                    }
                }
            }
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
