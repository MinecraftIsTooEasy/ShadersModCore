package shadersmodcore.mixin.client.render;

import java.util.List;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.*;
import shadersmodcore.api.RenderGlobalAccessor;
import shadersmodcore.config.Config;
import shadersmodcore.client.shader.Shaders;
import shadersmodcore.client.dynamicLight.DynamicLights;
import shadersmodcore.config.ShaderConfig;
import net.xiaoyu233.fml.util.ReflectHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RenderGlobal.class)
public abstract class RenderGlobalMixin implements IWorldAccess, RenderGlobalAccessor {

   @Shadow private List worldRenderersToUpdate;

   @Shadow public abstract void renderCloudsFancy(float par1);

   @Shadow private int cloudTickCounter;
   @Shadow @Final private static ResourceLocation locationCloudsPng;
   @Shadow @Final private TextureManager renderEngine;
   @Shadow private Minecraft mc;
   @Shadow double prevSortZ;
   @Shadow double prevSortY;
   @Shadow double prevSortX;

   @Shadow protected abstract void drawOutlinedBoundingBox(AxisAlignedBB par1AxisAlignedBB);

   @Shadow private WorldClient theWorld;

//   /**
//    * @author
//    * @reason
//    */
//   @Overwrite
//   public void drawSelectionBox(EntityPlayer par1EntityPlayer, RaycastCollision rc, int par3, float par4) {
//      if (par3 == 0 && rc.isBlock() && Config.drawSelectionBox) {
//         GL11.glEnable(3042);
//         GL11.glBlendFunc(770, 771);
//         GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.4F);
//         GL11.glLineWidth(2.0F);
//         GL11.glDisable(3553);
//         Shaders.disableTexture2D();
//         GL11.glDepthMask(false);
//         float var5 = 0.002F;
//         Block block = rc.getBlockHit();
//         double var7 = par1EntityPlayer.lastTickPosX + (par1EntityPlayer.posX - par1EntityPlayer.lastTickPosX) * (double)par4;
//         double var9 = par1EntityPlayer.lastTickPosY + (par1EntityPlayer.posY - par1EntityPlayer.lastTickPosY) * (double)par4;
//         double var11 = par1EntityPlayer.lastTickPosZ + (par1EntityPlayer.posZ - par1EntityPlayer.lastTickPosZ) * (double)par4;
//         this.drawOutlinedBoundingBox(block.getSelectedBoundingBoxFromPool(this.theWorld, rc.block_hit_x, rc.block_hit_y, rc.block_hit_z).expand((double)var5, (double)var5, (double)var5).getOffsetBoundingBox(-var7, -var9, -var11));
//         GL11.glDepthMask(true);
//         GL11.glEnable(3553);
//         Shaders.enableTexture2D();
//         GL11.glDisable(3042);
//      }
//
//   }

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

//   /**
//    * @author
//    * @reason
//    */
//   @Overwrite
//   public void renderClouds(float par1) {
//      if (this.mc.theWorld.provider.isSurfaceWorld()) {
//         boolean force_fancy_clouds = true;
//         if (!force_fancy_clouds && !this.mc.gameSettings.isFancyGraphicsEnabled()) {
//            GL11.glEnable(2884);
//            float var2 = (float)(this.mc.renderViewEntity.lastTickPosY + (this.mc.renderViewEntity.posY - this.mc.renderViewEntity.lastTickPosY) * (double)par1);
//            byte var3 = 32;
//            int var4 = 256 / var3;
//            Tessellator var5 = Tessellator.instance;
//            this.renderEngine.bindTexture(locationCloudsPng);
//            GL11.glEnable(3042);
//            GL11.glBlendFunc(770, 771);
//            Vec3 var6 = this.theWorld.getCloudColour(par1);
//            float var7 = (float)var6.xCoord;
//            float var8 = (float)var6.yCoord;
//            float var9 = (float)var6.zCoord;
//            float var10;
//            if (this.mc.gameSettings.anaglyph) {
//               var10 = (var7 * 30.0F + var8 * 59.0F + var9 * 11.0F) / 100.0F;
//               float var11 = (var7 * 30.0F + var8 * 70.0F) / 100.0F;
//               float var12 = (var7 * 30.0F + var9 * 70.0F) / 100.0F;
//               var7 = var10;
//               var8 = var11;
//               var9 = var12;
//            }
//
//            var10 = 4.8828125E-4F;
//            double var24 = (float)this.cloudTickCounter + par1;
//            double var13 = this.mc.renderViewEntity.prevPosX + (this.mc.renderViewEntity.posX - this.mc.renderViewEntity.prevPosX) * (double)par1 + var24 * 0.029999999329447746D;
//            double var15 = this.mc.renderViewEntity.prevPosZ + (this.mc.renderViewEntity.posZ - this.mc.renderViewEntity.prevPosZ) * (double)par1;
//            int var17 = MathHelper.floor_double(var13 / 2048.0D);
//            int var18 = MathHelper.floor_double(var15 / 2048.0D);
//            var13 -= var17 * 2048;
//            var15 -= var18 * 2048;
//            float var19 = this.theWorld.provider.getCloudHeight() - var2 + 0.33F;
//            float var20 = (float)(var13 * (double)var10);
//            float var21 = (float)(var15 * (double)var10);
//            boolean player_can_see_cloud_bottoms = var19 > -0.0F;
//            GL11.glCullFace(player_can_see_cloud_bottoms ? 1028 : 1029);
//            var5.startDrawingQuads();
//            GL11.glColor4f(var7, var8, var9, 0.8F);
//            var5.hasTexture = true;
//            int[] rawBuffer = var5.rawBuffer;
//            int y0 = Float.floatToRawIntBits(var19);
//
//            for(int var22 = -var3 * var4; var22 < var3 * var4; var22 += var3) {
//               int u0 = Float.floatToRawIntBits((float)var22 * var10 + var20);
//               int u1 = Float.floatToRawIntBits((float)(var22 + var3) * var10 + var20);
//               int x0 = Float.floatToRawIntBits((float)var22);
//               int x1 = Float.floatToRawIntBits((float)(var22 + var3));
//
//               for(int var23 = -var3 * var4; var23 < var3 * var4; var23 += var3) {
//                  if (RenderingScheme.current == 0) {
//                     var5.addVertexWithUV(var22, var19, var23 + var3, (float)(var22) * var10 + var20, (float)(var23 + var3) * var10 + var21);
//                     var5.addVertexWithUV(var22 + var3, var19, var23 + var3, (float)(var22 + var3) * var10 + var20, (float)(var23 + var3) * var10 + var21);
//                     var5.addVertexWithUV(var22 + var3, var19, var23, (float)(var22 + var3) * var10 + var20, (float)(var23) * var10 + var21);
//                     var5.addVertexWithUV(var22, var19, var23, (float)(var22) * var10 + var20, (float)(var23) * var10 + var21);
//                  } else {
//                     int v0 = Float.floatToRawIntBits((float)(var23 + var3) * var10 + var21);
//                     int v2 = Float.floatToRawIntBits((float)var23 * var10 + var21);
//                     int z0 = Float.floatToRawIntBits((float)(var23 + var3));
//                     int z1 = Float.floatToRawIntBits((float)var23);
//                     rawBuffer[var5.rawBufferIndex + 3] = u0;
//                     rawBuffer[var5.rawBufferIndex + 11] = u1;
//                     rawBuffer[var5.rawBufferIndex + 19] = u1;
//                     rawBuffer[var5.rawBufferIndex + 27] = u0;
//                     rawBuffer[var5.rawBufferIndex + 4] = v0;
//                     rawBuffer[var5.rawBufferIndex + 12] = v0;
//                     rawBuffer[var5.rawBufferIndex + 20] = v2;
//                     rawBuffer[var5.rawBufferIndex + 28] = v2;
//                     rawBuffer[var5.rawBufferIndex] = x0;
//                     rawBuffer[var5.rawBufferIndex + 8] = x1;
//                     rawBuffer[var5.rawBufferIndex + 16] = x1;
//                     rawBuffer[var5.rawBufferIndex + 24] = x0;
//                     rawBuffer[var5.rawBufferIndex + 1] = y0;
//                     rawBuffer[var5.rawBufferIndex + 9] = y0;
//                     rawBuffer[var5.rawBufferIndex + 17] = y0;
//                     rawBuffer[var5.rawBufferIndex + 25] = y0;
//                     rawBuffer[var5.rawBufferIndex + 2] = z0;
//                     rawBuffer[var5.rawBufferIndex + 10] = z0;
//                     rawBuffer[var5.rawBufferIndex + 18] = z1;
//                     rawBuffer[var5.rawBufferIndex + 26] = z1;
//                     var5.rawBufferIndex += 32;
//                     var5.addedVertices += 4;
//                     var5.vertexCount += 4;
//                     if (var5.rawBufferIndex >= 2097120) {
//                        var5.draw();
//                        var5.isDrawing = true;
//                     }
//                  }
//               }
//            }
//
//            var5.draw();
//            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
//            GL11.glDisable(3042);
//            GL11.glCullFace(1029);
//         } else if (this.mc.gameSettings.anaglyph) {
//            this.renderCloudsFancy(par1);
//         } else {
//            this.renderCloudsFancy_MITE(par1);
//         }
//      }
//
//   }

   public WorldClient getClientWorld() {
      return this.theWorld;
   }

   @Inject(at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glBlendFunc(II)V", shift = Shift.AFTER),
      method = {"drawBlockDamageTexture"}
   )
   private void injectDrawBlockDamageTexture0(CallbackInfo callbackInfo) {
      Shaders.beginBlockDestroyProgress();
   }

   @Inject(at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glPopMatrix()V", shift = Shift.AFTER),
      method = {"drawBlockDamageTexture"}
   )
   private void injectDrawBlockDamageTexture1(CallbackInfo callbackInfo) {
      Shaders.endBlockDestroyProgress();
   }

   public List getWorldRenderersToUpdate() {
      return this.worldRenderersToUpdate;
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
   public void destroyBlockPartially(int i, int i1, int i2, int i3, int i4) {
   }
}
