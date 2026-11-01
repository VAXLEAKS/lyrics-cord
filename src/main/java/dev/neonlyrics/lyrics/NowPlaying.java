package dev.neonlyrics.lyrics;

import java.util.Collections;
import java.util.List;

public final class NowPlaying {
   public final String trackId;
   public final String title;
   public final String artist;
   public final long durationMs;
   public final boolean playing;
   public final long progressMs;
   public final long fetchedAt;
   public final List<NowPlaying.Line> lines;
   public final NowPlaying.LyricsState lyricsState;
   public static final NowPlaying EMPTY = new NowPlaying(null, "", "", 0L, false, 0L, 0L, Collections.emptyList(), NowPlaying.LyricsState.NONE);

   public NowPlaying(
      String var1, String var2, String var3, long var4, boolean var6, long var7, long var9, List<NowPlaying.Line> var11, NowPlaying.LyricsState var12
   ) {
      this.trackId = var1;
      this.title = var2 == null ? "" : var2;
      this.artist = var3 == null ? "" : var3;
      this.durationMs = var4;
      this.playing = var6;
      this.progressMs = var7;
      this.fetchedAt = var9;
      this.lines = var11 == null ? Collections.emptyList() : var11;
      this.lyricsState = var12;
   }

   public boolean hasTrack() {
      return this.trackId != null;
   }

   public NowPlaying withLyrics(List<NowPlaying.Line> var1, NowPlaying.LyricsState var2) {
      return new NowPlaying(this.trackId, this.title, this.artist, this.durationMs, this.playing, this.progressMs, this.fetchedAt, var1, var2);
   }

   public long progressNow(int var1) {
      long var2 = this.progressMs + (this.playing ? System.currentTimeMillis() - this.fetchedAt : 0L) + var1;
      if (this.durationMs > 0L) {
         var2 = Math.min(var2, this.durationMs);
      }

      return Math.max(0L, var2);
   }

   public int lineIndex(long var1) {
      int var3 = -1;
      int var4 = 0;

      while (var4 < this.lines.size() && this.lines.get(var4).timeMs() <= var1) {
         var3 = var4++;
      }

      return var3;
   }

   public long lineEnd(int var1) {
      if (var1 >= 0 && var1 < this.lines.size()) {
         long var2 = this.lines.get(var1).timeMs();
         long var4 = var1 + 1 < this.lines.size() ? this.lines.get(var1 + 1).timeMs() : var2 + 5000L;
         return Math.min(var4, var2 + 7000L);
      } else {
         return 0L;
      }
   }

   static int syllables(String var0) {
      int var1 = 0;
      boolean var2 = false;

      for (int var3 = 0; var3 < var0.length(); var3++) {
         boolean var4 = "aeiouyäöüAEIOUYÄÖÜ".indexOf(var0.charAt(var3)) >= 0;
         if (var4 && !var2) {
            var1++;
         }

         var2 = var4;
      }

      return Math.max(1, var1);
   }

   public float wordPosition(int var1, long var2) {
      LyricTiming var4 = LyricTiming.of(this.lines);
      return var4 == null ? -1.0F : var4.wordPosition(var1, var2, this.lineEnd(var1));
   }

   public double beatPos(long var1) {
      LyricTiming var3 = LyricTiming.of(this.lines);
      return var3 != null && var3.beatMs > 0.0 && var3.confidence > 0.2F ? (var1 - var3.beatPhase) / var3.beatMs : var1 / 500.0;
   }

   public float beatPulse(long var1) {
      LyricTiming var3 = LyricTiming.of(this.lines);
      return var3 != null && this.playing ? var3.beatPulse(var1) : 0.0F;
   }

   public record Line(long timeMs, String text, String[] words, long[] wordTimes) {
      public Line(long var1, String var3, String[] var4) {
         this(var1, var3, var4, null);
      }
   }

   public enum LyricsState {
      NONE,
      LOADING,
      SYNCED,
      NOT_FOUND,
      ERROR;
   }
}
