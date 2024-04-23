package net.wenscHuix.mitemod.mixin.render;

import net.minecraft.OpenGlHelper;
import net.wenscHuix.mitemod.shader.util.OpenGlHelperExtra;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(OpenGlHelper.class)
public class OpenGlHelperMixin {

   @Inject(method = "setActiveTexture", at = @At("HEAD"))
   private static void setActiveTexture(int par0, CallbackInfo info) {
      OpenGlHelperExtra.activeTexUnit = par0;
   }

}
