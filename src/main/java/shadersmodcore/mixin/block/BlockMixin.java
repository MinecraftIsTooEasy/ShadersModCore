package shadersmodcore.mixin.block;

import net.minecraft.Block;
import net.minecraft.IBlockAccess;
import shadersmodcore.api.BlockAccessor;
import shadersmodcore.client.shader.Shaders;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin({Block.class})
public class BlockMixin implements BlockAccessor {
   @Shadow @Final private boolean is_tree_leaves;
   @Shadow @Final public static int[] lightValue;
   @Shadow @Final public int blockID;

   public int getLightValue() {
      return lightValue[this.blockID];
   }

   /**
    * @author
    * @reason
    */
   @Final
   @Overwrite
   public float getBlockBrightness(IBlockAccess par1IBlockAccess, int par2, int par3, int par4) {
      return par1IBlockAccess.isBlockNormalCube(par2, par3, par4) ? Shaders.blockAoLight : 1.0F;
   }
}
