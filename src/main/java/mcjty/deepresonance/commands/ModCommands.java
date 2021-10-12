package mcjty.deepresonance.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.modules.core.block.ResonatingCrystalTileEntity;
import mcjty.deepresonance.modules.core.util.CrystalHelper;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public class ModCommands {

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        LiteralCommandNode<CommandSource> commands = dispatcher.register(
                Commands.literal(DeepResonance.MODID)
                        .then(registerSetChapter(dispatcher))
//                        .then(registerReset(dispatcher))
//                        .then(registerList(dispatcher))
//                        .then(registerListKnown(dispatcher))
//                        .then(registerAdd(dispatcher))
        );

        dispatcher.register(Commands.literal("tales").redirect(commands));
    }

    public static ArgumentBuilder<CommandSource, ?> registerSetChapter(CommandDispatcher<CommandSource> dispatcher) {
        return Commands.literal("setchapter")
                .requires(cs -> cs.hasPermission(2))
                .then(Commands.argument("special", IntegerArgumentType.integer())
                        .executes(context -> {
                            int special = context.getArgument("special", Integer.class);
                            ServerPlayerEntity player = context.getSource().getPlayerOrException();
                            World world = player.getLevel();
                            int x = (int) (player.getX() - .5);
                            int y = (int) player.getY();
                            int z = (int) (player.getZ() - .5);
                            Random random = new Random(System.currentTimeMillis());
                            random.nextFloat();

                            CrystalHelper.spawnRandomCrystal(world, random, new BlockPos(x, y, z), special);
                            return 0;
                        }));
    }

}
