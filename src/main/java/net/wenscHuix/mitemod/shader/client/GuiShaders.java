package net.wenscHuix.mitemod.shader.client;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import net.minecraft.CrashReportSystemKind;
import net.minecraft.GuiScreen;
import net.minecraft.Minecraft;
import net.minecraft.aut;
import net.minecraft.awe;
import org.lwjgl.Sys;

public class GuiShaders extends GuiScreen {
   protected GuiScreen parentGui;
   private int updateTimer = -1;
   public boolean needReinit;
   private GuiSlotShaders shaderList;

   public GuiShaders(GuiScreen par1GuiScreen) {
      this.parentGui = par1GuiScreen;
   }

   public void A_() {
      if (Shaders.shadersConfig == null) {
         Shaders.loadConfig();
      }

      this.i.add(new aut(9, this.g * 3 / 4 - 60, 30, 160, 18, "云的光影效果: " + Shaders.configCloudShadow));
      this.i.add(new aut(4, this.g * 3 / 4 - 60, 50, 160, 18, "新的方块光影效果: " + Shaders.dtweak));
      this.i.add(new aut(10, this.g * 3 / 4 - 60, 70, 160, 18, "手臂进入方块的范围: " + String.format("%.4f", Shaders.configHandDepthMul)));
      this.i.add(new aut(15, this.g * 3 / 4 - 60, 90, 160, 18, "画面渲染: " + String.format("%.4f", Shaders.configRenderResMul)));
      this.i.add(new aut(16, this.g * 3 / 4 - 60, 110, 160, 18, "光影渲染: " + String.format("%.4f", Shaders.configShadowResMul)));
      this.i.add(new aut(14, this.g * 3 / 4 - 60, 130, 160, 18, "光影视锥: " + Shaders.configShadowClipFrustrum));
      this.i.add(new aut(11, this.g * 3 / 4 - 60, 150, 160, 18, "特克斯质量效果: " + Shaders.texMinFilDesc[Shaders.configTexMinFilB]));
      this.i.add(new aut(12, this.g * 3 / 4 - 60, 170, 160, 18, "特克斯最大质量: " + Shaders.texMagFilDesc[Shaders.configTexMagFilN]));
      this.i.add(new aut(13, this.g * 3 / 4 - 60, 190, 160, 18, "特克斯最大效果: " + Shaders.texMagFilDesc[Shaders.configTexMagFilS]));
      this.i.add(new aut(6, this.g * 3 / 4 - 60, this.h - 25, 160, 20, "完成"));
      this.i.add(new aut(5, this.g / 4 - 80, this.h - 25, 160, 20, "打开光影配置文件夹"));
      this.shaderList = new GuiSlotShaders(this);
      this.shaderList.d(7, 8);
      this.needReinit = false;
   }

