package shadersmodcore.mixin.client.render;

import net.minecraft.*;
import shadersmodcore.client.dynamicLight.DynamicLights;
import shadersmodcore.config.ShaderConfig;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({ItemRenderer.class})
public abstract class ItemRendererMixin {
   @Shadow public abstract void renderItem(EntityLivingBase par1EntityLivingBase, ItemStack par2ItemStack, int par3);
   @Shadow @Final public MapItemRenderer mapItemRenderer;
   @Shadow @Final private static ResourceLocation RES_MAP_BACKGROUND;
   @Shadow private float equippedProgress;
   @Shadow private float prevEquippedProgress;
   @Shadow private Minecraft mc;
   @Shadow private ItemStack itemToRender;

   @Redirect(method = "renderItemInFirstPerson", at = @At(value = "INVOKE",
           target = "Lnet/minecraft/WorldClient;getLightBrightnessForSkyBlocks(IIII)I"))
   public int getLightBrightnessForSkyBlocks(WorldClient worldClient, int par1, int par2, int par3, int par4) {
      int var10 = worldClient.getLightBrightnessForSkyBlocks(par1, par2, par3, par4);
      if (ShaderConfig.isDynamicLights()) {
         var10 = DynamicLights.getCombinedLight(this.mc.renderViewEntity, var10);
      }
      return var10;
   }
}
