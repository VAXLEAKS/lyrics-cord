package dev.neonlyrics.hud;

import net.minecraft.client.gui.DrawContext;

public final class Icons {
   private static final int U = 4;
   private static DrawContext G;
   private static int C;
   private static float A;

   private Icons() {
   }

   public static void draw(DrawContext var0, Icons.Icon var1, float var2, float var3, float var4, int var5, float var6) {
      if (!(var6 * Gfx.alpha <= 0.02F)) {
         G = var0;
         C = var5;
         A = var6;
         var0.getMatrices().pushMatrix();
         var0.getMatrices().translate(var2 - var4 / 2.0F, var3 - var4 / 2.0F);
         float var7 = var4 / 16.0F / 4.0F;
         var0.getMatrices().scale(var7, var7);
         switch (var1) {
            case HOME:
               line(2.0F, 8.0F, 8.0F, 2.5F);
               line(8.0F, 2.5F, 14.0F, 8.0F);
               line(3.8F, 7.0F, 3.8F, 13.5F);
               line(12.2F, 7.0F, 12.2F, 13.5F);
               line(3.8F, 13.5F, 12.2F, 13.5F);
               break;
            case LYRICS:
               line(6.0F, 12.0F, 6.0F, 3.2F);
               line(13.0F, 10.5F, 13.0F, 2.0F);
               line(6.0F, 3.2F, 13.0F, 2.0F, 2.2F);
               dot(4.3F, 12.3F, 2.0F);
               dot(11.3F, 10.8F, 2.0F);
               break;
            case TEXT:
               line(2.5F, 4.0F, 13.5F, 4.0F, 1.7F);
               line(2.5F, 8.0F, 11.0F, 8.0F, 1.7F);
               line(2.5F, 12.0F, 8.5F, 12.0F, 1.7F);
               break;
            case SKY:
               ring(7.0F, 8.5F, 4.6F);
               dot(12.5F, 3.5F, 1.1F);
               dot(14.0F, 7.0F, 0.8F);
               dot(10.5F, 1.8F, 0.7F);
               break;
            case SPOTIFY:
               arc(8.0F, 13.0F, 9.0F, 1.5F);
               arc(8.0F, 13.0F, 6.0F, 1.5F);
               arc(8.0F, 13.0F, 3.0F, 1.6F);
               break;
            case DISPLAY:
               box(2.0F, 3.0F, 14.0F, 11.5F);
               line(6.0F, 14.0F, 10.0F, 14.0F);
               line(8.0F, 11.5F, 8.0F, 14.0F);
               break;
            case PERF:
               line(9.5F, 1.8F, 4.5F, 9.0F);
               line(4.5F, 9.0F, 11.5F, 7.0F);
               line(11.5F, 7.0F, 6.5F, 14.2F);
               break;
            case FAV:
               line(4.0F, 2.0F, 12.0F, 2.0F);
               line(4.0F, 2.0F, 4.0F, 14.0F);
               line(12.0F, 2.0F, 12.0F, 14.0F);
               line(4.0F, 14.0F, 8.0F, 10.5F);
               line(12.0F, 14.0F, 8.0F, 10.5F);
               break;
            case SETTINGS:
               line(2.0F, 4.5F, 14.0F, 4.5F);
               line(2.0F, 8.0F, 14.0F, 8.0F);
               line(2.0F, 11.5F, 14.0F, 11.5F);
               dot(5.0F, 4.5F, 1.6F);
               dot(11.0F, 8.0F, 1.6F);
               dot(7.0F, 11.5F, 1.6F);
               break;
            case PLAY:
               tri(4.5F, 2.5F, 13.0F, 8.0F, 4.5F, 13.5F);
               break;
            case PAUSE:
               fill(4.0F, 3.0F, 6.6F, 13.0F);
               fill(9.4F, 3.0F, 12.0F, 13.0F);
               break;
            case NEXT:
               tri(3.0F, 3.0F, 10.5F, 8.0F, 3.0F, 13.0F);
               fill(11.0F, 3.0F, 13.0F, 13.0F);
               break;
            case PREV:
               tri(13.0F, 3.0F, 5.5F, 8.0F, 13.0F, 13.0F);
               fill(3.0F, 3.0F, 5.0F, 13.0F);
               break;
            case CLOSE:
               line(4.0F, 4.0F, 12.0F, 12.0F, 1.7F);
               line(12.0F, 4.0F, 4.0F, 12.0F, 1.7F);
               break;
            case BACK:
               line(10.0F, 3.0F, 5.0F, 8.0F, 1.8F);
               line(5.0F, 8.0F, 10.0F, 13.0F, 1.8F);
               break;
            case CHEVRON:
               line(6.0F, 3.0F, 11.0F, 8.0F, 1.8F);
               line(11.0F, 8.0F, 6.0F, 13.0F, 1.8F);
               break;
            case CHECK:
               line(3.0F, 8.5F, 6.5F, 12.0F, 2.0F);
               line(6.5F, 12.0F, 13.0F, 4.5F, 2.0F);
               break;
            case SYNC:
               arc(8.0F, 8.0F, 5.5F, 1.6F);
               line(13.5F, 8.0F, 11.6F, 6.0F, 1.6F);
               line(13.5F, 8.0F, 15.2F, 6.0F, 1.6F);
               break;
            case HAT:
               line(1.5F, 11.5F, 14.5F, 11.5F, 1.8F);
               line(4.5F, 11.5F, 5.2F, 4.5F);
               line(11.5F, 11.5F, 10.8F, 4.5F);
               line(5.2F, 4.5F, 10.8F, 4.5F);
               line(4.9F, 9.0F, 11.1F, 9.0F, 1.2F);
         }

         var0.getMatrices().popMatrix();
      }
   }

