package mcjty.deepresonance.blocks.crystalizer;

import mcjty.deepresonance.DeepResonance;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class CrystalizerTESR extends TileEntitySpecialRenderer<CrystalizerTileEntity> {

    ResourceLocation frontTexture = new ResourceLocation(DeepResonance.MODID, "blocks/crystalizer");

    private IModel model;
    private IBakedModel bakedModel;

    public CrystalizerTESR() {
        // Manually load our rotating crystal here
    }

    private IBakedModel getBakedModel() {
        // Since we cannot bake in preInit() we do lazy baking of the model as soon as we need it
        // for rendering
        if (bakedModel == null) {
            try {
                model = ModelLoaderRegistry.getModel(new ResourceLocation(DeepResonance.MODID, "block/crystal_inside.obj"));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            bakedModel = model.bake(TRSRTransformation.identity(), DefaultVertexFormats.ITEM,
                                    location -> Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString()));
        }
        return bakedModel;
    }

    @Override
    public void render(CrystalizerTileEntity te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        int progress = te.getProgress();
        boolean hasCrystal = te.hasCrystal();

        if (hasCrystal || progress > 0) {
            GlStateManager.pushMatrix();
            GlStateManager.enableRescaleNormal();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

            GlStateManager.translate((float) x + 0.5F, (float) y + 0.4F, (float) z + 0.5F);
            GlStateManager.scale(0.4f, 0.4f, 0.4f);

            if (0 < progress && progress < CrystalizerTileEntity.getTotalProgress()) {
                float t = (System.currentTimeMillis() % 10000) / 10000.0f;
                GlStateManager.rotate(t * 360.0f, 0.0F, 1.0F, 0.0F);
            }

            World world = te.getWorld();
            // Translate back to local view coordinates so that we can do the acual rendering here
            GlStateManager.translate(-te.getPos().getX(), -te.getPos().getY(), -te.getPos().getZ());

            bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

            Tessellator tessellator = Tessellator.getInstance();
            tessellator.getBuffer().begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
            Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelRenderer().renderModel(
                    world,
                    getBakedModel(),
                    world.getBlockState(te.getPos()),
                    te.getPos(),
                    Tessellator.getInstance().getBuffer(), false);
            tessellator.draw();

            RenderHelper.enableStandardItemLighting();

            GlStateManager.popMatrix();

            GlStateManager.disableBlend();
        }

//        GlStateManager.popAttrib();
    }

    public static void register() {
        ClientRegistry.bindTileEntitySpecialRenderer(CrystalizerTileEntity.class, new CrystalizerTESR());
    }
}
