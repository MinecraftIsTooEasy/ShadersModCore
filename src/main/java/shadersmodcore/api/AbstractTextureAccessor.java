package shadersmodcore.api;

import shadersmodcore.client.shader.MultiTexID;

public interface AbstractTextureAccessor {
    int getGlTextureId();

    void setGlTextureId(int id);

    MultiTexID getMultiTexID();

    MultiTexID getMultiTexID0();

    void setMultiTexID(MultiTexID id);
}
