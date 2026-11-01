package dev.neonlyrics.hud;

import dev.neonlyrics.Beat;
import dev.neonlyrics.NeonLyrics;
import dev.neonlyrics.config.Config;
import dev.neonlyrics.lyrics.NowPlaying;
import dev.neonlyrics.lyrics.SpotifyClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

public final class BeatGlow {
   private static float level = 0.0F;
   private static long last = System.nanoTime();

   private BeatGlow() {
   }

   public static void render(DrawContext var0, RenderTickCounter var1) {
      if (Beat.live()) {
         Config var2 = NeonLyrics.cfg();
         MinecraftClient var3 = MinecraftClient.getInstance();
         long var4 = System.nanoTime();
         float var6 = Math.min(0.1F, (float)(var4 - last) / 1.0E9F);
         last = var4;
         NowPlaying var7 = SpotifyClient.now();
         float var8 = var2.beatGlow && var7.playing && !var3.options.hudHidden ? var7.beatPulse(var7.progressNow(var2.lyricsOffsetMs)) : 0.0F;
         level = var8 > level ? var8 : level + (var8 - level) * (1.0F - (float)Math.exp(-5.0F * var6));
         if (!(level < 0.02F)) {
            float var9 = Gfx.alpha;
            Gfx.alpha = 1.0F;
            int var10 = var0.getScaledWindowWidth();
            int var11 = var0.getScaledWindowHeight();
            int var12 = Theme.accent();
            int var13 = Math.max(24, Math.min(var10, var11) / 7);
            float var14 = 0.28F * level;
            Gfx.vGradient(var0, 0, 0, var10, var13, var12, var12, var14, 0.0F);
            Gfx.vGradient(var0, 0, var11 - var13, var10, var13, var12, var12, 0.0F, var14);
            byte var15 = 12;

            for (int var16 = 0; var16 < var15; var16++) {
               float var17 = 1.0F - (float)var16 / var15;
               int var18 = Math.round((float)(var13 * var16) / var15);
               int var19 = Math.round((float)(var13 * (var16 + 1)) / var15);
               Gfx.rect(var0, var18, 0, var19 - var18, var11, var12, var14 * var17 * var17 / 2.2F);
               Gfx.rect(var0, var10 - var19, 0, var19 - var18, var11, var12, var14 * var17 * var17 / 2.2F);
            }

            Gfx.alpha = var9;
         }
      }
   }
}
