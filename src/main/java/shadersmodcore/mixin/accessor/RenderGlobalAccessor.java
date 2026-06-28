package shadersmodcore.mixin.accessor;

import net.minecraft.RenderGlobal;
import net.minecraft.WorldClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RenderGlobal.class)
public interface RenderGlobalAccessor {
    @Accessor("theWorld")
    WorldClient getClientWorld();
}
