package dev.neonlyrics.sky;

import dev.neonlyrics.config.Config;
import java.util.Random;

public final class LiveSky {
   public static final LiveSky MAIN = new LiveSky();
   private SkyPreset P;
   private float tr;
   private float tg;
   private float tb;
   private float hr;
   private float hg;
   private float hb;
   private float ar1;
   private float ag1;
   private float ab1;
   private float ar2;
   private float ag2;
   private float ab2;
   private double T;
   private float pattern = 1.0F;
   private float lively = 0.6F;
   private float breath = 1.0F;
   private float auroraOn = 1.0F;
   private double sunX;
   private double sunY;
   private double sunZ;
   private LiveSky.Body body;
   private float R;
   private float G;
   private float B;
   private static final double[][] PILLAR = new double[16][];

   public static LiveSky.Body body(SkyPreset var0) {
      return switch (var0) {
         case SYNTHWAVE -> new LiveSky.Body(180.0, 12.0, 12.0, 16765803, 3);
         case GOLDEN_HOUR -> new LiveSky.Body(200.0, 4.0, 3.6, 16761466, 0);
         case COTTON_CANDY -> new LiveSky.Body(120.0, 38.0, 2.4, 16774368, 0);
         case SAKURA -> new LiveSky.Body(60.0, 9.0, 2.6, 16769774, 0);
         case COSMOS -> new LiveSky.Body(60.0, 28.0, 5.0, 12166911, 1);
         case BLACK_HOLE -> new LiveSky.Body(165.0, 24.0, 5.2, 16757850, 2);
         case BLOOD_MOON -> new LiveSky.Body(200.0, 22.0, 4.4, 16728120, 1);
         case FROST -> new LiveSky.Body(210.0, 30.0, 2.6, var0.moon, 1);
         default -> var0.moon != 0 ? new LiveSky.Body(200.0, 26.0, 2.4, var0.moon, 1) : null;
      };
   }

   public static boolean synthGap(double var0, double var2) {
      if (var0 > 0.45) {
         return false;
      }

      double var4 = -var0 * 6.0 + var2 * 0.12;
      var4 -= Math.floor(var4);
      return var4 < 0.06 + 0.46 * Math.max(0.0, Math.min(1.0, (0.45 - var0) / 1.45));
   }

   public void prepare(SkyPreset var1, int var2, int var3, double var4, Config var6) {
      this.P = var1;
      this.tr = r(var1.top);
      this.tg = g(var1.top);
      this.tb = b(var1.top);
      this.hr = r(var1.horizon);
      this.hg = g(var1.horizon);
      this.hb = b(var1.horizon);
      this.ar1 = r(var2);
      this.ag1 = g(var2);
      this.ab1 = b(var2);
      this.ar2 = r(var3);
      this.ag2 = g(var3);
      this.ab2 = b(var3);
      this.T = var4 * 2.2;
      if (var6 != null) {
         this.pattern = Math.max(0.2F, Math.min(1.5F, var6.skyPattern));
         this.lively = Math.max(0.0F, Math.min(1.0F, var6.skyLively));
         this.breath = var6.skyBreath <= 0.01F ? 1.0F : 0.85F + 0.15F * (float)Math.sin(var4 * 0.45 * Math.max(0.2F, var6.skyBreath));
         this.auroraOn = var6.fxAurora ? 1.0F : 0.0F;
      }

      this.body = body(var1);
      if (this.body != null) {
         double var7 = Math.toRadians(this.body.az());
         double var9 = Math.toRadians(this.body.el());
         this.sunX = -Math.sin(var7) * Math.cos(var9);
         this.sunY = Math.sin(var9);
         this.sunZ = Math.cos(var7) * Math.cos(var9);
      }
   }

   public static int synthColor(double var0) {
      float var2 = (float)Math.max(0.0, Math.min(1.0, (var0 + 1.0) / 2.0));
      int var3 = 16727450;
      int var4 = 16747100;
      int var5 = 16769658;
      return var2 < 0.5F ? mix(var3, var4, var2 * 2.0F) : mix(var4, var5, (var2 - 0.5F) * 2.0F);
   }

   private static int mix(int var0, int var1, float var2) {
      int var3 = Math.round((var0 >> 16 & 0xFF) + ((var1 >> 16 & 0xFF) - (var0 >> 16 & 0xFF)) * var2);
      int var4 = Math.round((var0 >> 8 & 0xFF) + ((var1 >> 8 & 0xFF) - (var0 >> 8 & 0xFF)) * var2);
      int var5 = Math.round((var0 & 0xFF) + ((var1 & 0xFF) - (var0 & 0xFF)) * var2);
      return var3 << 16 | var4 << 8 | var5;
   }

   private static float r(int var0) {
      return (var0 >> 16 & 0xFF) / 255.0F;
   }

   private static float g(int var0) {
      return (var0 >> 8 & 0xFF) / 255.0F;
   }

   private static float b(int var0) {
      return (var0 & 0xFF) / 255.0F;
   }

   private void add(float var1, float var2, float var3, float var4) {
      this.R += var1 * var4;
      this.G += var2 * var4;
      this.B += var3 * var4;
   }

   private void over(float var1, float var2, float var3, float var4) {
      var4 = Math.max(0.0F, Math.min(1.0F, var4));
      this.R = this.R + (var1 - this.R) * var4;
      this.G = this.G + (var2 - this.G) * var4;
      this.B = this.B + (var3 - this.B) * var4;
   }

