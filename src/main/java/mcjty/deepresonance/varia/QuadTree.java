package mcjty.deepresonance.varia;

import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;

public class QuadTree {
    private AxisAlignedBB box;
    private QuadTree child1;
    private QuadTree child2;
    private float blocker = 1.0f;      // 0.0 is blocked, 1.0 is transparent

    public QuadTree(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        this((double) minX, (double) minY, (double) minZ, (double) maxX + 1, (double) maxY + 1, (double) maxZ + 1);
    }

    private QuadTree(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        box = AxisAlignedBB.getBoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
    }

    public void addBlocker(int x, int y, int z, float blocker) {
        Vec3 point = Vec3.createVectorHelper(x + .5, y + .5, z + .5);
        addBlocker(point, blocker);
    }

    // Return -1 if blockers inside this are different. Otherwise blocker value.
    private float addBlocker(Vec3 point, float blocker) {
        if (child1 != null) {
            if (isVecInside(child1.box, point)) {
                float b = child1.addBlocker(point, blocker);
                if (child2.blocker >= 0.0 && Math.abs(b-child2.blocker) < 0.01) {
                    // Blockers are almost the same. Optimize
                    this.blocker = b;
                    this.child1 = null;
                    this.child2 = null;
                    return b;
                }
                this.blocker = -1;
                return -1;
            } else if (isVecInside(child2.box, point)) {
                float b = child2.addBlocker(point, blocker);
                if (child1.blocker >= 0.0 && Math.abs(b-child1.blocker) < 0.01) {
                    // Blockers are almost the same. Optimize
                    this.blocker = b;
                    this.child1 = null;
                    this.child2 = null;
                    return b;
                }
                this.blocker = -1;
                return -1;
            } else {
                System.out.println("Impossible! Point " + point + " is not in either box!");
                System.out.println("    child1.box = " + child1.box);
                System.out.println("    child2.box = " + child2.box);
                this.blocker = -1;
                return -1;
            }
        } else {
            double lx = box.maxX - box.minX;
            double ly = box.maxY - box.minY;
            double lz = box.maxZ - box.minZ;
            double largest;
            int axis;
            if (lx >= ly && lx >= lz) {
                largest = lx;
                axis = 0;
            } else if (ly >= lz) {
                largest = ly;
                axis = 1;
            } else {
                largest = lz;
                axis = 2;
            }
            if (largest > 1.0) {
                switch (axis) {
                    case 0: {
                        double middle = (box.maxX + box.minX) / 2.0;
                        child1 = new QuadTree(box.minX, box.minY, box.minZ, middle,   box.maxY, box.maxZ);
                        child2 = new QuadTree(middle,   box.minY, box.minZ, box.maxX, box.maxY, box.maxZ);
                        break;
                    }
                    case 1: {
                        double middle = (box.maxY + box.minY) / 2.0;
                        child1 = new QuadTree(box.minX, box.minY, box.minZ, box.maxX, middle,   box.maxZ);
                        child2 = new QuadTree(box.minX, middle,   box.minZ, box.maxX, box.maxY, box.maxZ);
                        break;
                    }
                    case 2: {
                        double middle = (box.maxZ + box.minZ) / 2.0;
                        child1 = new QuadTree(box.minX, box.minY, box.minZ, box.maxX, box.maxY, middle);
                        child2 = new QuadTree(box.minX, box.minY, middle,   box.maxX, box.maxY, box.maxZ);
                        break;
                    }
                }
                child1.blocker = this.blocker;
                child2.blocker = this.blocker;
                return addBlocker(point, blocker);
            } else {
                // Don't split.
                this.blocker = blocker;
                return blocker;
            }
        }
    }


    public double factor(int x1, int y1, int z1, int x2, int y2, int z2) {
        Vec3 p1 = Vec3.createVectorHelper(x1 + .5, y1 + .5, z1 + .5);
        Vec3 p2 = Vec3.createVectorHelper(x2 + .5, y2 + .5, z2 + .5);
        return factor(new Ray(p1, p2));
    }

    private double factor(Ray ray) {
        if (child1 != null) {
            double factor = 1.0;
            if (testIntersect(child1.box, ray)) {
                factor *= child1.factor(ray);
            }
            if (testIntersect(child2.box, ray)) {
                factor *= child2.factor(ray);
            }
            return factor;
        } else {
            return blocker;
        }
    }

