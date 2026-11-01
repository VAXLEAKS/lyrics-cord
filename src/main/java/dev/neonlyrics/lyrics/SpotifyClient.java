package dev.neonlyrics.lyrics;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.neonlyrics.NeonLyrics;
import dev.neonlyrics.config.Config;
import dev.neonlyrics.config.Storage;
import dev.neonlyrics.hud.Cover;
import dev.neonlyrics.hud.Island;
import dev.neonlyrics.sky.SkyPreset;
import dev.neonlyrics.util.Fx;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Util;

public final class SpotifyClient {
   public static final int PORT = 8789;
   public static final String REDIRECT = "http://127.0.0.1:8789/callback";
   public static final String DASHBOARD = "https://developer.spotify.com/dashboard";
   private static final String SCOPES = "user-read-currently-playing user-read-playback-state user-modify-playback-state";
   private static SpotifyClient.Tokens tokens = new SpotifyClient.Tokens();
   private static volatile SpotifyClient.Status status = SpotifyClient.Status.DISCONNECTED;
   private static volatile String lastError = "";
   private static volatile NowPlaying now = NowPlaying.EMPTY;
   private static final ArrayDeque<SpotifyClient.Played> HISTORY = new ArrayDeque<>();
   private static volatile long backoffUntil = 0L;
   private static String coverKey = "";
   private static ScheduledExecutorService exec;
   private static volatile ServerSocket loginServer;
   private static long demoStart = 0L;
   private static List<NowPlaying.Line> demoLines;

   private SpotifyClient() {
   }

   private static void remember(String var0, String var1) {
      if (var0 != null && !var0.isBlank()) {
         SpotifyClient.Played var2 = HISTORY.peekFirst();
         if (var2 == null || !var2.title().equals(var0) || !var2.artist().equals(String.valueOf(var1))) {
            HISTORY.addFirst(new SpotifyClient.Played(var0, String.valueOf(var1), System.currentTimeMillis()));

            while (HISTORY.size() > 12) {
               HISTORY.removeLast();
            }
         }
      }
   }

   private static void coverFallback(String var0, String var1) {
      long var2 = System.currentTimeMillis();
      if (exec != null) {
         exec.schedule(() -> {
            try {
               if (Cover.loadedAt() >= var2) {
                  return;
               }

               NowPlaying var4 = now;
               if (var4.title == null || !var4.title.equals(var0)) {
                  return;
               }

               String var5 = var0.replaceAll("\\s*[\\(\\[](feat|ft|with)[^\\)\\]]*[\\)\\]]", "").trim();
               String var6 = URLEncoder.encode(var1 + " " + var5, StandardCharsets.UTF_8);
               Http.Response var7 = Http.get("https://itunes.apple.com/search?media=music&entity=song&limit=1&term=" + var6, Map.of());
               if (var7.code() != 200) {
                  return;
               }

               JsonObject var8 = JsonParser.parseString(var7.body()).getAsJsonObject();
               JsonArray var9 = var8.getAsJsonArray("results");
               if (var9 == null || var9.isEmpty()) {
                  return;
               }

               JsonObject var10 = var9.get(0).getAsJsonObject();
               if (!var10.has("artworkUrl100")) {
                  return;
               }

               String var11 = var10.get("artworkUrl100").getAsString().replace("100x100bb", "600x600bb");
               if (Cover.loadedAt() >= var2) {
                  return;
               }

               Cover.load(Http.getBytes(var11));
            } catch (Throwable var12) {
            }
         }, 2500L, TimeUnit.MILLISECONDS);
      }
   }

   public static List<SpotifyClient.Played> history() {
      return new ArrayList<>(HISTORY);
   }

   public static void init() {
      tokens = Storage.load("spotify.json", SpotifyClient.Tokens.class, SpotifyClient.Tokens::new);
      status = tokens.refresh != null && !tokens.refresh.isBlank() ? SpotifyClient.Status.CONNECTED : SpotifyClient.Status.DISCONNECTED;
      exec = Executors.newSingleThreadScheduledExecutor(var0 -> {
         Thread var1 = new Thread(var0, "NeonLyrics-Spotify");
         var1.setDaemon(true);
         return var1;
      });
      exec.scheduleWithFixedDelay(SpotifyClient::poll, 2L, 2L, TimeUnit.SECONDS);
      Runtime.getRuntime().addShutdownHook(new Thread(LocalMedia::stop, "NeonLyrics-MediaStop"));
   }

