package net.wenscHuix.mitemod.optimize.gui;

import net.minecraft.aul;
import net.minecraft.aut;
import net.minecraft.awe;
import net.minecraft.bkb;

public class GuiParticle extends awe {
   private awe parentGuiScreen;
   private aul guiGameSettings;

   public GuiParticle(awe par1GuiScreen, aul par2GameSettings) {
      this.parentGuiScreen = par1GuiScreen;
      this.guiGameSettings = par2GameSettings;
   }

   public void A_() {
      if (Config.optimizeConfig == null) {
         Config.loadConfig();
      }

      this.i.add(new aut(1, this.g / 2 - 155, this.h / 7, 150, 20, "破环方块: " + Config.blockDestroyEffects));
      this.i.add(new aut(200, this.g / 2 - 100, this.h / 6 + 168, bkb.a("gui.done")));
   }

   public void a(aut par1GuiButton) {
      if (par1GuiButton.h) {
         if (par1GuiButton.g == 1) {
            Config.blockDestroyEffects = !Config.blockDestroyEffects;
            par1GuiButton.f = "破环方块: " + Config.blockDestroyEffects;
         } else if (par1GuiButton.g == 200) {
            this.f.u.b();
            this.f.a(this.parentGuiScreen);

            try {
               Config.storeConfig();
            } catch (Exception var3) {
               var3.printStackTrace();
            }
         }
      }

   }

   public void a(int par1, int par2, float par3) {
      this.e();
      this.a(this.o, "粒子效果设置", this.g / 2, 16, 16777215);
      super.a(par1, par2, par3);
   }
}
