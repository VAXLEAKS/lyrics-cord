package dev.neonlyrics.cosmetic;

import dev.neonlyrics.sky.SkyTextures;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

final class DeathHard {
   static final int FIRST = 3;
   static final float[] END = new float[]{6.4F};
   private static final float[][][] SLOW = new float[][][]{{{1.6F, 2.0F, 0.22F}, {3.45F, 3.7F, 0.45F}}};
   static final float[][] TT = new float[END.length][];
   static final int[] BLOOD;
   static final int[] DIRT;
   static final int[][] UV;
   static final int[][] DIM;
   static final int[] BOTTOM;
   static final float[] INFL;

   private DeathHard() {
   }

   private static float[] table(float[][] var0, float var1) {
      ArrayList var2 = new ArrayList();
      float var3 = 0.0F;

      while (var3 < var1 + 0.05F && var2.size() < 30000) {
         var2.add(var3);
         float var4 = 1.0F;

         for (float[] var8 : var0) {
            float var9 = DeathFx.sm(var8[0] - 0.08F, var8[0], var3) * (1.0F - DeathFx.sm(var8[1], var8[1] + 0.12F, var3));
            var4 = Math.min(var4, DeathFx.lerp(1.0F, var8[2], var9));
         }

         var3 += var4 / 1000.0F;
      }

      float[] var10 = new float[var2.size()];

      for (int var11 = 0; var11 < var10.length; var11++) {
         var10[var11] = (Float)var2.get(var11);
      }

      return var10;
   }

   static Object create(DeathFx.Fx var0, Random var1) {
      return new DeathHard.Tentacles(var1);
   }

   static void draw(MinecraftClient var0, VertexConsumerProvider var1, Matrix4f var2, Vec3d var3, DeathFx.Fx var4, float var5) {
      if (var4.extra instanceof DeathHard.Anim var6 && Accessories.whiteTex()) {
         DeathHard.Ctx var12 = new DeathHard.Ctx();
         var12.mc = var0;
         var12.base = var2;
         var12.cam = var3;
         var12.f = var4;
         var12.m = new Matrix4f(var2).translate((float)(var4.ox - var3.x), (float)(var4.oy - var3.y), (float)(var4.oz - var3.z)).rotateY(-var4.yaw);
         var12.light = DeathFx.light(var0, var4.ox, var4.oy + 1.0, var4.oz);
         var12.firstPerson = var4.self && var0.options.getPerspective().isFirstPerson();
         float var8 = var5 - var6.lastT;
         if (var8 < 0.0F || var8 > 0.5F) {
            var8 = 0.0F;
         }

         var6.lastT = var5;

         while (var8 > 1.0E-5F) {
            float var9 = Math.min(var8, 0.016666668F);
            var6.tick(var6.simT + var9, var9);
            var6.simT += var9;
            var8 -= var9;
         }

         var6.simT = var5;
         var6.pose(var5);
         VertexConsumer var13 = var1.getBuffer(RenderLayers.debugQuads());
         var6.glow(var12, var13, var5);
         var6.drawPools(var13, var12.m, var5);
         VertexConsumer var10 = var1.getBuffer(RenderLayers.entityCutoutNoCull(Accessories.WHITE));
         var6.solid(var12, var10, var5);
         if (var6.blood != null) {
            var6.blood.draw(var10, var12.m, var6.fade(var5), var12.light);
         }

         if (var6.dirt != null) {
            var6.dirt.draw(var10, var12.m, var6.fade(var5), var12.light);
         }

         if (var4.skin != null && !var12.firstPerson) {
            VertexConsumer var11 = var1.getBuffer(RenderLayers.entityCutoutNoCull(var4.skin));
            var6.skin(var12, var11, var5);
         }

         if (SkyTextures.spritesReady()) {
            VertexConsumer var14 = var1.getBuffer(RenderLayers.text(SkyTextures.SPRITES));
            var6.sprites(var12, var14, var5);
         }
      }
   }

   static void poolDisc(VertexConsumer var0, Matrix4f var1, float var2, float var3, float var4, float var5, float var6, int var7) {
      if (!(var5 <= 0.01F) && !(var6 <= 0.01F)) {
         byte var8 = 18;
         float[] var9 = new float[12];

         for (int var10 = 0; var10 < var8; var10++) {
            float var11 = var4 * var5 * (0.7F + 0.5F * hash(var7 + var10));
            float var12 = var4 * var5 * (0.7F + 0.5F * hash(var7 + (var10 + 1) % var8));
            double var13 = var10 * Math.PI * 2.0 / var8;
            double var15 = (var10 + 1) * Math.PI * 2.0 / var8;
            var9[0] = var2;
            var9[1] = 0.016F;
            var9[2] = var3;
            var9[3] = var2;
            var9[4] = 0.016F;
            var9[5] = var3;
            var9[6] = var2 + (float)Math.cos(var15) * var12;
            var9[7] = 0.016F;
            var9[8] = var3 + (float)Math.sin(var15) * var12;
            var9[9] = var2 + (float)Math.cos(var13) * var11;
            var9[10] = 0.016F;
            var9[11] = var3 + (float)Math.sin(var13) * var11;
            DeathFx.gquad(
               var0,
               var1,
               var9,
               DeathFx.argb(7999004, var6 * 0.95F),
               DeathFx.argb(7999004, var6 * 0.95F),
               DeathFx.argb(4851730, var6 * 0.92F),
               DeathFx.argb(4851730, var6 * 0.92F)
            );
         }
      }
   }

   static float hash(int var0) {
      int var1 = var0 * 374761393 + 668265263;
      var1 = (var1 ^ var1 >>> 13) * 1274126177;
      return ((var1 ^ var1 >>> 16) & 65535) / 65535.0F;
   }

