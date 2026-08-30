package shadersmodcore.client.dynamicLight;

import java.util.Arrays;

/**
 * A small direct-mapped cache for repeated block-light queries on one thread.
 * A collision only evicts an entry, so a miss can always fall back to the
 * original dynamic-light calculation.
 */
final class DynamicLightQueryCache {
    static final int MISS = -1;

    private final int mask;
    private final int[] x;
    private final int[] y;
    private final int[] z;
    private final int[] baseLight;
    private final long[] revision;
    private final int[] values;

    DynamicLightQueryCache(int capacity) {
        if (capacity < 1 || (capacity & (capacity - 1)) != 0) {
            throw new IllegalArgumentException("capacity must be a positive power of two");
        }
        this.mask = capacity - 1;
        this.x = new int[capacity];
        this.y = new int[capacity];
        this.z = new int[capacity];
        this.baseLight = new int[capacity];
        this.revision = new long[capacity];
        this.values = new int[capacity];
        Arrays.fill(this.revision, Long.MIN_VALUE);
    }

    int get(int x, int y, int z, int baseLight, long revision) {
        int slot = slot(x, y, z, baseLight);
        return this.revision[slot] == revision
            && this.x[slot] == x
            && this.y[slot] == y
            && this.z[slot] == z
            && this.baseLight[slot] == baseLight
            ? this.values[slot]
            : MISS;
    }

    void put(int x, int y, int z, int baseLight, long revision, int value) {
        int slot = slot(x, y, z, baseLight);
        this.x[slot] = x;
        this.y[slot] = y;
        this.z[slot] = z;
        this.baseLight[slot] = baseLight;
        this.values[slot] = value;
        this.revision[slot] = revision;
    }

    boolean sameSlot(int x1, int y1, int z1, int x2, int y2, int z2) {
        return slot(x1, y1, z1, 0) == slot(x2, y2, z2, 0);
    }

    private int slot(int x, int y, int z, int baseLight) {
        int hash = x * 73428767;
        hash = (hash ^ y) * 912931;
        hash = (hash ^ z) * 19349663;
        hash ^= baseLight * 83492791;
        return hash & this.mask;
    }
}
