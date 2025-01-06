package shadersmodcore.client.shader;

import net.minecraft.Frustrum;
import net.minecraft.ICamera;
import net.minecraft.ItemRenderer;
import net.minecraft.RenderGlobal;
import org.lwjgl.opengl.GL11;

public class ShadersRender {
   public static void setFrustrumPosition(Frustrum frustrum, double x, double y, double z) {
      frustrum.setPosition(x, y, z);
   }

   public static void clipRenderersByFrustrum(RenderGlobal renderGlobal, ICamera iCamera, float par2) {
      Shaders.checkGLError("pre clip");
      if (!Shaders.isShadowPass || Shaders.configShadowClipFrustrum) {
         renderGlobal.clipRenderersByFrustum(iCamera, par2);
         Shaders.checkGLError("clip");
      }

   }

   public static void renderItemFP(ItemRenderer itemRenderer, float par1) {
      GL11.glDepthFunc(518);
      GL11.glPushMatrix();
      itemRenderer.renderItemInFirstPerson(par1);
      GL11.glPopMatrix();
      GL11.glDepthFunc(515);
      itemRenderer.renderItemInFirstPerson(par1);
   }
}
