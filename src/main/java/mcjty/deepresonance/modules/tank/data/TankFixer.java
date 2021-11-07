package mcjty.deepresonance.modules.tank.data;

import mcjty.lib.multiblock.IMultiblockFixer;
import mcjty.lib.multiblock.MultiblockDriver;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Set;

public class TankFixer implements IMultiblockFixer<TankBlob> {

    @Override
    public void initialize(MultiblockDriver<TankBlob> driver, World level, TankBlob newMb, int id) {
    }

    @Override
    public void merge(MultiblockDriver<TankBlob> driver, World level, TankBlob mbMain, TankBlob mbOther) {
        mbMain.merge(mbOther);
    }

    @Override
    public void distribute(MultiblockDriver<TankBlob> driver, World level, TankBlob original, List<Pair<Integer, Set<BlockPos>>> todo) {
        original.getData().ifPresent(data -> {
            int totalAmount = data.getAmount();
            int totalBlocks = original.getTankBlocks();

            int amountPerBlock = totalAmount / totalBlocks;
            int remainder = totalAmount % totalBlocks;

            for (Pair<Integer, Set<BlockPos>> pair : todo) {
                TankBlob builder = new TankBlob();
                int generatorBlocks = pair.getRight().size();
                data.setAmount(remainder + amountPerBlock * generatorBlocks);
                TankBlob mb = builder.setTankBlocks(generatorBlocks)
                        .copyData(data);
                mb.updateDistribution(pair.getRight());
                driver.createOrUpdate(pair.getKey(), mb);
                remainder = 0;
            }
        });
    }
}
