package shadersmodcore.mixin.client.render;

import net.minecraft.AbstractTexture;
import net.minecraft.ThreadDownloadImageData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin({ThreadDownloadImageData.class})
public abstract class ThreadDownloadImageDataMixin extends AbstractTexture {

   @Shadow private boolean textureUploaded;

//   public MultiTexID getMultiTexID() {
//      if (!this.textureUploaded) {
//         this.getGlTextureId();
//      }
//
//      return ((AbstractTextureAccessor)this).getMultiTexID();
//   }
}
