package mcjty.deepresonance.commands;

import mcjty.deepresonance.blocks.ModBlocks;
import mcjty.deepresonance.blocks.crystals.ResonatingCrystalTileEntity;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

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
            ITextComponent component = new TextComponentString(TextFormatting.RED + "Too many parameters!");
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

        EntityPlayer player = (EntityPlayer) sender;
        ItemStack heldItem = player.getHeldItem(EnumHand.MAIN_HAND);
        if (heldItem.isEmpty()) {
            ITextComponent component = new TextComponentString(TextFormatting.RED + "You must hold a crystal in your hand!");
            if (sender instanceof EntityPlayer) {
                ((EntityPlayer) sender).sendStatusMessage(component, false);
            } else {
                sender.sendMessage(component);
            }
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
            ITextComponent component2 = new TextComponentString(TextFormatting.GREEN + "Total power: " + (int)totalPower + " RF");
            if (sender instanceof EntityPlayer) {
                ((EntityPlayer) sender).sendStatusMessage(component2, false);
            } else {
                sender.sendMessage(component2);
            }
            ITextComponent component1 = new TextComponentString(TextFormatting.GREEN + "RF per tick: " + rfPerTick);
            if (sender instanceof EntityPlayer) {
                ((EntityPlayer) sender).sendStatusMessage(component1, false);
            } else {
                sender.sendMessage(component1);
            }
            ITextComponent component = new TextComponentString(TextFormatting.GREEN + "Lifetime: " + totalSeconds + " seconds or " + totalMinutes + " minutes");
            if (sender instanceof EntityPlayer) {
                ((EntityPlayer) sender).sendStatusMessage(component, false);
            } else {
                sender.sendMessage(component);
            }
        } else {
            ITextComponent component = new TextComponentString(TextFormatting.RED + "You must hold a crystal in your hand!");
            if (sender instanceof EntityPlayer) {
                ((EntityPlayer) sender).sendStatusMessage(component, false);
            } else {
                sender.sendMessage(component);
            }
        }
    }
}
