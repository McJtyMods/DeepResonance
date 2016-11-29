package mcjty.deepresonance.commands;

import mcjty.deepresonance.radiation.DRRadiationManager;
import mcjty.lib.tools.ChatTools;
import mcjty.lib.varia.Logging;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

public class CmdCleanRadiation extends AbstractDRCommand {
    @Override
    public String getHelp() {
        return "";
    }

    @Override
    public String getCommand() {
        return "cleanradiation";
    }

    @Override
    public int getPermissionLevel() {
        return 2;
    }

    @Override
    public void execute(ICommandSender sender, String[] args) {
        if (args.length > 1) {
            ChatTools.addChatMessage(sender, new TextComponentString(TextFormatting.RED + "Too many parameters!"));
            return;
        }

        EntityPlayer player = null;
        if (sender instanceof EntityPlayer) {
            player = (EntityPlayer) sender;
        }

        DRRadiationManager manager = DRRadiationManager.getManager(sender.getEntityWorld());
        int cnt = manager.getRadiationSources().size();
        manager.removeAllRadiation();

        if (player != null) {
            Logging.message(player, "Removed " + cnt + " radiation sources!");
        } else {
            Logging.log("Removed " + cnt + " radiation sources!");
        }

        manager.save(sender.getEntityWorld());
    }
}
