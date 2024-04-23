package net.wenscHuix.mitemod.mixin.render;

import java.util.List;

import net.minecraft.*;
import net.wenscHuix.mitemod.optimize.gui.Config;
import net.wenscHuix.mitemod.shader.client.Shaders;
import net.wenscHuix.mitemod.shader.client.dynamicLight.DynamicLights;
import net.wenscHuix.mitemod.shader.client.dynamicLight.config.ShaderConfig;
import net.xiaoyu233.fml.util.ReflectHelper;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin({RenderGlobal.class})
public abstract class RenderGlobalMixin implements IWorldAccess {

   @Shadow double prevSortZ;
   @Shadow double prevSortY;
   @Shadow double prevSortX;

   @Shadow protected abstract void drawOutlinedBoundingBox(AxisAlignedBB par1AxisAlignedBB);

   @Shadow private WorldClient theWorld;

   /**
    * @author
    * @reason
    */
   @Overwrite
   public void drawSelectionBox(EntityPlayer par1EntityPlayer, RaycastCollision rc, int par3, float par4) {
      if (par3 == 0 && rc.isBlock() && Config.drawSelectionBox) {
         GL11.glEnable(3042);
         GL11.glBlendFunc(770, 771);
         GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.4F);
         GL11.glLineWidth(2.0F);
         GL11.glDisable(3553);
         Shaders.disableTexture2D();
         GL11.glDepthMask(false);
         float var5 = 0.002F;
         Block block = rc.getBlockHit();
         double var7 = par1EntityPlayer.lastTickPosX + (par1EntityPlayer.posX - par1EntityPlayer.lastTickPosX) * (double)par4;
         double var9 = par1EntityPlayer.lastTickPosY + (par1EntityPlayer.posY - par1EntityPlayer.lastTickPosY) * (double)par4;
         double var11 = par1EntityPlayer.lastTickPosZ + (par1EntityPlayer.posZ - par1EntityPlayer.lastTickPosZ) * (double)par4;
         this.drawOutlinedBoundingBox(block.getSelectedBoundingBoxFromPool(this.theWorld, rc.block_hit_x, rc.block_hit_y, rc.block_hit_z).expand((double)var5, (double)var5, (double)var5).getOffsetBoundingBox(-var7, -var9, -var11));
         GL11.glDepthMask(true);
         GL11.glEnable(3553);
         Shaders.enableTexture2D();
         GL11.glDisable(3042);
      }

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

   @Inject(method = "onEntityCreate", at = @At("HEAD"))
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

