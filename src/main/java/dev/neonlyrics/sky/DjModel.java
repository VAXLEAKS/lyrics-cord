package dev.neonlyrics.sky;

import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import org.joml.Matrix4f;
import org.joml.Vector3f;

final class DjModel {
   private static final int CLOTH = 0;
   private static final int SKIN = 1;
   private static final int CHROME = 2;
   private static final int GLOW = 3;
   private static final int GOLD = 4;
   private static final int GLOSS = 5;
   private static VertexConsumer vc;
   private static Matrix4f base;
   private static double D;
   private static double K;
   private static float alpha = 1.0F;
   private static float hit;
   private static float flash;
   private static final Matrix4f M = new Matrix4f();
   private static final Vector3f T0 = new Vector3f();
   private static final Vector3f T1 = new Vector3f();
   private static final float[] QP = new float[12];
   private static final float[] QN = new float[12];

   private DjModel() {
   }

   static void draw(VertexConsumer var0, Matrix4f var1, double var2, float var4, double var5, boolean var7, double var8, int var10) {
      vc = var0;
      base = var1;
      D = var2;
      K = var2 * Math.PI / 180.0;
      alpha = Math.max(0.0F, Math.min(1.0F, var4));
      int var11 = (int)Math.floor(var5);
      float var12 = (float)(var5 - var11);
      hit = var7 ? (float)Math.exp(-var12 * 5.0F) : 0.0F;
      flash = var7 ? hit : 0.15F + 0.1F * (float)Math.sin(var8 * 1.3);
      desk(var8, var5, var7);
      crowd(var5, var7, var8);
      figure(var11, var12, var7, var8, var10);
   }

   private static void figure(int var0, float var1, boolean var2, double var3, int var5) {
      byte var9 = 1;
      int var10 = 8014387;
      int var6;
      int var7;
      int var8;
      switch (var5) {
         case 1:
            var6 = 11450050;
            var7 = 10134448;
            var8 = 9344674;
            break;
         case 2:
            var6 = 15132396;
            var7 = 14342882;
            var8 = 15921910;
            break;
         case 3:
            var6 = 2829116;
            var7 = 2500154;
            var8 = var10;
            break;
         case 4:
            var6 = 13114922;
            var7 = 11541030;
            var8 = var10;
            break;
         default:
            var6 = 15789812;
            var7 = 15131884;
            var8 = var10;
      }

      int var11 = var5 == 1 ? 2 : 0;
      float var12 = var2 ? -0.55F * hit : 0.15F * (float)Math.sin(var3 * 1.6);
      M.identity().translate(0.0F, var12, 0.0F);
      ellipsoid(0.0F, 21.5F, 0.0F, 5.7F, 7.4F, 3.5F, 14, 10, var6, var11);
      ellipsoid(-5.3F, 27.3F, 0.0F, 2.2F, 2.0F, 2.2F, 10, 8, var7, var11);
      ellipsoid(5.3F, 27.3F, 0.0F, 2.2F, 2.0F, 2.2F, 10, 8, var7, var11);
      torsoExtras(var5, var0, var1, var2);
      float[] var13 = new float[3];
      float[] var14 = new float[3];
      float[] var15 = new float[3];
      float[] var16 = new float[3];
      hands(var0, var1, var2, var3, var13, var14);
      hands(var0 - 1, 1.0F, var2, var3, var15, var16);
      float var17 = var2 ? smooth(Math.min(1.0F, var1 / 0.28F)) : 1.0F;

      for (int var18 = 0; var18 < 3; var18++) {
         var13[var18] = var15[var18] + (var13[var18] - var15[var18]) * var17;
         var14[var18] = var16[var18] + (var14[var18] - var16[var18]) * var17;
      }

      var13[1] += var12;
      var14[1] += var12;
      arm(-5.3F, 27.3F + var12, 0.2F, var13, -1, var7, var8, var11);
      arm(5.3F, 27.3F + var12, 0.2F, var14, 1, var7, var8, var11);
      M.identity().translate(0.0F, var12, 0.0F);
      limb(0.0F, 28.5F, 0.0F, 0.0F, 31.0F, 0.2F, 1.3F, 8, var5 != 1 && var5 != 2 ? var10 : var6, var5 == 1 ? 2 : 1);
      float var20 = var2 ? 0.2F * hit : 0.04F * (float)Math.sin(var3 * 1.1);
      float var19 = var2 ? 0.12F * (float)Math.sin((var0 + var1) * Math.PI * 0.5) : 0.08F * (float)Math.sin(var3 * 0.5);
      M.identity().translate(0.0F, 31.0F + var12, 0.2F).rotateY(var19).rotateX(var20).translate(0.0F, -31.0F, -0.2F);
      head(var5, var1, var2, var10, var9);
   }