   static Matrix4f part(DeathHard.Rig var0, int var1, Matrix4f var2) {
      var2.identity().translate(var0.px, var0.py, var0.pz).rotateY(var0.ry).rotateX(var0.rx).rotateZ(var0.rz).translate(0.0F, -1.0F, 0.0F);
      switch (var1) {
         case 0:
            var2.translate(-0.1171875F, 0.703125F, 0.0F).rotateX(var0.legR[0]).rotateY(var0.legR[1]).rotateZ(var0.legR[2]).translate(0.0F, -0.3515625F, 0.0F);
            break;
         case 1:
         default:
            var2.translate(0.1171875F, 0.703125F, 0.0F).rotateX(var0.legL[0]).rotateY(var0.legL[1]).rotateZ(var0.legL[2]).translate(0.0F, -0.3515625F, 0.0F);
            break;
         case 2:
            var2.translate(0.0F, 1.0546875F, 0.0F).scale(1.0F, 1.0F, var0.belly);
            break;
         case 3:
            var2.translate(-0.3515625F, 1.2890625F, 0.0F).rotateX(var0.armR[0]).rotateY(var0.armR[1]).rotateZ(var0.armR[2]).translate(0.0F, -0.234375F, 0.0F);
            break;
         case 4:
            var2.translate(0.3515625F, 1.2890625F, 0.0F).rotateX(var0.armL[0]).rotateY(var0.armL[1]).rotateZ(var0.armL[2]).translate(0.0F, -0.234375F, 0.0F);
            break;
         case 5:
            var2.translate(0.0F, 1.40625F, 0.0F).rotateX(var0.head[0]).rotateY(var0.head[1]).rotateZ(var0.head[2]).translate(0.0F, 0.234375F, 0.0F);
      }

      return var2;
   }

   static void rig(VertexConsumer var0, Matrix4f var1, DeathHard.Rig var2, int var3, int var4, int var5, int var6) {
      Matrix4f var7 = new Matrix4f();

      for (int var8 = 0; var8 < 6; var8++) {
         int var9 = BOTTOM[var8];
         int var10 = DIM[var8][1];
         float var11 = Math.max(0, var5 - var9);
         float var12 = Math.min(var10, var6 - var9);
         if (!(var11 >= var12)) {
            Matrix4f var13 = new Matrix4f(var1).mul(part(var2, var8, var7));
            sbox(var0, var13, DIM[var8][0], var10, DIM[var8][2], 0.0F, UV[var8][0], UV[var8][1], var11, var12, var3, var4);
            sbox(var0, var13, DIM[var8][0], var10, DIM[var8][2], INFL[var8], UV[var8][2], UV[var8][3], var11, var12, var3, var4);
         }
      }
   }

   static void caps(VertexConsumer var0, Matrix4f var1, DeathHard.Rig var2, int var3, int var4) {
      Matrix4f var5 = new Matrix4f();

      for (int var6 = 0; var6 < 6; var6++) {
         int var7 = BOTTOM[var6];
         int var8 = DIM[var6][1];
         if (var3 > var7 && var3 < var7 + var8) {
            Matrix4f var9 = new Matrix4f(var1).mul(part(var2, var6, var5));
            float var10 = (var3 - var7 - var8 / 2.0F) * 0.05859375F;
            float var11 = DIM[var6][0] / 2.0F * 0.05859375F * 0.98F;
            float var12 = DIM[var6][2] / 2.0F * 0.05859375F * 0.98F;
            DeathFx.face(
               var0,
               var9,
               -var11,
               var10,
               -var12,
               var11,
               var10,
               -var12,
               var11,
               var10,
               var12,
               -var11,
               var10,
               var12,
               0.5F,
               0.5F,
               0.5F,
               0.5F,
               0.0F,
               1.0F,
               0.0F,
               var4,
               15728880
            );
            DeathFx.face(
               var0,
               var9,
               -var11,
               var10,
               var12,
               var11,
               var10,
               var12,
               var11,
               var10,
               -var12,
               -var11,
               var10,
               -var12,
               0.5F,
               0.5F,
               0.5F,
               0.5F,
               0.0F,
               -1.0F,
               0.0F,
               var4,
               15728880
            );
         }
      }
   }

