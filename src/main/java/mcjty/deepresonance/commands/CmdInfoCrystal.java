package mcjty.deepresonance.commands;

import mcjty.deepresonance.blocks.ModBlocks;
import mcjty.deepresonance.blocks.crystals.ResonatingCrystalTileEntity;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

public class CmdInfoCrystal extends AbstractDRCommand {
    @Override
    public String getHelp() {
        return "";
    }

    @Override
    public String getCommand() {
        return "info";
    }

    @Override
    public int getPermissionLevel() {
        return 1;
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
        ItemStack heldItem = player.getHeldItem();
        if (heldItem == null) {
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "You must hold a crystal in your hand!"));
            return;
        }

        if (heldItem.getItem() instanceof ItemBlock && ((ItemBlock) heldItem.getItem()).getBlock() == ModBlocks.resonatingCrystalBlock) {
            NBTTagCompound tagCompound = heldItem.getTagCompound();
            float strength = tagCompound.getFloat("strength");
            float efficiency = tagCompound.getFloat("efficiency");
            float purity = tagCompound.getFloat("purity");

            float totalPower = ResonatingCrystalTileEntity.getTotalPower(strength, purity);
            int rfPerTick = ResonatingCrystalTileEntity.getRfPerTick(efficiency, purity);
            int totalSeconds = (int) ((totalPower / rfPerTick) / 20);
            int totalMinutes = (int) ((totalPower / rfPerTick) / 1200);
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "Total power: " + (int)totalPower + " RF"));
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "RF per tick: " + rfPerTick));
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "Lifetime: " + totalSeconds + " seconds or " + totalMinutes + " minutes"));
        } else {
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "You must hold a crystal in your hand!"));
        }
    }
}
