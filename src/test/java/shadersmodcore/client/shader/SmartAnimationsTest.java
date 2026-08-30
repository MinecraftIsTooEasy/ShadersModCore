package shadersmodcore.client.shader;

public final class SmartAnimationsTest {
    private SmartAnimationsTest() {
    }

    public static void main(String[] args) {
        SmartAnimations.setEnabled(false);
        SmartAnimations.resetTexturesRendered();
        check(SmartAnimations.shouldAnimateTexture(7, false), "disabled mode keeps vanilla path");

        SmartAnimations.setEnabled(true);
        SmartAnimations.reset();
        check(SmartAnimations.shouldAnimateTexture(7, false), "empty tracking keeps vanilla path");

        SmartAnimations.textureRendered(7, false);
        check(SmartAnimations.shouldAnimateTexture(7, false), "rendered texture animates");
        check(SmartAnimations.shouldAnimateTexture(8, false), "untracked texture keeps vanilla path");

        SmartAnimations.textureRendered(8, false);
        SmartAnimations.resetTexturesRendered();
        SmartAnimations.textureRendered(7, false);
        check(!SmartAnimations.shouldAnimateTexture(8, false), "known but unrendered texture is skipped");

        SmartAnimations.textureRendered(9, true);
        check(SmartAnimations.shouldAnimateTexture(9, false), "shadow pass does not mark textures");

        SmartAnimations.resetTexturesRendered();
        SmartAnimations.textureRendered(7, false);
        check(!SmartAnimations.shouldAnimateTexture(8, false), "tick boundary keeps known texture tracking");
        SmartAnimations.reset();
        check(SmartAnimations.shouldAnimateTexture(8, false), "resource reset forgets texture IDs");
        SmartAnimations.setEnabled(false);
        System.out.println("SmartAnimationsTest passed");
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
