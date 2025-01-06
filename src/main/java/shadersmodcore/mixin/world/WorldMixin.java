package shadersmodcore.mixin.world;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.Block;
import net.minecraft.EnumSkyBlock;
import net.minecraft.Minecraft;
import net.minecraft.World;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import shadersmodcore.client.dynamicLight.DynamicLights;
import shadersmodcore.config.ShaderConfig;
import shadersmodcore.util.BlockPos;

@Mixin(World.class)
public abstract class WorldMixin {
   @Shadow
   public abstract int getSkyBlockTypeBrightness(EnumSkyBlock par1EnumSkyBlock, int par2, int par3, int par4);

   @Shadow
   public abstract Block getBlock(int[] var1);

//   /**
//    * @author
//    * @reason
//    */
//   @Overwrite
//   public final int getLightBrightnessForSkyBlocks(int par1, int par2, int par3, int par4) {
//      int var5 = this.getSkyBlockTypeBrightness(EnumSkyBlock.Sky, par1, par2, par3);
//      int var6 = this.getSkyBlockTypeBrightness(EnumSkyBlock.Block, par1, par2, par3);
//      if (var6 < par4) {
//         var6 = par4;
//      }
//
//      int cout = var5 << 20 | var6 << 4;
//      if (ShaderConfig.isDynamicLights()) {
//         cout = DynamicLights.getCombinedLight(new BlockPos(par1, par2, par3), cout);
//      }
//
//      return cout;
//   }

   @ModifyReturnValue(method = "getLightBrightnessForSkyBlocks", at = @At("TAIL"))
   private int modifyCout(int original, @Local(argsOnly = true, ordinal = 0) int par1, @Local(argsOnly = true, ordinal = 1) int par2, @Local(argsOnly = true, ordinal = 2) int par3) {
      if (ShaderConfig.isDynamicLights()) {
         return DynamicLights.getCombinedLight(new BlockPos(par1, par2, par3), original);
      }
      return original;
   }

   @Final
   public Block getBlock(BlockPos pos) {
      return this.getBlock(pos.x, pos.y, pos.z);
   }

   @Shadow
   @Final
   public Block getBlock(int x, int y, int z) {
      return null;
   }
}
