package dev.neonlyrics.lyrics;

import dev.neonlyrics.NeonLyrics;
import dev.neonlyrics.config.Storage;
import dev.neonlyrics.hud.Island;
import dev.neonlyrics.util.Fx;
import java.util.ArrayList;
import java.util.List;

public final class Favorites {
   private static Favorites.Data data;

   private Favorites() {
   }

   private static Favorites.Data data() {
      if (data == null) {
         data = Storage.load("favorites.json", Favorites.Data.class, Favorites.Data::new);
         if (data.list == null) {
            data.list = new ArrayList<>();
         }
      }

      return data;
   }

   public static List<Favorites.Entry> all() {
      return data().list;
   }

   public static void saveCurrent() {
      NowPlaying var0 = SpotifyClient.now();
      if (var0.hasTrack() && !var0.lines.isEmpty()) {
         long var1 = var0.progressNow(NeonLyrics.cfg().lyricsOffsetMs);
         int var3 = var0.lineIndex(var1);
         if (var3 >= 0 && !var0.lines.get(var3).text().isBlank()) {
            String var4 = var0.lines.get(var3).text();

            for (Favorites.Entry var6 : data().list) {
               if (var6.line.equals(var4) && var6.title.equals(var0.title)) {
                  Island.notify(Fx.t("neonlyrics.fav.already"), true);
                  return;
               }
            }

            Favorites.Entry var7 = new Favorites.Entry();
            var7.line = var4;
            var7.title = var0.title;
            var7.artist = var0.artist;
            var7.time = System.currentTimeMillis();
            data().list.add(0, var7);

            while (data().list.size() > 200) {
               data().list.remove(data().list.size() - 1);
            }

            Storage.save("favorites.json", data());
            Island.notify(Fx.t("neonlyrics.fav.saved"), true);
         } else {
            Island.notify(Fx.t("neonlyrics.fav.noline"), false);
         }
      } else {
         Island.notify(Fx.t("neonlyrics.media.nolyrics"), false);
      }
   }

   public static void remove(Favorites.Entry var0) {
      data().list.remove(var0);
      Storage.save("favorites.json", data());
   }

   private static final class Data {
      List<Favorites.Entry> list = new ArrayList<>();
   }

   public static final class Entry {
      public String line = "";
      public String title = "";
      public String artist = "";
      public long time;
   }
}
