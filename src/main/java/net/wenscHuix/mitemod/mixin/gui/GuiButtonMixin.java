package net.wenscHuix.mitemod.mixin.gui;

import net.minecraft.GuiButton;
import net.minecraft.I18n;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(GuiButton.class)
public class GuiButtonMixin {
    @Shadow protected int width;

    @Inject(method = "<init>(IIIIILjava/lang/String;)V", at = @At("RETURN"))
    private void setGuiWidth(int par1, int par2, int par3, int par4, int par5, String par6Str, CallbackInfo ci) {
        if ((par1 == 100 && Objects.equals(par6Str, I18n.getString("options.controls"))) ||
                par1 == 101 && Objects.equals(par6Str, I18n.getString("options.video"))) {
            this.width = par4 / 2 - 2;
        }
    }
}
