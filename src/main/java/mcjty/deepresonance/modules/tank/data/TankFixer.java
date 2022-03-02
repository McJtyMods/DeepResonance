package mcjty.deepresonance.modules.tank.data;

import mcjty.deepresonance.util.LiquidCrystalData;
import mcjty.lib.multiblock.IMultiblockFixer;
import mcjty.lib.multiblock.MultiblockDriver;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Set;

public class TankFixer implements IMultiblockFixer<TankBlob> {

    @Override
    public void initialize(MultiblockDriver<TankBlob> driver, Level level, TankBlob newMb, int id) {
    }

    @Override
    public void merge(MultiblockDriver<TankBlob> driver, Level level, TankBlob mbMain, TankBlob mbOther) {
        mbMain.merge(mbOther);
    }

    @Override
    public void distribute(MultiblockDriver<TankBlob> driver, Level level, TankBlob original, List<Pair<Integer, Set<BlockPos>>> todo) {
        LiquidCrystalData data = original.getData();
        int totalAmount = data.getAmount();
        int totalBlocks = 0;
        for (var pair : todo) {
            totalBlocks += pair.getRight().size();
        }

        if (totalBlocks == 0) {
            return;
        }
        int amountPerBlock = totalAmount / totalBlocks;
        int remainder = totalAmount % totalBlocks;

        for (var pair : todo) {
            TankBlob builder = new TankBlob();
            int generatorBlocks = pair.getRight().size();
            data.setAmount(remainder + amountPerBlock * generatorBlocks);
            TankBlob mb = builder.setTankBlocks(generatorBlocks)
                    .copyData(data);
            mb.updateDistribution(pair.getRight());
            driver.createOrUpdate(pair.getKey(), mb);
            remainder = 0;
        }
    }
}
