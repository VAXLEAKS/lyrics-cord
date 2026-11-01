package dev.neonlyrics.sky;

import java.util.Random;

public final class SkyPainter {
   public static final float EL_TOP = 90.0F;
   public static final float EL_BOTTOM = -8.0F;
   public static final int SPRITE = 64;

   private SkyPainter() {
   }

   private static int c255(float var0) {
      return Math.max(0, Math.min(255, Math.round(var0 * 255.0F)));
   }

   public static int[] base(SkyPainter.Spec var0, int var1, int var2) {
      SkyPainter.Buf var3 = new SkyPainter.Buf(var1, var2);
      Random var4 = new Random(var0.kind().hashCode() * 7919L + 13L);
      String var5 = var0.kind();
      boolean var6 = var5.equals("CLOUDS") || var5.equals("WINTER");
      int var7 = var0.a1();
      int var8 = var0.a2();
      switch (var5) {
         case "NEBULA":
            nebula(var3, var7, var8, 1.0F, 11);
            nebula(var3, var8, 5930239, 0.35F, 29);
            horizonGlow(var3, var7, 0.3F, 16.0F);
            break;
         case "GALAXY":
            milkyWay(var3, var7, var8, 1.15F);
            nebula(var3, var7, var8, 0.45F, 5);
            horizonGlow(var3, var8, 0.25F, 14.0F);
            break;
         case "STARRY":
            milkyWay(var3, 10466559, 16771280, 0.75F);
            horizonGlow(var3, 3890088, 0.3F, 14.0F);
            break;
         case "MIDNIGHT":
            milkyWay(var3, 8162559, 14213375, 0.95F);
            nebula(var3, 3819720, 8162559, 0.25F, 41);
            horizonGlow(var3, 2767504, 0.35F, 12.0F);
            break;
         case "PURPLE":
         case "NEON":
         case "ROSE":
            nebula(var3, var7, var8, 0.45F, 3);
            milkyWay(var3, var7, 16777215, 0.35F);
            horizonGlow(var3, var7, 0.35F, 12.0F);
            break;
         case "NORTHERN":
         case "OCEAN":
         case "ICE":
         case "CUSTOM":
            milkyWay(var3, 10466559, var8, 0.4F);
            horizonGlow(var3, var7, 0.3F, 10.0F);
            break;
         case "PILLARS":
            nebula(var3, var7, var8, 0.4F, 17);
            horizonGlow(var3, var7, 0.7F, 14.0F);
            break;
         case "LAGOON":
            nebula(var3, var7, var8, 0.25F, 37);
            horizonGlow(var3, var8, 0.4F, 18.0F);
            break;
         case "BLOOD_MOON":
            nebula(var3, 11540522, 16734780, 0.5F, 23);
            horizonGlow(var3, 16726826, 0.45F, 12.0F);
            break;
         case "ECLIPSE":
            milkyWay(var3, 9207039, 16766632, 0.45F);
            nebula(var3, var8, var7, 0.25F, 31);
            break;
         case "SUNSET":
            rays(var3, 16756848, 200.0F, 3.0F, 0.4F);
            sun(var3, 16761466, 200.0F, 3.0F, 3.8F);
            horizonGlow(var3, 16742972, 0.55F, 12.0F);
            clouds(var3, 16752250, 5909082, 0.55F, 0.6F);
            break;
         case "SAKURA":
            sun(var3, 16765158, 60.0F, 4.0F, 3.2F);
            rays(var3, 16762592, 60.0F, 4.0F, 0.35F);
            horizonGlow(var3, 16751819, 0.5F, 14.0F);
            nebula(var3, 16757721, 13144319, 0.25F, 13);
            break;
         case "STORM":
         case "RAIN":
            horizonGlow(var3, 4876954, 0.35F, 10.0F);
            break;
         case "WINTER":
            sun(var3, 16771272, 150.0F, 14.0F, 2.4F);
            rays(var3, 16773344, 150.0F, 14.0F, 0.1F);
            break;
         case "CLOUDS":
            sun(var3, 16774352, 120.0F, 40.0F, 3.0F);
            horizonGlow(var3, 16777215, 0.3F, 16.0F);
      }

      if (!var6) {
         stars(var3, var4, var0.stars() * (!var5.equals("SUNSET") && !var5.equals("SAKURA") ? 1.25F : 0.6F));
      }

      if (var5.equals("ECLIPSE")) {
         eclipse(var3, var7, var8);
      }

      if (var0.moon() != 0) {
         moon(var3, var0.moon(), 200.0F, 30.0F, var5.equals("BLOOD_MOON") ? 4.2F : 3.0F);
      }

      fadeBelowHorizon(var3);
      return var3.toEmissive();
   }

   private static void rays(SkyPainter.Buf var0, int var1, float var2, float var3, float var4) {
      double var5 = Math.toRadians(var2);
      double var7 = Math.toRadians(var3);
      float var9 = (float)(-Math.sin(var5) * Math.cos(var7));
      float var10 = (float)Math.sin(var7);
      float var11 = (float)(Math.cos(var5) * Math.cos(var7));
      float var12 = (float)(-Math.cos(var5));
      float var13 = (float)(-Math.sin(var5));
      float var14 = (float)(Math.sin(var5) * Math.sin(var7));
      float var15 = (float)Math.cos(var7);
      float var16 = (float)(-Math.cos(var5) * Math.sin(var7));

      for (int var17 = 0; var17 < var0.h; var17++) {
         if (!(var0.el[var17] < -2.0F)) {
            for (int var18 = 0; var18 < var0.w; var18++) {
               float var19 = var0.dx(var18, var17);
               float var20 = var0.dy(var17);
               float var21 = var0.dz(var18, var17);
               float var22 = var19 * var9 + var20 * var10 + var21 * var11;
               if (!(var22 < 0.2F)) {
                  float var23 = (float)Math.toDegrees(Math.acos(Math.min(1.0F, var22)));
                  float var24 = (float)Math.atan2(var19 * var14 + var20 * var15 + var21 * var16, var19 * var12 + var21 * var13);
                  float var25 = SkyPainter.Noise.noise((float)Math.cos(var24) * 9.0F, (float)Math.sin(var24) * 9.0F, 3.3F);
                  float var26 = smooth(0.48F, 0.78F, var25);
                  float var27 = (float)Math.exp(-var23 / 34.0F) * smooth(1.5F, 6.0F, var23);
                  if (!(var26 * var27 < 0.002F)) {
                     var0.add(var17 * var0.w + var18, var1, var26 * var27 * var4);
                  }
               }
            }
         }
      }
   }

