package mcjty.deepresonance.modules.generator.data;

import mcjty.deepresonance.DeepResonance;
import mcjty.lib.multiblock.IMultiblockConnector;
import mcjty.lib.multiblock.MultiblockDriver;
import mcjty.lib.worlddata.AbstractWorldData;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class DRGeneratorNetwork extends AbstractWorldData<DRGeneratorNetwork> {

    private static final String GENERATOR_NETWORK_NAME = "DRGeneratorNetwork";
    public static final ResourceLocation GENERATOR_NETWORK_ID = new ResourceLocation(DeepResonance.MODID, "generator");

    private final MultiblockDriver<GeneratorBlob> driver = MultiblockDriver.<GeneratorBlob>builder()
            .loader(GeneratorBlob::load)
            .saver(GeneratorBlob::save)
            .dirtySetter(d -> setDirty())
            .mergeChecker((b1, b2) -> true)
            .fixer(new GeneratorFixer())
            .holderGetter(
                    (world, blockPos) -> {
                        TileEntity be = world.getBlockEntity(blockPos);
                        if (be instanceof IMultiblockConnector && ((IMultiblockConnector) be).getId().equals(GENERATOR_NETWORK_ID)) {
                            return (IMultiblockConnector) be;
                        } else {
                            return null;
                        }
                    })
            .build();

    public DRGeneratorNetwork(String name) {
        super(name);
    }

    public void clear() {
        driver.clear();
    }

    public MultiblockDriver<GeneratorBlob> getDriver() {
        return driver;
    }

    public static DRGeneratorNetwork getNetwork(World world) {
        return getData(world, () -> new DRGeneratorNetwork(GENERATOR_NETWORK_NAME), GENERATOR_NETWORK_NAME);
    }

    public GeneratorBlob getBlob(int id) {
        return driver.get(id);
    }

    public GeneratorBlob getOrCreateBlob(int id) {
        GeneratorBlob network = getBlob(id);
        if (network == null) {
            network = new GeneratorBlob()
                    .setGeneratorBlocks(0)
                    .setActive(false);
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
