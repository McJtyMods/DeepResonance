package mcjty.deepresonance.modules.tank.client;

import com.google.common.base.Preconditions;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import elec332.core.api.client.ITessellator;
import elec332.core.client.RenderHelper;
import elec332.core.client.util.AbstractTileEntityRenderer;
import elec332.core.world.WorldHelper;
import mcjty.deepresonance.modules.tank.tile.TileEntityTank;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Elec332 on 9-1-2020
 */
@OnlyIn(Dist.CLIENT)
public class TankTESR extends AbstractTileEntityRenderer<TileEntityTank> {

    private static final EnumSet<Direction> ITEM_DIRECTIONS = EnumSet.range(Direction.UP, Direction.EAST); //All except bottom

    @Override
    public void render(@Nonnull TileEntityTank tileTank, float partialTicks, @Nonnull MatrixStack matrixStackIn, @Nonnull IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
        if (!TankRenderer.INSTANCE.hasInitialized()) {
            return;
        }

        BlockPos pos = tileTank.getPos();
        final Fluid renderFluid = tileTank.getClientRenderFluid();
        EnumSet<Direction> dirs = Arrays.stream(Direction.values()).filter(dir -> {
            if (dir == Direction.DOWN && !(renderFluid == null || renderFluid == Fluids.EMPTY) && RenderTypeLookup.canRenderInLayer(renderFluid.getDefaultState(), RenderType.getSolid())) {
                return false; //If there is a fluid being rendered, the bottom doesn't need to be checked if the fluid is opaque
            }
            TileEntity tile = WorldHelper.getTileAt(Preconditions.checkNotNull(tileTank.getWorld()), pos.offset(dir));
            return !(tile instanceof TileEntityTank && ((TileEntityTank) tile).getClientRenderFluid() == renderFluid);
        }).collect(Collectors.toCollection(() -> EnumSet.noneOf(Direction.class)));

        float scale = tileTank.getClientRenderHeight();
        int color = 0;
        if (renderFluid != null) {
            color = renderFluid.getAttributes().getColor(tileTank.getWorld(), tileTank.getPos());
        }

        render(matrixStackIn, bufferIn, renderFluid, dirs, combinedLightIn, scale, color);
    }

    public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, Fluid renderFluid, float height, int brightness) {
        int color = 0;
        if (renderFluid != null) {
            color = renderFluid.getAttributes().getColor();
        }
        render(matrixStackIn, bufferIn, renderFluid, ITEM_DIRECTIONS, brightness, height, color);
    }

    private void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, Fluid renderFluid, Set<Direction> dirs, int brightness, float scale, int color) {
        matrixStackIn.push();

        renderModel(matrixStackIn, bufferIn.getBuffer(RenderType.getTranslucent()), dirs, brightness);

        if (renderFluid != null) {
            for (RenderType renderType : RenderType.getBlockRenderTypes()) {
                if (RenderTypeLookup.canRenderInLayer(renderFluid.getDefaultState(), renderType)) {
                    renderFluid(scale, color, RenderHelper.forWorldRenderer(bufferIn.getBuffer(renderType)), renderFluid, dirs, brightness, matrixStackIn);
                }
            }
        }

        matrixStackIn.pop();
    }

    private void renderModel(MatrixStack matrixStack, IVertexBuilder vertexBuilder, Set<Direction> dirs, int brightness) {
        for (Direction dir : dirs) {
            vertexBuilder.addVertexData(matrixStack.getLast(), TankRenderer.INSTANCE.getInsideQuad(dir), 1, 1, 1, brightness, OverlayTexture.NO_OVERLAY, true);
        }
    }

    private void renderFluid(float scale, int color, ITessellator tessellator, Fluid renderFluid, Set<Direction> dirs, int brightness, MatrixStack matrixStack) {

        float offset = -0.002f;
        TextureAtlasSprite fluid = RenderHelper.getFluidTexture(renderFluid, false);

        float u1 = fluid.getMinU();
        float v1 = fluid.getMinV();
        float u2 = fluid.getMaxU();
        float v2 = fluid.getMaxV();
        float edge = 2.9f / 16f;

        tessellator.setColorRGBA_I(color, 255);
        tessellator.setBrightness(brightness);
        tessellator.setMatrix(matrixStack.getLast().getMatrix());

        if (scale > 0.0f) {
            //TOP
            tessellator.addVertexWithUV(0, scale + offset, 0, u1, v1);
            tessellator.addVertexWithUV(0, scale + offset, 1, u1, v2);
            tessellator.addVertexWithUV(1, scale + offset, 1, u2, v2);
            tessellator.addVertexWithUV(1, scale + offset, 0, u2, v1);

            if (scale > edge) {

                if (scale > 1 - edge) {
                    scale = 1 - edge;
                }

                v2 -= (fluid.getMaxV() - fluid.getMinV()) * (1 - scale);


                if (dirs.contains(Direction.NORTH)) {
                    tessellator.addVertexWithUV(1 - edge, scale, -offset, u1, v1);
                    tessellator.addVertexWithUV(1 - edge, edge, -offset, u1, v2);
                    tessellator.addVertexWithUV(edge, edge, -offset, u2, v2);
                    tessellator.addVertexWithUV(edge, scale, -offset, u2, v1);
                }

                if (dirs.contains(Direction.WEST)) {
                    tessellator.addVertexWithUV(-offset, edge, 1 - edge, u1, v2);
                    tessellator.addVertexWithUV(-offset, scale, 1 - edge, u1, v1);
                    tessellator.addVertexWithUV(-offset, scale, edge, u2, v1);
                    tessellator.addVertexWithUV(-offset, edge, edge, u2, v2);
                }

                if (dirs.contains(Direction.SOUTH)) {
                    tessellator.addVertexWithUV(1 - edge, edge, 1 + offset, u1, v2);
                    tessellator.addVertexWithUV(1 - edge, scale, 1 + offset, u1, v1);
                    tessellator.addVertexWithUV(edge, scale, 1 + offset, u2, v1);
                    tessellator.addVertexWithUV(edge, edge, 1 + offset, u2, v2);
                }

                if (dirs.contains(Direction.EAST)) {
                    tessellator.addVertexWithUV(1 + offset, scale, 1 - edge, u1, v1);
                    tessellator.addVertexWithUV(1 + offset, edge, 1 - edge, u1, v2);
                    tessellator.addVertexWithUV(1 + offset, edge, edge, u2, v2);
                    tessellator.addVertexWithUV(1 + offset, scale, edge, u2, v1);
                }
            }
        }
        tessellator.clearMatrix();
    }

}