   public static int[] fx(SkyPainter.Spec var0, int var1, int var2) {
      SkyPainter.Buf var3 = new SkyPainter.Buf(var1, var2);
      String var4 = var0.kind();
      boolean var5 = true;
      switch (var4) {
         case "PILLARS":
            pillars(var3, var0.a1(), var0.a2());
            break;
         case "LAGOON":
            caustics(var3, var0.a1(), var0.a2());
            break;
         case "NEBULA":
            wisps(var3, var0.a1(), var0.a2());
            break;
         case "CLOUDS":
            clouds(var3, 16777215, 12175842, 0.95F, 0.46F);
            break;
         case "WINTER":
            clouds(var3, 16777215, 13161702, 0.85F, 0.5F);
            break;
         case "SAKURA":
            clouds(var3, 16769776, 13144248, 0.55F, 0.55F);
            break;
         case "SUNSET":
            clouds(var3, 16756874, 8010346, 0.6F, 0.56F);
            break;
         case "STORM":
            clouds(var3, 5923960, 1711656, 0.95F, 0.38F);
            break;
         case "RAIN":
            clouds(var3, 4871270, 1843760, 0.85F, 0.42F);
            break;
         default:
            var5 = var0.aurora() && !var4.equals("GALAXY");
      }

      if (var0.aurora() && !var4.equals("GALAXY")) {
         aurora(var3, var0.a1(), var0.a2(), var4.hashCode());
      }

      if (!var5) {
         return null;
      }

      fadeBelowHorizon(var3);
      return var3.toEmissive();
   }

   private static void stars(SkyPainter.Buf var0, Random var1, float var2) {
      if (!(var2 <= 0.0F)) {
         int var3 = Math.round(var2 * 1700.0F);

         for (int var4 = 0; var4 < var3; var4++) {
            float var5 = -0.05F + var1.nextFloat() * 1.05F;
            float var6 = (float)Math.toDegrees(Math.asin(Math.min(1.0F, var5)));
            float var7 = var1.nextFloat() * 360.0F;
            float var8 = (float)Math.pow(var1.nextFloat(), 3.2);
            int var9 = var1.nextFloat() < 0.15F ? 16770760 : (var1.nextFloat() < 0.3F ? 13162751 : 16777215);
            float var10 = Math.max(252.0F / var0.w, 0.045F + 0.06F * var8);
            float var11 = 0.22F + 0.6F * var8;
            star(var0, var7, var6, var10, var9, var11);
            if (var8 > 0.82F) {
               star(var0, var7, var6, var10 * 5.5F, var9, 0.12F * var8);
            }
         }
      }
   }

   private static void star(SkyPainter.Buf var0, float var1, float var2, float var3, int var4, float var5) {
      double var6 = Math.cos(Math.toRadians(var1));
      double var8 = Math.sin(Math.toRadians(var1));
      double var10 = Math.cos(Math.toRadians(var2));
      double var12 = Math.sin(Math.toRadians(var2));
      float var14 = (float)(-var8 * var10);
      float var15 = (float)var12;
      float var16 = (float)(var6 * var10);
      float var17 = var0.w / 360.0F;
      float var18 = var0.h / 98.0F;
      float var19 = var3 * 3.0F;
      int var20 = Math.round((90.0F - var2) * var18);
      int var21 = (int)Math.ceil(var19 * var18) + 1;
      float var22 = (float)Math.max(0.05, var10);
      int var23 = Math.round(var1 * var17);
      int var24 = (int)Math.ceil(var19 * var17 / var22) + 1;
      if (var24 > var0.w / 2) {
         var24 = var0.w / 2;
      }

      double var25 = Math.toRadians(var3);
      var25 *= var25;

      for (int var27 = var20 - var21; var27 <= var20 + var21; var27++) {
         if (var27 >= 0 && var27 < var0.h) {
            for (int var28 = var23 - var24; var28 <= var23 + var24; var28++) {
               int var29 = Math.floorMod(var28, var0.w);
               float var30 = var14 * var0.dx(var29, var27) + var15 * var0.dy(var27) + var16 * var0.dz(var29, var27);
               double var31 = 2.0F * (1.0F - var30);
               float var33 = (float)Math.exp(-var31 / (2.0 * var25));
               if (var33 > 0.003F) {
                  var0.add(var27 * var0.w + var29, var4, var33 * var5);
               }
            }
         }
      }
   }

   private static void milkyWay(SkyPainter.Buf var0, int var1, int var2, float var3) {
      float var4 = 0.32F;
      float var5 = 0.58F;
      float var6 = 0.75F;
      float var7 = (float)Math.sqrt(var4 * var4 + var5 * var5 + var6 * var6);
      var4 /= var7;
      var5 /= var7;
      var6 /= var7;
      SkyPainter.Low var8 = new SkyPainter.Low(var0, 3, (var0x, var1x, var2x) -> SkyPainter.Noise.fbm(var0x * 3.2F + 11.0F, var1x * 3.2F, var2x * 3.2F, 5));
      SkyPainter.Low var9 = new SkyPainter.Low(var0, 2, (var0x, var1x, var2x) -> SkyPainter.Noise.fbm(var0x * 7.0F, var1x * 7.0F + 5.0F, var2x * 7.0F, 4));
      float var10 = var4;
      float var11 = var5;
      float var12 = var6;

      for (int var13 = 0; var13 < var0.h; var13++) {
         for (int var14 = 0; var14 < var0.w; var14++) {
            float var15 = var0.dx(var14, var13);
            float var16 = var0.dy(var13);
            float var17 = var0.dz(var14, var13);
            float var18 = var15 * var10 + var16 * var11 + var17 * var12;
            float var19 = (float)Math.exp(-(var18 * var18) / 0.045F);
            if (!(var19 < 0.01F)) {
               float var20 = var8.get(var14, var13);
               float var21 = var9.get(var14, var13);
               float var22 = var19 * (0.25F + 0.75F * var20);
               float var23 = smooth(0.52F, 0.72F, var21) * var19;
               float var24 = (float)Math.exp(-(var18 * var18) / 0.008F) * smooth(0.45F, 0.8F, var20);
               int var25 = lerp(var1, var2, smooth(0.3F, 0.85F, var20));
               int var26 = var13 * var0.w + var14;
               var0.add(var26, var25, var22 * 0.42F * var3 * (1.0F - 0.75F * var23));
               var0.add(var26, 16774374, var24 * 0.25F * var3 * (1.0F - var23));
               float var27 = SkyPainter.Noise.hash(var14 * 7919 + var13 * 104729);
               if (var27 > 0.985F - var19 * 0.03F) {
                  var0.add(var26, 16777215, 0.5F * var19 * var3);
               }
            }
         }
      }
   }

   private static void nebula(SkyPainter.Buf var0, int var1, int var2, float var3, int var4) {
      float var5 = var4 * 3.7F;
      SkyPainter.Low var6 = new SkyPainter.Low(var0, 3, (var1x, var2x, var3x) -> {
         float var4x = SkyPainter.Noise.fbm(var1x * 1.4F + var5, var2x * 1.4F, var3x * 1.4F, 3) - 0.5F;
         float var5x = SkyPainter.Noise.fbm(var1x * 1.4F, var2x * 1.4F + var5, var3x * 1.4F, 3) - 0.5F;
         return SkyPainter.Noise.fbm(var1x * 1.9F + var4x * 1.3F + var5, var2x * 1.9F + var5x * 1.3F, var3x * 1.9F, 6);
      });
      SkyPainter.Low var7 = new SkyPainter.Low(
         var0, 3, (var1x, var2x, var3x) -> SkyPainter.Noise.fbm(var1x * 4.2F, var2x * 4.2F + var5, var3x * 4.2F + 3.0F, 4)
      );

      for (int var8 = 0; var8 < var0.h; var8++) {
         if (!(var0.el[var8] < -4.0F)) {
            for (int var9 = 0; var9 < var0.w; var9++) {
               float var10 = var6.get(var9, var8);
               float var11 = smooth(0.46F, 0.82F, var10);
               if (!(var11 <= 0.002F)) {
                  float var12 = var7.get(var9, var8);
                  int var13 = lerp(var1, var2, smooth(0.25F, 0.8F, var12));
                  int var14 = var8 * var0.w + var9;
                  var0.over(var14, var13, var11 * 0.62F * var3);
                  float var15 = smooth(0.72F, 0.95F, var10);
                  var0.add(var14, lerp(var13, 16777215, 0.55F), var15 * 0.35F * var3);
               }
            }
         }
      }
   }

