package shadersmodcore.mixin.client.render.texture;

import java.util.List;

import net.minecraft.LayeredTexture;
import net.minecraft.ResourceManager;
import shadersmodcore.client.shader.ShadersTex;
import net.xiaoyu233.fml.util.ReflectHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
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
