package dev.neonlyrics.cosmetic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import org.joml.Matrix4f;
import org.joml.Vector3f;

final class SportCar {
   static final float FRONT_AXLE = 1.62F;
   static final float REAR_AXLE = -1.51F;
   static final float AXLE_Y = 0.39F;
   static final float WHEEL_X = 0.9F;
   private static final float ZF = 2.56F;
   private static final float ZR = -2.52F;
   private static final float CAB_F = 0.98F;
   private static final float CAB_R = -1.62F;
   private static final float CHAR_Y = 0.7F;
   private static final float ARCH = 0.47F;
   private static final int OPAQUE = 0;
   private static final int GLOW = 1;
   private static final int GLASS = 2;
   private static final int PAINT = -15987440;
   private static final int GLOSS = -16250613;
   private static final int CARBON_A = -14803165;
   private static final int CARBON_B = -15329510;
   private static final int CHROME = -1841171;
   private static final int TIRE = -15329768;
   private static final int WALL = -14737630;
   private static final int DARK = -16316664;
   private static final int WINDOW = -1944971232;
   private static final int CREAM = -1449001;
   private static final int INSERT = -15000803;
   private static final int DASH = -15461354;
   private static final int AMBER = -15846;
   private static final int RED = -4190180;
   private static final int RIM = -15263717;
   private static final int RIM_LIP = -14013392;
   private static final int TIP = -12960446;
   private static final byte M_MATTE = 0;
   private static final byte M_PAINT = 1;
   private static final byte M_CHROME = 2;
   private static final byte M_GLASS = 3;
   private static final byte M_GLOW = 4;
   private static final byte M_CARBON = 5;
   private static final int NL = 8;
   private static final int NU = 6;
   private static final int NC = 6;
   private static final int NK = 10;
   private static final int H = 17;
   private static float[] V;
   private static int[] COL;
   private static byte[] MAT;
   private static final List<float[]> LIGHTS = new ArrayList<>();
   private static final float LX;
   private static final float LY;
   private static final float LZ;
   private static final ArrayList<float[]> BV = new ArrayList<>();
   private static final ArrayList<int[]> BC = new ArrayList<>();

   private SportCar() {
   }

   static void draw(VertexConsumer var0, boolean var1, Matrix4f var2, Matrix4f var3, int var4, List<float[]> var5, Matrix4f var6) {
      if (CarModel.ready()) {
         CarModel.draw(var0, var1, var2, var3, var4, var5, var6);
      } else {
         if (V == null) {
            build();
         }

         float var7 = 0.32F + 0.68F * Math.max(var4 >> 4 & 15, var4 >> 20 & 15) / 15.0F;
         Vector3f var8 = new Vector3f();
         Vector3f var9 = new Vector3f();
         float[] var10 = new float[16];

         for (int var11 = 0; var11 < COL.length; var11++) {
            byte var12 = MAT[var11];
            if (var12 == 3 == var1) {
               int var13 = COL[var11];
               float var14 = (var13 >> 16 & 0xFF) / 255.0F;
               float var15 = (var13 >> 8 & 0xFF) / 255.0F;
               float var16 = (var13 & 0xFF) / 255.0F;
               int var17 = var11 * 24;

               for (int var18 = 0; var18 < 4; var18++) {
                  int var19 = var17 + var18 * 6;
                  var3.transformPosition(var8.set(V[var19], V[var19 + 1], V[var19 + 2]));
                  var3.transformDirection(var9.set(V[var19 + 3], V[var19 + 4], V[var19 + 5])).normalize();
                  shade(var12, var14, var15, var16, var9, var8, var7, var10, var18 * 4);
               }

               for (int var25 = 0; var25 < 2; var25++) {
                  for (int var26 = 0; var26 < 4; var26++) {
                     int var20 = var25 == 0 ? var26 : 3 - var26;
                     int var21 = var17 + var20 * 6;
                     var0.vertex(var2, V[var21], V[var21 + 1], V[var21 + 2])
                        .color(var10[var20 * 4], var10[var20 * 4 + 1], var10[var20 * 4 + 2], var10[var20 * 4 + 3])
                        .texture(0.5F, 0.5F)
                        .overlay(OverlayTexture.DEFAULT_UV)
                        .light(15728880)
                        .normal(0.0F, 1.0F, 0.0F);
                  }
               }
            }
         }

         if (!var1) {
            for (float[] var23 : LIGHTS) {
               Vector3f var24 = var6.transformPosition(new Vector3f(var23[0], var23[1], var23[2]));
               var5.add(new float[]{var24.x, var24.y, var24.z, var23[3], var23[4], var23[5]});
            }
         }
      }
   }

   static void shade(byte var0, float var1, float var2, float var3, Vector3f var4, Vector3f var5, float var6, float[] var7, int var8) {
      if (var0 == 4) {
         var7[var8] = var1;
         var7[var8 + 1] = var2;
         var7[var8 + 2] = var3;
         var7[var8 + 3] = 1.0F;
      } else {
         float var9 = 1.0F / Math.max(1.0E-4F, var5.length());
         float var10 = -var5.x * var9;
         float var11 = -var5.y * var9;
         float var12 = -var5.z * var9;
         float var13 = var4.x * var10 + var4.y * var11 + var4.z * var12;
         if (var13 < 0.0F) {
            var4.negate();
            var13 = -var13;
         }

         float var14 = 2.0F * var13 * var4.x - var10;
         float var15 = 2.0F * var13 * var4.y - var11;
         float var16 = 2.0F * var13 * var4.z - var12;
         float var17 = Math.max(0.0F, var4.x * LX + var4.y * LY + var4.z * LZ);
         float var18 = 0.28F + 0.22F * (var4.y * 0.5F + 0.5F) + 0.6F * var17;
         float var19;
         float var20;
         float var21;
         if (var15 >= 0.0F) {
            float var22 = (float)Math.sqrt(var15);
            var19 = 0.95F - 0.55F * var22;
            var20 = 0.96F - 0.44F * var22;
            var21 = 0.98F - 0.26F * var22;
         } else {
            float var36 = Math.min(1.0F, -var15 * 1.6F);
            var36 = var36 * var36 * (3.0F - 2.0F * var36);
            var19 = 0.3F - 0.23F * var36;
            var20 = 0.3F - 0.225F * var36;
            var21 = 0.31F - 0.23F * var36;
         }

         float var38 = (var15 - 0.32F) / 0.16F;
         float var23 = 0.45F * (float)Math.exp(-var38 * var38);
         var19 += var23;
         var20 += var23;
         var21 += var23;
         float var24 = Math.max(0.0F, var14 * LX + var15 * LY + var16 * LZ);
         var24 *= var24;
         var24 *= var24;
         var24 *= var24;
         float var25 = var24;
         var24 = var25 * var25 * var25 * var25 * var25 * 0.55F;
         float var26 = 1.0F - var13;
         float var27 = var26 * var26;
         var27 *= var27 * var26;
         float var31 = 1.0F;
         float var28;
         float var29;
         float var30;
         switch (var0) {
            case 1:
               float var47 = 0.12F + 0.88F * var27;
               var28 = var1 * var18 * 0.6F + var19 * var47 + var24;
               var29 = var2 * var18 * 0.6F + var20 * var47 + var24;
               var30 = var3 * var18 * 0.6F + var21 * var47 + var24;
               break;
            case 2:
               float var46 = 0.75F + 0.25F * var27;
               var28 = var19 * var46 * var1 + var24 * 1.5F;
               var29 = var20 * var46 * var2 + var24 * 1.5F;
               var30 = var21 * var46 * var3 + var24 * 1.5F;
               break;
            case 3:
               float var45 = 0.08F + 0.92F * var27;
               var28 = 0.015F * (1.0F - var45) + var19 * var45 * 0.9F + var24 * 0.8F;
               var29 = 0.017F * (1.0F - var45) + var20 * var45 * 0.9F + var24 * 0.8F;
               var30 = 0.021F * (1.0F - var45) + var21 * var45 * 0.9F + var24 * 0.8F;
               var31 = Math.min(1.0F, 0.84F + 0.16F * var45);
               break;
            case 4:
            default:
               float var44 = 0.03F + 0.15F * var27;
               var28 = var1 * var18 + var19 * var44;
               var29 = var2 * var18 + var20 * var44;
               var30 = var3 * var18 + var21 * var44;
               break;
            case 5:
               float var32 = (0.05F + 0.6F * var27) * 0.8F;
               var28 = var1 * var18 + var19 * var32 + var24 * 0.5F;
               var29 = var2 * var18 + var20 * var32 + var24 * 0.5F;
               var30 = var3 * var18 + var21 * var32 + var24 * 0.5F;
         }

         var7[var8] = Math.min(1.0F, var28 * var6);
         var7[var8 + 1] = Math.min(1.0F, var29 * var6);
         var7[var8 + 2] = Math.min(1.0F, var30 * var6);
         var7[var8 + 3] = var31;
      }
   }

