package dev.neonlyrics.sky;

import dev.neonlyrics.NeonLyrics;

public final class SkyPreview {
   private static final int NONE = -99;
   private static int sky = -99;

   private SkyPreview() {
   }

   public static void set(SkyPreset var0) {
      sky = var0 == null ? -99 : var0.ordinal();
   }

   public static void clear() {
      sky = -99;
   }

   public static boolean active() {
      return sky != -99;
   }

   public static int sky() {
      if (sky != -99) {
         return sky;
      }

      int var0 = NeonLyrics.cfg().sky;
      return var0 >= 0 ? var0 : Math.max(0, NeonLyrics.cfg().lastSky);
   }
}
