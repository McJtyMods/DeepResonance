package mcjty.deepresonance.compat.jei;

import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.compat.jei.laser.LaserRecipeHandler;
import mcjty.deepresonance.modules.machines.block.LaserContainer;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

@JeiPlugin
public class DeepResonanceJeiPlugin implements IModPlugin {

    @Nonnull
    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(DeepResonance.MODID, "jeiplugin");
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        registration.addRecipeTransferHandler(new LaserRecipeHandler());
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        registration.registerSubtypeInterpreter();

        IJeiHelpers helpers = registry.getJeiHelpers();
        IGuiHelper guiHelper = helpers.getGuiHelper();

        registry.addRecipeCategories(
                new LaserRecipeCategory(guiHelper),
                new SmelterRecipeCategory(guiHelper),
                new PurifierRecipeCategory(guiHelper));
        registry.addRecipeHandlers(
                new LaserRecipeHandler(),
                new SmelterRecipeHandler(),
                new PurifierRecipeHandler());

        List<IRecipeWrapper> recipes = new ArrayList<>();
        for (String registryName : LaserTileEntity.infusingBonusMap.keySet()) {
            Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(registryName));
            recipes.add(new LaserRecipeWrapper(item));
        }
        recipes.add(new SmelterRecipeWrapper());
        recipes.add(new PurifierRecipeWrapper());
        registry.addRecipes(recipes);

        registry.addRecipeCategoryCraftingItem(new ItemStack(LaserSetup.laserBlock), LaserRecipeCategory.ID);
        registry.addRecipeCategoryCraftingItem(new ItemStack(SmelterSetup.smelter), SmelterRecipeCategory.ID);
        registry.addRecipeCategoryCraftingItem(new ItemStack(PurifierSetup.purifierBlock), PurifierRecipeCategory.ID);
    }
}
