package net.wenscHuix.mitemod.mixin.block;

import net.minecraft.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({Block.class})
public interface BlockAccessor {
   @Accessor("is_normal_cube_lookup")
   static boolean[] getis_normal_cube_lookup() {
      throw new AssertionError();
   }
}
