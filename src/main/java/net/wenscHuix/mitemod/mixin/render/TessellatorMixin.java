package net.wenscHuix.mitemod.mixin.render;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import net.minecraft.bfq;
import net.wenscHuix.mitemod.shader.client.ShadersTess;
import net.xiaoyu233.fml.util.ReflectHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin({bfq.class})
public class TessellatorMixin {
   public ShadersTess shadersTess;
   public boolean defaultTexture;
   public int rawBufferSize;
   public int textureID;
   public float normalX;
   public float normalY;
   public float normalZ;
   public float midTextureU;
   public float midTextureV;
   public float[] vertexPos;
   @Shadow
   public IntBuffer B;
   @Shadow
   public ByteBuffer d;
   @Shadow
   public IntBuffer e;
   @Shadow
   public FloatBuffer f;
   @Shadow
   public ShortBuffer g;
   @Shadow
   public int[] h;
   @Shadow
   public int i;
   @Shadow
   public double j;
   @Shadow
   public double k;
   @Shadow
   public int l;
   @Shadow
   public int m;
   @Shadow
   public boolean q;
   @Shadow
   public int y;
   @Shadow
   private boolean A;
   @Shadow
   private int D;
   @Shadow
   private static boolean c;

   public boolean isUseVBO() {
      return this.A;
   }

   public boolean setUseVBO(boolean is) {
      return this.A = is;
   }

   public int getVboCount() {
      return this.D;
   }

   public boolean tryVBO() {
      return c;
   }

   @Overwrite
   public void a(double par1, double par3, double par5) {
      ShadersTess.addVertex((bfq)ReflectHelper.dyCast(this), par1, par3, par5);
   }

   @Overwrite
   public int a() {
      return ShadersTess.draw((bfq)ReflectHelper.dyCast(bfq.class, this));
   }

   @Overwrite
   public final void b(float par1, float par2, float par3) {
      this.q = true;
      this.normalX = par1;
      this.normalY = par2;
      this.normalZ = par3;
   }
}
