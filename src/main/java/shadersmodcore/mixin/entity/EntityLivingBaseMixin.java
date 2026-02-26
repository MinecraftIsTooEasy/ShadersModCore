package shadersmodcore.mixin.entity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.EntityLivingBase;
import net.minecraft.EnumParticle;
import net.minecraft.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import shadersmodcore.config.OptimizeConfig;

@Mixin(EntityLivingBase.class)
public abstract class EntityLivingBaseMixin {
    @WrapOperation(method = "updatePotionEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/World;spawnParticle(Lnet/minecraft/EnumParticle;DDDDDD)V"))
    private void disablePotionEffects(World instance, EnumParticle enumParticle, double enum_particle, double par2, double par4, double par6, double par8, double par10, Operation<Void> original) {
        if (OptimizeConfig.potionEffects)
            original.call(instance, enumParticle, enum_particle, par2, par4, par6, par8, par10);
    }
}
