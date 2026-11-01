package dev.neonlyrics.hud;

import dev.neonlyrics.NeonLyrics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;

public final class Shapes {
   public static final int MAX_R = 512;
   private static final Map<Integer, Identifier> CIRCLES = new HashMap<>();
   private static boolean failed = false;

   private Shapes() {
   }

   public static boolean usable() {
      return !failed;
   }

   public static void fail() {
      failed = true;
   }

   public static Identifier circle(int var0) {
      if (!failed && var0 >= 1) {
         if (var0 > 96) {
            var0 = Math.min(512, (var0 + 4) / 8 * 8);
         }

         Identifier var1 = CIRCLES.get(var0);
         if (var1 != null) {
            return var1;
         }

         try {
            int var2 = var0 * 2;
            BufferedImage var3 = new BufferedImage(var2, var2, 2);

            for (int var4 = 0; var4 < var2; var4++) {
               for (int var5 = 0; var5 < var2; var5++) {
                  int var6 = 0;

                  for (int var7 = 0; var7 < 4; var7++) {
                     for (int var8 = 0; var8 < 4; var8++) {
                        float var9 = var5 + (var8 + 0.5F) / 4.0F - var0;
                        float var10 = var4 + (var7 + 0.5F) / 4.0F - var0;
                        if (var9 * var9 + var10 * var10 <= (float)var0 * var0) {
                           var6++;
                        }
                     }
                  }

                  int var16 = Math.round(var6 / 16.0F * 255.0F);
                  var3.setRGB(var5, var4, var16 << 24 | 16777215);
               }
            }

            ByteArrayOutputStream var13 = new ByteArrayOutputStream();
            ImageIO.write(var3, "png", var13);
            NativeImage var14 = NativeImage.read(var13.toByteArray());
            var1 = Identifier.of("neonlyrics", "shape/c" + var0);
            NativeImageBackedTexture var15 = new NativeImageBackedTexture(() -> "neonlyrics_circle", var14);
            MinecraftClient.getInstance().getTextureManager().registerTexture(var1, var15);
            CIRCLES.put(var0, var1);
            return var1;
         } catch (Throwable var11) {
            NeonLyrics.LOG.warn("Formen als Textur gehen nicht, nutze einfache Variante", var11);
            failed = true;
            return null;
         }
      } else {
         return null;
      }
   }
}
