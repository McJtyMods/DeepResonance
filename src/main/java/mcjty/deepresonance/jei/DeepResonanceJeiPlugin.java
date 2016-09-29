package mcjty.deepresonance.jei;

import mcjty.deepresonance.blocks.laser.LaserSetup;
import mcjty.deepresonance.blocks.laser.LaserTileEntity;
import mcjty.deepresonance.blocks.smelter.SmelterSetup;
import mezz.jei.api.*;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

@JEIPlugin
public class DeepResonanceJeiPlugin extends BlankModPlugin {

    @Override
    public void register(@Nonnull IModRegistry registry) {
        IJeiHelpers helpers = registry.getJeiHelpers();
        IGuiHelper guiHelper = helpers.getGuiHelper();

        registry.addRecipeCategories(
                new LaserRecipeCategory(guiHelper),
                new SmelterRecipeCategory(guiHelper));
        registry.addRecipeHandlers(
                new LaserRecipeHandler(),
                new SmelterRecipeHandler());

        List<IRecipeWrapper> recipes = new ArrayList<>();
        recipes.add(new SmelterRecipeWrapper());

        registry.addRecipeCategoryCraftingItem(new ItemStack(SmelterSetup.smelter), LaserRecipeCategory.ID);
    }
}
