package shadersmodcore.client.shader;

import java.io.InputStream;

public interface IShaderPack {
   void close();

   InputStream getResourceAsStream(String var1);
}
