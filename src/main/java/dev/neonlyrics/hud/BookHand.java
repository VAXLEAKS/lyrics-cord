package dev.neonlyrics.hud;

import dev.neonlyrics.lyrics.NowPlaying;
import dev.neonlyrics.lyrics.SpotifyClient;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.font.TextRenderer.TextLayerType;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.MatrixStack.Entry;
import net.minecraft.text.Style;
import net.minecraft.text.StyleSpriteSource;
import net.minecraft.text.Text;
import net.minecraft.text.StyleSpriteSource.Font;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

public final class BookHand {
   public static boolean drawing;
   private static final Identifier WHITE = Identifier.of("neonlyrics", "textures/misc/white.png");
   private static final StyleSpriteSource HAND = fd("caveat_lyrics");
   private static final StyleSpriteSource HAND_B = fd("caveat_bold");
   private static final StyleSpriteSource SMALL = fd("menu_s");
   private static final int PAPER = 16249058;
   private static final int PAPER_EDGE = 15129026;
   private static final int PAPER_LINE = 13483942;
   private static final int INK = 2826521;
   private static final int INK_SOFT = 8020818;
   private static final int FAINT = 11048319;
   private static final int GOLD = 14268793;
   private static final float SPINE = 64.0F;
   private static final float HALF = 84.0F;
   private static final float Y0 = 14.0F;
   private static final float H = 104.0F;
   private static final float STACK = 4.0F;
   private static final float COVER = 1.4F;
   private static final float OVER = 2.6F;
   private static final float S = 0.95F;
   private static final float K = 0.78F;
   private static final float V = 14.0F;
   private static final int PAGE_W = Math.round(88.42105F);
   private static final int PAGE_H = Math.round(109.47369F);
   private static final int MARGIN = 9;
   private static final int TOP = 9;
   private static final int ROW_H = 10;
   private static final int HEADER_ROWS = 2;

   private BookHand() {
   }

   private static StyleSpriteSource fd(String var0) {
      return new Font(Identifier.of("neonlyrics", var0));
   }

   private static Text comp(String var0, StyleSpriteSource var1) {
      return Text.literal(var0).fillStyle(Style.EMPTY.withFont(var1));
   }

   public static boolean wants() {
      return BookLyrics.inHands() && !MinecraftClient.getInstance().options.hudHidden && SpotifyClient.now() != null && SpotifyClient.now().hasTrack();
   }

