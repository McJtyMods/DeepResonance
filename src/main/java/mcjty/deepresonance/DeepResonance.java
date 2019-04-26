package mcjty.deepresonance;

import mcjty.deepresonance.commands.CommandDRGen;
import mcjty.deepresonance.compat.CompatHandler;
import mcjty.deepresonance.items.manual.GuiDeepResonanceManual;
import mcjty.deepresonance.setup.ModSetup;
import mcjty.lib.base.ModBase;
import mcjty.lib.proxy.IProxy;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

@Mod(modid = DeepResonance.MODID, name="DeepResonance",
        dependencies =
                        "required-after:mcjtylib_ng@[" + DeepResonance.MIN_MCJTYLIB_VER + ",);" +
                        "required-after:eleccore@[" + DeepResonance.MIN_ELECCORE_VER + ",);" +
                        "after:forge@[" + DeepResonance.MIN_FORGE11_VER + ",);" +
                        "after:opencomputers@[" + DeepResonance.MIN_OPENCOMPUTERS_VER + ",);" +
                        "after:rftools@[" + DeepResonance.MIN_RFTOOLS_VER + ",)",
        acceptedMinecraftVersions = "[1.12,1.13)",
        version = DeepResonance.VERSION)
public class DeepResonance implements ModBase {
    public static final String MODID = "deepresonance";
    public static final String VERSION = "1.8.0";
    public static final String MIN_ELECCORE_VER = "1.9.452";
    public static final String MIN_OPENCOMPUTERS_VER = "1.6.0";
    public static final String MIN_FORGE11_VER = "13.19.0.2176";
    public static final String MIN_MCJTYLIB_VER = "3.5.0";
    public static final String MIN_RFTOOLS_VER = "7.70";

    @SidedProxy(clientSide="mcjty.deepresonance.setup.ClientProxy", serverSide="mcjty.deepresonance.setup.ServerProxy")
    public static IProxy proxy;
    public static ModSetup setup = new ModSetup();

    @Mod.Instance("deepresonance")
    public static DeepResonance instance;

    public static CompatHandler compatHandler;

    public DeepResonance() {
        // This has to be done VERY early
        FluidRegistry.enableUniversalBucket();
    }

    /**
     * Run before anything else. Read your config, create blocks, items, etc, and
     * register them with the GameRegistry.
     */
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        setup.preInit(e);
        proxy.preInit(e);
    }


    /**
     * Do your mod setup. Build whatever data structures you care about. Register recipes.
     */
    @Mod.EventHandler
    public void init(FMLInitializationEvent e) {
        setup.init(e);
        proxy.init(e);
    }

    /**
     * Handle interaction with other mods, complete your setup based on this.
     */
    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent e) {
        setup.postInit(e);
        proxy.postInit(e);
    }

    @Mod.EventHandler
    public void serverLoad(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandDRGen());
    }

    @Override
    public String getModId() {
        return MODID;
    }

    @Override
    public void openManual(EntityPlayer player, int bookIndex, String page) {
        GuiDeepResonanceManual.locatePage = page;
        player.openGui(DeepResonance.instance, bookIndex, player.getEntityWorld(), (int) player.posX, (int) player.posY, (int) player.posZ);
    }

}
