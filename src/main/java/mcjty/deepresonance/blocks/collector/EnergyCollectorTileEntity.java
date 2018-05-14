package mcjty.deepresonance.blocks.collector;

import mcjty.deepresonance.blocks.ModBlocks;
import mcjty.deepresonance.blocks.crystals.ResonatingCrystalTileEntity;
import mcjty.deepresonance.blocks.generator.GeneratorConfiguration;
import mcjty.deepresonance.blocks.generator.GeneratorTileEntity;
import mcjty.deepresonance.config.ConfigMachines;
import mcjty.deepresonance.generatornetwork.DRGeneratorNetwork;
import mcjty.deepresonance.radiation.DRRadiationManager;
import mcjty.deepresonance.radiation.SuperGenerationConfiguration;
import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.lib.varia.Broadcaster;
import mcjty.lib.varia.GlobalCoordinate;
import mcjty.lib.varia.Logging;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.HashSet;
import java.util.Set;

public class EnergyCollectorTileEntity extends GenericTileEntity implements ITickable {

    // Minimum power before we stop using a crystal.
    public static final float CRYSTAL_MIN_POWER = .00001f;

    public static int MAXTICKS = 20;        // Update radiation every second.

    // Relative coordinates of active crystals.
    private Set<BlockPos> crystals = new HashSet<>();
    private boolean lasersActive = false;
    private int laserStartup = 0;        // A mirror (for the client) of the network startup counter.
    private int radiationUpdateCount = MAXTICKS;
    private int networkID = -1;

    public EnergyCollectorTileEntity() {
        super();
    }

    @Override
    public void update() {
        if (!getWorld().isRemote) {
            checkStateServer();
        }
    }

    private void checkStateServer() {
        boolean active = false;
        int startup = 0;
        DRGeneratorNetwork.Network network = null;

        TileEntity te = getWorld().getTileEntity(getPos().down());
        if (te instanceof GeneratorTileEntity) {
            GeneratorTileEntity generatorTileEntity = (GeneratorTileEntity) te;
            DRGeneratorNetwork generatorNetwork = DRGeneratorNetwork.getChannels(getWorld());

            if (networkID != generatorTileEntity.getNetworkId()) {
                if (networkID != -1) {
                    generatorNetwork.getOrCreateNetwork(networkID).decCollectorBlocks();
                }

                networkID = generatorTileEntity.getNetworkId();
                generatorTileEntity.getNetwork().incCollectorBlocks();
                generatorNetwork.save();
            }


            network = generatorTileEntity.getNetwork();
            if (network != null) {
                if (network.isActive()) {
                    int rfPerTick = calculateRF();
                    network.setLastRfPerTick(rfPerTick);
                    int newEnergy = network.getEnergy() + rfPerTick;
                    int maxEnergy = network.getGeneratorBlocks() * GeneratorConfiguration.rfPerGeneratorBlock;
                    if (newEnergy > maxEnergy) {
                        newEnergy = maxEnergy;
                    }
                    if (network.getEnergy() != newEnergy) {
                        network.setEnergy(newEnergy);
                        generatorNetwork.save();
                    }

                    active = true;
                } else {
                    network.setLastRfPerTick(0);
                }
                startup = network.getStartupCounter();
            }
        } else {
            if (networkID != -1) {
                networkID = -1;
                markDirty();
            }
        }

        if (active != lasersActive || startup != laserStartup) {
            // Only when active changes and the lasert started recently do we check for new crystals.
            boolean doFind = lasersActive != active || (laserStartup > (GeneratorConfiguration.startupTime - 5));
            lasersActive = active;
            laserStartup = startup;
            markDirtyClient();

            if (doFind && te instanceof GeneratorTileEntity) {
                findCrystals(network);
            }
        }
    }

    public void disableCrystalGlow() {
        for (BlockPos coordinate : crystals) {
            TileEntity te = getWorld().getTileEntity(new BlockPos(getPos().getX() + coordinate.getX(), getPos().getY() + coordinate.getY(), getPos().getZ() + coordinate.getZ()));
            if (te instanceof ResonatingCrystalTileEntity) {
                ResonatingCrystalTileEntity resonatingCrystalTileEntity = (ResonatingCrystalTileEntity) te;
                resonatingCrystalTileEntity.setGlowing(false);
            }
        }
    }

