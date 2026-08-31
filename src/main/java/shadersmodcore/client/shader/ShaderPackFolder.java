package shadersmodcore.client.shader;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ShaderPackFolder implements IShaderPack {
    protected File packFile;

    public ShaderPackFolder(String name, File file) {
        this.packFile = file;
    }

    public void close() {
    }

    public InputStream getResourceAsStream(String resName) {
        if (resName == null) {
            return null;
        }

        int start = resName.startsWith("/") ? 1 : 0;
        int end = resName.endsWith("/") ? resName.length() - 1 : resName.length();
        String path = resName.substring(start, Math.max(start, end));
        try {
            File packRoot = this.packFile.getCanonicalFile();
            File resFile = new File(packRoot, path).getCanonicalFile();
            if (resFile.equals(packRoot) || !resFile.toPath().startsWith(packRoot.toPath())) {
                return null;
            }
            if (!resFile.isFile()) {
                return null;
            }
            return new BufferedInputStream(new FileInputStream(resFile));
        } catch (IOException | SecurityException ignored) {
            // Missing or inaccessible shader resources use the normal fallback path.
            return null;
        }
    }
}
