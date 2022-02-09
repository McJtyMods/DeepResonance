package mcjty.deepresonance.modules.generator.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import mcjty.deepresonance.modules.generator.GeneratorModule;
import mcjty.deepresonance.modules.generator.block.EnergyCollectorTileEntity;
import mcjty.deepresonance.modules.generator.util.GeneratorConfig;
import mcjty.deepresonance.setup.ClientSetup;
import mcjty.lib.client.CustomRenderTypes;
import mcjty.lib.client.DelayedRenderer;
import mcjty.lib.client.RenderHelper;
import mcjty.lib.client.RenderSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nonnull;
import java.util.Random;
import java.util.Set;

public class CollectorRenderer implements BlockEntityRenderer<EnergyCollectorTileEntity> {

    private static final RenderSettings SETTINGS = RenderSettings.builder()
            .color(255, 0, 0)
            .renderType(CustomRenderTypes.TRANSLUCENT_LIGHTNING_NOLIGHTMAPS)
            .width(.1f)
            .alpha(200)
            .build();
    private static final RenderSettings SETTINGS_LASER = RenderSettings.builder()
            .width(.1f)
            .alpha(128)
            .build();

    private final Random random = new Random();

    public CollectorRenderer(BlockEntityRendererProvider.Context context) {
    }

    public static void register() {
        BlockEntityRenderers.register(GeneratorModule.TYPE_ENERGY_COLLECTOR.get(), CollectorRenderer::new);
    }

    @Override
    public void render(@Nonnull EnergyCollectorTileEntity tileEntity, float partialTicks, @Nonnull PoseStack matrixStack, @Nonnull MultiBufferSource buffer, int combinedLightIn, int combinedOverlayIn) {
        if (tileEntity.getCrystals().isEmpty() || (!tileEntity.areLasersActive() && tileEntity.getLaserStartup() <= 0)) {
            return;
        }

        DelayedRenderer.addRender(RenderType.translucent(), tileEntity.getBlockPos(), (stack, buf) -> {
            renderHalo(stack, buf);
            renderLasers(tileEntity.getBlockPos(), tileEntity.getLaserStartup(), tileEntity.getCrystals(), stack, buf);
        });
    }

    private void renderHalo(PoseStack matrixStack, VertexConsumer buffer) {
        RenderHelper.renderSplitBillboard(matrixStack, buffer, 1.0f, new Vec3(0, .25, 0), ClientSetup.HALO);
    }

    private void renderLasers(BlockPos pos, int laserStartup, Set<BlockPos> crystals, PoseStack matrixStack, VertexConsumer builder) {
        float startupFactor = laserStartup / (float) GeneratorConfig.STARTUP_TIME.get();
        for (BlockPos destination : crystals) {
            TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(ClientSetup.LASERBEAMS[random.nextInt(4)]);

            int tex = pos.getX();
            int tey = pos.getY();
            int tez = pos.getZ();
            Vec3 projectedView = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition().add(-tex, -tey, -tez);

            // Crystal coordinates are relative!
            Vector3f start = new Vector3f(.5f, .8f, .5f);
            Vector3f end = new Vector3f(destination.getX() + .5f, destination.getY() + .5f, destination.getZ() + .5f);
            Vector3f player = new Vector3f((float)projectedView.x, (float)projectedView.y, (float)projectedView.z);

            Matrix4f matrix = matrixStack.last().pose();

            if (startupFactor > .8f) {
                // Do nothing
            } else if (startupFactor > .001f) {
                Vector3f middle = new Vector3f(jitter(startupFactor, start.x(), end.x()), jitter(startupFactor, start.y(), end.y()), jitter(startupFactor, start.z(), end.z()));
                RenderHelper.drawBeam(matrix, builder, sprite, start, middle, player, SETTINGS_LASER);
                RenderHelper.drawBeam(matrix, builder, sprite, middle, end, player, SETTINGS_LASER);
            } else {
                RenderHelper.drawBeam(matrix, builder, sprite, start, end, player, SETTINGS_LASER);
            }
        }
    }

    private float jitter(float startupFactor, float a1, float a2) {
        return (a1 + a2) / 2.0f + (random.nextFloat() * 2.0f - 1.0f) * startupFactor;
    }
}
