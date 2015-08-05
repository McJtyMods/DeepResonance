package mcjty.deepresonance;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mcjty.base.ModBase;
import mcjty.deepresonance.blocks.ModBlocks;
import mcjty.gui.GuiStyle;
import mcjty.varia.Logging;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;

@Mod(modid = DeepResonance.MODID, name="DeepResonance", dependencies =
        "required-after:Forge@["+DeepResonance.MIN_FORGE_VER+
        ",);required-after:CoFHCore@["+DeepResonance.MIN_COFHCORE_VER+
        ",);required-after:McJtyLib@["+DeepResonance.MIN_MCJTYLIB_VER+",)",
        version = DeepResonance.VERSION)
public class DeepResonance implements ModBase {
    public static final String MODID = "deepresonance";
    public static final String VERSION = "0.1.1";
    public static final String MIN_FORGE_VER = "10.13.2.1291";
    public static final String MIN_COFHCORE_VER = "1.7.10R3.0.0B9";
    public static final String MIN_MCJTYLIB_VER = "1.2.0";

    @SidedProxy(clientSide="mcjty.deepresonance.ClientProxy", serverSide="mcjty.deepresonance.ServerProxy")
    public static CommonProxy proxy;

    @Mod.Instance("deepresonance")
    public static DeepResonance instance;

    public static final String SHIFT_MESSAGE = "<Press Shift>";

    /** This is used to keep track of GUIs that we make*/
    private static int modGuiIndex = 0;

    public static final int GUI_MANUAL_MAIN = modGuiIndex++;

    public static CreativeTabs tabDeepResonance = new CreativeTabs("DeepResonance") {
        @Override
        @SideOnly(Side.CLIENT)
        public Item getTabIconItem() {
            return Item.getItemFromBlock(ModBlocks.resonatingCrystalBlock);
        }
    };

    /**
     * Run before anything else. Read your config, create blocks, items, etc, and
     * register them with the GameRegistry.
     */
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        this.proxy.preInit(e);

        FMLInterModComms.sendMessage("Waila", "register", "mcjty.wailasupport.WailaCompatibility.load");
        FMLInterModComms.sendMessage("rftools", "dimlet_configure", "Material.tile.oreResonating=30000,6000,400,5");
//        modConfigDir = e.getModConfigurationDirectory();
//        mainConfig = new Configuration(new File(modConfigDir.getPath() + File.separator + "rftools", "main.cfg"));
//
//        readMainConfig();
//
//        PacketHandler.registerMessages("rftools");
//        RFToolsMessages.registerNetworkMessages();
//
    }



    /**
     * Do your mod setup. Build whatever data structures you care about. Register recipes.
     */
    @Mod.EventHandler
    public void init(FMLInitializationEvent e) {
        this.proxy.init(e);
    }

    @Mod.EventHandler
    public void serverStopped(FMLServerStoppedEvent event) {
        Logging.log("Deep Resonance: server is stopping. Shutting down gracefully");
    }

    /**
     * Handle interaction with other mods, complete your setup based on this.
     */
    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent e) {
        this.proxy.postInit(e);

        if (Loader.isModLoaded("ComputerCraft")) {
            Logging.log("Deep Resonance Detected ComputerCraft: enabling support");
//            ComputerCraftHelper.register();
        }
    }

    @Override
    public String getModId() {
        return MODID;
    }

    @Override
    public void setGuiStyle(EntityPlayerMP entityPlayerMP, GuiStyle guiStyle) {

    }

    @Override
    public GuiStyle getGuiStyle(EntityPlayer entityPlayer) {
        return null;
    }

    @Override
    public void openManual(EntityPlayer entityPlayer, int i, String s) {

    }
}
