package shadersmodcore.client.shader;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Set;
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
            check(read(pack.getResourceAsStream("/shaders/test.vsh/")).equals("void main() {}"),
                "a folder shader path may have a trailing slash");
            check(pack.getResourceAsStream("/shaders/missing.fsh") == null,
                "a missing shader resource must return null");
            check(pack.getResourceAsStream("/shaders/") == null,
                "a shader directory must not be opened as a resource stream");
            check(pack.getResourceAsStream("/") == null,
                "a folder root must not be opened as a resource stream");
            check(pack.getResourceAsStream(null) == null,
                "a null shader resource path must return null");

            Path unreadable = root.resolve("shaders/unreadable.vsh");
            Files.writeString(unreadable, "void main() {}", StandardCharsets.UTF_8);
            try {
                Set<PosixFilePermission> original = Files.getPosixFilePermissions(unreadable);
                try {
                    Files.setPosixFilePermissions(unreadable, Set.of());
                    check(pack.getResourceAsStream("/shaders/unreadable.vsh") == null,
                        "an inaccessible folder resource must return null");
                } finally {
                    Files.setPosixFilePermissions(unreadable, original);
                }
            } catch (UnsupportedOperationException | SecurityException ignored) {
                // File permission checks are unavailable on some filesystems.
            } finally {
                Files.deleteIfExists(unreadable);
            }

            Path zip = root.resolve("pack.zip");
            try (ZipOutputStream output = new ZipOutputStream(Files.newOutputStream(zip))) {
                output.putNextEntry(new ZipEntry("shaders/"));
                output.closeEntry();
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
            check(zipPack.getResourceAsStream("/shaders/") == null,
                "a zip shader directory must not be opened as a resource stream");
            check(zipPack.getResourceAsStream("/shaders/test.vsh/") == null,
                "a zip shader path keeps a trailing slash significant");
            check(zipPack.getResourceAsStream(null) == null,
                "a null zip shader resource path must return null");

            zipPack.close();
            check(read(zipPack.getResourceAsStream("/shaders/test.vsh")).equals("void main() {}"),
                "a closed zip pack must reopen lazily for the next resource");

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

            Path ambiguousZip = root.resolve("ambiguous-pack.zip");
            try (ZipOutputStream output = new ZipOutputStream(Files.newOutputStream(ambiguousZip))) {
                output.putNextEntry(new ZipEntry("FirstPack/shaders/"));
                output.closeEntry();
                output.putNextEntry(new ZipEntry("FirstPack/shaders/test.vsh"));
                output.write("first".getBytes(StandardCharsets.UTF_8));
                output.closeEntry();
                output.putNextEntry(new ZipEntry("SecondPack/shaders/"));
                output.closeEntry();
                output.putNextEntry(new ZipEntry("SecondPack/shaders/test.vsh"));
                output.write("second".getBytes(StandardCharsets.UTF_8));
                output.closeEntry();
            }

            ShaderPackZip ambiguousPack = new ShaderPackZip("ambiguous-pack.zip", ambiguousZip.toFile());
            try {
                check(ambiguousPack.getResourceAsStream("/shaders/test.vsh") == null,
                    "a zip with multiple top-level shader folders must not select one arbitrarily");
            } finally {
                ambiguousPack.close();
            }

            Path invalidZip = root.resolve("invalid-pack.zip");
            Files.writeString(invalidZip, "not a zip", StandardCharsets.UTF_8);
            ShaderPackZip invalidPack = new ShaderPackZip("invalid-pack.zip", invalidZip.toFile());
            try {
                check(invalidPack.getResourceAsStream("/shaders/test.vsh") == null,
                    "an invalid zip must fall back to a missing resource");
            } finally {
                invalidPack.close();
            }

            expectNullPointer(() -> new ShaderPackZip("broken.zip", null)
                    .getResourceAsStream("/shaders/test.vsh"),
                "a null zip file must not be swallowed as a resource miss");
        } finally {
            if (zipPack != null) {
                zipPack.close();
            }
            Files.deleteIfExists(root.resolve("shaders/test.vsh"));
            Files.deleteIfExists(root.resolve("shaders"));
            Files.deleteIfExists(root.resolve("pack.zip"));
            Files.deleteIfExists(root.resolve("nested-pack.zip"));
            Files.deleteIfExists(root.resolve("ambiguous-pack.zip"));
            Files.deleteIfExists(root.resolve("invalid-pack.zip"));
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

    private static void expectNullPointer(Runnable action, String message) {
        try {
            action.run();
        } catch (NullPointerException expected) {
            return;
        }
        throw new AssertionError(message);
    }

}
