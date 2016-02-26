package mcjty.deepresonance.commands;

import mcjty.deepresonance.blocks.crystals.ResonatingCrystalTileEntity;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

import java.util.Random;

public class CmdSpawnCrystal extends AbstractDRCommand {
    @Override
    public String getHelp() {
        return "[0=nor, 1=avg, 2=maxrnd, 3=max, 4=dirty, 5=near empty]";
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
        if (args.length > 2) {
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Too many parameters!"));
            return;
        }

        if (!(sender instanceof EntityPlayer)) {
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "This command only works as a player!"));
            return;
        }

        int special = fetchInt(sender, args, 1, 0);

        EntityPlayer player = (EntityPlayer) sender;
        World world = player.worldObj;
        int x = (int) player.posX;
        int y = (int) player.posY;
        int z = (int) player.posZ;
        Random random = new Random(System.currentTimeMillis());
        random.nextFloat();

        ResonatingCrystalTileEntity.spawnRandomCrystal(world, random, new BlockPos(x, y, z), special);
    }
}
