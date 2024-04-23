package net.wenscHuix.mitemod.mixin.particle;

import net.minecraft.AxisAlignedBB;
import net.minecraft.EffectRenderer;
import net.wenscHuix.mitemod.optimize.gui.Config;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({EffectRenderer.class})
public class EffectRendererMixin {

   @Inject(method = "addBlockDestroyEffects(IIIIII)V", at = @At("HEAD"), cancellable = true)
   public void addBlockDestroyEffects(int x, int y, int z, int block_id, int metadata, int aux_data, CallbackInfo info) {
      if (Config.blockDestroyEffects) {
         this.addBlockDestroyEffects(x, y, z, block_id, metadata, aux_data, (AxisAlignedBB)null);
      }
      info.cancel();
   }

   @Shadow
   public void addBlockDestroyEffects(int x, int y, int z, int block_id, int metadata, int aux_data, AxisAlignedBB bounds_of_exclusion) {}
}
