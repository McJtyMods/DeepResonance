package mcjty.deepresonance.blocks.pedestal;

import mcjty.container.GenericGuiContainer;
import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.blocks.purifier.PurifierContainer;
import mcjty.deepresonance.blocks.purifier.PurifierTileEntity;
import mcjty.gui.Window;
import mcjty.gui.layout.PositionalLayout;
import mcjty.gui.widgets.Panel;
import mcjty.gui.widgets.Widget;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

public class GuiPedestal extends GenericGuiContainer<PedestalTileEntity> {
    public static final int PEDESTAL_WIDTH = 180;
    public static final int PEDESTAL_HEIGHT = 152;

    private static final ResourceLocation iconLocation = new ResourceLocation(DeepResonance.MODID, "textures/gui/pedestal.png");

    public GuiPedestal(PedestalTileEntity pedestalTileEntity, PedestalContainer container) {
        super(DeepResonance.instance, DeepResonance.networkHandler.getNetworkWrapper(), pedestalTileEntity, container, 0, "pedestal");

        xSize = PEDESTAL_WIDTH;
        ySize = PEDESTAL_HEIGHT;
    }

    @Override
    public void initGui() {
        super.initGui();

        Widget toplevel = new Panel(mc, this).setBackground(iconLocation).setLayout(new PositionalLayout());
        toplevel.setBounds(new Rectangle(guiLeft, guiTop, xSize, ySize));

        window = new Window(this, toplevel);
    }


    @Override
    protected void drawGuiContainerBackgroundLayer(float v, int i, int i2) {
        drawWindow();
    }
}
