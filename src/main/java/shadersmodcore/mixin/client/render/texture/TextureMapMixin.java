package shadersmodcore.mixin.client.render.texture;

import com.google.common.collect.Maps;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.*;
import net.xiaoyu233.fml.util.ReflectHelper;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import shadersmodcore.api.AbstractTextureAccessor;
import shadersmodcore.api.TextureAtlasSpriteAccessor;
import shadersmodcore.api.TextureMapAccessor;
import shadersmodcore.client.shader.ShadersTex;
import shadersmodcore.util.TextureUtilExtra;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Mixin({TextureMap.class})
public abstract class TextureMapMixin extends AbstractTexture implements TextureMapAccessor {
   @Shadow @Final private List listAnimatedSprites;
   @Shadow @Final private Map mapRegisteredSprites;
   @Shadow @Final private Map mapUploadedSprites;
   @Shadow @Final private String basePath;
   @Shadow @Final private TextureAtlasSprite missingImage;

   @Unique public int atlasWidth;
   @Unique public int atlasHeight;

   public int getAtlasWidth() {
      return this.atlasWidth;
   }

   public int getAtlasHeight() {
      return this.atlasHeight;
   }

   public void setAtlasWidth(int atlasWidth) {
      this.atlasWidth = atlasWidth;
   }

   public void setAtlasHeight(int atlasHeight) {
      this.atlasHeight = atlasHeight;
   }

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

      TextureAtlasSprite var17;
      for(Object var5 : this.mapRegisteredSprites.entrySet()) {
         Map.Entry map = (Map.Entry) var5;
         ResourceLocation var6 = new ResourceLocation((String) map.getKey(), false);
         var17 = (TextureAtlasSprite) map.getValue();
         ResourceLocation var8 = new ResourceLocation(var6.getResourceDomain(), String.format("%s/%s%s", this.basePath, var6.getResourcePath(), ".png"), false);

         try {
            if (!((TextureAtlasSpriteAccessor) var17).load(par1ResourceManager, var8)) {
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

      while (var16.hasNext()) {
         var17 = (TextureAtlasSprite) var16.next();
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

      while (var16.hasNext()) {
         var17 = (TextureAtlasSprite) var16.next();
         var17.copyFrom(this.missingImage);
      }

   }

   @Inject(method = "updateAnimations", at = @At("HEAD"))
   public void updateAnimationsHead(CallbackInfo ci) {
      ShadersTex.updatingTex = ((AbstractTextureAccessor) this).getMultiTexID();
   }

   @Inject(method = "updateAnimations", at = @At("TAIL"))
   public void updateAnimationsTail(CallbackInfo ci) {
      ShadersTex.updatingTex = null;
   }

   @Redirect(method = "updateAnimations", at = @At(value = "INVOKE", target = "Lnet/minecraft/TextureUtil;bindTexture(I)V"))
   private void bindTexture(int i) {
      TextureUtilExtra.bindTexture(this.getGlTextureId());
   }
}
