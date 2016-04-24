package mcjty.deepresonance.commands;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import java.util.List;
import java.util.Map;

public abstract class DefaultCommand implements ICommand {

    protected final Map<String,DRCommand> commands = Maps.newHashMap();

    public DefaultCommand() {
        registerCommand(new CmdHelp());
    }

    protected void registerCommand(DRCommand command) {
        commands.put(command.getCommand(), command);
    }

    public void showHelp(ICommandSender sender) {
        sender.addChatMessage(new TextComponentString(TextFormatting.BLUE + getCommandName() + " <subcommand> <args>"));
        for (Map.Entry<String,DRCommand> me : commands.entrySet()) {
            sender.addChatMessage(new TextComponentString("    " + me.getKey() + " " + me.getValue().getHelp()));
        }
    }

    class CmdHelp implements DRCommand {
        @Override
        public String getHelp() {
            return "";
        }

        @Override
        public int getPermissionLevel() {
            return 0;
        }

        @Override
        public boolean isClientSide() {
            return false;
        }

        @Override
        public String getCommand() {
            return "help";
        }

        @Override
        public void execute(ICommandSender sender, String[] args) {
            showHelp(sender);
        }
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return getCommandName() + " <subcommand> <args> (try '" + getCommandName() + " help' for more info)";
    }

    @Override
    public List<String> getCommandAliases() {
        return ImmutableList.of();
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        World world = sender.getEntityWorld();
        if (args.length <= 0) {
            if (!world.isRemote) {
                showHelp(sender);
            }
        } else {
            DRCommand command = commands.get(args[0]);
            if (command == null) {
                if (!world.isRemote) {
                    sender.addChatMessage(new TextComponentString(TextFormatting.RED + "Unknown Deep Resonance command: " + args[0]));
                }
            } else {
                if (world.isRemote) {
                    // We are client-side. Only do client-side commands.
                    if (command.isClientSide()) {
                        command.execute(sender, args);
                    }
                } else {
                    // Server-side.
                    if (!sender.canCommandSenderUseCommand(command.getPermissionLevel(), getCommandName())) {
                        sender.addChatMessage(new TextComponentString(TextFormatting.RED + "Command is not allowed!"));
                    } else {
                        command.execute(sender, args);
                    }
                }
            }
        }
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return true;
    }

    @Override
    public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
        return ImmutableList.of();
    }

    @Override
    public boolean isUsernameIndex(String[] sender, int p_82358_2_) {
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    @SuppressWarnings("all")
    //TODO: Param seems to be nullable, NPE catcher.
    public int compareTo(ICommand command) {
        return getCommandName().compareTo(command.getCommandName());
    }

}
