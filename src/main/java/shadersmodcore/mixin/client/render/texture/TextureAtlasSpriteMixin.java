package shadersmodcore.mixin.client.render.texture;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
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
    @Shadow private AnimationMetadataSection animationMetadata;
    @Shadow protected List framesTextureData;
    @Shadow protected boolean rotated;
    @Shadow protected int originX;
    @Shadow protected int originY;
    @Shadow protected int width;
    @Shadow protected int height;
    @Shadow protected int frameCounter;
    @Shadow protected int tickCounter;
    @Shadow public abstract void loadSprite(Resource par1Resource) throws IOException;
    @Shadow private float minU;
    @Shadow private float maxU;
    @Shadow private float minV;
    @Shadow private float maxV;

    @Redirect(method = "updateAnimation", at = @At(value = "INVOKE", target = "Lnet/minecraft/TextureUtil;uploadTextureSub([IIIIIZZ)V"))
    private void redirectUpdateAnimation(int[] pixels, int width, int height, int originX, int originY, boolean linear, boolean clamp) {
        ShadersTex.updateSubImage(pixels, width, height, originX, originY, linear, clamp);
    }

    @WrapOperation(method = "loadSprite", at = @At(value = "INVOKE",
        target = "Ljavax/imageio/ImageIO;read(Ljava/io/InputStream;)Ljava/awt/image/BufferedImage;"))
    private BufferedImage closeSpriteResource(InputStream input, Operation<BufferedImage> original)
        throws IOException {
        return readImageAndClose(input, original);
    }

    static BufferedImage readImageAndClose(InputStream input, Operation<BufferedImage> original)
        throws IOException {
        try (InputStream stream = input) {
            return original.call(stream);
        }
    }

    @WrapOperation(method = "loadSprite", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/Resource;getMetadata(Ljava/lang/String;)Lnet/minecraft/MetadataSection;"))
    private MetadataSection closeSpriteResourceOnMetadataFailure(Resource resource, String section,
                                                                  Operation<MetadataSection> original,
                                                                  @Local(index = 2) InputStream input) {
        return readMetadataAndCloseOnFailure(resource, section, input, original);
    }

    static MetadataSection readMetadataAndCloseOnFailure(Resource resource, String section, InputStream input,
                                                         Operation<MetadataSection> original) {
        try {
            return original.call(resource, section);
        } catch (RuntimeException | Error failure) {
            if (input != null) {
                try {
                    input.close();
                } catch (Throwable closeFailure) {
                    failure.addSuppressed(closeFailure);
                }
            }
            throw failure;
        }
    }

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

    @Inject(method = "initSprite", at = @At("TAIL"))
    private void shaderInitSprite(int par1, int par2, int par3, int par4, boolean par5, CallbackInfo ci) {
        this.minU = (float) par3 / (float) ((double) par1);
        this.maxU = (float) (par3 + this.width) / (float) ((double) par1);
        this.minV = (float) par4 / (float) par2;
        this.maxV = (float) (par4 + this.height) / (float) par2;
    }

    @Override
    public boolean load(ResourceManager manager, ResourceLocation location) throws IOException {
        this.loadSprite(ShadersTex.loadResource(manager, location));
        return true;
    }
}
