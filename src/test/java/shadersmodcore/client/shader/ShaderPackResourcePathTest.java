package shadersmodcore.client.shader;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ShaderPackResourcePathTest {
    private ShaderPackResourcePathTest() {
    }

    public static void main(String[] args) throws Exception {
        Path root = Files.createTempDirectory("shadersmodcore-pack");
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
        } finally {
            Files.deleteIfExists(root.resolve("shaders/test.vsh"));
            Files.deleteIfExists(root.resolve("shaders"));
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