   private static void torsoExtras(int var0, int var1, float var2, boolean var3) {
      switch (var0) {
         case 0:
            for (int var11 = 0; var11 < 6; var11++) {
               float var12 = var3 ? 0.6F + 2.6F * (float)Math.abs(Math.sin(var11 * 1.7 + var1 * 2.3 + var2 * 3.0F)) * (0.5F + 0.5F * hit) : 0.8F;
               int var6 = mix(16727464, 3073791, var11 / 5.0F);
               box(-2.75F + var11 * 1.1F, 19.5F + var12 / 2.0F, 3.45F, 0.38F, var12 / 2.0F, 0.12F, var6, 3);
            }
            break;
         case 1:
            for (int var10 = 0; var10 < 3; var10++) {
               box(-1.6F + var10 * 1.6F, 23.0F, 3.4F, 0.5F, 0.5F, 0.12F, (var1 + var10) % 3 == 0 ? 16724032 : 3211152, 3);
            }

            box(0.0F, 19.5F, 3.35F, 2.6F, 1.6F, 0.15F, 1711654, 5);
            break;
         case 2:
            box(0.0F, 21.0F, 3.5F, 2.2F, 1.6F, 0.4F, 13159122, 0);

            for (int var9 = 0; var9 < 4; var9++) {
               box(-1.2F + var9 * 0.8F, 21.4F, 3.95F, 0.25F, 0.25F, 0.08F, var9 == (var1 & 3) ? 4259712 : 16732224, 3);
            }

            box(0.0F, 23.0F, -4.0F, 4.2F, 5.0F, 1.4F, 13684952, 0);
         case 3:
         default:
            break;
         case 4:
            for (int var4 = 0; var4 <= 12; var4++) {
               double var5 = Math.PI * (0.12 + 0.76 * var4 / 12.0);
               float var7 = (float)Math.cos(var5) * 3.4F;
               float var8 = 27.4F - (float)Math.sin(var5) * 4.2F;
               ellipsoid(var7, var8, 3.3F, 0.55F, 0.55F, 0.55F, 6, 4, 16762940, 4);
            }

            ellipsoid(0.0F, 22.6F, 3.6F, 1.1F, 1.3F, 0.5F, 8, 6, 16762940, 4);
      }
   }

   private static void head(int var0, float var1, boolean var2, int var3, int var4) {
      switch (var0) {
         case 1:
            ellipsoid(0.0F, 34.0F, 0.3F, 3.9F, 4.1F, 3.7F, 14, 10, 12107980, 2);
            box(0.0F, 34.6F, 3.55F, 3.2F, 0.9F, 0.45F, mix(2152703, 16777215, flash * 0.5F), 3);
            limb(0.0F, 37.8F, 0.2F, 0.4F, 41.2F, 0.2F, 0.22F, 6, 8949920, 2);
            ellipsoid(0.4F, 41.5F, 0.2F, 0.6F, 0.6F, 0.6F, 8, 6, mix(8392720, 16724016, flash), 3);
            headphones(2105380, 13164576);
            break;
         case 2:
            ellipsoid(0.0F, 34.0F, 0.3F, 4.9F, 4.9F, 4.9F, 16, 12, 15790324, 5);
            ellipsoid(0.0F, 34.2F, 2.6F, 3.6F, 3.0F, 2.5F, 14, 10, 15247416, 4);
            limb(0.0F, 29.6F, 0.2F, 0.0F, 30.4F, 0.2F, 3.6F, 14, 14211296, 0);
            break;
         case 3:
            int var5 = mix(2410608, 7143344, flash);
            box(0.0F, 34.2F, 0.3F, 4.1F, 4.1F, 4.1F, var5, 3);
            box(-1.6F, 35.4F, 4.45F, 0.6F, 0.75F, 0.1F, 397320, 0);
            box(1.6F, 35.4F, 4.45F, 0.6F, 0.75F, 0.1F, 397320, 0);
            float var6 = 0.35F + (var2 ? 1.1F * hit : 0.1F);
            box(0.0F, 32.6F - var6 / 2.0F, 4.45F, 1.5F, var6 / 2.0F + 0.1F, 0.1F, 397320, 0);
            headphones(1381658, 16727464);
            break;
         case 4:
            ellipsoid(0.0F, 34.0F, 0.4F, 3.9F, 4.4F, 3.9F, 14, 10, var3, var4);
            cylinder(0.0F, 36.6F, 0.3F, 4.2F, 2.6F, 16, 14207136, 0);
            cylinder(0.0F, 36.3F, 0.3F, 5.8F, 0.35F, 18, 13482896, 0);
            box(0.0F, 34.5F, 3.95F, 3.3F, 0.75F, 0.35F, 657934, 5);
            break;
         default:
            ellipsoid(0.0F, 34.0F, 0.4F, 3.9F, 4.4F, 3.9F, 14, 10, var3, var4);
            ellipsoid(0.0F, 36.4F, 0.2F, 4.1F, 2.4F, 4.1F, 14, 6, 1118486, 0);
            box(0.0F, 36.2F, 3.9F, 3.6F, 0.25F, 1.9F, 1118486, 0);
            box(0.0F, 34.6F, 3.95F, 3.3F, 0.8F, 0.35F, 657934, 5);
            headphones(1118486, 13164576);
      }
   }

