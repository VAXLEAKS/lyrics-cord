package dev.neonlyrics.util;

import dev.neonlyrics.NeonLyrics;
import dev.neonlyrics.config.Config;
import net.minecraft.client.MinecraftClient;

public final class AutoPerf {
   public static final int MAX = 6;
   private static int level = 0;
   private static int lastFps = 0;
   private static int lowTicks = 0;
   private static int okTicks = 0;
   private static int relaxTicks = 0;
   private static long lastCheck = 0L;
   private static int badLevel = -1;
   private static long badUntil = 0L;
   private static long raisedAt = 0L;
   private static double modMs = 0.0;
   private static long frameStart = 0L;
   private static double frameAcc = 0.0;

   private AutoPerf() {
   }

   public static int level() {
      Config var0 = NeonLyrics.cfg();
      return var0.autoPerf ? level : 0;
   }

   public static int fps() {
      return lastFps;
   }

   public static double modMs() {
      return modMs;
   }

   public static int quality() {
      int var0 = Math.max(0, Math.min(2, NeonLyrics.cfg().quality));
      int var1 = level();
      if (var1 >= 2) {
         return 0;
      } else {
         return var1 >= 1 ? Math.min(var0, 1) : var0;
      }
   }

   public static boolean allow(int var0) {
      return level() < var0;
   }

   public static int target() {
      Config var0 = NeonLyrics.cfg();
      int var1 = var0.targetFps <= 0 ? 60 : var0.targetFps;

      try {
         MinecraftClient var2 = MinecraftClient.getInstance();
         if ((Boolean)var2.options.getEnableVsync().getValue()) {
            var1 = Math.min(var1, 57);
         }

         int var3 = (Integer)var2.options.getMaxFps().getValue();
         if (var3 > 0 && var3 < 260) {
            var1 = Math.min(var1, var3 - 3);
         }
      } catch (Throwable var4) {
      }

      return Math.max(20, var1);
   }

   public static void begin() {
      frameStart = System.nanoTime();
   }

   public static void end() {
      if (frameStart != 0L) {
         frameAcc = frameAcc + (System.nanoTime() - frameStart) / 1000000.0;
      }

      frameStart = 0L;
   }

   public static void frame() {
      modMs = modMs * 0.95 + frameAcc * 0.05;
      frameAcc = 0.0;
   }

   public static void tick(MinecraftClient var0) {
      long var1 = System.currentTimeMillis();
      if (var1 - lastCheck >= 500L) {
         lastCheck = var1;
         Config var3 = NeonLyrics.cfg();
         lastFps = var0.getCurrentFps();
         if (var3.autoPerf && var0.world != null && var0.player != null && var0.currentScreen == null) {
            int var4 = target();
            if (lastFps < var4 * 0.92) {
               lowTicks++;
               okTicks = 0;
            } else {
               okTicks++;
               lowTicks = 0;
            }

            double var5 = 1000.0 / Math.max(1, lastFps);
            boolean var7 = modMs > var5 * 0.1 || modMs > 2.5;
            if (!var7) {
               lowTicks = 0;
               if (level > 0 && ++relaxTicks >= 6 && (level - 1 > badLevel || var1 >= badUntil)) {
                  level--;
                  raisedAt = var1;
                  relaxTicks = 0;
               }
            } else {
               relaxTicks = 0;
               if (lowTicks >= 2 && level < 6 && var7) {
                  if (var1 - raisedAt < 6000L) {
                     badLevel = level;
                     badUntil = var1 + 120000L;
                  }

                  level = Math.min(6, level + (lastFps < var4 * 0.6 ? 2 : 1));
                  lowTicks = 0;
                  okTicks = 0;
               } else {
                  boolean var8 = lastFps >= var4 * 1.25;
                  boolean var9 = lastFps >= var4 * 0.98;
                  if (level > 0 && (var8 && okTicks >= 8 || var9 && okTicks >= 40)) {
                     int var10 = level - 1;
                     if (var10 <= badLevel && var1 < badUntil) {
                        return;
                     }

                     level = var10;
                     raisedAt = var1;
                     okTicks = 0;
                  }
               }
            }
         } else {
            okTicks = 0;
            lowTicks = 0;
         }
      }
   }

   public static void reset() {
      level = 0;
      okTicks = 0;
      lowTicks = 0;
      badLevel = -1;
   }
}