   public int color(double var1, double var3, double var5, float var7) {
      SkyPreset var8 = this.P;
      float var9 = var7;
      double var10 = Math.toDegrees(Math.atan2(-var1, var5));
      if (var10 < 0.0) {
         var10 += 360.0;
      }

      double var12 = this.T;
      float var14 = (float)Math.pow(clamp(var9 / 72.0F), 0.55);
      this.R = this.hr + (this.tr - this.hr) * var14;
      this.G = this.hg + (this.tg - this.hg) * var14;
      this.B = this.hb + (this.tb - this.hb) * var14;
      if (var9 < 0.0F) {
         float var15 = clamp(-var9 / 6.0F) * 0.55F;
         this.R *= 1.0F - var15;
         this.G *= 1.0F - var15;
         this.B *= 1.0F - var15;
      }

      float var19 = (float)Math.exp(-Math.max(0.0F, var9) / 9.0);
      switch (var8) {
         case SYNTHWAVE:
            this.synth(var9);
            break;
         case GOLDEN_HOUR:
            this.add(1.0F, 0.55F, 0.28F, 0.5F * var19);
            this.clouds(var1, var3, var5, 0.54F, 0.85F, 16761994, 5909598, 0.02);
            break;
         case COTTON_CANDY:
            this.add(1.0F, 0.8F, 0.92F, 0.14F * var19);
            this.clouds(var1, var3, var5, 0.6F, 0.92F, 16777215, 15907032, 0.025);
            break;
         case SAKURA:
            this.add(1.0F, 0.61F, 0.8F, 0.4F * var19);
            this.clouds(var1, var3, var5, 0.57F, 0.6F, 16771314, 13144248, 0.02);
            break;
         case COSMOS:
            this.galaxyBand(var1, var3, var5);
            this.add(this.ar2, this.ag2, this.ab2, 0.08F * var19);
            break;
         case BLACK_HOLE:
            this.milkyWay(var1, var3, var5, 0.22F);
            this.accretion(var10, var9);
            break;
         case BLOOD_MOON:
            this.add(1.0F, 0.23F, 0.16F, 0.25F * var19);
            this.clouds(var1, var3, var5, 0.6F, 0.75F, 12070970, 1704968, 0.03);
            break;
         case FROST:
            this.iceBands(var10, var9);
            this.curtains(var10, var9, 0.45F);
            this.milkyWay(var1, var3, var5, 0.2F);
            break;
         case AURORA:
         case CUSTOM:
            this.milkyWay(var1, var3, var5, 0.16F);
            this.curtains(var10, var9, 1.0F);
            this.add(this.ar1, this.ag1, this.ab1, 0.06F * var19);
            break;
         case NEBULA:
            this.swirl(var1, var3, var5);
            this.add(this.ar1, this.ag1, this.ab1, 0.1F * var19);
            break;
         case NEON_CITY:
            this.cityGlow(var10, var9);
            this.clouds(var1, var3, var5, 0.62F, 0.55F, 6958202, 789022, 0.03);
            break;
         case MIDNIGHT:
            this.milkyWay(var1, var3, var5, 0.5F);
            this.add(0.16F, 0.23F, 0.56F, 0.18F * var19);
            break;
         case DEEP_SEA:
            this.caustics(var1, var3, var5, var9);
            this.add(this.ar2, this.ag2, this.ab2, 0.16F * var19);
            break;
         case STORM:
            this.clouds(var1, var3, var5, 0.3F, 0.97F, 5923960, 1185311, 0.11);
            break;
         case INFERNO:
            this.lava(var1, var3, var5, var9);
            this.add(1.0F, 0.35F, 0.08F, 0.35F * var19);
            break;
         case MATRIX:
            this.code(var10, var9);
            break;
         case PRISM:
            this.prism(var10, var9);
            break;
         case PULSAR:
            this.milkyWay(var1, var3, var5, 0.25F);
            this.pulsar(var1, var3, var5);
            break;
         case AQUARELL:
            this.paint(var1, var3, var5, var9);
      }

      if (this.body != null) {
         this.bodyGlow(var1, var3, var5, var10, var9);
      }

      int var16 = Math.round(soft(this.R) * 255.0F);
      int var17 = Math.round(soft(this.G) * 255.0F);
      int var18 = Math.round(soft(this.B) * 255.0F);
      return var16 << 16 | var17 << 8 | var18;
   }

   private static float soft(float var0) {
      if (var0 <= 0.8F) {
         return Math.max(0.0F, var0);
      }

      float var1 = var0 - 0.8F;
      return 0.8F + 0.2F * (var1 / (var1 + 0.2F));
   }

   private void synth(float var1) {
      float var2 = (float)Math.exp(-Math.max(0.0F, var1) / 9.0);
      this.add(this.ar1, this.ag1, this.ab1, 0.5F * var2);
      double var3 = Math.exp(-Math.pow((var1 - 0.7) / 0.6, 2.0));
      float var5 = 0.75F + 0.25F * (float)Math.sin(this.T * 1.6);
      this.add(this.ar2, this.ag2, this.ab2, (float)(var3 * 0.85 * var5));
      this.add(this.ar2, this.ag2, this.ab2, 0.08F * (float)Math.exp(-Math.max(0.0F, var1) / 3.0));
      if (var1 > 20.0F) {
         double var6 = 0.5 + 0.5 * Math.sin(var1 * 1.3 - this.T * 0.5);
         this.add(this.ar1, this.ag1, this.ab1, (float)(0.03 * var6 * smooth(20.0, 40.0, var1) * this.pattern));
      }
   }

   private void curtains(double var1, float var3, float var4) {
      if (!(this.auroraOn <= 0.0F) && !(var3 < -2.0F)) {
         double var5 = Math.toRadians(var1);
         double var7 = this.T * (0.3 + this.lively * 0.45);

         for (int var9 = 0; var9 < 2; var9++) {
            double var10 = 17 + var9 * 11 + 7.0 * Math.sin(var5 * 2.0 + var7 * 0.22 + var9 * 1.7) + 3.0 * Math.sin(var5 * 5.0 - var7 * 0.35 + var9);
            double var12 = var3 - var10;
            double var14 = var12 < 0.0 ? Math.exp(-var12 * var12 / 3.0) : Math.exp(-var12 / (15.0 + 6 * var9));
            if (!(var14 < 0.01)) {
               double var16 = 0.55 + 0.45 * Math.sin(var5 * 9.0 + 2.5 * Math.sin(var5 * 2.0 + var7 * 0.45) + var7 * 0.7 + var9 * 3);
               double var18 = smooth(0.25, 0.65, noise(Math.cos(var5) * 0.6876 + var9 * 5, Math.sin(var5) * 0.6876 + var7 * 0.04, var9 + 0.3));
               float var20 = (float)(var14 * (0.35 + 0.65 * var16) * (0.25 + 0.75 * var18) * (var9 == 0 ? 1.0 : 0.6) * var4 * this.pattern * this.breath);
               float var21 = clamp((float)(var12 / 18.0));
               this.add(
                  this.ar1 + (this.ar2 - this.ar1) * var21, this.ag1 + (this.ag2 - this.ag1) * var21, this.ab1 + (this.ab2 - this.ab1) * var21, var20 * 0.85F
               );
            }
         }
      }
   }

