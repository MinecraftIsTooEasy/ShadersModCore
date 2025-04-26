package shadersmodcore.mixin.world;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import shadersmodcore.api.WorldAccessor;
import shadersmodcore.client.dynamicLight.DynamicLights;
import shadersmodcore.config.ShaderConfig;
import shadersmodcore.util.BlockPos;

@Mixin(World.class)
public abstract class WorldMixin implements WorldAccessor {
   @ModifyReturnValue(method = "getLightBrightnessForSkyBlocks", at = @At("TAIL"))
   private int modifyCout(int original, @Local(argsOnly = true, ordinal = 0) int par1, @Local(argsOnly = true, ordinal = 1) int par2, @Local(argsOnly = true, ordinal = 2) int par3) {
      if (ShaderConfig.isDynamicLights()) {
         return DynamicLights.getCombinedLight(new BlockPos(par1, par2, par3), original);
      }
      return original;
   }

   @Shadow @Final public Block getBlock(int x, int y, int z) {
      return null;
   }
   @Shadow protected WorldInfo worldInfo;

   @Override
   public long getWorldTime() {
      return this.worldInfo.getWorldTotalTime(0);
   }
}
