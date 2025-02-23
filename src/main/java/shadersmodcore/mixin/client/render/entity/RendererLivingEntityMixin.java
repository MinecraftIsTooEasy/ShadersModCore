package shadersmodcore.mixin.client.render.entity;

import net.minecraft.*;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import shadersmodcore.client.shader.Shaders;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import shadersmodcore.util.Utils;

@Mixin(RendererLivingEntity.class)
public abstract class RendererLivingEntityMixin extends Render {
   @Shadow protected abstract int getColorMultiplier(EntityLivingBase par1EntityLivingBase, float par2, float par3);

   @Redirect(at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glDisable(I)V", ordinal = 2),
      method = {"renderLivingLabel"}
   )
   private void redirectRenderLivingLabel0(int cap) {
      Shaders.sglDisableT2D(cap);
   }

   @Redirect(at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glEnable(I)V", ordinal = 1),
      method = {"renderLivingLabel"}
   )
   private void redirectRenderLivingLabel1(int cap) {
      Shaders.sglEnableT2D(cap);
   }

   @Inject(method = "doRenderLiving", at = @At("HEAD"))
   private void setEntityHurtFlash(EntityLivingBase par1EntityLivingBase, double par2, double par4, double par6, float par8, float par9, CallbackInfo ci) {
      if (Shaders.useEntityHurtFlash) {
         Shaders.setEntityHurtFlash(par1EntityLivingBase.hurtTime <= 0 && par1EntityLivingBase.deathTime <= 0 ? 0 : 102, this.getColorMultiplier(par1EntityLivingBase, par1EntityLivingBase.getBrightness(par9), par9));
      }
   }

   @Inject(method = "doRenderLiving", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glDepthMask(Z)V", shift = At.Shift.AFTER))
   private void resetEntityHurtFlash(EntityLivingBase par1EntityLivingBase, double par2, double par4, double par6, float par8, float par9, CallbackInfo ci) {
      Shaders.resetEntityHurtFlash();
   }

   @Inject(method = "doRenderLiving", at = @At(value = "INVOKE", target = "Lnet/minecraft/OpenGlHelper;setActiveTexture(I)V", shift = At.Shift.AFTER))
   private void disableLightmap(EntityLivingBase par1EntityLivingBase, double par2, double par4, double par6, float par8, float par9, CallbackInfo ci) {
      Shaders.disableLightmap();
   }

   @Inject(method = "doRenderLiving", at = @At(value = "FIELD", target = "Lnet/minecraft/EntityLivingBase;hurtTime:I"))
   private void beginLivingDamage(EntityLivingBase par1EntityLivingBase, double par2, double par4, double par6, float par8, float par9, CallbackInfo ci) {
      Shaders.beginLivingDamage();
   }

   @Inject(method = "doRenderLiving", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glDepthFunc(I)V", shift = At.Shift.AFTER, ordinal = 3))
   private void endLivingDamage(EntityLivingBase par1EntityLivingBase, double par2, double par4, double par6, float par8, float par9, CallbackInfo ci) {
      Shaders.endLivingDamage();
   }

   //Nametag rendering on mobs (actually no)
   @Inject(method = "passSpecialRender",
           at = @At(value = "INVOKE",
                   target = "Lnet/minecraft/Tessellator;startDrawingQuads()V"))
   protected void passSpecialRender(EntityLivingBase p_77033_1_, double p_77033_2_, double p_77033_4_, double p_77033_6_, CallbackInfo ci) {
      Utils.Fix();
   }
}