   static void sbox(
      VertexConsumer var0, Matrix4f var1, float var2, float var3, float var4, float var5, int var6, int var7, float var8, float var9, int var10, int var11
   ) {
      float var12 = (var2 / 2.0F + var5) * 0.05859375F;
      float var13 = (var4 / 2.0F + var5) * 0.05859375F;
      float var14 = (var8 - var3 / 2.0F) * 0.05859375F - (var8 <= 0.0F ? var5 * 0.05859375F : 0.0F);
      float var15 = (var9 - var3 / 2.0F) * 0.05859375F + (var9 >= var3 ? var5 * 0.05859375F : 0.0F);
      float var16 = 0.015625F;
      float var17 = (var7 + var4 + (var3 - var9)) * var16;
      float var18 = (var7 + var4 + (var3 - var8)) * var16;
      DeathFx.face(
         var0,
         var1,
         -var12,
         var15,
         var13,
         var12,
         var15,
         var13,
         var12,
         var14,
         var13,
         -var12,
         var14,
         var13,
         (var6 + var4) * var16,
         var17,
         (var6 + var4 + var2) * var16,
         var18,
         0.0F,
         0.0F,
         1.0F,
         var10,
         var11
      );
      DeathFx.face(
         var0,
         var1,
         var12,
         var15,
         -var13,
         -var12,
         var15,
         -var13,
         -var12,
         var14,
         -var13,
         var12,
         var14,
         -var13,
         (var6 + 2.0F * var4 + var2) * var16,
         var17,
         (var6 + 2.0F * var4 + 2.0F * var2) * var16,
         var18,
         0.0F,
         0.0F,
         -1.0F,
         var10,
         var11
      );
      DeathFx.face(
         var0,
         var1,
         -var12,
         var15,
         -var13,
         -var12,
         var15,
         var13,
         -var12,
         var14,
         var13,
         -var12,
         var14,
         -var13,
         var6 * var16,
         var17,
         (var6 + var4) * var16,
         var18,
         -1.0F,
         0.0F,
         0.0F,
         var10,
         var11
      );
      DeathFx.face(
         var0,
         var1,
         var12,
         var15,
         var13,
         var12,
         var15,
         -var13,
         var12,
         var14,
         -var13,
         var12,
         var14,
         var13,
         (var6 + var4 + var2) * var16,
         var17,
         (var6 + 2.0F * var4 + var2) * var16,
         var18,
         1.0F,
         0.0F,
         0.0F,
         var10,
         var11
      );
      if (var9 >= var3) {
         DeathFx.face(
            var0,
            var1,
            -var12,
            var15,
            -var13,
            var12,
            var15,
            -var13,
            var12,
            var15,
            var13,
            -var12,
            var15,
            var13,
            (var6 + var4) * var16,
            var7 * var16,
            (var6 + var4 + var2) * var16,
            (var7 + var4) * var16,
            0.0F,
            1.0F,
            0.0F,
            var10,
            var11
         );
      }

      if (var8 <= 0.0F) {
         DeathFx.face(
            var0,
            var1,
            -var12,
            var14,
            var13,
            var12,
            var14,
            var13,
            var12,
            var14,
            -var13,
            -var12,
            var14,
            -var13,
            (var6 + var4 + var2) * var16,
            var7 * var16,
            (var6 + var4 + 2.0F * var2) * var16,
            (var7 + var4) * var16,
            0.0F,
            -1.0F,
            0.0F,
            var10,
            var11
         );
      }
   }

   static void voxel(VertexConsumer var0, Matrix4f var1, int var2, float var3, float var4, float var5, float var6, int var7, int var8) {
      int var9 = DIM[var2][0];
      int var10 = DIM[var2][1];
      int var11 = DIM[var2][2];
      int var12 = UV[var2][0];
      int var13 = UV[var2][1];
      float var14 = 0.015625F;
      float var15 = var6 / 2.0F * 0.05859375F;
      float var16 = var12 + var11 + (var3 - var6 / 2.0F + var9 / 2.0F);
      float var17 = var13 + var11 + (var10 / 2.0F - (var4 + var6 / 2.0F));
      DeathFx.face(
         var0,
         var1,
         -var15,
         var15,
         var15,
         var15,
         var15,
         var15,
         var15,
         -var15,
         var15,
         -var15,
         -var15,
         var15,
         var16 * var14,
         var17 * var14,
         (var16 + var6) * var14,
         (var17 + var6) * var14,
         0.0F,
         0.0F,
         1.0F,
         var7,
         var8
      );
      float var18 = var12 + 2 * var11 + var9 + (var9 / 2.0F - (var3 + var6 / 2.0F));
      DeathFx.face(
         var0,
         var1,
         var15,
         var15,
         -var15,
         -var15,
         var15,
         -var15,
         -var15,
         -var15,
         -var15,
         var15,
         -var15,
         -var15,
         var18 * var14,
         var17 * var14,
         (var18 + var6) * var14,
         (var17 + var6) * var14,
         0.0F,
         0.0F,
         -1.0F,
         var7,
         var8
      );
      float var19 = var12 + (var5 - var6 / 2.0F + var11 / 2.0F);
      DeathFx.face(
         var0,
         var1,
         -var15,
         var15,
         -var15,
         -var15,
         var15,
         var15,
         -var15,
         -var15,
         var15,
         -var15,
         -var15,
         -var15,
         var19 * var14,
         var17 * var14,
         (var19 + var6) * var14,
         (var17 + var6) * var14,
         -1.0F,
         0.0F,
         0.0F,
         var7,
         var8
      );
      float var20 = var12 + var11 + var9 + (var11 / 2.0F - (var5 + var6 / 2.0F));
      DeathFx.face(
         var0,
         var1,
         var15,
         var15,
         var15,
         var15,
         var15,
         -var15,
         var15,
         -var15,
         -var15,
         var15,
         -var15,
         var15,
         var20 * var14,
         var17 * var14,
         (var20 + var6) * var14,
         (var17 + var6) * var14,
         1.0F,
         0.0F,
         0.0F,
         var7,
         var8
      );
      float var21 = var12 + var11 + (var3 - var6 / 2.0F + var9 / 2.0F);
      float var22 = var13 + (var5 - var6 / 2.0F + var11 / 2.0F);
      DeathFx.face(
         var0,
         var1,
         -var15,
         var15,
         -var15,
         var15,
         var15,
         -var15,
         var15,
         var15,
         var15,
         -var15,
         var15,
         var15,
         var21 * var14,
         var22 * var14,
         (var21 + var6) * var14,
         (var22 + var6) * var14,
         0.0F,
         1.0F,
         0.0F,
         var7,
         var8
      );
      float var23 = var12 + var11 + var9 + (var3 - var6 / 2.0F + var9 / 2.0F);
      float var24 = var13 + (var11 / 2.0F - (var5 + var6 / 2.0F));
      DeathFx.face(
         var0,
         var1,
         -var15,
         -var15,
         var15,
         var15,
         -var15,
         var15,
         var15,
         -var15,
         -var15,
         -var15,
         -var15,
         -var15,
         var23 * var14,
         var24 * var14,
         (var23 + var6) * var14,
         (var24 + var6) * var14,
         0.0F,
         -1.0F,
         0.0F,
         var7,
         var8
      );
   }

