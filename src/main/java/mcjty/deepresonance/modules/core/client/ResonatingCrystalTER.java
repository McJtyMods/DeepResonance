package mcjty.deepresonance.modules.core.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.modules.core.CoreModule;
import mcjty.deepresonance.modules.core.tile.TileEntityResonatingCrystal;
import mcjty.lib.client.RenderHelper;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class ResonatingCrystalTER extends TileEntityRenderer<TileEntityResonatingCrystal> {

    public static final ResourceLocation REDHALO = new ResourceLocation(DeepResonance.MODID, "effects/redhalo");

    public ResonatingCrystalTER(TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(TileEntityResonatingCrystal tileEntity, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLightIn, int combinedOverlayIn) {
        if (tileEntity.isGlowing()) {
            RenderHelper.renderBillboardQuadBright(matrixStack, buffer, 0.6f, REDHALO);
        }
    }

    public static void register() {
        ClientRegistry.bindTileEntityRenderer(CoreModule.TYPE_RESONATING_CRYSTAL.get(), ResonatingCrystalTER::new);
    }
}
