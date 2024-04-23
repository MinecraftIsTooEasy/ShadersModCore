package net.wenscHuix.mitemod.shader.client.dynamicLight.config;

import net.minecraft.ResourceLocation;
import net.wenscHuix.mitemod.shader.util.EntityUtils;

public class EntityClassLocator implements IObjectLocator {
   public Object getObject(ResourceLocation loc) {
       return EntityUtils.getEntityClassByName(loc.getResourcePath());
   }
}
