package dev.neonlyrics.hud;

import dev.neonlyrics.NeonLyrics;
import dev.neonlyrics.config.Storage;
import dev.neonlyrics.sky.LiveSky;
import dev.neonlyrics.sky.SkyPainter;
import dev.neonlyrics.sky.SkyPreset;
import dev.neonlyrics.sky.SkyTextures;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.imageio.ImageIO;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;

public final class SkyThumbs {
   public static final int TW = 288;
   public static final int TH = 162;
   private static final ExecutorService EXEC = Executors.newSingleThreadExecutor(var0 -> {
      Thread var1 = new Thread(var0, "NeonLyrics-Thumbs");
      var1.setDaemon(true);
      var1.setPriority(1);
      return var1;
   });
   private static final Map<String, String> SHOWN = new HashMap<>();
   private static final Set<String> QUEUED = ConcurrentHashMap.newKeySet();
   private static final ConcurrentLinkedQueue<SkyThumbs.Done> DONE = new ConcurrentLinkedQueue<>();
   private static final int CACHE_VER = 12;
   private static long lastUpload = 0L;

   private SkyThumbs() {
   }

   private static float[] look(String var0) {
      return switch (var0) {
         case "SYNTHWAVE" -> new float[]{180.0F, 11.0F};
         case "BLACK_HOLE" -> new float[]{165.0F, 22.0F};
         case "MIDNIGHT", "BLOOD_MOON" -> new float[]{200.0F, 22.0F};
         case "GOLDEN_HOUR" -> new float[]{200.0F, 12.0F};
         case "COSMOS" -> new float[]{60.0F, 28.0F};
         case "DEEP_SEA" -> new float[]{40.0F, 32.0F};
         case "SAKURA" -> new float[]{60.0F, 12.0F};
         case "COTTON_CANDY" -> new float[]{120.0F, 26.0F};
         case "NEBULA" -> new float[]{40.0F, 26.0F};
         case "NEON_CITY" -> new float[]{60.0F, 16.0F};
         case "FROST" -> new float[]{210.0F, 24.0F};
         case "PULSAR" -> new float[]{30.0F, 34.0F};
         case "PRISM", "MATRIX", "INFERNO", "AQUARELL" -> new float[]{60.0F, 24.0F};
         default -> new float[]{150.0F, 20.0F};
      };
   }

   public static boolean draw(DrawContext var0, SkyPreset var1, int var2, int var3, int var4, int var5, float var6) {
      if (var1.painted()) {
         var0.drawTexture(RenderPipelines.GUI_TEXTURED, png(var1), var2, var3, 0.0F, 0.0F, var4, var5, var4, var5, Gfx.argb(16777215, var6));
         return true;
      }

      upload();
      SkyPainter.Spec var7 = SkyTextures.spec(var1);
      String var8 = SkyTextures.key(var7);
      String var9 = SHOWN.get(var1.name());
      if (!var8.equals(var9)) {
         request(var1, var7, var8);
      }

      if (var9 == null) {
         return false;
      }

      Identifier var10 = id(var1);
      var0.drawTexture(RenderPipelines.GUI_TEXTURED, var10, var2, var3, 0.0F, 0.0F, var4, var5, var4, var5, Gfx.argb(16777215, var6));
      return true;
   }

   public static Identifier texture(SkyPreset var0) {
      if (var0.painted()) {
         return png(var0);
      }

      upload();
      SkyPainter.Spec var1 = SkyTextures.spec(var0);
      String var2 = SkyTextures.key(var1);
      String var3 = SHOWN.get(var0.name());
      if (!var2.equals(var3)) {
         request(var0, var1, var2);
      }

      return var3 == null ? null : id(var0);
   }

   private static Path cacheFile(String var0) {
      try {
         return Storage.dir().resolve("thumbs").resolve("v12_" + Integer.toHexString(var0.hashCode()) + "_288.png");
      } catch (Throwable var2) {
         return null;
      }
   }

   private static Identifier png(SkyPreset var0) {
      return Identifier.of("neonlyrics", "textures/gui/skythumbs/" + var0.name().toLowerCase(Locale.ROOT) + ".png");
   }

   private static Identifier id(SkyPreset var0) {
      return Identifier.of("neonlyrics", "thumb/" + var0.name().toLowerCase(Locale.ROOT));
   }

   private static void request(SkyPreset var0, SkyPainter.Spec var1, String var2) {
      if (QUEUED.add(var2)) {
         EXEC.execute(() -> {
            try {
               Path var3 = cacheFile(var2);
               if (var3 != null && Files.isRegularFile(var3)) {
                  DONE.add(new SkyThumbs.Done(var0.name(), var2, NativeImage.read(Files.readAllBytes(var3))));
                  return;
               }

               float[] var4 = look(var1.kind());
               int[] var5 = new LiveSky().view(var0, var1.a1(), var1.a2(), 12.0, 288, 162, var4[0], -var4[1], 78.0F);
               BufferedImage var6 = new BufferedImage(288, 162, 2);
               var6.setRGB(0, 0, 288, 162, var5, 0, 288);
               ByteArrayOutputStream var7 = new ByteArrayOutputStream();
               ImageIO.write(var6, "png", var7);
               byte[] var8 = var7.toByteArray();
               if (var3 != null) {
                  try {
                     Files.createDirectories(var3.getParent());
                     Files.write(var3, var8);
                  } catch (Exception var10) {
                  }
               }

               DONE.add(new SkyThumbs.Done(var0.name(), var2, NativeImage.read(var8)));
            } catch (Throwable var11) {
               NeonLyrics.LOG.debug("Himmel-Vorschau fehlgeschlagen", var11);
            }
         });
      }
   }

   private static void upload() {
      long var0 = System.nanoTime() / 5000000L;
      if (var0 != lastUpload) {
         SkyThumbs.Done var2 = DONE.poll();
         if (var2 != null) {
            lastUpload = var0;

            try {
               SkyPreset var3 = SkyPreset.valueOf(var2.name());
               MinecraftClient.getInstance()
                  .getTextureManager()
                  .registerTexture(id(var3), new NativeImageBackedTexture(() -> "neonlyrics_thumb_" + var2.name(), var2.img()));
               SHOWN.put(var2.name(), var2.key());
            } catch (Throwable var4) {
               var2.img().close();
            }

            QUEUED.remove(var2.key());
         }
      }
   }

   private record Done(String name, String key, NativeImage img) {
   }
}
