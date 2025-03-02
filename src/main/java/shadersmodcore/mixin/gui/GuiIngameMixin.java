package shadersmodcore.mixin.gui;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.GuiIngame;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import shadersmodcore.config.OptimizeConfig;

@Mixin(GuiIngame.class)
public class GuiIngameMixin {
    @WrapOperation(method = "renderGameOverlay", at = @At(value = "INVOKE", target = "Lnet/minecraft/GuiIngame;renderVignette(FII)V"))
    private void vignetteQuality(GuiIngame instance, float v, int par1, int par2, Operation<Void> original) {
        if (OptimizeConfig.vignetteQuality)
            original.call(instance, v, par1, par2);
    }
}
