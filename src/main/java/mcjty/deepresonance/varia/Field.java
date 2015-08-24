package mcjty.deepresonance.varia;

public class Field {
    private final int radius;
    private final Blocker blocker;
    private final int dim;
    private byte map[][][];
    private float d;

    public Field(int radius, Blocker blocker) {
        this.radius = radius;
        this.blocker = blocker;

        dim = (radius*2)+1;
        map = new byte[dim+2][dim+2][dim+2];
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

    public byte get(int x, int y, int z) {
        return map[x][y][z];
    }

    private void setInternal(int x, int y, int z, byte value) {
        map[x][y][z] = value;
    }

    public float getBlocking(int x, int y, int z) {
        return blocker.getBlockingFactor(x, y, z);
    }

    public float set(int x, int y, int z, float value) {
        value *= getBlocking(x, y, z);
        float old = get(x, y, z) / 127.0f;
        if (value <= old) {
            // Nothing to do.
            return value;
        }
        setInternal(x, y, z, (byte) (value * 127));
        float f011 = set(x-1, y, z, value-d);
        float f211 = set(x+1, y, z, value-d);
        float f101 = set(x, y-1, z, value-d);
        float f121 = set(x, y+1, z, value-d);
        float f110 = set(x, y, z-1, value-d);
        float f112 = set(x, y, z+1, value-d);

        float f010 = set(x-1, y, z-1, Math.max(f011 - .6f * d, f110 - .6f * d));
        float f210 = set(x+1, y, z-1, Math.max(f211 - .6f * d, f110 - .6f * d));
        float f012 = set(x-1, y, z+1, Math.max(f011 - .6f * d, f112 - .6f * d));
        float f212 = set(x+1, y, z+1, Math.max(f211 - .6f * d, f112 - .6f * d));

        float f102 = set(x, y-1, z+1, Math.max(f101 - .6f * d, f112 - .6f * d));
        float f001 = set(x-1, y-1, z, Math.max(f101 - .6f * d, f011 - .6f * d));
        float f201 = set(x+1, y-1, z, Math.max(f101 - .6f * d, f211 - .6f * d));
        float f100 = set(x, y-1, z-1, Math.max(f101 - .6f * d, f110 - .6f * d));

        float f122 = set(x, y+1, z+1, Math.max(f121 - .6f * d, f112 - .6f * d));
        float f021 = set(x-1, y+1, z, Math.max(f121 - .6f * d, f011 - .6f * d));
        float f221 = set(x+1, y+1, z, Math.max(f121 - .6f * d, f211 - .6f * d));
        float f120 = set(x, y+1, z-1, Math.max(f121 - .6f * d, f110 - .6f * d));

//        float f002 = set(x-1, y-1, z+1, Math.max(Math.max(f001 - .5f * d, f102 - .5f * d), f012 - .5f * d));
//        float f202 = set(x+1, y-1, z+1, Math.max(Math.max(f201 - .5f * d, f102 - .5f * d), f212 - .5f * d));
//        float f000 = set(x-1, y-1, z-1, Math.max(Math.max(f001 - .5f * d, f100 - .5f * d), f010 - .5f * d));
//        float f200 = set(x+1, y-1, z-1, Math.max(Math.max(f201 - .5f * d, f100 - .5f * d), f210 - .5f * d));
//
//        float f022 = set(x-1, y+1, z+1, Math.max(Math.max(f021 - .5f * d, f122 - .5f * d), f012 - .5f * d));
//        float f222 = set(x+1, y+1, z+1, Math.max(Math.max(f221 - .5f * d, f122 - .5f * d), f212 - .5f * d));
//        float f020 = set(x-1, y+1, z-1, Math.max(Math.max(f021 - .5f * d, f120 - .5f * d), f010 - .5f * d));
//        float f220 = set(x+1, y+1, z-1, Math.max(Math.max(f221 - .5f * d, f120 - .5f * d), f210 - .5f * d));

        return value;
    }

    public float setNoRecurse(int x, int y, int z, float value) {
        value *= getBlocking(x, y, z);
        float old = get(x, y, z) / 127.0f;
        if (value <= old) {
            // Nothing to do.
            return value;
        }
        setInternal(x, y, z, (byte) (value * 127));
        return value;
    }

    public interface Blocker {
        // A value between 0 and 1 indicating how much of the radiation is blocked. 0 means totally blocked. 1 is totally not blocked
        float getBlockingFactor(int x, int y, int z);
    }

    public static char d(byte v) {
        if (v >= 125) {
            return '#';
        } else if (v >= 100) {
            return '*';
        } else if (v >= 70) {
            return '+';
        } else if (v >= 40) {
            return '-';
        } else if (v >= 10) {
            return '.';
        } else {
            return ' ';
        }
    }

    public static void main(String[] args) {
        int radius = 15;
        Field field = new Field(radius, new Blocker() {
            @Override
            public float getBlockingFactor(int x, int y, int z) {
                if (x == 20 && z >= 30 && z <= 50) {
                    return 0;
                }
                return 1;
            }
        });
        field.set(radius+1, radius+1, radius+1, 1.0f);
        for (int y = 0 ; y < radius + radius +1 ; y++) {
            System.out.println("======================================================: y=" + y);
            for (int z = 0 ; z < radius + radius +1 ; z++) {
                for (int x = 0 ; x < radius + radius +1 ; x++) {
                    System.out.print(d(field.get(x+1, y+1, z+1)));
                }
                System.out.println("");
            }
        }
    }
}
