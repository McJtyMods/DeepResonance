package mcjty.deepresonance;

import com.mojang.authlib.GameProfile;
import mcjty.deepresonance.modules.core.CoreModule;
import mcjty.deepresonance.modules.core.block.ResonatingCrystalTileEntity;
import mcjty.deepresonance.modules.generator.GeneratorModule;
import mcjty.deepresonance.modules.generator.block.GeneratorPartTileEntity;
import mcjty.deepresonance.modules.machines.block.CrystallizerTileEntity;
import mcjty.deepresonance.modules.machines.block.LaserTileEntity;
import mcjty.deepresonance.modules.machines.block.SmelterTileEntity;
import mcjty.deepresonance.modules.machines.util.config.CrystallizerConfig;
import mcjty.deepresonance.modules.machines.util.config.PurifierConfig;
import mcjty.deepresonance.modules.machines.util.config.SmelterConfig;
import mcjty.deepresonance.modules.pedestal.PedestalModule;
import mcjty.deepresonance.modules.radiation.RadiationModule;
import mcjty.deepresonance.modules.tank.blocks.TankTileEntity;
import mcjty.deepresonance.modules.tank.util.TankTransfers;
import mcjty.deepresonance.util.LiquidCrystalData;
import mcjty.lib.varia.EnergyTools;
import mcjty.lib.varia.Tools;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.PermissionSet;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluids;

import java.util.UUID;

final class ResonanceTestSetup {

    private static final BlockPos ORIGIN = new BlockPos(8, 1, 11);
    private static final Identifier CREATIVE_CELL = Identifier.fromNamespaceAndPath("rftoolspower", "dimensionalcell_creative");
    private static final Identifier STORAGE_CELL = Identifier.fromNamespaceAndPath("rftoolspower", "dimensionalcell");

    private ResonanceTestSetup() {
    }

    static void purification(GameTestHelper test) {
        CommandSourceStack source = source(test);
        BlockPos outputPos = ORIGIN.north(2);
        BlockPos floor = ORIGIN.below();
        run(test, source.withPermission(PermissionSet.NO_PERMISSIONS), "purification");
        test.assertBlockPresent(Blocks.AIR, floor);
        run(test, test.getLevel().getServer().createCommandSourceStack().withSuppressedOutput(), "purification");
        test.assertBlockPresent(Blocks.AIR, floor);

        test.setBlock(outputPos, Blocks.STONE);
        run(test, source, "purification");
        test.assertBlockPresent(Blocks.STONE, outputPos);
        test.assertBlockPresent(Blocks.AIR, floor);
        test.setBlock(outputPos, Blocks.AIR);

        run(test, source, "purification");
        test.assertBlockPresent(Blocks.QUARTZ_BLOCK, floor);
        var supplies = test.getBlockEntity(ORIGIN.east(3), ChestBlockEntity.class);
        test.assertTrue(supplies.getItem(0).is(CoreModule.FILTER_MATERIAL_ITEM.get())
                && supplies.getItem(1).is(CoreModule.LIQUID_INJECTOR_ITEM.get()), "the supply chest contains filters and an injector");

        var input = test.getBlockEntity(outputPos.above(2), TankTileEntity.class).getFluidHandler();
        var output = test.getBlockEntity(outputPos, TankTileEntity.class).getFluidHandler();
        test.assertValueEqual(TankTransfers.contents(input).getAmount(), 8000, "the input tank holds raw RCL");

        Block creativeCell = Tools.getBlock(CREATIVE_CELL);
        boolean hasPower = creativeCell != null && creativeCell != Blocks.AIR;
        Direction[] directions = {Direction.WEST, Direction.NORTH, Direction.EAST};
        for (Direction direction : directions) {
            BlockPos laser = outputPos.relative(direction, 3);
            if (hasPower) {
                test.assertBlockPresent(creativeCell, laser.relative(direction.getClockWise()));
            } else {
                var machine = test.getBlockEntity(laser, LaserTileEntity.class);
                test.onEachTick(() -> EnergyTools.receiveEnergy(machine, direction.getClockWise(), 1000));
            }
        }

        test.startSequence()
                .thenWaitUntil(() -> {
                    var processed = LiquidCrystalData.fromStack(TankTransfers.contents(output));
                    test.assertTrue(processed.getAmount() > 0 && processed.getPurity() > 0.30, "the purifier improves the RCL");
                })
                .thenExecute(() -> {
                    for (Direction direction : directions) {
                        BlockPos laser = outputPos.relative(direction, 3);
                        test.assertTrue(test.getBlockEntity(laser, LaserTileEntity.class).getCurrentPower() > 0, "each laser receives energy");
                        test.pullLever(laser.relative(direction));
                    }
                })
                .thenWaitUntil(() -> {
                    var processed = LiquidCrystalData.fromStack(TankTransfers.contents(output));
                    double purifierLimit = 0.30 + PurifierConfig.ADDED_PURITY.get() / 100.0;
                    test.assertTrue(processed.getStrength() > 0.20 && processed.getEfficiency() > 0.15
                            && processed.getPurity() > purifierLimit + 0.0001, "all three lasers improve the RCL");
                })
                .thenSucceed();
    }

