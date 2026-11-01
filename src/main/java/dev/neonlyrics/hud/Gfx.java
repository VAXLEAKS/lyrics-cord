package dev.neonlyrics.hud;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import dev.neonlyrics.NeonLyrics;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.StyleSpriteSource;
import net.minecraft.text.Text;
import net.minecraft.text.StyleSpriteSource.Font;
import net.minecraft.util.Identifier;

public final class Gfx {
   public static float alpha = 1.0F;
   private static long revealStart = 0L;
   private static int revealTop = 0;
   private static int curY = Integer.MIN_VALUE;
   public static boolean revealOn = false;
   public static final Gfx.Role UI = Gfx.Role.UI;
   public static final Gfx.Role BOLD = Gfx.Role.BOLD;
   public static final Gfx.Role LYRICS = Gfx.Role.LYRICS;
   public static final String[] FONT_IDS = new String[]{
      "poppins",
      "nunito",
      "montserrat",
      "inter",
      "outfit",
      "spacegrotesk",
      "manrope",
      "dmsans",
      "plusjakartasans",
      "sora",
      "lexend",
      "rubik",
      "urbanist",
      "syne",
      "kanit",
      "fredoka",
      "baloo2",
      "unbounded",
      "orbitron",
      "bebasneue",
      "pacifico",
      "caveat"
   };
   public static final String[] FONT_NAMES = new String[]{
      "Poppins",
      "Nunito",
      "Montserrat",
      "Inter",
      "Outfit",
      "Space Grotesk",
      "Manrope",
      "DM Sans",
      "Jakarta Sans",
      "Sora",
      "Lexend",
      "Rubik",
      "Urbanist",
      "Syne",
      "Kanit",
      "Fredoka",
      "Baloo",
      "Unbounded",
      "Orbitron",
      "Bebas Neue",
      "Pacifico",
      "Caveat",
      "Minecraft"
   };
   private static final StyleSpriteSource[][] FD = new StyleSpriteSource[FONT_IDS.length][3];
   private static Gfx.Role cur;
   private static final Map<StyleSpriteSource, Map<String, Integer>> WIDTHS;

   private Gfx() {
   }

   public static void startReveal(int var0, long var1) {
      revealTop = var0;
      revealStart = var1;
   }

   private static float reveal(int var0) {
      if (revealOn && revealStart != 0L && NeonLyrics.cfg().fxMenuAnim && var0 != Integer.MIN_VALUE) {
         long var1 = System.currentTimeMillis() - revealStart;
         if (var1 > 900L) {
            revealStart = 0L;
            return 1.0F;
         } else {
            float var3 = (float)var1 * 0.55F;
            float var4 = var3 - (var0 - revealTop);
            return Math.max(0.0F, Math.min(1.0F, var4 / 40.0F));
         }
      } else {
         return 1.0F;
      }
   }

   public static int argb(int var0, float var1) {
      int var2 = Math.round(Math.max(0.0F, Math.min(1.0F, var1 * alpha * reveal(curY))) * 255.0F);
      return var2 << 24 | var0 & 16777215;
   }

   public static int argbRaw(int var0, float var1) {
      int var2 = Math.round(Math.max(0.0F, Math.min(1.0F, var1)) * 255.0F);
      return var2 << 24 | var0 & 16777215;
   }

   public static int lerpColor(int var0, int var1, float var2) {
      var2 = Math.max(0.0F, Math.min(1.0F, var2));
      int var3 = (int)((var0 >> 16 & 0xFF) + ((var1 >> 16 & 0xFF) - (var0 >> 16 & 0xFF)) * var2);
      int var4 = (int)((var0 >> 8 & 0xFF) + ((var1 >> 8 & 0xFF) - (var0 >> 8 & 0xFF)) * var2);
      int var5 = (int)((var0 & 0xFF) + ((var1 & 0xFF) - (var0 & 0xFF)) * var2);
      return var3 << 16 | var4 << 8 | var5;
   }

