package mcjty.deepresonance.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import mcjty.deepresonance.modules.core.CoreModule;
import mcjty.deepresonance.modules.core.block.ResonatingCrystalTileEntity;
import mcjty.deepresonance.modules.core.data.Crystal;
import mcjty.deepresonance.modules.generator.GeneratorModule;
import mcjty.deepresonance.modules.machines.MachinesModule;
import mcjty.deepresonance.modules.machines.block.LaserTileEntity;
import mcjty.deepresonance.modules.machines.block.SmelterTileEntity;
import mcjty.deepresonance.modules.machines.util.config.CrystallizerConfig;
import mcjty.deepresonance.modules.pedestal.PedestalModule;
import mcjty.deepresonance.modules.pedestal.block.PedestalTileEntity;
import mcjty.deepresonance.modules.radiation.RadiationModule;
import mcjty.deepresonance.modules.tank.TankModule;
import mcjty.deepresonance.modules.tank.blocks.TankTileEntity;
import mcjty.deepresonance.util.Constants;
import mcjty.deepresonance.util.LiquidCrystalData;
import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.lib.typed.TypedMap;
import mcjty.lib.varia.Tools;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

final class TestSetupCommand {

    private static final String MESSAGE = "commands.deepresonance.testsetup.";
    private static final ResourceLocation CREATIVE_CELL = ResourceLocation.fromNamespaceAndPath("rftoolspower", "dimensionalcell_creative");
    private static final ResourceLocation STORAGE_CELL = ResourceLocation.fromNamespaceAndPath("rftoolspower", "dimensionalcell");
    private static final int TANK_CAPACITY = Constants.TANK_BUCKETS * 1000;
    private static final DynamicCommandExceptionType BLOCKED = new DynamicCommandExceptionType(
            pos -> Component.translatable(MESSAGE + "blocked", pos));
    private static final DynamicCommandExceptionType UNAVAILABLE = new DynamicCommandExceptionType(
            pos -> Component.translatable(MESSAGE + "unavailable", pos));

    private enum Setup {
        PURIFICATION("purification", 5, 6, 2),
        GENERATION("generation", 5, 9, 4),
        CRYSTAL("crystal", 3, 5, 1),
        SMELTER("smelter", 3, 5, 2);

        private final String id;
        private final int halfWidth;
        private final int depth;
        private final int height;

        Setup(String id, int halfWidth, int depth, int height) {
            this.id = id;
            this.halfWidth = halfWidth;
            this.depth = depth;
            this.height = height;
        }
    }

    private final ServerPlayer player;
    private final ServerLevel level;
    private final BlockPos origin;

    private TestSetupCommand(ServerPlayer player) {
        this.player = player;
        level = (ServerLevel) player.level();
        origin = player.blockPosition();
    }

    static LiteralArgumentBuilder<CommandSourceStack> register() {
        LiteralArgumentBuilder<CommandSourceStack> command = Commands.literal("testsetup")
                .requires(source -> source.getEntity() instanceof ServerPlayer && source.hasPermission(2));
        for (Setup setup : Setup.values()) {
            command.then(Commands.literal(setup.id).executes(context -> build(context.getSource(), setup)));
        }
        return command;
    }

    private static int build(CommandSourceStack source, Setup setup) throws CommandSyntaxException {
        TestSetupCommand station = new TestSetupCommand(source.getPlayerOrException());
        station.checkSpace(setup);
        station.buildFloor(setup);
        boolean powered = switch (setup) {
            case PURIFICATION -> station.buildPurification();
            case GENERATION -> {
                station.buildGeneration();
                yield true;
            }
            case CRYSTAL -> station.buildCrystallizer();
            case SMELTER -> station.buildSmelter();
        };
        source.sendSuccess(() -> Component.translatable(MESSAGE + setup.id), false);
        if (!powered) {
            source.sendSuccess(() -> Component.translatable(MESSAGE + "needs_power"), false);
        }
        return 1;
    }

    private void checkSpace(Setup setup) throws CommandSyntaxException {
        BlockPos from = at(-setup.halfWidth, -1, -setup.depth);
        BlockPos to = at(setup.halfWidth, setup.height, 1);
        for (BlockPos pos : BlockPos.betweenClosed(from, to)) {
            if (level.isOutsideBuildHeight(pos) || !level.getWorldBorder().isWithinBounds(pos) || !level.hasChunkAt(pos)) {
                throw UNAVAILABLE.create(pos.toShortString());
            }
            BlockState state = level.getBlockState(pos);
            boolean floor = pos.getY() == from.getY();
            if (floor ? state.hasBlockEntity() || state.getDestroySpeed(level, pos) < 0
                    : !state.canBeReplaced() || !state.getFluidState().isEmpty()) {
                throw BLOCKED.create(pos.toShortString());
            }
        }
    }

    private void buildFloor(Setup setup) {
        for (BlockPos pos : BlockPos.betweenClosed(at(-setup.halfWidth, -1, -setup.depth), at(setup.halfWidth, -1, 1))) {
            level.setBlockAndUpdate(pos, Blocks.QUARTZ_BLOCK.defaultBlockState());
        }
    }

