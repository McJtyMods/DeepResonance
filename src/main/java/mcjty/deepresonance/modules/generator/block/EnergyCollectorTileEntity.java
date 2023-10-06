package mcjty.deepresonance.modules.generator.block;

import mcjty.deepresonance.modules.core.block.ResonatingCrystalBlock;
import mcjty.deepresonance.modules.core.block.ResonatingCrystalTileEntity;
import mcjty.deepresonance.modules.generator.GeneratorModule;
import mcjty.deepresonance.modules.generator.data.DRGeneratorNetwork;
import mcjty.deepresonance.modules.generator.data.GeneratorBlob;
import mcjty.deepresonance.modules.generator.util.CollectorConfig;
import mcjty.deepresonance.modules.generator.util.GeneratorConfig;
import mcjty.deepresonance.modules.radiation.manager.DRRadiationManager;
import mcjty.lib.multiblock.MultiblockDriver;
import mcjty.lib.tileentity.TickingTileEntity;
import mcjty.lib.varia.Broadcaster;
import mcjty.lib.varia.Logging;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Set;

public class EnergyCollectorTileEntity extends TickingTileEntity {

    // Minimum power before we stop using a crystal.
    public static final float CRYSTAL_MIN_POWER = .00001f;

    public static final int MAXTICKS = 20;        // Update radiation every second.

    // Relative coordinates of active crystals.
    private Set<BlockPos> crystals = new HashSet<>();
    private boolean lasersActive = false;
    private int laserStartup = 0;        // A mirror (for the client) of the network startup counter.
    private int radiationUpdateCount = MAXTICKS;
    private int blobId = -1;

    public EnergyCollectorTileEntity(BlockPos pos, BlockState state) {
        super(GeneratorModule.TYPE_ENERGY_COLLECTOR.get(), pos, state);
    }

    @Override
    public void tickServer() {
        boolean active = false;
        int startup = 0;
        GeneratorBlob network = null;

        BlockEntity te = level.getBlockEntity(getBlockPos().below());
        if (te instanceof GeneratorPartTileEntity generator) {
            DRGeneratorNetwork generatorNetwork = DRGeneratorNetwork.getNetwork(level);

            if (blobId != generator.getMultiblockId()) {
                if (blobId != -1) {
                    getDriver().modify(blobId, holder -> holder.getMb().setCollectorBlocks(-1));
                }

                blobId = generator.getMultiblockId();
                getDriver().modify(blobId, holder -> holder.getMb().setCollectorBlocks(-1));
                generatorNetwork.save();
            }


            int multiblockId = generator.getMultiblockId();
            network = generator.getBlob();
            if (network != null) {
                if (network.isActive()) {
                    getDriver().modify(multiblockId, holder -> {
                        int rfPerTick = calculateRF();
                        holder.getMb().setLastRfPerTick(rfPerTick);
                        int newEnergy = holder.getMb().getEnergy() + rfPerTick;
                        int maxEnergy = holder.getMb().getGeneratorBlocks() * GeneratorConfig.POWER_STORAGE_PER_BLOCK.get();
                        if (newEnergy > maxEnergy) {
                            newEnergy = maxEnergy;
                        }
                        if (holder.getMb().getEnergy() != newEnergy) {
                            holder.getMb().setEnergy(newEnergy);
                            generatorNetwork.save();
                        }
                    });

                    active = true;
                } else {
                    getDriver().modify(multiblockId, holder -> holder.getMb().setLastRfPerTick(0));
                }
                startup = network.getStartupCounter();
            }
        } else {
            if (blobId != -1) {
                blobId = -1;
                setChanged();
            }
        }

        if (active != lasersActive || startup != laserStartup) {
            // Only when active changes and the lasert started recently do we check for new crystals.
            boolean doFind = lasersActive != active || (laserStartup > (GeneratorConfig.STARTUP_TIME.get() - 5));
            lasersActive = active;
            laserStartup = startup;
            markDirtyClient();

            if (doFind && te instanceof GeneratorPartTileEntity) {
                findCrystals(network);
            }
        }
    }

    public void disableCrystalGlow() {
        for (BlockPos coordinate : crystals) {
            BlockEntity te = level.getBlockEntity(new BlockPos(getBlockPos().getX() + coordinate.getX(), getBlockPos().getY() + coordinate.getY(), getBlockPos().getZ() + coordinate.getZ()));
            if (te instanceof ResonatingCrystalTileEntity crystal) {
                crystal.setGlowing(false);
            }
        }
    }

