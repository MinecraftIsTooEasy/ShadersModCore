package shadersmodcore.config;

public final class ConfigParsingTest {
    private ConfigParsingTest() {
    }

    public static void main(String[] args) {
        check(ConfigUtils.parseBoolean(" true ", false), "trimmed true must parse");
        check(!ConfigUtils.parseBoolean("not-a-boolean", true),
            "invalid boolean keeps OptiFine's primitive parser behavior");
        check(ConfigUtils.parseBoolean(null, true), "null boolean uses the fallback");

        check(ConfigUtils.parseFloat(" 1.25 ", 0.0F) == 1.25F,
            "trimmed float must parse");
        check(ConfigUtils.parseFloat("not-a-float", 2.5F) == 2.5F,
            "invalid float uses the fallback");
        check(ConfigUtils.parseFloat(null, 3.5F) == 3.5F,
            "null float uses the fallback");

        check(ConfigUtils.parseInt(" 7 ", 0) == 7, "trimmed integer must parse");
        check(ConfigUtils.parseInt("not-an-int", 11) == 11,
            "invalid integer uses the fallback");

        System.out.println("ConfigParsingTest passed");
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
