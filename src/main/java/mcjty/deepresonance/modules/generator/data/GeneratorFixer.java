package mcjty.deepresonance.modules.generator.data;

import mcjty.lib.multiblock.IMultiblockFixer;
import mcjty.lib.multiblock.MultiblockDriver;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Set;

public class GeneratorFixer implements IMultiblockFixer<GeneratorNetwork> {

    @Override
    public void initialize(MultiblockDriver<GeneratorNetwork> driver, World level, GeneratorNetwork newMb, int id) {
    }

    @Override
    public void blockAdded(MultiblockDriver<GeneratorNetwork> driver, World level, BlockPos pos,
                           int id, @Nonnull GeneratorNetwork newMb) {
        GeneratorNetwork existingMb = driver.get(id);
        if (existingMb == null) {
            throw new RuntimeException("Something awful went wrong!");
        }
        GeneratorNetwork merged = GeneratorNetwork.builder().network(existingMb)
                .addGeneratorBlocks(newMb.getGeneratorBlocks())
                .addEnergy(newMb.getEnergy())
                .collectorBlocks(-1)
                .build();
        driver.createOrUpdate(id, merged);
    }

    @Override
    public void merge(MultiblockDriver<GeneratorNetwork> driver, World level, Set<GeneratorNetwork> mbs, int masterId) {
        GeneratorNetwork.Builder builder = GeneratorNetwork.builder();
        mbs.forEach(builder::merge);
        builder.collectorBlocks(-1);
        driver.createOrUpdate(masterId, builder.build());
    }

    @Override
    public void distribute(MultiblockDriver<GeneratorNetwork> driver, World level, GeneratorNetwork original, List<Pair<Integer, Set<BlockPos>>> todo) {
        int totalEnergy = original.getEnergy();
        int totalBlocks = original.getGeneratorBlocks();
        int energy = totalEnergy / totalBlocks;
        int remainder = totalEnergy % totalBlocks;

        int energyToGive = energy + remainder;
        for (Pair<Integer, Set<BlockPos>> pair : todo) {
            GeneratorNetwork.Builder builder = GeneratorNetwork.builder();
            int generatorBlocks = pair.getRight().size();
            GeneratorNetwork mb = builder.generatorBlocks(generatorBlocks)
                    .collectorBlocks(-1)    // Set to -1 to mark invalid
                    .energy(energyToGive)
                    .build();
            driver.createOrUpdate(pair.getKey(), mb);
            energyToGive = energy;
        }
    }
}
