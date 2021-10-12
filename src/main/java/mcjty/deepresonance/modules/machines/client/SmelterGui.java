package mcjty.deepresonance.modules.machines.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.modules.machines.block.SmelterTileEntity;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.gui.GenericGuiContainer;
import mcjty.lib.gui.ManualEntry;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.layout.PositionalLayout;
import mcjty.lib.gui.widgets.EnergyBar;
import mcjty.lib.gui.widgets.ImageLabel;
import mcjty.lib.gui.widgets.Label;
import mcjty.lib.gui.widgets.Panel;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

public class SmelterGui extends GenericGuiContainer<SmelterTileEntity, GenericContainer> {

    public static final int SMELTER_WIDTH = 180;
    public static final int SMELTER_HEIGHT = 152;

    private EnergyBar energyBar;
    private ImageLabel burningImage;
    private Label percentage;

    private static final ResourceLocation iconLocation = new ResourceLocation(DeepResonance.MODID, "textures/gui/smelter.png");
    private static final ResourceLocation iconBurning = new ResourceLocation(DeepResonance.MODID, "textures/gui/burning.png");

    public SmelterGui(SmelterTileEntity tile, GenericContainer container, PlayerInventory inventory) {
        super(tile, container, inventory, ManualEntry.EMPTY);   // @todo 1.16 manual

        imageWidth = SMELTER_WIDTH;
        imageHeight = SMELTER_HEIGHT;
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
        toplevel.setBounds(new Rectangle(leftPos, topPos, imageWidth, imageHeight));

        window = new Window(this, toplevel);
    }

    @Override
    protected void renderBg(MatrixStack matrixStack, float partialTicks, int x, int y) {
        int progress = tileEntity.getProgress();
        if (0 < progress && progress < 100) {
            int p = ((progress / 3) % 9) + 1;
            int xx = (p % 4) * 64;
            int yy = (p / 4) * 64;
            burningImage.image(iconBurning, xx, yy);
        } else {
            burningImage.image(iconBurning, 0, 0);
        }
        percentage.text(progress + "%");
        energyBar.value(tileEntity.getCurrentPower());

        super.renderBg(matrixStack, partialTicks, x, y);
    }

}
