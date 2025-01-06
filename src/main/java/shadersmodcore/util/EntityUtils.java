package shadersmodcore.util;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.EntityList;
import shadersmodcore.config.ShaderConfig;

public class EntityUtils {
   private static final Map mapClassByName = new HashMap();

   public static Class getEntityClassByName(String name) {
      Class oclass = (Class)mapClassByName.get(name);
      return oclass;
   }

   static {
      for(int i = 0; i < 1000; ++i) {
         Class oclass = EntityList.getClassFromID(i);
         if (oclass != null) {
            String s = EntityList.getStringFromID(i);
            if (s != null) {
               if (mapClassByName.containsKey(s)) {
                  ShaderConfig.warn("Duplicate entity name: " + s + ", class1: " + mapClassByName.get(s) + ", class2: " + oclass);
               }

               mapClassByName.put(s, oclass);
            }
         }
      }

   }
}
