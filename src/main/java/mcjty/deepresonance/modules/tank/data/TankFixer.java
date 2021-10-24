package mcjty.deepresonance.modules.tank.data;

import mcjty.lib.multiblock.IMultiblockFixer;
import mcjty.lib.multiblock.MultiblockDriver;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Set;

public class TankFixer implements IMultiblockFixer<TankBlob> {

    @Override
    public void initialize(MultiblockDriver<TankBlob> driver, World level, TankBlob newMb, int id) {
    }

    @Override
    public void blockAdded(MultiblockDriver<TankBlob> driver, World level, BlockPos pos,
                           int id, @Nonnull TankBlob newMb) {
        TankBlob existingMb = driver.get(id);
        if (existingMb == null) {
            throw new RuntimeException("Something awful went wrong!");
        }
        TankBlob merged = TankBlob.builder().network(existingMb)
                .addGeneratorBlocks(newMb.getGeneratorBlocks())
//                .addEnergy(newMb.getEnergy())
//                .collectorBlocks(-1)
                // @todo 1.16 MERGE
                .build();
        driver.createOrUpdate(id, merged);
    }

    @Override
    public void merge(MultiblockDriver<TankBlob> driver, World level, Set<TankBlob> mbs, int masterId) {
        TankBlob.Builder builder = TankBlob.builder();
        // @todo 1.16
//        mbs.forEach(builder::merge);
//        builder.collectorBlocks(-1);
        driver.createOrUpdate(masterId, builder.build());
    }

    @Override
    public void distribute(MultiblockDriver<TankBlob> driver, World level, TankBlob original, List<Pair<Integer, Set<BlockPos>>> todo) {
        // @todo 1.16
//        int totalEnergy = original.getEnergy();
//        int totalBlocks = original.getGeneratorBlocks();
//        int energy = totalEnergy / totalBlocks;
//        int remainder = totalEnergy % totalBlocks;
//
//        int energyToGive = energy + remainder;
//        for (Pair<Integer, Set<BlockPos>> pair : todo) {
//            TankBlob.Builder builder = TankBlob.builder();
//            int generatorBlocks = pair.getRight().size();
//            TankBlob mb = builder.generatorBlocks(generatorBlocks)
//                    .collectorBlocks(-1)    // Set to -1 to mark invalid
//                    .energy(energyToGive)
//                    .build();
//            driver.createOrUpdate(pair.getKey(), mb);
//            energyToGive = energy;
//        }
    }
}
