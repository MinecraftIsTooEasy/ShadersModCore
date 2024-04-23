package net.wenscHuix.mitemod.mixin.render.texture;

import net.minecraft.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ResourceLocation.class})
public class ResourceLocationMixin {
   @Inject(method = "verifyExistence", at = @At("HEAD"), cancellable = true)
   public void verifyExistence(CallbackInfo info) {
      info.cancel();
   }
}
