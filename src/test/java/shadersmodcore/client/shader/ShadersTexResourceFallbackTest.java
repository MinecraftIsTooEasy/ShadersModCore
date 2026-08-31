package shadersmodcore.client.shader;

import net.minecraft.FallbackResourceManager;
import net.minecraft.MetadataSection;
import net.minecraft.Resource;
import net.minecraft.ResourceLocation;
import net.minecraft.ResourceManager;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Set;

public final class ShadersTexResourceFallbackTest {
    private ShadersTexResourceFallbackTest() {
    }

    public static void main(String[] args) throws Exception {
        ResourceLocation location = new ResourceLocation("minecraft", "textures/block/stone.png");

        int[] nullLocation = new int[2];
        ShadersTex.loadNSMap1(resourceManager(null, null), null, 1, 1, nullLocation, 0, 0x12345678);
        check(nullLocation[0] == 0x12345678,
            "a null auxiliary-map location must use the default color");

        int[] nullManager = new int[2];
        ShadersTex.loadNSMap1(null, location, 1, 1, nullManager, 0, 0x13572468);
        check(nullManager[0] == 0x13572468,
            "a null resource manager must use the default color");

        int[] missing = new int[2];
        ShadersTex.loadNSMap1(new FallbackResourceManager(null), location, 1, 1, missing, 0, 0x23456789);
        check(missing[0] == 0x23456789,
            "a missing auxiliary-map resource must use the default color");

        int[] nullResource = new int[2];
        ShadersTex.loadNSMap1(resourceManagerWithResource(location, null), location,
            1, 1, nullResource, 0, 0x2A3B4C5D);
        check(nullResource[0] == 0x2A3B4C5D,
            "a null resource result must use the default color");

        int[] invalid = new int[2];
        ShadersTex.loadNSMap1(resourceManager(location, new byte[]{1, 2, 3}), location,
            1, 1, invalid, 0, 0x3456789A);
        check(invalid[0] == 0x3456789A,
            "a non-image auxiliary-map resource must use the default color");

        int[] empty = new int[2];
        ShadersTex.loadNSMap1(resourceManager(location, new byte[0]), location,
            1, 1, empty, 0, 0x456789AB);
        check(empty[0] == 0x456789AB,
            "an empty auxiliary-map stream must use the default color");

        int[] wrongSize = new int[2];
        ShadersTex.loadNSMap1(resourceManager(location, png(2, 1, 0xFF102030)), location,
            1, 1, wrongSize, 0, 0x56789ABC);
        check(wrongSize[0] == 0x56789ABC,
            "an auxiliary map with mismatched dimensions must use the default color");

        boolean[] closed = {false};
        int[] valid = {0x0BADBEEF, 0, 0x0BADBEEF};
        byte[] image = onePixelPng(0xFF102030);
        ShadersTex.loadNSMap1(resourceManager(location, image, closed), location, 1, 1, valid, 1, 0);
        check(valid[0] == 0x0BADBEEF && valid[1] == 0xFF102030 && valid[2] == 0x0BADBEEF,
            "a valid auxiliary-map resource must populate only the requested page offset");
        check(closed[0], "an auxiliary-map input stream must be closed after decoding");

        ResourceLocation modLocation = new ResourceLocation("examplemod", "textures/block/stone.png");
        int[] customDomain = new int[2];
        ShadersTex.loadNSMap1(resourceManager(modLocation, image), modLocation,
            1, 1, customDomain, 0, 0);
        check(customDomain[0] == 0xFF102030,
            "an auxiliary-map resource domain must be passed through unchanged");

        expectThrows(IllegalStateException.class,
            () -> ShadersTex.loadNSMap1(throwingResourceManager(), location, 1, 1,
                new int[2], 0, 0x6789ABCD));
        expectThrows(IllegalStateException.class,
            () -> ShadersTex.loadNSMap1(streamThrowingResourceManager(location), location, 1, 1,
                new int[2], 0, 0x789ABCDE));
        expectThrows(IndexOutOfBoundsException.class,
            () -> ShadersTex.loadNSMap1(resourceManager(location, image), location, 1, 1,
                new int[0], 0, 0x89ABCDEF));

        System.out.println("ShadersTexResourceFallbackTest passed");
    }

