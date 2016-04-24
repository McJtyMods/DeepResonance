package mcjty.deepresonance.client.render;

public abstract class DefaultISBRH{}/* implements ISimpleBlockRenderingHandler {

    public static final class Vt {
        public final float x;
        public final float y;
        public final float z;

        public Vt(float x, float y, float z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }

    public static final class Quad {
        public final Vt v1;
        public final Vt v2;
        public final Vt v3;
        public final Vt v4;

        public Quad(Vt v1, Vt v2, Vt v3, Vt v4) {
            this.v1 = v1;
            this.v2 = v2;
            this.v3 = v3;
            this.v4 = v4;
        }

        public Quad rotate(EnumFacing direction) {
            switch (direction) {
                case NORTH: return new Quad(v4, v1, v2, v3);
                case EAST: return new Quad(v3, v4, v1, v2);
                case SOUTH: return new Quad(v2, v3, v4, v1);
                case WEST: return this;
                default: return this;
            }
        }
    }

    protected static final Quad quads[] = new Quad[] {
            new Quad(new Vt(0, 0, 0), new Vt(1, 0, 0), new Vt(1, 0, 1), new Vt(0, 0, 1)),       // DOWN
            new Quad(new Vt(0, 1, 1), new Vt(1, 1, 1), new Vt(1, 1, 0), new Vt(0, 1, 0)),       // UP
            new Quad(new Vt(1, 1, 0), new Vt(1, 0, 0), new Vt(0, 0, 0), new Vt(0, 1, 0)),       // NORTH
            new Quad(new Vt(1, 0, 1), new Vt(1, 1, 1), new Vt(0, 1, 1), new Vt(0, 0, 1)),       // SOUTH
            new Quad(new Vt(0, 0, 1), new Vt(0, 1, 1), new Vt(0, 1, 0), new Vt(0, 0, 0)),       // WEST
            new Quad(new Vt(1, 0, 0), new Vt(1, 1, 0), new Vt(1, 1, 1), new Vt(1, 0, 1)),       // EAST
    };

    public static Quad getQuad(EnumFacing side){
        return getQuad(side.ordinal());
    }

    public static Quad getQuad(int i){
        return quads[i];
    }

    public static void addSideFullTexture(Tessellator tessellator, int side, float mult, float offset) {
        float u1 = 0;
        float v1 = 0;
        float u2 = 1;
        float v2 = 1;
        Quad quad = quads[side];
        tessellator.addVertexWithUV(quad.v1.x * mult + offset, quad.v1.y * mult + offset, quad.v1.z * mult + offset, u1, v1);
        tessellator.addVertexWithUV(quad.v2.x * mult + offset, quad.v2.y * mult + offset, quad.v2.z * mult + offset, u1, v2);
        tessellator.addVertexWithUV(quad.v3.x * mult + offset, quad.v3.y * mult + offset, quad.v3.z * mult + offset, u2, v2);
        tessellator.addVertexWithUV(quad.v4.x * mult + offset, quad.v4.y * mult + offset, quad.v4.z * mult + offset, u2, v1);
    }

    public static void addSide(Tessellator tessellator, int side, float mult, float offset, float u1, float v1, float u2, float v2) {
        Quad quad = quads[side];
        tessellator.addVertexWithUV(quad.v1.x * mult + offset, quad.v1.y * mult + offset, quad.v1.z * mult + offset, u1, v1);
        tessellator.addVertexWithUV(quad.v2.x * mult + offset, quad.v2.y * mult + offset, quad.v2.z * mult + offset, u1, v2);
        tessellator.addVertexWithUV(quad.v3.x * mult + offset, quad.v3.y * mult + offset, quad.v3.z * mult + offset, u2, v2);
        tessellator.addVertexWithUV(quad.v4.x * mult + offset, quad.v4.y * mult + offset, quad.v4.z * mult + offset, u2, v1);
    }

    public static void addSideConfigurableHeight(Tessellator tessellator, int side, float height, float offset, float u1, float v1, float u2, float v2) {
        Quad quad = quads[side];
        tessellator.addVertexWithUV(quad.v1.x + offset, quad.v1.y * height + offset, quad.v1.z + offset, u1, v1);
        tessellator.addVertexWithUV(quad.v2.x + offset, quad.v2.y * height + offset, quad.v2.z + offset, u1, v2);
        tessellator.addVertexWithUV(quad.v3.x + offset, quad.v3.y * height + offset, quad.v3.z + offset, u2, v2);
        tessellator.addVertexWithUV(quad.v4.x + offset, quad.v4.y * height + offset, quad.v4.z + offset, u2, v1);
    }

    public static void addSideConditionally(IBlockAccess world, int x, int y, int z, Block block, Tessellator tessellator, IIcon icon, EnumFacing direction) {
        if (block.shouldSideBeRendered(world, x+direction.offsetX, y+direction.offsetY, z+direction.offsetZ, direction.ordinal())) {
            addSide(tessellator, direction.ordinal(), icon);
        }
    }

    public static void addSide(Tessellator tessellator, int side, IIcon c) {
        float u1 = c.getMinU();
        float v1 = c.getMinV();
        float u2 = c.getMaxU();
        float v2 = c.getMaxV();
        Quad quad = quads[side];
        tessellator.addVertexWithUV(quad.v1.x, quad.v1.y, quad.v1.z, u1, v1);
        tessellator.addVertexWithUV(quad.v2.x, quad.v2.y, quad.v2.z, u1, v2);
        tessellator.addVertexWithUV(quad.v3.x, quad.v3.y, quad.v3.z, u2, v2);
        tessellator.addVertexWithUV(quad.v4.x, quad.v4.y, quad.v4.z, u2, v1);
    }

    protected void drawInventoryBlock(Block block, int meta, RenderBlocks renderer) {
        Tessellator t = Tessellator.instance;

        t.startDrawingQuads();
        t.setNormal(-1, 0, 0);
        renderer.renderFaceXNeg(block, 0, 0, 0, renderer.getBlockIconFromSideAndMetadata(block, EnumFacing.WEST.ordinal(), meta));
        t.draw();

        t.startDrawingQuads();
        t.setNormal(1, 0, 0);
        renderer.renderFaceXPos(block, 0, 0, 0, renderer.getBlockIconFromSideAndMetadata(block, EnumFacing.EAST.ordinal(), meta));
        t.draw();

        t.startDrawingQuads();
        t.setNormal(0, 0, -1);
        renderer.renderFaceZNeg(block, 0, 0, 0, renderer.getBlockIconFromSideAndMetadata(block, EnumFacing.NORTH.ordinal(), meta));
        t.draw();

        t.startDrawingQuads();
        t.setNormal(0, 0, 1);
        renderer.renderFaceZPos(block, 0, 0, 0, renderer.getBlockIconFromSideAndMetadata(block, EnumFacing.SOUTH.ordinal(), meta));
        t.draw();

        t.startDrawingQuads();
        t.setNormal(0, -1, 0);
        renderer.renderFaceYNeg(block, 0, 0, 0, renderer.getBlockIconFromSideAndMetadata(block, EnumFacing.DOWN.ordinal(), meta));
        t.draw();

        t.startDrawingQuads();
        t.setNormal(0, 1, 0);
        renderer.renderFaceYPos(block, 0, 0, 0, renderer.getBlockIconFromSideAndMetadata(block, EnumFacing.UP.ordinal(), meta));
        t.draw();
    }

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
        drawInventoryBlock(block, metadata, renderer);
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
        return false;
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId) {
        return false;
    }
}*/
