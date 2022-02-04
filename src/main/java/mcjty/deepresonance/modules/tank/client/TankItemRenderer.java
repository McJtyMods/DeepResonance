package mcjty.deepresonance.modules.tank.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.ItemStack;

import java.util.concurrent.Callable;

public class TankItemRenderer extends ItemStackTileEntityRenderer {

    @Override
    public void renderByItem(ItemStack stack, ItemCameraTransforms.TransformType transformType, MatrixStack matrixStack, IRenderTypeBuffer buffer, int lightIn, int overlayIn) {
        super.renderByItem(stack, transformType, matrixStack, buffer, lightIn, overlayIn);
//        matrixStack.pushPose();
//        CompoundNBT compoundnbt = stack.getTagElement("BlockEntityTag");
//        FluidStack fluidStack = TankBlock.readFromTileNbt(compoundnbt);
//        if (!fluidStack.isEmpty()) {
//            TankTESR.renderInternal(matrixStack, renderTypeBuffer, fluidStack.getFluid(), (fluidStack.getAmount() / ((float) TankGrid.TANK_BUCKETS * 1000)), combinedLightIn);
//        }
//        for (Direction dir : Direction.values()) {
//            for (BakedQuad quad : TankTESR.INSTANCE.getModelQuads(dir)) {
//                renderTypeBuffer.getBuffer(RenderType.getTranslucent()).addVertexData(matrixStack.getLast(), quad, 1, 1, 1, 1, combinedLightIn, combinedOverlayIn);
//            }
//        }
//        matrixStack.popPose();
    }

    public static Callable<ItemStackTileEntityRenderer> getRenderer() {
        return TankItemRenderer::new;
    }

}
