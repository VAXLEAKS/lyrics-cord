package dev.neonlyrics.hud;

import dev.neonlyrics.Beat;
import dev.neonlyrics.NeonLyrics;
import dev.neonlyrics.config.Config;
import dev.neonlyrics.lyrics.NowPlaying;
import dev.neonlyrics.lyrics.SpotifyClient;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Style;
import net.minecraft.text.StyleSpriteSource;
import net.minecraft.text.Text;
import net.minecraft.text.StyleSpriteSource.Font;
import net.minecraft.util.Identifier;

public final class BookLyrics {
   private static final StyleSpriteSource HAND = fd("caveat_lyrics");
   private static final StyleSpriteSource HAND_B = fd("caveat_bold");
   private static final StyleSpriteSource SMALL = fd("menu_s");
   private static final StyleSpriteSource BOLD = fd("menu_b");
   private static final int PAPER = 16249058;
   private static final int PAPER_EDGE = 14865853;
   private static final int PAPER_DARK = 13483942;
   private static final int INK = 2826521;
   private static final int INK_SOFT = 8020818;
   private static final int FAINT = 11048319;
   private static final int GOLD = 14268793;
   private static final int ROW_H = 11;
   private static final int MARGIN = 13;
   private static final int TOP = 14;
   private static final int BOTTOM = 16;
   private static final int HEADER_ROWS = 3;
   private static final long CLOSE_MS = 650L;
   private static final long SHOW_MS = 2300L;
   private static final long OPEN_MS = 750L;
   private static final long FLIP_MS = 720L;
   private static BookLyrics.Layout cur;
   private static BookLyrics.Layout prev;
   private static int layoutW = -1;
   private static long trackAt = -1L;
   private static int shownSpread = 0;
   private static int flipFrom = -1;
   private static int prevSpread = 0;
   private static long flipAt;
   private static float vis;
   private static long lastNs = System.nanoTime();
   static int li = -1;
   static float frac;

   private BookLyrics() {
   }

   private static StyleSpriteSource fd(String var0) {
      return new Font(Identifier.of("neonlyrics", var0));
   }

   private static Text comp(String var0, StyleSpriteSource var1) {
      return Text.literal(var0).fillStyle(Style.EMPTY.withFont(var1));
   }

   public static boolean active() {
      Config var0 = NeonLyrics.cfg();
      return var0.lyricsSky && var0.lyricsStyle == 2 && Beat.owner();
   }

   public static boolean inHands() {
      MinecraftClient var0 = MinecraftClient.getInstance();
      return active()
         && var0.player != null
         && var0.options.getPerspective().isFirstPerson()
         && var0.player.getMainHandStack().isEmpty()
         && var0.player.getOffHandStack().isEmpty();
   }

