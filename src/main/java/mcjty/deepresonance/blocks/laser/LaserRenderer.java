package mcjty.deepresonance.blocks.laser;

import mcjty.deepresonance.DeepResonance;
import mcjty.lib.gui.RenderHelper;
import mcjty.lib.varia.BlockTools;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class LaserRenderer extends TileEntitySpecialRenderer<LaserTileEntity> {

    private static final ResourceLocation bluelaser = new ResourceLocation(DeepResonance.MODID, "textures/effects/blueLaserbeam.png");
    private static final ResourceLocation redlaser = new ResourceLocation(DeepResonance.MODID, "textures/effects/redLaserbeam.png");
    private static final ResourceLocation greenlaser = new ResourceLocation(DeepResonance.MODID, "textures/effects/greenLaserbeam.png");
    private static final ResourceLocation yellowlaser = new ResourceLocation(DeepResonance.MODID, "textures/effects/yellowLaserbeam.png");

    @Override
    public void renderTileEntityAt(LaserTileEntity tileEntity, double x, double y, double z, float f, int breakState) {
        Tessellator tessellator = Tessellator.getInstance();

        int color = tileEntity.getColor();
        if (color != 0) {
            int meta = tileEntity.getBlockMetadata();
            EnumFacing direction = BlockTools.getOrientationHoriz(meta);
            float destX = tileEntity.getPos().getX() + 0.5f + direction.getFrontOffsetX()*2.5f;
            float destY = tileEntity.getPos().getY() + 0.5f;
            float destZ = tileEntity.getPos().getZ() + 0.5f + direction.getFrontOffsetZ()*2.5f;
//            GL11.glPushAttrib(GL11.GL_CURRENT_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_ENABLE_BIT | GL11.GL_LIGHTING_BIT | GL11.GL_TEXTURE_BIT);

            tessellator.getBuffer().begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_LMAP_COLOR);
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GL11.GL_ONE, GL11.GL_ONE);
            GlStateManager.enableDepth();
            GlStateManager.depthMask(false);
            GlStateManager.pushMatrix();
            switch (color) {
                case LaserTileEntity.COLOR_BLUE: this.bindTexture(bluelaser); break;
                case LaserTileEntity.COLOR_RED: this.bindTexture(redlaser); break;
                case LaserTileEntity.COLOR_GREEN: this.bindTexture(greenlaser); break;
                case LaserTileEntity.COLOR_YELLOW: this.bindTexture(yellowlaser); break;
            }

            Minecraft mc = Minecraft.getMinecraft();
            EntityPlayerSP p = mc.thePlayer;
            double doubleX = p.lastTickPosX + (p.posX - p.lastTickPosX) * f;
            double doubleY = p.lastTickPosY + (p.posY - p.lastTickPosY) * f;
            double doubleZ = p.lastTickPosZ + (p.posZ - p.lastTickPosZ) * f;
            GlStateManager.translate(-doubleX, -doubleY, -doubleZ);

            RenderHelper.Vector start = new RenderHelper.Vector(tileEntity.getPos().getX() + .5f, tileEntity.getPos().getY() + .5f, tileEntity.getPos().getZ() + .5f);
            RenderHelper.Vector end = new RenderHelper.Vector(destX, destY, destZ);
            RenderHelper.Vector player = new RenderHelper.Vector((float) doubleX, (float) doubleY + p.getEyeHeight(), (float) doubleZ);
            RenderHelper.drawBeam(start, end, player, .2f);

            tessellator.draw();
            GlStateManager.popMatrix();

            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        }
    }
}

