package mcjty.deepresonance.modules.radiation;

import elec332.core.api.module.ElecModule;
import elec332.core.util.RegistryHelper;
import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.api.radiation.IWorldRadiationManager;
import mcjty.deepresonance.modules.radiation.item.ItemRadiationSuit;
import mcjty.deepresonance.modules.radiation.manager.RadiationEventHandler;
import mcjty.deepresonance.modules.radiation.util.RadiationConfiguration;
import mcjty.deepresonance.util.DeepResonanceResourceLocation;
import net.minecraft.block.Block;
import net.minecraft.block.GlassBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import static mcjty.deepresonance.DeepResonance.MODID;

/**
 * Created by Elec332 on 12-7-2020
 */
@ElecModule(owner = MODID, name = "Radiation")
public class RadiationModule {

    public static final RegistryObject<Block> POISONED_DIRT_BLOCK = DeepResonance.BLOCKS.register("poisoned_dirt", () -> new Block(Block.Properties.create(Material.EARTH, MaterialColor.DIRT).hardnessAndResistance(0.5f).sound(SoundType.GROUND)));
    public static final RegistryObject<Block> DENSE_GLASS_BLOCK = DeepResonance.BLOCKS.register("dense_glass", () -> new GlassBlock(Block.Properties.create(Material.GLASS).hardnessAndResistance(3.0f, 500.0f).sound(SoundType.GLASS).notSolid().harvestTool(ToolType.PICKAXE).harvestLevel(2)));
    public static final RegistryObject<Block> DENSE_OBSIDIAN_BLOCK = DeepResonance.BLOCKS.register("dense_obsidian", () -> new Block(Block.Properties.create(Material.ROCK, MaterialColor.BLACK).hardnessAndResistance(50.0f, 2000.0f).sound(SoundType.STONE).harvestTool(ToolType.PICKAXE).harvestLevel(3)));

    public static final RegistryObject<Item> POISONED_DIRT_ITEM = DeepResonance.fromBlock(POISONED_DIRT_BLOCK);
    public static final RegistryObject<Item> DENSE_GLASS_ITEM = DeepResonance.fromBlock(DENSE_GLASS_BLOCK);
    public static final RegistryObject<Item> DENSE_OBSIDIAN_ITEM = DeepResonance.fromBlock(DENSE_OBSIDIAN_BLOCK);

    public static final RegistryObject<Item> RADIATION_SUIT_HELMET = DeepResonance.ITEMS.register("radiation_suit_helmet", () -> new ItemRadiationSuit(EquipmentSlotType.HEAD));
    public static final RegistryObject<Item> RADIATION_SUIT_CHESTPLATE = DeepResonance.ITEMS.register("radiation_suit_chestplate", () -> new ItemRadiationSuit(EquipmentSlotType.CHEST));
    public static final RegistryObject<Item> RADIATION_SUIT_LEGGINGS = DeepResonance.ITEMS.register("radiation_suit_leggings", () -> new ItemRadiationSuit(EquipmentSlotType.LEGS));
    public static final RegistryObject<Item> RADIATION_SUIT_BOOTS = DeepResonance.ITEMS.register("radiation_suit_boots", () -> new ItemRadiationSuit(EquipmentSlotType.FEET));

    @CapabilityInject(IWorldRadiationManager.class)
    public static Capability<IWorldRadiationManager> CAPABILITY;
    public static ResourceLocation CAPABILITY_NAME = new DeepResonanceResourceLocation("radiation");

    public RadiationModule() {
        DeepResonance.config.registerConfigurableElement(new RadiationConfiguration(), "radiation", "Radiation settings");
        RegistryHelper.registerEmptyCapability(IWorldRadiationManager.class);
    }

    @ElecModule.EventHandler
    public void setup(FMLCommonSetupEvent event) {
        MinecraftForge.EVENT_BUS.register(new RadiationEventHandler());
    }

}
