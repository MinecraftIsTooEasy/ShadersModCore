package shadersmodcore.mixin.client.render.texture;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.*;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import shadersmodcore.api.TextureAtlasSpriteAccessor;
import shadersmodcore.client.shader.ShadersTex;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.IntBuffer;
import java.util.List;

@Mixin(TextureAtlasSprite.class)
public abstract class TextureAtlasSpriteMixin implements TextureAtlasSpriteAccessor {

   @Shadow protected abstract void allocateFrameTextureData(int par1);

   @Shadow protected abstract void resetSprite();

   public IntBuffer[] frameBuffers;
   public boolean mipmapActive = false;
   @Shadow
   private AnimationMetadataSection animationMetadata;
   @Shadow
   protected List framesTextureData;
   @Shadow
   protected boolean rotated;
   @Shadow
   protected int originX;
   @Shadow
   protected int originY;
   @Shadow
   protected int width;
   @Shadow
   protected int height;
   @Shadow
   protected int frameCounter;
   @Shadow
   protected int tickCounter;

   @Shadow public abstract void loadSprite(Resource par1Resource) throws IOException;

   public int getOriginX() {
      return this.originX;
   }

   public int getOriginY() {
      return this.originY;
   }

   @Inject(method = "updateAnimation", at = @At(value = "INVOKE", target = "Lnet/minecraft/TextureUtil;uploadTextureSub([IIIIIZZ)V"))
   public void updateAnimation(CallbackInfo info) {
      ++this.tickCounter;
      if (this.tickCounter >= this.animationMetadata.getFrameTimeSingle(this.frameCounter)) {
         int var1 = this.animationMetadata.getFrameIndex(this.frameCounter);
         int var2 = this.animationMetadata.getFrameCount() == 0 ? this.framesTextureData.size() : this.animationMetadata.getFrameCount();
         this.frameCounter = (this.frameCounter + 1) % var2;
         this.tickCounter = 0;
         int var3 = this.animationMetadata.getFrameIndex(this.frameCounter);
         if (var1 != var3 && var3 >= 0 && var3 < this.framesTextureData.size()) {
            ShadersTex.updateSubImage((int[]) this.framesTextureData.get(var3), this.width, this.height, this.originX, this.originY, false, false);
         }
      }
   }

   public void uploadFrameMipmaps(int frameIndex, int xPos, int yPos) {
   }

//   /**
//    * @author
//    * @reason
//    */
//   @SuppressWarnings("unchecked")
//   @Overwrite
//   public final void loadSprite(Resource par1Resource) throws IOException {
//      this.resetSprite();
//      InputStream var2 = par1Resource.getInputStream();
//      AnimationMetadataSection var3 = (AnimationMetadataSection)par1Resource.getMetadata("animation");
//      BufferedImage var4 = ImageIO.read(var2);
//      this.height = var4.getHeight();
//      this.width = var4.getWidth();
//      int[] var5 = new int[this.height * this.width * 3];
//      ShadersTex.loadAtlasSprite(var4, 0, 0, this.width, this.height, var5, 0, this.width);
//      var4.getRGB(0, 0, this.width, this.height, var5, 0, this.width);
//      if (var3 == null) {
//         if (this.height != this.width) {
//            throw new RuntimeException("broken aspect ratio and not an animation");
//         }
//
//         this.framesTextureData.add(var5);
//      } else {
//         int var6 = this.height / this.width;
//         int var7 = this.width;
//         int var8 = this.width;
//         this.height = this.width;
//         int var10;
//         if (var3.getFrameCount() > 0) {
//
//             for (Object o : var3.getFrameIndexSet()) {
//                 var10 = (Integer) o;
//                 if (var10 >= var6) {
//                     throw new RuntimeException("invalid frameindex " + var10);
//                 }
//
//                 this.allocateFrameTextureData(var10);
//                 this.framesTextureData.set(var10, ShadersTex.extractFrame(var5, var7, var8, var10));
//             }
//
//            this.animationMetadata = var3;
//         } else {
//            ArrayList<Object> var11 = Lists.newArrayList();
//
//            for(var10 = 0; var10 < var6; ++var10) {
//               this.framesTextureData.add(ShadersTex.extractFrame(var5, var7, var8, var10));
//               var11.add(new AnimationFrame(var10, -1));
//            }
//
//            this.animationMetadata = new AnimationMetadataSection(var11, this.width, this.height, var3.getFrameTime());
//         }
//      }
//
//   }

   @Redirect(method = "loadSprite", at = @At(value = "FIELD", target = "Lnet/minecraft/TextureAtlasSprite;width:I", ordinal = 1))
   private int redirectWidth(TextureAtlasSprite instance) {
      return this.width * 3;
   }

   @Inject(method = "loadSprite", at = @At(value = "INVOKE", target = "Ljava/awt/image/BufferedImage;getRGB(IIII[III)[I"), locals = LocalCapture.CAPTURE_FAILSOFT)
   private void loadAtlasSprite(Resource par1Resource, CallbackInfo ci, InputStream var2, AnimationMetadataSection var3, BufferedImage var4, int[] var5) {
      ShadersTex.loadAtlasSprite(var4, 0, 0, this.width, this.height, var5, 0, this.width);
   }


   @WrapOperation(method = "loadSprite", at = @At(value = "INVOKE", target = "Lnet/minecraft/TextureAtlasSprite;getFrameTextureData([IIII)[I", ordinal = 0))
   private int[] extractFrame0(int[] par0ArrayOfInteger, int par1, int par2, int par3, Operation<int[]> original) {
      return ShadersTex.extractFrame(par0ArrayOfInteger, par1, par2, par3);
   }

   @WrapOperation(method = "loadSprite", at = @At(value = "INVOKE", target = "Lnet/minecraft/TextureAtlasSprite;getFrameTextureData([IIII)[I", ordinal = 1))
   private int[] extractFrame(int[] par0ArrayOfInteger, int par1, int par2, int par3, Operation<int[]> original) {
      return ShadersTex.extractFrame(par0ArrayOfInteger, par1, par2, par3);
   }

//   @Inject(method = "loadSprite", at = @At(value = "INVOKE", target = "Ljava/util/List;set(ILjava/lang/Object;)Ljava/lang/Object;"), locals = LocalCapture.CAPTURE_FAILSOFT)
//   private void extractFrame0(Resource par1Resource, CallbackInfo ci, InputStream var2, AnimationMetadataSection var3, BufferedImage var4, int[] var5, int var6, int var7, int var8, int var10, Iterator var9) {
//      this.framesTextureData.set(var10, ShadersTex.extractFrame(var5, var7, var8, var10));
//   }
//
//   @Inject(method = "loadSprite", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", ordinal = 1), locals = LocalCapture.CAPTURE_FAILSOFT)
//   private void extractFrame(Resource par1Resource, CallbackInfo ci, InputStream var2, AnimationMetadataSection var3, BufferedImage var4, int[] var5, int var6, int var7, int var8, int var10) {
//      this.framesTextureData.add(ShadersTex.extractFrame(var5, var7, var8, var10));
//   }

   @Override
   public boolean load(ResourceManager manager, ResourceLocation location) throws IOException {
      this.loadSprite(ShadersTex.loadResource(manager, location));
      return true;
   }
}
