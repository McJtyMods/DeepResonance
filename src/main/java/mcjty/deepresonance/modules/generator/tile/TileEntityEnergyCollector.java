package mcjty.deepresonance.modules.generator.tile;

import mcjty.deepresonance.modules.core.CoreModule;
import mcjty.deepresonance.modules.core.tile.TileEntityResonatingCrystal;
import mcjty.deepresonance.modules.generator.GeneratorModule;
import mcjty.deepresonance.modules.generator.data.DRGeneratorNetwork;
import mcjty.deepresonance.modules.radiation.manager.DRRadiationManager;
import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.lib.varia.Broadcaster;
import mcjty.lib.varia.GlobalCoordinate;
import mcjty.lib.varia.Logging;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

import java.util.HashSet;
import java.util.Set;

public class TileEntityEnergyCollector extends GenericTileEntity implements ITickableTileEntity {

    // Minimum power before we stop using a crystal.
    public static final float CRYSTAL_MIN_POWER = .00001f;

    public static int MAXTICKS = 20;        // Update radiation every second.

    // Relative coordinates of active crystals.
    private Set<BlockPos> crystals = new HashSet<>();
    private boolean lasersActive = false;
    private int laserStartup = 0;        // A mirror (for the client) of the network startup counter.
    private int radiationUpdateCount = MAXTICKS;
    private int networkID = -1;

    public TileEntityEnergyCollector() {
        super(GeneratorModule.TYPE_ENERGY_COLLECTOR.get());
    }

    @Override
    public void tick() {
        if (!level.isClientSide()) {
            checkStateServer();
        }
    }

    private void checkStateServer() {
        boolean active = false;
        int startup = 0;
        DRGeneratorNetwork.Network network = null;

        TileEntity te = level.getBlockEntity(getBlockPos().below());
        if (te instanceof TileEntityGeneratorPart) {
            TileEntityGeneratorPart generatorTileEntity = (TileEntityGeneratorPart) te;
            DRGeneratorNetwork generatorNetwork = DRGeneratorNetwork.getChannels(level);

            if (networkID != generatorTileEntity.getMultiblockId()) {
                if (networkID != -1) {
                    // @todo 1.16
//                    generatorNetwork.getNetwork(networkID).decCollectorBlocks();
                }

                networkID = generatorTileEntity.getMultiblockId();
                // @todo 1.16
//                generatorTileEntity.getNetwork().incCollectorBlocks();
                generatorNetwork.save();
            }


            network = generatorTileEntity.getNetwork();
            if (network != null) {
                if (network.isActive()) {
                    int rfPerTick = calculateRF();
                    // @todo 1.16
//                    network.setLastRfPerTick(rfPerTick);
                    int newEnergy = network.getEnergy() + rfPerTick;
                    int maxEnergy = network.getGeneratorBlocks() * GeneratorModule.generatorConfig.powerStoragePerBlock.get();
                    if (newEnergy > maxEnergy) {
                        newEnergy = maxEnergy;
                    }
                    if (network.getEnergy() != newEnergy) {
                        // @todo 1.16
//                        network.setEnergy(newEnergy);
                        generatorNetwork.save();
                    }

                    active = true;
                } else {
                    // @todo 1.16
//                    network.setLastRfPerTick(0);
                }
                startup = network.getStartupCounter();
            }
        } else {
            if (networkID != -1) {
                networkID = -1;
                setChanged();
            }
        }

        if (active != lasersActive || startup != laserStartup) {
            // Only when active changes and the lasert started recently do we check for new crystals.
            boolean doFind = lasersActive != active || (laserStartup > (GeneratorModule.generatorConfig.startupTime.get() - 5));
            lasersActive = active;
            laserStartup = startup;
            markDirtyClient();

            if (doFind && te instanceof TileEntityGeneratorPart) {
                findCrystals(network);
            }
        }
    }

