package mcjty.deepresonance.modules.machines.client;

import com.mojang.blaze3d.vertex.PoseStack;
import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.modules.machines.MachinesModule;
import mcjty.deepresonance.modules.machines.block.LaserTileEntity;
import mcjty.deepresonance.modules.machines.data.InfusingBonus;
import mcjty.deepresonance.modules.machines.util.config.LaserConfig;
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
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nonnull;
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

    public LaserGui(LaserTileEntity tileEntity, GenericContainer container, Inventory inventory) {
        super(tileEntity, container, inventory, ManualEntry.EMPTY);

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
                .maxValue(LaserConfig.CRYSTAL_LIQUID_MAXIMUM.get())
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
    protected void renderBg(@Nonnull GuiGraphics graphics, float partialTicks, int x, int y) {
        Slot slot = this.menu.getSlot(LaserTileEntity.SLOT_CATALYST);
        if (slot.hasItem()) {
            InfusingBonus bonus = LaserTileEntity.getInfusingBonus(slot.getItem());
            if (bonus != null) {
                setBonusText(bonus.purityModifier(), "P", purifyBonus);
                setBonusText(bonus.strengthModifier(), "S", strengthBonus);
                setBonusText(bonus.efficiencyModifier(), "E", efficiencyBonus);
            } else {
                purifyBonus.text("Not a catalyst!");
                strengthBonus.text("");
                efficiencyBonus.text("");
            }
        } else {
            purifyBonus.text("Catalyst missing!");
            strengthBonus.text("");
            efficiencyBonus.text("");
        }
        crystalBar.value((int) tileEntity.getCrystalLiquid());
        updateEnergyBar(energyBar);

        super.renderBg(graphics, partialTicks, x, y);
    }

    private void setBonusText(InfusingBonus.Modifier modifier, String prefix, Label label) {
        if (Math.abs(modifier.bonus()) > 0.01f) {
            label.text(prefix + ": " + formatted(modifier.bonus()) + "% (cap " + formatted(modifier.maxOrMin()) + ")");
        } else {
            label.text(prefix + ": none");
        }
    }

    private String formatted(float f) {
        return new DecimalFormat("##.#").format(f);
    }

    public static void register() {
        register(MachinesModule.LASER_CONTAINER.get(), LaserGui::new);
    }
}
