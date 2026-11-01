package dev.neonlyrics.sky;

import dev.neonlyrics.NeonLyrics;
import dev.neonlyrics.config.Storage;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Stream;
import javax.imageio.ImageIO;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

public final class SkyImages {
   private static final Map<String, RenderLayer> TYPES = new HashMap<>();
   private static final List<String> BROKEN = new ArrayList<>();
   private static List<String> files = new ArrayList<>();
   private static long listAt;

   private SkyImages() {
   }

   public static Path folder() {
      Path var0 = Storage.dir().resolve("skies");

      try {
         Files.createDirectories(var0);
      } catch (Exception var2) {
      }

      return var0;
   }

   public static List<String> list() {
      if (System.currentTimeMillis() - listAt < 2000L) {
         return files;
      }

      listAt = System.currentTimeMillis();
      ArrayList<String> var0 = new ArrayList();

      try (Stream<Path> var1 = Files.list(folder())) {
         var1.<String>map(var0x -> var0x.getFileName().toString())
            .filter(var0x -> var0x.toLowerCase(Locale.ROOT).matches(".*\\.(png|jpe?g)$"))
            .sorted()
            .forEach(var0::add);
      } catch (Exception var6) {
      }

      files = var0;
      return var0;
   }

   public static void openFolder() {
      try {
         Util.getOperatingSystem().open(folder().toUri().toString());
      } catch (Throwable var1) {
         NeonLyrics.LOG.warn("Himmel-Ordner laesst sich nicht oeffnen", var1);
      }
   }

   public static RenderLayer type(String var0) {
      if (var0 != null && !var0.isEmpty() && !BROKEN.contains(var0)) {
         RenderLayer var1 = TYPES.get(var0);
         if (var1 != null) {
            return var1;
         }

         try (InputStream var2 = Files.newInputStream(folder().resolve(var0))) {
            BufferedImage var3 = ImageIO.read(var2);
            if (var3 == null) {
               throw new IllegalStateException("kein Bild");
            }

            short var4 = 4096;
            if (var3.getWidth() > var4) {
               int var5 = Math.round((float)(var3.getHeight() * var4) / var3.getWidth());
               BufferedImage var6 = new BufferedImage(var4, var5, 1);
               var6.createGraphics().drawImage(var3.getScaledInstance(var4, var5, 4), 0, 0, null);
               var3 = var6;
            }

            ByteArrayOutputStream var13 = new ByteArrayOutputStream();
            ImageIO.write(var3, "png", var13);
            NativeImage var14 = NativeImage.read(var13.toByteArray());
            Identifier var7 = Identifier.of("neonlyrics", "custom_sky/" + Integer.toHexString(var0.hashCode()));
            MinecraftClient.getInstance().getTextureManager().registerTexture(var7, new NativeImageBackedTexture(() -> "neonlyrics_custom_sky", var14));
            RenderLayer var8 = WorldSky.noFog(var7);
            TYPES.put(var0, var8);
            return var8;
         } catch (Throwable var12) {
            NeonLyrics.LOG.warn("Himmel-Bild {} geht nicht", var0, var12);
            BROKEN.add(var0);
            return null;
         }
      } else {
         return null;
      }
   }
}
