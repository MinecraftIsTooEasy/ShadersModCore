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
   @Shadow protected abstract boolean renderBlockHopper(BlockHopper par1BlockHopper, int par2, int par3, int par4);

   @Shadow protected abstract boolean renderBlockRedstoneLogic(BlockRedstoneLogic par1BlockRedstoneLogic, int par2, int par3, int par4);

   @Shadow protected abstract boolean renderBlockAnvil(BlockAnvil par1BlockAnvil, int par2, int par3, int par4);

   @Shadow protected abstract boolean renderBlockBeacon(BlockBeacon par1BlockBeacon, int par2, int par3, int par4);

   @Shadow protected abstract boolean renderBlockFlowerpot(BlockFlowerPot par1BlockFlowerPot, int par2, int par3, int par4);

   @Shadow public abstract boolean renderBlockWall(BlockWall par1BlockWall, int par2, int par3, int par4);

   @Shadow public abstract boolean renderBlockLog(Block par1Block, int par2, int par3, int par4);

   @Shadow public abstract boolean renderBlockTripWire(Block par1Block, int par2, int par3, int par4);

   @Shadow public abstract boolean renderBlockTripWireSource(Block par1Block, int par2, int par3, int par4);

   @Shadow protected abstract boolean renderBlockCocoa(BlockCocoa par1BlockCocoa, int par2, int par3, int par4);

   @Shadow public abstract boolean renderBlockDragonEgg(BlockDragonEgg par1BlockDragonEgg, int par2, int par3, int par4);

   @Shadow protected abstract boolean renderBlockEndPortalFrame(BlockEndPortalFrame par1BlockEndPortalFrame, int par2, int par3, int par4);

   @Shadow protected abstract boolean renderBlockBrewingStand(BlockBrewingStand par1BlockBrewingStand, int par2, int par3, int par4);

   @Shadow protected abstract boolean renderBlockCauldron(BlockCauldron par1BlockCauldron, int par2, int par3, int par4);

   @Shadow public abstract boolean renderBlockLilyPad(Block par1Block, int par2, int par3, int par4);

   @Shadow public abstract boolean renderBlockFenceGate(BlockFenceGate par1BlockFenceGate, int par2, int par3, int par4);

   @Shadow public abstract boolean renderBlockVine(Block par1Block, int par2, int par3, int par4);

   @Shadow public abstract boolean renderBlockStem(Block par1Block, int par2, int par3, int par4);

   @Shadow public abstract boolean renderBlockPane(BlockPane par1BlockPane, int par2, int par3, int par4);

   @Shadow protected abstract boolean renderPistonExtension(Block par1Block, int par2, int par3, int par4, boolean par5);

   @Shadow protected abstract boolean renderPistonBase(Block par1Block, int par2, int par3, int par4, boolean par5);

   @Shadow protected abstract boolean renderBlockRepeater(BlockRedstoneRepeater par1BlockRedstoneRepeater, int par2, int par3, int par4);

   @Shadow protected abstract boolean renderBlockBed(Block par1Block, int par2, int par3, int par4);

   @Shadow public abstract boolean renderBlockCactus(Block par1Block, int par2, int par3, int par4);

   @Shadow public abstract boolean renderBlockLever(Block par1Block, int par2, int par3, int par4);

   @Shadow public abstract boolean renderBlockFence(BlockFence par1BlockFence, int par2, int par3, int par4);

   @Shadow public abstract boolean renderBlockStairs(BlockStairs par1BlockStairs, int par2, int par3, int par4);

   @Shadow public abstract boolean renderBlockMinecartTrack(BlockRailBase par1BlockRailBase, int par2, int par3, int par4);

   @Shadow public abstract boolean renderBlockLadder(Block par1Block, int par2, int par3, int par4);

   @Shadow public abstract boolean renderBlockDoor(Block par1Block, int par2, int par3, int par4);

   @Shadow public abstract boolean renderBlockCrops(Block par1Block, int par2, int par3, int par4);

   @Shadow public abstract boolean renderBlockRedstoneWire(Block par1Block, int par2, int par3, int par4);

   @Shadow public abstract boolean renderBlockFluids(Block par1Block, int par2, int par3, int par4);

   @Shadow public abstract boolean renderBlockFire(BlockFire par1BlockFire, int par2, int par3, int par4);

   @Shadow public abstract boolean renderBlockTorch(Block par1Block, int par2, int par3, int par4);

   @Shadow public abstract boolean renderCrossedSquares(Block par1Block, int par2, int par3, int par4);

   @Shadow public abstract boolean renderStandardBlock(Block par1Block, int par2, int par3, int par4);

   @Shadow public abstract void setRenderBoundsForStandardFormBlock();

   @Shadow public abstract void setRenderBoundsForNonStandardFormBlock(Block block);

   @Shadow public IBlockAccess blockAccess;
   @Shadow private Icon overrideBlockTexture;

   @Shadow public abstract boolean renderBlockQuartz(Block par1Block, int par2, int par3, int par4);

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