    private boolean buildPurification() {
        BlockPos output = at(0, 0, -2);
        BlockPos purifier = output.above();
        BlockPos input = purifier.above();
        placeMachine(output, TankModule.TANK.block().get());
        placeMachine(input, TankModule.TANK.block().get());
        placeMachine(purifier, MachinesModule.PURIFIER.block().get());
        fillTank(input, LiquidCrystalData.makeLiquidCrystalStack(8000, 1.0f, 0.30f, 0.20f, 0.15f));
        insertItem(purifier, 0, new ItemStack(CoreModule.FILTER_MATERIAL_ITEM.get(), 32));
        placeChest(purifier.south());

        boolean westPowered = buildLaser(output, Direction.WEST, Items.REDSTONE);
        boolean northPowered = buildLaser(output, Direction.NORTH, Items.QUARTZ);
        boolean eastPowered = buildLaser(output, Direction.EAST, Items.DIAMOND);
        placeChest(at(3, 0, 0), new ItemStack(CoreModule.FILTER_MATERIAL_ITEM.get(), 64),
                new ItemStack(CoreModule.LIQUID_INJECTOR_ITEM.get()), crystal(), crystal(), crystal(),
                new ItemStack(Items.REDSTONE, 64), new ItemStack(Items.QUARTZ, 64), new ItemStack(Items.DIAMOND, 64));
        return westPowered && northPowered && eastPowered;
    }

