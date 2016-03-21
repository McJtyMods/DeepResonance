package mcjty.deepresonance.blocks.tank;

import elec332.core.client.RenderHelper;
import elec332.core.world.WorldHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class TankTESR extends TileEntitySpecialRenderer<TileTank> {

    @Override
    public void renderTileEntityAt(TileTank tileTank, double x, double y, double z, float time, int breakTime) {
//        GL11.glPushAttrib(GL11.GL_CURRENT_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_ENABLE_BIT | GL11.GL_LIGHTING_BIT | GL11.GL_TEXTURE_BIT);
        GlStateManager.pushAttrib();

        Tessellator tessellator = Tessellator.getInstance();

        net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();

        GlStateManager.disableRescaleNormal();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableBlend();

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);

        bindTexture(TextureMap.locationBlocksTexture);
        World world = tileTank.getWorld();
        renderTankInside(tessellator, world, tileTank.getPos(), tileTank);

        Fluid renderFluid = tileTank.getClientRenderFluid();
        if (renderFluid != null) {
            renderFluid(tileTank, tessellator, renderFluid);
        }

        net.minecraft.client.renderer.RenderHelper.enableStandardItemLighting();

        GlStateManager.popMatrix();
        GlStateManager.popAttrib();
    }

    // ---------------------------------------------------------------
    // Render the fluid inside the tank
    // ---------------------------------------------------------------
    private void renderFluid(TileTank tileTank, Tessellator tessellator, Fluid renderFluid) {
        WorldRenderer renderer = tessellator.getWorldRenderer();

        float offset = 0.002f;

        TextureAtlasSprite fluid = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(renderFluid.getStill().toString());
        fluid = RenderHelper.checkIcon(fluid);

        renderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);