//   /**
//    * @author
//    * @reason
//    */
//   @Overwrite
//   public boolean renderBlockByRenderType(Block par1Block, int par2, int par3, int par4) {
//      Shaders.pushEntity(par1Block);
//      int renderType = par1Block.getRenderType();
//      if (renderType == -1) {
//         Shaders.popEntity();
//         return false;
//      } else {
//         if (this.overrideBlockTexture != null && renderType == 22) {
//            renderType = 0;
//         }
//
//         if (par1Block.isAlwaysStandardFormCube()) {
//            this.setRenderBoundsForStandardFormBlock();
//         } else {
//            par1Block.setBlockBoundsBasedOnStateAndNeighbors(this.blockAccess, par2, par3, par4);
//            this.setRenderBoundsForNonStandardFormBlock(par1Block);
//         }
//
//         boolean value;
//         if (renderType == 0) {
//            value = this.renderStandardBlock(par1Block, par2, par3, par4);
//            Shaders.popEntity();
//            return value;
//         } else if (renderType == 1) {
//            value = this.renderCrossedSquares(par1Block, par2, par3, par4);
//            Shaders.popEntity();
//            return value;
//         } else if (renderType == 2) {
//            value = this.renderBlockTorch(par1Block, par2, par3, par4);
//            Shaders.popEntity();
//            return value;
//         } else if (renderType == 3) {
//            value = this.renderBlockFire((BlockFire)par1Block, par2, par3, par4);
//            Shaders.popEntity();
//            return value;
//         } else if (renderType == 4) {
//            value = this.renderBlockFluids(par1Block, par2, par3, par4);
//            Shaders.popEntity();
//            return value;
//         } else if (renderType == 5) {
//            value = this.renderBlockRedstoneWire(par1Block, par2, par3, par4);
//            Shaders.popEntity();
//            return value;
//         } else if (renderType == 6) {
//            value = this.renderBlockCrops(par1Block, par2, par3, par4);
//            Shaders.popEntity();
//            return value;
//         } else if (renderType == 7) {
//            value = this.renderBlockDoor(par1Block, par2, par3, par4);
//            Shaders.popEntity();
//            return value;
//         } else if (renderType == 8) {
//            value = this.renderBlockLadder(par1Block, par2, par3, par4);
//            Shaders.popEntity();
//            return value;
//         } else if (renderType == 9) {
//            value = this.renderBlockMinecartTrack((BlockRailBase)par1Block, par2, par3, par4);
//            Shaders.popEntity();
//            return value;
//         } else if (renderType == 10) {
//            value = this.renderBlockStairs((BlockStairs)par1Block, par2, par3, par4);
//            Shaders.popEntity();
//            return value;
//         } else if (renderType == 11) {
//            value = this.renderBlockFence((BlockFence)par1Block, par2, par3, par4);
//            Shaders.popEntity();
//            return value;
//         } else if (renderType == 12) {
//            value = this.renderBlockLever(par1Block, par2, par3, par4);
//            Shaders.popEntity();
//            return value;
//         } else if (renderType == 13) {
//            value = this.renderBlockCactus(par1Block, par2, par3, par4);
//            Shaders.popEntity();
//            return value;
//         } else if (renderType == 14) {
//            value = this.renderBlockBed(par1Block, par2, par3, par4);
//            Shaders.popEntity();
//            return value;
//         } else if (renderType == 15) {
//            value = this.renderBlockRepeater((BlockRedstoneRepeater)par1Block, par2, par3, par4);
//            Shaders.popEntity();
//            return value;
//         } else if (renderType == 16) {
//            value = this.renderPistonBase(par1Block, par2, par3, par4, false);
//            Shaders.popEntity();
//            return value;
//         } else if (renderType == 17) {
//            value = this.renderPistonExtension(par1Block, par2, par3, par4, true);
//            Shaders.popEntity();
//            return value;
//         } else if (renderType == 18) {
//            value = this.renderBlockPane((BlockPane)par1Block, par2, par3, par4);
//            Shaders.popEntity();
//            return value;
//         } else if (renderType == 19) {
//            value = this.renderBlockStem(par1Block, par2, par3, par4);
//            Shaders.popEntity();
//            return value;
//         } else if (renderType == 20) {
//            value = this.renderBlockVine(par1Block, par2, par3, par4);
//            Shaders.popEntity();
//            return value;
//         } else if (renderType == 21) {
//            value = this.renderBlockFenceGate((BlockFenceGate)par1Block, par2, par3, par4);
//            Shaders.popEntity();
//            return value;
//         } else if (renderType == 23) {
//            value = this.renderBlockLilyPad(par1Block, par2, par3, par4);
//            Shaders.popEntity();
//            return value;
//         } else if (renderType == 24) {
//            value = this.renderBlockCauldron((BlockCauldron)par1Block, par2, par3, par4);
//            Shaders.popEntity();
//            return value;
//         } else if (renderType == 25) {
//            value = this.renderBlockBrewingStand((BlockBrewingStand)par1Block, par2, par3, par4);
//            Shaders.popEntity();
//            return value;
//         } else if (renderType == 26) {
//            value = this.renderBlockEndPortalFrame((BlockEndPortalFrame)par1Block, par2, par3, par4);
//            Shaders.popEntity();
//            return value;
//         } else if (renderType == 27) {
//            value = this.renderBlockDragonEgg((BlockDragonEgg)par1Block, par2, par3, par4);
//            Shaders.popEntity();
//            return value;
//         } else if (renderType == 28) {
//            value = this.renderBlockCocoa((BlockCocoa)par1Block, par2, par3, par4);
//            Shaders.popEntity();
//            return value;
//         } else if (renderType == 29) {
//            value = this.renderBlockTripWireSource(par1Block, par2, par3, par4);
//            Shaders.popEntity();
//            return value;
//         } else if (renderType == 30) {
//            value = this.renderBlockTripWire(par1Block, par2, par3, par4);
//            Shaders.popEntity();
//            return value;
//         } else if (renderType == 31) {
//            value = this.renderBlockLog(par1Block, par2, par3, par4);
//            Shaders.popEntity();
//            return value;
//         } else if (renderType == 32) {
//            value = this.renderBlockWall((BlockWall)par1Block, par2, par3, par4);
//            Shaders.popEntity();
//            return value;
//         } else if (renderType == 33) {
//            value = this.renderBlockFlowerpot((BlockFlowerPot)par1Block, par2, par3, par4);
//            Shaders.popEntity();
//            return value;
//         } else if (renderType == 34) {
//            value = this.renderBlockBeacon((BlockBeacon)par1Block, par2, par3, par4);
//            Shaders.popEntity();
//            return value;
//         } else if (renderType == 35) {
//            value = this.renderBlockAnvil((BlockAnvil)par1Block, par2, par3, par4);
//            Shaders.popEntity();
//            return value;
//         } else if (renderType == 36) {
//            value = this.renderBlockRedstoneLogic((BlockRedstoneLogic)par1Block, par2, par3, par4);
//            Shaders.popEntity();
//            return value;
//         } else if (renderType == 37) {
//            value = this.renderBlockRedstoneLogic((BlockRedstoneLogic)par1Block, par2, par3, par4);
//            Shaders.popEntity();
//            return value;
//         } else if (renderType == 38) {
//            value = this.renderBlockHopper((BlockHopper)par1Block, par2, par3, par4);
//            Shaders.popEntity();
//            return value;
//         } else if (renderType == 39) {
//            value = this.renderBlockQuartz(par1Block, par2, par3, par4);
//            Shaders.popEntity();
//            return value;
//         } else {
//            Shaders.popEntity();
//            return false;
//         }
//      }
//   }

   @Inject(method = "renderBlockByRenderType", at = @At(value = "INVOKE", target = "Lnet/minecraft/Block;getRenderType()I"))
   private void pushEntity(Block par1Block, int par2, int par3, int par4, CallbackInfoReturnable<Boolean> cir) {
      Shaders.pushEntity(par1Block);
   }

   @Inject(method = "renderBlockByRenderType", at = @At("RETURN"))
   private void popEntity(Block par1Block, int par2, int par3, int par4, CallbackInfoReturnable<Boolean> cir) {
      Shaders.popEntity();
   }

}
