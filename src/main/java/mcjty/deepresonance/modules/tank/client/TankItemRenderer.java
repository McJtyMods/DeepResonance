package mcjty.deepresonance.modules.tank.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class TankItemRenderer extends BlockEntityWithoutLevelRenderer {

    public TankItemRenderer(BlockEntityRenderDispatcher pBlockEntityRenderDispatcher, EntityModelSet pEntityModelSet) {
        super(pBlockEntityRenderDispatcher, pEntityModelSet);
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext transformType, PoseStack matrixStack, MultiBufferSource buffer, int lightIn, int overlayIn) {
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

//    public static Callable<BlockEntityWithoutLevelRenderer> getRenderer() {
//        return TankItemRenderer::new;
//    }

}
