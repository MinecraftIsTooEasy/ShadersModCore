package shadersmodcore.mixin.client.render.texture;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.ResourceLocation;
import net.minecraft.ResourceManager;
import net.minecraft.AbstractTexture;
import net.minecraft.TextureManager;
import net.minecraft.TextureObject;
import net.minecraft.TextureUtil;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import shadersmodcore.client.shader.ShadersTex;
import shadersmodcore.client.shader.SmartAnimations;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;

@Mixin({TextureManager.class})
public abstract class TextureManagerMixin {
    @Shadow @Final private Map mapTextureObjects;
    @Shadow @Final private java.util.List listTickables;

    @Inject(method = "loadTexture", at = @At("HEAD"))
    private void releaseReplacedTexture(ResourceLocation location, TextureObject texture,
                                        CallbackInfoReturnable<Boolean> cir) {
        TextureObject previous = (TextureObject) this.mapTextureObjects.get(location);
        if (previous == null || previous == texture || previous == TextureUtil.missingTexture) {
            return;
        }

        this.mapTextureObjects.remove(location);
        if (this.mapTextureObjects.containsValue(previous)) {
            return;
        }

        while (this.listTickables.remove(previous)) {
        }
        if (previous instanceof AbstractTexture) {
            ShadersTex.deleteTextures((AbstractTexture) previous);
        }
    }

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
