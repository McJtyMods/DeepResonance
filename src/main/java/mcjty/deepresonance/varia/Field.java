package mcjty.deepresonance.varia;

public class Field {
    private final int radius;
    private final Blocker blocker;
    private final int dim;
    private float map[][][];
    private float d;

    public Field(int radius, Blocker blocker) {
        this.radius = radius;
        this.blocker = blocker;

        dim = (radius*2)+1;
        map = new float[dim+2][dim+2][dim+2];
        d = 1.0f / radius;

        // Initialize a 'border'
        for (int i = 0 ; i < dim+2 ; i++) {
            for (int j = 0 ; j < dim+2 ; j++) {
                map[i][j][0] = Float.MAX_VALUE;
                map[i][j][dim+1] = Float.MAX_VALUE;
                map[i][0][j] = Float.MAX_VALUE;
                map[i][dim+1][j] = Float.MAX_VALUE;
                map[0][i][j] = Float.MAX_VALUE;
                map[dim+1][i][j] = Float.MAX_VALUE;
            }
        }
    }

    public float get(int x, int y, int z) {
        return map[x+1][y+1][z+1];
    }

    private void setInternal(int x, int y, int z, float value) {
        map[x+1][y+1][z+1] = value;
    }

    public float getBlocking(int x, int y, int z) {
        if (x < 0 || x >= dim) {
            return 0.0f;
        }
        if (y < 0 || y >= dim) {
            return 0.0f;
        }
        if (z < 0 || z >= dim) {
            return 0.0f;
        }
        return blocker.getBlockingFactor(x, y, z);
    }

    public float set(int x, int y, int z, float value) {
        value *= getBlocking(x, y, z);
        float old = get(x, y, z);
        if (value <= old) {
            // Nothing to do.
            return old;
        }
        setInternal(x, y, z, value);
        float f011 = set(x-1, y, z, value-d);
        float f211 = set(x+1, y, z, value-d);
        float f101 = set(x, y-1, z, value-d);
        float f121 = set(x, y+1, z, value-d);
        float f110 = set(x, y, z-1, value-d);
        float f112 = set(x, y, z+1, value-d);
        float f010 = set(x-1, y, z-1, Math.max(f011 * .7f, f110 * .7f));
        float f210 = set(x+1, y, z-1, Math.max(f211 * .7f, f110 * .7f));
        float f012 = set(x+1, y, z-1, Math.max(f011 * .7f, f112 * .7f));
        float f212 = set(x+1, y, z-1, Math.max(f211 * .7f, f112 * .7f));
        return value;
    }

    public static interface Blocker {
        // A value between 0 and 1 indicating how much of the radiation is blocked. 0 means totally blocked. 1 is totally not blocked
        float getBlockingFactor(int x, int y, int z);
    }
}
