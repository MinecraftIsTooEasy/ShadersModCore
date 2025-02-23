package shadersmodcore.mixin.client.render;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.*;
import shadersmodcore.api.RenderGlobalAccessor;
import shadersmodcore.config.Config;
import shadersmodcore.client.shader.Shaders;
import shadersmodcore.client.dynamicLight.DynamicLights;
import shadersmodcore.config.ShaderConfig;
import net.xiaoyu233.fml.util.ReflectHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import shadersmodcore.util.Utils;

@Mixin(RenderGlobal.class)
public abstract class RenderGlobalMixin implements IWorldAccess, RenderGlobalAccessor {
   @Shadow private WorldClient theWorld;

   @Inject(method = "drawSelectionBox", at = @At("HEAD"), cancellable = true)
   private void enableConfigDrawSelectionBox(EntityPlayer par1EntityPlayer, RaycastCollision rc, int par3, float par4, CallbackInfo ci) {
      if (!Config.drawSelectionBox) {
         ci.cancel();
      }
   }

   @Inject(method = "drawSelectionBox", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glDisable(I)V", ordinal = 0))
   private void disableTexture2D(EntityPlayer par1EntityPlayer, RaycastCollision rc, int par3, float par4, CallbackInfo ci) {
      Shaders.disableTexture2D();
   }

   @Inject(method = "drawSelectionBox", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glDisable(I)V", ordinal = 1))
   private void enableTexture2D(EntityPlayer par1EntityPlayer, RaycastCollision rc, int par3, float par4, CallbackInfo ci) {
      Shaders.enableTexture2D();
   }

   @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/Profiler;startSection(Ljava/lang/String;)V", ordinal = 1, shift = Shift.AFTER),
      method = {"updateRenderers"}
   )
   private void injectUpdateRenderers(EntityLivingBase par1EntityLivingBase, boolean par2, CallbackInfoReturnable<Boolean> cir) {
      if (ShaderConfig.isDynamicLights()) {
         DynamicLights.update(ReflectHelper.dyCast(this));
      }

   }

   @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/GameSettings;getRenderDistance()I", shift = Shift.AFTER),
      method = {"loadRenderers"}
   )
   private void injectLoadRenderers(CallbackInfo callbackInfo) {
      if (ShaderConfig.isDynamicLights()) {
         DynamicLights.clear();
      }

   }

   @Inject(method = "onEntityCreate", at = @At("HEAD"))
   public void onEntityCreate(Entity par1Entity, CallbackInfo info) {
      if (ShaderConfig.isDynamicLights()) {
         DynamicLights.entityAdded(par1Entity, ReflectHelper.dyCast(this));
      }

   }

   @Inject(method = "onEntityDestroy", at = @At("HEAD"))
   public void onEntityDestroy(Entity par1Entity, CallbackInfo info) {
      if (ShaderConfig.isDynamicLights()) {
         DynamicLights.entityRemoved(par1Entity, ReflectHelper.dyCast(this));
      }

   }

   @Inject(method = "setWorldAndLoadRenderers", at = @At(value = "INVOKE",
           target = "Lnet/minecraft/RenderBlocks;<init>(Lnet/minecraft/IBlockAccess;)V", shift = Shift.BEFORE))
   public void setWorldAndLoadRenderers(WorldClient par1WorldClient, CallbackInfo info) {
      if (ShaderConfig.isDynamicLights()) {
         DynamicLights.clear();
      }
   }

