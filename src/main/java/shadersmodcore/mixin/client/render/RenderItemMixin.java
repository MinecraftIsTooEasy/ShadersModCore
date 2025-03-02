package shadersmodcore.mixin.client.render;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.GameSettings;
import net.minecraft.RenderItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import shadersmodcore.config.OptimizeConfig;
import shadersmodcore.util.Utils;

@Mixin(RenderItem.class)
public class RenderItemMixin {
    @WrapOperation(method = "renderDroppedItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/GameSettings;isFancyGraphicsEnabled()Z"))
    private boolean leavesQuality(GameSettings instance, Operation<Boolean> original) {
        if (OptimizeConfig.dropsQuality != 0)
            return Utils.convertIntToBoolean(OptimizeConfig.dropsQuality);
        return original.call(instance);
    }
}