   private static void wisps(SkyPainter.Buf var0, int var1, int var2) {
      SkyPainter.Low var3 = new SkyPainter.Low(var0, 2, (var0x, var1x, var2x) -> SkyPainter.Noise.fbm(var0x * 3.4F + 40.0F, var1x * 3.4F, var2x * 3.4F, 5));

      for (int var4 = 0; var4 < var0.h; var4++) {
         if (!(var0.el[var4] < -4.0F)) {
            for (int var5 = 0; var5 < var0.w; var5++) {
               float var6 = var3.get(var5, var4);
               float var7 = smooth(0.55F, 0.85F, var6);
               if (!(var7 <= 0.002F)) {
                  var0.over(var4 * var0.w + var5, lerp(var2, 16777215, 0.25F), var7 * 0.38F);
               }
            }
         }
      }
   }

   private static void aurora(SkyPainter.Buf var0, int var1, int var2, int var3) {
      float var4 = (var3 & 1023) * 0.37F;
      float[] var5 = new float[var0.w];
      float[] var6 = new float[var0.w];
      float[] var7 = new float[var0.w];
      float[] var8 = new float[var0.w];

      for (int var9 = 0; var9 < var0.w; var9++) {
         float var10 = var0.cosA[var9];
         float var11 = var0.sinA[var9];
         var5[var9] = 8.0F
            + 10.0F * SkyPainter.Noise.fbm(var10 * 1.6F + var4, var11 * 1.6F, 0.3F, 3)
            + 3.5F * (float)Math.sin(var9 * 6.2832 / var0.w * 7.0 + var4)
            + 1.5F * (float)Math.sin(var9 * 6.2832 / var0.w * 19.0 + var4 * 2.0F);
         var6[var9] = 14.0F + 26.0F * SkyPainter.Noise.fbm(var10 * 2.3F, var11 * 2.3F + var4, 1.7F, 3);
         float var12 = SkyPainter.Noise.noise(var10 * 60.0F + var4, var11 * 60.0F, 0.5F);
         float var13 = SkyPainter.Noise.noise(var10 * 160.0F, var11 * 160.0F + var4, 2.5F);
         var7[var9] = (float)Math.pow(0.55F * var12 + 0.45F * var13, 2.2) * 1.8F;
         var8[var9] = smooth(0.3F, 0.72F, SkyPainter.Noise.fbm(var10 * 1.7F + var4 * 2.0F, var11 * 1.7F, 4.1F, 4));
      }

      for (int var17 = 0; var17 < var0.h; var17++) {
         float var18 = var0.el[var17];
         if (!(var18 < -2.0F) && !(var18 > 75.0F)) {
            for (int var19 = 0; var19 < var0.w; var19++) {
               if (!(var8[var19] <= 0.01F)) {
                  float var20 = var18 - var5[var19];
                  float var21;
                  if (var20 < 0.0F) {
                     var21 = (float)Math.exp(var20 / 1.4F);
                  } else {
                     var21 = (float)Math.exp(-var20 / var6[var19]) * (0.35F + 0.65F * Math.min(1.0F, var7[var19]));
                  }

                  var21 *= var8[var19];
                  if (!(var21 <= 0.004F)) {
                     float var14 = Math.max(0.0F, Math.min(1.0F, var20 / (var6[var19] * 1.4F)));
                     int var15 = lerp(var1, var2, var14);
                     int var16 = var17 * var0.w + var19;
                     var0.over(var16, var15, Math.min(0.85F, var21 * 0.75F));
                     if (var20 > -1.0F && var20 < 3.0F) {
                        var0.add(var16, lerp(var1, 16777215, 0.5F), var21 * 0.18F);
                     }
                  }
               }
            }
         }
      }
   }

   private static void pillars(SkyPainter.Buf var0, int var1, int var2) {
      Random var3 = new Random(77L);
      byte var4 = 46;
      float[] var5 = new float[var4];
      float[] var6 = new float[var4];
      float[] var7 = new float[var4];
      float[] var8 = new float[var4];
      int[] var9 = new int[var4];

      for (int var10 = 0; var10 < var4; var10++) {
         var5[var10] = var3.nextFloat() * 360.0F;
         var6[var10] = 0.18F + var3.nextFloat() * 0.7F;
         var7[var10] = 25.0F + var3.nextFloat() * 55.0F;
         var8[var10] = 0.35F + var3.nextFloat() * 0.65F;
         var9[var10] = lerp(var1, var2, var3.nextFloat());
      }

      float var25 = var0.w / 360.0F;

      for (int var11 = 0; var11 < var4; var11++) {
         for (int var12 = 0; var12 < var0.h; var12++) {
            float var13 = var0.el[var12];
            if (!(var13 < -3.0F) && !(var13 > var7[var11])) {
               float var14 = smooth(-3.0F, 2.0F, var13) * (float)Math.pow(1.0F - var13 / var7[var11], 1.6);
               float var15 = Math.max(0.1F, var0.cosE[var12]);
               float var16 = var6[var11] / var15;
               int var17 = (int)((var5[var11] - var16 * 6.0F) * var25);
               int var18 = (int)((var5[var11] + var16 * 6.0F) * var25);

               for (int var19 = var17; var19 <= var18; var19++) {
                  int var20 = Math.floorMod(var19, var0.w);
                  float var21 = (var19 / var25 - var5[var11]) / var16;
                  float var22 = (float)Math.exp(-var21 * var21);
                  float var23 = (float)Math.exp(-var21 * var21 / 16.0F);
                  int var24 = var12 * var0.w + var20;
                  var0.add(var24, lerp(var9[var11], 16777215, 0.55F * var22), (var22 * 0.55F + var23 * 0.12F) * var14 * var8[var11]);
               }
            }
         }
      }
   }

