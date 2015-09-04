package mcjty.deepresonance.blocks.machine;

import mcjty.container.GenericGuiContainer;
import mcjty.deepresonance.DeepResonance;
import mcjty.gui.Window;
import mcjty.gui.layout.PositionalLayout;
import mcjty.gui.widgets.EnergyBar;
import mcjty.gui.widgets.Panel;
import mcjty.gui.widgets.Widget;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.ForgeDirection;

import java.awt.*;

public class GuiSmelter extends GenericGuiContainer<TileSmelter> {
    public static final int SMELTER_WIDTH = 180;
    public static final int SMELTER_HEIGHT = 152;

    private EnergyBar energyBar;
//    private ImageLabel arrow;

    private static final ResourceLocation iconLocation = new ResourceLocation(DeepResonance.MODID, "textures/gui/smelter.png");

    public GuiSmelter(TileSmelter machineInfuserTileEntity, SmelterContainer container) {
        super(DeepResonance.instance, DeepResonance.networkHandler.getNetworkWrapper(), machineInfuserTileEntity, container, 0, "smelter");
        machineInfuserTileEntity.setCurrentRF(machineInfuserTileEntity.getEnergyStored(ForgeDirection.DOWN));

        xSize = SMELTER_WIDTH;
        ySize = SMELTER_HEIGHT;
    }

    @Override
    public void initGui() {
        super.initGui();

        int maxEnergyStored = tileEntity.getMaxEnergyStored(ForgeDirection.DOWN);
        energyBar = new EnergyBar(mc, this).setVertical().setMaxValue(maxEnergyStored).setLayoutHint(new PositionalLayout.PositionalHint(10, 7, 8, 54)).setShowText(false);
        energyBar.setValue(tileEntity.getCurrentRF());

//        arrow = new ImageLabel(mc, this).setImage(iconGuiElements, 192, 0);
//        arrow.setLayoutHint(new PositionalLayout.PositionalHint(90, 26, 16, 16));

        Widget toplevel = new Panel(mc, this).setBackground(iconLocation).setLayout(new PositionalLayout()).addChild(energyBar); //.addChild(arrow);
        toplevel.setBounds(new Rectangle(guiLeft, guiTop, xSize, ySize));

        window = new Window(this, toplevel);
        tileEntity.requestRfFromServer(DeepResonance.networkHandler.getNetworkWrapper());
//        tileEntity.requestResearchingFromServer();
    }


    @Override
    protected void drawGuiContainerBackgroundLayer(float v, int i, int i2) {
//        int researching = tileEntity.getResearching();
//        if (researching > 0) {
//            arrow.setImage(iconGuiElements, 144, 0);
//        } else {
//            arrow.setImage(iconGuiElements, 192, 0);
//        }

        drawWindow();

        energyBar.setValue(tileEntity.getCurrentRF());

        tileEntity.requestRfFromServer(DeepResonance.networkHandler.getNetworkWrapper());
//        tileEntity.requestResearchingFromServer();
    }
}