   private static byte material(int var0, int var1) {
      if (var1 == 1) {
         return 4;
      } else if (var1 == 2) {
         return 3;
      } else if (var0 == -15987440
         || var0 == -16250613
         || var0 == -15263717
         || var0 == -14013392
         || var0 == -12960446
         || var0 == -14737372
         || var0 == -14276564
         || var0 == -13881805) {
         return 1;
      } else if (var0 == -1841171 || var0 == -6511958 || var0 == -3880496 || var0 == -9536368) {
         return 2;
      } else {
         return (byte)(var0 != -14803165 && var0 != -15329510 ? 0 : 5);
      }
   }

   private static float top(float var0) {
      if (var0 >= 0.98F) {
         float var2 = (var0 - 0.98F) / 1.5799999F;
         return 1.03F - 0.12F * var2 - 0.1F * pow(var2, 6.0F);
      }

      if (var0 >= -1.62F) {
         return 1.03F;
      }

      float var1 = (-1.62F - var0) / 0.9F;
      return 1.03F + 0.02F * (float)Math.sin(Math.PI * var1 * 0.8) - 0.12F * pow(var1, 5.0F);
   }

   private static float bottom(float var0) {
      float var1 = var0 > 2.2F ? 0.2F - 0.04F * (var0 - 2.2F) / 0.3599999F : 0.2F + 0.02F * clamp((-2.2F - var0) / 0.3F);

      for (float var5 : new float[]{1.62F, -1.51F}) {
         float var6 = var0 - var5;
         if (Math.abs(var6) < 0.47F) {
            var1 = Math.max(var1, 0.39F + (float)Math.sqrt(0.2209F - var6 * var6));
         }
      }

      return var1;
   }

   private static float hw(float var0) {
      float var1 = 1.0F + 0.06F * (gauss((var0 - 1.62F) / 0.48F) + gauss((var0 - -1.51F) / 0.5F));
      if (var0 > 2.1F) {
         var1 -= 0.12F * sq((var0 - 2.1F) / 0.46000004F);
      }

      if (var0 < -2.1F) {
         var1 -= 0.1F * sq((-2.1F - var0) / 0.42000008F);
      }

      return var1;
   }

   private static float gF(float var0) {
      return pow(clamp((var0 - 0.45F) / 0.4F), 1.5F);
   }

   private static float gR(float var0) {
      return pow(clamp((var0 - 0.5F) / 0.4F), 1.5F);
   }

   private static float zAt(float var0, float var1, float var2) {
      float var3 = clamp((var0 - 1.9F) / 0.65999997F);
      float var4 = clamp((-1.9F - var0) / 0.62F);
      return var0 - var3 * (0.24F * var1 * var1 + 0.12F * gF(var2)) + var4 * (0.16F * var1 * var1 + 0.06F * gR(var2));
   }

   private static float frontZ(float var0, float var1) {
      return 2.56F - 0.24F * var0 * var0 - 0.12F * gF(var1);
   }

   private static float rearZ(float var0, float var1) {
      return -2.52F + 0.16F * var0 * var0 + 0.06F * gR(var1);
   }

   private static float sideX(float var0, float var1) {
      float var2 = (var1 - 0.55F) / 0.4F;
      float var3 = hw(var0) * (1.0F - 0.035F * var2 * var2);
      float var4 = top(var0) - 0.09F;
      if (var1 > 0.7F) {
         var3 -= 0.05F * pow(clamp((var1 - 0.7F) / Math.max(0.01F, var4 - 0.7F)), 1.4F);
      }

      return var3;
   }

   private static float[][] lowerHalf(float var0) {
      float var1 = bottom(var0);
      float var2 = Math.max(var1, 0.7F);
      float[][] var3 = new float[9][];

      for (int var4 = 0; var4 <= 8; var4++) {
         float var5 = var1 + (var2 - var1) * var4 / 8.0F;
         var3[var4] = new float[]{sideX(var0, var5), var5};
      }

      return var3;
   }

   private static float[][] upperHalf(float var0) {
      float var1 = top(var0) - 0.09F;
      float var2 = Math.min(var1, Math.max(bottom(var0), 0.7F));
      float[][] var3 = new float[7][];

      for (int var4 = 0; var4 <= 6; var4++) {
         float var5 = var2 + (var1 - var2) * var4 / 6.0F;
         var3[var4] = new float[]{sideX(var0, var5), var5};
      }

      return var3;
   }

   private static float[][] topHalf(float var0) {
      float var1 = top(var0);
      float var2 = var1 - 0.09F;
      float var3 = sideX(var0, var2);
      float var4 = var3 - 0.1F;
      float[][] var5 = new float[17][];

      for (int var6 = 0; var6 <= 6; var6++) {
         double var7 = var6 / 6.0 * Math.PI / 2.0;
         var5[var6] = new float[]{var3 - 0.1F * (1.0F - (float)Math.cos(var7)), var2 + 0.09F * (float)Math.sin(var7)};
      }

      for (int var9 = 1; var9 <= 10; var9++) {
         float var10 = var4 * (1.0F - var9 / 10.0F);
         var5[6 + var9] = new float[]{var10, var1 + crown(var0, var10, var4)};
      }

      return var5;
   }

   private static float crown(float var0, float var1, float var2) {
      float var3 = var1 / Math.max(0.01F, var2);
      float var4 = 0.025F * (1.0F - var3 * var3);
      if (var0 > 0.98F) {
         float var5 = clamp((var0 - 0.98F) / 0.25F) * (1.0F - clamp((var0 - 2.3F) / 0.2F));
         float var6 = clamp((var0 - 0.98F) / 1.3199999F);
         float var7 = 0.36F - 0.16F * var6;
         var4 += 0.055F * var5 * gauss((Math.abs(var1) - var7) / 0.1F);
         var4 -= 0.012F * var5 * gauss(var1 / 0.06F);
      }

      return var4;
   }

   private static float topY(float var0, float var1) {
      float[][] var2 = topHalf(var0);
      float var3 = var2[6][0];
      return top(var0) + crown(var0, Math.min(Math.abs(var1), var3), var3);
   }

