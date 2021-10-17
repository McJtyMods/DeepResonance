package mcjty.deepresonance.modules.generator.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import mcjty.deepresonance.modules.generator.GeneratorModule;
import mcjty.deepresonance.modules.generator.block.EnergyCollectorTileEntity;
import mcjty.deepresonance.setup.ClientSetup;
import mcjty.lib.client.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.fml.client.registry.ClientRegistry;

import javax.annotation.Nonnull;
import java.util.Random;

public class CollectorTESR extends TileEntityRenderer<EnergyCollectorTileEntity> {

    private static final double SIZE = 0.6;
    private Random random = new Random();

    public CollectorTESR(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    public static void register() {
        ClientRegistry.bindTileEntityRenderer(GeneratorModule.TYPE_ENERGY_COLLECTOR.get(), CollectorTESR::new);
    }

    @Override
    public void render(@Nonnull EnergyCollectorTileEntity tileEntity, float partialTicks, @Nonnull MatrixStack matrixStack, @Nonnull IRenderTypeBuffer buffer, int combinedLightIn, int combinedOverlayIn) {
        if (tileEntity.getCrystals().isEmpty() || (!tileEntity.areLasersActive() || tileEntity.getLaserStartup() <= 0)) {
            return;
        }

        matrixStack.pushPose();
        matrixStack.translate(.5f, .85f, .5f);
        RenderHelper.renderBillboardQuadBright(matrixStack, buffer, 1.0f, ClientSetup.HALO);// + random.nextFloat() * .05f);
        matrixStack.popPose();


        matrixStack.pushPose();
        for (BlockPos destination : tileEntity.getCrystals()) {
            TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(AtlasTexture.LOCATION_BLOCKS).apply(ClientSetup.LASERBEAM);

            IVertexBuilder builder = buffer.getBuffer(RenderType.translucent());

            int tex = tileEntity.getBlockPos().getX();
            int tey = tileEntity.getBlockPos().getY();
            int tez = tileEntity.getBlockPos().getZ();
            Vector3d projectedView = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition().add(-tex, -tey, -tez);

            RenderHelper.Vector start = new RenderHelper.Vector(.5f, .5f, .5f);
            RenderHelper.Vector end = new RenderHelper.Vector(destination.getX() - tex + .5f, destination.getY() - tey + .5f, destination.getZ() - tez + .5f);
            RenderHelper.Vector player = new RenderHelper.Vector((float)projectedView.x, (float)projectedView.y, (float)projectedView.z);

            Matrix4f matrix = matrixStack.last().pose();
            RenderHelper.drawBeam(matrix, builder, sprite, start, end, player, .1f);
        }
        matrixStack.popPose();


//        buffer = buffer_.getBuffer(CRYSTAL_HALO);
//        tessellator = RenderHelper.forWorldRenderer(buffer);
//        for (BlockPos pos : tileEntity.getCrystals()) {
//            matrixStack.pushPose();
//            tessellator.setTransformation(matrixStack);
//            matrixStack.translate(pos.getX(), pos.getY(), pos.getZ());
//            RenderHelper.facingPlayer(matrixStack);
//            tessellator.addVertexWithUV(-SIZE, -SIZE, 0, 0, 0);
//            tessellator.addVertexWithUV(-SIZE, SIZE, 0, 0, 1);
//            tessellator.addVertexWithUV(SIZE, SIZE, 0, 1, 1);
//            tessellator.addVertexWithUV(SIZE, -SIZE, 0, 1, 0);
//
//            //todo: beam
//            matrixStack.pop();
//        }
    }

    private float jitter(float startupFactor, float a1, float a2) {
        return (a1 + a2) / 2.0f + (random.nextFloat() * 2.0f - 1.0f) * startupFactor;
    }
}
