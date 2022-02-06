package mcjty.deepresonance.modules.generator.data;

import mcjty.deepresonance.DeepResonance;
import mcjty.lib.multiblock.IMultiblockConnector;
import mcjty.lib.multiblock.MultiblockDriver;
import mcjty.lib.worlddata.AbstractWorldData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nonnull;

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
                        BlockEntity be = world.getBlockEntity(blockPos);
                        if (be instanceof IMultiblockConnector && ((IMultiblockConnector) be).getId().equals(GENERATOR_NETWORK_ID)) {
                            return (IMultiblockConnector) be;
                        } else {
                            return null;
                        }
                    })
            .build();

    public DRGeneratorNetwork() {
    }

    public DRGeneratorNetwork(CompoundTag tag) {
        driver.load(tag);
    }

    public void clear() {
        driver.clear();
    }

    public MultiblockDriver<GeneratorBlob> getDriver() {
        return driver;
    }

    public static DRGeneratorNetwork getNetwork(Level world) {
        return getData(world, DRGeneratorNetwork::new, DRGeneratorNetwork::new, GENERATOR_NETWORK_NAME);
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

    @Nonnull
    @Override
    public CompoundTag save(@Nonnull CompoundTag tagCompound) {
        return driver.save(tagCompound);
    }

}