   static boolean update(boolean var0, int var1, int var2) {
      MinecraftClient var3 = MinecraftClient.getInstance();
      long var4 = System.nanoTime();
      float var6 = Math.min(0.05F, (float)(var4 - lastNs) / 1.0E9F);
      lastNs = var4;
      NowPlaying var7 = SpotifyClient.now();
      var0 = var0 && var7 != null && var7.hasTrack();
      float var8 = vis;
      vis = vis + ((var0 ? 1.0F : 0.0F) - vis) * Math.min(1.0F, var6 * 6.0F);
      if (!(vis < 0.01F) && var7 != null && var7.trackId != null) {
         Config var9 = NeonLyrics.cfg();
         TextRenderer var10 = var3.textRenderer;
         long var11 = System.currentTimeMillis();
         int var13 = var1 + 26;
         boolean var14 = cur == null || !var7.trackId.equals(cur.np.trackId);
         if (var14 || cur.np.lines != var7.lines || layoutW != var13) {
            BookLyrics.Layout var15 = layout(var7, var10, var1, var2);
            if (var14) {
               prev = cur;
               prevSpread = shownSpread;
               trackAt = prev != null && !(var8 < 0.05F) ? var11 : var11 - 650L;
               shownSpread = 0;
               flipFrom = -1;
            }

            cur = var15;
            layoutW = var13;
         }

         long var23 = var7.progressNow(var9.lyricsOffsetMs);
         li = var7.lines.isEmpty() ? -1 : var7.lineIndex(var23);
         frac = 0.0F;
         if (li >= 0) {
            NowPlaying.Line var17 = var7.lines.get(li);
            float var18 = var7.wordPosition(li, var23);
            int var19 = Math.max(1, var17.words().length);
            float var20 = (float)(var23 - var17.timeMs()) / (float)Math.max(400L, var7.lineEnd(li) - var17.timeMs());
            frac = var18 >= 0.0F ? Math.min(1.0F, (var18 + 0.6F) / var19) : Math.min(1.0F, var20 / 0.85F);
         }

         long var24 = var11 - trackAt;
         boolean var25 = var24 < 3700L;
         if (!var25) {
            int var26 = li >= 0 && cur.linePage.length != 0 ? cur.linePage[Math.min(li, cur.linePage.length - 1)] : 0;
            int var21 = Math.max(0, var26 / 2);
            if (var21 != shownSpread) {
               flipFrom = shownSpread;
               shownSpread = var21;
               flipAt = var11;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   static int phase() {
      long var0 = System.currentTimeMillis() - trackAt;
      return var0 < 650L ? 1 : (var0 < 2950L ? 2 : (var0 < 3700L ? 3 : 0));
   }

   static float phaseT() {
      long var0 = System.currentTimeMillis() - trackAt;

      return switch (phase()) {
         case 1 -> smooth((float)var0 / 650.0F);
         case 2 -> smooth(Math.min(1.0F, (float)(var0 - 650L) / 400.0F));
         case 3 -> smooth((float)(var0 - 650L - 2300L) / 750.0F);
         default -> 1.0F;
      };
   }

   static float flipT() {
      if (flipFrom < 0) {
         return -1.0F;
      } else {
         float var0 = Anim.clamp((float)(System.currentTimeMillis() - flipAt) / 720.0F);
         if (var0 >= 1.0F) {
            flipFrom = -1;
            return -1.0F;
         } else {
            return smooth(var0);
         }
      }
   }

   static BookLyrics.Layout cur() {
      return cur;
   }

   static BookLyrics.Layout prev() {
      return prev != null ? prev : cur;
   }

   static int prevSpread() {
      return prev != null ? prevSpread : 0;
   }

   static int spread() {
      return shownSpread;
   }

   static int flipFrom() {
      return flipFrom;
   }

   static float vis() {
      return Anim.outCubic(vis);
   }

   public static void render(DrawContext var0, RenderTickCounter var1) {
      MinecraftClient var2 = MinecraftClient.getInstance();
      int var3 = var0.getScaledWindowWidth();
      int var4 = var0.getScaledWindowHeight();
      int var5 = Math.round(Math.min(340.0F, var3 * 0.42F));
      int var6 = Math.round(var5 * 0.6F);
      int var7 = var5 / 2;
      int var8 = Math.max(3, (var6 - 14 - 16) / 11);
      boolean var9 = active() && !var2.options.hudHidden && var2.currentScreen == null && !inHands();
      if (!inHands()) {
         if (update(var9, var7 - 26, var8)) {
            NowPlaying var10 = cur.np;
            TextRenderer var11 = var2.textRenderer;
            long var12 = System.currentTimeMillis();
            long var14 = var12 - trackAt;
            boolean var16 = var14 < 3700L;
            float var17 = Anim.outCubic(vis);
            int var18 = var3 - var5 - 16;
            int var19 = Math.round(var4 - var6 - 46 + (1.0F - var17) * 30.0F);
            int var20 = var18 + var7;
            float var21 = Gfx.alpha;
            Gfx.alpha = var17;
            int[] var22 = Cover.colors();
            int var23 = var22 != null ? Gfx.lerpColor(var22[0], 1708556, 0.62F) : 3877407;
            if (var16 && var14 < 650L) {
               float var29 = smooth((float)var14 / 650.0F);
               BookLyrics.Layout var30 = prev != null ? prev : cur;
               int var26 = prev != null ? prevSpread : 0;
               openBase(var0, var18, var19, var5, var6, var7, var23, true, 1.0F - var29);
               pageContent(var0, var11, var30, var26 * 2 + 1, var20, var19, var5 - var7, var6, Integer.MAX_VALUE, 0.0F);
               flap(var0, var11, var20, var19, var5 - var7, var6, -1.0F + 2.0F * var29, var23, var30, var26 * 2, Integer.MAX_VALUE, 0.0F, true, var10);
            } else if (var16 && var14 < 2950L) {
               float var28 = smooth(Math.min(1.0F, (float)(var14 - 650L) / 400.0F));
               closedBase(var0, var20, var19, var5 - var7, var6, var23);
               coverFace(var0, var11, var20, var19, var5 - var7, var6, var23, var10, 0.85F + 0.15F * var28);
            } else if (var16) {
               float var24 = smooth((float)(var14 - 650L - 2300L) / 750.0F);
               openBase(var0, var18, var19, var5, var6, var7, var23, true, var24);
               pageContent(var0, var11, cur, 1, var20, var19, var5 - var7, var6, li, frac);
               flap(var0, var11, var20, var19, var5 - var7, var6, 1.0F - 2.0F * var24, var23, cur, 0, li, frac, true, var10);
            } else {
               float var27 = flipFrom < 0 ? 1.0F : Anim.clamp((float)(var12 - flipAt) / 720.0F);
               if (var27 >= 1.0F) {
                  flipFrom = -1;
               }

               openBase(var0, var18, var19, var5, var6, var7, var23, false, 1.0F);
               if (flipFrom < 0) {
                  pageContent(var0, var11, cur, shownSpread * 2, var18, var19, var7, var6, li, frac);
                  pageContent(var0, var11, cur, shownSpread * 2 + 1, var20, var19, var5 - var7, var6, li, frac);
               } else {
                  float var25 = smooth(var27);
                  pageContent(var0, var11, cur, flipFrom * 2, var18, var19, var7, var6, li, frac);
                  pageContent(var0, var11, cur, shownSpread * 2 + 1, var20, var19, var5 - var7, var6, li, frac);
                  flapPaper(var0, var11, var20, var19, var5 - var7, var6, 1.0F - 2.0F * var25, cur, flipFrom * 2 + 1, shownSpread * 2, li, frac);
               }
            }

            Gfx.alpha = var21;
         }
      }
   }

   private static void openBase(DrawContext var0, int var1, int var2, int var3, int var4, int var5, int var6, boolean var7, float var8) {
      int var9 = var1 + var5;
      int var10 = Math.round(var9 - var5 * Math.max(0.0F, var8));
      Gfx.aaRound(var0, var10 - 7, var2 + 5, var1 + var3 - var10 + 14, var4 + 9, 10.0F, 0, 0.3F);
      Gfx.aaRound(var0, var10 - 4, var2 - 3, var1 + var3 - var10 + 8, var4 + 8, 7.0F, var6, 0.98F);
      Gfx.aaRound(var0, var10 - 4, var2 + var4 + 2, var1 + var3 - var10 + 8, 3.0F, 1.5F, 0, 0.25F);

      for (int var11 = 3; var11 >= 1; var11--) {
         Gfx.aaRound(var0, var9, var2 + var11, var3 - var5 + var11, var4, 3.0F, var11 % 2 == 0 ? 13483942 : 14865853, 1.0F);
         if (var8 > 0.98F) {
            Gfx.aaRound(var0, var1 - var11, var2 + var11, var5 + var11, var4, 3.0F, var11 % 2 == 0 ? 13483942 : 14865853, 1.0F);
         }
      }

      paper(var0, var9, var2, var3 - var5, var4, false);
      if (var8 > 0.98F) {
         paper(var0, var1, var2, var5, var4, true);
      }

      if (var8 > 0.98F) {
         for (int var12 = 0; var12 < 8; var12++) {
            Gfx.rect(var0, var9 - 1 - var12, var2, 1, var4, 3877407, 0.16F * (1.0F - var12 / 8.0F));
         }
      }

      for (int var13 = 0; var13 < 8; var13++) {
         Gfx.rect(var0, var9 + var13, var2, 1, var4, 3877407, 0.16F * (1.0F - var13 / 8.0F));
      }
   }

   private static void paper(DrawContext var0, int var1, int var2, int var3, int var4, boolean var5) {
      Gfx.aaRound(var0, var1, var2, var3, var4, 3.0F, 16249058, 1.0F);
      int var6 = var5 ? var1 : var1 + var3 - 1;

      for (int var7 = 0; var7 < 5; var7++) {
         Gfx.rect(var0, var5 ? var6 + var7 : var6 - var7, var2 + 1, 1, var4 - 2, 9204565, 0.06F * (1.0F - var7 / 5.0F));
      }

      Gfx.vGradient(var0, var1, var2 + var4 - 10, var3, 10, 9204565, 9204565, 0.0F, 0.06F);
   }

   private static void closedBase(DrawContext var0, int var1, int var2, int var3, int var4, int var5) {
      Gfx.aaRound(var0, var1 - 4, var2 + 6, var3 + 14, var4 + 8, 9.0F, 0, 0.32F);
      Gfx.aaRound(var0, var1 - 2, var2 - 1, var3 + 8, var4 + 6, 6.0F, var5, 0.98F);

      for (int var6 = 3; var6 >= 1; var6--) {
         Gfx.aaRound(var0, var1 + 1, var2 + var6, var3 + var6, var4 - 1, 3.0F, var6 % 2 == 0 ? 13483942 : 14865853, 1.0F);
      }
   }

   private static void coverFace(DrawContext var0, TextRenderer var1, int var2, int var3, int var4, int var5, int var6, NowPlaying var7, float var8) {
      Gfx.aaRound(var0, var2 - 3, var3 - 3, var4 + 6, var5 + 6, 6.0F, var6, 1.0F);
      Gfx.vGradient(var0, var2 - 3, var3 - 3, var4 + 6, var5 / 2, 16777215, 16777215, 0.08F, 0.0F);

      for (int var9 = 0; var9 < 7; var9++) {
         Gfx.rect(var0, var2 - 3 + var9, var3 - 3, 1, var5 + 6, 0, 0.22F * (1.0F - var9 / 7.0F));
      }

      frame(var0, var2 + 7, var3 + 6, var4 - 14, var5 - 12, 14268793, 0.55F * var8);
      int var13 = Math.min(var5 - 54, var4 - 40);
      int var10 = var2 + var4 / 2 - var13 / 2;
      int var11 = var3 + 14;
      Gfx.aaRound(var0, var10 - 2, var11 - 2, var13 + 4, var13 + 4, 4.0F, 0, 0.35F);
      if (!Cover.drawSquare(var0, var10, var11, var13, var8)) {
         Gfx.aaRound(var0, var10, var11, var13, var13, 3.0F, Gfx.lerpColor(var6, 16777215, 0.12F), 1.0F);
         Gfx.aaCircle(var0, var10 + var13 / 2.0F, var11 + var13 / 2.0F, var13 * 0.3F, 14268793, 0.5F * var8);
      }

      frame(var0, var10 - 1, var11 - 1, var13 + 2, var13 + 2, 14268793, 0.7F * var8);
      String var12 = ellip(var1, var7.title, var4 - 22, HAND_B);
      txtC(var0, var1, var12, var2 + var4 / 2.0F, var11 + var13 + 7, 14268793, var8, HAND_B);
      txtC(
         var0,
         var1,
         ellip(var1, var7.artist, var4 - 26, SMALL),
         var2 + var4 / 2.0F,
         var11 + var13 + 20,
         Gfx.lerpColor(14268793, 16777215, 0.2F),
         0.75F * var8,
         SMALL
      );
   }

   private static void frame(DrawContext var0, float var1, float var2, float var3, float var4, int var5, float var6) {
      Gfx.aaRound(var0, var1, var2, var3, 1.0F, 0.5F, var5, var6);
      Gfx.aaRound(var0, var1, var2 + var4 - 1.0F, var3, 1.0F, 0.5F, var5, var6);
      Gfx.aaRound(var0, var1, var2, 1.0F, var4, 0.5F, var5, var6);
      Gfx.aaRound(var0, var1 + var3 - 1.0F, var2, 1.0F, var4, 0.5F, var5, var6);
   }

   private static void flap(
      DrawContext var0,
      TextRenderer var1,
      int var2,
      int var3,
      int var4,
      int var5,
      float var6,
      int var7,
      BookLyrics.Layout var8,
      int var9,
      int var10,
      float var11,
      boolean var12,
      NowPlaying var13
   ) {
      float var14 = Math.abs(var6);
      if (var6 > -0.98F) {
         Gfx.vGradient(var0, var2, var3, Math.round(var4 * (1.0F - var14) * 0.6F) + 2, var5, 0, 0, 0.18F * (1.0F - var14), 0.18F * (1.0F - var14));
      }

      var0.getMatrices().pushMatrix();
      var0.getMatrices().translate(var2, var3);
      var0.getMatrices().scale(Math.max(0.02F, var14) * Math.signum(var6 == 0.0F ? 1.0F : var6), 1.0F);
      if (var6 > 0.0F) {
         coverFace(var0, var1, 0, 0, var4, var5, var7, var13, 1.0F);
      } else {
         var0.getMatrices().translate(var4, 0.0F);
         var0.getMatrices().scale(-1.0F, 1.0F);
         Gfx.aaRound(var0, -3.0F, -3.0F, var4 + 3, var5 + 7, 6.0F, var7, 1.0F);
         paper(var0, 0, 0, var4, var5, true);
         pageContent(var0, var1, var8, var9, 0, 0, var4, var5, var10, var11);
      }

      Gfx.rect(var0, var6 > 0.0F ? 0 : 0, 0, var4, var5, 0, 0.28F * (1.0F - var14));
      var0.getMatrices().popMatrix();
   }

   private static void flapPaper(
      DrawContext var0,
      TextRenderer var1,
      int var2,
      int var3,
      int var4,
      int var5,
      float var6,
      BookLyrics.Layout var7,
      int var8,
      int var9,
      int var10,
      float var11
   ) {
      float var12 = Math.abs(var6);
      int var13 = var6 > 0.0F ? var2 : Math.round(var2 - var4 * var12);
      Gfx.vGradient(var0, var13, var3, Math.round(var4 * var12), var5, 0, 0, 0.1F * (1.0F - var12), 0.1F * (1.0F - var12));
      var0.getMatrices().pushMatrix();
      var0.getMatrices().translate(var2, var3 - (1.0F - var12) * 4.0F);
      var0.getMatrices().scale(Math.max(0.02F, var12) * (var6 >= 0.0F ? 1 : -1), 1.0F);
      if (var6 >= 0.0F) {
         paper(var0, 0, 0, var4, var5, false);
         pageContent(var0, var1, var7, var8, 0, 0, var4, var5, var10, var11);
      } else {
         var0.getMatrices().translate(var4, 0.0F);
         var0.getMatrices().scale(-1.0F, 1.0F);
         paper(var0, 0, 0, var4, var5, true);
         pageContent(var0, var1, var7, var9, 0, 0, var4, var5, var10, var11);
      }

      Gfx.rect(var0, 0, 0, var4, var5, 0, 0.22F * (1.0F - var12));
      Gfx.vGradient(var0, 0, 0, 6, var5, 16777215, 16777215, 0.15F * (1.0F - var12), 0.15F * (1.0F - var12));
      var0.getMatrices().popMatrix();
   }

   private static void pageContent(
      DrawContext var0, TextRenderer var1, BookLyrics.Layout var2, int var3, int var4, int var5, int var6, int var7, int var8, float var9
   ) {
      if (var2 != null) {
         NowPlaying var10 = var2.np;
         int var11 = var4 + 13;
         int var12 = var5 + 14;
         if (var3 == 0) {
            txt(var0, var1, ellip(var1, var10.title, var6 - 26, HAND_B), var11, var12 - 2, 2826521, 1.0F, HAND_B);
            txt(var0, var1, ellip(var1, var10.artist, var6 - 26, SMALL), var11, var12 + 10, 8020818, 0.9F, SMALL);
            Gfx.rect(var0, var11, var12 + 20, Math.min(46, var6 - 26), 1, 8020818, 0.45F);
         }

         if (var3 >= 0 && var3 < var2.pages.size()) {
            List var13 = var2.pages.get(var3);
            int var14 = var3 == 0 ? 3 : 0;

            for (int var15 = 0; var15 < var13.size(); var15++) {
               BookLyrics.Row var16 = (BookLyrics.Row)var13.get(var15);
               int var17 = var12 + (var14 + var15) * 11;
               if (var16.line < var8) {
                  txt(var0, var1, var16.text, var11, var17, 2826521, 0.92F, HAND);
               } else if (var16.line == var8) {
                  String var18 = var10.lines.get(var8).text();
                  float var19 = var9 * var18.length() - var16.from;
                  if (!(var19 <= 0.0F)) {
                     int var20 = Math.min(var16.text.length(), (int)var19);
                     String var21 = var16.text.substring(0, var20);
                     txt(var0, var1, var21, var11, var17, 2826521, 1.0F, HAND);
                     int var22 = var1.getWidth(comp(var21, HAND));
                     if (var20 < var16.text.length()) {
                        String var23 = var16.text.substring(var20, var20 + 1);
                        txt(var0, var1, var23, var11 + var22, var17, 2826521, var19 - var20, HAND);
                        float var24 = 0.6F + 0.4F * (float)Math.sin(System.currentTimeMillis() / 120.0);
                        Gfx.aaCircle(var0, var11 + var22 + var1.getWidth(comp(var23, HAND)) + 1.5F, var17 + 7, 1.2F, 2826521, 0.7F * var24);
                     }
                  }
               }
            }
         }

         if (var10.lines.isEmpty() && var3 == 0) {
            txt(var0, var1, "...", var11, var12 + 33, 11048319, 0.8F, HAND);
         }

         String var25 = String.valueOf(var3 + 1);
         txt(var0, var1, var25, var4 + var6 / 2 - var1.getWidth(comp(var25, SMALL)) / 2, var5 + var7 - 11, 11048319, 0.9F, SMALL);
      }
   }

   private static BookLyrics.Layout layout(NowPlaying var0, TextRenderer var1, int var2, int var3) {
      BookLyrics.Layout var4 = new BookLyrics.Layout(var0);
      var4.linePage = new int[var0.lines.size()];
      ArrayList var5 = new ArrayList();
      int var6 = var3 - 3;

      for (int var7 = 0; var7 < var0.lines.size(); var7++) {
         String var8 = var0.lines.get(var7).text().trim();
         ArrayList var9 = new ArrayList();
         if (!var8.isEmpty()) {
            StringBuilder var10 = new StringBuilder();
            int var11 = 0;
            int var12 = 0;

            for (String var16 : var8.split(" ")) {
               String var17 = var10.length() == 0 ? var16 : var10 + " " + var16;
               if (var10.length() > 0 && var1.getWidth(comp(var17, HAND)) > var2) {
                  var9.add(new BookLyrics.Row(var7, var11, var10.toString()));
                  var11 = var12;
                  var10 = new StringBuilder(var16);
               } else {
                  var10 = new StringBuilder(var17);
               }

               var12 += var16.length() + 1;
            }

            if (var10.length() > 0) {
               var9.add(new BookLyrics.Row(var7, var11, var10.toString()));
            }
         }

         if (!var9.isEmpty() && var5.size() + var9.size() > var6 && !var5.isEmpty()) {
            var4.pages.add(var5);
            var5 = new ArrayList();
            var6 = var3;
         }

         var4.linePage[var7] = var4.pages.size();
         var5.addAll(var9);
      }

      var4.pages.add(var5);
      return var4;
   }

   private static float smooth(float var0) {
      var0 = Anim.clamp(var0);
      return var0 * var0 * (3.0F - 2.0F * var0);
   }

   private static String ellip(TextRenderer var0, String var1, int var2, StyleSpriteSource var3) {
      if (var0.getWidth(comp(var1, var3)) <= var2) {
         return var1;
      }

      String var4 = var1;

      while (var4.length() > 1 && var0.getWidth(comp(var4 + "…", var3)) > var2) {
         var4 = var4.substring(0, var4.length() - 1);
      }

      return var4 + "…";
   }

   private static void txt(DrawContext var0, TextRenderer var1, String var2, int var3, int var4, int var5, float var6, StyleSpriteSource var7) {
      if (!(var6 <= 0.01F)) {
         var0.drawText(var1, comp(var2, var7), var3, var4, Gfx.argb(var5, Math.min(1.0F, var6) * Gfx.alpha), false);
      }
   }

   private static void txtC(DrawContext var0, TextRenderer var1, String var2, float var3, float var4, int var5, float var6, StyleSpriteSource var7) {
      txt(var0, var1, var2, Math.round(var3 - var1.getWidth(comp(var2, var7)) / 2.0F), Math.round(var4), var5, var6, var7);
   }

   static final class Layout {
      final NowPlaying np;
      final List<List<BookLyrics.Row>> pages = new ArrayList<>();
      int[] linePage = new int[0];

      Layout(NowPlaying var1) {
         this.np = var1;
      }
   }

   record Row(int line, int from, String text) {
   }
}
