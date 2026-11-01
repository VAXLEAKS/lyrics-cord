package dev.neonlyrics.lyrics;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.neonlyrics.NeonLyrics;
import dev.neonlyrics.config.Storage;
import dev.neonlyrics.hud.Cover;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.Locale;

public final class LocalMedia {
   private static volatile LocalMedia.Status status = LocalMedia.Status.STARTING;
   private static volatile String app = "";
   private static volatile String lastError = "";
   private static volatile Process proc;
   private static volatile long restartAt = 0L;
   private static int failures = 0;
   private static Path log;
   private static String ownId = "";
   private static long ownStart = 0L;
   private static long pausedSince = 0L;
   private static int logLines = 0;
   private static long lastCover = 0L;
   public static final boolean WINDOWS = System.getProperty("os.name", "").toLowerCase(Locale.ROOT).contains("win");
   public static volatile boolean exactTime = true;
   private static final String SCRIPT = "$ErrorActionPreference = 'Stop'\n[Console]::OutputEncoding = New-Object System.Text.UTF8Encoding $false\nfunction Out-Line($l) { try { [Console]::Out.WriteLine($l); [Console]::Out.Flush() } catch { exit } }\n$mgr = $null\ntry {\n  Add-Type -AssemblyName System.Runtime.WindowsRuntime\n  $asTask = ([System.WindowsRuntimeSystemExtensions].GetMethods() | Where-Object { $_.Name -eq 'AsTask' -and $_.GetParameters().Count -eq 1 -and $_.GetParameters()[0].ParameterType.Name -eq 'IAsyncOperation`1' })[0]\n  function Await($op, $type) { $t = $asTask.MakeGenericMethod($type).Invoke($null, @($op)); $null = $t.Wait(-1); $t.Result }\n  $null = [Windows.Media.Control.GlobalSystemMediaTransportControlsSessionManager,Windows.Media.Control,ContentType=WindowsRuntime]\n  $null = [Windows.Media.Control.GlobalSystemMediaTransportControlsSessionMediaProperties,Windows.Media.Control,ContentType=WindowsRuntime]\n  $mgr = Await ([Windows.Media.Control.GlobalSystemMediaTransportControlsSessionManager]::RequestAsync()) ([Windows.Media.Control.GlobalSystemMediaTransportControlsSessionManager])\n  [Console]::Error.WriteLine('mediensteuerung ok')\n} catch { [Console]::Error.WriteLine('mediensteuerung fehlt: ' + $_) }\nfunction FromSession($s) {\n  $p = Await ($s.TryGetMediaPropertiesAsync()) ([Windows.Media.Control.GlobalSystemMediaTransportControlsSessionMediaProperties])\n  if ($null -eq $p -or [string]::IsNullOrEmpty($p.Title)) { return $null }\n  $tl = $s.GetTimelineProperties()\n  $playing = ($s.GetPlaybackInfo().PlaybackStatus.ToString() -eq 'Playing')\n  $pos = $tl.Position.TotalMilliseconds\n  $dur = $tl.EndTime.TotalMilliseconds\n  if ($dur -le 0) { $pos = -1 }\n  elseif ($playing) {\n    $d = ([DateTimeOffset]::Now - $tl.LastUpdatedTime).TotalMilliseconds\n    if ($d -gt 0 -and $d -lt $dur) { $pos += $d }\n  }\n  # Albumcover einmal pro Song als Datei ablegen\n  $key = [string]$p.Title + '|' + [string]$p.Artist\n  if ($key -ne $script:coverKey) {\n    $script:coverKey = $key\n    try {\n      if ($null -ne $p.Thumbnail) {\n        $rs = Await ($p.Thumbnail.OpenReadAsync()) ([Windows.Storage.Streams.IRandomAccessStreamWithContentType])\n        $ns = [System.IO.WindowsRuntimeStreamExtensions]::AsStreamForRead($rs)\n        $fs = [System.IO.File]::Create($coverFile)\n        $ns.CopyTo($fs); $fs.Close(); $ns.Close()\n        $script:coverId++\n      } else { $script:coverId = -[Math]::Abs($script:coverId) - 1 }\n    } catch { $script:coverId = -[Math]::Abs($script:coverId) - 1 }\n  }\n  return [ordered]@{ app = [string]$s.SourceAppUserModelId; title = [string]$p.Title; artist = [string]$p.Artist; album = [string]$p.AlbumTitle; pos = [long]$pos; dur = [long]$dur; playing = $playing; cover = $script:coverId }\n}\nfunction FromWindow {\n  # Ersatz: Fenstertitel der Spotify-App (\"Kuenstler - Titel\"). Pausiert steht dort nur \"Spotify\".\n  $p = Get-Process -Name Spotify -ErrorAction SilentlyContinue | Where-Object { $_.MainWindowTitle -like '* - *' } | Select-Object -First 1\n  if ($null -eq $p) { return $null }\n  $t = $p.MainWindowTitle; $i = $t.IndexOf(' - ')\n  return [ordered]@{ app = 'Spotify.exe'; title = $t.Substring($i + 3); artist = $t.Substring(0, $i); album = ''; pos = -1; dur = 0; playing = $true }\n}\n$cmdFile = Join-Path $PSScriptRoot 'spotify-cmd.txt'\n$coverFile = Join-Path $PSScriptRoot 'cover.img'\n$script:coverKey = ''\n$script:coverId = 0\ntry { $null = [Windows.Storage.Streams.IRandomAccessStreamWithContentType,Windows.Storage.Streams,ContentType=WindowsRuntime] } catch {}\nfunction Do-Command($cmd) {\n  $done = $false\n  try {\n    if ($null -ne $mgr) {\n      $s = $null\n      foreach ($x in $mgr.GetSessions()) { if ($x.SourceAppUserModelId -like '*Spotify*') { $s = $x; break } }\n      if ($null -eq $s) { $s = $mgr.GetCurrentSession() }\n      if ($null -ne $s) {\n        if ($cmd -eq 'next') { $done = Await ($s.TrySkipNextAsync()) ([bool]) }\n        elseif ($cmd -eq 'previous') { $done = Await ($s.TrySkipPreviousAsync()) ([bool]) }\n        elseif ($cmd -eq 'playpause') { $done = Await ($s.TryTogglePlayPauseAsync()) ([bool]) }\n      }\n    }\n  } catch { [Console]::Error.WriteLine('befehl fehlgeschlagen: ' + $_) }\n  if (-not $done) {\n    # Ersatz: Medientaste druecken (176 = naechster, 177 = vorheriger, 179 = Play/Pause)\n    $key = 0\n    if ($cmd -eq 'next') { $key = 176 } elseif ($cmd -eq 'previous') { $key = 177 } elseif ($cmd -eq 'playpause') { $key = 179 }\n    if ($key -ne 0) { try { (New-Object -ComObject WScript.Shell).SendKeys([string][char]$key) } catch {} }\n  }\n}\nwhile ($true) {\n  $o = $null\n  try {\n    if ($null -ne $mgr) {\n      foreach ($x in $mgr.GetSessions()) { if ($x.SourceAppUserModelId -like '*Spotify*') { $o = FromSession $x; break } }\n    }\n    if ($null -eq $o) { $o = FromWindow }\n    if ($null -eq $o -and $null -ne $mgr) { $c = $mgr.GetCurrentSession(); if ($null -ne $c) { $o = FromSession $c } }\n  } catch { [Console]::Error.WriteLine('fehler: ' + $_) }\n  if ($null -eq $o) { Out-Line '{}' } else { Out-Line ($o | ConvertTo-Json -Compress) }\n  for ($w = 0; $w -lt 5; $w++) {\n    if (Test-Path -LiteralPath $cmdFile) {\n      $cmd = ''\n      try { $cmd = (Get-Content -LiteralPath $cmdFile -Raw).Trim(); Remove-Item -LiteralPath $cmdFile -Force } catch {}\n      Do-Command $cmd\n      break\n    }\n    Start-Sleep -Milliseconds 100\n  }\n}\n";

