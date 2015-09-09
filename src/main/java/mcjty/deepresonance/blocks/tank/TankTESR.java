package mcjty.deepresonance.blocks.tank;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mcjty.deepresonance.client.render.DefaultISBRH;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class TankTESR extends TileEntitySpecialRenderer {
    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float time) {
        World world = tileEntity.getWorldObj();
        RenderBlocks renderer = new RenderBlocks(world);

        Block block = world.getBlock((int) x, (int) y, (int) z);
//        renderer.renderStandardBlock(block, (int) x, (int) y, (int) z);

        if (tileEntity instanceof TileTank) {
            TileTank tileTank = (TileTank) tileEntity;
            Fluid renderFluid = tileTank.getClientRenderFluid();
            if (renderFluid != null) {
                Tessellator tessellator = Tessellator.instance;

                IIcon fluid = renderFluid.getStillIcon();
                fluid = renderer.getIconSafe(fluid);

                GL11.glPushMatrix();
                GL11.glTranslated(x, y, z);
                tessellator.startDrawingQuads();
                tessellator.setColorRGBA(255, 255, 255, 128);
                tessellator.setBrightness(240);

                bindTexture(TextureMap.locationBlocksTexture);
//                tessellator.setColorOpaque_I(renderFluid.getColor());
//                tessellator.setBrightness(240);

                float scale = 1.0f;
                float offset = 0.0f;
                DefaultISBRH.addSide(tessellator, ForgeDirection.NORTH.ordinal(), scale, offset, fluid.getMinU(), fluid.getMinV(), fluid.getMaxU(), fluid.getMaxV());
                DefaultISBRH.addSide(tessellator, ForgeDirection.SOUTH.ordinal(), scale, offset, fluid.getMinU(), fluid.getMinV(), fluid.getMaxU(), fluid.getMaxV());
                DefaultISBRH.addSide(tessellator, ForgeDirection.WEST.ordinal(), scale, offset, fluid.getMinU(), fluid.getMinV(), fluid.getMaxU(), fluid.getMaxV());
                DefaultISBRH.addSide(tessellator, ForgeDirection.EAST.ordinal(), scale, offset, fluid.getMinU(), fluid.getMinV(), fluid.getMaxU(), fluid.getMaxV());

                tessellator.draw();
                GL11.glPopMatrix();
            }
        }
    }
}
