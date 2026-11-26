package mcjty.deepresonance;

import mcjty.deepresonance.modules.core.CoreModule;
import mcjty.deepresonance.modules.core.block.ResonatingCrystalTileEntity;
import mcjty.deepresonance.modules.core.data.Crystal;
import mcjty.deepresonance.modules.generator.GeneratorModule;
import mcjty.deepresonance.modules.generator.block.GeneratorPartTileEntity;
import mcjty.deepresonance.modules.machines.MachinesModule;
import mcjty.deepresonance.modules.machines.block.LaserTileEntity;
import mcjty.deepresonance.modules.radiation.RadiationModule;
import mcjty.deepresonance.modules.radiation.manager.DRRadiationManager;
import mcjty.deepresonance.modules.tank.TankModule;
import mcjty.deepresonance.modules.tank.blocks.TankTileEntity;
import mcjty.deepresonance.modules.tank.util.TankTransfers;
import mcjty.deepresonance.util.LiquidCrystalData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import mcjty.lib.container.IndexedStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import team.reborn.energy.api.EnergyStorage;

public final class ResonanceTests {

    private ResonanceTests() {
    }

    // The two Deep Resonance stations from the 1.20.1 McJty Mod Lab.
    public static void purifierAndBothLasersChangeRcl(GameTestHelper test) {
        BlockPos outputPos = new BlockPos(8, 1, 8);
        BlockPos purifier = outputPos.above();
        BlockPos inputPos = purifier.above();
        place(test, outputPos, TankModule.TANK.block().get());
        place(test, inputPos, TankModule.TANK.block().get());
        test.setBlock(purifier, MachinesModule.PURIFIER.block().get());
        insertItem(test, purifier, 0, new ItemStack(CoreModule.FILTER_MATERIAL_ITEM.get(), 32));

        var input = test.getBlockEntity(inputPos, TankTileEntity.class).getFluidHandler();
        var output = test.getBlockEntity(outputPos, TankTileEntity.class).getFluidHandler();
        var raw = LiquidCrystalData.makeLiquidCrystalStack(8000, 1.0f, 0.30f, 0.20f, 0.15f);
        test.assertValueEqual(TankTransfers.canInsert(input, raw), 8000, "tank can accept the raw RCL");
        test.assertTrue(TankTransfers.contents(input).isEmpty(), "tank simulation rolls back");
        test.assertValueEqual(TankTransfers.insert(input, raw), 8000, "tank accepts raw RCL");

        BlockPos redstone = outputPos.west(3);
        BlockPos quartz = outputPos.north(3);
        laser(test, redstone, Direction.EAST, Items.REDSTONE);
        laser(test, quartz, Direction.SOUTH, Items.QUARTZ);
        test.startSequence()
                .thenIdle(60)
                .thenExecute(() -> {
                    test.assertValueEqual(itemCount(test, redstone, LaserTileEntity.SLOT_CATALYST), 16L, "unpowered redstone laser preserves catalyst");
                    test.assertValueEqual(itemCount(test, quartz, LaserTileEntity.SLOT_CATALYST), 16L, "unpowered quartz laser preserves catalyst");
                    test.setBlock(redstone.below(), Blocks.REDSTONE_BLOCK);
                    test.setBlock(quartz.below(), Blocks.REDSTONE_BLOCK);
                })
                .thenWaitUntil(() -> {
                    var processed = LiquidCrystalData.fromStack(TankTransfers.contents(output));
                    test.assertTrue(TankTransfers.contents(input).getAmount() < 8000 && processed.getAmount() > 0, "purifier moves RCL");
                    test.assertTrue(processed.getPurity() > 0.30, "purifier increases purity");
                    test.assertTrue(processed.getStrength() > 0.20 && processed.getEfficiency() > 0.15, "both lasers improve the RCL");
                })
                .thenSucceed();
    }

