package dev.neonlyrics.hud;

import dev.neonlyrics.Beat;
import dev.neonlyrics.NeonLyrics;
import dev.neonlyrics.config.Config;
import dev.neonlyrics.gui.LyricsScreen;
import dev.neonlyrics.lyrics.NowPlaying;
import dev.neonlyrics.lyrics.SpotifyClient;
import dev.neonlyrics.lyrics.Translator;
import dev.neonlyrics.util.AutoPerf;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.render.RenderTickCounter;

public final class Island {
   private static final DateTimeFormatter HM = DateTimeFormatter.ofPattern("HH:mm");
   private static final Island.Spring W = new Island.Spring(60.0F, 170.0F, 19.0F);
   private static final Island.Spring H = new Island.Spring(14.0F, 170.0F, 19.0F);
   private static final Island.Spring HL_X = new Island.Spring(0.0F, 260.0F, 30.0F);
   private static final Island.Spring HL_W = new Island.Spring(0.0F, 260.0F, 30.0F);
   private static final Island.Spring SCROLL = new Island.Spring(0.0F, 90.0F, 18.0F);
   private static final Island.Spring SHOW = new Island.Spring(0.0F, 120.0F, 20.0F);
   private static String note = null;
   private static boolean noteOk = true;
   private static long noteTime = 0L;
   private static String curKey = "";
   private static long switchTime = 0L;
   private static Island.Kind prevKind = Island.Kind.IDLE;
   private static String prevText = "";
   private static Island.Kind curKind = Island.Kind.IDLE;
   private static String curText = "";
   private static long lastNanos = System.nanoTime();

   private Island() {
   }

   public static void notify(String var0, boolean var1) {
      note = var0;
      noteOk = var1;
      noteTime = System.currentTimeMillis();
   }

   public static void render(DrawContext var0, RenderTickCounter var1) {
      if (Beat.live() && Beat.sync()) {
         MinecraftClient var2 = MinecraftClient.getInstance();
         Config var3 = NeonLyrics.cfg();
         if (!var2.options.hudHidden && var2.player != null && var3.islandEnabled) {
            if (!(var2.currentScreen instanceof LyricsScreen)) {
               AutoPerf.begin();

               try {
                  renderIsland(var0, var2, var3);
               } finally {
                  AutoPerf.end();
               }
            }
         }
      }
   }

