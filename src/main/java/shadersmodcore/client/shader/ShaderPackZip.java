package shadersmodcore.client.shader;

import java.io.File;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ShaderPackZip implements IShaderPack {
    protected File packFile;
    protected ZipFile packZipFile;

    public ShaderPackZip(String name, File file) {
        this.packFile = file;
        this.packZipFile = null;
    }

    public void close() {
        if (this.packZipFile != null) {
            try {
                this.packZipFile.close();
            } catch (Exception var2) {
                var2.printStackTrace();
            }

            this.packZipFile = null;
        }

    }

    public InputStream getResourceAsStream(String resName) {
        try {
            if (resName == null) {
                return null;
            }

            if (this.packZipFile == null) {
                this.packZipFile = new ZipFile(this.packFile);
            }

            int start = resName.startsWith("/") ? 1 : 0;
            String path = resName.substring(start);
            ZipEntry entry = this.packZipFile.getEntry(path);
            if (entry != null) {
                return this.packZipFile.getInputStream(entry);
            }
        } catch (Exception ignored) {
            // Missing or inaccessible shader resources use the normal fallback path.
        }

        return null;
    }
}
