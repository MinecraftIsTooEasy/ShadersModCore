package shadersmodcore.api;

import net.minecraft.ResourceLocation;
import net.minecraft.ResourceManager;

import java.io.IOException;

public interface TextureAtlasSpriteAccessor {
    boolean load(ResourceManager manager, ResourceLocation location) throws IOException;
}
