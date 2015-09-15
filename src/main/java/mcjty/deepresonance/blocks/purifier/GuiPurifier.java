package mcjty.deepresonance.blocks.purifier;

import mcjty.container.GenericGuiContainer;
import mcjty.deepresonance.DeepResonance;
import mcjty.gui.Window;
import mcjty.gui.layout.PositionalLayout;
import mcjty.gui.widgets.Panel;
import mcjty.gui.widgets.Widget;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

public class GuiPurifier extends GenericGuiContainer<PurifierTileEntity> {
    public static final int PURIFIER_WIDTH = 180;
    public static final int PURIFIER_HEIGHT = 152;

    private static final ResourceLocation iconLocation = new ResourceLocation(DeepResonance.MODID, "textures/gui/purifier.png");

    public GuiPurifier(PurifierTileEntity purifierTileEntity, PurifierContainer container) {
        super(DeepResonance.instance, DeepResonance.networkHandler.getNetworkWrapper(), purifierTileEntity, container, 0, "smelter");

        xSize = PURIFIER_WIDTH;
        ySize = PURIFIER_HEIGHT;
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
