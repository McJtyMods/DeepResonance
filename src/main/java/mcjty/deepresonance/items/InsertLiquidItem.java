package mcjty.deepresonance.items;

import elec332.core.world.WorldHelper;
import mcjty.deepresonance.blocks.tank.TileTank;
import mcjty.deepresonance.fluid.LiquidCrystalFluidTagData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;

public class InsertLiquidItem extends GenericDRItem {

    public InsertLiquidItem() {
        super("insert_liquid");
        setMaxStackSize(1);
    }

    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer player, List<String> list, boolean advancedToolTip) {
        super.addInformation(itemStack, player, list, advancedToolTip);
        list.add("Creative only item to inject 100mb of liquid");
        list.add("crystal to a tank");
    }

    @Override
    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            TileEntity te = WorldHelper.getTileAt(world, pos);
            if (te instanceof TileTank && ((TileTank) te).getTank() != null) {
                TileTank tileTank = (TileTank) te;
                FluidStack fluidStack = LiquidCrystalFluidTagData.makeLiquidCrystalStack(100, 1.0f, 0.1f, 0.1f, 0.1f);
                tileTank.getTank().fill(fluidStack, true);
            } else {
                player.addChatComponentMessage(new TextComponentString(TextFormatting.YELLOW + "This is not a tank!"));
            }
        }
        return EnumActionResult.SUCCESS;
    }
}