   private static void headphones(int var0, int var1) {
      float var2 = -4.4F;
      float var3 = 34.0F;

      for (int var4 = 1; var4 <= 6; var4++) {
         double var5 = Math.PI * var4 / 6.0;
         float var7 = -4.4F * (float)Math.cos(var5);
         float var8 = 34.0F + 4.9F * (float)Math.sin(var5);
         limb(var2, var3, 0.1F, var7, var8, 0.1F, 0.42F, 6, var0, 5);
         var2 = var7;
         var3 = var8;
      }

      ellipsoid(-4.5F, 34.0F, 0.2F, 1.1F, 1.9F, 1.9F, 10, 8, var1, 5);
      ellipsoid(4.5F, 34.0F, 0.2F, 1.1F, 1.9F, 1.9F, 10, 8, var1, 5);
   }

   private static void hands(int var0, float var1, boolean var2, double var3, float[] var5, float[] var6) {
      if (!var2) {
         set(var5, -2.6F, 14.9F, 2.4F);
         set(var6, 12.0F + 0.3F * (float)Math.sin(var3 * 0.8), 14.8F, 1.6F);
      } else {
         int var7 = Math.floorMod(var0, 8);
         boolean var8 = Math.floorMod(var0, 32) >= 28;
         float var9 = (float)Math.exp(-var1 * 5.0F);
         if (!var8 && var7 != 7) {
            if (var7 != 2 && var7 != 3) {
               float var12 = (float)Math.sin(var1 * Math.PI * 4.0);
               set(var6, 12.0F + 1.4F * var12, 14.9F, 1.4F + 0.6F * var12);
            } else {
               float var11 = smooth(1.0F - var1);
               set(var6, 8.5F + 1.5F * var11, 31.0F + 12.0F * var11, 2.6F - 1.2F * var11);
            }

            if (var7 != 4 && var7 != 5) {
               float var13 = (float)Math.sin((var0 + var1) * Math.PI);
               set(var5, -2.6F + 0.6F * var13, 14.9F, 2.4F);
            } else {
               set(var5, -4.7F, 33.2F, 1.2F);
            }
         } else {
            float var10 = (float)Math.sin((var0 + var1) * Math.PI);
            set(var5, -12.5F + 1.5F * var10, 40.5F + 1.2F * var9, 0.8F);
            set(var6, 12.5F + 1.5F * var10, 40.5F + 1.2F * var9, 0.8F);
         }
      }
   }

