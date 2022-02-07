package mcjty.deepresonance.modules.radiation.manager;

import mcjty.deepresonance.modules.radiation.util.SimpleAABB;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

/**
 * Created by McJty
 */
public class QuadTree {

    private final SimpleAABB box;
    private QuadTree child1;
    private QuadTree child2;
    private float blocker = 1.0f;      // 0.0 is blocked, 1.0 is transparent

    public QuadTree(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        box = SimpleAABB.getBoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
    }

    public void addBlocker(int x, int y, int z, float blocker) {
        addBlocker(new BlockPos(x, y, z), blocker);
    }

    // Return -1 if blockers inside this are different. Otherwise blocker value.
    public float addBlocker(BlockPos coordinate, float blocker) {
        if (child1 != null) {
            if (child1.box.isVecInside(coordinate)) {
                float b = child1.addBlocker(coordinate, blocker);
                if (child2.blocker >= 0.0 && Math.abs(b - child2.blocker) < 0.01) {
                    // Blockers are almost the same. Optimize
                    this.blocker = b;
                    this.child1 = null;
                    this.child2 = null;
                    return b;
                }
                this.blocker = -1;
                return -1;
            } else if (child2.box.isVecInside(coordinate)) {
                float b = child2.addBlocker(coordinate, blocker);
                if (child1.blocker >= 0.0 && Math.abs(b - child1.blocker) < 0.01) {
                    // Blockers are almost the same. Optimize
                    this.blocker = b;
                    this.child1 = null;
                    this.child2 = null;
                    return b;
                }
                this.blocker = -1;
                return -1;
            } else {
                System.out.println("Impossible! Point " + coordinate + " is not in either box!");
                System.out.println("    child1.box = " + child1.box);
                System.out.println("    child2.box = " + child2.box);
                this.blocker = -1;
                return -1;
            }
        } else {
            int lx = box.maxX - box.minX;
            int ly = box.maxY - box.minY;
            int lz = box.maxZ - box.minZ;
            int largest;
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
            if (largest > 1) {
                switch (axis) {
                    case 0 -> {
                        int middle = (box.maxX + box.minX) / 2;
                        child1 = new QuadTree(box.minX, box.minY, box.minZ, middle, box.maxY, box.maxZ);
                        child2 = new QuadTree(middle, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ);
                    }
                    case 1 -> {
                        int middle = (box.maxY + box.minY) / 2;
                        child1 = new QuadTree(box.minX, box.minY, box.minZ, box.maxX, middle, box.maxZ);
                        child2 = new QuadTree(box.minX, middle, box.minZ, box.maxX, box.maxY, box.maxZ);
                    }
                    case 2 -> {
                        int middle = (box.maxZ + box.minZ) / 2;
                        child1 = new QuadTree(box.minX, box.minY, box.minZ, box.maxX, box.maxY, middle);
                        child2 = new QuadTree(box.minX, box.minY, middle, box.maxX, box.maxY, box.maxZ);
                    }
                }
                child1.blocker = this.blocker;
                child2.blocker = this.blocker;
                this.blocker = -1;
                return addBlocker(coordinate, blocker);
            } else {
                // Don't split.
                this.blocker = blocker;
                return blocker;
            }
        }
    }


    public double factor(int x1, int y1, int z1, int x2, int y2, int z2) {
        Vec3 p1 = new Vec3(x1 + .5, y1 + .5, z1 + .5);
        Vec3 p2 = new Vec3(x2 + .5, y2 + .5, z2 + .5);
        return factor(new Ray(p1, p2));
    }

    // Calculate radiation for a 2-high character
    public double factor2(int x1, int y1, int z1, int x2, int y2, int z2) {
        Vec3 p1 = new Vec3(x1 + .5, y1 + .5, z1 + .5);
        Vec3 p2 = new Vec3(x2 + .5, y2 + .5, z2 + .5);
        double f1 = factor(new Ray(p1, p2));
        Vec3 p3 = new Vec3(x2 + .5, y2 + 1.1, z2 + .5);
        double f2 = factor(new Ray(p1, p3));
        return Math.max(f1, f2);
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

    private static boolean testIntersect(SimpleAABB box, Ray ray) {
        Vec3 invDir = ray.getInvDir();

        boolean signDirX = invDir.x < 0;
        boolean signDirY = invDir.y < 0;
        boolean signDirZ = invDir.z < 0;

        double v = signDirX ? box.maxX : box.minX;
        double tmin = (v - ray.getStart().x) * invDir.x;
        v = signDirX ? box.minX : box.maxX;
        double tmax = (v - ray.getStart().x) * invDir.x;

        v = signDirY ? box.maxY : box.minY;
        double tymin = (v - ray.getStart().y) * invDir.y;
        v = signDirY ? box.minY : box.maxY;
        double tymax = (v - ray.getStart().y) * invDir.y;

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
        double tzmin = (v - ray.getStart().z) * invDir.z;
        v = signDirZ ? box.minZ : box.maxZ;
        double tzmax = (v - ray.getStart().z) * invDir.z;

        if ((tmin > tzmax) || (tzmin > tmax)) {
            return false;
        }
        if (tzmin > tmin) {
            tmin = tzmin;
        }
        if (tzmax < tmax) {
            tmax = tzmax;
        }
        return (tmin < ray.getLength()) && (tmax > 0.01);
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

        private final Vec3 start;
        private final Vec3 dir;
        private final Vec3 invDir;
        private final double length;

        public Ray(Vec3 start, Vec3 end) {
            this.start = start;
            Vec3 dir = end.subtract(start);
            length = dir.length();
            this.dir = dir.normalize();
            this.invDir = new Vec3(1.0 / this.dir.x, 1.0 / this.dir.y, 1.0 / this.dir.z);
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
        for (int y = 0; y <= 5; y++) {
            for (int z = 0; z <= dim; z++) {
                tree.addBlocker(3, y, z, 0.5f);
                tree.addBlocker(20, y, z, 0.5f);
                tree.addBlocker(21, y, z, 0.5f);
            }
        }

        System.out.println("Twice Blocked: " + tree.factor(1, 3, 3, 40, 3, 3));
        System.out.println("Once Blocked: " + tree.factor(1, 3, 3, 10, 3, 3));
        System.out.println("Not Blocked: " + tree.factor(1, 7, 3, 8, 7, 3));

        System.out.println("tree.treeSize() = " + tree.treeSize());

        for (int y = 0; y <= 5; y++) {
            for (int z = 0; z <= dim; z++) {
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
