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
      this.buttonList.add(new GuiButton(191, this.width / 2 - 152 + 77, this.height / 6 + 120 - 6, 73, 20, I18n.getString("options.shader.settings")));
      this.buttonList.add(new GuiButton(190, this.width / 2 + 2 + 77, this.height / 6 + 96 - 6, 73, 20, I18n.getString("options.advanced.video.settings")));
   }


   @Inject(method = "actionPerformed", at = @At("HEAD"))
   private void addShaderButtonAction(GuiButton par1GuiButton, CallbackInfo callbackInfo) {
      if (par1GuiButton.enabled) {
         if (par1GuiButton.id == 190 && !Main.is_MITE_DS) {
            this.mc.gameSettings.saveOptions();
            this.mc.displayGuiScreen(new GuiPlusVideo(ReflectHelper.dyCast(this), this.options));
         }

         if (par1GuiButton.id == 191 && !Main.is_MITE_DS) {
            this.mc.gameSettings.saveOptions();
            this.mc.displayGuiScreen(new GuiShaders(ReflectHelper.dyCast(this)));
         }
      }

   }
}
