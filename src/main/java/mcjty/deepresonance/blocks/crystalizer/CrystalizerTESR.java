package mcjty.deepresonance.blocks.crystalizer;

import mcjty.deepresonance.DeepResonance;
import mcjty.lib.varia.BlockTools;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.TRSRTransformation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.io.IOException;

@SideOnly(Side.CLIENT)
public class CrystalizerTESR extends TileEntitySpecialRenderer<CrystalizerTileEntity> {

    ResourceLocation frontTexture = new ResourceLocation(DeepResonance.MODID, "blocks/crystalizer");

    private IModel model;
    private IBakedModel bakedModel;

    public CrystalizerTESR() {
        try {
            // Manually load our rotating crystal here
            model = ModelLoaderRegistry.getModel(new ResourceLocation(DeepResonance.MODID, "block/crystal_inside.obj"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private IBakedModel getBakedModel() {
        // Since we cannot bake in preInit() we do lazy baking of the model as soon as we need it
        // for rendering
        if (bakedModel == null) {
            bakedModel = model.bake(TRSRTransformation.identity(), DefaultVertexFormats.ITEM,
                                    location -> Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString()));
        }
        return bakedModel;
    }


    @Override
    public void renderTileEntityAt(CrystalizerTileEntity te, double x, double y, double z, float time, int breakTime) {
        GlStateManager.pushAttrib();
//        GL11.glPushAttrib(GL11.GL_CURRENT_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_ENABLE_BIT | GL11.GL_LIGHTING_BIT | GL11.GL_TEXTURE_BIT);

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

            bindTexture(TextureMap.locationBlocksTexture);

            Tessellator tessellator = Tessellator.getInstance();
            tessellator.getWorldRenderer().begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
            Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelRenderer().renderModel(
                    world,
                    getBakedModel(),
                    world.getBlockState(te.getPos()),
                    te.getPos(),
                    Tessellator.getInstance().getWorldRenderer());
            tessellator.draw();

            RenderHelper.enableStandardItemLighting();

            GlStateManager.popMatrix();

            GlStateManager.disableBlend();
        }

        // @todo remove once the multi layer thing works
        EnumFacing orientation = BlockTools.getOrientationHoriz(CrystalizerSetup.crystalizer.getMetaFromState(getWorld().getBlockState(te.getPos())));

        GlStateManager.pushMatrix();
        GlStateManager.translate((float) x, (float) y, (float) z);
        bindTexture(TextureMap.locationBlocksTexture);
        renderFront(Tessellator.getInstance(), orientation, te.getPos());
        GlStateManager.popMatrix();

        GlStateManager.popAttrib();
    }

    private void renderFront(Tessellator tessellator, EnumFacing orientation, BlockPos pos) {

        WorldRenderer renderer = tessellator.getWorldRenderer();
        renderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_LMAP_COLOR);
//        tessellator.setColorRGBA(255, 255, 255, 128);
//        tessellator.setBrightness(100);
//        int brightness = 240;
        int brightness = CrystalizerSetup.crystalizer.getMixedBrightnessForBlock(getWorld(), pos);
        int b1 = brightness >> 16 & 65535;
        int b2 = brightness & 65535;

        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        float offset = .05f;

        // ---------------------------------------------------------------
        // Render the inside of the tank
        // ---------------------------------------------------------------

        bindTexture(TextureMap.locationBlocksTexture);
        TextureAtlasSprite sprite = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(frontTexture.toString());

        switch (orientation) {
            case NORTH:
                //NORTH other side
                renderer.pos(1, 1, offset).tex(sprite.getMinU(), sprite.getMinV()).lightmap(b1, b2).color(255, 255, 255, 255).endVertex();
                renderer.pos(1, 0, offset).tex(sprite.getMinU(), sprite.getMaxV()).lightmap(b1, b2).color(255, 255, 255, 255).endVertex();
                renderer.pos(0, 0, offset).tex(sprite.getMaxU(), sprite.getMaxV()).lightmap(b1, b2).color(255, 255, 255, 255).endVertex();
                renderer.pos(0, 1, offset).tex(sprite.getMaxU(), sprite.getMinV()).lightmap(b1, b2).color(255, 255, 255, 255).endVertex();
                break;
            case SOUTH:
                //SOUTH other side
                renderer.pos(1, 0, 1-offset).tex(sprite.getMinU(), sprite.getMaxV()).lightmap(b1, b2).color(255, 255, 255, 255).endVertex();
                renderer.pos(1, 1, 1-offset).tex(sprite.getMinU(), sprite.getMinV()).lightmap(b1, b2).color(255, 255, 255, 255).endVertex();
                renderer.pos(0, 1, 1-offset).tex(sprite.getMaxU(), sprite.getMinV()).lightmap(b1, b2).color(255, 255, 255, 255).endVertex();
                renderer.pos(0, 0, 1-offset).tex(sprite.getMaxU(), sprite.getMaxV()).lightmap(b1, b2).color(255, 255, 255, 255).endVertex();
                break;
            case WEST:
                //EAST other side
                renderer.pos(offset, 1, 0).tex(sprite.getMaxU(), sprite.getMinV()).lightmap(b1, b2).color(255, 255, 255, 255).endVertex();
                renderer.pos(offset, 0, 0).tex(sprite.getMaxU(), sprite.getMaxV()).lightmap(b1, b2).color(255, 255, 255, 255).endVertex();
                renderer.pos(offset, 0, 1).tex(sprite.getMinU(), sprite.getMaxV()).lightmap(b1, b2).color(255, 255, 255, 255).endVertex();
                renderer.pos(offset, 1, 1).tex(sprite.getMinU(), sprite.getMinV()).lightmap(b1, b2).color(255, 255, 255, 255).endVertex();
                break;
            case EAST:
                //WEST other side
                renderer.pos(1-offset, 0, 0).tex(sprite.getMaxU(), sprite.getMaxV()).lightmap(b1, b2).color(255, 255, 255, 255).endVertex();
                renderer.pos(1-offset, 1, 0).tex(sprite.getMaxU(), sprite.getMinV()).lightmap(b1, b2).color(255, 255, 255, 255).endVertex();
                renderer.pos(1-offset, 1, 1).tex(sprite.getMinU(), sprite.getMinV()).lightmap(b1, b2).color(255, 255, 255, 255).endVertex();
                renderer.pos(1-offset, 0, 1).tex(sprite.getMinU(), sprite.getMaxV()).lightmap(b1, b2).color(255, 255, 255, 255).endVertex();
                break;
            case DOWN:
            case UP:
            default:
                break;
        }

        tessellator.draw();

        GlStateManager.disableBlend();
    }
}
