package mcjty.deepresonance.modules.machines.client;

import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.modules.machines.MachinesModule;
import mcjty.deepresonance.modules.machines.block.PurifierTileEntity;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.gui.GenericGuiContainer;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.layout.PositionalLayout;
import mcjty.lib.gui.widgets.Panel;
import mcjty.rftoolsbase.tools.ManualHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.resources.Identifier;
import net.minecraft.client.gui.screens.MenuScreens;

import java.awt.*;

public class PurifierGui extends GenericGuiContainer<PurifierTileEntity, GenericContainer> {

    public static final int PURIFIER_WIDTH = 180;
    public static final int PURIFIER_HEIGHT = 152;

    private static final Identifier GUI = Identifier.fromNamespaceAndPath(DeepResonance.MODID, "textures/gui/purifier.png");

    public PurifierGui(GenericContainer container, Inventory inventory, Component title) {
        super(container, inventory, title, ManualHelper.create("deepresonance:machines/purifier"));
        setWindowDimensions(PURIFIER_WIDTH, PURIFIER_HEIGHT);

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

    public static void register() {
        MenuScreens.register(MachinesModule.PURIFIER_CONTAINER.get(), PurifierGui::new);
    }
}
