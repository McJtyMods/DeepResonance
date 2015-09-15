package mcjty.deepresonance.blocks.tank;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import elec332.core.client.render.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

@SideOnly(Side.CLIENT)
public class TankTESR extends TileEntitySpecialRenderer {

    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float time) {
        if (tileEntity instanceof TileTank) {
            TileTank tileTank = (TileTank) tileEntity;

            Tessellator tessellator = Tessellator.instance;

            net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();

            GL11.glDisable(GL12.GL_RESCALE_NORMAL);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glDisable(GL11.GL_BLEND);

            GL11.glPushMatrix();
            GL11.glTranslated(x, y, z);

            bindTexture(TextureMap.locationBlocksTexture);
            World world = tileEntity.getWorldObj();
            renderTankInside(tessellator, world, tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord);

            Fluid renderFluid = tileTank.getClientRenderFluid();
            if (renderFluid != null) {
                renderFluid(tileTank, tessellator, renderFluid);
            }

            GL11.glPopMatrix();
        }
    }

    // ---------------------------------------------------------------
    // Render the fluid inside the tank
    // ---------------------------------------------------------------
    private void renderFluid(TileTank tileTank, Tessellator tessellator, Fluid renderFluid) {

        float offset = 0.002f;

        IIcon fluid = renderFluid.getStillIcon();
        fluid = RenderHelper.checkIcon(fluid);

        tessellator.startDrawingQuads();

        tessellator.setColorRGBA(255, 255, 255, 128);
        tessellator.setBrightness(240);

        float scale = tileTank.getRenderHeight();
        float u1 = fluid.getMinU();
        float v1 = fluid.getMinV();
        float u2 = fluid.getMaxU();
        float v2 = fluid.getMaxV();

        if (scale > 0.0f) {

            // Top
            tessellator.addVertexWithUV(0, scale - offset, 0, u1, v1);
            tessellator.addVertexWithUV(0, scale - offset, 1, u1, v2);
            tessellator.addVertexWithUV(1, scale - offset, 1, u2, v2);
            tessellator.addVertexWithUV(1, scale - offset, 0, u2, v1);
            
            if (scale > 3/16f) {

                if (scale > 1 - 3/16f)
                    scale = 1 - 3/16f;

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
        tessellator.draw();
    }

    private void renderTankInside(Tessellator tessellator, World world, int ix, int iy, int iz) {
        float offset = 0.002f;

        tessellator.startDrawingQuads();
        tessellator.setColorRGBA(255, 255, 255, 128);
        tessellator.setBrightness(100);

        // ---------------------------------------------------------------
        // Render the inside of the tank
        // ---------------------------------------------------------------
        IIcon blockIcon = TankSetup.tank.getSideIcon();

        //NORTH other side
        if (world.getBlock(ix, iy, iz-1) != TankSetup.tank) {
            tessellator.addVertexWithUV(1, 0, offset, blockIcon.getMinU(), blockIcon.getMinV());
            tessellator.addVertexWithUV(1, 1, offset, blockIcon.getMinU(), blockIcon.getMaxV());
            tessellator.addVertexWithUV(0, 1, offset, blockIcon.getMaxU(), blockIcon.getMaxV());
            tessellator.addVertexWithUV(0, 0, offset, blockIcon.getMaxU(), blockIcon.getMinV());
        }

        //SOUTH other side
        if (world.getBlock(ix, iy, iz+1) != TankSetup.tank) {
            tessellator.addVertexWithUV(1, 1, 1 - offset, blockIcon.getMinU(), blockIcon.getMinV());
            tessellator.addVertexWithUV(1, 0, 1 - offset, blockIcon.getMinU(), blockIcon.getMaxV());
            tessellator.addVertexWithUV(0, 0, 1 - offset, blockIcon.getMaxU(), blockIcon.getMaxV());
            tessellator.addVertexWithUV(0, 1, 1 - offset, blockIcon.getMaxU(), blockIcon.getMinV());
        }

        //EAST other side
        if (world.getBlock(ix-1, iy, iz) != TankSetup.tank) {
            tessellator.addVertexWithUV(offset, 1, 1, blockIcon.getMinU(), blockIcon.getMinV());
            tessellator.addVertexWithUV(offset, 0, 1, blockIcon.getMinU(), blockIcon.getMaxV());
            tessellator.addVertexWithUV(offset, 0, 0, blockIcon.getMaxU(), blockIcon.getMaxV());
            tessellator.addVertexWithUV(offset, 1, 0, blockIcon.getMaxU(), blockIcon.getMinV());
        }

        //WEST other side
        if (world.getBlock(ix+1, iy, iz) != TankSetup.tank) {
            tessellator.addVertexWithUV(1 - offset, 0, 1, blockIcon.getMinU(), blockIcon.getMinV());
            tessellator.addVertexWithUV(1 - offset, 1, 1, blockIcon.getMinU(), blockIcon.getMaxV());
            tessellator.addVertexWithUV(1 - offset, 1, 0, blockIcon.getMaxU(), blockIcon.getMaxV());
            tessellator.addVertexWithUV(1 - offset, 0, 0, blockIcon.getMaxU(), blockIcon.getMinV());
        }

        // Bottom other side
        if (world.getBlock(ix, iy-1, iz) != TankSetup.tank) {
            blockIcon = TankSetup.tank.getBottomIcon();
            tessellator.addVertexWithUV(0, offset, 0, blockIcon.getMinU(), blockIcon.getMinV());
            tessellator.addVertexWithUV(0, offset, 1, blockIcon.getMinU(), blockIcon.getMaxV());
            tessellator.addVertexWithUV(1, offset, 1, blockIcon.getMaxU(), blockIcon.getMaxV());
            tessellator.addVertexWithUV(1, offset, 0, blockIcon.getMaxU(), blockIcon.getMinV());
        }

        // Top other side
        if (world.getBlock(ix, iy+1, iz) != TankSetup.tank) {
            blockIcon = TankSetup.tank.getTopIcon();
            tessellator.addVertexWithUV(0, 1 - offset, 0, blockIcon.getMinU(), blockIcon.getMinV());
            tessellator.addVertexWithUV(1, 1 - offset, 0, blockIcon.getMinU(), blockIcon.getMaxV());
            tessellator.addVertexWithUV(1, 1 - offset, 1, blockIcon.getMaxU(), blockIcon.getMaxV());
            tessellator.addVertexWithUV(0, 1 - offset, 1, blockIcon.getMaxU(), blockIcon.getMinV());
        }

        tessellator.draw();
    }
}