   public static SpotifyClient.Status status() {
      return NeonLyrics.cfg().spotifyClientId.isBlank() && status != SpotifyClient.Status.CONNECTED ? SpotifyClient.Status.NOT_CONFIGURED : status;
   }

   public static String lastError() {
      return lastError;
   }

   public static NowPlaying now() {
      return NeonLyrics.cfg().demoLyrics ? demo() : now;
   }

   private static NowPlaying demo() {
      if (demoLines == null) {
         demoLines = LyricsProvider.demo();
      }

      long var0 = demoLines.get(demoLines.size() - 1).timeMs() + 1500L;
      if (demoStart == 0L) {
         demoStart = System.currentTimeMillis();
      }

      long var2 = (System.currentTimeMillis() - demoStart) % var0;
      return new NowPlaying("demo", "Neon Nights (Demo)", "Lyrics Cord", var0, true, var2, System.currentTimeMillis(), demoLines, NowPlaying.LyricsState.SYNCED);
   }

   public static void restartDemo() {
      demoStart = System.currentTimeMillis();
   }

   public static void login() {
      String var0 = NeonLyrics.cfg().spotifyClientId.trim();
      if (var0.isEmpty()) {
         Fx.error(Fx.t("neonlyrics.spotify.needid"));
      } else {
         closeLoginServer();

         try {
            byte[] var1 = new byte[48];
            new SecureRandom().nextBytes(var1);
            String var2 = Base64.getUrlEncoder().withoutPadding().encodeToString(var1);
            String var3 = Base64.getUrlEncoder()
               .withoutPadding()
               .encodeToString(MessageDigest.getInstance("SHA-256").digest(var2.getBytes(StandardCharsets.US_ASCII)));
            byte[] var4 = new byte[12];
            new SecureRandom().nextBytes(var4);
            String var5 = Base64.getUrlEncoder().withoutPadding().encodeToString(var4);
            ServerSocket var6 = new ServerSocket(8789, 4, InetAddress.getByName("127.0.0.1"));
            var6.setSoTimeout(180000);
            loginServer = var6;
            status = SpotifyClient.Status.WAITING_LOGIN;
            String var7 = "https://accounts.spotify.com/authorize?response_type=code&client_id="
               + LyricsProvider.enc(var0)
               + "&scope="
               + LyricsProvider.enc("user-read-currently-playing user-read-playback-state user-modify-playback-state")
               + "&redirect_uri="
               + LyricsProvider.enc("http://127.0.0.1:8789/callback")
               + "&code_challenge_method=S256&code_challenge="
               + var3
               + "&state="
               + var5;
            Util.getOperatingSystem().open(var7);
            Thread var8 = new Thread(() -> waitForCallback(var6, var0, var2, var5), "NeonLyrics-SpotifyLogin");
            var8.setDaemon(true);
            var8.start();
         } catch (Exception var9) {
            fail(var9);
         }
      }
   }

   private static void waitForCallback(ServerSocket var0, String var1, String var2, String var3) {
      try {
         try (var0) {
            while (true) {
               try (Socket var5 = var0.accept()) {
                  var5.setSoTimeout(5000);
                  BufferedReader var6 = new BufferedReader(new InputStreamReader(var5.getInputStream(), StandardCharsets.UTF_8));
                  String var7 = var6.readLine();
                  if (var7 != null && var7.startsWith("GET /callback")) {
                     String var8 = var7.split(" ")[1];
                     Map var9 = query(var8.contains("?") ? var8.substring(var8.indexOf(63) + 1) : "");
                     if (var9.containsKey("error") || !var3.equals(var9.get("state")) || !var9.containsKey("code")) {
                        respond(var5, 400, page(false));
                        throw new IllegalStateException("Spotify: " + var9.getOrDefault("error", "ungueltige Antwort"));
                     }

                     respond(var5, 200, page(true));
                     exchange(var1, var2, (String)var9.get("code"));
                     return;
                  }

                  respond(var5, 404, "Not found");
               }
            }
         } catch (SocketTimeoutException var22) {
            status = SpotifyClient.Status.DISCONNECTED;
         } catch (Exception var23) {
            if (status == SpotifyClient.Status.WAITING_LOGIN) {
               fail(var23);
               return;
            }
         }
      } finally {
         loginServer = null;
      }
   }

