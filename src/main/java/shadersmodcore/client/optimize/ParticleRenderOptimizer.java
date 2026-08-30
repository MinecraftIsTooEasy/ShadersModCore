package shadersmodcore.client.optimize;

import java.util.List;

/**
 * Keeps the fully empty particle path out of the renderer while retaining a
 * vanilla fallback for an unexpected layer layout. Layer 3 is included because
 * lit particles can consume the interpolation values populated by the regular
 * particle method.
 */
public final class ParticleRenderOptimizer {
    private static final int RENDERED_LAYER_COUNT = 4;

    private ParticleRenderOptimizer() {
    }

    public static boolean shouldRender(List<?>[] layers) {
        return shouldRender(layers, true);
    }

    public static boolean shouldRender(List<?>[] layers, boolean skipEmpty) {
        if (!skipEmpty) {
            return true;
        }

        if (layers == null || layers.length != RENDERED_LAYER_COUNT) {
            return true;
        }

        for (int i = 0; i < RENDERED_LAYER_COUNT; ++i) {
            List<?> layer = layers[i];
            if (layer == null || !layer.isEmpty()) {
                return true;
            }
        }

        return false;
    }
}
