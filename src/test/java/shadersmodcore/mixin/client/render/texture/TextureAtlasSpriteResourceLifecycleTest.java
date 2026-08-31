package shadersmodcore.mixin.client.render.texture;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

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