   private static void caustics(SkyPainter.Buf var0, int var1, int var2) {
      SkyPainter.Low var3 = new SkyPainter.Low(var0, 2, (var0x, var1x, var2x) -> {
         float var3x = 3.2F / (Math.max(-0.2F, var1x) + 0.3F);
         return (SkyPainter.Noise.fbm(var0x * var3x * 0.35F, var2x * var3x * 0.35F, 1.3F, 3) - 0.5F) * 1.6F;
      });
      SkyPainter.Low var4 = new SkyPainter.Low(var0, 2, (var0x, var1x, var2x) -> {
         float var3x = 3.2F / (Math.max(-0.2F, var1x) + 0.3F);
         return (SkyPainter.Noise.fbm(var0x * var3x * 0.35F + 9.0F, var2x * var3x * 0.35F, 4.7F, 3) - 0.5F) * 1.6F;
      });

      for (int var5 = 0; var5 < var0.h; var5++) {
         float var6 = var0.el[var5];
         if (!(var6 < 1.0F)) {
            float var7 = smooth(1.0F, 14.0F, var6);

            for (int var8 = 0; var8 < var0.w; var8++) {
               float var9 = var0.dx(var8, var5);
               float var10 = var0.dy(var5);
               float var11 = var0.dz(var8, var5);
               float var12 = 3.2F / (var10 + 0.3F);
               float var13 = var9 * var12 + var3.get(var8, var5);
               float var14 = var11 * var12 + var4.get(var8, var5);
               float var15 = cellEdge(var13 * 1.15F, var14 * 1.15F);
               float var16 = cellEdge(var13 * 2.3F + 7.1F, var14 * 2.3F + 3.3F);
               float var17 = (float)Math.pow(1.0F - smooth(0.0F, 0.16F, var15), 2.2) + 0.45F * (float)Math.pow(1.0F - smooth(0.0F, 0.12F, var16), 2.5);
               float var18 = Math.min(1.2F, var17) * var7;
               int var19 = var5 * var0.w + var8;
               var0.add(var19, var1, 0.1F * var7);
               if (var18 > 0.004F) {
                  var0.add(var19, lerp(var1, var2, Math.min(1.0F, var18)), var18 * 0.55F);
               }
            }
         }
      }
   }

   private static float cellEdge(float var0, float var1) {
      int var2 = (int)Math.floor(var0);
      int var3 = (int)Math.floor(var1);
      float var4 = 9.0F;
      float var5 = 9.0F;

      for (int var6 = -1; var6 <= 1; var6++) {
         for (int var7 = -1; var7 <= 1; var7++) {
            int var8 = var2 + var7;
            int var9 = var3 + var6;
            float var10 = var8 + 0.15F + 0.7F * SkyPainter.Noise.hash(var8 * 374761 + var9 * 668265);
            float var11 = var9 + 0.15F + 0.7F * SkyPainter.Noise.hash(var8 * 912367 + var9 * 177823 + 5);
            float var12 = (float)Math.sqrt((var10 - var0) * (var10 - var0) + (var11 - var1) * (var11 - var1));
            if (var12 < var4) {
               var5 = var4;
               var4 = var12;
            } else if (var12 < var5) {
               var5 = var12;
            }
         }
      }

      return var5 - var4;
   }

   private static void clouds(SkyPainter.Buf var0, int var1, int var2, float var3, float var4) {
      SkyPainter.Low var5 = new SkyPainter.Low(var0, 2, (var0x, var1x, var2x) -> {
         if (var1x < -0.03F) {
            return 0.0F;
         }

         float var3x = 1.8F / (var1x + 0.1F);
         return SkyPainter.Noise.fbm(var0x * var3x * 0.42F, var2x * var3x * 0.42F, 0.7F, 6);
      });
      SkyPainter.Low var6 = new SkyPainter.Low(var0, 3, (var0x, var1x, var2x) -> {
         if (var1x < -0.03F) {
            return 0.0F;
         }

         float var3x = 1.8F / (var1x + 0.1F);
         return SkyPainter.Noise.fbm(var0x * var3x * 0.42F + 0.18F, var2x * var3x * 0.42F + 0.18F, 0.7F, 4);
      });

      for (int var7 = 0; var7 < var0.h; var7++) {
         float var8 = var0.dy(var7);
         if (!(var8 < -0.02F)) {
            float var9 = smooth(-0.02F, 0.12F, var8);

            for (int var10 = 0; var10 < var0.w; var10++) {
               float var11 = var5.get(var10, var7);
               float var12 = smooth(var4, var4 + 0.28F, var11);
               if (!(var12 <= 0.002F)) {
                  float var13 = var6.get(var10, var7);
                  float var14 = smooth(-0.08F, 0.12F, var11 - var13);
                  int var15 = lerp(var1, var2, 0.25F + 0.6F * var14);
                  var0.over(var7 * var0.w + var10, var15, var12 * var3 * var9);
               }
            }
         }
      }
   }

   private static void eclipse(SkyPainter.Buf var0, int var1, int var2) {
      float var3 = 165.0F;
      float var4 = 24.0F;
      float var5 = 7.5F;
      double var6 = Math.toRadians(var3);
      double var8 = Math.toRadians(var4);
      float var10 = (float)(-Math.sin(var6) * Math.cos(var8));
      float var11 = (float)Math.sin(var8);
      float var12 = (float)(Math.cos(var6) * Math.cos(var8));
      float var13 = (float)(-Math.cos(var6));
      float var14 = (float)(-Math.sin(var6));
      float var15 = (float)(Math.sin(var6) * Math.sin(var8));
      float var16 = (float)Math.cos(var8);
      float var17 = (float)(-Math.cos(var6) * Math.sin(var8));
      float var18 = (float)Math.toRadians(-11.0);
      float var19 = (float)Math.cos(var18);
      float var20 = (float)Math.sin(var18);

      for (int var21 = 0; var21 < var0.h; var21++) {
         if (!(Math.abs(var0.el[var21] - var4) > var5 * 6.0F)) {
            for (int var22 = 0; var22 < var0.w; var22++) {
               float var23 = var0.dx(var22, var21);
               float var24 = var0.dy(var21);
               float var25 = var0.dz(var22, var21);
               float var26 = var23 * var10 + var24 * var11 + var25 * var12;
               if (!(var26 < 0.75F)) {
                  float var27 = (float)Math.toDegrees(Math.acos(Math.min(1.0F, var26)));
                  float var28 = (float)Math.toDegrees(Math.asin(Math.max(-1.0F, Math.min(1.0F, var23 * var13 + var25 * var14))));
                  float var29 = (float)Math.toDegrees(Math.asin(Math.max(-1.0F, Math.min(1.0F, var23 * var15 + var24 * var16 + var25 * var17))));
                  float var30 = var28 * var19 - var29 * var20;
                  float var31 = var28 * var20 + var29 * var19;
                  float var32 = 0.27F;
                  float var33 = (float)Math.sqrt(var30 * var30 + var31 / var32 * (var31 / var32));
                  float var34 = (float)Math.atan2(var31 / var32, var30);
                  float var35 = smooth(var5 * 1.25F, var5 * 1.6F, var33) * (1.0F - smooth(var5 * 2.4F, var5 * 3.8F, var33));
                  float var36 = 0.6F + 0.4F * SkyPainter.Noise.noise(var33 * 3.1F, var34 * 3.0F, 1.1F);
                  float var37 = 0.4F + 0.6F * (0.5F + 0.5F * (float)Math.cos(var34 - 0.4F));
                  float var38 = var35 * var36 * var37;
                  int var39 = var21 * var0.w + var22;
                  float var40 = var27 > var5 ? (float)Math.exp(-(var27 - var5) / 3.2F) : 1.0F;
                  var0.add(var39, var1, var40 * 0.22F);
                  boolean var41 = var31 < 0.0F;
                  int var42 = lerp(var2, lerp(var1, 16777215, 0.6F), var37);
                  if (!var41) {
                     var0.add(var39, var42, var38 * 0.85F);
                  }

                  float var43 = (float)Math.exp(-Math.pow((var27 - var5 * 1.32F) / 0.32F, 2.0)) * (0.55F + 0.45F * Math.max(0.0F, var29 / (var27 + 0.001F)));
                  var0.add(var39, lerp(var1, 16777215, 0.5F), var43 * 0.55F);
                  float var44 = 1.0F - smooth(var5 - 0.08F, var5 + 0.04F, var27);
                  if (var44 > 0.0F) {
                     var0.over(var39, 131332, var44);
                  }

                  float var45 = (float)Math.exp(-Math.pow((var27 - var5 * 1.03F) / 0.1F, 2.0));
                  var0.add(var39, lerp(var1, 16777215, 0.8F), var45 * 0.95F);
                  if (var41) {
                     var0.add(var39, var42, var38 * 0.95F);
                  }
               }
            }
         }
      }
   }

