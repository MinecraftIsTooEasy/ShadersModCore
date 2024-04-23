package net.wenscHuix.mitemod.mixin.render.texture;

import net.minecraft.ResourceLocation;
import net.minecraft.SimpleTexture;
import net.minecraft.TextureManager;
import net.minecraft.TextureObject;
import net.wenscHuix.mitemod.shader.client.ShadersTex;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;

@Mixin({TextureManager.class})
public abstract class TextureManagerMixin {
   @Shadow
   @Final
   private Map mapTextureObjects;

   @Shadow public abstract boolean loadTexture(ResourceLocation par1ResourceLocation, TextureObject par2TextureObject);

   /**
    * @author
    * @reason
    */
   @Overwrite
   public void bindTexture(ResourceLocation par1ResourceLocation) {
      Object var2 = this.mapTextureObjects.get(par1ResourceLocation);
      if (var2 == null) {
         var2 = new SimpleTexture(par1ResourceLocation);
         this.loadTexture(par1ResourceLocation, (TextureObject)var2);
      }

      ShadersTex.bindTexture((TextureObject) var2);
   }
}
