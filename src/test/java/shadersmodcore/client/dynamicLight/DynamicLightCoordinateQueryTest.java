package shadersmodcore.client.dynamicLight;

import net.minecraft.Entity;
import net.minecraft.NBTTagCompound;
import shadersmodcore.util.BlockPos;

import java.lang.reflect.Field;

public final class DynamicLightCoordinateQueryTest {
    private DynamicLightCoordinateQueryTest() {
    }

    public static void main(String[] args) {
        check(DynamicLights.getCombinedLight(3, -2, 7, 0xA00020) == 0xA00020,
            "coordinate queries must preserve brightness when no light is present");
        check(DynamicLights.getCombinedLight(3, -2, 7, 0xA000F0) == 0xA000F0,
            "coordinate queries must preserve maximum block brightness");

        try {
            DynamicLightsMap lights = getLightMap();
            lights.clear();
            DynamicLight light = new DynamicLight(new Entity(null) {
                @Override
                protected void entityInit() {
                }

                @Override
                protected void readEntityFromNBT(NBTTagCompound tag) {
                }

                @Override
                protected void writeEntityToNBT(NBTTagCompound tag) {
                }
            });
            set(light, "lastPosX", 3.25D);
            set(light, "lastPosY", -2.5D);
            set(light, "lastPosZ", 7.75D);
            set(light, "lastLightLevel", 12);
            set(light, "underwater", false);
            lights.put(1, light);

            int objectResult = DynamicLights.getCombinedLight(new BlockPos(4, -1, 6), 0xA00030);
            int primitiveResult = DynamicLights.getCombinedLight(4, -1, 6, 0xA00030);
            check(primitiveResult == objectResult,
                "primitive cache misses must use the same distance and merge math as BlockPos queries");
            lights.clear();
        } catch (ReflectiveOperationException exception) {
            throw new AssertionError("fixture could not configure a dynamic light", exception);
        }

        System.out.println("DynamicLightCoordinateQueryTest passed");
    }

    @SuppressWarnings("unchecked")
    private static DynamicLightsMap getLightMap() throws ReflectiveOperationException {
        Field field = DynamicLights.class.getDeclaredField("mapDynamicLights");
        field.setAccessible(true);
        return (DynamicLightsMap) field.get(null);
    }

    private static void set(DynamicLight light, String name, Object value)
        throws ReflectiveOperationException {
        Field field = DynamicLight.class.getDeclaredField(name);
        field.setAccessible(true);
        field.set(light, value);
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
