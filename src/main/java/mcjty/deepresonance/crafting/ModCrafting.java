package mcjty.deepresonance.crafting;

import cpw.mods.fml.common.registry.GameRegistry;
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
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public final class ModCrafting {
    public static void init() {
        GameRegistry.addRecipe(new ItemStack(ModItems.deepResonanceManualItem), " o ", "rbr", " r ", 'r', Items.redstone, 'b', Items.book, 'o', ModBlocks.resonatingOreBlock);
        GameRegistry.addRecipe(new ItemStack(ModItems.radiationMonitorItem), "qcq", "tot", "qrq", 'r', Items.redstone, 'q', Items.quartz, 'o', ModItems.resonatingPlateItem,
                'c', Items.clock, 't', Items.compass);
        GameRegistry.addRecipe(new ItemStack(ModBlocks.machineFrame), "ioi", "oso", "ioi", 'i', Items.iron_ingot, 's', Blocks.stone, 'o', ModItems.resonatingPlateItem);
        GameRegistry.addRecipe(new ItemStack(ModItems.filterMaterialItem, 8), "gcg", "csc", "gcg", 'g', Blocks.gravel, 'c', Items.coal, 's', Blocks.sand);
        GameRegistry.addRecipe(new ItemStack(ModItems.filterMaterialItem, 8), "gcg", "csc", "gcg", 'g', Blocks.gravel, 'c', new ItemStack(Items.coal, 1, 1), 's', Blocks.sand);
        GameRegistry.addRecipe(new ItemStack(ModBlocks.denseObsidianBlock, 4), "sos", "oso", "sos", 's', ModItems.spentFilterMaterialItem, 'o', Blocks.obsidian);
        GameRegistry.addRecipe(new ItemStack(ModBlocks.denseGlassBlock, 4), "sgs", "gog", "sgs", 's', ModItems.spentFilterMaterialItem, 'o', Blocks.obsidian, 'g', Blocks.glass);

        GameRegistry.addSmelting(ModBlocks.resonatingOreBlock, new ItemStack(ModItems.resonatingPlateItem, 8), 0.0f);

        GameRegistry.addRecipe(new ItemStack(ModItems.helmet), "ppp", "p p", "   ", 'p', ModItems.resonatingPlateItem);
        GameRegistry.addRecipe(new ItemStack(ModItems.chestplate), "ppp", "ppp", "p p", 'p', ModItems.resonatingPlateItem);
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
