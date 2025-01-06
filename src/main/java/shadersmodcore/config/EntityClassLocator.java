package shadersmodcore.config;

import net.minecraft.ResourceLocation;
import shadersmodcore.util.EntityUtils;

public class EntityClassLocator implements IObjectLocator {
   public Object getObject(ResourceLocation loc) {
       return EntityUtils.getEntityClassByName(loc.getResourcePath());
   }
}
