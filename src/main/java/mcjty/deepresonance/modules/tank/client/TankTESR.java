package mcjty.deepresonance.modules.tank.client;

import com.mojang.blaze3d.vertex.PoseStack;
import mcjty.deepresonance.modules.core.CoreModule;
import mcjty.deepresonance.modules.core.fluid.FluidLiquidCrystal;
import mcjty.deepresonance.modules.tank.TankModule;
import mcjty.deepresonance.modules.tank.blocks.TankTileEntity;
import mcjty.lib.client.CustomRenderTypes;
import mcjty.lib.client.RenderHelper;
import mcjty.lib.client.RenderSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Direction;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RegisterFluidModelsEvent;
import net.neoforged.neoforge.client.fluid.FluidTintSource;

import java.util.EnumSet;

public class TankTESR implements BlockEntityRenderer<TankTileEntity, TankTESR.State> {

    public static class State extends BlockEntityRenderState {
        TextureAtlasSprite sprite;
        int color;
        float height;
        final EnumSet<Direction> sides = EnumSet.noneOf(Direction.class);
    }

    public TankTESR(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(TankTileEntity tank, State state, float partialTicks, Vec3 camera, CrumblingOverlay overlay) {
        BlockEntityRenderer.super.extractRenderState(tank, state, partialTicks, camera, overlay);
        state.height = tank.getClientRenderHeight();
        state.sprite = null;
        state.sides.clear();
        var fluid = tank.getClientRenderFluid();
        if (fluid == Fluids.EMPTY || state.height <= 0) {
            return;
        }
        var fluidState = fluid.defaultFluidState();
        var model = Minecraft.getInstance().getModelManager().getFluidStateModelSet().get(fluidState);
        state.sprite = model.stillMaterial().sprite();
        var tint = model.fluidTintSource();
        state.color = tint == null ? -1 : tint.colorInWorld(fluidState, tank.getBlockState(), Minecraft.getInstance().level, tank.getBlockPos());
        for (Direction side : Direction.values()) {
            if (!(tank.getLevel().getBlockEntity(tank.getBlockPos().relative(side)) instanceof TankTileEntity neighbor)
                    || neighbor.getClientRenderFluid() != fluid || neighbor.getClientRenderHeight() < state.height) {
                state.sides.add(side);
            }
        }
    }

    @Override
    public void submit(State state, PoseStack pose, SubmitNodeCollector collector, CameraRenderState camera) {
        if (state.sprite == null) {
            return;
        }
        collector.submitCustomGeometry(pose, CustomRenderTypes.translucent(), (captured, vertices) -> {
            PoseStack copy = new PoseStack();
            copy.last().set(captured);
            float inset = 0.015f;
            var sides = state.sides;
            RenderHelper.drawBoxUnit(copy, vertices, state.sprite,
                    sides.contains(Direction.DOWN), sides.contains(Direction.UP),
                    sides.contains(Direction.NORTH), sides.contains(Direction.SOUTH),
                    sides.contains(Direction.WEST), sides.contains(Direction.EAST),
                    inset, 1 - inset, inset, Math.max(inset, state.height - inset), inset, 1 - inset,
                    RenderSettings.builder().color(state.color).brightness(state.lightCoords).build());
        });
    }

    public static void register() {
        BlockEntityRenderers.register(TankModule.TYPE_TANK.get(), TankTESR::new);
    }

    public static void registerFluidModel(RegisterFluidModelsEvent event) {
        Material material = new Material(FluidLiquidCrystal.TEXTURE).withForceTranslucent(true);
        FluidTintSource tint = state -> 0xffffdd00;
        event.register(new FluidModel.Unbaked(material, material, null, tint), CoreModule.LIQUID_CRYSTAL);
    }
}