   private void swirl(double var1, double var3, double var5) {
      double var7 = this.T * 0.012 * (0.5 + this.lively);
      double var9 = Math.cos(var7);
      double var11 = Math.sin(var7);
      double var13 = var1 * var9 - var5 * var11;
      double var15 = var1 * var11 + var5 * var9;
      double var17 = fbm(var13 * 1.2 + 3.0, var3 * 1.2, var15 * 1.2, 2);
      double var19 = fbm(var13 * 1.2, var3 * 1.2 + 7.0, var15 * 1.2 + 1.0, 2);
      double var21 = fbm(var13 * 1.8 + var17 * 2.2, var3 * 1.8 + var19 * 2.2, var15 * 1.8 - var17 * 1.5 + var7 * 2.0, 3);
      double var23 = smooth(0.36, 0.78, var21);
      double var25 = smooth(0.58, 0.86, var21);
      if (!(var23 < 0.01)) {
         float var27 = (float)smooth(0.3, 0.7, var17);
         float var28 = (float)(var23 * 0.6 * this.pattern * this.breath);
         this.add(this.ar2 + (this.ar1 - this.ar2) * var27, this.ag2 + (this.ag1 - this.ag2) * var27, this.ab2 + (this.ab1 - this.ab2) * var27, var28);
         this.add(1.0F, 0.92F, 1.0F, (float)(var25 * 0.22 * this.pattern));
      }
   }

   private void accretion(double var1, float var3) {
      LiveSky.Body var4 = this.body;
      if (var4 != null) {
         double var5 = ((var1 - var4.az()) % 360.0 + 540.0) % 360.0 - 180.0;
         var5 *= Math.cos(Math.toRadians(var3));
         double var7 = var3 - var4.el();
         double var9 = var4.radius();
         double var11 = 0.2;
         double var13 = Math.sqrt(var5 * var5 + Math.pow(var7 / var11, 2.0));
         double var15 = Math.sqrt(var5 * var5 + var7 * var7);
         if (var15 > var9) {
            this.add(this.ar1, this.ag1, this.ab1, (float)(Math.exp(-(var15 - var9) / (var9 * 0.9)) * 0.35 * this.pattern));
         }

         if (!(var13 < var9 * 1.1) && !(var13 > var9 * 6.5)) {
            double var17 = smooth(var9 * 1.1, var9 * 1.35, var13) * (1.0 - smooth(var9 * 3.0, var9 * 6.5, var13));
            double var19 = Math.atan2(var7 / var11, var5);
            double var21 = var19 - this.T * 0.3;
            double var23 = 0.5 + 0.5 * noise(Math.cos(var21) * 3.0, Math.sin(var21) * 3.0 + var13 * 0.3, 4.2);
            double var25 = 1.0 + 0.75 * (-var5 / var13);
            float var27 = (float)(var17 * var23 * var25 * this.pattern);
            float var28 = clamp((float)(1.0 - (var13 - var9 * 1.1) / (var9 * 3.0)));
            this.add(
               this.ar2 + (1.0F - this.ar2) * var28 * 0.7F,
               this.ag2 + (this.ag1 - this.ag2) * var28 + (1.0F - this.ag1) * var28 * 0.3F,
               this.ab2 + (this.ab1 - this.ab2) * var28,
               var27 * 1.1F
            );
         }
      }
   }

   private void galaxyBand(double var1, double var3, double var5) {
      double var7 = this.T * 0.004;
      double var9 = 0.36 * Math.cos(var7) - 0.73 * Math.sin(var7);
      double var11 = 0.58;
      double var13 = 0.36 * Math.sin(var7) + 0.73 * Math.cos(var7);
      double var15 = Math.sqrt(var9 * var9 + var11 * var11 + var13 * var13);
      double var17 = (var1 * var9 + var3 * var11 + var5 * var13) / var15;
      double var19 = Math.exp(-var17 * var17 / 0.04);
      if (!(var19 < 0.01)) {
         double var21 = Math.exp(-var17 * var17 / 0.005);
         double var23 = fbm(var1 * 2.2 + 5.0, var3 * 2.2, var5 * 2.2, 2);
         this.add(this.ar1, this.ag1, this.ab1, (float)(var19 * smooth(0.45, 0.75, var23) * 0.22 * this.pattern));
         double var25 = fbm(var1 * 5.0 + 2.0, var3 * 5.0, var5 * 5.0 - 1.0, 3);
         double var27 = Math.exp(-var17 * var17 / 0.0025) * smooth(0.45, 0.7, fbm(var1 * 11.0, var3 * 11.0 + 3.0, var5 * 11.0, 2));
         float var29 = (float)(var19 * (0.4 + 0.8 * var25) * (1.0 - 0.7 * var27) * this.pattern);
         float var30 = (float)smooth(0.3, 0.8, var25);
         this.add(this.ar2 + (this.ar1 - this.ar2) * var30, this.ag2 + (this.ag1 - this.ag2) * var30, this.ab2 + (this.ab1 - this.ab2) * var30, var29 * 0.45F);
         this.add(1.0F, 0.9F, 0.8F, (float)(var21 * (1.0 - 0.7 * var27) * 0.35 * this.pattern));
      }
   }