   private static void arm(float var0, float var1, float var2, float[] var3, int var4, int var5, int var6, int var7) {
      M.identity();
      float var8 = 7.0F;
      float var9 = 6.6F;
      float var10 = var3[0] - var0;
      float var11 = var3[1] - var1;
      float var12 = var3[2] - var2;
      float var13 = (float)Math.sqrt(var10 * var10 + var11 * var11 + var12 * var12);
      float var14 = Math.max(0.5F, Math.min(var13, (var8 + var9) * 0.985F));
      float var15 = var10 / var13;
      float var16 = var11 / var13;
      float var17 = var12 / var13;
      float var18 = (var8 * var8 - var9 * var9 + var14 * var14) / (2.0F * var14);
      float var19 = (float)Math.sqrt(Math.max(0.0F, var8 * var8 - var18 * var18));
      float var20 = var4 * 1.0F;
      float var21 = -0.7F;
      float var22 = -0.35F;
      float var23 = var20 * var15 + var21 * var16 + var22 * var17;
      var20 -= var23 * var15;
      var21 -= var23 * var16;
      var22 -= var23 * var17;
      float var24 = (float)Math.sqrt(var20 * var20 + var21 * var21 + var22 * var22) + 1.0E-6F;
      float var25 = var0 + var15 * var18 + var20 / var24 * var19;
      float var26 = var1 + var16 * var18 + var21 / var24 * var19;
      float var27 = var2 + var17 * var18 + var22 / var24 * var19;
      float var28 = var0 + var15 * var14;
      float var29 = var1 + var16 * var14;
      float var30 = var2 + var17 * var14;
      limb(var0, var1, var2, var25, var26, var27, 1.65F, 10, var5, var7);
      ellipsoid(var25, var26, var27, 1.55F, 1.55F, 1.55F, 8, 6, var5, var7);
      limb(var25, var26, var27, var28, var29, var30, 1.45F, 10, var5, var7);
      ellipsoid(var28, var29, var30, 1.35F, 1.35F, 1.35F, 10, 8, var6, var7 == 2 ? 2 : 1);
   }

   private static void desk(double var0, double var2, boolean var4) {
      M.identity();
      box(0.0F, 7.6F, 1.0F, 24.0F, 5.6F, 3.2F, 1381148, 5);
      box(0.0F, 13.0F, 4.15F, 24.0F, 0.42F, 0.12F, mix(8007935, 13664511, flash), 3);
      box(0.0F, 2.3F, 4.15F, 24.0F, 0.25F, 0.12F, mix(3073791, 16777215, flash * 0.4F), 3);

      for (int var5 = 0; var5 < 3; var5++) {
         for (int var6 = 0; var6 < 18; var6++) {
            float var7 = -21.25F + var6 * 2.5F;
            float var8 = 4.8F + var5 * 2.6F;
            double var9 = Math.sin(var6 * 0.55 - var2 * Math.PI * 0.5 + var5 * 0.8);
            float var11 = var4 ? (float)(0.35 + 0.65 * Math.max(0.0, var9)) * (0.6F + 0.4F * hit) : 0.35F;
            int var12 = mix(4198448, (var6 + var5) % 2 == 0 ? 16727464 : 3073791, var11);
            box(var7, var8, 4.2F, 0.55F, 0.55F, 0.08F, var12, 3);
         }
      }

      for (byte var13 = -1; var13 <= 1; var13 += 2) {
         float var15 = var13 * 13.0F;
         cylinder(var15, 13.5F, 1.2F, 4.4F, 0.5F, 20, 2763314, 5);
         cylinder(var15, 14.05F, 1.2F, 3.8F, 0.12F, 20, 789520, 5);
         cylinder(var15, 14.2F, 1.2F, 1.0F, 0.1F, 12, var13 < 0 ? 16727464 : 3073791, 3);
         double var16 = var0 * (var4 ? 3.5 : 0.6) * var13;
         float var17 = var15 + (float)Math.cos(var16) * 2.6F;
         float var10 = 1.2F + (float)Math.sin(var16) * 2.6F;
         box(var17, 14.22F, var10, 0.35F, 0.06F, 0.35F, 16777215, 3);
         limb(var15 + var13 * 4.0F, 14.3F, -1.6F, var15 + var13 * 1.6F, 14.35F, 2.2F, 0.18F, 6, 12633292, 2);
      }

      box(0.0F, 13.9F, 1.5F, 4.2F, 0.45F, 2.6F, 1842212, 5);

      for (int var14 = 0; var14 < 4; var14++) {
         cylinder(-2.4F + var14 * 1.6F, 14.35F, 2.6F, 0.35F, 0.25F, 8, var14 % 2 == 0 ? 16727464 : 3073791, 3);
      }

      box(0.0F, 13.65F, -0.6F, 3.2F, 0.15F, 2.0F, 3421246, 2);
      box(0.0F, 15.9F, -2.45F, 3.2F, 2.2F, 0.15F, 3421246, 2);
      ellipsoid(0.0F, 16.1F, -2.25F, 0.55F, 0.55F, 0.1F, 8, 4, 16769088, 3);
   }

