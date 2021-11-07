package mcjty.deepresonance.modules.tank.data;

import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.util.LiquidCrystalData;
import mcjty.lib.multiblock.IMultiblockConnector;
import mcjty.lib.multiblock.MultiblockDriver;
import mcjty.lib.varia.OrientationTools;
import mcjty.lib.worlddata.AbstractWorldData;
import net.minecraft.fluid.Fluid;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

public class DRTankNetwork extends AbstractWorldData<DRTankNetwork> {

    private static final String TANK_NETWORK_NAME = "DRTankNetwork";
    public static final ResourceLocation TANK_NETWORK_ID = new ResourceLocation(DeepResonance.MODID, "tank");

    private final MultiblockDriver<TankBlob> driver = MultiblockDriver.<TankBlob>builder()
            .loader(TankBlob::load)
            .saver(TankBlob::save)
            .dirtySetter(d -> setDirty())
            .mergeChecker((b1, b2) -> isCompatible(b1.getData(), b2.getData()))
            .fixer(new TankFixer())
            .holderGetter(
                    (world, blockPos) -> {
                        TileEntity be = world.getBlockEntity(blockPos);
                        if (be instanceof IMultiblockConnector) {
                            IMultiblockConnector connector = (IMultiblockConnector) be;
                            if (TANK_NETWORK_ID.equals(connector.getId())) {
                                return connector;
                            }
                        }
                        return null;
                    })
            .build();

    public static void foreach(World level, int blobId, Consumer<BlockPos> consumer, BlockPos current) {
        MultiblockDriver<TankBlob> driver = getNetwork(level).getDriver();
        foreach(level, blobId, consumer, driver, current, new HashSet<>());
    }

    private static void foreach(World level, int blobId, Consumer<BlockPos> consumer, MultiblockDriver<TankBlob> driver, BlockPos current, Set<BlockPos> done) {
        if (done.contains(current)) {
            return;
        }
        IMultiblockConnector connector = driver.getHolderGetter().apply(level, current);
        if (connector != null && connector.getId() == TANK_NETWORK_ID && connector.getMultiblockId() == blobId) {
            done.add(current);
            consumer.accept(current);
            for (Direction direction : OrientationTools.DIRECTION_VALUES) {
                foreach(level, blobId, consumer, driver, current.relative(direction), done);
            }
        }
    }

    public static boolean isCompatible(@Nonnull Optional<LiquidCrystalData> data1, @Nonnull Optional<LiquidCrystalData> data2) {
        // If one liquid is empty then it is compatible
        if (!data1.isPresent() || !data2.isPresent() || data1.get().getStack().isEmpty() || data2.get().getStack().isEmpty()) {
            return true;
        }
        Fluid fluid1 = data1.get().getStack().getFluid();
        Fluid fluid2 = data2.get().getStack().getFluid();
        return fluid1 == fluid2;
    }

    public DRTankNetwork(String name) {
        super(name);
    }

    public void clear() {
        driver.clear();
    }

    public MultiblockDriver<TankBlob> getDriver() {
        return driver;
    }

    public static DRTankNetwork getNetwork(World world) {
        return getData(world, () -> new DRTankNetwork(TANK_NETWORK_NAME), TANK_NETWORK_NAME);
    }

    public TankBlob getBlob(int id) {
        return driver.get(id);
    }

    public TankBlob getOrCreateBlob(int id) {
        TankBlob network = getBlob(id);
        if (network == null) {
            network = new TankBlob()
                    .setTankBlocks(0);
            driver.createOrUpdate(id, network);
        }
        return network;
    }

    public void deleteBlob(int id) {
        driver.delete(id);
    }

    public int newBlob() {
        return driver.createId();
    }

    @Override
    public void load(CompoundNBT tagCompound) {
        driver.load(tagCompound);
    }

    @Override
    public CompoundNBT save(CompoundNBT tagCompound) {
        return driver.save(tagCompound);
    }

}
