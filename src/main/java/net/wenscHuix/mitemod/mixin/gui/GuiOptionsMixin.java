package net.wenscHuix.mitemod.mixin.gui;

import net.minecraft.*;
import net.minecraft.client.main.Main;
import net.wenscHuix.mitemod.optimize.gui.GuiPlusVideo;
import net.wenscHuix.mitemod.shader.client.GuiShaders;
import net.xiaoyu233.fml.util.ReflectHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({GuiOptions.class})
public class GuiOptionsMixin extends GuiScreen {
   @Shadow
   @Final
   private GameSettings options;

   @Shadow protected String screenTitle;

   @Shadow @Final private static EnumOptions[] relevantOptions;

   /**
    * @author
    * @reason
    */
   @Overwrite
   public void initGui() {
      int var1 = 0;
      this.screenTitle = I18n.getString("options.title");
      EnumOptions[] var2 = relevantOptions;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         EnumOptions var5 = var2[var4];
         if (var5.getEnumFloat()) {
            this.buttonList.add(new GuiSlider(var5.returnEnumOrdinal(), this.width / 2 - 155 + var1 % 2 * 160, this.height / 6 - 12 + 24 * (var1 >> 1), var5, this.options.getKeyBinding(var5), this.options.getOptionFloatValue(var5)));
         } else {
            GuiSmallButton var6 = new GuiSmallButton(var5.returnEnumOrdinal(), this.width / 2 - 155 + var1 % 2 * 160, this.height / 6 - 12 + 24 * (var1 >> 1), var5, this.options.getKeyBinding(var5));
            if (var5 == EnumOptions.DIFFICULTY && this.mc.theWorld != null && this.mc.theWorld.getWorldInfo().isHardcoreModeEnabled()) {
               var6.enabled = false;
               var6.displayString = I18n.getString("options.difficulty") + ": " + I18n.getString("options.difficulty.hardcore");
            }

            if (var5 == EnumOptions.DIFFICULTY) {
               var6.enabled = false;
            }

            this.buttonList.add(var6);
         }

         ++var1;
      }

      GuiButton button_video_settings = new GuiButton(101, this.width / 2 - 152, this.height / 6 + 96 - 6, 73, 20, I18n.getString("options.video"));
      button_video_settings.enabled = !Main.is_MITE_DS;
      this.buttonList.add(button_video_settings);
      this.buttonList.add(new GuiButton(100, this.width / 2 + 2, this.height / 6 + 96 - 6, 150, 20, I18n.getString("options.controls")));
      this.buttonList.add(new GuiButton(102, this.width / 2 - 152, this.height / 6 + 120 - 6, 150, 20, I18n.getString("options.language")));
      this.buttonList.add(new GuiButton(103, this.width / 2 + 2, this.height / 6 + 120 - 6, 150, 20, I18n.getString("options.multiplayer.title")));
      this.buttonList.add(new GuiButton(105, this.width / 2 - 152, this.height / 6 + 144 - 6, 150, 20, I18n.getString("options.resourcepack")));
      this.buttonList.add(new GuiButton(104, this.width / 2 + 2, this.height / 6 + 144 - 6, 150, 20, I18n.getString("options.snooper.view")));
      this.buttonList.add(new GuiButton(200, this.width / 2 - 100, this.height / 6 + 168, I18n.getString("gui.done")));
      this.buttonList.add(new GuiButton(191, this.width / 2 - 152 + 77, this.height / 6 + 96 - 6, 73, 20, I18n.getString("option.shader.settings")));
      this.buttonList.add(new GuiButton(190, this.width / 2 + 2, this.height / 6 + 96 - 6, 150, 20, I18n.getString("option.advanced.video.settings")));
   }

   @Inject(method = "actionPerformed", at = @At("HEAD"))
   private void injectActionPerformed(GuiButton par1GuiButton, CallbackInfo callbackInfo) {
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
