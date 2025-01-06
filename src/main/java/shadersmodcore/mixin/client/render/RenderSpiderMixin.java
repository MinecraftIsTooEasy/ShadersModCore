package shadersmodcore.mixin.client.render;

import net.minecraft.EntityLiving;
import net.minecraft.ModelArachnid;
import net.minecraft.RenderArachnid;
import net.minecraft.RenderSpider;
import shadersmodcore.client.shader.Shaders;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin({RenderSpider.class})
public class RenderSpiderMixin extends RenderArachnid {
   public RenderSpiderMixin(ModelArachnid base_model, ModelArachnid render_pass_model, float scale) {
      super(base_model, render_pass_model, scale);
   }

   public void doRenderLiving(EntityLiving par1EntityLiving, double par2, double par4, double par6, float par8, float par9) {
      super.doRenderLiving(par1EntityLiving, par2, par4, par6, par8, par9);
      Shaders.beginMobEye();
   }

   @Shadow
   public String getSubtypeName() {
      return null;
   }
}
