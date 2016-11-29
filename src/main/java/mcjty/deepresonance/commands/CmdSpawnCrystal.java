package mcjty.deepresonance.commands;

import mcjty.deepresonance.blocks.crystals.ResonatingCrystalTileEntity;
import mcjty.lib.tools.ChatTools;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import java.util.Random;

public class CmdSpawnCrystal extends AbstractDRCommand {
    @Override
    public String getHelp() {
        return "[0=nor, 1=avg, 2=maxrnd, 3=max, 4=dirty, 5=near empty]";
    }

    @Override
    public String getCommand() {
        return "spawncrystal";
    }

    @Override
    public int getPermissionLevel() {
        return 2;
    }

    @Override
    public void execute(ICommandSender sender, String[] args) {
        if (args.length > 2) {
            ChatTools.addChatMessage(sender, new TextComponentString(TextFormatting.RED + "Too many parameters!"));
            return;
        }

        if (!(sender instanceof EntityPlayer)) {
            ChatTools.addChatMessage(sender, new TextComponentString(TextFormatting.RED + "This command only works as a player!"));
            return;
        }

        int special = fetchInt(sender, args, 1, 0);

        EntityPlayer player = (EntityPlayer) sender;
        World world = player.getEntityWorld();
        int x = (int) (player.posX - .5);
        int y = (int) player.posY;
        int z = (int) (player.posZ - .5);
        Random random = new Random(System.currentTimeMillis());
        random.nextFloat();

        ResonatingCrystalTileEntity.spawnRandomCrystal(world, random, new BlockPos(x, y, z), special);
    }
}
