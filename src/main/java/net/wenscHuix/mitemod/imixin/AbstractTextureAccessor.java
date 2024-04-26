package net.wenscHuix.mitemod.imixin;

import net.wenscHuix.mitemod.shader.client.MultiTexID;

public interface AbstractTextureAccessor {
    int mITE_Shader_Loader$getGlTextureId();

    void mITE_Shader_Loader$setGlTextureId(int id);

    MultiTexID mITE_Shader_Loader$getMultiTexID();

    MultiTexID mITE_Shader_Loader$getMultiTexID0();

    void mITE_Shader_Loader$setMultiTexID(MultiTexID id);

}