   private static float roofY(float var0) {
      if (var0 > 0.05F) {
         float var2 = (0.98F - var0) / 0.93F;
         return 1.03F + 0.58F * (1.0F - pow(1.0F - var2, 1.3F));
      }

      if (var0 > -0.9F) {
         return 1.61F;
      }

      float var1 = (-0.9F - var0) / 0.72F;
      return 1.61F - 0.58F * pow(var1, 1.25F);
   }

   private static synchronized void build() {
      if (V == null) {
         body();
         greenhouse();
         front();
         rear();
         sides();
         interior();

         for (float var3 : new float[]{1.62F, -1.51F}) {
            for (byte var4 = -1; var4 <= 1; var4 += 2) {
               wheel(var3, var4);
            }
         }

         float[] var5 = new float[BV.size() * 24];
         int[] var6 = new int[BV.size()];
         byte[] var7 = new byte[BV.size()];

         for (int var8 = 0; var8 < BV.size(); var8++) {
            System.arraycopy(BV.get(var8), 0, var5, var8 * 24, 24);
            var6[var8] = BC.get(var8)[0];
            var7[var8] = material(var6[var8], BC.get(var8)[1]);
         }

         BV.clear();
         BC.clear();
         COL = var6;
         MAT = var7;
         V = var5;
      }
   }

   private static float[] stations() {
      ArrayList var0 = new ArrayList();
      range(var0, -2.52F, -1.62F, 18);
      range(var0, -1.62F, 0.98F, 40);
      range(var0, 0.98F, 2.56F, 26);
      var0.add(2.56F);
      float[] var1 = new float[var0.size()];

      for (int var2 = 0; var2 < var1.length; var2++) {
         var1[var2] = (Float)var0.get(var2);
      }

      return var1;
   }

   private static void range(List<Float> var0, float var1, float var2, int var3) {
      for (int var4 = 0; var4 < var3; var4++) {
         var0.add(var1 + (var2 - var1) * var4 / var3);
      }
   }

   private static float[] at(float var0, float var1, float var2) {
      return new float[]{var1, var2, zAt(var0, var1, var2)};
   }

   private static void body() {
      float[] var0 = stations();
      int var1 = var0.length;
      SportCar.Ref var2 = (var0x, var1x, var2x) -> new float[]{0.0F, 0.55F, var2x[2]};

      for (byte var3 = -1; var3 <= 1; var3 += 2) {
         float[][][] var4 = new float[var1][9][];
         float[][][] var5 = new float[var1][7][];

         for (int var6 = 0; var6 < var1; var6++) {
            float[][] var7 = lowerHalf(var0[var6]);
            float[][] var8 = upperHalf(var0[var6]);

            for (int var9 = 0; var9 <= 8; var9++) {
               var4[var6][var9] = at(var0[var6], var3 * var7[var9][0], var7[var9][1]);
            }

            for (int var27 = 0; var27 <= 6; var27++) {
               var5[var6][var27] = at(var0[var6], var3 * var8[var27][0], var8[var27][1]);
            }
         }

         grid(var4, (var0x, var1x) -> c(-15987440), var2);
         grid(var5, (var0x, var1x) -> c(-15987440), var2);
      }

      byte var17 = 33;
      float[][][] var18 = new float[var1][var17][];

      for (int var19 = 0; var19 < var1; var19++) {
         float[][] var21 = topHalf(var0[var19]);

         for (int var23 = 0; var23 < 17; var23++) {
            var18[var19][var23] = at(var0[var19], var21[var23][0], var21[var23][1]);
         }

         for (int var24 = 17; var24 < var17; var24++) {
            var18[var19][var24] = at(var0[var19], -var21[var17 - 1 - var24][0], var21[var17 - 1 - var24][1]);
         }
      }

      grid(var18, (var2x, var3x) -> {
         float var4x = (var0[var2x] + var0[var2x + 1]) / 2.0F;
         boolean var5x = var3x >= 6 && var3x < var17 - 1 - 6;
         return var5x && var4x > -1.62F && var4x < 0.98F ? 0L : c(-15987440);
      }, var2);
      cap(true);
      cap(false);
      float var20 = sideX(0.0F, 0.2F) - 0.02F;
      flat(v(-0.72F, 0.19F, -2.35F), v(0.72F, 0.19F, -2.35F), v(0.72F, 0.19F, 2.4F), v(-0.72F, 0.19F, 2.4F), v(0.0F, -1.0F, 0.0F), -16316664, 0);
      flat(v(-var20, 0.192F, -1.04F), v(var20, 0.192F, -1.04F), v(var20, 0.192F, 1.15F), v(-var20, 0.192F, 1.15F), v(0.0F, -1.0F, 0.0F), -16316664, 0);

      for (float var28 : new float[]{1.62F, -1.51F}) {
         for (byte var10 = -1; var10 <= 1; var10 += 2) {
            float[][][] var11 = new float[21][2][];

            for (int var12 = 0; var12 <= 20; var12++) {
               double var13 = Math.PI * var12 / 20.0;
               float var15 = 0.39F + 0.47F * (float)Math.sin(var13);
               float var16 = var28 + 0.47F * (float)Math.cos(var13);
               var11[var12][0] = at(var16, var10 * (sideX(var16, var15) - 0.002F), var15);
               var11[var12][1] = at(var16, var10 * 0.6F, var15);
            }

            float var29 = var28;
            grid(
               var11,
               (var0x, var1x) -> c(-16316664),
               (var1x, var2x, var3x) -> new float[]{var3x[0], 0.39F + 3.0F * (var3x[1] - 0.39F), var29 + 3.0F * (var3x[2] - var29)}
            );
         }
      }
   }

   private static void cap(boolean var0) {
      float var1 = var0 ? 2.56F : -2.52F;
      float[][] var2 = lowerHalf(var1);
      float[][] var3 = upperHalf(var1);
      float[][] var4 = topHalf(var1);
      ArrayList var5 = new ArrayList();

      for (int var6 = 0; var6 <= 8; var6++) {
         var5.add(var2[var6]);
      }

      for (int var17 = 1; var17 <= 6; var17++) {
         var5.add(var3[var17]);
      }

      for (int var18 = 1; var18 < 17; var18++) {
         var5.add(var4[var18]);
      }

      for (int var19 = 15; var19 >= 0; var19--) {
         var5.add(new float[]{-var4[var19][0], var4[var19][1]});
      }

      for (int var20 = 5; var20 >= 0; var20--) {
         var5.add(new float[]{-var3[var20][0], var3[var20][1]});
      }

      for (int var21 = 7; var21 >= 0; var21--) {
         var5.add(new float[]{-var2[var21][0], var2[var21][1]});
      }

      float var22 = var2[0][0];
      float var7 = var2[0][1];

      for (int var8 = 1; var8 < 10; var8++) {
         var5.add(new float[]{-var22 + 2.0F * var22 * var8 / 10.0F, var7});
      }

      var5.add(var2[0]);
      float var23 = (var7 + var4[16][1]) / 2.0F;
      byte var9 = 6;
      int var10 = var5.size();
      float[][][] var11 = new float[var9 + 1][var10][];

      for (int var12 = 0; var12 <= var9; var12++) {
         float var13 = 1.0F - (float)var12 / var9;

         for (int var14 = 0; var14 < var10; var14++) {
            float var15 = ((float[])var5.get(var14))[0] * var13;
            float var16 = var23 + (((float[])var5.get(var14))[1] - var23) * var13;
            var11[var12][var14] = v(var15, var16, var0 ? frontZ(var15, var16) : rearZ(var15, var16));
         }
      }

      grid(var11, (var0x, var1x) -> c(-15987440), (var2x, var3x, var4x) -> new float[]{0.0F, var23, var4x[2] + (var0 ? -2 : 2)});
   }

