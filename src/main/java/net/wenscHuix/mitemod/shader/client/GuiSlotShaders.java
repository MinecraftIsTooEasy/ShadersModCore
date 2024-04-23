package net.wenscHuix.mitemod.shader.client;

import java.util.ArrayList;
import net.minecraft.GuiSlot;
import net.minecraft.bfq;

public class GuiSlotShaders extends GuiSlot {
   public ArrayList shaderslist;
   private int scrollBarX;
   final GuiShaders shadersGui;

   public GuiSlotShaders(GuiShaders par1GuiShaders) {
      super(par1GuiShaders.getMc(), par1GuiShaders.g / 2 + 20, par1GuiShaders.h, 40, par1GuiShaders.h - 70, 16);
      this.scrollBarX = par1GuiShaders.g / 2 + 14;
      this.shadersGui = par1GuiShaders;
      this.shaderslist = Shaders.listofShaders();
   }

   public void updateList() {
      this.shaderslist = Shaders.listofShaders();
   }

   public int getScrollBarX() {
      return this.scrollBarX;
   }

   protected int d() {
      return this.a() * 18;
   }

   public void overlayBackground(int par1, int par2, int par3, int par4) {
   }

   public void drawContainerBackground(bfq tess) {
   }

   protected int a() {
      return this.shaderslist.size();
   }

   protected void a(int i, boolean b) {
      Shaders.setShaderPack((String)this.shaderslist.get(i));
      this.shadersGui.needReinit = false;
      Shaders.loadShaderPack();
      Shaders.uninit();
   }

   protected boolean a(int i) {
      return this.shaderslist.get(i).equals(Shaders.currentshadername);
   }

   protected void b() {
      this.shadersGui.e();
   }

   protected void a(int i, int i1, int i2, int i3, bfq bfq) {
      this.shadersGui.a((String)this.shaderslist.get(i), this.scrollBarX / 2, i2 + 1, 16777215);
   }
}
