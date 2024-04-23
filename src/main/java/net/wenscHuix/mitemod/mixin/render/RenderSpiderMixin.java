package net.wenscHuix.mitemod.mixin.render;

import net.minecraft.EntityInsentient;
import net.minecraft.ModelArachnid;
import net.minecraft.RenderArachnid;
import net.minecraft.bhp;
import net.wenscHuix.mitemod.shader.client.Shaders;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin({bhp.class})
public class RenderSpiderMixin extends RenderArachnid {
   public RenderSpiderMixin(ModelArachnid base_model, ModelArachnid render_pass_model, float scale) {
      super(base_model, render_pass_model, scale);
   }

   public void a(EntityInsentient par1EntityLiving, double par2, double par4, double par6, float par8, float par9) {
      super.a(par1EntityLiving, par2, par4, par6, par8, par9);
      Shaders.beginMobEye();
   }

   @Shadow
   public String getSubtypeName() {
      return null;
   }
}