   private static void greenhouse() {
      byte var0 = 56;
      float[][][] var1 = new float[var0 + 1][25][];
      float[] var2 = new float[var0 + 1];

      for (int var3 = 0; var3 <= var0; var3++) {
         float var4 = 0.98F + -2.6F * var3 / var0;
         var2[var3] = var4;
         float var5 = roofY(var4);
         float var6 = var5 - 1.03F;
         float var7 = var6 / 0.58F;
         float var8 = sideX(var4, top(var4) - 0.09F) - 0.1F;
         float var9 = var8 - 0.2F * var7;
         float[][] var10 = new float[13][];
         float[] var11 = new float[]{0.0F, 0.06F, 0.4F, 0.75F, 1.0F};
         float var12 = var6 - 0.05F * var7;
         float var13 = var9 + 0.04F * var7;

         for (int var14 = 0; var14 < 5; var14++) {
            var10[var14] = new float[]{
               var8 + (var13 - var8) * var11[var14] + 0.015F * var7 * (float)Math.sin(Math.PI * var11[var14]), 1.03F + var12 * var11[var14]
            };
         }

         float var19 = var9 - 0.02F * var7;
         var10[5] = new float[]{var9 + 0.01F * var7, var5 - 0.015F * var7};

         for (int var15 = 0; var15 <= 6; var15++) {
            float var16 = var19 * (1.0F - var15 / 6.0F);
            float var17 = var16 / Math.max(0.01F, var8);
            float var18 = var16 / Math.max(0.01F, var19);
            var10[6 + var15] = new float[]{var16, var5 + 0.025F * (1.0F - var17 * var17) + 0.015F * var7 * (1.0F - var18 * var18)};
         }

         for (int var20 = 0; var20 < 13; var20++) {
            var1[var3][var20] = v(var10[var20][0], var10[var20][1], var4);
         }

         for (int var21 = 13; var21 < 25; var21++) {
            var1[var3][var21] = v(-var10[24 - var21][0], var10[24 - var21][1], var4);
         }
      }

      grid(var1, (var1x, var2x) -> {
         float var3x = (var2[var1x] + var2[var1x + 1]) / 2.0F;
         if (var2x == 0 || var2x == 23) {
            return c(-16250613);
         }

         if (var2x < 4 || var2x >= 20) {
            boolean var4x = var3x > -0.22F && var3x < -0.1F;
            boolean var5x = var3x > -1.12F && var3x < 0.84F && !var4x;
            if (!var5x) {
               return c(var3x < -1.12F ? -15987440 : -16250613);
            } else {
               return var2x < 4 && var3x > -0.1F ? 0L : c(-1944971232, 2);
            }
         } else if (var2x >= 6 && var2x < 18) {
            return var3x > -0.92F && var3x < 0.07F ? c((var1x & 1) == 0 ? -14803165 : -15329510) : c(-1944971232, 2);
         } else {
            return c(-16250613);
         }
      }, (var0x, var1x, var2x) -> new float[]{0.0F, 1.0F, var2x[2]});
   }

   private static void front() {
      for (byte var0 = -1; var0 <= 1; var0 += 2) {
         List var1 = kidney(var0, 0.0F);
         List var2 = kidney(var0, 0.03F);
         band(var1, var1, 0.0F, 0.05F, true, -14737372);
         band(var1, var2, 0.05F, 0.05F, true, -14276564);
         shape(var2, 0.012F, true, -16316664, 0);

         for (float var6 : new float[]{0.4F, 0.53F, 0.66F}) {
            float var7 = kidneyX(var6, 0.03F);
            patch(Math.min(var0 * 0.065F, var0 * var7), var6, Math.max(var0 * 0.065F, var0 * var7), var6 + 0.022F, 0.035F, true, -13881805, 0);
         }

         shape(poly(var0, 0.4F, 0.655F, 0.85F, 0.735F, 0.85F, 0.797F, 0.44F, 0.716F), 0.012F, true, -15723752, 0);
         shape(poly(var0, 0.47F, 0.7F, 0.81F, 0.762F, 0.81F, 0.777F, 0.47F, 0.715F), 0.02F, true, -15846, 1);
         shape(poly(var0, 0.47F, 0.672F, 0.486F, 0.675F, 0.486F, 0.708F, 0.47F, 0.705F), 0.02F, true, -15846, 1);
         shape(poly(var0, 0.58F, 0.7F, 0.8F, 0.74F, 0.8F, 0.753F, 0.58F, 0.713F), 0.02F, true, -15846, 1);
         shape(poly(var0, 0.58F, 0.685F, 0.594F, 0.688F, 0.594F, 0.708F, 0.58F, 0.705F), 0.02F, true, -15846, 1);
         LIGHTS.add(new float[]{var0 * 0.63F, 0.73F, frontZ(0.63F, 0.73F) + 0.06F, 0.42F, Float.intBitsToFloat(16761370), 0.45F});
         shape(poly(var0, 0.42F, 0.21F, 0.9F, 0.24F, 0.88F, 0.58F, 0.62F, 0.61F, 0.42F, 0.42F), 0.008F, true, -16316664, 0);

         for (float var22 : new float[]{0.52F, 0.64F, 0.76F}) {
            shape(poly(var0, var22, 0.24F, var22 + 0.016F, 0.24F, var22 + 0.07F, 0.55F, var22 + 0.054F, 0.55F), 0.022F, true, -14803165, 0);
         }

         for (float var23 : new float[]{0.28F, 0.38F}) {
            float[] var25 = v(var0 * 0.9F, var23, frontZ(0.9F, var23) + 0.03F);
            obox(var25, v(0.08F, 0.0F, 0.0F), v(0.0F, 0.006F, 0.0F), v(0.0F, 0.0F, 0.06F), -14803165, 0);
         }

         hoodPatch(1.2F, 1.52F, var0 * 0.13F, var0 * 0.43F, 0.004F, -16316664);

         for (int var13 = 0; var13 < 4; var13++) {
            float var17 = 1.25F + var13 * 0.075F;
            hoodPatch(var17, var17 + 0.018F, var0 * 0.13F, var0 * 0.43F, 0.012F, -14803165);
         }
      }

      byte var8 = 24;

      for (int var9 = 0; var9 < var8; var9++) {
         float var10 = -1.0F + 2.0F * var9 / var8;
         float var14 = -1.0F + 2.0F * (var9 + 1) / var8;
         float var18 = frontZ(var10, 0.17F) - 0.01F;
         float var21 = frontZ(var14, 0.17F) - 0.01F;
         float var24 = 0.13F;
         int var26 = (var9 & 1) == 0 ? -14803165 : -15329510;
         flat(v(var10, 0.187F, var18), v(var14, 0.187F, var21), v(var14, 0.187F, var21 + var24), v(var10, 0.187F, var18 + var24), v(0.0F, 1.0F, 0.0F), var26, 0);
         flat(
            v(var10, 0.187F, var18 + var24),
            v(var14, 0.187F, var21 + var24),
            v(var14, 0.158F, var21 + var24 + 0.01F),
            v(var10, 0.158F, var18 + var24 + 0.01F),
            v(0.0F, 0.0F, 1.0F),
            var26,
            0
         );
         flat(
            v(var10, 0.158F, var18),
            v(var14, 0.158F, var21),
            v(var14, 0.158F, var21 + var24 + 0.01F),
            v(var10, 0.158F, var18 + var24 + 0.01F),
            v(0.0F, -1.0F, 0.0F),
            -15329510,
            0
         );
      }
   }

