package net.wenscHuix.mitemod.mixin.render;

import net.minecraft.bia;
import net.minecraft.bic;
import net.minecraft.bjp;
import net.wenscHuix.mitemod.shader.client.MultiTexID;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin({bic.class})
public class ThreadDownloadImageDataMixin extends bia {
   @Shadow
   private boolean g;

   public MultiTexID getMultiTexID() {
      if (!this.g) {
         this.b();
      }

      return super.getMultiTexID();
   }

   @Shadow
   public void a(bjp bjp) {
   }

   @Shadow
   public int b() {
      return 0;
   }
}
