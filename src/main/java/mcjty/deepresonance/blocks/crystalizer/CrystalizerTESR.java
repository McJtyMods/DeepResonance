package mcjty.deepresonance.blocks.crystalizer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.blocks.ModBlocks;
import mcjty.deepresonance.config.ConfigMachines;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

@SideOnly(Side.CLIENT)
public class CrystalizerTESR extends TileEntitySpecialRenderer {
    IModelCustom model = AdvancedModelLoader.loadModel(new ResourceLocation(DeepResonance.MODID, "obj/crystal.obj"));
    ResourceLocation texture = new ResourceLocation(DeepResonance.MODID, "textures/blocks/crystal.png");

    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float time) {
        if (tileEntity instanceof CrystalizerTileEntity) {
            CrystalizerTileEntity crystalizerTileEntity = (CrystalizerTileEntity) tileEntity;
            int progress = crystalizerTileEntity.getProgress();
            boolean hasCrystal = crystalizerTileEntity.hasCrystal();

            if (hasCrystal || progress > 0) {
                bindTexture(texture);

                GL11.glPushMatrix();
                GL11.glEnable(GL12.GL_RESCALE_NORMAL);
                //        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

                GL11.glTranslatef((float) x + 0.5F, (float) y + 0.4F, (float) z + 0.5F);
                GL11.glScalef(0.4f, 0.4f, 0.4f);

                if (0 < progress && progress < ConfigMachines.Crystalizer.processTime) {
                    float t = (System.currentTimeMillis() % 10000) / 10000.0f;
                    GL11.glRotatef(t * 360.0f, 0.0F, 1.0F, 0.0F);
                }

                model.renderAll();

                GL11.glPopMatrix();

                GL11.glDisable(GL11.GL_BLEND);
            }

            GL11.glPushMatrix();
            GL11.glTranslatef((float) x, (float) y, (float) z);
            bindTexture(TextureMap.locationBlocksTexture);
            renderInside(Tessellator.instance);
            GL11.glPopMatrix();
        }
    }

    private void renderInside(Tessellator tessellator) {
        float offset = 0.002f;

        tessellator.startDrawingQuads();
        tessellator.setColorRGBA(255, 255, 255, 128);
        tessellator.setBrightness(100);

        // ---------------------------------------------------------------
        // Render the inside of the tank
        // ---------------------------------------------------------------
        IIcon blockIcon;

        //NORTH other side
        blockIcon = ModBlocks.crystalizer.getSideIcon();
        tessellator.addVertexWithUV(1, 0, offset, blockIcon.getMinU(), blockIcon.getMinV());
        tessellator.addVertexWithUV(1, 1, offset, blockIcon.getMinU(), blockIcon.getMaxV());
        tessellator.addVertexWithUV(0, 1, offset, blockIcon.getMaxU(), blockIcon.getMaxV());
        tessellator.addVertexWithUV(0, 0, offset, blockIcon.getMaxU(), blockIcon.getMinV());

        //SOUTH other side
        blockIcon = ModBlocks.crystalizer.getSouthIcon();
        tessellator.addVertexWithUV(1, 1, 1 - offset, blockIcon.getMinU(), blockIcon.getMinV());
        tessellator.addVertexWithUV(1, 0, 1 - offset, blockIcon.getMinU(), blockIcon.getMaxV());
        tessellator.addVertexWithUV(0, 0, 1 - offset, blockIcon.getMaxU(), blockIcon.getMaxV());
        tessellator.addVertexWithUV(0, 1, 1 - offset, blockIcon.getMaxU(), blockIcon.getMinV());

        //EAST other side
        blockIcon = ModBlocks.crystalizer.getSideIcon();
        tessellator.addVertexWithUV(offset, 1, 1, blockIcon.getMinU(), blockIcon.getMinV());
        tessellator.addVertexWithUV(offset, 0, 1, blockIcon.getMinU(), blockIcon.getMaxV());
        tessellator.addVertexWithUV(offset, 0, 0, blockIcon.getMaxU(), blockIcon.getMaxV());
        tessellator.addVertexWithUV(offset, 1, 0, blockIcon.getMaxU(), blockIcon.getMinV());

        //WEST other side
        blockIcon = ModBlocks.crystalizer.getSideIcon();
        tessellator.addVertexWithUV(1 - offset, 0, 1, blockIcon.getMinU(), blockIcon.getMinV());
        tessellator.addVertexWithUV(1 - offset, 1, 1, blockIcon.getMinU(), blockIcon.getMaxV());
        tessellator.addVertexWithUV(1 - offset, 1, 0, blockIcon.getMaxU(), blockIcon.getMaxV());
        tessellator.addVertexWithUV(1 - offset, 0, 0, blockIcon.getMaxU(), blockIcon.getMinV());

        // Bottom other side. Raised a bit
        blockIcon = ModBlocks.crystalizer.getBottomIcon();
        tessellator.addVertexWithUV(0, .4f, 0, blockIcon.getMinU(), blockIcon.getMinV());
        tessellator.addVertexWithUV(0, .4f, 1, blockIcon.getMinU(), blockIcon.getMaxV());
        tessellator.addVertexWithUV(1, .4f, 1, blockIcon.getMaxU(), blockIcon.getMaxV());
        tessellator.addVertexWithUV(1, .4f, 0, blockIcon.getMaxU(), blockIcon.getMinV());

        // Top other side
        blockIcon = ModBlocks.crystalizer.getTopIcon();
        tessellator.addVertexWithUV(0, 1 - offset, 0, blockIcon.getMinU(), blockIcon.getMinV());
        tessellator.addVertexWithUV(1, 1 - offset, 0, blockIcon.getMinU(), blockIcon.getMaxV());
        tessellator.addVertexWithUV(1, 1 - offset, 1, blockIcon.getMaxU(), blockIcon.getMaxV());
        tessellator.addVertexWithUV(0, 1 - offset, 1, blockIcon.getMaxU(), blockIcon.getMinV());

        tessellator.draw();
    }
}
