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
   @Shadow
   public static int current;

//   /**
//    * @author
//    * @reason
//    */
//   @Overwrite
//   public static void setCurrent(int scheme_index) {
//      if (getSchemeDescriptor(scheme_index) == null) {
//         if (Minecraft.theMinecraft != null) {
//            Minecraft.theMinecraft.getLogAgent().logWarning("Invalid rendering scheme (" + scheme_index + "), reverting to " + getSchemeDescriptor(1) + " (" + 1 + ")");
//         }
//
//         scheme_index = 1;
//      } else {
//         Minecraft.theMinecraft.getLogAgent().logInfo("Rendering scheme: " + getSchemeDescriptor(scheme_index));
//      }
//
//      current = 0;
//      Tessellator.instance = current == 0 ? new TessellatorExtra(2097152) : new TessellatorMITE();
//   }

   @Inject(method = "setCurrent", at = @At("TAIL"))
   private static void modifyTessellator(int scheme_index, CallbackInfo ci) {
      current = 0;
      Tessellator.instance = current == 0 ? new TessellatorExtra(2097152) : new TessellatorMITE();
   }

   @Shadow
   public static String getSchemeDescriptor(int scheme_index) {
      return null;
   }
}
