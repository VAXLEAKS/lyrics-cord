package dev.neonlyrics.lyrics;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.neonlyrics.NeonLyrics;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class Translator {
   private static final Map<String, String> CACHE = Collections.synchronizedMap(new LinkedHashMap<String, String>(256, 0.75F, true) {
      @Override
      protected boolean removeEldestEntry(Entry<String, String> var1) {
         return this.size() > 400;
      }
   });
   private static final Set<String> PENDING = Collections.synchronizedSet(new HashSet<>());
   private static final ExecutorService EXEC = Executors.newSingleThreadExecutor(var0 -> {
      Thread var1 = new Thread(var0, "NeonLyrics-Translate");
      var1.setDaemon(true);
      return var1;
   });
   private static volatile long blockedUntil = 0L;

   private Translator() {
   }

   public static String get(String var0) {
      if (NeonLyrics.cfg().translate && var0 != null && !var0.isBlank()) {
         String var1 = NeonLyrics.cfg().translateTo + "|" + var0;
         String var2 = CACHE.get(var1);
         if (var2 != null) {
            return var2.isEmpty() ? null : var2;
         }

         request(var0);
         return null;
      } else {
         return null;
      }
   }

   public static void prefetch(String var0) {
      if (NeonLyrics.cfg().translate && var0 != null && !var0.isBlank()) {
         get(var0);
      }
   }

   private static void request(String var0) {
      String var1 = NeonLyrics.cfg().translateTo;
      String var2 = var1 + "|" + var0;
      if (System.currentTimeMillis() >= blockedUntil && PENDING.add(var2)) {
         EXEC.execute(() -> {
            try {
               String var3 = "https://api.mymemory.translated.net/get?q=" + LyricsProvider.enc(var0) + "&langpair=en%7C" + LyricsProvider.enc(var1);
               Http.Response var4 = Http.get(var3, Map.of());
               if (var4.code() != 429) {
                  String var5 = "";
                  if (var4.code() == 200) {
                     JsonObject var6 = JsonParser.parseString(var4.body()).getAsJsonObject();
                     if (var6.has("responseData") && var6.get("responseData").isJsonObject()) {
                        JsonObject var7 = var6.getAsJsonObject("responseData");
                        if (var7.has("translatedText") && !var7.get("translatedText").isJsonNull()) {
                           var5 = var7.get("translatedText").getAsString().trim();
                        }
                     }
                  }

                  if (var5.equalsIgnoreCase(var0.trim()) || var5.toUpperCase().contains("MYMEMORY WARNING")) {
                     var5 = "";
                  }

                  CACHE.put(var2, var5);
                  Thread.sleep(350L);
                  return;
               }

               blockedUntil = System.currentTimeMillis() + 60000L;
            } catch (Exception var11) {
               blockedUntil = System.currentTimeMillis() + 15000L;
               return;
            } finally {
               PENDING.remove(var2);
            }
         });
      }
   }
}