   @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/Profiler;endStartSection(Ljava/lang/String;)V",
           shift = Shift.AFTER),
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

   @Inject(locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE_ASSIGN",
   target = "Lnet/minecraft/WorldClient;getSkyColor(Lnet/minecraft/Entity;F)Lnet/minecraft/Vec3;"),
      method = {"renderSky"}
   )
   private void injectRenderSky3(float par1, CallbackInfo ci, Vec3 var2) {
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

   @Inject(
      at = {@At(
   value = "INVOKE",
   target = "Lorg/lwjgl/opengl/GL11;glDisable(I)V",
   ordinal = 4,
   shift = Shift.AFTER
)},
      method = {"a(F)V"}
   )
   private void injectRenderSky6(CallbackInfo callbackInfo) {
      Shaders.disableFog();
   }

   @Inject(
      at = {@At(
   value = "INVOKE",
   target = "Lorg/lwjgl/opengl/GL11;glDisable(I)V",
   ordinal = 6,
   shift = Shift.AFTER
)},
      method = {"a(F)V"}
   )
   private void injectRenderSky7(CallbackInfo callbackInfo) {
      Shaders.disableTexture2D();
   }

   @Inject(
      at = {@At(
   value = "INVOKE",
   target = "Lorg/lwjgl/opengl/GL11;glEnable(I)V",
   ordinal = 5,
   shift = Shift.AFTER
)},
      method = {"a(F)V"}
   )
   private void injectRenderSky8(CallbackInfo callbackInfo) {
      Shaders.enableTexture2D();
   }

   @Inject(
      at = {@At(
   value = "INVOKE",
   target = "Lorg/lwjgl/opengl/GL11;glRotatef(FFFF)V",
   ordinal = 8,
   shift = Shift.AFTER
)},
      method = {"a(F)V"}
   )
   private void injectRenderSky9(CallbackInfo callbackInfo) {
      Shaders.preCelestialRotate();
   }

   @Inject(
      at = {@At(
   value = "INVOKE",
   target = "Lorg/lwjgl/opengl/GL11;glRotatef(FFFF)V",
   ordinal = 9,
   shift = Shift.AFTER
)},
      method = {"a(F)V"}
   )
   private void injectRenderSky10(CallbackInfo callbackInfo) {
      Shaders.postCelestialRotate();
   }

   @Inject(
      at = {@At(
   value = "INVOKE",
   target = "Lorg/lwjgl/opengl/GL11;glDisable(I)V",
   ordinal = 7,
   shift = Shift.AFTER
)},
      method = {"a(F)V"}
   )
   private void injectRenderSky11(CallbackInfo callbackInfo) {
      Shaders.disableTexture2D();
   }

   @Inject(
      at = {@At(
   value = "INVOKE",
   target = "Lorg/lwjgl/opengl/GL11;glEnable(I)V",
   ordinal = 7,
   shift = Shift.AFTER
)},
      method = {"a(F)V"}
   )
   private void injectRenderSky12(CallbackInfo callbackInfo) {
      Shaders.enableFog();
   }

   @Inject(
      at = {@At(
   value = "INVOKE",
   target = "Lorg/lwjgl/opengl/GL11;glDisable(I)V",
   ordinal = 9,
   shift = Shift.AFTER
)},
      method = {"a(F)V"}
   )
   private void injectRenderSky13(CallbackInfo callbackInfo) {
      Shaders.disableTexture2D();
   }

   @Inject(
      at = {@At(
   value = "INVOKE",
   target = "Lorg/lwjgl/opengl/GL11;glEnable(I)V",
   ordinal = 8,
   shift = Shift.AFTER
)},
      method = {"a(F)V"}
   )
   private void injectRenderSky14(CallbackInfo callbackInfo) {
      Shaders.enableTexture2D();
   }

   @Inject(
      at = {@At(
   value = "INVOKE",
   target = "Lorg/lwjgl/opengl/GL11;glDisable(I)V",
   ordinal = 0,
   shift = Shift.AFTER
)},
      method = {"compileCloudsFancy"}
   )
   private void injectCompileCloudsFancy0(CallbackInfo callbackInfo) {
      Shaders.disableFog();
   }

   @Inject(
      at = {@At(
   value = "INVOKE",
   target = "Lorg/lwjgl/opengl/GL11;glDisable(I)V",
   ordinal = 2,
   shift = Shift.AFTER
)},
      method = {"compileCloudsFancy"}
   )
   private void injectCompileCloudsFancy1(CallbackInfo callbackInfo) {
      Shaders.disableTexture2D();
   }

   @Inject(
      at = {@At(
   value = "INVOKE",
   target = "Lorg/lwjgl/opengl/GL11;glEnable(I)V",
   ordinal = 1,
   shift = Shift.AFTER
)},
      method = {"compileCloudsFancy"}
   )
   private void injectCompileCloudsFancy2(CallbackInfo callbackInfo) {
      Shaders.enableFog();
   }

   @Inject(
      at = {@At(
   value = "INVOKE",
   target = "Lorg/lwjgl/opengl/GL11;glEnable(I)V",
   ordinal = 3,
   shift = Shift.AFTER
)},
      method = {"compileCloudsFancy"}
   )
   private void injectCompileCloudsFancy3(CallbackInfo callbackInfo) {
      Shaders.enableTexture2D();
   }

   @Overwrite
   public void b(float par1) {
      if (this.t.f.provider.isSurfaceWorld()) {
         boolean force_fancy_clouds = true;
         if (!force_fancy_clouds && !this.t.u.isFancyGraphicsEnabled()) {
            GL11.glEnable(2884);
            float var2 = (float)(this.t.i.lastTickPosY + (this.t.i.posY - this.t.i.lastTickPosY) * (double)par1);
            byte var3 = 32;
            int var4 = 256 / var3;
            bfq var5 = bfq.a;
            this.l.a(i);
            GL11.glEnable(3042);
            GL11.glBlendFunc(770, 771);
            Vec3D var6 = this.k.e(par1);
            float var7 = (float)var6.xCoord;
            float var8 = (float)var6.yCoord;
            float var9 = (float)var6.zCoord;
            float var10;
            if (this.t.u.g) {
               var10 = (var7 * 30.0F + var8 * 59.0F + var9 * 11.0F) / 100.0F;
               float var11 = (var7 * 30.0F + var8 * 70.0F) / 100.0F;
               float var12 = (var7 * 30.0F + var9 * 70.0F) / 100.0F;
               var7 = var10;
               var8 = var11;
               var9 = var12;
            }

            var10 = 4.8828125E-4F;
            double var24 = (double)((float)this.x + par1);
            double var13 = this.t.i.prevPosX + (this.t.i.posX - this.t.i.prevPosX) * (double)par1 + var24 * 0.029999999329447746D;
            double var15 = this.t.i.prevPosZ + (this.t.i.posZ - this.t.i.prevPosZ) * (double)par1;
            int var17 = MathHelper.floor_double(var13 / 2048.0D);
            int var18 = MathHelper.floor_double(var15 / 2048.0D);
            var13 -= (double)(var17 * 2048);
            var15 -= (double)(var18 * 2048);
            float var19 = this.k.provider.f() - var2 + 0.33F;
            float var20 = (float)(var13 * (double)var10);
            float var21 = (float)(var15 * (double)var10);
            boolean player_can_see_cloud_bottoms = var19 > -0.0F;
            GL11.glCullFace(player_can_see_cloud_bottoms ? 1028 : 1029);
            var5.b();
            GL11.glColor4f(var7, var8, var9, 0.8F);
            var5.o = true;
            int[] rawBuffer = var5.h;
            int y0 = Float.floatToRawIntBits(var19);

            for(int var22 = -var3 * var4; var22 < var3 * var4; var22 += var3) {
               int u0 = Float.floatToRawIntBits((float)var22 * var10 + var20);
               int u1 = Float.floatToRawIntBits((float)(var22 + var3) * var10 + var20);
               int x0 = Float.floatToRawIntBits((float)var22);
               int x1 = Float.floatToRawIntBits((float)(var22 + var3));

               for(int var23 = -var3 * var4; var23 < var3 * var4; var23 += var3) {
                  if (RenderingScheme.current == 0) {
                     var5.a((double)(var22 + 0), (double)var19, (double)(var23 + var3), (double)((float)(var22 + 0) * var10 + var20), (double)((float)(var23 + var3) * var10 + var21));
                     var5.a((double)(var22 + var3), (double)var19, (double)(var23 + var3), (double)((float)(var22 + var3) * var10 + var20), (double)((float)(var23 + var3) * var10 + var21));
                     var5.a((double)(var22 + var3), (double)var19, (double)(var23 + 0), (double)((float)(var22 + var3) * var10 + var20), (double)((float)(var23 + 0) * var10 + var21));
                     var5.a((double)(var22 + 0), (double)var19, (double)(var23 + 0), (double)((float)(var22 + 0) * var10 + var20), (double)((float)(var23 + 0) * var10 + var21));
                  } else {
                     int v0 = Float.floatToRawIntBits((float)(var23 + var3) * var10 + var21);
                     int v2 = Float.floatToRawIntBits((float)var23 * var10 + var21);
                     int z0 = Float.floatToRawIntBits((float)(var23 + var3));
                     int z1 = Float.floatToRawIntBits((float)var23);
                     rawBuffer[var5.r + 3] = u0;
                     rawBuffer[var5.r + 11] = u1;
                     rawBuffer[var5.r + 19] = u1;
                     rawBuffer[var5.r + 27] = u0;
                     rawBuffer[var5.r + 4] = v0;
                     rawBuffer[var5.r + 12] = v0;
                     rawBuffer[var5.r + 20] = v2;
                     rawBuffer[var5.r + 28] = v2;
                     rawBuffer[var5.r + 0] = x0;
                     rawBuffer[var5.r + 8] = x1;
                     rawBuffer[var5.r + 16] = x1;
                     rawBuffer[var5.r + 24] = x0;
                     rawBuffer[var5.r + 1] = y0;
                     rawBuffer[var5.r + 9] = y0;
                     rawBuffer[var5.r + 17] = y0;
                     rawBuffer[var5.r + 25] = y0;
                     rawBuffer[var5.r + 2] = z0;
                     rawBuffer[var5.r + 10] = z0;
                     rawBuffer[var5.r + 18] = z1;
                     rawBuffer[var5.r + 26] = z1;
                     var5.r += 32;
                     var5.s += 4;
                     var5.i += 4;
                     if (var5.r >= 2097120) {
                        var5.a();
                        var5.z = true;
                     }
                  }
               }
            }

            var5.a();
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glDisable(3042);
            GL11.glCullFace(1029);
         } else if (this.t.u.g) {
            this.c(par1);
         } else {
            this.c(par1);
         }
      }

   }

   public WorldClient getClientWorld() {
      return this.cli;
   }

   @Inject(
      at = {@At(
   value = "INVOKE",
   target = "Lorg/lwjgl/opengl/GL11;glBlendFunc(II)V",
   shift = Shift.AFTER
)},
      method = {"a(Lnet/minecraft/bfq;Lnet/minecraft/EntityPlayer;F)V"}
   )
   private void injectDrawBlockDamageTexture0(CallbackInfo callbackInfo) {
      Shaders.beginBlockDestroyProgress();
   }

   @Inject(
      at = {@At(
   value = "INVOKE",
   target = "Lorg/lwjgl/opengl/GL11;glPopMatrix()V",
   shift = Shift.AFTER
)},
      method = {"a(Lnet/minecraft/bfq;Lnet/minecraft/EntityPlayer;F)V"}
   )
   private void injectDrawBlockDamageTexture1(CallbackInfo callbackInfo) {
      Shaders.endBlockDestroyProgress();
   }

   public List getWorldRenderersToUpdate() {
      return this.m;
   }

   @Shadow
   public void c(float par1) {
   }

   @Shadow
   public void a() {
   }

   @Shadow
   public void renderCloudsFancy_MITE(float par1) {
   }

   @Shadow
   public void markBlockForUpdate(int i, int i1, int i2) {
   }

   @Shadow
   public void markBlockForRenderUpdate(int i, int i1, int i2) {
   }

   @Shadow
   public void markBlockRangeForRenderUpdate(int i, int i1, int i2, int i3, int i4, int i5) {
   }

   @Shadow
   public void playSound(String s, double v, double v1, double v2, float v3, float v4) {
   }

   @Shadow
   public void playLongDistanceSound(String s, double v, double v1, double v2, float v3, float v4) {
   }

   @Shadow
   public void playSoundToNearExcept(EntityPlayer entityPlayer, String s, double v, double v1, double v2, float v3, float v4) {
   }

   @Shadow
   public void spawnParticle(EnumParticle enumParticle, double v, double v1, double v2, double v3, double v4, double v5) {
   }

   @Shadow
   public void spawnParticleEx(EnumParticle enumParticle, int i, int i1, double v, double v1, double v2, double v3, double v4, double v5) {
   }

   @Shadow
   public void playRecord(String s, int i, int i1, int i2) {
   }

   @Shadow
   public void broadcastSound(int i, int i1, int i2, int i3, int i4) {
   }

   @Shadow
   public void playAuxSFX(EntityPlayer entityPlayer, int i, int i1, int i2, int i3, int i4) {
   }

   @Shadow
   private void a(AxisAlignedBB par1AxisAlignedBB) {
   }

   @Shadow
   public void destroyBlockPartially(int i, int i1, int i2, int i3, int i4) {
   }
}
