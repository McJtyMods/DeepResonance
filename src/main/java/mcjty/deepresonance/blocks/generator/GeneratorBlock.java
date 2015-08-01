package mcjty.deepresonance.blocks.generator;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mcjty.container.GenericBlock;
import mcjty.deepresonance.DeepResonance;
import mcjty.entity.GenericTileEntity;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import java.util.List;

public class GeneratorBlock extends GenericBlock {

    private IIcon icon;
    private IIcon iconUp;

    public GeneratorBlock() {
        super(DeepResonance.instance, Material.iron, GenericTileEntity.class, true);
        setBlockName("endergenicBlock");
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
}
