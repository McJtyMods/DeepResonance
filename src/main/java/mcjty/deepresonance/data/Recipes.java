package mcjty.deepresonance.data;

import mcjty.lib.datagen.BaseRecipeProvider;
import mcjty.rftoolsbase.modules.various.VariousModule;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;

import java.util.function.Consumer;

public class Recipes extends BaseRecipeProvider {

    public Recipes(DataGenerator generatorIn) {
        super(generatorIn);
        add('F', VariousModule.MACHINE_FRAME.get());
        add('s', VariousModule.DIMENSIONALSHARD.get());
    }

    @Override
    protected void buildShapelessRecipes(Consumer<IFinishedRecipe> consumer) {
        // @todo 1.16
//        recipeBuilder.key('c', ItemTags.COALS);
//        recipeBuilder.key('s', ItemTags.SAND);
//        recipeBuilder.key('g', Tags.Items.GLASS);
//        recipeBuilder.key('i', Tags.Items.INGOTS_IRON);
//        recipeBuilder.key('p', CoreModule.RESONATING_PLATE_ITEM.get());
//        recipeBuilder.key('o', Tags.Items.OBSIDIAN);
//        recipeBuilder.grouped("core", recipes -> {
//            recipes.shapedRecipe(CoreModule.FILTER_MATERIAL_ITEM.get(), 8).key('r', Tags.Items.GRAVEL)
//                    .addCriterion("has_gravel", hasItem(Tags.Items.GRAVEL)).addCriterion("has_coal", hasItem(ItemTags.COALS)).addCriterion("has_sand", hasItem(ItemTags.SAND))
//                    .patternLine("rcr").patternLine("csc").patternLine("rcr").build();
//            recipes.shapedRecipe(CoreModule.MACHINE_FRAME_ITEM.get()).key('q', Tags.Items.STONE)
//                    .addCriterion("has_iron", hasItem(Tags.Items.INGOTS_IRON))
//                    .patternLine("ipi").patternLine("pqp").patternLine("ipi").build();
//
//            recipes.addCriterion("has_resonant_plate", hasItem(CoreModule.RESONATING_PLATE_ITEM.get()));
//            recipes.shapedRecipe(CoreModule.RESONATING_PLATE_BLOCK_ITEM.get())
//                    .patternLine("ppp").patternLine("ppp").patternLine("ppp").build();
//            recipes.shapelessRecipe(CoreModule.RESONATING_PLATE_ITEM.get(), 9)
//                    .addIngredient(CoreModule.RESONATING_PLATE_BLOCK_ITEM.get()).build();
//        });
//        recipeBuilder.grouped("tank", recipes -> recipes.shapedRecipe(TankModule.TANK_ITEM.get())
//                .addCriterion("has_resonant_plate", hasItem(CoreModule.RESONATING_PLATE_ITEM.get()))
//                .patternLine("ipi").patternLine("ggg").patternLine("ioi").build());
//        recipeBuilder.grouped("radiation", recipes -> {
//            recipes.key('f', CoreModule.SPENT_FILTER_ITEM.get());
//            recipes.grouped(armor -> {
//                armor.addCriterion("has_resonant_plate", hasItem(CoreModule.RESONATING_PLATE_ITEM.get()));
//                armor.shapedRecipe(RadiationModule.RADIATION_SUIT_HELMET.get())
//                        .patternLine("ppp").patternLine("p p").build();
//                armor.shapedRecipe(RadiationModule.RADIATION_SUIT_CHESTPLATE.get())
//                        .patternLine("p p").patternLine("ppp").patternLine("ppp").build();
//                armor.shapedRecipe(RadiationModule.RADIATION_SUIT_LEGGINGS.get())
//                        .patternLine("ppp").patternLine("p p").patternLine("p p").build();
//                armor.shapedRecipe(RadiationModule.RADIATION_SUIT_BOOTS.get())
//                        .patternLine("p p").patternLine("p p").build();
//            });
//
//            recipes.addCriterion("has_spent_filter", hasItem(CoreModule.SPENT_FILTER_ITEM.get()));
//            recipes.shapedRecipe(RadiationModule.DENSE_GLASS_ITEM.get(), 4)
//                    .patternLine("fgf").patternLine("gog").patternLine("fgf").build();
//            recipes.shapedRecipe(RadiationModule.DENSE_OBSIDIAN_ITEM.get(), 4)
//                    .patternLine("ofo").patternLine("fof").patternLine("ofo").build();
//        });
//        recipeBuilder.grouped("machines", recipes -> {
//            recipes.key('m', CoreModule.MACHINE_FRAME_ITEM.get());
//            recipes.key('f', CoreModule.FILTER_MATERIAL_ITEM.get());
//            recipes.addCriterion("has_machine_frame", hasItem(CoreModule.MACHINE_FRAME_ITEM.get()));
//            recipes.shapedRecipe(MachinesModule.VALVE_ITEM.get())
//                    .addCriterion("has_comparator", hasItem(Items.COMPARATOR))
//                    .key('C', Items.COMPARATOR)
//                    .key('q', Items.QUARTZ)
//                    .patternLine("gqg").patternLine("fmf").patternLine("gCg").build();
//            recipes.shapedRecipe(MachinesModule.SMELTER_ITEM.get())
//                    .addCriterion("has_nether_brick", hasItem(Items.NETHER_BRICK))
//                    .key('b', Items.NETHER_BRICK)
//                    .patternLine("ppp").patternLine("imi").patternLine("bbb").build();
//            recipes.shapedRecipe(MachinesModule.PURIFIER_ITEM.get())
//                    .addCriterion("has_filter", hasItem(CoreModule.FILTER_MATERIAL_ITEM.get()))
//                    .patternLine("fff").patternLine("imi").patternLine("fff").build();
//            recipes.shapedRecipe(MachinesModule.LASER_ITEM.get())
//                    .addCriterion("has_diamond", hasItem(Items.DIAMOND))
//                    .addCriterion("has_emerald", hasItem(Items.EMERALD))
//                    .key('d', Items.DIAMOND)
//                    .key('e', Items.EMERALD)
//                    .patternLine("ggg").patternLine("eme").patternLine("ddd").build();
//            recipes.shapedRecipe(MachinesModule.CRYSTALLIZER_ITEM.get())
//                    .addCriterion("has_quartz", hasItem(Items.QUARTZ))
//                    .key('q', Items.QUARTZ)
//                    .patternLine("ggg").patternLine("qmq").patternLine("iii").build();
//        });
//        recipeBuilder.grouped("generator", recipes -> {
//            recipes.key('m', CoreModule.MACHINE_FRAME_ITEM.get());
//            recipes.key('G', Tags.Items.INGOTS_GOLD);
//            recipes.addCriterion("has_machine_frame", hasItem(CoreModule.MACHINE_FRAME_ITEM.get()));
//            recipes.addCriterion("has_resonant_plate", hasItem(CoreModule.RESONATING_PLATE_ITEM.get()));
//            recipes.shapedRecipe(GeneratorModule.ENERGY_COLLECTOR_ITEM.get())
//                    .addCriterion("has_diamond", hasItem(Items.DIAMOND))
//                    .addCriterion("has_quartz", hasItem(Tags.Items.GEMS_QUARTZ))
//                    .key('d', Items.DIAMOND)
//                    .key('q', Tags.Items.GEMS_QUARTZ)
//                    .patternLine("pdp").patternLine("qmq").patternLine("GGG").build();
//            recipes.shapedRecipe(GeneratorModule.GENERATOR_PART_ITEM.get())
//                    .key('r', Blocks.REDSTONE_BLOCK)
//                    .patternLine("GrG").patternLine("imi").patternLine("prp").build();
//            recipes.shapedRecipe(GeneratorModule.GENERATOR_CONTROLLER_ITEM.get())
//                    .addCriterion("has_comparator", hasItem(Blocks.COMPARATOR))
//                    .key('C', Blocks.COMPARATOR)
//                    .key('r', Tags.Items.DUSTS_REDSTONE)
//                    .patternLine("rCr").patternLine("imi").patternLine("pip").build();
//
//        });
    }

}
