package dev.neonlyrics.hud;

import dev.neonlyrics.Beat;
import dev.neonlyrics.NeonLyrics;
import dev.neonlyrics.config.Config;
import dev.neonlyrics.lyrics.NowPlaying;
import dev.neonlyrics.lyrics.SpotifyClient;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public final class Crosshair {
   private Crosshair() {
   }

   public static HudElement wrap(HudElement var0) {
      return (var1, var2) -> {
         Config var3 = NeonLyrics.cfg();
         MinecraftClient var4 = MinecraftClient.getInstance();
         if (var3.crosshair > 0 && Beat.live() && var4.player != null && var4.options.getPerspective().isFirstPerson() && !var4.options.hudHidden) {
            draw(var1, var3);
         } else {
            var0.render(var1, var2);
         }
      };
   }

   private static void draw(DrawContext var0, Config var1) {
      float var2 = Gfx.alpha;
      Gfx.alpha = 1.0F;
      float var3 = var0.getScaledWindowWidth() / 2.0F;
      float var4 = var0.getScaledWindowHeight() / 2.0F;
      int var5 = 16777215;
      byte var6 = 0;
      switch (var1.crosshair) {
         case 1:
            Gfx.aaCircle(var0, var3, var4, 2.2F, var6, 0.45F);
            Gfx.aaCircle(var0, var3, var4, 1.4F, var5, 1.0F);
            break;
         case 2:
            cross(var0, var3, var4, 2.5F, 4.5F, var5, var6);
            break;
         case 3:
            ring(var0, var3, var4, 4.5F, var6, 0.45F, 1.6F);
            ring(var0, var3, var4, 4.5F, var5, 1.0F, 0.9F);
            Gfx.aaCircle(var0, var3, var4, 0.9F, var5, 1.0F);
            break;
         case 4:
            NowPlaying var7 = SpotifyClient.now();
            float var8 = var7.playing ? var7.beatPulse(var7.progressNow(var1.lyricsOffsetMs)) : 0.0F;
            cross(var0, var3, var4, 2.0F + 2.5F * var8, 4.0F, Gfx.lerpColor(var5, Theme.accent(), var8), var6);
            Gfx.aaCircle(var0, var3, var4, 0.9F, var5, 1.0F);
      }

      Gfx.alpha = var2;
   }

   private static void cross(DrawContext var0, float var1, float var2, float var3, float var4, int var5, int var6) {
      float var7 = 1.2F;

      for (int var8 = 0; var8 < 2; var8++) {
         float var9 = var8 == 0 ? 0.6F : 0.0F;
         int var10 = var8 == 0 ? var6 : var5;
         float var11 = var8 == 0 ? 0.45F : 1.0F;
         Gfx.aaRound(var0, var1 - var7 / 2.0F - var9, var2 - var3 - var4 - var9, var7 + 2.0F * var9, var4 + 2.0F * var9, 0.6F, var10, var11);
         Gfx.aaRound(var0, var1 - var7 / 2.0F - var9, var2 + var3 - var9, var7 + 2.0F * var9, var4 + 2.0F * var9, 0.6F, var10, var11);
         Gfx.aaRound(var0, var1 - var3 - var4 - var9, var2 - var7 / 2.0F - var9, var4 + 2.0F * var9, var7 + 2.0F * var9, 0.6F, var10, var11);
         Gfx.aaRound(var0, var1 + var3 - var9, var2 - var7 / 2.0F - var9, var4 + 2.0F * var9, var7 + 2.0F * var9, 0.6F, var10, var11);
      }
   }

   private static void ring(DrawContext var0, float var1, float var2, float var3, int var4, float var5, float var6) {
      byte var7 = 28;

      for (int var8 = 0; var8 < var7; var8++) {
         double var9 = var8 * Math.PI * 2.0 / var7;
         Gfx.aaCircle(var0, (float)(var1 + Math.cos(var9) * var3), (float)(var2 + Math.sin(var9) * var3), var6 / 2.0F + 0.3F, var4, var5);
      }
   }
}
