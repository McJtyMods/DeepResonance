package mcjty.deepresonance.modules.tank.data;

import mcjty.deepresonance.DeepResonance;
import mcjty.lib.multiblock.IMultiblockConnector;
import mcjty.lib.multiblock.MultiblockDriver;
import mcjty.lib.worlddata.AbstractWorldData;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class DRTankNetwork extends AbstractWorldData<DRTankNetwork> {

    private static final String TANK_NETWORK_NAME = "DRTankNetwork";
    public static final ResourceLocation TANK_NETWORK_ID = new ResourceLocation(DeepResonance.MODID, "tank");

    private final MultiblockDriver<TankBlob> driver = MultiblockDriver.<TankBlob>builder()
            .loader(TankBlob::load)
            .saver(TankBlob::save)
            .dirtySetter(d -> setDirty())
            .fixer(new TankFixer())
            .holderGetter(
                    (world, blockPos) -> {
                        TileEntity be = world.getBlockEntity(blockPos);
                        if (be instanceof IMultiblockConnector && ((IMultiblockConnector) be).getId().equals(TANK_NETWORK_ID)) {
                            return (IMultiblockConnector) be;
                        } else {
                            return null;
                        }
                    })
            .build();

    public DRTankNetwork(String name) {
        super(name);
    }

    public void clear() {
        driver.clear();
    }

    public MultiblockDriver<TankBlob> getDriver() {
        return driver;
    }

    public static DRTankNetwork getGeneratorNetwork(World world) {
        return getData(world, () -> new DRTankNetwork(TANK_NETWORK_NAME), TANK_NETWORK_NAME);
    }

    public TankBlob getBlob(int id) {
        return driver.get(id);
    }

    public TankBlob getOrCreateBlob(int id) {
        TankBlob network = getBlob(id);
        if (network == null) {
            network = TankBlob.builder()
                    .generatorBlocks(0)
                    .build();
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