   private static float kidneyX(float var0, float var1) {
      float var2 = clamp((0.8F - var0) / 0.53F);
      return 0.3F + 0.08F * var2 - var1;
   }

   private static List<float[]> kidney(int var0, float var1) {
      List<float[]> var2 = roundRect(0.035F + var1, 0.27F + var1, 0.3F - var1, 0.8F - var1, 0.07F - var1 * 0.7F);
      ArrayList var3 = new ArrayList();

      for (float[] var5 : var2) {
         float var6 = clamp((0.8F - var5[1]) / 0.53F);
         float var7 = 0.035F + var1 + (var5[0] - 0.035F - var1) * (1.0F + 0.3F * var6);
         var3.add(new float[]{var0 * var7, var5[1]});
      }

      if (var0 < 0) {
         Collections.reverse(var3);
      }

      return var3;
   }

   private static void hoodPatch(float var0, float var1, float var2, float var3, float var4, int var5) {
      byte var6 = 4;
      byte var7 = 5;
      float[][][] var8 = new float[var6 + 1][var7 + 1][];

      for (int var9 = 0; var9 <= var6; var9++) {
         for (int var10 = 0; var10 <= var7; var10++) {
            float var11 = var0 + (var1 - var0) * var9 / var6;
            float var12 = var2 + (var3 - var2) * var10 / var7;
            var8[var9][var10] = at(var11, var12, topY(var11, var12) + var4);
         }
      }

      grid(var8, (var1x, var2x) -> c(var5), (var0x, var1x, var2x) -> new float[]{var2x[0], var2x[1] - 1.0F, var2x[2]});
   }

   private static void rear() {
      for (byte var0 = -1; var0 <= 1; var0 += 2) {
         shape(poly(var0, 0.34F, 0.74F, 0.82F, 0.72F, 0.82F, 0.8F, 0.38F, 0.84F), 0.006F, false, -15072250, 0);
         shape(poly(var0, 0.4F, 0.805F, 0.79F, 0.776F, 0.79F, 0.792F, 0.4F, 0.821F), 0.014F, false, -4190180, 1);
         shape(poly(var0, 0.772F, 0.728F, 0.79F, 0.728F, 0.79F, 0.785F, 0.772F, 0.785F), 0.014F, false, -4190180, 1);
         shape(poly(var0, 0.48F, 0.765F, 0.7F, 0.752F, 0.7F, 0.762F, 0.48F, 0.775F), 0.012F, false, -8779246, 1);
         LIGHTS.add(new float[]{var0 * 0.6F, 0.79F, rearZ(0.6F, 0.79F) - 0.06F, 0.32F, Float.intBitsToFloat(16720438), 0.35F});
         shape(poly(var0, 0.8F, 0.28F, 0.88F, 0.28F, 0.88F, 0.52F, 0.8F, 0.5F), 0.008F, false, -16316664, 0);

         for (float var4 : new float[]{0.4F, 0.54F}) {
            exhaust(var0 * var4, 0.27F);
         }

         for (float var18 : new float[]{0.1F, 0.24F, 0.66F}) {
            float[] var5 = v(var0 * var18, 0.3F, rearZ(var18, 0.3F) - 0.06F);
            obox(var5, v(0.007F, 0.0F, 0.0F), v(0.0F, 0.11F, 0.0F), v(0.0F, 0.0F, 0.065F), -14803165, 0);
         }
      }

      patch(-0.76F, 0.19F, 0.76F, 0.41F, 0.01F, false, -15329510, 0);
      byte var11 = 16;

      for (int var13 = 0; var13 < var11; var13++) {
         float var15 = -0.82F + 1.64F * var13 / var11;
         float var17 = -0.82F + 1.64F * (var13 + 1) / var11;
         float[] var19 = at(-2.22F, var15, topY(-2.22F, var15));
         float[] var20 = at(-2.22F, var17, topY(-2.22F, var17));
         float[] var6 = at(-2.5F, var15, topY(-2.4F, var15) + 0.1F);
         float[] var7 = at(-2.5F, var17, topY(-2.4F, var17) + 0.1F);
         float[] var8 = at(-2.44F, var15, topY(-2.44F, var15) - 0.01F);
         float[] var9 = at(-2.44F, var17, topY(-2.44F, var17) - 0.01F);
         int var10 = (var13 & 1) == 0 ? -14803165 : -15329510;
         flat(var19, var20, var7, var6, v(0.0F, 1.0F, -0.3F), var10, 0);
         flat(var6, var7, var9, var8, v(0.0F, 0.0F, -1.0F), var10, 0);
      }
   }

   private static void exhaust(float var0, float var1) {
      float var2 = rearZ(Math.abs(var0), var1) + 0.03F;
      float var3 = rearZ(Math.abs(var0), var1) - 0.075F;
      byte var4 = 16;
      float[][][] var5 = new float[var4 + 1][2][];
      float[][][] var6 = new float[var4 + 1][2][];

      for (int var7 = 0; var7 <= var4; var7++) {
         double var8 = (Math.PI * 2) * var7 / var4;
         float var10 = (float)Math.cos(var8);
         float var11 = (float)Math.sin(var8);
         var5[var7][0] = v(var0 + 0.046F * var10, var1 + 0.046F * var11, var2);
         var5[var7][1] = v(var0 + 0.046F * var10, var1 + 0.046F * var11, var3);
         var6[var7][0] = v(var0 + 0.038F * var10, var1 + 0.038F * var11, var3);
         var6[var7][1] = v(var0 + 0.038F * var10, var1 + 0.038F * var11, var3 + 0.06F);
      }

      grid(var5, (var0x, var1x) -> c(-12960446), (var2x, var3x, var4x) -> new float[]{var0, var1, var4x[2]});
      grid(
         var6, (var0x, var1x) -> c(-16119285), (var2x, var3x, var4x) -> new float[]{var0 + 3.0F * (var4x[0] - var0), var1 + 3.0F * (var4x[1] - var1), var4x[2]}
      );

      for (int var12 = 0; var12 < var4; var12++) {
         float[] var13 = var5[var12][1];
         float[] var9 = var5[var12 + 1][1];
         float[] var14 = var6[var12][0];
         float[] var15 = var6[var12 + 1][0];
         flat(var13, var9, var15, var14, v(0.0F, 0.0F, -1.0F), -12960446, 0);
      }
   }

