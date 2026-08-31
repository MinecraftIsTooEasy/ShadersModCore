package shadersmodcore.client.shader;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public final class ShaderPackResourcePathTest {
    private ShaderPackResourcePathTest() {
    }

    public static void main(String[] args) throws Exception {
        Path root = Files.createTempDirectory("shadersmodcore-pack");
        ShaderPackZip zipPack = null;
        try {
            Path shader = root.resolve("shaders/test.vsh");
            Files.createDirectories(shader.getParent());
            Files.writeString(shader, "void main() {}", StandardCharsets.UTF_8);

            ShaderPackFolder pack = new ShaderPackFolder("fixture", root.toFile());
            check(read(pack.getResourceAsStream("/shaders/test.vsh")).equals("void main() {}"),
                "a slash-prefixed shader path must load");
            check(read(pack.getResourceAsStream("shaders/test.vsh")).equals("void main() {}"),
                "a shader path without a leading slash must load");
            check(pack.getResourceAsStream("/shaders/missing.fsh") == null,
                "a missing shader resource must return null");
            check(pack.getResourceAsStream("/shaders/") == null,
                "a shader directory must not be opened as a resource stream");
            check(pack.getResourceAsStream(null) == null,
                "a null shader resource path must return null");

            Path zip = root.resolve("pack.zip");
            try (ZipOutputStream output = new ZipOutputStream(Files.newOutputStream(zip))) {
                output.putNextEntry(new ZipEntry("shaders/test.vsh"));
                output.write("void main() {}".getBytes(StandardCharsets.UTF_8));
                output.closeEntry();
            }

            zipPack = new ShaderPackZip("fixture.zip", zip.toFile());
            check(read(zipPack.getResourceAsStream("/shaders/test.vsh")).equals("void main() {}"),
                "a slash-prefixed zip shader path must load");
            check(read(zipPack.getResourceAsStream("shaders/test.vsh")).equals("void main() {}"),
                "a zip shader path without a leading slash must load");
            check(zipPack.getResourceAsStream("/shaders/missing.fsh") == null,
                "a missing zip shader resource must return null");
            check(zipPack.getResourceAsStream(null) == null,
                "a null zip shader resource path must return null");

            Path nestedZip = root.resolve("nested-pack.zip");
            try (ZipOutputStream output = new ZipOutputStream(Files.newOutputStream(nestedZip))) {
                output.putNextEntry(new ZipEntry("ExamplePack/"));
                output.closeEntry();
                output.putNextEntry(new ZipEntry("ExamplePack/shaders/"));
                output.closeEntry();
                output.putNextEntry(new ZipEntry("ExamplePack/shaders/test.vsh"));
                output.write("void main() {}".getBytes(StandardCharsets.UTF_8));
                output.closeEntry();
            }

            ShaderPackZip nestedPack = new ShaderPackZip("nested-pack.zip", nestedZip.toFile());
            try {
                check(read(nestedPack.getResourceAsStream("/shaders/test.vsh")).equals("void main() {}"),
                    "a zip shader path under a single top-level folder must load");
            } finally {
                nestedPack.close();
            }
        } finally {
            if (zipPack != null) {
                zipPack.close();
            }
            Files.deleteIfExists(root.resolve("shaders/test.vsh"));
            Files.deleteIfExists(root.resolve("shaders"));
            Files.deleteIfExists(root.resolve("pack.zip"));
            Files.deleteIfExists(root.resolve("nested-pack.zip"));
            Files.deleteIfExists(root);
        }

        System.out.println("ShaderPackResourcePathTest passed");
    }

    private static String read(InputStream input) throws Exception {
        check(input != null, "expected shader resource stream");
        try (InputStream stream = input) {
            return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
