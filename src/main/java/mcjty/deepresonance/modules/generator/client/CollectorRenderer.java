package mcjty.deepresonance.modules.generator.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import mcjty.deepresonance.modules.generator.GeneratorModule;
import mcjty.deepresonance.modules.generator.block.EnergyCollectorTileEntity;
import mcjty.deepresonance.modules.generator.util.GeneratorConfig;
import mcjty.deepresonance.setup.ClientSetup;
import mcjty.lib.client.CustomRenderTypes;
import mcjty.lib.client.DelayedRenderer;
import mcjty.lib.client.RenderHelper;
import mcjty.lib.client.RenderSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.fml.client.registry.ClientRegistry;

import javax.annotation.Nonnull;
import java.util.Random;
import java.util.Set;

public class CollectorRenderer extends TileEntityRenderer<EnergyCollectorTileEntity> {

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

    public CollectorRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    public static void register() {
        ClientRegistry.bindTileEntityRenderer(GeneratorModule.TYPE_ENERGY_COLLECTOR.get(), CollectorRenderer::new);
    }

    @Override
    public void render(@Nonnull EnergyCollectorTileEntity tileEntity, float partialTicks, @Nonnull MatrixStack matrixStack, @Nonnull IRenderTypeBuffer buffer, int combinedLightIn, int combinedOverlayIn) {
        if (tileEntity.getCrystals().isEmpty() || (!tileEntity.areLasersActive() && tileEntity.getLaserStartup() <= 0)) {
            return;
        }

        DelayedRenderer.addRender(tileEntity.getBlockPos(), (stack, buf) -> {
            renderInternal(tileEntity.getBlockPos(), tileEntity.getLaserStartup(), tileEntity.getCrystals(), stack, buf);
        });
    }

    private void renderInternal(BlockPos pos, int laserStartup, Set<BlockPos> crystals, MatrixStack matrixStack, IRenderTypeBuffer buffer) {
        matrixStack.translate(0f, .25f, .0f);

        RenderHelper.renderBillboardQuadBright(matrixStack, buffer, 1.0f, ClientSetup.HALO, SETTINGS);// + random.nextFloat() * .05f);

        float startupFactor = laserStartup / (float) GeneratorConfig.STARTUP_TIME.get();

        matrixStack.translate(0, -.25f, 0f);
        for (BlockPos destination : crystals) {
            TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(AtlasTexture.LOCATION_BLOCKS).apply(ClientSetup.LASERBEAMS[random.nextInt(4)]);

            IVertexBuilder builder = buffer.getBuffer(CustomRenderTypes.TRANSLUCENT_ADD_NOLIGHTMAPS);

            int tex = pos.getX();
            int tey = pos.getY();
            int tez = pos.getZ();
            Vector3d projectedView = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition().add(-tex, -tey, -tez);

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
