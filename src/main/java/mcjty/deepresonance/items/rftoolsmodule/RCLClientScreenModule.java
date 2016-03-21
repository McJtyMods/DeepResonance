package mcjty.deepresonance.items.rftoolsmodule;

import mcjty.rftools.api.screens.IClientScreenModule;
import mcjty.rftools.api.screens.IModuleGuiBuilder;
import mcjty.rftools.api.screens.IModuleRenderHelper;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class RCLClientScreenModule implements IClientScreenModule<ModuleDataRCL> {
    private String line = "";
    private int color = 0xffffff;
    private int radcolor = 0xffffff;

    @Override
    public TransformMode getTransformMode() {
        return TransformMode.TEXT;
    }

    @Override
    public int getHeight() {
        return 30;
    }

    @Override
    public void render(IModuleRenderHelper helper, FontRenderer fontRenderer, int currenty, ModuleDataRCL screenData, float factor) {
        GlStateManager.disableLighting();
        int xoffset;
        if (!line.isEmpty()) {
            fontRenderer.drawString(line, 7, currenty, color);
            xoffset = 7 + 40;
        } else {
            xoffset = 7;
        }
        if (screenData != null) {
            fontRenderer.drawString("Purity:", xoffset, currenty, radcolor);
            fontRenderer.drawString(String.valueOf(screenData.getPurity()) + "%", xoffset + 55, currenty, radcolor);
            currenty += 10;
            fontRenderer.drawString("Strength:", xoffset, currenty, radcolor);
            fontRenderer.drawString(String.valueOf(screenData.getStrength()) + "%", xoffset + 55, currenty, radcolor);
            currenty += 10;
            fontRenderer.drawString("Efficiency:", xoffset, currenty, radcolor);
            fontRenderer.drawString(String.valueOf(screenData.getEfficiency()) + "%", xoffset + 55, currenty, radcolor);
        }
    }

    @Override
    public void mouseClick(World world, int x, int y, boolean clicked) {

    }

    @Override
    public void createGui(IModuleGuiBuilder guiBuilder) {
        guiBuilder.
                label("Label:").text("text", "Label text").color("color", "Color for the label").nl().
                label("Stats:").color("radcolor", "Color for the statistics text").nl().
                block("monitor").nl();
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
        }
    }

    @Override
    public boolean needsServerData() {
        return true;
    }
}