    private boolean buildLaser(BlockPos output, Direction outward, Item catalyst) {
        Direction facing = outward.getOpposite();
        BlockPos laser = output.relative(outward, 3);
        placeMachine(laser, MachinesModule.LASER.block().get().defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, facing));
        placeMachine(output.relative(outward), MachinesModule.LENS.block().get().defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, facing));
        insertItem(laser, LaserTileEntity.SLOT_CRYSTAL, crystal());
        insertItem(laser, LaserTileEntity.SLOT_CATALYST, new ItemStack(catalyst, 16));
        placeLever(laser.relative(outward));
        return placeCell(laser.relative(outward.getClockWise()), CREATIVE_CELL, "setOutput");
    }

    private void buildGeneration() {
        BlockState wall = RadiationModule.DENSE_OBSIDIAN.block().get().defaultBlockState();
        BlockState window = RadiationModule.DENSE_GLASS.block().get().defaultBlockState();
        for (BlockPos pos : BlockPos.betweenClosed(at(-5, 0, -9), at(5, 4, -2))) {
            int x = pos.getX() - origin.getX();
            int y = pos.getY() - origin.getY();
            int z = pos.getZ() - origin.getZ();
            if (y == 4 || Math.abs(x) == 5 || z == -9 || z == -2) {
                if (z == -2 && x == 0 && y < 2) {
                    continue;
                }
                boolean frontWindow = z == -2 && Math.abs(x) > 0 && Math.abs(x) < 5 && (y == 1 || y == 2);
                level.setBlockAndUpdate(pos, frontWindow ? window : wall);
            }
        }
        BlockState door = Blocks.IRON_DOOR.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH);
        level.setBlock(at(0, 0, -2), door, Block.UPDATE_CLIENTS);
        level.setBlockAndUpdate(at(0, 1, -2), door.setValue(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.UPPER));
        level.setBlockAndUpdate(at(0, 0, -1), Blocks.STONE_PRESSURE_PLATE.defaultBlockState());
        level.setBlockAndUpdate(at(0, 0, -3), Blocks.STONE_PRESSURE_PLATE.defaultBlockState());
        level.setBlockAndUpdate(at(0, 3, -5), Blocks.SEA_LANTERN.defaultBlockState());

        BlockPos controller = at(-3, 0, -3);
        placeMachine(controller, GeneratorModule.GENERATOR_CONTROLLER.block().get());
        for (int z = -4; z >= -8; z--) {
            placeMachine(at(-3, 0, z), GeneratorModule.GENERATOR_PART.block().get());
        }
        placeMachine(at(-3, 1, -7), GeneratorModule.ENERGY_COLLECTOR.block().get());
        placeCell(at(-4, 0, -6), STORAGE_CELL, "setInput");
        placeLever(controller.west());

        buildPedestal(at(-1, 0, -7), 92, 54, 70);
        buildPedestal(at(1, 0, -7), 95, 58, 75);
        buildPedestal(at(2, 0, -8), 98, 62, 80);
        placeChest(at(2, 0, 0), new ItemStack(RadiationModule.RADIATION_MONITOR.get()),
                new ItemStack(RadiationModule.RADIATION_SUIT_HELMET.get()),
                new ItemStack(RadiationModule.RADIATION_SUIT_CHESTPLATE.get()),
                new ItemStack(RadiationModule.RADIATION_SUIT_LEGGINGS.get()),
                new ItemStack(RadiationModule.RADIATION_SUIT_BOOTS.get()), crystal(), crystal());
    }

    private void buildPedestal(BlockPos pos, double purity, double strength, double efficiency) {
        placeMachine(pos, PedestalModule.PEDESTAL.block().get().defaultBlockState().setValue(BlockStateProperties.FACING, Direction.UP));
        insertItem(pos, PedestalTileEntity.SLOT_CRYSTAL, crystal());
        placeMachine(pos.above(), CoreModule.RESONATING_CRYSTAL_GENERATED.block().get());
        if (level.getBlockEntity(pos.above()) instanceof ResonatingCrystalTileEntity crystal) {
            crystal.setPower(100);
            crystal.setPurity(purity);
            crystal.setStrength(strength);
            crystal.setEfficiency(efficiency);
            crystal.setChanged();
        }
    }

    private boolean buildCrystallizer() {
        BlockPos tank = at(0, 0, -2);
        int amount = Math.max(8000, CrystallizerConfig.RCL_PER_CRYSTAL.get());
        int tanks = (amount + TANK_CAPACITY - 1) / TANK_CAPACITY;
        for (int i = 0; i < tanks; i++) {
            placeMachine(tank.offset(-(i / 4), 0, -(i % 4)), TankModule.TANK.block().get());
        }
        fillTank(tank, LiquidCrystalData.makeLiquidCrystalStack(amount, 1.0f, 0.90f, 0.75f, 0.65f));
        placeMachine(tank.above(), MachinesModule.CRYSTALLIZER.block().get());
        placeChest(at(2, 0, 0), new ItemStack(CoreModule.LIQUID_INJECTOR_ITEM.get()));
        return placeCell(tank.above().east(), CREATIVE_CELL, "setOutput");
    }

    private boolean buildSmelter() {
        BlockPos lavaTank = at(0, 0, -2);
        BlockPos smelter = lavaTank.above();
        placeMachine(lavaTank, TankModule.TANK.block().get());
        placeMachine(smelter.above(), TankModule.TANK.block().get());
        placeMachine(smelter, MachinesModule.SMELTER.block().get().defaultBlockState()
                .setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH)
                .setValue(BlockStateProperties.POWERED, false));
        fillTank(lavaTank, new FluidStack(Fluids.LAVA, TANK_CAPACITY / 2));
        Item ore = CoreModule.RESONATING_ORE_STONE.item().get();
        insertItem(smelter, SmelterTileEntity.SLOT, new ItemStack(ore, 8));
        placeChest(at(2, 0, 0), new ItemStack(ore, 64), new ItemStack(Items.LAVA_BUCKET), new ItemStack(Items.LAVA_BUCKET));
        return placeCell(smelter.east(), CREATIVE_CELL, "setOutput");
    }

    private boolean placeCell(BlockPos pos, ResourceLocation id, String mode) {
        Block block = Tools.getBlock(id);
        if (block == null || block == Blocks.AIR) {
            return false;
        }
        placeMachine(pos, block);
        return level.getBlockEntity(pos) instanceof GenericTileEntity cell
                && cell.executeServerCommand(mode, player, TypedMap.EMPTY);
    }

    private void placeMachine(BlockPos pos, Block block) {
        placeMachine(pos, block.defaultBlockState());
    }

    private void placeMachine(BlockPos pos, BlockState state) {
        level.setBlockAndUpdate(pos, state);
        state.getBlock().setPlacedBy(level, pos, state, player, new ItemStack(state.getBlock()));
    }

    private void placeLever(BlockPos pos) {
        BlockState lever = Blocks.LEVER.defaultBlockState().setValue(BlockStateProperties.ATTACH_FACE, AttachFace.FLOOR)
                .setValue(BlockStateProperties.POWERED, false);
        level.setBlockAndUpdate(pos, lever);
    }

    private void placeChest(BlockPos pos, ItemStack... contents) {
        level.setBlockAndUpdate(pos, Blocks.CHEST.defaultBlockState());
        if (level.getBlockEntity(pos) instanceof ChestBlockEntity chest) {
            for (int slot = 0; slot < contents.length; slot++) {
                chest.setItem(slot, contents[slot]);
            }
            chest.setChanged();
        }
    }

    private void fillTank(BlockPos pos, FluidStack stack) {
        if (level.getBlockEntity(pos) instanceof TankTileEntity tank) {
            tank.getFluidHandler().fill(stack, IFluidHandler.FluidAction.EXECUTE);
        }
    }

    private void insertItem(BlockPos pos, int slot, ItemStack stack) {
        var handler = level.getCapability(Capabilities.ItemHandler.BLOCK, pos, null);
        if (handler != null) {
            handler.insertItem(slot, stack, false);
        }
    }

    private static ItemStack crystal() {
        ItemStack stack = new ItemStack(CoreModule.RESONATING_CRYSTAL_GENERATED.item().get());
        stack.set(CoreModule.ITEM_CRYSTAL_DATA.get(), new Crystal(100, 90, 50, 65));
        return stack;
    }

    private BlockPos at(int x, int y, int z) {
        return origin.offset(x, y, z);
    }
}
