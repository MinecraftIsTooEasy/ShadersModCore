package shadersmodcore.client.dynamicLight;

import net.minecraft.*;
import shadersmodcore.mixin.accessor.RenderGlobalAccessor;
import shadersmodcore.config.ShaderConfig;
import shadersmodcore.util.BlockPos;
import net.xiaoyu233.fml.util.ReflectHelper;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class DynamicLight {
    private Entity entity;
    private double offsetY;
    private volatile double lastPosX = -2.147483648E9D;
    private volatile double lastPosY = -2.147483648E9D;
    private volatile double lastPosZ = -2.147483648E9D;
    private volatile int lastLightLevel = 0;
    private volatile boolean underwater = false;
    private long timeCheckMs = 0L;
    private RenderGlobal renderGlobal;
    private boolean willFlash = false;
    private int lightValue;
    private World world;
    private boolean changed;
    private final int[] chunkUpdateCoordinates = new int[24];

    public DynamicLight(Entity entity) {
        this.entity = entity;
        this.offsetY = entity.getEyeHeight();
    }

    public void update(RenderGlobal renderGlobal) {
        this.changed = false;
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
            WorldClient world = ((RenderGlobalAccessor)ReflectHelper.dyCast(renderGlobal)).getClientWorld();
            this.world = world;
            if (world != null) {
                Block block = world.getBlock(MathHelper.floor_double(d6), MathHelper.floor_double(d0), MathHelper.floor_double(d1));
                this.underwater = block == Block.waterStill;
            }

            if (j > 0) {
                this.updateChunkLight(new BlockPos((int) d6, (int) d0, (int) d1));
            }

            this.updateLitChunks(renderGlobal);
            this.changed = true;
        }

    }

    boolean updateAndReport(RenderGlobal renderGlobal) {
        this.update(renderGlobal);
        return this.changed;
    }

    private void updateChunkLight(BlockPos pos) {
        int d6 = pos.x();
        int d0 = pos.y();
        int d1 = pos.z();
        if (this.renderGlobal != null) {
            EnumFacing enumfacing2 = (MathHelper.floor_double((double)d6) & 15) >= 8 ? EnumFacing.EAST : EnumFacing.WEST;
            EnumFacing enumfacing = (MathHelper.floor_double((double)d0) & 15) >= 8 ? EnumFacing.UP : EnumFacing.DOWN;
            EnumFacing enumfacing1 = (MathHelper.floor_double((double)d1) & 15) >= 8 ? EnumFacing.SOUTH : EnumFacing.NORTH;

            for(int i = 0; i <= 16; ++i) {
                fillChunkUpdateCoordinates(this.chunkUpdateCoordinates, d6 + i, d0, d1 + i,
                    enumfacing2, enumfacing, enumfacing1);
                for (int index = 0; index < this.chunkUpdateCoordinates.length; index += 3) {
                    this.renderGlobal.markBlockForRenderUpdate(
                        this.chunkUpdateCoordinates[index],
                        this.chunkUpdateCoordinates[index + 1],
                        this.chunkUpdateCoordinates[index + 2]);
                }
            }
        }

    }

    static void fillChunkUpdateCoordinates(int[] coordinates, int x, int y, int z,
                                            EnumFacing xFacing, EnumFacing yFacing, EnumFacing zFacing) {
        int xOffset = xFacing.getFrontOffsetX() * 16;
        int yOffset = yFacing.getFrontOffsetY() * 16;
        int zOffset = zFacing.getFrontOffsetZ() * 16;
        coordinates[0] = x;
        coordinates[1] = y;
        coordinates[2] = z;
        coordinates[3] = x + xOffset;
        coordinates[4] = y;
        coordinates[5] = z;
        coordinates[6] = x;
        coordinates[7] = y;
        coordinates[8] = z + zOffset;
        coordinates[9] = x + xOffset;
        coordinates[10] = y;
        coordinates[11] = z + zOffset;
        coordinates[12] = x;
        coordinates[13] = y + yOffset;
        coordinates[14] = z;
        coordinates[15] = x + xOffset;
        coordinates[16] = y + yOffset;
        coordinates[17] = z;
        coordinates[18] = x;
        coordinates[19] = y + yOffset;
        coordinates[20] = z + zOffset;
        coordinates[21] = x + xOffset;
        coordinates[22] = y + yOffset;
        coordinates[23] = z + zOffset;
    }

    public void updateLitChunks(RenderGlobal renderGlobal) {
        if (this.lastLightLevel != 0) {
            this.willFlash = true;
        }

        if (this.lastLightLevel == 0 && this.willFlash) {
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

    public void setLightLevel(int lightLevel) {
        this.lastLightLevel = lightLevel;
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
