package mcjty.deepresonance.modules.machines.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import mcjty.deepresonance.modules.machines.MachinesModule;
import mcjty.deepresonance.modules.machines.block.LaserTileEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.ClientRegistry;

import javax.annotation.Nonnull;

public class LaserTESR extends TileEntityRenderer<LaserTileEntity> {

    private static final float BEAM_WIDTH = 3.8f;
    private static BakedQuad quad;

    public LaserTESR(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    public static void register() {
        ClientRegistry.bindTileEntityRenderer(MachinesModule.TYPE_LASER.get(), LaserTESR::new);
    }

    @Override
    public void render(@Nonnull LaserTileEntity tileEntityIn, float partialTicks, @Nonnull MatrixStack matrixStackIn, @Nonnull IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
        BlockPos prev = BlockPos.ZERO;
        if (tileEntityIn.getActiveBonus().isEmpty()) {
            return;
        }
        int color = tileEntityIn.getActiveBonus().getColor();
        for (BlockPos pos : tileEntityIn.getLaserBeam()) {
            BlockPos p = pos.subtract(tileEntityIn.getBlockPos());
            drawBeamPart(prev, p, color, tileEntityIn.getLevel(), matrixStackIn, bufferIn.getBuffer(RenderType.translucent()));
            prev = p;
        }
    }

    private void drawBeamPart(BlockPos from, BlockPos to, int color, World world, @Nonnull MatrixStack matrix, IVertexBuilder buffer) {
        matrix.pushPose();
        BlockPos diff = from.subtract(to);

        matrix.translate(from.getX() + 0.5, from.getY() + 0.5, from.getZ() + 0.5);
        Direction dir = Direction.fromNormal(diff.getX(), diff.getY(), diff.getZ());
        if (dir == null) {
            return;
        }

        matrix.mulPose(dir.getRotation());
        matrix.mulPose(Vector3f.XN.rotationDegrees(90));

        matrix.translate(0, 0, -0.5);

        int r = color >> 16 & 255;
        int g = color >> 8 & 255;
        int b = color & 255;

        float f = Math.floorMod(world.getGameTime(), 40L);
        matrix.mulPose(Vector3f.ZN.rotationDegrees(f * 2.25f));

        for (int i = 0; i < 4; i++) {
            matrix.pushPose();
            matrix.mulPose(Vector3f.ZN.rotationDegrees(i * 90));
            buffer.addVertexData(matrix.last(), quad, r, g, b, 15728880, OverlayTexture.NO_OVERLAY, true);
            matrix.popPose();
        }
        matrix.popPose();
    }

    // @todo 1.16
//    static {
//        RenderingRegistry.instance().registerLoader(new IModelAndTextureLoader() {
//
//            private TextureAtlasSprite tex;
//
//            @Override
//            public void registerTextures(IIconRegistrar iconRegistrar) {
//                tex = iconRegistrar.registerSprite(new ResourceLocation(DeepResonance.MODID, "effects/laserbeam"));
//            }
//
//            @Override
//            public void registerModels(IQuadBakery quadBakery, IModelBakery modelBakery, ITemplateBakery templateBakery) {
//                IForgeTransformationMatrix m = new TransformationMatrix(new Vector3f(0, 0, -1), null, null, null);
//                LaserTESR.quad = quadBakery.bakeQuad(new Vector3f(-BEAM_WIDTH / 2, BEAM_WIDTH / 2, 0), new Vector3f(BEAM_WIDTH / 2, BEAM_WIDTH / 2, 16), tex, Direction.UP, m);
//            }
//
//        });
//    }

}
