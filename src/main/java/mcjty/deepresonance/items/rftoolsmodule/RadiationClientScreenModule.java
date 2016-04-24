package mcjty.deepresonance.items.rftoolsmodule;

import mcjty.rftools.api.screens.IClientScreenModule;
import mcjty.rftools.api.screens.IModuleGuiBuilder;
import mcjty.rftools.api.screens.IModuleRenderHelper;
import mcjty.rftools.api.screens.data.IModuleDataInteger;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class RadiationClientScreenModule implements IClientScreenModule<IModuleDataInteger> {
    private String line = "";
    private int color = 0xffffff;
    private int radcolor = 0xffffff;

    @Override
    public TransformMode getTransformMode() {
        return TransformMode.TEXT;
    }

    @Override
    public int getHeight() {
        return 10;
    }

    @Override
    public void render(IModuleRenderHelper helper, FontRenderer fontRenderer, int currenty, IModuleDataInteger screenData, float factor) {
        GlStateManager.disableLighting();
        int xoffset;
        if (!line.isEmpty()) {
            fontRenderer.drawString(line, 7, currenty, color);
            xoffset = 7 + 40;
        } else {
            xoffset = 7;
        }
        if (screenData != null) {
            fontRenderer.drawString(String.valueOf(screenData.get()), xoffset, currenty, radcolor);
        }
    }

    @Override
    public void mouseClick(World world, int x, int y, boolean clicked) {

    }

    @Override
    public void createGui(IModuleGuiBuilder guiBuilder) {
        guiBuilder.
                label("Label:").text("text", "Label text").color("color", "Color for the label").nl().
                label("Rad:").color("radcolor", "Color for the radiation text").nl().
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