   private static void moon(SkyPainter.Buf var0, int var1, float var2, float var3, float var4) {
      double var5 = Math.toRadians(var2);
      double var7 = Math.toRadians(var3);
      float var9 = (float)(-Math.sin(var5) * Math.cos(var7));
      float var10 = (float)Math.sin(var7);
      float var11 = (float)(Math.cos(var5) * Math.cos(var7));

      for (int var12 = 0; var12 < var0.h; var12++) {
         if (!(Math.abs(var0.el[var12] - var3) > var4 * 9.0F)) {
            for (int var13 = 0; var13 < var0.w; var13++) {
               float var14 = var0.dx(var13, var12);
               float var15 = var0.dy(var12);
               float var16 = var0.dz(var13, var12);
               float var17 = var14 * var9 + var15 * var10 + var16 * var11;
               if (!(var17 < 0.97F)) {
                  float var18 = (float)Math.toDegrees(Math.acos(Math.min(1.0F, var17)));
                  int var19 = var12 * var0.w + var13;
                  var0.add(var19, var1, (float)Math.exp(-var18 / (var4 * 1.6F)) * 0.22F);
                  float var20 = 1.0F - smooth(var4 - 0.06F, var4 + 0.06F, var18);
                  if (var20 > 0.0F) {
                     float var21 = SkyPainter.Noise.fbm(var14 * 90.0F, var15 * 90.0F, var16 * 90.0F, 4);
                     int var22 = lerp(var1, lerp(var1, 0, 0.35F), smooth(0.5F, 0.75F, var21));
                     var0.over(var19, var22, var20);
                  }
               }
            }
         }
      }
   }

   private static void sun(SkyPainter.Buf var0, int var1, float var2, float var3, float var4) {
      double var5 = Math.toRadians(var2);
      double var7 = Math.toRadians(var3);
      float var9 = (float)(-Math.sin(var5) * Math.cos(var7));
      float var10 = (float)Math.sin(var7);
      float var11 = (float)(Math.cos(var5) * Math.cos(var7));

      for (int var12 = 0; var12 < var0.h; var12++) {
         for (int var13 = 0; var13 < var0.w; var13++) {
            float var14 = var0.dx(var13, var12) * var9 + var0.dy(var12) * var10 + var0.dz(var13, var12) * var11;
            if (!(var14 < 0.5F)) {
               float var15 = (float)Math.toDegrees(Math.acos(Math.min(1.0F, var14)));
               int var16 = var12 * var0.w + var13;
               var0.add(var16, var1, (float)Math.exp(-var15 / 18.0F) * 0.35F);
               var0.add(var16, 16773848, (1.0F - smooth(var4 - 0.2F, var4 + 0.2F, var15)) * 0.95F);
            }
         }
      }
   }

   private static void horizonGlow(SkyPainter.Buf var0, int var1, float var2, float var3) {
      for (int var4 = 0; var4 < var0.h; var4++) {
         float var5 = var0.el[var4];
         if (!(var5 < -4.0F) && !(var5 > var3 * 2.0F)) {
            float var6 = smooth(-4.0F, 0.0F, var5) * (float)Math.exp(-Math.max(0.0F, var5) / (var3 * 0.45F));

            for (int var7 = 0; var7 < var0.w; var7++) {
               var0.add(var4 * var0.w + var7, var1, var6 * var2);
            }
         }
      }
   }

   private static void fadeBelowHorizon(SkyPainter.Buf var0) {
      for (int var1 = 0; var1 < var0.h; var1++) {
         float var2 = smooth(-7.0F, 1.0F, var0.el[var1]);
         if (!(var2 >= 0.999F)) {
            for (int var3 = 0; var3 < var0.w; var3++) {
               int var4 = var1 * var0.w + var3;
               var0.r[var4] = var0.r[var4] * var2;
               var0.g[var4] = var0.g[var4] * var2;
               var0.b[var4] = var0.b[var4] * var2;
               var0.a[var4] = var0.a[var4] * var2;
            }
         }
      }
   }

   public static int[] sprites() {
      byte var0 = 64;
      int var1 = var0 * 4;
      int[] var2 = new int[var1 * var0];

      for (int var3 = 0; var3 < var0; var3++) {
         for (int var4 = 0; var4 < var0; var4++) {
            float var5 = (var4 + 0.5F) / var0 * 2.0F - 1.0F;
            float var6 = (var3 + 0.5F) / var0 * 2.0F - 1.0F;
            float var7 = (var6 + 1.0F) / 2.0F;
            float var8 = (float)(0.78 * Math.pow(Math.sin(Math.PI * Math.min(1.0F, var7 * 0.92F + 0.04F)), 0.55) * (1.0 - 0.62 * var7 * var7));
            float var9 = var8 - Math.abs(var5);
            float var10 = (0.11F - var7) * 3.4F - Math.abs(var5) * 3.6F;
            float var11 = smooth(0.0F, 0.07F, var9) * (1.0F - smooth(-0.05F, 0.08F, var10));
            float var12 = 1.0F - Math.min(1.0F, Math.abs(var5) / Math.max(0.05F, var8));
            float var13 = (float)Math.exp(-var5 * var5 * 160.0F) * 0.25F * var7;
            int var14 = lerp(16095940, 16773366, 0.25F + 0.35F * var12 + 0.3F * var7 + var13 * 0.5F);
            var2[var3 * var1 + var4] = argb(var14, var11 * 0.97F);
            float var15 = (float)Math.sqrt(var5 * var5 + var6 * var6);
            var2[var3 * var1 + var0 + var4] = argb(16777215, 1.0F - smooth(0.15F, 1.0F, var15));
            float var16 = (float)Math.exp(-var15 * var15 * 9.0F) * 0.85F + (float)Math.exp(-var15 * var15 * 60.0F) * 0.6F;
            var2[var3 * var1 + 2 * var0 + var4] = argb(16777215, Math.min(1.0F, var16));
            float var17 = 1.0F - smooth(0.05F, 0.35F, Math.abs(var5));
            float var18 = smooth(-1.0F, 0.6F, var6) * (1.0F - smooth(0.85F, 1.0F, var6));
            var2[var3 * var1 + 3 * var0 + var4] = argb(16777215, var17 * var18);
         }
      }

      return var2;
   }