   private LocalMedia() {
   }

   private static synchronized void logLine(String var0) {
      Path var1 = log;
      if (var1 != null && logLines++ <= 200) {
         try {
            Files.writeString(var1, var0 + "\n", StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
         } catch (Exception var3) {
         }
      }
   }

   public static LocalMedia.Status status() {
      return WINDOWS ? status : LocalMedia.Status.UNSUPPORTED;
   }

   public static String app() {
      return app;
   }

   public static String lastError() {
      return lastError;
   }

   static void ensureRunning() {
      if (WINDOWS) {
         Process var0 = proc;
         if (var0 == null || !var0.isAlive()) {
            if (System.currentTimeMillis() >= restartAt) {
               start();
            }
         }
      }
   }

   public static void command(String var0) {
      try {
         Path var1 = Storage.dir().resolve("spotify-cmd.txt");
         Files.writeString(var1, var0, StandardCharsets.UTF_8);
      } catch (Exception var2) {
         lastError = String.valueOf(var2.getMessage());
      }
   }

   public static void stop() {
      Process var0 = proc;
      proc = null;
      if (var0 != null) {
         var0.destroy();
      }

      status = LocalMedia.Status.STARTING;
   }

   public static void restart() {
      failures = 0;
      restartAt = 0L;
      stop();
   }

   private static void start() {
      try {
         Path var0 = Storage.dir().resolve("spotify-helper.ps1");
         Files.createDirectories(var0.getParent());
         Files.writeString(
            var0,
            "\ufeff$ErrorActionPreference = 'Stop'\n[Console]::OutputEncoding = New-Object System.Text.UTF8Encoding $false\nfunction Out-Line($l) { try { [Console]::Out.WriteLine($l); [Console]::Out.Flush() } catch { exit } }\n$mgr = $null\ntry {\n  Add-Type -AssemblyName System.Runtime.WindowsRuntime\n  $asTask = ([System.WindowsRuntimeSystemExtensions].GetMethods() | Where-Object { $_.Name -eq 'AsTask' -and $_.GetParameters().Count -eq 1 -and $_.GetParameters()[0].ParameterType.Name -eq 'IAsyncOperation`1' })[0]\n  function Await($op, $type) { $t = $asTask.MakeGenericMethod($type).Invoke($null, @($op)); $null = $t.Wait(-1); $t.Result }\n  $null = [Windows.Media.Control.GlobalSystemMediaTransportControlsSessionManager,Windows.Media.Control,ContentType=WindowsRuntime]\n  $null = [Windows.Media.Control.GlobalSystemMediaTransportControlsSessionMediaProperties,Windows.Media.Control,ContentType=WindowsRuntime]\n  $mgr = Await ([Windows.Media.Control.GlobalSystemMediaTransportControlsSessionManager]::RequestAsync()) ([Windows.Media.Control.GlobalSystemMediaTransportControlsSessionManager])\n  [Console]::Error.WriteLine('mediensteuerung ok')\n} catch { [Console]::Error.WriteLine('mediensteuerung fehlt: ' + $_) }\nfunction FromSession($s) {\n  $p = Await ($s.TryGetMediaPropertiesAsync()) ([Windows.Media.Control.GlobalSystemMediaTransportControlsSessionMediaProperties])\n  if ($null -eq $p -or [string]::IsNullOrEmpty($p.Title)) { return $null }\n  $tl = $s.GetTimelineProperties()\n  $playing = ($s.GetPlaybackInfo().PlaybackStatus.ToString() -eq 'Playing')\n  $pos = $tl.Position.TotalMilliseconds\n  $dur = $tl.EndTime.TotalMilliseconds\n  if ($dur -le 0) { $pos = -1 }\n  elseif ($playing) {\n    $d = ([DateTimeOffset]::Now - $tl.LastUpdatedTime).TotalMilliseconds\n    if ($d -gt 0 -and $d -lt $dur) { $pos += $d }\n  }\n  # Albumcover einmal pro Song als Datei ablegen\n  $key = [string]$p.Title + '|' + [string]$p.Artist\n  if ($key -ne $script:coverKey) {\n    $script:coverKey = $key\n    try {\n      if ($null -ne $p.Thumbnail) {\n        $rs = Await ($p.Thumbnail.OpenReadAsync()) ([Windows.Storage.Streams.IRandomAccessStreamWithContentType])\n        $ns = [System.IO.WindowsRuntimeStreamExtensions]::AsStreamForRead($rs)\n        $fs = [System.IO.File]::Create($coverFile)\n        $ns.CopyTo($fs); $fs.Close(); $ns.Close()\n        $script:coverId++\n      } else { $script:coverId = -[Math]::Abs($script:coverId) - 1 }\n    } catch { $script:coverId = -[Math]::Abs($script:coverId) - 1 }\n  }\n  return [ordered]@{ app = [string]$s.SourceAppUserModelId; title = [string]$p.Title; artist = [string]$p.Artist; album = [string]$p.AlbumTitle; pos = [long]$pos; dur = [long]$dur; playing = $playing; cover = $script:coverId }\n}\nfunction FromWindow {\n  # Ersatz: Fenstertitel der Spotify-App (\"Kuenstler - Titel\"). Pausiert steht dort nur \"Spotify\".\n  $p = Get-Process -Name Spotify -ErrorAction SilentlyContinue | Where-Object { $_.MainWindowTitle -like '* - *' } | Select-Object -First 1\n  if ($null -eq $p) { return $null }\n  $t = $p.MainWindowTitle; $i = $t.IndexOf(' - ')\n  return [ordered]@{ app = 'Spotify.exe'; title = $t.Substring($i + 3); artist = $t.Substring(0, $i); album = ''; pos = -1; dur = 0; playing = $true }\n}\n$cmdFile = Join-Path $PSScriptRoot 'spotify-cmd.txt'\n$coverFile = Join-Path $PSScriptRoot 'cover.img'\n$script:coverKey = ''\n$script:coverId = 0\ntry { $null = [Windows.Storage.Streams.IRandomAccessStreamWithContentType,Windows.Storage.Streams,ContentType=WindowsRuntime] } catch {}\nfunction Do-Command($cmd) {\n  $done = $false\n  try {\n    if ($null -ne $mgr) {\n      $s = $null\n      foreach ($x in $mgr.GetSessions()) { if ($x.SourceAppUserModelId -like '*Spotify*') { $s = $x; break } }\n      if ($null -eq $s) { $s = $mgr.GetCurrentSession() }\n      if ($null -ne $s) {\n        if ($cmd -eq 'next') { $done = Await ($s.TrySkipNextAsync()) ([bool]) }\n        elseif ($cmd -eq 'previous') { $done = Await ($s.TrySkipPreviousAsync()) ([bool]) }\n        elseif ($cmd -eq 'playpause') { $done = Await ($s.TryTogglePlayPauseAsync()) ([bool]) }\n      }\n    }\n  } catch { [Console]::Error.WriteLine('befehl fehlgeschlagen: ' + $_) }\n  if (-not $done) {\n    # Ersatz: Medientaste druecken (176 = naechster, 177 = vorheriger, 179 = Play/Pause)\n    $key = 0\n    if ($cmd -eq 'next') { $key = 176 } elseif ($cmd -eq 'previous') { $key = 177 } elseif ($cmd -eq 'playpause') { $key = 179 }\n    if ($key -ne 0) { try { (New-Object -ComObject WScript.Shell).SendKeys([string][char]$key) } catch {} }\n  }\n}\nwhile ($true) {\n  $o = $null\n  try {\n    if ($null -ne $mgr) {\n      foreach ($x in $mgr.GetSessions()) { if ($x.SourceAppUserModelId -like '*Spotify*') { $o = FromSession $x; break } }\n    }\n    if ($null -eq $o) { $o = FromWindow }\n    if ($null -eq $o -and $null -ne $mgr) { $c = $mgr.GetCurrentSession(); if ($null -ne $c) { $o = FromSession $c } }\n  } catch { [Console]::Error.WriteLine('fehler: ' + $_) }\n  if ($null -eq $o) { Out-Line '{}' } else { Out-Line ($o | ConvertTo-Json -Compress) }\n  for ($w = 0; $w -lt 5; $w++) {\n    if (Test-Path -LiteralPath $cmdFile) {\n      $cmd = ''\n      try { $cmd = (Get-Content -LiteralPath $cmdFile -Raw).Trim(); Remove-Item -LiteralPath $cmdFile -Force } catch {}\n      Do-Command $cmd\n      break\n    }\n    Start-Sleep -Milliseconds 100\n  }\n}\n",
            StandardCharsets.UTF_8
         );
         Files.deleteIfExists(Storage.dir().resolve("spotify-cmd.txt"));
         log = Storage.dir().resolve("spotify-helper.log");
         logLines = 0;
         Files.writeString(log, "Start " + LocalDateTime.now() + "\n", StandardCharsets.UTF_8);
         String var1 = System.getenv("SystemRoot");
         File var2 = new File(var1 == null ? "C:\\Windows" : var1, "System32\\WindowsPowerShell\\v1.0\\powershell.exe");
         ProcessBuilder var3 = new ProcessBuilder(
            var2.isFile() ? var2.getPath() : "powershell.exe",
            "-NoProfile",
            "-NonInteractive",
            "-NoLogo",
            "-ExecutionPolicy",
            "Bypass",
            "-WindowStyle",
            "Hidden",
            "-File",
            var0.toString()
         );
         Process var4 = var3.start();
         var4.getOutputStream().close();
         proc = var4;
         status = LocalMedia.Status.STARTING;
         long var5 = System.currentTimeMillis();
         Thread var7 = new Thread(() -> {
            String var2x;
            try (BufferedReader var1x = new BufferedReader(new InputStreamReader(var4.getErrorStream(), StandardCharsets.UTF_8))) {
               while ((var2x = var1x.readLine()) != null) {
                  if (!var2x.isBlank()) {
                     lastError = var2x.trim();
                     logLine(var2x.trim());
                  }
               }
            } catch (Exception var6) {
            }
         }, "NeonLyrics-MediaErr");
         var7.setDaemon(true);
         var7.start();
         Thread var8 = new Thread(() -> {
            String var4x;
            try (BufferedReader var3x = new BufferedReader(new InputStreamReader(var4.getInputStream(), StandardCharsets.UTF_8))) {
               while ((var4x = var3x.readLine()) != null && proc == var4) {
                  handle(var4x.trim());
               }
            } catch (Exception var8x) {
               lastError = String.valueOf(var8x.getMessage());
            }

            if (proc == var4) {
               proc = null;
               if (System.currentTimeMillis() - var5 < 10000L) {
                  failures++;
               } else {
                  failures = 0;
               }

               status = failures >= 3 ? LocalMedia.Status.FAILED : LocalMedia.Status.STARTING;
               restartAt = System.currentTimeMillis() + Math.min(60000L, 3000L * (1L << Math.min(4, failures)));
               logLine("Helfer beendet (Code " + (var4.isAlive() ? "?" : String.valueOf(var4.exitValue())) + ")");
               if (failures >= 3) {
                  NeonLyrics.LOG.warn("Windows-Mediensteuerung nicht verfuegbar: {}", lastError);
               }
            }
         }, "NeonLyrics-Media");
         var8.setDaemon(true);
         var8.start();
      } catch (Exception var9) {
         lastError = String.valueOf(var9.getMessage());
         logLine("Start fehlgeschlagen: " + lastError);
         status = LocalMedia.Status.FAILED;
         restartAt = System.currentTimeMillis() + 60000L;
      }
   }

   private static void handle(String var0) {
      if (var0.startsWith("{")) {
         JsonObject var1;
         try {
            var1 = JsonParser.parseString(var0).getAsJsonObject();
         } catch (Exception var15) {
            return;
         }

         if (!var1.has("title")) {
            if (pausedSince == 0L) {
               pausedSince = System.currentTimeMillis();
            }

            status = LocalMedia.Status.NO_PLAYER;
            app = "";
            SpotifyClient.publishNothing();
         } else {
            String var2 = str(var1, "title");
            String var3 = str(var1, "artist");
            if (var2.isBlank()) {
               status = LocalMedia.Status.NO_PLAYER;
               SpotifyClient.publishNothing();
            } else {
               failures = 0;
               app = prettyApp(str(var1, "app"));
               status = LocalMedia.Status.PLAYING;
               long var4 = var1.has("pos") ? var1.get("pos").getAsLong() : 0L;
               long var6 = var1.has("dur") ? var1.get("dur").getAsLong() : 0L;
               boolean var8 = var1.has("playing") && var1.get("playing").getAsBoolean();
               String var9 = var2 + "|" + var3;
               long var10 = System.currentTimeMillis();
               exactTime = var4 >= 0L;
               if (var4 < 0L) {
                  if (!var9.equals(ownId)) {
                     ownId = var9;
                     ownStart = var10;
                  } else if (pausedSince > 0L) {
                     ownStart = ownStart + (var10 - pausedSince);
                  }

                  pausedSince = 0L;
                  var4 = var10 - ownStart;
               }

               if (var6 > 0L) {
                  var4 = Math.max(0L, Math.min(var4, var6));
               }

               long var12 = var1.has("cover") ? var1.get("cover").getAsLong() : 0L;
               if (var12 != lastCover) {
                  lastCover = var12;
                  if (var12 > 0L) {
                     Thread var14 = new Thread(() -> {
                        try {
                           Cover.load(Files.readAllBytes(Storage.dir().resolve("cover.img")));
                        } catch (Exception var1x) {
                        }
                     }, "NeonLyrics-Cover");
                     var14.setDaemon(true);
                     var14.start();
                  } else {
                     Cover.clear();
                  }
               }

               SpotifyClient.publish("local:" + var2 + "|" + var3, var2, var3, var3, str(var1, "album"), var6, var4, var8);
            }
         }
      }
   }

   private static String prettyApp(String var0) {
      String var1 = var0.toLowerCase(Locale.ROOT);
      if (var1.contains("spotify")) {
         return "Spotify";
      } else if (var1.contains("chrome")) {
         return "Chrome";
      } else if (var1.contains("msedge") || var1.contains("edge")) {
         return "Edge";
      } else if (var1.contains("firefox")) {
         return "Firefox";
      } else if (var1.contains("opera")) {
         return "Opera";
      } else if (!var1.contains("zunemusic") && !var1.contains("media")) {
         int var2 = var0.indexOf(33);
         String var3 = var2 > 0 ? var0.substring(0, var2) : var0;
         return var3.replace(".exe", "");
      } else {
         return "Media Player";
      }
   }

   private static String str(JsonObject var0, String var1) {
      return var0.has(var1) && !var0.get(var1).isJsonNull() ? var0.get(var1).getAsString() : "";
   }

   public enum Status {
      UNSUPPORTED,
      STARTING,
      NO_PLAYER,
      PLAYING,
      FAILED;
   }
}
