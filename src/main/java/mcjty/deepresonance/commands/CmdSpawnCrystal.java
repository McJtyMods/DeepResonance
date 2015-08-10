package mcjty.deepresonance.commands;

import mcjty.deepresonance.blocks.ModBlocks;
import mcjty.deepresonance.blocks.crystals.ResonatingCrystalTileEntity;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

import java.util.Random;

public class CmdSpawnCrystal extends AbstractDRCommand {
    @Override
    public String getHelp() {
        return "";
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
        if (args.length > 1) {
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Too many parameters!"));
            return;
        }

        if (!(sender instanceof EntityPlayer)) {
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "This command only works as a player!"));
            return;
        }

        EntityPlayer player = (EntityPlayer) sender;
        World world = player.worldObj;
        int x = (int) player.posX;
        int y = (int) player.posY;
        int z = (int) player.posZ;
        world.setBlock(x, y, z, ModBlocks.resonatingCrystalBlock, 0, 3);
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof ResonatingCrystalTileEntity) {
            Random random = new Random(System.currentTimeMillis());
            random.nextFloat();
            ResonatingCrystalTileEntity resonatingCrystalTileEntity = (ResonatingCrystalTileEntity) te;
            resonatingCrystalTileEntity.setStrength(random.nextFloat() * 3.0f + 0.01f);
            resonatingCrystalTileEntity.setPower(random.nextFloat() * 3.0f + 0.01f);
            resonatingCrystalTileEntity.setEfficiency(random.nextFloat() * 3.0f + 0.1f);
            resonatingCrystalTileEntity.setPurity(random.nextFloat() * 10.0f + 5.0f);
        }

    }
}
