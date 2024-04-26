package net.wenscHuix.mitemod.optimize.gui;

import net.minecraft.GameSettings;
import net.minecraft.GuiButton;
import net.minecraft.GuiScreen;
import net.minecraft.I18n;

public class GuiPlusVideo extends GuiScreen {
   private GuiScreen parentGuiScreen;
   protected String screenTitle = "Plus Video Settings";
   private GameSettings guiGameSettings;

   public GuiPlusVideo(GuiScreen par1GuiScreen, GameSettings par2GameSettings) {
      this.parentGuiScreen = par1GuiScreen;
      this.guiGameSettings = par2GameSettings;
   }

   public void initGui() {
      this.screenTitle = I18n.getString("Plus Video Settings");
      this.buttonList.add(new GuiButton(200, this.width / 2 - 100, this.height / 6 + 168, I18n.getString("gui.done")));
      this.buttonList.add(new GuiButton(101, this.width / 2 - 155, this.height / 7 + 25, 300, 20, I18n.getString("粒子效果")));
      this.buttonList.add(new GuiButton(102, this.width / 2 - 155, this.height / 7 + 50, 150, 20, "动态光源: " + Config.dynamicLights));
      this.buttonList.add(new GuiButton(103, this.width / 2 - 155, this.height / 7 + 75, 150, 20, "绘制选择框: " + Config.drawSelectionBox));
   }

   public void actionPerformed(GuiButton par1GuiButton) {
      if (par1GuiButton.enabled) {
         if (par1GuiButton.id == 101) {
            this.mc.gameSettings.saveOptions();
            this.mc.displayGuiScreen(new GuiParticle(this, this.guiGameSettings));
         } else if (par1GuiButton.id == 102) {
            Config.dynamicLights = !Config.dynamicLights;
            par1GuiButton.displayString = "动态光源: " + Config.dynamicLights;
         } else if (par1GuiButton.id == 103) {
            Config.drawSelectionBox = !Config.drawSelectionBox;
            par1GuiButton.displayString = "绘制选择方块: " + Config.drawSelectionBox;
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
      this.drawCenteredString(this.fontRenderer, "高级视频设置", this.width / 2, 16, 16777215);
      this.drawCenteredString(this.fontRenderer, "v0.1.0(by wensc,Huix)", this.width - 40, 10, 8421504);
      super.drawScreen(par1, par2, par3);
   }
}