   private static void crowd(double var0, boolean var2, double var3) {
      byte var5 = 20;

      for (int var6 = 0; var6 < var5; var6++) {
         float var7 = -37.0F + var6 * 3.9F + (float)Math.sin(var6 * 12.9898) * 0.9F;
         float var8 = -1.2F + (float)Math.sin(var6 * 78.233) * 0.7F;
         float var9 = 13.0F + var6 % 3 * 1.5F;
         double var10 = var0 + var6 * 0.13;
         float var12 = var2
            ? 0.9F * (float)Math.max(0.0, Math.sin((var10 - Math.floor(var10)) * Math.PI)) * (var6 % 2 == 0 ? 1.0F : 0.6F)
            : 0.15F * (float)Math.sin(var3 * 1.4 + var6);
         float var13 = var8 + var12;
         M.identity();
         int var14 = 460298;
         ellipsoid(var7, var13 - 1.6F, var9, 1.9F, 2.4F, 1.2F, 8, 6, var14, 0);
         ellipsoid(var7, var13 + 1.3F, var9, 1.3F, 1.4F, 1.3F, 8, 6, var14, 0);
         boolean var15 = var2 ? (var6 + (int)Math.floor(var0 / 2.0)) % 3 != 0 : var6 % 4 == 0;

         for (byte var16 = -1; var16 <= 1; var16 += 2) {
            boolean var17 = var15 && (var16 > 0 || var6 % 2 == 0);
            float var18 = var7 + var16 * (var17 ? 2.0F : 1.8F);
            float var19 = var17 ? var13 + 5.2F + var12 * 0.6F : var13 - 2.6F;
            limb(var7 + var16 * 1.4F, var13 - 0.2F, var9, var18, var19, var9, 0.5F, 6, var14, 0);
            if (var17) {
               ellipsoid(var18, var19 + 0.3F, var9, 0.6F, 0.6F, 0.6F, 6, 4, var14, 0);
            }
         }
      }
   }

   private static void set(float[] var0, float var1, float var2, float var3) {
      var0[0] = var1;
      var0[1] = var2;
      var0[2] = var3;
   }

   private static float smooth(float var0) {
      var0 = Math.max(0.0F, Math.min(1.0F, var0));
      return var0 * var0 * (3.0F - 2.0F * var0);
   }

   private static int mix(int var0, int var1, float var2) {
      var2 = Math.max(0.0F, Math.min(1.0F, var2));
      int var3 = (int)((var0 >> 16 & 0xFF) + ((var1 >> 16 & 0xFF) - (var0 >> 16 & 0xFF)) * var2);
      int var4 = (int)((var0 >> 8 & 0xFF) + ((var1 >> 8 & 0xFF) - (var0 >> 8 & 0xFF)) * var2);
      int var5 = (int)((var0 & 0xFF) + ((var1 & 0xFF) - (var0 & 0xFF)) * var2);
      return var3 << 16 | var4 << 8 | var5;
   }

   private static void ellipsoid(float var0, float var1, float var2, float var3, float var4, float var5, int var6, int var7, int var8, int var9) {
      for (int var10 = 0; var10 < var7; var10++) {
         double var11 = Math.PI * var10 / var7 - (Math.PI / 2);
         double var13 = Math.PI * (var10 + 1) / var7 - (Math.PI / 2);

         for (int var15 = 0; var15 < var6; var15++) {
            double var16 = (Math.PI * 2) * var15 / var6;
            double var18 = (Math.PI * 2) * (var15 + 1) / var6;
            sph(0, var0, var1, var2, var3, var4, var5, var11, var16);
            sph(1, var0, var1, var2, var3, var4, var5, var11, var18);
            sph(2, var0, var1, var2, var3, var4, var5, var13, var18);
            sph(3, var0, var1, var2, var3, var4, var5, var13, var16);
            quad(var8, var9);
         }
      }
   }