   public static int brighten(int var0, float var1) {
      return lerpColor(var0, 16777215, var1);
   }

   public static int hsv(float var0, float var1, float var2) {
      var0 -= (float)Math.floor(var0);
      int var3 = (int)(var0 * 6.0F);
      float var4 = var0 * 6.0F - var3;
      float var5 = var2 * (1.0F - var1);
      float var6 = var2 * (1.0F - var4 * var1);
      float var7 = var2 * (1.0F - (1.0F - var4) * var1);
      float var8;
      float var9;
      float var10;
      switch (var3 % 6) {
         case 0:
            var8 = var2;
            var9 = var7;
            var10 = var5;
            break;
         case 1:
            var8 = var6;
            var9 = var2;
            var10 = var5;
            break;
         case 2:
            var8 = var5;
            var9 = var2;
            var10 = var7;
            break;
         case 3:
            var8 = var5;
            var9 = var6;
            var10 = var2;
            break;
         case 4:
            var8 = var7;
            var9 = var5;
            var10 = var2;
            break;
         default:
            var8 = var2;
            var9 = var5;
            var10 = var6;
      }

      return (int)(var8 * 255.0F) << 16 | (int)(var9 * 255.0F) << 8 | (int)(var10 * 255.0F);
   }

   public static void rect(DrawContext var0, int var1, int var2, int var3, int var4, int var5, float var6) {
      if (var3 > 0 && var4 > 0 && !(var6 * alpha <= 0.004F)) {
         curY = var2;
         var0.fill(var1, var2, var1 + var3, var2 + var4, argb(var5, var6));
      }
   }

   private static int guiScale() {
      if (!revealOn && NeonLyrics.cfg().quality == 0) {
         return 1;
      }

      try {
         return Math.max(1, Math.min(2, Math.round(MinecraftClient.getInstance().getWindow().getScaleFactor())));
      } catch (Exception var1) {
         return 1;
      }
   }

   public static void round(DrawContext var0, int var1, int var2, int var3, int var4, int var5, int var6, float var7) {
      if (var3 > 0 && var4 > 0 && !(var7 * alpha <= 0.004F)) {
         curY = var2;
         var5 = Math.max(0, Math.min(var5, Math.min(var3, var4) / 2));
         int var8 = argb(var6, var7);
         if (var5 == 0) {
            var0.fill(var1, var2, var1 + var3, var2 + var4, var8);
         } else if (var5 >= 2 && Shapes.usable()) {
            aaRound(var0, var1, var2, var3, var4, var5, var6, var7);
         } else {
            int var9 = guiScale();
            if (var9 == 1) {
               roundRows(var0, var1, var2, var3, var4, var5, var8);
            } else {
               var0.fill(var1, var2 + var5, var1 + var3, var2 + var4 - var5, var8);
               var0.getMatrices().pushMatrix();
               var0.getMatrices().scale(1.0F / var9, 1.0F / var9);
               int var10 = var1 * var9;
               int var11 = var2 * var9;
               int var12 = var3 * var9;
               int var13 = var4 * var9;
               int var14 = var5 * var9;

               for (int var15 = 0; var15 < var14; var15++) {
                  double var16 = var14 - var15 - 0.5;
                  int var18 = (int)Math.round(var14 - Math.sqrt(Math.max(0.0, var14 * var14 - var16 * var16)));
                  var0.fill(var10 + var18, var11 + var15, var10 + var12 - var18, var11 + var15 + 1, var8);
                  var0.fill(var10 + var18, var11 + var13 - var15 - 1, var10 + var12 - var18, var11 + var13 - var15, var8);
               }

               var0.getMatrices().popMatrix();
            }
         }
      }
   }

   private static int fullScale() {
      try {
         return Math.max(1, Math.min(6, Math.round(MinecraftClient.getInstance().getWindow().getScaleFactor())));
      } catch (Exception var1) {
         return 2;
      }
   }

