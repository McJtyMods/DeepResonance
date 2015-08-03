package mcjty.deepresonance.blocks.generator;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mcjty.container.GenericBlock;
import mcjty.deepresonance.DeepResonance;
import mcjty.varia.BlockTools;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.List;

public class GeneratorBlock extends GenericBlock {

    private IIcon iconUpDown;
    private IIcon icons[] = new IIcon[8];

    public static final int META_ON = 1;
    public static final int META_OFF = 0;
    public static final int META_HASUPPER = 2;
    public static final int META_HASLOWER = 4;

    public GeneratorBlock() {
        super(DeepResonance.instance, Material.iron, GeneratorTileEntity.class, true);
        setBlockName("generatorBlock");
        setHorizRotation(true);
        setCreativeTab(DeepResonance.tabDeepResonance);
    }

    @Override
    public int getGuiID() {
        return -1;
    }


    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer player, List list, boolean whatIsThis) {
        super.addInformation(itemStack, player, list, whatIsThis);
//
//        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
//            list.add(EnumChatFormatting.WHITE + "Generate power out of ender pearls. You need at");
//            list.add(EnumChatFormatting.WHITE + "least two generators for this to work and the setup");
//            list.add(EnumChatFormatting.WHITE + "is relatively complicated. Timing is crucial.");
//            list.add(EnumChatFormatting.YELLOW + "Infusing bonus: increased power generation and");
//            list.add(EnumChatFormatting.YELLOW + "reduced powerloss for holding pearls.");
//        } else {
//            list.add(EnumChatFormatting.WHITE + RFTools.SHIFT_MESSAGE);
//        }
    }

    @Override
    public String getSideIconName() {
        return "machineGenerator";
    }

    @Override
    protected ForgeDirection getOrientation(int x, int y, int z, EntityLivingBase entityLivingBase) {
        return ForgeDirection.NORTH;
    }

    private void updateMeta(World world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z) & BlockTools.MASK_REDSTONE_IN;
        if (world.getBlock(x, y+1, z) == GeneratorSetup.generatorBlock) {
            meta |= META_HASUPPER;
        }
        if (world.getBlock(x, y-1, z) == GeneratorSetup.generatorBlock) {
            meta |= META_HASLOWER;
        }
        world.setBlockMetadataWithNotify(x, y, z, meta, 3);
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entityLivingBase, ItemStack itemStack) {
        super.onBlockPlacedBy(world, x, y, z, entityLivingBase, itemStack);
        if (!world.isRemote) {
            updateMeta(world, x, y, z);
            if (world.getBlock(x, y+1, z) == GeneratorSetup.generatorBlock) {
                updateMeta(world, x, y+1, z);
            }
            if (world.getBlock(x, y-1, z) == GeneratorSetup.generatorBlock) {
                updateMeta(world, x, y-1, z);
            }
            TileEntity te = world.getTileEntity(x, y, z);
            if (te instanceof GeneratorTileEntity) {
                ((GeneratorTileEntity) te).addBlockToNetwork();
            }
        }
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
        super.breakBlock(world, x, y, z, block, meta);
        if (!world.isRemote) {
            if (world.getBlock(x, y+1, z) == GeneratorSetup.generatorBlock) {
                updateMeta(world, x, y+1, z);
            }
            if (world.getBlock(x, y-1, z) == GeneratorSetup.generatorBlock) {
                updateMeta(world, x, y-1, z);
            }
            TileEntity te = world.getTileEntity(x, y, z);
            if (te instanceof GeneratorTileEntity) {
                ((GeneratorTileEntity) te).removeBlockFromNetwork();
            }
        }
    }

    @Override
    public void registerBlockIcons(IIconRegister iconRegister) {
        iconUpDown = iconRegister.registerIcon(DeepResonance.MODID + ":machineBase");

        IIcon iconFull[] = new IIcon[2];
        IIcon iconTop[] = new IIcon[2];
        IIcon iconBottom[] = new IIcon[2];
        IIcon iconMiddle[] = new IIcon[2];
        for (int i = 0 ; i < 2 ; i++) {
            iconFull[i] = iconRegister.registerIcon(DeepResonance.MODID + ":generatorSideFull" + (i==0 ? "Off" : "On"));
            iconTop[i] = iconRegister.registerIcon(DeepResonance.MODID + ":generatorSideTop" + (i==0 ? "Off" : "On"));
            iconBottom[i] = iconRegister.registerIcon(DeepResonance.MODID + ":generatorSideBottom" + (i==0 ? "Off" : "On"));
            iconMiddle[i] = iconRegister.registerIcon(DeepResonance.MODID + ":generatorSideMiddle" + (i==0 ? "Off" : "On"));
        }

        icons[META_OFF] = iconFull[0];
        icons[META_ON] = iconFull[1];
        icons[META_OFF + META_HASUPPER] = iconBottom[0];
        icons[META_ON + META_HASUPPER] = iconBottom[1];
        icons[META_OFF + META_HASLOWER] = iconTop[0];
        icons[META_ON + META_HASLOWER] = iconTop[1];
        icons[META_OFF + META_HASUPPER + META_HASLOWER] = iconMiddle[0];
        icons[META_ON + META_HASUPPER + META_HASLOWER] = iconMiddle[1];
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
        checkRedstone(world, x, y, z);
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        if (side == ForgeDirection.DOWN.ordinal() || side == ForgeDirection.UP.ordinal()) {
            return iconUpDown;
        }
        return icons[meta & 0x7];
    }

    @Override
    public IIcon getIcon(IBlockAccess blockAccess, int x, int y, int z, int side) {
        if (side == ForgeDirection.DOWN.ordinal() || side == ForgeDirection.UP.ordinal()) {
            return iconUpDown;
        }
        int meta = blockAccess.getBlockMetadata(x, y, z);
        boolean rs = BlockTools.getRedstoneSignalIn(meta);
        if (rs) {
            meta |= META_ON;
        }
        return icons[meta & 0x7];
    }
}