   private static void sides() {
      for (byte var0 = -1; var0 <= 1; var0 += 2) {
         obox(v(var0 * 1.1F, 1.09F, 0.8F), v(0.09F, 0.0F, 0.0F), v(0.0F, 0.055F, 0.0F), v(0.0F, 0.0F, 0.06F), -14803165, 0);
         obox(v(var0 * 0.96F, 1.045F, 0.8F), v(0.06F, 0.0F, 0.0F), v(0.0F, 0.015F, 0.0F), v(0.0F, 0.0F, 0.035F), -16250613, 0);
         flat(
            v(var0 * 1.03F, 1.05F, 0.739F),
            v(var0 * 1.17F, 1.05F, 0.739F),
            v(var0 * 1.17F, 1.13F, 0.739F),
            v(var0 * 1.03F, 1.13F, 0.739F),
            v(0.0F, 0.0F, -1.0F),
            -9536368,
            0
         );

         for (float var4 : new float[]{0.95F, -0.17F, -1.06F}) {
            byte var5 = 10;

            for (int var6 = 0; var6 < var5; var6++) {
               float var7 = 0.3F + 0.72F * var6 / var5;
               float var8 = 0.3F + 0.72F * (var6 + 1) / var5;
               float var9 = var0 * (sideX(var4, var7) + 0.003F);
               float var10 = var0 * (sideX(var4, var8) + 0.003F);
               flat(
                  at(var4 - 0.005F, var9, var7),
                  at(var4 + 0.005F, var9, var7),
                  at(var4 + 0.005F, var10, var8),
                  at(var4 - 0.005F, var10, var8),
                  v(var0, 0.0F, 0.0F),
                  -15592940,
                  0
               );
            }
         }

         for (float var18 : new float[]{0.42F, -0.72F}) {
            obox(v(var0 * (sideX(var18, 0.9F) + 0.004F), 0.9F, var18), v(0.006F, 0.0F, 0.0F), v(0.0F, 0.016F, 0.0F), v(0.0F, 0.0F, 0.075F), -16250613, 0);
         }

         float var13 = 1.05F;
         float var15 = 1.13F;
         flat(
            at(var13, var0 * (sideX(var13, 0.6F) + 0.003F), 0.6F),
            at(var15, var0 * (sideX(var15, 0.62F) + 0.003F), 0.62F),
            at(var15, var0 * (sideX(var15, 0.78F) + 0.003F), 0.78F),
            at(var13, var0 * (sideX(var13, 0.76F) + 0.003F), 0.76F),
            v(var0, 0.0F, 0.0F),
            -16250613,
            0
         );
         byte var17 = 16;

         for (int var19 = 0; var19 < var17; var19++) {
            float var20 = -1.08F + 2.2F * var19 / var17;
            float var21 = -1.08F + 2.2F * (var19 + 1) / var17;
            float var22 = var0 * (sideX(var20, 0.25F) + 0.035F);
            float var23 = var0 * (sideX(var21, 0.25F) + 0.035F);
            float var24 = var0 * (sideX(var20, 0.25F) - 0.02F);
            float var25 = var0 * (sideX(var21, 0.25F) - 0.02F);
            int var11 = (var19 & 1) == 0 ? -14803165 : -15329510;
            flat(v(var24, 0.25F, var20), v(var22, 0.25F, var20), v(var23, 0.25F, var21), v(var25, 0.25F, var21), v(0.0F, 1.0F, 0.0F), var11, 0);
            flat(v(var22, 0.25F, var20), v(var22, 0.17F, var20), v(var23, 0.17F, var21), v(var23, 0.25F, var21), v(var0, 0.0F, 0.0F), var11, 0);
            flat(v(var24, 0.17F, var20), v(var22, 0.17F, var20), v(var23, 0.17F, var21), v(var25, 0.17F, var21), v(0.0F, -1.0F, 0.0F), -15329510, 0);
         }
      }
   }

   private static void interior() {
      flat(v(-0.84F, 0.26F, -1.55F), v(0.84F, 0.26F, -1.55F), v(0.84F, 0.26F, 0.95F), v(-0.84F, 0.26F, 0.95F), v(0.0F, 1.0F, 0.0F), -14803423, 0);
      box(-0.84F, 0.7F, 0.66F, 0.84F, 0.98F, 0.95F, -15461354);
      flat(v(-0.22F, 0.89F, 0.655F), v(0.12F, 0.89F, 0.655F), v(0.12F, 0.97F, 0.655F), v(-0.22F, 0.97F, 0.655F), v(0.0F, 0.0F, -1.0F), -13669377, 1);
      box(-0.12F, 0.26F, -0.6F, 0.12F, 0.5F, 0.66F, -15461354);

      for (byte var0 = -1; var0 <= 1; var0 += 2) {
         seat(var0 * 0.42F);
      }

      box(-0.8F, 0.26F, -1.3F, 0.8F, 0.38F, -0.85F, -1449001);
      backrest(0.0F, 0.8F, v(0.0F, 0.38F, -1.3F), v(0.0F, 1.15F, -1.45F));
      float[] var14 = v(0.42F, 0.88F, 0.4F);
      float[] var1 = norm(v(0.0F, 0.4F, -1.0F));
      float[] var2 = v(1.0F, 0.0F, 0.0F);
      float[] var3 = norm(cross(var1, var2));
      byte var4 = 24;
      byte var5 = 6;
      float[][][] var6 = new float[var4 + 1][var5 + 1][];

      for (int var7 = 0; var7 <= var4; var7++) {
         for (int var8 = 0; var8 <= var5; var8++) {
            double var9 = (Math.PI * 2) * var7 / var4;
            double var11 = (Math.PI * 2) * var8 / var5;
            float var13 = 0.16F + 0.022F * (float)Math.cos(var11);
            var6[var7][var8] = add(
               var14,
               add(scale(var2, var13 * (float)Math.cos(var9)), add(scale(var3, var13 * (float)Math.sin(var9)), scale(var1, 0.022F * (float)Math.sin(var11))))
            );
         }
      }

      grid(var6, (var0x, var1x) -> c(-15132389), (var4x, var5x, var6x) -> {
         double var7x = (Math.PI * 2) * var4x / var4;
         return add(var14, add(scale(var2, 0.16F * (float)Math.cos(var7x)), scale(var3, 0.16F * (float)Math.sin(var7x))));
      });
      obox(var14, scale(var2, 0.15F), scale(var3, 0.02F), scale(var1, 0.015F), -15132389, 0);
      obox(add(var14, v(0.0F, 0.02F, 0.13F)), v(0.025F, 0.0F, 0.0F), v(0.0F, 0.025F, 0.0F), v(0.0F, 0.01F, 0.13F), -15461354, 0);
   }

   private static void seat(float var0) {
      box(var0 - 0.21F, 0.24F, -0.45F, var0 + 0.21F, 0.32F, 0.12F, -1449001);
      box(var0 - 0.08F, 0.32F, -0.42F, var0 + 0.08F, 0.325F, 0.1F, -15000803);

      for (byte var1 = -1; var1 <= 1; var1 += 2) {
         box(var0 + var1 * 0.21F - 0.03F, 0.32F, -0.45F, var0 + var1 * 0.21F + 0.03F, 0.38F, 0.1F, -1449001);
      }

      backrest(var0, 0.21F, v(var0, 0.32F, -0.47F), v(var0, 1.3F, -0.64F));
   }

   private static void backrest(float var0, float var1, float[] var2, float[] var3) {
      float[] var4 = scale(add(var2, var3), 0.5F);
      float[] var5 = scale(sub(var3, var2), 0.5F);
      float[] var6 = scale(norm(v(0.0F, -var5[2], var5[1])), 0.07F);
      obox(var4, v(var1, 0.0F, 0.0F), var5, var6, -1449001, 0);
      obox(add(var4, scale(var6, 1.02F)), v(Math.min(0.08F, var1 * 0.4F), 0.0F, 0.0F), scale(var5, 0.8F), scale(var6, 0.02F), -15000803, 0);
      if (var1 < 0.3F) {
         for (byte var7 = -1; var7 <= 1; var7 += 2) {
            obox(
               add(add(var4, v(var7 * (var1 - 0.02F), 0.0F, 0.0F)), scale(var6, 0.9F)),
               v(0.035F, 0.0F, 0.0F),
               scale(var5, 0.85F),
               scale(var6, 0.6F),
               -1449001,
               0
            );
         }
      }
   }

