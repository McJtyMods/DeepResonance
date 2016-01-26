package mcjty.deepresonance.blocks.smelter;

import mcjty.deepresonance.DeepResonance;
import mcjty.lib.container.GenericGuiContainer;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.layout.PositionalLayout;
import mcjty.lib.gui.widgets.*;
import mcjty.lib.gui.widgets.Label;
import mcjty.lib.gui.widgets.Panel;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.EnumFacing;

import java.awt.*;

public class GuiSmelter extends GenericGuiContainer<SmelterTileEntity> {
    public static final int SMELTER_WIDTH = 180;
    public static final int SMELTER_HEIGHT = 152;

    private EnergyBar energyBar;
    private ImageLabel burningImage;
    private Label percentage;

    private static final ResourceLocation iconLocation = new ResourceLocation(DeepResonance.MODID, "textures/gui/smelter.png");
    private static final ResourceLocation iconBurning = new ResourceLocation(DeepResonance.MODID, "textures/gui/burning.png");

    public GuiSmelter(SmelterTileEntity smelterTileEntity, SmelterContainer container) {
        super(DeepResonance.instance, DeepResonance.networkHandler.getNetworkWrapper(), smelterTileEntity, container, 0, "smelter");
        smelterTileEntity.setCurrentRF(smelterTileEntity.getEnergyStored(EnumFacing.DOWN));

        xSize = SMELTER_WIDTH;
        ySize = SMELTER_HEIGHT;
    }

    @Override
    public void initGui() {
        super.initGui();

        int maxEnergyStored = tileEntity.getMaxEnergyStored(EnumFacing.DOWN);
        energyBar = new EnergyBar(mc, this).setVertical().setMaxValue(maxEnergyStored).setLayoutHint(new PositionalLayout.PositionalHint(10, 7, 8, 54)).setShowText(false);
        energyBar.setValue(tileEntity.getCurrentRF());

        burningImage = new ImageLabel(mc, this).setImage(iconBurning, 0, 0);
        burningImage.setLayoutHint(new PositionalLayout.PositionalHint(90, 2, 64, 64));

        percentage = new Label(mc, this);
        percentage.setLayoutHint(new PositionalLayout.PositionalHint(54, 44, 32, 14));

        Widget toplevel = new Panel(mc, this).setBackground(iconLocation).setLayout(new PositionalLayout()).addChild(energyBar).addChild(burningImage).addChild(percentage);
        toplevel.setBounds(new Rectangle(guiLeft, guiTop, xSize, ySize));

        window = new Window(this, toplevel);
        tileEntity.requestRfFromServer(DeepResonance.MODID);
        tileEntity.requestProgressFromServer();
    }


    @Override
    protected void drawGuiContainerBackgroundLayer(float v, int i, int i2) {
        int progress = tileEntity.getProgressPercentage();
        if (0 < progress && progress < 100) {
            int p = ((progress/3) % 9) + 1;
            int x = (p % 4) * 64;
            int y = (p / 4) * 64;
            burningImage.setImage(iconBurning, x, y);
        } else {
            burningImage.setImage(iconBurning, 0, 0);
        }
        percentage.setText(progress + "%");

        drawWindow();

        energyBar.setValue(tileEntity.getCurrentRF());

        tileEntity.requestRfFromServer(DeepResonance.MODID);
        tileEntity.requestProgressFromServer();
    }
}
