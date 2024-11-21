package net.wenscHuix.mitemod.mixin.render;

import java.lang.reflect.InvocationTargetException;
import java.nio.FloatBuffer;
import java.util.Random;

import net.minecraft.*;
import net.wenscHuix.mitemod.shader.client.Shaders;
import net.wenscHuix.mitemod.shader.client.ShadersRender;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.util.glu.Project;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin {
   @Shadow public ItemRenderer itemRenderer;

   @Shadow protected abstract void setupViewBobbing(float par1);

   @Shadow protected abstract void hurtCameraEffect(float par1);

   @Shadow protected abstract float getFOVModifier(float par1, boolean par2);

   @Shadow public abstract void setupCameraTransform(float par1, int par2, boolean extend_far_clipping_plane);

   @Shadow public abstract double getDistanceToNearestBiomeThatCanBeFoggy(double pos_x, double pos_z);

   @Shadow
   public static float getProximityToNearestFogPost(EntityLivingBase viewer) {
      return 0.0F;
   }

   @Shadow private double cameraPitch;
   @Shadow private double cameraYaw;

   @Shadow protected abstract void renderRainSnow(float par1);

   @Shadow private double cameraZoom;

   @Shadow public abstract void disableLightmap(double par1);

   @Shadow public abstract void enableLightmap(double par1);

   @Shadow public int debugViewDirection;

   @Shadow protected abstract void updateFogColor(float par1);

   @Shadow public static int anaglyphField;

   @Shadow public abstract void getMouseOver(float partial_tick);

   @Shadow protected abstract void updateLightmap(float par1);

   @Shadow private boolean lightmapUpdateNeeded;
   @Shadow private boolean cloudFog;
   @Shadow private float farPlaneDistance;
   @Shadow private float fogColorBlue;
   @Shadow private float fogColorGreen;
   @Shadow private float fogColorRed;

   @Shadow protected abstract FloatBuffer setFogColorBuffer(float par1, float par2, float par3, float par4);

   @Shadow private Minecraft mc;
   @Shadow
   private static double distance_from_biome_that_can_be_foggy;
   @Shadow
   private static long distance_from_biome_that_can_be_foggy_tick;
   @Shadow
   private static World distance_from_biome_that_can_be_foggy_last_viewer_world;
   @Shadow
   private static double distance_from_biome_that_can_be_foggy_last_viewer_pos_x;
   @Shadow
   private static double distance_from_biome_that_can_be_foggy_last_viewer_pos_z;
   @Shadow
   private static Random random_for_fog_events;
   @Shadow
   private long last_vsync_nanotime;
   @Shadow
   private long fps_start_time;
   @Shadow
   private int fps_counter;
   @Shadow
   private long fp10s_start_time;
   @Shadow
   private int fp10s_counter;
   @Shadow
   public static boolean disable_fog;
   @Shadow
   @Final
   private static boolean capability_gl_nv_fog_distance;
   @Shadow
   private float skylight_brightness_used_for_fog;

   @Shadow private float[] r;

   @Unique
   private int var13;

   @Inject(at = {@At("RETURN")}, method = {"disableLightmap"}
   )
   private void injectDisableLightmap(CallbackInfo callbackInfo) {
      Shaders.disableLightmap();
   }

   @Inject(at = {@At("RETURN")}, method = {"enableLightmap"}
   )
   private void injectEnableLightmap(CallbackInfo callbackInfo) {
      Shaders.enableLightmap();
   }

   @Inject(at = {@At("HEAD")}, method = {"setFogColorBuffer"}
   )
   private void injectSetFogColorBuffer(float par1, float par2, float par3, float par4, CallbackInfoReturnable<FloatBuffer> cir) {
      Shaders.setFogColor(par1, par2, par3);
   }

   /**
    * @author
    * @reason
    */
   @Overwrite
   private void setupFog(int par1, float par2) {
      EntityLivingBase var3 = this.mc.renderViewEntity;
      boolean var4 = disable_fog;
      if (par1 == 999) {
         GL11.glFog(2918, this.setFogColorBuffer(0.0F, 0.0F, 0.0F, 1.0F));
         Shaders.sglFogi(2917, 9729);
         GL11.glFogi(2917, 9729);
         GL11.glFogf(2915, 0.0F);
         GL11.glFogf(2916, 8.0F);
         if (GLContext.getCapabilities().GL_NV_fog_distance) {
            Shaders.sglFogi(34138, 34139);
         }

         GL11.glFogf(2915, 0.0F);
      } else {
         GL11.glFog(2918, this.setFogColorBuffer(this.fogColorRed, this.fogColorGreen, this.fogColorBlue, 1.0F));
         GL11.glNormal3f(0.0F, -1.0F, 0.0F);
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         int var5 = ActiveRenderInfo.getBlockIdAtEntityViewpoint(this.mc.theWorld, var3, par2);
         float var6;
         if (var3.isPotionActive(Potion.blindness)) {
            var6 = 5.0F;
            int var7 = var3.getActivePotionEffect(Potion.blindness).getDuration();
            if (var7 < 20) {
               var6 = 5.0F + (this.farPlaneDistance - 5.0F) * (1.0F - (float)var7 / 20.0F);
            }

            Shaders.sglFogi(2917, 9729);
            if (par1 < 0) {
               GL11.glFogf(2915, 0.0F);
               GL11.glFogf(2916, var6 * 0.8F);
            } else {
               GL11.glFogf(2915, var6 * 0.25F);
               GL11.glFogf(2916, var6);
            }

            if (GLContext.getCapabilities().GL_NV_fog_distance) {
               Shaders.sglFogi(34138, 34139);
            }
         } else if (this.cloudFog) {
            Shaders.sglFogi(2917, 2048);
            GL11.glFogf(2914, 0.1F);
         } else if (var5 > 0 && Block.blocksList[var5].blockMaterial == Material.water) {
            Shaders.sglFogi(2917, 2048);
            if (var3.isPotionActive(Potion.waterBreathing)) {
               GL11.glFogf(2914, 0.05F);
            } else {
               GL11.glFogf(2914, 0.1F - (float)EnchantmentHelper.getRespiration(var3) * 0.03F);
            }
         } else if (var5 > 0 && Block.blocksList[var5].blockMaterial == Material.lava) {
            Shaders.sglFogi(2917, 2048);
            GL11.glFogf(2914, 2.0F);
         } else {
            var6 = this.farPlaneDistance;
            if (!var4) {
               if (!this.mc.theWorld.provider.getWorldHasVoidFog()) {
                  if (this.mc.theWorld.isUnderworld()) {
                     var6 = 128.0F;
                  }
               } else {
                  Vec3 eye_pos = var3.getEyePos();
                  int skylight_brightness_at_eye_pos = this.mc.theWorld.getSkyBlockTypeBrightness(EnumSkyBlock.Sky, eye_pos.getBlockX(), eye_pos.getBlockY(), eye_pos.getBlockZ());
                  int effective_fps = Math.max(Minecraft.last_fps, 1);
                  float delta;
                  float max_change;
                  if (this.skylight_brightness_used_for_fog < (float)skylight_brightness_at_eye_pos) {
                     delta = (float)skylight_brightness_at_eye_pos - this.skylight_brightness_used_for_fog;
                     max_change = !(delta < 12.0F) && eye_pos.getBlockY() >= 0 ? delta : 0.6F / (float)effective_fps;
                     this.skylight_brightness_used_for_fog += Math.min(delta, max_change);
                  } else if (this.skylight_brightness_used_for_fog > (float)skylight_brightness_at_eye_pos) {
                     delta = this.skylight_brightness_used_for_fog - (float)skylight_brightness_at_eye_pos;
                     max_change = !(delta < 12.0F) && eye_pos.getBlockY() >= 0 ? delta : 0.6F / (float)effective_fps;
                     this.skylight_brightness_used_for_fog -= Math.min(delta, max_change);
                  }

                  double interpolated_pos_y = MathHelper.getInterpolatedValue(var3.lastTickPosY, var3.posY, par2);
                  double var10 = (double)this.skylight_brightness_used_for_fog / 16.0D + (interpolated_pos_y + 4.0D) / 20.0D;
                  if (var10 < 0.0D) {
                     var10 = 0.0D;
                  }

                  double power = 1.0D + (16.0D - interpolated_pos_y) / 2.0D;
                  if (power > 1.0D) {
                     var10 = Math.pow(var10, power);
                  }

                  float var9 = 100.0F * (float)var10;
                  if (var9 < 5.0F) {
                     var9 = 5.0F;
                  }

                  if (var6 > var9) {
                     var6 = var9;
                  }

                  if (var6 < this.farPlaneDistance) {
                     var6 = (float)((double)var6 / Math.max(Math.sqrt(getProximityToNearestFogPost(var3)), 0.009999999776482582D));
                  }

                  if (var6 < 24.0F) {
                     var6 = 24.0F;
                  }

                  if (var6 > 96.0F && this.mc.theWorld.isOverworld()) {
                     long shifted_total_world_time = this.mc.theWorld.getTotalWorldTime() - 12000L;
                     int shifted_day_of_world = World.getDayOfWorld(shifted_total_world_time);
                     if (shifted_day_of_world > 7) {
                        random_for_fog_events.setSeed((long)shifted_day_of_world * 365024131L * this.mc.theWorld.getWorldCreationTime() * 672784657L);
                        random_for_fog_events.nextInt();
                        if (random_for_fog_events.nextInt(7) == 0) {
                           float fog_max_strength = 96.0F + random_for_fog_events.nextFloat() * (var6 - 96.0F) * 0.75F;
                           long ticks_from_midnight = this.mc.theWorld.getAdjustedTimeOfDay();
                           boolean is_dusk = false;
                           if (ticks_from_midnight > 12000L) {
                              ticks_from_midnight = 24000L - ticks_from_midnight;
                              is_dusk = true;
                           }

                           float day_cycle_factor = MathHelper.clamp_float((float)(8000L - ticks_from_midnight) / (is_dusk ? 4000.0F : 2000.0F), 0.0F, 1.0F);
                           if (day_cycle_factor > 0.0F) {
                              if (distance_from_biome_that_can_be_foggy_last_viewer_world != this.mc.theWorld || distance_from_biome_that_can_be_foggy_tick != this.mc.theWorld.getTotalWorldTime()) {
                                 boolean player_moved = distance_from_biome_that_can_be_foggy_last_viewer_world != this.mc.theWorld || distance_from_biome_that_can_be_foggy_last_viewer_pos_x != var3.posX || distance_from_biome_that_can_be_foggy_last_viewer_pos_z != var3.posZ;
                                 if (player_moved) {
                                    distance_from_biome_that_can_be_foggy = this.getDistanceToNearestBiomeThatCanBeFoggy(var3.posX, var3.posZ);
                                    distance_from_biome_that_can_be_foggy_last_viewer_pos_x = var3.posX;
                                    distance_from_biome_that_can_be_foggy_last_viewer_pos_z = var3.posZ;
                                 }

                                 distance_from_biome_that_can_be_foggy_tick = this.mc.theWorld.getTotalWorldTime();
                              }

                              float distance_from_biome_that_can_be_foggy_factor = Math.max(1.0F - (float)(distance_from_biome_that_can_be_foggy / 32.0D), 0.0F);
                              float final_factor = day_cycle_factor * distance_from_biome_that_can_be_foggy_factor;
                              if (final_factor > 0.0F) {
                                 float fog_strength = fog_max_strength * final_factor + var6 * (1.0F - final_factor);
                                 if (var6 > fog_strength) {
                                    var6 = fog_strength;
                                 }
                              }
                           }
                        }
                     }
                  }

                  if (var6 > this.farPlaneDistance) {
                     var6 = this.farPlaneDistance;
                  }
               }

               Shaders.sglFogi(2917, 9729);
            }

            GL11.glFogi(2917, 9729);
            if (par1 < 0) {
               GL11.glFogf(2915, 0.0F);
               GL11.glFogf(2916, var6 * 0.8F);
            } else {
               GL11.glFogf(2915, var6 * 0.25F);
               GL11.glFogf(2916, var6);
            }

            if (GLContext.getCapabilities().GL_NV_fog_distance) {
               Shaders.sglFogi(34138, 34139);
            }

            if (this.mc.theWorld.provider.doesXZShowFog(var3.getBlockPosX(), var3.getEyeBlockPosY(), var3.getBlockPosZ())) {
               GL11.glFogf(2915, var6 * 0.05F);
               GL11.glFogf(2916, Math.min(var6, 192.0F) * 0.5F);
            }
         }

         GL11.glEnable(2903);
         GL11.glColorMaterial(1028, 4608);
      }

   }

//   @Overwrite
//   private void renderCloudsCheck(RenderGlobal par1RenderGlobal, float par2) {
//      // *********<
//      if (Shaders.shouldRenderClouds(this.mc.gameSettings)) {
//         // *******>
//         this.mc.mcProfiler.endStartSection("clouds");
//         GL11.glPushMatrix();
//         this.setupFog(0, par2);
//         GL11.glEnable(2912);
//         // *****<
//         Shaders.beginClouds();
//         // *****>
//         par1RenderGlobal.renderClouds(par2);
//         // *****<
//         Shaders.endClouds();
//         // *****>
//         GL11.glDisable(2912);
//         this.setupFog(1, par2);
//         GL11.glPopMatrix();
//      }
//
//   }
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

//   @Overwrite
//   public void renderWorld(float par1, long par2) throws NoSuchFieldException, InvocationTargetException, IllegalAccessException, NoSuchMethodException {
//      Shaders.beginRender(this.mc, par1, par2);
//      this.mc.mcProfiler.startSection("lightTex");
//      if (this.lightmapUpdateNeeded) {
//         this.updateLightmap(par1);
//      }
//
//      GL11.glEnable(2884);
//      GL11.glEnable(2929);
//      if (this.mc.renderViewEntity == null) {
//         this.mc.renderViewEntity = this.mc.thePlayer;
//      }
//
//      this.mc.mcProfiler.endStartSection("pick");
//      this.getMouseOver(par1);
//      EntityLivingBase var4 = this.mc.renderViewEntity;
//      if (var4 instanceof EntityPlayer entity_player) {
//          if (entity_player.inBed()) {
//            entity_player.setPositionAndRotationInBed();
//         }
//      }
//
//      RenderGlobal var5 = this.mc.renderGlobal;
//      EffectRenderer var6 = this.mc.effectRenderer;
//      double var7 = var4.lastTickPosX + (var4.posX - var4.lastTickPosX) * (double)par1;
//      double var9 = var4.lastTickPosY + (var4.posY - var4.lastTickPosY) * (double)par1;
//      double var11 = var4.lastTickPosZ + (var4.posZ - var4.lastTickPosZ) * (double)par1;
//      this.mc.mcProfiler.endStartSection("center");
//
//      for(int var13 = 0; var13 < 2; ++var13) {
//         if (this.mc.gameSettings.anaglyph) {
//            anaglyphField = var13;
//            if (anaglyphField == 0) {
//               GL11.glColorMask(false, true, true, false);
//            } else {
//               GL11.glColorMask(true, false, false, false);
//            }
//         }
//
//         this.mc.mcProfiler.endStartSection("clear");
//         Shaders.setViewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
//         this.updateFogColor(par1);
//         GL11.glClear(16640);
//         Shaders.clearRenderBuffer();
//         GL11.glEnable(2884);
//         this.mc.mcProfiler.endStartSection("camera");
//         this.setupCameraTransform(par1, var13, false);
//         Shaders.setCamera(par1);
//         if (this.last_vsync_nanotime != -1L) {
//            long milliseconds_since_last_vsync = (System.nanoTime() - this.last_vsync_nanotime) / 1000000L;
//            this.mc.downtimeProcessing(16L - milliseconds_since_last_vsync);
//         }
//
//         ActiveRenderInfo.updateRenderInfo(this.mc.thePlayer, this.mc.gameSettings.thirdPersonView == 2);
//         this.last_vsync_nanotime = System.nanoTime();
//         if (this.last_vsync_nanotime > this.fps_start_time + 1000000000L) {
//            this.fps_start_time = this.last_vsync_nanotime;
//            Minecraft.last_fps = this.fps_counter;
//            this.fps_counter = 0;
//         } else {
//            ++this.fps_counter;
//         }
//
//         if (this.last_vsync_nanotime > this.fp10s_start_time + 10000000000L) {
//            this.fp10s_start_time = this.last_vsync_nanotime;
//            Minecraft.last_fp10s = this.fp10s_counter;
//            this.fp10s_counter = 0;
//         } else {
//            ++this.fp10s_counter;
//         }
//
//         this.mc.mcProfiler.endStartSection("frustrum");
//         ClippingHelperImpl.getInstance();
//         if (!Shaders.isShadowPass) {
//            this.setupFog(-1, par1);
//            Shaders.beginSky();
//            this.mc.mcProfiler.endStartSection("sky");
//            var5.renderSky(par1);
//            Shaders.endSky();
//         }
//
//         GL11.glEnable(2912);
//         this.setupFog(1, par1);
//         if (this.mc.gameSettings.ambientOcclusion != 0) {
//            GL11.glShadeModel(7425);
//         }
//
//         this.mc.mcProfiler.endStartSection("culling");
//         Frustrum frustrum = new Frustrum();
//         ShadersRender.setFrustrumPosition(frustrum, var7, var9, var11);
//         ShadersRender.clipRenderersByFrustrum(this.mc.renderGlobal, frustrum, par1);
//         if (var13 == 0) {
//            Shaders.beginUpdateChunks();
//            this.mc.mcProfiler.endStartSection("updatechunks");
//
//            while(!this.mc.renderGlobal.updateRenderers(var4, false) && par2 != 0L) {
//               long var15 = par2 - System.nanoTime();
//               if (this.mc.gameSettings.limitFramerate == 3) {
//                  if (var15 < 1000000L || var15 > 1000000000L) {
//                     break;
//                  }
//               } else if (var15 < 0L || var15 > 1000000000L) {
//                  break;
//               }
//            }
//
//            Shaders.endUpdateChunks();
//         }
//
//         if (var4.posY < 128.0D) {
//            this.renderCloudsCheck(var5, par1);
//         }
//
//         this.mc.mcProfiler.endStartSection("prepareterrain");
//         this.setupFog(0, par1);
//         GL11.glEnable(2912);
//         this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
//         RenderHelper.disableStandardItemLighting();
//         this.mc.mcProfiler.endStartSection("terrain");
//         Shaders.beginTerrain();
//         var5.sortAndRender(var4, 0, (double)par1);
//         Shaders.endTerrain();
//         GL11.glShadeModel(7424);
//         EntityPlayer var17;
//         if (this.debugViewDirection == 0) {
//            RenderHelper.enableStandardItemLighting();
//            this.mc.mcProfiler.endStartSection("entities");
//            var5.renderEntities(var4.getPosition(par1), frustrum, par1);
//            this.enableLightmap(par1);
//            this.mc.mcProfiler.endStartSection("litParticles");
//            Shaders.beginLitParticles();
//            var6.renderLitParticles(var4, par1);
//            RenderHelper.disableStandardItemLighting();
//            this.setupFog(0, par1);
//            Shaders.beginParticles();
//            this.mc.mcProfiler.endStartSection("particles");
//            var6.renderParticles(var4, par1);
//            Shaders.endParticles();
//            this.disableLightmap(par1);
//            if (this.mc.objectMouseOver != null && var4.isInsideOfMaterial(Material.water) && var4 instanceof EntityPlayer && this.mc.gameSettings.gui_mode == 0) {
//               var17 = (EntityPlayer)var4;
//               GL11.glDisable(3008);
//               this.mc.mcProfiler.endStartSection("outline");
//               var5.drawSelectionBox(var17, this.mc.objectMouseOver, 0, par1);
//               GL11.glEnable(3008);
//            }
//         }
//
//         GL11.glDisable(3042);
//         GL11.glEnable(2884);
//         GL11.glBlendFunc(770, 771);
//         GL11.glDepthMask(true);
//         Shaders.beginHand();
//         this.renderHand(par1, var13);
//         Shaders.endHand();
//         Shaders.preWater();
//         GL11.glEnable(3042);
//         GL11.glDisable(2884);
//         this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
//         if (this.mc.gameSettings.isFancyGraphicsEnabled()) {
//            this.mc.mcProfiler.endStartSection("water");
//            if (this.mc.gameSettings.ambientOcclusion != 0) {
//               GL11.glShadeModel(7425);
//            }
//
//            GL11.glColorMask(false, false, false, false);
//            Shaders.beginWaterFancy();
//            int var18 = var5.sortAndRender(var4, 1, par1);
//            if (this.mc.gameSettings.anaglyph) {
//               if (anaglyphField == 0) {
//                  GL11.glColorMask(false, true, true, true);
//               } else {
//                  GL11.glColorMask(true, false, false, true);
//               }
//            } else {
//               GL11.glColorMask(true, true, true, true);
//            }
//
//            if (var18 > 0) {
//               Shaders.midWaterFancy();
//               var5.renderAllRenderLists(1, par1);
//            }
//
//            Shaders.endWater();
//            GL11.glShadeModel(7424);
//         } else {
//            this.mc.mcProfiler.endStartSection("water");
//            Shaders.beginWater();
//            var5.sortAndRender(var4, 1, par1);
//            Shaders.endWater();
//         }
//
//         GL11.glDepthMask(true);
//         GL11.glEnable(2884);
//         GL11.glDisable(3042);
//         if (Shaders.isShadowPass) {
//            return;
//         }
//
//         Shaders.readCenterDepth();
//         if (this.cameraZoom == 1.0 && var4 instanceof EntityPlayer && this.mc.gameSettings.gui_mode == 0 && this.mc.objectMouseOver != null && !var4.isInsideOfMaterial(Material.water)) {
//            var17 = (EntityPlayer)var4;
//            GL11.glDisable(3008);
//            this.mc.mcProfiler.endStartSection("outline");
//            var5.drawSelectionBox(var17, this.mc.objectMouseOver, 0, par1);
//            GL11.glEnable(3008);
//         }
//
//         this.mc.mcProfiler.endStartSection("destroyProgress");
//         GL11.glEnable(3042);
//         GL11.glBlendFunc(770, 1);
//          if (var4 instanceof EntityPlayer) {
//             var5.drawBlockDamageTexture(Tessellator.instance, (EntityPlayer)var4, par1);
//          }
//          GL11.glDisable(3042);
//         this.mc.mcProfiler.endStartSection("weather");
//         Shaders.beginWeather();
//         this.renderRainSnow(par1);
//         Shaders.endWeather();
//         GL11.glDisable(2912);
//         Shaders.disableFog();
//         if (var4.posY >= 128.0D) {
//            this.renderCloudsCheck(var5, par1);
//         }
//
//         this.mc.mcProfiler.endStartSection("hand");
//         Shaders.renderCompositeFinal();
//         if (this.cameraZoom == 1.0D) {
//            GL11.glClear(256);
//            Shaders.beginFPOverlay();
//            this.renderHand(par1, var13);
//            Shaders.endFPOverlay();
//         }
//
//         Shaders.endRender();
//         if (!this.mc.gameSettings.anaglyph) {
//            this.mc.mcProfiler.endSection();
//            return;
//         }
//      }
//
//      GL11.glColorMask(true, true, true, false);
//      this.mc.mcProfiler.endSection();
//   }

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

   /**
    * @author
    * @reason
    */
   @Overwrite
   private void renderHand(float par1, int par2) {
      if (this.debugViewDirection <= 0) {
         GL11.glMatrixMode(5889);
         GL11.glLoadIdentity();
         float var3 = 0.07F;
         if (this.mc.gameSettings.anaglyph) {
            GL11.glTranslatef((float)(-(par2 * 2 - 1)) * var3, 0.0F, 0.0F);
         }

         if (this.cameraZoom != 1.0) {
            GL11.glTranslatef((float)this.cameraYaw, (float)(-this.cameraPitch), 0.0F);
            GL11.glScaled(this.cameraZoom, this.cameraZoom, 1.0);
         }
         //***********
         float var10000 = this.getFOVModifier(par1, false);
         float var10001 = (float)this.mc.displayWidth / (float)this.mc.displayHeight;
         float var10003 = this.farPlaneDistance * 2.0F;
         Shaders.applyHandDepth();
         //***********
         Project.gluPerspective(var10000, var10001, 0.05F, var10003);
         if (this.mc.playerController.enableEverythingIsScrewedUpMode()) {
            float var4 = 0.6666667F;
            GL11.glScalef(1.0F, var4, 1.0F);
         }

         GL11.glMatrixMode(5888);
         GL11.glLoadIdentity();
         if (this.mc.gameSettings.anaglyph) {
            GL11.glTranslatef((float)(par2 * 2 - 1) * 0.1F, 0.0F, 0.0F);
         }
         // ******
         if (!Shaders.isCompositeRendered) {
            this.hurtCameraEffect(par1);
            if (this.mc.gameSettings.viewBobbing) {
               this.setupViewBobbing(par1);
            }

            if (this.mc.gameSettings.thirdPersonView == 0 && !this.mc.renderViewEntity.inBed() && this.mc.gameSettings.gui_mode != 2 && !this.mc.playerController.enableEverythingIsScrewedUpMode()) {
               this.enableLightmap(par1);
               this.itemRenderer.renderItemInFirstPerson(par1);
               this.disableLightmap(par1);
            }
            return;
         }
         // ******

         if (this.mc.gameSettings.thirdPersonView == 0 && !this.mc.renderViewEntity.inBed()) {
            this.itemRenderer.renderOverlays(par1);
            this.hurtCameraEffect(par1);
         }

         if (this.mc.gameSettings.viewBobbing) {
            this.setupViewBobbing(par1);
         }
      }

   }
}
