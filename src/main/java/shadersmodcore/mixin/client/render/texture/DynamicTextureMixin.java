package shadersmodcore.mixin.client.render.texture;

import net.minecraft.AbstractTexture;
import net.minecraft.DynamicTexture;
import shadersmodcore.client.shader.ShadersTex;
import net.xiaoyu233.fml.util.ReflectHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DynamicTexture.class)
public abstract class DynamicTextureMixin extends AbstractTexture {

   @Shadow public abstract int[] getTextureData();

   @Shadow @Final private int[] dynamicTextureData;

   @Shadow @Final private int width;

   @Shadow @Final private int height;

   @ModifyVariable(method = "<init>(II)V", argsOnly = true, index = 1,
           at = @At(value = "FIELD", shift = Shift.AFTER, ordinal = 1))
   private int injectInit(int i) {
      return i * 3;
   }

   @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/TextureUtil;allocateTexture(III)V"),
           method = {"<init>(II)V"})
   private void redirectInit(int id, int par1, int par2) {
      ShadersTex.initDynamicTexture(id, par1 / 3, par2, ReflectHelper.dyCast(this));
   }

   @Inject(method = "updateDynamicTexture", at = @At("HEAD"), cancellable = true)
   public void updateDynamicTexture(CallbackInfo info) {
      ShadersTex.updateDynamicTexture(this.getGlTextureId(), this.dynamicTextureData, this.width, this.height, ReflectHelper.dyCast(this));
      info.cancel();
   }
}
