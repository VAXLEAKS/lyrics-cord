package dev.neonlyrics.sky;

import dev.neonlyrics.NeonLyrics;
import dev.neonlyrics.hud.Anim;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.imageio.ImageIO;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;

public final class SkyTextures {
   public static final Identifier SPRITES = Identifier.of("neonlyrics", "sky/sprites");
   public static final int W = 2048;
   public static final int H = 512;
   private static final ExecutorService EXEC = Executors.newSingleThreadExecutor(var0 -> {
      Thread var1 = new Thread(var0, "NeonLyrics-SkyPaint");
      var1.setDaemon(true);
      var1.setPriority(4);
      return var1;
   });
   private static volatile String wantKey = "";
   private static volatile SkyTextures.Pending pending;
   private static String doneKey = "";
   private static int[] base;
   private static int[] fx;
   private static int version = 0;
   private static boolean spritesReady;
   private static boolean spritesFailed;
   private static volatile NativeImage spritePending;
   private static boolean spritesRequested;
   private static long shownAt = 0L;

   private SkyTextures() {
   }

   public static SkyPainter.Spec spec(SkyPreset var0) {
      if (var0 == SkyPreset.CUSTOM) {
         SkyPreset.applyCustom();
      }

      return new SkyPainter.Spec(
         var0.name(), var0.top, var0.horizon, WorldSky.aurora1(var0), WorldSky.aurora2(var0), var0.stars, var0.aurora, var0.moon, var0.weather.name()
      );
   }

   public static String key(SkyPainter.Spec var0) {
      return var0.kind()
         + "_"
         + Integer.toHexString(var0.top())
         + "_"
         + Integer.toHexString(var0.horizon())
         + "_"
         + Integer.toHexString(var0.a1())
         + "_"
         + Integer.toHexString(var0.a2())
         + "_"
         + var0.stars()
         + "_"
         + var0.aurora()
         + "_"
         + Integer.toHexString(var0.moon());
   }

   public static void request(SkyPreset var0) {
      SkyPainter.Spec var1 = spec(var0);
      String var2 = key(var1);
      if (!var2.equals(wantKey)) {
         wantKey = var2;
         EXEC.execute(() -> {
            try {
               if (!var2.equals(wantKey)) {
                  return;
               }

               int[] var2x = SkyPainter.opaqueBase(SkyPainter.smooth(SkyPainter.base(var1, 2048, 512), 2048, 512, false), var1, 2048, 512);
               if (!var2.equals(wantKey)) {
                  return;
               }

               int[] var3 = SkyPainter.fx(var1, 2048, 512);
               if (var3 != null) {
                  var3 = SkyPainter.smooth(var3, 2048, 512, true);
               }

               pending = new SkyTextures.Pending(var2, var2x, var3);
            } catch (Throwable var4) {
               NeonLyrics.LOG.warn("Himmel konnte nicht gemalt werden", var4);
            }
         });
      }
   }

   public static void update() {
      if (!spritesRequested) {
         spritesRequested = true;
         EXEC.execute(() -> {
            try {
               spritePending = image(SkyPainter.sprites(), 256, 64);
            } catch (Throwable var1x) {
               NeonLyrics.LOG.warn("Partikel-Bilder fehlen", var1x);
               spritesFailed = true;
            }
         });
      }

      NativeImage var0 = spritePending;
      if (var0 != null) {
         spritePending = null;

         try {
            MinecraftClient.getInstance().getTextureManager().registerTexture(SPRITES, new NativeImageBackedTexture(() -> "neonlyrics_sprites", var0));
            spritesReady = true;
         } catch (Throwable var2) {
            spritesFailed = true;
         }
      }

      SkyTextures.Pending var1 = pending;
      if (var1 != null) {
         pending = null;
         if (var1.key().equals(wantKey)) {
            base = var1.base();
            fx = var1.fx();
            doneKey = var1.key();
            version++;
            shownAt = System.currentTimeMillis();
         }
      }
   }

   public static boolean ready() {
      return base != null && doneKey.equals(wantKey);
   }

   public static boolean hasFx() {
      return fx != null;
   }

   public static int[] base() {
      return base;
   }

   public static int[] fx() {
      return fx;
   }

   public static int version() {
      return version;
   }

   public static boolean spritesReady() {
      return spritesReady && !spritesFailed;
   }

   public static float fadeIn() {
      return Anim.outCubic(Anim.since(shownAt, 1400L));
   }

   private static NativeImage image(int[] var0, int var1, int var2) throws Exception {
      BufferedImage var3 = new BufferedImage(var1, var2, 2);
      var3.setRGB(0, 0, var1, var2, var0, 0, var1);
      ByteArrayOutputStream var4 = new ByteArrayOutputStream(var1 * var2);
      ImageIO.write(var3, "png", var4);
      return NativeImage.read(var4.toByteArray());
   }

   private record Pending(String key, int[] base, int[] fx) {
   }
}