    public static void crystalsGenerateRfInsideRadiationShield(GameTestHelper test) {
        var level = test.getLevel();
        BlockPos controller = new BlockPos(5, 1, 4);
        BlockPos part = new BlockPos(5, 1, 8);
        BlockPos collector = part.above();
        BlockPos crystal = part.east(2);
        Block shield = RadiationModule.DENSE_OBSIDIAN.block().get();
        for (BlockPos wall : BlockPos.betweenClosed(new BlockPos(3, 1, 5), new BlockPos(3, 5, 12))) {
            test.setBlock(wall, shield);
        }
        test.setBlock(controller, GeneratorModule.GENERATOR_CONTROLLER.block().get());
        for (int z = 5; z <= 8; z++) {
            place(test, new BlockPos(5, 1, z), GeneratorModule.GENERATOR_PART.block().get());
        }
        place(test, collector, GeneratorModule.ENERGY_COLLECTOR.block().get());
        test.setBlock(crystal, CoreModule.RESONATING_CRYSTAL_GENERATED.block().get());
        var crystalTile = test.getBlockEntity(crystal, ResonatingCrystalTileEntity.class);
        crystalTile.setPurity(90);
        crystalTile.setStrength(44);
        crystalTile.setEfficiency(5);
        crystalTile.setPower(100);
        test.setBlock(controller.west(), Blocks.REDSTONE_BLOCK);

        var partTile = test.getBlockEntity(part, GeneratorPartTileEntity.class);
        BlockPos absoluteCollector = test.absolutePos(collector);
        test.succeedWhen(() -> {
            test.assertTrue(partTile.getBlob() != null && partTile.getBlob().getEnergy() > 0, "crystal supplies energy to the generator");
            test.assertTrue(crystalTile.isGlowing(), "collector activates the crystal");
            var source = DRRadiationManager.getManager(level).getRadiationSource(GlobalPos.of(level.dimension(), absoluteCollector));
            test.assertTrue(source != null, "collector creates a radiation source");
            int x = absoluteCollector.getX();
            int y = absoluteCollector.getY();
            int z = absoluteCollector.getZ();
            int targetX = test.absolutePos(collector.west(3)).getX();
            int targetZ = test.absolutePos(collector.west(3)).getZ();
            double factor = source.getRadiationTree(level, x, y, z).factor(x, y, z, targetX, y, targetZ);
            test.assertTrue(factor < 0.1, "dense obsidian absorbs over 90% of radiation");
        });
    }

    private static void laser(GameTestHelper test, BlockPos pos, Direction facing, Item catalyst) {
        test.setBlock(pos, MachinesModule.LASER.block().get().defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, facing));
        test.setBlock(pos.relative(facing, 2), MachinesModule.LENS.block().get().defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, facing));
        ItemStack crystal = new ItemStack(CoreModule.RESONATING_CRYSTAL_GENERATED.item().get());
        crystal.set(CoreModule.ITEM_CRYSTAL_DATA.get(), new Crystal(100, 65, 45, 8));
        insertItem(test, pos, LaserTileEntity.SLOT_CRYSTAL, crystal);
        insertItem(test, pos, LaserTileEntity.SLOT_CATALYST, new ItemStack(catalyst, 16));
        charge(test, pos);
    }

    private static void place(GameTestHelper test, BlockPos relative, Block block) {
        test.setBlock(relative, block);
        var level = test.getLevel();
        var pos = test.absolutePos(relative);
        block.setPlacedBy(level, pos, level.getBlockState(pos), test.makeMockPlayer(GameType.SURVIVAL), new ItemStack(block));
    }

    private static IndexedStorage<ItemVariant> items(GameTestHelper test, BlockPos pos) {
        return (IndexedStorage<ItemVariant>) ItemStorage.SIDED.find(test.getLevel(), test.absolutePos(pos), null);
    }

    private static void insertItem(GameTestHelper test, BlockPos pos, int slot, ItemStack stack) {
        var items = items(test, pos);
        test.assertTrue(items != null, "machine accepts items");
        try (Transaction transaction = Transaction.openOuter()) {
            test.assertValueEqual(items.insert(slot, ItemVariant.of(stack), stack.getCount(), transaction), (long) stack.getCount(), "fixture inserts items");
            transaction.commit();
        }
    }

    private static long itemCount(GameTestHelper test, BlockPos pos, int slot) {
        return items(test, pos).getAmountAsLong(slot);
    }

    private static void charge(GameTestHelper test, BlockPos pos) {
        var energy = EnergyStorage.SIDED.find(test.getLevel(), test.absolutePos(pos), null);
        test.assertTrue(energy != null, "laser accepts energy");
        for (int i = 0; i < 80; i++) {
            try (Transaction transaction = Transaction.openOuter()) {
                energy.insert(1000, transaction);
                transaction.commit();
            }
        }
    }
}
