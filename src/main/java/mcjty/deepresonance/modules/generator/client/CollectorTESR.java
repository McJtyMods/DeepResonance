package mcjty.deepresonance.modules.generator.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import mcjty.deepresonance.modules.generator.GeneratorModule;
import mcjty.deepresonance.modules.generator.tile.TileEntityEnergyCollector;
import mcjty.lib.client.RenderHelper;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraftforge.fml.client.registry.ClientRegistry;

import javax.annotation.Nonnull;

public class CollectorTESR extends TileEntityRenderer<TileEntityEnergyCollector> {

    private static final RenderType CRYSTAL_HALO, COLLECTOR_HALO;
    private static final double SIZE = 0.6;

    public CollectorTESR(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    public static void register() {
        ClientRegistry.bindTileEntityRenderer(GeneratorModule.TYPE_ENERGY_COLLECTOR.get(), CollectorTESR::new);
    }

    @Override
    public void render(@Nonnull TileEntityEnergyCollector tileEntity, float partialTicks, @Nonnull MatrixStack matrixStack, @Nonnull IRenderTypeBuffer buffer_, int combinedLightIn, int combinedOverlayIn) {
        // @todo 1.16
//        if (tileEntity.getStartupTimer() != 0 || tileEntity.getCrystals().isEmpty()) {
//            return;
//        }
        matrixStack.translate(0.5, 0.6, 0.5);
        IVertexBuilder buffer;

        matrixStack.pushPose();
        matrixStack.translate(0, 0.25, 0);
        RenderHelper.rotateToPlayer(matrixStack);
        buffer = buffer_.getBuffer(COLLECTOR_HALO);
        // @todo 1.16
//        tessellator = RenderHelper.forWorldRenderer(buffer);
//        tessellator.setTransformation(matrixStack);
//        tessellator.addVertexWithUV(-SIZE, -SIZE, 0, 0, 0);
//        tessellator.addVertexWithUV(-SIZE, SIZE, 0, 0, 1);
//        tessellator.addVertexWithUV(SIZE, SIZE, 0, 1, 1);
//        tessellator.addVertexWithUV(SIZE, -SIZE, 0, 1, 0);
//        matrixStack.pop();
//
//        buffer = buffer_.getBuffer(CRYSTAL_HALO);
//        tessellator = RenderHelper.forWorldRenderer(buffer);
//        for (BlockPos pos : tileEntity.getCrystals()) {
//            matrixStack.pushPose();
//            tessellator.setTransformation(matrixStack);
//            matrixStack.translate(pos.getX(), pos.getY(), pos.getZ());
//            RenderHelper.facingPlayer(matrixStack);
//            tessellator.addVertexWithUV(-SIZE, -SIZE, 0, 0, 0);
//            tessellator.addVertexWithUV(-SIZE, SIZE, 0, 0, 1);
//            tessellator.addVertexWithUV(SIZE, SIZE, 0, 1, 1);
//            tessellator.addVertexWithUV(SIZE, -SIZE, 0, 1, 0);
//
//            //todo: beam
//            matrixStack.pop();
//        }
    }

    static {
        CRYSTAL_HALO = null; // @todo .116 RenderHelper.createRenderType("crystal_halo", RenderType.getTranslucent(), new ResourceLocation(DeepResonance.MODID, "textures/effects/redhalo.png"));
        COLLECTOR_HALO = null; // @todo .116 RenderHelper.createRenderType("collector_halo", RenderType.getTranslucent(), new ResourceLocation(DeepResonance.MODID, "textures/effects/halo.png"));
    }

}
