package shadersmodcore.client.optimize;

import net.minecraft.GameSettings;
import net.minecraft.GuiButton;
import net.minecraft.GuiScreen;
import net.minecraft.I18n;
import shadersmodcore.client.shader.GuiShaders;
import shadersmodcore.config.OptimizeConfig;
import shadersmodcore.util.Utils;

public class GuiPlusVideo extends GuiScreen {
   private GuiScreen parentGuiScreen;
   protected String screenTitle = "Plus Video Settings";
   private GameSettings guiGameSettings;

   public GuiPlusVideo(GuiScreen par1GuiScreen, GameSettings par2GameSettings) {
      this.parentGuiScreen = par1GuiScreen;
      this.guiGameSettings = par2GameSettings;
   }

   public void initGui() {
      this.screenTitle =
              I18n.getString("Plus Video Settings");
      this.buttonList.add(new GuiButton(200, this.width / 2 - 100, this.height / 6 + 168, I18n.getString("gui.done")));

      this.buttonList.add(new GuiButton(100, this.width / 2 - 155, this.height / 7, 150, 20,
              I18n.getString("options.shader.settings")));
      this.buttonList.add(new GuiButton(101, this.width / 2 + 5, this.height / 7, 150, 20,
              I18n.getString("options.particle.settings")));
      this.buttonList.add(new GuiButton(102, this.width / 2 - 155, this.height / 7 + 25, 150, 20,
              I18n.getString("options.quality.settings")));

      this.buttonList.add(new GuiButton(110, this.width / 2 - 155, this.height / 7 + 75, 150, 20,
              I18n.getString("options.dynamic.light") + ": " + Utils.getTranslationBoolean(OptimizeConfig.dynamicLights)));
      this.buttonList.add(new GuiButton(111, this.width / 2 + 5, this.height / 7 + 75, 150, 20,
              I18n.getString("options.drawing.selection.block") + ": " + Utils.getTranslationBoolean(OptimizeConfig.drawSelectionBox)));
      this.buttonList.add(new GuiButton(112, this.width / 2 - 155, this.height / 7 + 100, 150, 20,
              I18n.getString("options.render.rainsnow") + ": " + Utils.getTranslationBoolean(OptimizeConfig.renderRainSnow)));
      this.buttonList.add(new GuiButton(113, this.width / 2 + 5, this.height / 7 + 100, 150, 20,
              I18n.getString("options.render.shadow") + ": " + Utils.getTranslationBoolean(OptimizeConfig.renderShadow)));

   }

   public void actionPerformed(GuiButton par1GuiButton) {
      if (par1GuiButton.enabled) {
         if (par1GuiButton.id == 100) {
            this.mc.gameSettings.saveOptions();
            this.mc.displayGuiScreen(new GuiShaders(this));
         } else if (par1GuiButton.id == 101) {
            this.mc.gameSettings.saveOptions();
            this.mc.displayGuiScreen(new GuiParticle(this, this.guiGameSettings));
         } else if (par1GuiButton.id == 102) {
            this.mc.gameSettings.saveOptions();
            this.mc.displayGuiScreen(new GuiQuality(this, this.guiGameSettings));

         } else if (par1GuiButton.id == 110) {
            OptimizeConfig.dynamicLights = !OptimizeConfig.dynamicLights;
            par1GuiButton.displayString = I18n.getString("options.dynamic.light") + ": " + Utils.getTranslationBoolean(OptimizeConfig.dynamicLights);
         } else if (par1GuiButton.id == 111) {
            OptimizeConfig.drawSelectionBox = !OptimizeConfig.drawSelectionBox;
            par1GuiButton.displayString = I18n.getString("options.drawing.selection.block") + ": " + Utils.getTranslationBoolean(OptimizeConfig.drawSelectionBox);
         } else if (par1GuiButton.id == 112) {
            OptimizeConfig.renderRainSnow = !OptimizeConfig.renderRainSnow;
            par1GuiButton.displayString = I18n.getString("options.render.rainsnow") + ": " + Utils.getTranslationBoolean(OptimizeConfig.renderRainSnow);
         } else if (par1GuiButton.id == 113) {
            OptimizeConfig.renderShadow = !OptimizeConfig.renderShadow;
            par1GuiButton.displayString = I18n.getString("options.render.shadow") + ": " + Utils.getTranslationBoolean(OptimizeConfig.renderShadow);

         } else if (par1GuiButton.id == 200) {
            this.mc.gameSettings.saveOptions();
            this.mc.displayGuiScreen(this.parentGuiScreen);

            try {
               OptimizeConfig.storeConfig();
            } catch (Exception var3) {
               var3.printStackTrace();
            }
         }
      }

   }

   public void drawScreen(int par1, int par2, float par3) {
      this.drawDefaultBackground();
      this.drawCenteredString(this.fontRenderer, I18n.getString("options.advanced.video.settings"), this.width / 2, 16, 16777215);
      super.drawScreen(par1, par2, par3);
   }
}
