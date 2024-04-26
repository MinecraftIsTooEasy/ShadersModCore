package net.wenscHuix.mitemod.shader.client;

import net.minecraft.*;
import net.wenscHuix.mitemod.imixin.AbstractTextureAccessor;
import net.wenscHuix.mitemod.imixin.TextureMapAccessor;
import net.wenscHuix.mitemod.imixin.TextureObjectAccessor;
import net.wenscHuix.mitemod.shader.util.OpenGlHelperExtra;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.*;

public class ShadersTex {
   public static ByteBuffer byteBuffer = BufferUtils.createByteBuffer(4194304);
   public static IntBuffer intBuffer;
   public static int[] intArray;
   public static Map multiTexMap;
   public static MultiTexID updatingTex;
   public static MultiTexID boundTex;
   public static int updatingPage;
   static ResourceManager resManager;
   static ResourceLocation resLocation;
   static int imageSize;

   public static IntBuffer getIntBuffer(int size) {
      if (intBuffer.capacity() < size) {
         int bufferSize = roundUpPOT(size);
         byteBuffer = BufferUtils.createByteBuffer(bufferSize * 4);
         intBuffer = byteBuffer.asIntBuffer();
      }

      return intBuffer;
   }

   public static int[] getIntArray(int size) {
      if (intArray.length < size) {
         intArray = null;
         intArray = new int[roundUpPOT(size)];
      }

      return intArray;
   }

   public static int roundUpPOT(int x) {
      int i = x - 1;
      i |= i >> 1;
      i |= i >> 2;
      i |= i >> 4;
      i |= i >> 8;
      i |= i >> 16;
      return i + 1;
   }

   public static IntBuffer fillIntBuffer(int size, int value) {
      Arrays.fill(intArray, 0, size, value);
      intBuffer.put(intArray, 0, size);
      return intBuffer;
   }

   public static int[] createAIntImage(int size) {
      int[] aint = new int[size * 3];
      Arrays.fill(aint, 0, size, 0);
      Arrays.fill(aint, size, size * 2, -8421377);
      Arrays.fill(aint, size * 2, size * 3, 0);
      return aint;
   }

   public static int[] createAIntImage(int size, int color) {
      int[] aint = new int[size * 3];
      Arrays.fill(aint, 0, size, color);
      Arrays.fill(aint, size, size * 2, -8421377);
      Arrays.fill(aint, size * 2, size * 3, 0);
      return aint;
   }

   public static MultiTexID getMultiTexID(AbstractTexture tex) {
      MultiTexID multiTex = ((AbstractTextureAccessor) (tex)).mITE_Shader_Loader$getMultiTexID0();
      if (multiTex == null) {
         int baseTex = tex.getGlTextureId();
         multiTex = (MultiTexID)multiTexMap.get(baseTex);
         if (multiTex == null) {
            multiTex = new MultiTexID(baseTex, GL11.glGenTextures(), GL11.glGenTextures());
            multiTexMap.put(baseTex, multiTex);
         }

         ((AbstractTextureAccessor) (tex)).mITE_Shader_Loader$setMultiTexID(multiTex);
      }

      return multiTex;
   }

   public static void deleteTextures(AbstractTexture atex) {
      int texid = atex.getGlTextureId();
      GL11.glDeleteTextures(texid);
      ((AbstractTextureAccessor) atex).mITE_Shader_Loader$setGlTextureId(0);
      MultiTexID multiTex = ((AbstractTextureAccessor) atex).mITE_Shader_Loader$getMultiTexID();
      if (multiTex != null) {
         ((AbstractTextureAccessor) atex).mITE_Shader_Loader$setMultiTexID(null);
         multiTexMap.remove(multiTex.base);
         GL11.glDeleteTextures(multiTex.norm);
         GL11.glDeleteTextures(multiTex.spec);
         if (multiTex.base != texid) {
            System.err.println("Error : MultiTexID.base mismatch.");
            GL11.glDeleteTextures(multiTex.base);
         }
      }

   }

