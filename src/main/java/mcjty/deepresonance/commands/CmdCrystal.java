package mcjty.deepresonance.commands;

import mcjty.deepresonance.blocks.crystals.ResonatingCrystalTileEntity;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

import java.util.Random;

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
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Too many parameters!"));
            return;
        }
        if (args.length < 5) {
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Too few parameters!"));
            return;
        }

        if (!(sender instanceof EntityPlayer)) {
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "This command only works as a player!"));
            return;
        }

        int purity = fetchInt(sender, args, 1, 0);
        int strength = fetchInt(sender, args, 2, 0);
        int efficiency = fetchInt(sender, args, 3, 0);
        int power = fetchInt(sender, args, 4, 0);

        EntityPlayer player = (EntityPlayer) sender;
        World world = player.worldObj;
        int x = (int) (player.posX - .5);
        int y = (int) player.posY;
        int z = (int) (player.posZ - .5);

        ResonatingCrystalTileEntity.spawnCrystal(world, new BlockPos(x, y, z), purity, strength, efficiency, power);
    }
}