    public void disableCrystalGlow() {
        for (BlockPos coordinate : crystals) {
            TileEntity te = level.getBlockEntity(new BlockPos(getBlockPos().getX() + coordinate.getX(), getBlockPos().getY() + coordinate.getY(), getBlockPos().getZ() + coordinate.getZ()));
            if (te instanceof TileEntityResonatingCrystal) {
                TileEntityResonatingCrystal resonatingCrystalTileEntity = (TileEntityResonatingCrystal) te;
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
            TileEntity te = getLevel().getBlockEntity(new BlockPos(getBlockPos().getX() + coordinate.getX(), getBlockPos().getY() + coordinate.getY(), getBlockPos().getZ() + coordinate.getZ()));
            if (te instanceof TileEntityResonatingCrystal) {
                TileEntityResonatingCrystal crystal = (TileEntityResonatingCrystal) te;
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
                            // @todo 1.16
//                            if (crystal.getResistance() < SuperGenerationConfiguration.maxResistance) {
//                                float factor = (float) crystal.getResistance() / SuperGenerationConfiguration.maxResistance;
//                                strength = strength / factor;
//                            }
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
            DRRadiationManager radiationManager = DRRadiationManager.getManager(getLevel());
            GlobalCoordinate thisCoordinate = new GlobalCoordinate(getBlockPos(), level);
            if (radiationManager.getRadiationSource(thisCoordinate) == null) {
                Logging.log("Created radiation source with radius " + radiationRadius + " and strength " + radiationStrength);
            }
            DRRadiationManager.RadiationSource radiationSource = radiationManager.getOrCreateRadiationSource(thisCoordinate);
            radiationSource.update(radiationRadius, radiationStrength, MAXTICKS);
            radiationManager.save();
        }

        return rf;
    }

    private void findCrystals(DRGeneratorNetwork.Network network) {
        Set<BlockPos> newCrystals = new HashSet<>();

        int maxSupportedRF = network.getGeneratorBlocks() * GeneratorModule.generatorConfig.maxPowerInputPerBlock.get();

        boolean tooManyCrystals = false;
        boolean tooMuchPower = false;

        // @todo this is not a good algorithm. It should find the closest first.
        int xCoord = getBlockPos().getX();
        int yCoord = getBlockPos().getY();
        int zCoord = getBlockPos().getZ();
        for (int y = yCoord - GeneratorModule.collectorConfig.maxVerticalCrystalDistance.get() ; y <= yCoord + GeneratorModule.collectorConfig.maxVerticalCrystalDistance.get() ; y++) {
            if (y >= 0 && y < getLevel().getHeight()) {
                int maxhordist = GeneratorModule.collectorConfig.maxHorizontalCrystalDistance.get();
                for (int x = xCoord - maxhordist; x <= xCoord + maxhordist; x++) {
                    for (int z = zCoord - maxhordist; z <= zCoord + maxhordist; z++) {
                        if (getLevel().getBlockState(new BlockPos(x, y, z)).getBlock() == CoreModule.RESONATING_CRYSTAL_BLOCK.get()) {
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
                Broadcaster.broadcast(getLevel(), xCoord, yCoord, zCoord, "There are too many crystals for this size generator!", 100);
            }
            if (tooMuchPower) {
                Broadcaster.broadcast(getLevel(), xCoord, yCoord, zCoord, "Some crystals are too powerful for this size generator!!", 100);
            }
        }

    }

    public void addCrystal(int x, int y, int z) {
        if (networkID == -1) {
            return;
        }

        DRGeneratorNetwork channels = DRGeneratorNetwork.getChannels(level);
        DRGeneratorNetwork.Network network = channels.getChannel(networkID);
        if (network == null) {
            return;
        }

        int maxSupportedRF = network.getGeneratorBlocks() * GeneratorModule.generatorConfig.maxPowerInputPerBlock.get();
        for (BlockPos coordinate : crystals) {
            TileEntity te = level.getBlockEntity(new BlockPos(getBlockPos().getX() + coordinate.getX(), getBlockPos().getY() + coordinate.getY(), getBlockPos().getZ() + coordinate.getZ()));
            if (te instanceof TileEntityResonatingCrystal) {
                TileEntityResonatingCrystal resonatingCrystalTileEntity = (TileEntityResonatingCrystal) te;
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
        int maxSupportedCrystals = network.getGeneratorBlocks() * GeneratorModule.generatorConfig.maxCrystalsPerBlock.get();

        TileEntity te = level.getBlockEntity(new BlockPos(x, y, z));
        if (te instanceof TileEntityResonatingCrystal) {
            TileEntityResonatingCrystal resonatingCrystalTileEntity = (TileEntityResonatingCrystal) te;
            if (resonatingCrystalTileEntity.getPower() > CRYSTAL_MIN_POWER) {
                BlockPos crystalCoordinate = new BlockPos(x - getBlockPos().getX(), y - getBlockPos().getY(), z - getBlockPos().getZ());
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


    @Override
    public CompoundNBT save(CompoundNBT tagCompound) {
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

        tagCompound.putByteArray("crystalsX", crystalX);
        tagCompound.putByteArray("crystalsY", crystalY);
        tagCompound.putByteArray("crystalsZ", crystalZ);
        tagCompound.putBoolean("lasersActive", lasersActive);
        tagCompound.putInt("laserStartup", laserStartup);
        tagCompound.putInt("networkId", networkID);
        return super.save(tagCompound);
    }

    @Override
    public void read(CompoundNBT tagCompound) {
        super.read(tagCompound);
        byte[] crystalX = tagCompound.getByteArray("crystalsX");
        byte[] crystalY = tagCompound.getByteArray("crystalsY");
        byte[] crystalZ = tagCompound.getByteArray("crystalsZ");
        crystals.clear();
        for (int i = 0 ; i < crystalX.length ; i++) {
            crystals.add(new BlockPos(crystalX[i], crystalY[i], crystalZ[i]));
        }
        lasersActive = tagCompound.getBoolean("lasersActive");
        laserStartup = tagCompound.getInt("laserStartup");
        if (tagCompound.contains("networkId")) {
            networkID = tagCompound.getInt("networkId");
        } else {
            networkID = -1;
        }
    }

    public Set<BlockPos> getCrystals() {
        return crystals;
    }

}
