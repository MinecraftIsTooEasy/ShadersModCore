package shadersmodcore.mixin.client.render.texture;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.ResourceLocation;
import net.minecraft.ResourceManager;
import net.minecraft.AbstractTexture;
import net.minecraft.TextureManager;
import net.minecraft.TextureObject;
import net.minecraft.TextureUtil;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import shadersmodcore.client.shader.ShadersTex;
import shadersmodcore.client.shader.SmartAnimations;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;
import java.util.Map;

@Mixin({TextureManager.class})
public abstract class TextureManagerMixin {
    @Shadow @Final private Map mapTextureObjects;
    @Shadow @Final private List listTickables;

    // MITE has exactly two map writes: the IOException fallback (ordinal 0) and the common tail (ordinal 1).
    @WrapOperation(method = "loadTexture", require = 1, expect = 1, at = @At(value = "INVOKE", ordinal = 0,
        target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"))
    private Object retainTextureOnLoadFailure(Map map, Object key, Object value,
                                              Operation<Object> original) {
        TextureObject previous = (TextureObject) map.get(key);
        if (value == TextureUtil.missingTexture && previous != null
            && previous != TextureUtil.missingTexture) {
            return previous;
        }

        return original.call(map, key, value);
    }

    @WrapOperation(method = "loadTexture", require = 1, expect = 1, at = @At(value = "INVOKE", ordinal = 1,
        target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"))
    private Object finishTextureLoad(Map map, Object key, Object value,
                                     Operation<Object> original,
                                     @Local(index = 3) boolean loaded) {
        TextureObject previous = (TextureObject) map.get(key);
        if (!loaded && value == TextureUtil.missingTexture && previous != null
            && previous != TextureUtil.missingTexture) {
            return previous;
        }

        Object result = original.call(map, key, value);
        if (loaded && previous != null && previous != value) {
            finishTextureReplacement(map, this.listTickables, (ResourceLocation) key,
                previous, (TextureObject) value, TextureUtil.missingTexture, true);
        }
        return result;
    }

    @Unique
    static void finishTextureReplacement(Map map, List list, ResourceLocation location,
                                         TextureObject previous, TextureObject replacement,
                                         boolean loaded) {
        finishTextureReplacement(map, list, location, previous, replacement,
            TextureUtil.missingTexture, loaded);
    }

    @Unique
    static void finishTextureReplacement(Map map, List list, ResourceLocation location,
                                         TextureObject previous, TextureObject replacement,
                                         TextureObject missingTexture, boolean loaded) {
        if (previous == null || previous == replacement || previous == missingTexture) {
            return;
        }

        if (!loaded) {
            if (map.get(location) == replacement) {
                map.put(location, previous);
            }
            return;
        }

        if (containsIdentity(map, previous)) {
            return;
        }

        removeIdentity(list, previous);
        if (previous instanceof AbstractTexture) {
            ShadersTex.deleteTextures((AbstractTexture) previous);
        }
    }

    @Unique
    private static boolean containsIdentity(Map map, Object value) {
        for (Object candidate : map.values()) {
            if (candidate == value) {
                return true;
            }
        }
        return false;
    }

    @Unique
    private static void removeIdentity(List list, Object value) {
        for (int index = list.size() - 1; index >= 0; --index) {
            if (list.get(index) == value) {
                list.remove(index);
            }
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
