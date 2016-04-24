package mcjty.deepresonance.crafting;

import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.blocks.ModBlocks;
import mcjty.deepresonance.blocks.collector.EnergyCollectorSetup;
import mcjty.deepresonance.blocks.crystalizer.CrystalizerSetup;
import mcjty.deepresonance.blocks.gencontroller.GeneratorControllerSetup;
import mcjty.deepresonance.blocks.generator.GeneratorSetup;
import mcjty.deepresonance.blocks.laser.LaserSetup;
import mcjty.deepresonance.blocks.lens.LensSetup;
import mcjty.deepresonance.blocks.pedestal.PedestalSetup;
import mcjty.deepresonance.blocks.purifier.PurifierSetup;
import mcjty.deepresonance.blocks.smelter.SmelterSetup;
import mcjty.deepresonance.blocks.tank.TankSetup;
import mcjty.deepresonance.blocks.valve.ValveSetup;
import mcjty.deepresonance.items.ModItems;
import mcjty.deepresonance.items.rftoolsmodule.RFToolsSupport;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

public final class ModCrafting {
    public static void init() {
        GameRegistry.addRecipe(new ItemStack(ModItems.deepResonanceManualItem), " o ", "rbr", " r ", 'r', Items.REDSTONE, 'b', Items.BOOK, 'o', ModBlocks.resonatingOreBlock);
        GameRegistry.addRecipe(new ItemStack(ModItems.radiationMonitorItem), "qcq", "tot", "qrq", 'r', Items.REDSTONE, 'q', Items.QUARTZ, 'o', ModItems.resonatingPlateItem,
                'c', Items.CLOCK, 't', Items.COMPASS);
        GameRegistry.addRecipe(new ItemStack(ModBlocks.machineFrame), "ioi", "oso", "ioi", 'i', Items.IRON_INGOT, 's', Blocks.STONE, 'o', ModItems.resonatingPlateItem);
        GameRegistry.addRecipe(new ItemStack(ModItems.filterMaterialItem, 8), "gcg", "csc", "gcg", 'g', Blocks.GRAVEL, 'c', Items.COAL, 's', Blocks.SAND);
        GameRegistry.addRecipe(new ItemStack(ModItems.filterMaterialItem, 8), "gcg", "csc", "gcg", 'g', Blocks.GRAVEL, 'c', new ItemStack(Items.COAL, 1, 1), 's', Blocks.SAND);
        GameRegistry.addRecipe(new ItemStack(ModBlocks.denseObsidianBlock, 4), "sos", "oso", "sos", 's', ModItems.spentFilterMaterialItem, 'o', Blocks.OBSIDIAN);
        GameRegistry.addRecipe(new ItemStack(ModBlocks.denseGlassBlock, 4), "sgs", "gog", "sgs", 's', ModItems.spentFilterMaterialItem, 'o', Blocks.OBSIDIAN, 'g', Blocks.GLASS);
        GameRegistry.addRecipe(new ItemStack(ModBlocks.resonatingPlateBlock), "ppp", "ppp", "ppp", 'p', ModItems.resonatingPlateItem);
        GameRegistry.addRecipe(new ItemStack(ModItems.resonatingPlateItem, 9), "p", 'p', ModBlocks.resonatingPlateBlock);

        GameRegistry.addSmelting(ModBlocks.resonatingOreBlock, new ItemStack(ModItems.resonatingPlateItem, 8), 0.0f);

        if (DeepResonance.instance.rftools) {
            RFToolsSupport.initCrafting();
        }

        GameRegistry.addRecipe(new ItemStack(ModItems.helmet), "ppp", "p p", "   ", 'p', ModItems.resonatingPlateItem);
        GameRegistry.addRecipe(new ItemStack(ModItems.chestplate), "p p", "ppp", "ppp", 'p', ModItems.resonatingPlateItem);
        GameRegistry.addRecipe(new ItemStack(ModItems.boots), "   ", "p p", "p p", 'p', ModItems.resonatingPlateItem);
        GameRegistry.addRecipe(new ItemStack(ModItems.leggings), "ppp", "p p", "p p", 'p', ModItems.resonatingPlateItem);

        GeneratorSetup.setupCrafting();
        GeneratorControllerSetup.setupCrafting();
        EnergyCollectorSetup.setupCrafting();
        CrystalizerSetup.setupCrafting();
        SmelterSetup.setupCrafting();
        TankSetup.setupCrafting();
        PurifierSetup.setupCrafting();
        PedestalSetup.setupCrafting();
        ValveSetup.setupCrafting();
        LensSetup.setupCrafting();
        LaserSetup.setupCrafting();
    }
}
