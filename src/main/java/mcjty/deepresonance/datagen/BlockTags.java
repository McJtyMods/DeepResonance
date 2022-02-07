package mcjty.deepresonance.datagen;

import mcjty.deepresonance.modules.core.CoreModule;
import mcjty.deepresonance.modules.generator.GeneratorModule;
import mcjty.deepresonance.modules.machines.MachinesModule;
import mcjty.deepresonance.modules.radiation.RadiationModule;
import mcjty.deepresonance.modules.tank.TankModule;
import mcjty.deepresonance.modules.worldgen.WorldGenModule;
import mcjty.deepresonance.util.DeepResonanceTags;
import mcjty.lib.datagen.BaseBlockTagsProvider;
import mcjty.rftoolsbase.RFToolsBase;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nonnull;

public class BlockTags extends BaseBlockTagsProvider {

    public BlockTags(DataGenerator generator, ExistingFileHelper helper) {
        super(generator, RFToolsBase.MODID, helper);
    }

    @Override
    protected void addTags() {
        tag(DeepResonanceTags.RESONANT_ORE)
                .add(CoreModule.RESONATING_ORE_DEEPSLATE_BLOCK.get(), CoreModule.RESONATING_ORE_STONE_BLOCK.get(), CoreModule.RESONATING_ORE_NETHER_BLOCK.get(), CoreModule.RESONATING_ORE_END_BLOCK.get());
        ironPickaxe(
                CoreModule.RESONATING_PLATE_BLOCK_BLOCK,
                GeneratorModule.ENERGY_COLLECTOR_BLOCK, GeneratorModule.GENERATOR_CONTROLLER_BLOCK, GeneratorModule.GENERATOR_PART_BLOCK,
                MachinesModule.CRYSTALLIZER_BLOCK, MachinesModule.LASER_BLOCK, MachinesModule.LENS_BLOCK, MachinesModule.PURIFIER_BLOCK,
                MachinesModule.SMELTER_BLOCK, MachinesModule.VALVE_BLOCK,
                RadiationModule.DENSE_GLASS_BLOCK,
                TankModule.TANK_BLOCK
        );
        diamondPickaxe(
                CoreModule.RESONATING_ORE_DEEPSLATE_BLOCK, CoreModule.RESONATING_ORE_END_BLOCK, CoreModule.RESONATING_ORE_NETHER_BLOCK, CoreModule.RESONATING_ORE_STONE_BLOCK,
                CoreModule.RESONATING_CRYSTAL_GENERATED, CoreModule.RESONATING_CRYSTAL_GENERATED_EMPTY, CoreModule.RESONATING_CRYSTAL_NATURAL, CoreModule.RESONATING_CRYSTAL_NATURAL_EMPTY,
                RadiationModule.DENSE_OBSIDIAN_BLOCK
        );
        tag(net.minecraft.tags.BlockTags.MINEABLE_WITH_SHOVEL).add(RadiationModule.POISONED_DIRT_BLOCK.get());
        tag(net.minecraft.tags.BlockTags.NEEDS_IRON_TOOL).add(RadiationModule.POISONED_DIRT_BLOCK.get());
    }

    @Override
    @Nonnull
    public String getName() {
        return "DeepResonance Tags";
    }
}
