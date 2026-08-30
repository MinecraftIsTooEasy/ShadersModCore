package shadersmodcore.client.dynamicLight;

public final class DynamicLightBoundaryTest {
    private DynamicLightBoundaryTest() {
    }

    public static void main(String[] args) {
        check(DynamicLights.canSkipDynamicLightQuery(240),
            "maximum block brightness should skip dynamic-light work");
        check(DynamicLights.canSkipDynamicLightQuery(0xA000F0),
            "sky-light bits must not prevent the maximum-brightness fast path");
        check(!DynamicLights.canSkipDynamicLightQuery(239),
            "a lower block brightness still needs the dynamic-light query");
        check(!DynamicLights.canSkipDynamicLightQuery(-1),
            "unknown brightness values must keep the original path");
        check(!DynamicLights.canSkipDynamicLightQuery(0xA000FF),
            "non-standard low-byte encodings must keep the original path");
        check(DynamicLights.getCombinedLight(15.0D, 240) == 240,
            "entity light merge must preserve maximum block brightness");
        check(DynamicLights.getCombinedLight(15.0D, 0xA000F0) == 0xA000F0,
            "entity light merge must preserve sky-light bits at the boundary");

        System.out.println("DynamicLightBoundaryTest passed");
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