    private int calculateRF() {
        Set<BlockPos> tokeep = new HashSet<>();
        boolean dirty = false;

        float radiationRadius = 0;
        float radiationStrength = 0;

        boolean doRadiation = false;
        radiationUpdateCount--;
        if (radiationUpdateCount <= 0) {
            radiationUpdateCount = MAXTICKS;
            doRadiation = true;
        }

        int rf = 0;
        for (BlockPos coordinate : crystals) {
            TileEntity te = getWorld().getTileEntity(new BlockPos(getPos().getX() + coordinate.getX(), getPos().getY() + coordinate.getY(), getPos().getZ() + coordinate.getZ()));
            if (te instanceof ResonatingCrystalTileEntity) {
                ResonatingCrystalTileEntity crystal = (ResonatingCrystalTileEntity) te;
                if (crystal.getPower() > CRYSTAL_MIN_POWER) {
                    crystal.setGlowing(lasersActive);
                    tokeep.add(coordinate);

                    float power = crystal.getPower();
                    if (power < crystal.getPowerPerTick()) {
                        // We are empty.
                        crystal.setPower(0);
                        // Crystal will be removed from the list of active crystals next tick.
                    } else {
                        power -= crystal.getPowerPerTick();
                        crystal.setPower(power); // @@@ No block update on crystal yet!
                        int rfPerTick = crystal.getRfPerTick();
                        rf += rfPerTick;

                        if (doRadiation) {
                            float purity = crystal.getPurity();
                            float radius = DRRadiationManager.calculateRadiationRadius(crystal.getStrength(), crystal.getEfficiency(), purity);
                            if (radius > radiationRadius) {
                                radiationRadius = radius;
                            }
                            float strength = DRRadiationManager.calculateRadiationStrength(crystal.getStrength(), purity);
                            if (crystal.getResistance() < SuperGenerationConfiguration.maxResistance) {
                                float factor = (float) crystal.getResistance() / SuperGenerationConfiguration.maxResistance;
                                strength = strength / factor;
                            }
                            radiationStrength += strength;
                        }
                    }
                } else {
                    crystal.setGlowing(false);
                    dirty = true;
                }
            } else {
                dirty = true;
            }
        }
        if (dirty) {
            crystals = tokeep;
            markDirtyClient();
        }

        if (doRadiation && radiationRadius > 0.1f) {
            DRRadiationManager radiationManager = DRRadiationManager.getManager(getWorld());
            GlobalCoordinate thisCoordinate = new GlobalCoordinate(getPos(), getWorld().provider.getDimension());
            if (radiationManager.getRadiationSource(thisCoordinate) == null) {
                Logging.log("Created radiation source with radius " + radiationRadius + " and strength " + radiationStrength);
            }
            DRRadiationManager.RadiationSource radiationSource = radiationManager.getOrCreateRadiationSource(thisCoordinate);
            radiationSource.update(radiationRadius, radiationStrength, MAXTICKS);
            radiationManager.save();
        }

        return rf;
    }

    public void addCrystal(int x, int y, int z) {
        if (networkID == -1) {
            return;
        }

        DRGeneratorNetwork channels = DRGeneratorNetwork.getChannels(getWorld());
        DRGeneratorNetwork.Network network = channels.getChannel(networkID);
        if (network == null) {
            return;
        }

        int maxSupportedRF = network.getGeneratorBlocks() * GeneratorConfiguration.maxRFInputPerBlock;
        for (BlockPos coordinate : crystals) {
            TileEntity te = getWorld().getTileEntity(new BlockPos(getPos().getX() + coordinate.getX(), getPos().getY() + coordinate.getY(), getPos().getZ() + coordinate.getZ()));
            if (te instanceof ResonatingCrystalTileEntity) {
                ResonatingCrystalTileEntity resonatingCrystalTileEntity = (ResonatingCrystalTileEntity) te;
                if (resonatingCrystalTileEntity.getPower() > CRYSTAL_MIN_POWER) {
                    maxSupportedRF -= resonatingCrystalTileEntity.getRfPerTick();
                }
            }
        }

        if (addCrystal(x, y, z, network, crystals, crystals, maxSupportedRF) >= 0) {
            // Success.
            markDirtyClient();
        }
    }

    private static int ERROR_TOOMANYCRYSTALS = -1;
    private static int ERROR_TOOMUCHPOWER = -2;

    // Returns remaining RF that is supported if crystal could be added. Otherwise one of the errors above.
    private int addCrystal(int x, int y, int z, DRGeneratorNetwork.Network network, Set<BlockPos> newCrystals, Set<BlockPos> oldCrystals, int maxSupportedRF) {
        int maxSupportedCrystals = network.getGeneratorBlocks() * GeneratorConfiguration.maxCrystalsPerBlock;

        TileEntity te = getWorld().getTileEntity(new BlockPos(x, y, z));
        if (te instanceof ResonatingCrystalTileEntity) {
            ResonatingCrystalTileEntity resonatingCrystalTileEntity = (ResonatingCrystalTileEntity) te;
            if (resonatingCrystalTileEntity.getPower() > CRYSTAL_MIN_POWER) {
                BlockPos crystalCoordinate = new BlockPos(x - getPos().getX(), y - getPos().getY(), z - getPos().getZ());
                if (resonatingCrystalTileEntity.isGlowing() && !oldCrystals.contains(crystalCoordinate)) {
                    // The crystal is already glowing and is not in our 'old' crystal set. That means that it
                    // is currently being managed by another generator. We ignore it then.
                    return maxSupportedRF;
                }

                if (newCrystals.size() >= maxSupportedCrystals) {
                    resonatingCrystalTileEntity.setGlowing(false);
                    return ERROR_TOOMANYCRYSTALS;
                } else if (resonatingCrystalTileEntity.getRfPerTick() > maxSupportedRF) {
                    resonatingCrystalTileEntity.setGlowing(false);
                    return ERROR_TOOMUCHPOWER;
                } else {
                    maxSupportedRF -= resonatingCrystalTileEntity.getRfPerTick();
                    newCrystals.add(crystalCoordinate);
                    resonatingCrystalTileEntity.setGlowing(lasersActive);
                }
            } else {
                resonatingCrystalTileEntity.setGlowing(false);
            }
        }
        return maxSupportedRF;
    }

