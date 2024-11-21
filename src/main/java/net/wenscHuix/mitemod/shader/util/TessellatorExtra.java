package net.wenscHuix.mitemod.shader.util;

import net.minecraft.GLAllocation;
import net.minecraft.Tessellator;
import net.wenscHuix.mitemod.imixin.TessellatorAccessor0;
import net.wenscHuix.mitemod.mixin.render.TessellatorAccessor;
import net.wenscHuix.mitemod.shader.client.ShadersTess;
import org.lwjgl.opengl.ARBVertexBufferObject;
import org.lwjgl.opengl.GLContext;

public class TessellatorExtra extends Tessellator {
   public static int bufferSize = 2097152;
   public boolean defaultTexture;
   public int rawBufferSize;
   public int textureID;

   public ShadersTess shadersTess;

   public TessellatorExtra(int par1) {
      this.defaultTexture = false;
      this.rawBufferSize = 0;
      this.textureID = 0;
      this.byteBuffer = GLAllocation.createDirectByteBuffer(par1 * 4);
      this.intBuffer = this.byteBuffer.asIntBuffer();
      this.floatBuffer = this.byteBuffer.asFloatBuffer();
      this.shortBuffer = this.byteBuffer.asShortBuffer();
      this.rawBuffer = new int[par1];
      ((TessellatorAccessor) this).setUseVBO(TessellatorAccessor.getTryVBO() && GLContext.getCapabilities().GL_ARB_vertex_buffer_object);
      if (((TessellatorAccessor) this).getUseVBO()) {
         Common.vertexBuffers = GLAllocation.createDirectIntBuffer(Common.vboCount);
         ARBVertexBufferObject.glGenBuffersARB(Common.vertexBuffers);
      }

      ((TessellatorAccessor0) this).mITE_Shader_Loader$setVertexPos(new float[par1]);
      this.shadersTess = new ShadersTess();
   }
}
