package mcjty.deepresonance.client.render;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import mcjty.deepresonance.blocks.ModBlocks;
import mcjty.deepresonance.blocks.collector.EnergyCollectorItemRenderer;
import mcjty.deepresonance.blocks.collector.EnergyCollectorSetup;
import mcjty.deepresonance.blocks.collector.EnergyCollectorTESR;
import mcjty.deepresonance.blocks.collector.EnergyCollectorTileEntity;
import mcjty.deepresonance.blocks.crystalizer.CrystalizerTESR;
import mcjty.deepresonance.blocks.crystalizer.CrystalizerTileEntity;
import mcjty.deepresonance.blocks.crystals.ResonatingCrystalItemRenderer;
import mcjty.deepresonance.blocks.crystals.ResonatingCrystalTESR;
import mcjty.deepresonance.blocks.crystals.ResonatingCrystalTileEntity;
import mcjty.deepresonance.blocks.laser.LaserRenderer;
import mcjty.deepresonance.blocks.laser.LaserTileEntity;
import mcjty.deepresonance.blocks.lens.LensItemRenderer;
import mcjty.deepresonance.blocks.lens.LensSetup;
import mcjty.deepresonance.blocks.lens.LensTESR;
import mcjty.deepresonance.blocks.lens.LensTileEntity;
import mcjty.deepresonance.blocks.tank.TankTESR;
import mcjty.deepresonance.blocks.tank.TileTank;
import mcjty.deepresonance.client.render.duct.FluidDuctISBHR;
import net.minecraft.item.Item;
import net.minecraftforge.client.MinecraftForgeClient;

public final class ModRenderers {

    public static void init() {
        ClientRegistry.bindTileEntitySpecialRenderer(ResonatingCrystalTileEntity.class, new ResonatingCrystalTESR());
        MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(ModBlocks.resonatingCrystalBlock), new ResonatingCrystalItemRenderer());

        ClientRegistry.bindTileEntitySpecialRenderer(TileTank.class, new TankTESR());

        ClientRegistry.bindTileEntitySpecialRenderer(CrystalizerTileEntity.class, new CrystalizerTESR());

        ClientRegistry.bindTileEntitySpecialRenderer(LaserTileEntity.class, new LaserRenderer());

        ClientRegistry.bindTileEntitySpecialRenderer(EnergyCollectorTileEntity.class, new EnergyCollectorTESR());
        MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(EnergyCollectorSetup.energyCollectorBlock), new EnergyCollectorItemRenderer());

        ClientRegistry.bindTileEntitySpecialRenderer(LensTileEntity.class, new LensTESR());
        MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(LensSetup.lensBlock), new LensItemRenderer());

        RenderingRegistry.registerBlockHandler(new FluidDuctISBHR());
    }


    public static final int ductRenderID;


    static {
        ductRenderID = RenderingRegistry.getNextAvailableRenderId();
    }
}
