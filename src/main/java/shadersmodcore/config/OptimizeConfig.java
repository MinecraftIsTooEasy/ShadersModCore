package shadersmodcore.config;

import net.minecraft.Minecraft;
import shadersmodcore.client.shader.SmartAnimations;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

public class OptimizeConfig {
    static String optionsFileName = "optimize.txt";
    public static File configFile;
    public static Properties optimizeConfig;

    public static boolean blockDestroyEffects;
    public static boolean explodeEffects;
    public static boolean potionEffects;

    public static boolean dynamicLights;
    /** OptiFine-compatible light position polling throttle (500 ms). */
    public static boolean dynamicLightsFast;
    /** OptiFine-style texture usage tracking; disabled until explicitly enabled. */
    public static boolean smartAnimations;
    public static boolean drawSelectionBox;
    public static boolean renderRainSnow;
    public static boolean renderShadow;

    public static int grassQuality;
    public static int leavesQuality;
    public static boolean vignetteQuality;
    public static int dropsQuality;
    public static int waterQuality;
    public static int rainQuality;

    public static void loadConfig() {
        boolean configExists = configFile.exists();
        optimizeConfig.clear();

        if (configExists) {
            try (FileReader reader = new FileReader(configFile)) {
                optimizeConfig.load(reader);
            } catch (Exception var1) {
                var1.printStackTrace();
            }
        } else {
            applyConfig();
            storeConfig();
            return;
        }

        applyConfig();
    }

    private static void applyConfig() {
        blockDestroyEffects = ConfigUtils.parseBoolean(optimizeConfig.getProperty("blockDestroyParticles"), true);
        explodeEffects = ConfigUtils.parseBoolean(optimizeConfig.getProperty("explodeParticles"), true);
        potionEffects = ConfigUtils.parseBoolean(optimizeConfig.getProperty("effectParticles"), true);

        dynamicLights = ConfigUtils.parseBoolean(optimizeConfig.getProperty("dynamicLights"), true);
        dynamicLightsFast = ConfigUtils.parseBoolean(optimizeConfig.getProperty("dynamicLightsFast"), false);
        smartAnimations = ConfigUtils.parseBoolean(optimizeConfig.getProperty("smartAnimations"), false);
        SmartAnimations.setEnabled(smartAnimations);
        drawSelectionBox = ConfigUtils.parseBoolean(optimizeConfig.getProperty("drawSelectionBox"), true);
        renderRainSnow = ConfigUtils.parseBoolean(optimizeConfig.getProperty("renderRainSnow"), true);
        renderShadow = ConfigUtils.parseBoolean(optimizeConfig.getProperty("renderShadow"), true);

        grassQuality = ConfigUtils.parseInt(optimizeConfig.getProperty("grassQuality"), 0);
        leavesQuality = ConfigUtils.parseInt(optimizeConfig.getProperty("leavesQuality"), 0);
        vignetteQuality = ConfigUtils.parseBoolean(optimizeConfig.getProperty("cloudsQuality"), true);
        dropsQuality = ConfigUtils.parseInt(optimizeConfig.getProperty("dropsQuality"), 0);
        waterQuality = ConfigUtils.parseInt(optimizeConfig.getProperty("waterQuality"), 0);
        rainQuality = ConfigUtils.parseInt(optimizeConfig.getProperty("rainQuality"), 0);
    }

    public static void storeConfig() {
        optimizeConfig.setProperty("blockDestroyParticles", Boolean.toString(blockDestroyEffects));
        optimizeConfig.setProperty("explodeParticles", Boolean.toString(explodeEffects));
        optimizeConfig.setProperty("effectParticles", Boolean.toString(potionEffects));

        optimizeConfig.setProperty("dynamicLights", Boolean.toString(dynamicLights));
        optimizeConfig.setProperty("dynamicLightsFast", Boolean.toString(dynamicLightsFast));
        optimizeConfig.setProperty("smartAnimations", Boolean.toString(smartAnimations));
        optimizeConfig.setProperty("drawSelectionBox", Boolean.toString(drawSelectionBox));
        optimizeConfig.setProperty("renderRainSnow", Boolean.toString(renderRainSnow));
        optimizeConfig.setProperty("renderShadow", Boolean.toString(renderShadow));

        optimizeConfig.setProperty("grassQuality", Integer.toString(grassQuality));
        optimizeConfig.setProperty("leavesQuality", Integer.toString(leavesQuality));
        optimizeConfig.setProperty("cloudsQuality", Boolean.toString(vignetteQuality));
        optimizeConfig.setProperty("dropsQuality", Integer.toString(dropsQuality));
        optimizeConfig.setProperty("waterQuality", Integer.toString(waterQuality));
        optimizeConfig.setProperty("rainQuality", Integer.toString(rainQuality));

        try (FileWriter writer = new FileWriter(configFile)) {
            optimizeConfig.store(writer, null);
        } catch (IOException var1) {
            var1.printStackTrace();
        }

    }

    static {
        configFile = new File(Minecraft.getMinecraft().mcDataDir, optionsFileName);
        optimizeConfig = new Properties();
    }
}
