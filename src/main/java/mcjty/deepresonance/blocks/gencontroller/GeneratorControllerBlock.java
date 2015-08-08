package mcjty.deepresonance.blocks.gencontroller;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mcjty.container.GenericBlock;
import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.blocks.generator.GeneratorSetup;
import mcjty.deepresonance.blocks.generator.GeneratorTileEntity;
import mcjty.varia.BlockTools;
import mcjty.varia.Coordinate;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import org.lwjgl.input.Keyboard;

import java.util.List;

public class GeneratorControllerBlock extends GenericBlock {

    private IIcon iconOn;
    private IIcon iconOff;

    public GeneratorControllerBlock() {
        super(DeepResonance.instance, Material.iron, GeneratorControllerTileEntity.class, false);
        setBlockName("generatorControllerBlock");
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

        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
            list.add(EnumChatFormatting.WHITE + "Part of a generator multi-block.");
            list.add(EnumChatFormatting.WHITE + "Use this block to turn on/off the reactor with");
            list.add(EnumChatFormatting.WHITE + "a redstone signal.");
        } else {
            list.add(EnumChatFormatting.WHITE + DeepResonance.SHIFT_MESSAGE);
        }
    }

    @Override
    public String getSideIconName() {
        return "generatorControllerOn";
    }

    @Override
    public void registerBlockIcons(IIconRegister iconRegister) {
        super.registerBlockIcons(iconRegister);
        iconOn = iconRegister.registerIcon(DeepResonance.MODID + ":generatorControllerOn");
        iconOff = iconRegister.registerIcon(DeepResonance.MODID + ":generatorControllerOff");
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
        checkRedstone(world, x, y, z);

        if (!world.isRemote) {
            int meta = world.getBlockMetadata(x, y, z);
            Coordinate thisCoord = new Coordinate(x, y, z);
            for (ForgeDirection direction : ForgeDirection.values()) {
                if (!direction.equals(ForgeDirection.UNKNOWN)) {
                    Coordinate newC = thisCoord.addDirection(direction);
                    Block b = world.getBlock(newC.getX(), newC.getY(), newC.getZ());
                    if (b == GeneratorSetup.generatorBlock) {
                        GeneratorTileEntity generatorTileEntity = (GeneratorTileEntity) world.getTileEntity(newC.getX(), newC.getY(), newC.getZ());
                        boolean active = BlockTools.getRedstoneSignalIn(meta);
                        generatorTileEntity.activate(active);
                    }
                }
            }
            world.setBlockMetadataWithNotify(x, y, z, meta, 3);

        }
    }

    @Override
    public IIcon getIconInd(IBlockAccess blockAccess, int x, int y, int z, int meta) {
        if (BlockTools.getRedstoneSignalIn(meta)) {
            return iconOn;
        } else {
            return iconOff;
        }
    }
}
