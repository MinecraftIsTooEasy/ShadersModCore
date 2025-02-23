package shadersmodcore.mixin.client.render.entity;

import net.minecraft.EntityLiving;
import net.minecraft.RenderLiving;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import shadersmodcore.util.Utils;

@Mixin(value = RenderLiving.class, priority = 999)
public abstract class RenderLivingMixin {

    //Leash render
    @Inject(method = "func_110827_b",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/Tessellator;startDrawing(I)V"))
    protected void func_110827_b(EntityLiving p_110827_1_, double p_110827_2_, double p_110827_4_, double p_110827_6_, float p_110827_8_, float p_110827_9_, CallbackInfo ci) {
        Utils.Fix();
    }
}
