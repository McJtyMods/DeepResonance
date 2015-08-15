package mcjty.deepresonance.client.render;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import mcjty.deepresonance.blocks.ModBlocks;
import mcjty.deepresonance.blocks.collector.EnergyCollectorItemRenderer;
import mcjty.deepresonance.blocks.collector.EnergyCollectorSetup;
import mcjty.deepresonance.blocks.collector.EnergyCollectorTESR;
import mcjty.deepresonance.blocks.collector.EnergyCollectorTileEntity;
import mcjty.deepresonance.blocks.crystals.ResonatingCrystalItemRenderer;
import mcjty.deepresonance.blocks.crystals.ResonatingCrystalTESR;
import mcjty.deepresonance.blocks.crystals.ResonatingCrystalTileEntity;
import mcjty.deepresonance.client.render.duct.FluidDuctISBHR;
import net.minecraft.item.Item;
import net.minecraftforge.client.MinecraftForgeClient;

public final class ModRenderers {

    public static void init() {
        ClientRegistry.bindTileEntitySpecialRenderer(ResonatingCrystalTileEntity.class, new ResonatingCrystalTESR());
        MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(ModBlocks.resonatingCrystalBlock), new ResonatingCrystalItemRenderer());

        ClientRegistry.bindTileEntitySpecialRenderer(EnergyCollectorTileEntity.class, new EnergyCollectorTESR());
        MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(EnergyCollectorSetup.energyCollectorBlock), new EnergyCollectorItemRenderer());

        RenderingRegistry.registerBlockHandler(new FluidDuctISBHR());
    }


    public static final int ductRenderID;

    
    static {
        ductRenderID = RenderingRegistry.getNextAvailableRenderId();
    }
}