    static void generation(GameTestHelper test) {
        run(test, source(test), "generation");
        test.assertBlockPresent(Blocks.QUARTZ_BLOCK, ORIGIN.below());
        test.assertBlockPresent(Blocks.IRON_DOOR, ORIGIN.north(2));
        test.assertBlockPresent(Blocks.IRON_DOOR, ORIGIN.north(2).above());
        test.assertBlockPresent(RadiationModule.DENSE_GLASS.block().get(), ORIGIN.offset(1, 2, -2));
        test.assertBlockPresent(RadiationModule.DENSE_OBSIDIAN.block().get(), ORIGIN.offset(-5, 1, -7));
        test.assertBlockPresent(RadiationModule.DENSE_OBSIDIAN.block().get(), ORIGIN.offset(0, 4, -5));
        test.assertBlockPresent(PedestalModule.PEDESTAL.block().get(), ORIGIN.offset(-1, 0, -7));
        var first = test.getBlockEntity(ORIGIN.offset(-1, 1, -7), ResonatingCrystalTileEntity.class);
        var second = test.getBlockEntity(ORIGIN.offset(1, 1, -7), ResonatingCrystalTileEntity.class);
        var third = test.getBlockEntity(ORIGIN.offset(2, 1, -8), ResonatingCrystalTileEntity.class);
        BlockPos partPos = ORIGIN.offset(-3, 0, -7);
        BlockPos leverPos = ORIGIN.offset(-4, 0, -3);
        var part = test.getBlockEntity(partPos, GeneratorPartTileEntity.class);
        test.assertTrue(!test.getBlockState(partPos).getValue(BlockStateProperties.POWERED), "the freshly built generator is off");

        test.pullLever(leverPos);
        test.startSequence()
                .thenWaitUntil(() -> {
                    test.assertTrue(first.isGlowing() && second.isGlowing() && third.isGlowing(), "the collector activates all three crystals");
                    test.assertTrue(test.getBlockState(partPos).getValue(BlockStateProperties.POWERED), "the generator parts light up");
                    test.assertTrue(part.getBlob() != null && part.getBlob().getEnergy() > 0, "the generator produces energy");
                    Block cell = Tools.getBlock(STORAGE_CELL);
                    if (cell != null && cell != Blocks.AIR) {
                        BlockPos cellPos = ORIGIN.offset(-4, 0, -6);
                        test.assertBlockPresent(cell, cellPos);
                        var storage = test.getLevel().getBlockEntity(test.absolutePos(cellPos));
                        test.assertTrue(storage != null && EnergyTools.getEnergyLevelMulti(storage, null).energy() > 0, "the storage cell receives the generator output");
                    }
                })
                .thenExecute(() -> test.pullLever(leverPos))
                .thenWaitUntil(() -> {
                    test.assertTrue(!test.getBlockState(partPos).getValue(BlockStateProperties.POWERED), "turning the generator off clears its lit state");
                    test.assertTrue(!first.isGlowing() && !second.isGlowing() && !third.isGlowing(), "all crystals stop glowing");
                })
                .thenExecute(() -> test.pullLever(leverPos))
                .thenWaitUntil(() -> test.assertTrue(test.getBlockState(partPos).getValue(BlockStateProperties.POWERED), "the generator restarts"))
                .thenExecute(() -> {
                    var player = test.makeMockServerPlayerInLevel();
                    player.setGameMode(GameType.CREATIVE);
                    BlockPos broken = ORIGIN.offset(-3, 0, -5);
                    test.assertTrue(player.gameMode.destroyBlock(test.absolutePos(broken)), "creative breaking succeeds");
                    BlockPos isolated = ORIGIN.south();
                    test.setBlock(isolated, GeneratorModule.GENERATOR_PART.block().get());
                    test.assertTrue(player.gameMode.destroyBlock(test.absolutePos(isolated)), "an unlinked generator part can be broken");
                })
                .thenIdle(5)
                .thenExecute(() -> {
                    test.assertBlockPresent(Blocks.AIR, ORIGIN.offset(-3, 0, -5));
                    test.assertBlockPresent(Blocks.AIR, ORIGIN.south());
                    test.assertValueEqual(part.getBlob().getGeneratorBlocks(), 3, "the detached multiblock contains the three remaining parts");
                    test.assertTrue(!test.getBlockState(partPos).getValue(BlockStateProperties.POWERED), "the detached generator parts go dark");
                    test.assertTrue(!first.isGlowing() && !second.isGlowing() && !third.isGlowing(), "a disconnected collector switches off its crystals");
                })
                .thenSucceed();
    }

