package shadersmodcore.util;

/**
 * Capacity rules for the shader tessellator's integer vertex buffer.
 * The shader vertex layout reserves 64 words at the end for quad expansion.
 */
public final class TessellatorBufferGrowth {
    public static final int MAX_CAPACITY = 16777216;
    public static final int VERTEX_SCRATCH_SIZE = 16;
    private static final int INITIAL_CAPACITY = 65536;
    private static final int RESERVED_WORDS = 64;

    private TessellatorBufferGrowth() {
    }

    public static boolean needsGrowth(int rawBufferIndex, int capacity) {
        return rawBufferIndex >= capacity - RESERVED_WORDS;
    }

    public static boolean needsFlushAfterGrowth(int rawBufferIndex, int addedVertices, int capacity) {
        return capacity >= MAX_CAPACITY && addedVertices % 4 == 0 && needsGrowth(rawBufferIndex, capacity);
    }

    public static int nextCapacity(int capacity) {
        if (capacity < INITIAL_CAPACITY) {
            return INITIAL_CAPACITY;
        }
        if (capacity >= MAX_CAPACITY) {
            return MAX_CAPACITY;
        }
        return Math.min(capacity * 2, MAX_CAPACITY);
    }
}
