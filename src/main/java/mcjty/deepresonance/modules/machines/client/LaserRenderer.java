package mcjty.deepresonance.modules.machines.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mcjty.deepresonance.modules.machines.MachinesModule;
import mcjty.deepresonance.modules.machines.block.LaserTileEntity;
import mcjty.deepresonance.modules.machines.data.InfusionBonusRegistry;
import mcjty.deepresonance.setup.ClientSetup;
import mcjty.lib.client.DelayedRenderer;
import mcjty.lib.client.RenderHelper;
import mcjty.lib.client.RenderSettings;
import mcjty.lib.varia.OrientationTools;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nonnull;

public class LaserRenderer implements BlockEntityRenderer<LaserTileEntity> {

    public static final Vec3 START = new Vec3(.5, .5, .5);

    public LaserRenderer(BlockEntityRendererProvider.Context context) {
    }

    public static void register() {
        BlockEntityRenderers.register(MachinesModule.TYPE_LASER.get(), LaserRenderer::new);
    }

    @Override
    public void render(@Nonnull LaserTileEntity tileEntity, float partialTicks, @Nonnull PoseStack matrixStack, @Nonnull MultiBufferSource buffer, int combinedLightIn, int combinedOverlayIn) {
        DelayedRenderer.addRender(RenderType.translucent(), tileEntity.getBlockPos(), (stack, buf) -> {
            int color = tileEntity.getBlockState().getValue(LaserTileEntity.COLOR);
            renderInternal(tileEntity.getBlockPos(), color, stack, buf);
        });
    }

    private void renderInternal(BlockPos pos, int color, PoseStack matrixStack, VertexConsumer builder) {
        if (color != 0) {
            Direction direction = OrientationTools.getOrientationHoriz(Minecraft.getInstance().level.getBlockState(pos));
            float destX = 0.5f + direction.getStepX()*2.5f;
            float destY = 0.5f;
            float destZ = 0.5f + direction.getStepZ()*2.5f;

            ResourceLocation laser = switch (color) {
                case InfusionBonusRegistry.COLOR_BLUE -> ClientSetup.BLUELASER;
                case InfusionBonusRegistry.COLOR_RED -> ClientSetup.REDLASER;
                case InfusionBonusRegistry.COLOR_GREEN -> ClientSetup.GREENLASER;
                case InfusionBonusRegistry.COLOR_YELLOW -> ClientSetup.YELLOWLASER;
                default -> null;
            };

            int tex = pos.getX();
            int tey = pos.getY();
            int tez = pos.getZ();
            Vec3 player = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition().add(-tex, -tey, -tez);

            // Crystal coordinates are relative!
            Vec3 end = new Vec3(destX, destY, destZ);

            TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(laser);

            matrixStack.pushPose();
            RenderSettings settingsLaser = RenderSettings.builder()
                    .width(.25f)
                    .alpha(128)
                    .build();
            RenderHelper.drawBeam(matrixStack, builder, sprite, START, end, player, settingsLaser);
            matrixStack.popPose();
        }
    }
}
