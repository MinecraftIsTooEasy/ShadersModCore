package shadersmodcore.client.shader;

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

        int[] missing = new int[2];
        ShadersTex.loadNSMap1(resourceManager(location, null), location, 1, 1, missing, 0, 0x23456789);
        check(missing[0] == 0x23456789,
            "a missing auxiliary-map resource must use the default color");

        int[] invalid = new int[2];
        ShadersTex.loadNSMap1(resourceManager(location, new byte[]{1, 2, 3}), location,
            1, 1, invalid, 0, 0x3456789A);
        check(invalid[0] == 0x3456789A,
            "a non-image auxiliary-map resource must use the default color");

        int[] valid = new int[2];
        byte[] image = onePixelPng(0xFF102030);
        ShadersTex.loadNSMap1(resourceManager(location, image), location, 1, 1, valid, 0, 0);
        check(valid[0] == 0xFF102030,
            "a valid auxiliary-map resource must still populate the source pixels");

        System.out.println("ShadersTexResourceFallbackTest passed");
    }

    private static ResourceManager resourceManager(ResourceLocation expected, byte[] data) {
        return new ResourceManager() {
            @Override
            public Set getResourceDomains() {
                return Collections.singleton("minecraft");
            }

            @Override
            public Resource getResource(ResourceLocation location) {
                if (data == null) {
                    throw new RuntimeException("resource missing");
                }
                if (!expected.equals(location)) {
                    throw new AssertionError("unexpected resource location: " + location);
                }
                return new Resource() {
                    @Override
                    public InputStream getInputStream() {
                        return new ByteArrayInputStream(data);
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

    private static byte[] onePixelPng(int color) throws IOException {
        BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        image.setRGB(0, 0, color);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ImageIO.write(image, "png", output);
        return output.toByteArray();
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
