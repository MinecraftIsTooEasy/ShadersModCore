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

public final class ShadersTexLayeredResourceTest {
    private ShadersTexLayeredResourceTest() {
    }

    public static void main(String[] args) throws Exception {
        ResourceLocation location = new ResourceLocation("minecraft", "textures/entity/layer.png");
        boolean[] closed = {false};
        BufferedImage image = ShadersTex.loadLayeredImage(resourceManager(location, png(), closed), location);
        check(image != null && image.getWidth() == 1 && image.getHeight() == 1,
            "a valid layered image must be decoded");
        check(closed[0], "a layered image input stream must be closed after decoding");

        check(ShadersTex.loadLayeredImage(resourceManager(location, null, null), location) == null,
            "a null layered resource stream must fall back without an image");

        System.out.println("ShadersTexLayeredResourceTest passed");
    }

    private static ResourceManager resourceManager(ResourceLocation expected, byte[] data, boolean[] closed) {
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

    private static byte[] png() throws IOException {
        BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        image.setRGB(0, 0, 0xFF102030);
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
