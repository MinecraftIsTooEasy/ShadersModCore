package shadersmodcore.client.shader;


import net.minecraft.AbstractTexture;
import net.minecraft.ResourceManager;
import shadersmodcore.api.AbstractTextureAccessor;
import net.xiaoyu233.fml.util.ReflectHelper;

public class DefaultTexture extends AbstractTexture {
   public DefaultTexture() {
      this.loadTexture(null);
   }

   public void loadTexture(ResourceManager resourceManager) {
      int[] aint = ShadersTex.createAIntImage(1, -1);
      ShadersTex.setupTexture(((AbstractTextureAccessor) ReflectHelper.dyCast(this))
              .getMultiTexID(), aint, 1, 1, false, false);
   }
}
