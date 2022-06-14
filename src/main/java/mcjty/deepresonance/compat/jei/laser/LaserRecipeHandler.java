package mcjty.deepresonance.compat.jei.laser;

import mcjty.deepresonance.modules.machines.block.LaserContainer;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public class LaserRecipeHandler implements IRecipeTransferHandler<LaserContainer, LaserRecipeWrapper> {

    @Override
    public Class<LaserRecipeWrapper> getRecipeClass() {
        return LaserRecipeWrapper.class;
    }

    @Override
    public Class<LaserContainer> getContainerClass() {
        return LaserContainer.class;
    }

    @Override
    public @Nullable IRecipeTransferError transferRecipe(LaserContainer container, LaserRecipeWrapper recipe, IRecipeSlotsView recipeSlots, Player player, boolean maxTransfer, boolean doTransfer) {
        return IRecipeTransferHandler.super.transferRecipe(container, recipe, recipeSlots, player, maxTransfer, doTransfer);
    }
}
