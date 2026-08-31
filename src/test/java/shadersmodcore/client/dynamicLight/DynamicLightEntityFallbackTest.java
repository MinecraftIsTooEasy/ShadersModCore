package shadersmodcore.client.dynamicLight;

import net.minecraft.Entity;

public final class DynamicLightEntityFallbackTest {
    private DynamicLightEntityFallbackTest() {
    }

    public static void main(String[] args) {
        int combinedLight = 0xA00020;
        check(DynamicLights.getCombinedLight((Entity) null, combinedLight) == combinedLight,
            "a null entity must preserve the original combined light");

        System.out.println("DynamicLightEntityFallbackTest passed");
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
