package mcjty.deepresonance.blocks.sensors;

import mcjty.deepresonance.blocks.GenericDRBlock;
import mcjty.deepresonance.client.ClientHandler;
import mcjty.lib.container.EmptyContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import java.util.List;

public class CooldownSensorBlock extends GenericDRBlock<CooldownSensorTileEntity, EmptyContainer> {

    public CooldownSensorBlock() {
        super(Material.IRON, CooldownSensorTileEntity.class, EmptyContainer.class, "cooldown_sensor", false);
    }

    @Override
    public int getGuiID() {
        return -1;
    }

    @Override
    public boolean hasRedstoneOutput() {
        return true;
    }

    @Override
    protected int getRedstoneOutput(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        EnumFacing direction = state.getValue(FACING);
        if (side == direction) {
            TileEntity te = world.getTileEntity(pos);
            if (state.getBlock() instanceof CooldownSensorBlock && te instanceof CooldownSensorTileEntity) {
                CooldownSensorTileEntity sensor = (CooldownSensorTileEntity) te;
                return sensor.getPower();
            }
        }
        return 0;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack itemStack, World player, List<String> list, ITooltipFlag advancedToolTip) {
        super.addInformation(itemStack, player, list, advancedToolTip);

        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
            list.add("This sensor has to be placed at most");
            list.add("8 blocks above a crystal. It will emit");
            list.add("a redstone signal if the crystal has");
            list.add("cooled down");
        } else {
            list.add(TextFormatting.WHITE + ClientHandler.getShiftMessage());
        }
    }
}
