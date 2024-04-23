package net.wenscHuix.mitemod.shader.util;

import net.minecraft.atu;
import net.minecraft.bfq;
import net.wenscHuix.mitemod.mixin.render.TessellatorAccessor;
import net.wenscHuix.mitemod.shader.client.ShadersTess;
import org.lwjgl.opengl.ARBVertexBufferObject;
import org.lwjgl.opengl.GLContext;

public class TessellatorExtra extends bfq {
   public static int bufferSize = 2097152;

   public TessellatorExtra(int par1) {
      this.defaultTexture = false;
      this.rawBufferSize = 0;
      this.textureID = 0;
      this.d = atu.c(par1 * 4);
      this.e = this.d.asIntBuffer();
      this.f = this.d.asFloatBuffer();
      this.g = this.d.asShortBuffer();
      this.h = new int[par1];
      this.setuseVBO(TessellatorAccessor.gettryVBO() && GLContext.getCapabilities().GL_ARB_vertex_buffer_object);
      if (this.getuseVBO()) {
         Common.vertexBuffers = atu.f(Common.vboCount);
         ARBVertexBufferObject.glGenBuffersARB(Common.vertexBuffers);
      }

      this.vertexPos = new float[par1];
      this.shadersTess = new ShadersTess();
   }
}
