package net.wenscHuix.mitemod.mixin.render;

import net.minecraft.Tessellator;
import net.wenscHuix.mitemod.imixin.TessellatorAccessor0;
import net.wenscHuix.mitemod.shader.client.ShadersTess;
import net.xiaoyu233.fml.util.ReflectHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Tessellator.class)
public class TessellatorMixin implements TessellatorAccessor0 {

   @Shadow private boolean useVBO;

   @Shadow private int vboCount;

   @Shadow private static boolean tryVBO;

   @Shadow public boolean hasNormals;

   @Shadow public double textureU;

   @Shadow public int normal;
   public float normalX;
   public float normalY;
   public float normalZ;
   public float midTextureU;
   public float midTextureV;
   public float[] vertexPos;

   public float mITE_Shader_Loader$getNormalX() { return this.normalX; }

   public float mITE_Shader_Loader$getNormalY() { return this.normalY; }

   public float mITE_Shader_Loader$getNormalZ() { return this.normalZ; }

   public float mITE_Shader_Loader$setNormalX(float normalX) { return this.normalX = normalX; }

   public float mITE_Shader_Loader$setNormalY(float normalY) { return this.normalY = normalY; }

   public float mITE_Shader_Loader$setNormalZ(float normalZ) { return this.normalZ = normalZ; }

   public float[] mITE_Shader_Loader$getVertexPos() { return this.vertexPos; }

   public void mITE_Shader_Loader$setVertexPos(float[] vertexPos) { this.vertexPos = vertexPos; }

   public float mITE_Shader_Loader$getMidTextureU() { return this.midTextureU; }

   public float mITE_Shader_Loader$getMidTextureV() { return this.midTextureV; }

   public void mITE_Shader_Loader$setMidTextureU(float midTextureU) { this.midTextureU = midTextureU; }

   public void mITE_Shader_Loader$setMidTextureV(float midTextureV) { this.midTextureV = midTextureV; }

   public boolean isUseVBO() {
      return this.useVBO;
   }

   public boolean setUseVBO(boolean is) {
      return this.useVBO = is;
   }

   public int getVboCount() {
      return this.vboCount;
   }

   public boolean tryVBO() {
      return tryVBO;
   }

   /**
    * @author
    * @reason
    */
   @Overwrite
   public void addVertex(double par1, double par3, double par5) {
      ShadersTess.addVertex(ReflectHelper.dyCast(this), par1, par3, par5);
   }

   /**
    * @author
    * @reason
    */
   @Overwrite
   public int draw() {
      return ShadersTess.draw(ReflectHelper.dyCast(Tessellator.class, this));
   }

   /**
    * @author
    * @reason
    */
   @Overwrite
   public final void setNormal(float par1, float par2, float par3) {
      this.hasNormals = true;
      this.normalX = par1;
      this.normalY = par2;
      this.normalZ = par3;
   }
}
