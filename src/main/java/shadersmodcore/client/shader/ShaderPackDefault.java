package shadersmodcore.client.shader;

import java.io.InputStream;

public class ShaderPackDefault implements IShaderPack {
   public void close() {
   }

   public InputStream getResourceAsStream(String resName) {
      return ShaderPackDefault.class.getResourceAsStream(resName);
   }
}
