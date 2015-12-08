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
     * Returns if the supplied Coordinate is completely inside the bounding box
     */
    public boolean isVecInside(Coordinate c) {
        return c.getX() >= this.minX && c.getX() < this.maxX && (c.getY() >= this.minY && c.getY() < this.maxY && c.getZ() >= this.minZ && c.getZ() < this.maxZ);
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



    @Override
    public String toString() {
        return "box[" + this.minX + ", " + this.minY + ", " + this.minZ + " -> " + this.maxX + ", " + this.maxY + ", " + this.maxZ + "]";
    }
}
