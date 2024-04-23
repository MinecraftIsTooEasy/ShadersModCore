package net.wenscHuix.mitemod.shader.util;

import net.minecraft.EnumFacing;

public class BlockPos {
   public int x;
   public int y;
   public int z;

   public BlockPos(int x, int y, int z) {
      this.x = x;
      this.y = y;
      this.z = z;
   }

   public BlockPos(double x, double y, double z) {
      this.x = (int)x;
      this.y = (int)y;
      this.z = (int)z;
   }

   public BlockPos offset(EnumFacing facing, int n) {
      return n == 0 ? this : new BlockPos(this.x + facing.getFrontOffsetX() * n, this.y + facing.getFrontOffsetY() * n, this.z + facing.getFrontOffsetZ() * n);
   }
}
