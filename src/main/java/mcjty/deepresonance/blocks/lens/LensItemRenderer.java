package mcjty.deepresonance.blocks.lens;

import cpw.mods.fml.client.FMLClientHandler;
import mcjty.deepresonance.DeepResonance;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class LensItemRenderer implements IItemRenderer {
    IModelCustom model = AdvancedModelLoader.loadModel(new ResourceLocation(DeepResonance.MODID, "obj/lens.obj"));
    ResourceLocation texture = new ResourceLocation(DeepResonance.MODID, "textures/blocks/lens.png");

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return true;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return true;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(texture);

        GL11.glPushMatrix();
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        GL11.glTranslatef(0.5F, 0.0F, 0.5F);

        model.renderAll();
        GL11.glPopMatrix();
    }
}
