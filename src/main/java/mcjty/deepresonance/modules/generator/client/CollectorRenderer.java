package mcjty.deepresonance.modules.generator.client;

import mcjty.deepresonance.modules.core.client.ResonatingCrystalRenderer;
import mcjty.lib.client.AbstractDynamicBakedModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mcjty.deepresonance.modules.generator.GeneratorModule;
import mcjty.deepresonance.modules.generator.block.EnergyCollectorTileEntity;
import mcjty.deepresonance.modules.generator.util.GeneratorConfig;
import mcjty.deepresonance.setup.ClientSetup;
import mcjty.lib.client.CustomRenderTypes;
import mcjty.lib.client.RenderHelper;
import mcjty.lib.client.RenderSettings;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Random;
import java.util.Set;

public class CollectorRenderer implements BlockEntityRenderer<EnergyCollectorTileEntity, CollectorRenderer.State> {

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

    public static class State extends BlockEntityRenderState {
        Set<BlockPos> crystals = Set.of();
        int startup;
    }

    @Override
    public boolean shouldRenderOffScreen() {
        return true;
    }

    @Override
    public boolean shouldRender(EnergyCollectorTileEntity tile, Vec3 cameraPosition) {
        return (tile.areLasersActive() || tile.getLaserStartup() > 0)
                && !tile.getCrystals().isEmpty()
                && BlockEntityRenderer.super.shouldRender(tile, cameraPosition);
    }

    @Override
    public AABB getRenderBoundingBox(EnergyCollectorTileEntity tile) {
        BlockPos origin = tile.getBlockPos();
        AABB bounds = new AABB(origin);
        for (BlockPos crystal : tile.getCrystals()) {
            bounds = bounds.minmax(new AABB(origin.offset(crystal)));
        }
        return bounds.inflate(1);
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(EnergyCollectorTileEntity tile, State state, float partialTicks, Vec3 camera, CrumblingOverlay overlay) {
        BlockEntityRenderer.super.extractRenderState(tile, state, partialTicks, camera, overlay);
        state.startup = tile.getLaserStartup();
        state.crystals = tile.areLasersActive() || state.startup > 0 ? Set.copyOf(tile.getCrystals()) : Set.of();
    }

    @Override
    public void submit(State state, PoseStack pose, SubmitNodeCollector collector, CameraRenderState camera) {
        if (!state.crystals.isEmpty()) {
            ResonatingCrystalRenderer.submitHalo(pose, collector, camera, ClientSetup.HALO, 0.75f, 1.0f);
            Vec3 eye = camera.pos.subtract(Vec3.atLowerCornerOf(state.blockPos));
            collector.submitCustomGeometry(pose, CustomRenderTypes.translucent(), (captured, vertices) -> {
                PoseStack copy = new PoseStack();
                copy.last().set(captured);
                renderLasers(eye, state.startup, state.crystals, copy, vertices);
            });
        }
    }

    private void renderLasers(Vec3 player, int laserStartup, Set<BlockPos> crystals, PoseStack matrixStack, VertexConsumer builder) {
        double startupFactor = (double) laserStartup / GeneratorConfig.STARTUP_TIME.get();
        for (BlockPos destination : crystals) {
            TextureAtlasSprite sprite = AbstractDynamicBakedModel.getTexture(ClientSetup.LASERBEAMS[random.nextInt(ClientSetup.LASERBEAMS.length)]);

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
