package net.wenscHuix.mitemod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import net.wenscHuix.mitemod.mixin.MinecraftMixin;
import net.xiaoyu233.fml.classloading.Mod;
import net.xiaoyu233.fml.config.InjectionConfig;
import net.xiaoyu233.fml.config.InjectionConfig.Builder;
import org.spongepowered.asm.mixin.MixinEnvironment.Phase;
import org.spongepowered.asm.mixin.MixinEnvironment.Side;

@Mod({Side.CLIENT})
public class MITEShaderLoader implements ClientModInitializer, PreLaunchEntrypoint {
   public static final String VERSION = "v0.1.0";

   public int modVerNum() {
      return 10;
   }

   public void preInit() {
   }

   public void postInit() {
   }

   public String modId() {
      return "mite-shader-loader";
   }

   public String modVerStr() {
      return "v0.1.0";
   }

   @Override
   public void onInitializeClient() {

   }

   @Override
   public void onPreLaunch() {
      System.out.println("[MITE-Shader-Loader] Early riser registering chat formatting");
   }
}