   private static void renderIsland(DrawContext var0, MinecraftClient var1, Config var2) {
      long var3 = System.nanoTime();
      float var5 = Math.min(0.05F, (float)(var3 - lastNanos) / 1.0E9F);
      lastNanos = var3;
      long var6 = System.currentTimeMillis();
      int var8 = var0.getScaledWindowWidth();
      int var9 = var8 / 2;
      byte var10 = 5;
      float var11 = Gfx.alpha;
      Gfx.alpha = 1.0F;
      int var12 = Theme.accent();
      boolean var13 = note != null && var6 - noteTime < 2400L;
      NowPlaying var14 = SpotifyClient.now();
      boolean var15 = var2.lyricsIsland && var14.hasTrack();
      long var16 = var14.progressNow(var2.lyricsOffsetMs + 120);
      int var18 = var15 && var14.lyricsState == NowPlaying.LyricsState.SYNCED && var14.playing ? var14.lineIndex(var16) : -1;
      NowPlaying.Line var19 = var18 >= 0 ? var14.lines.get(var18) : null;
      boolean var20 = var19 != null && !var19.text().isBlank() && var16 <= var14.lineEnd(var18) + 400L;
      Island.Kind var21;
      String var22;
      String var23;
      if (var13) {
         var21 = Island.Kind.NOTE;
         var22 = note;
         var23 = "n" + noteTime;
      } else if (var20) {
         var21 = Island.Kind.LYRIC;
         var22 = var19.text();
         var23 = "l" + var14.trackId + var18;
      } else if (var15) {
         var21 = Island.Kind.SONG;
         var22 = var14.title + (var14.artist.isBlank() ? "" : "  ·  " + var14.artist);
         var23 = "s" + var14.trackId;
      } else {
         var21 = Island.Kind.IDLE;
         var22 = "";
         var23 = "i";
      }

      if (!var23.equals(curKey)) {
         prevKind = curKind;
         prevText = curText;
         curKey = var23;
         curKind = var21;
         curText = var22;
         switchTime = var6;
         if (var21 == Island.Kind.LYRIC) {
            SCROLL.snap(0.0F);
            HL_X.snap(0.0F);
            HL_W.snap(0.0F);
         }
      }

      int var24 = (int)Math.max(200.0F, Math.min(300.0F, var8 * 0.42F));
      float var25;
      float var26;
      switch (var21) {
         case NOTE:
            var25 = Math.min(var24, Gfx.w(var22) + 40);
            var26 = 18.0F;
            break;
         case SONG:
         case LYRIC:
            var25 = Math.max(110, Math.min(var24, Gfx.w(var22) + 58));
            var26 = 18.0F;
            break;
         default:
            var25 = 40.0F;
            var26 = 14.0F;
      }

      boolean var27 = var21 == Island.Kind.IDLE && !var2.showClock && !var2.showPing;
      float var28 = SHOW.update(var27 ? 0.0F : 1.0F, var5);
      float var29 = W.update(var25, var5);
      float var30 = H.update(var26, var5);
      if (var28 < 0.02F) {
         Gfx.alpha = var11;
      } else {
         int var31 = Math.max(12, Math.round(var29));
         int var32 = Math.max(8, Math.round(var30));
         int var33 = var9 - var31 / 2;
         int var34 = var32 / 2;
         int var35 = var10 + var32 / 2;
         Gfx.alpha = var28;
         if (var2.showClock) {
            String var36 = LocalTime.now().format(HM);
            Gfx.textBold(var0, var36, var33 - 10 - Gfx.w(var36, Gfx.BOLD), var35 - 4, 16777215, 0.95F, true);
         }

         int var46 = var33 + var31 + 10;
         if (var2.showPing && var1.getNetworkHandler() != null) {
            PlayerListEntry var37 = var1.getNetworkHandler().getPlayerListEntry(var1.player.getUuid());
            int var38 = var37 == null ? 0 : var37.getLatency();
            int var39 = var38 <= 0 ? 0 : (var38 < 60 ? 4 : (var38 < 120 ? 3 : (var38 < 250 ? 2 : 1)));
            float[] var40 = new float[9];
            float[] var41 = new float[9];

            for (int var42 = 0; var42 < 9; var42++) {
               double var43 = Math.PI + var42 * Math.PI / 8.0;
               var40[var42] = var46 + 3.5F + (float)Math.cos(var43) * 3.0F;
               var41[var42] = var35 + 0.5F + (float)Math.sin(var43) * 3.0F;
            }

            Gfx.aaStroke(var0, var40, var41, 1.1F, 10326968, 0.9F);
            String var55 = Integer.toString(var38);
            Gfx.textBold(var0, var55, var46 + 9, var35 - 4, 16777215, 0.95F, true);
            int var57 = var46 + 9 + Gfx.w(var55, Gfx.BOLD) + 5;

            for (int var44 = 0; var44 < 4; var44++) {
               int var45 = 2 + var44 * 2;
               Gfx.aaRound(var0, var57 + var44 * 3, var35 + 4 - var45, 2.0F, var45, 1.0F, 16777215, var44 < var39 ? 0.95F : 0.22F);
            }

            var46 = var57 + 16;
         }

         if (var2.showFps) {
            Gfx.text(var0, var1.getCurrentFps() + " fps", var46, var35 - 4, 10326968, 0.85F, true);
         }

         float var47 = var9 - var29 / 2.0F;
         float var48 = var30;
         float var49 = var48 / 2.0F;
         if (AutoPerf.allow(2)) {
            for (int var50 = 3; var50 >= 1; var50--) {
               Gfx.aaRound(var0, var47 - var50, var10 - var50 + 1.5F, var29 + var50 * 2, var48 + var50 * 2, var49 + var50, 0, 0.05F);
            }
         }

         Gfx.aaRound(var0, var47 - 0.75F, var10 - 0.75F, var29 + 1.5F, var48 + 1.5F, var49 + 0.75F, 12167664, 0.38F);
         Gfx.aaRound(var0, var47, var10, var29, var48, var49, 1380895, 0.94F);
         var0.enableScissor(var33 + 2, var10, var33 + var31 - 2, var10 + var32);
         float var51 = Anim.outCubic(Anim.since(switchTime, 380L));
         if (var51 < 1.0F && prevKind != Island.Kind.IDLE && !prevText.isEmpty()) {
            float var52 = var28 * (1.0F - var51);
            Gfx.alpha = var52 * 0.5F;
            drawSimple(var0, prevKind, prevText, var33 - Math.round(var51 * 3.0F), var10, var31, var32, var12, var14, var6, false);
            drawSimple(var0, prevKind, prevText, var33 + Math.round(var51 * 3.0F), var10, var31, var32, var12, var14, var6, false);
         }

         Gfx.alpha = var28 * var51;
         switch (var21) {
            case IDLE:
               float var53 = 0.5F + 0.5F * (float)Math.sin(var6 / 900.0);
               Gfx.aaCircle(var0, var9, var10 + var30 / 2.0F, 2.2F, var12, 0.55F + 0.35F * var53);
               break;
            case LYRIC:
               drawLyric(var0, var19, var14, var18, var16, var33, var10, var31, var32, var12, var5, var51);
               break;
            default:
               drawSimple(var0, var21, var22, var33, var10, var31, var32, var12, var14, var6, true);
         }

         var0.disableScissor();
         if (var21 == Island.Kind.LYRIC && var2.translate) {
            String var54 = Translator.get(var19.text());

            for (int var56 = 1; var56 <= 2 && var18 + var56 < var14.lines.size(); var56++) {
               Translator.prefetch(var14.lines.get(var18 + var56).text());
            }

            if (var54 != null) {
               Gfx.alpha = var28 * var51;
               var0.getMatrices().pushMatrix();
               var0.getMatrices().translate(var9, var10 + var32 + 4);
               var0.getMatrices().scale(0.85F, 0.85F);
               Gfx.center(var0, Gfx.ellipsize(var54, Math.round(var31 / 0.85F)), 0, 0, 14275058, 0.9F, true);
               var0.getMatrices().popMatrix();
            }
         }

         Gfx.alpha = var11;
      }
   }

