package mcjty.deepresonance.data;

import elec332.core.api.data.recipe.IGroupedRecipeManager;
import elec332.core.data.AbstractRecipeProvider;
import mcjty.deepresonance.modules.core.CoreModule;
import mcjty.deepresonance.modules.machines.MachinesModule;
import mcjty.deepresonance.modules.radiation.RadiationModule;
import mcjty.deepresonance.modules.tank.TankModule;
import net.minecraft.data.DataGenerator;
import net.minecraft.item.Items;
import net.minecraft.tags.ItemTags;
import net.minecraftforge.common.Tags;

/**
 * Created by Elec332 on 10-1-2020
 */
public class RecipesProvider extends AbstractRecipeProvider {

    RecipesProvider(DataGenerator datagen) {
        super(datagen);
    }

    @Override
    protected void registerRecipes(IGroupedRecipeManager recipeBuilder) {
        recipeBuilder.key('c', ItemTags.COALS);
        recipeBuilder.key('s', ItemTags.SAND);
        recipeBuilder.key('g', Tags.Items.GLASS);
        recipeBuilder.key('i', Tags.Items.INGOTS_IRON);
        recipeBuilder.key('p', CoreModule.RESONATING_PLATE_ITEM.get());
        recipeBuilder.key('o', Tags.Items.OBSIDIAN);
        recipeBuilder.grouped("core", recipes -> {
            recipes.shapedRecipe(CoreModule.FILTER_MATERIAL_ITEM.get(), 8).key('r', Tags.Items.GRAVEL)
                    .addCriterion("has_gravel", hasItem(Tags.Items.GRAVEL)).addCriterion("has_coal", hasItem(ItemTags.COALS)).addCriterion("has_sand", hasItem(ItemTags.SAND))
                    .patternLine("rcr").patternLine("csc").patternLine("rcr").build();
            recipes.shapedRecipe(CoreModule.MACHINE_FRAME_ITEM.get()).key('q', Tags.Items.STONE)
                    .addCriterion("has_iron", hasItem(Tags.Items.INGOTS_IRON))
                    .patternLine("ipi").patternLine("pqp").patternLine("ipi").build();

            recipes.addCriterion("has_resonant_plate", hasItem(CoreModule.RESONATING_PLATE_ITEM.get()));
            recipes.shapedRecipe(CoreModule.RESONATING_PLATE_BLOCK_ITEM.get())
                    .patternLine("ppp").patternLine("ppp").patternLine("ppp").build();
            recipes.shapelessRecipe(CoreModule.RESONATING_PLATE_ITEM.get(), 9)
                    .addIngredient(CoreModule.RESONATING_PLATE_BLOCK_ITEM.get()).build();
        });
        recipeBuilder.grouped("tank", recipes -> {
            recipes.shapedRecipe(TankModule.TANK_ITEM.get())
                    .addCriterion("has_resonant_plate", hasItem(CoreModule.RESONATING_PLATE_ITEM.get()))
                    .patternLine("ipi").patternLine("ggg").patternLine("ioi").build();
        });
        recipeBuilder.grouped("radiation", recipes -> {
            recipes.key('f', CoreModule.SPENT_FILTER_ITEM.get());
            recipes.grouped(armor -> {
                armor.addCriterion("has_resonant_plate", hasItem(CoreModule.RESONATING_PLATE_ITEM.get()));
                armor.shapedRecipe(RadiationModule.RADIATION_SUIT_HELMET.get())
                        .patternLine("ppp").patternLine("p p").build();
                armor.shapedRecipe(RadiationModule.RADIATION_SUIT_CHESTPLATE.get())
                        .patternLine("p p").patternLine("ppp").patternLine("ppp").build();
                armor.shapedRecipe(RadiationModule.RADIATION_SUIT_LEGGINGS.get())
                        .patternLine("ppp").patternLine("p p").patternLine("p p").build();
                armor.shapedRecipe(RadiationModule.RADIATION_SUIT_BOOTS.get())
                        .patternLine("p p").patternLine("p p").build();
            });

            recipes.addCriterion("has_spent_filter", hasItem(CoreModule.SPENT_FILTER_ITEM.get()));
            recipes.shapedRecipe(RadiationModule.DENSE_GLASS_ITEM.get(), 4)
                    .patternLine("fgf").patternLine("gog").patternLine("fgf").build();
            recipes.shapedRecipe(RadiationModule.DENSE_OBSIDIAN_ITEM.get(), 4)
                    .patternLine("ofo").patternLine("fof").patternLine("ofo").build();
        });
        recipeBuilder.grouped("machines", recipes -> {
            recipes.key('m', CoreModule.MACHINE_FRAME_ITEM.get());
            recipes.key('f', CoreModule.FILTER_MATERIAL_ITEM.get());
            recipes.addCriterion("has_machine_frame", hasItem(CoreModule.MACHINE_FRAME_ITEM.get()));
            recipes.shapedRecipe(MachinesModule.VALVE_ITEM.get())
                    .addCriterion("has_comparator", hasItem(Items.COMPARATOR))
                    .key('C', Items.COMPARATOR)
                    .key('q', Items.QUARTZ)
                    .patternLine("gqg").patternLine("fmf").patternLine("gCg").build();
        });
    }

}
