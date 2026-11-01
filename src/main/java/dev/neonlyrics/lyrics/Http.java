package dev.neonlyrics.lyrics;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Map.Entry;

final class Http {
   static final String UA = "NeonLyricsHUD/1.1 (Minecraft Fabric mod)";

   private Http() {
   }

   static Http.Response get(String var0, Map<String, String> var1) throws Exception {
      return send("GET", var0, var1, null, null);
   }

   static Http.Response postForm(String var0, Map<String, String> var1, String var2) throws Exception {
      return send("POST", var0, var1, var2, "application/x-www-form-urlencoded");
   }

   static byte[] getBytes(String var0) throws Exception {
      HttpURLConnection var1 = (HttpURLConnection)URI.create(var0).toURL().openConnection();
      var1.setConnectTimeout(6000);
      var1.setReadTimeout(8000);
      var1.setRequestProperty("User-Agent", "NeonLyricsHUD/1.1 (Minecraft Fabric mod)");

      try (InputStream var2 = var1.getInputStream()) {
         return var2.readAllBytes();
      } finally {
         var1.disconnect();
      }
   }

   static Http.Response empty(String var0, String var1, Map<String, String> var2) throws Exception {
      return send(var0, var1, var2, "", "application/json");
   }

   private static Http.Response send(String var0, String var1, Map<String, String> var2, String var3, String var4) throws Exception {
      HttpURLConnection var5 = (HttpURLConnection)URI.create(var1).toURL().openConnection();
      var5.setRequestMethod(var0);
      var5.setConnectTimeout(6000);
      var5.setReadTimeout(8000);
      var5.setRequestProperty("User-Agent", "NeonLyricsHUD/1.1 (Minecraft Fabric mod)");
      var5.setRequestProperty("Accept", "application/json");

      for (Entry var7 : var2.entrySet()) {
         var5.setRequestProperty((String)var7.getKey(), (String)var7.getValue());
      }

      if (var3 != null) {
         var5.setDoOutput(true);
         var5.setRequestProperty("Content-Type", var4);

         try (OutputStream var16 = var5.getOutputStream()) {
            var16.write(var3.getBytes(StandardCharsets.UTF_8));
         }
      }

      int var17 = var5.getResponseCode();
      InputStream var18 = var17 >= 400 ? var5.getErrorStream() : var5.getInputStream();
      String var8 = "";
      if (var18 != null) {
         try (var18) {
            ByteArrayOutputStream var10 = new ByteArrayOutputStream();
            var18.transferTo(var10);
            var8 = var10.toString(StandardCharsets.UTF_8);
         }
      }

      var5.disconnect();
      return new Http.Response(var17, var8);
   }

   record Response(int code, String body) {
   }
}
