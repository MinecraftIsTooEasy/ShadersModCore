package shadersmodcore.mixin.client.render;

import net.minecraft.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import shadersmodcore.client.shader.Shaders;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin({RenderBlocks.class})
public abstract class RenderBlocksMixin {
   @ModifyConstant(constant = @Constant(floatValue = 0.5F, ordinal = 0),
      method = {"renderBlockBed", "renderBlockFluids", "renderBlockSandFalling", "renderStandardBlockWithColorMultiplier", "renderBlockCactusImpl", "renderBlockDoor"}
   )
   private float injectedRenderBlockBed0(float value) {
      return Shaders.blockLightLevel05;
   }

   @ModifyConstant(constant = @Constant(floatValue = 0.8F, ordinal = 0),
      method = {"renderBlockBed", "renderBlockFluids", "renderBlockSandFalling", "renderStandardBlockWithColorMultiplier", "renderBlockCactusImpl", "renderBlockDoor"}
   )
   private float injectedRenderBlockBed1(float value) {
      return Shaders.blockLightLevel08;
   }

   @ModifyConstant(constant = @Constant(floatValue = 0.6F, ordinal = 0),
      method = {"renderBlockBed", "renderBlockFluids", "renderBlockSandFalling", "renderStandardBlockWithColorMultiplier", "renderBlockCactusImpl", "renderBlockDoor"}
   )
   private float injectedRenderBlockBed2(float value) {
      return Shaders.blockLightLevel06;
   }

   @ModifyConstant(constant = @Constant(floatValue = 0.5F),
      method = {"renderPistonExtension", "renderStandardBlockWithAmbientOcclusion", "renderStandardBlockWithAmbientOcclusionPartial"}
   )
   private float injectedRenderPistonExtension0(float value) {
      return Shaders.blockLightLevel05;
   }

   @ModifyConstant(constant = @Constant(floatValue = 0.8F),
      method = {"renderPistonExtension", "renderStandardBlockWithAmbientOcclusion", "renderStandardBlockWithAmbientOcclusionPartial"}
   )
   private float injectedRenderPistonExtension1(float value) {
      return Shaders.blockLightLevel08;
   }

   @ModifyConstant(constant = @Constant(floatValue = 0.6F),
      method = {"renderPistonExtension", "renderStandardBlockWithAmbientOcclusion", "renderStandardBlockWithAmbientOcclusionPartial"}
   )
   private float injectedRenderPistonExtension2(float value) {
      return Shaders.blockLightLevel06;
   }

   @Inject(method = "renderBlockByRenderType", at = @At(value = "INVOKE", target = "Lnet/minecraft/Block;getRenderType()I"))
   private void pushEntity(Block par1Block, int par2, int par3, int par4, CallbackInfoReturnable<Boolean> cir) {
      Shaders.pushEntity(par1Block);
   }

   @Inject(method = "renderBlockByRenderType", at = @At("RETURN"))
   private void popEntity(Block par1Block, int par2, int par3, int par4, CallbackInfoReturnable<Boolean> cir) {
      Shaders.popEntity();
   }

}
