package mcjty.deepresonance.modules.radiation;

import elec332.core.util.RegistryHelper;
import mcjty.deepresonance.api.radiation.IWorldRadiationManager;
import mcjty.deepresonance.modules.radiation.item.ItemRadiationSuit;
import mcjty.deepresonance.modules.radiation.manager.RadiationEventHandler;
import mcjty.deepresonance.modules.radiation.util.RadiationConfiguration;
import mcjty.deepresonance.setup.Config;
import mcjty.deepresonance.setup.Registration;
import mcjty.lib.modules.IModule;
import net.minecraft.block.Block;
import net.minecraft.block.GlassBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

/**
 * Created by Elec332 on 12-7-2020
 */
public class RadiationModule implements IModule {

    public static final RegistryObject<Block> POISONED_DIRT_BLOCK = Registration.BLOCKS.register("poisoned_dirt", () -> new Block(Block.Properties.create(Material.EARTH, MaterialColor.DIRT).hardnessAndResistance(0.5f).sound(SoundType.GROUND)));
    public static final RegistryObject<Block> DENSE_GLASS_BLOCK = Registration.BLOCKS.register("dense_glass", () -> new GlassBlock(Block.Properties.create(Material.GLASS).hardnessAndResistance(3.0f, 500.0f).sound(SoundType.GLASS).notSolid().harvestTool(ToolType.PICKAXE).harvestLevel(2)));
    public static final RegistryObject<Block> DENSE_OBSIDIAN_BLOCK = Registration.BLOCKS.register("dense_obsidian", () -> new Block(Block.Properties.create(Material.ROCK, MaterialColor.BLACK).hardnessAndResistance(50.0f, 2000.0f).sound(SoundType.STONE).harvestTool(ToolType.PICKAXE).harvestLevel(3)));

    public static final RegistryObject<Item> POISONED_DIRT_ITEM = Registration.fromBlock(POISONED_DIRT_BLOCK);
    public static final RegistryObject<Item> DENSE_GLASS_ITEM = Registration.fromBlock(DENSE_GLASS_BLOCK);
    public static final RegistryObject<Item> DENSE_OBSIDIAN_ITEM = Registration.fromBlock(DENSE_OBSIDIAN_BLOCK);

    public static final RegistryObject<Item> RADIATION_SUIT_HELMET = Registration.ITEMS.register("radiation_suit_helmet", () -> new ItemRadiationSuit(EquipmentSlotType.HEAD));
    public static final RegistryObject<Item> RADIATION_SUIT_CHESTPLATE = Registration.ITEMS.register("radiation_suit_chestplate", () -> new ItemRadiationSuit(EquipmentSlotType.CHEST));
    public static final RegistryObject<Item> RADIATION_SUIT_LEGGINGS = Registration.ITEMS.register("radiation_suit_leggings", () -> new ItemRadiationSuit(EquipmentSlotType.LEGS));
    public static final RegistryObject<Item> RADIATION_SUIT_BOOTS = Registration.ITEMS.register("radiation_suit_boots", () -> new ItemRadiationSuit(EquipmentSlotType.FEET));

    @CapabilityInject(IWorldRadiationManager.class)
    public static Capability<IWorldRadiationManager> CAPABILITY;
    public static ResourceLocation CAPABILITY_NAME = new DeepResonanceResourceLocation("radiation");

    public static RadiationConfiguration config;

    public RadiationModule() {
        RegistryHelper.registerEmptyCapability(IWorldRadiationManager.class);
    }

    @Override
    public void init(FMLCommonSetupEvent event) {
        RadiationEventHandler.register();
    }

    @Override
    public void initClient(FMLClientSetupEvent event) {

    }

    @Override
    public void initConfig() {
        config = Config.configuration.registerConfig(RadiationConfiguration::new, "radiation", "Radiation settings");
    }
}
