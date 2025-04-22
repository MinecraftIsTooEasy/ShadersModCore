package shadersmodcore.api;

import net.minecraft.ResourceManager;

import java.io.IOException;

public interface TextureObjectAccessor {
    void loadTexture(ResourceManager resourceManager) throws IOException;

    MultiTexID getMultiTexID();
}
