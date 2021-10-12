package mcjty.deepresonance.modules.machines.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import mcjty.deepresonance.modules.core.CoreModule;
import mcjty.deepresonance.modules.machines.MachinesModule;
import mcjty.deepresonance.modules.machines.block.CrystallizerTileEntity;
import mcjty.lib.client.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.fml.client.registry.ClientRegistry;

import javax.annotation.Nonnull;

public class CrystallizerTESR extends TileEntityRenderer<CrystallizerTileEntity> {

    private static IBakedModel crystal;
    private static ItemStack stack;

    public CrystallizerTESR(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    public static void setModel(IBakedModel model) {
        crystal = model;
        stack = new ItemStack(CoreModule.RESONATING_CRYSTAL_ITEM.get());
    }

    public static void register() {
        ClientRegistry.bindTileEntityRenderer(MachinesModule.TYPE_CRYSTALIZER.get(), CrystallizerTESR::new);
    }

    @Override
    public void render(@Nonnull CrystallizerTileEntity tile, float partialTicks, @Nonnull MatrixStack matrixStack, @Nonnull IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
        int progress = tile.getProgress();
        if (tile.hasCrystal()) {
            progress = 100;
        }
        if (progress > 0) {
            matrixStack.pushPose();
            float scale = 0.15f + 0.35f * (progress / 100.0f);
            float f = Math.floorMod(tile.getLevel().getGameTime(), 120);
            matrixStack.translate(0.5, 0.52 + (0.002 * progress), 0.5);
            matrixStack.mulPose(Vector3f.YP.rotationDegrees(f * 3));
            matrixStack.scale(scale, scale, scale);
            Minecraft.getInstance().getItemRenderer().render(stack, ItemCameraTransforms.TransformType.GROUND, false, matrixStack, type -> buffer.getBuffer(RenderType.solid()), RenderHelper.MAX_BRIGHTNESS / 2, combinedOverlay, crystal);
            matrixStack.popPose();
        }
    }

}
