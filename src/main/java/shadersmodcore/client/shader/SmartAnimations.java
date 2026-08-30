package shadersmodcore.client.shader;

import java.util.BitSet;

/**
 * Tracks texture usage between animation ticks so unused atlas animations can
 * keep their previous frame. Sprite-level tracking is intentionally outside
 * this slice because this Minecraft target has no OptiFine animation index
 * or compiled-chunk sprite bitsets.
 */
public final class SmartAnimations {
    private static final BitSet texturesRendered = new BitSet();
    private static final BitSet trackedTextures = new BitSet();
    private static boolean enabled;
    private static boolean trackingObserved;

    private SmartAnimations() {
    }

    public static void setEnabled(boolean value) {
        if (enabled != value) {
            enabled = value;
            reset();
        }
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static boolean isActive(boolean shadowPass) {
        return enabled && !shadowPass;
    }

    public static void textureRendered(int textureId, boolean shadowPass) {
        if (isActive(shadowPass) && textureId >= 0) {
            texturesRendered.set(textureId);
            trackedTextures.set(textureId);
            trackingObserved = true;
        }
    }

    /**
     * Returns whether an atlas should run its normal animation path. An empty
     * snapshot is treated as unavailable tracking data to preserve vanilla
     * behavior during startup or when another renderer bypasses our binder.
     */
    public static boolean shouldAnimateTexture(int textureId, boolean shadowPass) {
        if (!isActive(shadowPass) || !trackingObserved) {
            return true;
        }
        return textureId < 0 || !trackedTextures.get(textureId) || texturesRendered.get(textureId);
    }

    public static void resetTexturesRendered() {
        texturesRendered.clear();
        trackingObserved = false;
    }

    public static void reset() {
        resetTexturesRendered();
        trackedTextures.clear();
    }

}
