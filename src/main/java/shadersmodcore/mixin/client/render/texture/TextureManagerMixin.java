package shadersmodcore.mixin.client.render.texture;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.TextureManager;
import net.minecraft.TextureObject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.Mixin;
import shadersmodcore.client.shader.ShadersTex;

@Mixin({TextureManager.class})
public abstract class TextureManagerMixin {

   @WrapWithCondition(method = "bindTexture", at = @At(value = "INVOKE", target = "Lnet/minecraft/TextureUtil;bindTexture(I)V"))
   private boolean bindTexture(int i, @Local Object var2) {
      ShadersTex.bindTexture((TextureObject) var2);
      return true;
   }
}