   static List<DeathHard.Vox> voxelize(DeathHard.Rig var0, int var1) {
      ArrayList var2 = new ArrayList();
      Matrix4f var3 = new Matrix4f();

      for (int var4 = 0; var4 < 6; var4++) {
         part(var0, var4, var3);
         Quaternionf var5 = var3.getNormalizedRotation(new Quaternionf());
         int var6 = 0;

         while (var6 < DIM[var4][0]) {
            for (int var7 = 0; var7 < DIM[var4][1]; var7 += var1) {
               for (int var8 = 0; var8 < DIM[var4][2]; var8 += var1) {
                  DeathHard.Vox var9 = new DeathHard.Vox();
                  var9.part = var4;
                  var9.cx = var6 + var1 / 2.0F - DIM[var4][0] / 2.0F;
                  var9.cy = var7 + var1 / 2.0F - DIM[var4][1] / 2.0F;
                  var9.cz = var8 + var1 / 2.0F - DIM[var4][2] / 2.0F;
                  var3.transformPosition(var9.cx * 0.05859375F, var9.cy * 0.05859375F, var9.cz * 0.05859375F, var9.p);
                  var9.q.set(var5);
                  var2.add(var9);
               }
            }

            var6 += var1;
         }
      }

      return var2;
   }

   static void drawVox(VertexConsumer var0, Matrix4f var1, DeathHard.Vox var2, float var3, float var4, int var5) {
      if (!(var4 <= 0.01F)) {
         Matrix4f var6 = new Matrix4f(var1).translate(var2.p).rotate(var2.q).rotateXYZ(var2.r.x, var2.r.y, var2.r.z).scale(var4);
         voxel(var0, var6, var2.part, var2.cx, var2.cy, var2.cz, var3, var2.tint, var5);
      }
   }

   static boolean fly(Vector3f var0, Vector3f var1, Vector3f var2, Vector3f var3, float var4, float var5, float var6) {
      var1.y -= var6 * var4;
      var0.add(var1.x * var4, var1.y * var4, var1.z * var4);
      var2.add(var3.x * var4, var3.y * var4, var3.z * var4);
      if (var0.y < var5) {
         var0.y = var5;
         if (var1.y < 0.0F) {
            var1.y *= -0.3F;
         }

         var1.x *= 0.55F;
         var1.z *= 0.55F;
         var3.mul(0.55F);
         return true;
      } else {
         return false;
      }
   }

   static void gline(
      VertexConsumer var0, Matrix4f var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, int var9, float var10
   ) {
      if (!(var10 <= 0.003F)) {
         float var11 = var5 - var2;
         float var12 = var6 - var3;
         float var13 = var7 - var4;
         float var14 = (float)Math.sqrt(var11 * var11 + var12 * var12 + var13 * var13);
         if (!(var14 < 1.0E-5F)) {
            var11 /= var14;
            var12 /= var14;
            var13 /= var14;
            float var15 = 0.0F;
            float var16 = 1.0F;
            float var17 = 0.0F;
            if (Math.abs(var12) > 0.9F) {
               var15 = 1.0F;
               var16 = 0.0F;
            }

            float var18 = var12 * var17 - var13 * var16;
            float var19 = var13 * var15 - var11 * var17;
            float var20 = var11 * var16 - var12 * var15;
            float var21 = (float)Math.sqrt(var18 * var18 + var19 * var19 + var20 * var20);
            var18 /= var21;
            var19 /= var21;
            var20 /= var21;
            float var22 = var12 * var20 - var13 * var19;
            float var23 = var13 * var18 - var11 * var20;
            float var24 = var11 * var19 - var12 * var18;
            int var25 = DeathFx.argb(var9, var10);
            float var26 = var8 / 2.0F;
            float[] var27 = new float[12];

            for (int var28 = 0; var28 < 2; var28++) {
               float var29 = var28 == 0 ? var18 : var22;
               float var30 = var28 == 0 ? var19 : var23;
               float var31 = var28 == 0 ? var20 : var24;
               var27[0] = var2 + var29 * var26;
               var27[1] = var3 + var30 * var26;
               var27[2] = var4 + var31 * var26;
               var27[3] = var5 + var29 * var26;
               var27[4] = var6 + var30 * var26;
               var27[5] = var7 + var31 * var26;
               var27[6] = var5 - var29 * var26;
               var27[7] = var6 - var30 * var26;
               var27[8] = var7 - var31 * var26;
               var27[9] = var2 - var29 * var26;
               var27[10] = var3 - var30 * var26;
               var27[11] = var4 - var31 * var26;
               DeathFx.gquad(var0, var1, var27, var25, var25, var25, var25);
            }
         }
      }
   }

   static void gbox(VertexConsumer var0, Matrix4f var1, float var2, float var3, float var4, int var5) {
      float[][] var6 = new float[][]{
         {-var2, var3, var4, var2, var3, var4, var2, -var3, var4, -var2, -var3, var4},
         {var2, var3, -var4, -var2, var3, -var4, -var2, -var3, -var4, var2, -var3, -var4},
         {-var2, var3, -var4, -var2, var3, var4, -var2, -var3, var4, -var2, -var3, -var4},
         {var2, var3, var4, var2, var3, -var4, var2, -var3, -var4, var2, -var3, var4},
         {-var2, var3, -var4, var2, var3, -var4, var2, var3, var4, -var2, var3, var4},
         {-var2, -var3, var4, var2, -var3, var4, var2, -var3, -var4, -var2, -var3, -var4}
      };

      for (float[] var10 : var6) {
         DeathFx.gquad(var0, var1, var10, var5, var5, var5, var5);
      }
   }

