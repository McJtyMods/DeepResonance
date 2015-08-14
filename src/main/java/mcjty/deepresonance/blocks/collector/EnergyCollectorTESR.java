package mcjty.deepresonance.blocks.collector;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.blocks.generator.GeneratorConfiguration;
import mcjty.gui.RenderHelper;
import mcjty.gui.RenderHelper.Vector;
import mcjty.varia.Coordinate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import java.util.Random;

@SideOnly(Side.CLIENT)
public class EnergyCollectorTESR extends TileEntitySpecialRenderer {
    IModelCustom model = AdvancedModelLoader.loadModel(new ResourceLocation(DeepResonance.MODID, "obj/collector.obj"));
    ResourceLocation blockTexture = new ResourceLocation(DeepResonance.MODID, "textures/blocks/energyCollector.png");
    ResourceLocation halo = new ResourceLocation(DeepResonance.MODID, "textures/effects/halo.png");
    ResourceLocation laserbeams[] = new ResourceLocation[4];
    Random random = new Random();

    public EnergyCollectorTESR() {
        laserbeams[0] = new ResourceLocation(DeepResonance.MODID, "textures/effects/laserbeam1.png");
        laserbeams[1] = new ResourceLocation(DeepResonance.MODID, "textures/effects/laserbeam2.png");
        laserbeams[2] = new ResourceLocation(DeepResonance.MODID, "textures/effects/laserbeam3.png");
        laserbeams[3] = new ResourceLocation(DeepResonance.MODID, "textures/effects/laserbeam4.png");
    }

    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float time) {
        bindTexture(blockTexture);

        GL11.glPushMatrix();
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        GL11.glTranslatef((float) x + 0.5F, (float) y + 0.0F, (float) z + 0.5F);

        model.renderAll();
        GL11.glPopMatrix();

        EnergyCollectorTileEntity energyCollectorTileEntity = (EnergyCollectorTileEntity) tileEntity;

        if ((!energyCollectorTileEntity.getCrystals().isEmpty()) && (energyCollectorTileEntity.areLasersActive() || energyCollectorTileEntity.getLaserStartup() > 0)) {
            boolean blending = GL11.glIsEnabled(GL11.GL_BLEND);
            boolean depthMask = GL11.glGetBoolean(GL11.GL_DEPTH_WRITEMASK);
            GL11.glDepthMask(false);

            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);

            GL11.glPushMatrix();
            GL11.glTranslatef((float) x + 0.5F, (float) y + 0.85F, (float) z + 0.5F);
            this.bindTexture(halo);
            RenderHelper.renderBillboardQuad(1.0f);
            GL11.glPopMatrix();

            Minecraft mc = Minecraft.getMinecraft();
            EntityClientPlayerMP p = mc.thePlayer;
            double doubleX = p.lastTickPosX + (p.posX - p.lastTickPosX) * time;
            double doubleY = p.lastTickPosY + (p.posY - p.lastTickPosY) * time;
            double doubleZ = p.lastTickPosZ + (p.posZ - p.lastTickPosZ) * time;

            Vector start = new Vector(tileEntity.xCoord + .5f, tileEntity.yCoord + .5f + .3f, tileEntity.zCoord + .5f);
            Vector player = new Vector((float) doubleX, (float) doubleY, (float) doubleZ);

            GL11.glPushMatrix();
            GL11.glTranslated(-doubleX, -doubleY, -doubleZ);

            Tessellator tessellator = Tessellator.instance;

            // ----------------------------------------

            this.bindTexture(laserbeams[random.nextInt(4)]);

            tessellator.startDrawingQuads();
            tessellator.setBrightness(240);

            float startupFactor = (float) energyCollectorTileEntity.getLaserStartup() / (float) GeneratorConfiguration.startupTime;

            for (Coordinate relative : energyCollectorTileEntity.getCrystals()) {
                Coordinate destination = new Coordinate(relative.getX() + tileEntity.xCoord, relative.getY() + tileEntity.yCoord, relative.getZ() + tileEntity.zCoord);
                Vector end = new Vector(destination.getX() + .5f, destination.getY() + .5f, destination.getZ() + .5f);

                // @todo Increase jitter if crystals are not pure

                if (startupFactor > .8f) {
                    // Do nothing
                } else if (startupFactor > .001f) {
                    Vector middle = new Vector(jitter(startupFactor, start.x, end.x), jitter(startupFactor, start.y, end.y), jitter(startupFactor, start.z, end.z));
                    RenderHelper.drawBeam(start, middle, player, .1f);
                    RenderHelper.drawBeam(middle, end, player, .1f);
                } else {
                    RenderHelper.drawBeam(start, end, player, .1f);
                }
            }

            tessellator.draw();

            GL11.glPopMatrix();

            if (!blending) {
                GL11.glDisable(GL11.GL_BLEND);
            }
            if (depthMask) {
                GL11.glDepthMask(true);
            }
        }
    }

    private float jitter(float startupFactor, float a1, float a2) {
        return (a1 + a2) / 2.0f + (random.nextFloat() * 2.0f - 1.0f) * startupFactor;
    }


}
