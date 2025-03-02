package shadersmodcore.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.GameSettings;
import net.minecraft.GuiChat;
import net.minecraft.Minecraft;
import org.spongepowered.asm.mixin.Shadow;
import shadersmodcore.config.OptimizeConfig;
import shadersmodcore.client.shader.Shaders;
import net.xiaoyu233.fml.util.ReflectHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import shadersmodcore.util.Utils;

@Mixin({Minecraft.class})
public class MinecraftMixin {
   @Shadow public GuiChat imposed_gui_chat;

   @Inject(at = {@At("TAIL")}, method = {"startGame"})
   private void injectEnableLightmap(CallbackInfo callbackInfo) {
      Shaders.startup(ReflectHelper.dyCast(this));
      OptimizeConfig.loadConfig();
   }

   @WrapOperation(method = "runGameLoop", at = @At(value = "INVOKE", target = "Lnet/minecraft/GameSettings;isFancyGraphicsEnabled()Z"))
   private boolean grassQuality(GameSettings instance, Operation<Boolean> original) {
      if (OptimizeConfig.grassQuality != 0)
         return Utils.convertIntToBoolean(OptimizeConfig.grassQuality);
       return original.call(instance);
   }

}
