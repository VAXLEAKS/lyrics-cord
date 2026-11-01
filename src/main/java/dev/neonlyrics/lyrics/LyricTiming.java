package dev.neonlyrics.lyrics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public final class LyricTiming {
   public final long[][] wordStart;
   public final double beatMs;
   public final double beatPhase;
   public final float confidence;
   public final float sylPerSec;
   private static final Map<List<NowPlaying.Line>, LyricTiming> CACHE = Collections.synchronizedMap(new IdentityHashMap<>());

   private LyricTiming(long[][] var1, double var2, double var4, float var6, float var7) {
      this.wordStart = var1;
      this.beatMs = var2;
      this.beatPhase = var4;
      this.confidence = var6;
      this.sylPerSec = var7;
   }

   public static LyricTiming of(List<NowPlaying.Line> var0) {
      if (var0 != null && !var0.isEmpty()) {
         LyricTiming var1 = CACHE.get(var0);
         if (var1 == null) {
            var1 = build(var0);
            if (CACHE.size() > 20) {
               CACHE.clear();
            }

            CACHE.put(var0, var1);
         }

         return var1;
      } else {
         return null;
      }
   }

   static float weight(String var0) {
      float var1 = NowPlaying.syllables(var0);
      char var2 = var0.charAt(var0.length() - 1);
      if (var2 == ',' || var2 == '.' || var2 == '!' || var2 == '?' || var2 == ')') {
         var1 += 0.6F;
      }

      return var1;
   }

   private static long nextStart(List<NowPlaying.Line> var0, int var1) {
      return var1 + 1 < var0.size() ? ((NowPlaying.Line)var0.get(var1 + 1)).timeMs() : ((NowPlaying.Line)var0.get(var1)).timeMs() + 5000L;
   }

   private static LyricTiming build(List<NowPlaying.Line> var0) {
      int var1 = var0.size();
      ArrayList var2 = new ArrayList();

      for (int var3 = 0; var3 < var1; var3++) {
         NowPlaying.Line var4 = (NowPlaying.Line)var0.get(var3);
         if (var4.words().length >= 3) {
            long var5 = nextStart(var0, var3) - var4.timeMs();
            if (var5 >= 900L && var5 <= 9000L) {
               float var7 = 0.0F;

               for (String var11 : var4.words()) {
                  var7 += weight(var11);
               }

               var2.add(var7 / ((float)var5 * 0.85F / 1000.0F));
            }
         }
      }

      float var35 = 4.0F;
      if (!var2.isEmpty()) {
         Collections.sort(var2);
         var35 = (Float)var2.get(var2.size() / 2);
      }

      var35 = Math.max(2.2F, Math.min(8.0F, var35));
      ArrayList<Long> var37 = new ArrayList();

      for (NowPlaying.Line var6 : var0) {
         if (!var6.text().isBlank()) {
            var37.add(var6.timeMs());
         }
      }

      double var39 = 0.0;
      double var40 = 0.0;
      float var41 = 0.0F;
      if (var37.size() >= 6) {
         for (double var42 = 60.0; var42 <= 180.0; var42 += 0.25) {
            double var12 = 60000.0 / var42;
            double var14 = 0.0;
            double var16 = 0.0;

            for (long var19 : var37) {
               double var21 = (Math.PI * 2) * (var19 % var12) / var12;
               var14 += Math.cos(var21);
               var16 += Math.sin(var21);
            }

            float var47 = (float)(Math.sqrt(var14 * var14 + var16 * var16) / var37.size());
            float var48 = var42 >= 80.0 && var42 <= 150.0 ? 1.0F : 0.93F;
            if (var47 * var48 > var41) {
               var41 = var47 * var48;
               var39 = var12;
               double var20 = Math.atan2(var16, var14);
               if (var20 < 0.0) {
                  var20 += Math.PI * 2;
               }

               var40 = var20 / (Math.PI * 2) * var12;
            }
         }
      }

      boolean var43 = var41 >= 0.45F && var39 > 0.0;
      long[][] var44 = new long[var1][];

      for (int var45 = 0; var45 < var1; var45++) {
         NowPlaying.Line var13 = (NowPlaying.Line)var0.get(var45);
         String[] var46 = var13.words();
         var44[var45] = new long[var46.length];
         if (var46.length != 0) {
            if (var13.wordTimes() != null && var13.wordTimes().length == var46.length) {
               var44[var45] = (long[])var13.wordTimes().clone();
            } else {
               long var15 = var13.timeMs();
               long var17 = Math.min(nextStart(var0, var45) - var15, 7000L);
               float var49 = 0.0F;

               for (String var23 : var46) {
                  var49 += weight(var23);
               }

               double var51 = Math.min(var17 * 0.92, Math.max(300.0, var49 / var35 * 1000.0));
               double var53 = 0.0;
               double var24 = 0.0;
               if (var43) {
                  double var26 = var51 / var46.length;
                  var24 = var26 < var39 / 2.0 * 0.75 ? var39 / 4.0 : var39 / 2.0;
               }

               long var54 = var15 - 1L;

               for (int var28 = 0; var28 < var46.length; var28++) {
                  double var29 = var15 + var53 / var49 * var51;
                  if (var28 == 0) {
                     var29 = var15;
                  } else if (var24 > 0.0) {
                     double var31 = var29 - var40;
                     double var33 = var40 + Math.round(var31 / var24) * var24;
                     if (Math.abs(var33 - var29) < var24 * 0.6) {
                        var29 = var33;
                     }
                  }

                  long var55 = Math.max(var54 + 40L, Math.round(var29));
                  var55 = Math.min(var55, var15 + (long)(var17 * 0.97));
                  var44[var45][var28] = var55;
                  var54 = var55;
                  var53 += weight(var46[var28]);
               }
            }
         }
      }

      return new LyricTiming(var44, var43 ? var39 : 0.0, var40, var43 ? Math.min(1.0F, var41) : 0.0F, var35);
   }

   public float wordPosition(int var1, long var2, long var4) {
      if (var1 >= 0 && var1 < this.wordStart.length) {
         long[] var6 = this.wordStart[var1];
         if (var6.length == 0) {
            return -1.0F;
         }

         int var7 = Arrays.binarySearch(var6, var2);
         if (var7 < 0) {
            var7 = -var7 - 2;
         }

         if (var7 < 0) {
            return 0.0F;
         } else if (var7 >= var6.length - 1) {
            long var8 = Math.max(var6[var6.length - 1] + 200L, Math.min(var4, var6[var6.length - 1] + 900L));
            return var6.length - 1 + Math.min(0.999F, (float)(var2 - var6[var6.length - 1]) / (float)(var8 - var6[var6.length - 1]));
         } else {
            return var7 + (float)(var2 - var6[var7]) / (float)Math.max(1L, var6[var7 + 1] - var6[var7]);
         }
      } else {
         return -1.0F;
      }
   }

   public float beatPulse(long var1) {
      if (this.beatMs <= 0.0) {
         return 0.0F;
      }

      double var3 = ((var1 - this.beatPhase) % this.beatMs + this.beatMs) % this.beatMs;
      return (float)Math.exp(-var3 / (this.beatMs * 0.18)) * this.confidence;
   }
}
