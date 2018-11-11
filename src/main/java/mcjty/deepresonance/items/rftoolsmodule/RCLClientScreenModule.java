package mcjty.deepresonance.items.rftoolsmodule;

import mcjty.rftools.api.screens.*;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class RCLClientScreenModule implements IClientScreenModule<ModuleDataRCL> {
    private String line = "";
    private int color = 0xffffff;
    private int radcolor = 0xffffff;
    private TextAlign textAlign = TextAlign.ALIGN_LEFT;

    private ITextRenderHelper labelCache = null;

    @Override
    public TransformMode getTransformMode() {
        return TransformMode.TEXT;
    }

    @Override
    public int getHeight() {
        return 30;
    }

    @Override
    public void render(IModuleRenderHelper renderHelper, FontRenderer fontRenderer, int currenty, ModuleDataRCL screenData, ModuleRenderInfo renderInfo) {
        if (labelCache == null) {
            labelCache = renderHelper.createTextRenderHelper().align(textAlign);
        }

        GlStateManager.disableLighting();
        int xoffset;
        if (!line.isEmpty()) {
            labelCache.setup(line, 160, renderInfo);
            labelCache.renderText(0, currenty, color, renderInfo);
            xoffset = 7 + 40;
        } else {
            xoffset = 7;
        }
        if (screenData != null) {
            renderHelper.renderText(xoffset, currenty, radcolor, renderInfo, "Purity:");
            renderHelper.renderText(xoffset + 55, currenty, radcolor, renderInfo, String.valueOf(screenData.getPurity()) + "%");
            currenty += 10;
            renderHelper.renderText(xoffset, currenty, radcolor, renderInfo, "Strength:");
            renderHelper.renderText(xoffset + 55, currenty, radcolor, renderInfo, String.valueOf(screenData.getStrength()) + "%");
            currenty += 10;
            renderHelper.renderText(xoffset, currenty, radcolor, renderInfo, "Efficiency:");
            renderHelper.renderText(xoffset + 55, currenty, radcolor, renderInfo, String.valueOf(screenData.getEfficiency()) + "%");
        }
    }

    @Override
    public void mouseClick(World world, int x, int y, boolean clicked) {

    }

    @Override
    public void setupFromNBT(NBTTagCompound tagCompound, int dim, BlockPos pos) {
        if (tagCompound != null) {
            line = tagCompound.getString("text");
            if (tagCompound.hasKey("color")) {
                color = tagCompound.getInteger("color");
            } else {
                color = 0xffffff;
            }
            if (tagCompound.hasKey("radcolor")) {
                radcolor = tagCompound.getInteger("radcolor");
            } else {
                radcolor = 0xffffff;
            }
            if (tagCompound.hasKey("align")) {
                String alignment = tagCompound.getString("align");
                textAlign = TextAlign.get(alignment);
            } else {
                textAlign = TextAlign.ALIGN_LEFT;
            }
        }
    }

    @Override
    public boolean needsServerData() {
        return true;
    }
}
