package mcjty.deepresonance.modules.machines.client;

import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.modules.machines.block.PurifierTileEntity;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.gui.GenericGuiContainer;
import mcjty.lib.gui.ManualEntry;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.layout.PositionalLayout;
import mcjty.lib.gui.widgets.Panel;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

/**
 * Created by Elec332 on 27-7-2020
 */
public class PurifierGui extends GenericGuiContainer<PurifierTileEntity, GenericContainer> {

    public static final int PURIFIER_WIDTH = 180;
    public static final int PURIFIER_HEIGHT = 152;

    private static final ResourceLocation GUI = new ResourceLocation(DeepResonance.MODID, "textures/gui/purifier.png");

    public PurifierGui(PurifierTileEntity tileEntity, GenericContainer container, PlayerInventory inventory) {
        super(tileEntity, container, inventory, ManualEntry.EMPTY); // @todo 1.16 manual

        imageWidth = PURIFIER_WIDTH;
        imageHeight = PURIFIER_HEIGHT;
    }

    @Override
    public void init() {
        super.init();

        Panel toplevel = new Panel()
                .background(GUI)
                .layout(new PositionalLayout());
        toplevel.setBounds(new Rectangle(leftPos, topPos, imageWidth, imageHeight));

        window = new Window(this, toplevel);
    }

}
