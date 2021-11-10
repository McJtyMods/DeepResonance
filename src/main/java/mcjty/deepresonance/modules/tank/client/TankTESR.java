package mcjty.deepresonance.modules.tank.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import mcjty.deepresonance.modules.tank.TankModule;
import mcjty.deepresonance.modules.tank.blocks.TankTileEntity;
import mcjty.lib.client.RenderHelper;
import mcjty.lib.varia.OrientationTools;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.client.model.pipeline.VertexBufferConsumer;
import net.minecraftforge.fml.client.registry.ClientRegistry;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;

public class TankTESR extends TileEntityRenderer<TankTileEntity> {

    private static final Set<Direction> ITEM_DIRECTIONS = EnumSet.range(Direction.UP, Direction.EAST); //All except bottom

    public TankTESR(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    public static void register() {
        ClientRegistry.bindTileEntityRenderer(TankModule.TYPE_TANK.get(), TankTESR::new);
    }

    private static final Vector3d UP = new Vector3d(0, 1, 0);
    private static final Vector3d DOWN = new Vector3d(0, -1, 0);
    private static final Vector3d NORTH = new Vector3d(0, 0, -1);
    private static final Vector3d SOUTH = new Vector3d(0, 0, 1);
    private static final Vector3d WEST = new Vector3d(-1, 0, 0);
    private static final Vector3d EAST = new Vector3d(1, 0, 0);

    @Override
    public void render(@Nonnull TankTileEntity tileTank, float partialTicks, @Nonnull MatrixStack matrixStackIn, @Nonnull IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
        BlockPos pos = tileTank.getBlockPos();
        final Fluid fluidToRender = tileTank.getClientRenderFluid();


        Set<Direction> dirs = Arrays.stream(OrientationTools.DIRECTION_VALUES).filter(dir -> {
            if (dir == Direction.DOWN && tileTank.getClientRenderHeight() > 0.0001 && !(fluidToRender == null || fluidToRender == Fluids.EMPTY) && RenderTypeLookup.canRenderInLayer(fluidToRender.defaultFluidState(), RenderType.solid())) {
                return false; //If there is a fluid being rendered, the bottom doesn't need to be checked if the fluid is opaque
            }
            TileEntity tile = tileTank.getLevel().getBlockEntity(pos.relative(dir));
            return !(tile instanceof TankTileEntity && ((TankTileEntity) tile).getClientRenderFluid() == fluidToRender);
        }).collect(Collectors.toCollection(() -> EnumSet.noneOf(Direction.class)));

        float scale = tileTank.getClientRenderHeight();
        if (fluidToRender != null) {
            int color = fluidToRender.getAttributes().getColor(tileTank.getLevel(), tileTank.getBlockPos());
            render(matrixStackIn, bufferIn, fluidToRender, dirs, combinedLightIn, scale, color);
        }
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

//        renderModel(matrixStackIn, bufferIn.getBuffer(RenderType.translucent()), dirs, brightness);

        if (renderFluid != null) {
            for (RenderType renderType : RenderType.chunkBufferLayers()) {
                if (RenderTypeLookup.canRenderInLayer(renderFluid.defaultFluidState(), renderType)) {
                    renderFluid(scale, color, bufferIn.getBuffer(renderType), renderFluid, dirs, brightness, matrixStackIn);
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

    private static void renderFluid(float scale, int color, IVertexBuilder vertexBuilder, Fluid fluidToRender, Set<Direction> dirs, int brightness, MatrixStack matrixStack) {

        float offset = -0.002f;
        TextureAtlasSprite fluid = Minecraft.getInstance().getTextureAtlas(AtlasTexture.LOCATION_BLOCKS).apply(fluidToRender.getAttributes().getStillTexture());

        float u1 = fluid.getU0();
        float v1 = fluid.getV0();
        float u2 = fluid.getU1();
        float v2 = fluid.getV1();
        float edge = 2.9f / 16f;

        int b1 = brightness >> 16 & 65535;
        int b2 = brightness & 65535;

//        vertexBuilder.setColorRGBA_I(color, 255);
//        vertexBuilder.setBrightness(brightness);
//        vertexBuilder.setMatrix(matrixStack.getLast().getMatrix());
        float r = (color >> 16 & 0xFF) / 255.0F;
        float g = (color >> 8 & 0xFF) / 255.0F;
        float b = (color & 0xFF) / 255.0F;
        float a = (color >> 24 & 0xFF) / 255.0F;

        Matrix4f matrix = matrixStack.last().pose();

        if (scale > 0.0f) {
            //TOP
            RenderHelper.vt(vertexBuilder, matrix, 0, scale + offset, 0, u1, v1, b1, b2, r, g, b, a);
            RenderHelper.vt(vertexBuilder, matrix, 0, scale + offset, 1, u1, v2, b1, b2, r, g, b, a);
            RenderHelper.vt(vertexBuilder, matrix, 1, scale + offset, 1, u2, v2, b1, b2, r, g, b, a);
            RenderHelper.vt(vertexBuilder, matrix, 1, scale + offset, 0, u2, v1, b1, b2, r, g, b, a);

            if (scale > edge) {
                if (scale > 1 - edge) {
                    scale = 1 - edge;
                }

                v2 -= (fluid.getV1() - fluid.getV0()) * (1 - scale);

                if (dirs.contains(Direction.NORTH)) {
                    RenderHelper.vt(vertexBuilder, matrix, 1 - edge, scale, -offset, u1, v1, b1, b2, r, g, b, a);
                    RenderHelper.vt(vertexBuilder, matrix, 1 - edge, edge, -offset, u1, v2, b1, b2, r, g, b, a);
                    RenderHelper.vt(vertexBuilder, matrix, edge, edge, -offset, u2, v2, b1, b2, r, g, b, a);
                    RenderHelper.vt(vertexBuilder, matrix, edge, scale, -offset, u2, v1, b1, b2, r, g, b, a);
                }

                if (dirs.contains(Direction.WEST)) {
                    RenderHelper.vt(vertexBuilder, matrix, -offset, edge, 1 - edge, u1, v2, b1, b2, r, g, b, a);
                    RenderHelper.vt(vertexBuilder, matrix, -offset, scale, 1 - edge, u1, v1, b1, b2, r, g, b, a);
                    RenderHelper.vt(vertexBuilder, matrix, -offset, scale, edge, u2, v1, b1, b2, r, g, b, a);
                    RenderHelper.vt(vertexBuilder, matrix, -offset, edge, edge, u2, v2, b1, b2, r, g, b, a);
                }

                if (dirs.contains(Direction.SOUTH)) {
                    RenderHelper.vt(vertexBuilder, matrix, 1 - edge, edge, 1 + offset, u1, v2, b1, b2, r, g, b, a);
                    RenderHelper.vt(vertexBuilder, matrix, 1 - edge, scale, 1 + offset, u1, v1, b1, b2, r, g, b, a);
                    RenderHelper.vt(vertexBuilder, matrix, edge, scale, 1 + offset, u2, v1, b1, b2, r, g, b, a);
                    RenderHelper.vt(vertexBuilder, matrix, edge, edge, 1 + offset, u2, v2, b1, b2, r, g, b, a);
                }

                if (dirs.contains(Direction.EAST)) {
                    RenderHelper.vt(vertexBuilder, matrix, 1 + offset, scale, 1 - edge, u1, v1, b1, b2, r, g, b, a);
                    RenderHelper.vt(vertexBuilder, matrix, 1 + offset, edge, 1 - edge, u1, v2, b1, b2, r, g, b, a);
                    RenderHelper.vt(vertexBuilder, matrix, 1 + offset, edge, edge, u2, v2, b1, b2, r, g, b, a);
                    RenderHelper.vt(vertexBuilder, matrix, 1 + offset, scale, edge, u2, v1, b1, b2, r, g, b, a);
                }
            }
        }
    }

}
