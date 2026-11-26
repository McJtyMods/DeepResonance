package mcjty.deepresonance.modules.core.client;

import com.mojang.blaze3d.vertex.PoseStack;
import mcjty.deepresonance.modules.core.CoreModule;
import mcjty.deepresonance.modules.core.block.ResonatingCrystalTileEntity;
import mcjty.deepresonance.setup.ClientSetup;
import mcjty.lib.client.AbstractDynamicBakedModel;
import mcjty.lib.client.CustomRenderTypes;
import mcjty.lib.client.RenderHelper;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;

public class ResonatingCrystalRenderer implements BlockEntityRenderer<ResonatingCrystalTileEntity, ResonatingCrystalRenderer.State> {

    public static class State extends BlockEntityRenderState {
        boolean glowing;
    }

    public ResonatingCrystalRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(ResonatingCrystalTileEntity tile, State state, float partialTicks, Vec3 camera, CrumblingOverlay overlay) {
        BlockEntityRenderer.super.extractRenderState(tile, state, partialTicks, camera, overlay);
        state.glowing = tile.isGlowing();
    }

    @Override
    public void submit(State state, PoseStack pose, SubmitNodeCollector collector, CameraRenderState camera) {
        if (state.glowing) {
            submitHalo(pose, collector, camera, ClientSetup.REDHALO, 0.5f, 0.6f);
        }
    }

    public static void submitHalo(PoseStack pose, SubmitNodeCollector collector, CameraRenderState camera, Identifier texture, float y, float size) {
        var sprite = AbstractDynamicBakedModel.getTexture(texture);
        float radius = size / 2;
        pose.pushPose();
        pose.translate(0.5f, y, 0.5f);
        pose.rotate(camera.orientation);
        collector.submitCustomGeometry(pose, CustomRenderTypes.translucent(), (captured, vertices) -> {
            var matrix = captured.pose();
            int light = RenderHelper.MAX_BRIGHTNESS;
            vertices.addVertex(matrix, -radius, -radius, 0).setColor(-1).setUv(sprite.getU0(), sprite.getV1()).setOverlay(0).setLight(light).setNormal(0, 0, 1);
            vertices.addVertex(matrix, radius, -radius, 0).setColor(-1).setUv(sprite.getU1(), sprite.getV1()).setOverlay(0).setLight(light).setNormal(0, 0, 1);
            vertices.addVertex(matrix, radius, radius, 0).setColor(-1).setUv(sprite.getU1(), sprite.getV0()).setOverlay(0).setLight(light).setNormal(0, 0, 1);
            vertices.addVertex(matrix, -radius, radius, 0).setColor(-1).setUv(sprite.getU0(), sprite.getV0()).setOverlay(0).setLight(light).setNormal(0, 0, 1);
        });
        pose.popPose();
    }

    public static void register() {
        BlockEntityRenderers.register(CoreModule.TYPE_RESONATING_CRYSTAL.get(), ResonatingCrystalRenderer::new);
    }
}
