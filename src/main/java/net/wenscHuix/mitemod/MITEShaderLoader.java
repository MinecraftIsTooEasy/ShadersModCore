package net.wenscHuix.mitemod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import net.wenscHuix.mitemod.mixin.MinecraftMixin;
import net.xiaoyu233.fml.classloading.Mod;
import net.xiaoyu233.fml.config.InjectionConfig;
import net.xiaoyu233.fml.config.InjectionConfig.Builder;
import org.spongepowered.asm.mixin.MixinEnvironment.Phase;
import org.spongepowered.asm.mixin.MixinEnvironment.Side;

public class MITEShaderLoader implements ClientModInitializer, PreLaunchEntrypoint {

   @Override
   public void onInitializeClient() {

   }

   @Override
   public void onPreLaunch() {
      System.out.println("[MITE-Shader-Loader] Early riser registering chat formatting");
   }
}