   public static int deleteMultiTex(TextureObject tex) {
      if (tex instanceof AbstractTexture) {
         deleteTextures((AbstractTexture)tex);
      } else {
         GL11.glDeleteTextures(tex.getGlTextureId());
      }

      return 0;
   }

   public static void bindTextures(int baseTex, int normTex, int specTex) {
      if (Shaders.isRenderingWorld && OpenGlHelperExtra.activeTexUnit == 33984) {
         GL13.glActiveTexture(33986);
         GL11.glBindTexture(3553, normTex);
         GL13.glActiveTexture(33987);
         GL11.glBindTexture(3553, specTex);
         GL13.glActiveTexture(33984);
      }

      GL11.glBindTexture(3553, baseTex);
   }

   public static void bindNSTextures(int normTex, int specTex) {
      if (Shaders.isRenderingWorld && OpenGlHelperExtra.activeTexUnit == 33984) {
         GL13.glActiveTexture(33986);
         GL11.glBindTexture(3553, normTex);
         GL13.glActiveTexture(33987);
         GL11.glBindTexture(3553, specTex);
         GL13.glActiveTexture(33984);
      }

   }

   public static void bindTextures(MultiTexID multiTex) {
      boundTex = multiTex;
      bindTextures(multiTex.base, multiTex.norm, multiTex.spec);
   }

   public static void bindTexture(TextureObject tex) {
      if (tex instanceof TextureMap) {
         Shaders.atlasSizeX = ((TextureMapAccessor) tex).mITE_Shader_Loader$getAtlasWidth();
         Shaders.atlasSizeY = ((TextureMapAccessor) tex).mITE_Shader_Loader$getAtlasHeight();
      } else {
         Shaders.atlasSizeX = 0;
         Shaders.atlasSizeY = 0;
      }

      bindTextures(((AbstractTextureAccessor) tex).mITE_Shader_Loader$getMultiTexID());
   }

   public static void bindNSTextures(MultiTexID multiTex) {
      bindNSTextures(multiTex.norm, multiTex.spec);
   }

   public static void bindTextures(int baseTex) {
      MultiTexID multiTex = (MultiTexID)multiTexMap.get(baseTex);
      bindTextures(multiTex);
   }

   public static void allocTexStorage(int width, int height) {
      Shaders.checkGLError("pre allocTexStorage");
      int level = 0;
      int wt = width;

      for(int ht = height; wt > 0 && ht > 0; ++level) {
         GL11.glTexImage2D(3553, level, 6408, wt, ht, 0, 32993, 33639, (IntBuffer)null);
         wt /= 2;
         ht /= 2;
      }

      GL11.glTexParameteri(3553, 33085, level - 1);
      Shaders.checkGLError("allocTexStorage");
   }

   public static void initDynamicTexture(int texID, int width, int height, DynamicTexture tex) {
      MultiTexID multiTex = ((AbstractTextureAccessor) tex).mITE_Shader_Loader$getMultiTexID();
      int[] aint = tex.getTextureData();
      int size = width * height;
      Arrays.fill(aint, size, size * 2, -8421377);
      Arrays.fill(aint, size * 2, size * 3, 0);
      GL11.glDeleteTextures(multiTex.base);
      GL11.glBindTexture(3553, multiTex.base);
      allocTexStorage(width, height);
      GL11.glTexParameteri(3553, 10241, 9728);
      GL11.glTexParameteri(3553, 10240, 9728);
      GL11.glTexParameteri(3553, 10242, 10497);
      GL11.glTexParameteri(3553, 10243, 10497);
      GL11.glDeleteTextures(multiTex.norm);
      GL11.glBindTexture(3553, multiTex.norm);
      allocTexStorage(width, height);
      GL11.glTexParameteri(3553, 10241, 9728);
      GL11.glTexParameteri(3553, 10240, 9728);
      GL11.glTexParameteri(3553, 10242, 10497);
      GL11.glTexParameteri(3553, 10243, 10497);
      GL11.glDeleteTextures(multiTex.spec);
      GL11.glBindTexture(3553, multiTex.spec);
      allocTexStorage(width, height);
      GL11.glTexParameteri(3553, 10241, 9728);
      GL11.glTexParameteri(3553, 10240, 9728);
      GL11.glTexParameteri(3553, 10242, 10497);
      GL11.glTexParameteri(3553, 10243, 10497);
      GL11.glBindTexture(3553, multiTex.base);
   }

