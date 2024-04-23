package net.wenscHuix.mitemod.imixin;

import net.minecraft.ResourceLocation;
import net.minecraft.ResourceManager;

import java.io.IOException;

public interface TextureAtlasSpriteAccessor {
    boolean mITE_Shader_Loader$load(ResourceManager manager, ResourceLocation location) throws IOException;
}
