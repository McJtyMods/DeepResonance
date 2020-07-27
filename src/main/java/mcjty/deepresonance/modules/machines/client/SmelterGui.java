package mcjty.deepresonance.modules.machines.client;

import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.client.AbstractDeepResonanceGui;
import mcjty.deepresonance.modules.machines.tile.SmelterTileEntity;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.layout.PositionalLayout;
import mcjty.lib.gui.widgets.EnergyBar;
import mcjty.lib.gui.widgets.ImageLabel;
import mcjty.lib.gui.widgets.Label;
import mcjty.lib.gui.widgets.Panel;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

/**
 * Created by Elec332 on 27-7-2020
 */
public class SmelterGui extends AbstractDeepResonanceGui<SmelterTileEntity> {

    public static final int SMELTER_WIDTH = 180;
    public static final int SMELTER_HEIGHT = 152;

    private EnergyBar energyBar;
    private ImageLabel burningImage;
    private Label percentage;

    private static final ResourceLocation iconLocation = new ResourceLocation(DeepResonance.MODID, "textures/gui/smelter.png");
    private static final ResourceLocation iconBurning = new ResourceLocation(DeepResonance.MODID, "textures/gui/burning.png");

    public SmelterGui(SmelterTileEntity tile, GenericContainer container, PlayerInventory inventory) {
        super(tile, container, inventory);

        xSize = SMELTER_WIDTH;
        ySize = SMELTER_HEIGHT;
    }

    @Override
    public void init() {
        super.init();

        long maxEnergyStored = tileEntity.getMaxPower();
        energyBar = new EnergyBar().vertical().maxValue(maxEnergyStored).hint(new PositionalLayout.PositionalHint(10, 7, 8, 54)).showText(false);
        energyBar.value(tileEntity.getCurrentPower());

        burningImage = new ImageLabel()
                .image(iconBurning, 0, 0)
                .hint(new PositionalLayout.PositionalHint(90, 2, 64, 64));

        percentage = new Label()
                .hint(new PositionalLayout.PositionalHint(54, 44, 32, 14));

        Panel toplevel = new Panel()
                .background(iconLocation)
                .layout(new PositionalLayout())
                .children(energyBar, burningImage, percentage);
        toplevel.setBounds(new Rectangle(guiLeft, guiTop, xSize, ySize));

        window = new Window(this, toplevel);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float v, int i, int i2) {
        int progress = tileEntity.getProgress();
        if (0 < progress && progress < 100) {
            int p = ((progress / 3) % 9) + 1;
            int x = (p % 4) * 64;
            int y = (p / 4) * 64;
            burningImage.image(iconBurning, x, y);
        } else {
            burningImage.image(iconBurning, 0, 0);
        }
        percentage.text(progress + "%");

        drawWindow();

        energyBar.value(tileEntity.getCurrentPower());
    }

}
