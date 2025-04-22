package shadersmodcore.client.shader;

import java.io.InputStream;

public class ShaderPackDefault implements IShaderPack {
    @Override
    public void close() {
    }

    @Override
    public InputStream getResourceAsStream(String resName) {
        return ShaderPackDefault.class.getResourceAsStream(resName);
    }
}
