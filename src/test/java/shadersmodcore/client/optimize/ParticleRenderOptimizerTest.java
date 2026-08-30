package shadersmodcore.client.optimize;

import java.util.ArrayList;
import java.util.List;

public final class ParticleRenderOptimizerTest {
    private ParticleRenderOptimizerTest() {
    }

    public static void main(String[] args) {
        List<Object>[] empty = layers();
        check(!ParticleRenderOptimizer.shouldRender(empty),
            "empty regular particle layers should skip rendering");
        check(ParticleRenderOptimizer.shouldRender(empty, false),
            "disabled optimization must keep the vanilla path");

        List<Object>[] withTerrain = layers();
        withTerrain[1].add(new Object());
        check(ParticleRenderOptimizer.shouldRender(withTerrain),
            "a populated regular layer must keep rendering");

        List<Object>[] withLitOnly = layers();
        withLitOnly[3].add(new Object());
        check(ParticleRenderOptimizer.shouldRender(withLitOnly),
            "lit-only particles keep interpolation state up to date");

        check(ParticleRenderOptimizer.shouldRender(null),
            "unknown layer state must fall back to vanilla rendering");
        check(ParticleRenderOptimizer.shouldRender(new List<?>[2]),
            "short layer state must fall back to vanilla rendering");
        check(ParticleRenderOptimizer.shouldRender(new List<?>[5]),
            "unexpected layer state must fall back to vanilla rendering");

        System.out.println("ParticleRenderOptimizerTest passed");
    }

    @SuppressWarnings("unchecked")
    private static List<Object>[] layers() {
        List<Object>[] layers = (List<Object>[]) new List<?>[4];
        for (int i = 0; i < layers.length; ++i) {
            layers[i] = new ArrayList<Object>();
        }
        return layers;
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
