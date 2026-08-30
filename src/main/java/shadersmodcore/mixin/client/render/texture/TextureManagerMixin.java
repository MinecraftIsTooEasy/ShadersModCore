package shadersmodcore.mixin.client.render.texture;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.ResourceLocation;
import net.minecraft.ResourceManager;
import net.minecraft.TextureManager;
import net.minecraft.TextureObject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import shadersmodcore.client.shader.ShadersTex;
import shadersmodcore.client.shader.SmartAnimations;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;

@Mixin({TextureManager.class})
public abstract class TextureManagerMixin {

    @WrapWithCondition(method = "bindTexture", at = @At(value = "INVOKE", target = "Lnet/minecraft/TextureUtil;bindTexture(I)V"))
    private boolean bindTexture(int i, @Local Object var2) {
        ShadersTex.bindTexture((TextureObject) var2);
        return true;
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void resetSmartAnimationTextures(CallbackInfo ci) {
        SmartAnimations.resetTexturesRendered();
    }

    @Inject(method = "onResourceManagerReload", at = @At("HEAD"))
    private void resetSmartAnimationsOnReload(ResourceManager resourceManager, CallbackInfo ci) {
        SmartAnimations.reset();
    }
}
