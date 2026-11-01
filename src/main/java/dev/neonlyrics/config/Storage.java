package dev.neonlyrics.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.neonlyrics.NeonLyrics;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.function.Supplier;
import net.fabricmc.loader.api.FabricLoader;

public final class Storage {
   private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

   private Storage() {
   }

   public static Path dir() {
      return FabricLoader.getInstance().getConfigDir().resolve("neonlyrics");
   }

   public static void migrateFromAuction() {
      try {
         Path var0 = FabricLoader.getInstance().getConfigDir().resolve("neonauction");
         if (!Files.isDirectory(var0) || Files.exists(dir().resolve("config.json"))) {
            return;
         }

         Files.createDirectories(dir());
         Path var1 = Files.exists(var0.resolve("config-v13-backup.json")) ? var0.resolve("config-v13-backup.json") : var0.resolve("config.json");
         if (Files.exists(var1)) {
            Files.copy(var1, dir().resolve("config.json"));
         }

         for (String var5 : new String[]{"spotify.json", "favorites.json"}) {
            Path var6 = var0.resolve(var5);
            if (Files.exists(var6) && !Files.exists(dir().resolve(var5))) {
               Files.copy(var6, dir().resolve(var5));
            }
         }

         NeonLyrics.LOG.info("Einstellungen aus config/neonauction uebernommen");
      } catch (Exception var7) {
         NeonLyrics.LOG.warn("Konnte alte Einstellungen nicht uebernehmen", var7);
      }
   }

   public static <T> T load(String var0, Type var1, Supplier<T> var2) {
      Path var3 = dir().resolve(var0);
      if (!Files.exists(var3)) {
         return (T)var2.get();
      }

      try (BufferedReader var4 = Files.newBufferedReader(var3, StandardCharsets.UTF_8)) {
         Object var5 = GSON.fromJson(var4, var1);
         return (T)(var5 != null ? var5 : var2.get());
      } catch (Exception var9) {
         NeonLyrics.LOG.warn("Konnte {} nicht laden, nutze Standardwerte", var0, var9);
         return (T)var2.get();
      }
   }

   public static void save(String var0, Object var1) {
      try {
         Files.createDirectories(dir());
         Path var2 = dir().resolve(var0);
         Path var3 = dir().resolve(var0 + ".tmp");

         try (BufferedWriter var4 = Files.newBufferedWriter(var3, StandardCharsets.UTF_8)) {
            GSON.toJson(var1, var4);
         }

         Files.move(var3, var2, StandardCopyOption.REPLACE_EXISTING);
      } catch (Exception var9) {
         NeonLyrics.LOG.warn("Konnte {} nicht speichern", var0, var9);
      }
   }
}
