package net.wenscHuix.mitemod.optimize.gui;

import net.minecraft.GameSettings;
import net.minecraft.GuiButton;
import net.minecraft.GuiScreen;
import net.minecraft.I18n;

public class GuiParticle extends GuiScreen {
   private GuiScreen parentGuiScreen;
   private GameSettings guiGameSettings;

   public GuiParticle(GuiScreen par1GuiScreen, GameSettings par2GameSettings) {
      this.parentGuiScreen = par1GuiScreen;
      this.guiGameSettings = par2GameSettings;
   }

   public void initGui() {
      if (Config.optimizeConfig == null) {
         Config.loadConfig();
      }

      this.buttonList.add(new GuiButton(1, this.width / 2 - 155, this.height / 7, 150, 20,
              I18n.getString("options.block.destroy.effect")+ ": " + Config.blockDestroyEffects));
      this.buttonList.add(new GuiButton(200, this.width / 2 - 100, this.height / 6 + 168, I18n.getString("gui.done")));
   }

   public void actionPerformed(GuiButton par1GuiButton) {
      if (par1GuiButton.enabled) {
         if (par1GuiButton.id == 1) {
            Config.blockDestroyEffects = !Config.blockDestroyEffects;
            par1GuiButton.displayString = I18n.getString("options.block.destroy.effect") + ": " + Config.blockDestroyEffects;
         } else if (par1GuiButton.id == 200) {
            this.mc.gameSettings.saveOptions();
            this.mc.displayGuiScreen(this.parentGuiScreen);

            try {
               Config.storeConfig();
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