   private static void exchange(String var0, String var1, String var2) throws Exception {
      String var3 = "grant_type=authorization_code&code="
         + LyricsProvider.enc(var2)
         + "&redirect_uri="
         + LyricsProvider.enc("http://127.0.0.1:8789/callback")
         + "&client_id="
         + LyricsProvider.enc(var0)
         + "&code_verifier="
         + LyricsProvider.enc(var1);
      Http.Response var4 = Http.postForm("https://accounts.spotify.com/api/token", Map.of(), var3);
      if (var4.code() != 200) {
         throw new IllegalStateException("Token " + var4.code() + ": " + var4.body());
      }

      readTokens(var4.body());
      status = SpotifyClient.Status.CONNECTED;
      lastError = "";
      MinecraftClient.getInstance().execute(() -> {
         Island.notify(Fx.t("neonlyrics.spotify.connected"), true);
         Fx.sound(Fx.Sfx.WIN);
      });
   }

   private static void readTokens(String var0) {
      JsonObject var1 = JsonParser.parseString(var0).getAsJsonObject();
      tokens.access = var1.get("access_token").getAsString();
      if (var1.has("refresh_token") && !var1.get("refresh_token").isJsonNull()) {
         tokens.refresh = var1.get("refresh_token").getAsString();
      }

      tokens.expiresAt = System.currentTimeMillis() + (var1.has("expires_in") ? var1.get("expires_in").getAsLong() : 3600L) * 1000L - 30000L;
      Storage.save("spotify.json", tokens);
   }

   private static boolean refresh() {
      String var0 = NeonLyrics.cfg().spotifyClientId.trim();
      if (tokens.refresh != null && !tokens.refresh.isBlank() && !var0.isEmpty()) {
         try {
            String var1 = "grant_type=refresh_token&refresh_token=" + LyricsProvider.enc(tokens.refresh) + "&client_id=" + LyricsProvider.enc(var0);
            Http.Response var2 = Http.postForm("https://accounts.spotify.com/api/token", Map.of(), var1);
            if (var2.code() != 200) {
               if (var2.code() == 400) {
                  logout();
               }

               lastError = "Refresh " + var2.code();
               return false;
            } else {
               readTokens(var2.body());
               return true;
            }
         } catch (Exception var3) {
            lastError = String.valueOf(var3.getMessage());
            return false;
         }
      } else {
         return false;
      }
   }

   public static void logout() {
      tokens = new SpotifyClient.Tokens();
      Storage.save("spotify.json", tokens);
      now = NowPlaying.EMPTY;
      status = SpotifyClient.Status.DISCONNECTED;
   }

   private static void closeLoginServer() {
      ServerSocket var0 = loginServer;
      if (var0 != null) {
         try {
            var0.close();
         } catch (Exception var2) {
         }
      }
   }

   private static void poll() {
      try {
         if (NeonLyrics.cfg().demoLyrics) {
            return;
         }

         if (autoMode()) {
            LocalMedia.ensureRunning();
            return;
         }

         LocalMedia.stop();
         if (status != SpotifyClient.Status.CONNECTED) {
            return;
         }

         if (System.currentTimeMillis() < backoffUntil) {
            return;
         }

         if (MinecraftClient.getInstance().world == null) {
            return;
         }

         if (System.currentTimeMillis() > tokens.expiresAt && !refresh()) {
            return;
         }

         Http.Response var0 = Http.get("https://api.spotify.com/v1/me/player/currently-playing", Map.of("Authorization", "Bearer " + tokens.access));
         if (var0.code() == 401) {
            if (refresh()) {
               poll();
            }

            return;
         }

         if (var0.code() == 429) {
            backoffUntil = System.currentTimeMillis() + 15000L;
            return;
         }

         if (var0.code() == 204 || var0.body().isBlank()) {
            now = NowPlaying.EMPTY;
            return;
         }

         if (var0.code() != 200) {
            lastError = "Spotify " + var0.code();
            return;
         }

         JsonObject var1 = JsonParser.parseString(var0.body()).getAsJsonObject();
         if (!var1.has("item") || var1.get("item").isJsonNull()) {
            now = NowPlaying.EMPTY;
            return;
         }

         JsonObject var2 = var1.getAsJsonObject("item");
         String var3 = var2.has("id") && !var2.get("id").isJsonNull() ? var2.get("id").getAsString() : var2.get("name").getAsString();
         String var4 = var2.get("name").getAsString();
         StringBuilder var5 = new StringBuilder();
         if (var2.has("artists")) {
            JsonArray var6 = var2.getAsJsonArray("artists");

            for (int var7 = 0; var7 < var6.size(); var7++) {
               if (var7 > 0) {
                  var5.append(", ");
               }

               var5.append(var6.get(var7).getAsJsonObject().get("name").getAsString());
            }
         }

         String var16 = var5.toString().split(", ")[0];
         String var17 = var2.has("album") ? var2.getAsJsonObject("album").get("name").getAsString() : "";
         long var8 = var2.has("duration_ms") ? var2.get("duration_ms").getAsLong() : 0L;
         long var10 = var1.has("progress_ms") && !var1.get("progress_ms").isJsonNull() ? var1.get("progress_ms").getAsLong() : 0L;
         boolean var12 = var1.has("is_playing") && var1.get("is_playing").getAsBoolean();
         publish(var3, var4, var5.toString(), var16, var17, var8, var10, var12);
         if (!var3.equals(coverKey)) {
            coverKey = var3;
            String var13 = null;
            if (var2.has("album") && var2.getAsJsonObject("album").has("images")) {
               JsonArray var14 = var2.getAsJsonObject("album").getAsJsonArray("images");
               if (!var14.isEmpty()) {
                  var13 = var14.get(var14.size() - 1).getAsJsonObject().get("url").getAsString();
               }
            }

            if (var13 == null) {
               Cover.clear();
            } else {
               String var18 = var13;
               exec.execute(() -> {
                  try {
                     Cover.load(Http.getBytes(var18));
                  } catch (Exception var2x) {
                  }
               });
            }
         }
      } catch (Exception var15) {
         lastError = String.valueOf(var15.getMessage());
      }
   }

