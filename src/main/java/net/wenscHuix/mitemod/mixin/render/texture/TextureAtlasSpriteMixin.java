package net.wenscHuix.mitemod.mixin.render.texture;

import com.google.common.collect.Lists;
import net.minecraft.*;
import net.wenscHuix.mitemod.imixin.TextureAtlasSpriteAccessor;
import net.wenscHuix.mitemod.shader.client.ShadersTex;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.IntBuffer;
import java.util.ArrayList;
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

   /**
    * @author
    * @reason
    */
   @SuppressWarnings("unchecked")
   @Overwrite
   public final void loadSprite(Resource par1Resource) throws IOException {
      this.resetSprite();
      InputStream var2 = par1Resource.getInputStream();
      AnimationMetadataSection var3 = (AnimationMetadataSection)par1Resource.getMetadata("animation");
      BufferedImage var4 = ImageIO.read(var2);
      this.height = var4.getHeight();
      this.width = var4.getWidth();
      int[] var5 = new int[this.height * this.width * 3];
      ShadersTex.loadAtlasSprite(var4, 0, 0, this.width, this.height, var5, 0, this.width);
      var4.getRGB(0, 0, this.width, this.height, var5, 0, this.width);
      if (var3 == null) {
         if (this.height != this.width) {
            throw new RuntimeException("broken aspect ratio and not an animation");
         }

         this.framesTextureData.add(var5);
      } else {
         int var6 = this.height / this.width;
         int var7 = this.width;
         int var8 = this.width;
         this.height = this.width;
         int var10;
         if (var3.getFrameCount() > 0) {

             for (Object o : var3.getFrameIndexSet()) {
                 var10 = (Integer) o;
                 if (var10 >= var6) {
                     throw new RuntimeException("invalid frameindex " + var10);
                 }

                 this.allocateFrameTextureData(var10);
                 this.framesTextureData.set(var10, ShadersTex.extractFrame(var5, var7, var8, var10));
             }

            this.animationMetadata = var3;
         } else {
            ArrayList<Object> var11 = Lists.newArrayList();

            for(var10 = 0; var10 < var6; ++var10) {
               this.framesTextureData.add(ShadersTex.extractFrame(var5, var7, var8, var10));
               var11.add(new AnimationFrame(var10, -1));
            }

            this.animationMetadata = new AnimationMetadataSection(var11, this.width, this.height, var3.getFrameTime());
         }
      }

   }

   @Override
   public boolean mITE_Shader_Loader$load(ResourceManager manager, ResourceLocation location) throws IOException {
      this.loadSprite(ShadersTex.loadResource(manager, location));
      return true;
   }
}
