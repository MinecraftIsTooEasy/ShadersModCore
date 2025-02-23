package shadersmodcore.mixin.client.render.entity;

import net.minecraft.EntityEnderman;
import net.minecraft.ModelBase;
import net.minecraft.RenderEnderman;
import net.minecraft.RenderLiving;
import shadersmodcore.client.shader.Shaders;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderEnderman.class)
public abstract class RenderEndermanMixin extends RenderLiving {
   public RenderEndermanMixin(ModelBase par1ModelBase, float par2) {
      super(par1ModelBase, par2);
   }

   @Inject(method = "renderEnderman", at = @At(value = "INVOKE", target = "Lnet/minecraft/RenderLiving;doRenderLiving(Lnet/minecraft/EntityLiving;DDDFF)V"))
   public void renderEnderman(EntityEnderman par1EntityEnderman, double par2, double par4, double par6, float par8, float par9, CallbackInfo ci) {
      Shaders.beginMobEye();
   }
}
