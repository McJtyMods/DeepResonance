package mcjty.deepresonance.modules.generator.data;

import mcjty.lib.multiblock.IMultiblockConnector;
import mcjty.lib.multiblock.MultiblockDriver;
import mcjty.lib.worlddata.AbstractWorldData;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class DRGeneratorNetwork extends AbstractWorldData<DRGeneratorNetwork> {

    private static final String GENERATOR_NETWORK_NAME = "DRGeneratorNetwork";

    private final MultiblockDriver<GeneratorBlob> driver = MultiblockDriver.<GeneratorBlob>builder()
            .loader(GeneratorBlob::load)
            .saver(GeneratorBlob::save)
            .dirtySetter(d -> setDirty())
            .fixer(new GeneratorFixer())
            .holderGetter(
                    (world, blockPos) -> {
                        TileEntity be = world.getBlockEntity(blockPos);
                        if (be instanceof IMultiblockConnector) {
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

    public static DRGeneratorNetwork getGeneratorNetwork(World world) {
        return getData(world, () -> new DRGeneratorNetwork(GENERATOR_NETWORK_NAME), GENERATOR_NETWORK_NAME);
    }

    public GeneratorBlob getBlob(int id) {
        return driver.get(id);
    }

    public GeneratorBlob getOrCreateBlob(int id) {
        GeneratorBlob network = getBlob(id);
        if (network == null) {
            network = GeneratorBlob.builder()
                    .generatorBlocks(0)
                    .active(false)
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
