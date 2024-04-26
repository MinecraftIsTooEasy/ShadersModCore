package net.wenscHuix.mitemod.mixin.render.texture;

import net.minecraft.AbstractTexture;
import net.wenscHuix.mitemod.imixin.AbstractTextureAccessor;
import net.wenscHuix.mitemod.shader.client.MultiTexID;
import net.wenscHuix.mitemod.shader.client.ShadersTex;
import net.xiaoyu233.fml.util.ReflectHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(AbstractTexture.class)
public class AbstractTextureMixin implements AbstractTextureAccessor {
   @Shadow protected int glTextureId;
   @Unique
   public MultiTexID multiTex;

   @Unique
   public int mITE_Shader_Loader$getGlTextureId() {
      return this.glTextureId;
   }

   @Unique
   public void mITE_Shader_Loader$setMultiTexID(MultiTexID id) { this.multiTex = id; }

   @Unique
   public void mITE_Shader_Loader$setGlTextureId(int id) {
      this.glTextureId = id;
   }

   @Unique
   public MultiTexID mITE_Shader_Loader$getMultiTexID() {
      return ShadersTex.getMultiTexID(ReflectHelper.dyCast(this));
   }

   @Unique
   public MultiTexID mITE_Shader_Loader$getMultiTexID0() {
      return this.multiTex;
   }
}
