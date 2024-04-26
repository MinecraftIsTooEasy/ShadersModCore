package net.wenscHuix.mitemod.mixin.render;

import net.minecraft.Tessellator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({Tessellator.class})
public interface TessellatorAccessor {
   @Accessor("useVBO")
   void setUseVBO(boolean var1);

   @Accessor("useVBO")
   boolean getUseVBO();

   @Accessor("tryVBO")
   static boolean getTryVBO() {
      throw new AssertionError();
   }

   @Accessor("vboIndex")
   int getVboIndex();

   @Accessor("vboIndex")
   void setVboIndex(int var1);
}
