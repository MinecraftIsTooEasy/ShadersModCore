package net.wenscHuix.mitemod.optimize.gui;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;
import net.minecraft.Minecraft;

public class Config {
   static String optionsFileName = "optimize.txt";
   public static File configFile;
   public static Properties optimizeConfig;
   public static boolean blockDestroyEffects;
   public static boolean dynamicLights;
   public static boolean drawSelectionBox;

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
      dynamicLights = Boolean.parseBoolean(optimizeConfig.getProperty("dynamicLights", "false"));
      drawSelectionBox = Boolean.parseBoolean(optimizeConfig.getProperty("drawSelectionBox", "true"));
   }

   public static void storeConfig() {
      optimizeConfig.setProperty("blockDestroyEffects", Boolean.toString(blockDestroyEffects));
      optimizeConfig.setProperty("dynamicLights", Boolean.toString(dynamicLights));
      optimizeConfig.setProperty("drawSelectionBox", Boolean.toString(drawSelectionBox));

      try {
         FileWriter writer = new FileWriter(configFile);
         optimizeConfig.store(writer, (String)null);
         writer.close();
      } catch (IOException var1) {
         var1.printStackTrace();
      }

   }

   static {
      configFile = new File(Minecraft.w().x, optionsFileName);
      optimizeConfig = new Properties();
   }
}
