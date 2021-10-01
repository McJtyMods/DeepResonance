package mcjty.deepresonance.modules.tank.client;

public class TankItemRenderer {} /* @todo 1.16implements ITESRItem {

    @Override
    public void renderItem(ItemStack itemStack, @Nonnull MatrixStack matrixStack, @Nonnull IRenderTypeBuffer renderTypeBuffer, int combinedLightIn, int combinedOverlayIn) {
        matrixStack.push();
        FluidStack stack = BlockTank.readFromTileNbt(AbstractItemBlock.getTileData(itemStack));
        if (!stack.isEmpty()) {
            TankTESR.render(matrixStack, renderTypeBuffer, stack.getFluid(), (stack.getAmount() / ((float) TankGrid.TANK_BUCKETS * 1000)), combinedLightIn);
        }
        for (Direction dir : Direction.values()) {
            for (BakedQuad quad : TankRenderer.INSTANCE.getModelQuads(dir)) {
                renderTypeBuffer.getBuffer(RenderType.getTranslucent()).addVertexData(matrixStack.getLast(), quad, 1, 1, 1, 1, combinedLightIn, combinedOverlayIn);
            }
        }
        matrixStack.pop();
    }


}
*/