   public static TextureObject createDefaultTexture() {
      DynamicTexture tex = new DynamicTexture(1, 1);
      tex.getTextureData()[0] = -1;
      tex.updateDynamicTexture();
      return tex;
   }

   public static void setupTextureMap(int width, int height, Stitcher stitcher, TextureMap tex) {
      MultiTexID multiTex = getMultiTexID(tex);
      ((TextureMapAccessor) tex).mITE_Shader_Loader$setAtlasWidth(width);
      ((TextureMapAccessor) tex).mITE_Shader_Loader$setAtlasHeight(height);
      List spriteList = stitcher.getStichSlots();
      GL11.glDeleteTextures(multiTex.base);
      GL11.glBindTexture(3553, multiTex.base);
      allocTexStorage(width, height);
      GL11.glTexParameteri(3553, 10241, Shaders.texMinFilValue[Shaders.configTexMinFilB]);
      GL11.glTexParameteri(3553, 10240, Shaders.texMagFilValue[Shaders.configTexMagFilB]);
      GL11.glTexParameteri(3553, 10242, 10497);
      GL11.glTexParameteri(3553, 10243, 10497);
      GL11.glTexParameteri(3553, 33083, 4);
      Iterator iterator = spriteList.iterator();

      TextureAtlasSprite sprite;
      while(iterator.hasNext()) {
         sprite = (TextureAtlasSprite)iterator.next();
         updateSubImage1(sprite.getFrameTextureData(0), sprite.getIconWidth(), sprite.getIconHeight(), sprite.getOriginX(), sprite.getOriginY(), 0, 0);
      }

      GL11.glDeleteTextures(multiTex.norm);
      GL11.glBindTexture(3553, multiTex.norm);
      allocTexStorage(width, height);
      GL11.glTexParameteri(3553, 10241, Shaders.texMinFilValue[Shaders.configTexMinFilN]);
      GL11.glTexParameteri(3553, 10240, Shaders.texMagFilValue[Shaders.configTexMagFilN]);
      GL11.glTexParameteri(3553, 10242, 10497);
      GL11.glTexParameteri(3553, 10243, 10497);
      GL11.glTexParameteri(3553, 33083, 4);
      iterator = spriteList.iterator();

      while(iterator.hasNext()) {
         sprite = (TextureAtlasSprite)iterator.next();
         updateSubImage1(sprite.getFrameTextureData(0), sprite.getIconWidth(), sprite.getIconHeight(), sprite.getOriginX(), sprite.getOriginY(), 1, -8421377);
      }

      GL11.glDeleteTextures(multiTex.spec);
      GL11.glBindTexture(3553, multiTex.spec);
      allocTexStorage(width, height);
      GL11.glTexParameteri(3553, 10241, Shaders.texMinFilValue[Shaders.configTexMinFilS]);
      GL11.glTexParameteri(3553, 10240, Shaders.texMagFilValue[Shaders.configTexMagFilS]);
      GL11.glTexParameteri(3553, 10242, 10497);
      GL11.glTexParameteri(3553, 10243, 10497);
      GL11.glTexParameteri(3553, 33083, 4);
      iterator = spriteList.iterator();

      while(iterator.hasNext()) {
         sprite = (TextureAtlasSprite)iterator.next();
         updateSubImage1(sprite.getFrameTextureData(0), sprite.getIconWidth(), sprite.getIconHeight(), sprite.getOriginX(), sprite.getOriginY(), 2, 0);
      }

      GL11.glBindTexture(3553, multiTex.base);
   }

