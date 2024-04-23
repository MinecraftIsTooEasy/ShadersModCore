package net.wenscHuix.mitemod.imixin;

import net.minecraft.ResourceManager;
import net.wenscHuix.mitemod.shader.client.MultiTexID;

import java.io.IOException;

public interface TextureObjectAccessor {
    void mITE_Shader_Loader$loadTexture(ResourceManager resourceManager) throws IOException;

    MultiTexID mITE_Shader_Loader$getMultiTexID();
}
