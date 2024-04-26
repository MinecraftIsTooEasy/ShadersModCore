package net.wenscHuix.mitemod.shader.client;

import java.util.ArrayList;
import net.minecraft.GuiSlot;
import net.minecraft.Tessellator;

public class GuiSlotShaders extends GuiSlot {
   public ArrayList shaderslist;
   private int scrollBarX;
   final GuiShaders shadersGui;

   public GuiSlotShaders(GuiShaders par1GuiShaders) {
      super(par1GuiShaders.getMc(), par1GuiShaders.width / 2 + 20, par1GuiShaders.height, 40, par1GuiShaders.height - 70, 16);
      this.scrollBarX = par1GuiShaders.width / 2 + 14;
      this.shadersGui = par1GuiShaders;
      this.shaderslist = Shaders.listofShaders();
   }

   public void updateList() {
      this.shaderslist = Shaders.listofShaders();
   }

   public int getScrollBarX() {
      return this.scrollBarX;
   }

   protected int getContentHeight() {
      return this.getSize() * 18;
   }

   public void overlayBackground(int par1, int par2, int par3, int par4) {
   }

   public void drawContainerBackground(Tessellator tess) {
   }

   protected int getSize() {
      return this.shaderslist.size();
   }

   protected void elementClicked(int i, boolean b) {
      Shaders.setShaderPack((String)this.shaderslist.get(i));
      this.shadersGui.needReinit = false;
      Shaders.loadShaderPack();
      Shaders.uninit();
   }

   protected boolean isSelected(int i) {
      return this.shaderslist.get(i).equals(Shaders.currentshadername);
   }

   protected void drawBackground() {
      this.shadersGui.drawDefaultBackground();
   }

   protected void drawSlot(int i, int j, int k, int l, Tessellator tessellator) {
      this.shadersGui.drawCenteredString((String)this.shaderslist.get(i), this.scrollBarX / 2, k + 1, 16777215);
   }
}
