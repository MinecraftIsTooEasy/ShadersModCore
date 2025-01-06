package shadersmodcore.mixin.client;

import net.minecraft.Minecraft;
import shadersmodcore.config.Config;
import shadersmodcore.client.shader.Shaders;
import net.xiaoyu233.fml.util.ReflectHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Minecraft.class})
public class MinecraftMixin {
   @Inject(at = {@At("RETURN")}, method = {"<init>"})
   private void injectEnableLightmap(CallbackInfo callbackInfo) {
      Shaders.startup(ReflectHelper.dyCast(this));
      Config.loadConfig();
   }
}
