package mcjty.deepresonance.modules.tank.client;

import com.google.common.base.Preconditions;
import com.mojang.blaze3d.platform.GlStateManager;
import elec332.core.api.client.ITessellator;
import elec332.core.client.RenderHelper;
import elec332.core.world.WorldHelper;
import mcjty.deepresonance.modules.tank.TankModule;
import mcjty.deepresonance.modules.tank.tile.TileEntityTank;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.model.pipeline.VertexLighterFlat;
import net.minecraftforge.fml.common.Mod;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Elec332 on 9-1-2020
 */
@Mod.EventBusSubscriber
public class TankTESR extends TileEntityRenderer<TileEntityTank> {

    private static final ThreadLocal<VertexLighterFlat> lighterRef = ThreadLocal.withInitial(() -> new VertexLighterFlat(RenderHelper.getBlockColors()));
    private static boolean forceOpaque = true;

    @Override
    public void render(TileEntityTank tileTank, double x, double y, double z, float partialTicks, int destroyStage) {
        if (!TankRenderer.INSTANCE.hasInitialized()) {
            return;
        }
        RenderHelper.disableStandardItemLighting();

        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.pushMatrix();
        GlStateManager.disableBlend();
        GlStateManager.translated(x, y, z);

        RenderHelper.bindBlockTextures();

        BlockPos pos = tileTank.getPos();
        ITessellator tessellator = RenderHelper.getTessellator();
        final Fluid renderFluid = tileTank.getClientRenderFluid();
        EnumSet<Direction> dirs = Arrays.stream(Direction.values()).filter(dir -> {
            if (dir == Direction.DOWN && !(renderFluid == null || renderFluid == Fluids.EMPTY) && renderFluid.getRenderLayer() == BlockRenderLayer.SOLID) {
                return false; //If there is a fluid being rendered, the bottom doesn't need to be checked if the fluid is opaque
            }
            TileEntity tile = WorldHelper.getTileAt(Preconditions.checkNotNull(tileTank.getWorld()), pos.offset(dir));
            return !(tile instanceof TileEntityTank && ((TileEntityTank) tile).getClientRenderFluid() == renderFluid);
        }).collect(Collectors.toCollection(() -> EnumSet.noneOf(Direction.class)));

        tessellator.startDrawingWorldBlock();
        if (!forceOpaque && !TankModule.quickRender.get()) {
            GlStateManager.enableBlend();
        }
        renderModel(tileTank, tessellator, dirs, true);
        tessellator.draw();

        if (renderFluid != null) {
            if (renderFluid.getRenderLayer() == BlockRenderLayer.TRANSLUCENT && !forceOpaque && !TankModule.quickRender.get()) {
                GlStateManager.enableBlend();
            }
            tessellator.startDrawingWorldBlock();
            renderFluid(tileTank, tessellator, renderFluid, dirs);
            tessellator.draw();
        }

        RenderHelper.enableStandardItemLighting();
        GlStateManager.popMatrix();
    }


    @SuppressWarnings("SameParameterValue")
    private void renderModel(TileEntityTank tileTank, ITessellator tessellator, Set<Direction> dirs, boolean inside) {
        VertexLighterFlat advRenderer = lighterRef.get();
        advRenderer.setParent(tessellator.getVertexBufferConsumer());
        advRenderer.setBlockPos(tileTank.getPos());
        advRenderer.setWorld(Preconditions.checkNotNull(tileTank.getWorld()));
        advRenderer.setState(tileTank.getBlockState());
        advRenderer.updateBlockInfo();
        for (Direction dir : dirs) {
            if (inside) {
                TankRenderer.INSTANCE.getInsideQuad(dir).pipe(advRenderer);
            } else {
                for (BakedQuad quad : TankRenderer.INSTANCE.getModelQuads(dir)) {
                    quad.pipe(advRenderer);
                }
            }
        }
        advRenderer.resetBlockInfo();
    }

    private void renderFluid(TileEntityTank tileTank, ITessellator tessellator, Fluid renderFluid, Set<Direction> dirs) {

        float offset = -0.002f;
        TextureAtlasSprite fluid = RenderHelper.getFluidTexture(renderFluid, false);

        float scale = tileTank.getClientRenderHeight();
        float u1 = fluid.getMinU();
        float v1 = fluid.getMinV();
        float u2 = fluid.getMaxU();
        float v2 = fluid.getMaxV();
        float edge = 2.9f / 16f;

        int color = renderFluid.getAttributes().getColor(getWorld(), tileTank.getPos());
        tessellator.setColorRGBA_I(color, 255);
        tessellator.setBrightness(tileTank.getBlockState().getPackedLightmapCoords(Preconditions.checkNotNull(tileTank.getWorld()), tileTank.getPos()));

        if (scale > 0.0f) {
            //TOP
            tessellator.addVertexWithUV(0, scale, 0, u1, v1);
            tessellator.addVertexWithUV(0, scale, 1, u1, v2);
            tessellator.addVertexWithUV(1, scale, 1, u2, v2);
            tessellator.addVertexWithUV(1, scale, 0, u2, v1);

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

                if (dirs.contains(Direction.EAST)) {
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

                if (dirs.contains(Direction.WEST)) {
                    tessellator.addVertexWithUV(1 + offset, scale, 1 - edge, u1, v1);
                    tessellator.addVertexWithUV(1 + offset, edge, 1 - edge, u1, v2);
                    tessellator.addVertexWithUV(1 + offset, edge, edge, u2, v2);
                    tessellator.addVertexWithUV(1 + offset, scale, edge, u2, v1);
                }
            }
        }
    }

}
