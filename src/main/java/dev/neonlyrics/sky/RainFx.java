package dev.neonlyrics.sky;

import dev.neonlyrics.cosmetic.Accessories;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.Heightmap.Type;
import org.joml.Matrix4f;

final class RainFx {
   private static final int R = 16;
   private static final int SIDE = 33;
   private static final Random RND = new Random();
   private static final float[] TOP = new float[1089];
   private static final int[] LIGHT = new int[1089];
   private static int cx0 = Integer.MIN_VALUE;
   private static int cz0;
   private static long lastScan;
   private static World lastLevel;
   private static final List<float[]> LAMPS = new ArrayList<>();
   private static final List<float[]> SPLASH = new ArrayList<>();
   private static final List<float[]> DROPS = new ArrayList<>();
   private static float spawnAcc;
   private static double fx;
   private static double fy;
   private static double fz;
   private static double rx;
   private static double ry;
   private static double rz;
   private static double ux;
   private static double uy;
   private static double uz;
   private static Matrix4f base;

   private RainFx() {
   }

   static void render(
      MinecraftClient var0,
      VertexConsumerProvider var1,
      Matrix4f var2,
      Vec3d var3,
      float var4,
      float var5,
      double var6,
      double var8,
      double var10,
      double var12,
      double var14,
      double var16,
      double var18,
      double var20,
      double var22
   ) {
      if (!(var4 <= 0.02F) && var0.world != null && var0.player != null) {
         fx = var6;
         fy = var8;
         fz = var10;
         rx = var12;
         ry = var14;
         rz = var16;
         ux = var18;
         uy = var20;
         uz = var22;
         base = var2;
         BlockPos var24 = var0.player.getBlockPos();
         long var25 = System.currentTimeMillis();
         if (var0.world != lastLevel || Math.abs(var24.getX() - (cx0 + 16)) > 2 || Math.abs(var24.getZ() - (cz0 + 16)) > 2 || var25 - lastScan > 400L) {
            scan(var0.world, var24);
            lastScan = var25;
            lastLevel = var0.world;
         }

         VertexConsumer var27 = var1.getBuffer(RenderLayers.text(Accessories.WHITE));
         drawGround(var27, var3, var4);
         updateSplashes(var27, var3, var4, var5);
         VertexConsumer var28 = var1.getBuffer(RenderLayers.text(SkyTextures.SPRITES));
         drawLamps(var28, var3, var4);
         drawDrops(var28, var3, var4, var5);
      }
   }

