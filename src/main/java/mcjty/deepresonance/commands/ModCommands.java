package mcjty.deepresonance.commands;

import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.network.chat.Component;
import net.minecraft.server.permissions.Permissions;
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
import com.mojang.brigadier.arguments.StringArgumentType;
import java.util.Locale;

import java.util.Random;

public class ModCommands {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralCommandNode<CommandSourceStack> commands = dispatcher.register(
                Commands.literal(DeepResonance.MODID)
                        .then(registerCreateCrystal(dispatcher))
                        .then(TestSetupCommand.register())
//                        .then(registerReset(dispatcher))
//                        .then(registerList(dispatcher))
//                        .then(registerListKnown(dispatcher))
//                        .then(registerAdd(dispatcher))
        );

        dispatcher.register(Commands.literal("dr").redirect(commands));
    }

    public static ArgumentBuilder<CommandSourceStack, ?> registerCreateCrystal(CommandDispatcher<CommandSourceStack> dispatcher) {
        return Commands.literal("create")
                .requires(cs -> cs.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER))
                .then(Commands.argument("type", StringArgumentType.word())
                        .suggests((context, builder) -> {
                            for (CrystalHelper.CrystalOption option : CrystalHelper.CrystalOption.values()) {
                                builder.suggest(option.getSerializedName());
                            }
                            return builder.buildFuture();
                        })
                        .executes(context -> {
                            CrystalHelper.CrystalOption type;
                            try {
                                type = CrystalHelper.CrystalOption.valueOf(StringArgumentType.getString(context, "type").toUpperCase(Locale.ROOT));
                            } catch (IllegalArgumentException exception) {
                                throw new SimpleCommandExceptionType(Component.literal("Unknown resonating crystal type")).create();
                            }
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
