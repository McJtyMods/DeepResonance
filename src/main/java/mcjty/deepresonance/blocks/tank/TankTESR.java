package mcjty.deepresonance.blocks.tank;

import elec332.core.client.ElecTessellator;
import elec332.core.client.ITessellator;
import elec332.core.client.RenderHelper;
import elec332.core.world.WorldHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

@SideOnly(Side.CLIENT)
public class TankTESR extends TileEntitySpecialRenderer<TileTank> {

    @Override
    public void renderTileEntityAt(TileTank tileTank, double x, double y, double z, float time, int breakTime) {
        GL11.glPushAttrib(GL11.GL_CURRENT_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_ENABLE_BIT | GL11.GL_LIGHTING_BIT | GL11.GL_TEXTURE_BIT);

        ITessellator tessellator = RenderHelper.getTessellator();

        net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();

        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDisable(GL11.GL_BLEND);

        GL11.glPushMatrix();
        GL11.glTranslated(x, y, z);

        bindTexture(TextureMap.locationBlocksTexture);
        World world = tileTank.getWorld();
        renderTankInside(tessellator, world, tileTank.getPos(), tileTank);

        Fluid renderFluid = tileTank.getClientRenderFluid();
        if (renderFluid != null) {
            renderFluid(tileTank, tessellator, renderFluid);
        }

        GL11.glPopMatrix();

        GL11.glPopAttrib();
    }

    // ---------------------------------------------------------------
    // Render the fluid inside the tank
    // ---------------------------------------------------------------
    private void renderFluid(TileTank tileTank, ITessellator tessellator, Fluid renderFluid) {

        float offset = 0.002f;

        TextureAtlasSprite fluid = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(renderFluid.getStill().toString());
        fluid = RenderHelper.checkIcon(fluid);

        ((ElecTessellator)tessellator).startDrawingWorldBlock();

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
        tessellator.getMCTessellator().draw();
    }

    private void renderTankInside(ITessellator tessellator, World world, BlockPos pos, TileTank tank) {
        float offset = 0.002f;
        int ix = pos.getX(), iy = pos.getY(), iz = pos.getZ();

        ((ElecTessellator)tessellator).startDrawingWorldBlock();
        tessellator.setColorRGBA(255, 255, 255, 128);
        tessellator.setBrightness(100);

        // ---------------------------------------------------------------
        // Render the inside of the tank
        // ---------------------------------------------------------------
        TextureAtlasSprite blockIcon = TankSetup.tank.getSideIcon();

        //NORTH other side
        if (doRenderToSide(world, ix, iy, iz-1, tank)) {
            tessellator.addVertexWithUV(1, 0, offset, blockIcon.getMinU(), blockIcon.getMinV());
            tessellator.addVertexWithUV(1, 1, offset, blockIcon.getMinU(), blockIcon.getMaxV());
            tessellator.addVertexWithUV(0, 1, offset, blockIcon.getMaxU(), blockIcon.getMaxV());
            tessellator.addVertexWithUV(0, 0, offset, blockIcon.getMaxU(), blockIcon.getMinV());
        }

        //SOUTH other side
        if (doRenderToSide(world, ix, iy, iz+1, tank)) {
            tessellator.addVertexWithUV(1, 1, 1 - offset, blockIcon.getMinU(), blockIcon.getMinV());
            tessellator.addVertexWithUV(1, 0, 1 - offset, blockIcon.getMinU(), blockIcon.getMaxV());
            tessellator.addVertexWithUV(0, 0, 1 - offset, blockIcon.getMaxU(), blockIcon.getMaxV());
            tessellator.addVertexWithUV(0, 1, 1 - offset, blockIcon.getMaxU(), blockIcon.getMinV());
        }

        //EAST other side
        if (doRenderToSide(world, ix-1, iy, iz, tank)) {
            tessellator.addVertexWithUV(offset, 1, 1, blockIcon.getMinU(), blockIcon.getMinV());
            tessellator.addVertexWithUV(offset, 0, 1, blockIcon.getMinU(), blockIcon.getMaxV());
            tessellator.addVertexWithUV(offset, 0, 0, blockIcon.getMaxU(), blockIcon.getMaxV());
            tessellator.addVertexWithUV(offset, 1, 0, blockIcon.getMaxU(), blockIcon.getMinV());
        }

        //WEST other side
        if (doRenderToSide(world, ix+1, iy, iz, tank)) {
            tessellator.addVertexWithUV(1 - offset, 0, 1, blockIcon.getMinU(), blockIcon.getMinV());
            tessellator.addVertexWithUV(1 - offset, 1, 1, blockIcon.getMinU(), blockIcon.getMaxV());
            tessellator.addVertexWithUV(1 - offset, 1, 0, blockIcon.getMaxU(), blockIcon.getMaxV());
            tessellator.addVertexWithUV(1 - offset, 0, 0, blockIcon.getMaxU(), blockIcon.getMinV());
        }

        // Bottom other side
        if (doRenderToSide(world, ix, iy-1, iz, tank)) {
            blockIcon = TankSetup.tank.getBottomIcon();
            tessellator.addVertexWithUV(0, offset, 0, blockIcon.getMinU(), blockIcon.getMinV());
            tessellator.addVertexWithUV(0, offset, 1, blockIcon.getMinU(), blockIcon.getMaxV());
            tessellator.addVertexWithUV(1, offset, 1, blockIcon.getMaxU(), blockIcon.getMaxV());
            tessellator.addVertexWithUV(1, offset, 0, blockIcon.getMaxU(), blockIcon.getMinV());
        }

        // Top other side
        if (doRenderToSide(world, ix, iy+1, iz, tank)) {
            blockIcon = TankSetup.tank.getTopIcon();
            tessellator.addVertexWithUV(0, 1 - offset, 0, blockIcon.getMinU(), blockIcon.getMinV());
            tessellator.addVertexWithUV(1, 1 - offset, 0, blockIcon.getMinU(), blockIcon.getMaxV());
            tessellator.addVertexWithUV(1, 1 - offset, 1, blockIcon.getMaxU(), blockIcon.getMaxV());
            tessellator.addVertexWithUV(0, 1 - offset, 1, blockIcon.getMaxU(), blockIcon.getMinV());
        }

        tessellator.getMCTessellator().draw();
    }

    private boolean doRenderToSide(World world, int x, int y ,int z, TileTank tank){
        TileEntity tile = WorldHelper.getTileAt(world, new BlockPos(x, y, z));
        return !(tile instanceof TileTank && ((TileTank)tile).getClientRenderFluid() == tank.getClientRenderFluid());
    }
}
