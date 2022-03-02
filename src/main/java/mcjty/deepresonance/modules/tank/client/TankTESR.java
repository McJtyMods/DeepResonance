package mcjty.deepresonance.modules.tank.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.modules.tank.TankModule;
import mcjty.deepresonance.modules.tank.blocks.TankTileEntity;
import mcjty.lib.client.RenderHelper;
import mcjty.lib.client.RenderSettings;
import mcjty.lib.varia.OrientationTools;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;

public class TankTESR implements BlockEntityRenderer<TankTileEntity> {

    public static final ResourceLocation TANK_BOTTOM = new ResourceLocation(DeepResonance.MODID, "block/tank_bottom");
    public static final ResourceLocation TANK_TOP = new ResourceLocation(DeepResonance.MODID, "block/tank_top");
    public static final ResourceLocation TANK_SIDE = new ResourceLocation(DeepResonance.MODID, "block/tank_side");

    public TankTESR(BlockEntityRendererProvider.Context context) {
    }

    public static void register() {
        BlockEntityRenderers.register(TankModule.TYPE_TANK.get(), TankTESR::new);
    }

    @Override
    public void render(@Nonnull TankTileEntity tileTank, float partialTicks, @Nonnull PoseStack matrixStackIn, @Nonnull MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        renderInternal(tileTank, matrixStackIn, bufferIn, combinedLightIn);
    }

    public static void renderInternal(TankTileEntity tileTank, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn) {
        BlockPos pos = tileTank.getBlockPos();
        final Fluid fluidToRender = tileTank.getClientRenderFluid();

        Set<Direction> dirs = Arrays.stream(OrientationTools.DIRECTION_VALUES).filter(dir -> {
            if (dir == Direction.DOWN && tileTank.getClientRenderHeight() > 0.0001 && !(fluidToRender == Fluids.EMPTY) && ItemBlockRenderTypes.canRenderInLayer(fluidToRender.defaultFluidState(), RenderType.solid())) {
                return false; //If there is a fluid being rendered, the bottom doesn't need to be checked if the fluid is opaque
            }
            BlockEntity tile = tileTank.getLevel().getBlockEntity(pos.relative(dir));
            return !(tile instanceof TankTileEntity && ((TankTileEntity) tile).getClientRenderFluid() == fluidToRender);
        }).collect(Collectors.toCollection(() -> EnumSet.noneOf(Direction.class)));

        float scale = tileTank.getClientRenderHeight();
        if (fluidToRender != Fluids.EMPTY) {
            int color = fluidToRender.getAttributes().getColor(tileTank.getLevel(), tileTank.getBlockPos());
            int luminosity = fluidToRender.getAttributes().getLuminosity();
//            int block = LightTexture.block(combinedLightIn);
            int packed = LightTexture.pack(Math.max(luminosity, combinedLightIn), 0);
            render(matrixStackIn, bufferIn, fluidToRender, dirs, combinedLightIn, packed, scale, color);
        }
    }

//    public static void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, Fluid renderFluid, float height, int brightness) {
//        int color = 0;
//        if (renderFluid != null) {
//            color = renderFluid.getAttributes().getColor();
//        }
//        render(matrixStackIn, bufferIn, renderFluid, ITEM_DIRECTIONS, brightness, height, color);
//    }

    private static void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, Fluid renderFluid, Set<Direction> dirs, int combinedLightIn, int liquidBrightness, float scale, int color) {
        matrixStackIn.pushPose();

        renderModel(matrixStackIn, bufferIn.getBuffer(RenderType.translucent()), dirs, combinedLightIn);

        if (renderFluid != null) {
            for (RenderType renderType : RenderType.chunkBufferLayers()) {
                if (ItemBlockRenderTypes.canRenderInLayer(renderFluid.defaultFluidState(), renderType)) {
                    renderFluid(scale, color, bufferIn.getBuffer(renderType), renderFluid, dirs, liquidBrightness, matrixStackIn);
                }
            }
        }

        matrixStackIn.popPose();
    }

    private static void renderModel(PoseStack matrixStack, VertexConsumer vertexBuilder, Set<Direction> dirs, int brightness) {
        Matrix4f matrix = matrixStack.last().pose();

        if (dirs.contains(Direction.UP)) {
            TextureAtlasSprite tank_top = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(TANK_TOP);
            RenderHelper.drawQuad(matrix, vertexBuilder, tank_top, Direction.UP, true, 0.1f, RenderSettings.builder().brightness(brightness).build());
        }
        if (dirs.contains(Direction.DOWN)) {
            TextureAtlasSprite tank_bottom = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(TANK_BOTTOM);
            RenderHelper.drawQuad(matrix, vertexBuilder, tank_bottom, Direction.DOWN, true, 0.1f, RenderSettings.builder().brightness(brightness).build());
        }
        TextureAtlasSprite tank_side = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(TANK_SIDE);
        for (Direction dir : OrientationTools.HORIZONTAL_DIRECTION_VALUES) {
            RenderHelper.drawQuad(matrix, vertexBuilder, tank_side, dir, true, 0.1f, RenderSettings.builder().brightness(brightness).build());
        }
    }

    private static void renderFluid(float scale, int color, VertexConsumer vertexBuilder, Fluid fluidToRender, Set<Direction> dirs, int brightness, PoseStack matrixStack) {

        float offset = -0.002f;
        TextureAtlasSprite fluid = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(fluidToRender.getAttributes().getStillTexture());

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