   private static void scan(World var0, BlockPos var1) {
      cx0 = var1.getX() - 16;
      cz0 = var1.getZ() - 16;
      LAMPS.clear();
      Mutable var2 = new Mutable();

      for (int var3 = 0; var3 < 33; var3++) {
         for (int var4 = 0; var4 < 33; var4++) {
            int var5 = cx0 + var3;
            int var6 = cz0 + var4;
            int var7 = var3 * 33 + var4;
            TOP[var7] = Float.NaN;
            int var8 = var0.getTopY(Type.MOTION_BLOCKING_NO_LEAVES, var5, var6);
            if (var8 > var0.getBottomY()) {
               var2.set(var5, var8 - 1, var6);
               BlockState var9 = var0.getBlockState(var2);
               if (var9.getFluidState().isEmpty()) {
                  VoxelShape var10 = var9.getCollisionShape(var0, var2);
                  if (!var10.isEmpty()) {
                     TOP[var7] = (float)(var8 - 1 + var10.getMax(Axis.Y));
                     var2.set(var5, var8, var6);
                     LIGHT[var7] = LightmapTextureManager.pack(var0.getLightLevel(LightType.BLOCK, var2), var0.getLightLevel(LightType.SKY, var2));

                     for (int var11 = 0; var11 < 6; var11++) {
                        var2.set(var5, var8 + var11, var6);
                        BlockState var12 = var0.getBlockState(var2);
                        int var13 = var12.getLuminance();
                        if (var13 >= 8) {
                           LAMPS.add(
                              new float[]{var5 + 0.5F, var8 + var11 + 0.5F, var6 + 0.5F, TOP[var7], var13 / 15.0F, Float.intBitsToFloat(lampColor(var12))}
                           );
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private static int lampColor(BlockState var0) {
      String var1 = Registries.BLOCK.getId(var0.getBlock()).getPath();
      if (var1.contains("soul")) {
         return 7330047;
      } else if (var1.contains("redstone")) {
         return 16730682;
      } else if (var1.contains("sea_lantern") || var1.contains("end_rod") || var1.contains("beacon") || var1.contains("froglight")) {
         return 14543871;
      } else {
         return var1.contains("copper") ? 16770736 : 16757852;
      }
   }

   private static float top(int var0, int var1) {
      int var2 = var0 - cx0;
      int var3 = var1 - cz0;
      return var2 >= 0 && var3 >= 0 && var2 < 33 && var3 < 33 ? TOP[var2 * 33 + var3] : Float.NaN;
   }

   private static float puddle(int var0, int var1) {
      float var2 = var0 * 0.21F;
      float var3 = var1 * 0.21F;
      int var4 = (int)Math.floor(var2);
      int var5 = (int)Math.floor(var3);
      float var6 = var2 - var4;
      float var7 = var3 - var5;
      var6 = var6 * var6 * (3.0F - 2.0F * var6);
      var7 = var7 * var7 * (3.0F - 2.0F * var7);
      float var8 = lerp(lerp(hash(var4, var5), hash(var4 + 1, var5), var6), lerp(hash(var4, var5 + 1), hash(var4 + 1, var5 + 1), var6), var7);
      return Math.max(0.0F, Math.min(1.0F, (var8 - 0.45F) * 3.5F));
   }

   private static float hash(int var0, int var1) {
      int var2 = var0 * 374761393 + var1 * 668265263;
      var2 = (var2 ^ var2 >>> 13) * 1274126177;
      return ((var2 ^ var2 >>> 16) & 65535) / 65535.0F;
   }

   private static float lerp(float var0, float var1, float var2) {
      return var0 + (var1 - var0) * var2;
   }

   private static void drawGround(VertexConsumer var0, Vec3d var1, float var2) {
      for (int var3 = 0; var3 < 33; var3++) {
         for (int var4 = 0; var4 < 33; var4++) {
            float var5 = TOP[var3 * 33 + var4];
            if (!Float.isNaN(var5)) {
               int var6 = cx0 + var3;
               int var7 = cz0 + var4;
               double var8 = var6 + 0.5 - var1.x;
               double var10 = var5 - var1.y;
               double var12 = var7 + 0.5 - var1.z;
               if (!(var10 > -0.05)) {
                  double var14 = Math.sqrt(var8 * var8 + var12 * var12);
                  if (!(var14 > 15.5)) {
                     double var16 = Math.sqrt(var14 * var14 + var10 * var10);
                     if (!((var8 * fx + var10 * fy + var12 * fz) / var16 < -0.2)) {
                        float var18 = (float)Math.min(1.0, (15.5 - var14) / 4.0);
                        int var19 = LIGHT[var3 * 33 + var4];
                        float var20 = var5 + 0.008F;
                        int var21 = wet(var6, var7, var20, var1, var18 * var2);
                        int var22 = wet(var6 + 1, var7, var20, var1, var18 * var2);
                        int var23 = wet(var6 + 1, var7 + 1, var20, var1, var18 * var2);
                        int var24 = wet(var6, var7 + 1, var20, var1, var18 * var2);
                        float var25 = (float)(var6 - var1.x);
                        float var26 = (float)(var20 - var1.y);
                        float var27 = (float)(var7 - var1.z);
                        gv(var0, var25, var26, var27, var21, var19);
                        gv(var0, var25, var26, var27 + 1.0F, var24, var19);
                        gv(var0, var25 + 1.0F, var26, var27 + 1.0F, var23, var19);
                        gv(var0, var25 + 1.0F, var26, var27, var22, var19);
                        gv(var0, var25 + 1.0F, var26, var27, var22, var19);
                        gv(var0, var25 + 1.0F, var26, var27 + 1.0F, var23, var19);
                        gv(var0, var25, var26, var27 + 1.0F, var24, var19);
                        gv(var0, var25, var26, var27, var21, var19);
                     }
                  }
               }
            }
         }
      }
   }

   private static int wet(int var0, int var1, float var2, Vec3d var3, float var4) {
      double var5 = var0 - var3.x;
      double var7 = var2 - var3.y;
      double var9 = var1 - var3.z;
      double var11 = Math.sqrt(var5 * var5 + var7 * var7 + var9 * var9) + 1.0E-6;
      float var13 = (float)Math.min(1.0, -var7 / var11);
      float var14 = puddle(var0, var1);
      float var15 = 1.0F - 0.85F * var14;
      float var16 = 0.03F + 0.97F * (float)Math.pow(1.0F - var13, 5.0F - 2.5F * var15);
      float var17 = Math.min(0.92F, var16 * (0.65F + 0.35F * var14) + 0.12F * var14);
      float var18 = 0.28F + 0.25F * var14;
      float var19 = 1.0F - (1.0F - var18) * (1.0F - var17);
      float var20 = (float)Math.pow(1.0F - var13, 2.0);
      float var21 = lerp(0.08F, 0.42F, var20);
      float var22 = lerp(0.09F, 0.47F, var20);
      float var23 = lerp(0.12F, 0.58F, var20);
      float var24 = var17 / Math.max(0.001F, var19);
      int var25 = Math.round(Math.min(1.0F, var21 * var24) * 255.0F) << 16
         | Math.round(Math.min(1.0F, var22 * var24) * 255.0F) << 8
         | Math.round(Math.min(1.0F, var23 * var24) * 255.0F);
      return Math.round(Math.max(0.0F, Math.min(1.0F, var19 * var4)) * 255.0F) << 24 | var25;
   }

   private static void gv(VertexConsumer var0, float var1, float var2, float var3, int var4, int var5) {
      var0.vertex(base, var1, var2, var3).color(var4).texture(0.5F, 0.5F).light(var5);
   }

   private static void drawLamps(VertexConsumer var0, Vec3d var1, float var2) {
      for (float[] var4 : LAMPS) {
         double var5 = var4[3];
         double var7 = var5 - var1.y;
         if (!(var7 > -0.1)) {
            double var9 = var4[0] - var1.x;
            double var11 = 2.0 * var5 - var4[1] - var1.y;
            double var13 = var4[2] - var1.z;
            if (!(var11 >= var7)) {
               double var15 = var7 / var11;
               double var17 = var9 * var15;
               double var19 = var13 * var15;
               double var21 = Math.sqrt(var17 * var17 + var19 * var19);
               if (!(var21 < 0.3) && !(var21 > 20.0) && !(var17 * fx + var7 * fy + var19 * fz < 0.0)) {
                  double var23 = var17 / var21;
                  double var25 = var19 / var21;
                  float var27 = (float)Math.min(1.0, (20.0 - var21) / 6.0);
                  double var28 = 0.9 + var21 * 0.22;
                  double var30 = 0.42 + 0.03 * var21;
                  float var32 = var2 * var27 * var4[4] * 0.55F;
                  int var33 = Float.floatToRawIntBits(var4[5]);
                  flatSprite(var0, var17, var7 + 0.02, var19, var23, var25, var28, var30, 2, var33, var32);
                  flatSprite(var0, var17, var7 + 0.021, var19, var23, var25, var28 * 0.45, var30 * 0.4, 2, 16777215, var32 * 0.5F);
               }
            }
         }
      }
   }

   private static void flatSprite(
      VertexConsumer var0, double var1, double var3, double var5, double var7, double var9, double var11, double var13, int var15, int var16, float var17
   ) {
      if (!(var17 <= 0.02F)) {
         int var18 = Math.round(Math.min(1.0F, var17) * 255.0F) << 24 | var16 & 16777215;
         double var19 = var7 * var11;
         double var21 = var9 * var11;
         double var23 = -var9 * var13;
         double var25 = var7 * var13;
         float var27 = var15 * 0.25F;
         float var28 = var27 + 0.25F;
         double[][] var29 = new double[][]{
            {var1 - var19 - var23, var5 - var21 - var25},
            {var1 + var19 - var23, var5 + var21 - var25},
            {var1 + var19 + var23, var5 + var21 + var25},
            {var1 - var19 + var23, var5 - var21 + var25}
         };
         float[][] var30 = new float[][]{{var27, 1.0F}, {var28, 1.0F}, {var28, 0.0F}, {var27, 0.0F}};

         for (int var31 = 0; var31 < 8; var31++) {
            int var32 = var31 < 4 ? var31 : 7 - var31;
            var0.vertex(base, (float)var29[var32][0], (float)var3, (float)var29[var32][1])
               .color(var18)
               .texture(var30[var32][0], var30[var32][1])
               .light(15728880);
         }
      }
   }

   private static void updateSplashes(VertexConsumer var0, Vec3d var1, float var2, float var3) {
      spawnAcc += var3 * 140.0F * var2;

      while (spawnAcc >= 1.0F) {
         spawnAcc--;
         double var4 = RND.nextDouble() * Math.PI * 2.0;
         double var6 = Math.sqrt(RND.nextDouble()) * 11.0;
         double var8 = var1.x + Math.cos(var4) * var6;
         double var10 = var1.z + Math.sin(var4) * var6;
         int var12 = (int)Math.floor(var8);
         int var13 = (int)Math.floor(var10);
         float var14 = top(var12, var13);
         if (!Float.isNaN(var14) && !(var14 > var1.y + 6.0)) {
            float var15 = puddle(var12, var13);
            SPLASH.add(new float[]{(float)var8, var14, (float)var10, 0.0F, var15});
            int var16 = var15 > 0.3F ? 4 : 2 + RND.nextInt(2);

            for (int var17 = 0; var17 < var16; var17++) {
               double var18 = RND.nextDouble() * Math.PI * 2.0;
               double var20 = 0.5 + RND.nextDouble() * 0.9;
               DROPS.add(
                  new float[]{
                     (float)var8,
                     var14 + 0.02F,
                     (float)var10,
                     (float)(Math.cos(var18) * var20),
                     1.4F + RND.nextFloat() * 1.4F,
                     (float)(Math.sin(var18) * var20),
                     var14,
                     0.0F
                  }
               );
            }
         }
      }

      if (SPLASH.size() > 220) {
         SPLASH.subList(0, SPLASH.size() - 220).clear();
      }

      if (DROPS.size() > 600) {
         DROPS.subList(0, DROPS.size() - 600).clear();
      }

      for (int var22 = SPLASH.size() - 1; var22 >= 0; var22--) {
         float[] var5 = SPLASH.get(var22);
         var5[3] += var3;
         float var23 = var5[4] > 0.3F ? 0.7F : 0.38F;
         float var7 = var5[3] / var23;
         if (var7 >= 1.0F) {
            SPLASH.remove(var22);
         } else {
            float var24 = 0.04F + (var5[4] > 0.3F ? 0.34F : 0.2F) * (float)Math.sqrt(var7);
            float var9 = var2 * (1.0F - var7) * (var5[4] > 0.3F ? 0.45F : 0.3F);
            ring(var0, var5[0] - var1.x, var5[1] + 0.012 - var1.y, var5[2] - var1.z, var24, 0.016F + 0.01F * (1.0F - var7), 13162222, var9);
            if (var5[4] > 0.3F && var7 > 0.25F) {
               ring(var0, var5[0] - var1.x, var5[1] + 0.012 - var1.y, var5[2] - var1.z, var24 * 0.55F, 0.012F, 13162222, var9 * 0.6F);
            }
         }
      }
   }

   private static void ring(VertexConsumer var0, double var1, double var3, double var5, float var7, float var8, int var9, float var10) {
      if (!(var10 <= 0.02F)) {
         int var11 = Math.round(Math.min(1.0F, var10) * 255.0F) << 24 | var9;
         byte var12 = 14;
         float var13 = (float)var3;

         for (int var14 = 0; var14 < var12; var14++) {
            double var15 = var14 * Math.PI * 2.0 / var12;
            double var17 = (var14 + 1) * Math.PI * 2.0 / var12;
            float var19 = (float)Math.cos(var15);
            float var20 = (float)Math.sin(var15);
            float var21 = (float)Math.cos(var17);
            float var22 = (float)Math.sin(var17);
            float var23 = var7 - var8;
            float var24 = var7 + var8;
            float[][] var25 = new float[][]{
               {(float)var1 + var19 * var23, (float)var5 + var20 * var23},
               {(float)var1 + var21 * var23, (float)var5 + var22 * var23},
               {(float)var1 + var21 * var24, (float)var5 + var22 * var24},
               {(float)var1 + var19 * var24, (float)var5 + var20 * var24}
            };

            for (int var26 = 0; var26 < 8; var26++) {
               int var27 = var26 < 4 ? var26 : 7 - var26;
               var0.vertex(base, var25[var27][0], var13, var25[var27][1]).color(var11).texture(0.5F, 0.5F).light(15728880);
            }
         }
      }
   }

   private static void drawDrops(VertexConsumer var0, Vec3d var1, float var2, float var3) {
      for (int var4 = DROPS.size() - 1; var4 >= 0; var4--) {
         float[] var5 = DROPS.get(var4);
         var5[7] += var3;
         var5[4] -= 11.0F * var3;
         var5[0] += var5[3] * var3;
         var5[1] += var5[4] * var3;
         var5[2] += var5[5] * var3;
         if (!(var5[1] < var5[6]) && !(var5[7] > 0.6F)) {
            double var6 = var5[0] - var1.x;
            double var8 = var5[1] - var1.y;
            double var10 = var5[2] - var1.z;
            if (!(var6 * fx + var8 * fy + var10 * fz < 0.2)) {
               float var12 = var2 * 0.75F * (1.0F - var5[7] / 0.6F);
               streak(var0, var6 - var5[3] * 0.02, var8 - var5[4] * 0.02, var10 - var5[5] * 0.02, var6, var8, var10, 0.022, 14214392, var12);
            }
         } else {
            DROPS.remove(var4);
         }
      }
   }

   private static void streak(
      VertexConsumer var0, double var1, double var3, double var5, double var7, double var9, double var11, double var13, int var15, float var16
   ) {
      if (!(var16 <= 0.02F)) {
         double var17 = var7 - var1;
         double var19 = var9 - var3;
         double var21 = var11 - var5;
         double var23 = (var1 + var7) / 2.0;
         double var25 = (var3 + var9) / 2.0;
         double var27 = (var5 + var11) / 2.0;
         double var29 = var19 * var27 - var21 * var25;
         double var31 = var21 * var23 - var17 * var27;
         double var33 = var17 * var25 - var19 * var23;
         double var35 = Math.sqrt(var29 * var29 + var31 * var31 + var33 * var33);
         if (!(var35 < 1.0E-6)) {
            double var37 = var13 / 2.0 / var35;
            var29 *= var37;
            var31 *= var37;
            var33 *= var37;
            int var39 = Math.round(Math.min(1.0F, var16) * 255.0F) << 24 | var15;
            double[][] var40 = new double[][]{
               {var7 - var29, var9 - var31, var11 - var33},
               {var7 + var29, var9 + var31, var11 + var33},
               {var1 + var29, var3 + var31, var5 + var33},
               {var1 - var29, var3 - var31, var5 - var33}
            };
            float[][] var41 = new float[][]{{0.25F, 1.0F}, {0.5F, 1.0F}, {0.5F, 0.0F}, {0.25F, 0.0F}};

            for (int var42 = 0; var42 < 8; var42++) {
               int var43 = var42 < 4 ? var42 : 7 - var42;
               var0.vertex(base, (float)var40[var43][0], (float)var40[var43][1], (float)var40[var43][2])
                  .color(var39)
                  .texture(var41[var43][0], var41[var43][1])
                  .light(15728880);
            }
         }
      }
   }
}
