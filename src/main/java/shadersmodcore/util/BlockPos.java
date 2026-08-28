package shadersmodcore.util;

import net.minecraft.EnumFacing;

public record BlockPos(int x, int y, int z) {

    public BlockPos offset(EnumFacing facing, int n) {
        return n == 0 ? this : new BlockPos(this.x + facing.getFrontOffsetX() * n, this.y + facing.getFrontOffsetY() * n, this.z + facing.getFrontOffsetZ() * n);
    }
}
