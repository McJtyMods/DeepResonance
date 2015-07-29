package mcjty.deepresonance.blocks.crystals;

import mcjty.deepresonance.blocks.ModBlocks;
import mcjty.deepresonance.render.DefaultISBRH;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

public class ResonatingCrystalRenderer extends DefaultISBRH {

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
        Tessellator tessellator = Tessellator.instance;
        tessellator.addTranslation(x, y, z);
        tessellator.setBrightness(240);

        IIcon icon = ModBlocks.resonatingCrystalBlock.getIcon(0, 0);

        float u1 = icon.getMinU();
        float u2 = icon.getMaxU();
        float v1 = icon.getMinV();
        float v2 = icon.getMaxV();

        tessellator.addVertexWithUV(0, 0, 0, u1, v1);
        tessellator.addVertexWithUV(0, 0, 1, u1, v2);
        tessellator.addVertexWithUV(0, 1, 1, u2, v2);
        tessellator.addVertexWithUV(0, 1, 0, u2, v1);

        tessellator.addVertexWithUV(0, 0, 0, u1, v1);
        tessellator.addVertexWithUV(0, 0, 1, u1, v2);
        tessellator.addVertexWithUV(0, 1, 1, u2, v2);
        tessellator.addVertexWithUV(0, 1, 0, u2, v1);

        tessellator.addTranslation(-x, -y, -z);

        return true;
    }

    @Override
    public int getRenderId() {
        return ResonatingCrystalBlock.RENDERID_RESONATINGCRYSTAL;
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId) {
        return true;
    }
}
