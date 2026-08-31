package shadersmodcore.client.shader;

import net.minecraft.AbstractTexture;
import net.minecraft.ResourceManager;
import shadersmodcore.api.AbstractTextureAccessor;

public final class ShadersTexTextureLifecycleTest {
    private ShadersTexTextureLifecycleTest() {
    }

    public static void main(String[] args) {
        ShadersTex.multiTexMap.clear();

        FakeTexture texture = new FakeTexture(41, new MultiTexID(41, 42, 43));
        ShadersTex.multiTexMap.put(texture.multiTex.base, texture.multiTex);

        MultiTexID detached = ShadersTex.detachTextureState(texture);
        check(detached != null && detached.norm == 42 && detached.spec == 43,
            "detaching a texture must return its existing auxiliary IDs");
        check(texture.multiTex == null, "detaching a texture must clear its auxiliary state");
        check(!ShadersTex.multiTexMap.containsKey(41),
            "detaching a texture must remove its base ID from the cache");
        check(texture.glTextureId == -1,
            "detaching a texture must restore vanilla's unallocated texture sentinel");

        check(ShadersTex.detachTextureState(texture) == null,
            "detaching an already detached texture must be idempotent");
        check(texture.glTextureId == -1 && texture.multiTex == null,
            "repeated detaching must not recreate texture state");

        FakeTexture withoutMultiTex = new FakeTexture(51, null);
        MultiTexID absent = ShadersTex.detachTextureState(withoutMultiTex);
        check(absent == null, "a texture without auxiliary state must not allocate one while releasing");
        check(withoutMultiTex.glTextureId == -1,
            "a texture without auxiliary state must still restore the unallocated sentinel");

        NoGlTexture noStateTexture = new NoGlTexture();
        ShadersTex.deleteTextures(noStateTexture);
        check(noStateTexture.getGlTextureId() == -1,
            "deleting an unallocated texture must keep the vanilla sentinel");

        System.out.println("ShadersTexTextureLifecycleTest passed");
    }

    private static final class FakeTexture implements AbstractTextureAccessor {
        private int glTextureId;
        private MultiTexID multiTex;

        private FakeTexture(int glTextureId, MultiTexID multiTex) {
            this.glTextureId = glTextureId;
            this.multiTex = multiTex;
        }

        @Override
        public int getGlTextureId() {
            return glTextureId;
        }

        @Override
        public void setGlTextureId(int id) {
            glTextureId = id;
        }

        @Override
        public MultiTexID getMultiTexID() {
            throw new AssertionError("release must not call the lazy accessor");
        }

        @Override
        public MultiTexID getMultiTexID0() {
            return multiTex;
        }

        @Override
        public void setMultiTexID(MultiTexID id) {
            multiTex = id;
        }
    }

    private static final class NoGlTexture extends AbstractTexture implements AbstractTextureAccessor {
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

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