   static void block(VertexConsumer var0, Matrix4f var1, float var2, float var3, float var4, float var5, float var6, float var7, int var8, int var9) {
      DeathFx.cube(
         var0,
         var1,
         var2 - var5 / 2.0F,
         var3 - var6 / 2.0F,
         var4 - var7 / 2.0F,
         var2 + var5 / 2.0F,
         var3 + var6 / 2.0F,
         var4 + var7 / 2.0F,
         0xFF000000 | var8,
         var9
      );
   }

   static float rnd(Random var0) {
      return var0.nextFloat() - 0.5F;
   }

   static {
      for (int var0 = 0; var0 < END.length; var0++) {
         TT[var0] = table(SLOW[var0], END[var0]);
      }

      BLOOD = new int[]{3802640, 6949400, 11540526, 16722501};
      DIRT = new int[]{5214008, 6965804, 4160046, 8017460};
      UV = new int[][]{{0, 16, 0, 32}, {16, 48, 0, 48}, {16, 16, 16, 32}, {40, 16, 40, 32}, {32, 48, 48, 48}, {0, 0, 32, 0}};
      DIM = new int[][]{{4, 12, 4}, {4, 12, 4}, {8, 12, 4}, {4, 12, 4}, {4, 12, 4}, {8, 8, 8}};
      BOTTOM = new int[]{0, 0, 12, 12, 12, 24};
      INFL = new float[]{0.25F, 0.25F, 0.25F, 0.25F, 0.25F, 0.5F};
   }

   abstract static class Anim {
      final Random r;
      float lastT = 0.0F;
      float simT = 0.0F;
      DeathHard.Blood blood;
      DeathHard.Blood dirt;
      final List<float[]> pools = new ArrayList<>();

      Anim(Random var1) {
         this.r = new Random(var1.nextLong());
      }

      void tick(float var1, float var2) {
         if (this.blood != null) {
            this.blood.step(var2);
         }

         if (this.dirt != null) {
            this.dirt.step(var2);
         }
      }

      abstract void pose(float var1);

      void glow(DeathHard.Ctx var1, VertexConsumer var2, float var3) {
      }

      void solid(DeathHard.Ctx var1, VertexConsumer var2, float var3) {
      }

      void skin(DeathHard.Ctx var1, VertexConsumer var2, float var3) {
      }

      void sprites(DeathHard.Ctx var1, VertexConsumer var2, float var3) {
      }

      float end() {
         return 5.4F;
      }

      float fade(float var1) {
         return 1.0F - DeathFx.sm(this.end() - 0.45F, this.end(), var1);
      }

      void pool(float var1, float var2, float var3, float var4, float var5) {
         this.pools.add(new float[]{var1, var2, var3, var4, var5, this.r.nextInt(100000)});
      }

      void drawPools(VertexConsumer var1, Matrix4f var2, float var3) {
         float var4 = this.fade(var3);

         for (float[] var6 : this.pools) {
            DeathHard.poolDisc(var1, var2, var6[0], var6[1], var6[2], DeathFx.eo((var3 - var6[3]) / var6[4]), var4, (int)var6[5]);
         }
      }

      void spr(DeathHard.Ctx var1, VertexConsumer var2, float var3, float var4, float var5, float var6, int var7, int var8, float var9) {
         DeathFx.sprite(
            var2,
            var1.base,
            var1.f,
            var1.cam,
            var3,
            var4,
            var5,
            var6,
            var7,
            var8,
            var9,
            DeathFx.AX[0],
            DeathFx.AX[1],
            DeathFx.AX[2],
            DeathFx.AX[3],
            DeathFx.AX[4],
            DeathFx.AX[5]
         );
      }
   }

   static final class Blood {
      final int max;
      final int[] cols;
      int n = 0;
      final float[] x;
      final float[] y;
      final float[] z;
      final float[] vx;
      final float[] vy;
      final float[] vz;
      final float[] s;
      final float[] splat;
      final float[] rot;
      final int[] col;
      final boolean[] down;
      final Random r;

      Blood(int var1, Random var2, int[] var3) {
         this.max = var1;
         this.r = var2;
         this.cols = var3;
         this.x = new float[var1];
         this.y = new float[var1];
         this.z = new float[var1];
         this.vx = new float[var1];
         this.vy = new float[var1];
         this.vz = new float[var1];
         this.s = new float[var1];
         this.splat = new float[var1];
         this.rot = new float[var1];
         this.col = new int[var1];
         this.down = new boolean[var1];
      }

      void spurt(float var1, float var2, float var3, int var4, float var5, float var6, float var7, float var8, float var9) {
         int var10 = 0;

         while (var10 < var4 && this.n < this.max) {
            this.x[this.n] = var1 + (this.r.nextFloat() - 0.5F) * 0.15F;
            this.y[this.n] = var2 + (this.r.nextFloat() - 0.5F) * 0.15F;
            this.z[this.n] = var3 + (this.r.nextFloat() - 0.5F) * 0.15F;
            this.vx[this.n] = var5 + (this.r.nextFloat() - 0.5F) * var8;
            this.vy[this.n] = var6 + (this.r.nextFloat() - 0.5F) * var8 * 0.7F + var8 * 0.25F;
            this.vz[this.n] = var7 + (this.r.nextFloat() - 0.5F) * var8;
            this.s[this.n] = var9 * (0.5F + this.r.nextFloat());
            this.splat[this.n] = 1.0F + this.r.nextFloat() * 2.2F;
            this.rot[this.n] = this.r.nextFloat() * 6.28F;
            this.col[this.n] = this.cols[this.r.nextInt(this.cols.length)];
            this.down[this.n] = false;
            var10++;
            this.n++;
         }
      }