    private static boolean testIntersect(AxisAlignedBB box, Ray ray) {
        Vec3 invDir = ray.getInvDir();

        boolean signDirX = invDir.xCoord < 0;
        boolean signDirY = invDir.yCoord < 0;
        boolean signDirZ = invDir.zCoord < 0;

        double v = signDirX ? box.maxX : box.minX;
        double tmin = (v - ray.getStart().xCoord) * invDir.xCoord;
        v = signDirX ? box.minX : box.maxX;
        double tmax = (v - ray.getStart().xCoord) * invDir.xCoord;

        v = signDirY ? box.maxY : box.minY;
        double tymin = (v - ray.getStart().yCoord) * invDir.yCoord;
        v = signDirY ? box.minY : box.maxY;
        double tymax = (v - ray.getStart().yCoord) * invDir.yCoord;

        if ((tmin > tymax) || (tymin > tmax)) {
            return false;
        }
        if (tymin > tmin) {
            tmin = tymin;
        }
        if (tymax < tmax) {
            tmax = tymax;
        }

        v = signDirZ ? box.maxZ : box.minZ;
        double tzmin = (v - ray.getStart().zCoord) * invDir.zCoord;
        v = signDirZ ? box.minZ : box.maxZ;
        double tzmax = (v - ray.getStart().zCoord) * invDir.zCoord;

        if ((tmin > tzmax) || (tzmin > tmax)) {
            return false;
        }
        if (tzmin > tmin) {
            tmin = tzmin;
        }
        if (tzmax < tmax) {
            tmax = tzmax;
        }
        if ((tmin < ray.getLength()) && (tmax > 0.01)) {
            return true;
        }
        return false;
    }

    private static boolean isVecInside(AxisAlignedBB box, Vec3 point) {
        return point.xCoord >= box.minX && point.xCoord <= box.maxX ? (point.yCoord >= box.minY && point.yCoord <= box.maxY ? point.zCoord >= box.minZ && point.zCoord <= box.maxZ : false) : false;
    }

    private void dump(int indent) {
        if (child1 == null) {
            System.out.println("                                                                     ".substring(0, indent) + "Leaf: " + box + ", blocker=" + blocker);
        } else {
            System.out.println("                                                                     ".substring(0, indent) + "Node: " + box);
            child1.dump(indent + 2);
            child2.dump(indent + 2);
        }
    }

    private int treeSize() {
        if (child1 == null) {
            return 1;
        } else {
            return 1 + child1.treeSize() + child2.treeSize();
        }
    }

    private static class Ray {
        private Vec3 start;
        private Vec3 dir;
        private Vec3 invDir;
        private double length;

        public Ray(Vec3 start, Vec3 end) {
            this.start = start;
            this.dir = start.subtract(end);
            length = this.dir.lengthVector();
            this.dir = this.dir.normalize();
            this.invDir = Vec3.createVectorHelper(1.0 / this.dir.xCoord, 1.0 / this.dir.yCoord, 1.0 / this.dir.zCoord);
        }

        public Vec3 getDir() {
            return dir;
        }

        public Vec3 getInvDir() {
            return invDir;
        }

        public Vec3 getStart() {
            return start;
        }

        public double getLength() {
            return length;
        }
    }


    public static void main(String[] args) {
        int dim = 100;
        QuadTree tree = new QuadTree(0, 0, 0, dim, dim, dim);
        for (int y = 0 ; y <= 5 ; y++) {
            for (int z = 0 ; z <= dim; z++) {
                tree.addBlocker(3, y, z, 0.5f);
                tree.addBlocker(20, y, z, 0.5f);
                tree.addBlocker(21, y, z, 0.5f);
            }
        }

        System.out.println("Twice Blocked: " + tree.factor(1, 3, 3, 40, 3, 3));
        System.out.println("Once Blocked: " + tree.factor(1, 3, 3, 10, 3, 3));
        System.out.println("Not Blocked: " + tree.factor(1, 7, 3, 8, 7, 3));

        System.out.println("tree.treeSize() = " + tree.treeSize());

        for (int y = 0 ; y <= 5 ; y++) {
            for (int z = 0 ; z <= dim; z++) {
                tree.addBlocker(3, y, z, 0.5f);
                tree.addBlocker(20, y, z, 1.0f);
                tree.addBlocker(21, y, z, 0.5f);
            }
        }

        System.out.println("Twice Blocked: " + tree.factor(1, 3, 3, 40, 3, 3));
        System.out.println("Once Blocked: " + tree.factor(1, 3, 3, 10, 3, 3));
        System.out.println("Not Blocked: " + tree.factor(1, 7, 3, 8, 7, 3));


        System.out.println("tree.treeSize() = " + tree.treeSize());

//        tree.dump(0);
    }
}
