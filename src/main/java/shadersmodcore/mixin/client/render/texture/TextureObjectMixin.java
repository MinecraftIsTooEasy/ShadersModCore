package shadersmodcore.mixin.client.render.texture;

import net.minecraft.ResourceManager;
import net.minecraft.TextureObject;
import shadersmodcore.api.TextureObjectAccessor;
import shadersmodcore.client.shader.MultiTexID;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.io.IOException;

@Mixin({TextureObject.class})
public interface TextureObjectMixin extends TextureObjectAccessor {
   @Unique
   void loadTexture(ResourceManager resourceManager) throws IOException;

   @Unique
   MultiTexID getMultiTexID();
}
