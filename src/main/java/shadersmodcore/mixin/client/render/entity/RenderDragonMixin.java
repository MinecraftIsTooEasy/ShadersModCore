package shadersmodcore.mixin.client.render.entity;

import net.minecraft.EntityDragon;
import net.minecraft.RenderDragon;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import shadersmodcore.util.Utils;

@Mixin(value = RenderDragon.class, priority = 999)
public abstract class RenderDragonMixin {

    //Dragon death beams
    @Inject(method = "renderDragonDying",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/Tessellator;startDrawing(I)V"))
    protected void renderEquippedItems(EntityDragon par1EntityDragon, float par2, CallbackInfo ci) {
        Utils.Fix();
    }
}