    public int getLaserStartup() {
        return laserStartup;
    }

    public boolean areLasersActive() {
        return lasersActive;
    }

    private MultiblockDriver<GeneratorBlob> getDriver() {
        return DRGeneratorNetwork.getNetwork(level).getDriver();
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
            BlockEntity te = getLevel().getBlockEntity(new BlockPos(getBlockPos().getX() + coordinate.getX(), getBlockPos().getY() + coordinate.getY(), getBlockPos().getZ() + coordinate.getZ()));
            if (te instanceof ResonatingCrystalTileEntity crystal) {
                if (crystal.getPower() > CRYSTAL_MIN_POWER) {
                    crystal.setGlowing(lasersActive);
                    tokeep.add(coordinate);

                    double power = crystal.getPower();
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
                            double purity = crystal.getPurity();
                            float radius = DRRadiationManager.calculateRadiationRadius(crystal.getStrength(), crystal.getEfficiency(), purity);
                            if (radius > radiationRadius) {
                                radiationRadius = radius;
                            }
                            float strength = DRRadiationManager.calculateRadiationStrength(crystal.getStrength(), purity);
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
            setChanged();
        }

        if (doRadiation && radiationRadius > 0.1f) {
            DRRadiationManager radiationManager = DRRadiationManager.getManager(getLevel());
            GlobalPos thisCoordinate = GlobalPos.of(level.dimension(), getBlockPos());
            if (radiationManager.getRadiationSource(thisCoordinate) == null) {
                Logging.log("Created radiation source with radius " + radiationRadius + " and strength " + radiationStrength);
            }
            DRRadiationManager.RadiationSource radiationSource = radiationManager.getOrCreateRadiationSource(thisCoordinate);
            radiationSource.update(radiationRadius, radiationStrength, MAXTICKS);
            radiationManager.save();
        }

        return rf;
    }

    @Override
    public void onBlockPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        if (!world.isClientSide()) {
            BlockEntity te = world.getBlockEntity(pos.below());
            if (te instanceof GeneratorPartTileEntity) {
                blobId = ((GeneratorPartTileEntity) te).getMultiblockId();
                getDriver().modify(blobId, holder -> holder.getMb().setCollectorBlocks(-1));
            }
        }
    }

    @Override
    public void onReplaced(Level world, BlockPos pos, BlockState state, BlockState newstate) {
        BlockEntity te = world.getBlockEntity(pos.below());
        if (te instanceof GeneratorPartTileEntity) {
            int id = ((GeneratorPartTileEntity) te).getMultiblockId();
            getDriver().modify(id, holder -> holder.getMb().setCollectorBlocks(-1));
        }
    }

