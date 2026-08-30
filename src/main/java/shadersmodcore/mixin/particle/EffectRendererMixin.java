package shadersmodcore.mixin.particle;

import net.minecraft.AxisAlignedBB;
import net.minecraft.Entity;
import net.minecraft.EffectRenderer;
import shadersmodcore.config.OptimizeConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import shadersmodcore.client.optimize.ParticleRenderOptimizer;

@Mixin({EffectRenderer.class})
public class EffectRendererMixin {

    @Shadow
    private List<?>[] fxLayers;

    @Inject(method = "renderParticles", at = @At("HEAD"), cancellable = true)
    private void skipEmptyRegularParticles(Entity entity, float partialTicks, CallbackInfo info) {
        if (entity != null && !ParticleRenderOptimizer.shouldRender(this.fxLayers, OptimizeConfig.skipEmptyParticleRender)) {
            info.cancel();
        }
    }

    @Inject(method = "addBlockDestroyEffects(IIIIII)V", at = @At("HEAD"), cancellable = true)
    public void addBlockDestroyEffects(int x, int y, int z, int block_id, int metadata, int aux_data, CallbackInfo info) {
        if (OptimizeConfig.blockDestroyEffects) {
            this.addBlockDestroyEffects(x, y, z, block_id, metadata, aux_data, (AxisAlignedBB)null);
        }
        info.cancel();
    }

    @Shadow
    public void addBlockDestroyEffects(int x, int y, int z, int block_id, int metadata, int aux_data, AxisAlignedBB bounds_of_exclusion) {}
}
