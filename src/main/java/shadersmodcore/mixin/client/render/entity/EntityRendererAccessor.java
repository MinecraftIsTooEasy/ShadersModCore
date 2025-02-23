package shadersmodcore.mixin.client.render.entity;

import net.minecraft.EntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({EntityRenderer.class})
public interface EntityRendererAccessor {
   @Accessor("anaglyphField")
   static void setAnaglyphField(int val) {
      throw new AssertionError();
   }

   @Accessor("anaglyphField")
   static int getAnaglyphField() {
      throw new AssertionError();
   }
}
