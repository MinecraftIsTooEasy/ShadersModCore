package shadersmodcore.client.shader;

import java.io.InputStream;

public class ShaderPackNone implements IShaderPack {
   public void close() {
   }

   public InputStream getResourceAsStream(String resName) {
      return null;
   }
}
