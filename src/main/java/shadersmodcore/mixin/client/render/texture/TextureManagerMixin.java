package shadersmodcore.mixin.client.render.texture;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.ResourceLocation;
import net.minecraft.TextureManager;
import net.minecraft.TextureObject;
import org.spongepowered.asm.mixin.injection.At;
import shadersmodcore.client.shader.ShadersTex;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;

@Mixin({TextureManager.class})
public abstract class TextureManagerMixin {
   @Shadow
   @Final
   private Map mapTextureObjects;

   @Shadow public abstract boolean loadTexture(ResourceLocation par1ResourceLocation, TextureObject par2TextureObject);

//   /**
//    * @author
//    * @reason
//    */
//   @Overwrite
//   public void bindTexture(ResourceLocation par1ResourceLocation) {
//      Object var2 = this.mapTextureObjects.get(par1ResourceLocation);
//      if (var2 == null) {
//         var2 = new SimpleTexture(par1ResourceLocation);
//         this.loadTexture(par1ResourceLocation, (TextureObject)var2);
//      }
//
//      ShadersTex.bindTexture((TextureObject) var2);
//   }

   @WrapWithCondition(method = "bindTexture", at = @At(value = "INVOKE", target = "Lnet/minecraft/TextureUtil;bindTexture(I)V"))
   private boolean bindTexture(int i, @Local Object var2) {
      ShadersTex.bindTexture((TextureObject) var2);
      return true;
   }
}
