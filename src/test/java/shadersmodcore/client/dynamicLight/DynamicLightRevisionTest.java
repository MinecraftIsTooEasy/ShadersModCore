package shadersmodcore.client.dynamicLight;

public final class DynamicLightRevisionTest {
    private DynamicLightRevisionTest() {
    }

    public static void main(String[] args) {
        check(DynamicLights.advanceLightRevision(12L, false) == 12L,
            "unchanged light state must keep the query-cache revision stable");
        check(DynamicLights.advanceLightRevision(12L, true) == 13L,
            "changed light state must invalidate cached queries");
        check(DynamicLights.advanceLightRevision(Long.MAX_VALUE, false) == Long.MAX_VALUE,
            "unchanged state must not overflow the revision counter");

        System.out.println("DynamicLightRevisionTest passed");
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
