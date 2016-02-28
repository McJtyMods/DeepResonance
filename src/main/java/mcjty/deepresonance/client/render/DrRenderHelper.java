package mcjty.deepresonance.client.render;

import mcjty.lib.gui.RenderHelper;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

public class DrRenderHelper {

    public static void renderBillboardQuadBright(double scale) {
        int brightness = 240;
        int b1 = brightness >> 16 & 65535;
        int b2 = brightness & 65535;
        GlStateManager.pushMatrix();
        RenderHelper.rotateToPlayer();
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer renderer = tessellator.getWorldRenderer();
        renderer.begin(7, DefaultVertexFormats.POSITION_TEX_LMAP_COLOR);
        renderer.pos(-scale, -scale, 0.0D).tex(0.0D, 0.0D).lightmap(b1, b2).color(255, 255, 255, 128).endVertex();
        renderer.pos(-scale, scale, 0.0D).tex(0.0D, 1.0D).lightmap(b1, b2).color(255, 255, 255, 128).endVertex();
        renderer.pos(scale, scale, 0.0D).tex(1.0D, 1.0D).lightmap(b1, b2).color(255, 255, 255, 128).endVertex();
        renderer.pos(scale, -scale, 0.0D).tex(1.0D, 0.0D).lightmap(b1, b2).color(255, 255, 255, 128).endVertex();
        tessellator.draw();
        GlStateManager.popMatrix();
    }


    public static void drawBeam(RenderHelper.Vector S, RenderHelper.Vector E, RenderHelper.Vector P, float width) {
        RenderHelper.Vector PS = Sub(S, P);
        RenderHelper.Vector SE = Sub(E, S);
        RenderHelper.Vector normal = Cross(PS, SE);
        normal = normal.normalize();
        RenderHelper.Vector half = Mul(normal, width);
        RenderHelper.Vector p1 = Add(S, half);
        RenderHelper.Vector p2 = Sub(S, half);
        RenderHelper.Vector p3 = Add(E, half);
        RenderHelper.Vector p4 = Sub(E, half);
        drawQuad(Tessellator.getInstance(), p1, p3, p4, p2);
    }

    private static RenderHelper.Vector Cross(RenderHelper.Vector a, RenderHelper.Vector b) {
        float x = a.y * b.z - a.z * b.y;
        float y = a.z * b.x - a.x * b.z;
        float z = a.x * b.y - a.y * b.x;
        return new RenderHelper.Vector(x, y, z);
    }

    private static RenderHelper.Vector Sub(RenderHelper.Vector a, RenderHelper.Vector b) {
        return new RenderHelper.Vector(a.x - b.x, a.y - b.y, a.z - b.z);
    }

    private static RenderHelper.Vector Add(RenderHelper.Vector a, RenderHelper.Vector b) {
        return new RenderHelper.Vector(a.x + b.x, a.y + b.y, a.z + b.z);
    }

    private static RenderHelper.Vector Mul(RenderHelper.Vector a, float f) {
        return new RenderHelper.Vector(a.x * f, a.y * f, a.z * f);
    }

    public static void drawQuad(Tessellator tessellator, RenderHelper.Vector p1, RenderHelper.Vector p2, RenderHelper.Vector p3, RenderHelper.Vector p4) {
        int brightness = 240;
        int b1 = brightness >> 16 & 65535;
        int b2 = brightness & 65535;

        WorldRenderer renderer = tessellator.getWorldRenderer();
        renderer.pos(p1.getX(), p1.getY(), p1.getZ()).tex(0.0D, 0.0D).lightmap(b1, b2).color(255, 255, 255, 128).endVertex();
        renderer.pos(p2.getX(), p2.getY(), p2.getZ()).tex(1.0D, 0.0D).lightmap(b1, b2).color(255, 255, 255, 128).endVertex();
        renderer.pos(p3.getX(), p3.getY(), p3.getZ()).tex(1.0D, 1.0D).lightmap(b1, b2).color(255, 255, 255, 128).endVertex();
        renderer.pos(p4.getX(), p4.getY(), p4.getZ()).tex(0.0D, 1.0D).lightmap(b1, b2).color(255, 255, 255, 128).endVertex();
    }


}