   private static void line(float var0, float var1, float var2, float var3) {
      line(var0, var1, var2, var3, 1.5F);
   }

   private static void line(float var0, float var1, float var2, float var3, float var4) {
      float var5 = (var2 - var0) * 4.0F;
      float var6 = (var3 - var1) * 4.0F;
      float var7 = (float)Math.sqrt(var5 * var5 + var6 * var6);
      int var8 = Math.max(1, Math.round(var4 * 4.0F / 2.0F));
      G.getMatrices().pushMatrix();
      G.getMatrices().translate(var0 * 4.0F, var1 * 4.0F);
      G.getMatrices().rotate((float)Math.atan2(var6, var5));
      G.fill(-var8 + 1, -var8, Math.round(var7) + var8 - 1, var8, Gfx.argb(C, A));
      G.getMatrices().popMatrix();
   }

   private static void box(float var0, float var1, float var2, float var3) {
      line(var0, var1, var2, var1);
      line(var2, var1, var2, var3);
      line(var2, var3, var0, var3);
      line(var0, var3, var0, var1);
   }

   private static void fill(float var0, float var1, float var2, float var3) {
      Gfx.round(G, Math.round(var0 * 4.0F), Math.round(var1 * 4.0F), Math.round((var2 - var0) * 4.0F), Math.round((var3 - var1) * 4.0F), 2, C, A);
   }

   private static void tri(float var0, float var1, float var2, float var3, float var4, float var5) {
      float var6 = Math.min(var1, Math.min(var3, var5));
      float var7 = Math.max(var1, Math.max(var3, var5));
      int var8 = Gfx.argb(C, A);

      for (int var9 = Math.round(var6 * 4.0F); var9 < Math.round(var7 * 4.0F); var9++) {
         float var10 = (var9 + 0.5F) / 4.0F;
         float var11 = Float.MAX_VALUE;
         float var12 = -Float.MAX_VALUE;
         float[][] var13 = new float[][]{{var0, var1, var2, var3}, {var2, var3, var4, var5}, {var4, var5, var0, var1}};

         for (float[] var17 : var13) {
            if (var10 >= var17[1] && var10 <= var17[3] || var10 >= var17[3] && var10 <= var17[1]) {
               float var18 = var17[3] == var17[1] ? 0.0F : (var10 - var17[1]) / (var17[3] - var17[1]);
               float var19 = var17[0] + (var17[2] - var17[0]) * var18;
               var11 = Math.min(var11, var19);
               var12 = Math.max(var12, var19);
            }
         }

         if (var12 > var11) {
            G.fill(Math.round(var11 * 4.0F), var9, Math.round(var12 * 4.0F), var9 + 1, var8);
         }
      }
   }

   private static void dot(float var0, float var1, float var2) {
      Gfx.circle(G, var0 * 4.0F, var1 * 4.0F, var2 * 4.0F, C, A);
   }

   private static void ring(float var0, float var1, float var2) {
      byte var3 = 14;

      for (int var4 = 0; var4 < var3; var4++) {
         double var5 = var4 * Math.PI * 2.0 / var3;
         double var7 = (var4 + 1) * Math.PI * 2.0 / var3;
         line(
            var0 + (float)Math.cos(var5) * var2, var1 + (float)Math.sin(var5) * var2, var0 + (float)Math.cos(var7) * var2, var1 + (float)Math.sin(var7) * var2
         );
      }
   }

   private static void arc(float var0, float var1, float var2, float var3) {
      byte var4 = 8;

      for (int var5 = 0; var5 < var4; var5++) {
         double var6 = 3.6128315516282616 + var5 * Math.PI * 0.7 / var4;
         double var8 = 3.6128315516282616 + (var5 + 1) * Math.PI * 0.7 / var4;
         line(
            var0 + (float)Math.cos(var6) * var2,
            var1 + (float)Math.sin(var6) * var2,
            var0 + (float)Math.cos(var8) * var2,
            var1 + (float)Math.sin(var8) * var2,
            var3
         );
      }
   }

   public enum Icon {
      HOME,
      LYRICS,
      TEXT,
      SKY,
      SPOTIFY,
      DISPLAY,
      PERF,
      FAV,
      SETTINGS,
      PLAY,
      PAUSE,
      NEXT,
      PREV,
      CLOSE,
      BACK,
      CHEVRON,
      CHECK,
      SYNC,
      HAT;
   }
}