   private static void sph(int var0, float var1, float var2, float var3, float var4, float var5, float var6, double var7, double var9) {
      float var11 = (float)Math.cos(var7);
      float var12 = (float)Math.sin(var7);
      float var13 = (float)Math.cos(var9);
      float var14 = (float)Math.sin(var9);
      QP[var0 * 3] = var1 + var4 * var11 * var13;
      QP[var0 * 3 + 1] = var2 + var5 * var12;
      QP[var0 * 3 + 2] = var3 + var6 * var11 * var14;
      float var15 = var11 * var13 / var4;
      float var16 = var12 / var5;
      float var17 = var11 * var14 / var6;
      float var18 = (float)Math.sqrt(var15 * var15 + var16 * var16 + var17 * var17) + 1.0E-9F;
      QN[var0 * 3] = var15 / var18;
      QN[var0 * 3 + 1] = var16 / var18;
      QN[var0 * 3 + 2] = var17 / var18;
   }

   private static void box(float var0, float var1, float var2, float var3, float var4, float var5, int var6, int var7) {
      float[][] var8 = new float[][]{{1.0F, 0.0F, 0.0F}, {-1.0F, 0.0F, 0.0F}, {0.0F, 1.0F, 0.0F}, {0.0F, -1.0F, 0.0F}, {0.0F, 0.0F, 1.0F}, {0.0F, 0.0F, -1.0F}};

      for (float[] var12 : var8) {
         float[] var13 = var12[0] != 0.0F ? new float[]{0.0F, 1.0F, 0.0F} : new float[]{1.0F, 0.0F, 0.0F};
         float[] var14 = new float[]{
            var12[1] * var13[2] - var12[2] * var13[1], var12[2] * var13[0] - var12[0] * var13[2], var12[0] * var13[1] - var12[1] * var13[0]
         };
         float[][] var15 = new float[][]{{-1.0F, -1.0F}, {1.0F, -1.0F}, {1.0F, 1.0F}, {-1.0F, 1.0F}};

         for (int var16 = 0; var16 < 4; var16++) {
            float var17 = var12[0] + var13[0] * var15[var16][0] + var14[0] * var15[var16][1];
            float var18 = var12[1] + var13[1] * var15[var16][0] + var14[1] * var15[var16][1];
            float var19 = var12[2] + var13[2] * var15[var16][0] + var14[2] * var15[var16][1];
            QP[var16 * 3] = var0 + var17 * var3;
            QP[var16 * 3 + 1] = var1 + var18 * var4;
            QP[var16 * 3 + 2] = var2 + var19 * var5;
            QN[var16 * 3] = var12[0];
            QN[var16 * 3 + 1] = var12[1];
            QN[var16 * 3 + 2] = var12[2];
         }

         quad(var6, var7);
      }
   }

   private static void cylinder(float var0, float var1, float var2, float var3, float var4, int var5, int var6, int var7) {
      for (int var8 = 0; var8 < var5; var8++) {
         double var9 = (Math.PI * 2) * var8 / var5;
         double var11 = (Math.PI * 2) * (var8 + 1) / var5;
         float var13 = (float)Math.cos(var9);
         float var14 = (float)Math.sin(var9);
         float var15 = (float)Math.cos(var11);
         float var16 = (float)Math.sin(var11);
         pt(0, var0 + var13 * var3, var1, var2 + var14 * var3, var13, 0.0F, var14);
         pt(1, var0 + var15 * var3, var1, var2 + var16 * var3, var15, 0.0F, var16);
         pt(2, var0 + var15 * var3, var1 + var4, var2 + var16 * var3, var15, 0.0F, var16);
         pt(3, var0 + var13 * var3, var1 + var4, var2 + var14 * var3, var13, 0.0F, var14);
         quad(var6, var7);
         pt(0, var0, var1 + var4, var2, 0.0F, 1.0F, 0.0F);
         pt(1, var0 + var13 * var3, var1 + var4, var2 + var14 * var3, 0.0F, 1.0F, 0.0F);
         pt(2, var0 + var15 * var3, var1 + var4, var2 + var16 * var3, 0.0F, 1.0F, 0.0F);
         pt(3, var0, var1 + var4, var2, 0.0F, 1.0F, 0.0F);
         quad(var6, var7);
      }
   }

