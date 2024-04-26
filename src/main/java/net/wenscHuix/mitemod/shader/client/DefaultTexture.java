package net.wenscHuix.mitemod.shader.client;


import net.minecraft.AbstractTexture;
import net.minecraft.ResourceManager;
import net.wenscHuix.mitemod.imixin.AbstractTextureAccessor;
import net.xiaoyu233.fml.util.ReflectHelper;

public class DefaultTexture extends AbstractTexture {
   public DefaultTexture() {
      this.loadTexture((ResourceManager)null);
   }

   public void loadTexture(ResourceManager resourceManager) {
      int[] aint = ShadersTex.createAIntImage(1, -1);
      ShadersTex.setupTexture(((AbstractTextureAccessor) ReflectHelper.dyCast(this))
              .mITE_Shader_Loader$getMultiTexID(), aint, 1, 1, false, false);
   }

   public void a(ResourceManager resourceManager) {
   }
}
