package mcjty.deepresonance.modules.machines.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.api.infusion.InfusionBonus;
import mcjty.deepresonance.api.infusion.InfusionModifier;
import mcjty.deepresonance.modules.machines.MachinesModule;
import mcjty.deepresonance.modules.machines.block.LaserTileEntity;
import mcjty.lib.base.StyleConfig;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.gui.GenericGuiContainer;
import mcjty.lib.gui.ManualEntry;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.layout.HorizontalAlignment;
import mcjty.lib.gui.layout.PositionalLayout;
import mcjty.lib.gui.widgets.EnergyBar;
import mcjty.lib.gui.widgets.Label;
import mcjty.lib.gui.widgets.Panel;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.text.DecimalFormat;

public class LaserGui extends GenericGuiContainer<LaserTileEntity, GenericContainer> {

    public static final int LASER_WIDTH = 180;
    public static final int LASER_HEIGHT = 152;

    private EnergyBar energyBar;
    private EnergyBar crystalBar;
    private Label purifyBonus;
    private Label strengthBonus;
    private Label efficiencyBonus;

    private static final ResourceLocation GUI = new ResourceLocation(DeepResonance.MODID, "textures/gui/laser.png");

    public LaserGui(LaserTileEntity tileEntity, GenericContainer container, PlayerInventory inventory) {
        super(tileEntity, container, inventory, ManualEntry.EMPTY); // @todo 1.16 manual

        imageWidth = LASER_WIDTH;
        imageHeight = LASER_HEIGHT;
    }

    @Override
    public void init() {
        super.init();

        energyBar = new EnergyBar()
                .vertical()
                .maxValue(tileEntity.getMaxPower())
                .hint(new PositionalLayout.PositionalHint(10, 7, 8, 59))
                .showText(false)
                .value(tileEntity.getCurrentPower());

        crystalBar = new EnergyBar()
                .vertical()
                .maxValue(MachinesModule.laserConfig.crystalLiquidMaximum.get())
                .hint(new PositionalLayout.PositionalHint(153, 7, 19, 38))
                .showText(false)
                .setEnergyOnColor(0xff0066ff)
                .setEnergyOffColor(0xff003366)
                .setSpacerColor(0xff001122)
                .value(0);

        purifyBonus = new Label()
                .horizontalAlignment(HorizontalAlignment.ALIGN_LEFT)
                .hint(new PositionalLayout.PositionalHint(5, 5, 100, 14));
        strengthBonus = new Label()
                .horizontalAlignment(HorizontalAlignment.ALIGN_LEFT)
                .hint(new PositionalLayout.PositionalHint(5, 23, 100, 14));
        efficiencyBonus = new Label()
                .horizontalAlignment(HorizontalAlignment.ALIGN_LEFT)
                .hint(new PositionalLayout.PositionalHint(5, 41, 100, 14));

        Panel catalystPanel = new Panel()
                .layout(new PositionalLayout())
                .hint(new PositionalLayout.PositionalHint(41, 7, 109, 59))
                .filledRectThickness(-2)
                .filledBackground(StyleConfig.colorListBackground)
                .children(purifyBonus, strengthBonus, efficiencyBonus);

        Panel toplevel = new Panel()
                .background(GUI)
                .layout(new PositionalLayout())
                .children(energyBar, catalystPanel, crystalBar);
        toplevel.setBounds(new Rectangle(leftPos, topPos, imageWidth, imageHeight));

        window = new Window(this, toplevel);
    }

    @Override
    protected void renderBg(MatrixStack matrixStack, float partialTicks, int x, int y) {
        InfusionBonus bonus = tileEntity.getActiveBonus();
        if (bonus.isEmpty()) {
            purifyBonus.text("No active catalyst");
            strengthBonus.text("");
            efficiencyBonus.text("");
        } else {
            setBonusText(bonus.getPurityModifier(), "P", purifyBonus);
            setBonusText(bonus.getStrengthModifier(), "S", strengthBonus);
            setBonusText(bonus.getEfficiencyModifier(), "E", efficiencyBonus);
        }
        energyBar.value(tileEntity.getCurrentPower());
        crystalBar.value((int) tileEntity.getCrystalLiquid());

        super.renderBg(matrixStack, partialTicks, x, y);
    }

    private void setBonusText(InfusionModifier modifier, String prefix, Label label) {
        if (Math.abs(modifier.getBonus()) > 0.01f) {
            label.text(prefix + ": " + formatted(modifier.getBonus()) + "% (cap " + formatted(modifier.getMaxOrMin()) + ")");
        } else {
            label.text(prefix + ": none");
        }
    }

    private String formatted(float f) {
        return new DecimalFormat("##.#").format(f);
    }

}
