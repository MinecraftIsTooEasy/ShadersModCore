package shadersmodcore.client.shader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ShaderPackZip implements IShaderPack {
    protected File packFile;
    protected ZipFile packZipFile;
    protected String baseFolder;

    public ShaderPackZip(String name, File file) {
        this.packFile = file;
        this.packZipFile = null;
        this.baseFolder = "";
    }

    public void close() {
        if (this.packZipFile != null) {
            try {
                this.packZipFile.close();
            } catch (IOException ignored) {
                // Closing is best effort; the pack is still reset below.
            } finally {
                this.packZipFile = null;
                this.baseFolder = "";
            }
        }

    }

    public InputStream getResourceAsStream(String resName) {
        try {
            if (resName == null) {
                return null;
            }

            if (this.packZipFile == null) {
                this.packZipFile = new ZipFile(this.packFile);
                this.baseFolder = detectBaseFolder(this.packZipFile);
            }

            int start = resName.startsWith("/") ? 1 : 0;
            String path = resName.substring(start);
            if (this.baseFolder == null) {
                return null;
            }
            ZipEntry entry = this.packZipFile.getEntry(this.baseFolder + path);
            if (entry != null && !entry.isDirectory()) {
                return this.packZipFile.getInputStream(entry);
            }
        } catch (IOException | SecurityException ignored) {
            // Missing or inaccessible shader resources use the normal fallback path.
        }

        return null;
    }

    private String detectBaseFolder(ZipFile zip) {
        String candidate = null;
        Enumeration<? extends ZipEntry> entries = zip.entries();
        while (entries.hasMoreElements()) {
            String name = entries.nextElement().getName();
            if (name.equals("shaders/") || name.startsWith("shaders/")) {
                return "";
            }

            int marker = name.indexOf("/shaders/");
            if (marker > 0) {
                String prefix = name.substring(0, marker + 1);
                if (prefix.substring(0, prefix.length() - 1).indexOf('/') < 0) {
                    if (candidate == null) {
                        candidate = prefix;
                    } else if (!candidate.equals(prefix)) {
                        return null;
                    }
                }
            }
        }

        return candidate == null ? "" : candidate;
    }
}