   private static int argb(int var0, float var1) {
      return c255(var1) << 24 | var0 & 16777215;
   }

   public static int[] smooth(int[] var0, int var1, int var2, boolean var3) {
      int[] var4 = (int[])var0.clone();
      int[] var5 = new int[var1];
      int[] var6 = new int[var1];
      int[] var7 = new int[var1];
      int[] var8 = new int[var1];

      for (int var9 = 0; var9 < var2; var9++) {
         float var10 = 90.0F - (var9 + 0.5F) / var2 * 98.0F;
         double var11 = Math.max(0.015, Math.cos(Math.toRadians(var10)));
         int var13 = (int)Math.min(64.0, Math.floor(0.55 / var11)) + (var3 ? 1 : 0);
         if (var13 > 0) {
            int var14 = var9 * var1;

            for (int var15 = 0; var15 < var1; var15++) {
               int var16 = var4[var14 + var15];
               var6[var15] = var16 >> 16 & 0xFF;
               var7[var15] = var16 >> 8 & 0xFF;
               var8[var15] = var16 & 0xFF;
            }

            int var27 = var13 * 2 + 1;
            int var29 = 0;
            int var17 = 0;
            int var18 = 0;

            for (int var19 = -var13; var19 <= var13; var19++) {
               int var20 = Math.floorMod(var19, var1);
               var29 += var6[var20];
               var17 += var7[var20];
               var18 += var8[var20];
            }

            for (int var32 = 0; var32 < var1; var32++) {
               var5[var32] = 0xFF000000 | var29 / var27 << 16 | var17 / var27 << 8 | var18 / var27;
               int var33 = Math.floorMod(var32 - var13, var1);
               int var21 = Math.floorMod(var32 + var13 + 1, var1);
               var29 += var6[var21] - var6[var33];
               var17 += var7[var21] - var7[var33];
               var18 += var8[var21] - var8[var33];
            }

            System.arraycopy(var5, 0, var4, var14, var1);
         }
      }

      if (var3) {
         int[] var22 = (int[])var4.clone();

         for (int var23 = 1; var23 < var2 - 1; var23++) {
            for (int var24 = 0; var24 < var1; var24++) {
               int var12 = var22[(var23 - 1) * var1 + var24];
               int var25 = var22[var23 * var1 + var24];
               int var26 = var22[(var23 + 1) * var1 + var24];
               int var28 = (var12 >> 16 & 0xFF) + 2 * (var25 >> 16 & 0xFF) + (var26 >> 16 & 0xFF) >> 2;
               int var30 = (var12 >> 8 & 0xFF) + 2 * (var25 >> 8 & 0xFF) + (var26 >> 8 & 0xFF) >> 2;
               int var31 = (var12 & 0xFF) + 2 * (var25 & 0xFF) + (var26 & 0xFF) >> 2;
               var4[var23 * var1 + var24] = 0xFF000000 | var28 << 16 | var30 << 8 | var31;
            }
         }
      }

      return var4;
   }

   private static int rowColor(SkyPainter.Spec var0, float var1) {
      float var2 = Math.max(0.0F, Math.min(1.0F, (var1 + 2.0F) / 82.0F));
      var2 = (float)Math.pow(var2, 0.65);
      int var3 = lerp(lerp(var0.horizon(), var0.top(), 0.5F), var0.a1(), 0.1F);
      return var2 < 0.45F ? lerp(var0.horizon(), var3, var2 / 0.45F) : lerp(var3, var0.top(), smooth(0.0F, 1.0F, (var2 - 0.45F) / 0.55F));
   }

   public static int[] opaqueBase(int[] var0, SkyPainter.Spec var1, int var2, int var3) {
      int[] var4 = new int[var0.length];

      for (int var5 = 0; var5 < var3; var5++) {
         float var6 = 90.0F - (var5 + 0.5F) / var3 * 98.0F;
         int var7 = rowColor(var1, var6);
         float var8 = smooth(-3.0F, 3.0F, var6);
         int var9 = Math.round(var8 * 255.0F);
         if (var9 != 0) {
            int var10 = var7 >> 16 & 0xFF;
            int var11 = var7 >> 8 & 0xFF;
            int var12 = var7 & 0xFF;

            for (int var13 = 0; var13 < var2; var13++) {
               int var14 = var0[var5 * var2 + var13];
               int var15 = Math.min(255, var10 + (var14 >> 16 & 0xFF));
               int var16 = Math.min(255, var11 + (var14 >> 8 & 0xFF));
               int var17 = Math.min(255, var12 + (var14 & 0xFF));
               var4[var5 * var2 + var13] = var9 << 24 | var15 << 16 | var16 << 8 | var17;
            }
         }
      }

      return var4;
   }

   public static int[] toBlendOver(int[] var0, SkyPainter.Spec var1, int var2, int var3) {
      int[] var4 = new int[var0.length];

      for (int var5 = 0; var5 < var3; var5++) {
         float var6 = 90.0F - (var5 + 0.5F) / var3 * 98.0F;
         int var7 = rowColor(var1, var6);
         float var8 = (var7 >> 16 & 0xFF) / 255.0F;
         float var9 = (var7 >> 8 & 0xFF) / 255.0F;
         float var10 = (var7 & 0xFF) / 255.0F;

         for (int var11 = 0; var11 < var2; var11++) {
            int var12 = var5 * var2 + var11;
            int var13 = var0[var12];
            float var14 = (var13 >> 16 & 0xFF) / 255.0F;
            float var15 = (var13 >> 8 & 0xFF) / 255.0F;
            float var16 = (var13 & 0xFF) / 255.0F;
            float var17 = Math.max(var14, Math.max(var15, var16));
            if (!(var17 < 0.005882353F)) {
               float var18 = Math.max(0.12F, var17);
               var18 = Math.max(
                  var18, Math.max(var14 / Math.max(0.02F, 1.0F - var8), Math.max(var15 / Math.max(0.02F, 1.0F - var9), var16 / Math.max(0.02F, 1.0F - var10)))
               );
               var18 = Math.min(1.0F, var18);
               int var19 = c255(var14 / var18 + var8);
               int var20 = c255(var15 / var18 + var9);
               int var21 = c255(var16 / var18 + var10);
               var4[var12] = c255(var18) << 24 | var19 << 16 | var20 << 8 | var21;
            }
         }
      }

      return var4;
   }

