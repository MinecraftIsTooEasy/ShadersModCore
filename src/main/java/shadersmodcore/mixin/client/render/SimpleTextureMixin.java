package shadersmodcore.mixin.client.render;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.*;
import org.spongepowered.asm.mixin.injection.At;
import shadersmodcore.api.AbstractTextureAccessor;
import shadersmodcore.client.shader.ShadersTex;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.awt.image.BufferedImage;

@Mixin({SimpleTexture.class})
public abstract class SimpleTextureMixin extends AbstractTexture {

   @Shadow @Final private ResourceLocation textureLocation;

//   /**
//    * @author
//    * @reason
//    */
//   @Overwrite
//   public void loadTexture(ResourceManager par1ResourceManager) {
//      InputStream var2 = null;
//
//      try {
//         Resource var3 = par1ResourceManager.getResource(this.textureLocation);
//         var2 = var3.getInputStream();
//         if (this.textureLocation.generate_encoded_file && !Minecraft.inDevMode()) {
//            this.textureLocation.generate_encoded_file = false;
//            Minecraft.setErrorMessage("SimpleTexture: Error for " + this.textureLocation.getResourcePath());
//         }
//
//         byte[] bytes;
//         if (this.textureLocation.getResourcePath().endsWith(".enc")) {
//             try {
//                 bytes = IOUtils.toByteArray(var2);
//             } catch (IOException e) {
//                 throw new RuntimeException(e);
//             }
//             this.rB(bytes);
//            bytes[1] = 80;
//            bytes[2] = 78;
//            bytes[3] = 71;
//            var2 = new ByteArrayInputStream(bytes);
//         } else if (this.textureLocation.generate_encoded_file) {
//             try {
//                 bytes = IOUtils.toByteArray(var2);
//             } catch (IOException e) {
//                 throw new RuntimeException(e);
//             }
//             byte[] copy_of_bytes = new byte[bytes.length];
//            System.arraycopy(bytes, 0, copy_of_bytes, 0, bytes.length);
//            copy_of_bytes[1] = 0;
//            copy_of_bytes[2] = 0;
//            copy_of_bytes[3] = 0;
//            this.rB(copy_of_bytes);
//            String resource_path = this.textureLocation.getResourcePath();
//            if (resource_path.endsWith(".png")) {
//               resource_path = resource_path.substring(0, resource_path.length() - 4);
//            }
//
//            String output_path = "resourcepacks/MITE Resource Pack 1.6.4/assets/minecraft/" + resource_path + ".enc";
//            System.out.print("Attempting to create encoded file (" + output_path + ")...");
//
//            try {
//               FileOutputStream fos = new FileOutputStream(new File(output_path));
//               fos.write(copy_of_bytes, 0, copy_of_bytes.length);
//               fos.flush();
//               fos.close();
//               System.out.println("succeeded");
//            } catch (Exception var14) {
//               System.out.println("failed");
//            }
//
//            var2 = new ByteArrayInputStream(bytes);
//         }
//
//         BufferedImage var4 = ImageIO.read(var2);
//         boolean var5 = false;
//         boolean var6 = false;
//         if (var3.hasMetadata()) {
//            try {
//               TextureMetadataSection var7 = (TextureMetadataSection)var3.getMetadata("texture");
//               if (var7 != null) {
//                  var5 = var7.getTextureBlur();
//                  var6 = var7.getTextureClamp();
//               }
//            } catch (RuntimeException var13) {
//               Minecraft.getMinecraft().getLogAgent().logWarningException("Failed reading metadata of: " + this.textureLocation, var13);
//            }
//         }
//
//         ShadersTex.loadSimpleTexture(this.getGlTextureId(), var4, var5, var6, par1ResourceManager, this.textureLocation,
//                 ((AbstractTextureAccessor)this).getMultiTexID());
//      } catch (IOException e) {
//          throw new RuntimeException(e);
//      } finally {
//         if (var2 != null) {
//             try {
//                 var2.close();
//             } catch (IOException e) {
//                 throw new RuntimeException(e);
//             }
//         }
//      }
//
//   }

    @WrapWithCondition(method = "loadTexture", at = @At(value = "INVOKE", target = "Lnet/minecraft/TextureUtil;uploadTextureImageAllocate(ILjava/awt/image/BufferedImage;ZZ)I"))
    private boolean loadSimpleTexture(int i, BufferedImage bufferedImage, boolean bl, boolean bl2) {
       ShadersTex.loadSimpleTexture(this.getGlTextureId(), bufferedImage, bl, bl2, Minecraft.getMinecraft().getResourceManager(), this.textureLocation,
                ((AbstractTextureAccessor)this).getMultiTexID());
        return true;
    }

   @Shadow
   private void rB(byte[] bytes) {
   }
}
