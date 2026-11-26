package mcjty.deepresonance.modules.machines.client;

import com.mojang.blaze3d.vertex.PoseStack;
import mcjty.deepresonance.modules.core.CoreModule;
import mcjty.deepresonance.modules.machines.MachinesModule;
import mcjty.deepresonance.modules.machines.block.CrystallizerTileEntity;
import mcjty.lib.client.RenderHelper;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class CrystallizerRenderer implements BlockEntityRenderer<CrystallizerTileEntity, CrystallizerRenderer.State> {

    private ItemStack crystal;

    public static class State extends BlockEntityRenderState {
        int progress;
        float rotation;
    }

    public CrystallizerRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(CrystallizerTileEntity tile, State state, float partialTicks, Vec3 camera, CrumblingOverlay overlay) {
        BlockEntityRenderer.super.extractRenderState(tile, state, partialTicks, camera, overlay);
        state.progress = tile.hasCrystal() ? 100 : tile.getProgress();
        state.rotation = (Math.floorMod(tile.getLevel().getGameTime(), 120) + partialTicks) * 3;
    }

    @Override
    public void submit(State state, PoseStack pose, SubmitNodeCollector collector, CameraRenderState camera) {
        if (state.progress > 0) {
            if (crystal == null) {
                crystal = new ItemStack(CoreModule.RESONATING_CRYSTAL_GENERATED.item().get());
            }
            float scale = 0.75f + 0.45f * (state.progress / 100.0f);
            pose.pushPose();
            pose.translate(0.5, 0.35, 0.5);
            RenderHelper.rotateYP(pose, state.rotation);
            pose.scale(scale, scale, scale);
            RenderHelper.renderItemGround(pose, collector, crystal, state.lightCoords, 0);
            pose.popPose();
        }
    }

    public static void register() {
        BlockEntityRenderers.register(MachinesModule.TYPE_CRYSTALIZER.get(), CrystallizerRenderer::new);
    }
}