   public static void updateTextureMap(int[] par0ArrayOfInteger, int par1, int par2, int par3, int par4, boolean par5, boolean par6) {
   }

   public static int blend4Alpha(int c0, int c1, int c2, int c3) {
      int a0 = c0 >>> 24 & 255;
      int a1 = c1 >>> 24 & 255;
      int a2 = c2 >>> 24 & 255;
      int a3 = c3 >>> 24 & 255;
      int as = a0 + a1 + a2 + a3;
      int an = (as + 2) / 4;
      int dv;
      if (as != 0) {
         dv = as;
      } else {
         dv = 4;
         a0 = 1;
         a1 = 1;
         a2 = 1;
         a3 = 1;
      }

      int frac = (dv + 1) / 2;
      int color = an << 24 | ((c0 >>> 16 & 255) * a0 + (c1 >>> 16 & 255) * a1 + (c2 >>> 16 & 255) * a2 + (c3 >>> 16 & 255) * a3 + frac) / dv << 16 | ((c0 >>> 8 & 255) * a0 + (c1 >>> 8 & 255) * a1 + (c2 >>> 8 & 255) * a2 + (c3 >>> 8 & 255) * a3 + frac) / dv << 8 | ((c0 >>> 0 & 255) * a0 + (c1 >>> 0 & 255) * a1 + (c2 >>> 0 & 255) * a2 + (c3 >>> 0 & 255) * a3 + frac) / dv << 0;
      return color;
   }

   public static int blend4Simple(int c0, int c1, int c2, int c3) {
      int color = ((c0 >>> 24 & 255) + (c1 >>> 24 & 255) + (c2 >>> 24 & 255) + (c3 >>> 24 & 255) + 2) / 4 << 24 | ((c0 >>> 16 & 255) + (c1 >>> 16 & 255) + (c2 >>> 16 & 255) + (c3 >>> 16 & 255) + 2) / 4 << 16 | ((c0 >>> 8 & 255) + (c1 >>> 8 & 255) + (c2 >>> 8 & 255) + (c3 >>> 8 & 255) + 2) / 4 << 8 | ((c0 >>> 0 & 255) + (c1 >>> 0 & 255) + (c2 >>> 0 & 255) + (c3 >>> 0 & 255) + 2) / 4 << 0;
      return color;
   }

   public static void genMipmapAlpha(int[] aint, int offset, int width, int height) {
      Math.min(width, height);
      int o2 = offset;
      int w2 = width;
      int h2 = height;
      int o1 = 0;
      int w1 = 0;
      int h1 = 0;
      int level = 0;
      while (w2 > 1 && h2 > 1) {
         o1 = o2 + w2 * h2;
         w1 = w2 / 2;
         h1 = h2 / 2;
         for (int y = 0; y < h1; ++y) {
            int p1 = o1 + y * w1;
            int p2 = o2 + y * 2 * w2;
            for (int x = 0; x < w1; ++x) {
               aint[p1 + x] = ShadersTex.blend4Alpha(aint[p2 + x * 2], aint[p2 + x * 2 + 1], aint[p2 + w2 + x * 2], aint[p2 + w2 + x * 2 + 1]);
            }
         }
         ++level;
         w2 = w1;
         h2 = h1;
         o2 = o1;
      }
      while (level > 0) {
         w2 = width >> --level;
         h2 = height >> level;
         int p2 = o2 = o1 - w2 * h2;
         for (int y = 0; y < h2; ++y) {
            for (int x = 0; x < w2; ++x) {
               if (aint[p2] == 0) {
                  aint[p2] = aint[o1 + y / 2 * w1 + x / 2] & 0xFFFFFF;
               }
               ++p2;
            }
         }
         o1 = o2;
         w1 = w2;
      }
   }

