package shadersmodcore.client.dynamicLight;

import net.minecraft.EnumFacing;

public final class DynamicLightChunkUpdateTest {
    private DynamicLightChunkUpdateTest() {
    }

    public static void main(String[] args) {
        int[] coordinates = new int[24];
        DynamicLight.fillChunkUpdateCoordinates(coordinates, 8, 64, -4,
            EnumFacing.EAST, EnumFacing.UP, EnumFacing.SOUTH);

        check(coordinates[0] == 8 && coordinates[1] == 64 && coordinates[2] == -4,
            "base chunk coordinate must be written first");
        check(coordinates[3] == -8 && coordinates[4] == 64 && coordinates[5] == -4,
            "x-facing chunk offset must be 16 blocks");
        check(coordinates[6] == 8 && coordinates[7] == 64 && coordinates[8] == 12,
            "z-facing chunk offset must be 16 blocks");
        check(coordinates[9] == -8 && coordinates[10] == 64 && coordinates[11] == 12,
            "combined horizontal chunk offset must preserve order");
        check(coordinates[12] == 8 && coordinates[13] == 80 && coordinates[14] == -4,
            "y-facing chunk offset must be 16 blocks");
        check(coordinates[21] == -8 && coordinates[22] == 80 && coordinates[23] == 12,
            "final combined offset must preserve order");

        System.out.println("DynamicLightChunkUpdateTest passed");
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