   static void publish(String var0, String var1, String var2, String var3, String var4, long var5, long var7, boolean var9) {
      NowPlaying var10 = now;
      boolean var11 = !var0.equals(var10.trackId);
      if (!var11 && var9 && var10.playing) {
         long var12 = var10.progressNow(0);
         long var14 = var7 - var12;
         if (Math.abs(var14) < 1500L) {
            var7 = var12 + var14 / 4L;
         }
      }

      List var16 = var11 ? LyricsProvider.cached(var0) : var10.lines;
      NowPlaying.LyricsState var13 = var11
         ? (var16 == null ? NowPlaying.LyricsState.LOADING : (var16.isEmpty() ? NowPlaying.LyricsState.NOT_FOUND : NowPlaying.LyricsState.SYNCED))
         : var10.lyricsState;
      now = new NowPlaying(var0, var1, var2, var5, var9, var7, System.currentTimeMillis(), var16, var13);
      if (var11) {
         coverFallback(var1, var3);
         MinecraftClient.getInstance().execute(() -> {
            Island.notify("♪ " + var1, true);
            remember(var1, var2);
            Config var2x = NeonLyrics.cfg();
            if (var2x.skyShuffle && var2x.sky >= 0) {
               SkyPreset.shuffle();
            }
         });
         if (var13 == NowPlaying.LyricsState.LOADING) {
            exec.execute(() -> {
               try {
                  List var6 = LyricsProvider.fetch(var0, var1, var3, var4, var5);
                  NowPlaying var9x = now;
                  if (var0.equals(var9x.trackId)) {
                     now = var9x.withLyrics(var6, var6 == null ? NowPlaying.LyricsState.NOT_FOUND : NowPlaying.LyricsState.SYNCED);
                  }
               } catch (Exception var8) {
                  NowPlaying var7x = now;
                  if (var0.equals(var7x.trackId)) {
                     now = var7x.withLyrics(null, NowPlaying.LyricsState.ERROR);
                  }
               }
            });
         }
      }
   }

   static void publishNothing() {
      now = NowPlaying.EMPTY;
   }

   public static void syncHere() {
      NowPlaying var0 = now();
      Config var1 = NeonLyrics.cfg();
      if (var0.hasTrack() && !var0.lines.isEmpty()) {
         long var2 = var0.progressNow(0);
         long var4 = var2 + var1.lyricsOffsetMs;
         long var6 = Long.MAX_VALUE;
         long var8 = 0L;

         for (NowPlaying.Line var11 : var0.lines) {
            if (!var11.text().isBlank()) {
               long var12 = Math.abs(var11.timeMs() - var4);
               if (var12 < var6) {
                  var6 = var12;
                  var8 = var11.timeMs();
               }
            }
         }

         var1.lyricsOffsetMs = (int)Math.max(-5000L, Math.min(5000L, var8 - var2));
         NeonLyrics.saveConfig();
         Island.notify(Fx.t("neonlyrics.media.synced", (var1.lyricsOffsetMs > 0 ? "+" : "") + var1.lyricsOffsetMs), true);
      } else {
         Island.notify(Fx.t("neonlyrics.media.nolyrics"), false);
      }
   }