   public static void aaRound(DrawContext var0, float var1, float var2, float var3, float var4, float var5, int var6, float var7) {
      if (!(var3 <= 0.0F) && !(var4 <= 0.0F) && !(var7 * alpha <= 0.004F)) {
         int var8 = fullScale();
         float var9 = var1 * var8;
         float var10 = var2 * var8;
         float var11 = var3 * var8;
         float var12 = var4 * var8;
         float var13 = Math.max(0.0F, Math.min(var5 * var8, Math.min(var11, var12) / 2.0F));
         var0.getMatrices().pushMatrix();
         var0.getMatrices().scale(1.0F / var8, 1.0F / var8);
         int var14 = Math.round(var9);
         int var15 = Math.round(var10);
         int var16 = Math.round(var9 + var11);
         int var17 = Math.round(var10 + var12);
         int var18 = var16 - var14;
         int var19 = var17 - var15;
         int var20 = (int)Math.ceil(var13);
         int var21 = Math.min(Math.round(var13), Math.min(var18, var19) / 2);
         if (var21 > 96) {
            var21 = Math.min(512, (var21 + 4) / 8 * 8);
         }

         if (var21 * 2 > Math.min(var18, var19)) {
            int var22 = var21 * 2;
            if (var18 == var19) {
               int var23 = var14 + var18 / 2;
               int var24 = var15 + var19 / 2;
               var14 = var23 - var21;
               var15 = var24 - var21;
               var16 = var14 + var22;
               var17 = var15 + var22;
               var19 = var22;
               var18 = var22;
            } else {
               var21 = Math.min(var18, var19) / 2;
            }
         }

         if (var21 >= 2 && Shapes.usable()) {
            Identifier var33 = Shapes.circle(var21);
            if (var33 != null) {
               try {
                  int var36 = argb(var6, var7);
                  int var38 = var21 * 2;
                  RenderPipeline var39 = RenderPipelines.GUI_TEXTURED;
                  if (var18 == var38 && var19 == var38) {
                     var0.drawTexture(var39, var33, var14, var15, 0.0F, 0.0F, var38, var38, var38, var38, var36);
                  } else {
                     var0.drawTexture(var39, var33, var14, var15, 0.0F, 0.0F, var21, var21, var38, var38, var36);
                     var0.drawTexture(var39, var33, var16 - var21, var15, var21, 0.0F, var21, var21, var38, var38, var36);
                     var0.drawTexture(var39, var33, var14, var17 - var21, 0.0F, var21, var21, var21, var38, var38, var36);
                     var0.drawTexture(var39, var33, var16 - var21, var17 - var21, var21, var21, var21, var21, var38, var38, var36);
                     if (var18 > var38) {
                        var0.fill(var14 + var21, var15, var16 - var21, var15 + var21, var36);
                        var0.fill(var14 + var21, var17 - var21, var16 - var21, var17, var36);
                     }

                     if (var19 > var38) {
                        var0.fill(var14, var15 + var21, var16, var17 - var21, var36);
                     }
                  }

                  var0.getMatrices().popMatrix();
                  return;
               } catch (Throwable var32) {
                  Shapes.fail();
               }
            }
         }

         if (var19 - 2 * var20 > 0) {
            var0.fill(var14, var15 + var20, var16, var17 - var20, argb(var6, var7));
         }

         float[] var34 = new float[4];

         for (int var35 = 0; var35 < var20; var35++) {
            float var37 = Float.MAX_VALUE;
            float var25 = -1.0F;

            for (int var26 = 0; var26 < 4; var26++) {
               float var27 = var13 - (var35 + 0.125F + var26 * 0.25F);
               float var28 = var27 <= 0.0F ? 0.0F : var13 - (float)Math.sqrt(Math.max(0.0F, var13 * var13 - var27 * var27));
               var34[var26] = var28;
               var37 = Math.min(var37, var28);
               var25 = Math.max(var25, var28);
            }

            int var40 = (int)Math.ceil(var25);
            int var41 = var15 + var35;
            int var42 = var17 - 1 - var35;
            if (var18 - 2 * var40 > 0) {
               var0.fill(var14 + var40, var41, var16 - var40, var41 + 1, argb(var6, var7));
               if (var42 != var41) {
                  var0.fill(var14 + var40, var42, var16 - var40, var42 + 1, argb(var6, var7));
               }
            }

            for (int var29 = (int)Math.floor(var37); var29 < var40; var29++) {
               float var30 = 0.0F;

               for (int var31 = 0; var31 < 4; var31++) {
                  var30 += Math.max(0.0F, Math.min(1.0F, var29 + 1 - var34[var31]));
               }

               var30 /= 4.0F;
               if (!(var30 <= 0.01F)) {
                  int var44 = argb(var6, var7 * var30);
                  var0.fill(var14 + var29, var41, var14 + var29 + 1, var41 + 1, var44);
                  var0.fill(var16 - var29 - 1, var41, var16 - var29, var41 + 1, var44);
                  if (var42 != var41) {
                     var0.fill(var14 + var29, var42, var14 + var29 + 1, var42 + 1, var44);
                     var0.fill(var16 - var29 - 1, var42, var16 - var29, var42 + 1, var44);
                  }
               }
            }
         }

         var0.getMatrices().popMatrix();
      }
   }

