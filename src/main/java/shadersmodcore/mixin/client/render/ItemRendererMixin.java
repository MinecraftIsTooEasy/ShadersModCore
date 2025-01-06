package shadersmodcore.mixin.client.render;

import net.minecraft.*;
import shadersmodcore.client.dynamicLight.DynamicLights;
import shadersmodcore.config.ShaderConfig;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({ItemRenderer.class})
public abstract class ItemRendererMixin {

   @Shadow public abstract void renderItem(EntityLivingBase par1EntityLivingBase, ItemStack par2ItemStack, int par3);

   @Shadow @Final public MapItemRenderer mapItemRenderer;
   @Shadow @Final private static ResourceLocation RES_MAP_BACKGROUND;
   @Shadow private float equippedProgress;
   @Shadow private float prevEquippedProgress;
   @Shadow private Minecraft mc;
   @Shadow private ItemStack itemToRender;

   @Redirect(method = "renderItemInFirstPerson", at = @At(value = "INVOKE",
           target = "Lnet/minecraft/WorldClient;getLightBrightnessForSkyBlocks(IIII)I"))
   public int getLightBrightnessForSkyBlocks(WorldClient worldClient, int par1, int par2, int par3, int par4) {
      int var10 = worldClient.getLightBrightnessForSkyBlocks(par1, par2, par3, par4);
      if (ShaderConfig.isDynamicLights()) {
         var10 = DynamicLights.getCombinedLight(this.mc.renderViewEntity, var10);
      }
      return var10;
   }


