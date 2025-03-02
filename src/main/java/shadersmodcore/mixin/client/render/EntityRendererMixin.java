package shadersmodcore.mixin.client.render;

import java.lang.reflect.InvocationTargetException;
import java.nio.FloatBuffer;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.*;
import shadersmodcore.client.shader.Shaders;
import shadersmodcore.client.shader.ShadersRender;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import shadersmodcore.config.OptimizeConfig;
import shadersmodcore.util.Utils;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin {
   @Shadow private Minecraft mc;
   @Shadow protected abstract void renderHand(float par1, int par2);

   @Shadow public ItemRenderer itemRenderer;
   @Unique private int var13;

   @Inject(at = {@At("RETURN")}, method = {"disableLightmap"})
   private void injectDisableLightmap(CallbackInfo callbackInfo) {
      Shaders.disableLightmap();
   }

   @Inject(at = {@At("RETURN")}, method = {"enableLightmap"})
   private void injectEnableLightmap(CallbackInfo callbackInfo) {
      Shaders.enableLightmap();
   }

   @Inject(at = {@At("HEAD")}, method = {"setFogColorBuffer"})
   private void injectSetFogColorBuffer(float par1, float par2, float par3, float par4, CallbackInfoReturnable<FloatBuffer> cir) {
      Shaders.setFogColor(par1, par2, par3);
   }


   @Redirect(method = "setupFog", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glFogi(II)V"))
   private void sglFogi(int pname, int param) {
      Shaders.sglFogi(pname, param);
   }

   @Redirect(method = "renderCloudsCheck", at = @At(value = "INVOKE", target = "Lnet/minecraft/GameSettings;shouldRenderClouds()Z"))
   public boolean shouldRenderClouds(GameSettings instance) {
      return Shaders.shouldRenderClouds(instance);
   }

   @Inject(method = "renderCloudsCheck", at = @At(value = "INVOKE", target = "Lnet/minecraft/RenderGlobal;renderClouds(F)V"))
   public void beginClouds(RenderGlobal par1RenderGlobal, float par2, CallbackInfo ci) {
      Shaders.beginClouds();
   }

   @Inject(method = "renderCloudsCheck", at = @At(value = "INVOKE", target = "Lnet/minecraft/RenderGlobal;renderClouds(F)V", shift = At.Shift.AFTER))
   public void endClouds(RenderGlobal par1RenderGlobal, float par2, CallbackInfo ci) {
      Shaders.endClouds();
   }

   // A
   @Inject(method = "renderWorld", at = @At("HEAD"))
   public void renderWorldBeginRender(float par1, long par2, CallbackInfo info) throws NoSuchFieldException, InvocationTargetException, IllegalAccessException, NoSuchMethodException {
      Shaders.beginRender(this.mc, par1, par2);
   }

   // A
   @Redirect(method = "renderWorld", at = @At(value = "INVOKE" ,target = "Lorg/lwjgl/opengl/GL11;glViewport(IIII)V"))
   public void renderWorldSetViewport(int x, int y, int width, int height) {
      Shaders.setViewport(x, y, width, height);
      var13 = 1;
   }

   // A
   @Inject(method = "renderWorld", at = @At(value = "INVOKE" ,target = "Lorg/lwjgl/opengl/GL11;glClear(I)V", shift = At.Shift.AFTER, ordinal = 0))
   public void renderWorldClearRenderBuffer(float par1, long par2, CallbackInfo info) {
      Shaders.clearRenderBuffer();
   }

   // A
   @Inject(method = "renderWorld", at = @At(value = "INVOKE" ,target = "Lnet/minecraft/EntityRenderer;setupCameraTransform(FIZ)V", shift = At.Shift.AFTER))
   public void renderWorldSetCamera(float par1, long par2, CallbackInfo info) {
      Shaders.setCamera(par1);
   }

   // A
   @Redirect(method = "renderWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/GameSettings;getRenderDistance()I"))
   public int isShadowPass(GameSettings instance) {
      return !Shaders.isShadowPass? 1: 2;
   }

   // A
   @Inject(method = "renderWorld", at = @At(value = "INVOKE" ,target = "Lnet/minecraft/EntityRenderer;setupFog(IF)V", shift = At.Shift.AFTER, ordinal = 0))
   public void renderWorldBeginSky(float par1, long par2, CallbackInfo info) {
      Shaders.beginSky();
   }

   // A
   @Inject(method = "renderWorld", at = @At(value = "INVOKE" ,target = "Lnet/minecraft/RenderGlobal;renderSky(F)V", shift = At.Shift.AFTER))
   public void renderWorldEndSky(float par1, long par2, CallbackInfo info) {
      Shaders.endSky();
   }

   // nothing
//   @Redirect(method = "renderWorld", at = @At(value = "INVOKE" ,target = "Lnet/minecraft/Frustrum;setPosition(DDD)V"))
//   public void renderWorldSetFrustrumPosition(Frustrum frustrum, double var7, double var9, double var11) {
//      ShadersRender.setFrustrumPosition(frustrum, var7, var9, var11);
//   }

   // A
   @Redirect(method = "renderWorld", at = @At(value = "INVOKE" ,target = "Lnet/minecraft/RenderGlobal;clipRenderersByFrustum(Lnet/minecraft/ICamera;F)V"))
   public void renderWorldClipRenderersByFrustrum(RenderGlobal renderGlobal, ICamera iCamera, float par1ICamera) {
      ShadersRender.clipRenderersByFrustrum(renderGlobal, iCamera, par1ICamera);
   }

   // A
   @Inject(method = "renderWorld", at = @At(value = "INVOKE" ,target = "Lnet/minecraft/Profiler;endStartSection(Ljava/lang/String;)V", ordinal = 7))
   public void renderWorldBeginUpdateChunks(float par1, long par2, CallbackInfo info) {
      Shaders.beginUpdateChunks();
      var13 = 0;
   }

   // B
   @Inject(method = "renderWorld", at = @At(value = "FIELD" ,target = "Lnet/minecraft/EntityLivingBase;posY:D", ordinal = 1))
   public void renderWorldEndUpdateChunks(float par1, long par2, CallbackInfo ci) {
      if (var13 == 0) {
         Shaders.endUpdateChunks();
      }
   }

   // A
   @Inject(method = "renderWorld", at = @At(value = "INVOKE" ,target = "Lnet/minecraft/RenderGlobal;sortAndRender(Lnet/minecraft/EntityLivingBase;ID)I", ordinal = 0))
   public void renderWorldBeginTerrain(float par1, long par2, CallbackInfo ci) {
      Shaders.beginTerrain();
   }

   // B
   @Inject(method = "renderWorld", at = @At(value = "INVOKE" ,target = "Lnet/minecraft/RenderGlobal;sortAndRender(Lnet/minecraft/EntityLivingBase;ID)I", ordinal = 0, shift = At.Shift.AFTER))
   public void renderWorldEndTerrain(float par1, long par2, CallbackInfo ci) {
      Shaders.endTerrain();
   }

   // A
   @Inject(method = "renderWorld", at = @At(value = "INVOKE" ,target = "Lnet/minecraft/EffectRenderer;renderLitParticles(Lnet/minecraft/Entity;F)V"))
   public void renderWorldBeginLitParticles(float par1, long par2, CallbackInfo ci) {
      Shaders.beginLitParticles();
   }

   // A
   @Inject(method = "renderWorld", at = @At(value = "INVOKE" ,target = "Lnet/minecraft/EntityRenderer;setupFog(IF)V", ordinal = 3, shift = At.Shift.AFTER))
   public void renderWorldBeginParticles(float par1, long par2, CallbackInfo ci) {
      Shaders.beginParticles();
   }

   // A
   @Inject(method = "renderWorld", at = @At(value = "INVOKE" ,target = "Lnet/minecraft/EffectRenderer;renderParticles(Lnet/minecraft/Entity;F)V", shift = At.Shift.AFTER))
   public void renderWorldEndParticles(float par1, long par2, CallbackInfo ci) {
      Shaders.endParticles();
   }

   // B
   @Redirect(method = "renderWorld", at = @At(value = "INVOKE" ,target = "Lnet/minecraft/EntityRenderer;setupFog(IF)V", ordinal = 4))
   public void renderEndHand(EntityRenderer instance, int var6, float delta) {
      Shaders.beginHand();
      this.renderHand(delta, var13);
      Shaders.endHand();
      Shaders.preWater();
   }

   // A
   @Inject(method = "renderWorld", at = @At(value = "INVOKE" ,target = "Lnet/minecraft/RenderGlobal;sortAndRender(Lnet/minecraft/EntityLivingBase;ID)I", ordinal = 1))
   public void renderWorldBeginWaterFancy(float par1, long par2, CallbackInfo ci) {
      Shaders.beginWaterFancy();
   }

   // A
   @Inject(method = "renderWorld", at = @At(value = "INVOKE" ,target = "Lnet/minecraft/RenderGlobal;renderAllRenderLists(ID)V"))
   public void renderWorldMidWaterFancy(float par1, long par2, CallbackInfo ci) {
      Shaders.midWaterFancy();
   }

   // A
   @Inject(method = "renderWorld", at = @At(value = "INVOKE" ,target = "Lorg/lwjgl/opengl/GL11;glShadeModel(I)V", ordinal = 3))
   public void renderWorldEndWater0(float par1, long par2, CallbackInfo ci) {
      Shaders.endWater();
   }

   // A
   @Inject(method = "renderWorld", at = @At(value = "INVOKE" ,target = "Lnet/minecraft/RenderGlobal;sortAndRender(Lnet/minecraft/EntityLivingBase;ID)I", ordinal = 2))
   public void renderWorldBeginWater(float par1, long par2, CallbackInfo ci) {
      Shaders.beginWater();
   }

   // A
   @Inject(method = "renderWorld", at = @At(value = "INVOKE" ,target = "Lnet/minecraft/RenderGlobal;sortAndRender(Lnet/minecraft/EntityLivingBase;ID)I", ordinal = 2, shift = At.Shift.AFTER))
   public void renderWorldEndWater1(float par1, long par2, CallbackInfo ci) {
      Shaders.endWater();
   }

   // A
   @Inject(method = "renderWorld", at = @At(value = "INVOKE" ,target = "Lorg/lwjgl/opengl/GL11;glDisable(I)V", ordinal = 3, shift = At.Shift.AFTER), cancellable = true)
   public void renderWorldIsShadowPass(float par1, long par2, CallbackInfo ci) {
      if (Shaders.isShadowPass) {
         ci.cancel();
      }

      Shaders.readCenterDepth();
   }

   // A
   @Inject(method = "renderWorld", at = @At(value = "INVOKE" ,target = "Lnet/minecraft/EntityRenderer;renderRainSnow(F)V"))
   public void renderWorldBeginWeather(float par1, long par2, CallbackInfo ci) {
      Shaders.beginWeather();
   }

   // A
   @Inject(method = "renderWorld", at = @At(value = "INVOKE" ,target = "Lnet/minecraft/EntityRenderer;renderRainSnow(F)V", shift = At.Shift.AFTER))
   public void renderWorldEndWeather(float par1, long par2, CallbackInfo ci) {
      Shaders.endWeather();
   }

   // A
   @Inject(method = "renderWorld", at = @At(value = "INVOKE" ,target = "Lorg/lwjgl/opengl/GL11;glDisable(I)V", ordinal = 6, shift = At.Shift.AFTER))
   public void renderWorldDisableFog(float par1, long par2, CallbackInfo ci) {
      Shaders.disableFog();
   }

   // A
   @Inject(method = "renderWorld", at = @At(value = "INVOKE" ,target = "Lnet/minecraft/Profiler;endStartSection(Ljava/lang/String;)V", ordinal = 19, shift = At.Shift.AFTER))
   public void renderWorldRenderCompositeFinal(float par1, long par2, CallbackInfo ci) {
      Shaders.renderCompositeFinal();
   }

   // A
   @Inject(method = "renderWorld", at = @At(value = "INVOKE" ,target = "Lnet/minecraft/EntityRenderer;renderHand(FI)V"))
   public void renderWorldBeginFPOverlay(float par1, long par2, CallbackInfo ci) {
      Shaders.beginFPOverlay();
   }

   // A
   @Inject(method = "renderWorld", at = @At(value = "INVOKE" ,target = "Lnet/minecraft/EntityRenderer;renderHand(FI)V", shift = At.Shift.AFTER))
   public void renderWorldEndFPOverlay(float par1, long par2, CallbackInfo ci) {
      Shaders.endFPOverlay();
   }

   // A
   @Inject(method = "renderWorld", at = @At(value = "FIELD" ,target = "Lnet/minecraft/GameSettings;anaglyph:Z", ordinal = 2))
   public void renderWorldEndRender(float par1, long par2, CallbackInfo ci) {
      Shaders.endRender();
   }

   @Inject(method = "renderHand", at = @At(value = "INVOKE", target = "Lorg/lwjgl/util/glu/Project;gluPerspective(FFFF)V"))
   private void applyHandDepth(float par1, int par2, CallbackInfo ci) {
      Shaders.applyHandDepth();
   }

   @WrapOperation(method = "renderWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/EntityRenderer;renderRainSnow(F)V"))
   private void renderWeather(EntityRenderer instance, float var6, Operation<Void> original) {
      if (OptimizeConfig.renderRainSnow)
         original.call(instance, var6);
   }

   @WrapOperation(method = "updateRenderer", at = @At(value = "INVOKE", target = "Lnet/minecraft/EntityRenderer;addRainParticles()V"))
   private void renderWeatherParticles(EntityRenderer instance, Operation<Void> original) {
      if (OptimizeConfig.renderRainSnow)
         original.call(instance);
   }

   @WrapOperation(method = "renderWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/GameSettings;isFancyGraphicsEnabled()Z"))
   private boolean waterQuality(GameSettings instance, Operation<Boolean> original) {
      if (OptimizeConfig.waterQuality != 0)
         return Utils.convertIntToBoolean(OptimizeConfig.waterQuality);
      return original.call(instance);
   }

   @WrapOperation(method = "addRainParticles", at = @At(value = "INVOKE", target = "Lnet/minecraft/GameSettings;isFancyGraphicsEnabled()Z"))
   private boolean rainQuality(GameSettings instance, Operation<Boolean> original) {
      if (OptimizeConfig.rainQuality != 0)
         return Utils.convertIntToBoolean(OptimizeConfig.rainQuality);
      return original.call(instance);
   }

   @WrapOperation(method = "renderRainSnow", at = @At(value = "INVOKE", target = "Lnet/minecraft/GameSettings;isFancyGraphicsEnabled()Z"))
   private boolean rainQuality_1(GameSettings instance, Operation<Boolean> original) {
      if (OptimizeConfig.rainQuality != 0)
         return Utils.convertIntToBoolean(OptimizeConfig.rainQuality);
      return original.call(instance);
   }
}
