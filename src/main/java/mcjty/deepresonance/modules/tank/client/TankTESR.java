package mcjty.deepresonance.modules.tank.client;

import com.mojang.blaze3d.platform.GlStateManager;
import elec332.core.api.client.ITessellator;
import elec332.core.client.RenderHelper;
import mcjty.deepresonance.modules.tank.tile.TileEntityTank;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.fluid.Fluid;

/**
 * Created by Elec332 on 9-1-2020
 */
public class TankTESR extends TileEntityRenderer<TileEntityTank> {

    @Override
    public void render(TileEntityTank tileTank, double x, double y, double z, float partialTicks, int destroyStage) {
        Tessellator tessellator = Tessellator.getInstance();

        net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();

        GlStateManager.disableRescaleNormal();
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.disableBlend();

        GlStateManager.pushMatrix();
        GlStateManager.translated(x, y, z);

        RenderHelper.bindBlockTextures();

        Fluid renderFluid = tileTank.getClientRenderFluid();
        if (renderFluid != null) {
            renderFluid(tileTank, tessellator, renderFluid);
        }

        net.minecraft.client.renderer.RenderHelper.enableStandardItemLighting();

        GlStateManager.popMatrix();
    }

    private void renderFluid(TileEntityTank tileTank, Tessellator tessellator_, Fluid renderFluid) {

        float offset = 0.00002f;
        TextureAtlasSprite fluid = RenderHelper.getFluidTexture(renderFluid, false);

        float scale = tileTank.getClientRenderHeight();
        float u1 = fluid.getMinU();
        float v1 = fluid.getMinV();
        float u2 = fluid.getMaxU();
        float v2 = fluid.getMaxV();
        ITessellator tessellator = RenderHelper.forWorldRenderer(tessellator_.getBuffer());
        tessellator.startDrawingWorldBlock();

        int color = renderFluid.getAttributes().getColor(getWorld(), tileTank.getPos());
        tessellator.setColorRGBA_I(color, 128);
        tessellator.setBrightness(240);

        if (scale > 0.0f) {
            //TOP
            tessellator.addVertexWithUV(0, scale - offset, 0, u1, v1);
            tessellator.addVertexWithUV(0, scale - offset, 1, u1, v2);
            tessellator.addVertexWithUV(1, scale - offset, 1, u2, v2);
            tessellator.addVertexWithUV(1, scale - offset, 0, u2, v1);

            if (scale > 3 / 16f) {

                if (scale > 1 - 3 / 16f) {
                    scale = 1 - 3 / 16f;
                }

                v2 -= (fluid.getMaxV() - fluid.getMinV()) * (1 - scale);

                //NORTH
                tessellator.addVertexWithUV(1 - 3 / 16f, scale, -offset, u1, v1);
                tessellator.addVertexWithUV(1 - 3 / 16f, 3 / 16f, -offset, u1, v2);
                tessellator.addVertexWithUV(3 / 16f, 3 / 16f, -offset, u2, v2);
                tessellator.addVertexWithUV(3 / 16f, scale, -offset, u2, v1);

                //EAST
                tessellator.addVertexWithUV(-offset, 3 / 16f, 1 - 3 / 16f, u1, v2);
                tessellator.addVertexWithUV(-offset, scale, 1 - 3 / 16f, u1, v1);
                tessellator.addVertexWithUV(-offset, scale, 3 / 16f, u2, v1);
                tessellator.addVertexWithUV(-offset, 3 / 16f, 3 / 16f, u2, v2);

                //SOUTH
                tessellator.addVertexWithUV(1 - 3 / 16f, 3 / 16f, 1 + offset, u1, v2);
                tessellator.addVertexWithUV(1 - 3 / 16f, scale, 1 + offset, u1, v1);
                tessellator.addVertexWithUV(3 / 16f, scale, 1 + offset, u2, v1);
                tessellator.addVertexWithUV(3 / 16f, 3 / 16f, 1 + offset, u2, v2);

                //WEST
                tessellator.addVertexWithUV(1 + offset, scale, 1 - 3 / 16f, u1, v1);
                tessellator.addVertexWithUV(1 + offset, 3 / 16f, 1 - 3 / 16f, u1, v2);
                tessellator.addVertexWithUV(1 + offset, 3 / 16f, 3 / 16f, u2, v2);
                tessellator.addVertexWithUV(1 + offset, scale, 3 / 16f, u2, v1);
            }
        }
        tessellator_.draw();
    }

}
