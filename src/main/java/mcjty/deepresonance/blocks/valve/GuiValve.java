package mcjty.deepresonance.blocks.valve;

import mcjty.container.GenericGuiContainer;
import mcjty.deepresonance.DeepResonance;
import mcjty.gui.Window;
import mcjty.gui.layout.PositionalLayout;
import mcjty.gui.widgets.Panel;
import mcjty.gui.widgets.Widget;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

public class GuiValve extends GenericGuiContainer<ValveTileEntity> {
    public static final int VALVE_WIDTH = 180;
    public static final int VALVE_HEIGHT = 152;

    private static final ResourceLocation iconLocation = new ResourceLocation(DeepResonance.MODID, "textures/gui/valve.png");

    public GuiValve(ValveTileEntity valveTileEntity, ValveContainer container) {
        super(DeepResonance.instance, DeepResonance.networkHandler.getNetworkWrapper(), valveTileEntity, container, 0, "valve");

        xSize = VALVE_WIDTH;
        ySize = VALVE_HEIGHT;
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