   private static void cover(DrawContext var0, int var1, int var2, int var3, boolean var4, long var5) {
      if (!Cover.draw(var0, var1 - 6, var2 - 6, 12, 1.0F)) {
         Gfx.aaCircle(var0, var1, var2, 6.0F, Gfx.lerpColor(var3, 2760266, 0.35F), 1.0F);
         Gfx.aaCircle(var0, var1, var2, 4.6F, Gfx.lerpColor(var3, 16777215, 0.25F), 0.9F);
         double var7 = var4 ? var5 / 450.0 : 0.8;
         Gfx.aaCircle(var0, (float)(var1 + Math.cos(var7) * 3.0), (float)(var2 + Math.sin(var7) * 3.0), 1.1F, 16777215, 0.75F);
         Gfx.aaCircle(var0, var1, var2, 1.4F, 1380895, 1.0F);
      }
   }

   private static void drawSimple(
      DrawContext var0, Island.Kind var1, String var2, int var3, int var4, int var5, int var6, int var7, NowPlaying var8, long var9, boolean var11
   ) {
      int var12 = var4 + var6 / 2;
      int var13 = var12 - 4;
      if (var1 == Island.Kind.NOTE) {
         int var14 = noteOk ? 6022299 : 16735354;
         float var15 = var11 ? Anim.outBack(Anim.since(noteTime, 380L)) : 1.0F;
         float var16 = var3 + 11;
         float var17 = var4 + var6 / 2.0F;
         Gfx.aaCircle(var0, var16, var17, 5.0F * var15, var14, 1.0F);
         if (noteOk) {
            Gfx.aaStroke(
               var0, new float[]{var16 - 2.3F, var16 - 0.6F, var16 + 2.4F}, new float[]{var17 + 0.1F, var17 + 1.8F, var17 - 1.8F}, 1.3F * var15, 16777215, 1.0F
            );
         } else {
            Gfx.aaRound(var0, var16 - 2.5F, var17 - 0.7F, 5.0F, 1.4F, 0.7F, 16777215, 1.0F);
         }

         Gfx.text(var0, Gfx.ellipsize(var2, var5 - 30), var3 + 21, var13, 16777215, 1.0F, false);
      } else if (var1 == Island.Kind.SONG || var1 == Island.Kind.LYRIC) {
         cover(var0, var3 + 11, var12, var7, var8.playing, var9);
         equalizer(var0, var3 + var5 - 20, var12, var7, var8.playing, var9);
         Gfx.text(var0, Gfx.ellipsize(var2, var5 - 50), var3 + 22, var13, 16777215, var1 == Island.Kind.SONG ? 0.95F : 0.45F, false);
      }
   }

