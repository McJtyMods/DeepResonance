package mcjty.deepresonance.modules.generator.data;

import mcjty.lib.multiblock.IMultiblockFixer;
import mcjty.lib.multiblock.MultiblockDriver;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Set;

public class GeneratorFixer implements IMultiblockFixer<DRGeneratorNetwork.Network> {

    @Override
    public void initialize(MultiblockDriver<DRGeneratorNetwork.Network> driver, World level, DRGeneratorNetwork.Network newMb, int id) {
    }

    @Override
    public void blockAdded(MultiblockDriver<DRGeneratorNetwork.Network> driver, World level, BlockPos pos,
                           int id, DRGeneratorNetwork.Network newMb) {
        DRGeneratorNetwork.Network existingMb = driver.get(id);
        DRGeneratorNetwork.Network merged = new DRGeneratorNetwork.Network(
                existingMb.getGeneratorBlocks() + newMb.getGeneratorBlocks(),
                existingMb.getCollectorBlocks() + newMb.getCollectorBlocks(),
                existingMb.getEnergy() + newMb.getEnergy(),
                false, existingMb.getStartupCounter(), existingMb.getShutdownCounter(), existingMb.getLastRfPerTick()); // @todo is this correct?
        driver.createOrUpdate(id, merged);
    }

    @Override
    public void merge(MultiblockDriver<DRGeneratorNetwork.Network> driver, World level, Set<DRGeneratorNetwork.Network> mbs, int masterId, DRGeneratorNetwork.Network newData) {

    }

    @Override
    public void distribute(MultiblockDriver<DRGeneratorNetwork.Network> driver, World level, DRGeneratorNetwork.Network original, List<Pair<DRGeneratorNetwork.Network, Set<BlockPos>>> todo) {

    }
}
