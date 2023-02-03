package mcjty.deepresonance.modules.pedestal.client;

import com.mojang.blaze3d.vertex.PoseStack;
import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.modules.pedestal.PedestalModule;
import mcjty.deepresonance.modules.pedestal.block.PedestalTileEntity;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.gui.GenericGuiContainer;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.layout.PositionalLayout;
import mcjty.lib.gui.widgets.Panel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import javax.annotation.Nonnull;
import java.awt.*;

public class PedestalGui extends GenericGuiContainer<PedestalTileEntity, GenericContainer> {

    public static final int PEDESTAL_WIDTH = 180;
    public static final int PEDESTAL_HEIGHT = 152;

    private static final ResourceLocation iconLocation = new ResourceLocation(DeepResonance.MODID, "textures/gui/pedestal.png");

    public PedestalGui(PedestalTileEntity tileEntity, GenericContainer container, Inventory inventory) {
        super(tileEntity, container, inventory, PedestalModule.PEDESTAL.get().getManualEntry());

        imageWidth = PEDESTAL_WIDTH;
        imageHeight = PEDESTAL_HEIGHT;
    }

    @Override
    public void init() {
        super.init();

        Panel toplevel = new Panel().background(iconLocation).layout(new PositionalLayout());
        toplevel.setBounds(new Rectangle(leftPos, topPos, imageWidth, imageHeight));

        window = new Window(this, toplevel);
    }


    @Override
    protected void renderBg(@Nonnull PoseStack matrixStack, float partialTicks, int x, int y) {
        drawWindow(matrixStack);
    }

    public static void register() {
        register(PedestalModule.CONTAINER_PEDESTAL.get(), PedestalGui::new);
    }
}
