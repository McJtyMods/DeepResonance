package mcjty.deepresonance.blocks.valve;

import mcjty.deepresonance.DeepResonance;
import mcjty.lib.container.GenericGuiContainer;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.events.ButtonEvent;
import mcjty.lib.gui.layout.HorizontalLayout;
import mcjty.lib.gui.layout.VerticalLayout;
import mcjty.lib.gui.widgets.Button;
import mcjty.lib.gui.widgets.Label;
import mcjty.lib.gui.widgets.Panel;
import mcjty.lib.gui.widgets.TextField;
import mcjty.lib.gui.widgets.*;
import mcjty.lib.network.Argument;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

public class GuiValve extends GenericGuiContainer<ValveTileEntity> {
    public static final int VALVE_WIDTH = 180;
    public static final int VALVE_HEIGHT = 152;

    private static final ResourceLocation iconLocation = new ResourceLocation(DeepResonance.MODID, "textures/gui/valve.png");

    private TextField minPurity;
    private TextField minStrength;
    private TextField minEfficiency;

    public GuiValve(ValveTileEntity valveTileEntity, ValveContainer container) {
        super(DeepResonance.instance, DeepResonance.networkHandler.getNetworkWrapper(), valveTileEntity, container, 0, "valve");

        xSize = VALVE_WIDTH;
        ySize = VALVE_HEIGHT;
    }

    @Override
    public void initGui() {
        super.initGui();

        Panel toplevel = new Panel(mc, this).setBackground(iconLocation).setLayout(new VerticalLayout());

        minPurity = new TextField(mc, this).setTooltips("The minimum purity % to", "accept the liquid");
        minStrength = new TextField(mc, this).setTooltips("The minimum strength % to", "accept the liquid");
        minEfficiency = new TextField(mc, this).setTooltips("The minimum efficiency % to", "accept the liquid");
        minPurity.setText(Integer.toString((int) (tileEntity.getMinPurity() * 100))).setDesiredWidth(50);
        minStrength.setText(Integer.toString((int) (tileEntity.getMinStrength() * 100))).setDesiredWidth(50);
        minEfficiency.setText(Integer.toString((int) (tileEntity.getMinEfficiency() * 100))).setDesiredWidth(50);
        Button applyButton = new Button(mc, this)
                .setText("Apply")
                .setTooltips("Apply the new setting")
                .setDesiredHeight(14)
                .addButtonEvent(new ButtonEvent() {
                    @Override
                    public void buttonClicked(Widget parent) {
                        updateSettings();
                    }
                });
        Panel purityPanel = new Panel(mc, this).setLayout(new HorizontalLayout())
                .setDesiredHeight(16)
                .addChild(new Label(mc, this).setText("Purity").setDesiredWidth(60))
                .addChild(minPurity);
        Panel strengthPanel = new Panel(mc, this).setLayout(new HorizontalLayout())
                .setDesiredHeight(16)
                .addChild(new Label(mc, this).setText("Strength").setDesiredWidth(60))
                .addChild(minStrength)
                .addChild(applyButton);
        Panel efficiencyPanel = new Panel(mc, this).setLayout(new HorizontalLayout())
                .setDesiredHeight(16)
                .addChild(new Label(mc, this).setText("Efficiency").setDesiredWidth(60))
                .addChild(minEfficiency);

        toplevel.addChild(purityPanel).addChild(strengthPanel).addChild(efficiencyPanel);

        toplevel.setBounds(new Rectangle(guiLeft, guiTop, xSize, ySize));

        window = new Window(this, toplevel);
    }

    private void updateSettings() {
        int purity = 0;
        try {
            purity = Integer.parseInt(minPurity.getText());
        } catch (NumberFormatException e) {
        }
        int strength = 0;
        try {
            strength = Integer.parseInt(minStrength.getText());
        } catch (NumberFormatException e) {
        }
        int efficiency = 0;
        try {
            efficiency = Integer.parseInt(minEfficiency.getText());
        } catch (NumberFormatException e) {
        }
        tileEntity.setMinPurity(purity / 100.0f);
        tileEntity.setMinStrength(strength / 100.0f);
        tileEntity.setMinEfficiency(efficiency / 100.0f);
        sendServerCommand(DeepResonance.networkHandler.getNetworkWrapper(), ValveTileEntity.CMD_SETTINGS,
                new Argument("purity", purity / 100.0f),
                new Argument("strength", strength / 100.0f),
                new Argument("efficiency", efficiency / 100.0f));
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float v, int i, int i2) {
        drawWindow();
    }
}