    private static ResourceManager resourceManager(ResourceLocation expected, byte[] data) {
        return resourceManager(expected, data, null);
    }

    private static ResourceManager resourceManager(ResourceLocation expected, byte[] data, boolean[] closed) {
        return new ResourceManager() {
            @Override
            public Set getResourceDomains() {
                return Collections.singleton(expected == null ? "minecraft" : expected.getResourceDomain());
            }

            @Override
            public Resource getResource(ResourceLocation location) {
                if (!expected.equals(location)) {
                    throw new AssertionError("unexpected resource location: " + location);
                }
                return new Resource() {
                    @Override
                    public InputStream getInputStream() {
                        if (data == null) {
                            return null;
                        }
                        InputStream input = new ByteArrayInputStream(data);
                        if (closed == null) {
                            return input;
                        }
                        return new InputStream() {
                            @Override
                            public int read() throws IOException {
                                return input.read();
                            }

                            @Override
                            public int read(byte[] bytes, int offset, int length) throws IOException {
                                return input.read(bytes, offset, length);
                            }

                            @Override
                            public void close() throws IOException {
                                closed[0] = true;
                                input.close();
                            }
                        };
                    }

                    @Override
                    public boolean hasMetadata() {
                        return false;
                    }

                    @Override
                    public MetadataSection getMetadata(String sectionName) {
                        return null;
                    }
                };
            }

            @Override
            public java.util.List getAllResources(ResourceLocation location) {
                return Collections.emptyList();
            }
        };
    }

    private static ResourceManager throwingResourceManager() {
        return new ResourceManager() {
            @Override
            public Set getResourceDomains() {
                return Collections.singleton("minecraft");
            }

            @Override
            public Resource getResource(ResourceLocation location) {
                throw new IllegalStateException("resource manager failure");
            }

            @Override
            public java.util.List getAllResources(ResourceLocation location) {
                return Collections.emptyList();
            }
        };
    }

    private static ResourceManager streamThrowingResourceManager(ResourceLocation expected) {
        return resourceManagerWithResource(expected, new Resource() {
            @Override
            public InputStream getInputStream() {
                throw new IllegalStateException("resource stream failure");
            }

            @Override
            public boolean hasMetadata() {
                return false;
            }

            @Override
            public MetadataSection getMetadata(String sectionName) {
                return null;
            }
        });
    }

    private static ResourceManager resourceManagerWithResource(ResourceLocation expected, Resource resource) {
        return new ResourceManager() {
            @Override
            public Set getResourceDomains() {
                return Collections.singleton(expected.getResourceDomain());
            }

            @Override
            public Resource getResource(ResourceLocation location) {
                if (!expected.equals(location)) {
                    throw new AssertionError("unexpected resource location: " + location);
                }
                return resource;
            }

            @Override
            public java.util.List getAllResources(ResourceLocation location) {
                return Collections.emptyList();
            }
        };
    }

    private static byte[] onePixelPng(int color) throws IOException {
        return png(1, 1, color);
    }

    private static byte[] png(int width, int height, int color) throws IOException {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                image.setRGB(x, y, color);
            }
        }
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ImageIO.write(image, "png", output);
        return output.toByteArray();
    }

    private static <T extends Throwable> void expectThrows(Class<T> type, Runnable action) {
        try {
            action.run();
        } catch (Throwable thrown) {
            if (type.isInstance(thrown)) {
                return;
            }
            throw new AssertionError("expected " + type.getName() + " but got "
                + thrown.getClass().getName(), thrown);
        }
        throw new AssertionError("expected " + type.getName() + " to be thrown");
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
