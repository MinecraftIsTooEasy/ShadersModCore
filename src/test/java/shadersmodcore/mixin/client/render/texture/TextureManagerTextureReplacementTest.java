package shadersmodcore.mixin.client.render.texture;

import net.minecraft.ResourceLocation;
import net.minecraft.ResourceManager;
import net.minecraft.AbstractTexture;
import net.minecraft.TextureObject;
import shadersmodcore.api.AbstractTextureAccessor;
import shadersmodcore.client.shader.MultiTexID;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class TextureManagerTextureReplacementTest {
    private TextureManagerTextureReplacementTest() {
    }

    public static void main(String[] args) {
        ResourceLocation location = new ResourceLocation("test:texture");
        TextureObject previous = new FakeTexture();
        TextureObject replacement = new FakeTexture();
        TextureObject missingTexture = new FakeTexture();
        Map<ResourceLocation, TextureObject> objects = new HashMap<>();
        List<TextureObject> tickables = new ArrayList<>();
        objects.put(location, previous);
        tickables.add(previous);

        TextureManagerMixin.finishTextureReplacement(objects, tickables, location,
            previous, missingTexture, missingTexture, false);
        check(objects.get(location) == previous,
            "an IOException fallback must retain the previous texture mapping");
        check(tickables.size() == 1 && tickables.get(0) == previous,
            "an IOException fallback must retain the previous tickable");

        objects.put(location, replacement);
        TextureManagerMixin.finishTextureReplacement(objects, tickables, location,
            previous, replacement, missingTexture, false);
        check(objects.get(location) == previous,
            "a failed replacement must restore the previous mapping if a wrapper already put the new object");

        ResourceLocation alias = new ResourceLocation("test:alias");
        objects.put(alias, previous);
        objects.put(location, replacement);
        TextureManagerMixin.finishTextureReplacement(objects, tickables, location,
            previous, replacement, missingTexture, true);
        check(objects.get(location) == replacement,
            "a successful replacement must leave the new texture mapped");
        check(objects.get(alias) == previous,
            "an aliased previous texture must remain mapped");
        check(tickables.size() == 1 && tickables.get(0) == previous,
            "an aliased previous texture must retain its tickable");

        objects.remove(alias);
        tickables.add(previous);
        objects.put(location, replacement);
        TextureManagerMixin.finishTextureReplacement(objects, tickables, location,
            previous, replacement, missingTexture, true);
        check(!tickables.contains(previous),
            "an unaliased previous texture must lose all duplicate tick entries");

        ResourceLocation sameLocation = new ResourceLocation("test:same");
        objects.put(sameLocation, previous);
        tickables.add(previous);
        TextureManagerMixin.finishTextureReplacement(objects, tickables, sameLocation,
            previous, previous, missingTexture, true);
        check(objects.get(sameLocation) == previous && containsIdentity(tickables, previous),
            "reloading the same texture object must preserve its map and tick state");

        ResourceLocation sharedMissingLocation = new ResourceLocation("test:shared-missing");
        objects.put(sharedMissingLocation, missingTexture);
        tickables.add(missingTexture);
        objects.put(sharedMissingLocation, replacement);
        TextureManagerMixin.finishTextureReplacement(objects, tickables, sharedMissingLocation,
            missingTexture, replacement, missingTexture, true);
        check(objects.get(sharedMissingLocation) == replacement && containsIdentity(tickables, missingTexture),
            "a shared missing texture must never be released during replacement");

        ResourceLocation glLocation = new ResourceLocation("test:gl");
        FakeAbstractTexture previousGl = new FakeAbstractTexture();
        objects.put(glLocation, previousGl);
        tickables.add(previousGl);
        objects.put(glLocation, replacement);
        TextureManagerMixin.finishTextureReplacement(objects, tickables, glLocation,
            previousGl, replacement, missingTexture, true);
        check(previousGl.getGlTextureId() == -1 && !tickables.contains(previousGl),
            "an unaliased AbstractTexture must detach GL state and tick entries");

        ResourceLocation equalLocation = new ResourceLocation("test:equal");
        TextureObject equalPrevious = new EqualTexture();
        TextureObject equalValue = new EqualTexture();
        objects.put(equalLocation, equalPrevious);
        objects.put(new ResourceLocation("test:equal-alias"), equalValue);
        tickables.add(equalPrevious);
        tickables.add(equalValue);
        objects.put(equalLocation, replacement);
        TextureManagerMixin.finishTextureReplacement(objects, tickables, equalLocation,
            equalPrevious, replacement, missingTexture, true);
        check(!containsIdentity(tickables, equalPrevious) && containsIdentity(tickables, equalValue),
            "alias and tick cleanup must compare texture object identity, not equals");

        System.out.println("TextureManagerTextureReplacementTest passed");
    }

    private static class FakeTexture implements TextureObject {
        @Override
        public void loadTexture(ResourceManager resourceManager) {
        }

        @Override
        public int getGlTextureId() {
            return -1;
        }
    }

    private static final class FakeAbstractTexture extends AbstractTexture implements AbstractTextureAccessor {
        private MultiTexID multiTex;

        @Override
        public void loadTexture(ResourceManager resourceManager) {
        }

        @Override
        public int getGlTextureId() {
            return this.glTextureId;
        }

        @Override
        public void setGlTextureId(int id) {
            this.glTextureId = id;
        }

        @Override
        public MultiTexID getMultiTexID() {
            throw new AssertionError("release must not call the lazy accessor");
        }

        @Override
        public MultiTexID getMultiTexID0() {
            return this.multiTex;
        }

        @Override
        public void setMultiTexID(MultiTexID id) {
            this.multiTex = id;
        }
    }

    private static final class EqualTexture extends FakeTexture {
        @Override
        public boolean equals(Object other) {
            return other instanceof EqualTexture;
        }

        @Override
        public int hashCode() {
            return 1;
        }
    }

    private static boolean containsIdentity(List<?> values, Object value) {
        for (Object candidate : values) {
            if (candidate == value) {
                return true;
            }
        }
        return false;
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
