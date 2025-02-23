package shadersmodcore.mixin.client.render;

import net.minecraft.Tessellator;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import shadersmodcore.api.TessellatorAccessor0;
import shadersmodcore.client.shader.Shaders;
import shadersmodcore.client.shader.ShadersTess;
import net.xiaoyu233.fml.util.ReflectHelper;

@Mixin(Tessellator.class)
public class TessellatorMixin implements TessellatorAccessor0 {
   @Shadow public boolean hasNormals;

   @Unique public float normalX;
   @Unique public float normalY;
   @Unique public float normalZ;
   @Unique public float midTextureU;
   @Unique public float midTextureV;
   @Unique public float[] vertexPos;

   @Override
   public float getNormalX() { return this.normalX; }
   @Override
   public float getNormalY() { return this.normalY; }
   @Override
   public float getNormalZ() { return this.normalZ; }
   @Override
   public float setNormalX(float normalX) { return this.normalX = normalX; }
   @Override
   public float setNormalY(float normalY) { return this.normalY = normalY; }
   @Override
   public float setNormalZ(float normalZ) { return this.normalZ = normalZ; }
   @Override
   public float[] getVertexPos() { return this.vertexPos; }
   @Override
   public void setVertexPos(float[] vertexPos) { this.vertexPos = vertexPos; }
   @Override
   public float getMidTextureU() { return this.midTextureU; }
   @Override
   public float getMidTextureV() { return this.midTextureV; }
   @Override
   public void setMidTextureU(float midTextureU) { this.midTextureU = midTextureU; }
   @Override
   public void setMidTextureV(float midTextureV) { this.midTextureV = midTextureV; }

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