    private void findCrystals(GeneratorBlob network) {
        Set<BlockPos> newCrystals = new HashSet<>();

        int maxSupportedRF = network.getGeneratorBlocks() * GeneratorConfig.MAX_POWER_INPUT_PER_BLOCK.get();

        boolean tooManyCrystals = false;
        boolean tooMuchPower = false;

        // @todo this is not a good algorithm. It should find the closest first.
        int xCoord = getBlockPos().getX();
        int yCoord = getBlockPos().getY();
        int zCoord = getBlockPos().getZ();
        for (int y = yCoord - CollectorConfig.MAX_VERTICAL_CRYSTAL_DISTANCE.get(); y <= yCoord + CollectorConfig.MAX_VERTICAL_CRYSTAL_DISTANCE.get() ; y++) {
            if (y >= level.getMinBuildHeight() && y < level.getMaxBuildHeight()) {
                int maxhordist = CollectorConfig.MAX_HORIZONTAL_CRYSTAL_DISTANCE.get();
                for (int x = xCoord - maxhordist; x <= xCoord + maxhordist; x++) {
                    for (int z = zCoord - maxhordist; z <= zCoord + maxhordist; z++) {
                        if (level.getBlockState(new BlockPos(x, y, z)).getBlock() instanceof ResonatingCrystalBlock) {
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
            setChanged();
        }

        if (lasersActive && (tooManyCrystals || tooMuchPower)) {
            if (tooManyCrystals) {
                Broadcaster.broadcast(getLevel(), xCoord, yCoord, zCoord, "There are too many crystals for this size generator!", 100);
            }
            if (tooMuchPower) {
                Broadcaster.broadcast(getLevel(), xCoord, yCoord, zCoord, "Some crystals are too powerful for this size generator!!", 100);
            }
        }

    }

    public void addCrystal(int x, int y, int z) {
        if (blobId == -1) {
            return;
        }

        DRGeneratorNetwork channels = DRGeneratorNetwork.getNetwork(level);
        GeneratorBlob blob = channels.getBlob(blobId);
        if (blob == null) {
            return;
        }

        int maxSupportedRF = blob.getGeneratorBlocks() * GeneratorConfig.MAX_POWER_INPUT_PER_BLOCK.get();
        for (BlockPos coordinate : crystals) {
            BlockEntity te = level.getBlockEntity(new BlockPos(getBlockPos().getX() + coordinate.getX(), getBlockPos().getY() + coordinate.getY(), getBlockPos().getZ() + coordinate.getZ()));
            if (te instanceof ResonatingCrystalTileEntity crystal) {
                if (crystal.getPower() > CRYSTAL_MIN_POWER) {
                    maxSupportedRF -= crystal.getRfPerTick();
                }
            }
        }

        if (addCrystal(x, y, z, blob, crystals, crystals, maxSupportedRF) >= 0) {
            // Success.
            setChanged();
        }
    }

    private static final int ERROR_TOOMANYCRYSTALS = -1;
    private static final int ERROR_TOOMUCHPOWER = -2;

    // Returns remaining RF that is supported if crystal could be added. Otherwise one of the errors above.
    private int addCrystal(int x, int y, int z, GeneratorBlob network, Set<BlockPos> newCrystals, Set<BlockPos> oldCrystals, int maxSupportedRF) {
        int maxSupportedCrystals = network.getGeneratorBlocks() * GeneratorConfig.MAX_CRYSTALS_PER_BLOCK.get();

        BlockEntity te = level.getBlockEntity(new BlockPos(x, y, z));
        if (te instanceof ResonatingCrystalTileEntity crystal) {
            if (crystal.getPower() > CRYSTAL_MIN_POWER) {
                BlockPos crystalCoordinate = new BlockPos(x - getBlockPos().getX(), y - getBlockPos().getY(), z - getBlockPos().getZ());
                if (crystal.isGlowing() && !oldCrystals.contains(crystalCoordinate)) {
                    // The crystal is already glowing and is not in our 'old' crystal set. That means that it
                    // is currently being managed by another generator. We ignore it then.
                    return maxSupportedRF;
                }

                if (newCrystals.size() >= maxSupportedCrystals) {
                    crystal.setGlowing(false);
                    return ERROR_TOOMANYCRYSTALS;
                } else if (crystal.getRfPerTick() > maxSupportedRF) {
                    crystal.setGlowing(false);
                    return ERROR_TOOMUCHPOWER;
                } else {
                    maxSupportedRF -= crystal.getRfPerTick();
                    newCrystals.add(crystalCoordinate);
                    crystal.setGlowing(lasersActive);
                }
            } else {
                crystal.setGlowing(false);
            }
        }
        return maxSupportedRF;
    }


    @Override
    public void saveAdditional(@Nonnull CompoundTag tagCompound) {
        saveClientDataToNBT(tagCompound);
        tagCompound.putInt("networkId", blobId);
        super.saveAdditional(tagCompound);
    }

    @Override
    public void load(CompoundTag tagCompound) {
        super.load(tagCompound);
        loadClientDataFromNBT(tagCompound);
        if (tagCompound.contains("networkId")) {
            blobId = tagCompound.getInt("networkId");
        } else {
            blobId = -1;
        }
    }

    @Override
    public void loadClientDataFromNBT(CompoundTag tagCompound) {
        lasersActive = tagCompound.getBoolean("lasersActive");
        laserStartup = tagCompound.getInt("laserStartup");
        byte[] crystalX = tagCompound.getByteArray("crystalsX");
        byte[] crystalY = tagCompound.getByteArray("crystalsY");
        byte[] crystalZ = tagCompound.getByteArray("crystalsZ");
        crystals.clear();
        for (int i = 0 ; i < crystalX.length ; i++) {
            crystals.add(new BlockPos(crystalX[i], crystalY[i], crystalZ[i]));
        }
    }

    @Override
    public void saveClientDataToNBT(CompoundTag tagCompound) {
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
    }

    public Set<BlockPos> getCrystals() {
        return crystals;
    }

}