   private static void wheel(float var0, int var1) {
      float[][] var2 = new float[][]{
         {0.27F, 0.125F},
         {0.31F, 0.15F},
         {0.35F, 0.15F},
         {0.365F, 0.135F},
         {0.372F, 0.1F},
         {0.372F, -0.1F},
         {0.365F, -0.135F},
         {0.35F, -0.15F},
         {0.31F, -0.15F},
         {0.27F, -0.125F}
      };
      revolve(var0, var1, var2, 56, (var0x, var1x) -> c(var1x >= 3 && var1x <= 5 ? -15329768 : -14737630), 0.31F, 0.0F);
      revolve(var0, var1, new float[][]{{0.274F, 0.127F}, {0.264F, 0.134F}, {0.25F, 0.128F}}, 40, (var0x, var1x) -> c(-14013392), 0.26F, 0.0F);
      revolve(var0, var1, new float[][]{{0.25F, 0.125F}, {0.25F, -0.12F}}, 40, (var0x, var1x) -> c(-11184034), 0.6F, 0.0F);
      revolve(var0, var1, new float[][]{{0.075F, 0.085F}, {0.034F, 0.09F}}, 20, (var0x, var1x) -> c(-15263717), 0.0F, -0.3F);
      revolve(var0, var1, new float[][]{{0.034F, 0.092F}, {0.0F, 0.094F}}, 20, (var0x, var1x) -> c(-15987698), 0.0F, -0.3F);
      revolve(var0, var1, new float[][]{{0.225F, -0.02F}, {0.08F, -0.02F}}, 32, (var0x, var1x) -> c(-10591898), 0.0F, -0.3F);

      for (int var3 = 0; var3 < 5; var3++) {
         for (byte var4 = -1; var4 <= 1; var4 += 2) {
            double var5 = Math.toRadians(var3 * 72 + 18);
            double var7 = var5 + Math.toRadians(var4 * 5);
            double var9 = var5 + Math.toRadians(var4 * 11);
            float[] var11 = wp(var0, var1, 0.078F, 0.07F, var7);
            float[] var12 = wp(var0, var1, 0.118F, 0.252F, var9);
            double var13 = (var7 + var9) / 2.0;
            float[] var15 = scale(add(var11, var12), 0.5F);
            float[] var16 = scale(sub(var12, var11), 0.5F);
            float[] var17 = v(0.0F, -((float)Math.sin(var13)) * 0.013F, (float)Math.cos(var13) * 0.013F);
            obox(var15, var16, var17, v(var1 * 0.016F, 0.0F, 0.0F), -15263717, 0);
         }
      }

      double var18 = 2.2;
      float[] var19 = wp(var0, var1, -0.005F, 0.19F, var18);
      obox(
         var19,
         v(var1 * 0.04F, 0.0F, 0.0F),
         v(0.0F, (float)Math.cos(var18) * 0.045F, (float)Math.sin(var18) * 0.045F),
         v(0.0F, -((float)Math.sin(var18)) * 0.08F, (float)Math.cos(var18) * 0.08F),
         -13947599,
         0
      );
   }

   private static float[] wp(float var0, int var1, float var2, float var3, double var4) {
      return v(var1 * (0.9F + var2), 0.39F + var3 * (float)Math.cos(var4), var0 + var3 * (float)Math.sin(var4));
   }

   private static void revolve(float var0, int var1, float[][] var2, int var3, SportCar.Cell var4, float var5, float var6) {
      float[][][] var7 = new float[var3 + 1][var2.length][];

      for (int var8 = 0; var8 <= var3; var8++) {
         double var9 = (Math.PI * 2) * var8 / var3;

         for (int var11 = 0; var11 < var2.length; var11++) {
            var7[var8][var11] = wp(var0, var1, var2[var11][1], var2[var11][0], var9);
         }
      }

      grid(var7, var4, (var5x, var6x, var7x) -> wp(var0, var1, var6, var5, (Math.PI * 2) * var5x / var3));
   }

   private static float faceZ(boolean var0, float var1, float var2, float var3) {
      return var0 ? frontZ(var1, var2) + var3 : rearZ(var1, var2) - var3;
   }

   private static List<float[]> poly(int var0, float... var1) {
      ArrayList var2 = new ArrayList();

      for (int var3 = 0; var3 < var1.length; var3 += 2) {
         var2.add(new float[]{var0 * var1[var3], var1[var3 + 1]});
      }

      return var2;
   }

   private static List<float[]> roundRect(float var0, float var1, float var2, float var3, float var4) {
      ArrayList var5 = new ArrayList();
      float[][] var6 = new float[][]{{var2 - var4, var3 - var4}, {var0 + var4, var3 - var4}, {var0 + var4, var1 + var4}, {var2 - var4, var1 + var4}};

      for (int var7 = 0; var7 < 4; var7++) {
         for (int var8 = 0; var8 <= 5; var8++) {
            double var9 = (Math.PI / 2) * (var7 + var8 / 5.0);
            var5.add(new float[]{var6[var7][0] + var4 * (float)Math.cos(var9), var6[var7][1] + var4 * (float)Math.sin(var9)});
         }
      }

      return var5;
   }

   private static void shape(List<float[]> var0, float var1, boolean var2, int var3, int var4) {
      float var5 = 0.0F;
      float var6 = 0.0F;

      for (float[] var8 : var0) {
         var5 += var8[0];
         var6 += var8[1];
      }

      var5 /= var0.size();
      var6 /= var0.size();
      int var18 = var0.size() + 1;
      byte var19 = 3;
      float[][][] var9 = new float[var19 + 1][var18][];

      for (int var10 = 0; var10 <= var19; var10++) {
         float var11 = 1.0F - (float)var10 / var19;

         for (int var12 = 0; var12 < var18; var12++) {
            float[] var13 = (float[])var0.get(var12 % var0.size());
            float var14 = var5 + (var13[0] - var5) * var11;
            float var15 = var6 + (var13[1] - var6) * var11;
            var9[var10][var12] = v(var14, var15, faceZ(var2, var14, var15, var1));
         }
      }

      grid(var9, (var2x, var3x) -> c(var3, var4), (var1x, var2x, var3x) -> new float[]{var3x[0], var3x[1], var3x[2] + (var2 ? -1 : 1)});
   }

   private static void band(List<float[]> var0, List<float[]> var1, float var2, float var3, boolean var4, int var5) {
      int var6 = var0.size() + 1;
      float[][][] var7 = new float[var6][2][];

      for (int var8 = 0; var8 < var6; var8++) {
         float[] var9 = (float[])var0.get(var8 % var0.size());
         float[] var10 = (float[])var1.get(var8 % var1.size());
         var7[var8][0] = v(var9[0], var9[1], faceZ(var4, var9[0], var9[1], var2));
         var7[var8][1] = v(var10[0], var10[1], faceZ(var4, var10[0], var10[1], var3));
      }

      float[] var11 = new float[2];

      for (float[] var13 : var1) {
         var11[0] += var13[0] / var1.size();
         var11[1] += var13[1] / var1.size();
      }

      grid(
         var7,
         (var1x, var2x) -> c(var5),
         (var2x, var3x, var4x) -> new float[]{var11[0] + (var4x[0] - var11[0]) * 0.5F, var11[1] + (var4x[1] - var11[1]) * 0.5F, var4x[2] + (var4 ? -1 : 1)}
      );
   }

