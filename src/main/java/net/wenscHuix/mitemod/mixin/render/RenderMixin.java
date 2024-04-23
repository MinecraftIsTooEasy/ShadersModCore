package net.wenscHuix.mitemod.mixin.render;

import net.minecraft.Entity;
import net.minecraft.EntityInsentient;
import net.minecraft.MathHelper;
import net.minecraft.World;
import net.minecraft.bfq;
import net.minecraft.bfr;
import net.minecraft.bgl;
import net.minecraft.bgm;
import net.minecraft.bjo;
import net.wenscHuix.mitemod.shader.client.Shaders;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin({bgm.class})
public class RenderMixin {
   @Shadow
   protected bgl b;
   @Shadow
   protected bfr c;
   @Shadow
   protected float d;
   @Shadow
   @Final
   private static bjo a;

   @Overwrite
   private void c(Entity par1Entity, double par2, double par4, double par6, float par8, float par9) {
      if (!Shaders.shouldSkipDefaultShadow) {
         par4 -= (double)par1Entity.yOffset;
         GL11.glEnable(3042);
         GL11.glBlendFunc(770, 771);
         this.b.e.a(a);
         GL11.glDepthMask(false);
         float var11 = this.d;
         if (par1Entity instanceof EntityInsentient) {
            EntityInsentient var12 = (EntityInsentient)par1Entity;
            var11 *= var12.bt();
            if (var12.isChild()) {
               var11 *= 0.5F;
            }
         }

         double var35 = par1Entity.lastTickPosX + (par1Entity.posX - par1Entity.lastTickPosX) * (double)par9;
         double var14 = par1Entity.lastTickPosY + (par1Entity.posY - par1Entity.lastTickPosY) * (double)par9 + (double)par1Entity.S();
         var14 -= (double)par1Entity.yOffset;
         double var16 = par1Entity.lastTickPosZ + (par1Entity.posZ - par1Entity.lastTickPosZ) * (double)par9;
         int var18 = MathHelper.floor_double(var35 - (double)var11);
         int var19 = MathHelper.floor_double(var35 + (double)var11);
         int var22 = MathHelper.floor_double(var16 - (double)var11);
         int var23 = MathHelper.floor_double(var16 + (double)var11);
         double var24 = par2 - var35;
         double var26 = par4 - var14;
         double var28 = par6 - var16;
         bfq var30 = bfq.a;
         var30.b();
         float shadow_size = par1Entity.S();
         float object_opacity = this.b.a(par1Entity).getModelOpacity(par1Entity);
         GL11.glAlphaFunc(516, 0.001F);

         for(int x = var18; x <= var19; ++x) {
            for(int z = var22; z <= var23; ++z) {
               this.renderShadowOnBlockMITE(par2, par4 + (double)shadow_size, par6, x, z, par8, var11, var24, var26 + (double)shadow_size, var28, object_opacity, par1Entity);
            }
         }

         var30.a();
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         GL11.glDisable(3042);
         GL11.glDepthMask(true);
         GL11.glAlphaFunc(516, 0.1F);
      }
   }

   @Shadow
   private void renderShadowOnBlockMITE(double par2, double par4, double par6, int block_x, int block_z, float par11, float par12, double par13, double par15, double par17, float opacity_of_object, Entity entity) {
   }

   @Shadow
   private World b() {
      return null;
   }
}