   public static void genMipmapSimple(int[] aint, int offset, int width, int height) {
      Math.min(width, height);
      int o2 = offset;
      int w2 = width;
      int h2 = height;
      int o1 = 0;
      int w1 = 0;
      int h1 = 0;
      int level = 0;
      while (w2 > 1 && h2 > 1) {
         o1 = o2 + w2 * h2;
         w1 = w2 / 2;
         h1 = h2 / 2;
         for (int y = 0; y < h1; ++y) {
            int p1 = o1 + y * w1;
            int p2 = o2 + y * 2 * w2;
            for (int x = 0; x < w1; ++x) {
               aint[p1 + x] = ShadersTex.blend4Simple(aint[p2 + x * 2], aint[p2 + x * 2 + 1], aint[p2 + w2 + x * 2], aint[p2 + w2 + x * 2 + 1]);
            }
         }
         ++level;
         w2 = w1;
         h2 = h1;
         o2 = o1;
      }
      while (level > 0) {
         w2 = width >> --level;
         h2 = height >> level;
         int p2 = o2 = o1 - w2 * h2;
         for (int y = 0; y < h2; ++y) {
            for (int x = 0; x < w2; ++x) {
               if (aint[p2] == 0) {
                  aint[p2] = aint[o1 + y / 2 * w1 + x / 2] & 0xFFFFFF;
               }
               ++p2;
            }
         }
         o1 = o2;
         w1 = w2;
      }
   }

   public static boolean isSemiTransparent(int[] aint, int width, int height) {
      int size = width * height;
      if (aint[0] >>> 24 == 255 && aint[size - 1] == 0) {
         return true;
      } else {
         for(int i = 0; i < size; ++i) {
            int alpha = aint[i] >>> 24;
            if (alpha != 0 && alpha != 255) {
               return true;
            }
         }

         return false;
      }
   }

   public static void updateSubImage1(int[] src, int width, int height, int posX, int posY, int page, int color) {
      int size = width * height;
      IntBuffer intBuf = getIntBuffer(size);
      int[] aint = getIntArray((size * 4 + 2) / 3);
      if (src.length >= size * (page + 1)) {
         System.arraycopy(src, size * page, aint, 0, size);
      } else {
         Arrays.fill(aint, color);
      }

      genMipmapAlpha(aint, 0, width, height);
      int level = 0;
      int offset = 0;
      int lw = width;
      int lh = height;
      int px = posX;

      for(int py = posY; lw > 0 && lh > 0; ++level) {
         int lsize = lw * lh;
         intBuf.clear();
         intBuf.put(aint, offset, lsize).position(0).limit(lsize);
         GL11.glTexSubImage2D(3553, level, px, py, lw, lh, 32993, 33639, intBuf);
         offset += lsize;
         lw /= 2;
         lh /= 2;
         px /= 2;
         py /= 2;
      }

      intBuf.clear();
   }

   public static void updateSubTex1(int[] src, int width, int height, int posX, int posY) {
      int level = 0;
      int cw = width;
      int ch = height;
      int cx = posX;

      for(int cy = posY; cw > 0 && ch > 0; cy /= 2) {
         GL11.glCopyTexSubImage2D(3553, level, cx, cy, 0, 0, cw, ch);
         ++level;
         cw /= 2;
         ch /= 2;
         cx /= 2;
      }

   }

   public static void setupTextureMipmap(TextureMap tex) {
   }

   public static void updateDynamicTexture(int texID, int[] src, int width, int height, DynamicTexture tex) {
      MultiTexID multiTex = ((AbstractTextureAccessor) tex).mITE_Shader_Loader$getMultiTexID();
      GL11.glBindTexture(3553, multiTex.norm);
      updateSubImage1(src, width, height, 0, 0, 1, -8421377);
      GL11.glBindTexture(3553, multiTex.spec);
      updateSubImage1(src, width, height, 0, 0, 2, 0);
      GL11.glBindTexture(3553, multiTex.base);
      updateSubImage1(src, width, height, 0, 0, 0, 0);
   }

