package mcjty.deepresonance.setup;


import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.blocks.ModBlocks;
import mcjty.deepresonance.crafting.ModCrafting;
import mcjty.deepresonance.items.ModItems;
import mcjty.deepresonance.items.rftoolsmodule.RFToolsSupport;
import mcjty.lib.McJtyRegister;
import mcjty.lib.blocks.DamageMetadataItemBlock;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.OreDictionary;

@Mod.EventBusSubscriber
public class Registration {

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        McJtyRegister.registerBlocks(DeepResonance.instance, event.getRegistry());
        event.getRegistry().register(ModBlocks.denseGlassBlock);
        event.getRegistry().register(ModBlocks.denseObsidianBlock);
        event.getRegistry().register(ModBlocks.debugBlock);
        event.getRegistry().register(ModBlocks.poisonedDirtBlock);
        event.getRegistry().register(ModBlocks.machineFrame);
        event.getRegistry().register(ModBlocks.resonatingPlateBlock);
        event.getRegistry().register(ModBlocks.resonatingOreBlock);
        if (DeepResonance.setup.rftools) {
            event.getRegistry().register(RFToolsSupport.getRadiationSensorBlock());
        }
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        McJtyRegister.registerItems(DeepResonance.instance, event.getRegistry());
        event.getRegistry().register(new ItemBlock(ModBlocks.denseGlassBlock).setRegistryName(ModBlocks.denseGlassBlock.getRegistryName()));
        event.getRegistry().register(new ItemBlock(ModBlocks.denseObsidianBlock).setRegistryName(ModBlocks.denseObsidianBlock.getRegistryName()));
        event.getRegistry().register(new ItemBlock(ModBlocks.debugBlock).setRegistryName(ModBlocks.debugBlock.getRegistryName()));
        event.getRegistry().register(new ItemBlock(ModBlocks.poisonedDirtBlock).setRegistryName(ModBlocks.poisonedDirtBlock.getRegistryName()));
        event.getRegistry().register(new ItemBlock(ModBlocks.machineFrame).setRegistryName(ModBlocks.machineFrame.getRegistryName()));
        event.getRegistry().register(new ItemBlock(ModBlocks.resonatingPlateBlock).setRegistryName(ModBlocks.resonatingPlateBlock.getRegistryName()));
        event.getRegistry().register(new DamageMetadataItemBlock(ModBlocks.resonatingOreBlock).setRegistryName(ModBlocks.resonatingOreBlock.getRegistryName()));

        if (DeepResonance.setup.rftools) {
            event.getRegistry().register(new ItemBlock(RFToolsSupport.getRadiationSensorBlock()).setRegistryName(RFToolsSupport.getRadiationSensorBlock().getRegistryName()));
        }
        event.getRegistry().register(ModItems.boots);
        event.getRegistry().register(ModItems.helmet);
        event.getRegistry().register(ModItems.chestplate);
        event.getRegistry().register(ModItems.leggings);
        ModCrafting.init();
        OreDictionary.registerOre("oreResonating", ModBlocks.resonatingOreBlock);
    }

    @SubscribeEvent
    public static void registerSounds(RegistryEvent.Register<SoundEvent> sounds) {
    }

}
