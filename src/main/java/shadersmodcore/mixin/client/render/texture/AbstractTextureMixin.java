package shadersmodcore.mixin.client.render.texture;

import net.minecraft.AbstractTexture;
import shadersmodcore.api.AbstractTextureAccessor;
import shadersmodcore.client.shader.MultiTexID;
import shadersmodcore.client.shader.ShadersTex;
import net.xiaoyu233.fml.util.ReflectHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(AbstractTexture.class)
public class AbstractTextureMixin implements AbstractTextureAccessor {
   @Shadow protected int glTextureId;

   @Unique public MultiTexID multiTex;

   @Unique
   public int getGlTextureId() {
      return this.glTextureId;
   }

   @Unique
   public void setMultiTexID(MultiTexID id) { this.multiTex = id; }

   @Unique
   public void setGlTextureId(int id) {
      this.glTextureId = id;
   }

   @Unique
   public MultiTexID getMultiTexID() {
      return ShadersTex.getMultiTexID(ReflectHelper.dyCast(this));
   }

   @Unique
   public MultiTexID getMultiTexID0() {
      return this.multiTex;
   }
}
