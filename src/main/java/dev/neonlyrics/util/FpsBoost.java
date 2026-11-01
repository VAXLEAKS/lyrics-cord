package dev.neonlyrics.util;

import dev.neonlyrics.NeonLyrics;
import dev.neonlyrics.config.Config;
import java.io.Serializable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.SimpleOption;

public final class FpsBoost {
   private FpsBoost() {
   }

   public static void apply(boolean var0) {
      MinecraftClient var1 = MinecraftClient.getInstance();
      Config var2 = NeonLyrics.cfg();

      try {
         if (var0) {
            var2.boostBefore = save(var1.options.getEntityShadows()) + ";" + save(var1.options.getParticles()) + ";" + save(var1.options.getBiomeBlendRadius());
            set(var1.options.getEntityShadows(), "false");
            set(var1.options.getParticles(), "MINIMAL");
            set(var1.options.getBiomeBlendRadius(), "0");
            var2.quality = 0;
         } else if (var2.boostBefore != null && !var2.boostBefore.isEmpty()) {
            String[] var3 = var2.boostBefore.split(";", -1);
            if (var3.length >= 3) {
               set(var1.options.getEntityShadows(), var3[0]);
               set(var1.options.getParticles(), var3[1]);
               set(var1.options.getBiomeBlendRadius(), var3[2]);
            }

            var2.boostBefore = "";
            if (var2.quality == 0) {
               var2.quality = 1;
            }
         }

         var1.options.write();
      } catch (Exception var4) {
         NeonLyrics.LOG.warn("FPS-Boost konnte nicht alles umstellen", var4);
      }

      var2.fpsBoost = var0;
      NeonLyrics.saveConfig();
   }

   private static String save(SimpleOption<?> var0) {
      Object var1 = var0.getValue();
      return var1 instanceof Enum var2 ? var2.name() : String.valueOf(var1);
   }

   private static void set(SimpleOption var0, String var1) {
      Object var2 = var0.getValue();
      Serializable var3;
      if (var2 instanceof Boolean) {
         var3 = Boolean.parseBoolean(var1);
      } else if (var2 instanceof Integer) {
         var3 = Integer.parseInt(var1);
      } else {
         if (!(var2 instanceof Enum var4)) {
            return;
         }

         var3 = Enum.valueOf(var4.getDeclaringClass(), var1);
      }

      var0.setValue(var3);
   }
}
