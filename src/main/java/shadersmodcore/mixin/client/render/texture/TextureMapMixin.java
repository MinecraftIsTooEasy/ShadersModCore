package shadersmodcore.mixin.client.render.texture;

import com.google.common.collect.Maps;
import net.minecraft.*;
import net.xiaoyu233.fml.util.ReflectHelper;
import org.spongepowered.asm.mixin.*;
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

//   @Unique
//   private ResourceManager par1ResourceManager;

//   @Shadow
//   public void loadTextureAtlas(ResourceManager par1ResourceManager) {
//      this.par1ResourceManager = par1ResourceManager;
//   }


   public int getAtlasWidth() { return this.atlasWidth; }

   public int getAtlasHeight() { return this.atlasHeight; }

   public void setAtlasWidth(int atlasWidth) { this.atlasWidth = atlasWidth; }

   public void setAtlasHeight(int atlasHeight) { this.atlasHeight = atlasHeight; }

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
         Map.Entry var5 = (Map.Entry)var4.next();
         ResourceLocation var6 = new ResourceLocation((String)var5.getKey(), false);
         var17 = (TextureAtlasSprite)var5.getValue();
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

//   @WrapOperation(method = "loadTextureAtlas", at = @At(value = "INVOKE", target = "Lnet/minecraft/TextureAtlasSprite;loadSprite(Lnet/minecraft/Resource;)V"))
//   private void load(TextureAtlasSprite instance, Resource var9, Operation<Void> original) throws IOException {
//      Iterator var4 = this.mapRegisteredSprites.entrySet().iterator();
//
//      while(var4.hasNext()) {
//         Map.Entry var5 = (Map.Entry) var4.next();
//         ResourceLocation var6 = new ResourceLocation((String) var5.getKey(), false);
//         ResourceLocation var8 = new ResourceLocation(var6.getResourceDomain(), String.format("%s/%s%s", this.basePath, var6.getResourcePath(), ".png"), false);
//
//         if (!((TextureAtlasSpriteAccessor) instance).load((ResourceManager) var9, var8)) {
//            original.call(instance, var9);
//         }
//      }
//      original.call(instance, var9);
//   }
//
//   @WrapOperation(method = "loadTextureAtlas", at = @At(value = "INVOKE", target = "Lnet/minecraft/TextureUtil;allocateTexture(III)V"))
//   private void setupTextureMap(int i, int j, int k, Operation<Void> original) {
//      int var2 = Minecraft.getGLMaximumTextureSize();
//      Stitcher var3 = new Stitcher(var2, var2, true);
//      ShadersTex.setupTextureMap(var3.getCurrentWidth(), var3.getCurrentHeight(), var3, ReflectHelper.dyCast(this));
//   }
//
//   @WrapOperation(method = "loadTextureAtlas", at = @At(value = "INVOKE", target = "Lnet/minecraft/TextureUtil;uploadTextureSub([IIIIIZZ)V"))
//   private void setupTextureMap(int[] is, int i, int j, int k, int l, boolean bl, boolean bl2, Operation<Void> original) {
//      ShadersTex.updateTextureMap(is, i, j, k, l, bl, bl2);
//   }

   /**
    * @author
    * @reason
    */
   @Overwrite
   public void updateAnimations() {
      ShadersTex.updatingTex = ((AbstractTextureAccessor) this).getMultiTexID();

      TextureUtilExtra.bindTexture(this.getGlTextureId());

       for (Object listAnimatedSprite : this.listAnimatedSprites) {
           TextureAtlasSprite var2 = (TextureAtlasSprite) listAnimatedSprite;
           var2.updateAnimation();
       }

      ShadersTex.updatingTex = null;
   }
}
