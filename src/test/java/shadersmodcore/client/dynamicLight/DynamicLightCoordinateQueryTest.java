package shadersmodcore.client.dynamicLight;

public final class DynamicLightCoordinateQueryTest {
    private DynamicLightCoordinateQueryTest() {
    }

    public static void main(String[] args) {
        check(DynamicLights.getCombinedLight(3, -2, 7, 0xA00020) == 0xA00020,
            "coordinate queries must preserve brightness when no light is present");
        check(DynamicLights.getCombinedLight(3, -2, 7, 0xA000F0) == 0xA000F0,
            "coordinate queries must preserve maximum block brightness");

        System.out.println("DynamicLightCoordinateQueryTest passed");
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
