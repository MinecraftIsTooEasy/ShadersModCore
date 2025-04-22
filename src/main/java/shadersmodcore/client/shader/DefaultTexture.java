package shadersmodcore.client.shader;

import net.minecraft.AbstractTexture;
import net.minecraft.ResourceManager;

public class DefaultTexture extends AbstractTexture {
    public DefaultTexture() {
        this.loadTexture(null);
    }

    @Override
    public void loadTexture(ResourceManager resourcemanager) {
        int[] aint = ShadersTex.createAIntImage(1, -1);
        ShadersTex.setupTexture(ReflectionHandler.getMultiTexIDByField(this), aint, 1, 1, false, false);
    }
}
