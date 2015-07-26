package mcjty.deepresonance;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;
import mcjty.base.ModBase;
import mcjty.base.ModBaseRef;
import mcjty.gui.GuiStyle;
import mcjty.varia.Logging;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

@Mod(modid = DeepResonance.MODID, name="DeepResonance", dependencies = "required-after:Forge@["+DeepResonance.MIN_FORGE_VER+",);required-after:CoFHCore@["+DeepResonance.MIN_COFHCORE_VER+",)", version = DeepResonance.VERSION)
public class DeepResonance implements ModBase {
    public static final String MODID = "deepresonance";
    public static final String VERSION = "0.01alpha1";
    public static final String MIN_FORGE_VER = "10.13.2.1291";
    public static final String MIN_COFHCORE_VER = "1.7.10R3.0.0B9";

    @SidedProxy(clientSide="mcjty.deepresonance.ClientProxy", serverSide="mcjty.deepresonance.ServerProxy")
    public static CommonProxy proxy;

    @Mod.Instance("deepresonance")
    public static DeepResonance instance;

//    public static CreativeTabs tabDeepResonance = new CreativeTabs("DeepResonance") {
//        @Override
//        @SideOnly(Side.CLIENT)
//        public Item getTabIconItem() {
//            return ModItems.rfToolsManualItem;
//        }
//    };

    /**
     * Run before anything else. Read your config, create blocks, items, etc, and
     * register them with the GameRegistry.
     */
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        ModBaseRef.INSTANCE = this;
        ModBaseRef.MODID = MODID;

        this.proxy.preInit(e);
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
