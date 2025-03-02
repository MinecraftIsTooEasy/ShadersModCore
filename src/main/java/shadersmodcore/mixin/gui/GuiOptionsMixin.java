package shadersmodcore.mixin.gui;

import net.minecraft.*;
import net.minecraft.client.main.Main;
import shadersmodcore.client.optimize.GuiPlusVideo;
import shadersmodcore.client.shader.GuiShaders;
import net.xiaoyu233.fml.util.ReflectHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({GuiOptions.class})
public class GuiOptionsMixin extends GuiScreen {
   @Shadow @Final private GameSettings options;

   @Inject(method = "initGui", at = @At("RETURN"))
   public void addShaderButton(CallbackInfo ci) {
      this.buttonList.add(new GuiButton(190, this.width / 2 + 2, this.height / 6 + 72 - 6, 150, 20, I18n.getString("options.advanced.video.settings")));
   }


   @Inject(method = "actionPerformed", at = @At("HEAD"))
   private void addShaderButtonAction(GuiButton par1GuiButton, CallbackInfo callbackInfo) {
      if (par1GuiButton.enabled) {
         if (par1GuiButton.id == 190 && !Main.is_MITE_DS) {
            this.mc.gameSettings.saveOptions();
            this.mc.displayGuiScreen(new GuiPlusVideo(ReflectHelper.dyCast(this), this.options));
         }
      }
   }
}
