package mcjty.deepresonance.modules.machines.client;

import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.modules.machines.MachinesModule;
import mcjty.deepresonance.modules.machines.block.ValveTileEntity;
import mcjty.deepresonance.setup.DeepResonanceMessages;
import mcjty.lib.base.StyleConfig;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.gui.GenericGuiContainer;
import mcjty.lib.gui.ManualEntry;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.layout.HorizontalAlignment;
import mcjty.lib.gui.layout.HorizontalLayout;
import mcjty.lib.gui.layout.PositionalLayout;
import mcjty.lib.gui.layout.VerticalLayout;
import mcjty.lib.gui.widgets.Button;
import mcjty.lib.gui.widgets.Label;
import mcjty.lib.gui.widgets.Panel;
import mcjty.lib.gui.widgets.TextField;
import mcjty.lib.gui.widgets.*;
import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.lib.varia.RedstoneMode;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.awt.*;

import static mcjty.deepresonance.modules.machines.block.ValveTileEntity.*;

public class ValveGui extends GenericGuiContainer<ValveTileEntity, GenericContainer> {

    public static final int VALVE_WIDTH = 180;
    public static final int VALVE_HEIGHT = 152;

    private static final ResourceLocation iconLocation = new ResourceLocation(DeepResonance.MODID, "textures/gui/valve.png");
    private static final ResourceLocation iconGuiElements = new ResourceLocation(DeepResonance.MODID, "textures/gui/guielements.png");

    private TextField minPurity;
    private TextField minStrength;
    private TextField minEfficiency;
    private TextField maxAmount;

    public ValveGui(ValveTileEntity tileEntity, GenericContainer container, Inventory inventory) {
        super(tileEntity, container, inventory, ManualEntry.EMPTY); // @todo 1.16 manual

        imageWidth = VALVE_WIDTH;
        imageHeight = VALVE_HEIGHT;
    }

    private ImageChoiceLabel initRedstoneMode() {
        ImageChoiceLabel redstoneMode = new ImageChoiceLabel().
                name("redstone").
                choice(RedstoneMode.REDSTONE_IGNORED.getDescription(), "Redstone mode:\nIgnored", iconGuiElements, 0, 0).
                choice(RedstoneMode.REDSTONE_OFFREQUIRED.getDescription(), "Redstone mode:\nOff to activate", iconGuiElements, 16, 0).
                choice(RedstoneMode.REDSTONE_ONREQUIRED.getDescription(), "Redstone mode:\nOn to activate", iconGuiElements, 32, 0);
        redstoneMode.hint(new PositionalLayout.PositionalHint(154, 47, 16, 16));
        return redstoneMode;
    }

    @Override
    public void init() {
        super.init();

        Panel toplevel = new Panel()
                .background(iconLocation)
                .layout(new PositionalLayout());

        Panel inputPanel = setupInputPanel();
        Panel outputPanel = setupOutputPanel();

        Button applyButton = new Button()
                .channel("apply")
                .text("Apply")
                .tooltips("Apply the new setting")
                .hint(new PositionalLayout.PositionalHint(112, 49, 40, 14));

        ImageChoiceLabel redstoneMode = initRedstoneMode();

        toplevel.children(inputPanel, outputPanel, applyButton, redstoneMode);

        toplevel.setBounds(new Rectangle(leftPos, topPos, imageWidth, imageHeight));

        window = new Window(this, toplevel);

        window.bind(DeepResonanceMessages.INSTANCE, "redstone", tileEntity, GenericTileEntity.VALUE_RSMODE.name());
        window.event("apply", (source, params) -> updateSettings());
    }

    private Panel setupOutputPanel() {
        maxAmount = new TextField()
                .tooltips("The maximum amount of liquid", "in the bottom tank")
                .text(Integer.toString(tileEntity.getMaxMb()))
                .desiredWidth(45)
                .desiredHeight(15);

        return new Panel()
                .layout(new VerticalLayout().setSpacing(1).setVerticalMargin(2))
                .filledRectThickness(-2)
                .filledBackground(StyleConfig.colorListBackground)
                .children(new Label()
                        .text("Max mb")
                        .desiredWidth(50)
                        .desiredHeight(15)
                )
                .children(maxAmount)
                .hint(new PositionalLayout.PositionalHint(112, 6, 60, 40));
    }

    private Panel setupInputPanel() {
        minPurity = new TextField()
                .tooltips("The minimum purity % to", "accept the liquid")
                .text(Integer.toString((int) (tileEntity.getMinPurity() * 100)))
                .desiredWidth(30)
                .desiredHeight(15);
        minStrength = new TextField()
                .tooltips("The minimum strength % to", "accept the liquid")
                .text(Integer.toString((int) (tileEntity.getMinStrength() * 100)))
                .desiredWidth(30)
                .desiredHeight(15);
        minEfficiency = new TextField()
                .tooltips("The minimum efficiency % to", "accept the liquid")
                .text(Integer.toString((int) (tileEntity.getMinEfficiency() * 100)))
                .desiredWidth(30)
                .desiredHeight(15);
        Panel purityPanel = new Panel()
                .layout(new HorizontalLayout())
                .desiredHeight(16)
                .children(new Label()
                        .horizontalAlignment(HorizontalAlignment.ALIGN_LEFT)
                        .text("Purity")
                        .desiredWidth(50)
                )
                .children(minPurity);
        Panel strengthPanel = new Panel()
                .layout(new HorizontalLayout())
                .desiredHeight(16)
                .children(new mcjty.lib.gui.widgets.Label()
                        .horizontalAlignment(HorizontalAlignment.ALIGN_LEFT)
                        .text("Strength")
                        .desiredWidth(50)
                )
                .children(minStrength);
        Panel efficiencyPanel = new Panel()
                .layout(new HorizontalLayout())
                .desiredHeight(16)
                .children(new Label()
                        .horizontalAlignment(HorizontalAlignment.ALIGN_LEFT)
                        .text("Efficiency")
                        .desiredWidth(50)
                )
                .children(minEfficiency);

        return new Panel()
                .layout(new VerticalLayout().setSpacing(1).setVerticalMargin(3))
                .filledRectThickness(-2)
                .filledBackground(StyleConfig.colorListBackground)
                .children(purityPanel, strengthPanel, efficiencyPanel)
                .hint(new PositionalLayout.PositionalHint(9, 6, 100, 58));
    }

    private void updateSettings() {
        int purity = 0;
        try {
            purity = Integer.parseInt(minPurity.getText());
        } catch (NumberFormatException e) {
            //
        }
        int strength = 0;
        try {
            strength = Integer.parseInt(minStrength.getText());
        } catch (NumberFormatException e) {
            //
        }
        int efficiency = 0;
        try {
            efficiency = Integer.parseInt(minEfficiency.getText());
        } catch (NumberFormatException e) {
            //
        }
        int maxMb = 0;
        try {
            maxMb = Integer.parseInt(maxAmount.getText());
        } catch (NumberFormatException e) {
            //
        }

        setValue(DeepResonanceMessages.INSTANCE, VALUE_MINPURITY, purity / 100.0f);
        setValue(DeepResonanceMessages.INSTANCE, VALUE_STRENGTH, strength / 100.0f);
        setValue(DeepResonanceMessages.INSTANCE, VALUE_EFFICIENCY, efficiency / 100.0f);
        setValue(DeepResonanceMessages.INSTANCE, VALUE_MAXMB, maxMb);
    }

    public static void register() {
        register(MachinesModule.VALVE_CONTAINER.get(), ValveGui::new);
    }
}
