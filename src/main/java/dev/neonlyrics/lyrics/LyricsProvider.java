package dev.neonlyrics.lyrics;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class LyricsProvider {
   private static final String BASE = "https://lrclib.net/api";
   private static final Pattern LRC = Pattern.compile("\\[(\\d{1,3}):(\\d{1,2}(?:[.:]\\d{1,3})?)]");
   private static final Map<String, List<NowPlaying.Line>> CACHE = new HashMap<>();
   private static final Map<String, String> NETEASE_HEADERS = Map.of("Referer", "https://music.163.com", "User-Agent", "Mozilla/5.0");
   private static final Pattern CREDIT = Pattern.compile("^\\s*\\S{1,8}\\s*[:：].*");
   private static final Pattern WORD_TAG = Pattern.compile("<(\\d{1,3}):(\\d{1,2}(?:[.:]\\d{1,3})?)>");

   private LyricsProvider() {
   }

   public static synchronized List<NowPlaying.Line> cached(String var0) {
      return CACHE.get(var0);
   }

   public static List<NowPlaying.Line> fetch(String var0, String var1, String var2, String var3, long var4) throws Exception {
      List var6 = cached(var0);
      if (var6 != null) {
         return var6;
      }

      String var7 = null;
      String var8 = "https://lrclib.net/api/get?track_name="
         + enc(var1)
         + "&artist_name="
         + enc(var2)
         + (var3 != null && !var3.isBlank() ? "&album_name=" + enc(var3) : "")
         + (var4 > 0L ? "&duration=" + Math.round(var4 / 1000.0) : "");
      String var9 = null;
      double var10 = 1.0;
      Http.Response var12 = Http.get(var8, Map.of());
      if (var12.code() == 200) {
         JsonObject var13 = JsonParser.parseString(var12.body()).getAsJsonObject();
         var7 = str(var13, "syncedLyrics");
         var9 = str(var13, "plainLyrics");
      }

      String var42 = cleanTitle(var1);
      String var14 = var2 == null ? "" : var2.split("\\s*(,|&| x | feat\\.? | ft\\.? )\\s*")[0].trim();
      boolean var15 = !var42.equalsIgnoreCase(var1.trim());
      double var16 = var4 / 1000.0;
      if (var7 == null) {
         for (String var21 : new String[]{"/search?track_name=" + enc(var42) + "&artist_name=" + enc(var14), "/search?q=" + enc(var42 + " " + var14)}) {
            Http.Response var22 = Http.get("https://lrclib.net/api" + var21, Map.of());
            if (var22.code() == 200) {
               JsonArray var23 = JsonParser.parseString(var22.body()).getAsJsonArray();
               double var24 = Double.MAX_VALUE;

               for (JsonElement var27 : var23) {
                  JsonObject var28 = var27.getAsJsonObject();
                  String var29 = str(var28, "syncedLyrics");
                  if (var9 == null) {
                     var9 = str(var28, "plainLyrics");
                  }

                  if (var29 != null) {
                     double var30 = var28.has("duration") && !var28.get("duration").isJsonNull() ? var28.get("duration").getAsDouble() : 0.0;
                     double var32 = var4 > 0L && !(var30 <= 0.0) ? Math.abs(var30 - var16) : 0.0;
                     double var34 = var4 > 0L && !(var30 <= 0.0) ? var16 / var30 : 1.0;
                     boolean var36 = var32 < 8.0 || var15 && var34 > 0.6 && var34 < 1.7;
                     double var37 = var32 < 8.0 ? var32 : 100.0 + Math.abs(1.0 - var34) * 100.0;
                     if (var36 && var37 < var24) {
                        var24 = var37;
                        var7 = var29;
                        var10 = var32 < 8.0 ? 1.0 : var34;
                     }
                  }
               }

               if (var7 != null) {
                  break;
               }
            }
         }
      }

      if (var7 == null) {
         try {
            double[] var43 = new double[]{1.0};
            var7 = netease(var42, var14, var15, var4, var43);
            if (var7 != null) {
               var10 = var43[0];
            }
         } catch (Exception var41) {
         }
      }

      List<NowPlaying.Line> var44 = var7 == null ? null : parse(var7);
      if (var44 != null && var10 != 1.0) {
         ArrayList<NowPlaying.Line> var45 = new ArrayList(var44.size());

         for (NowPlaying.Line var48 : var44) {
            var45.add(line(Math.round(var48.timeMs() * var10), var48.text()));
         }

         var44 = var45;
      }

      if (var44 == null && var9 != null && var4 > 20000L) {
         var44 = spread(var9, var4);
      }

      if (var44 != null && var44.isEmpty()) {
         var44 = null;
      }

      synchronized (LyricsProvider.class) {
         CACHE.put(var0, var44 == null ? Collections.emptyList() : var44);
         if (CACHE.size() > 60) {
            CACHE.clear();
         }

         return var44;
      }
   }

   static String netease(String var0, String var1, boolean var2, long var3, double[] var5) throws Exception {
      Http.Response var6 = Http.get("https://music.163.com/api/search/get?type=1&limit=10&s=" + enc(var0 + " " + var1), NETEASE_HEADERS);
      if (var6.code() != 200) {
         return null;
      }

      JsonObject var7 = JsonParser.parseString(var6.body()).getAsJsonObject().getAsJsonObject("result");
      if (var7 != null && var7.has("songs")) {
         double var8 = var3 / 1000.0;
         double var10 = Double.MAX_VALUE;
         long var12 = -1L;
         double var14 = 1.0;
         String var16 = norm(var0);
         String var17 = norm(var1);

         for (JsonElement var19 : var7.getAsJsonArray("songs")) {
            JsonObject var20 = var19.getAsJsonObject();
            String var21 = norm(cleanTitle(var20.get("name").getAsString()));
            if (var21.equals(var16) || var21.contains(var16) || var16.contains(var21)) {
               boolean var22 = var17.isEmpty();

               for (JsonElement var24 : var20.getAsJsonArray("artists")) {
                  if (norm(var24.getAsJsonObject().get("name").getAsString()).contains(var17)
                     || var17.contains(norm(var24.getAsJsonObject().get("name").getAsString()))) {
                     var22 = true;
                  }
               }

               if (var22) {
                  double var38 = var20.has("duration") ? var20.get("duration").getAsLong() / 1000.0 : 0.0;
                  double var25 = var3 > 0L && !(var38 <= 0.0) ? Math.abs(var38 - var8) : 0.0;
                  double var27 = var3 > 0L && !(var38 <= 0.0) ? var8 / var38 : 1.0;
                  boolean var29 = var25 < 8.0 || var2 && var27 > 0.6 && var27 < 1.7;
                  double var30 = var25 < 8.0 ? var25 : 100.0 + Math.abs(1.0 - var27) * 100.0;
                  if (var29 && var30 < var10) {
                     var10 = var30;
                     var12 = var20.get("id").getAsLong();
                     var14 = var25 < 8.0 ? 1.0 : var27;
                  }
               }
            }
         }

         if (var12 < 0L) {
            return null;
         }

         Http.Response var32 = Http.get("https://music.163.com/api/song/lyric?lv=1&kv=1&tv=-1&id=" + var12, NETEASE_HEADERS);
         if (var32.code() != 200) {
            return null;
         }

         JsonObject var33 = JsonParser.parseString(var32.body()).getAsJsonObject();
         if (var33.has("lrc") && var33.getAsJsonObject("lrc").has("lyric")) {
            String var34 = var33.getAsJsonObject("lrc").get("lyric").getAsString();
            StringBuilder var35 = new StringBuilder();

            for (String var41 : var34.split("\\r?\\n")) {
               String var26 = var41.replaceAll("^(\\[[^]]*])+", "");
               if (!CREDIT.matcher(var26).matches()) {
                  var35.append(var41).append('\n');
               }
            }

            String var37 = var35.toString();
            if (!LRC.matcher(var37).find()) {
               return null;
            }

            var5[0] = var14;
            return var37;
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   private static String norm(String var0) {
      return var0 == null ? "" : var0.toLowerCase(Locale.ROOT).replaceAll("[^\\p{L}\\p{N}]+", "");
   }

   static String cleanTitle(String var0) {
      String var1 = var0 == null ? "" : var0;
      var1 = var1.replaceAll("\\s*[-–—]\\s.*$", "").replaceAll("\\s*[(\\[].*?[)\\]]", "");
      var1 = var1.replaceAll("(?i)\\b(sped up|speed up|slowed( down)?|reverb|nightcore|remix|remaster(ed)?|radio edit|extended|version)\\b", "");
      var1 = var1.replaceAll("\\s+", " ").trim();
      return var1.isEmpty() ? (var0 == null ? "" : var0.trim()) : var1;
   }

   static List<NowPlaying.Line> spread(String var0, long var1) {
      ArrayList<String> var3 = new ArrayList();

      for (String var7 : var0.split("\\r?\\n")) {
         if (!var7.isBlank()) {
            var3.add(var7.trim());
         }
      }

      if (var3.isEmpty()) {
         return null;
      }

      long var12 = Math.min(12000L, var1 / 12L);
      long var13 = var1 - Math.min(10000L, var1 / 15L);
      double var8 = (double)(var13 - var12) / var3.size();
      ArrayList<NowPlaying.Line> var10 = new ArrayList();

      for (int var11 = 0; var11 < var3.size(); var11++) {
         var10.add(line(var12 + Math.round(var11 * var8), (String)var3.get(var11)));
      }

      var10.add(line(var13, ""));
      return var10;
   }

   public static List<NowPlaying.Line> parse(String var0) {
      ArrayList<NowPlaying.Line> var1 = new ArrayList();

      for (String var5 : var0.split("\\r?\\n")) {
         Matcher var6 = LRC.matcher(var5);
         ArrayList<Long> var7 = new ArrayList();

         int var8;
         for (var8 = 0; var6.find() && var6.start() == var8; var8 = var6.end()) {
            long var9 = Long.parseLong(var6.group(1));
            double var11 = Double.parseDouble(var6.group(2).replace(':', '.'));
            var7.add(var9 * 60000L + Math.round(var11 * 1000.0));
         }

         if (!var7.isEmpty()) {
            String var13 = var5.substring(var8).trim();

            for (long var14 : var7) {
               var1.add(var13.indexOf(60) >= 0 ? enhancedLine(var14, var13) : line(var14, var13));
            }
         }
      }

      var1.sort((var0x, var1x) -> Long.compare(var0x.timeMs(), var1x.timeMs()));
      return var1;
   }

   static NowPlaying.Line enhancedLine(long var0, String var2) {
      Matcher var3 = WORD_TAG.matcher(var2);
      ArrayList<String> var4 = new ArrayList();
      ArrayList<Long> var5 = new ArrayList();
      int var6 = -1;
      long var7 = var0;

      while (var3.find()) {
         if (var6 >= 0) {
            addWords(var2.substring(var6, var3.start()), var7, var4, var5);
         }

         var7 = Long.parseLong(var3.group(1)) * 60000L + Math.round(Double.parseDouble(var3.group(2).replace(':', '.')) * 1000.0);
         var6 = var3.end();
      }

      if (var6 >= 0) {
         addWords(var2.substring(var6), var7, var4, var5);
      }

      String var9 = WORD_TAG.matcher(var2).replaceAll("").replaceAll("\\s+", " ").trim();
      if (var4.isEmpty()) {
         return line(var0, var9);
      }

      long[] var10 = new long[var5.size()];

      for (int var11 = 0; var11 < var10.length; var11++) {
         var10[var11] = Math.max(var0, (Long)var5.get(var11));
      }

      return new NowPlaying.Line(var0, var9, var4.toArray(new String[0]), var10);
   }

   private static void addWords(String var0, long var1, List<String> var3, List<Long> var4) {
      for (String var8 : var0.trim().split("\\s+")) {
         if (!var8.isBlank()) {
            var3.add(var8);
            var4.add(var1);
         }
      }
   }

   public static NowPlaying.Line line(long var0, String var2) {
      String[] var3 = var2.isBlank() ? new String[0] : var2.trim().split("\\s+");
      return new NowPlaying.Line(var0, var2, var3);
   }

   private static String str(JsonObject var0, String var1) {
      if (var0.has(var1) && !var0.get(var1).isJsonNull()) {
         String var2 = var0.get(var1).getAsString();
         return var2.isBlank() ? null : var2;
      } else {
         return null;
      }
   }

   static String enc(String var0) {
      return URLEncoder.encode(var0 == null ? "" : var0, StandardCharsets.UTF_8);
   }

   public static List<NowPlaying.Line> demo() {
      String[] var0 = new String[]{
         "Neon lights across a frozen sky",
         "every block a little brighter tonight",
         "auction's open, put your money down",
         "highest bid takes the purple crown",
         "snow is falling on the spawner farm",
         "sixty four skellys safe from harm",
         "pay me once and I will pay you twice",
         "if you win the gamble, that's the price",
         "time is ticking, only ten to go",
         "sold to you in the falling snow"
      };
      ArrayList var1 = new ArrayList();
      long var2 = 1500L;

      for (String var7 : var0) {
         var1.add(line(var2, var7));
         var2 += 3400L;
      }

      var1.add(line(var2, ""));
      return var1;
   }
}
