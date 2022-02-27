package mcjty.deepresonance.modules.generator.data;

import mcjty.lib.multiblock.IMultiblockFixer;
import mcjty.lib.multiblock.MultiblockDriver;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Set;

public class GeneratorFixer implements IMultiblockFixer<GeneratorBlob> {

    @Override
    public void initialize(MultiblockDriver<GeneratorBlob> driver, World level, GeneratorBlob newMb, int id) {
    }

    @Override
    public void merge(MultiblockDriver<GeneratorBlob> driver, World level, GeneratorBlob mbMain, GeneratorBlob mbOther) {
        mbMain.merge(mbOther);
        mbMain.setCollectorBlocks(-1);
    }

    @Override
    public void distribute(MultiblockDriver<GeneratorBlob> driver, World level, GeneratorBlob original, List<Pair<Integer, Set<BlockPos>>> todo) {
        int totalEnergy = original.getEnergy();
        int totalBlocks = 0;
        for (Pair<Integer, Set<BlockPos>> pair : todo) {
            totalBlocks += pair.getRight().size();
        }
        if (totalBlocks == 0) {
            return;
        }
        int energyPerBlock = totalEnergy / totalBlocks;
        int remainder = totalEnergy % totalBlocks;

        for (Pair<Integer, Set<BlockPos>> pair : todo) {
            GeneratorBlob blob = new GeneratorBlob();
            int generatorBlocks = pair.getRight().size();
            GeneratorBlob mb = blob.setGeneratorBlocks(generatorBlocks)
                    .setCollectorBlocks(-1)    // Set to -1 to mark invalid
                    .setEnergy(remainder + energyPerBlock * generatorBlocks);
            driver.createOrUpdate(pair.getKey(), mb);
            remainder = 0;
        }
    }
}