   public static void aaCircle(DrawContext var0, float var1, float var2, float var3, int var4, float var5) {
      aaRound(var0, var1 - var3, var2 - var3, var3 * 2.0F, var3 * 2.0F, var3, var4, var5);
   }

   public static void aaStroke(DrawContext var0, float[] var1, float[] var2, float var3, int var4, float var5) {
      float var6 = Math.max(0.5F, var3 * 0.45F);

      for (int var7 = 0; var7 + 1 < var1.length; var7++) {
         float var8 = var1[var7 + 1] - var1[var7];
         float var9 = var2[var7 + 1] - var2[var7];
         float var10 = (float)Math.sqrt(var8 * var8 + var9 * var9);
         int var11 = Math.max(1, (int)(var10 / var6));

         for (int var12 = 0; var12 <= var11; var12++) {
            float var13 = (float)var12 / var11;
            aaCircle(var0, var1[var7] + var8 * var13, var2[var7] + var9 * var13, var3 / 2.0F, var4, var5);
         }
      }
   }

   private static void roundRows(DrawContext var0, int var1, int var2, int var3, int var4, int var5, int var6) {
      var0.fill(var1, var2 + var5, var1 + var3, var2 + var4 - var5, var6);

      for (int var7 = 0; var7 < var5; var7++) {
         double var8 = var5 - var7 - 0.5;
         int var10 = (int)Math.round(var5 - Math.sqrt(Math.max(0.0, var5 * var5 - var8 * var8)));
         var0.fill(var1 + var10, var2 + var7, var1 + var3 - var10, var2 + var7 + 1, var6);
         var0.fill(var1 + var10, var2 + var4 - var7 - 1, var1 + var3 - var10, var2 + var4 - var7, var6);
      }
   }

   public static void glow(DrawContext var0, int var1, int var2, int var3, int var4, int var5, int var6, float var7, int var8) {
      if (var8 >= 7 && !(var7 <= 0.05F) && !(alpha <= 0.01F)) {
         float var9 = Math.min(1.0F, var7);
         aaRound(var0, var1 - 3, var2 - 1, var3 + 6, var4 + 6, var5 + 3, 0, 0.14F * var9);
      }
   }

   public static void flowBorder(DrawContext var0, int var1, int var2, int var3, int var4, int var5, int var6, float var7, float var8) {
      flowBorder(var0, var1, var2, var3, var4, var5, var6, var7, var8, 1.0F);
   }

