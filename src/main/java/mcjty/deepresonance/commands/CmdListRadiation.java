package mcjty.deepresonance.commands;

import mcjty.deepresonance.radiation.DRRadiationManager;
import mcjty.lib.varia.GlobalCoordinate;
import mcjty.lib.varia.Logging;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import java.util.Map;

public class CmdListRadiation extends AbstractDRCommand {
    @Override
    public String getHelp() {
        return "";
    }

    @Override
    public String getCommand() {
        return "listradiation";
    }

    @Override
    public int getPermissionLevel() {
        return 2;
    }

    @Override
    public void execute(ICommandSender sender, String[] args) {
        if (args.length > 1) {
            ITextComponent component = new TextComponentString(TextFormatting.RED + "Too many parameters!");
            if (sender instanceof EntityPlayer) {
                ((EntityPlayer) sender).sendStatusMessage(component, false);
            } else {
                sender.sendMessage(component);
            }
            return;
        }

        EntityPlayer player = null;
        if (sender instanceof EntityPlayer) {
            player = (EntityPlayer) sender;
        }

        DRRadiationManager manager = DRRadiationManager.getManager(sender.getEntityWorld());
        for (Map.Entry<GlobalCoordinate, DRRadiationManager.RadiationSource> source : manager.getRadiationSources().entrySet()) {
            GlobalCoordinate c = source.getKey();
            DRRadiationManager.RadiationSource radiationSource = source.getValue();
            String msg = "Radiation at " + c.getCoordinate() + " (dim " + c.getDimension() + "):";
            String msg2 = "Radius=" + radiationSource.getRadius() +", Strength=" + radiationSource.getStrength() + ", Max=" + radiationSource.getMaxStrength();
            if (player != null) {
                Logging.message(player, msg);
                Logging.message(player, msg2);
            } else {
                Logging.log(msg);
                Logging.log(msg2);
            }
        }
    }
}
