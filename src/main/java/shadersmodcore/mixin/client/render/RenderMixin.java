package shadersmodcore.mixin.client.render;

import net.minecraft.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import shadersmodcore.client.shader.Shaders;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin({Render.class})
public class RenderMixin {
   @Redirect(method = "renderShadow", at = @At(value = "FIELD", target = "Lnet/minecraft/Entity;disable_shadow:Z"))
   private boolean shouldSkipDefaultShadow(Entity instance) {
      return Shaders.shouldSkipDefaultShadow;
   }
}
