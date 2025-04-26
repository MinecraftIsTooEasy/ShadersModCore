package shadersmodcore.api;

import shadersmodcore.client.shader.ShadersTess;

public interface TessellatorAccessor0 {

//    float getNormalX();
//
//    float getNormalY();
//
//    float getNormalZ();
//
//    float[] getVertexPos();
//
//    void setVertexPos(float[] vertexPos);
//
//    float getMidTextureU();
//
//    float getMidTextureV();
//
//    void setMidTextureU(float midTextureU);
//
//    void setMidTextureV(float midTextureV);
//
//    float setNormalX(float normalX);
//
//    float setNormalY(float normalY);
//
//    float setNormalZ(float normalZ);

    default ShadersTess getShadersTess() {
        return null;
    }
}