    static void crystal(GameTestHelper test) {
        run(test, source(test), "crystal");
        test.assertBlockPresent(Blocks.QUARTZ_BLOCK, ORIGIN.below());
        BlockPos tank = ORIGIN.north(2);
        var crystallizer = test.getBlockEntity(tank.above(), CrystallizerTileEntity.class);
        var input = test.getBlockEntity(tank, TankTileEntity.class).getFluidHandler();
        test.assertTrue(TankTransfers.contents(input).getAmount() >= CrystallizerConfig.RCL_PER_CRYSTAL.get(), "there is enough RCL to form a crystal");

        Block creativeCell = Tools.getBlock(CREATIVE_CELL);
        if (creativeCell != null && creativeCell != Blocks.AIR) {
            test.assertBlockPresent(creativeCell, tank.above().east());
        } else {
            test.onEachTick(() -> EnergyTools.receiveEnergy(crystallizer, Direction.EAST, 1000));
        }
        test.startSequence()
                .thenWaitUntil(() -> test.assertTrue(crystallizer.getCurrentPower() > 0, "the crystallizer receives energy"))
                .thenExecute(() -> {
                    // The normal recipe takes several minutes at the default transfer rate.
                    int ticks = CrystallizerConfig.RCL_PER_CRYSTAL.get();
                    for (int i = 0; i < ticks && !crystallizer.hasCrystal(); i++) {
                        EnergyTools.receiveEnergy(crystallizer, Direction.EAST, 1000);
                        crystallizer.tickServer();
                    }
                    test.assertTrue(crystallizer.hasCrystal(), "the supplied RCL forms a crystal");
                })
                .thenSucceed();
    }

    static void smelter(GameTestHelper test) {
        run(test, source(test), "smelter");
        test.assertBlockPresent(Blocks.QUARTZ_BLOCK, ORIGIN.below());
        BlockPos lavaPos = ORIGIN.north(2);
        BlockPos smelterPos = lavaPos.above();
        var lava = test.getBlockEntity(lavaPos, TankTileEntity.class).getFluidHandler();
        var smelter = test.getBlockEntity(smelterPos, SmelterTileEntity.class);
        var output = test.getBlockEntity(smelterPos.above(), TankTileEntity.class).getFluidHandler();

        var startingLava = TankTransfers.contents(lava);
        test.assertTrue(startingLava.getFluid() == Fluids.LAVA
                && startingLava.getAmount() * 2 == TankTransfers.capacity(lava), "the bottom tank starts half full of lava");
        test.assertTrue(TankTransfers.contents(output).isEmpty(), "the upper tank starts empty");

        Block creativeCell = Tools.getBlock(CREATIVE_CELL);
        if (creativeCell != null && creativeCell != Blocks.AIR) {
            test.assertBlockPresent(creativeCell, smelterPos.east());
        } else {
            test.onEachTick(() -> EnergyTools.receiveEnergy(smelter, Direction.EAST, 1000));
        }
        test.succeedWhen(() -> {
            var produced = TankTransfers.contents(output);
            test.assertTrue(LiquidCrystalData.isValidLiquidCrystalStack(produced), "the smelter produces RCL in the upper tank");
            test.assertValueEqual(produced.getAmount(), SmelterConfig.RCL_PER_ORE.get(), "the output contains one batch of RCL");
            test.assertTrue(TankTransfers.contents(lava).getAmount() < startingLava.getAmount(), "the smelter consumes lava");
        });
    }

    private static CommandSourceStack source(GameTestHelper test) {
        var level = test.getLevel();
        var server = level.getServer();
        var player = new ServerPlayer(server, level, new GameProfile(UUID.randomUUID(), "DRTest"), ClientInformation.createDefault());
        BlockPos pos = test.absolutePos(ORIGIN);
        player.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
        return server.createCommandSourceStack().withLevel(level).withEntity(player)
                .withPermission(PermissionSet.ALL_PERMISSIONS).withSuppressedOutput();
    }

    private static void run(GameTestHelper test, CommandSourceStack source, String id) {
        test.getLevel().getServer().getCommands().performPrefixedCommand(source, "deepresonance testsetup " + id);
    }
}
