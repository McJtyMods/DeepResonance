package mcjty.deepresonance.blocks.crystals;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.blocks.collector.EnergyCollectorTileEntity;
import mcjty.gui.RenderHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

@SideOnly(Side.CLIENT)
public class ResonatingCrystalTESR extends TileEntitySpecialRenderer {
    IModelCustom model = AdvancedModelLoader.loadModel(new ResourceLocation(DeepResonance.MODID, "obj/crystal.obj"));
    ResourceLocation crystal = new ResourceLocation(DeepResonance.MODID, "textures/blocks/crystal.png");
    ResourceLocation emptyCrystal = new ResourceLocation(DeepResonance.MODID, "textures/blocks/emptycrystal.png");
    ResourceLocation redhalo = new ResourceLocation(DeepResonance.MODID, "textures/effects/redhalo.png");

    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float time) {
        ResonatingCrystalTileEntity resonatingCrystalTileEntity = (ResonatingCrystalTileEntity) tileEntity;
        if (resonatingCrystalTileEntity.getPower() > EnergyCollectorTileEntity.CRYSTAL_MIN_POWER) {
            bindTexture(crystal);
        } else {
            bindTexture(emptyCrystal);
        }

        GL11.glPushMatrix();
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        boolean blending = GL11.glIsEnabled(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        GL11.glTranslatef((float) x + 0.5F, (float) y + 0.0F, (float) z + 0.5F);
//        GL11.glScalef(0.09375F, 0.09375F, 0.09375F);

        model.renderAll();

        if (resonatingCrystalTileEntity.isGlowing()) {
            GL11.glTranslatef(0.0f, 0.5f, 0.0f);
            GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);
            this.bindTexture(redhalo);
            RenderHelper.renderBillboardQuad(0.6f);
        }

        GL11.glPopMatrix();

        if (!blending) {
            GL11.glDisable(GL11.GL_BLEND);
        }
    }
}