   /**
    * @author
    * @reason
    */
//   @Overwrite
//   public void renderItemInFirstPerson(float par1) {
//      EntityClientPlayerMP player = this.mc.thePlayer;
//      if (this.itemToRender == null || !(this.itemToRender.getItem() instanceof ItemFishingRod) || !player.zoomed) {
//         if (player.ticksExisted < 1) {
//            player.prevRenderArmYaw = player.renderArmYaw = player.rotationYaw;
//            player.prevRenderArmPitch = player.renderArmPitch = player.rotationPitch;
//         }
//
//         if (this.mc.theWorld.doesChunkAndAllNeighborsExist(player.getChunkPosX(), player.getChunkPosZ(), 0, false)) {
//            float var2 = this.prevEquippedProgress + (this.equippedProgress - this.prevEquippedProgress) * par1;
//            EntityClientPlayerMP var3 = this.mc.thePlayer;
//            float var4 = var3.prevRotationPitch + (var3.rotationPitch - var3.prevRotationPitch) * par1;
//            GL11.glPushMatrix();
//            GL11.glRotatef(var4, 1.0F, 0.0F, 0.0F);
//            GL11.glRotatef(var3.prevRotationYaw + (var3.rotationYaw - var3.prevRotationYaw) * par1, 0.0F, 1.0F, 0.0F);
//            RenderHelper.enableStandardItemLighting();
//            GL11.glPopMatrix();
//            float var6 = var3.prevRenderArmPitch + (var3.renderArmPitch - var3.prevRenderArmPitch) * par1;
//            float var7 = var3.prevRenderArmYaw + (var3.renderArmYaw - var3.prevRenderArmYaw) * par1;
//            GL11.glRotatef((var3.rotationPitch - var6) * 0.1F, 1.0F, 0.0F, 0.0F);
//            GL11.glRotatef((var3.rotationYaw - var7) * 0.1F, 0.0F, 1.0F, 0.0F);
//            ItemStack var8 = this.itemToRender;
//            float var9 = this.mc.theWorld.getLightBrightness(MathHelper.floor_double(var3.posX), MathHelper.floor_double(var3.posY), MathHelper.floor_double(var3.posZ));
//            var9 = 1.0F;
//            int var10 = this.mc.theWorld.getLightBrightnessForSkyBlocks(MathHelper.floor_double(var3.posX), MathHelper.floor_double(var3.posY), MathHelper.floor_double(var3.posZ), 0);
//            if (ShaderConfig.isDynamicLights()) {
//               var10 = DynamicLights.getCombinedLight(this.mc.renderViewEntity, var10);
//            }
//
//            int var11 = var10 % 65536;
//            int var12 = var10 / 65536;
//            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)var11 / 1.0F, (float)var12 / 1.0F);
//            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
//            float var13;
//            float var20;
//            float var22;
//            if (var8 != null) {
//               var10 = Item.itemsList[var8.itemID].getColorFromItemStack(var8, 0);
//               var20 = (float)(var10 >> 16 & 255) / 255.0F;
//               var22 = (float)(var10 >> 8 & 255) / 255.0F;
//               var13 = (float)(var10 & 255) / 255.0F;
//               GL11.glColor4f(var9 * var20, var9 * var22, var9 * var13, 1.0F);
//            } else {
//               GL11.glColor4f(var9, var9, var9, 1.0F);
//            }
//
//            float var14;
//            float var15;
//            float var16;
//            float var21;
//            Render var27;
//            RenderPlayer var26;
//            if (var8 != null && var8.itemID == Item.map.itemID) {
//               GL11.glPushMatrix();
//               var21 = 0.8F;
//               var20 = var3.getSwingProgress(par1);
//               var22 = MathHelper.sin(var20 * 3.1415927F);
//               var13 = MathHelper.sin(MathHelper.sqrt_float(var20) * 3.1415927F);
//               GL11.glTranslatef(-var13 * 0.4F, MathHelper.sin(MathHelper.sqrt_float(var20) * 3.1415927F * 2.0F) * 0.2F, -var22 * 0.2F);
//               var20 = 1.0F - var4 / 45.0F + 0.1F;
//               if (var20 < 0.0F) {
//                  var20 = 0.0F;
//               }
//
//               if (var20 > 1.0F) {
//                  var20 = 1.0F;
//               }
//
//               var20 = -MathHelper.cos(var20 * 3.1415927F) * 0.5F + 0.5F;
//               GL11.glTranslatef(0.0F, 0.0F * var21 - (1.0F - var2) * 1.2F - var20 * 0.5F + 0.04F, -0.9F * var21);
//               GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
//               GL11.glRotatef(var20 * -85.0F, 0.0F, 0.0F, 1.0F);
//               GL11.glEnable(32826);
//               this.mc.getTextureManager().bindTexture(var3.getLocationSkin());
//
//               for(var12 = 0; var12 < 2; ++var12) {
//                  int var24 = var12 * 2 - 1;
//                  GL11.glPushMatrix();
//                  GL11.glTranslatef(-0.0F, -0.6F, 1.1F * (float)var24);
//                  GL11.glRotatef((float)(-45 * var24), 1.0F, 0.0F, 0.0F);
//                  GL11.glRotatef(-90.0F, 0.0F, 0.0F, 1.0F);
//                  GL11.glRotatef(59.0F, 0.0F, 0.0F, 1.0F);
//                  GL11.glRotatef((float)(-65 * var24), 0.0F, 1.0F, 0.0F);
//                  var27 = RenderManager.instance.getEntityRenderObject(this.mc.thePlayer);
//                  var26 = (RenderPlayer)var27;
//                  var16 = 1.0F;
//                  GL11.glScalef(var16, var16, var16);
//                  var26.renderFirstPersonArm(this.mc.thePlayer);
//                  GL11.glPopMatrix();
//               }
//
//               var22 = var3.getSwingProgress(par1);
//               var13 = MathHelper.sin(var22 * var22 * 3.1415927F);
//               var14 = MathHelper.sin(MathHelper.sqrt_float(var22) * 3.1415927F);
//               GL11.glRotatef(-var13 * 20.0F, 0.0F, 1.0F, 0.0F);
//               GL11.glRotatef(-var14 * 20.0F, 0.0F, 0.0F, 1.0F);
//               GL11.glRotatef(-var14 * 80.0F, 1.0F, 0.0F, 0.0F);
//               var15 = 0.38F;
//               GL11.glScalef(var15, var15, var15);
//               GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
//               GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
//               GL11.glTranslatef(-1.0F, -1.0F, 0.0F);
//               var16 = 0.015625F;
//               GL11.glScalef(var16, var16, var16);
//               this.mc.getTextureManager().bindTexture(RES_MAP_BACKGROUND);
//               Tessellator var30 = Tessellator.instance;
//               GL11.glNormal3f(0.0F, 0.0F, -1.0F);
//               var30.startDrawingQuads();
//               byte var29 = 7;
//               var30.addVertexWithUV(-var29, 128 + var29, 0.0, 0.0, 1.0);
//               var30.addVertexWithUV(128 + var29, 128 + var29, 0.0, 1.0, 1.0);
//               var30.addVertexWithUV(128 + var29, -var29, 0.0, 1.0, 0.0);
//               var30.addVertexWithUV(-var29, -var29, 0.0, 0.0, 0.0);
//               var30.draw();
//               MapData var19 = Item.map.getMapData(var8, this.mc.theWorld);
//               if (var19 != null) {
//                  this.mapItemRenderer.renderMap(this.mc.thePlayer, this.mc.getTextureManager(), var19);
//               }
//
//               GL11.glPopMatrix();
//            } else if (var8 == null) {
//               if (!var3.isInvisible()) {
//                  GL11.glPushMatrix();
//                  var21 = 0.8F;
//                  var20 = var3.getSwingProgress(par1);
//                  var22 = MathHelper.sin(var20 * 3.1415927F);
//                  var13 = MathHelper.sin(MathHelper.sqrt_float(var20) * 3.1415927F);
//                  GL11.glTranslatef(-var13 * 0.3F, MathHelper.sin(MathHelper.sqrt_float(var20) * 3.1415927F * 2.0F) * 0.4F, -var22 * 0.4F);
//                  GL11.glTranslatef(0.8F * var21, -0.75F * var21 - (1.0F - var2) * 0.6F, -0.9F * var21);
//                  GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
//                  GL11.glEnable(32826);
//                  var20 = var3.getSwingProgress(par1);
//                  var22 = MathHelper.sin(var20 * var20 * 3.1415927F);
//                  var13 = MathHelper.sin(MathHelper.sqrt_float(var20) * 3.1415927F);
//                  GL11.glRotatef(var13 * 70.0F, 0.0F, 1.0F, 0.0F);
//                  GL11.glRotatef(-var22 * 20.0F, 0.0F, 0.0F, 1.0F);
//                  this.mc.getTextureManager().bindTexture(var3.getLocationSkin());
//                  GL11.glTranslatef(-1.0F, 3.6F, 3.5F);
//                  GL11.glRotatef(120.0F, 0.0F, 0.0F, 1.0F);
//                  GL11.glRotatef(200.0F, 1.0F, 0.0F, 0.0F);
//                  GL11.glRotatef(-135.0F, 0.0F, 1.0F, 0.0F);
//                  GL11.glScalef(1.0F, 1.0F, 1.0F);
//                  GL11.glTranslatef(5.6F, 0.0F, 0.0F);
//                  var27 = RenderManager.instance.getEntityRenderObject(this.mc.thePlayer);
//                  var26 = (RenderPlayer)var27;
//                  var16 = 1.0F;
//                  GL11.glScalef(var16, var16, var16);
//                  var26.renderFirstPersonArm(this.mc.thePlayer);
//                  GL11.glPopMatrix();
//               }
//            } else {
//               GL11.glPushMatrix();
//               var21 = 0.8F;
//               if (var3.getItemInUseCount() > 0) {
//                  EnumItemInUseAction var23 = var8.getItemInUseAction(this.mc.thePlayer);
//                  if (var23 == EnumItemInUseAction.EAT || var23 == EnumItemInUseAction.DRINK) {
//                     var22 = (float)var3.getItemInUseCount() - par1 + 1.0F;
//                     var13 = 1.0F - var22 / (float)var8.getMaxItemUseDuration();
//                     var14 = 1.0F - var13;
//                     var14 = var14 * var14 * var14;
//                     var14 = var14 * var14 * var14;
//                     var14 = var14 * var14 * var14;
//                     var15 = 1.0F - var14;
//                     GL11.glTranslatef(0.0F, MathHelper.abs(MathHelper.cos(var22 / 4.0F * 3.1415927F) * 0.1F) * (float)((double)var13 > 0.2D ? 1 : 0), 0.0F);
//                     GL11.glTranslatef(var15 * 0.6F, -var15 * 0.5F, 0.0F);
//                     GL11.glRotatef(var15 * 90.0F, 0.0F, 1.0F, 0.0F);
//                     GL11.glRotatef(var15 * 10.0F, 1.0F, 0.0F, 0.0F);
//                     GL11.glRotatef(var15 * 30.0F, 0.0F, 0.0F, 1.0F);
//                  }
//               } else {
//                  var20 = var3.getSwingProgress(par1);
//                  var22 = MathHelper.sin(var20 * 3.1415927F);
//                  var13 = MathHelper.sin(MathHelper.sqrt_float(var20) * 3.1415927F);
//                  GL11.glTranslatef(-var13 * 0.4F, MathHelper.sin(MathHelper.sqrt_float(var20) * 3.1415927F * 2.0F) * 0.2F, -var22 * 0.2F);
//               }
//
//               GL11.glTranslatef(0.7F * var21, -0.65F * var21 - (1.0F - var2) * 0.6F, -0.9F * var21);
//               GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
//               GL11.glEnable(32826);
//               var20 = var3.getSwingProgress(par1);
//               var22 = MathHelper.sin(var20 * var20 * 3.1415927F);
//               var13 = MathHelper.sin(MathHelper.sqrt_float(var20) * 3.1415927F);
//               GL11.glRotatef(-var22 * 20.0F, 0.0F, 1.0F, 0.0F);
//               GL11.glRotatef(-var13 * 20.0F, 0.0F, 0.0F, 1.0F);
//               GL11.glRotatef(-var13 * 80.0F, 1.0F, 0.0F, 0.0F);
//               var14 = 0.4F;
//               GL11.glScalef(var14, var14, var14);
//               float var17;
//               float var18;
//               if (var3.getItemInUseCount() > 0) {
//                  EnumItemInUseAction var25 = var8.getItemInUseAction(this.mc.thePlayer);
//                  if (var25 == EnumItemInUseAction.BLOCK) {
//                     GL11.glTranslatef(-0.5F, 0.2F, 0.0F);
//                     GL11.glRotatef(30.0F, 0.0F, 1.0F, 0.0F);
//                     GL11.glRotatef(-80.0F, 1.0F, 0.0F, 0.0F);
//                     GL11.glRotatef(60.0F, 0.0F, 1.0F, 0.0F);
//                  } else if (var25 == EnumItemInUseAction.BOW) {
//                     GL11.glRotatef(-18.0F, 0.0F, 0.0F, 1.0F);
//                     GL11.glRotatef(-12.0F, 0.0F, 1.0F, 0.0F);
//                     GL11.glRotatef(-8.0F, 1.0F, 0.0F, 0.0F);
//                     GL11.glTranslatef(-0.9F, 0.2F, 0.0F);
//                     var16 = (float)var8.getMaxItemUseDuration() - ((float)var3.getItemInUseCount() - par1 + 1.0F);
//                     var17 = var16 / (float)ItemBow.getTicksForMaxPull(var8);
//                     var17 = (var17 * var17 + var17 * 2.0F) / 3.0F;
//                     if (var17 > 1.0F) {
//                        var17 = 1.0F;
//                     }
//
//                     if (var17 > 0.1F) {
//                        GL11.glTranslatef(0.0F, MathHelper.sin((var16 - 0.1F) * 1.3F) * 0.01F * (var17 - 0.1F), 0.0F);
//                     }
//
//                     GL11.glTranslatef(0.0F, 0.0F, var17 * 0.1F);
//                     GL11.glRotatef(-335.0F, 0.0F, 0.0F, 1.0F);
//                     GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
//                     GL11.glTranslatef(0.0F, 0.5F, 0.0F);
//                     var18 = 1.0F + var17 * 0.2F;
//                     GL11.glScalef(1.0F, 1.0F, var18);
//                     GL11.glTranslatef(0.0F, -0.5F, 0.0F);
//                     GL11.glRotatef(50.0F, 0.0F, 1.0F, 0.0F);
//                     GL11.glRotatef(335.0F, 0.0F, 0.0F, 1.0F);
//                  }
//               }
//
//               if (var8.getItem().shouldRotateAroundWhenRendering()) {
//                  GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
//               }
//
//               if (var8.getItem().requiresMultipleRenderPasses()) {
//                  this.renderItem(var3, var8, 0);
//                  int var28 = Item.itemsList[var8.itemID].getColorFromItemStack(var8, 1);
//                  var16 = (float)(var28 >> 16 & 255) / 255.0F;
//                  var17 = (float)(var28 >> 8 & 255) / 255.0F;
//                  var18 = (float)(var28 & 255) / 255.0F;
//                  GL11.glColor4f(var9 * var16, var9 * var17, var9 * var18, 1.0F);
//                  this.renderItem(var3, var8, 1);
//               } else {
//                  this.renderItem(var3, var8, 0);
//               }
//
//               GL11.glPopMatrix();
//            }
//
//            GL11.glDisable(32826);
//            RenderHelper.disableStandardItemLighting();
//         }
//      }
//
//   }
}
