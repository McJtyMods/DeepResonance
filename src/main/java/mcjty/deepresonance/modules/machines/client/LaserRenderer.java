package mcjty.deepresonance.modules.machines.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import mcjty.deepresonance.modules.machines.MachinesModule;
import mcjty.deepresonance.modules.machines.block.LaserTileEntity;
import mcjty.deepresonance.modules.machines.data.InfusionBonusRegistry;
import mcjty.deepresonance.setup.ClientSetup;
import mcjty.lib.client.CustomRenderTypes;
import mcjty.lib.client.DelayedRenderer;
import mcjty.lib.client.RenderHelper;
import mcjty.lib.client.RenderSettings;
import mcjty.lib.varia.OrientationTools;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
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

    public LaserRenderer(BlockEntityRendererProvider.Context context) {
    }

    public static void register() {
        BlockEntityRenderers.register(MachinesModule.TYPE_LASER.get(), LaserRenderer::new);
    }

    @Override
    public void render(@Nonnull LaserTileEntity tileEntity, float partialTicks, @Nonnull PoseStack matrixStack, @Nonnull MultiBufferSource buffer, int combinedLightIn, int combinedOverlayIn) {
        DelayedRenderer.addRender(tileEntity.getBlockPos(), (stack, buf) -> {
            renderInternal(tileEntity, stack, buf);
        });
    }

    private void renderInternal(LaserTileEntity tileEntity, PoseStack matrixStack, MultiBufferSource buffer) {
        int color = tileEntity.getBlockState().getValue(LaserTileEntity.COLOR);
        if (color != 0) {
            BlockPos pos = tileEntity.getBlockPos();
            Direction direction = OrientationTools.getOrientationHoriz(Minecraft.getInstance().level.getBlockState(pos));
            float destX = 0.5f + direction.getStepX()*2.5f;
            float destY = 0.5f;
            float destZ = 0.5f + direction.getStepZ()*2.5f;

            ResourceLocation laser = null;
            switch (color) {
                case InfusionBonusRegistry.COLOR_BLUE: laser = ClientSetup.BLUELASER; break;
                case InfusionBonusRegistry.COLOR_RED: laser = ClientSetup.REDLASER; break;
                case InfusionBonusRegistry.COLOR_GREEN: laser = ClientSetup.GREENLASER; break;
                case InfusionBonusRegistry.COLOR_YELLOW: laser = ClientSetup.YELLOWLASER; break;
            }

            int tex = tileEntity.getBlockPos().getX();
            int tey = tileEntity.getBlockPos().getY();
            int tez = tileEntity.getBlockPos().getZ();
            Vec3 projectedView = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition().add(-tex, -tey, -tez);

            // Crystal coordinates are relative!
            Vector3f start = new Vector3f(.5f, .5f, .5f);
            Vector3f end = new Vector3f(destX, destY, destZ);
            Vector3f player = new Vector3f((float)projectedView.x, (float)projectedView.y, (float)projectedView.z);

            TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(laser);

            matrixStack.pushPose();
            VertexConsumer builder = buffer.getBuffer(CustomRenderTypes.TRANSLUCENT_ADD_NOLIGHTMAPS);
            RenderSettings settingsLaser = RenderSettings.builder()
                    .width(.25f)
                    .alpha(128)
                    .build();
            RenderHelper.drawBeam(matrixStack.last().pose(), builder, sprite, start, end, player, settingsLaser);
            matrixStack.popPose();
        }
    }

    // @todo 1.16
//    static {
//        RenderingRegistry.instance().registerLoader(new IModelAndTextureLoader() {
//
//            private TextureAtlasSprite tex;
//
//            @Override
//            public void registerTextures(IIconRegistrar iconRegistrar) {
//                tex = iconRegistrar.registerSprite(new ResourceLocation(DeepResonance.MODID, "effects/laserbeam"));
//            }
//
//            @Override
//            public void registerModels(IQuadBakery quadBakery, IModelBakery modelBakery, ITemplateBakery templateBakery) {
//                IForgeTransformationMatrix m = new TransformationMatrix(new Vector3f(0, 0, -1), null, null, null);
//                LaserTESR.quad = quadBakery.bakeQuad(new Vector3f(-BEAM_WIDTH / 2, BEAM_WIDTH / 2, 0), new Vector3f(BEAM_WIDTH / 2, BEAM_WIDTH / 2, 16), tex, Direction.UP, m);
//            }
//
//        });
//    }

}
