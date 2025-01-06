package shadersmodcore.mixin.client.render;

import net.minecraft.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import shadersmodcore.client.shader.Shaders;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin({Render.class})
public class RenderMixin {

   @Shadow protected float shadowSize;
   @Shadow @Final private static ResourceLocation shadowTextures;
   @Shadow protected RenderManager renderManager;

//   /**
//    * @author
//    * @reason
//    */
//   @Overwrite
//   private void renderShadow(Entity par1Entity, double par2, double par4, double par6, float par8, float par9) {
//      if (!Shaders.shouldSkipDefaultShadow) {
//         par4 -= par1Entity.yOffset;
//         GL11.glEnable(3042);
//         GL11.glBlendFunc(770, 771);
//         this.renderManager.renderEngine.bindTexture(shadowTextures);
//         GL11.glDepthMask(false);
//         float var11 = this.shadowSize;
//         if (par1Entity instanceof EntityLiving var12) {
//             var11 *= var12.getRenderSizeModifier();
//            if (var12.isChild()) {
//               var11 *= 0.5F;
//            }
//         }
//
//         double var35 = par1Entity.lastTickPosX + (par1Entity.posX - par1Entity.lastTickPosX) * (double)par9;
//         double var14 = par1Entity.lastTickPosY + (par1Entity.posY - par1Entity.lastTickPosY) * (double)par9 + par1Entity.getShadowSize();
//         var14 -= par1Entity.yOffset;
//         double var16 = par1Entity.lastTickPosZ + (par1Entity.posZ - par1Entity.lastTickPosZ) * (double)par9;
//         int var18 = MathHelper.floor_double(var35 - (double)var11);
//         int var19 = MathHelper.floor_double(var35 + (double)var11);
//         int var22 = MathHelper.floor_double(var16 - (double)var11);
//         int var23 = MathHelper.floor_double(var16 + (double)var11);
//         double var24 = par2 - var35;
//         double var26 = par4 - var14;
//         double var28 = par6 - var16;
//         Tessellator var30 = Tessellator.instance;
//         var30.startDrawingQuads();
//         float shadow_size = par1Entity.getShadowSize();
//         float object_opacity = this.renderManager.getEntityRenderObject(par1Entity).getModelOpacity(par1Entity);
//         GL11.glAlphaFunc(516, 0.001F);
//
//         for(int x = var18; x <= var19; ++x) {
//            for(int z = var22; z <= var23; ++z) {
//               this.renderShadowOnBlockMITE(par2, par4 + (double)shadow_size, par6, x, z, par8, var11, var24, var26 + (double)shadow_size, var28, object_opacity, par1Entity);
//            }
//         }
//
//         var30.draw();
//         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
//         GL11.glDisable(3042);
//         GL11.glDepthMask(true);
//         GL11.glAlphaFunc(516, 0.1F);
//      }
//   }

   @Redirect(method = "renderShadow", at = @At(value = "FIELD", target = "Lnet/minecraft/Entity;disable_shadow:Z"))
   private boolean shouldSkipDefaultShadow(Entity instance) {
      return Shaders.shouldSkipDefaultShadow;
   }

   @Shadow
   private void renderShadowOnBlockMITE(double par2, double par4, double par6, int block_x, int block_z, float par11, float par12, double par13, double par15, double par17, float opacity_of_object, Entity entity) {
   }
}
