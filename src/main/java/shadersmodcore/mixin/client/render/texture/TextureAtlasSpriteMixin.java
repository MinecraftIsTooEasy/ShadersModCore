package shadersmodcore.mixin.client.render.texture;

import net.minecraft.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import shadersmodcore.api.TextureAtlasSpriteAccessor;
import shadersmodcore.client.shader.ShadersTex;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

@Mixin(TextureAtlasSprite.class)
public abstract class TextureAtlasSpriteMixin implements TextureAtlasSpriteAccessor {
   @Shadow private AnimationMetadataSection animationMetadata;
   @Shadow protected List framesTextureData;
   @Shadow protected boolean rotated;
   @Shadow protected int originX;
   @Shadow protected int originY;
   @Shadow protected int width;
   @Shadow protected int height;
   @Shadow protected int frameCounter;
   @Shadow protected int tickCounter;
   @Shadow public abstract void loadSprite(Resource par1Resource) throws IOException;

//   @Inject(method = "updateAnimation", at = @At(value = "INVOKE", target = "Lnet/minecraft/TextureUtil;uploadTextureSub([IIIIIZZ)V"))
//   public void updateAnimation(CallbackInfo info) {
//      ++this.tickCounter;
//      if (this.tickCounter >= this.animationMetadata.getFrameTimeSingle(this.frameCounter)) {
//         int var1 = this.animationMetadata.getFrameIndex(this.frameCounter);
//         int var2 = this.animationMetadata.getFrameCount() == 0 ? this.framesTextureData.size() : this.animationMetadata.getFrameCount();
//         this.frameCounter = (this.frameCounter + 1) % var2;
//         this.tickCounter = 0;
//         int var3 = this.animationMetadata.getFrameIndex(this.frameCounter);
//         if (var1 != var3 && var3 >= 0 && var3 < this.framesTextureData.size()) {
//            ShadersTex.updateSubImage((int[]) this.framesTextureData.get(var3), this.width, this.height, this.originX, this.originY, false, false);
//         }
//      }
//   }
//
//   @Redirect(method = "loadSprite", at = @At(value = "FIELD", target = "Lnet/minecraft/TextureAtlasSprite;width:I", ordinal = 1))
//   private int redirectWidth(TextureAtlasSprite instance) {
//      return this.width * 3;
//   }
//
//   @Inject(method = "loadSprite", at = @At(value = "INVOKE", target = "Ljava/awt/image/BufferedImage;getRGB(IIII[III)[I"), locals = LocalCapture.CAPTURE_FAILSOFT)
//   private void loadAtlasSprite(Resource par1Resource, CallbackInfo ci, InputStream var2, AnimationMetadataSection var3, BufferedImage var4, int[] var5) {
//      ShadersTex.loadAtlasSprite(var4, 0, 0, this.width, this.height, var5, 0, this.width);
//   }
//
//
////   @WrapOperation(method = "loadSprite", at = @At(value = "INVOKE", target = "Lnet/minecraft/TextureAtlasSprite;getFrameTextureData([IIII)[I", ordinal = 0))
////   private int[] extractFrame0(int[] par0ArrayOfInteger, int par1, int par2, int par3, Operation<int[]> original) {
////      return ShadersTex.extractFrame(par0ArrayOfInteger, par1, par2, par3);
////   }
////
////   @WrapOperation(method = "loadSprite", at = @At(value = "INVOKE", target = "Lnet/minecraft/TextureAtlasSprite;getFrameTextureData([IIII)[I", ordinal = 1))
////   private int[] extractFrame(int[] par0ArrayOfInteger, int par1, int par2, int par3, Operation<int[]> original) {
////      return ShadersTex.extractFrame(par0ArrayOfInteger, par1, par2, par3);
////   }
//
//   @Override
//   public boolean load(ResourceManager manager, ResourceLocation location) throws IOException {
//      this.loadSprite(ShadersTex.loadResource(manager, location));
//      return true;
//   }

   /**
    * Redirect the call to TextureUtil.uploadTexSub() in updateAnimation().
    */
   @Redirect(
           method = "updateAnimation",
           at = @At(
                   value = "INVOKE",
                   target = "Lnet/minecraft/TextureUtil;uploadTextureSub([IIIIIZZ)V"
           )
   )
   private void redirectUploadTexSub(int[] is, int i, int j, int k, int l, boolean bl, boolean bl2) {
      ShadersTex.uploadTexSub(is, i, j, k, l, bl, bl2);
   }

   /**
    * Redirect the call to ResourceManager.getResource() in load().
    */
   @Override
   public boolean load(ResourceManager manager, ResourceLocation location) throws IOException {
      this.loadSprite(ShadersTex.loadResource(manager, location));
      return true;
   }

   /**
    * Redirect calls in loadSprite().
    */
   @Redirect(
           method = "loadSprite",
           at = @At(
                   value = "INVOKE",
                   target = "Ljava/awt/image/BufferedImage;getRGB(IIII[III)[I"
           )
   )
   private int[] redirectGetRGB(BufferedImage bufferedImage, int x, int y, int width, int height, int[] pixels, int offset, int scansize) {
      return ShadersTex.loadAtlasSprite(bufferedImage, x, y, width, height, pixels, offset, scansize);
   }

   @Redirect(
           method = "loadSprite",
           at = @At(
                   value = "INVOKE",
                   target = "Lnet/minecraft/TextureAtlasSprite;getFrameTextureData([IIII)[I"
           )
   )
   private int[] redirectGetFrameTextureData(int[] textureAtlasSprite, int width, int height, int frameIndex) {
      return ShadersTex.getFrameTexData(textureAtlasSprite, width, height, frameIndex);
   }

//   @Redirect(
//           method = "loadSprite",
//           at = @At(
//                   value = "INVOKE",
//                   target = "Lnet/minecraft/texture/TextureAtlasSprite;prepareAF()[[I"
//           )
//   )
//   private int[][] redirectPrepareAF(Object textureAtlasSprite) {
//      return ShadersTex.prepareAF(textureAtlasSprite);
//   }
//
//   @Redirect(
//           method = "loadSprite",
//           at = @At(
//                   value = "INVOKE",
//                   target = "Lnet/minecraft/TextureAtlasSprite;fixTransparentColor([I)V"
//           )
//   )
//   private void redirectFixTransparentColor(Object textureAtlasSprite, int[] p_147943_1_) {
//      ShadersTex.fixTransparentColor(textureAtlasSprite, p_147943_1_);
//   }

//   /**
//    * Override uploadFrameTexture().
//    */
//   @Inject(method = "uploadFrameTexture", at = @At("HEAD"), cancellable = true)
//   private void injectUploadFrameTexture(int frameIndex, int xPos, int yPos, CallbackInfo ci) {
//      ShadersTex.uploadFrameTexture((TextureAtlasSprite) (Object) this, frameIndex, xPos, yPos);
//      ci.cancel(); // Cancel the original method execution
//   }
}
