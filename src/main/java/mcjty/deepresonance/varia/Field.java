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
//        for (int i = 0 ; i < dim+2 ; i++) {
//            for (int j = 0 ; j < dim+2 ; j++) {
//                map[i][j][0] = Float.MAX_VALUE;
//                map[i][j][dim+1] = Float.MAX_VALUE;
//                map[i][0][j] = Float.MAX_VALUE;
//                map[i][dim+1][j] = Float.MAX_VALUE;
//                map[0][i][j] = Float.MAX_VALUE;
//                map[dim+1][i][j] = Float.MAX_VALUE;
//            }
//        }
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

        float f010 = set(x-1, y, z-1, Math.max(f011 - .7f * d, f110 - .7f * d));
        float f210 = set(x+1, y, z-1, Math.max(f211 - .7f * d, f110 - .7f * d));
        float f012 = set(x+1, y, z-1, Math.max(f011 - .7f * d, f112 - .7f * d));
        float f212 = set(x+1, y, z-1, Math.max(f211 - .7f * d, f112 - .7f * d));

        float f102 = set(x, y-1, z+1, Math.max(f101 - .7f * d, f112 - .7f * d));
        float f001 = set(x-1, y-1, z, Math.max(f101 - .7f * d, f011 - .7f * d));
        float f201 = set(x+1, y-1, z, Math.max(f101 - .7f * d, f211 - .7f * d));
        float f100 = set(x, y-1, z-1, Math.max(f101 - .7f * d, f110 - .7f * d));

        float f122 = set(x, y+1, z+1, Math.max(f121 - .7f * d, f112 - .7f * d));
        float f021 = set(x-1, y+1, z, Math.max(f121 - .7f * d, f011 - .7f * d));
        float f221 = set(x+1, y+1, z, Math.max(f121 - .7f * d, f211 - .7f * d));
        float f120 = set(x, y+1, z-1, Math.max(f121 - .7f * d, f110 - .7f * d));

        float f002 = set(x-1, y-1, z+1, Math.max(Math.max(f001 - .5f * d, f102 - .5f * d), f012 - .5f * d));
        float f202 = set(x+1, y-1, z+1, Math.max(Math.max(f201 - .5f * d, f102 - .5f * d), f212 - .5f * d));
        float f000 = set(x-1, y-1, z-1, Math.max(Math.max(f001 - .5f * d, f100 - .5f * d), f010 - .5f * d));
        float f200 = set(x+1, y-1, z-1, Math.max(Math.max(f201 - .5f * d, f100 - .5f * d), f210 - .5f * d));

        float f022 = set(x-1, y+1, z+1, Math.max(Math.max(f021 - .5f * d, f122 - .5f * d), f012 - .5f * d));
        float f222 = set(x+1, y+1, z+1, Math.max(Math.max(f221 - .5f * d, f122 - .5f * d), f212 - .5f * d));
        float f020 = set(x-1, y+1, z-1, Math.max(Math.max(f021 - .5f * d, f120 - .5f * d), f010 - .5f * d));
        float f220 = set(x+1, y+1, z-1, Math.max(Math.max(f221 - .5f * d, f120 - .5f * d), f210 - .5f * d));

        return value;
    }

    public static interface Blocker {
        // A value between 0 and 1 indicating how much of the radiation is blocked. 0 means totally blocked. 1 is totally not blocked
        float getBlockingFactor(int x, int y, int z);
    }

    public static char d(float v) {
        if (v >= 0.99f) {
            return '#';
        } else if (v >= 0.80f) {
            return '*';
        } else if (v >= 0.60f) {
            return '+';
        } else if (v >= 0.40f) {
            return '-';
        } else if (v >= 0.20f) {
            return '.';
        } else {
            return ' ';
        }
    }

    public static void main(String[] args) {
        Field field = new Field(3, new Blocker() {
            @Override
            public float getBlockingFactor(int x, int y, int z) {
                return 1;
            }
        });
        field.set(3, 3, 3, 1.0f);
        for (int y = 0 ; y < 3+3+1 ; y++) {
            System.out.println("======================================================: y=" + y);
            for (int z = 0 ; z < 3+3+1 ; z++) {
                for (int x = 0 ; x < 3+3+1 ; x++) {
                    System.out.print(d(field.get(x, y, z)));
                }
                System.out.println("");
            }
        }
    }
}
