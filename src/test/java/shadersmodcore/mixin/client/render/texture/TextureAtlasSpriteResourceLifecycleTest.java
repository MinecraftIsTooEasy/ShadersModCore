package shadersmodcore.mixin.client.render.texture;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.MetadataSection;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;

public final class TextureAtlasSpriteResourceLifecycleTest {
    private TextureAtlasSpriteResourceLifecycleTest() {
    }

    public static void main(String[] args) throws Exception {
        boolean[] closed = {false};
        BufferedImage image = TextureAtlasSpriteMixin.readImageAndClose(
            trackedInput(png(), closed), values -> decode((InputStream) values[0]));
        check(image != null && image.getWidth() == 1 && image.getHeight() == 1,
            "a valid atlas image must still be decoded");
        check(closed[0], "a successful atlas image decode must close its input stream");

        boolean[] failedClosed = {false};
        try {
            TextureAtlasSpriteMixin.readImageAndClose(
                trackedInput(new byte[] {1, 2, 3}, failedClosed),
                stream -> {
                    throw new IllegalStateException("decode failed");
                });
            throw new AssertionError("a failed atlas decode must propagate the failure");
        } catch (IllegalStateException expected) {
            check(failedClosed[0], "a failed atlas image decode must close its input stream");
        }

        boolean[] metadataSuccessClosed = {false};
        FakeResource metadataSuccessResource = new FakeResource(
            trackedInput(new byte[] {7, 8, 9}, metadataSuccessClosed));
        TextureAtlasSpriteMixin.readMetadataAndCloseOnFailure(
            metadataSuccessResource,
            "animation",
            metadataSuccessResource.getInputStream(),
            values -> null);
        check(!metadataSuccessClosed[0], "a successful atlas metadata read must leave decoding ownership unchanged");
        metadataSuccessResource.getInputStream().close();

        boolean[] metadataClosed = {false};
        FakeResource metadataResource = new FakeResource(
            trackedInput(new byte[] {4, 5, 6}, metadataClosed));
        try {
            TextureAtlasSpriteMixin.readMetadataAndCloseOnFailure(
                metadataResource,
                "animation",
                metadataResource.getInputStream(),
                values -> {
                    throw new IllegalStateException("metadata failed");
                });
            throw new AssertionError("a failed atlas metadata read must propagate the failure");
        } catch (IllegalStateException expected) {
            check(metadataClosed[0], "a failed atlas metadata read must close its input stream");
        }

        System.out.println("TextureAtlasSpriteResourceLifecycleTest passed");
    }

    private static BufferedImage decode(InputStream input) {
        try {
            return ImageIO.read(input);
        } catch (IOException exception) {
            throw new UncheckedIOException(exception);
        }
    }

    private static InputStream trackedInput(byte[] data, boolean[] closed) {
        InputStream input = new ByteArrayInputStream(data);
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

    private static final class FakeResource implements net.minecraft.Resource {
        private final InputStream input;

        private FakeResource(InputStream input) {
            this.input = input;
        }

        @Override
        public InputStream getInputStream() {
            return this.input;
        }

        @Override
        public boolean hasMetadata() {
            return true;
        }

        @Override
        public MetadataSection getMetadata(String section) {
            return null;
        }
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
