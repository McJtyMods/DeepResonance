package mcjty.deepresonance.blocks.purifier;

import mcjty.deepresonance.blocks.GenericDRBlock;
import mcjty.deepresonance.client.ClientHandler;
import mcjty.deepresonance.proxy.GuiProxy;
import mcjty.lib.gui.GenericGuiContainer;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import java.util.List;
import java.util.function.BiFunction;

public class PurifierBlock extends GenericDRBlock<PurifierTileEntity, PurifierContainer> {

    public PurifierBlock() {
        super(Material.ROCK, PurifierTileEntity.class, PurifierContainer::new, "purifier", true);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public BiFunction<PurifierTileEntity, PurifierContainer, GenericGuiContainer<? super PurifierTileEntity>> getGuiFactory() {
        return GuiPurifier::new;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public List<String> getWailaBody(ItemStack itemStack, List<String> currentTip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        return currentTip;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack itemStack, World player, List<String> list, ITooltipFlag advancedToolTip) {
        super.addInformation(itemStack, player, list, advancedToolTip);
        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
            list.add("This machine needs filter material and will purify");
            list.add("the liquid crystal from the top tank and place it in");
            list.add("another tank below this block.");
            list.add("The spent filter will be ejected in a nearby inventory.");
        } else {
            list.add(TextFormatting.WHITE + ClientHandler.getShiftMessage());
        }
    }

    @Override
    public int getGuiID() {
        return GuiProxy.GUI_PURIFIER;
    }

}
