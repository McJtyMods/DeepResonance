package mcjty.deepresonance.blocks.laser;

import cpw.mods.fml.common.Optional;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.api.redstone.IRedstoneConnectable;
import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.client.ClientHandler;
import mcjty.deepresonance.gui.GuiProxy;
import mcjty.lib.container.GenericBlock;
import mcjty.lib.varia.BlockTools;
import net.minecraft.block.Block;
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
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import org.lwjgl.input.Keyboard;

import java.util.List;

@Optional.InterfaceList({
        @Optional.Interface(iface = "crazypants.enderio.api.redstone.IRedstoneConnectable", modid = "EnderIO")})
public class LaserBlock extends GenericBlock implements IRedstoneConnectable {
    private IIcon icons[] = new IIcon[4];

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
        icons[2] = iconRegister.registerIcon(DeepResonance.MODID + ":laserGreenBlock");
        icons[3] = iconRegister.registerIcon(DeepResonance.MODID + ":laserYellowBlock");

        iconTop = iconRegister.registerIcon(DeepResonance.MODID + ":" + getTopIconName());
        iconBottom = iconRegister.registerIcon(DeepResonance.MODID + ":" + getBottomIconName());
    }

    @Override
    protected void rotateBlock(World world, int x, int y, int z) {
        super.rotateBlock(world, x, y, z);
        if (world.isRemote) {
            // Make sure rendering is up to date.
            world.markBlockForUpdate(x, y, z);
        }
    }

    @Override
    public boolean shouldRedstoneConduitConnect(World world, int x, int y, int z, ForgeDirection from) {
        return true;
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
        checkRedstoneWithTE(world, x, y, z);
    }

    @Override
    public IIcon getIcon(IBlockAccess blockAccess, int x, int y, int z, int side) {
        return getIconInternal(side, blockAccess.getBlockMetadata(x, y, z));
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        return getIconInternal(side, meta);
    }

    private IIcon getIconInternal(int side, int meta) {
        ForgeDirection k = getOrientation(meta);
        if (iconInd != null && side == k.ordinal()) {
            return iconInd;
        } else if (iconTop != null && side == BlockTools.getTopDirection(k).ordinal()) {
            return iconTop;
        } else if (iconBottom != null && side ==  BlockTools.getBottomDirection(k).ordinal()) {
            return iconBottom;
        } else {
            // Due to meta limitation we can't have a yellow side here!
            int color = (meta >> 2) & 3;

            if (color == LaserTileEntity.COLOR_BLUE) {
                return icons[0];
            } else if (color == LaserTileEntity.COLOR_RED) {
                return icons[1];
            } else if (color == LaserTileEntity.COLOR_GREEN) {
                return icons[2];
//            } else if (color == LaserTileEntity.COLOR_YELLOW) {
//                return icons[3];
            } else {
                return iconSide;
            }
        }
    }

}
