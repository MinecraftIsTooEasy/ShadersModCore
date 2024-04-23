package net.wenscHuix.mitemod.mixin.render;

import java.util.Map;
import net.minecraft.avi;
import net.minecraft.bgl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin({bgl.class})
public class RenderManagerMixin {
   @Shadow
   private avi r;
   @Shadow
   private Map q;

   public avi getFontRenderer() {
      return this.r;
   }

   public Map getEntityRenderMap() {
      return this.q;
   }
}