    private void findCrystals(DRGeneratorNetwork.Network network) {
        Set<BlockPos> newCrystals = new HashSet<>();

        int maxSupportedRF = network.getGeneratorBlocks() * GeneratorConfiguration.maxRFInputPerBlock;

        boolean tooManyCrystals = false;
        boolean tooMuchPower = false;

        // @todo this is not a good algorithm. It should find the closest first.
        int xCoord = getPos().getX();
        int yCoord = getPos().getY();
        int zCoord = getPos().getZ();
        for (int y = yCoord - ConfigMachines.collector.maxVerticalCrystalDistance ; y <= yCoord + ConfigMachines.collector.maxVerticalCrystalDistance ; y++) {
            if (y >= 0 && y < getWorld().getHeight()) {
                int maxhordist = ConfigMachines.collector.maxHorizontalCrystalDistance;
                for (int x = xCoord - maxhordist; x <= xCoord + maxhordist; x++) {
                    for (int z = zCoord - maxhordist; z <= zCoord + maxhordist; z++) {
                        if (getWorld().getBlockState(new BlockPos(x, y, z)).getBlock() == ModBlocks.resonatingCrystalBlock) {
                            maxSupportedRF = addCrystal(x, y, z, network, newCrystals, crystals, maxSupportedRF);
                            if (maxSupportedRF == ERROR_TOOMANYCRYSTALS) {
                                tooManyCrystals = true;
                            } else if (maxSupportedRF == ERROR_TOOMUCHPOWER) {
                                tooMuchPower = true;
                            }
                        }
                    }
                }
            }
        }
        if (!newCrystals.equals(crystals)) {
            crystals = newCrystals;
            markDirtyClient();
        }

        if (lasersActive && (tooManyCrystals || tooMuchPower)) {
            // @todo This should be put in the Logging class as a broadcast message
            if (tooManyCrystals) {
                Broadcaster.broadcast(getWorld(), xCoord, yCoord, zCoord, "There are too many crystals for this size generator!", 100);
            }
            if (tooMuchPower) {
                Broadcaster.broadcast(getWorld(), xCoord, yCoord, zCoord, "Some crystals are too powerful for this size generator!!", 100);
            }
        }

    }

    public Set<BlockPos> getCrystals() {
        return crystals;
    }

    public boolean areLasersActive() {
        return lasersActive;
    }

    public int getLaserStartup() {
        return laserStartup;
    }

    public int getNetworkID() {
        return networkID;
    }

    public void setNetworkID(int networkID) {
        this.networkID = networkID;
        markDirty();
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);
        byte[] crystalX = tagCompound.getByteArray("crystalsX");
        byte[] crystalY = tagCompound.getByteArray("crystalsY");
        byte[] crystalZ = tagCompound.getByteArray("crystalsZ");
        crystals.clear();
        for (int i = 0 ; i < crystalX.length ; i++) {
            crystals.add(new BlockPos(crystalX[i], crystalY[i], crystalZ[i]));
        }
        lasersActive = tagCompound.getBoolean("lasersActive");
        laserStartup = tagCompound.getInteger("laserStartup");
        if (tagCompound.hasKey("networkId")) {
            networkID = tagCompound.getInteger("networkId");
        } else {
            networkID = -1;
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);
        byte[] crystalX = new byte[crystals.size()];
        byte[] crystalY = new byte[crystals.size()];
        byte[] crystalZ = new byte[crystals.size()];
        int i = 0;
        for (BlockPos crystal : crystals) {
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
        tagCompound.setInteger("networkId", networkID);
        return tagCompound;
    }



    @Override
    public boolean shouldRenderInPass(int pass) {
        return pass == 1;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        int xCoord = getPos().getX();
        int yCoord = getPos().getY();
        int zCoord = getPos().getZ();

        return new AxisAlignedBB(xCoord - 7, yCoord - 1, zCoord - 7, xCoord + 8, yCoord + 2, zCoord + 8);
    }

}