   private static void patch(float var0, float var1, float var2, float var3, float var4, boolean var5, int var6, int var7) {
      ArrayList var8 = new ArrayList();
      byte var9 = 4;

      for (int var10 = 0; var10 < var9; var10++) {
         var8.add(new float[]{var0 + (var2 - var0) * var10 / var9, var1});
      }

      var8.add(new float[]{var2, var1});
      var8.add(new float[]{var2, var3});

      for (int var11 = var9 - 1; var11 >= 1; var11--) {
         var8.add(new float[]{var0 + (var2 - var0) * var11 / var9, var3});
      }

      var8.add(new float[]{var0, var3});
      shape(var8, var4, var5, var6, var7);
   }

   private static long c(int var0) {
      return var0 & 4294967295L;
   }

   private static long c(int var0, int var1) {
      return (long)var1 << 32 | var0 & 4294967295L;
   }

   private static void grid(float[][][] var0, SportCar.Cell var1, SportCar.Ref var2) {
      int var3 = var0.length;
      int var4 = var0[0].length;
      float[][][] var5 = new float[var3][var4][];

      for (int var6 = 0; var6 < var3; var6++) {
         for (int var7 = 0; var7 < var4; var7++) {
            float[] var8 = sub(var0[Math.min(var6 + 1, var3 - 1)][var7], var0[Math.max(var6 - 1, 0)][var7]);
            float[] var9 = sub(var0[var6][Math.min(var7 + 1, var4 - 1)], var0[var6][Math.max(var7 - 1, 0)]);
            float[] var10 = cross(var8, var9);
            float[] var11 = sub(var0[var6][var7], var2.at(var6, var7, var0[var6][var7]));
            if (len(var10) < 1.0E-9F) {
               var10 = var11;
            }

            if (dot(var10, var11) < 0.0F) {
               var10 = scale(var10, -1.0F);
            }

            var5[var6][var7] = norm(var10);
         }
      }

      for (int var12 = 0; var12 < var3 - 1; var12++) {
         for (int var13 = 0; var13 < var4 - 1; var13++) {
            long var14 = var1.at(var12, var13);
            if (var14 != 0L) {
               quad(
                  var0[var12][var13],
                  var0[var12 + 1][var13],
                  var0[var12 + 1][var13 + 1],
                  var0[var12][var13 + 1],
                  var5[var12][var13],
                  var5[var12 + 1][var13],
                  var5[var12 + 1][var13 + 1],
                  var5[var12][var13 + 1],
                  (int)var14,
                  (int)(var14 >>> 32)
               );
            }
         }
      }
   }

   private static void quad(float[] var0, float[] var1, float[] var2, float[] var3, float[] var4, float[] var5, float[] var6, float[] var7, int var8, int var9) {
      float[] var10 = new float[24];
      put(var10, 0, var0, var4);
      put(var10, 6, var1, var5);
      put(var10, 12, var2, var6);
      put(var10, 18, var3, var7);
      BV.add(var10);
      BC.add(new int[]{var8, var9});
   }

   private static void put(float[] var0, int var1, float[] var2, float[] var3) {
      var0[var1] = var2[0];
      var0[var1 + 1] = var2[1];
      var0[var1 + 2] = var2[2];
      var0[var1 + 3] = var3[0];
      var0[var1 + 4] = var3[1];
      var0[var1 + 5] = var3[2];
   }

   private static void flat(float[] var0, float[] var1, float[] var2, float[] var3, float[] var4, int var5, int var6) {
      float[] var7 = cross(sub(var2, var0), sub(var3, var1));
      if (len(var7) < 1.0E-9F) {
         var7 = (float[])var4.clone();
      }

      if (dot(var7, var4) < 0.0F) {
         var7 = scale(var7, -1.0F);
      }

      var7 = norm(var7);
      quad(var0, var1, var2, var3, var7, var7, var7, var7, var5, var6);
   }

   private static void box(float var0, float var1, float var2, float var3, float var4, float var5, int var6) {
      obox(
         v((var0 + var3) / 2.0F, (var1 + var4) / 2.0F, (var2 + var5) / 2.0F),
         v((var3 - var0) / 2.0F, 0.0F, 0.0F),
         v(0.0F, (var4 - var1) / 2.0F, 0.0F),
         v(0.0F, 0.0F, (var5 - var2) / 2.0F),
         var6,
         0
      );
   }

   private static void obox(float[] var0, float[] var1, float[] var2, float[] var3, int var4, int var5) {
      face(add(var0, var1), var2, var3, var1, var4, var5);
      face(sub(var0, var1), var2, var3, scale(var1, -1.0F), var4, var5);
      face(add(var0, var2), var1, var3, var2, var4, var5);
      face(sub(var0, var2), var1, var3, scale(var2, -1.0F), var4, var5);
      face(add(var0, var3), var1, var2, var3, var4, var5);
      face(sub(var0, var3), var1, var2, scale(var3, -1.0F), var4, var5);
   }

   private static void face(float[] var0, float[] var1, float[] var2, float[] var3, int var4, int var5) {
      float[] var6 = norm((float[])var3.clone());
      quad(sub(sub(var0, var1), var2), sub(add(var0, var1), var2), add(add(var0, var1), var2), add(sub(var0, var1), var2), var6, var6, var6, var6, var4, var5);
   }

   private static float[] v(float var0, float var1, float var2) {
      return new float[]{var0, var1, var2};
   }

   private static float[] add(float[] var0, float[] var1) {
      return new float[]{var0[0] + var1[0], var0[1] + var1[1], var0[2] + var1[2]};
   }

   private static float[] sub(float[] var0, float[] var1) {
      return new float[]{var0[0] - var1[0], var0[1] - var1[1], var0[2] - var1[2]};
   }

   private static float[] scale(float[] var0, float var1) {
      return new float[]{var0[0] * var1, var0[1] * var1, var0[2] * var1};
   }

   private static float[] cross(float[] var0, float[] var1) {
      return new float[]{var0[1] * var1[2] - var0[2] * var1[1], var0[2] * var1[0] - var0[0] * var1[2], var0[0] * var1[1] - var0[1] * var1[0]};
   }

   private static float dot(float[] var0, float[] var1) {
      return var0[0] * var1[0] + var0[1] * var1[1] + var0[2] * var1[2];
   }

   private static float len(float[] var0) {
      return (float)Math.sqrt(dot(var0, var0));
   }

   private static float[] norm(float[] var0) {
      float var1 = len(var0);
      return var1 < 1.0E-12F ? new float[]{0.0F, 1.0F, 0.0F} : scale(var0, 1.0F / var1);
   }

   private static float clamp(float var0) {
      return Math.max(0.0F, Math.min(1.0F, var0));
   }

   private static float soft(float var0, float var1, float var2) {
      float var3 = 0.03F;
      float var4 = clamp((var0 - var1 + var3) / (2.0F * var3)) * clamp((var2 + var3 - var0) / (2.0F * var3));
      return var4 * var4 * (3.0F - 2.0F * var4);
   }

   private static float sq(float var0) {
      return var0 * var0;
   }

   private static float pow(float var0, float var1) {
      return (float)Math.pow(Math.max(0.0F, var0), var1);
   }

   private static float gauss(float var0) {
      return (float)Math.exp(-var0 * var0);
   }

   static {
      float var0 = (float)Math.sqrt(0.9993);
      LX = 0.32F / var0;
      LY = 0.88F / var0;
      LZ = 0.35F / var0;
   }

   private interface Cell {
      long at(int var1, int var2);
   }

   private interface Ref {
      float[] at(int var1, int var2, float[] var3);
   }
}
