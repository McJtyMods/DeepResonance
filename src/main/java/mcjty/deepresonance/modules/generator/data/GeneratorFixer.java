package mcjty.deepresonance.modules.generator.data;

import mcjty.deepresonance.modules.generator.GeneratorModule;
import mcjty.lib.multiblock.IMultiblockFixer;
import mcjty.lib.multiblock.MultiblockDriver;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Set;

public class GeneratorFixer implements IMultiblockFixer<DRGeneratorNetwork.Network> {

    @Override
    public void initialize(MultiblockDriver<DRGeneratorNetwork.Network> driver, World level, DRGeneratorNetwork.Network newMb, int id) {
    }

    @Override
    public void blockAdded(MultiblockDriver<DRGeneratorNetwork.Network> driver, World level, BlockPos pos,
                           int id, @Nonnull DRGeneratorNetwork.Network newMb) {
        DRGeneratorNetwork.Network existingMb = driver.get(id);
        if (existingMb == null) {
            throw new RuntimeException("Something awful went wrong!");
        }
        // @todo check if this merge is correct
        DRGeneratorNetwork.Network merged = DRGeneratorNetwork.Network.builder().network(existingMb)
                .addGeneratorBlocks(newMb.getGeneratorBlocks())
                .addCollectorBlocks(newMb.getCollectorBlocks())
                .addEnergy(newMb.getEnergy())
                .build();
        driver.createOrUpdate(id, merged);
    }

    @Override
    public void merge(MultiblockDriver<DRGeneratorNetwork.Network> driver, World level, Set<DRGeneratorNetwork.Network> mbs, int masterId) {
        DRGeneratorNetwork.Network.Builder builder = DRGeneratorNetwork.Network.builder();
        mbs.forEach(builder::merge);
        driver.createOrUpdate(masterId, builder.build());
    }

    @Override
    public void distribute(MultiblockDriver<DRGeneratorNetwork.Network> driver, World level, DRGeneratorNetwork.Network original, List<Pair<Integer, Set<BlockPos>>> todo) {
        int totalEnergy = original.getEnergy();
        int totalBlocks = original.getGeneratorBlocks();
        int energy = totalEnergy / totalBlocks;
        int remainder = totalEnergy % totalBlocks;

        int energyToGive = energy + remainder;
        for (Pair<Integer, Set<BlockPos>> pair : todo) {
            DRGeneratorNetwork.Network.Builder builder = DRGeneratorNetwork.Network.builder();
            int generatorBlocks = (int) pair.getRight().stream().filter(p -> level.getBlockState(p).getBlock() == GeneratorModule.GENERATOR_PART_BLOCK.get()).count();
            int collectorBlocks = pair.getRight().size() - generatorBlocks;
            DRGeneratorNetwork.Network mb = builder.generatorBlocks(generatorBlocks)
                    .collectorBlocks(collectorBlocks)
                    .energy(energyToGive)
                    .build();
            driver.createOrUpdate(pair.getKey(), mb);
            energyToGive = energy;
        }
    }
}
