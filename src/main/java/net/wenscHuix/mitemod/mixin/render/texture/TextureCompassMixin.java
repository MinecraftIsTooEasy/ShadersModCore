package net.wenscHuix.mitemod.mixin.render.texture;

import net.minecraft.TextureAtlasSprite;
import net.minecraft.TextureCompass;
import net.wenscHuix.mitemod.shader.client.ShadersTex;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({TextureCompass.class})
public class TextureCompassMixin extends TextureAtlasSprite {
   protected TextureCompassMixin(String par1Str) {
      super(par1Str);
   }

   @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/TextureUtil;uploadTextureSub([IIIIIZZ)V"),
           method = {"updateCompass"})
   private void redirectUpdateCompass(int[] var0, int var1, int var2, int var3, int var4, boolean var5, boolean var6) {
      ShadersTex.updateSubImage((int[]) this.framesTextureData.get(this.frameCounter), this.width, this.height, this.originX, this.originY, false, false);
   }
}