   private void cityGlow(double var1, float var3) {
      float var4 = (float)Math.exp(-Math.max(0.0F, var3) / 10.0);
      this.add(this.ar1, this.ag1, this.ab1, 0.42F * var4);
      this.add(this.ar2, this.ag2, this.ab2, 0.22F * (float)Math.exp(-Math.max(0.0F, var3) / 3.5));
      double var5 = Math.toRadians(var1);
      double var7 = smooth(0.4, 0.8, noise(Math.cos(var5) * 2.865, 0.5 + Math.sin(var5) * 2.865, 1.3));
      this.add(this.ar1, this.ag1, this.ab1, (float)(0.22 * var7 * Math.exp(-Math.max(0.0F, var3) / 5.0)));
      if (!(var3 < -1.0F)) {
         for (int var9 = 0; var9 < 3; var9++) {
            double var10 = 60 + var9 * 120 + 28.0 * Math.sin(this.T * 0.25 + var9 * 2.1);
            double var12 = 18.0 * Math.sin(this.T * 0.18 + var9);
            double var14 = ((var1 - var10 - var12 * var3 / 60.0) % 360.0 + 540.0) % 360.0 - 180.0;
            double var16 = 1.8 + var3 * 0.06;
            double var18 = Math.exp(-(var14 * var14) / (var16 * var16)) * Math.exp(-Math.max(0.0F, var3) / 50.0) * smooth(-1.0, 3.0, var3);
            if (!(var18 < 0.005)) {
               float var20 = (float)(var18 * 0.5 * this.pattern);
               if (var9 == 1) {
                  this.add(this.ar2, this.ag2, this.ab2, var20);
               } else if (var9 == 0) {
                  this.add(this.ar1, this.ag1, this.ab1, var20);
               } else {
                  this.add(0.9F, 0.9F, 1.0F, var20 * 0.8F);
               }
            }
         }
      }
   }

   private void iceBands(double var1, float var3) {
      if (!(var3 < 2.0F)) {
         double var4 = Math.toRadians(var1);
         double var6 = noise(Math.cos(var4) * 1.719, this.T * 0.05 + Math.sin(var4) * 1.719, 2.2);
         double var8 = Math.pow(0.5 + 0.5 * Math.sin(var3 * 0.35 + var6 * 3.0 - this.T * 0.15), 6.0);
         this.add(this.ar1, this.ag1, this.ab1, (float)(0.16 * var8 * smooth(5.0, 25.0, var3) * this.pattern));
      }
   }

   private void lava(double var1, double var3, double var5, float var7) {
      double var8 = this.T * 0.03 * (0.6 + this.lively);
      double var10 = fbm(var1 * 1.6 + 3.0, var3 * 1.6 - var8, var5 * 1.6, 2);
      double var12 = fbm(var1 * 2.4 + var10 * 1.6 + var8, var3 * 2.4 - var8 * 0.7, var5 * 2.4 - var10 * 1.2, 3);
      double var14 = Math.pow(1.0 - Math.abs(2.0 * var12 - 1.0), 6.0);
      double var16 = 0.75 + 0.25 * Math.sin(this.T * 1.3 + var12 * 12.0);
      float var18 = (float)(var14 * var16 * this.pattern * this.breath * (0.4 + 0.6 * smooth(-2.0, 20.0, var7)));
      this.add(this.ar1, this.ag1, this.ab1, var18 * 1.1F);
      this.add(this.ar2, this.ag2, this.ab2, (float)(Math.pow(var14, 3.0) * 0.8 * this.pattern));
      this.add(0.5F, 0.06F, 0.02F, (float)(smooth(0.45, 0.8, var12) * 0.18));
   }

   private void code(double var1, float var3) {
      if (!(var3 < -2.0F)) {
         double var4 = 4.0;
         double var6 = Math.floor(var1 / var4);
         double var8 = hash3((int)var6, 7, 3);
         double var10 = hash3((int)var6, 11, 5);
         double var12 = 0.25 + var8 * 0.5 * (0.5 + this.lively);
         double var14 = 90.0 - (this.T * var12 * 0.35 + var10) % 1.0 * 110.0;
         double var16 = var14 - var3;
         double var18 = Math.floor(var3 / 3.0);
         double var20 = hash3((int)var6, (int)var18, (int)Math.floor(this.T * 3.0 + var8 * 7.0)) > 0.25 ? 1.0 : 0.35;
         double var22 = Math.abs(var1 / var4 - var6 - 0.5) < 0.38 ? 1.0 : 0.2;
         if (var16 < 0.0) {
            double var24 = Math.exp(var16 / 14.0) * var20 * var22;
            this.add(this.ar1, this.ag1, this.ab1, (float)(var24 * 0.75 * this.pattern));
         }

         if (Math.abs(var16) < 2.2) {
            this.add(this.ar2, this.ag2, this.ab2, (float)((1.0 - Math.abs(var16) / 2.2) * var22 * this.pattern));
         }

         this.add(this.ar1, this.ag1, this.ab1, (float)(0.05 * var20 * var22 * (0.5 + 0.5 * Math.sin(this.T + var6))));
      }
   }

   private void prism(double var1, float var3) {
      double var4 = Math.toRadians(var1);
      double var6 = this.T * (0.15 + this.lively * 0.2);
      double var8 = Math.pow(0.5 + 0.5 * Math.sin(var4 * 7.0 + var6 + var3 * 0.035), 14.0);
      double var10 = Math.pow(0.5 + 0.5 * Math.sin(var4 * 11.0 - var6 * 1.3 + var3 * 0.02 + 1.0), 18.0);
      double var12 = smooth(0.0, 75.0, var3);
      double var14 = (var1 / 360.0 + this.T * 0.02) % 1.0;
      int var16 = hsv(var14, 0.6, 1.0);
      int var17 = hsv((var14 + 0.33) % 1.0, 0.6, 1.0);
      this.add(r(var16), g(var16), b(var16), (float)(var8 * var12 * 0.5 * this.pattern * this.breath));
      this.add(r(var17), g(var17), b(var17), (float)(var10 * var12 * 0.35 * this.pattern));
      double var18 = Math.exp(-(90.0F - var3) / 18.0);
      this.add(1.0F, 0.95F, 1.0F, (float)(var18 * 0.25 * this.pattern));
   }

