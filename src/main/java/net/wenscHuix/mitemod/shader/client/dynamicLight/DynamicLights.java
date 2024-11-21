package net.wenscHuix.mitemod.shader.client.dynamicLight;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.minecraft.*;
import net.minecraft.RenderGlobal;
import net.wenscHuix.mitemod.imixin.BlockAccessor;
import net.wenscHuix.mitemod.imixin.RenderGlobalAccessor;
import net.wenscHuix.mitemod.shader.client.dynamicLight.config.ShaderConfig;
import net.wenscHuix.mitemod.shader.util.BlockPos;
import net.xiaoyu233.fml.util.ReflectHelper;

public class DynamicLights {
   private static final DynamicLightsMap mapDynamicLights = new DynamicLightsMap();
   private static Map mapEntityLightLevels = new HashMap();
   private static Map mapItemLightLevels = new HashMap();
   private static long timeUpdateMs = 0L;
   private static boolean initialized;

   public static void entityAdded(Entity entityIn, RenderGlobal renderGlobal) {
   }

   public static void entityRemoved(Entity entityIn, RenderGlobal renderGlobal) {
      synchronized(mapDynamicLights) {
         DynamicLight dynamiclight = mapDynamicLights.remove(entityIn.entityId);
         if (dynamiclight != null) {
            dynamiclight.updateLitChunks(renderGlobal);
         }

      }
   }

   public static void update(RenderGlobal renderGlobal) {
      long i = System.currentTimeMillis();
      if (i >= timeUpdateMs + 50L) {
         timeUpdateMs = i;
         if (!initialized) {
            initialize();
         }

         synchronized(mapDynamicLights) {
            updateMapDynamicLights(renderGlobal);
            if (mapDynamicLights.size() > 0) {
               List list = mapDynamicLights.valueList();

                for (Object o : list) {
                    DynamicLight dynamiclight = (DynamicLight) o;
                    dynamiclight.update(renderGlobal);
                }
            }
         }
      }

   }

   private static void initialize() {
      initialized = true;
      mapEntityLightLevels.clear();
      mapItemLightLevels.clear();
      if (!mapEntityLightLevels.isEmpty()) {
         ShaderConfig.dbg("DynamicLights entities: " + mapEntityLightLevels.size());
      }

      if (!mapItemLightLevels.isEmpty()) {
         ShaderConfig.dbg("DynamicLights items: " + mapItemLightLevels.size());
      }

   }

   private static void updateMapDynamicLights(RenderGlobal renderGlobal) {
      World world = ((RenderGlobalAccessor) ReflectHelper.dyCast(renderGlobal)).getClientWorld();
      if (world != null) {

          for (Object list : world.loadedEntityList) {
              if (list instanceof Entity entity) {
                  int i = getLightLevel(entity);
                  int j;
                  DynamicLight dynamiclight;
                  if (i > 0) {
                      j = entity.entityId;
                      dynamiclight = mapDynamicLights.get(j);
                      if (dynamiclight == null) {
                          dynamiclight = new DynamicLight(entity);
                          mapDynamicLights.put(j, dynamiclight);
                      }
                  } else {
                      j = entity.entityId;
                      dynamiclight = mapDynamicLights.remove(j);
                      if (dynamiclight != null) {
                          dynamiclight.updateLitChunks(renderGlobal);
                      }
                  }
              }
          }
      }

   }

   public static int getCombinedLight(BlockPos pos, int combinedLight) {
      double d0 = getLightLevel(pos);
      combinedLight = getCombinedLight(d0, combinedLight);
      return combinedLight;
   }

   public static int getCombinedLight(Entity entity, int combinedLight) {
      double d0 = (double)getLightLevel(entity);
      combinedLight = getCombinedLight(d0, combinedLight);
      return combinedLight;
   }

   public static int getCombinedLight(double lightPlayer, int combinedLight) {
      if (lightPlayer > 0.0D) {
         int i = (int)(lightPlayer * 16.0D);
         int j = combinedLight & 255;
         if (i > j) {
            combinedLight &= -256;
            combinedLight |= i;
         }
      }

      return combinedLight;
   }

