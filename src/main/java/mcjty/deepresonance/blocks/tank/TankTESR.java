package mcjty.deepresonance.blocks.tank;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import elec332.core.client.render.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraftforge.fluids.Fluid;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class TankTESR extends TileEntitySpecialRenderer {

    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float time) {
        if (tileEntity instanceof TileTank) {
            TileTank tileTank = (TileTank) tileEntity;
            Fluid renderFluid = tileTank.getClientRenderFluid();
            if (renderFluid != null) {
                Tessellator tessellator = Tessellator.instance;
                IIcon fluid = renderFluid.getStillIcon();
                fluid = RenderHelper.checkIcon(fluid);

                GL11.glPushMatrix();
                GL11.glTranslated(x, y, z);
                tessellator.startDrawingQuads();
                tessellator.setColorRGBA(255, 255, 255, 128);
                tessellator.setBrightness(240);

                bindTexture(TextureMap.locationBlocksTexture);

                float scale = tileTank.getRenderHeight();
                float renderDistance = 0.002f;
                float u1 = fluid.getMinU();
                float v1 = fluid.getMinV();
                float u2 = fluid.getMaxU();
                float v2 = fluid.getMaxV() - (fluid.getMaxV()-fluid.getMinV()) * (1 - scale);

                //NORTH
                tessellator.addVertexWithUV(1 - 3/16f, (1 - 3/16f) * scale, -renderDistance, u1, v1);
                tessellator.addVertexWithUV(1 - 3/16f, 3/16f, -renderDistance, u1, v2);
                tessellator.addVertexWithUV(3/16f, 3/16f, -renderDistance, u2, v2);
                tessellator.addVertexWithUV(3/16f, (1 - 3/16f) * scale, -renderDistance, u2, v1);

                //EAST
                //TODO

                //SOUTH
                tessellator.addVertexWithUV(1 - 3/16f, 3/16f, 1 + renderDistance, u1, v2);
                tessellator.addVertexWithUV(1 - 3/16f, (1 - 3/16f) * scale, 1 + renderDistance, u1, v1);
                tessellator.addVertexWithUV(3/16f, (1 - 3/16f) * scale, 1 + renderDistance, u2, v1);
                tessellator.addVertexWithUV(3/16f, 3/16f, 1 + renderDistance, u2, v2);

                //WEST
                //TODO


                tessellator.draw();
                GL11.glPopMatrix();
            }
        }
    }
}
