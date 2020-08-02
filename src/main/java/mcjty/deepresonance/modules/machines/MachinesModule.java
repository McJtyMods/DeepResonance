package mcjty.deepresonance.modules.machines;

import elec332.core.api.client.model.ModelLoadEvent;
import elec332.core.api.module.ElecModule;
import elec332.core.block.BlockSubTile;
import elec332.core.client.RenderHelper;
import elec332.core.tile.sub.SubTileRegistry;
import elec332.core.util.BlockProperties;
import elec332.core.util.RegistryHelper;
import elec332.core.world.WorldHelper;
import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.api.infusion.InfusionBonus;
import mcjty.deepresonance.api.laser.ILens;
import mcjty.deepresonance.api.laser.ILensMirror;
import mcjty.deepresonance.modules.machines.client.LensModelCache;
import mcjty.deepresonance.modules.machines.item.ItemLens;
import mcjty.deepresonance.modules.machines.tile.*;
import mcjty.deepresonance.modules.machines.util.InfusionBonusRegistry;
import mcjty.deepresonance.modules.machines.util.config.*;
import mcjty.deepresonance.util.DeepResonanceResourceLocation;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;

/**
 * Created by Elec332 on 25-7-2020
 */
@SuppressWarnings("unchecked")
@ElecModule(owner = DeepResonance.MODID, name = "Machines")
public class MachinesModule {

    public static final RegistryObject<Block> VALVE_BLOCK = DeepResonance.nonRotatingBlock("valve", TileEntityValve::new);
    public static final RegistryObject<Block> SMELTER_BLOCK = DeepResonance.defaultBlock("smelter", TileEntitySmelter::new, state -> state.with(BlockProperties.ACTIVE, false), BlockProperties.ACTIVE);
    public static final RegistryObject<Block> PURIFIER_BLOCK = DeepResonance.defaultBlock("purifier", TileEntityPurifier::new);
    public static final RegistryObject<BlockSubTile> LENS_BLOCK = DeepResonance.BLOCKS.register("lens", () -> new BlockSubTile(Block.Properties.create(Material.IRON).hardnessAndResistance(2.0F).sound(SoundType.METAL), SubTileLens.class, SubTileLensMirror.class));
    public static final RegistryObject<Block> LASER_BLOCK = DeepResonance.defaultBlock("laser", TileEntityLaser::new);
    public static final RegistryObject<Block> CRYSTALLIZER_BLOCK = DeepResonance.defaultBlock("crystallizer", TileEntityCrystallizer::new);

    public static final RegistryObject<Item> VALVE_ITEM = DeepResonance.fromBlock(VALVE_BLOCK);
    public static final RegistryObject<Item> SMELTER_ITEM = DeepResonance.fromBlock(SMELTER_BLOCK);
    public static final RegistryObject<Item> PURIFIER_ITEM = DeepResonance.fromBlock(PURIFIER_BLOCK);
    public static final RegistryObject<Item> LENS_ITEM = DeepResonance.ITEMS.register("lens", () -> new ItemLens(LENS_BLOCK.get(), DeepResonance.createStandardProperties()));
    public static final RegistryObject<Item> LASER_ITEM = DeepResonance.fromBlock(LASER_BLOCK);
    public static final RegistryObject<Item> CRYSTALLIZER_ITEM = DeepResonance.fromBlock(CRYSTALLIZER_BLOCK);

    public static final InfusionBonusRegistry INFUSION_BONUSES = new InfusionBonusRegistry();

    @CapabilityInject(ILens.class)
    public static Capability<ILens> LENS_CAPABILITY;

    @CapabilityInject(ILensMirror.class)
    public static Capability<ILensMirror> LENS_MIRROR_CAPABILITY;

    public static CrystallizerConfig crystallizerConfig;
    public static LaserConfig laserConfig;
    public static PurifierConfig purifierConfig;
    public static SmelterConfig smelterConfig;
    public static ValveConfig valveConfig;

    public MachinesModule() {
        DeepResonance.config.configureSubConfig("machines", "Machines module settings", config -> {
            crystallizerConfig = config.registerConfig(CrystallizerConfig::new, "crystallizer", "Crystallizer settings");
            laserConfig = config.registerConfig(LaserConfig::new, "laser", "Laser settings");
            purifierConfig = config.registerConfig(PurifierConfig::new, "purifier", "Purifier settings");
            smelterConfig = config.registerConfig(SmelterConfig::new, "smelter", "Smelter settings");
            valveConfig = config.registerConfig(ValveConfig::new, "valve", "Valve settings");
        });
        RegistryHelper.registerEmptyCapability(ILens.class);
        RegistryHelper.registerEmptyCapability(ILensMirror.class);
        SubTileRegistry.INSTANCE.registerSubTile(SubTileLens.class, new DeepResonanceResourceLocation("lens"));
        SubTileRegistry.INSTANCE.registerSubTile(SubTileLensMirror.class, new DeepResonanceResourceLocation("lens_mirror"));
    }

    @OnlyIn(Dist.CLIENT)
    @ElecModule.EventHandler
    public void loadModels(ModelLoadEvent event) {
        ModelResourceLocation location = new ModelResourceLocation(new DeepResonanceResourceLocation("lens"), "");
        event.registerModel(location, LensModelCache.INSTANCE.setModel(event.getModel(location)));
    }

    @OnlyIn(Dist.CLIENT)
    @ElecModule.EventHandler
    public void registerLaserColors(FMLLoadCompleteEvent event) {
        RenderHelper.getBlockColors().register((s, world, pos, index) -> {
            if (index == 1) {
                TileEntity tile = WorldHelper.getTileAt(world, pos);
                if (tile instanceof TileEntityLaser) {
                    InfusionBonus bonus = ((TileEntityLaser) tile).getActiveBonus();
                    if (!bonus.isEmpty()) {
                        return bonus.getColor();
                    }
                }
                return 0x484B52;
            }
            return -1;
        }, MachinesModule.LASER_BLOCK.get());
        RenderHelper.getItemColors().register((stack, index) -> index == 1 ? 0x484B52 : -1, MachinesModule.LASER_ITEM.get());
    }

}
