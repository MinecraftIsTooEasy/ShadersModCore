package net.wenscHuix.mitemod.shader.client;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;
import net.minecraft.bfq;
import net.minecraft.bma;
import net.wenscHuix.mitemod.mixin.render.TessellatorAccessor;
import net.wenscHuix.mitemod.shader.util.Common;
import net.wenscHuix.mitemod.shader.util.TessellatorExtra;
import net.wenscHuix.mitemod.shader.util.Utils;
import org.lwjgl.opengl.ARBVertexBufferObject;
import org.lwjgl.opengl.ARBVertexShader;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

public class ShadersTess {
   public static final int vertexStride = 16;
   public float normalX;
   public float normalY;
   public float normalZ;

   public static int draw(bfq tess) {
      if (!tess.z) {
         throw new IllegalStateException("Not tesselating!");
      } else {
         tess.z = false;
         int offs = 0;

         int vtc;
         while(offs < tess.i) {
            int realDrawMode;
            if (tess.u == 7 && (Boolean)Utils.get(bfq.class, "b", Boolean.TYPE)) {
               vtc = Math.min(tess.i - offs, tess.d.capacity() / 96);
               realDrawMode = 4;
            } else {
               vtc = Math.min(tess.i - offs, tess.d.capacity() / 64);
               realDrawMode = tess.u;
            }

            tess.e.clear();
            tess.e.put(tess.h, offs * 16, vtc * 16);
            tess.d.position(0);
            tess.d.limit(vtc * 64);
            offs += vtc;
            if (TessellatorAccessor.gettryVBO()) {
               tess.setVboIndex((tess.getVboIndex() + 1) % Common.vboCount);
               ARBVertexBufferObject.glBindBufferARB(34962, Common.vertexBuffers.get((Integer)Utils.get((Object)tess, "C", Integer.TYPE)));
               ARBVertexBufferObject.glBufferDataARB(34962, tess.d, 35040);
               if (tess.o) {
                  GL11.glTexCoordPointer(2, 5126, 64, 12L);
                  GL11.glEnableClientState(32888);
               }

               if (tess.p) {
                  bma.b(bma.b);
                  GL11.glTexCoordPointer(2, 5122, 64, 28L);
                  GL11.glEnableClientState(32888);
                  bma.b(bma.a);
               }

               if (tess.n) {
                  GL11.glColorPointer(4, 5121, 64, 20L);
                  GL11.glEnableClientState(32886);
               }

               if (tess.q) {
                  GL11.glNormalPointer(5126, 64, 32L);
                  GL11.glEnableClientState(32885);
               }

               GL11.glVertexPointer(3, 5126, 64, 0L);
               GL11.glEnableClientState(32884);
               preDrawArrayVBO(tess);
            } else {
               if (tess.o) {
                  tess.f.position(3);
                  GL11.glTexCoordPointer(2, 64, tess.f);
                  GL11.glEnableClientState(32888);
               }

               if (tess.p) {
                  bma.b(bma.b);
                  tess.g.position(14);
                  GL11.glTexCoordPointer(2, 64, tess.g);
                  GL11.glEnableClientState(32888);
                  bma.b(bma.a);
               }

               if (tess.n) {
                  tess.d.position(20);
                  GL11.glColorPointer(4, true, 64, tess.d);
                  GL11.glEnableClientState(32886);
               }

               if (tess.q) {
                  tess.f.position(8);
                  GL11.glNormalPointer(64, tess.f);
                  GL11.glEnableClientState(32885);
               }

               tess.f.position(0);
               GL11.glVertexPointer(3, 64, tess.f);
               preDrawArray(tess);
            }

            GL11.glEnableClientState(32884);
            GL11.glDrawArrays(realDrawMode, 0, vtc);
         }

         GL11.glDisableClientState(32884);
         postDrawArray(tess);
         if (tess.o) {
            GL11.glDisableClientState(32888);
         }

         if (tess.p) {
            bma.b(bma.b);
            GL11.glDisableClientState(32888);
            bma.b(bma.a);
         }

         if (tess.n) {
            GL11.glDisableClientState(32886);
         }

         if (tess.q) {
            GL11.glDisableClientState(32885);
         }

         vtc = tess.r * 4;
         return vtc;
      }
   }

