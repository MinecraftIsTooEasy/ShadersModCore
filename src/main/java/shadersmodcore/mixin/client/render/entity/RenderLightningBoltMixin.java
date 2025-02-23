package shadersmodcore.mixin.client.render.entity;

import net.minecraft.EntityLightningBolt;
import net.minecraft.Render;
import net.minecraft.RenderLightningBolt;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import shadersmodcore.util.Utils;

@Mixin(value = RenderLightningBolt.class, priority = 999)
public abstract class RenderLightningBoltMixin extends Render {

    //Lightning Bolt
    @Inject(method = "doRenderLightningBolt",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/Tessellator;startDrawing(I)V"))
    public void doRender(EntityLightningBolt p_76986_1_, double p_76986_2_, double p_76986_4_, double p_76986_6_, float p_76986_8_, float p_76986_9_, CallbackInfo ci ) {
        Utils.Fix();
        Utils.EnableFullBrightness();
    }
}
