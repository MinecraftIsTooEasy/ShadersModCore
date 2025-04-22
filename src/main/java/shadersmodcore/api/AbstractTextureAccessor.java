package shadersmodcore.api;

public interface AbstractTextureAccessor {
    int getGlTextureId();

    void setGlTextureId(int id);

    MultiTexID getMultiTexID();

    MultiTexID getMultiTexID0();

    void setMultiTexID(MultiTexID id);
}
