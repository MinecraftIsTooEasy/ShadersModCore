package net.wenscHuix.mitemod.mixin.render.texture;

import net.minecraft.AbstractTexture;
import net.wenscHuix.mitemod.shader.client.MultiTexID;
import net.wenscHuix.mitemod.shader.client.ShadersTex;
import net.xiaoyu233.fml.util.ReflectHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(AbstractTexture.class)
public class AbstractTextureMixin {
   @Shadow protected int glTextureId;
   @Unique
   public MultiTexID multiTex;

   @Unique
   public int getGlTextureId() {
      return this.glTextureId;
   }

   @Unique
   public void setGlTextureId(int a) {
      this.glTextureId = a;
   }

   @Unique
   public MultiTexID getMultiTexID() {
      return ShadersTex.getMultiTexID(ReflectHelper.dyCast(this));
   }
}
