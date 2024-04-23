package net.wenscHuix.mitemod.mixin.render.texture;

import net.minecraft.ResourceManager;
import net.wenscHuix.mitemod.imixin.TextureObjectAccessor;
import net.wenscHuix.mitemod.shader.client.MultiTexID;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.io.IOException;

@Mixin({TextureObject.class})
public interface TextureObject extends TextureObjectAccessor {
   @Unique
   void mITE_Shader_Loader$loadTexture(ResourceManager resourceManager) throws IOException;

   @Unique
   MultiTexID mITE_Shader_Loader$getMultiTexID();
}
