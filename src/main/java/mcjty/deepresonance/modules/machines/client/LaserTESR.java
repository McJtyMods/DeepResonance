package mcjty.deepresonance.modules.machines.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import elec332.core.api.annotations.StaticLoad;
import elec332.core.api.client.IIconRegistrar;
import elec332.core.api.client.model.IElecModelBakery;
import elec332.core.api.client.model.IElecQuadBakery;
import elec332.core.api.client.model.IElecTemplateBakery;
import elec332.core.api.client.model.IModelAndTextureLoader;
import elec332.core.client.util.AbstractTileEntityRenderer;
import elec332.core.loader.client.RenderingRegistry;
import mcjty.deepresonance.modules.machines.tile.TileEntityLaser;
import mcjty.deepresonance.util.DeepResonanceResourceLocation;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.TransformationMatrix;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.extensions.IForgeTransformationMatrix;

import javax.annotation.Nonnull;

/**
 * Created by Elec332 on 29-7-2020
 */
@StaticLoad
public class LaserTESR extends AbstractTileEntityRenderer<TileEntityLaser> {

    private static final float BEAM_WIDTH = 3.8f;
    private static BakedQuad quad;

    @Override
    public void render(@Nonnull TileEntityLaser tileEntityIn, float partialTicks, @Nonnull MatrixStack matrixStackIn, @Nonnull IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
        BlockPos prev = BlockPos.ZERO;
        if (tileEntityIn.getActiveBonus().isEmpty()) {
            return;
        }
        int color = tileEntityIn.getActiveBonus().getColor();
        for (BlockPos pos : tileEntityIn.getLaserBeam()) {
            pos = pos.subtract(tileEntityIn.getPos());
            drawBeamPart(prev, pos, color, tileEntityIn.getWorld(), matrixStackIn, bufferIn.getBuffer(RenderType.getTranslucent()));
            prev = pos;
        }
    }

    private void drawBeamPart(BlockPos from, BlockPos to, int color, World world, @Nonnull MatrixStack matrix, IVertexBuilder buffer) {
        matrix.push();
        BlockPos diff = from.subtract(to);

        matrix.translate(from.getX() + 0.5, from.getY() + 0.5, from.getZ() + 0.5);
        Direction dir = Direction.byLong(diff.getX(), diff.getY(), diff.getZ());
        if (dir == null) {
            return;
        }

        matrix.rotate(dir.getRotation());
        matrix.rotate(Vector3f.XN.rotationDegrees(90));

        matrix.translate(0, 0, -0.5);

        int r = color >> 16 & 255;
        int g = color >> 8 & 255;
        int b = color & 255;

        float f = (float) Math.floorMod(world.getGameTime(), 40L);
        matrix.rotate(Vector3f.ZN.rotationDegrees(f * 2.25f));

        for (int i = 0; i < 4; i++) {
            matrix.push();
            matrix.rotate(Vector3f.ZN.rotationDegrees(i * 90));
            buffer.addVertexData(matrix.getLast(), quad, r, g, b, 15728880, OverlayTexture.NO_OVERLAY, true);
            matrix.pop();
        }
        matrix.pop();
    }

    static {
        RenderingRegistry.instance().registerLoader(new IModelAndTextureLoader() {

            private TextureAtlasSprite tex;

            @Override
            public void registerTextures(IIconRegistrar iconRegistrar) {
                tex = iconRegistrar.registerSprite(new DeepResonanceResourceLocation("effects/laserbeam"));
            }

            @Override
            public void registerModels(IElecQuadBakery quadBakery, IElecModelBakery modelBakery, IElecTemplateBakery templateBakery) {
                IForgeTransformationMatrix m = new TransformationMatrix(new Vector3f(0, 0, -1), null, null, null);
                LaserTESR.quad = quadBakery.bakeQuad(new Vector3f(-BEAM_WIDTH / 2, BEAM_WIDTH / 2, 0), new Vector3f(BEAM_WIDTH / 2, BEAM_WIDTH / 2, 16), tex, Direction.UP, m);
            }

        });
    }

}
