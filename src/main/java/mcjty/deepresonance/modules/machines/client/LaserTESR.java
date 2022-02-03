package mcjty.deepresonance.modules.machines.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import mcjty.deepresonance.modules.machines.MachinesModule;
import mcjty.deepresonance.modules.machines.block.LaserTileEntity;
import mcjty.deepresonance.modules.machines.data.InfusionBonusRegistry;
import mcjty.deepresonance.setup.ClientSetup;
import mcjty.lib.client.CustomRenderTypes;
import mcjty.lib.client.RenderHelper;
import mcjty.lib.client.RenderSettings;
import mcjty.lib.varia.OrientationTools;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.fml.client.registry.ClientRegistry;

import javax.annotation.Nonnull;

public class LaserTESR extends TileEntityRenderer<LaserTileEntity> {

    private static final float BEAM_WIDTH = 3.8f;
    private static BakedQuad quad;

    public LaserTESR(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    public static void register() {
        ClientRegistry.bindTileEntityRenderer(MachinesModule.TYPE_LASER.get(), LaserTESR::new);
    }

    @Override
    public void render(@Nonnull LaserTileEntity tileEntity, float partialTicks, @Nonnull MatrixStack matrixStack, @Nonnull IRenderTypeBuffer buffer, int combinedLightIn, int combinedOverlayIn) {
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
            Vector3d projectedView = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition().add(-tex, -tey, -tez);

            // Crystal coordinates are relative!
            Vector3f start = new Vector3f(.5f, .5f, .5f);
            Vector3f end = new Vector3f(destX, destY, destZ);
            Vector3f player = new Vector3f((float)projectedView.x, (float)projectedView.y, (float)projectedView.z);

            TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(AtlasTexture.LOCATION_BLOCKS).apply(laser);

            matrixStack.pushPose();
            IVertexBuilder builder = buffer.getBuffer(CustomRenderTypes.TRANSLUCENT_ADD_NOLIGHTMAPS);
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
