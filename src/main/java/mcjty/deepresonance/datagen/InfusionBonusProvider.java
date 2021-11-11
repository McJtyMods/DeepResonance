package mcjty.deepresonance.datagen;

import net.minecraft.data.DataGenerator;

public class InfusionBonusProvider {

    public InfusionBonusProvider(DataGenerator generatorIn) {
//        super(generatorIn);
    }

    // @todo 1.16
//    @Override
//    protected void registerObjects(TriConsumer<ResourceLocation, Ingredient, InfusionBonus> registry) {
//        registry.accept(new ResourceLocation(DeepResonance.MODID, "diamond"), Ingredient.fromTag(GEMS_DIAMOND), new InfusionBonus(
//                DyeColor.BLUE,
//                new InfusionModifier(5, 100),
//                InfusionModifier.NONE,
//                InfusionModifier.NONE
//        ));
//        registry.accept(new ResourceLocation(DeepResonance.MODID, "emerald"), Ingredient.fromTag(GEMS_EMERALD), new InfusionBonus(
//                DyeColor.GREEN,
//                new InfusionModifier(8.0f, 100.0f),
//                InfusionModifier.NONE,
//                InfusionModifier.NONE
//        ));
//        registry.accept(new ResourceLocation(DeepResonance.MODID, "ender"), Ingredient.fromTag(ENDER_PEARLS), new InfusionBonus(
//                DyeColor.GREEN,
//                new InfusionModifier(2.0f, 100.0f),
//                InfusionModifier.NONE,
//                InfusionModifier.NONE
//        ));
//        registry.accept(new ResourceLocation(DeepResonance.MODID, "redstone"), Ingredient.fromTag(DUSTS_REDSTONE), new InfusionBonus(
//                DyeColor.RED,
//                new InfusionModifier(-1.0f, 0.0f),
//                new InfusionModifier(5.0f, 60.0f),
//                InfusionModifier.NONE
//        ));
//        registry.accept(new ResourceLocation(DeepResonance.MODID, "gunpowder"), Ingredient.fromTag(GUNPOWDER), new InfusionBonus(
//                DyeColor.GRAY,
//                new InfusionModifier(-5.0f, 0.0f),
//                new InfusionModifier(8.0f, 70.0f),
//                new InfusionModifier(4.0f, 60.0f)
//        ));
//        registry.accept(new ResourceLocation(DeepResonance.MODID, "glowstone"), Ingredient.fromTag(DUSTS_GLOWSTONE), new InfusionBonus(
//                DyeColor.YELLOW,
//                new InfusionModifier(-2.0f, 0.0f),
//                new InfusionModifier(6.0f, 50.0f),
//                new InfusionModifier(3.0f, 50.0f)
//        ));
//        registry.accept(new ResourceLocation(DeepResonance.MODID, "blaze_powder"), Ingredient.fromItems(Items.BLAZE_POWDER), new InfusionBonus(
//                DyeColor.YELLOW,
//                new InfusionModifier(-6.0f, 0.0f),
//                new InfusionModifier(5.0f, 70.0f),
//                new InfusionModifier(5.0f, 70.0f)
//        ));
//        registry.accept(new ResourceLocation(DeepResonance.MODID, "quartz"), Ingredient.fromTag(GEMS_QUARTZ), new InfusionBonus(
//                DyeColor.WHITE,
//                new InfusionModifier(-1.0f, 0.0f),
//                InfusionModifier.NONE,
//                new InfusionModifier(7.0f, 80.0f)
//        ));
//        registry.accept(new ResourceLocation(DeepResonance.MODID, "nether_star"), Ingredient.fromTag(NETHER_STARS), new InfusionBonus(
//                DyeColor.LIME,
//                new InfusionModifier(-60.0f, 0.0f),
//                new InfusionModifier(90.0f, 100.0f),
//                new InfusionModifier(90.0f, 100.0f)
//        ));
//        registry.accept(new ResourceLocation(DeepResonance.MODID, "ghast_tear"), Ingredient.fromItems(Items.GHAST_TEAR), new InfusionBonus(
//                DyeColor.WHITE,
//                new InfusionModifier(-20.0f, 0.0f),
//                new InfusionModifier(25.0f, 100.0f),
//                new InfusionModifier(15.0f, 100.0f)
//        ));
//        registry.accept(new ResourceLocation(DeepResonance.MODID, "prismarine"), Ingredient.fromItems(Items.PRISMARINE_SHARD), new InfusionBonus(
//                DyeColor.BLUE,
//                InfusionModifier.NONE,
//                new InfusionModifier(3.0f, 30.0f),
//                new InfusionModifier(3.0f, 30.0f)
//        ));
//        registry.accept(new ResourceLocation(DeepResonance.MODID, "prismarine_crystals"), Ingredient.fromItems(Items.PRISMARINE_CRYSTALS), new InfusionBonus(
//                DyeColor.BLUE,
//                InfusionModifier.NONE,
//                new InfusionModifier(4.0f, 35.0f),
//                new InfusionModifier(4.0f, 35.0f)
//        ));
//        registry.accept(new ResourceLocation(DeepResonance.MODID, "slime"), Ingredient.fromItems(Items.SLIME_BALL), new InfusionBonus(
//                DyeColor.GREEN,
//                InfusionModifier.NONE,
//                InfusionModifier.NONE,
//                new InfusionModifier(-10.0f, 1.0f)
//        ));
//        registry.accept(new ResourceLocation(DeepResonance.MODID, "coal"), Ingredient.fromTag(ItemTags.COALS), new InfusionBonus(
//                DyeColor.BLACK,
//                new InfusionModifier(-1.0f, 0.0f),
//                new InfusionModifier(-10.0f, 0.0f),
//                InfusionModifier.NONE
//        ));
//        registry.accept(new ResourceLocation(DeepResonance.MODID, "nether_wart"), Ingredient.fromTag(CROPS_NETHER_WART), new InfusionBonus(
//                DyeColor.RED,
//                new InfusionModifier(-3.0f, 0.0f),
//                new InfusionModifier(2.0f, 35.0f),
//                new InfusionModifier(-2.0f, 1.0f)
//        ));
//        registry.accept(new ResourceLocation(DeepResonance.MODID, "gold"), Ingredient.fromTag(INGOTS_GOLD), new InfusionBonus(
//                DyeColor.YELLOW,
//                InfusionModifier.NONE,
//                new InfusionModifier(-1.0f, 0.0f),
//                new InfusionModifier(1.0f, 30.0f)
//        ));
//        registry.accept(new ResourceLocation(DeepResonance.MODID, "iron"), Ingredient.fromTag(INGOTS_IRON), new InfusionBonus(
//                DyeColor.GRAY,
//                InfusionModifier.NONE,
//                new InfusionModifier(-2.0f, 0.0f),
//                new InfusionModifier(1.0f, 20.0f)
//        ));
//        registry.accept(new ResourceLocation(DeepResonance.MODID, "snow"), Ingredient.fromItems(Items.SNOWBALL), new InfusionBonus(
//                DyeColor.BLUE,
//                new InfusionModifier(1.0f, 30.0f),
//                InfusionModifier.NONE,
//                new InfusionModifier(1.0f, 40.0f)
//        ));
//    }

}