   public static void circle(DrawContext var0, float var1, float var2, float var3, int var4, float var5) {
      int var6 = Math.max(1, Math.round(var3));
      round(var0, Math.round(var1) - var6, Math.round(var2) - var6, var6 * 2, var6 * 2, var6, var4, var5);
   }

   public static void flowBorder(DrawContext var0, int var1, int var2, int var3, int var4, int var5, int var6, float var7, float var8, float var9) {
      float var10 = Math.max(0.0F, Math.min(1.0F, var7)) * Math.max(0.0F, Math.min(1.0F, var9));
      if (!(var10 <= 0.01F)) {
         rect(var0, var1, var2, var3, 1, 16777215, 0.1F * var10);
         rect(var0, var1, var2 + var4 - 1, var3, 1, 16777215, 0.04F * var10);
      }
   }

   public static void hGradient(DrawContext var0, int var1, int var2, int var3, int var4, int var5, int var6, float var7) {
      if (var3 > 0 && var4 > 0) {
         byte var8 = 2;

         for (int var9 = 0; var9 < var3; var9 += var8) {
            var0.fill(var1 + var9, var2, var1 + Math.min(var3, var9 + var8), var2 + var4, argb(lerpColor(var5, var6, (float)var9 / var3), var7));
         }
      }
   }

   public static void vGradient(DrawContext var0, int var1, int var2, int var3, int var4, int var5, int var6, float var7, float var8) {
      var0.fillGradient(var1, var2, var1 + var3, var2 + var4, argb(var5, var7), argb(var6, var8));
   }

   public static TextRenderer font() {
      return MinecraftClient.getInstance().textRenderer;
   }

   private static int uiIndex() {
      return NeonLyrics.cfg().uiFont;
   }

   public static boolean clean() {
      int var0 = uiIndex();
      return var0 >= 0 && var0 < FONT_IDS.length;
   }

   private static StyleSpriteSource resolve(Gfx.Role var0) {
      if (var0 == Gfx.Role.LYRICS) {
         int var1 = Math.floorMod(NeonLyrics.cfg().lyricsFont, FONT_IDS.length);
         return FD[var1][2];
      } else {
         return !clean() ? null : FD[uiIndex()][var0 == Gfx.Role.BOLD ? 1 : 0];
      }
   }

   public static void textIn(DrawContext var0, String var1, int var2, int var3, int var4, float var5, int var6, boolean var7) {
      if (!(var5 * alpha < 0.03F)) {
         MutableText var8 = var6 >= 0 && var6 < FONT_IDS.length
            ? Text.literal(var1).fillStyle(Style.EMPTY.withFont(FD[var6][var7 ? 1 : 0]))
            : Text.literal(var1);
         var0.drawText(font(), var8, var2, var3, argb(var4, var5), false);
      }
   }

   public static int wIn(String var0, int var1, boolean var2) {
      return var1 >= 0 && var1 < FONT_IDS.length
         ? font().getWidth(Text.literal(var0).fillStyle(Style.EMPTY.withFont(FD[var1][var2 ? 1 : 0])))
         : font().getWidth(var0);
   }

   public static Gfx.Role useFont(Gfx.Role var0) {
      Gfx.Role var1 = cur;
      cur = var0;
      return var1;
   }

   public static Text comp(String var0, Gfx.Role var1) {
      StyleSpriteSource var2 = resolve(var1);
      return var2 != null ? Text.literal(var0).fillStyle(Style.EMPTY.withFont(var2)) : Text.literal(var0);
   }

   public static int w(String var0) {
      return w(var0, cur);
   }

   public static int w(String var0, Gfx.Role var1) {
      if (var0 != null && !var0.isEmpty()) {
         StyleSpriteSource var2 = resolve(var1);
         if (var2 == null) {
            return font().getWidth(var0);
         }

         Map var3 = WIDTHS.computeIfAbsent(var2, var0x -> new HashMap<>());
         Integer var4 = (Integer)var3.get(var0);
         if (var4 == null) {
            if (var3.size() > 3000) {
               var3.clear();
            }

            var4 = font().getWidth(comp(var0, var1));
            var3.put(var0, var4);
         }

         return var4;
      } else {
         return 0;
      }
   }

