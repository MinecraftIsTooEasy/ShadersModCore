package shadersmodcore.mixin.client.render.entity;

import net.minecraft.EntityFishHook;
import net.minecraft.Render;
import net.minecraft.RenderFish;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import shadersmodcore.util.Utils;

@Mixin(value = RenderFish.class, priority = 999)
public abstract class RenderFishMixin extends Render {

    //Fish line
    @Inject(method = "doRenderFishHook",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/Tessellator;startDrawing(I)V"))
    public void doRender(EntityFishHook par1EntityFishHook, double par2, double par4, double par6, float par8, float par9, CallbackInfo ci) {
        Utils.Fix();
    }
}