   private void pulsar(double var1, double var3, double var5) {
      double var7 = Math.toRadians(30.0);
      double var9 = Math.toRadians(50.0);
      double var11 = -Math.sin(var7) * Math.cos(var9);
      double var13 = Math.sin(var9);
      double var15 = Math.cos(var7) * Math.cos(var9);
      double var17 = var1 * var11 + var3 * var13 + var5 * var15;
      double var19 = Math.toDegrees(Math.acos(Math.max(-1.0, Math.min(1.0, var17))));
      this.add(1.0F, 1.0F, 1.0F, (float)(Math.exp(-var19 / 1.2) * 0.9 + Math.exp(-var19 / 6.0) * 0.25));
      this.add(this.ar1, this.ag1, this.ab1, (float)(Math.exp(-var19 / 14.0) * 0.2));
      double var21 = this.T * (0.5 + this.lively * 0.6);
      double var23 = -Math.sin(var7 + (Math.PI / 2));
      double var25 = Math.cos(var7 + (Math.PI / 2));
      double var27 = var13 * var25;
      double var29 = var15 * var23 - var11 * var25;
      double var31 = -var13 * var23;
      double var33 = Math.cos(var21) * var23 + Math.sin(var21) * var27;
      double var35 = Math.sin(var21) * var29;
      double var37 = Math.cos(var21) * var25 + Math.sin(var21) * var31;
      double var39 = var1 * var33 + var3 * var35 + var5 * var37;
      double var41 = Math.exp(-var39 * var39 / 0.0025) * Math.exp(-var19 / 55.0);
      this.add(this.ar1, this.ag1, this.ab1, (float)(var41 * 0.7 * this.pattern));
      double var43 = this.T * 0.25 % 1.0 * 70.0;
      double var45 = Math.exp(-Math.pow((var19 - var43) / 2.0, 2.0)) * (1.0 - var43 / 70.0);
      this.add(this.ar2, this.ag2, this.ab2, (float)(var45 * 0.45 * this.pattern * this.breath));
   }

   private void paint(double var1, double var3, double var5, float var7) {
      double var8 = this.T * 0.02 * (0.5 + this.lively);
      int[] var10 = new int[]{16752329, 10475775, 16765850, 12186312, 13215487};

      for (int var11 = 0; var11 < 3; var11++) {
         double var12 = fbm(var1 * 1.3 + var11 * 7 + var8, var3 * 1.3 - var8 * 0.6, var5 * 1.3 + var11 * 3, 3);
         double var14 = smooth(0.44, 0.62, var12);
         if (!(var14 < 0.01)) {
            double var16 = Math.exp(-Math.pow((var12 - 0.5) / 0.05, 2.0));
            int var18 = var10[(var11 * 2 + (int)Math.floor(var12 * 5.0)) % var10.length];
            this.over(r(var18), g(var18), b(var18), (float)(var14 * 0.65 * this.pattern));
            this.R *= 1.0F - 0.12F * (float)var16;
            this.G *= 1.0F - 0.12F * (float)var16;
            this.B *= 1.0F - 0.12F * (float)var16;
         }
      }
   }

   private static int hsv(double var0, double var2, double var4) {
      double var6 = (var0 % 1.0 + 1.0) % 1.0 * 6.0;
      double var8 = var6 - Math.floor(var6);
      double var10 = var4 * (1.0 - var2);
      double var12 = var4 * (1.0 - var2 * var8);
      double var14 = var4 * (1.0 - var2 * (1.0 - var8));
      double var16;
      double var18;
      double var20;
      switch ((int)Math.floor(var6)) {
         case 0:
            var16 = var4;
            var18 = var14;
            var20 = var10;
            break;
         case 1:
            var16 = var12;
            var18 = var4;
            var20 = var10;
            break;
         case 2:
            var16 = var10;
            var18 = var4;
            var20 = var14;
            break;
         case 3:
            var16 = var10;
            var18 = var12;
            var20 = var4;
            break;
         case 4:
            var16 = var14;
            var18 = var10;
            var20 = var4;
            break;
         default:
            var16 = var4;
            var18 = var10;
            var20 = var12;
      }

      return (int)Math.round(var16 * 255.0) << 16 | (int)Math.round(var18 * 255.0) << 8 | (int)Math.round(var20 * 255.0);
   }

   private void aurora(double var1, float var3, float var4) {
      if (!(this.auroraOn <= 0.0F) && !(var3 < -2.0F)) {
         double var5 = Math.toRadians(var1);
         double var7 = this.T * (0.6 + this.lively * 0.8);

         for (int var9 = 0; var9 < 2; var9++) {
            double var10 = 22 + var9 * 9
               + 10.0 * Math.sin(var5 * 2.0 + var7 * 0.3 + var9 * 2.1)
               + 5.0 * Math.sin(var5 * 5.0 - var7 * 0.55 + var9)
               + 2.5 * Math.sin(var5 * 11.0 + var7 * 1.1);
            double var12 = var3 - var10;
            double var14 = var12 < 0.0 ? Math.exp(-var12 * var12 / 5.0) : Math.exp(-var12 / (13.0 + 5 * var9));
            if (!(var14 < 0.01)) {
               double var16 = noise(var1 * 0.2 + var9 * 41 + var7 * 0.35, var7 * 0.9, var9 * 7.3);
               var16 = 0.35 + 1.1 * var16 * var16;
               double var18 = smooth(0.28, 0.72, noise(var1 * 0.018 + var9 * 3, var7 * 0.06, 1.7 + var9));
               double var20 = 0.65 + 0.9 * Math.pow(0.5 + 0.5 * Math.sin(var5 * 3.0 - var7 * 1.4 + var9 * 2), 3.0);
               float var22 = (float)(var14 * var16 * var20 * (0.25 + 0.75 * var18) * (var9 == 0 ? 1.0 : 0.6) * var4 * this.pattern * this.breath);
               float var23 = clamp((float)(var12 / 20.0));
               this.add(
                  this.ar1 + (this.ar2 - this.ar1) * var23, this.ag1 + (this.ag2 - this.ag1) * var23, this.ab1 + (this.ab2 - this.ab1) * var23, var22 * 0.9F
               );
            }
         }
      }
   }

