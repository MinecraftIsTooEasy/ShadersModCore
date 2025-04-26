package shadersmodcore.util;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL14;

import java.nio.IntBuffer;

public class TextureUtilExtra {
   public static void bindTexture(int par0) {
      GL11.glBindTexture(3553, par0);
   }

   public static void allocateTextureImpl(int p_147946_0_, int p_147946_1_, int p_147946_2_, int p_147946_3_, float p_147946_4_) {
//        if (OpenGlHelper.anisotropicFilteringSupported) {
//            GL11.glTexParameterf(GL11.GL_TEXTURE_2D, 34046, p_147946_4_);
//        }

      if (p_147946_1_ > 0) {
         GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL12.GL_TEXTURE_MAX_LEVEL, p_147946_1_);
         GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL12.GL_TEXTURE_MIN_LOD, 0.0F);
         GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL12.GL_TEXTURE_MAX_LOD, (float) p_147946_1_);
         GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, 0.0F);
      }

      for (int i1 = 0; i1 <= p_147946_1_; ++i1) {
         GL11.glTexImage2D(GL11.GL_TEXTURE_2D, i1, GL11.GL_RGBA, p_147946_2_ >> i1, p_147946_3_ >> i1, 0, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, (IntBuffer) null);
      }
   }
}
