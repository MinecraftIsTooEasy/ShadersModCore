package net.wenscHuix.mitemod.mixin.render.texture;

import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Target;
import java.util.Iterator;
import java.util.List;
import javax.imageio.ImageIO;

import net.minecraft.AbstractTexture;
import net.minecraft.LayeredTexture;
import net.minecraft.ResourceLocation;
import net.minecraft.ResourceManager;
import net.wenscHuix.mitemod.shader.client.ShadersTex;
import net.xiaoyu233.fml.util.ReflectHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({LayeredTexture.class})
public class LayeredTextureMixin {
   @Shadow
   @Final
   public List layeredTextureNames;

   @Inject(method = "loadTexture", at = @At(value = "INVOKE", target = "Lnet/minecraft/TextureUtil;uploadTextureImage(ILjava/awt/image/BufferedImage;)I"), cancellable = true)
   public void loadTexture(ResourceManager resourceManager, CallbackInfo info) {
      ShadersTex.loadLayeredTexture(ReflectHelper.dyCast(this), resourceManager, this.layeredTextureNames);
      info.cancel();
   }
}
