package mcjty.deepresonance.modules.machines.client.gui;

import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.client.AbstractDeepResonanceGui;
import mcjty.deepresonance.modules.machines.tile.TileEntityCrystallizer;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.layout.PositionalLayout;
import mcjty.lib.gui.widgets.EnergyBar;
import mcjty.lib.gui.widgets.Label;
import mcjty.lib.gui.widgets.Panel;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

/**
 * Created by Elec332 on 30-7-2020
 */
public class CrystallizerGui extends AbstractDeepResonanceGui<TileEntityCrystallizer> {

    public static final int CRYSTALIZER_WIDTH = 180;
    public static final int CRYSTALIZER_HEIGHT = 152;

    private static final ResourceLocation iconLocation = new ResourceLocation(DeepResonance.MODID, "textures/gui/crystallizer.png");

    private EnergyBar energyBar;
    private Label percentage;

    public CrystallizerGui(TileEntityCrystallizer tileEntity, GenericContainer container, PlayerInventory inventory) {
        super(tileEntity, container, inventory);

        xSize = CRYSTALIZER_WIDTH;
        ySize = CRYSTALIZER_HEIGHT;
    }

    @Override
    public void init() {
        super.init();

        long maxEnergyStored = tileEntity.getMaxPower();
        energyBar = new EnergyBar()
                .vertical()
                .maxValue(maxEnergyStored)
                .hint(new PositionalLayout.PositionalHint(10, 7, 8, 54))
                .showText(false)
                .value(tileEntity.getCurrentPower());

        percentage = new Label()
                .hint(new PositionalLayout.PositionalHint(54, 44, 32, 14));

        Panel toplevel = new Panel()
                .background(iconLocation)
                .layout(new PositionalLayout())
                .children(energyBar, percentage);
        toplevel.setBounds(new Rectangle(guiLeft, guiTop, xSize, ySize));

        window = new Window(this, toplevel);
    }


    @Override
    protected void drawGuiContainerBackgroundLayer(float v, int i, int i2) {
        percentage.text(tileEntity.getProgress() + "%");
        energyBar.value(tileEntity.getCurrentPower());

        super.drawGuiContainerBackgroundLayer(v, i, i2);
    }

}
