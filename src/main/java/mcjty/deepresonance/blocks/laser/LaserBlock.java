package mcjty.deepresonance.blocks.laser;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.client.ClientHandler;
import mcjty.deepresonance.gui.GuiProxy;
import mcjty.lib.container.GenericBlock;
import mcjty.lib.varia.BlockTools;
import net.minecraft.block.material.Material;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
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
        return GuiProxy.GUI_LASER;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public GuiContainer createClientGui(EntityPlayer entityPlayer, TileEntity tileEntity) {
        LaserTileEntity laserTileEntity = (LaserTileEntity) tileEntity;
        LaserContainer laserContainer = new LaserContainer(entityPlayer, laserTileEntity);
        return new GuiLaser(laserTileEntity, laserContainer);
    }

    @Override
    public Container createServerContainer(EntityPlayer entityPlayer, TileEntity tileEntity) {
        return new LaserContainer(entityPlayer, (LaserTileEntity) tileEntity);
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
        LaserTileEntity laserTileEntity = (LaserTileEntity) blockAccess.getTileEntity(x, y, z);
        return getIconInternal(side, blockAccess.getBlockMetadata(x, y, z), laserTileEntity.getColor());
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        return getIconInternal(side, meta, 0);
    }

    private IIcon getIconInternal(int side, int meta, int color) {
        ForgeDirection k = getOrientation(meta);
        if (iconInd != null && side == k.ordinal()) {
            return iconInd;
        } else if (iconTop != null && side == BlockTools.getTopDirection(k).ordinal()) {
            return iconTop;
        } else if (iconBottom != null && side ==  BlockTools.getBottomDirection(k).ordinal()) {
            return iconBottom;
        } else {
            if (color == 0) {
                return iconSide;
            } else if (color == 1) {
                return icons[0];
            } else {
                return icons[1];
            }
        }
    }

}
