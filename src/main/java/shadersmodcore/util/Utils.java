package shadersmodcore.util;

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
}
