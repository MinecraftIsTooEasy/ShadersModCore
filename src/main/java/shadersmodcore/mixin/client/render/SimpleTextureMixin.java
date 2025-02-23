package shadersmodcore.mixin.client.render;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.*;
import org.spongepowered.asm.mixin.injection.At;
import shadersmodcore.api.AbstractTextureAccessor;
import shadersmodcore.client.shader.ShadersTex;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.awt.image.BufferedImage;

@Mixin({SimpleTexture.class})
public abstract class SimpleTextureMixin extends AbstractTexture {
   @Shadow @Final private ResourceLocation textureLocation;

    @WrapWithCondition(method = "loadTexture", at = @At(value = "INVOKE", target = "Lnet/minecraft/TextureUtil;uploadTextureImageAllocate(ILjava/awt/image/BufferedImage;ZZ)I"))
    private boolean loadSimpleTexture(int i, BufferedImage bufferedImage, boolean bl, boolean bl2) {
       ShadersTex.loadSimpleTexture(this.getGlTextureId(), bufferedImage, bl, bl2, Minecraft.getMinecraft().getResourceManager(), this.textureLocation,
                ((AbstractTextureAccessor) this).getMultiTexID());
        return true;
    }

   @Shadow protected abstract void rB(byte[] bytes);
}