   public static int[] view(SkyPainter.Spec var0, int[] var1, int[] var2, int var3, int var4, int var5, int var6, float var7, float var8, float var9, int var10) {
      int[] var11 = new int[var5 * var6];
      double var12 = var6 / 2.0 / Math.tan(Math.toRadians(var9 / 2.0F));
      double var14 = Math.toRadians(var7);
      double var16 = Math.toRadians(var8);
      double var18 = -Math.sin(var14) * Math.cos(var16);
      double var20 = -Math.sin(var16);
      double var22 = Math.cos(var14) * Math.cos(var16);
      double var24 = -Math.cos(var14);
      double var26 = 0.0;
      double var28 = -Math.sin(var14);
      double var30 = var26 * var22 - var28 * var20;
      double var32 = var28 * var18 - var24 * var22;
      double var34 = var24 * var20 - var26 * var18;
      Random var36 = new Random(var0.kind().hashCode());
      double var37 = var36.nextDouble() * 10.0;

      for (int var39 = 0; var39 < var6; var39++) {
         for (int var40 = 0; var40 < var5; var40++) {
            double var41 = var40 + 0.5 - var5 / 2.0;
            double var43 = -(var39 + 0.5 - var6 / 2.0);
            double var45 = var18 * var12 + var24 * var41 + var30 * var43;
            double var47 = var20 * var12 + var26 * var41 + var32 * var43;
            double var49 = var22 * var12 + var28 * var41 + var34 * var43;
            double var51 = Math.sqrt(var45 * var45 + var47 * var47 + var49 * var49);
            var45 /= var51;
            var47 /= var51;
            var49 /= var51;
            double var53 = Math.toDegrees(Math.asin(var47));
            double var55 = Math.toDegrees(Math.atan2(-var45, var49));
            if (var55 < 0.0) {
               var55 += 360.0;
            }

            int var57 = rowColor(var0, (float)var53);
            float var58 = (var57 >> 16 & 0xFF) / 255.0F;
            float var59 = (var57 >> 8 & 0xFF) / 255.0F;
            float var60 = (var57 & 0xFF) / 255.0F;
            float var61 = (float)(var55 / 360.0 * var3);
            float var62 = (float)((90.0 - var53) / 98.0 * var4);

            for (int[] var66 : new int[][]{var1, var2}) {
               if (var66 != null) {
                  int var67 = sample(var66, var3, var4, var61, var62);
                  var58 = Math.min(1.0F, var58 + (var67 >> 16 & 0xFF) / 255.0F);
                  var59 = Math.min(1.0F, var59 + (var67 >> 8 & 0xFF) / 255.0F);
                  var60 = Math.min(1.0F, var60 + (var67 & 0xFF) / 255.0F);
               }
            }

            double var77 = var55 / 360.0 * 6.2832;
            double var78 = 1.5 + 2.2 * Math.sin(var77 * 3.0 + var37) + 1.2 * Math.sin(var77 * 7.0 + var37 * 2.0);
            double var79 = Math.max(0.0, Math.sin(var77 * 95.0 + var37)) * Math.max(0.0, Math.sin(var77 * 37.0 + var37 * 3.0)) * 4.5;
            if (var53 < var78 + var79) {
               var58 = 0.024F;
               var59 = 0.016F;
               var60 = 0.04F;
            }

            float var69 = Math.max(0.0F, (var39 - var6 * 0.6F) / (var6 * 0.4F));
            var69 = var69 * var69 * 0.55F;
            var58 *= 1.0F - var69;
            var59 *= 1.0F - var69;
            var60 *= 1.0F - var69;
            float var70 = cornerAlpha(var40, var39, var5, var6, var10);
            var11[var39 * var5 + var40] = c255(var70) << 24 | c255(var58) << 16 | c255(var59) << 8 | c255(var60);
         }
      }

      return var11;
   }

   private static int sample(int[] var0, int var1, int var2, float var3, float var4) {
      var3 -= 0.5F;
      var4 -= 0.5F;
      int var5 = (int)Math.floor(var3);
      int var6 = (int)Math.floor(var4);
      float var7 = var3 - var5;
      float var8 = var4 - var6;
      int var9 = px(var0, var1, var2, var5, var6);
      int var10 = px(var0, var1, var2, var5 + 1, var6);
      int var11 = px(var0, var1, var2, var5, var6 + 1);
      int var12 = px(var0, var1, var2, var5 + 1, var6 + 1);
      int var13 = 0;

      for (int var14 = 0; var14 <= 24; var14 += 8) {
         float var15 = (var9 >>> var14 & 0xFF) * (1.0F - var7) + (var10 >>> var14 & 0xFF) * var7;
         float var16 = (var11 >>> var14 & 0xFF) * (1.0F - var7) + (var12 >>> var14 & 0xFF) * var7;
         var13 |= Math.round(var15 * (1.0F - var8) + var16 * var8) << var14;
      }

      return var13;
   }

   private static int px(int[] var0, int var1, int var2, int var3, int var4) {
      var3 = Math.floorMod(var3, var1);
      var4 = Math.max(0, Math.min(var2 - 1, var4));
      return var0[var4 * var1 + var3];
   }

   private static float cornerAlpha(int var0, int var1, int var2, int var3, int var4) {
      if (var4 <= 0) {
         return 1.0F;
      } else {
         float var5 = var0 < var4 ? var4 : (var0 >= var2 - var4 ? var2 - var4 : -1.0F);
         float var6 = var1 < var4 ? var4 : (var1 >= var3 - var4 ? var3 - var4 : -1.0F);
         if (!(var5 < 0.0F) && !(var6 < 0.0F)) {
            float var7 = (float)Math.hypot(var0 + 0.5F - var5, var1 + 0.5F - var6);
            return Math.max(0.0F, Math.min(1.0F, var4 - var7 + 0.5F));
         } else {
            return 1.0F;
         }
      }
   }

   static float smooth(float var0, float var1, float var2) {
      float var3 = Math.max(0.0F, Math.min(1.0F, (var2 - var0) / (var1 - var0)));
      return var3 * var3 * (3.0F - 2.0F * var3);
   }

   static int lerp(int var0, int var1, float var2) {
      var2 = Math.max(0.0F, Math.min(1.0F, var2));
      int var3 = (int)((var0 >> 16 & 0xFF) + ((var1 >> 16 & 0xFF) - (var0 >> 16 & 0xFF)) * var2);
      int var4 = (int)((var0 >> 8 & 0xFF) + ((var1 >> 8 & 0xFF) - (var0 >> 8 & 0xFF)) * var2);
      int var5 = (int)((var0 & 0xFF) + ((var1 & 0xFF) - (var0 & 0xFF)) * var2);
      return var3 << 16 | var4 << 8 | var5;
   }

   private static final class Buf {
      final int w;
      final int h;
      final float[] r;
      final float[] g;
      final float[] b;
      final float[] a;
      final float[] el;
      final float[] sinE;
      final float[] cosE;
      final float[] sinA;
      final float[] cosA;

      Buf(int var1, int var2) {
         this.w = var1;
         this.h = var2;
         this.r = new float[var1 * var2];
         this.g = new float[var1 * var2];
         this.b = new float[var1 * var2];
         this.a = new float[var1 * var2];
         this.el = new float[var2];
         this.sinE = new float[var2];
         this.cosE = new float[var2];

         for (int var3 = 0; var3 < var2; var3++) {
            this.el[var3] = 90.0F - (var3 + 0.5F) / var2 * 98.0F;
            double var4 = Math.toRadians(this.el[var3]);
            this.sinE[var3] = (float)Math.sin(var4);
            this.cosE[var3] = (float)Math.cos(var4);
         }

         this.sinA = new float[var1];
         this.cosA = new float[var1];

         for (int var6 = 0; var6 < var1; var6++) {
            double var7 = Math.toRadians((var6 + 0.5) / var1 * 360.0);
            this.sinA[var6] = (float)Math.sin(var7);
            this.cosA[var6] = (float)Math.cos(var7);
         }
      }