   private static void limb(float var0, float var1, float var2, float var3, float var4, float var5, float var6, int var7, int var8, int var9) {
      float var10 = var3 - var0;
      float var11 = var4 - var1;
      float var12 = var5 - var2;
      float var13 = (float)Math.sqrt(var10 * var10 + var11 * var11 + var12 * var12);
      if (!(var13 < 1.0E-4F)) {
         var10 /= var13;
         var11 /= var13;
         var12 /= var13;
         float var14 = Math.abs(var11) < 0.9F ? 0.0F : 1.0F;
         float var15 = Math.abs(var11) < 0.9F ? 1.0F : 0.0F;
         float var16 = 0.0F;
         float var17 = var15 * var12 - var16 * var11;
         float var18 = var16 * var10 - var14 * var12;
         float var19 = var14 * var11 - var15 * var10;
         float var20 = (float)Math.sqrt(var17 * var17 + var18 * var18 + var19 * var19);
         var17 /= var20;
         var18 /= var20;
         var19 /= var20;
         float var21 = var11 * var19 - var12 * var18;
         float var22 = var12 * var17 - var10 * var19;
         float var23 = var10 * var18 - var11 * var17;

         for (int var24 = 0; var24 < var7; var24++) {
            double var25 = (Math.PI * 2) * var24 / var7;
            double var27 = (Math.PI * 2) * (var24 + 1) / var7;
            float var29 = (float)Math.cos(var25);
            float var30 = (float)Math.sin(var25);
            float var31 = (float)Math.cos(var27);
            float var32 = (float)Math.sin(var27);
            float var33 = var17 * var29 + var21 * var30;
            float var34 = var18 * var29 + var22 * var30;
            float var35 = var19 * var29 + var23 * var30;
            float var36 = var17 * var31 + var21 * var32;
            float var37 = var18 * var31 + var22 * var32;
            float var38 = var19 * var31 + var23 * var32;
            pt(0, var0 + var33 * var6, var1 + var34 * var6, var2 + var35 * var6, var33, var34, var35);
            pt(1, var0 + var36 * var6, var1 + var37 * var6, var2 + var38 * var6, var36, var37, var38);
            pt(2, var3 + var36 * var6, var4 + var37 * var6, var5 + var38 * var6, var36, var37, var38);
            pt(3, var3 + var33 * var6, var4 + var34 * var6, var5 + var35 * var6, var33, var34, var35);
            quad(var8, var9);
         }
      }
   }

   private static void pt(int var0, float var1, float var2, float var3, float var4, float var5, float var6) {
      QP[var0 * 3] = var1;
      QP[var0 * 3 + 1] = var2;
      QP[var0 * 3 + 2] = var3;
      QN[var0 * 3] = var4;
      QN[var0 * 3 + 1] = var5;
      QN[var0 * 3 + 2] = var6;
   }

   private static void quad(int var0, int var1) {
      float[] var2 = new float[12];
      float[] var3 = new float[12];

      for (int var4 = 0; var4 < 4; var4++) {
         M.transformPosition(T0.set(QP[var4 * 3], QP[var4 * 3 + 1], QP[var4 * 3 + 2]));
         M.transformDirection(T1.set(QN[var4 * 3], QN[var4 * 3 + 1], QN[var4 * 3 + 2]));
         var2[var4 * 3] = T0.x;
         var2[var4 * 3 + 1] = T0.y;
         var2[var4 * 3 + 2] = T0.z;
         var3[var4 * 3] = T1.x;
         var3[var4 * 3 + 1] = T1.y;
         var3[var4 * 3 + 2] = T1.z;
      }

      float var11 = var3[2] + var3[5] + var3[8] + var3[11];
      if (!(var11 < -2.2F)) {
         float var5 = (var2[4] - var2[1]) * (var2[8] - var2[2]) - (var2[5] - var2[2]) * (var2[7] - var2[1]);
         float var6 = (var2[5] - var2[2]) * (var2[6] - var2[0]) - (var2[3] - var2[0]) * (var2[8] - var2[2]);
         float var7 = (var2[3] - var2[0]) * (var2[7] - var2[1]) - (var2[4] - var2[1]) * (var2[6] - var2[0]);
         boolean var8 = var5 * (var3[0] + var3[3] + var3[6] + var3[9]) + var6 * (var3[1] + var3[4] + var3[7] + var3[10]) + var7 * var11 < 0.0F;

         for (int var9 = 0; var9 < 4; var9++) {
            int var10 = var8 ? 3 - var9 : var9;
            vtx(var2[var10 * 3], var2[var10 * 3 + 1], var2[var10 * 3 + 2], shade(var3[var10 * 3], var3[var10 * 3 + 1], var3[var10 * 3 + 2], var0, var1));
         }
      }
   }

