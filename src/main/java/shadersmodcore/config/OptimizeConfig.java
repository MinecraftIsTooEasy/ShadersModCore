package shadersmodcore.config;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;
import net.minecraft.Minecraft;

public class OptimizeConfig {
   static String optionsFileName = "optimize.txt";
   public static File configFile;
   public static Properties optimizeConfig;

   public static boolean blockDestroyEffects;
   public static boolean explodeEffects;

   public static boolean dynamicLights;
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
      try {
         if (configFile.exists()) {
            FileReader reader = new FileReader(configFile);
            optimizeConfig.load(reader);
            reader.close();
         } else if (!configFile.exists()) {
            storeConfig();
         }
      } catch (Exception var1) {
         var1.printStackTrace();
      }

      blockDestroyEffects = Boolean.parseBoolean(optimizeConfig.getProperty("blockDestroyEffects", "true"));
      explodeEffects = Boolean.parseBoolean(optimizeConfig.getProperty("explodeEffects", "true"));

      dynamicLights = Boolean.parseBoolean(optimizeConfig.getProperty("dynamicLights", "true"));
      drawSelectionBox = Boolean.parseBoolean(optimizeConfig.getProperty("drawSelectionBox", "true"));
      renderRainSnow = Boolean.parseBoolean(optimizeConfig.getProperty("renderRainSnow", "true"));
      renderShadow = Boolean.parseBoolean(optimizeConfig.getProperty("renderShadow", "true"));

      grassQuality = Integer.parseInt(optimizeConfig.getProperty("grassQuality", "0"));
      leavesQuality = Integer.parseInt(optimizeConfig.getProperty("leavesQuality", "0"));
      vignetteQuality = Boolean.parseBoolean(optimizeConfig.getProperty("cloudsQuality", "true"));
      dropsQuality = Integer.parseInt(optimizeConfig.getProperty("dropsQuality", "0"));
      waterQuality = Integer.parseInt(optimizeConfig.getProperty("waterQuality", "0"));
      rainQuality = Integer.parseInt(optimizeConfig.getProperty("rainQuality", "0"));
   }

   public static void storeConfig() {
      optimizeConfig.setProperty("blockDestroyEffects", Boolean.toString(blockDestroyEffects));
      optimizeConfig.setProperty("explodeEffects", Boolean.toString(explodeEffects));

      optimizeConfig.setProperty("dynamicLights", Boolean.toString(dynamicLights));
      optimizeConfig.setProperty("drawSelectionBox", Boolean.toString(drawSelectionBox));
      optimizeConfig.setProperty("renderRainSnow", Boolean.toString(renderRainSnow));
      optimizeConfig.setProperty("renderShadow", Boolean.toString(renderShadow));

      optimizeConfig.setProperty("grassQuality", Integer.toString(grassQuality));
      optimizeConfig.setProperty("leavesQuality", Integer.toString(leavesQuality));
      optimizeConfig.setProperty("cloudsQuality", Boolean.toString(vignetteQuality));
      optimizeConfig.setProperty("dropsQuality", Integer.toString(dropsQuality));
      optimizeConfig.setProperty("waterQuality", Integer.toString(waterQuality));
      optimizeConfig.setProperty("rainQuality", Integer.toString(rainQuality));

      try {
         FileWriter writer = new FileWriter(configFile);
         optimizeConfig.store(writer, null);
         writer.close();
      } catch (IOException var1) {
         var1.printStackTrace();
      }

   }

   static {
      configFile = new File(Minecraft.getMinecraft().mcDataDir, optionsFileName);
      optimizeConfig = new Properties();
   }
}
