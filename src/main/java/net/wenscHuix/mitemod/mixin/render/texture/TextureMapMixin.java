package net.wenscHuix.mitemod.mixin.render.texture;

import com.google.common.collect.Maps;
import net.minecraft.*;
import net.wenscHuix.mitemod.imixin.TextureAtlasSpriteAccessor;
import net.wenscHuix.mitemod.imixin.TextureObjectAccessor;
import net.wenscHuix.mitemod.shader.client.ShadersTex;
import net.wenscHuix.mitemod.shader.util.TextureUtilExtra;
import net.xiaoyu233.fml.util.ReflectHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

@Mixin({TextureMap.class})
public abstract class TextureMapMixin extends AbstractTexture {
   public int atlasWidth;
   public int atlasHeight;
   @Shadow
   @Final
   public static ResourceLocation locationBlocksTexture;
   @Shadow
   @Final
   public static ResourceLocation locationItemsTexture;
   @Shadow
   @Final
   private List listAnimatedSprites;
   @Shadow
   @Final
   private Map mapRegisteredSprites;
   @Shadow
   @Final
   private Map mapUploadedSprites;
   @Shadow
   @Final
   private int textureType;
   @Shadow
   @Final
   private String basePath;
   @Shadow
   @Final
   private TextureAtlasSprite missingImage;

   @Shadow public abstract int getTextureType();

   /**
    * @author
    * @reason
    */
   @Overwrite
   public void loadTextureAtlas(ResourceManager par1ResourceManager) {
      int var2 = Minecraft.getGLMaximumTextureSize();
      Stitcher var3 = new Stitcher(var2, var2, true);
      this.mapUploadedSprites.clear();
      this.listAnimatedSprites.clear();
      Iterator var4 = this.mapRegisteredSprites.entrySet().iterator();

      TextureAtlasSprite var17;
      while(var4.hasNext()) {
         Entry var5 = (Entry)var4.next();
         ResourceLocation var6 = new ResourceLocation((String)var5.getKey(), false);
         var17 = (TextureAtlasSprite)var5.getValue();
         ResourceLocation var8 = new ResourceLocation(var6.getResourceDomain(), String.format("%s/%s%s", this.basePath, var6.getResourcePath(), ".png"), false);

         try {
            if (!((TextureAtlasSpriteAccessor) var17).mITE_Shader_Loader$load(par1ResourceManager, var8)) {
               continue;
            }
         } catch (RuntimeException var14) {
            Minecraft.getMinecraft().getLogAgent().logSevere(String.format("Unable to parse animation metadata from %s: %s", var8, var14.getMessage()));
            continue;
         } catch (IOException var15) {
            String error_message = "Missing resource: " + var8.getResourcePath();
            Minecraft.getMinecraft().getLogAgent().logSevere(error_message);
            Minecraft.setErrorMessage(error_message, false);
            continue;
         }

         var3.addSprite(var17);
      }

      var3.addSprite(this.missingImage);

      try {
         var3.doStitch();
      } catch (StitcherException var13) {
         throw var13;
      }

      ShadersTex.setupTextureMap(var3.getCurrentWidth(), var3.getCurrentHeight(), var3, ReflectHelper.dyCast(this));
      HashMap var15 = Maps.newHashMap(this.mapRegisteredSprites);
      Iterator var16 = var3.getStichSlots().iterator();

      while(var16.hasNext()) {
         var17 = (TextureAtlasSprite)var16.next();
         String var18 = var17.getIconName();
         var15.remove(var18);
         this.mapUploadedSprites.put(var18, var17);

         try {
            ShadersTex.updateTextureMap(var17.getFrameTextureData(0), var17.getIconWidth(), var17.getIconHeight(), var17.getOriginX(), var17.getOriginY(), false, false);
         } catch (Throwable var12) {
            CrashReport var9 = CrashReport.makeCrashReport(var12, "Stitching texture atlas");
            CrashReportCategory var10 = var9.makeCategory("Texture being stitched together");
            var10.addCrashSection("Atlas path", this.basePath);
            var10.addCrashSection("Sprite", var17);
            throw new ReportedException(var9);
         }

         if (var17.hasAnimationMetadata()) {
            this.listAnimatedSprites.add(var17);
         } else {
            var17.clearFramesTextureData();
         }
      }

      var16 = var15.values().iterator();

      while(var16.hasNext()) {
         var17 = (TextureAtlasSprite)var16.next();
         var17.copyFrom(this.missingImage);
      }

   }

   /**
    * @author
    * @reason
    */
   @Overwrite
   public void updateAnimations() {
      ShadersTex.updatingTex = ((TextureObjectAccessor) this).mITE_Shader_Loader$getMultiTexID();

      TextureUtilExtra.bindTexture(this.getGlTextureId());

       for (Object listAnimatedSprite : this.listAnimatedSprites) {
           TextureAtlasSprite var2 = (TextureAtlasSprite) listAnimatedSprite;
           var2.updateAnimation();
       }

      ShadersTex.updatingTex = null;
   }
}