   public static void updateSubImage(int[] src, int width, int height, int posX, int posY, boolean linear, boolean clamp) {
      if (updatingTex != null) {
         GL11.glBindTexture(3553, updatingTex.norm);
         updateSubImage1(src, width, height, posX, posY, 1, -8421377);
         GL11.glBindTexture(3553, updatingTex.spec);
         updateSubImage1(src, width, height, posX, posY, 2, 0);
         GL11.glBindTexture(3553, updatingTex.base);
      }

      updateSubImage1(src, width, height, posX, posY, 0, 0);
   }

   public static void updateAnimationTextureMap(TextureMap tex, List tasList) {
      MultiTexID multiTex = ((AbstractTextureAccessor) tex).mITE_Shader_Loader$getMultiTexID();
      GL11.glBindTexture(3553, multiTex.norm);
      Iterator iterator = tasList.iterator();

      TextureAtlasSprite tas;
      while(iterator.hasNext()) {
         tas = (TextureAtlasSprite)iterator.next();
         tas.updateAnimation();
      }

      GL11.glBindTexture(3553, multiTex.norm);
      iterator = tasList.iterator();

      while(iterator.hasNext()) {
         tas = (TextureAtlasSprite)iterator.next();
         tas.updateAnimation();
      }

      GL11.glBindTexture(3553, multiTex.norm);
      iterator = tasList.iterator();

      while(iterator.hasNext()) {
         tas = (TextureAtlasSprite)iterator.next();
         tas.updateAnimation();
      }

   }

   public static void setupTexture(MultiTexID multiTex, int[] src, int width, int height, boolean linear, boolean clamp) {
      int mmfilter = linear ? 9729 : 9728;
      int wraptype = clamp ? 10496 : 10497;
      int size = width * height;
      IntBuffer intBuf = getIntBuffer(size);
      intBuf.clear();
      intBuf.put(src, 0, size).position(0).limit(size);
      GL11.glBindTexture(3553, multiTex.base);
      GL11.glTexImage2D(3553, 0, 6408, width, height, 0, 32993, 33639, intBuf);
      GL11.glTexParameteri(3553, 10241, mmfilter);
      GL11.glTexParameteri(3553, 10240, mmfilter);
      GL11.glTexParameteri(3553, 10242, wraptype);
      GL11.glTexParameteri(3553, 10243, wraptype);
      intBuf.put(src, size, size).position(0).limit(size);
      GL11.glBindTexture(3553, multiTex.norm);
      GL11.glTexImage2D(3553, 0, 6408, width, height, 0, 32993, 33639, intBuf);
      GL11.glTexParameteri(3553, 10241, mmfilter);
      GL11.glTexParameteri(3553, 10240, mmfilter);
      GL11.glTexParameteri(3553, 10242, wraptype);
      GL11.glTexParameteri(3553, 10243, wraptype);
      intBuf.put(src, size * 2, size).position(0).limit(size);
      GL11.glBindTexture(3553, multiTex.spec);
      GL11.glTexImage2D(3553, 0, 6408, width, height, 0, 32993, 33639, intBuf);
      GL11.glTexParameteri(3553, 10241, mmfilter);
      GL11.glTexParameteri(3553, 10240, mmfilter);
      GL11.glTexParameteri(3553, 10242, wraptype);
      GL11.glTexParameteri(3553, 10243, wraptype);
      GL11.glBindTexture(3553, multiTex.base);
   }

