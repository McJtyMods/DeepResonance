package mcjty.deepresonance.modules.machines.client;

import com.google.common.base.Preconditions;
import com.mojang.blaze3d.matrix.MatrixStack;
import elec332.core.client.RenderHelper;
import mcjty.deepresonance.modules.core.CoreModule;
import mcjty.deepresonance.modules.machines.MachinesModule;
import mcjty.deepresonance.modules.machines.tile.TileEntityCrystallizer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.client.registry.ClientRegistry;

import javax.annotation.Nonnull;

/**
 * Created by Elec332 on 15-8-2020
 */
public class CrystallizerTESR extends TileEntityRenderer<TileEntityCrystallizer> {

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
    public void render(@Nonnull TileEntityCrystallizer tile, float partialTicks, @Nonnull MatrixStack matrixStack, @Nonnull IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
        int progress = tile.getProgress();
        if (tile.hasCrystal()) {
            progress = 100;
        }
        if (progress > 0) {
            matrixStack.push();
            float scale = 0.15f + 0.35f * (progress / 100.0f);
            float f = (float) Math.floorMod(Preconditions.checkNotNull(tile.getLevel()).getGameTime(), 120);
            matrixStack.translate(0.5, 0.52 + (0.002 * progress), 0.5);
            matrixStack.rotate(Vector3f.YP.rotationDegrees(f * 3));
            matrixStack.scale(scale, scale, scale);
            Minecraft.getInstance().getItemRenderer().renderItem(stack, ItemCameraTransforms.TransformType.GROUND, false, matrixStack, type -> buffer.getBuffer(RenderType.getSolid()), RenderHelper.MAX_BRIGHTNESS / 2, combinedOverlay, crystal);
            matrixStack.pop();
        }
    }

}