   public static double getLightLevel(BlockPos pos) {
      double d0 = 0.0D;
      synchronized(mapDynamicLights) {
         List list = mapDynamicLights.valueList();
         int i = list.size();

          for (Object o : list) {
              DynamicLight dynamiclight = (DynamicLight) o;
              int k = dynamiclight.getLastLightLevel();
              if (k > 0) {
                  double d1 = dynamiclight.getLastPosX();
                  double d2 = dynamiclight.getLastPosY();
                  double d3 = dynamiclight.getLastPosZ();
                  double d4 = (double) pos.x - d1;
                  double d5 = (double) pos.y - d2;
                  double d6 = (double) pos.z - d3;
                  double d7 = d4 * d4 + d5 * d5 + d6 * d6;
                  if (dynamiclight.isUnderwater() && !ShaderConfig.isClearWater()) {
                      k = ShaderConfig.limit(k - 2, 0, 15);
                      d7 *= 2.0D;
                  }

                  if (d7 <= 56.25D) {
                      double d8 = Math.sqrt(d7);
                      double d9 = 1.0D - d8 / 7.5D;
                      double d10 = d9 * (double) k;
                      if (d10 > d0) {
                          d0 = d10;
                      }
                  }
              }
          }
      }

      return ShaderConfig.limit(d0, 0.0D, 15.0D);
   }

   public static int getLightLevel(ItemStack itemStack) {
      if (itemStack == null) {
         return 0;
      } else {
         Item item = itemStack.getItem();
         if (item instanceof ItemBlock itemblock) {
             Block block = itemblock.getBlock();
            if (block != null) {
               return ((BlockAccessor) ReflectHelper.dyCast(block)).mITE_Shader_Loader$getLightValue();
            }
         }

         if (item != Item.bucketAdamantiumLava && item != Item.bucketGoldLava && item != Item.bucketCopperLava && item != Item.bucketIronLava && item != Item.bucketMithrilLava && item != Item.bucketAncientMetalLava && item != Item.bucketSilverLava) {
            if (item instanceof ItemCoin) {
               if (item == Item.coinAncientMetal) {
                  return 10;
               } else if (item == Item.coinMithril) {
                  return 12;
               } else {
                  return item == Item.coinAdamantium ? 14 : 5;
               }
            } else if (item != Item.blazeRod && item != Item.blazePowder) {
               if (item == Item.glowstone) {
                  return 8;
               } else if (item == Item.magmaCream) {
                  return 8;
               } else if (item == Item.netherStar) {
                  return ((BlockAccessor) ReflectHelper.dyCast(Block.beacon)).mITE_Shader_Loader$getLightValue() / 2;
               } else {
                  if (!mapItemLightLevels.isEmpty()) {
                     Integer integer = (Integer)mapItemLightLevels.get(item);
                     if (integer != null) {
                        return integer;
                     }
                  }

                  return 0;
               }
            } else {
               return 10;
            }
         } else {
            return ((BlockAccessor) ReflectHelper.dyCast(Block.lavaStill)).mITE_Shader_Loader$getLightValue();
         }
      }
   }

   public static int getLightLevel(Entity entity) {
      if (entity == ShaderConfig.getMinecraft().renderViewEntity && !ShaderConfig.isDynamicHandLight()) {
         return 0;
      } else if (entity.isBurning()) {
         return 15;
      } else {
         if (!mapEntityLightLevels.isEmpty()) {
            Integer integer = (Integer)mapEntityLightLevels.get(entity.getClass());
            if (integer != null) {
               return integer;
            }
         }

         if (entity instanceof EntityFireball) {
            return 15;
         } else if (entity instanceof EntityTNTPrimed) {
            return 15;
         } else if (entity instanceof EntityBlaze entityblaze) {
             return entityblaze.func_70845_n() ? 15 : 10;
         } else if (entity instanceof EntityMagmaCube entitymagmacube) {
             return (double)entitymagmacube.squishFactor > 0.6D ? 13 : 8;
         } else {
            if (entity instanceof EntityCreeper entitycreeper) {
                if ((double)entitycreeper.getCreeperFlashIntensity(0.0F) > 0.001D) {
                  return 15;
               }
            }

            ItemStack itemstack;
            if (entity instanceof EntityLivingBase entityLiving) {
                itemstack = entityLiving.getHeldItemStack();
                int i = getLightLevel(itemstack);
                ItemStack itemstack1 = entityLiving.getCurrentItemOrArmor(4);
                int j = getLightLevel(itemstack1);
                return Math.max(i, j);
            } else if (entity instanceof EntityItem entityitem) {
                itemstack = getItemStack(entityitem);
                return getLightLevel(itemstack);
            } else {
                return 0;
            }
         }
      }
   }

   public static void clear() {
      synchronized(mapDynamicLights) {
         mapDynamicLights.clear();
      }
   }

   public static int getCount() {
      synchronized(mapDynamicLights) {
         return mapDynamicLights.size();
      }
   }

   public static ItemStack getItemStack(EntityItem entityItem) {
       return entityItem.getDataWatcher().getWatchableObjectItemStack(10);
   }
}