   public static void updateSubImage(MultiTexID multiTex, int[] src, int width, int height, int posX, int posY, boolean linear, boolean clamp) {
      int size = width * height;
      IntBuffer intBuf = getIntBuffer(size);
      intBuf.clear();
      intBuf.put(src, 0, size);
      intBuf.position(0).limit(size);
      GL11.glBindTexture(3553, multiTex.base);
      GL11.glTexParameteri(3553, 10241, 9728);
      GL11.glTexParameteri(3553, 10240, 9728);
      GL11.glTexParameteri(3553, 10242, 10497);
      GL11.glTexParameteri(3553, 10243, 10497);
      GL11.glTexSubImage2D(3553, 0, posX, posY, width, height, 32993, 33639, intBuf);
      if (src.length == size * 3) {
         intBuf.clear();
         intBuf.put(src, size, size).position(0);
         intBuf.position(0).limit(size);
      }

      GL11.glBindTexture(3553, multiTex.norm);
      GL11.glTexParameteri(3553, 10241, 9728);
      GL11.glTexParameteri(3553, 10240, 9728);
      GL11.glTexParameteri(3553, 10242, 10497);
      GL11.glTexParameteri(3553, 10243, 10497);
      GL11.glTexSubImage2D(3553, 0, posX, posY, width, height, 32993, 33639, intBuf);
      if (src.length == size * 3) {
         intBuf.clear();
         intBuf.put(src, size * 2, size);
         intBuf.position(0).limit(size);
      }

      GL11.glBindTexture(3553, multiTex.spec);
      GL11.glTexParameteri(3553, 10241, 9728);
      GL11.glTexParameteri(3553, 10240, 9728);
      GL11.glTexParameteri(3553, 10242, 10497);
      GL11.glTexParameteri(3553, 10243, 10497);
      GL11.glTexSubImage2D(3553, 0, posX, posY, width, height, 32993, 33639, intBuf);
      GL13.glActiveTexture(33984);
   }

   public static ResourceLocation getNSMapLocation(ResourceLocation location, String mapName) {
      String basename = location.getResourcePath();
      String[] basenameParts = basename.split(".png");
      String basenameNoFileType = basenameParts[0];
      return new ResourceLocation(location.getResourceDomain(), basenameNoFileType + "_" + mapName + ".png");
   }

   public static void loadNSMap(ResourceManager manager, ResourceLocation location, int width, int height, int[] aint) {
      loadNSMap1(manager, getNSMapLocation(location, "n"), width, height, aint, width * height, -8421377);
      loadNSMap1(manager, getNSMapLocation(location, "s"), width, height, aint, width * height * 2, 0);
   }

   public static void loadNSMap1(ResourceManager manager, ResourceLocation location, int width, int height, int[] aint, int offset, int defaultColor) {
      boolean good = false;

      try {
         Resource res = manager.getResource(location);
         BufferedImage bufferedimage = ImageIO.read(res.getInputStream());
         if (bufferedimage.getWidth() == width && bufferedimage.getHeight() == height) {
            bufferedimage.getRGB(0, 0, width, height, aint, offset, width);
            good = true;
         }
      } catch (IOException ignored) {
      }

      if (!good) {
         Arrays.fill(aint, offset, offset + width * height, defaultColor);
      }

   }

   public static int loadSimpleTexture(int textureID, BufferedImage bufferedimage, boolean linear, boolean clamp, ResourceManager resourceManager, ResourceLocation location, MultiTexID multiTex) {
      int width = bufferedimage.getWidth();
      int height = bufferedimage.getHeight();
      int size = width * height;
      int[] aint = getIntArray(size * 3);
      bufferedimage.getRGB(0, 0, width, height, aint, 0, width);
      loadNSMap(resourceManager, location, width, height, aint);
      setupTexture(multiTex, aint, width, height, linear, clamp);
      return textureID;
   }

   public static void mergeImage(int[] aint, int dstoff, int srcoff, int size) {
   }

   public static int blendColor(int color1, int color2, int factor1) {
      int factor2 = 255 - factor1;
      return ((color1 >>> 24 & 255) * factor1 + (color2 >>> 24 & 255) * factor2) / 255 << 24 | ((color1 >>> 16 & 255) * factor1 + (color2 >>> 16 & 255) * factor2) / 255 << 16 | ((color1 >>> 8 & 255) * factor1 + (color2 >>> 8 & 255) * factor2) / 255 << 8 | ((color1 >>> 0 & 255) * factor1 + (color2 >>> 0 & 255) * factor2) / 255 << 0;
   }

