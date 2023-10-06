package mcjty.deepresonance.modules.machines.client;

import com.mojang.blaze3d.vertex.PoseStack;
import mcjty.deepresonance.modules.core.CoreModule;
import mcjty.deepresonance.modules.machines.MachinesModule;
import mcjty.deepresonance.modules.machines.block.CrystallizerTileEntity;
import mcjty.lib.client.RenderHelper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

public class CrystallizerRenderer implements BlockEntityRenderer<CrystallizerTileEntity> {

    private static final ItemStack stack = new ItemStack(CoreModule.RESONATING_CRYSTAL_GENERATED.get());

    public CrystallizerRenderer(BlockEntityRendererProvider.Context context) {
    }

    public static void register() {
        BlockEntityRenderers.register(MachinesModule.TYPE_CRYSTALIZER.get(), CrystallizerRenderer::new);
    }

    @Override
    public void render(@Nonnull CrystallizerTileEntity tile, float partialTicks, @Nonnull PoseStack matrixStack, @Nonnull MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
        int progress = tile.getProgress();
        if (tile.hasCrystal()) {
            progress = 100;
        }
        if (progress > 0) {
            matrixStack.pushPose();
            float scale = 0.75f + 0.45f * (progress / 100.0f);
            float f = Math.floorMod(tile.getLevel().getGameTime(), 120);
            matrixStack.translate(0.5, 0.35 + (0.00 * progress), 0.5);
            RenderHelper.rotateYP(matrixStack, f * 3);
            matrixStack.scale(scale, scale, scale);
            RenderHelper.renderItemGround(matrixStack, buffer, RenderType.solid(), stack, RenderHelper.MAX_BRIGHTNESS / 2, combinedOverlay);
            matrixStack.popPose();
        }
    }

}
