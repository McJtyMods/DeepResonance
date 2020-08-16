package mcjty.deepresonance.modules.tank.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import elec332.core.api.client.ITESRItem;
import elec332.core.item.AbstractItemBlock;
import mcjty.deepresonance.modules.tank.blocks.BlockTank;
import mcjty.deepresonance.modules.tank.grid.TankGrid;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;

/**
 * Created by Elec332 on 16-8-2020
 */
public class TankItemRenderer implements ITESRItem {

    private final TankTESR tesr = new TankTESR();

    @Override
    public void renderItem(ItemStack itemStack, @Nonnull MatrixStack matrixStack, @Nonnull IRenderTypeBuffer renderTypeBuffer, int combinedLightIn, int combinedOverlayIn) {
        matrixStack.push();
        FluidStack stack = BlockTank.readFromTileNbt(AbstractItemBlock.getTileData(itemStack));
        if (!stack.isEmpty()) {
            tesr.render(matrixStack, renderTypeBuffer, stack.getFluid(), (stack.getAmount() / ((float) TankGrid.TANK_BUCKETS * 1000)), combinedLightIn);
        }
        for (Direction dir : Direction.values()) {
            for (BakedQuad quad : TankRenderer.INSTANCE.getModelQuads(dir)) {
                renderTypeBuffer.getBuffer(RenderType.getTranslucent()).addVertexData(matrixStack.getLast(), quad, 1, 1, 1, 1, combinedLightIn, combinedOverlayIn);
            }
        }
        matrixStack.pop();
    }


}
