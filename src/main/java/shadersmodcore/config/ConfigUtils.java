package shadersmodcore.config;

public final class ConfigUtils {
    private ConfigUtils() {
    }

    public static int parseInt(String value, int fallback) {
        try {
            if (value == null) {
                return fallback;
            }
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException ignored) {
            return fallback;
        }
    }

    public static float parseFloat(String value, float fallback) {
        try {
            if (value == null) {
                return fallback;
            }
            return Float.parseFloat(value.trim());
        } catch (NumberFormatException ignored) {
            return fallback;
        }
    }

    public static boolean parseBoolean(String value, boolean fallback) {
        if (value == null) {
            return fallback;
        }
        return Boolean.parseBoolean(value.trim());
    }
}