   public static void preDrawArray(bfq tess) {
      if (Shaders.useMultiTexCoord3Attrib && tess.o) {
         GL13.glClientActiveTexture(33987);
         GL11.glTexCoordPointer(2, 64, (FloatBuffer)tess.f.position(11));
         GL11.glEnableClientState(32888);
         GL13.glClientActiveTexture(33984);
      }

      if (Shaders.useMidTexCoordAttrib && tess.o) {
         ARBVertexShader.glVertexAttribPointerARB(Shaders.midTexCoordAttrib, 2, false, 64, (FloatBuffer)tess.f.position(11));
         ARBVertexShader.glEnableVertexAttribArrayARB(Shaders.midTexCoordAttrib);
      }

      if (Shaders.useEntityAttrib) {
         ARBVertexShader.glVertexAttribPointerARB(Shaders.entityAttrib, 2, false, false, 64, (ShortBuffer)tess.g.position(12));
         ARBVertexShader.glEnableVertexAttribArrayARB(Shaders.entityAttrib);
      }

   }

   public static void preDrawArrayVBO(bfq tess) {
      if (Shaders.useMultiTexCoord3Attrib && tess.o) {
         GL13.glClientActiveTexture(33987);
         GL11.glTexCoordPointer(2, 5126, 64, 44L);
         GL11.glEnableClientState(32888);
         GL13.glClientActiveTexture(33984);
      }

      if (Shaders.useMidTexCoordAttrib && tess.o) {
         ARBVertexShader.glVertexAttribPointerARB(Shaders.midTexCoordAttrib, 2, 5126, false, 64, 44L);
         ARBVertexShader.glEnableVertexAttribArrayARB(Shaders.midTexCoordAttrib);
      }

      if (Shaders.useEntityAttrib) {
         ARBVertexShader.glVertexAttribPointerARB(Shaders.entityAttrib, 2, 5122, false, 64, 24L);
         ARBVertexShader.glEnableVertexAttribArrayARB(Shaders.entityAttrib);
      }

   }

   public static void postDrawArray(bfq tess) {
      if (Shaders.useEntityAttrib) {
         ARBVertexShader.glDisableVertexAttribArrayARB(Shaders.entityAttrib);
      }

      if (Shaders.useMidTexCoordAttrib && tess.o) {
         ARBVertexShader.glDisableVertexAttribArrayARB(Shaders.midTexCoordAttrib);
      }

      if (Shaders.useMultiTexCoord3Attrib && tess.o) {
         GL13.glClientActiveTexture(33987);
         GL11.glDisableClientState(32888);
         GL13.glClientActiveTexture(33984);
      }

   }

