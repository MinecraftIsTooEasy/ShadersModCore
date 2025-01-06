package shadersmodcore.mixin.client.render;

import java.util.Map;

import net.minecraft.FontRenderer;
import net.minecraft.RenderManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin({RenderManager.class})
public class RenderManagerMixin {

   @Shadow private FontRenderer fontRenderer;

   @Shadow private Map entityRenderMap;

   public FontRenderer getFontRenderer() {
      return this.fontRenderer;
   }

   public Map getEntityRenderMap() {
      return this.entityRenderMap;
   }
}
