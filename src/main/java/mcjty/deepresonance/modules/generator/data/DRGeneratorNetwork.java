package mcjty.deepresonance.modules.generator.data;

import mcjty.lib.multiblock.IMultiblockConnector;
import mcjty.lib.multiblock.MultiblockDriver;
import mcjty.lib.worlddata.AbstractWorldData;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class DRGeneratorNetwork extends AbstractWorldData<DRGeneratorNetwork> {

    private static final String GENERATOR_NETWORK_NAME = "DRGeneratorNetwork";

    private final MultiblockDriver<GeneratorNetwork> driver = MultiblockDriver.<GeneratorNetwork>builder()
            .loader(GeneratorNetwork::load)
            .saver(GeneratorNetwork::save)
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

    public MultiblockDriver<GeneratorNetwork> getDriver() {
        return driver;
    }

    public static DRGeneratorNetwork getChannels(World world) {
        return getData(world, () -> new DRGeneratorNetwork(GENERATOR_NETWORK_NAME), GENERATOR_NETWORK_NAME);
    }

    public GeneratorNetwork getNetwork(int id) {
        return driver.get(id);
    }

    public GeneratorNetwork getOrCreateNetwork(int id) {
        GeneratorNetwork network = getNetwork(id);
        if (network == null) {
            network = GeneratorNetwork.builder()
                    .generatorBlocks(0)
                    .active(false)
                    .build();
            driver.createOrUpdate(id, network);
        }
        return network;
    }

    public GeneratorNetwork getChannel(int id) {
        return driver.get(id);
    }

    public void deleteChannel(int id) {
        driver.delete(id);
    }

    public int newChannel() {
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