      void step(float var1) {
         for (int var2 = 0; var2 < this.n; var2++) {
            if (!this.down[var2]) {
               this.vy[var2] = this.vy[var2] - 14.0F * var1;
               this.x[var2] = this.x[var2] + this.vx[var2] * var1;
               this.y[var2] = this.y[var2] + this.vy[var2] * var1;
               this.z[var2] = this.z[var2] + this.vz[var2] * var1;
               if (this.y[var2] <= 0.015F) {
                  this.y[var2] = 0.015F;
                  this.down[var2] = true;
                  this.col[var2] = DeathFx.mix(this.col[var2], 4851730, 0.45F) & 16777215;
               }
            }
         }
      }

      void draw(VertexConsumer var1, Matrix4f var2, float var3, int var4) {
         if (!(var3 <= 0.01F)) {
            for (int var5 = 0; var5 < this.n; var5++) {
               int var6 = 0xFF000000 | this.col[var5];
               boolean var7 = this.col[var5] == 16722501;
               Matrix4f var8 = new Matrix4f(var2).translate(this.x[var5], this.y[var5], this.z[var5]).rotateY(this.rot[var5]);
               if (!this.down[var5]) {
                  float var9 = this.s[var5] / 2.0F * var3;
                  var8.rotateX(this.rot[var5] * this.y[var5]);
                  DeathFx.cube(var1, var8, -var9, -var9, -var9, var9, var9, var9, var6, var7 ? 15728880 : var4);
               } else {
                  float var11 = this.s[var5] * this.splat[var5] / 2.0F * var3;
                  float var10 = this.s[var5] * this.splat[var5] * 0.4F * var3;
                  DeathFx.cube(var1, var8, -var11, -0.004F, -var10, var11, 0.004F, var10, var6, var4);
               }
            }
         }
      }
   }

   static final class Ctx {
      MinecraftClient mc;
      Matrix4f base;
      Matrix4f m;
      Vec3d cam;
      DeathFx.Fx f;
      int light;
      boolean firstPerson;
   }

   static final class Rig {
      float px;
      float py = 1.0F;
      float pz;
      float rx;
      float ry;
      float rz;
      float belly = 1.0F;
      final float[] head = new float[3];
      final float[] armR = new float[3];
      final float[] armL = new float[3];
      final float[] legR = new float[3];
      final float[] legL = new float[3];

      void copy(DeathHard.Rig var1) {
         this.px = var1.px;
         this.py = var1.py;
         this.pz = var1.pz;
         this.rx = var1.rx;
         this.ry = var1.ry;
         this.rz = var1.rz;
         this.belly = var1.belly;
         System.arraycopy(var1.head, 0, this.head, 0, 3);
         System.arraycopy(var1.armR, 0, this.armR, 0, 3);
         System.arraycopy(var1.armL, 0, this.armL, 0, 3);
         System.arraycopy(var1.legR, 0, this.legR, 0, 3);
         System.arraycopy(var1.legL, 0, this.legL, 0, 3);
      }

      void zero() {
         this.px = 0.0F;
         this.py = 1.0F;
         this.pz = 0.0F;
         this.rx = this.ry = this.rz = 0.0F;
         this.belly = 1.0F;

         for (float[] var4 : new float[][]{this.head, this.armR, this.armL, this.legR, this.legL}) {
            Arrays.fill(var4, 0.0F);
         }
      }
   }

   static final class Tentacles extends DeathHard.Anim {
      final DeathHard.Rig g = new DeathHard.Rig();
      static final int N = 5;
      static final int SEG = 30;
      final float[] ph = new float[5];
      final float[] sp = new float[5];
      final float[] gx = new float[5];
      final float[] gz = new float[5];
      final boolean[] hit = new boolean[5];
      final Vector3f belly = new Vector3f();
      final Vector3f nrm = new Vector3f();
      boolean burst;
      float inner;
      float throb;
      float tug;
      float pullK;
      float arch;

      Tentacles(Random var1) {
         super(var1);
         this.blood = new DeathHard.Blood(520, this.r, DeathHard.BLOOD);
         this.dirt = new DeathHard.Blood(200, this.r, DeathHard.DIRT);

         for (int var2 = 0; var2 < 5; var2++) {
            double var3 = var2 / 5.0 * Math.PI * 2.0 + 0.4;
            this.gx[var2] = (float)Math.cos(var3) * 1.35F;
            this.gz[var2] = (float)Math.sin(var3) * 1.35F;
            this.ph[var2] = this.r.nextFloat() * 6.0F;
            this.sp[var2] = 0.8F + this.r.nextFloat() * 0.5F;
         }
      }

      @Override
      float end() {
         return 6.4F;
      }

