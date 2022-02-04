package mcjty.deepresonance.modules.core.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import mcjty.deepresonance.modules.core.CoreModule;
import mcjty.deepresonance.modules.core.block.ResonatingCrystalTileEntity;
import mcjty.deepresonance.setup.ClientSetup;
import mcjty.lib.client.DelayedRenderer;
import mcjty.lib.client.RenderHelper;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraftforge.fml.client.registry.ClientRegistry;

import javax.annotation.Nonnull;

public class ResonatingCrystalRenderer extends TileEntityRenderer<ResonatingCrystalTileEntity> {

    public ResonatingCrystalRenderer(TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(ResonatingCrystalTileEntity tileEntity, float partialTicks, @Nonnull MatrixStack matrixStack, @Nonnull IRenderTypeBuffer buffer, int combinedLightIn, int combinedOverlayIn) {
        if (tileEntity.isGlowing()) {
            DelayedRenderer.addRender(tileEntity.getBlockPos(), (stack, buf) -> {
                    RenderHelper.renderBillboardQuadBright(stack, buf, 0.6f, ClientSetup.REDHALO);
            });
        }
    }

    public static void register() {
        ClientRegistry.bindTileEntityRenderer(CoreModule.TYPE_RESONATING_CRYSTAL.get(), ResonatingCrystalRenderer::new);
    }
}
