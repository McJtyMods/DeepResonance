package mcjty.deepresonance.blocks.lens;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.client.ClientHandler;
import mcjty.lib.container.GenericBlock;
import mcjty.lib.varia.BlockTools;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import org.lwjgl.input.Keyboard;

import java.util.List;

public class LensBlock extends GenericBlock {

    public LensBlock() {
        super(DeepResonance.instance, Material.iron, LensTileEntity.class, false);
        setBlockName("lensBlock");
        setHorizRotation(true);
        setCreativeTab(DeepResonance.tabDeepResonance);
    }

    @Override
    public int getGuiID() {
        return -1;
    }

    @Override
    @SuppressWarnings("unchecked")
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack itemStack, EntityPlayer player, List list, boolean whatIsThis) {
        super.addInformation(itemStack, player, list, whatIsThis);

        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
            list.add("Place this block on a tank and aim an");
            list.add("infusion laser at it to enhance the liquid");
            list.add("in the tank");
        } else {
            list.add(EnumChatFormatting.WHITE + ClientHandler.getShiftMessage());
        }
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        return null;
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z);
        ForgeDirection direction = BlockTools.getOrientationHoriz(meta);
        switch (direction) {
            case DOWN:
            case UP:
            case UNKNOWN:
                break;
            case NORTH:
                this.setBlockBounds(0.1F, 0.1F, 0.0F, 0.9F, 0.9F, 0.1F);
                break;
            case SOUTH:
                this.setBlockBounds(0.1F, 0.1F, 0.9F, 0.9F, 0.9F, 1.0F);
                break;
            case WEST:
                this.setBlockBounds(0.0F, 0.1F, 0.1F, 0.1F, 0.9F, 0.9F);
                break;
            case EAST:
                this.setBlockBounds(0.9F, 0.1F, 0.1F, 1.0F, 0.9F, 0.9F);
                break;
        }
    }

    @Override
    protected boolean wrenchUse(World world, int x, int y, int z, EntityPlayer player) {
        // Do not rotate
        return true;
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entityLivingBase, ItemStack itemStack) {
    }

    @Override
    public String getSideIconName() {
        return "lens";
    }

    @Override
    public boolean shouldSideBeRendered(IBlockAccess blockAccess, int x, int y, int z, int side) {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }
}