//        tessellator.setColorRGBA(255, 255, 255, 128);
//        tessellator.setBrightness(240);

        float scale = tileTank.getRenderHeight();
        float u1 = fluid.getMinU();
        float v1 = fluid.getMinV();
        float u2 = fluid.getMaxU();
        float v2 = fluid.getMaxV();

        if (scale > 0.0f) {

            // Top
            renderer.pos(0, scale - offset, 0).tex(u1, v1).color(255, 255, 255, 128).endVertex();
            renderer.pos(0, scale - offset, 1).tex(u1, v2).color(255, 255, 255, 128).endVertex();
            renderer.pos(1, scale - offset, 1).tex(u2, v2).color(255, 255, 255, 128).endVertex();
            renderer.pos(1, scale - offset, 0).tex(u2, v1).color(255, 255, 255, 128).endVertex();
            
            if (scale > 3/16f) {

                if (scale > 1 - 3/16f)
                    scale = 1 - 3/16f;

                v2 -= (fluid.getMaxV() - fluid.getMinV()) * (1 - scale);

                //NORTH
                renderer.pos(1 - 3 / 16f, scale, -offset).tex(u1, v1).color(255, 255, 255, 128).endVertex();
                renderer.pos(1 - 3 / 16f, 3 / 16f, -offset).tex(u1, v2).color(255, 255, 255, 128).endVertex();
                renderer.pos(3 / 16f, 3 / 16f, -offset).tex(u2, v2).color(255, 255, 255, 128).endVertex();
                renderer.pos(3 / 16f, scale, -offset).tex(u2, v1).color(255, 255, 255, 128).endVertex();

                //EAST
                renderer.pos(-offset, 3 / 16f, 1 - 3 / 16f).tex(u1, v2).color(255, 255, 255, 128).endVertex();
                renderer.pos(-offset, scale, 1 - 3 / 16f).tex(u1, v1).color(255, 255, 255, 128).endVertex();
                renderer.pos(-offset, scale, 3 / 16f).tex(u2, v1).color(255, 255, 255, 128).endVertex();
                renderer.pos(-offset, 3 / 16f, 3 / 16f).tex(u2, v2).color(255, 255, 255, 128).endVertex();

                //SOUTH
                renderer.pos(1 - 3 / 16f, 3 / 16f, 1 + offset).tex(u1, v2).color(255, 255, 255, 128).endVertex();
                renderer.pos(1 - 3 / 16f, scale, 1 + offset).tex(u1, v1).color(255, 255, 255, 128).endVertex();
                renderer.pos(3 / 16f, scale, 1 + offset).tex(u2, v1).color(255, 255, 255, 128).endVertex();
                renderer.pos(3 / 16f, 3 / 16f, 1 + offset).tex(u2, v2).color(255, 255, 255, 128).endVertex();

                //WEST
                renderer.pos(1 + offset, scale, 1 - 3 / 16f).tex(u1, v1).color(255, 255, 255, 128).endVertex();
                renderer.pos(1 + offset, 3 / 16f, 1 - 3 / 16f).tex(u1, v2).color(255, 255, 255, 128).endVertex();
                renderer.pos(1 + offset, 3 / 16f, 3 / 16f).tex(u2, v2).color(255, 255, 255, 128).endVertex();
                renderer.pos(1 + offset, scale, 3 / 16f).tex(u2, v1).color(255, 255, 255, 128).endVertex();
            }
        }
        tessellator.draw();
    }

    private void renderTankInside(Tessellator tessellator, World world, BlockPos pos, TileTank tank) {
        WorldRenderer renderer = tessellator.getWorldRenderer();

        float offset = 0.002f;
        int ix = pos.getX(), iy = pos.getY(), iz = pos.getZ();

        renderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
//        tessellator.setColorRGBA(255, 255, 255, 128);
//        tessellator.setBrightness(100);

        // ---------------------------------------------------------------
        // Render the inside of the tank
        // ---------------------------------------------------------------
        TextureAtlasSprite blockIcon = TankSetup.tank.getSideIcon();

        //NORTH other side
        if (doRenderToSide(world, ix, iy, iz-1, tank)) {
            renderer.pos(1, 0, offset).tex(blockIcon.getMinU(), blockIcon.getMinV()).color(255, 255, 255, 128).endVertex();
            renderer.pos(1, 1, offset).tex(blockIcon.getMinU(), blockIcon.getMaxV()).color(255, 255, 255, 128).endVertex();
            renderer.pos(0, 1, offset).tex(blockIcon.getMaxU(), blockIcon.getMaxV()).color(255, 255, 255, 128).endVertex();
            renderer.pos(0, 0, offset).tex(blockIcon.getMaxU(), blockIcon.getMinV()).color(255, 255, 255, 128).endVertex();
        }

        //SOUTH other side
        if (doRenderToSide(world, ix, iy, iz+1, tank)) {
            renderer.pos(1, 1, 1 - offset).tex(blockIcon.getMinU(), blockIcon.getMinV()).color(255, 255, 255, 128).endVertex();
            renderer.pos(1, 0, 1 - offset).tex(blockIcon.getMinU(), blockIcon.getMaxV()).color(255, 255, 255, 128).endVertex();
            renderer.pos(0, 0, 1 - offset).tex(blockIcon.getMaxU(), blockIcon.getMaxV()).color(255, 255, 255, 128).endVertex();
            renderer.pos(0, 1, 1 - offset).tex(blockIcon.getMaxU(), blockIcon.getMinV()).color(255, 255, 255, 128).endVertex();
        }

        //EAST other side
        if (doRenderToSide(world, ix-1, iy, iz, tank)) {
            renderer.pos(offset, 1, 1).tex(blockIcon.getMinU(), blockIcon.getMinV()).color(255, 255, 255, 128).endVertex();
            renderer.pos(offset, 0, 1).tex(blockIcon.getMinU(), blockIcon.getMaxV()).color(255, 255, 255, 128).endVertex();
            renderer.pos(offset, 0, 0).tex(blockIcon.getMaxU(), blockIcon.getMaxV()).color(255, 255, 255, 128).endVertex();
            renderer.pos(offset, 1, 0).tex(blockIcon.getMaxU(), blockIcon.getMinV()).color(255, 255, 255, 128).endVertex();
        }

        //WEST other side
        if (doRenderToSide(world, ix+1, iy, iz, tank)) {
            renderer.pos(1 - offset, 0, 1).tex(blockIcon.getMinU(), blockIcon.getMinV()).color(255, 255, 255, 128).endVertex();
            renderer.pos(1 - offset, 1, 1).tex(blockIcon.getMinU(), blockIcon.getMaxV()).color(255, 255, 255, 128).endVertex();
            renderer.pos(1 - offset, 1, 0).tex(blockIcon.getMaxU(), blockIcon.getMaxV()).color(255, 255, 255, 128).endVertex();
            renderer.pos(1 - offset, 0, 0).tex(blockIcon.getMaxU(), blockIcon.getMinV()).color(255, 255, 255, 128).endVertex();
        }

        // Bottom other side
        if (doRenderToSide(world, ix, iy-1, iz, tank)) {
            blockIcon = TankSetup.tank.getBottomIcon();
            renderer.pos(0, offset, 0).tex(blockIcon.getMinU(), blockIcon.getMinV()).color(255, 255, 255, 128).endVertex();
            renderer.pos(0, offset, 1).tex(blockIcon.getMinU(), blockIcon.getMaxV()).color(255, 255, 255, 128).endVertex();
            renderer.pos(1, offset, 1).tex(blockIcon.getMaxU(), blockIcon.getMaxV()).color(255, 255, 255, 128).endVertex();
            renderer.pos(1, offset, 0).tex(blockIcon.getMaxU(), blockIcon.getMinV()).color(255, 255, 255, 128).endVertex();
        }

        // Top other side
        if (doRenderToSide(world, ix, iy+1, iz, tank)) {
            blockIcon = TankSetup.tank.getTopIcon();
            renderer.pos(0, 1 - offset, 0).tex(blockIcon.getMinU(), blockIcon.getMinV()).color(255, 255, 255, 128).endVertex();
            renderer.pos(1, 1 - offset, 0).tex(blockIcon.getMinU(), blockIcon.getMaxV()).color(255, 255, 255, 128).endVertex();
            renderer.pos(1, 1 - offset, 1).tex(blockIcon.getMaxU(), blockIcon.getMaxV()).color(255, 255, 255, 128).endVertex();
            renderer.pos(0, 1 - offset, 1).tex(blockIcon.getMaxU(), blockIcon.getMinV()).color(255, 255, 255, 128).endVertex();
        }

        tessellator.draw();
    }

    private boolean doRenderToSide(World world, int x, int y ,int z, TileTank tank){
        TileEntity tile = WorldHelper.getTileAt(world, new BlockPos(x, y, z));
        return !(tile instanceof TileTank && ((TileTank)tile).getClientRenderFluid() == tank.getClientRenderFluid());
    }
}
