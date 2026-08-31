package shadersmodcore.client.dynamicLight;

import shadersmodcore.util.BlockPos;

import java.util.AbstractList;
import java.util.Iterator;
import java.util.List;

public final class DynamicLightScanTest {
    private DynamicLightScanTest() {
    }

    public static void main(String[] args) {
        List<DynamicLight> snapshot = new AbstractList<DynamicLight>() {
            @Override
            public DynamicLight get(int index) {
                throw new AssertionError("empty snapshot must not request an element");
            }

            @Override
            public int size() {
                return 0;
            }

            @Override
            public Iterator<DynamicLight> iterator() {
                throw new AssertionError("light scans must not allocate an Iterator");
            }
        };

        check(DynamicLights.getLightLevel(snapshot, new BlockPos(0, 0, 0)) == 0.0D,
            "an empty light snapshot should return zero without iterator traversal");

        System.out.println("DynamicLightScanTest passed");
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
