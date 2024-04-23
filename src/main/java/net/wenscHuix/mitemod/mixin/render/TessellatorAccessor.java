package net.wenscHuix.mitemod.mixin.render;

import net.minecraft.bfq;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({bfq.class})
public interface TessellatorAccessor {
   @Accessor("A")
   void setuseVBO(boolean var1);

   @Accessor("A")
   boolean getuseVBO();

   @Accessor("c")
   static boolean gettryVBO() {
      throw new AssertionError();
   }

   @Accessor("C")
   int getVboIndex();

   @Accessor("C")
   void setVboIndex(int var1);
}
