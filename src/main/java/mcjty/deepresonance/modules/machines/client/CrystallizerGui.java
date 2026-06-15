package mcjty.deepresonance.modules.machines.client;

import com.mojang.blaze3d.vertex.PoseStack;
import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.modules.machines.MachinesModule;
import mcjty.deepresonance.modules.machines.block.CrystallizerTileEntity;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.gui.GenericGuiContainer;
import mcjty.lib.gui.ManualEntry;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.layout.PositionalLayout;
import mcjty.lib.gui.widgets.EnergyBar;
import mcjty.lib.gui.widgets.Label;
import mcjty.lib.gui.widgets.Panel;
import mcjty.rftoolsbase.tools.ManualHelper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

import javax.annotation.Nonnull;
import java.awt.*;

public class CrystallizerGui extends GenericGuiContainer<CrystallizerTileEntity, GenericContainer> {

    public static final int CRYSTALIZER_WIDTH = 180;
    public static final int CRYSTALIZER_HEIGHT = 152;

    private static final ResourceLocation GUI = ResourceLocation.fromNamespaceAndPath(DeepResonance.MODID, "textures/gui/crystallizer.png");

    private EnergyBar energyBar;
    private Label percentage;

    public CrystallizerGui(GenericContainer container, Inventory inventory, Component title) {
        super(container, inventory, title, ManualHelper.create("deepresonance:machines/crystallizer"));

        imageWidth = CRYSTALIZER_WIDTH;
        imageHeight = CRYSTALIZER_HEIGHT;
    }

    @Override
    public void init() {
        super.init();

        CrystallizerTileEntity be = getBE();
        long maxEnergyStored = be.getMaxPower();
        energyBar = new EnergyBar()
                .vertical()
                .maxValue(maxEnergyStored)
                .hint(new PositionalLayout.PositionalHint(10, 7, 8, 54))
                .showText(false)
                .value(be.getCurrentPower());

        percentage = new Label()
                .hint(new PositionalLayout.PositionalHint(54, 44, 32, 14));

        Panel toplevel = new Panel()
                .background(GUI)
                .layout(new PositionalLayout())
                .children(energyBar, percentage);
        toplevel.setBounds(new Rectangle(leftPos, topPos, imageWidth, imageHeight));

        window = new Window(this, toplevel);
    }

    @Override
    protected void renderBg(@Nonnull GuiGraphics graphics, float partialTicks, int x, int y) {
        CrystallizerTileEntity be = getBE();
        percentage.text(be.getProgress() + "%");
        updateEnergyBar(energyBar);
        super.renderBg(graphics, partialTicks, x, y);
    }

    public static void register(RegisterMenuScreensEvent event) {
        event.register(MachinesModule.CRYSTALIZER_CONTAINER.get(), CrystallizerGui::new);
    }
}
