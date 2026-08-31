package shadersmodcore.client.shader;

import net.minecraft.ResourceLocation;

public final class ShadersTexResourcePathTest {
    private ShadersTexResourcePathTest() {
    }

    public static void main(String[] args) {
        check(ShadersTex.getNSMapLocation(null, "n") == null,
            "null texture locations must preserve the OptiFine fallback");

        ResourceLocation texture = new ResourceLocation("minecraft", "textures/block/pngstone.png");
        check("minecraft:textures/block/pngstone_n.png".equals(
            ShadersTex.getNSMapLocation(texture, "n").toString()),
            "PNG suffix must be removed literally before adding the normal-map suffix");
        check("minecraft:textures/block/pngstone_s.png".equals(
            ShadersTex.getNSMapLocation(texture, "s").toString()),
            "specular-map suffix must use the complete source basename");

        ResourceLocation nonPng = new ResourceLocation("mod", "textures/custom/atlas");
        check("mod:textures/custom/atlas_n.png".equals(
            ShadersTex.getNSMapLocation(nonPng, "n").toString()),
            "paths without a PNG suffix must remain unchanged");

        ResourceLocation existingNormal = new ResourceLocation("mod", "textures/custom/atlas_n.png");
        check("mod:textures/custom/atlas_n_n.png".equals(
            ShadersTex.getNSMapLocation(existingNormal, "n").toString()),
            "existing normal-map suffixes must retain the original naming rule");
        ResourceLocation existingSpecular = new ResourceLocation("mod", "textures/custom/atlas_s.png");
        check("mod:textures/custom/atlas_s_s.png".equals(
            ShadersTex.getNSMapLocation(existingSpecular, "s").toString()),
            "existing specular-map suffixes must retain the original naming rule");

        System.out.println("ShadersTexResourcePathTest passed");
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
