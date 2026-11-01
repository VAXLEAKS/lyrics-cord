package dev.neonlyrics.cosmetic;

import dev.neonlyrics.NeonLyrics;
import dev.neonlyrics.config.Config;
import dev.neonlyrics.sky.SkyTextures;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LightType;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public final class DeathFx {
   public static final int COUNT = 8;
   static final float TE = 1.62F;
   static final float TI = 1.9F;
   static final float TF = 2.06F;
   static final float TP = 2.32F;
   static final float T_END = 4.75F;
   static final float[] TT = new float[9001];
   static final float HX = 1.7F;
   static final float HZ = 0.85F;
   static final float BX = 0.0F;
   static final float BY = 3.3F;
   static final float BZ = 0.0F;
   static final float STAND = 1.055F;
   static final float[] PX;
   static final float[] PY;
   static final float[] PZ;
   static final float[] PS;
   static final float SB;
   static final float SH;
   static final float SEND;
   static final float DX;
   static final float DY;
   static final float DZ;
   static final float TC;
   static final float[] TA;
   static final float[] TB;
   static final List<DeathFx.Fx> ACTIVE;
   static final Set<Integer> TRIGGERED;
   static final Set<Integer> SEEN;
   static final Map<Integer, Long> HITS;
   static final long KILL_WINDOW_MS = 6000L;
   static final float PXS = 0.05859375F;
   static final DeathFx.Pose POSE;
   static final float[] Q;
   static final float[] Q2;
   static final float[] AX;
   static final float[] GP;

   private DeathFx() {
   }

   static void pathPoint(float var0, float[] var1) {
      int var2 = PS.length;
      if (var0 <= 0.0F) {
         var1[0] = PX[0];
         var1[1] = PY[0] + var0;
         var1[2] = PZ[0];
      } else if (var0 >= PS[var2 - 1]) {
         float var6 = var0 - PS[var2 - 1];
         var1[0] = PX[var2 - 1] + DX * var6;
         var1[1] = PY[var2 - 1] + DY * var6;
         var1[2] = PZ[var2 - 1] + DZ * var6;
      } else {
         int var3 = 0;
         int var4 = var2 - 1;

         while (var4 - var3 > 1) {
            int var5 = var3 + var4 >>> 1;
            if (PS[var5] <= var0) {
               var3 = var5;
            } else {
               var4 = var5;
            }
         }

         float var7 = (var0 - PS[var3]) / Math.max(1.0E-6F, PS[var4] - PS[var3]);
         var1[0] = PX[var3] + (PX[var4] - PX[var3]) * var7;
         var1[1] = PY[var3] + (PY[var4] - PY[var3]) * var7;
         var1[2] = PZ[var3] + (PZ[var4] - PZ[var3]) * var7;
      }
   }

   static float sTip(float var0) {
      if (var0 < 1.62F) {
         return SH - 1.3F;
      } else if (var0 < 1.9F) {
         return lerp(SH - 1.3F, SB - 0.95F, (float)Math.pow((var0 - 1.62F) / 0.27999997F, 0.85));
      } else if (var0 < 2.06F) {
         return SB - 0.95F + 1.35F * eo((var0 - 1.9F) / 0.15999997F);
      } else {
         return var0 < 2.32F
            ? SEND + 0.025F * (float)Math.sin(var0 * 70.0F) * (1.0F - sm(2.1F, 2.32F, var0))
            : SEND - ei((var0 - 2.32F) / 0.75F) * (SEND - (SH - 7.0F));
      }
   }

   static float sBody(float var0) {
      return sTip(var0) - (SEND - SB);
   }

   static float wave(float var0, float var1) {
      float var2 = sTip(var1);
      float var3 = 0.42F * (1.0F - sm(1.62F, 2.02F, var1))
         + 0.04F * sm(1.9F, 2.0F, var1) * (1.0F - sm(2.25F, 2.32F, var1))
         + 0.13F * sm(2.32F, 2.5F, var1) * (1.0F - sm(2.95F, 3.05F, var1));
      var3 *= sm(SH - 0.2F, SH + 1.3F, var0) * sm(0.0F, 1.6F, var2 - var0 + 0.6F);
      if (var1 > 1.9F) {
         var3 *= sm(0.0F, 1.1F, Math.abs(var0 - sBody(var1)));
      }

      return var3;
   }

   static void chainPoint(float var0, float var1, float[] var2) {
      pathPoint(var0, var2);
      float var3 = wave(var0, var1);
      if (!(var3 < 1.0E-4F)) {
         pathPoint(var0 - 0.05F, TA);
         pathPoint(var0 + 0.05F, TB);
         float var4 = TB[0] - TA[0];
         float var5 = TB[1] - TA[1];
         float var6 = TB[2] - TA[2];
         float var7 = (float)Math.sqrt(var4 * var4 + var5 * var5 + var6 * var6);
         if (!(var7 < 1.0E-6F)) {
            var4 /= var7;
            var5 /= var7;
            var6 /= var7;
            float var8 = -var6;
            float var9 = 0.0F;
            float var10 = var4;
            float var11 = (float)Math.sqrt(var8 * var8 + var10 * var10);
            if (var11 < 1.0E-4F) {
               var8 = 1.0F;
               var10 = 0.0F;
            } else {
               var8 /= var11;
               var10 /= var11;
            }

            float var12 = var5 * var10 - var6 * var9;
            float var13 = var6 * var8 - var4 * var10;
            float var14 = var4 * var9 - var5 * var8;
            float var15 = var3 * (float)Math.sin(var0 * 2.3F - var1 * 34.0F);
            float var16 = var3 * 0.5F * (float)Math.cos(var0 * 1.7F - var1 * 27.0F);
            var2[0] += var8 * var15 + var12 * var16;
            var2[1] += var9 * var15 + var13 * var16;
            var2[2] += var10 * var15 + var14 * var16;
         }
      }
   }

   static void pose(float var0, DeathFx.Pose var1) {
      if (var0 < 1.9F) {
         float var2 = 1.0F - (float)Math.pow(1.0F - sm(0.22F, 1.62F, var0), 2.0);
         var1.p[0] = lerp(0.0F, 0.0F, var2);
         var1.p[1] = lerp(1.055F, 3.3F, var2) + (float)Math.sin(var0 * 2.6F) * 0.06F * sm(0.5F, 1.0F, var0) * (1.0F - sm(1.45F, 1.62F, var0));
         var1.p[2] = lerp(0.0F, 0.0F, var2) - 0.28F * (float)Math.sin(cl(var0 / 0.5F) * Math.PI) * (1.0F - var2);
         var1.rx = (float) (-Math.PI / 2) * sm(0.25F, 1.35F, var0);
         var1.ry = 0.55F * sm(0.2F, 1.6F, var0);
         var1.rz = 0.12F * (float)Math.sin(var0 * 1.8F) * sm(0.4F, 1.0F, var0) * (1.0F - sm(1.5F, 1.62F, var0));
         var1.arm = -0.9F * (float)Math.sin(cl(var0 / 0.4F) * Math.PI) + 1.35F * sm(0.35F, 1.2F, var0) + 0.12F * (float)Math.sin(var0 * 2.2F);
         var1.leg = 0.55F * sm(0.3F, 1.2F, var0);
         var1.head = -0.75F * sm(0.4F, 1.3F, var0);
      } else {
         chainPoint(sBody(var0), var0, var1.p);
         float var4 = (float)Math.sin(cl((var0 - 1.9F) / 0.3F) * Math.PI);
         float var3 = ei((var0 - 2.32F) / 0.6F);
         var1.rx = (float) (-Math.PI / 2) - 0.38F * var4 + 0.25F * var3;
         var1.ry = 0.55F;
         var1.rz = 0.05F * (float)Math.sin(var0 * 40.0F) * sm(2.06F, 2.1F, var0) * (1.0F - sm(2.25F, 2.32F, var0));
         var1.arm = lerp(1.35F - 1.0F * var4, -1.3F, var3);
         var1.leg = lerp(0.55F - 0.3F * var4, -0.9F, var3);
         var1.head = lerp(-0.75F + 0.5F * var4, -1.1F, var3);
      }

      var1.e = (float)(0.9 * Math.exp(-var0 * 12.0F) + (var0 >= 1.9F ? 0.8 * Math.exp(-(var0 - 1.9F) * 7.0F) : 0.0));
   }

   public static void tick(MinecraftClient var0) {
      Config var1 = NeonLyrics.cfg();
      if (var0.world != null && var0.player != null) {
         if (var1.deathFx <= 0) {
            ACTIVE.clear();
            TRIGGERED.clear();
         } else {
            SEEN.clear();

            for (AbstractClientPlayerEntity var3 : var0.world.getPlayers()) {
               int var4 = var3.getId();
               SEEN.add(var4);
               boolean var5 = var3.isDead();
               if (!var5) {
                  TRIGGERED.remove(var4);
               } else if (!TRIGGERED.contains(var4)) {
                  TRIGGERED.add(var4);
                  if (var3 != var0.player && !(var3.squaredDistanceTo(var0.player) > 6400.0) && (var1.deathOthers || killedByMe(var4))) {
                     spawn(var0, var3, false, false);
                  }
               }
            }

            TRIGGERED.retainAll(SEEN);
            long var6 = System.currentTimeMillis();
            HITS.values().removeIf(var2 -> var6 - var2 > 12000L);
         }
      } else {
         ACTIVE.clear();
         TRIGGERED.clear();
      }
   }

   public static void onAttack(Entity var0) {
      if (var0 instanceof AbstractClientPlayerEntity) {
         HITS.put(var0.getId(), System.currentTimeMillis());
      }
   }

   static boolean killedByMe(int var0) {
      Long var1 = HITS.get(var0);
      return var1 != null && System.currentTimeMillis() - var1 <= 6000L;
   }

   public static void test() {
      MinecraftClient var0 = MinecraftClient.getInstance();
      if (var0.player != null && var0.world != null) {
         spawn(var0, var0.player, true, true);
      }
   }

   public static boolean hides(Entity var0) {
      if (NeonLyrics.cfg().deathFx > 0 && var0 != null) {
         int var1 = var0.getId();
         if (TRIGGERED.contains(var1) && var0 instanceof AbstractClientPlayerEntity) {
            return true;
         }

         for (DeathFx.Fx var3 : ACTIVE) {
            if (var3.test && var3.id == var1) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   static void spawn(MinecraftClient var0, AbstractClientPlayerEntity var1, boolean var2, boolean var3) {
      if (ACTIVE.size() >= 6) {
         ACTIVE.remove(0);
      }

      DeathFx.Fx var4 = new DeathFx.Fx();
      var4.id = var1.getId();
      var4.self = var2;
      var4.test = var3;
      var4.kind = Math.max(1, NeonLyrics.cfg().deathFx);
      Vec3d var5 = var1.getEntityPos();
      var4.yaw = (float)Math.toRadians(var1.bodyYaw);
      var4.start = System.nanoTime();

      try {
         var4.skin = var1.getSkin().body().texturePath();
      } catch (Throwable var35) {
         var4.skin = null;
      }

      double var6 = var5.y;
      int var8 = 5999164;
      int var9 = 8017460;

      try {
         for (int var10 = 0; var10 <= 8; var10++) {
            BlockPos var11 = BlockPos.ofFloored(var5.x, var5.y - 0.01 - var10, var5.z);
            BlockState var12 = var0.world.getBlockState(var11);
            if (!var12.getCollisionShape(var0.world, var11).isEmpty()) {
               var6 = var11.getY() + 1.0;
               int var13 = var12.getMapColor(var0.world, var11).color;
               BlockPos var14 = var11.down();
               int var15 = var0.world.getBlockState(var14).getMapColor(var0.world, var14).color;
               if (var13 != 0) {
                  var8 = var13;
               }

               var9 = var15 != 0 ? var15 : var8;
               break;
            }
         }
      } catch (Throwable var36) {
      }

      var4.ox = var5.x;
      var4.oy = var6;
      var4.oz = var5.z;
      var4.groundCol = var8;
      var4.dirtCol = var9;
      Random var37 = new Random(var4.start ^ 31L * var4.id);
      ArrayList<float[]> var38 = new ArrayList();

      for (int var39 = 0; var39 < 13; var39++) {
         double var43 = var39 / 13.0 * Math.PI * 2.0 + var37.nextDouble() * 0.4;
         double var48 = 0.15;
         double var17 = 1.7F + Math.cos(var43) * var48;
         double var19 = 0.85F + Math.sin(var43) * var48;
         double var21 = 0.085;

         for (double var23 = 2.4 + var37.nextDouble() * 1.9; var48 < var23; var21 *= 0.94) {
            double var25 = 0.17 + var37.nextDouble() * 0.24;
            var43 += (var37.nextDouble() - 0.5) * 0.9;
            double var27 = var17 + Math.cos(var43) * var25;
            double var29 = var19 + Math.sin(var43) * var25;
            double var31 = (var17 + var27) / 2.0;
            double var33 = (var19 + var29) / 2.0;
            var38.add(
               new float[]{
                  (float)var17,
                  (float)var19,
                  (float)var27,
                  (float)var29,
                  (float)var21,
                  (float)var48,
                  (float)Math.hypot(var31 - 1.7F, var33 - 0.85F),
                  var37.nextFloat() * 9.0F
               }
            );
            var17 = var27;
            var19 = var29;
            var48 = Math.hypot(var17 - 1.7F, var19 - 0.85F);
         }
      }

      var4.cracks = var38.toArray(new float[0][]);
      var4.chunks = new float[42][];

      for (int var40 = 0; var40 < var4.chunks.length; var40++) {
         boolean var44 = var40 < 12;
         double var46 = var37.nextDouble() * Math.PI * 2.0;
         double var16 = var37.nextDouble() * (var44 ? 0.7 : 0.4);
         double var18 = var44 ? 1.2 + var37.nextDouble() * 1.8 : 1.0 + var37.nextDouble() * 3.0;
         var4.chunks[var40] = new float[]{
            (float)(1.7F + Math.cos(var46) * var16),
            -0.1F,
            (float)(0.85F + Math.sin(var46) * var16),
            (float)(Math.cos(var46) * var18),
            (float)(var44 ? 4.5 + var37.nextDouble() * 2.5 : 4.0 + var37.nextDouble() * 5.0),
            (float)(Math.sin(var46) * var18),
            (float)(var44 ? 0.24 + var37.nextDouble() * 0.2 : 0.07 + var37.nextDouble() * 0.12),
            var37.nextFloat() * 10.0F - 5.0F,
            var37.nextFloat() * 10.0F - 5.0F,
            var37.nextFloat() * 10.0F - 5.0F,
            shade(var37.nextBoolean() ? var8 : var9, 0.8F + var37.nextFloat() * 0.3F)
         };
      }

      var4.ring = new float[18][];

      for (int var41 = 0; var41 < var4.ring.length; var41++) {
         var4.ring[var41] = new float[]{
            (float)(var41 / 18.0 * Math.PI * 2.0 + var37.nextDouble() * 0.2),
            1.0F + var37.nextFloat() * 0.7F,
            0.28F + var37.nextFloat() * 0.16F,
            var37.nextFloat() * 0.12F,
            shade(var8, 0.75F + var37.nextFloat() * 0.3F)
         };
      }

      var4.burst = new float[70][];
      pathPoint(SB - 0.05F, TA);
      pathPoint(SB + 0.05F, TB);
      float var42 = (TB[0] - TA[0]) * 10.0F;
      float var45 = (TB[1] - TA[1]) * 10.0F;
      float var47 = (TB[2] - TA[2]) * 10.0F;

      for (int var49 = 0; var49 < var4.burst.length; var49++) {
         double var53 = var37.nextGaussian();
         double var55 = var37.nextGaussian();
         double var20 = var37.nextGaussian();
         double var22 = Math.max(0.01, Math.sqrt(var53 * var53 + var55 * var55 + var20 * var20));
         double var24 = 1.2 + var37.nextDouble() * 4.0;
         double var26 = 1.5 + var37.nextDouble() * 3.5;
         var4.burst[var49] = new float[]{
            (float)(var53 / var22 * var24 + var42 * var26),
            (float)(var55 / var22 * var24 + var45 * var26),
            (float)(var20 / var22 * var24 + var47 * var26),
            0.035F + var37.nextFloat() * 0.08F,
            var37.nextFloat() * 12.0F,
            var37.nextFloat() < 0.25F ? 1.0F : 0.0F,
            1.9F + var37.nextFloat() * 0.04F
         };
      }

      var4.embers = new float[56][];

      for (int var50 = 0; var50 < var4.embers.length; var50++) {
         var4.embers[var50] = new float[]{
            0.95F + var37.nextFloat() * 3.4F,
            1.2F + var37.nextFloat() * 0.9F,
            (float)Math.sqrt(var37.nextDouble()) * 1.5F,
            var37.nextFloat() * 6.283F,
            0.8F + var37.nextFloat() * 1.6F,
            0.06F + var37.nextFloat() * 0.08F,
            var37.nextFloat() < 0.7F ? 1.672096E7F : 1.6747088E7F
         };
      }

      var4.wisps = new float[24][];

      for (int var51 = 0; var51 < var4.wisps.length; var51++) {
         var4.wisps[var51] = new float[]{
            0.3F + var37.nextFloat() * 1.1F,
            (var37.nextFloat() - 0.5F) * 0.7F,
            (var37.nextFloat() - 0.5F) * 0.7F,
            (var37.nextFloat() - 0.5F) * 0.7F,
            var37.nextFloat() - 0.5F,
            var37.nextFloat() - 0.5F
         };
      }

      var4.dust = new float[56][];

      for (int var52 = 0; var52 < var4.dust.length; var52++) {
         boolean var54 = var52 < 22;
         var4.dust[var52] = new float[]{
            (var54 ? 1.62F : TC) + var37.nextFloat() * 0.08F,
            var37.nextFloat() * 6.283F,
            (var54 ? 0.5F : 1.3F) + var37.nextFloat() * 0.5F,
            (var54 ? 2.0F : 2.8F) * (0.6F + var37.nextFloat() * 0.6F),
            0.2F + var37.nextFloat() * 0.8F,
            1.5F + var37.nextFloat() * 1.3F,
            0.8F + var37.nextFloat() * 1.0F
         };
      }

      if (var4.kind == 2) {
         var4.extra = DeathCards.create(var4, var37);
      } else if (var4.kind >= 4) {
         var4.extra = DeathFresh.create(var4, var37);
      } else if (var4.kind >= 3) {
         var4.extra = DeathHard.create(var4, var37);
      }

      ACTIVE.add(var4);
   }

   public static void render(MinecraftClient var0, VertexConsumerProvider var1, Matrix4f var2, Vec3d var3, float var4) {
      if (!ACTIVE.isEmpty() && var0.world != null) {
         long var5 = System.nanoTime();
         cameraAxes(var0, var2, var4);
         Iterator var7 = ACTIVE.iterator();

         while (var7.hasNext()) {
            DeathFx.Fx var8 = (DeathFx.Fx)var7.next();
            int var9 = (int)((var5 - var8.start) / 1000000L);
            if (var8.kind >= 4) {
               float var17 = var9 / 1000.0F;
               if (var17 > DeathFresh.END[Math.min(var8.kind - 4, DeathFresh.END.length - 1)]) {
                  var7.remove();
               } else {
                  try {
                     DeathFresh.draw(var0, var1, var2, var3, var8, var17);
                  } catch (Throwable var16) {
                     NeonLyrics.LOG.warn("Todes-Animation abgebrochen", var16);
                     var7.remove();
                  }
               }
            } else {
               boolean var10 = var8.kind >= 3;
               float[] var11 = var8.kind == 2 ? DeathCards.TT : (var10 ? DeathHard.TT[Math.min(var8.kind - 3, DeathHard.TT.length - 1)] : TT);
               float var12 = var11[Math.min(var11.length - 1, Math.max(0, var9))];
               float var13 = var8.kind == 2 ? 4.2F : (var10 ? DeathHard.END[Math.min(var8.kind - 3, DeathHard.END.length - 1)] : 4.75F);
               if (!(var12 > var13) && var9 < var11.length - 1) {
                  try {
                     if (var8.kind == 2) {
                        DeathCards.draw(var0, var1, var2, var3, var8, var12);
                     } else if (var10) {
                        DeathHard.draw(var0, var1, var2, var3, var8, var12);
                     } else {
                        draw(var0, var1, var2, var3, var8, var12);
                     }
                  } catch (Throwable var15) {
                     NeonLyrics.LOG.warn("Todes-Animation abgebrochen", var15);
                     var7.remove();
                  }
               } else {
                  var7.remove();
               }
            }
         }
      }
   }

   static void draw(MinecraftClient var0, VertexConsumerProvider var1, Matrix4f var2, Vec3d var3, DeathFx.Fx var4, float var5) {
      if (Accessories.whiteTex()) {
         Matrix4f var6 = new Matrix4f(var2).translate((float)(var4.ox - var3.x), (float)(var4.oy - var3.y), (float)(var4.oz - var3.z)).rotateY(-var4.yaw);
         int var7 = light(var0, var4.ox, var4.oy + 0.5, var4.oz);
         float var8 = 1.0F - sm(3.7F, 4.7F, var5);
         boolean var9 = var4.self && var0.options.getPerspective().isFirstPerson();
         VertexConsumer var10 = var1.getBuffer(RenderLayers.debugQuads());
         float var11 = sm(0.95F, 1.6F, var5) * (1.0F + 0.2F * (float)Math.sin(var5 * 19.0F));
         float var12 = 0.02F;
         float var13 = (var5 < 1.62F ? 0.35F * var11 : 0.42F * (float)Math.exp(-(var5 - 1.62F) * 1.2) + 0.22F) * var8 * sm(0.95F, 1.3F, var5);
         disc(var10, var6, 1.7F, var12 - 0.005F, 0.85F, 0.0F, 2.6F, 16718390, var13, 16718390, 0.0F, 28);
         float var14 = 4.2F * eo((var5 - 0.92F) / 0.75F);
         float var15 = var5 < 1.62F ? 0.0F : (var5 < TC ? 0.95F : 1.75F);

         for (float[] var19 : var4.cracks) {
            float var20 = sm(var19[5], var19[5] + 0.35F, var14)
               * (0.75F + 0.25F * (float)Math.sin(var5 * 23.0F + var19[7]))
               * (1.0F - 0.7F * sm(3.4F, 4.6F, var5));
            if (!(var20 < 0.01F) && !(var19[6] < var15)) {
               strip(var10, var6, var19[0], var19[1], var19[2], var19[3], var12 + 0.004F, var19[4] * 3.2F, 16718904, var20 * 0.22F);
               strip(var10, var6, var19[0], var19[1], var19[2], var19[3], var12 + 0.008F, var19[4], 16725328, var20);
            }
         }

         if (var5 >= 1.62F) {
            float var37 = (var5 < TC ? 0.95F * eo((var5 - 1.62F) / 0.12F) : 0.95F + 0.8F * eo((var5 - TC) / 0.25F)) * (1.0F - 0.85F * sm(3.9F, 4.7F, var5));
            float var39 = 1.0F - sm(4.2F, 4.75F, var5);
            float var41 = 0.85F + 0.15F * (float)Math.sin(var5 * 9.0F);
            disc(var10, var6, 1.7F, var12 + 0.012F, 0.85F, 0.0F, var37 * 0.45F, 16726613, var39 * var41, 6947864, var39, 24);
            disc(var10, var6, 1.7F, var12 + 0.012F, 0.85F, var37 * 0.45F, var37, 6947864, var39, 393476, var39, 24);
            disc(var10, var6, 1.7F, var12 + 0.01F, 0.85F, var37, var37 * 1.18F, 16722504, 0.9F * var39, 16722504, 0.0F, 24);
         }

         float var38 = (var5 - 1.9F) / 0.32F;
         if (var38 > 0.0F && var38 < 1.0F) {
            pathPoint(SB - 0.05F, TA);
            pathPoint(SB + 0.05F, TB);
            ringAround(
               var10,
               var6,
               0.0F,
               3.3F,
               0.0F,
               TB[0] - TA[0],
               TB[1] - TA[1],
               TB[2] - TA[2],
               0.25F + eo(var38) * 1.7F,
               0.06F + 0.04F * (1.0F - var38),
               16730726,
               0.8F * (1.0F - var38)
            );
         }

         float var40 = (var5 - TC) / 0.9F;
         if (var40 > 0.0F && var40 < 1.0F) {
            float var42 = 0.5F + eo(var40) * 6.0F;
            disc(var10, var6, 1.7F, var12 + 0.02F, 0.85F, var42 * 0.9F, var42, 16722500, 0.0F, 16722500, 0.9F * (1.0F - var40), 40);
            disc(var10, var6, 1.7F, var12 + 0.02F, 0.85F, var42, var42 * 1.04F, 16751272, 0.6F * (1.0F - var40), 16751272, 0.0F, 40);
         }

         VertexConsumer var43 = var1.getBuffer(RenderLayers.entityCutoutNoCull(Accessories.WHITE));
         boolean var44 = var5 >= 1.62F;
         float var45 = sTip(var5);
         if (var44) {
            for (int var21 = 0; var21 < 44; var21++) {
               float var22 = var45 - 0.26F - var21 * 0.33F;
               if (var22 <= SH - 2.5F) {
                  break;
               }

               chainPoint(var22, var5, Q);
               chainPoint(var22 + 0.06F, var5, Q2);
               float var23 = 0.0F;
               if (var5 < 2.1599998F) {
                  var23 += 0.9F * (float)Math.exp(-(var45 - var22) / 0.45F) * sm(1.62F, 1.67F, var5) * (1.0F - sm(2.0F, 2.15F, var5));
               }

               if (var5 > 2.06F && var5 < 2.52F) {
                  float var24 = SB - (var5 - 2.06F) * 30.0F;
                  var23 += 1.6F * (float)Math.exp(-(var22 - var24) * (var22 - var24) / 0.25F);
               }

               if (var5 > 2.32F) {
                  var23 += 0.05F + 0.3F * Math.max(0.0F, (float)Math.sin(var22 * 2.5F + var5 * 45.0F));
               }

               int var62 = mix(2763314, 16722504, Math.min(1.0F, var23));
               int var25 = var23 > 0.35F ? 15728880 : var7;
               Matrix4f var26 = new Matrix4f(var6).translate(Q[0], Q[1], Q[2]).rotate(toward(Q2[0] - Q[0], Q2[1] - Q[1], Q2[2] - Q[2]));
               if ((var21 & 1) == 1) {
                  var26.rotateY((float) (Math.PI / 2));
               }

               link(var43, var26, var62, var25);
            }

            chainPoint(var45, var5, Q);
            chainPoint(var45 + 0.06F, var5, Q2);
            Matrix4f var46 = new Matrix4f(var6).translate(Q[0], Q[1], Q[2]).rotate(toward(Q2[0] - Q[0], Q2[1] - Q[1], Q2[2] - Q[2]));
            float var51 = var5 > 1.9F ? (float)Math.exp(-(var5 - 1.9F) * 4.0F) : 0.0F;
            spear(var43, var46, var7, mix(11540526, 16734832, var51));
         }

         for (float[] var63 : var4.chunks) {
            float var68 = var5 - 1.62F;
            if (!(var68 <= 0.0F) && !(var68 > 2.5F)) {
               float var73 = var63[0] + var63[3] * var68;
               float var27 = var63[1] + var63[4] * var68 - 8.0F * var68 * var68;
               float var28 = var63[2] + var63[5] * var68;
               if (!(var27 < -1.5F)) {
                  float var29 = var63[6];
                  Matrix4f var30 = new Matrix4f(var6).translate(var73, var27, var28).rotateXYZ(var63[7] * var68, var63[8] * var68, var63[9] * var68);
                  cube(
                     var43,
                     var30,
                     -var29 / 2.0F,
                     -var29 / 2.0F,
                     -var29 / 2.0F,
                     var29 / 2.0F,
                     var29 / 2.0F,
                     var29 / 2.0F,
                     0xFF000000 | Float.floatToRawIntBits(var63[10]),
                     var7
                  );
               }
            }
         }

         for (float[] var64 : var4.ring) {
            float var69 = var5 - TC - var64[3];
            if (!(var5 < TC) && !(var69 > 1.4F)) {
               float var74 = Math.max(0.0F, var69);
               float var77 = var64[1] - var74 * 0.8F;
               float var80 = 1.7F + (float)Math.cos(var64[0]) * var77;
               float var83 = 0.85F + (float)Math.sin(var64[0]) * var77;
               float var88 = -var64[2] * 0.4F - 9.0F * var74 * var74 - var74 * 0.5F;
               if (!(var88 < -2.5F)) {
                  float var31 = var64[2];
                  Matrix4f var32 = new Matrix4f(var6).translate(var80, var88, var83).rotateY(-var64[0]).rotateZ(var74 * 3.0F);
                  cube(
                     var43,
                     var32,
                     -var31 / 2.0F,
                     -var31 / 2.0F,
                     -var31 / 2.0F,
                     var31 / 2.0F,
                     var31 / 2.0F,
                     var31 / 2.0F,
                     0xFF000000 | Float.floatToRawIntBits(var64[4]),
                     var7
                  );
               }
            }
         }

         for (float[] var65 : var4.burst) {
            float var70 = var5 - var65[6];
            if (!(var70 <= 0.0F) && !(var70 > 1.6F)) {
               float var75 = 0.0F + var65[0] * var70;
               float var78 = 3.3F + var65[1] * var70 - 6.0F * var70 * var70;
               float var81 = 0.0F + var65[2] * var70;
               float var84 = var65[3] * (1.0F - var70 / 1.6F * 0.6F);
               Matrix4f var89 = new Matrix4f(var6).translate(var75, var78, var81).rotateXYZ(var65[4] * var70, var65[4] * var70 * 0.7F, 0.0F);
               boolean var93 = var65[5] > 0.5F;
               cube(
                  var43,
                  var89,
                  -var84 / 2.0F,
                  -var84 / 2.0F,
                  -var84 / 2.0F,
                  var84 / 2.0F,
                  var84 / 2.0F,
                  var84 / 2.0F,
                  var93 ? -54715 : -12974576,
                  var93 ? 15728880 : var7
               );
            }
         }

         pose(var5, POSE);
         boolean var50 = POSE.p[1] > -1.4F && !var9;
         if (var50 && var4.skin != null) {
            VertexConsumer var55 = var1.getBuffer(RenderLayers.entityCutoutNoCull(var4.skin));
            int var60 = light(var0, var4.ox + POSE.p[0], var4.oy + POSE.p[1], var4.oz + POSE.p[2]);
            float var66 = Math.min(1.0F, POSE.e);
            int var71 = -65536 | Math.round(255.0F * (1.0F - 0.6F * var66)) << 8 | Math.round(255.0F * (1.0F - 0.6F * var66));
            body(var55, new Matrix4f(var6).translate(POSE.p[0], POSE.p[1], POSE.p[2]).rotateY(POSE.ry).rotateX(POSE.rx).rotateZ(POSE.rz), POSE, var71, var60);
         }

         if (SkyTextures.spritesReady()) {
            VertexConsumer var56 = var1.getBuffer(RenderLayers.text(SkyTextures.SPRITES));
            float var61 = AX[0];
            float var67 = AX[1];
            float var72 = AX[2];
            float var76 = AX[3];
            float var79 = AX[4];
            float var82 = AX[5];
            if (!var9) {
               for (float[] var97 : var4.wisps) {
                  float var33 = (var5 - var97[0]) / 1.0F;
                  if (!(var33 <= 0.0F) && !(var33 >= 1.0F)) {
                     pose(Math.min(var97[0], 1.8989999F), POSE);
                     float var34 = POSE.p[0] + var97[1] + var97[4] * var33;
                     float var35 = POSE.p[1] + var97[2] + var33 * 1.2F;
                     float var36 = POSE.p[2] + var97[3] + var97[5] * var33;
                     sprite(
                        var56,
                        var2,
                        var4,
                        var3,
                        var34,
                        var35,
                        var36,
                        0.5F + var33 * 1.3F,
                        1,
                        1707298,
                        0.55F * (float)Math.sin(var33 * Math.PI),
                        var61,
                        var67,
                        var72,
                        var76,
                        var79,
                        var82
                     );
                  }
               }
            }

            for (float[] var98 : var4.embers) {
               float var100 = (var5 - var98[0]) / var98[1];
               if (!(var100 <= 0.0F) && !(var100 >= 1.0F)) {
                  float var102 = var98[3] + var100 * 1.5F;
                  float var104 = var98[2] * (1.0F + var100 * 0.3F);
                  sprite(
                     var56,
                     var2,
                     var4,
                     var3,
                     1.7F + (float)Math.cos(var102) * var104,
                     0.05F + var100 * var98[4] * 1.6F,
                     0.85F + (float)Math.sin(var102) * var104,
                     var98[5] * 2.2F * (1.2F - var100 * 0.6F),
                     2,
                     (int)var98[6],
                     (float)Math.sin(var100 * Math.PI) * var8,
                     var61,
                     var67,
                     var72,
                     var76,
                     var79,
                     var82
                  );
               }
            }

            for (float[] var99 : var4.dust) {
               float var101 = (var5 - var99[0]) / var99[5];
               if (!(var101 <= 0.0F) && !(var101 >= 1.0F)) {
                  float var103 = eo(var101 * 1.6F);
                  float var105 = 1.7F + (float)Math.cos(var99[1]) * (var99[2] + var99[3] * var103);
                  float var106 = 0.85F + (float)Math.sin(var99[1]) * (var99[2] + var99[3] * var103);
                  sprite(
                     var56,
                     var2,
                     var4,
                     var3,
                     var105,
                     0.25F + var99[4] * var101 * 1.5F,
                     var106,
                     var99[6] * (1.0F + var101 * 2.2F),
                     1,
                     mix(var4.dirtCol, 2761254, 0.55F),
                     0.5F * (float)Math.pow(1.0F - var101, 1.4) * cl(var101 * 8.0F),
                     var61,
                     var67,
                     var72,
                     var76,
                     var79,
                     var82
                  );
               }
            }
         }
      }
   }

   static void cameraAxes(MinecraftClient var0, Matrix4f var1, float var2) {
      boolean var3 = Math.abs(var1.m00() - 1.0F) + Math.abs(var1.m11() - 1.0F) + Math.abs(var1.m22() - 1.0F) < 1.0E-4F;
      if (!var3) {
         AX[0] = var1.m00();
         AX[1] = var1.m10();
         AX[2] = var1.m20();
         AX[3] = var1.m01();
         AX[4] = var1.m11();
         AX[5] = var1.m21();
      } else {
         double var4 = Math.toRadians(var0.player.getYaw(var2));
         double var6 = Math.toRadians(var0.player.getPitch(var2));
         if (var0.options.getPerspective().isFrontView()) {
            var4 += Math.PI;
            var6 = -var6;
         }

         double var8 = -Math.sin(var4) * Math.cos(var6);
         double var10 = -Math.sin(var6);
         double var12 = Math.cos(var4) * Math.cos(var6);
         double var14 = -Math.cos(var4);
         double var16 = 0.0;
         double var18 = -Math.sin(var4);
         AX[0] = (float)var14;
         AX[1] = (float)var16;
         AX[2] = (float)var18;
         AX[3] = (float)(var16 * var12 - var18 * var10);
         AX[4] = (float)(var18 * var8 - var14 * var12);
         AX[5] = (float)(var14 * var10 - var16 * var8);
      }
   }

   static Quaternionf toward(float var0, float var1, float var2) {
      float var3 = (float)Math.sqrt(var0 * var0 + var1 * var1 + var2 * var2);
      return var3 < 1.0E-6F ? new Quaternionf() : new Quaternionf().rotationTo(0.0F, 1.0F, 0.0F, var0 / var3, var1 / var3, var2 / var3);
   }

   static void link(VertexConsumer var0, Matrix4f var1, int var2, int var3) {
      float var4 = 0.25F;
      float var5 = 0.13F;
      float var6 = 0.034F;
      cube(var0, var1, -var5, -var4, -var6, -var5 + 2.0F * var6, var4, var6, var2, var3);
      cube(var0, var1, var5 - 2.0F * var6, -var4, -var6, var5, var4, var6, var2, var3);
      cube(var0, var1, -var5 + 2.0F * var6, var4 - 2.0F * var6, -var6, var5 - 2.0F * var6, var4, var6, var2, var3);
      cube(var0, var1, -var5 + 2.0F * var6, -var4, -var6, var5 - 2.0F * var6, -var4 + 2.0F * var6, var6, var2, var3);
   }

   static void spear(VertexConsumer var0, Matrix4f var1, int var2, int var3) {
      int var4 = -14408660;
      cube(var0, var1, -0.09F, -0.12F, -0.09F, 0.09F, 0.1F, 0.09F, var4, var2);
      float var5 = 0.12F;
      float var6 = 1.0F;
      float var7 = 0.17F;
      float var8 = 0.07F;
      float[][] var9 = new float[][]{{var7, var5, 0.0F}, {0.0F, var5, var8}, {-var7, var5, 0.0F}, {0.0F, var5, -var8}};

      for (int var10 = 0; var10 < 4; var10++) {
         float[] var11 = var9[var10];
         float[] var12 = var9[var10 + 1 & 3];
         tri(var0, var1, var11[0], var11[1], var11[2], var12[0], var12[1], var12[2], 0.0F, var6, 0.0F, var4, var2);
         tri(var0, var1, var11[0], var11[1], var11[2], var12[0], var12[1], var12[2], 0.0F, 0.06F, 0.0F, var4, var2);
      }

      for (byte var13 = -1; var13 <= 1; var13 += 2) {
         float var14 = var13 * (var7 + 0.012F);
         quadW(var0, var1, var14, var5, -0.012F, var14, var5, 0.012F, 0.0F, var6 + 0.02F, 0.006F, 0.0F, var6 + 0.02F, -0.006F, var3, 15728880);
         tri(var0, var1, var13 * 0.1F, 0.3F, -0.035F, var13 * 0.1F, 0.3F, 0.035F, var13 * 0.33F, -0.1F, 0.0F, var4, var2);
         tri(var0, var1, var13 * 0.1F, 0.3F, 0.035F, var13 * 0.13F, 0.18F, 0.0F, var13 * 0.33F, -0.1F, 0.0F, var4, var2);
         tri(var0, var1, var13 * 0.1F, 0.3F, -0.035F, var13 * 0.13F, 0.18F, 0.0F, var13 * 0.33F, -0.1F, 0.0F, var4, var2);
      }
   }

   static void body(VertexConsumer var0, Matrix4f var1, DeathFx.Pose var2, int var3, int var4) {
      skinBox(var0, var1, 8.0F, 12.0F, 4.0F, 0.0F, 16, 16, var3, var4);
      skinBox(var0, var1, 8.0F, 12.0F, 4.0F, 0.25F, 16, 32, var3, var4);
      Matrix4f var5 = new Matrix4f(var1).translate(0.0F, 0.3515625F, 0.0F).rotateX(var2.head).translate(0.0F, 0.234375F, 0.0F);
      skinBox(var0, var5, 8.0F, 8.0F, 8.0F, 0.0F, 0, 0, var3, var4);
      skinBox(var0, var5, 8.0F, 8.0F, 8.0F, 0.5F, 32, 0, var3, var4);
      Matrix4f var6 = new Matrix4f(var1)
         .translate(-0.3515625F, 0.234375F, 0.0F)
         .rotateX(var2.arm * 0.9F + 0.1F)
         .rotateZ(0.12F)
         .translate(0.0F, -0.234375F, 0.0F);
      skinBox(var0, var6, 4.0F, 12.0F, 4.0F, 0.0F, 40, 16, var3, var4);
      skinBox(var0, var6, 4.0F, 12.0F, 4.0F, 0.25F, 40, 32, var3, var4);
      Matrix4f var7 = new Matrix4f(var1).translate(0.3515625F, 0.234375F, 0.0F).rotateX(var2.arm).rotateZ(-0.12F).translate(0.0F, -0.234375F, 0.0F);
      skinBox(var0, var7, 4.0F, 12.0F, 4.0F, 0.0F, 32, 48, var3, var4);
      skinBox(var0, var7, 4.0F, 12.0F, 4.0F, 0.25F, 48, 48, var3, var4);
      Matrix4f var8 = new Matrix4f(var1).translate(-0.1171875F, -0.3515625F, 0.0F).rotateX(var2.leg * 0.75F).translate(0.0F, -0.3515625F, 0.0F);
      skinBox(var0, var8, 4.0F, 12.0F, 4.0F, 0.0F, 0, 16, var3, var4);
      skinBox(var0, var8, 4.0F, 12.0F, 4.0F, 0.25F, 0, 32, var3, var4);
      Matrix4f var9 = new Matrix4f(var1).translate(0.1171875F, -0.3515625F, 0.0F).rotateX(var2.leg).translate(0.0F, -0.3515625F, 0.0F);
      skinBox(var0, var9, 4.0F, 12.0F, 4.0F, 0.0F, 16, 48, var3, var4);
      skinBox(var0, var9, 4.0F, 12.0F, 4.0F, 0.25F, 0, 48, var3, var4);
   }

   static void skinBox(VertexConsumer var0, Matrix4f var1, float var2, float var3, float var4, float var5, int var6, int var7, int var8, int var9) {
      float var10 = (var2 / 2.0F + var5) * 0.05859375F;
      float var11 = (var3 / 2.0F + var5) * 0.05859375F;
      float var12 = (var4 / 2.0F + var5) * 0.05859375F;
      float var13 = 0.015625F;
      face(
         var0,
         var1,
         -var10,
         var11,
         var12,
         var10,
         var11,
         var12,
         var10,
         -var11,
         var12,
         -var10,
         -var11,
         var12,
         (var6 + var4) * var13,
         (var7 + var4) * var13,
         (var6 + var4 + var2) * var13,
         (var7 + var4 + var3) * var13,
         0.0F,
         0.0F,
         1.0F,
         var8,
         var9
      );
      face(
         var0,
         var1,
         var10,
         var11,
         -var12,
         -var10,
         var11,
         -var12,
         -var10,
         -var11,
         -var12,
         var10,
         -var11,
         -var12,
         (var6 + 2.0F * var4 + var2) * var13,
         (var7 + var4) * var13,
         (var6 + 2.0F * var4 + 2.0F * var2) * var13,
         (var7 + var4 + var3) * var13,
         0.0F,
         0.0F,
         -1.0F,
         var8,
         var9
      );
      face(
         var0,
         var1,
         -var10,
         var11,
         -var12,
         -var10,
         var11,
         var12,
         -var10,
         -var11,
         var12,
         -var10,
         -var11,
         -var12,
         var6 * var13,
         (var7 + var4) * var13,
         (var6 + var4) * var13,
         (var7 + var4 + var3) * var13,
         -1.0F,
         0.0F,
         0.0F,
         var8,
         var9
      );
      face(
         var0,
         var1,
         var10,
         var11,
         var12,
         var10,
         var11,
         -var12,
         var10,
         -var11,
         -var12,
         var10,
         -var11,
         var12,
         (var6 + var4 + var2) * var13,
         (var7 + var4) * var13,
         (var6 + 2.0F * var4 + var2) * var13,
         (var7 + var4 + var3) * var13,
         1.0F,
         0.0F,
         0.0F,
         var8,
         var9
      );
      face(
         var0,
         var1,
         -var10,
         var11,
         -var12,
         var10,
         var11,
         -var12,
         var10,
         var11,
         var12,
         -var10,
         var11,
         var12,
         (var6 + var4) * var13,
         var7 * var13,
         (var6 + var4 + var2) * var13,
         (var7 + var4) * var13,
         0.0F,
         1.0F,
         0.0F,
         var8,
         var9
      );
      face(
         var0,
         var1,
         -var10,
         -var11,
         var12,
         var10,
         -var11,
         var12,
         var10,
         -var11,
         -var12,
         -var10,
         -var11,
         -var12,
         (var6 + var4 + var2) * var13,
         var7 * var13,
         (var6 + var4 + 2.0F * var2) * var13,
         (var7 + var4) * var13,
         0.0F,
         -1.0F,
         0.0F,
         var8,
         var9
      );
   }

   static void face(
      VertexConsumer var0,
      Matrix4f var1,
      float var2,
      float var3,
      float var4,
      float var5,
      float var6,
      float var7,
      float var8,
      float var9,
      float var10,
      float var11,
      float var12,
      float var13,
      float var14,
      float var15,
      float var16,
      float var17,
      float var18,
      float var19,
      float var20,
      int var21,
      int var22
   ) {
      Vector3f var23 = var1.transformDirection(new Vector3f(var18, var19, var20)).normalize();
      vert(var0, var1, var2, var3, var4, var14, var15, var21, var22, var23);
      vert(var0, var1, var5, var6, var7, var16, var15, var21, var22, var23);
      vert(var0, var1, var8, var9, var10, var16, var17, var21, var22, var23);
      vert(var0, var1, var11, var12, var13, var14, var17, var21, var22, var23);
   }

   static void vert(VertexConsumer var0, Matrix4f var1, float var2, float var3, float var4, float var5, float var6, int var7, int var8, Vector3f var9) {
      var0.vertex(var1, var2, var3, var4).color(var7).texture(var5, var6).overlay(OverlayTexture.DEFAULT_UV).light(var8).normal(var9.x, var9.y, var9.z);
   }

   static void cube(VertexConsumer var0, Matrix4f var1, float var2, float var3, float var4, float var5, float var6, float var7, int var8, int var9) {
      float var10 = 0.5F;
      face(var0, var1, var2, var6, var7, var5, var6, var7, var5, var3, var7, var2, var3, var7, var10, var10, var10, var10, 0.0F, 0.0F, 1.0F, var8, var9);
      face(var0, var1, var5, var6, var4, var2, var6, var4, var2, var3, var4, var5, var3, var4, var10, var10, var10, var10, 0.0F, 0.0F, -1.0F, var8, var9);
      face(var0, var1, var2, var6, var4, var2, var6, var7, var2, var3, var7, var2, var3, var4, var10, var10, var10, var10, -1.0F, 0.0F, 0.0F, var8, var9);
      face(var0, var1, var5, var6, var7, var5, var6, var4, var5, var3, var4, var5, var3, var7, var10, var10, var10, var10, 1.0F, 0.0F, 0.0F, var8, var9);
      face(var0, var1, var2, var6, var4, var5, var6, var4, var5, var6, var7, var2, var6, var7, var10, var10, var10, var10, 0.0F, 1.0F, 0.0F, var8, var9);
      face(var0, var1, var2, var3, var7, var5, var3, var7, var5, var3, var4, var2, var3, var4, var10, var10, var10, var10, 0.0F, -1.0F, 0.0F, var8, var9);
   }

   static void tri(
      VertexConsumer var0,
      Matrix4f var1,
      float var2,
      float var3,
      float var4,
      float var5,
      float var6,
      float var7,
      float var8,
      float var9,
      float var10,
      int var11,
      int var12
   ) {
      float var13 = var5 - var2;
      float var14 = var6 - var3;
      float var15 = var7 - var4;
      float var16 = var8 - var2;
      float var17 = var9 - var3;
      float var18 = var10 - var4;
      Vector3f var19 = var1.transformDirection(new Vector3f(var14 * var18 - var15 * var17, var15 * var16 - var13 * var18, var13 * var17 - var14 * var16))
         .normalize();
      vert(var0, var1, var2, var3, var4, 0.5F, 0.5F, var11, var12, var19);
      vert(var0, var1, var5, var6, var7, 0.5F, 0.5F, var11, var12, var19);
      vert(var0, var1, var8, var9, var10, 0.5F, 0.5F, var11, var12, var19);
      vert(var0, var1, var8, var9, var10, 0.5F, 0.5F, var11, var12, var19);
   }

   static void quadW(
      VertexConsumer var0,
      Matrix4f var1,
      float var2,
      float var3,
      float var4,
      float var5,
      float var6,
      float var7,
      float var8,
      float var9,
      float var10,
      float var11,
      float var12,
      float var13,
      int var14,
      int var15
   ) {
      Vector3f var16 = var1.transformDirection(new Vector3f(0.0F, 0.0F, 1.0F)).normalize();
      vert(var0, var1, var2, var3, var4, 0.5F, 0.5F, var14, var15, var16);
      vert(var0, var1, var5, var6, var7, 0.5F, 0.5F, var14, var15, var16);
      vert(var0, var1, var8, var9, var10, 0.5F, 0.5F, var14, var15, var16);
      vert(var0, var1, var11, var12, var13, 0.5F, 0.5F, var14, var15, var16);
   }

   static void gv(VertexConsumer var0, Matrix4f var1, float var2, float var3, float var4, int var5) {
      var0.vertex(var1, var2, var3, var4).color(var5);
   }

   static void gquad(VertexConsumer var0, Matrix4f var1, float[] var2, int var3, int var4, int var5, int var6) {
      gv(var0, var1, var2[0], var2[1], var2[2], var3);
      gv(var0, var1, var2[3], var2[4], var2[5], var4);
      gv(var0, var1, var2[6], var2[7], var2[8], var5);
      gv(var0, var1, var2[9], var2[10], var2[11], var6);
      gv(var0, var1, var2[9], var2[10], var2[11], var6);
      gv(var0, var1, var2[6], var2[7], var2[8], var5);
      gv(var0, var1, var2[3], var2[4], var2[5], var4);
      gv(var0, var1, var2[0], var2[1], var2[2], var3);
   }

   static void disc(
      VertexConsumer var0, Matrix4f var1, float var2, float var3, float var4, float var5, float var6, int var7, float var8, int var9, float var10, int var11
   ) {
      if (!(var8 <= 0.003F) || !(var10 <= 0.003F)) {
         int var12 = argb(var7, var8);
         int var13 = argb(var9, var10);

         for (int var14 = 0; var14 < var11; var14++) {
            double var15 = var14 * Math.PI * 2.0 / var11;
            double var17 = (var14 + 1) * Math.PI * 2.0 / var11;
            float var19 = (float)Math.cos(var15);
            float var20 = (float)Math.sin(var15);
            float var21 = (float)Math.cos(var17);
            float var22 = (float)Math.sin(var17);
            GP[0] = var2 + var19 * var5;
            GP[1] = var3;
            GP[2] = var4 + var20 * var5;
            GP[3] = var2 + var21 * var5;
            GP[4] = var3;
            GP[5] = var4 + var22 * var5;
            GP[6] = var2 + var21 * var6;
            GP[7] = var3;
            GP[8] = var4 + var22 * var6;
            GP[9] = var2 + var19 * var6;
            GP[10] = var3;
            GP[11] = var4 + var20 * var6;
            gquad(var0, var1, GP, var12, var12, var13, var13);
         }
      }
   }

   static void strip(VertexConsumer var0, Matrix4f var1, float var2, float var3, float var4, float var5, float var6, float var7, int var8, float var9) {
      float var10 = var4 - var2;
      float var11 = var5 - var3;
      float var12 = (float)Math.sqrt(var10 * var10 + var11 * var11);
      if (!(var12 < 1.0E-5F)) {
         float var13 = -var11 / var12 * var7 / 2.0F;
         float var14 = var10 / var12 * var7 / 2.0F;
         int var15 = argb(var8, var9);
         GP[0] = var2 + var13;
         GP[1] = var6;
         GP[2] = var3 + var14;
         GP[3] = var4 + var13;
         GP[4] = var6;
         GP[5] = var5 + var14;
         GP[6] = var4 - var13;
         GP[7] = var6;
         GP[8] = var5 - var14;
         GP[9] = var2 - var13;
         GP[10] = var6;
         GP[11] = var3 - var14;
         gquad(var0, var1, GP, var15, var15, var15, var15);
      }
   }

   static void ringAround(
      VertexConsumer var0,
      Matrix4f var1,
      float var2,
      float var3,
      float var4,
      float var5,
      float var6,
      float var7,
      float var8,
      float var9,
      int var10,
      float var11
   ) {
      float var12 = (float)Math.sqrt(var5 * var5 + var6 * var6 + var7 * var7);
      if (!(var12 < 1.0E-6F)) {
         var5 /= var12;
         var6 /= var12;
         var7 /= var12;
         float var13 = -var7;
         float var14 = 0.0F;
         float var15 = var5;
         float var16 = (float)Math.sqrt(var13 * var13 + var15 * var15);
         if (var16 < 1.0E-4F) {
            var13 = 1.0F;
            var15 = 0.0F;
         } else {
            var13 /= var16;
            var15 /= var16;
         }

         float var17 = var6 * var15 - var7 * var14;
         float var18 = var7 * var13 - var5 * var15;
         float var19 = var5 * var14 - var6 * var13;
         int var20 = argb(var10, 0.0F);
         int var21 = argb(var10, var11);
         byte var22 = 40;
         float var23 = var8 - var9;
         float var24 = var8;

         for (int var25 = 0; var25 < var22; var25++) {
            double var26 = var25 * Math.PI * 2.0 / var22;
            double var28 = (var25 + 1) * Math.PI * 2.0 / var22;
            float var30 = (float)Math.cos(var26);
            float var31 = (float)Math.sin(var26);
            float var32 = (float)Math.cos(var28);
            float var33 = (float)Math.sin(var28);
            float var34 = var13 * var30 + var17 * var31;
            float var35 = var14 * var30 + var18 * var31;
            float var36 = var15 * var30 + var19 * var31;
            float var37 = var13 * var32 + var17 * var33;
            float var38 = var14 * var32 + var18 * var33;
            float var39 = var15 * var32 + var19 * var33;
            GP[0] = var2 + var34 * var23;
            GP[1] = var3 + var35 * var23;
            GP[2] = var4 + var36 * var23;
            GP[3] = var2 + var37 * var23;
            GP[4] = var3 + var38 * var23;
            GP[5] = var4 + var39 * var23;
            GP[6] = var2 + var37 * var24;
            GP[7] = var3 + var38 * var24;
            GP[8] = var4 + var39 * var24;
            GP[9] = var2 + var34 * var24;
            GP[10] = var3 + var35 * var24;
            GP[11] = var4 + var36 * var24;
            gquad(var0, var1, GP, var20, var20, var21, var21);
         }
      }
   }

   static void sprite(
      VertexConsumer var0,
      Matrix4f var1,
      DeathFx.Fx var2,
      Vec3d var3,
      float var4,
      float var5,
      float var6,
      float var7,
      int var8,
      int var9,
      float var10,
      float var11,
      float var12,
      float var13,
      float var14,
      float var15,
      float var16
   ) {
      if (!(var10 <= 0.02F)) {
         float var17 = (float)Math.cos(-var2.yaw);
         float var18 = (float)Math.sin(-var2.yaw);
         float var19 = (float)(var2.ox - var3.x) + var4 * var17 + var6 * var18;
         float var20 = (float)(var2.oy - var3.y) + var5;
         float var21 = (float)(var2.oz - var3.z) - var4 * var18 + var6 * var17;
         float var22 = var7 / 2.0F;
         int var23 = argb(var9, var10);
         float var24 = var8 * 0.25F;
         float var25 = var24 + 0.25F;
         float[] var26 = new float[]{
            var19 - var11 * var22 - var14 * var22,
            var20 - var12 * var22 - var15 * var22,
            var21 - var13 * var22 - var16 * var22,
            var19 + var11 * var22 - var14 * var22,
            var20 + var12 * var22 - var15 * var22,
            var21 + var13 * var22 - var16 * var22,
            var19 + var11 * var22 + var14 * var22,
            var20 + var12 * var22 + var15 * var22,
            var21 + var13 * var22 + var16 * var22,
            var19 - var11 * var22 + var14 * var22,
            var20 - var12 * var22 + var15 * var22,
            var21 - var13 * var22 + var16 * var22
         };
         float[][] var27 = new float[][]{{var24, 1.0F}, {var25, 1.0F}, {var25, 0.0F}, {var24, 0.0F}};

         for (int var28 = 0; var28 < 4; var28++) {
            sv(var0, var1, var26, var28, var27, var23);
         }

         for (int var29 = 3; var29 >= 0; var29--) {
            sv(var0, var1, var26, var29, var27, var23);
         }
      }
   }

   static void sv(VertexConsumer var0, Matrix4f var1, float[] var2, int var3, float[][] var4, int var5) {
      var0.vertex(var1, var2[var3 * 3], var2[var3 * 3 + 1], var2[var3 * 3 + 2])
         .color(var5)
         .texture(var4[var3][0], var4[var3][1])
         .overlay(OverlayTexture.DEFAULT_UV)
         .light(15728880)
         .normal(0.0F, 1.0F, 0.0F);
   }

   static int light(MinecraftClient var0, double var1, double var3, double var5) {
      try {
         BlockPos var7 = BlockPos.ofFloored(var1, var3, var5);
         return LightmapTextureManager.pack(var0.world.getLightLevel(LightType.BLOCK, var7), var0.world.getLightLevel(LightType.SKY, var7));
      } catch (Throwable var8) {
         return 15728880;
      }
   }

   static float shade(int var0, float var1) {
      int var2 = Math.min(255, Math.round((var0 >> 16 & 0xFF) * var1));
      int var3 = Math.min(255, Math.round((var0 >> 8 & 0xFF) * var1));
      int var4 = Math.min(255, Math.round((var0 & 0xFF) * var1));
      return Float.intBitsToFloat(var2 << 16 | var3 << 8 | var4);
   }

   static int mix(int var0, int var1, float var2) {
      var2 = cl(var2);
      int var3 = Math.round((var0 >> 16 & 0xFF) + ((var1 >> 16 & 0xFF) - (var0 >> 16 & 0xFF)) * var2);
      int var4 = Math.round((var0 >> 8 & 0xFF) + ((var1 >> 8 & 0xFF) - (var0 >> 8 & 0xFF)) * var2);
      int var5 = Math.round((var0 & 0xFF) + ((var1 & 0xFF) - (var0 & 0xFF)) * var2);
      return 0xFF000000 | var3 << 16 | var4 << 8 | var5;
   }

   static int argb(int var0, float var1) {
      return Math.round(cl(var1) * 255.0F) << 24 | var0 & 16777215;
   }

   static float cl(float var0) {
      return Math.max(0.0F, Math.min(1.0F, var0));
   }

   static float sm(float var0, float var1, float var2) {
      float var3 = cl((var2 - var0) / (var1 - var0));
      return var3 * var3 * (3.0F - 2.0F * var3);
   }

   static float eo(float var0) {
      float var1 = 1.0F - cl(var0);
      return 1.0F - var1 * var1 * var1;
   }

   static float ei(float var0) {
      float var1 = cl(var0);
      return var1 * var1 * var1;
   }

   static float lerp(float var0, float var1, float var2) {
      return var0 + (var1 - var0) * var2;
   }

   static {
      float var0 = 0.0F;

      for (int var1 = 0; var1 < TT.length; var1++) {
         TT[var1] = var0;
         float var2 = var1 / 1000.0F;
         var0 += (1.0F - 0.83F * sm(1.8F, 1.97F, var2) * (1.0F - sm(2.85F, 3.1F, var2))) / 1000.0F;
      }

      ArrayList var17 = new ArrayList();

      for (int var18 = 0; var18 <= 24; var18++) {
         var17.add(new float[]{1.7F, -7.0F + 7.0F * var18 / 24.0F, 0.85F});
      }

      float var19 = 1.5F;
      float var20 = 1.45F;
      float var3 = 0.75F;

      for (int var4 = 1; var4 <= 80; var4++) {
         float var5 = var4 / 80.0F;
         float var6 = (1.0F - var5) * (1.0F - var5);
         float var7 = 2.0F * var5 * (1.0F - var5);
         float var8 = var5 * var5;
         var17.add(new float[]{var6 * 1.7F + var7 * var19 + var8 * 0.0F, var6 * 0.0F + var7 * var20 + var8 * 3.3F, var6 * 0.85F + var7 * var3 + var8 * 0.0F});
      }

      float var21 = 0.0F - var19;
      float var22 = 3.3F - var20;
      float var23 = 0.0F - var3;
      float var24 = (float)Math.sqrt(var21 * var21 + var22 * var22 + var23 * var23);
      DX = var21 / var24;
      DY = var22 / var24;
      DZ = var23 / var24;

      for (int var25 = 1; var25 <= 30; var25++) {
         float var9 = 4.0F * var25 / 30.0F;
         var17.add(new float[]{0.0F + DX * var9, 3.3F + DY * var9, 0.0F + DZ * var9});
      }

      int var26 = var17.size();
      PX = new float[var26];
      PY = new float[var26];
      PZ = new float[var26];
      PS = new float[var26];
      float var27 = 0.0F;
      float var10 = 0.0F;
      float var11 = 0.0F;

      for (int var12 = 0; var12 < var26; var12++) {
         float[] var13 = (float[])var17.get(var12);
         if (var12 > 0) {
            float var14 = var13[0] - PX[var12 - 1];
            float var15 = var13[1] - PY[var12 - 1];
            float var16 = var13[2] - PZ[var12 - 1];
            var27 += (float)Math.sqrt(var14 * var14 + var15 * var15 + var16 * var16);
         }

         PX[var12] = var13[0];
         PY[var12] = var13[1];
         PZ[var12] = var13[2];
         PS[var12] = var27;
         if (var12 == 24) {
            var11 = var27;
         }

         if (var12 == 104) {
            var10 = var27;
         }
      }

      SB = var10;
      SH = var11;
      SEND = SB + 0.4F;
      float var28 = 3.0F;
      float[] var29 = new float[3];

      for (float var30 = 2.32F; var30 < 4.0F; var30 += 0.002F) {
         pathPoint(sBody(var30), var29);
         if (var29[1] < 0.35F) {
            var28 = var30;
            break;
         }
      }

      TC = var28;
      TA = new float[3];
      TB = new float[3];
      ACTIVE = new ArrayList<>();
      TRIGGERED = new HashSet<>();
      SEEN = new HashSet<>();
      HITS = new HashMap<>();
      POSE = new DeathFx.Pose();
      Q = new float[3];
      Q2 = new float[3];
      AX = new float[6];
      GP = new float[12];
   }

   static final class Fx {
      int id;
      boolean self;
      boolean test;
      double ox;
      double oy;
      double oz;
      float yaw;
      Identifier skin;
      int kind = 1;
      Object extra;
      long start;
      int groundCol;
      int dirtCol;
      float[][] cracks;
      float[][] chunks;
      float[][] ring;
      float[][] burst;
      float[][] embers;
      float[][] wisps;
      float[][] dust;
   }

   static final class Pose {
      final float[] p = new float[3];
      float rx;
      float ry;
      float rz;
      float arm;
      float leg;
      float head;
      float e;
   }
}
