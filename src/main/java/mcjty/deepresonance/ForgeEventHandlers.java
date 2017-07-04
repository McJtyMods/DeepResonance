package mcjty.deepresonance;

import mcjty.deepresonance.blocks.ModBlocks;
import mcjty.deepresonance.items.ModItems;
import mcjty.deepresonance.items.rftoolsmodule.RFToolsSupport;
import mcjty.deepresonance.radiation.DRRadiationManager;
import mcjty.deepresonance.radiation.RadiationShieldRegistry;
import mcjty.deepresonance.varia.QuadTree;
import mcjty.lib.McJtyRegister;
import mcjty.lib.varia.GlobalCoordinate;
import mcjty.lib.varia.Logging;
import mcjty.rftools.RFTools;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.OreDictionary;

import java.util.Map;

public class ForgeEventHandlers {

    @SubscribeEvent
    public void registerBlocks(RegistryEvent.Register<Block> event) {
        McJtyRegister.registerBlocks(RFTools.instance, event.getRegistry());
        event.getRegistry().register(ModBlocks.denseGlassBlock);
        event.getRegistry().register(ModBlocks.denseObsidianBlock);
        event.getRegistry().register(ModBlocks.debugBlock);
        event.getRegistry().register(ModBlocks.poisonedDirtBlock);
        event.getRegistry().register(ModBlocks.machineFrame);
        event.getRegistry().register(ModBlocks.resonatingPlateBlock);
        event.getRegistry().register(ModBlocks.resonatingOreBlock);
        OreDictionary.registerOre("oreResonating", ModBlocks.resonatingOreBlock);
        if (DeepResonance.instance.rftools) {
            event.getRegistry().register(RFToolsSupport.getRadiationSensorBlock());
        }
    }

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event) {
        McJtyRegister.registerItems(RFTools.instance, event.getRegistry());
        event.getRegistry().register(new ItemBlock(ModBlocks.denseGlassBlock).setRegistryName(ModBlocks.denseGlassBlock.getRegistryName()));
        event.getRegistry().register(new ItemBlock(ModBlocks.denseObsidianBlock).setRegistryName(ModBlocks.denseObsidianBlock.getRegistryName()));
        event.getRegistry().register(new ItemBlock(ModBlocks.debugBlock).setRegistryName(ModBlocks.debugBlock.getRegistryName()));
        event.getRegistry().register(new ItemBlock(ModBlocks.poisonedDirtBlock).setRegistryName(ModBlocks.poisonedDirtBlock.getRegistryName()));
        event.getRegistry().register(new ItemBlock(ModBlocks.machineFrame).setRegistryName(ModBlocks.machineFrame.getRegistryName()));
        event.getRegistry().register(new ItemBlock(ModBlocks.resonatingPlateBlock).setRegistryName(ModBlocks.resonatingPlateBlock.getRegistryName()));

        ItemBlock itemBlock = new ItemBlock(ModBlocks.resonatingOreBlock) {
            @Override
            public int getMetadata(int damage) {
                return damage;
            }
        };
        itemBlock.setHasSubtypes(true);
        event.getRegistry().register(itemBlock.setRegistryName(ModBlocks.resonatingOreBlock.getRegistryName()));

        if (DeepResonance.instance.rftools) {
            event.getRegistry().register(new ItemBlock(RFToolsSupport.getRadiationSensorBlock()).setRegistryName(RFToolsSupport.getRadiationSensorBlock().getRegistryName()));
        }
        event.getRegistry().register(ModItems.boots);
        event.getRegistry().register(ModItems.helmet);
        event.getRegistry().register(ModItems.chestplate);
        event.getRegistry().register(ModItems.leggings);
    }

    @SubscribeEvent
    public void registerSounds(RegistryEvent.Register<SoundEvent> sounds) {
    }


    @SubscribeEvent
    public void onBlockBreakEvent(BlockEvent.BreakEvent event) {
        float blocker = RadiationShieldRegistry.getBlocker(event.getState());
        if (blocker >= 0.99f) {
            return;
        }

        World world = event.getWorld();
        DRRadiationManager radiationManager = DRRadiationManager.getManager(world);
        Map<GlobalCoordinate, DRRadiationManager.RadiationSource> radiationSources = radiationManager.getRadiationSources();
        if (radiationSources.isEmpty()) {
            return;
        }

        int x = event.getPos().getX();
        int y = event.getPos().getY();
        int z = event.getPos().getZ();

        for (Map.Entry<GlobalCoordinate, DRRadiationManager.RadiationSource> entry : radiationSources.entrySet()) {
            DRRadiationManager.RadiationSource source = entry.getValue();
            float radius = source.getRadius();
            GlobalCoordinate gc = entry.getKey();
            BlockPos c = gc.getCoordinate();
            if (Math.abs(c.getX()-x) < radius && Math.abs(c.getY()-y) < radius && Math.abs(c.getZ()-z) < radius) {
                Logging.logDebug("Removed blocker at: " + x + "," + y + "," + z);
                QuadTree radiationTree = source.getRadiationTree(world, c.getX(), c.getY(), c.getZ());
                radiationTree.addBlocker(x, y, z, 1.0f);
            }
        }
    }

    @SubscribeEvent
    public void onBlockPlaceEvent(BlockEvent.PlaceEvent event) {
        float blocker = RadiationShieldRegistry.getBlocker(event.getState());
        if (blocker >= 0.99f) {
            return;
        }

        World world = event.getBlockSnapshot().getWorld();
        DRRadiationManager radiationManager = DRRadiationManager.getManager(world);
        Map<GlobalCoordinate, DRRadiationManager.RadiationSource> radiationSources = radiationManager.getRadiationSources();
        if (radiationSources.isEmpty()) {
            return;
        }

        int x = event.getBlockSnapshot().getPos().getX();
        int y = event.getBlockSnapshot().getPos().getY();
        int z = event.getBlockSnapshot().getPos().getZ();
        for (Map.Entry<GlobalCoordinate, DRRadiationManager.RadiationSource> entry : radiationSources.entrySet()) {
            DRRadiationManager.RadiationSource source = entry.getValue();
            float radius = source.getRadius();
            GlobalCoordinate gc = entry.getKey();
            BlockPos c = gc.getCoordinate();
            if (Math.abs(c.getX()-x) < radius && Math.abs(c.getY()-y) < radius && Math.abs(c.getZ()-z) < radius) {
                Logging.logDebug("Add blocker at: " + x + "," + y + "," + z);
                QuadTree radiationTree = source.getRadiationTree(world, c.getX(), c.getY(), c.getZ());
                radiationTree.addBlocker(x, y, z, blocker);
            }
        }

    }
}
