package mcjty.deepresonance.datagen;

import mcjty.deepresonance.modules.core.CoreModule;
import mcjty.deepresonance.modules.generator.GeneratorModule;
import mcjty.deepresonance.modules.machines.MachinesModule;
import mcjty.deepresonance.modules.radiation.RadiationModule;
import mcjty.deepresonance.modules.tank.TankModule;
import mcjty.lib.datagen.BaseRecipeProvider;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.Tags;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public class Recipes extends BaseRecipeProvider {

    public Recipes(DataGenerator generatorIn) {
        super(generatorIn);
        add('m', CoreModule.MACHINE_FRAME_ITEM.get());
        add('s', ItemTags.SAND);
        add('P', CoreModule.RESONATING_PLATE_ITEM.get());
        add('f', CoreModule.SPENT_FILTER_ITEM.get());
        add('F', CoreModule.FILTER_MATERIAL_ITEM.get());
        add('C', Items.COMPARATOR);
        add('q', Items.QUARTZ);
        add('X', Tags.Items.INGOTS_GOLD);
    }

    @Override
    protected void buildCraftingRecipes(@Nonnull Consumer<FinishedRecipe> consumer) {
//        recipeBuilder.key('g', Tags.Items.GLASS);
//        recipeBuilder.key('p', CoreModule.RESONATING_PLATE_ITEM.get());
//        recipeBuilder.key('o', Tags.Items.OBSIDIAN);

        // @todo cannot easily do this with datagen since it doesn't support "count": 8
//        CookingRecipeBuilder.smelting(Ingredient.of(DeepResonanceTags.RESONANT_ORE_ITEM), CoreModule.RESONATING_PLATE_ITEM.get(), 0.4f, 200)
//                .unlockedBy("has_ore", has(DeepResonanceTags.RESONANT_ORE_ITEM))
//                .save(consumer);

        build(consumer, ShapedRecipeBuilder.shaped(MachinesModule.LENS_BLOCK.get())
                        .define('g', Tags.Items.GLASS_PANES)
                        .unlockedBy("has_pane", has(Tags.Items.GLASS_PANES)),
                "gPg", "P P", "gPg");

        build(consumer, ShapedRecipeBuilder.shaped(CoreModule.FILTER_MATERIAL_ITEM.get(), 8)
                        .define('g', Tags.Items.GRAVEL)
                        .unlockedBy("has_gravel", inventoryTrigger(ItemPredicate.Builder.item().of(Tags.Items.GRAVEL).build())),
                "gcg", "csc", "gcg");
        build(consumer, ShapedRecipeBuilder.shaped(CoreModule.MACHINE_FRAME_ITEM.get())
                        .define('g', Tags.Items.STONE)
                        .unlockedBy("has_iron", inventoryTrigger(ItemPredicate.Builder.item().of(Tags.Items.INGOTS_IRON).build())),
                "iPi", "PgP", "iPi");
        build(consumer, ShapedRecipeBuilder.shaped(CoreModule.RESONATING_PLATE_BLOCK_ITEM.get())
                        .unlockedBy("has_resonant_plate", has(CoreModule.RESONATING_PLATE_ITEM.get())),
                "PPP", "PPP", "PPP");
        // @todo 1.16 FIX THIS
//        build(consumer, ShapelessRecipeBuilder.shapeless(CoreModule.RESONATING_PLATE_ITEM.get(), 9)
//                .requires(CoreModule.RESONATING_PLATE_BLOCK_ITEM.get()));

        build(consumer, ShapedRecipeBuilder.shaped(RadiationModule.RADIATION_MONITOR.get())
                        .define('x', Items.CLOCK)
                        .unlockedBy("", has(CoreModule.RESONATING_PLATE_ITEM.get())),
                    "qCq", "ror", "qxq"
        );

        build(consumer, ShapedRecipeBuilder.shaped(TankModule.TANK_ITEM.get())
                        .unlockedBy("has_resonant_plate", has(CoreModule.RESONATING_PLATE_ITEM.get())),
                "iPi", "GGG", "iOi");

        build(consumer, ShapedRecipeBuilder.shaped(RadiationModule.RADIATION_SUIT_HELMET.get())
                        .unlockedBy("has_resonant_plate", has(CoreModule.RESONATING_PLATE_ITEM.get())),
                "PPP", "P P");
        build(consumer, ShapedRecipeBuilder.shaped(RadiationModule.RADIATION_SUIT_CHESTPLATE.get())
                        .unlockedBy("has_resonant_plate", has(CoreModule.RESONATING_PLATE_ITEM.get())),
                "P P", "PPP", "PPP");
        build(consumer, ShapedRecipeBuilder.shaped(RadiationModule.RADIATION_SUIT_LEGGINGS.get())
                        .unlockedBy("has_resonant_plate", has(CoreModule.RESONATING_PLATE_ITEM.get())),
                "PPP", "P P", "P P");
        build(consumer, ShapedRecipeBuilder.shaped(RadiationModule.RADIATION_SUIT_BOOTS.get())
                        .unlockedBy("has_resonant_plate", has(CoreModule.RESONATING_PLATE_ITEM.get())),
                "P P", "P P");

        build(consumer, ShapedRecipeBuilder.shaped(RadiationModule.DENSE_GLASS_ITEM.get())
                        .unlockedBy("has_spent_filter", has(CoreModule.SPENT_FILTER_ITEM.get())),
                "fGf", "GOG", "fGf");
        build(consumer, ShapedRecipeBuilder.shaped(RadiationModule.DENSE_OBSIDIAN_ITEM.get())
                        .unlockedBy("has_spent_filter", has(CoreModule.SPENT_FILTER_ITEM.get())),
                "OfO", "fOf", "OfO");


        build(consumer, ShapedRecipeBuilder.shaped(MachinesModule.VALVE_ITEM.get())
                        .unlockedBy("has_machine_frame", has(CoreModule.MACHINE_FRAME_ITEM.get())),
                "GGG", "FmF", "GCG");
        build(consumer, ShapedRecipeBuilder.shaped(MachinesModule.SMELTER_ITEM.get())
                        .unlockedBy("has_machine_frame", has(CoreModule.MACHINE_FRAME_ITEM.get())),
                "FFF", "imi", "FFF");
        build(consumer, ShapedRecipeBuilder.shaped(MachinesModule.PURIFIER_ITEM.get())
                        .define('x', Items.NETHER_BRICK)
                        .unlockedBy("has_machine_frame", has(CoreModule.MACHINE_FRAME_ITEM.get())),
                "PPP", "imi", "xxx");
        build(consumer, ShapedRecipeBuilder.shaped(MachinesModule.LASER_ITEM.get())
                        .unlockedBy("has_machine_frame", has(CoreModule.MACHINE_FRAME_ITEM.get())),
                "GXG", "eme", "ddd");
        build(consumer, ShapedRecipeBuilder.shaped(MachinesModule.CRYSTALLIZER_ITEM.get())
                        .unlockedBy("has_machine_frame", has(CoreModule.MACHINE_FRAME_ITEM.get())),
                "GXG", "qmq", "iii");

        build(consumer, ShapedRecipeBuilder.shaped(GeneratorModule.ENERGY_COLLECTOR_ITEM.get())
                        .unlockedBy("has_machine_frame", has(CoreModule.MACHINE_FRAME_ITEM.get())),
                "PdP", "qmq", "XXX");
        build(consumer, ShapedRecipeBuilder.shaped(GeneratorModule.GENERATOR_PART_ITEM.get())
                        .unlockedBy("has_machine_frame", has(CoreModule.MACHINE_FRAME_ITEM.get())),
                "XRX", "imi", "PRP");
        build(consumer, ShapedRecipeBuilder.shaped(GeneratorModule.GENERATOR_CONTROLLER_ITEM.get())
                        .unlockedBy("has_machine_frame", has(CoreModule.MACHINE_FRAME_ITEM.get())),
                "RCR", "imi", "PiP");
    }

}
