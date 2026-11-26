package mcjty.deepresonance.modules.machines.client;

import com.mojang.blaze3d.vertex.PoseStack;
import mcjty.deepresonance.modules.machines.MachinesModule;
import mcjty.deepresonance.modules.machines.block.LaserTileEntity;
import mcjty.deepresonance.modules.machines.data.InfusionBonusRegistry;
import mcjty.deepresonance.setup.ClientSetup;
import mcjty.lib.client.AbstractDynamicBakedModel;
import mcjty.lib.client.CustomRenderTypes;
import mcjty.lib.client.RenderHelper;
import mcjty.lib.client.RenderSettings;
import mcjty.lib.varia.OrientationTools;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class LaserRenderer implements BlockEntityRenderer<LaserTileEntity, LaserRenderer.State> {

    private static final Vec3 START = new Vec3(0.5, 0.5, 0.5);

    private static final RenderSettings BEAM = RenderSettings.builder().width(0.25f).alpha(128).build();

    public static class State extends BlockEntityRenderState {
        TextureAtlasSprite sprite;
        Vec3 end;
    }

    public LaserRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(LaserTileEntity tile, State state, float partialTicks, Vec3 camera, CrumblingOverlay overlay) {
        BlockEntityRenderer.super.extractRenderState(tile, state, partialTicks, camera, overlay);
        Identifier texture = switch (tile.getBlockState().getValue(LaserTileEntity.COLOR)) {
            case InfusionBonusRegistry.COLOR_BLUE -> ClientSetup.BLUELASER;
            case InfusionBonusRegistry.COLOR_RED -> ClientSetup.REDLASER;
            case InfusionBonusRegistry.COLOR_GREEN -> ClientSetup.GREENLASER;
            case InfusionBonusRegistry.COLOR_YELLOW -> ClientSetup.YELLOWLASER;
            default -> null;
        };
        state.sprite = texture == null ? null : AbstractDynamicBakedModel.getTexture(texture);
        Direction direction = OrientationTools.getOrientationHoriz(tile.getBlockState());
        state.end = START.add(direction.getStepX() * 2.5, 0, direction.getStepZ() * 2.5);
    }

    @Override
    public void submit(State state, PoseStack pose, SubmitNodeCollector collector, CameraRenderState camera) {
        if (state.sprite != null) {
            Vec3 eye = camera.pos.subtract(Vec3.atLowerCornerOf(state.blockPos));
            collector.submitCustomGeometry(pose, CustomRenderTypes.translucent(), (captured, vertices) -> {
                PoseStack copy = new PoseStack();
                copy.last().set(captured);
                RenderHelper.drawBeam(copy, vertices, state.sprite, START, state.end, eye, BEAM);
            });
        }
    }

    @Override
    public AABB getRenderBoundingBox(LaserTileEntity tile) {
        return new AABB(tile.getBlockPos()).inflate(3);
    }

    public static void register() {
        BlockEntityRenderers.register(MachinesModule.TYPE_LASER.get(), LaserRenderer::new);
    }
}
