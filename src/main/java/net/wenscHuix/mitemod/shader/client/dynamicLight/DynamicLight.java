package net.wenscHuix.mitemod.shader.client.dynamicLight;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import net.minecraft.*;
import net.wenscHuix.mitemod.shader.client.dynamicLight.config.ShaderConfig;
import net.wenscHuix.mitemod.shader.util.BlockPos;

public class DynamicLight {
   private Entity entity;
   private double offsetY;
   private double lastPosX = -2.147483648E9D;
   private double lastPosY = -2.147483648E9D;
   private double lastPosZ = -2.147483648E9D;
   private int lastLightLevel = 0;
   private boolean underwater = false;
   private long timeCheckMs = 0L;
   private RenderGlobal renderGlobal;
   private boolean willFlash = false;
   private int lightValue;
   private World world;

   public DynamicLight(Entity entity) {
      this.entity = entity;
      this.offsetY = entity.getEyeHeight();
   }

   public void update(RenderGlobal renderGlobal) {
      this.renderGlobal = renderGlobal;
      if (ShaderConfig.isDynamicLightsFast()) {
         long i = System.currentTimeMillis();
         if (i < this.timeCheckMs + 500L) {
            return;
         }

         this.timeCheckMs = i;
      }

      double d6 = this.entity.posX - 0.5D;
      double d0 = this.entity.posY - 0.5D + this.offsetY;
      double d1 = this.entity.posZ - 0.5D;
      int j = DynamicLights.getLightLevel(this.entity);
      this.lightValue = j;
      double d2 = d6 - this.lastPosX;
      double d3 = d0 - this.lastPosY;
      double d4 = d1 - this.lastPosZ;
      double d5 = 0.1D;
      if (Math.abs(d2) > d5 || Math.abs(d3) > d5 || Math.abs(d4) > d5 || this.lastLightLevel != j) {
         this.lastPosX = d6;
         this.lastPosY = d0;
         this.lastPosZ = d1;
         this.lastLightLevel = j;
         this.underwater = false;
         WorldClient world = renderGlobal.getClientWorld();
         this.world = world;
         if (world != null) {
            Block block = world.getBlock(MathHelper.floor_double(d6), MathHelper.floor_double(d0), MathHelper.floor_double(d1));
            this.underwater = block == Block.waterStill;
         }

         if (j > 0) {
            this.updateChunkLight(new BlockPos(d6, d0, d1));
         }

         this.updateLitChunks(renderGlobal);
      }

   }

   private BlockPos getChunkPos(BlockPos pos, EnumFacing facing) {
      return pos.offset(facing, 16);
   }

   private void updateChunkLight(BlockPos pos) {
      int d6 = pos.x;
      int d0 = pos.y;
      int d1 = pos.z;
      if (this.renderGlobal != null) {
         EnumFacing enumfacing2 = (MathHelper.floor_double((double)d6) & 15) >= 8 ? EnumFacing.EAST : EnumFacing.WEST;
         EnumFacing enumfacing = (MathHelper.floor_double((double)d0) & 15) >= 8 ? EnumFacing.UP : EnumFacing.DOWN;
         EnumFacing enumfacing1 = (MathHelper.floor_double((double)d1) & 15) >= 8 ? EnumFacing.SOUTH : EnumFacing.NORTH;

         for(int i = 0; i <= 16; ++i) {
            BlockPos blockpos = new BlockPos(d6 + i, d0, d1 + i);
            BlockPos blockpos1 = this.getChunkPos(blockpos, enumfacing2);
            BlockPos blockpos2 = this.getChunkPos(blockpos, enumfacing1);
            BlockPos blockpos3 = this.getChunkPos(blockpos1, enumfacing1);
            BlockPos blockpos4 = this.getChunkPos(blockpos, enumfacing);
            BlockPos blockpos5 = this.getChunkPos(blockpos4, enumfacing2);
            BlockPos blockpos6 = this.getChunkPos(blockpos4, enumfacing1);
            BlockPos blockpos7 = this.getChunkPos(blockpos5, enumfacing1);
            this.renderGlobal.markBlockForRenderUpdate(blockpos.x, blockpos.y, blockpos.z);
            this.renderGlobal.markBlockForRenderUpdate(blockpos1.x, blockpos1.y, blockpos1.z);
            this.renderGlobal.markBlockForRenderUpdate(blockpos2.x, blockpos2.y, blockpos2.z);
            this.renderGlobal.markBlockForRenderUpdate(blockpos3.x, blockpos3.y, blockpos3.z);
            this.renderGlobal.markBlockForRenderUpdate(blockpos4.x, blockpos4.y, blockpos4.z);
            this.renderGlobal.markBlockForRenderUpdate(blockpos5.x, blockpos5.y, blockpos5.z);
            this.renderGlobal.markBlockForRenderUpdate(blockpos6.x, blockpos6.y, blockpos6.z);
            this.renderGlobal.markBlockForRenderUpdate(blockpos7.x, blockpos7.y, blockpos7.z);
         }
      }

   }

   public void updateLitChunks(RenderGlobal renderGlobal) {
      if (DynamicLights.getLightLevel(this.entity) != 0) {
         Runnable task = () -> {
            this.willFlash = true;
         };
         ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
         executor.scheduleAtFixedRate(task, 0L, 30L, TimeUnit.SECONDS);
      }

      if (DynamicLights.getLightLevel(this.entity) == 0 && this.willFlash) {
         this.willFlash = false;
         renderGlobal.markAllRenderersUninitialized();
      }

   }

   public Entity getEntity() {
      return this.entity;
   }

   public double getLastPosX() {
      return this.lastPosX;
   }

   public double getLastPosY() {
      return this.lastPosY;
   }

   public double getLastPosZ() {
      return this.lastPosZ;
   }

   public int getLastLightLevel() {
      return this.lastLightLevel;
   }

   public boolean isUnderwater() {
      return this.underwater;
   }

   public double getOffsetY() {
      return this.offsetY;
   }

   public String toString() {
      return "Entity: " + this.entity + ", offsetY: " + this.offsetY;
   }
}
