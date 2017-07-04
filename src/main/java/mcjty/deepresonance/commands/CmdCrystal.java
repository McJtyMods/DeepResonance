package mcjty.deepresonance.commands;

import mcjty.deepresonance.blocks.crystals.ResonatingCrystalTileEntity;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class CmdCrystal extends AbstractDRCommand {
    @Override
    public String getHelp() {
        return "<purity> <strength> <efficiency> <power>";
    }

    @Override
    public String getCommand() {
        return "crystal";
    }

    @Override
    public int getPermissionLevel() {
        return 2;
    }

    @Override
    public void execute(ICommandSender sender, String[] args) {
        if (args.length > 5) {
            ITextComponent component = new TextComponentString(TextFormatting.RED + "Too many parameters!");
            if (sender instanceof EntityPlayer) {
                ((EntityPlayer) sender).sendStatusMessage(component, false);
            } else {
                sender.sendMessage(component);
            }
            return;
        }
        if (args.length < 5) {
            ITextComponent component = new TextComponentString(TextFormatting.RED + "Too few parameters!");
            if (sender instanceof EntityPlayer) {
                ((EntityPlayer) sender).sendStatusMessage(component, false);
            } else {
                sender.sendMessage(component);
            }
            return;
        }

        if (!(sender instanceof EntityPlayer)) {
            ITextComponent component = new TextComponentString(TextFormatting.RED + "This command only works as a player!");
            if (sender instanceof EntityPlayer) {
                ((EntityPlayer) sender).sendStatusMessage(component, false);
            } else {
                sender.sendMessage(component);
            }
            return;
        }

        int purity = fetchInt(sender, args, 1, 0);
        int strength = fetchInt(sender, args, 2, 0);
        int efficiency = fetchInt(sender, args, 3, 0);
        int power = fetchInt(sender, args, 4, 0);

        EntityPlayer player = (EntityPlayer) sender;
        World world = player.getEntityWorld();
        int x = (int) (player.posX - .5);
        int y = (int) player.posY;
        int z = (int) (player.posZ - .5);

        ResonatingCrystalTileEntity.spawnCrystal(player, world, new BlockPos(x, y, z), purity, strength, efficiency, power);
    }
}