      float dx(int var1, int var2) {
         return -this.sinA[var1] * this.cosE[var2];
      }

      float dy(int var1) {
         return this.sinE[var1];
      }

      float dz(int var1, int var2) {
         return this.cosA[var1] * this.cosE[var2];
      }

      void over(int var1, int var2, float var3) {
         if (!(var3 <= 5.0E-4F)) {
            if (var3 > 1.0F) {
               var3 = 1.0F;
            }

            float var4 = 1.0F - var3;
            this.r[var1] = (var2 >> 16 & 0xFF) / 255.0F * var3 + this.r[var1] * var4;
            this.g[var1] = (var2 >> 8 & 0xFF) / 255.0F * var3 + this.g[var1] * var4;
            this.b[var1] = (var2 & 0xFF) / 255.0F * var3 + this.b[var1] * var4;
            this.a[var1] = var3 + this.a[var1] * var4;
         }
      }

      void add(int var1, int var2, float var3) {
         if (!(var3 <= 5.0E-4F)) {
            this.r[var1] = Math.min(1.0F, this.r[var1] + (var2 >> 16 & 0xFF) / 255.0F * var3);
            this.g[var1] = Math.min(1.0F, this.g[var1] + (var2 >> 8 & 0xFF) / 255.0F * var3);
            this.b[var1] = Math.min(1.0F, this.b[var1] + (var2 & 0xFF) / 255.0F * var3);
            this.a[var1] = Math.min(1.0F, this.a[var1] + var3 * 0.85F);
         }
      }

      int[] toEmissive() {
         int[] var1 = new int[this.w * this.h];

         for (int var2 = 0; var2 < var1.length; var2++) {
            var1[var2] = 0xFF000000 | SkyPainter.c255(this.r[var2]) << 16 | SkyPainter.c255(this.g[var2]) << 8 | SkyPainter.c255(this.b[var2]);
         }

         return var1;
      }

      int[] toArgb() {
         int[] var1 = new int[this.w * this.h];

         for (int var2 = 0; var2 < var1.length; var2++) {
            float var3 = this.a[var2];
            if (var3 <= 0.002F) {
               var1[var2] = 0;
            } else {
               int var4 = SkyPainter.c255(this.r[var2] / var3);
               int var5 = SkyPainter.c255(this.g[var2] / var3);
               int var6 = SkyPainter.c255(this.b[var2] / var3);
               var1[var2] = SkyPainter.c255(var3) << 24 | var4 << 16 | var5 << 8 | var6;
            }
         }

         return var1;
      }
   }

   private interface Field {
      float at(float var1, float var2, float var3);
   }

   private static final class Low {
      final int lw;
      final int lh;
      final int div;
      final float[] v;
      final SkyPainter.Buf b;

      Low(SkyPainter.Buf var1, int var2, SkyPainter.Field var3) {
         this.b = var1;
         this.div = var2;
         this.lw = Math.max(4, var1.w / var2);
         this.lh = Math.max(2, var1.h / var2 + 1);
         this.v = new float[this.lw * this.lh];

         for (int var4 = 0; var4 < this.lh; var4++) {
            float var5 = 90.0F - (var4 * var2 + var2 * 0.5F) / var1.h * 98.0F;
            double var6 = Math.toRadians(var5);
            float var8 = (float)Math.cos(var6);
            float var9 = (float)Math.sin(var6);

            for (int var10 = 0; var10 < this.lw; var10++) {
               double var11 = Math.toRadians((var10 + 0.5) / this.lw * 360.0);
               this.v[var4 * this.lw + var10] = var3.at((float)(-Math.sin(var11) * var8), var9, (float)(Math.cos(var11) * var8));
            }
         }
      }

      float get(int var1, int var2) {
         float var3 = (var1 + 0.5F) / this.div - 0.5F;
         float var4 = (var2 + 0.5F) / this.div - 0.5F;
         int var5 = (int)Math.floor(var3);
         int var6 = (int)Math.floor(var4);
         float var7 = var3 - var5;
         float var8 = var4 - var6;
         int var9 = Math.floorMod(var5, this.lw);
         int var10 = Math.floorMod(var5 + 1, this.lw);
         int var11 = Math.max(0, Math.min(this.lh - 1, var6));
         int var12 = Math.max(0, Math.min(this.lh - 1, var6 + 1));
         float var13 = this.v[var11 * this.lw + var9] + (this.v[var11 * this.lw + var10] - this.v[var11 * this.lw + var9]) * var7;
         float var14 = this.v[var12 * this.lw + var9] + (this.v[var12 * this.lw + var10] - this.v[var12 * this.lw + var9]) * var7;
         return var13 + (var14 - var13) * var8;
      }
   }

   static final class Noise {
      private Noise() {
      }

      static float hash(int var0) {
         var0 = var0 << 13 ^ var0;
         var0 = var0 * (var0 * var0 * 15731 + 789221) + 1376312589;
         return (var0 & 2147483647) / 2.1474836E9F;
      }

      private static float h3(int var0, int var1, int var2) {
         return hash(var0 * 73856093 ^ var1 * 19349663 ^ var2 * 83492791);
      }

      static float noise(float var0, float var1, float var2) {
         int var3 = (int)Math.floor(var0);
         int var4 = (int)Math.floor(var1);
         int var5 = (int)Math.floor(var2);
         float var6 = var0 - var3;
         float var7 = var1 - var4;
         float var8 = var2 - var5;
         float var9 = fade(var6);
         float var10 = fade(var7);
         float var11 = fade(var8);
         float var12 = h3(var3, var4, var5);
         float var13 = h3(var3 + 1, var4, var5);
         float var14 = h3(var3, var4 + 1, var5);
         float var15 = h3(var3 + 1, var4 + 1, var5);
         float var16 = h3(var3, var4, var5 + 1);
         float var17 = h3(var3 + 1, var4, var5 + 1);
         float var18 = h3(var3, var4 + 1, var5 + 1);
         float var19 = h3(var3 + 1, var4 + 1, var5 + 1);
         float var20 = var12 + (var13 - var12) * var9;
         float var21 = var14 + (var15 - var14) * var9;
         float var22 = var16 + (var17 - var16) * var9;
         float var23 = var18 + (var19 - var18) * var9;
         float var24 = var20 + (var21 - var20) * var10;
         float var25 = var22 + (var23 - var22) * var10;
         return var24 + (var25 - var24) * var11;
      }

      static float fbm(float var0, float var1, float var2, int var3) {
         float var4 = 0.0F;
         float var5 = 0.5F;
         float var6 = 0.0F;

         for (int var7 = 0; var7 < var3; var7++) {
            var4 += noise(var0, var1, var2) * var5;
            var6 += var5;
            var0 = var0 * 2.03F + 17.1F;
            var1 = var1 * 2.03F + 3.7F;
            var2 = var2 * 2.03F + 9.3F;
            var5 *= 0.5F;
         }

         return var4 / var6;
      }

      private static float fade(float var0) {
         return var0 * var0 * var0 * (var0 * (var0 * 6.0F - 15.0F) + 10.0F);
      }
   }

   public record Spec(String kind, int top, int horizon, int a1, int a2, float stars, boolean aurora, int moon, String weather) {
   }
}
