package mcjty.deepresonance.modules.tank.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import mcjty.deepresonance.modules.tank.TankModule;
import mcjty.deepresonance.modules.tank.blocks.TankTileEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.client.registry.ClientRegistry;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;

public class TankTESR extends TileEntityRenderer<TankTileEntity> {

    private static final EnumSet<Direction> ITEM_DIRECTIONS = EnumSet.range(Direction.UP, Direction.EAST); //All except bottom

    public TankTESR(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    public static void register() {
        ClientRegistry.bindTileEntityRenderer(TankModule.TYPE_TANK.get(), TankTESR::new);
    }

    @Override
    public void render(@Nonnull TankTileEntity tileTank, float partialTicks, @Nonnull MatrixStack matrixStackIn, @Nonnull IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
        // @todo 1.16
//        if (!TankRenderer.INSTANCE.hasInitialized()) {
//            return;
//        }

        BlockPos pos = tileTank.getBlockPos();
        final Fluid renderFluid = tileTank.getClientRenderFluid();
        EnumSet<Direction> dirs = Arrays.stream(Direction.values()).filter(dir -> {
            if (dir == Direction.DOWN && tileTank.getClientRenderHeight() > 0.0001 && !(renderFluid == null || renderFluid == Fluids.EMPTY) && RenderTypeLookup.canRenderInLayer(renderFluid.defaultFluidState(), RenderType.solid())) {
                return false; //If there is a fluid being rendered, the bottom doesn't need to be checked if the fluid is opaque
            }
            TileEntity tile = tileTank.getLevel().getBlockEntity(pos.relative(dir));
            return !(tile instanceof TankTileEntity && ((TankTileEntity) tile).getClientRenderFluid() == renderFluid);
        }).collect(Collectors.toCollection(() -> EnumSet.noneOf(Direction.class)));

        float scale = tileTank.getClientRenderHeight();
        int color = 0;
        if (renderFluid != null) {
            color = renderFluid.getAttributes().getColor(tileTank.getLevel(), tileTank.getBlockPos());
        }

        render(matrixStackIn, bufferIn, renderFluid, dirs, combinedLightIn, scale, color);
    }

    public static void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, Fluid renderFluid, float height, int brightness) {
        int color = 0;
        if (renderFluid != null) {
            color = renderFluid.getAttributes().getColor();
        }
        render(matrixStackIn, bufferIn, renderFluid, ITEM_DIRECTIONS, brightness, height, color);
    }

    private static void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, Fluid renderFluid, Set<Direction> dirs, int brightness, float scale, int color) {
        matrixStackIn.pushPose();

        renderModel(matrixStackIn, bufferIn.getBuffer(RenderType.translucent()), dirs, brightness);

        if (renderFluid != null) {
            for (RenderType renderType : RenderType.chunkBufferLayers()) {
                if (RenderTypeLookup.canRenderInLayer(renderFluid.defaultFluidState(), renderType)) {
                    // @todo 1.16
//                    renderFluid(scale, color, RenderHelper.forWorldRenderer(bufferIn.getBuffer(renderType)), renderFluid, dirs, brightness, matrixStackIn);
                }
            }
        }

        matrixStackIn.popPose();
    }

    private static void renderModel(MatrixStack matrixStack, IVertexBuilder vertexBuilder, Set<Direction> dirs, int brightness) {
        for (Direction dir : dirs) {
            // @todo 1.16
//            vertexBuilder.addVertexData(matrixStack.last(), TankRenderer.INSTANCE.getInsideQuad(dir), 1, 1, 1, brightness, OverlayTexture.NO_OVERLAY, true);
        }
    }

    // @todo 1.16
//    private static void renderFluid(float scale, int color, ITessellator tessellator, Fluid renderFluid, Set<Direction> dirs, int brightness, MatrixStack matrixStack) {
//
//        float offset = -0.002f;
//        TextureAtlasSprite fluid = RenderHelper.getFluidTexture(renderFluid, false);
//
//        float u1 = fluid.getMinU();
//        float v1 = fluid.getMinV();
//        float u2 = fluid.getMaxU();
//        float v2 = fluid.getMaxV();
//        float edge = 2.9f / 16f;
//
//        tessellator.setColorRGBA_I(color, 255);
//        tessellator.setBrightness(brightness);
//        tessellator.setMatrix(matrixStack.getLast().getMatrix());
//
//        if (scale > 0.0f) {
//            //TOP
//            tessellator.addVertexWithUV(0, scale + offset, 0, u1, v1);
//            tessellator.addVertexWithUV(0, scale + offset, 1, u1, v2);
//            tessellator.addVertexWithUV(1, scale + offset, 1, u2, v2);
//            tessellator.addVertexWithUV(1, scale + offset, 0, u2, v1);
//
//            if (scale > edge) {
//
//                if (scale > 1 - edge) {
//                    scale = 1 - edge;
//                }
//
//                v2 -= (fluid.getMaxV() - fluid.getMinV()) * (1 - scale);
//
//
//                if (dirs.contains(Direction.NORTH)) {
//                    tessellator.addVertexWithUV(1 - edge, scale, -offset, u1, v1);
//                    tessellator.addVertexWithUV(1 - edge, edge, -offset, u1, v2);
//                    tessellator.addVertexWithUV(edge, edge, -offset, u2, v2);
//                    tessellator.addVertexWithUV(edge, scale, -offset, u2, v1);
//                }
//
//                if (dirs.contains(Direction.WEST)) {
//                    tessellator.addVertexWithUV(-offset, edge, 1 - edge, u1, v2);
//                    tessellator.addVertexWithUV(-offset, scale, 1 - edge, u1, v1);
//                    tessellator.addVertexWithUV(-offset, scale, edge, u2, v1);
//                    tessellator.addVertexWithUV(-offset, edge, edge, u2, v2);
//                }
//
//                if (dirs.contains(Direction.SOUTH)) {
//                    tessellator.addVertexWithUV(1 - edge, edge, 1 + offset, u1, v2);
//                    tessellator.addVertexWithUV(1 - edge, scale, 1 + offset, u1, v1);
//                    tessellator.addVertexWithUV(edge, scale, 1 + offset, u2, v1);
//                    tessellator.addVertexWithUV(edge, edge, 1 + offset, u2, v2);
//                }
//
//                if (dirs.contains(Direction.EAST)) {
//                    tessellator.addVertexWithUV(1 + offset, scale, 1 - edge, u1, v1);
//                    tessellator.addVertexWithUV(1 + offset, edge, 1 - edge, u1, v2);
//                    tessellator.addVertexWithUV(1 + offset, edge, edge, u2, v2);
//                    tessellator.addVertexWithUV(1 + offset, scale, edge, u2, v1);
//                }
//            }
//        }
//        tessellator.clearMatrix();
//    }

}