   private void milkyWay(double var1, double var3, double var5, float var7) {
      double var8 = 0.36;
      double var10 = 0.58;
      double var12 = 0.73;
      double var14 = var1 * var8 + var3 * var10 + var5 * var12;
      double var16 = Math.exp(-var14 * var14 / 0.03);
      if (!(var16 < 0.01)) {
         double var18 = fbm(var1 * 4.0 + this.T * 0.03, var3 * 4.0 - this.T * 0.02, var5 * 4.0 + this.T * 0.015, 3);
         double var20 = Math.exp(-var14 * var14 / 0.0035) * smooth(0.42, 0.7, fbm(var1 * 9.0, var3 * 9.0 + 3.0, var5 * 9.0, 2));
         float var22 = (float)(var16 * (0.35 + 0.9 * var18) * (1.0 - 0.75 * var20) * var7 * this.pattern);
         float var23 = (float)var18;
         this.add(0.62F + 0.38F * var23, 0.7F + 0.2F * var23, 1.0F - 0.18F * var23, var22 * 0.55F);
      }
   }

   private void nebula(double var1, double var3, double var5, float var7, double var8) {
      double var10 = this.T * (0.035 + this.lively * 0.05);
      double var12 = noise(var1 * 1.3 + var10 * 0.7, var3 * 1.3, var5 * 1.3 - var10 * 0.5) - 0.5;
      double var14 = noise(var1 * 1.3 + 7.0, var3 * 1.3 + var10 * 0.6, var5 * 1.3 + 3.0) - 0.5;
      double var16 = fbm(var1 * var8 + var10 + var12 * 1.4, var3 * var8 - var10 * 0.6 + var14 * 1.4, var5 * var8 + var10 * 0.4 - var12, 3);
      double var18 = smooth(0.42, 0.85, var16);
      if (!(var18 < 0.01)) {
         double var20 = fbm(var1 * var8 * 1.7 - var10, var3 * var8 * 1.7, var5 * var8 * 1.7 + 5.0, 2);
         float var22 = (float)(var18 * var7 * this.pattern * this.breath);
         float var23 = (float)smooth(0.3, 0.75, var20);
         this.add(this.ar1 + (this.ar2 - this.ar1) * var23, this.ag1 + (this.ag2 - this.ag1) * var23, this.ab1 + (this.ab2 - this.ab1) * var23, var22 * 0.6F);
      }
   }

   private void pillars(double var1, float var3) {
      if (!(var3 < -2.0F)) {
         double var4 = this.T * (0.5 + this.lively);
         float var6 = 0.0F;

         for (double[] var10 : PILLAR) {
            double var11 = Math.abs(((var1 - var10[0] - 6.0 * Math.sin(var4 * 0.2 * var10[3] + var10[2])) % 360.0 + 540.0) % 360.0 - 180.0);
            if (!(var11 > var10[1] * 4.0)) {
               double var13 = Math.exp(-var11 * var11 / (var10[1] * var10[1]));
               double var15 = 18.0 + 22.0 * (0.5 + 0.5 * Math.sin(var4 * 0.5 * var10[3] + var10[2]));
               double var17 = Math.exp(-Math.max(0.0F, var3) / var15) * smooth(-2.0, 1.0, var3);
               var6 += (float)(var13 * var17 * (0.75 + 0.25 * Math.sin(var4 * 2.2 * var10[3] + var10[2] * 3.0)));
            }
         }

         if (!(var6 <= 0.003F)) {
            float var19 = clamp(var3 / 35.0F);
            this.add(
               this.ar1 + (this.ar2 - this.ar1) * var19,
               this.ag1 + (this.ag2 - this.ag1) * var19,
               this.ab1 + (this.ab2 - this.ab1) * var19,
               var6 * 0.85F * this.pattern * this.breath
            );
         }
      }
   }

   private void caustics(double var1, double var3, double var5, float var7) {
      double var8 = this.T * (0.45 + this.lively * 0.5);
      double var10 = 3.2;
      double var12 = noise(var1 * var10 + var8, var3 * var10 + var8 * 0.7, var5 * var10 - var8 * 0.4);
      double var14 = noise(var1 * var10 * 1.6 - var8 * 0.8, var3 * var10 * 1.6 + 4.0, var5 * var10 * 1.6 + var8 * 0.5);
      double var16 = 1.0 - Math.abs(2.0 * var12 - 1.0);
      double var18 = 1.0 - Math.abs(2.0 * var14 - 1.0);
      double var20 = Math.pow(var16, 7.0) + 0.7 * Math.pow(var18, 9.0);
      float var22 = smooth01((var7 - 5.0F) / 50.0F);
      this.add(this.ar1, this.ag1, this.ab1, (float)(var20 * 0.5 * var22 * this.pattern * this.breath));
      double var23 = Math.atan2(-var1, var5) + var8 * 0.05;
      double var25 = noise(Math.cos(var23) * 3.0, 2.5 + Math.sin(var23) * 3.0, 7.0);
      this.add(this.ar2, this.ag2, this.ab2, (float)(Math.pow(var25, 4.0) * 0.35 * var22 * this.pattern));
   }

   private void clouds(double var1, double var3, double var5, float var7, float var8, int var9, int var10, double var11) {
      if (!(var3 < 0.01)) {
         double var13 = 1.0 / (var3 + 0.12);
         double var15 = var1 * var13;
         double var17 = var5 * var13;
         double var19 = this.T * var11 * (1.2 + 1.5 * this.lively);
         double var21 = fbm(var15 * 0.85 + var19, var17 * 0.85 + var19 * 0.35, var19 * 0.6, 4);
         double var23 = smooth(var7, var7 + 0.22, var21) * smooth(0.0, 0.14, var3);
         if (!(var23 < 0.01)) {
            double var25 = smooth(0.35, 0.95, var21 + 0.15 * var3);
            if (this.body != null && this.body.kind() == 0) {
               var25 = Math.min(1.0, var25 + 0.35 * Math.max(0.0, var1 * this.sunX + var3 * this.sunY + var5 * this.sunZ));
            }

            float var27 = r(var9);
            float var28 = g(var9);
            float var29 = b(var9);
            float var30 = r(var10);
            float var31 = g(var10);
            float var32 = b(var10);
            float var33 = (float)var25;
            this.over(var30 + (var27 - var30) * var33, var31 + (var28 - var31) * var33, var32 + (var29 - var32) * var33, (float)var23 * var8);
         }
      }
   }