   public static void control(SpotifyClient.Cmd var0) {
      if (NeonLyrics.cfg().demoLyrics) {
         restartDemo();
      } else {
         String var1 = switch (var0) {
            case NEXT -> Fx.t("neonlyrics.media.next");
            case PREVIOUS -> Fx.t("neonlyrics.media.prev");
            case PLAYPAUSE -> now.playing ? Fx.t("neonlyrics.media.pause") : Fx.t("neonlyrics.media.play");
         };
         Island.notify(var1, true);
         if (autoMode()) {
            LocalMedia.command(var0.name().toLowerCase(Locale.ROOT));
         } else {
            exec.execute(() -> {
               try {
                  if (status != SpotifyClient.Status.CONNECTED) {
                     return;
                  }

                  if (System.currentTimeMillis() > tokens.expiresAt && !refresh()) {
                     return;
                  }

                  Map var1x = Map.of("Authorization", "Bearer " + tokens.access);

                  Http.Response var2 = switch (var0) {
                     case NEXT -> Http.empty("POST", "https://api.spotify.com/v1/me/player/next", var1x);
                     case PREVIOUS -> Http.empty("POST", "https://api.spotify.com/v1/me/player/previous", var1x);
                     case PLAYPAUSE -> Http.empty("PUT", "https://api.spotify.com/v1/me/player/" + (now.playing ? "pause" : "play"), var1x);
                  };
                  if (var2.code() == 401 || var2.code() == 403) {
                     MinecraftClient.getInstance().execute(() -> Island.notify(Fx.t("neonlyrics.media.noperm"), false));
                  }

                  backoffUntil = 0L;
                  Thread.sleep(400L);
                  poll();
               } catch (Exception var3) {
                  lastError = String.valueOf(var3.getMessage());
               }
            });
         }
      }
   }

   public static boolean autoMode() {
      return NeonLyrics.cfg().spotifyMode == 0;
   }

   public static void setMode(int var0) {
      if (NeonLyrics.cfg().spotifyMode != var0) {
         NeonLyrics.cfg().spotifyMode = var0;
         NeonLyrics.saveConfig();
         now = NowPlaying.EMPTY;
         if (var0 != 0) {
            LocalMedia.stop();
         }
      }
   }

   private static void fail(Exception var0) {
      status = SpotifyClient.Status.ERROR;
      lastError = String.valueOf(var0.getMessage());
      NeonLyrics.LOG.warn("Spotify-Login fehlgeschlagen", var0);
      MinecraftClient.getInstance().execute(() -> Fx.error(Fx.t("neonlyrics.spotify.failed", lastError)));
   }

   private static Map<String, String> query(String var0) {
      HashMap var1 = new HashMap();

      for (String var5 : var0.split("&")) {
         int var6 = var5.indexOf(61);
         if (var6 > 0) {
            var1.put(URLDecoder.decode(var5.substring(0, var6), StandardCharsets.UTF_8), URLDecoder.decode(var5.substring(var6 + 1), StandardCharsets.UTF_8));
         }
      }

      return var1;
   }

   private static void respond(Socket var0, int var1, String var2) throws Exception {
      byte[] var3 = var2.getBytes(StandardCharsets.UTF_8);
      OutputStream var4 = var0.getOutputStream();
      var4.write(
         ("HTTP/1.1 " + var1 + " OK\r\nContent-Type: text/html; charset=utf-8\r\nContent-Length: " + var3.length + "\r\nConnection: close\r\n\r\n")
            .getBytes(StandardCharsets.US_ASCII)
      );
      var4.write(var3);
      var4.flush();
   }

   private static String page(boolean var0) {
      return "<!doctype html><meta charset=utf-8><title>Lyrics Cord</title><body style=\"margin:0;height:100vh;display:grid;place-items:center;background:#0f0c17;color:#f3efff;font-family:system-ui,sans-serif\"><div style=\"text-align:center\"><div style=\"font-size:20px;font-weight:600\">"
         + (var0 ? "Spotify ist verbunden" : "Verbindung fehlgeschlagen")
         + "</div><div style=\"margin-top:8px;color:#9d93b8\">"
         + (var0 ? "Du kannst dieses Fenster schliessen und zurueck ins Spiel." : "Versuche es im Spiel noch einmal.")
         + "</div></div></body>";
   }

   public enum Cmd {
      NEXT,
      PREVIOUS,
      PLAYPAUSE;
   }

   public record Played(String title, String artist, long at) {
   }

   public enum Status {
      NOT_CONFIGURED,
      DISCONNECTED,
      WAITING_LOGIN,
      CONNECTED,
      ERROR;
   }

   private static final class Tokens {
      String access = "";
      String refresh = "";
      long expiresAt;
   }
}
