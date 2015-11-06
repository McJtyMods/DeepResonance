package mcjty.deepresonance.blocks.laser;

import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.config.ConfigMachines;
import mcjty.lib.base.StyleConfig;
import mcjty.lib.container.GenericGuiContainer;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.layout.HorizontalAlignment;
import mcjty.lib.gui.layout.PositionalLayout;
import mcjty.lib.gui.widgets.EnergyBar;
import mcjty.lib.gui.widgets.Label;
import mcjty.lib.gui.widgets.Panel;
import mcjty.lib.gui.widgets.Widget;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.ForgeDirection;

import java.awt.*;
import java.text.DecimalFormat;

public class GuiLaser extends GenericGuiContainer<LaserTileEntity> {
    public static final int LASER_WIDTH = 180;
    public static final int LASER_HEIGHT = 152;

    private EnergyBar energyBar;
    private EnergyBar crystalBar;
    private Label purifyBonus;
    private Label strengthBonus;
    private Label efficiencyBonus;

    private static final ResourceLocation iconLocation = new ResourceLocation(DeepResonance.MODID, "textures/gui/laser.png");

    public GuiLaser(LaserTileEntity laserTileEntity, LaserContainer container) {
        super(DeepResonance.instance, DeepResonance.networkHandler.getNetworkWrapper(), laserTileEntity, container, 0, "laser");
        laserTileEntity.setCurrentRF(laserTileEntity.getEnergyStored(ForgeDirection.DOWN));

        xSize = LASER_WIDTH;
        ySize = LASER_HEIGHT;
    }

    @Override
    public void initGui() {
        super.initGui();

        int maxEnergyStored = tileEntity.getMaxEnergyStored(ForgeDirection.DOWN);
        energyBar = new EnergyBar(mc, this).setVertical().setMaxValue(maxEnergyStored).setLayoutHint(new PositionalLayout.PositionalHint(10, 7, 8, 59)).setShowText(false);
        energyBar.setValue(tileEntity.getCurrentRF());

        crystalBar = new EnergyBar(mc, this).setVertical().setMaxValue(ConfigMachines.Laser.crystalLiquidMaximum).setLayoutHint(new PositionalLayout.PositionalHint(153, 7, 19, 38)).setShowText(false);
        crystalBar.setEnergyOnColor(0xff0066ff);
        crystalBar.setEnergyOffColor(0xff003366);
        crystalBar.setSpacerColor(0xff001122);
        crystalBar.setValue(0);

        purifyBonus = new Label(mc, this).setHorizontalAlignment(HorizontalAlignment.ALIGH_LEFT);
        purifyBonus.setLayoutHint(new PositionalLayout.PositionalHint(5, 5, 100, 14));
        strengthBonus = new Label(mc, this).setHorizontalAlignment(HorizontalAlignment.ALIGH_LEFT);
        strengthBonus.setLayoutHint(new PositionalLayout.PositionalHint(5, 23, 100, 14));
        efficiencyBonus = new Label(mc, this).setHorizontalAlignment(HorizontalAlignment.ALIGH_LEFT);
        efficiencyBonus.setLayoutHint(new PositionalLayout.PositionalHint(5, 41, 100, 14));

        Panel catalystPanel = new Panel(mc, this).setLayout(new PositionalLayout()).setLayoutHint(new PositionalLayout.PositionalHint(41, 7, 109, 59))
                .setFilledRectThickness(-2)
                .setFilledBackground(StyleConfig.colorListBackground)
                .addChild(purifyBonus)
                .addChild(strengthBonus)
                .addChild(efficiencyBonus);

        Widget toplevel = new Panel(mc, this).setBackground(iconLocation).setLayout(new PositionalLayout()).addChild(energyBar).addChild(catalystPanel).addChild(crystalBar);
        toplevel.setBounds(new Rectangle(guiLeft, guiTop, xSize, ySize));

        window = new Window(this, toplevel);
        tileEntity.requestRfFromServer(DeepResonance.networkHandler.getNetworkWrapper());
        tileEntity.requestCrystalLiquidFromServer();
    }


    @Override
    protected void drawGuiContainerBackgroundLayer(float v, int i, int i2) {
        Slot slot = inventorySlots.getSlot(LaserContainer.SLOT_CATALYST);
        if (slot.getHasStack()) {
            InfusingBonus bonus = LaserTileEntity.getInfusingBonus(slot.getStack());
            if (bonus != null) {
                setBonusText(bonus.getPurityModifier(), "P", purifyBonus);
                setBonusText(bonus.getStrengthModifier(), "S", strengthBonus);
                setBonusText(bonus.getEfficiencyModifier(), "E", efficiencyBonus);
            } else {
                purifyBonus.setText("Not a catalyst!");
                strengthBonus.setText("");
                efficiencyBonus.setText("");
            }
        } else {
            purifyBonus.setText("Catalyst missing!");
            strengthBonus.setText("");
            efficiencyBonus.setText("");
        }


        drawWindow();

        energyBar.setValue(tileEntity.getCurrentRF());
        crystalBar.setValue(LaserTileEntity.getCrystalLiquidClient());

        tileEntity.requestRfFromServer(DeepResonance.networkHandler.getNetworkWrapper());
        tileEntity.requestCrystalLiquidFromServer();
    }

    private void setBonusText(InfusingBonus.Modifier modifier, String prefix, Label label) {
        if (Math.abs(modifier.getBonus()) > 0.01f) {
            label.setText(prefix + ": " + formatted(modifier.getBonus()) + "% (cap " + formatted(modifier.getMaxOrMin()) + ")");
        } else {
            label.setText(prefix + ": none");
        }
    }

    private String formatted(float f) {
        return new DecimalFormat("##.#").format(f);
    }
}
