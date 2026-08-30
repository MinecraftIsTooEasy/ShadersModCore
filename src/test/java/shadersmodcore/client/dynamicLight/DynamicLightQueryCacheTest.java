package shadersmodcore.client.dynamicLight;

public final class DynamicLightQueryCacheTest {
    private DynamicLightQueryCacheTest() {
    }

    public static void main(String[] args) {
        DynamicLightQueryCache cache = new DynamicLightQueryCache(4);
        check(cache.get(1, 2, 3, 0, 7L) == DynamicLightQueryCache.MISS,
            "empty cache must miss");

        cache.put(1, 2, 3, 0, 7L, 42);
        check(cache.get(1, 2, 3, 0, 7L) == 42, "same query must hit");
        check(cache.get(1, 2, 3, 1, 7L) == DynamicLightQueryCache.MISS,
            "different base brightness must miss");
        check(cache.get(1, 2, 3, 0, 8L) == DynamicLightQueryCache.MISS,
            "new light revision must miss");

        cache.put(1, 2, 3, 0, 8L, 43);
        check(cache.get(1, 2, 3, 0, 8L) == 43, "new revision must store independently");

        int[] colliding = findCollision(cache);
        cache.put(colliding[0], colliding[1], colliding[2], 0, 9L, 99);
        check(cache.get(colliding[0], colliding[1], colliding[2], 0, 9L) == 99,
            "replacement query must hit");
        check(cache.get(1, 2, 3, 0, 8L) == DynamicLightQueryCache.MISS,
            "collision must evict and force a safe miss");

        System.out.println("DynamicLightQueryCacheTest passed");
    }

    private static int[] findCollision(DynamicLightQueryCache cache) {
        for (int x = -32; x <= 32; ++x) {
            for (int y = -32; y <= 32; ++y) {
                for (int z = -32; z <= 32; ++z) {
                    if ((x != 1 || y != 2 || z != 3) && cache.sameSlot(1, 2, 3, x, y, z)) {
                        return new int[] {x, y, z};
                    }
                }
            }
        }
        throw new AssertionError("fixture could not find a direct-mapped collision");
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
