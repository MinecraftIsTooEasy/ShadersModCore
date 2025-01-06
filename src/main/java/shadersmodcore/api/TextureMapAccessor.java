package shadersmodcore.api;

public interface TextureMapAccessor {
    int getAtlasWidth();

    int getAtlasHeight();

    void setAtlasWidth(int atlasWidth);

    void setAtlasHeight(int atlasHeight);
}
