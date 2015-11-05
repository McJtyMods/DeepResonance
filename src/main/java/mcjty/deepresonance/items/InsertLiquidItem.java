package mcjty.deepresonance.items;

import mcjty.deepresonance.blocks.tank.TileTank;
import mcjty.deepresonance.fluid.LiquidCrystalFluidTagData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;

public class InsertLiquidItem extends Item {

    public InsertLiquidItem() {
        setMaxStackSize(1);
    }

    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer player, List list, boolean whatIsThis) {
        super.addInformation(itemStack, player, list, whatIsThis);
        list.add("Creative only item to inject 100mb of liquid");
        list.add("crystal to a tank");
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float sx, float sy, float sz) {
        if (!world.isRemote) {
            TileEntity te = world.getTileEntity(x, y, z);
            if (te instanceof TileTank) {
                TileTank tileTank = (TileTank) te;
                FluidStack fluidStack = LiquidCrystalFluidTagData.makeLiquidCrystalStack(100, 1.0f, 0.1f, 0.1f, 0.1f);
                tileTank.fill(ForgeDirection.UNKNOWN, fluidStack, true);
            } else {
                player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "This is not a tank!"));
            }
        }
        return true;
    }
}
