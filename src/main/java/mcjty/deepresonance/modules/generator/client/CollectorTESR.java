package mcjty.deepresonance.modules.generator.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import mcjty.deepresonance.modules.generator.GeneratorModule;
import mcjty.deepresonance.modules.generator.block.EnergyCollectorTileEntity;
import mcjty.deepresonance.modules.generator.util.GeneratorConfig;
import mcjty.deepresonance.setup.ClientSetup;
import mcjty.lib.client.CustomRenderTypes;
import mcjty.lib.client.RenderHelper;
import mcjty.lib.client.RenderSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
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
        if (tileEntity.getCrystals().isEmpty() || (!tileEntity.areLasersActive() && tileEntity.getLaserStartup() <= 0)) {
            return;
        }

        matrixStack.pushPose();
        matrixStack.translate(0f, .25f, .0f);
        // @todo 1.16 optimize render settings in a final!
        RenderSettings settings = RenderSettings.builder()
                .color(255, 0, 0)
                .renderType(CustomRenderTypes.TRANSLUCENT_LIGHTNING_NOLIGHTMAPS)
                .width(.1f)
                .alpha(200)
                .build();
        RenderSettings settingsLaser = RenderSettings.builder()
                .width(.1f)
                .alpha(128)
                .build();
        RenderHelper.renderBillboardQuadBright(matrixStack, buffer, 1.0f, ClientSetup.HALO, settings);// + random.nextFloat() * .05f);
        matrixStack.popPose();

        float startupFactor = tileEntity.getLaserStartup() / (float) GeneratorConfig.STARTUP_TIME.get();

        matrixStack.pushPose();
        for (BlockPos destination : tileEntity.getCrystals()) {
            TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(AtlasTexture.LOCATION_BLOCKS).apply(ClientSetup.LASERBEAMS[random.nextInt(4)]);

            IVertexBuilder builder = buffer.getBuffer(CustomRenderTypes.TRANSLUCENT_ADD_NOLIGHTMAPS);

            int tex = tileEntity.getBlockPos().getX();
            int tey = tileEntity.getBlockPos().getY();
            int tez = tileEntity.getBlockPos().getZ();
            Vector3d projectedView = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition().add(-tex, -tey, -tez);

            // Crystal coordinates are relative!
            Vector3f start = new Vector3f(.5f, .8f, .5f);
            Vector3f end = new Vector3f(destination.getX() + .5f, destination.getY() + .5f, destination.getZ() + .5f);
            Vector3f player = new Vector3f((float)projectedView.x, (float)projectedView.y, (float)projectedView.z);

            Matrix4f matrix = matrixStack.last().pose();

            if (startupFactor > .8f) {
                // Do nothing
            } else if (startupFactor > .001f) {
                Vector3f middle = new Vector3f(jitter(startupFactor, start.x(), end.x()), jitter(startupFactor, start.y(), end.y()), jitter(startupFactor, start.z(), end.z()));
                RenderHelper.drawBeam(matrix, builder, sprite, start, middle, player, settingsLaser);
                RenderHelper.drawBeam(matrix, builder, sprite, middle, end, player, settingsLaser);
            } else {
                RenderHelper.drawBeam(matrix, builder, sprite, start, end, player, settingsLaser);
            }
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
