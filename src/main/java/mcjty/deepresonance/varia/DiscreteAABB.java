package mcjty.deepresonance.varia;

import mcjty.lib.varia.Coordinate;

public class DiscreteAABB {
    public int minX;
    public int minY;
    public int minZ;
    public int maxX;
    public int maxY;
    public int maxZ;

    public static DiscreteAABB getBoundingBox(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        return new DiscreteAABB(minX, minY, minZ, maxX, maxY, maxZ);
    }

    protected DiscreteAABB(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
    }

    /**
     * Sets the bounds of the bounding box. Args: minX, minY, minZ, maxX, maxY, maxZ
     */
    public DiscreteAABB setBounds(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
        return this;
    }

    /**
     * Adds the coordinates to the bounding box extending it if the point lies outside the current ranges. Args: x, y, z
     */
    public DiscreteAABB addCoord(int x, int y, int z) {
        int d3 = this.minX;
        int d4 = this.minY;
        int d5 = this.minZ;
        int d6 = this.maxX;
        int d7 = this.maxY;
        int d8 = this.maxZ;

        if (x < 0.0D) {
            d3 += x;
        }

        if (x > 0.0D) {
            d6 += x;
        }

        if (y < 0.0D) {
            d4 += y;
        }

        if (y > 0.0D) {
            d7 += y;
        }

        if (z < 0.0D) {
            d5 += z;
        }

        if (z > 0.0D) {
            d8 += z;
        }

        /**
         * Returns a bounding box with the specified bounds. Args: minX, minY, minZ, maxX, maxY, maxZ
         */
        return getBoundingBox(d3, d4, d5, d6, d7, d8);
    }

    /**
     * Returns a bounding box expanded by the specified vector (if negative numbers are given it will shrink). Args: x,
     * y, z
     */
    public DiscreteAABB expand(int x, int y, int z) {
        int d3 = this.minX - x;
        int d4 = this.minY - y;
        int d5 = this.minZ - z;
        int d6 = this.maxX + x;
        int d7 = this.maxY + y;
        int d8 = this.maxZ + z;
        /**
         * Returns a bounding box with the specified bounds. Args: minX, minY, minZ, maxX, maxY, maxZ
         */
        return getBoundingBox(d3, d4, d5, d6, d7, d8);
    }

    public DiscreteAABB func_111270_a(DiscreteAABB box) {
        int d0 = Math.min(this.minX, box.minX);
        int d1 = Math.min(this.minY, box.minY);
        int d2 = Math.min(this.minZ, box.minZ);
        int d3 = Math.max(this.maxX, box.maxX);
        int d4 = Math.max(this.maxY, box.maxY);
        int d5 = Math.max(this.maxZ, box.maxZ);
        /**
         * Returns a bounding box with the specified bounds. Args: minX, minY, minZ, maxX, maxY, maxZ
         */
        return getBoundingBox(d0, d1, d2, d3, d4, d5);
    }

    /**
     * Returns a bounding box offseted by the specified vector (if negative numbers are given it will shrink). Args: x,
     * y, z
     */
    public DiscreteAABB getOffsetBoundingBox(int x, int y, int z) {
        /**
         * Returns a bounding box with the specified bounds. Args: minX, minY, minZ, maxX, maxY, maxZ
         */
        return getBoundingBox(this.minX + x, this.minY + y, this.minZ + z, this.maxX + x, this.maxY + y, this.maxZ + z);
    }

    /**
     * Returns whether the given bounding box intersects with this one. Args: axisAlignedBB
     */
    public boolean intersectsWith(DiscreteAABB box) {
        return box.maxX >= this.minX && box.minX <= this.maxX && (box.maxY >= this.minY && box.minY <= this.maxY && box.maxZ >= this.minZ && box.minZ <= this.maxZ);
    }

    /**
     * Offsets the current bounding box by the specified coordinates. Args: x, y, z
     */
    public DiscreteAABB offset(int x, int y, int z) {
        this.minX += x;
        this.minY += y;
        this.minZ += z;
        this.maxX += x;
        this.maxY += y;
        this.maxZ += z;
        return this;
    }

    /**
     * Returns if the supplied Coordinate is completely inside the bounding box
     */
    public boolean isVecInside(Coordinate c) {
        return c.getX() >= this.minX && c.getX() <= this.maxX && (c.getY() >= this.minY && c.getY() <= this.maxY && c.getZ() >= this.minZ && c.getZ() <= this.maxZ);
    }

    /**
     * Returns a copy of the bounding box.
     */
    public DiscreteAABB copy() {
        /**
         * Returns a bounding box with the specified bounds. Args: minX, minY, minZ, maxX, maxY, maxZ
         */
        return getBoundingBox(this.minX, this.minY, this.minZ, this.maxX, this.maxY, this.maxZ);
    }



    public String toString() {
        return "box[" + this.minX + ", " + this.minY + ", " + this.minZ + " -> " + this.maxX + ", " + this.maxY + ", " + this.maxZ + "]";
    }
}
