package shadersmodcore.util;

import net.minecraft.GLAllocation;
import net.minecraft.Tessellator;
import shadersmodcore.api.IShaderTessellator;
import shadersmodcore.mixin.accessor.TessellatorAccessor;
import shadersmodcore.client.shader.ShadersTess;
import org.lwjgl.opengl.ARBVertexBufferObject;
import org.lwjgl.opengl.GLContext;

public class TessellatorExtra extends Tessellator {
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

        // Quad normal generation only needs four positions with three coordinates each.
        ((IShaderTessellator) this).setVertexPos(new float[TessellatorBufferGrowth.VERTEX_SCRATCH_SIZE]);
        this.shadersTess = new ShadersTess();
    }
}
