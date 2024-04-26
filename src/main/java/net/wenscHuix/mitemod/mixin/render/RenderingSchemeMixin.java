package net.wenscHuix.mitemod.mixin.render;

import net.minecraft.Minecraft;
import net.minecraft.RenderingScheme;
import net.minecraft.Tessellator;
import net.minecraft.TessellatorMITE;
import net.wenscHuix.mitemod.shader.util.TessellatorExtra;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin({RenderingScheme.class})
public class RenderingSchemeMixin {
   @Shadow
   public static int current;

   /**
    * @author
    * @reason
    */
   @Overwrite
   public static void setCurrent(int scheme_index) {
      if (getSchemeDescriptor(scheme_index) == null) {
         if (Minecraft.theMinecraft != null) {
            Minecraft.theMinecraft.getLogAgent().logWarning("Invalid rendering scheme (" + scheme_index + "), reverting to " + getSchemeDescriptor(1) + " (" + 1 + ")");
         }

         boolean scheme_index1 = true;
      } else {
         Minecraft.theMinecraft.getLogAgent().logInfo("Rendering scheme: " + getSchemeDescriptor(scheme_index));
      }

      current = 0;
      Tessellator.instance = (Tessellator)(current == 0 ? new TessellatorExtra(2097152) : new TessellatorMITE());
   }

   @Shadow
   public static String getSchemeDescriptor(int scheme_index) {
      return null;
   }
}