   private void bodyGlow(double var1, double var3, double var5, double var7, float var9) {
      double var10 = var1 * this.sunX + var3 * this.sunY + var5 * this.sunZ;
      double var12 = Math.toDegrees(Math.acos(Math.max(-1.0, Math.min(1.0, var10))));
      LiveSky.Body var14 = this.body;
      float var15 = r(var14.color());
      float var16 = g(var14.color());
      float var17 = b(var14.color());
      double var18 = var14.radius();
      if (var14.kind() == 0) {
         this.add(var15, var16, var17, (float)(Math.exp(-var12 / 14.0) * 0.55 + Math.exp(-var12 / 4.0) * 0.45));
         if (this.P == SkyPreset.GOLDEN_HOUR || this.P == SkyPreset.SAKURA) {
            double var20 = Math.atan2(var9 - var14.el(), ((var7 - var14.az()) % 360.0 + 540.0) % 360.0 - 180.0);
            double var22 = Math.pow(
               0.5 + 0.5 * Math.sin(var20 * 11.0 + this.T * 0.3 + noise(Math.cos(var20) * 2.0, this.T * 0.25 + Math.sin(var20) * 2.0, 3.0) * 3.0), 5.0
            );
            this.add(var15, var16, var17, (float)(var22 * Math.exp(-var12 / 26.0) * 0.28 * smooth(var18, var18 * 2.5, var12) * this.pattern));
         }
      } else if (var14.kind() == 1) {
         this.add(var15, var16, var17, (float)(Math.exp(-var12 / 5.0) * 0.32 + Math.exp(-var12 / 22.0) * 0.14));
      } else if (var14.kind() == 3) {
         double var30 = Math.max(0.0, var12 - var18);
         this.add(this.ar1, this.ag1, this.ab1, (float)(Math.exp(-var30 / 10.0) * 0.45 + Math.exp(-var30 / 3.0) * 0.35));
      } else {
         if (var12 < var18) {
            this.over(0.008F, 0.004F, 0.02F, 1.0F);
            return;
         }

         double var31 = Math.atan2(var9 - var14.el(), ((var7 - var14.az()) % 360.0 + 540.0) % 360.0 - 180.0);
         double var32 = 0.6 + 0.5 * noise(Math.cos(var31) * 3.0, this.T * 0.9 + Math.sin(var31) * 3.0, 5.0);
         double var24 = var31 + this.T * 0.02;
         double var26 = 0.5 + 1.0 * Math.pow(noise(Math.cos(var24) * 5.0 + 9.0, this.T * 0.35 + Math.sin(var24) * 5.0, 2.0), 2.0) * 2.0;
         double var28 = var12 - var18;
         this.add(this.ar1, this.ag1, this.ab1, (float)(Math.exp(-var28 / 1.6) * 1.2 * var32));
         this.add(this.ar2, this.ag2, this.ab2, (float)(Math.exp(-var28 / 7.0) * 0.45 * var26 * this.pattern));
      }
   }

   public int[] view(SkyPreset var1, int var2, int var3, double var4, int var6, int var7, float var8, float var9, float var10) {
      this.prepare(var1, var2, var3, var4, null);
      int[] var11 = new int[var6 * var7];
      double var12 = var7 / 2.0 / Math.tan(Math.toRadians(var10 / 2.0F));
      double var14 = Math.toRadians(var8);
      double var16 = Math.toRadians(var9);
      double var18 = -Math.sin(var14) * Math.cos(var16);
      double var20 = -Math.sin(var16);
      double var22 = Math.cos(var14) * Math.cos(var16);
      double var24 = -Math.cos(var14);
      double var26 = 0.0;
      double var28 = -Math.sin(var14);
      double var30 = var26 * var22 - var28 * var20;
      double var32 = var28 * var18 - var24 * var22;
      double var34 = var24 * var20 - var26 * var18;
      Random var36 = new Random(var1.name().hashCode());
      double var37 = var36.nextDouble() * 10.0;

      for (int var39 = 0; var39 < var7; var39++) {
         for (int var40 = 0; var40 < var6; var40++) {
            double var41 = var40 + 0.5 - var6 / 2.0;
            double var43 = -(var39 + 0.5 - var7 / 2.0);
            double var45 = var18 * var12 + var24 * var41 + var30 * var43;
            double var47 = var20 * var12 + var26 * var41 + var32 * var43;
            double var49 = var22 * var12 + var28 * var41 + var34 * var43;
            double var51 = Math.sqrt(var45 * var45 + var47 * var47 + var49 * var49);
            var45 /= var51;
            var47 /= var51;
            var49 /= var51;
            float var53 = (float)Math.toDegrees(Math.asin(var47));
            int var54 = this.color(var45, var47, var49, var53);
            float var55 = r(var54);
            float var56 = g(var54);
            float var57 = b(var54);
            if (var1.stars > 0.0F && var53 > 3.0F) {
               double var58 = hash3((int)Math.floor(var45 * 260.0), (int)Math.floor(var47 * 260.0), (int)Math.floor(var49 * 260.0));
               if (var58 > 1.0 - 0.0045 * var1.stars) {
                  float var60 = (float)((var58 - (1.0 - 0.0045 * var1.stars)) / (0.0045 * var1.stars)) * 0.9F;
                  var55 = Math.min(1.0F, var55 + var60);
                  var56 = Math.min(1.0F, var56 + var60);
                  var57 = Math.min(1.0F, var57 + var60);
               }
            }

            LiveSky.Body var72 = this.body;
            if (var72 != null) {
               double var59 = Math.toDegrees(Math.acos(Math.max(-1.0, Math.min(1.0, var45 * this.sunX + var47 * this.sunY + var49 * this.sunZ))));
               double var61 = clamp((float)((var72.radius() - var59) / 0.5 + 0.5));
               if (var72.kind() == 3) {
                  double var63 = (var53 - var72.el()) / var72.radius();
                  if (var61 > 0.0 && !synthGap(var63, this.T / 2.2)) {
                     int var65 = synthColor(var63);
                     var55 = (float)(var55 + (r(var65) - var55) * var61);
                     var56 = (float)(var56 + (g(var65) - var56) * var61);
                     var57 = (float)(var57 + (b(var65) - var57) * var61);
                  }
               } else if (var72.kind() == 2) {
                  double var75 = Math.exp(-Math.pow((var59 - var72.radius()) / 0.35, 2.0));
                  var55 = (float)Math.min(1.0, var55 * (1.0 - var61) + var75);
                  var56 = (float)Math.min(1.0, var56 * (1.0 - var61) + var75 * 0.92);
                  var57 = (float)Math.min(1.0, var57 * (1.0 - var61) + var75 * 0.8);
               } else if (var61 > 0.0) {
                  float var76 = var72.kind() == 0 ? 1.0F : 0.92F;
                  var55 = (float)(var55 + (Math.min(1.0F, r(var72.color()) * 0.3F + 0.7F * var76) - var55) * var61);
                  var56 = (float)(var56 + (Math.min(1.0F, g(var72.color()) * 0.6F + 0.4F * var76) - var56) * var61);
                  var57 = (float)(var57 + (Math.min(1.0F, b(var72.color()) * 0.6F + 0.4F * var76) - var57) * var61);
               }
            }

            double var73 = Math.atan2(-var45, var49);
            double var74 = 1.5 + 2.2 * Math.sin(var73 * 3.0 + var37) + 1.2 * Math.sin(var73 * 7.0 + var37 * 2.0);
            double var77 = Math.max(0.0, Math.sin(var73 * 95.0 + var37)) * Math.max(0.0, Math.sin(var73 * 37.0 + var37 * 3.0)) * 4.5;
            if (var53 < var74 + var77) {
               var55 = 0.024F;
               var56 = 0.016F;
               var57 = 0.04F;
            }

            float var78 = Math.max(0.0F, (var39 - var7 * 0.6F) / (var7 * 0.4F));
            var78 = var78 * var78 * 0.55F;
            var55 *= 1.0F - var78;
            var56 *= 1.0F - var78;
            var57 *= 1.0F - var78;
            var11[var39 * var6 + var40] = 0xFF000000
               | Math.round(clamp(var55) * 255.0F) << 16
               | Math.round(clamp(var56) * 255.0F) << 8
               | Math.round(clamp(var57) * 255.0F);
         }
      }

      return var11;
   }

