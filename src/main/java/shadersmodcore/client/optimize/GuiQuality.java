package shadersmodcore.client.optimize;

import net.minecraft.GameSettings;
import net.minecraft.GuiButton;
import net.minecraft.GuiScreen;
import net.minecraft.I18n;
import shadersmodcore.config.OptimizeConfig;
import shadersmodcore.util.Utils;

public class GuiQuality extends GuiScreen {
    private GuiScreen parentGuiScreen;
    private GameSettings guiGameSettings;
    private static final String[] Quality = new String[]{"options.quality.default", "options.quality.fancy", "options.quality.fast"};

    public GuiQuality(GuiScreen par1GuiScreen, GameSettings par2GameSettings) {
        this.parentGuiScreen = par1GuiScreen;
        this.guiGameSettings = par2GameSettings;
    }

    public void initGui() {
        if (OptimizeConfig.optimizeConfig == null) {
            OptimizeConfig.loadConfig();
        }

        this.buttonList.add(new GuiButton(1, this.width / 2 - 155, this.height / 7, 150, 20,
                I18n.getString("options.quality.grass") + ": " + Utils.getTranslationString(Quality, OptimizeConfig.grassQuality)));
        this.buttonList.add(new GuiButton(2, this.width / 2 + 5, this.height / 7, 150, 20,
                I18n.getString("options.quality.leaves") + ": " + Utils.getTranslationString(Quality, OptimizeConfig.leavesQuality)));
        this.buttonList.add(new GuiButton(3, this.width / 2 - 155, this.height / 7 + 25, 150, 20,
                I18n.getString("options.quality.vignette") + ": " + Utils.getTranslationBoolean(OptimizeConfig.vignetteQuality)));
        this.buttonList.add(new GuiButton(4, this.width / 2 + 5, this.height / 7 + 25, 150, 20,
                I18n.getString("options.quality.drops") + ": " + Utils.getTranslationString(Quality, OptimizeConfig.dropsQuality)));
        this.buttonList.add(new GuiButton(5, this.width / 2 - 155, this.height / 7 + 50, 150, 20,
                I18n.getString("options.quality.water") + ": " + Utils.getTranslationString(Quality, OptimizeConfig.waterQuality)));
        this.buttonList.add(new GuiButton(6, this.width / 2 + 5, this.height / 7 + 50, 150, 20,
                I18n.getString("options.quality.rain") + ": " + Utils.getTranslationString(Quality, OptimizeConfig.rainQuality)));

        this.buttonList.add(new GuiButton(200, this.width / 2 - 100, this.height / 6 + 168, I18n.getString("gui.done")));
    }

    public void actionPerformed(GuiButton par1GuiButton) {
        if (par1GuiButton.enabled) {
            if (par1GuiButton.id == 1) {
                OptimizeConfig.grassQuality = (OptimizeConfig.grassQuality + 1) % 3;
                par1GuiButton.displayString = I18n.getString("options.quality.grass") + ": " + Utils.getTranslationString(Quality, OptimizeConfig.grassQuality);
                this.mc.renderGlobal.loadRenderers();
            }
            if (par1GuiButton.id == 2) {
                OptimizeConfig.leavesQuality = (OptimizeConfig.leavesQuality + 1) % 3;
                par1GuiButton.displayString = I18n.getString("options.quality.leaves") + ": " + Utils.getTranslationString(Quality, OptimizeConfig.leavesQuality);
                this.mc.renderGlobal.loadRenderers();
            }
            if (par1GuiButton.id == 3) {
                OptimizeConfig.vignetteQuality = !OptimizeConfig.vignetteQuality;
                par1GuiButton.displayString = I18n.getString("options.quality.vignette") + ": " + Utils.getTranslationBoolean(OptimizeConfig.vignetteQuality);
            }
            if (par1GuiButton.id == 4) {
                OptimizeConfig.dropsQuality = (OptimizeConfig.dropsQuality + 1) % 3;
                par1GuiButton.displayString = I18n.getString("options.quality.drops") + ": " + Utils.getTranslationString(Quality, OptimizeConfig.dropsQuality);
            }
            if (par1GuiButton.id == 5) {
                OptimizeConfig.waterQuality = (OptimizeConfig.waterQuality + 1) % 3;
                par1GuiButton.displayString = I18n.getString("options.quality.water") + ": " + Utils.getTranslationString(Quality, OptimizeConfig.waterQuality);
            }
            if (par1GuiButton.id == 6) {
                OptimizeConfig.rainQuality = (OptimizeConfig.rainQuality + 1) % 3;
                par1GuiButton.displayString = I18n.getString("options.quality.rain") + ": " + Utils.getTranslationString(Quality, OptimizeConfig.rainQuality);
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
        this.drawCenteredString(this.fontRenderer, I18n.getString("options.quality.settings"), this.width / 2, 16, 16777215);
        super.drawScreen(par1, par2, par3);
    }
}