      @Override
      void pose(float var1) {
         this.arch = DeathFx.eo(DeathFx.sm(0.8F, 1.55F, var1));
         this.pullK = DeathFx.ei(DeathFx.sm(3.45F, 4.9F, var1));
         float var2 = DeathFx.sm(0.1F, 0.4F, var1) * (1.0F - this.arch) * (this.r.nextFloat() < 0.25F ? 1 : 0);
         this.g.rx = -1.2F * this.arch + DeathHard.rnd(this.r) * 0.15F * var2;
         this.g.rz = DeathHard.rnd(this.r) * 0.12F * var2;
         this.tug = var1 > 3.45F ? 0.12F * Math.max(0.0F, (float)Math.sin((var1 - 3.45F) * 11.0F)) : 0.0F;
         this.g.py = 1.0F + 0.55F * this.arch - this.pullK * 3.4F - this.tug;
         this.g.head[0] = -0.8F * this.arch + DeathHard.rnd(this.r) * 0.4F * var2;
         float var3 = DeathFx.sm(4.1F, 4.5F, var1);
         this.g.armR[0] = 0.7F * this.arch * (1.0F - var3);
         this.g.armR[2] = DeathFx.lerp(-1.35F * this.arch, -2.9F, var3) + (float)Math.sin(var1 * 13.0F) * 0.15F * var3;
         this.g.armL[0] = 0.7F * this.arch * (1.0F - var3);
         this.g.armL[2] = DeathFx.lerp(1.35F * this.arch, 2.9F, var3) - (float)Math.sin(var1 * 15.0F) * 0.15F * var3;
         this.g.legR[0] = 0.35F * this.arch + DeathHard.rnd(this.r) * 0.3F * var2;
         this.g.legL[0] = 0.2F * this.arch;
         this.throb = 0.5F + 0.5F * (float)Math.sin(var1 * (6.0F + 10.0F * DeathFx.sm(0.8F, 1.6F, var1)));
         this.inner = DeathFx.sm(0.6F, 1.5F, var1) * (1.0F - DeathFx.sm(1.6F, 1.8F, var1));
         this.g.belly = var1 < 1.6F ? 1.0F + 0.25F * this.inner * this.throb : 1.0F;
         Matrix4f var4 = DeathHard.part(this.g, 2, new Matrix4f());
         var4.transformPosition(0.0F, 0.02F, DeathHard.DIM[2][2] / 2.0F * 0.05859375F, this.belly);
         var4.transformDirection(0.0F, 0.0F, 1.0F, this.nrm).normalize();
      }

      @Override
      void tick(float var1, float var2) {
         this.pose(var1);
         if (var1 >= 1.6F && !this.burst) {
            this.burst = true;
            this.blood.spurt(this.belly.x, this.belly.y, this.belly.z, 110, this.nrm.x * 4.0F, this.nrm.y * 4.0F, this.nrm.z * 4.0F, 3.5F, 0.075F);
         }

         if (var1 > 1.6F && var1 < 3.4F && this.r.nextFloat() < 0.7F) {
            this.blood.spurt(this.belly.x, this.belly.y, this.belly.z, 2, this.nrm.x * 1.6F, this.nrm.y * 1.6F, this.nrm.z * 1.6F, 1.6F, 0.05F);
         }

         if (var1 > 3.4F && this.pullK < 0.85F && this.r.nextFloat() < 0.5F) {
            this.blood.spurt(this.belly.x, Math.max(0.05F, this.belly.y), this.belly.z, 2, 0.0F, 1.2F, 0.0F, 2.0F, 0.05F);
         }

         float var3 = DeathFx.sm(2.35F, 3.25F, var1);

         for (int var4 = 0; var4 < 5; var4++) {
            if (var3 > 0.85F && !this.hit[var4]) {
               this.hit[var4] = true;
               this.dirt.spurt(this.gx[var4], 0.05F, this.gz[var4], 22, 0.0F, 3.0F, 0.0F, 2.6F, 0.07F);
            }
         }

         if (var1 > 4.9F && this.pools.isEmpty()) {
            this.pool(0.0F, 0.0F, 1.5F, var1, 1.0F);
         }

         super.tick(var1, var2);
      }

      @Override
      void glow(DeathHard.Ctx var1, VertexConsumer var2, float var3) {
         float var4 = this.fade(var3);
         DeathFx.disc(
            var2, var1.m, 0.0F, 0.012F, 0.0F, 0.0F, 1.6F, 9054936, 0.35F * DeathFx.sm(0.0F, 0.9F, var3) * (0.7F + 0.3F * this.throb) * var4, 9054936, 0.0F, 32
         );

         for (int var5 = 0; var5 < 5; var5++) {
            if (this.hit[var5]) {
               float var6 = 1.0F + 0.6F * DeathFx.sm(3.0F, 3.6F, var3);
               float var7 = (0.7F + 0.3F * (float)Math.sin(var3 * 12.0F + var5)) * (1.0F - DeathFx.sm(5.2F, 6.0F, var3));
               DeathFx.disc(var2, var1.m, this.gx[var5], 0.015F, this.gz[var5], 0.12F * var6, 0.32F * var6, 11553535, var7, 11553535, 0.0F, 16);
            }
         }

         float var8 = DeathFx.sm(3.3F, 3.7F, var3) * (1.0F - DeathFx.sm(5.1F, 5.9F, var3));
         if (var8 > 0.01F) {
            DeathFx.disc(var2, var1.m, 0.0F, 0.014F, 0.0F, 0.0F, 0.75F * var8, 524812, 1.0F, 524812, 1.0F, 28);
            DeathFx.disc(var2, var1.m, 0.0F, 0.016F, 0.0F, 0.75F * var8, 0.92F * var8, 11553535, 0.8F * var8, 11553535, 0.0F, 28);
         }
      }