   public static void addVertex(bfq tess, double parx, double pary, double parz) {
      int[] rawBuffer = tess.h;
      int rbi = tess.r;
      float fx = (float)(parx + tess.v);
      float fy = (float)(pary + tess.w);
      float fz = (float)(parz + tess.x);
      if (rbi >= TessellatorExtra.bufferSize - 64) {
         if (TessellatorExtra.bufferSize >= 16777216) {
            if (tess.s % 4 == 0) {
               tess.a();
               tess.z = true;
            }
         } else if (TessellatorExtra.bufferSize > 0) {
            TessellatorExtra.bufferSize *= 2;
            tess.h = rawBuffer = Arrays.copyOf(tess.h, TessellatorExtra.bufferSize);
            System.out.format("Expand tesselator buffer %d\n", TessellatorExtra.bufferSize);
         } else {
            TessellatorExtra.bufferSize = 65536;
            tess.h = rawBuffer = new int[TessellatorExtra.bufferSize];
         }
      }

      if (tess.u == 7) {
         int i = tess.s % 4;
         float[] vertexPos = tess.vertexPos;
         vertexPos[i * 4 + 0] = fx;
         vertexPos[i * 4 + 1] = fy;
         vertexPos[i * 4 + 2] = fz;
         if (i == 3) {
            float x1 = vertexPos[8] - vertexPos[0];
            float y1 = vertexPos[9] - vertexPos[1];
            float z1 = vertexPos[10] - vertexPos[2];
            float x2 = vertexPos[12] - vertexPos[4];
            float y2 = vertexPos[13] - vertexPos[5];
            float z2 = vertexPos[14] - vertexPos[6];
            float vnx = y1 * z2 - y2 * z1;
            float vny = z1 * x2 - z2 * x1;
            float vnz = x1 * y2 - x2 * y1;
            float lensq = vnx * vnx + vny * vny + vnz * vnz;
            float mult = (double)lensq != 0.0D ? (float)(1.0D / Math.sqrt((double)lensq)) : 1.0F;
            rawBuffer[rbi + -40] = rawBuffer[rbi + -24] = rawBuffer[rbi + -8] = Float.floatToRawIntBits(tess.normalX = vnx * mult);
            rawBuffer[rbi + -39] = rawBuffer[rbi + -23] = rawBuffer[rbi + -7] = Float.floatToRawIntBits(tess.normalY = vny * mult);
            rawBuffer[rbi + -38] = rawBuffer[rbi + -22] = rawBuffer[rbi + -6] = Float.floatToRawIntBits(tess.normalZ = vnz * mult);
            tess.q = true;
            tess.midTextureU = (Float.intBitsToFloat(rawBuffer[rbi + -45]) + Float.intBitsToFloat(rawBuffer[rbi + -29]) + Float.intBitsToFloat(rawBuffer[rbi + -13]) + (float)tess.j) / 4.0F;
            tess.midTextureV = (Float.intBitsToFloat(rawBuffer[rbi + -44]) + Float.intBitsToFloat(rawBuffer[rbi + -28]) + Float.intBitsToFloat(rawBuffer[rbi + -12]) + (float)tess.k) / 4.0F;
            rawBuffer[rbi + -37] = rawBuffer[rbi + -21] = rawBuffer[rbi + -5] = Float.floatToRawIntBits(tess.midTextureU);
            rawBuffer[rbi + -36] = rawBuffer[rbi + -20] = rawBuffer[rbi + -4] = Float.floatToRawIntBits(tess.midTextureV);
            if ((Boolean)Utils.get(bfq.class, "b", Boolean.TYPE)) {
               System.arraycopy(rawBuffer, rbi - 48, rawBuffer, rbi, 16);
               System.arraycopy(rawBuffer, rbi - 16, rawBuffer, rbi + 16, 16);
               rbi += 32;
               tess.r = rbi;
               tess.i += 2;
            }
         }
      }

      ++tess.s;
      rawBuffer[rbi + 0] = Float.floatToRawIntBits(fx);
      rawBuffer[rbi + 1] = Float.floatToRawIntBits(fy);
      rawBuffer[rbi + 2] = Float.floatToRawIntBits(fz);
      rawBuffer[rbi + 3] = Float.floatToRawIntBits((float)tess.j);
      rawBuffer[rbi + 4] = Float.floatToRawIntBits((float)tess.k);
      rawBuffer[rbi + 5] = tess.m;
      rawBuffer[rbi + 6] = Shaders.getEntityData();
      rawBuffer[rbi + 7] = tess.l;
      rawBuffer[rbi + 8] = Float.floatToRawIntBits(tess.normalX);
      rawBuffer[rbi + 9] = Float.floatToRawIntBits(tess.normalY);
      rawBuffer[rbi + 10] = Float.floatToRawIntBits(tess.normalZ);
      rawBuffer[rbi + 11] = Float.floatToRawIntBits(tess.midTextureU);
      rawBuffer[rbi + 12] = Float.floatToRawIntBits(tess.midTextureV);
      rbi += 16;
      tess.r = rbi;
      ++tess.i;
   }
}