   public static void clearWidths() {
      WIDTHS.clear();
   }

   public static void text(DrawContext var0, String var1, int var2, int var3, int var4, float var5, boolean var6) {
      if (!(var5 * alpha < 0.03F) && var1 != null) {
         curY = var3;
         if (resolve(cur) == null) {
            var0.drawText(font(), var1, var2, var3, argb(var4, var5), var6);
         } else {
            Text var7 = comp(var1, cur);
            if (var6 && NeonLyrics.cfg().quality > 0) {
               var0.getMatrices().pushMatrix();
               var0.getMatrices().translate(0.5F, 0.7F);
               var0.drawText(font(), var7, var2, var3, argb(328458, var5 * 0.45F), false);
               var0.getMatrices().popMatrix();
            }

            var0.drawText(font(), var7, var2, var3, argb(var4, var5), false);
         }
      }
   }

   public static void textBold(DrawContext var0, String var1, int var2, int var3, int var4, float var5, boolean var6) {
      Gfx.Role var7 = useFont(BOLD);
      text(var0, var1, var2, var3, var4, var5, var6);
      useFont(var7);
   }

   public static void text(DrawContext var0, String var1, int var2, int var3, int var4) {
      text(var0, var1, var2, var3, var4, 1.0F, false);
   }

   public static void center(DrawContext var0, String var1, int var2, int var3, int var4, float var5, boolean var6) {
      text(var0, var1, var2 - w(var1) / 2, var3, var4, var5, var6);
   }

   public static void right(DrawContext var0, String var1, int var2, int var3, int var4, float var5, boolean var6) {
      text(var0, var1, var2 - w(var1), var3, var4, var5, var6);
   }

   public static void glowText(DrawContext var0, String var1, int var2, int var3, int var4, float var5) {
      Gfx.Role var6 = useFont(BOLD);
      text(var0, var1, var2, var3, brighten(var4, 0.5F), var5, false);
      useFont(var6);
   }

   public static void rainbowText(DrawContext var0, String var1, int var2, int var3, int var4, int var5, float var6, float var7) {
      textBold(var0, var1, var2, var3, 16777215, var6, false);
   }

   public static String ellipsize(String var0, int var1) {
      if (var0 == null) {
         return "";
      }

      if (w(var0) <= var1) {
         return var0;
      }

      while (var0.length() > 1 && w(var0 + "…") > var1) {
         var0 = var0.substring(0, var0.length() - 1);
      }

      return var0 + "…";
   }

   public static List<String> wrap(String var0, int var1) {
      ArrayList var2 = new ArrayList();

      for (String var6 : var0.split("\n")) {
         StringBuilder var7 = new StringBuilder();

         for (String var11 : var6.split(" ")) {
            String var12 = var7.length() == 0 ? var11 : var7 + " " + var11;
            if (w(var12) > var1 && var7.length() > 0) {
               var2.add(var7.toString());
               var7 = new StringBuilder(var11);
            } else {
               var7 = new StringBuilder(var12);
            }
         }

         var2.add(var7.toString());
      }

      return var2;
   }

   static {
      String[] var0 = new String[]{"_ui", "_bold", "_lyrics"};

      for (int var1 = 0; var1 < FONT_IDS.length; var1++) {
         for (int var2 = 0; var2 < 3; var2++) {
            FD[var1][var2] = new Font(Identifier.of("neonlyrics", FONT_IDS[var1] + var0[var2]));
         }
      }

      cur = Gfx.Role.UI;
      WIDTHS = new HashMap<>();
   }

   public enum Role {
      UI,
      BOLD,
      LYRICS;
   }
}
