package mcjty.deepresonance.blocks.laser;

import mcjty.deepresonance.DeepResonance;
import mcjty.lib.gui.RenderHelper;
import mcjty.lib.varia.Coordinate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class LaserRenderer extends TileEntitySpecialRenderer {

    private static final ResourceLocation bluelaser = new ResourceLocation(DeepResonance.MODID, "textures/effects/blueLaserbeam.png");
    private static final ResourceLocation redlaser = new ResourceLocation(DeepResonance.MODID, "textures/effects/redLaserbeam.png");

    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float f) {
        Tessellator tessellator = Tessellator.instance;

        LaserTileEntity laserTileEntity = (LaserTileEntity) tileEntity;
        Coordinate destination = new Coordinate(tileEntity.xCoord + 3, tileEntity.yCoord, tileEntity.zCoord);//laserTileEntity.getDestination();
        if (destination != null) {
            int meta = tileEntity.getWorldObj().getBlockMetadata(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord);
//            if ((meta & BlockTools.MASK_REDSTONE) != 0) {
                GL11.glPushAttrib(GL11.GL_CURRENT_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_ENABLE_BIT | GL11.GL_LIGHTING_BIT | GL11.GL_TEXTURE_BIT);

                tessellator.startDrawingQuads();
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);
                GL11.glEnable(GL11.GL_DEPTH_TEST);
                GL11.glDepthMask(false);
                tessellator.setColorRGBA(255, 255, 255, 128);
                tessellator.setBrightness(240);
                GL11.glPushMatrix();
                this.bindTexture(((tileEntity.yCoord & 1) == 0) ? bluelaser : redlaser);

                Minecraft mc = Minecraft.getMinecraft();
                EntityClientPlayerMP p = mc.thePlayer;
                double doubleX = p.lastTickPosX + (p.posX - p.lastTickPosX) * f;
                double doubleY = p.lastTickPosY + (p.posY - p.lastTickPosY) * f;
                double doubleZ = p.lastTickPosZ + (p.posZ - p.lastTickPosZ) * f;
                GL11.glTranslated(-doubleX, -doubleY, -doubleZ);

                RenderHelper.Vector start = new RenderHelper.Vector(tileEntity.xCoord + .5f, tileEntity.yCoord + .5f, tileEntity.zCoord + .5f);
                RenderHelper.Vector end = new RenderHelper.Vector(destination.getX() + .0f/*@@@.5f*/, destination.getY() + .5f, destination.getZ() + .5f);
                RenderHelper.Vector player = new RenderHelper.Vector((float) doubleX, (float) doubleY, (float) doubleZ);
                RenderHelper.drawBeam(start, end, player, (meta & 1) != 0 ? .2f : .1f);

                tessellator.draw();
                GL11.glPopMatrix();

                GL11.glPopAttrib();
//            }
        }
    }
}