   public static void loadLayeredTexture(LayeredTexture tex, ResourceManager manager, List list) {
      int width = 0;
      int height = 0;
      int size = 0;
      int[] image = null;
      Iterator iterator = list.iterator();

      while(true) {
         String s;
         do {
            if (!iterator.hasNext()) {
               setupTexture(((AbstractTextureAccessor) tex).mITE_Shader_Loader$getMultiTexID(),
                       image, width, height, false, false);
               return;
            }

            s = (String)iterator.next();
         } while(s == null);

         try {
            ResourceLocation location = new ResourceLocation(s);
            InputStream inputstream = manager.getResource(location).getInputStream();
            BufferedImage bufimg = ImageIO.read(inputstream);
            if (size == 0) {
               width = bufimg.getWidth();
               height = bufimg.getHeight();
               size = width * height;
               image = createAIntImage(size, 0);
            }

            int[] aint = getIntArray(size * 3);
            bufimg.getRGB(0, 0, width, height, aint, 0, width);
            loadNSMap(manager, location, width, height, aint);

            for(int i = 0; i < size; ++i) {
               int alpha = aint[i] >>> 24 & 255;
               image[i] = blendColor(aint[i], image[i], alpha);
               image[size + i] = blendColor(aint[size + i], image[size + i], alpha);
               image[size * 2 + i] = blendColor(aint[size * 2 + i], image[size * 2 + i], alpha);
            }
         } catch (IOException var15) {
            var15.printStackTrace();
         }
      }
   }

   static void updateTextureMinMagFilter() {
      TextureManager texman = Minecraft.getMinecraft().getTextureManager();
      TextureObject texObj = texman.getTexture(TextureMap.locationBlocksTexture);
      if (texObj != null) {
         MultiTexID multiTex = ((AbstractTextureAccessor) texObj).mITE_Shader_Loader$getMultiTexID();
         GL11.glBindTexture(3553, multiTex.base);
         GL11.glTexParameteri(3553, 10241, Shaders.texMinFilValue[Shaders.configTexMinFilB]);
         GL11.glTexParameteri(3553, 10240, Shaders.texMagFilValue[Shaders.configTexMagFilB]);
         GL11.glBindTexture(3553, multiTex.norm);
         GL11.glTexParameteri(3553, 10241, Shaders.texMinFilValue[Shaders.configTexMinFilN]);
         GL11.glTexParameteri(3553, 10240, Shaders.texMagFilValue[Shaders.configTexMagFilN]);
         GL11.glBindTexture(3553, multiTex.spec);
         GL11.glTexParameteri(3553, 10241, Shaders.texMinFilValue[Shaders.configTexMinFilS]);
         GL11.glTexParameteri(3553, 10240, Shaders.texMagFilValue[Shaders.configTexMagFilS]);
         GL11.glBindTexture(3553, 0);
      }

   }

   public static Resource loadResource(ResourceManager manager, ResourceLocation location) throws IOException {
      resManager = manager;
      resLocation = location;
      return manager.getResource(location);
   }

   public static int[] loadAtlasSprite(BufferedImage bufferedimage, int startX, int startY, int w, int h, int[] aint, int offset, int scansize) {
      imageSize = w * h;
      bufferedimage.getRGB(startX, startY, w, h, aint, offset, scansize);
      loadNSMap(resManager, resLocation, w, h, aint);
      return aint;
   }

   public static int[] extractFrame(int[] src, int width, int height, int frameIndex) {
      int srcSize = imageSize;
      int frameSize = width * height;
      int[] dst = new int[frameSize * 3];
      int srcPos = frameSize * frameIndex;
      int dstPos = 0;
      System.arraycopy(src, srcPos, dst, dstPos, frameSize);
      System.arraycopy(src, srcPos += srcSize, dst, dstPos += frameSize, frameSize);
      System.arraycopy(src, srcPos += srcSize, dst, dstPos += frameSize, frameSize);
      return dst;
   }

   static {
      intBuffer = byteBuffer.asIntBuffer();
      intArray = new int[1048576];
      multiTexMap = new HashMap();
      updatingTex = null;
      boundTex = null;
      updatingPage = 0;
      resManager = null;
      resLocation = null;
      imageSize = 0;
   }
}
