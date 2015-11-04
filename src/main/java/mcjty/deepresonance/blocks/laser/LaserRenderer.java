package mcjty.deepresonance.blocks.laser;

import mcjty.deepresonance.DeepResonance;
import mcjty.lib.gui.RenderHelper;
import mcjty.lib.varia.BlockTools;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.ForgeDirection;
import org.lwjgl.opengl.GL11;

public class LaserRenderer extends TileEntitySpecialRenderer {

    private static final ResourceLocation bluelaser = new ResourceLocation(DeepResonance.MODID, "textures/effects/blueLaserbeam.png");
    private static final ResourceLocation redlaser = new ResourceLocation(DeepResonance.MODID, "textures/effects/redLaserbeam.png");
    private static final ResourceLocation greenlaser = new ResourceLocation(DeepResonance.MODID, "textures/effects/greenLaserbeam.png");
    private static final ResourceLocation yellowlaser = new ResourceLocation(DeepResonance.MODID, "textures/effects/yellowLaserbeam.png");

    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float f) {
        Tessellator tessellator = Tessellator.instance;

        LaserTileEntity laserTileEntity = (LaserTileEntity) tileEntity;
        int color = laserTileEntity.getColor();
        if (color != 0) {
            int meta = tileEntity.getBlockMetadata();
            ForgeDirection direction = BlockTools.getOrientationHoriz(meta);
            float destX = tileEntity.xCoord + 0.5f + direction.offsetX*2.5f;
            float destY = tileEntity.yCoord + 0.5f;
            float destZ = tileEntity.zCoord + 0.5f + direction.offsetZ*2.5f;
            GL11.glPushAttrib(GL11.GL_CURRENT_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_ENABLE_BIT | GL11.GL_LIGHTING_BIT | GL11.GL_TEXTURE_BIT);

            tessellator.startDrawingQuads();
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glDepthMask(false);
            tessellator.setColorRGBA(255, 255, 255, 128);
            tessellator.setBrightness(240);
            GL11.glPushMatrix();
            switch (color) {
                case LaserTileEntity.COLOR_BLUE: this.bindTexture(bluelaser); break;
                case LaserTileEntity.COLOR_RED: this.bindTexture(redlaser); break;
                case LaserTileEntity.COLOR_GREEN: this.bindTexture(greenlaser); break;
                case LaserTileEntity.COLOR_YELLOW: this.bindTexture(yellowlaser); break;
            }

            Minecraft mc = Minecraft.getMinecraft();
            EntityClientPlayerMP p = mc.thePlayer;
            double doubleX = p.lastTickPosX + (p.posX - p.lastTickPosX) * f;
            double doubleY = p.lastTickPosY + (p.posY - p.lastTickPosY) * f;
            double doubleZ = p.lastTickPosZ + (p.posZ - p.lastTickPosZ) * f;
            GL11.glTranslated(-doubleX, -doubleY, -doubleZ);

            RenderHelper.Vector start = new RenderHelper.Vector(tileEntity.xCoord + .5f, tileEntity.yCoord + .5f, tileEntity.zCoord + .5f);
            RenderHelper.Vector end = new RenderHelper.Vector(destX, destY, destZ);
            RenderHelper.Vector player = new RenderHelper.Vector((float) doubleX, (float) doubleY, (float) doubleZ);
            RenderHelper.drawBeam(start, end, player, .2f);

            tessellator.draw();
            GL11.glPopMatrix();

            GL11.glPopAttrib();
        }
    }
}