   @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/Profiler;endStartSection(Ljava/lang/String;)V",
           ordinal = 1, shift = Shift.AFTER), method = {"renderEntities"}
   )
   private void injectRenderEntities0(CallbackInfo callbackInfo) {
      Shaders.beginEntities();
   }

   @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/Profiler;endStartSection(Ljava/lang/String;)V",
           ordinal = 2, shift = Shift.AFTER),
      method = {"renderEntities"}
   )
   private void injectRenderEntities1(CallbackInfo callbackInfo) {
      Shaders.endEntities();
      Shaders.beginTileEntities();
   }

   // ???
   @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/EntityRenderer;disableLightmap(D)V"),
      method = {"renderEntities"}
   )
   private void injectRenderEntities2(CallbackInfo callbackInfo) {
      Shaders.endTileEntities();
   }

   @Inject(at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glDisable(I)V",
           ordinal = 0, shift = Shift.AFTER),
      method = {"sortAndRender"}
   )
   private void injectSortAndRender0(EntityLivingBase par1EntityLivingBase, int par2, double par3, CallbackInfoReturnable<Integer> cir) {
      Shaders.disableTexture2D();
   }

   @Inject(at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glDisable(I)V",
           ordinal = 3, shift = Shift.AFTER),
      method = {"sortAndRender"}
   )
   private void injectSortAndRender1(EntityLivingBase par1EntityLivingBase, int par2, double par3, CallbackInfoReturnable<Integer> cir) {
      Shaders.disableFog();
   }

   @Inject(at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glEnable(I)V",
           ordinal = 0, shift = Shift.AFTER),
      method = {"sortAndRender"}
   )
   private void injectSortAndRender2(EntityLivingBase par1EntityLivingBase, int par2, double par3, CallbackInfoReturnable<Integer> cir) {
      Shaders.enableTexture2D();
   }

   @Inject(at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glEnable(I)V",
           ordinal = 2, shift = Shift.AFTER),
      method = {"sortAndRender"}
   )
   private void injectSortAndRender3(EntityLivingBase par1EntityLivingBase, int par2, double par3, CallbackInfoReturnable<Integer> cir) {
      Shaders.enableFog();
   }

   @Inject(at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glDisable(I)V", ordinal = 1, shift = Shift.AFTER),
      method = {"renderSky"}
   )
   private void injectRenderSky0(CallbackInfo callbackInfo) {
      Shaders.disableFog();
   }

   @Inject(at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glEnable(I)V", ordinal = 1, shift = Shift.AFTER),
      method = {"renderSky"}
   )
   private void injectRenderSky1(CallbackInfo callbackInfo) {
      Shaders.enableTexture2D();
   }

   @Inject(at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glDisable(I)V", ordinal = 3, shift = Shift.AFTER),
      method = {"renderSky"}
   )
   private void injectRenderSky2(CallbackInfo callbackInfo) {
      Shaders.disableTexture2D();
   }

   @Inject(at = @At(value = "FIELD",
           target = "Lnet/minecraft/Vec3;xCoord:D"),
           method = {"renderSky"}
   )
   private void injectRenderSky3(float par1, CallbackInfo ci, @Local(ordinal = 0) Vec3 var2) {
      Shaders.setSkyColor(var2);
   }

   @Inject(at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glEnable(I)V", ordinal = 3, shift = Shift.AFTER),
      method = {"renderSky"}
   )
   private void injectRenderSky4(CallbackInfo callbackInfo) {
      Shaders.enableFog();
   }

   @Inject(at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glColor3f(FFF)V",
           ordinal = 1, shift = Shift.AFTER),
      method = {"renderSky"}
   )
   private void injectRenderSky5(CallbackInfo callbackInfo) {
      Shaders.preSkyList();
   }

   @Inject(at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glDisable(I)V", ordinal = 4, shift = Shift.AFTER),
      method = {"renderSky"}
   )
   private void injectRenderSky6(CallbackInfo callbackInfo) {
      Shaders.disableFog();
   }

   @Inject(at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glDisable(I)V", ordinal = 6, shift = Shift.AFTER),
      method = {"renderSky"}
   )
   private void injectRenderSky7(CallbackInfo callbackInfo) {
      Shaders.disableTexture2D();
   }

   @Inject(at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glEnable(I)V", ordinal = 5, shift = Shift.AFTER),
      method = {"renderSky"}
   )
   private void injectRenderSky8(CallbackInfo callbackInfo) {
      Shaders.enableTexture2D();
   }

   @Inject(at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glRotatef(FFFF)V", ordinal = 8, shift = Shift.AFTER),
      method = {"renderSky"}
   )
   private void injectRenderSky9(CallbackInfo callbackInfo) {
      Shaders.preCelestialRotate();
   }

   @Inject(at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glRotatef(FFFF)V", ordinal = 9, shift = Shift.AFTER),
      method = {"renderSky"}
   )
   private void injectRenderSky10(CallbackInfo callbackInfo) {
      Shaders.postCelestialRotate();
   }

   @Inject(at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glDisable(I)V", ordinal = 7, shift = Shift.AFTER),
      method = {"renderSky"}
   )
   private void injectRenderSky11(CallbackInfo callbackInfo) {
      Shaders.disableTexture2D();
   }

   @Inject(at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glEnable(I)V", ordinal = 7, shift = Shift.AFTER),
      method = {"renderSky"}
   )
   private void injectRenderSky12(CallbackInfo callbackInfo) {
      Shaders.enableFog();
   }

   @Inject(at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glDisable(I)V", ordinal = 9, shift = Shift.AFTER),
      method = {"renderSky"}
   )
   private void injectRenderSky13(CallbackInfo callbackInfo) {
      Shaders.disableTexture2D();
   }

   @Inject(at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glEnable(I)V", ordinal = 8, shift = Shift.AFTER),
      method = {"renderSky"}
   )
   private void injectRenderSky14(CallbackInfo callbackInfo) {
      Shaders.enableTexture2D();
   }

   @Inject(at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glDisable(I)V", ordinal = 0, shift = Shift.AFTER),
      method = {"compileCloudsFancy"}
   )
   private void injectCompileCloudsFancy0(CallbackInfo callbackInfo) {
      Shaders.disableFog();
   }

   @Inject(at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glDisable(I)V", ordinal = 2, shift = Shift.AFTER),
      method = {"compileCloudsFancy"}
   )
   private void injectCompileCloudsFancy1(CallbackInfo callbackInfo) {
      Shaders.disableTexture2D();
   }

   @Inject(at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glEnable(I)V", ordinal = 1, shift = Shift.AFTER),
      method = {"compileCloudsFancy"}
   )
   private void injectCompileCloudsFancy2(CallbackInfo callbackInfo) {
      Shaders.enableFog();
   }

   @Inject(at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glEnable(I)V", ordinal = 3, shift = Shift.AFTER),
      method = {"compileCloudsFancy"}
   )
   private void injectCompileCloudsFancy3(CallbackInfo callbackInfo) {
      Shaders.enableTexture2D();
   }

   public WorldClient getClientWorld() {
      return this.theWorld;
   }

   @Inject(at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glBlendFunc(II)V", shift = Shift.AFTER),
      method = {"drawBlockDamageTexture"}
   )
   private void injectDrawBlockDamageTexture0(CallbackInfo callbackInfo) {
      if (Shaders.isShadersLoad()) {
         Shaders.beginBlockDestroyProgress();
      }
   }

   @Inject(at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glPopMatrix()V", shift = Shift.AFTER),
      method = {"drawBlockDamageTexture"}
   )
   private void injectDrawBlockDamageTexture1(CallbackInfo callbackInfo) {
      if (Shaders.isShadersLoad()) {
         Shaders.endBlockDestroyProgress();
      }
   }

   //Hitbox (F3+B)
   @Inject(method = "drawOutlinedBoundingBox",
           at = @At(value = "HEAD"))
   private void drawOutlinedBoundingBox(AxisAlignedBB par1AxisAlignedBB, CallbackInfo ci) {
      Utils.Fix();
   }

   @Shadow public void markBlockForUpdate(int i, int i1, int i2) {}
   @Shadow public void markBlockForRenderUpdate(int i, int i1, int i2) {}
   @Shadow public void markBlockRangeForRenderUpdate(int i, int i1, int i2, int i3, int i4, int i5) {}
   @Shadow public void playSound(String s, double v, double v1, double v2, float v3, float v4) {}
   @Shadow public void playLongDistanceSound(String s, double v, double v1, double v2, float v3, float v4) {}
   @Shadow public void playSoundToNearExcept(EntityPlayer entityPlayer, String s, double v, double v1, double v2, float v3, float v4) {}
   @Shadow public void spawnParticle(EnumParticle enumParticle, double v, double v1, double v2, double v3, double v4, double v5) {}
   @Shadow public void spawnParticleEx(EnumParticle enumParticle, int i, int i1, double v, double v1, double v2, double v3, double v4, double v5) {}
   @Shadow public void playRecord(String s, int i, int i1, int i2) {}
   @Shadow public void broadcastSound(int i, int i1, int i2, int i3, int i4) {}
   @Shadow public void playAuxSFX(EntityPlayer entityPlayer, int i, int i1, int i2, int i3, int i4) {}
   @Shadow public void destroyBlockPartially(int i, int i1, int i2, int i3, int i4) {}
}
