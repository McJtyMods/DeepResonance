package mcjty.deepresonance.modules.radiation.util;

import net.minecraft.util.math.BlockPos;

/**
 * Created by McJty
 * <p>
 * Simple AABB for the QuadTree, minimal bloat
 */
public class SimpleAABB {

    public final int minX;
    public final int minY;
    public final int minZ;
    public final int maxX;
    public final int maxY;
    public final int maxZ;

    public static SimpleAABB getBoundingBox(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        return new SimpleAABB(minX, minY, minZ, maxX, maxY, maxZ);
    }

    protected SimpleAABB(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
    }

    /**
     * Returns if the supplied Coordinate is completely inside the bounding box
     */
    public boolean isVecInside(BlockPos c) {
        return c.getX() >= this.minX && c.getX() < this.maxX && (c.getY() >= this.minY && c.getY() < this.maxY && c.getZ() >= this.minZ && c.getZ() < this.maxZ);
    }

    /**
     * Returns a copy of the bounding box.
     */
    public SimpleAABB copy() {
        return getBoundingBox(this.minX, this.minY, this.minZ, this.maxX, this.maxY, this.maxZ);
    }


    @Override
    public String toString() {
        return "box[" + this.minX + ", " + this.minY + ", " + this.minZ + " -> " + this.maxX + ", " + this.maxY + ", " + this.maxZ + "]";
    }

}
