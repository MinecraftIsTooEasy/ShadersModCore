package shadersmodcore.mixin.client.render.texture;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.*;
import net.xiaoyu233.fml.util.ReflectHelper;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import shadersmodcore.api.AbstractTextureAccessor;
import shadersmodcore.api.TextureMapAccessor;
import shadersmodcore.client.shader.MultiTexID;
import shadersmodcore.client.shader.Shaders;
import shadersmodcore.client.shader.ShadersTex;
import shadersmodcore.client.shader.SmartAnimations;
import shadersmodcore.util.TextureUtilExtra;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Mixin(value = TextureMap.class, priority = 999)
public abstract class TextureMapMixin extends AbstractTexture implements TextureMapAccessor {
    @Shadow @Final private List listAnimatedSprites;
    @Shadow @Final private Map mapRegisteredSprites;
    @Shadow @Final private Map mapUploadedSprites;
    @Shadow @Final private String basePath;
    @Shadow @Final private TextureAtlasSprite missingImage;

    @Unique public int atlasWidth;
    @Unique public int atlasHeight;

    public int getAtlasWidth() {
        return this.atlasWidth;
    }

    public int getAtlasHeight() {
        return this.atlasHeight;
    }

    public void setAtlasWidth(int atlasWidth) {
        this.atlasWidth = atlasWidth;
    }

    public void setAtlasHeight(int atlasHeight) {
        this.atlasHeight = atlasHeight;
    }

    @WrapOperation(method = "loadTextureAtlas", at = @At(value = "INVOKE", target = "Lnet/minecraft/ResourceManager;getResource(Lnet/minecraft/ResourceLocation;)Lnet/minecraft/Resource;"))
    private Resource loadAtlasResource(ResourceManager instance, ResourceLocation resourceLocation, Operation<Resource> original) throws IOException {
        return ShadersTex.loadResource(instance, resourceLocation);
    }

    @WrapOperation(method = "loadTextureAtlas", at = @At(value = "INVOKE", target = "Lnet/minecraft/TextureUtil;allocateTexture(III)V"))
    private void setupAtlasTexture(int glTextureId, int width, int height, Operation<Void> original, @Local(name = "var3") Stitcher stitcher) {
        ShadersTex.setupTextureMap(width, height, stitcher, ReflectHelper.dyCast(this));
    }

    @WrapOperation(method = "loadTextureAtlas", at = @At(value = "INVOKE", target = "Lnet/minecraft/TextureUtil;uploadTextureSub([IIIIIZZ)V"))
    private void uploadAtlasTexture(int[] is, int i, int j, int k, int l, boolean bl, boolean bl2, Operation<Void> original) {
        ShadersTex.updateTextureMap(is, i, j, k, l, bl, bl2);
    }

    // TextureManager reaches atlas animation through tick; the wrapper keeps the update context exception-safe.
    @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/TextureMap;updateAnimations()V"))
    private void tickAnimations(TextureMap instance, Operation<Void> original) {
        MultiTexID previousUpdatingTex = ShadersTex.updatingTex;
        MultiTexID currentUpdatingTex = ((AbstractTextureAccessor) this).getMultiTexID();
        if (!SmartAnimations.shouldAnimateTexture(currentUpdatingTex.base, Shaders.isShadowPass)) {
            return;
        }

        ShadersTex.updatingTex = currentUpdatingTex;
        try {
            original.call(instance);
        } finally {
            ShadersTex.updatingTex = previousUpdatingTex;
        }
    }

    @Redirect(method = "updateAnimations", at = @At(value = "INVOKE", target = "Lnet/minecraft/TextureUtil;bindTexture(I)V"))
    private void bindTexture(int i) {
        TextureUtilExtra.bindTexture(this.getGlTextureId());
    }
}
