package mcjty.deepresonance.blocks.laser;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.client.ClientHandler;
import mcjty.lib.container.GenericBlock;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import org.lwjgl.input.Keyboard;

import java.util.List;

public class LaserBlock extends GenericBlock {
    private IIcon icons[] = new IIcon[2];

    public LaserBlock() {
        super(DeepResonance.instance, Material.iron, LaserTileEntity.class, false);
        setBlockName("laserBlock");
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
            list.add("Place this laser so it faces a lens.");
            list.add("It will infuse the liquid in the tank");
            list.add("depending on the materials used.");
        } else {
            list.add(EnumChatFormatting.WHITE + ClientHandler.getShiftMessage());
        }
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float sidex, float sidey, float sidez) {
        world.setBlockMetadataWithNotify(x, y, z, ((y & 1) == 0 ) ? 1 : 2, 3);
        return super.onBlockActivated(world, x, y, z, player, side, sidex, sidey, sidez);
    }

    @Override
    public String getIdentifyingIconName() {
        return "laserBlockFront";
    }

    @Override
    public String getSideIconName() {
        return "laserBlock";
    }

    @Override
    public void registerBlockIcons(IIconRegister iconRegister) {
        iconInd = iconRegister.registerIcon(DeepResonance.MODID + ":" + getIdentifyingIconName());
        iconSide = iconRegister.registerIcon(this.modBase.getModId() + ":" + getSideIconName());
        icons[0] = iconRegister.registerIcon(DeepResonance.MODID + ":laserBlueBlock");
        icons[1] = iconRegister.registerIcon(DeepResonance.MODID + ":laserRedBlock");

        iconTop = iconRegister.registerIcon(DeepResonance.MODID + ":" + getTopIconName());
        iconBottom = iconRegister.registerIcon(DeepResonance.MODID + ":" + getBottomIconName());
    }

    @Override
    public IIcon getIcon(IBlockAccess blockAccess, int x, int y, int z, int side) {
        return getIcon(side, blockAccess.getBlockMetadata(x, y, z));
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        if (iconInd != null && side == ForgeDirection.SOUTH.ordinal()) {
            return iconInd;
        } else if (iconTop != null && side == ForgeDirection.UP.ordinal()) {
            return iconTop;
        } else if (iconBottom != null && side == ForgeDirection.DOWN.ordinal()) {
            return iconBottom;
        } else {
            if (meta == 0) {
                return iconSide;
            } else if (meta == 1) {
                return icons[0];
            } else {
                return icons[1];
            }
        }
    }

}
