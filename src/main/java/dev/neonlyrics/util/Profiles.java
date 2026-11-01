package dev.neonlyrics.util;

import dev.neonlyrics.NeonLyrics;
import dev.neonlyrics.config.Config;
import dev.neonlyrics.hud.Island;

public final class Profiles {
   private Profiles() {
   }

   public static void toggle() {
      set(NeonLyrics.cfg().profile == 0 ? 1 : 0);
   }

   public static void set(int var0) {
      Config var1 = NeonLyrics.cfg();
      boolean var2 = var0 == 0;
      if (!var2 && var1.sky >= 0) {
         var1.lastSky = var1.sky;
      }

      var1.sky = var2 ? Math.max(0, var1.lastSky) : -1;
      var1.fxSkyColor = var2;
      var1.fxWeather = var2;
      var1.fxAurora = var2;
      var1.fxStars = var2;
      var1.fxBeams = var2;
      var1.lyricsGlow = var2;
      var1.glassHotbar = var2;
      var1.blur = var2;
      var1.fxMenuAnim = var2;
      var1.fxConstellations = var2;
      var1.fxSkyMusic = var2;
      var1.quality = var2 ? 1 : 0;
      var1.profile = var0;
      if (!var2 && !var1.fpsBoost) {
         FpsBoost.apply(true);
      }

      if (var2 && var1.fpsBoost) {
         FpsBoost.apply(false);
      }

      NeonLyrics.saveConfig();
      Island.notify(Fx.t(var2 ? "neonlyrics.profile.chill" : "neonlyrics.profile.pvp"), true);
   }
}
