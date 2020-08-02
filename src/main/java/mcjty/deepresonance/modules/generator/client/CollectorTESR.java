package mcjty.deepresonance.modules.generator.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import elec332.core.api.client.ITessellator;
import elec332.core.client.RenderHelper;
import elec332.core.client.util.AbstractTileEntityRenderer;
import mcjty.deepresonance.modules.generator.tile.TileEntityEnergyCollector;
import mcjty.deepresonance.util.DeepResonanceResourceLocation;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;

/**
 * Created by Elec332 on 2-8-2020
 */
public class CollectorTESR extends AbstractTileEntityRenderer<TileEntityEnergyCollector> {

    private static final RenderType CRYSTAL_HALO, COLLECTOR_HALO;
    private static final double SIZE = 0.6;

    @Override
    public void render(@Nonnull TileEntityEnergyCollector tileEntity, float partialTicks, @Nonnull MatrixStack matrixStack, @Nonnull IRenderTypeBuffer buffer_, int combinedLightIn, int combinedOverlayIn) {
        if (tileEntity.getCrystals().isEmpty()) {
            return;
        }
        matrixStack.translate(0.5, 0.6, 0.5);
        IVertexBuilder buffer;
        ITessellator tessellator;

        matrixStack.push();
        matrixStack.translate(0, 0.25, 0);
        RenderHelper.facingPlayer(matrixStack);
        buffer = buffer_.getBuffer(COLLECTOR_HALO);
        tessellator = RenderHelper.forWorldRenderer(buffer);
        tessellator.setTransformation(matrixStack);
        tessellator.addVertexWithUV(-SIZE, -SIZE, 0, 0, 0);
        tessellator.addVertexWithUV(-SIZE, SIZE, 0, 0, 1);
        tessellator.addVertexWithUV(SIZE, SIZE, 0, 1, 1);
        tessellator.addVertexWithUV(SIZE, -SIZE, 0, 1, 0);
        matrixStack.pop();

        buffer = buffer_.getBuffer(CRYSTAL_HALO);
        tessellator = RenderHelper.forWorldRenderer(buffer);
        for (BlockPos pos : tileEntity.getCrystals()) {
            matrixStack.push();
            tessellator.setTransformation(matrixStack);
            matrixStack.translate(pos.getX(), pos.getY(), pos.getZ());
            RenderHelper.facingPlayer(matrixStack);
            tessellator.addVertexWithUV(-SIZE, -SIZE, 0, 0, 0);
            tessellator.addVertexWithUV(-SIZE, SIZE, 0, 0, 1);
            tessellator.addVertexWithUV(SIZE, SIZE, 0, 1, 1);
            tessellator.addVertexWithUV(SIZE, -SIZE, 0, 1, 0);

            //todo: beam
            matrixStack.pop();
        }
    }

    static {
        CRYSTAL_HALO = RenderHelper.createRenderType("crystal_halo", RenderType.getTranslucent(), new DeepResonanceResourceLocation("textures/effects/redhalo.png"));
        COLLECTOR_HALO = RenderHelper.createRenderType("collector_halo", RenderType.getTranslucent(), new DeepResonanceResourceLocation("textures/effects/halo.png"));
    }

}
