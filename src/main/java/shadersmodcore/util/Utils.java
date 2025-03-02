package shadersmodcore.util;

import net.minecraft.I18n;
import net.minecraft.Minecraft;
import net.minecraft.OpenGlHelper;
import net.minecraft.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import shadersmodcore.ShadersModCoreInit;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Utils {
   public static Object get(Object instance, String name, Class f) {
      try {
         Field field = instance.getClass().getDeclaredField(name);
         field.setAccessible(true);
         return field.get(instance);
      } catch (IllegalAccessException | NoSuchFieldException var4) {
         return null;
      }
   }

   public static void set(Object instance, String fileName, Object value) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
      try {
         Field field = instance.getClass().getDeclaredField(fileName);
         field.setAccessible(true);
         field.set(instance, value);
      } catch (IllegalAccessException | NoSuchFieldException var4) {
      }

   }

   public static Object call(Object instance, String name, Class r, Class[] parameterTypes, Object[] params) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
      Method method = instance.getClass().getDeclaredMethod(name, parameterTypes);
      method.setAccessible(true);
      return method.invoke(instance, params);
   }

   public static Object get(Class c, String name, Class f) {
      Field[] fields = c.getDeclaredFields();

      try {
         Field[] var4 = fields;
         int var5 = fields.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            Field field = var4[var6];
            field.setAccessible(true);
            if (field.getType() == f && field.getName().equals(name)) {
               return field.get(c);
            }
         }
      } catch (Exception var8) {
      }

      return null;
   }

   public static void set(Class c, String name, Object f) throws IllegalAccessException {
      Field[] fields = c.getDeclaredFields();
      Field[] var4 = fields;
      int var5 = fields.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         Field field = var4[var6];
         field.setAccessible(true);
         if (field.getName().equals(name)) {
            field.set(c, f);
         }
      }

   }

   public static Object call(Class c, String name, Class[] parameterTypes, Object[] params) {
      try {
         Method method = c.getMethod(name, parameterTypes);
         method.setAccessible(true);
         return method.invoke((Object)null, params);
      } catch (SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException var5) {
         return null;
      }
   }

   public static boolean convertIntToBoolean(int value) {
      if (value == 1) {
         return true;
      } else if (value == 2) {
         return false;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public static String getTranslationString(String[] par0ArrayOfStr, int par1) {
      if (par1 < 0 || par1 >= par0ArrayOfStr.length) {
         par1 = 0;
      }

      return I18n.getString(par0ArrayOfStr[par1]);
   }

   public static String getTranslationBoolean(boolean value) {
      return value ? I18n.getString("options.on") : I18n.getString("options.off");
   }

   //OP guys
   public static final ResourceLocation shaders_fix = new ResourceLocation(ShadersModCoreInit.shadersModID, "textures/shaders_workaround.png");

   public static void Fix() {
      Minecraft.getMinecraft().renderEngine.bindTexture(shaders_fix);
   }

   public static final ResourceLocation shaders_fix2 = new ResourceLocation(ShadersModCoreInit.shadersModID, "textures/LightingFix.png");

   public static void Fix2() {
      Minecraft.getMinecraft().renderEngine.bindTexture(shaders_fix2);
   }

   //numbers
   public static int INT_2X16 = 65536;
   public static int INT_MAX = 2147483647;

   //LIGHT
   public static int MAX_LIGHT_COORD = 15728880;


   public static void DisableFullBrightness(float lbx, float lby) {
      OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lbx, lby);
   }

   public static void EnableFullBrightness() {
      OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)(MAX_LIGHT_COORD % INT_2X16), (float)(MAX_LIGHT_COORD / INT_2X16));
   }

   public static int GLGetCurrentProgram() {
      return GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM);
   }

   public static void GLUseDefaultProgram() {
      GL20.glUseProgram(0);
   }

   public static void GLUseProgram(int program) {
      GL20.glUseProgram(program);
   }
}
