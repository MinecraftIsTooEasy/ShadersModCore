package shadersmodcore.mixin.client.render;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.EnumParticle;
import net.minecraft.RenderGlobal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import shadersmodcore.config.OptimizeConfig;

@Mixin(RenderGlobal.class)
public class RenderParticleMixin {
    @WrapOperation(method = "doSpawnParticle", at = @At(value = "FIELD", target = "Lnet/minecraft/EnumParticle;hugeexplosion:Lnet/minecraft/EnumParticle;"))
    private EnumParticle hugeexplosion(Operation<EnumParticle> original) {
        if (OptimizeConfig.explodeEffects)
            return EnumParticle.hugeexplosion;
        return null;
    }

    @WrapOperation(method = "doSpawnParticle", at = @At(value = "FIELD", target = "Lnet/minecraft/EnumParticle;largeexplode:Lnet/minecraft/EnumParticle;"))
    private EnumParticle largeexplode(Operation<EnumParticle> original) {
        if (OptimizeConfig.explodeEffects)
            return EnumParticle.largeexplode;
        return null;
    }

    @WrapOperation(method = "doSpawnParticle", at = @At(value = "FIELD", target = "Lnet/minecraft/EnumParticle;explode:Lnet/minecraft/EnumParticle;"))
    private EnumParticle explode(Operation<EnumParticle> original) {
        if (OptimizeConfig.explodeEffects)
            return EnumParticle.explode;
        return null;
    }
}
