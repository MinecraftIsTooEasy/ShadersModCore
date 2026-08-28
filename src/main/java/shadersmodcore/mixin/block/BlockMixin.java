package shadersmodcore.mixin.block;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.Block;
import net.minecraft.IBlockAccess;
import org.spongepowered.asm.mixin.injection.At;
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

    @ModifyReturnValue(method = "getBlockBrightness", at = @At("TAIL"))
    private float shaderBlockBrightness(float original, @Local(argsOnly = true) IBlockAccess par1IBlockAccess, @Local(name = "par2") int par2, @Local(name = "par3") int par3, @Local(name = "par4") int par4) {
        if (!Shaders.isShadersLoad()) return original;
        if (par1IBlockAccess.isBlockNormalCube(par2, par3, par4)) return 1.0F;
        return Shaders.blockAoLight;
    }
}
