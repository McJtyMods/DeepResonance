package mcjty.deepresonance.blocks.crystals;

import mcjty.deepresonance.DeepResonance;
import mcjty.lib.gui.RenderHelper;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class ResonatingCrystalTESR extends TileEntitySpecialRenderer<ResonatingCrystalTileEntity> {
    ResourceLocation redhalo = new ResourceLocation(DeepResonance.MODID, "textures/effects/redhalo.png");

    @Override
    public void renderTileEntityAt(ResonatingCrystalTileEntity tileEntity, double x, double y, double z, float time, int breakTime) {

        if (tileEntity.isGlowing()) {
            GlStateManager.pushMatrix();

            GlStateManager.enableRescaleNormal();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

            GlStateManager.enableBlend();

            GlStateManager.translate((float) x + 0.5F, (float) y + 0.5F, (float) z + 0.5F);
            GlStateManager.blendFunc(GL11.GL_ONE, GL11.GL_ONE);
            this.bindTexture(redhalo);
            RenderHelper.renderBillboardQuadBright(0.6f);

            GlStateManager.popMatrix();

            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        }
    }
}
