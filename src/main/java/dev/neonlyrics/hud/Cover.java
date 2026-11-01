package dev.neonlyrics.hud;

import dev.neonlyrics.NeonLyrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import javax.imageio.ImageIO;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Identifier;

public final class Cover {
   private static final Identifier ID = Identifier.of("neonlyrics", "cover");
   private static final Identifier ID_SQ = Identifier.of("neonlyrics", "cover_sq");
   private static final int SIZE = 64;
   private static final int SQ = 256;
   private static volatile Cover.Pending pending;
   private static volatile boolean clearPending;
   private static volatile long loadedAt = 0L;
   private static NativeImageBackedTexture tex;
   private static NativeImageBackedTexture texSq;
   private static boolean ready;
   private static long shownAt;
   private static int col1 = -1;
   private static int col2 = -1;

   private Cover() {
   }

   public static void load(byte[] var0) {
      try {
         BufferedImage var1 = ImageIO.read(new ByteArrayInputStream(var0));
         if (var1 == null) {
            return;
         }

         BufferedImage var2 = scale(var1, 64);
         float var3 = 32.0F;

         for (int var4 = 0; var4 < 64; var4++) {
            for (int var5 = 0; var5 < 64; var5++) {
               float var6 = var5 + 0.5F - var3;
               float var7 = var4 + 0.5F - var3;
               float var8 = Math.max(0.0F, Math.min(1.0F, var3 - (float)Math.sqrt(var6 * var6 + var7 * var7)));
               int var9 = var2.getRGB(var5, var4);
               var2.setRGB(var5, var4, Math.round((var9 >>> 24 & 0xFF) * var8) << 24 | var9 & 16777215);
            }
         }

         BufferedImage var11 = scale(var1, 256);
         float var12 = 11.52F;

         for (int var13 = 0; var13 < 256; var13++) {
            for (int var15 = 0; var15 < 256; var15++) {
               float var17 = cornerA(var15, var13, 256, var12);
               if (!(var17 >= 1.0F)) {
                  int var18 = var11.getRGB(var15, var13);
                  var11.setRGB(var15, var13, Math.round((var18 >>> 24 & 0xFF) * var17) << 24 | var18 & 16777215);
               }
            }
         }

         int var14 = avg(var11, 0, 128);
         int var16 = avg(var11, 128, 256);
         pending = new Cover.Pending(png(var2), png(var11), var14, var16);
         loadedAt = System.currentTimeMillis();
      } catch (Throwable var10) {
         NeonLyrics.LOG.debug("Cover konnte nicht geladen werden", var10);
      }
   }

   private static BufferedImage scale(BufferedImage var0, int var1) {
      BufferedImage var2 = new BufferedImage(var1, var1, 2);
      Graphics2D var3 = var2.createGraphics();
      var3.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
      var3.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
      int var4 = Math.min(var0.getWidth(), var0.getHeight());
      int var5 = (var0.getWidth() - var4) / 2;
      int var6 = (var0.getHeight() - var4) / 2;
      var3.drawImage(var0, 0, 0, var1, var1, var5, var6, var5 + var4, var6 + var4, null);
      var3.dispose();
      return var2;
   }

   private static float cornerA(int var0, int var1, int var2, float var3) {
      float var4 = var0 + 0.5F;
      float var5 = var1 + 0.5F;
      float var6 = Math.max(0.0F, Math.max(var3 - var4, var4 - (var2 - var3)));
      float var7 = Math.max(0.0F, Math.max(var3 - var5, var5 - (var2 - var3)));
      if (var6 != 0.0F && var7 != 0.0F) {
         float var8 = (float)Math.sqrt(var6 * var6 + var7 * var7);
         return Math.max(0.0F, Math.min(1.0F, var3 - var8 + 0.5F));
      } else {
         return 1.0F;
      }
   }

   private static int avg(BufferedImage var0, int var1, int var2) {
      long var3 = 0L;
      long var5 = 0L;
      long var7 = 0L;
      long var9 = 0L;

      for (int var11 = var1; var11 < var2; var11 += 4) {
         for (int var12 = 0; var12 < var0.getWidth(); var12 += 4) {
            int var13 = var0.getRGB(var12, var11);
            var3 += var13 >> 16 & 0xFF;
            var5 += var13 >> 8 & 0xFF;
            var7 += var13 & 0xFF;
            var9++;
         }
      }

      return var9 == 0L ? 2105380 : (int)(var3 / var9) << 16 | (int)(var5 / var9) << 8 | (int)(var7 / var9);
   }

   private static byte[] png(BufferedImage var0) throws Exception {
      ByteArrayOutputStream var1 = new ByteArrayOutputStream();
      ImageIO.write(var0, "png", var1);
      return var1.toByteArray();
   }

   public static void clear() {
      clearPending = true;
   }

   public static long loadedAt() {
      return loadedAt;
   }

   private static void sync() {
      if (clearPending) {
         clearPending = false;
         ready = false;
         col2 = -1;
         col1 = -1;
      }

      Cover.Pending var0 = pending;
      if (var0 != null) {
         pending = null;

         try {
            NativeImage var1 = NativeImage.read(var0.round());
            NativeImage var2 = NativeImage.read(var0.square());
            TextureManager var3 = MinecraftClient.getInstance().getTextureManager();
            if (tex == null) {
               tex = new NativeImageBackedTexture(() -> "neonlyrics_cover", var1);
               var3.registerTexture(ID, tex);
            } else {
               tex.setImage(var1);
               tex.upload();
            }

            if (texSq == null) {
               texSq = new NativeImageBackedTexture(() -> "neonlyrics_cover_sq", var2);
               var3.registerTexture(ID_SQ, texSq);
            } else {
               texSq.setImage(var2);
               texSq.upload();
            }

            col1 = var0.c1();
            col2 = var0.c2();
            ready = true;
            shownAt = System.currentTimeMillis();
         } catch (Throwable var4) {
            ready = false;
         }
      }
   }

   public static boolean draw(DrawContext var0, int var1, int var2, int var3, float var4) {
      if (!NeonLyrics.cfg().albumCover) {
         return false;
      }

      try {
         sync();
         if (!ready) {
            return false;
         }

         float var5 = Anim.outCubic(Anim.since(shownAt, 350L));
         var0.drawTexture(RenderPipelines.GUI_TEXTURED, ID, var1, var2, 0.0F, 0.0F, var3, var3, var3, var3, Gfx.argb(16777215, var4 * var5));
         return var5 > 0.5F;
      } catch (Throwable var6) {
         ready = false;
         return false;
      }
   }

   public static boolean drawSquare(DrawContext var0, int var1, int var2, int var3, float var4) {
      try {
         sync();
         if (!ready) {
            return false;
         }

         float var5 = Anim.outCubic(Anim.since(shownAt, 450L));
         var0.drawTexture(RenderPipelines.GUI_TEXTURED, ID_SQ, var1, var2, 0.0F, 0.0F, var3, var3, var3, var3, Gfx.argb(16777215, var4 * var5));
         return true;
      } catch (Throwable var6) {
         ready = false;
         return false;
      }
   }

   public static Identifier squareTexture() {
      try {
         sync();
         return ready ? ID_SQ : null;
      } catch (Throwable var1) {
         return null;
      }
   }

   public static int[] colors() {
      sync();
      return ready && col1 >= 0 ? new int[]{col1, col2} : null;
   }

   public static float fade() {
      return ready ? Anim.outCubic(Anim.since(shownAt, 600L)) : 0.0F;
   }

   private record Pending(byte[] round, byte[] square, int c1, int c2) {
   }
}