   public void a(aut par1GuiButton) {
      if (par1GuiButton.h) {
         float val;
         float[] choices;
         int i;
         switch(par1GuiButton.g) {
         case 4:
            Shaders.dtweak = !Shaders.dtweak;
            par1GuiButton.f = "新的方块光影效果: " + Shaders.dtweak;
            break;
         case 5:
            switch(CrashReportSystemKind.a()) {
            case d:
               try {
                  Runtime.getRuntime().exec(new String[]{"/usr/bin/open", Shaders.shaderpacksdir.getAbsolutePath()});
                  return;
               } catch (IOException var11) {
                  var11.printStackTrace();
                  break;
               }
            case c:
               String var2 = String.format("cmd.exe /C start \"Open file\" \"%s\"", Shaders.shaderpacksdir.getAbsolutePath());

               try {
                  Runtime.getRuntime().exec(var2);
                  return;
               } catch (IOException var10) {
                  var10.printStackTrace();
               }
            }

            boolean var8 = false;

            try {
               Class var3 = Class.forName("java.awt.Desktop");
               Object var4 = var3.getMethod("getDesktop").invoke((Object)null);
               var3.getMethod("browse", URI.class).invoke(var4, (new File(this.f.x, Shaders.shaderpacksdirname)).toURI());
            } catch (Throwable var9) {
               var9.printStackTrace();
               var8 = true;
            }

            if (var8) {
               System.out.println("Opening via system class!");
               Sys.openURL("file://" + Shaders.shaderpacksdir.getAbsolutePath());
            }
            break;
         case 6:
            new File(Shaders.shadersdir, "current.cfg");

            try {
               Shaders.storeConfig();
            } catch (Exception var8) {
               var8.printStackTrace();
            }

            if (this.needReinit) {
               this.needReinit = false;
               Shaders.loadShaderPack();
               Shaders.uninit();
               this.f.g.a();
            }

            this.f.a(this.parentGui);
            break;
         case 7:
         case 8:
         default:
            this.shaderList.a(par1GuiButton);
            break;
         case 9:
            Shaders.configCloudShadow = !Shaders.configCloudShadow;
            par1GuiButton.f = "云的光影效果: " + Shaders.configCloudShadow;
            break;
         case 10:
            val = Shaders.configHandDepthMul;
            choices = new float[]{0.0625F, 0.125F, 0.25F, 0.5F, 1.0F};
            if (!p()) {
               for(i = 0; i < choices.length && choices[i] <= val; ++i) {
               }

               if (i == choices.length) {
                  i = 0;
               }
            } else {
               for(i = choices.length - 1; i >= 0 && val <= choices[i]; --i) {
               }

               if (i < 0) {
                  i = choices.length - 1;
               }
            }

            Shaders.configHandDepthMul = choices[i];
            par1GuiButton.f = "手臂进入方块的范围: " + String.format("%.4f", Shaders.configHandDepthMul);
            break;
         case 11:
            Shaders.configTexMinFilB = (Shaders.configTexMinFilB + 1) % 3;
            Shaders.configTexMinFilN = Shaders.configTexMinFilS = Shaders.configTexMinFilB;
            par1GuiButton.f = "特克斯质量效果: " + Shaders.texMinFilDesc[Shaders.configTexMinFilB];
            ShadersTex.updateTextureMinMagFilter();
            break;
         case 12:
            Shaders.configTexMagFilN = (Shaders.configTexMagFilN + 1) % 2;
            par1GuiButton.f = "特克斯最大质量: " + Shaders.texMagFilDesc[Shaders.configTexMagFilN];
            ShadersTex.updateTextureMinMagFilter();
            break;
         case 13:
            Shaders.configTexMagFilS = (Shaders.configTexMagFilS + 1) % 2;
            par1GuiButton.f = "特克斯最大效果: " + Shaders.texMagFilDesc[Shaders.configTexMagFilS];
            ShadersTex.updateTextureMinMagFilter();
            break;
         case 14:
            Shaders.configShadowClipFrustrum = !Shaders.configShadowClipFrustrum;
            par1GuiButton.f = "光影视锥: " + Shaders.configShadowClipFrustrum;
            ShadersTex.updateTextureMinMagFilter();
            break;
         case 15:
            val = Shaders.configRenderResMul;
            choices = new float[]{0.25F, 0.33333334F, 0.5F, 0.70710677F, 1.0F, 1.4142135F, 2.0F};
            if (!p()) {
               for(i = 0; i < choices.length && choices[i] <= val; ++i) {
               }

               if (i == choices.length) {
                  i = 0;
               }
            } else {
               for(i = choices.length - 1; i >= 0 && val <= choices[i]; --i) {
               }

               if (i < 0) {
                  i = choices.length - 1;
               }
            }

            Shaders.configRenderResMul = choices[i];
            par1GuiButton.f = "画面渲染: " + String.format("%.4f", Shaders.configRenderResMul);
            Shaders.scheduleResize();
            break;
         case 16:
            val = Shaders.configShadowResMul;
            choices = new float[]{0.25F, 0.33333334F, 0.5F, 0.70710677F, 1.0F, 1.4142135F, 2.0F, 3.0F, 4.0F};
            if (!p()) {
               for(i = 0; i < choices.length && choices[i] <= val; ++i) {
               }

               if (i == choices.length) {
                  i = 0;
               }
            } else {
               for(i = choices.length - 1; i >= 0 && val <= choices[i]; --i) {
               }

               if (i < 0) {
                  i = choices.length - 1;
               }
            }

            Shaders.configShadowResMul = choices[i];
            par1GuiButton.f = "光影渲染: " + String.format("%.4f", Shaders.configShadowResMul);
            Shaders.scheduleResizeShadow();
         }
      }

   }

   public void a(int par1, int par2, float par3) {
      this.shaderList.a(par1, par2, par3);
      if (this.updateTimer <= 0) {
         this.shaderList.updateList();
         this.updateTimer += 20;
      }

      this.a(this.o, "光影配置文件列表", this.g / 2, 16, 16777215);
      super.a(par1, par2, par3);
   }

   public void c() {
      super.c();
      --this.updateTimer;
   }

   public Minecraft getMc() {
      return this.f;
   }

   public void a(String par1, int par2, int par3, int par4) {
      this.a(this.o, par1, par2, par3, par4);
   }
}