   public static void render(MatrixStack var0, OrderedRenderCommandQueue var1, int var2) {
      int var3 = (PAGE_H - 9 - 11) / 10;
      if (BookLyrics.update(true, PAGE_W - 18, var3)) {
         BookLyrics.Layout var4 = BookLyrics.cur();
         if (var4 != null) {
            var0.push();
            var0.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F));
            var0.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180.0F));
            var0.scale(0.38F, 0.38F, 0.38F);
            var0.translate(-0.5F, -0.5F, 0.0F);
            var0.scale(0.0078125F, 0.0078125F, 0.0078125F);
            float var5 = BookLyrics.vis();
            var0.translate(64.0F, 86.0F + (1.0F - var5) * 90.0F, 0.0F);
            var0.scale(0.78F, 0.78F, 0.78F);
            var0.translate(-64.0F, -64.0F, 0.0F);
            int[] var6 = Cover.colors();
            int var7 = var6 != null ? Gfx.lerpColor(var6[0], 1708556, 0.62F) : 3877407;
            int var8 = BookLyrics.li;
            float var9 = BookLyrics.frac;
            int var10 = BookLyrics.phase();
            float var11 = BookLyrics.phaseT();
            NowPlaying var12 = var4.np;
            half(var0, true);
            backHalf(var0, var1, var2, var7, true);
            var0.pop();
            if (var10 == 0) {
               half(var0, false);
               backHalf(var0, var1, var2, var7, false);
               var0.pop();
               float var13 = BookLyrics.flipT();
               int var14 = BookLyrics.spread();
               if (var13 < 0.0F) {
                  page(var0, var1, var2, var4, var14 * 2, false, var8, var9, 0.0F);
                  page(var0, var1, var2, var4, var14 * 2 + 1, true, var8, var9, 0.0F);
               } else {
                  int var15 = BookLyrics.flipFrom();
                  page(var0, var1, var2, var4, var15 * 2, false, var8, var9, 0.0F);
                  page(var0, var1, var2, var4, var14 * 2 + 1, true, var8, var9, 0.0F);
                  sheet(var0, var1, var2, var4, var15 * 2 + 1, var14 * 2, var8, var9, var13);
               }
            } else if (var10 == 1) {
               BookLyrics.Layout var16 = BookLyrics.prev();
               page(var0, var1, var2, var16, BookLyrics.prevSpread() * 2 + 1, true, Integer.MAX_VALUE, 0.0F, 0.0F);
               flap(var0, var1, var2, var7, var16, BookLyrics.prevSpread() * 2, Integer.MAX_VALUE, 0.0F, var11 * 180.0F, var12);
            } else if (var10 == 2) {
               flap(var0, var1, var2, var7, var4, 0, var8, var9, 180.0F, var12);
            } else {
               page(var0, var1, var2, var4, 1, true, var8, var9, 0.0F);
               flap(var0, var1, var2, var7, var4, 0, var8, var9, (1.0F - var11) * 180.0F, var12);
            }

            var0.pop();
         }
      }
   }

   private static void backHalf(MatrixStack var0, OrderedRenderCommandQueue var1, int var2, int var3, boolean var4) {
      float var5 = var4 ? 64.0F : -20.0F;
      float var6 = var4 ? 148.0F : 64.0F;
      float var7 = var4 ? var5 : var5 - 2.6F;
      float var8 = var4 ? var6 + 2.6F : var6;
      box(var0, var1, var2, var7, 11.4F, 4.0F, var8, 120.6F, 5.4F, var3);
      float var9 = var4 ? var6 : var5;
      face(var0, var1, var2, var9, 14.0F, 0.0F, var9, 118.0F, 4.0F, 15129026, 0.86F);
      face(var0, var1, var2, var5, 118.0F, 0.0F, var6, 118.0F, 4.0F, 15129026, 0.8F);
      face(var0, var1, var2, var5, 14.0F, 0.0F, var6, 14.0F, 4.0F, 15129026, 0.95F);

      for (int var10 = 1; var10 < 4; var10++) {
         float var11 = 4.0F * var10 / 4.0F;
         face(var0, var1, var2, var9 - 0.01F * (var4 ? -1 : 1), 14.0F, var11 - 0.12F, var9 - 0.01F * (var4 ? -1 : 1), 118.0F, var11 + 0.12F, 13483942, 0.9F);
      }
   }

   private static void page(
      MatrixStack var0, OrderedRenderCommandQueue var1, int var2, BookLyrics.Layout var3, int var4, boolean var5, int var6, float var7, float var8
   ) {
      boolean var9 = var8 >= 0.0F;
      if (var9) {
         half(var0, var5);
      }

      float var10 = var5 ? 64.0F : -20.0F;
      paperSurface(var0, var1, var2, var10, var5);
      var0.push();
      var0.translate(var10, 14.0F, -0.05F);
      var0.scale(0.95F, 0.95F, 1.0F);
      pageText(var0, var1, var2, var3, var4, var6, var7);
      var0.pop();
      if (var9) {
         var0.pop();
      }
   }

   private static void half(MatrixStack var0, boolean var1) {
      var0.push();
      var0.translate(64.0F, 0.0F, 0.0F);
      var0.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(var1 ? 14.0F : -14.0F));
      var0.translate(-64.0F, 0.0F, 0.0F);
   }

   private static void paperSurface(MatrixStack var0, OrderedRenderCommandQueue var1, int var2, float var3, boolean var4) {
      byte var5 = 10;
      float var6 = 9.0F;

      for (int var7 = 0; var7 < var5; var7++) {
         float var8 = (float)var7 / var5;
         float var9 = (float)(var7 + 1) / var5;
         float var10 = 2.6F * (1.0F - var8) * (1.0F - var8);
         float var11 = 2.6F * (1.0F - var9) * (1.0F - var9);
         float var12 = var4 ? var3 + var8 * var6 : var3 + 84.0F - var8 * var6;
         float var13 = var4 ? var3 + var9 * var6 : var3 + 84.0F - var9 * var6;
         float var14 = 0.72F + 0.28F * var8;
         face(var0, var1, var2, Math.min(var12, var13), 14.0F, var4 ? var10 : var11, Math.max(var12, var13), 118.0F, var4 ? var11 : var10, 16249058, var14);
      }

      float var15 = var4 ? var3 + var6 : var3;
      float var16 = var4 ? var3 + 84.0F : var3 + 84.0F - var6;
      face(var0, var1, var2, var15, 14.0F, 0.0F, var16, 118.0F, 0.0F, 16249058, 1.0F);
      float var17 = var4 ? var3 + 84.0F - 2.0F : var3;
      face(var0, var1, var2, var17, 14.0F, -0.01F, var17 + 2.0F, 118.0F, -0.01F, 9204565, 0.12F, true);
   }

   private static void flap(
      MatrixStack var0, OrderedRenderCommandQueue var1, int var2, int var3, BookLyrics.Layout var4, int var5, int var6, float var7, float var8, NowPlaying var9
   ) {
      var0.push();
      var0.translate(64.0F, 0.0F, 0.0F);
      var0.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-(14.0F + var8 * 152.0F / 180.0F)));
      var0.translate(-64.0F, 0.0F, 0.0F);
      backHalf(var0, var1, var2, var3, false);
      if (var8 < 178.0F) {
         page(var0, var1, var2, var4, var5, false, var6, var7, -1.0F);
      }

      coverOutside(var0, var1, var2, var3, var9);
      var0.pop();
   }

   private static void coverOutside(MatrixStack var0, OrderedRenderCommandQueue var1, int var2, int var3, NowPlaying var4) {
      float var5 = 5.42F;
      var0.push();
      var0.translate(128.0F, 0.0F, var5);
      var0.scale(-1.0F, 1.0F, 1.0F);
      float var6 = 64.0F;
      float var7 = 148.0F;
      frameFace(var0, var1, var2, var6 + 5.0F, 18.0F, var7 - 5.0F, 114.0F, 14268793, 0.6F);
      float var8 = 52.0F;
      float var9 = (var6 + var7) / 2.0F - var8 / 2.0F;
      float var10 = 24.0F;
      Identifier var11 = Cover.squareTexture();
      if (var11 != null) {
         texQuad(var0, var1, var2, var11, var9, var10, var9 + var8, var10 + var8);
      } else {
         face(var0, var1, var2, var9, var10, 0.0F, var9 + var8, var10 + var8, 0.0F, Gfx.lerpColor(var3, 16777215, 0.12F), 1.0F, true);
      }

      frameFace(var0, var1, var2, var9 - 1.0F, var10 - 1.0F, var9 + var8 + 1.0F, var10 + var8 + 1.0F, 14268793, 0.8F);
      var0.push();
      var0.translate((var6 + var7) / 2.0F, var10 + var8 + 6.0F, -0.02F);
      var0.scale(0.95F, 0.95F, 1.0F);
      TextRenderer var12 = MinecraftClient.getInstance().textRenderer;
      String var13 = ellip(var12, var4.title, Math.round(77.89474F), HAND_B);
      text(var0, var1, var2, var13, -var12.getWidth(comp(var13, HAND_B)) / 2.0F, 0.0F, 14268793, 1.0F, HAND_B);
      String var14 = ellip(var12, var4.artist, Math.round(75.789474F), SMALL);
      text(var0, var1, var2, var14, -var12.getWidth(comp(var14, SMALL)) / 2.0F, 14.0F, Gfx.lerpColor(14268793, 16777215, 0.25F), 0.8F, SMALL);
      var0.pop();
      var0.pop();
   }

   private static void sheet(
      MatrixStack var0, OrderedRenderCommandQueue var1, int var2, BookLyrics.Layout var3, int var4, int var5, int var6, float var7, float var8
   ) {
      float var9 = 14.0F + var8 * 152.0F;
      var0.push();
      var0.translate(64.0F, 0.0F, -((float)Math.sin(var8 * Math.PI)) * 6.0F);
      var0.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(var9));
      var0.translate(-64.0F, 0.0F, 0.0F);
      float var10 = 0.78F + 0.22F * (float)Math.abs(Math.cos(var8 * Math.PI));
      face(var0, var1, var2, 64.0F, 14.0F, -0.2F, 148.0F, 118.0F, -0.2F, 16249058, var10, true);
      var0.push();
      var0.translate(64.0F, 14.0F, -0.25F);
      var0.scale(0.95F, 0.95F, 1.0F);
      pageText(var0, var1, var2, var3, var4, var6, var7);
      var0.pop();
      var0.push();
      var0.translate(148.0F, 14.0F, -0.15F);
      var0.scale(-0.95F, 0.95F, 1.0F);
      pageText(var0, var1, var2, var3, var5, var6, var7);
      var0.pop();
      var0.pop();
   }

   private static void pageText(MatrixStack var0, OrderedRenderCommandQueue var1, int var2, BookLyrics.Layout var3, int var4, int var5, float var6) {
      if (var3 != null) {
         TextRenderer var7 = MinecraftClient.getInstance().textRenderer;
         NowPlaying var8 = var3.np;
         byte var9 = 9;
         byte var10 = 9;
         if (var4 == 0) {
            text(var0, var1, var2, ellip(var7, var8.title, PAGE_W - 18, HAND_B), var9, var10 - 2, 2826521, 1.0F, HAND_B);
            text(var0, var1, var2, ellip(var7, var8.artist, PAGE_W - 18, SMALL), var9, var10 + 8, 8020818, 0.9F, SMALL);
         }

         if (var4 >= 0 && var4 < var3.pages.size()) {
            List var11 = var3.pages.get(var4);
            int var12 = var4 == 0 ? 2 : 0;

            for (int var13 = 0; var13 < var11.size(); var13++) {
               BookLyrics.Row var14 = (BookLyrics.Row)var11.get(var13);
               int var15 = var10 + (var12 + var13) * 10;
               if (var14.line() < var5) {
                  text(var0, var1, var2, var14.text(), var9, var15, 2826521, 0.95F, HAND);
               } else if (var14.line() == var5) {
                  String var16 = var8.lines.get(var5).text();
                  float var17 = var6 * var16.length() - var14.from();
                  if (!(var17 <= 0.0F)) {
                     int var18 = Math.min(var14.text().length(), (int)var17);
                     String var19 = var14.text().substring(0, var18);
                     text(var0, var1, var2, var19, var9, var15, 2826521, 1.0F, HAND);
                     if (var18 < var14.text().length()) {
                        int var20 = var7.getWidth(comp(var19, HAND));
                        String var21 = var14.text().substring(var18, var18 + 1);
                        text(var0, var1, var2, var21, var9 + var20, var15, 2826521, var17 - var18, HAND);
                        float var22 = var9 + var20 + var7.getWidth(comp(var21, HAND)) + 1.5F;
                        face(var0, var1, var2, var22 - 1.2F, var15 + 6, -0.03F, var22 + 1.2F, var15 + 8.4F, -0.03F, 2826521, 1.0F, true);
                     }
                  }
               }
            }
         }

         String var23 = String.valueOf(var4 + 1);
         text(var0, var1, var2, var23, PAGE_W / 2.0F - var7.getWidth(comp(var23, SMALL)) / 2.0F, PAGE_H - 10, 11048319, 0.9F, SMALL);
      }
   }

   private static RenderLayer white() {
      return RenderLayers.text(WHITE);
   }

   private static void box(
      MatrixStack var0, OrderedRenderCommandQueue var1, int var2, float var3, float var4, float var5, float var6, float var7, float var8, int var9
   ) {
      face(var0, var1, var2, var3, var4, var5, var6, var7, var5, var9, 1.0F, true);
      face(var0, var1, var2, var3, var4, var8, var6, var7, var8, var9, 0.7F, true);
      face(var0, var1, var2, var3, var4, var5, var3, var7, var8, var9, 0.78F);
      face(var0, var1, var2, var6, var4, var5, var6, var7, var8, var9, 0.78F);
      face(var0, var1, var2, var3, var4, var5, var6, var4, var8, var9, 0.9F);
      face(var0, var1, var2, var3, var7, var5, var6, var7, var8, var9, 0.62F);
   }

   private static void face(
      MatrixStack var0, OrderedRenderCommandQueue var1, int var2, float var3, float var4, float var5, float var6, float var7, float var8, int var9, float var10
   ) {
      face(var0, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, false);
   }

   private static void face(
      MatrixStack var0,
      OrderedRenderCommandQueue var1,
      int var2,
      float var3,
      float var4,
      float var5,
      float var6,
      float var7,
      float var8,
      int var9,
      float var10,
      boolean var11
   ) {
      int var12 = 0xFF000000 | Gfx.lerpColor(0, var9, var10);
      float[][] var13;
      if (!var11 && (var3 == var6 || var4 == var7)) {
         if (var3 == var6) {
            var13 = new float[][]{{var3, var4, var5}, {var3, var4, var8}, {var3, var7, var8}, {var3, var7, var5}};
         } else {
            var13 = new float[][]{{var3, var4, var5}, {var6, var4, var5}, {var6, var4, var8}, {var3, var4, var8}};
         }
      } else {
         var13 = new float[][]{{var3, var4, var5}, {var6, var4, var8}, {var6, var7, var8}, {var3, var7, var5}};
      }

      float[][] var14 = var13;
      var1.submitCustom(var0, white(), (var3x, var4x) -> quad(var3x, var4x, var14, var12, var2, 0.0F, 0.0F, 1.0F, 1.0F));
   }

   private static void texQuad(MatrixStack var0, OrderedRenderCommandQueue var1, int var2, Identifier var3, float var4, float var5, float var6, float var7) {
      float[][] var8 = new float[][]{{var4, var5, 0.0F}, {var6, var5, 0.0F}, {var6, var7, 0.0F}, {var4, var7, 0.0F}};
      var1.submitCustom(var0, RenderLayers.text(var3), (var2x, var3x) -> quad(var2x, var3x, var8, -1, var2, 0.0F, 0.0F, 1.0F, 1.0F));
   }

   private static void quad(Entry var0, VertexConsumer var1, float[][] var2, int var3, int var4, float var5, float var6, float var7, float var8) {
      float[][] var9 = new float[][]{{var5, var6}, {var7, var6}, {var7, var8}, {var5, var8}};

      for (int var10 = 0; var10 < 4; var10++) {
         var1.vertex(var0, var2[var10][0], var2[var10][1], var2[var10][2]).color(var3).texture(var9[var10][0], var9[var10][1]).light(var4);
      }

      for (int var11 = 3; var11 >= 0; var11--) {
         var1.vertex(var0, var2[var11][0], var2[var11][1], var2[var11][2]).color(var3).texture(var9[var11][0], var9[var11][1]).light(var4);
      }
   }

   private static void frameFace(
      MatrixStack var0, OrderedRenderCommandQueue var1, int var2, float var3, float var4, float var5, float var6, int var7, float var8
   ) {
      int var9 = Gfx.lerpColor(2760212, var7, var8);
      float var10 = 0.6F;
      face(var0, var1, var2, var3, var4, -0.01F, var5, var4 + var10, -0.01F, var9, 1.0F, true);
      face(var0, var1, var2, var3, var6 - var10, -0.01F, var5, var6, -0.01F, var9, 1.0F, true);
      face(var0, var1, var2, var3, var4, -0.01F, var3 + var10, var6, -0.01F, var9, 1.0F, true);
      face(var0, var1, var2, var5 - var10, var4, -0.01F, var5, var6, -0.01F, var9, 1.0F, true);
   }

   private static void text(
      MatrixStack var0, OrderedRenderCommandQueue var1, int var2, String var3, float var4, float var5, int var6, float var7, StyleSpriteSource var8
   ) {
      if (!(var7 <= 0.02F) && !var3.isEmpty()) {
         int var9 = Gfx.argb(var6, Math.min(1.0F, var7));
         var1.submitText(var0, var4, var5, comp(var3, var8).asOrderedText(), false, TextLayerType.POLYGON_OFFSET, var2, var9, 0, 0);
      }
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
}
