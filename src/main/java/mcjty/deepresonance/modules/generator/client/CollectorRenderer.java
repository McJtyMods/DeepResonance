package mcjty.deepresonance.modules.generator.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
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
    public static final Vec3 START = new Vec3(.5, .8, .5);

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
        double startupFactor = (double) laserStartup / GeneratorConfig.STARTUP_TIME.get();
        for (BlockPos destination : crystals) {
            TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(ClientSetup.LASERBEAMS[random.nextInt(4)]);

            int tex = pos.getX();
            int tey = pos.getY();
            int tez = pos.getZ();
            Vec3 player = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition().add(-tex, -tey, -tez);

            // Crystal coordinates are relative!
            Vec3 end = new Vec3(destination.getX() + .5, destination.getY() + .5, destination.getZ() + .5);

            if (startupFactor > .8) {
                // Do nothing
            } else if (startupFactor > .001) {
                Vec3 middle = new Vec3(jitter(startupFactor, START.x(), end.x()), jitter(startupFactor, START.y(), end.y()), jitter(startupFactor, START.z(), end.z()));
                RenderHelper.drawBeam(matrixStack, builder, sprite, START, middle, player, SETTINGS_LASER);
                RenderHelper.drawBeam(matrixStack, builder, sprite, middle, end, player, SETTINGS_LASER);
            } else {
                RenderHelper.drawBeam(matrixStack, builder, sprite, START, end, player, SETTINGS_LASER);
            }
        }
    }

    private double jitter(double startupFactor, double a1, double a2) {
        return (a1 + a2) / 2.0 + (random.nextDouble() * 2.0 - 1.0) * startupFactor;
    }
}
