package shadersmodcore.util;

public final class TessellatorBufferGrowthTest {
    private TessellatorBufferGrowthTest() {
    }

    public static void main(String[] args) {
        check(TessellatorBufferGrowth.VERTEX_SCRATCH_SIZE == 16,
            "quad normal generation should use a fixed four-vertex scratch array");
        check(TessellatorBufferGrowth.nextCapacity(0) == 65536,
            "an empty buffer should use the vanilla-compatible initial capacity");
        check(TessellatorBufferGrowth.nextCapacity(1) == 65536,
            "a tiny instance buffer must grow enough for one complete vertex");
        check(TessellatorBufferGrowth.nextCapacity(2097152) == 4194304,
            "growth should be based on the current instance capacity");
        check(TessellatorBufferGrowth.nextCapacity(8388608) == 16777216,
            "growth should reach the maximum vertex buffer capacity");
        check(TessellatorBufferGrowth.nextCapacity(16777216) == 16777216,
            "a maximum-sized buffer must not grow or overflow");
        check(TessellatorBufferGrowth.needsGrowth(2097088, 2097152),
            "the reserved tail must trigger growth before a vertex write");
        check(!TessellatorBufferGrowth.needsGrowth(2097024, 2097152),
            "capacity checks must use the instance array length");
        check(TessellatorBufferGrowth.needsFlushAfterGrowth(16777152, 0, 16777216),
            "growth into the maximum tail must flush at a quad boundary");
        check(!TessellatorBufferGrowth.needsFlushAfterGrowth(16777152, 1, 16777216),
            "partial quads must remain in the buffer while the reserved tail is safe");

        System.out.println("TessellatorBufferGrowthTest passed");
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
