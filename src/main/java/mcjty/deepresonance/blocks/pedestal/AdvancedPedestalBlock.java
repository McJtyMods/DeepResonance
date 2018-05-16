package mcjty.deepresonance.blocks.pedestal;

import mcjty.deepresonance.blocks.GenericDRBlock;
import mcjty.deepresonance.client.ClientHandler;
import mcjty.deepresonance.gui.GuiProxy;
import mcjty.lib.gui.GenericGuiContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import java.util.List;

public class AdvancedPedestalBlock extends GenericDRBlock<AdvancedPedestalTileEntity, PedestalContainer> {

    public AdvancedPedestalBlock() {
        super(Material.IRON, AdvancedPedestalTileEntity.class, PedestalContainer.class, "advanced_pedestal", false);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public Class<? extends GenericGuiContainer<PedestalTileEntity>> getGuiClass() {
        return GuiPedestal.class;
    }

    @Override
    public int getGuiID() {
        return GuiProxy.GUI_PEDESTAL;
    }


    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack itemStack, World player, List<String> list, ITooltipFlag advancedToolTip) {
        super.addInformation(itemStack, player, list, advancedToolTip);

        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
            list.add("This block can place crystals and");
            list.add("pick up spent crystals");
            list.add("This advanced version helps stability");
            list.add("in combination with the Pulser");
        } else {
            list.add(TextFormatting.WHITE + ClientHandler.getShiftMessage());
        }
    }
}