   private static int shade(float var0, float var1, float var2, int var3, int var4) {
      float var5 = (var3 >> 16 & 0xFF) / 255.0F;
      float var6 = (var3 >> 8 & 0xFF) / 255.0F;
      float var7 = (var3 & 0xFF) / 255.0F;
      if (var4 == 3) {
         float var27 = 1.0F + 0.25F * flash;
         return argb(var5 * var27, var6 * var27, var7 * var27);
      }

      float var8 = Math.max(0.0F, var0 * -0.35F + var1 * 0.55F + var2 * 0.76F);
      float var9 = 0.3F + 0.12F * var1;
      float var10 = Math.max(0.0F, var0 * -0.75F + var1 * 0.15F + var2 * -0.64F);
      float var11 = Math.max(0.0F, var0 * 0.75F + var1 * 0.15F + var2 * -0.64F);
      float var12 = (float)Math.pow(1.0F - Math.max(0.0F, var2), 3.0);
      float var13 = 0.8F + 0.5F * flash;
      float var14 = 0.3F * var10 + 0.18F * var11;
      float var15 = 0.24F * var10 * 0.25F + 0.27F * var11;
      float var16 = 0.19800001F * var10 + 0.3F * var11;
      var14 = (var14 + var12 * 0.22F) * var13;
      var15 = (var15 + var12 * 0.2F) * var13;
      var16 = (var16 + var12 * 0.3F) * var13;
      float var17 = -0.35F;
      float var18 = 0.55F;
      float var19 = 1.76F;
      float var20 = (float)Math.sqrt(var17 * var17 + var18 * var18 + var19 * var19);
      float var21 = (float)Math.pow(Math.max(0.0F, (var0 * var17 + var1 * var18 + var2 * var19) / var20), 30.0);
      float var22 = var9 + 0.85F * var8;
      float var23;
      float var24;
      float var25;
      switch (var4) {
         case 1:
            var23 = var5 * (var22 + 0.1F) + var14 * 0.8F + var21 * 0.15F;
            var24 = var6 * (var22 + 0.1F) + var15 * 0.8F + var21 * 0.15F;
            var25 = var7 * (var22 + 0.1F) + var16 * 0.8F + var21 * 0.15F;
            break;
         case 2:
         case 4:
            float var26 = 0.35F + 0.5F * (var1 * 0.5F + 0.5F);
            var23 = var5 * (var26 + 0.4F * var8) + var14 * 1.4F + var21 * 1.2F;
            var24 = var6 * (var26 + 0.4F * var8) + var15 * 1.4F + var21 * 1.2F;
            var25 = var7 * (var26 + 0.4F * var8) + var16 * 1.4F + var21 * 1.2F;
            break;
         case 3:
         default:
            var23 = var5 * var22 + var14;
            var24 = var6 * var22 + var15;
            var25 = var7 * var22 + var16;
            break;
         case 5:
            var23 = var5 * var22 + var14 + var21 * 0.7F;
            var24 = var6 * var22 + var15 + var21 * 0.7F;
            var25 = var7 * var22 + var16 + var21 * 0.7F;
      }

      return argb(var23, var24, var25);
   }

   private static int argb(float var0, float var1, float var2) {
      int var3 = Math.round(alpha * 255.0F);
      return var3 << 24
         | Math.round(Math.min(1.0F, var0) * 255.0F) << 16
         | Math.round(Math.min(1.0F, var1) * 255.0F) << 8
         | Math.round(Math.min(1.0F, var2) * 255.0F);
   }

   private static void vtx(float var0, float var1, float var2, int var3) {
      double var4 = Math.toRadians(180.0F + var0);
      double var6 = Math.toRadians(var1);
      double var8 = D - var2 * K;
      double var10 = Math.cos(var6);
      vc.vertex(base, (float)(-Math.sin(var4) * var10 * var8), (float)(Math.sin(var6) * var8), (float)(Math.cos(var4) * var10 * var8))
         .color(var3)
         .texture(0.5F, 0.5F)
         .overlay(OverlayTexture.DEFAULT_UV)
         .light(15728880)
         .normal(0.0F, 1.0F, 0.0F);
   }
}
