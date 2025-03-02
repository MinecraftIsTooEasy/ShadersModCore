package shadersmodcore.mixin.client.render;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import shadersmodcore.client.shader.Shaders;
import org.spongepowered.asm.mixin.Mixin;
import shadersmodcore.config.OptimizeConfig;

@Mixin({Render.class})
public class RenderMixin {
   @Redirect(method = "renderShadow", at = @At(value = "FIELD", target = "Lnet/minecraft/Entity;disable_shadow:Z"))
   private boolean shouldSkipDefaultShadow(Entity instance) {
      return Shaders.shouldSkipDefaultShadow;
   }

   @WrapOperation(method = "doRenderShadowAndFire", at = @At(value = "INVOKE", target = "Lnet/minecraft/Render;renderShadow(Lnet/minecraft/Entity;DDDFF)V"))
   private void renderShadow(Render instance, Entity z, double x, double v, double par1Entity, float par2, float par4, Operation<Void> original) {
      if (OptimizeConfig.renderShadow)
         original.call(instance, z, x, v, par1Entity, par2, par4);
   }
}
