package mcjty.deepresonance.modules.core.client;

import com.mojang.blaze3d.vertex.PoseStack;
import mcjty.deepresonance.modules.core.CoreModule;
import mcjty.deepresonance.modules.core.block.ResonatingCrystalTileEntity;
import mcjty.deepresonance.setup.ClientSetup;
import mcjty.lib.client.CustomRenderTypes;
import mcjty.lib.client.DelayedRenderer;
import mcjty.lib.client.RenderHelper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;

import javax.annotation.Nonnull;

public class ResonatingCrystalRenderer implements BlockEntityRenderer<ResonatingCrystalTileEntity> {

    public ResonatingCrystalRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(ResonatingCrystalTileEntity tileEntity, float partialTicks, @Nonnull PoseStack matrixStack, @Nonnull MultiBufferSource buffer, int combinedLightIn, int combinedOverlayIn) {
        if (tileEntity.isGlowing()) {
            DelayedRenderer.addRender(CustomRenderTypes.TRANSLUCENT_ADD_NOLIGHTMAPS, tileEntity.getBlockPos(), (stack, buf) -> {
                    RenderHelper.renderBillboardQuadBright(stack, buf, 0.6f, ClientSetup.REDHALO);
            });
        }
    }

    public static void register() {
        BlockEntityRenderers.register(CoreModule.TYPE_RESONATING_CRYSTAL.get(), ResonatingCrystalRenderer::new);
    }
}
