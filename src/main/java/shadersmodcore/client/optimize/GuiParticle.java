package shadersmodcore.client.optimize;

import net.minecraft.GameSettings;
import net.minecraft.GuiButton;
import net.minecraft.GuiScreen;
import net.minecraft.I18n;
import shadersmodcore.config.OptimizeConfig;
import shadersmodcore.util.Utils;

public class GuiParticle extends GuiScreen {
   private GuiScreen parentGuiScreen;
   private GameSettings guiGameSettings;

   public GuiParticle(GuiScreen par1GuiScreen, GameSettings par2GameSettings) {
      this.parentGuiScreen = par1GuiScreen;
      this.guiGameSettings = par2GameSettings;
   }

   public void initGui() {
      if (OptimizeConfig.optimizeConfig == null) {
         OptimizeConfig.loadConfig();
      }

      this.buttonList.add(new GuiButton(1, this.width / 2 - 155, this.height / 7, 150, 20,
              I18n.getString("options.block.destroy.effect") + ": " + Utils.getTranslationBoolean(OptimizeConfig.blockDestroyEffects)));
      this.buttonList.add(new GuiButton(2, this.width / 2 + 5, this.height / 7, 150, 20,
              I18n.getString("options.block.explode.effect") + ": " + Utils.getTranslationBoolean(OptimizeConfig.explodeEffects)));

      this.buttonList.add(new GuiButton(200, this.width / 2 - 100, this.height / 6 + 168, I18n.getString("gui.done")));
   }

   public void actionPerformed(GuiButton par1GuiButton) {
      if (par1GuiButton.enabled) {
         if (par1GuiButton.id == 1) {
            OptimizeConfig.blockDestroyEffects = !OptimizeConfig.blockDestroyEffects;
            par1GuiButton.displayString = I18n.getString("options.block.destroy.effect") + ": " + Utils.getTranslationBoolean(OptimizeConfig.blockDestroyEffects);
         }
         if (par1GuiButton.id == 2) {
            OptimizeConfig.explodeEffects = !OptimizeConfig.explodeEffects;
            par1GuiButton.displayString = I18n.getString("options.block.explode.effect") + ": " + Utils.getTranslationBoolean(OptimizeConfig.explodeEffects);
         }
         if (par1GuiButton.id == 200) {
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
      this.drawCenteredString(this.fontRenderer, I18n.getString("options.particle.settings"), this.width/ 2, 16, 16777215);
      super.drawScreen(par1, par2, par3);
   }
}