   private static double hash3(int var0, int var1, int var2) {
      int var3 = var0 * 374761393 + var1 * 668265263 + var2 * 1274126177;
      var3 = (var3 ^ var3 >>> 13) * 1274126177;
      var3 ^= var3 >>> 16;
      return (var3 & 16777215) / 1.6777215E7;
   }

   static double noise(double var0, double var2, double var4) {
      int var6 = (int)Math.floor(var0);
      int var7 = (int)Math.floor(var2);
      int var8 = (int)Math.floor(var4);
      double var9 = var0 - var6;
      double var11 = var2 - var7;
      double var13 = var4 - var8;
      double var15 = var9 * var9 * (3.0 - 2.0 * var9);
      double var17 = var11 * var11 * (3.0 - 2.0 * var11);
      double var19 = var13 * var13 * (3.0 - 2.0 * var13);
      double var21 = hash3(var6, var7, var8);
      double var23 = hash3(var6 + 1, var7, var8);
      double var25 = hash3(var6, var7 + 1, var8);
      double var27 = hash3(var6 + 1, var7 + 1, var8);
      double var29 = hash3(var6, var7, var8 + 1);
      double var31 = hash3(var6 + 1, var7, var8 + 1);
      double var33 = hash3(var6, var7 + 1, var8 + 1);
      double var35 = hash3(var6 + 1, var7 + 1, var8 + 1);
      double var37 = var21 + (var23 - var21) * var15;
      double var39 = var25 + (var27 - var25) * var15;
      double var41 = var29 + (var31 - var29) * var15;
      double var43 = var33 + (var35 - var33) * var15;
      double var45 = var37 + (var39 - var37) * var17;
      double var47 = var41 + (var43 - var41) * var17;
      return var45 + (var47 - var45) * var19;
   }

   static double fbm(double var0, double var2, double var4, int var6) {
      double var7 = 0.0;
      double var9 = 0.5;
      double var11 = 0.0;

      for (int var13 = 0; var13 < var6; var13++) {
         var7 += noise(var0, var2, var4) * var9;
         var11 += var9;
         var0 = var0 * 2.03 + 17.1;
         var2 = var2 * 2.03 - 9.7;
         var4 = var4 * 2.03 + 3.3;
         var9 *= 0.5;
      }

      return var7 / var11;
   }

   private static double smooth(double var0, double var2, double var4) {
      double var6 = Math.max(0.0, Math.min(1.0, (var4 - var0) / (var2 - var0)));
      return var6 * var6 * (3.0 - 2.0 * var6);
   }

   private static float smooth01(float var0) {
      float var1 = clamp(var0);
      return var1 * var1 * (3.0F - 2.0F * var1);
   }

   private static float clamp(float var0) {
      return Math.max(0.0F, Math.min(1.0F, var0));
   }

   private static double clamp(double var0) {
      return Math.max(0.0, Math.min(1.0, var0));
   }

   static {
      Random var0 = new Random(77L);

      for (int var1 = 0; var1 < PILLAR.length; var1++) {
         PILLAR[var1] = new double[]{var0.nextDouble() * 360.0, 1.2 + var0.nextDouble() * 2.2, var0.nextDouble() * 6.28, 0.6 + var0.nextDouble() * 0.8};
      }
   }

   public record Body(double az, double el, double radius, int color, int kind) {
      public static final int SUN = 0;
      public static final int MOON = 1;
      public static final int ECLIPSE = 2;
      public static final int SYNTH = 3;
   }
}
