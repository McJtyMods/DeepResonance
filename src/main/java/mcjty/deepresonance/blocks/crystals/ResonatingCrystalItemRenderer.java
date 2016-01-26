package mcjty.deepresonance.blocks.crystals;

public class ResonatingCrystalItemRenderer{}/* implements IItemRenderer {
    IModelCustom model = AdvancedModelLoader.loadModel(new ResourceLocation(DeepResonance.MODID, "obj/crystal.obj"));
    ResourceLocation crystal = new ResourceLocation(DeepResonance.MODID, "textures/blocks/crystal.png");
    ResourceLocation emptyCrystal = new ResourceLocation(DeepResonance.MODID, "textures/blocks/emptycrystal.png");

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
        float power = 100.0f;
        if (item.getTagCompound() != null) {
            power = item.getTagCompound().getFloat("power");
        }
        if (power > EnergyCollectorTileEntity.CRYSTAL_MIN_POWER) {
            FMLClientHandler.instance().getClient().renderEngine.bindTexture(crystal);
        } else {
            FMLClientHandler.instance().getClient().renderEngine.bindTexture(emptyCrystal);
        }

        GL11.glPushMatrix();
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        boolean blending = GL11.glIsEnabled(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        GL11.glTranslatef(0.5F, 0.0F, 0.5F);
        GL11.glScalef(1.4F, 1.4F, 1.4F);
//        GL11.glScalef(0.09375F, 0.09375F, 0.09375F);

        model.renderAll();
        GL11.glPopMatrix();

        if (!blending) {
            GL11.glDisable(GL11.GL_BLEND);
        }
    }
}*/
