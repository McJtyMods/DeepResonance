package mcjty.deepresonance.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.modules.core.util.CrystalHelper;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.server.command.EnumArgument;

import java.util.Random;

public class ModCommands {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralCommandNode<CommandSourceStack> commands = dispatcher.register(
                Commands.literal(DeepResonance.MODID)
                        .then(registerCreateCrystal(dispatcher))
//                        .then(registerReset(dispatcher))
//                        .then(registerList(dispatcher))
//                        .then(registerListKnown(dispatcher))
//                        .then(registerAdd(dispatcher))
        );

        dispatcher.register(Commands.literal("dr").redirect(commands));
    }

    public static ArgumentBuilder<CommandSourceStack, ?> registerCreateCrystal(CommandDispatcher<CommandSourceStack> dispatcher) {
        return Commands.literal("create")
                .requires(cs -> cs.hasPermission(2))
                .then(Commands.argument("type", EnumArgument.enumArgument(CrystalHelper.CrystalOption.class))
                        .executes(context -> {
                            CrystalHelper.CrystalOption type = context.getArgument("type", CrystalHelper.CrystalOption.class);
                            ServerPlayer player = context.getSource().getPlayerOrException();
                            Level world = player.level();
                            int x = (int) (player.getX() - .5);
                            int y = (int) player.getY();
                            int z = (int) (player.getZ() - .5);
                            Random random = new Random(System.currentTimeMillis());
                            random.nextFloat();

                            CrystalHelper.spawnCrystal(world, random, new BlockPos(x, y, z), type);
                            return 0;
                        }));
    }

}