      @Override
      void solid(DeathHard.Ctx var1, VertexConsumer var2, float var3) {
         float var4 = DeathFx.eo(DeathFx.sm(1.6F, 2.4F, var3));
         float var5 = DeathFx.sm(2.35F, 3.25F, var3);
         if (var4 > 0.01F) {
            for (int var6 = 0; var6 < 5; var6++) {
               float var7 = (float)Math.cos(var6 / 5.0 * Math.PI * 2.0 + 0.4);
               float var8 = (float)Math.sin(var6 / 5.0 * Math.PI * 2.0 + 0.4);
               float var9 = -var8;
               float var10 = var7;
               float var11 = 2.3F * var4;
               float var12 = this.belly.x;
               float var13 = this.belly.y;
               float var14 = this.belly.z;
               float var15 = this.belly.x + this.nrm.x * 1.2F + var7 * 0.6F;
               float var16 = this.belly.y + this.nrm.y * 1.2F;
               float var17 = this.belly.z + this.nrm.z * 1.2F + var8 * 0.6F;
               float var18 = this.gx[var6];
               float var19 = 1.3F - 0.9F * this.pullK;
               float var20 = this.gz[var6];
               float var21 = this.gx[var6];
               float var22 = -0.9F;
               float var23 = this.gz[var6];

               for (int var24 = 0; var24 < 30; var24++) {
                  float var25 = var24 / 29.0F;
                  float var26 = (float)Math.sin(var25 * 7.0F - var3 * 6.0F * this.sp[var6] + this.ph[var6]) * 0.35F * var25 * var4;
                  float var27 = this.belly.x + this.nrm.x * var25 * var11 * 0.85F + var7 * var25 * var11 * 0.55F + var9 * var26;
                  float var28 = this.belly.y
                     + this.nrm.y * var25 * var11 * 0.85F
                     + (float)Math.cos(var25 * 5.0F - var3 * 5.0F + this.ph[var6]) * 0.2F * var25 * var4;
                  float var29 = this.belly.z + this.nrm.z * var25 * var11 * 0.85F + var8 * var25 * var11 * 0.55F + var10 * var26;
                  float var30 = 1.0F - var25;
                  float var31 = var30 * var30 * var30;
                  float var32 = 3.0F * var30 * var30 * var25;
                  float var33 = 3.0F * var30 * var25 * var25;
                  float var34 = var25 * var25 * var25;
                  float var35 = (float)Math.sin(var25 * 9.0F - var3 * 9.0F + this.ph[var6]) * 0.06F * (1.0F - this.pullK);
                  float var36 = var31 * var12 + var32 * var15 + var33 * var18 + var34 * var21 + var9 * var35;
                  float var37 = var31 * var13 + var32 * var16 + var33 * var19 + var34 * var22;
                  float var38 = var31 * var14 + var32 * var17 + var33 * var20 + var34 * var23 + var10 * var35;
                  float var39 = DeathFx.ei(var5);
                  float var40 = DeathFx.lerp(var27, var36, var39);
                  float var41 = DeathFx.lerp(var28, var37, var39);
                  float var42 = DeathFx.lerp(var29, var38, var39);
                  if (!(var41 < -0.4F)) {
                     float var43 = (0.13F * (float)Math.pow(1.0F - var25, 0.7) + 0.025F) * Math.min(1.0F, var4 * 1.5F);
                     int var44 = DeathFx.mix(2756154, 10506456, var25);
                     if (var24 % 3 == 1) {
                        var44 = DeathFx.mix(var44 & 16777215, 14715592, 0.35F);
                     }

                     Matrix4f var45 = new Matrix4f(var1.m).translate(var40, var41, var42).rotateY(var25 * 3.0F + var6);
                     DeathFx.cube(var2, var45, -var43, -var43, -var43, var43, var43, var43, var44, var1.light);
                  }
               }
            }
         }

         if (this.burst && !var1.firstPerson) {
            Matrix4f var46 = new Matrix4f(var1.m).mul(DeathHard.part(this.g, 2, new Matrix4f()));
            float var47 = DeathHard.DIM[2][2] / 2.0F * 0.05859375F;
            DeathFx.cube(var2, var46, -0.15F, -0.16F, var47, 0.15F, 0.2F, var47 + 0.02F, -5236690, 15728880);
            DeathFx.cube(var2, var46, -0.09F, -0.09F, var47 + 0.005F, 0.09F, 0.13F, var47 + 0.025F, -12974576, var1.light);
         }
      }

      @Override
      void skin(DeathHard.Ctx var1, VertexConsumer var2, float var3) {
         int var4 = DeathFx.mix(16777215, 13144319, 0.5F * this.inner * this.throb);
         DeathHard.rig(var2, var1.m, this.g, var4, var1.light, 0, 32);
      }

      @Override
      void sprites(DeathHard.Ctx var1, VertexConsumer var2, float var3) {
         float var4 = DeathFx.sm(0.3F, 1.0F, var3) * (1.0F - DeathFx.sm(5.4F, 6.2F, var3));

         for (int var5 = 0; var5 < 60; var5++) {
            double var6 = var5 * 2.39996;
            float var8 = 0.3F + var5 % 9 * 0.15F;
            float var9 = (var3 * 0.6F + var5 % 13 / 13.0F) % 1.0F;
            this.spr(
               var1,
               var2,
               (float)Math.cos(var6) * var8,
               var9 * 2.2F,
               (float)Math.sin(var6) * var8,
               0.12F,
               2,
               13144319,
               var4 * (float)Math.sin(var9 * Math.PI) * 0.8F
            );
         }

         if (this.inner > 0.05F) {
            this.spr(
               var1,
               var2,
               this.belly.x + this.nrm.x * 0.1F,
               this.belly.y + this.nrm.y * 0.1F,
               this.belly.z + this.nrm.z * 0.1F,
               0.9F,
               2,
               11553535,
               this.inner * this.throb * 0.8F
            );
         }

         if (var3 > 1.6F && var3 < 2.2F) {
            this.spr(var1, var2, this.belly.x, this.belly.y, this.belly.z, 1.6F, 2, 16724048, 1.0F - (var3 - 1.6F) / 0.6F);
         }
      }
   }

   static final class Vox {
      int part;
      float cx;
      float cy;
      float cz;
      final Vector3f p = new Vector3f();
      final Vector3f v = new Vector3f();
      final Vector3f w = new Vector3f();
      final Vector3f r = new Vector3f();
      final Quaternionf q = new Quaternionf();
      int tint = -1;
      float floor;
      float delay;
   }
}