   private static void equalizer(DrawContext var0, int var1, int var2, int var3, boolean var4, long var5) {
      if (!AutoPerf.allow(4)) {
         var4 = false;
      }

      double var7 = var5 / 1000.0;
      NowPlaying var9 = SpotifyClient.now();
      float var10 = var9.beatPulse(var9.progressNow(NeonLyrics.cfg().lyricsOffsetMs));

      for (int var11 = 0; var11 < 4; var11++) {
         float var12 = var4
            ? (float)(3.0 + 3.5 * Math.abs(Math.sin(var7 * (2.6 + var11 * 1.1) + var11 * 1.7)) + 3.5 * var10 * (var11 % 2 == 0 ? 1.0 : 0.7))
            : 2.5F;
         int var13 = Math.round(var12);
         Gfx.aaRound(var0, var1 + var11 * 3, var2 - var12 / 2.0F, 2.0F, var12, 1.0F, var3, 0.95F);
      }
   }

   private static void drawLyric(
      DrawContext var0, NowPlaying.Line var1, NowPlaying var2, int var3, long var4, int var6, int var7, int var8, int var9, int var10, float var11, float var12
   ) {
      long var13 = System.currentTimeMillis();
      int var15 = var7 + var9 / 2;
      cover(var0, var6 + 11, var15, var10, var2.playing, var13);
      equalizer(var0, var6 + var8 - 20, var15, var10, var2.playing, var13);
      int var16 = var6 + 22;
      int var17 = var8 - 22 - 26;
      String[] var18 = var1.words();
      float var19 = var2.wordPosition(var3, var4);
      int var20 = (int)Math.floor(var19);
      int var21 = Gfx.w(" ");
      int[] var22 = new int[var18.length];
      int[] var23 = new int[var18.length];
      int var24 = 0;

      for (int var25 = 0; var25 < var18.length; var25++) {
         var22[var25] = var24;
         var23[var25] = Gfx.w(var18[var25]);
         var24 += var23[var25] + var21;
      }

      int var38 = var24 - var21;
      int var26 = Math.max(0, Math.min(var18.length - 1, var20 - 1));
      int var27 = Math.max(0, Math.min(var18.length - 1, var20));
      int var28 = var20 >= 0 ? var22[var26] : 0;
      int var29 = var20 >= 0 ? var22[var27] + var23[var27] - var22[var26] : 0;
      float var30 = var38 > var17 ? Math.max(0.0F, Math.min(var38 - var17, var22[var27] + var23[var27] / 2.0F - var17 * 0.55F)) : 0.0F;
      float var31 = SCROLL.update(var30, var11);
      float var32 = HL_X.update(var28, var11);
      float var33 = HL_W.update(var29, var11);
      int var34 = var16 - Math.round(var31);
      int var35 = var15 - 4;
      var0.enableScissor(var16 - 2, var7, var16 + var17 + 2, var7 + var9);
      if (var20 >= 0 && var20 < var18.length) {
         Gfx.aaRound(var0, var34 + var32 - 3.0F, var7 + 3, var33 + 6.0F, var9 - 6, 3.5F, var10, 0.85F);
      }

      for (int var36 = 0; var36 < var18.length; var36++) {
         boolean var37 = var36 <= var20;
         Gfx.text(var0, var18[var36], var34 + var22[var36], var35, 16777215, var37 ? 1.0F : 0.42F, false);
      }

      var0.disableScissor();
   }

   private enum Kind {
      IDLE,
      NOTE,
      SONG,
      LYRIC;
   }

   private static final class Spring {
      float x;
      float v;
      final float k;
      final float d;

      Spring(float var1, float var2, float var3) {
         this.x = var1;
         this.k = var2;
         this.d = var3;
      }

      float update(float var1, float var2) {
         int var3 = Math.max(1, (int)Math.ceil(var2 / 0.008F));
         float var4 = var2 / var3;

         for (int var5 = 0; var5 < var3; var5++) {
            float var6 = this.k * (var1 - this.x) - this.d * this.v;
            this.v += var6 * var4;
            this.x = this.x + this.v * var4;
         }

         return this.x;
      }

      void snap(float var1) {
         this.x = var1;
         this.v = 0.0F;
      }
   }
}
