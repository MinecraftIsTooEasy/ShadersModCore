package shadersmodcore.mixin.client.render;

import net.minecraft.RenderingScheme;
import net.minecraft.Tessellator;
import net.minecraft.TessellatorMITE;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import shadersmodcore.util.TessellatorExtra;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin({RenderingScheme.class})
public class RenderingSchemeMixin {
   @Shadow public static int current;

   @Inject(method = "setCurrent", at = @At("TAIL"))
   private static void modifyTessellator(int scheme_index, CallbackInfo ci) {
      current = 0;
      Tessellator.instance = current == 0 ? new TessellatorExtra(2097152) : new TessellatorMITE();
   }
}
