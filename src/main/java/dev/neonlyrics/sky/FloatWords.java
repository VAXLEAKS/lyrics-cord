package dev.neonlyrics.sky;

import dev.neonlyrics.config.Config;
import dev.neonlyrics.hud.Gfx;
import dev.neonlyrics.lyrics.NowPlaying;
import dev.neonlyrics.lyrics.SpotifyClient;
import dev.neonlyrics.util.AutoPerf;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.font.TextRenderer.TextLayerType;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

public final class FloatWords {
   private static final float[][] SLOTS = new float[][]{
      {-22.0F, 7.0F},
      {20.0F, -4.0F},
      {-6.0F, -12.0F},
      {16.0F, 12.0F},
      {-26.0F, -8.0F},
      {8.0F, 4.0F},
      {-14.0F, 14.0F},
      {26.0F, 6.0F},
      {2.0F, -16.0F},
      {-18.0F, 0.0F},
      {12.0F, -10.0F},
      {-8.0F, 9.0F}
   };
   private static final float BLOCKS_PER_PX = 0.062F;
   private static final List<FloatWords.Word> WORDS = new ArrayList<>();
   private static final Random RNG = new Random();
   private static String lastTrack = "";
   private static int lastLine = -1;
   private static int lastChunk = -1;
   private static int lastSlot = -1;
   private static int prevSlot = -1;
   private static String[] chunks = new String[0];
   private static int[] chunkOf = new int[0];
   private static final Map<String, Text> COMP = new HashMap<>();
   private static final Matrix4f M0 = new Matrix4f();
   private static final Matrix4f TMP = new Matrix4f();
   private static final Matrix4f WM = new Matrix4f();
   private static final Matrix4f SCR = new Matrix4f();

   private FloatWords() {
   }

   public static void clear() {
      WORDS.clear();
      lastLine = -1;
      lastChunk = -1;
   }

   public static void renderWorld(MinecraftClient var0, Config var1, VertexConsumerProvider var2, Matrix4f var3, Vec3d var4, float var5, boolean var6) {
      if (var0 != null && var1 != null && var3 != null && var4 != null) {
         if (var1.lyricsSky && var1.lyricsStyle == 1 && !var0.options.hudHidden && var0.player != null && var0.world != null) {
            long var7 = System.currentTimeMillis();
            boolean var9 = false;
            NowPlaying var10 = SpotifyClient.now();
            if (var10 != null && var10.hasTrack() && var10.playing && !var10.lines.isEmpty()) {
               String var11 = String.valueOf(var10.trackId);
               if (!var11.equals(lastTrack)) {
                  lastTrack = var11;
                  WORDS.clear();
                  lastLine = -1;
                  lastChunk = -1;
               }

               long var12 = var10.progressNow(var1.lyricsOffsetMs + 150);
               int var14 = var10.lineIndex(var12);
               if (var14 >= 0 && var14 < var10.lines.size() && var12 <= var10.lineEnd(var14) + 450L) {
                  var9 = update(var0, var1, var10, var14, var12, var4, var5, var7);
               }
            }

            if (!var9) {
               for (FloatWords.Word var16 : WORDS) {
                  if (var16.gone == 0L) {
                     var16.gone = var7;
                  }
               }
            }

            WORDS.removeIf(var2x -> var2x.gone != 0L && (float)(var7 - var2x.gone) > 2200.0F);
            draw(var0, var1, var2, var3, var4, var5, var7, 2200.0F, var6, var10);
         } else {
            clear();
         }
      }
   }

   private static boolean update(MinecraftClient var0, Config var1, NowPlaying var2, int var3, long var4, Vec3d var6, float var7, long var8) {
      NowPlaying.Line var10 = var2.lines.get(var3);
      String var11 = var10.text();
      if (var11 != null && !var11.isBlank()) {
         String[] var12 = var10.words();
         if (var12 == null || var12.length == 0) {
            var12 = var11.trim().split("\\s+");
         }

         if (var3 != lastLine) {
            lastLine = var3;
            lastChunk = -1;
            buildChunks(var12);
         }

         if (chunks.length == 0) {
            return false;
         }

         float var14 = var2.wordPosition(var3, var4);
         int var13;
         if (var14 >= 0.0F) {
            var13 = Math.min(var12.length - 1, (int)Math.floor(var14));
         } else {
            long var15 = var10.timeMs();
            long var17 = Math.max(var15 + 1L, var2.lineEnd(var3));
            var13 = (int)Math.floor((float)(var4 - var15) / (float)(var17 - var15) * var12.length);
            var13 = Math.max(0, Math.min(var12.length - 1, var13));
         }

         int var20 = Math.max(0, Math.min(chunks.length - 1, chunkOf[Math.min(var13, chunkOf.length - 1)]));
         if (var20 > lastChunk) {
            if (var20 - lastChunk > 2) {
               lastChunk = var20 - 1;
            }

            while (lastChunk < var20) {
               lastChunk++;
               spawn(chunks[lastChunk], var0, var1, var6, var7, var8);
            }
         }

         return true;
      } else {
         return false;
      }
   }

   private static float sz(Config var0) {
      return var0.lyricsSize / 1.7F;
   }

   private static void spawn(String var0, MinecraftClient var1, Config var2, Vec3d var3, float var4, long var5) {
      if (var0 != null && !var0.isEmpty()) {
         float var7 = var1.player.getYaw(var4);
         float var8 = -var1.player.getPitch(var4) + 12.0F;
         String var9 = var0.toUpperCase(Locale.ROOT);
         int var10 = Gfx.w(var9, Gfx.LYRICS);
         double var11 = Double.MAX_VALUE;
         double var13 = 0.0;
         double var15 = 0.0;
         double var17 = 8.0;
         double var19 = 1.0;
         double var21 = 1.0;
         double var23 = 1.0;
         int var25 = 0;

         for (int var26 = 0; var26 < 28; var26++) {
            int var28 = 0;

            int var27;
            do {
               var27 = RNG.nextInt(SLOTS.length);
               var28++;
            } while ((var27 == lastSlot || var27 == prevSlot) && var28 < 12);

            double var29 = Math.toRadians(var7 + (SLOTS[var27][0] + (RNG.nextFloat() - 0.5F) * 7.0F));
            double var31 = Math.toRadians(var8 + (SLOTS[var27][1] + (RNG.nextFloat() - 0.5F) * 6.0F));
            double var33 = 6.5 + RNG.nextDouble() * 8.5;
            float var35 = 0.82F + RNG.nextFloat() * 0.5F;
            double var36 = var10 * 0.062F * sz(var2) * var35 * 0.5 * 0.95;
            double var38 = 0.682F * sz(var2) * var35 * 0.5;
            double var40 = overlapScore(var29, var31, var33, var36, var38, var3);
            if (var40 < var11) {
               var11 = var40;
               var13 = var29;
               var15 = var31;
               var17 = var33;
               var19 = var35;
               var21 = var36;
               var23 = var38;
               var25 = var27;
            }

            if (var40 <= 0.0) {
               break;
            }
         }

         prevSlot = lastSlot;
         lastSlot = var25;
         double var56 = var13;
         double var57 = var15;
         double var30 = var17;
         float var32 = (float)var19;
         double var58 = -Math.sin(var56) * Math.cos(var57);
         double var59 = Math.sin(var57);
         double var37 = Math.cos(var56) * Math.cos(var57);
         double var39 = var3.x + var58 * var30;
         double var41 = var3.y + var59 * var30;
         double var43 = var3.z + var37 * var30;
         double var45 = Math.sqrt(var58 * var58 + var37 * var37);
         double var47;
         double var49;
         if (var45 < 1.0E-4) {
            double var51 = Math.toRadians(var7);
            var47 = Math.sin(var51);
            var49 = -Math.cos(var51);
         } else {
            var47 = -var58 / var45;
            var49 = -var37 / var45;
         }

         float var60 = (float)Math.toRadians((RNG.nextBoolean() ? 1 : -1) * (10.0F + RNG.nextFloat() * 22.0F));
         float var52 = (float)Math.toRadians((RNG.nextFloat() - 0.5F) * 14.0F);
         float var53 = (float)Math.toRadians((RNG.nextFloat() - 0.5F) * 8.0F);

         for (FloatWords.Word var55 : WORDS) {
            if (var55.gone == 0L) {
               var55.gone = var5;
            }
         }

         WORDS.add(new FloatWords.Word(var0, var39, var41, var43, var47, var49, var60, var52, var53, var32, (float)var21, (float)var23, var5));
         byte var61 = 7;

         while (WORDS.size() > var61) {
            WORDS.remove(0);
         }
      }
   }

   private static double overlapScore(double var0, double var2, double var4, double var6, double var8, Vec3d var10) {
      double var11 = 0.0;
      double var13 = var6 / var4;
      double var15 = var8 / var4;

      for (FloatWords.Word var18 : WORDS) {
         double var19 = var18.x - var10.x;
         double var21 = var18.y - var10.y;
         double var23 = var18.z - var10.z;
         double var25 = Math.sqrt(var19 * var19 + var21 * var21 + var23 * var23);
         if (!(var25 < 0.5)) {
            double var27 = Math.atan2(-var19, var23);
            double var29 = Math.asin(Math.max(-1.0, Math.min(1.0, var21 / var25)));
            double var31 = Math.abs(((var0 - var27) % (Math.PI * 2) + (Math.PI * 3)) % (Math.PI * 2) - Math.PI) * Math.cos(var2);
            double var33 = Math.abs(var2 - var29);
            double var35 = var13 + var18.halfW / var25 + 0.035;
            double var37 = var15 + var18.halfH / var25 + 0.03;
            if (var31 < var35 && var33 < var37) {
               var11 += 1.0 - var31 / var35 + (1.0 - var33 / var37) + 0.5;
            }
         }
      }

      return var11;
   }

   private static void buildChunks(String[] var0) {
      ArrayList<String> var1 = new ArrayList();
      int[] var2 = new int[var0.length];
      int var3 = 0;

      while (var3 < var0.length) {
         String var4 = clean(var0[var3]);
         if (var4.isEmpty()) {
            var2[var3] = var1.isEmpty() ? 0 : var1.size() - 1;
            var3++;
         } else {
            int var5 = var1.size();
            String var6 = var4;
            byte var7 = 1;
            if (var4.length() <= 3 && var3 + 1 < var0.length) {
               String var8 = clean(var0[var3 + 1]);
               if (!var8.isEmpty()) {
                  var6 = var4 + " " + var8;
                  var7 = 2;
                  var2[var3 + 1] = var5;
               }
            }

            var2[var3] = var5;
            var1.add(var6);
            var3 += var7;
         }
      }

      chunks = var1.toArray(new String[0]);
      chunkOf = var2;
   }

   private static String clean(String var0) {
      if (var0 == null) {
         return "";
      }

      int var1 = 0;
      int var2 = var0.length();

      while (var1 < var2 && !keep(var0.charAt(var1))) {
         var1++;
      }

      while (var2 > var1 && !keep(var0.charAt(var2 - 1))) {
         var2--;
      }

      return var0.substring(var1, var2);
   }

   private static boolean keep(char var0) {
      return Character.isLetterOrDigit(var0) || var0 == '*' || var0 == '\'';
   }

   private static float clamp01(float var0) {
      return var0 < 0.0F ? 0.0F : Math.min(var0, 1.0F);
   }

   private static float easeOut(float var0) {
      float var1 = 1.0F - var0;
      return 1.0F - var1 * var1 * var1;
   }

   private static Text comp(String var0, int var1) {
      if (COMP.size() > 600) {
         COMP.clear();
      }

      return COMP.computeIfAbsent(var1 + "|" + var0, var1x -> Gfx.comp(var0, Gfx.LYRICS));
   }

   private static void draw(
      MinecraftClient var0,
      Config var1,
      VertexConsumerProvider var2,
      Matrix4f var3,
      Vec3d var4,
      float var5,
      long var6,
      float var8,
      boolean var9,
      NowPlaying var10
   ) {
      if (!WORDS.isEmpty()) {
         TextRenderer var11 = var0.textRenderer;
         int var12 = var1.lyricsFont;
         int var13 = var1.lyricsColor >= 0 ? WorldSky.LYRIC_COLORS[Math.floorMod(var1.lyricsColor, WorldSky.LYRIC_COLORS.length)] : 16777215;
         boolean var14 = var1.lyricsGlow && AutoPerf.allow(3);
         int var15 = AutoPerf.quality();
         int var16 = var15 <= 0 ? 0 : (var15 == 1 ? 2 : 3);
         TextLayerType var17 = var9 ? TextLayerType.SEE_THROUGH : TextLayerType.NORMAL;
         double var18 = Math.toRadians(var0.player.getYaw(var5));
         double var20 = Math.toRadians(var0.player.getPitch(var5));
         double var22 = -Math.sin(var18) * Math.cos(var20);
         double var24 = -Math.sin(var20);
         double var26 = Math.cos(var18) * Math.cos(var20);
         float var28 = var1.fxBeatPulse && var10 != null && var10.hasTrack() ? var10.beatPulse(var10.progressNow(var1.lyricsOffsetMs)) : 0.0F;
         Iterator var29 = WORDS.iterator();

         while (true) {
            FloatWords.Word var30;
            long var31;
            float var33;
            float var34;
            boolean var35;
            while (true) {
               if (!var29.hasNext()) {
                  return;
               }

               var30 = (FloatWords.Word)var29.next();
               var31 = var6 - var30.born;
               var33 = clamp01((float)var31 / 140.0F) * var1.lyricsOpacity;
               var34 = 1.0F;
               var35 = var30.gone == 0L;
               if (var35) {
                  break;
               }

               float var36 = (float)(var6 - var30.gone) / var8;
               if (!(var36 >= 1.0F)) {
                  var33 *= 1.0F - var36 * var36;
                  var34 = 1.0F - 0.5F * easeOut(clamp01((float)(var6 - var30.gone) / 240.0F));
                  break;
               }
            }

            if (!(var33 < 0.03F)) {
               double var117 = Math.min(var31 / 1000.0 * 0.25, 1.0);
               double var38 = var30.x - var4.x;
               double var40 = var30.y + var117 - var4.y;
               double var42 = var30.z - var4.z;
               if (!(var38 * var22 + var40 * var24 + var42 * var26 < 0.3)) {
                  double var44 = Math.cos(var30.tilt);
                  double var46 = Math.sin(var30.tilt);
                  double var48 = var30.nx * var44 - var30.nz * var46;
                  double var50 = var30.nx * var46 + var30.nz * var44;
                  double var52 = var50;
                  double var54 = 0.0;
                  double var56 = -var48;
                  double var58 = 0.0;
                  double var60 = 1.0;
                  double var62 = 0.0;
                  double var64 = var48;
                  double var66 = 0.0;
                  double var68 = var50;
                  double var70 = Math.cos(var30.pitch);
                  double var72 = Math.sin(var30.pitch);
                  double var74 = var58 * var70 + var64 * var72;
                  double var76 = var60 * var70 + var66 * var72;
                  double var78 = var62 * var70 + var68 * var72;
                  double var80 = var64 * var70 - var58 * var72;
                  double var82 = var66 * var70 - var60 * var72;
                  double var84 = var68 * var70 - var62 * var72;
                  double var86 = Math.cos(var30.roll);
                  double var88 = Math.sin(var30.roll);
                  double var90 = var52 * var86 + var74 * var88;
                  double var92 = var54 * var86 + var76 * var88;
                  double var94 = var56 * var86 + var78 * var88;
                  double var96 = var74 * var86 - var52 * var88;
                  double var98 = var76 * var86 - var54 * var88;
                  double var100 = var78 * var86 - var56 * var88;
                  if (var38 * var80 + var40 * var82 + var42 * var84 > 0.0) {
                     var90 = -var90;
                     var92 = -var92;
                     var94 = -var94;
                     var80 = -var80;
                     var82 = -var82;
                     var84 = -var84;
                  }

                  float var102 = 1.0F + 0.22F * (1.0F - easeOut(clamp01((float)var31 / 240.0F)));
                  float var103 = 0.062F * sz(var1) * var30.size * var102;
                  Matrix4f var104 = M0.set(var3)
                     .mul(
                        TMP.set(
                           (float)var90 * var103,
                           (float)var92 * var103,
                           (float)var94 * var103,
                           0.0F,
                           (float)(-var96) * var103,
                           (float)(-var98) * var103,
                           (float)(-var100) * var103,
                           0.0F,
                           (float)var80 * var103,
                           (float)var82 * var103,
                           (float)var84 * var103,
                           0.0F,
                           (float)var38,
                           (float)var40,
                           (float)var42,
                           1.0F
                        )
                     );
                  String var105 = var30.text.toUpperCase(Locale.ROOT);
                  int var106 = Gfx.w(var105, Gfx.LYRICS);
                  int var107 = var105.length();
                  var107 = Math.max(1, (int)Math.ceil(var105.length() * clamp01((float)var31 / 230.0F)));
                  Text var108 = comp(var105.substring(0, var107), var12);
                  Matrix4f var109 = WM.set(var104);
                  if (var35 && var28 > 0.01F) {
                     float var110 = 1.0F + 0.06F * var28;
                     var109.scale(var110, var110, 1.0F);
                  }

                  var109.translate(-var106 / 2.0F, -4.5F, 0.0F);
                  int var119 = Gfx.lerpColor(9211040, var13, var34);
                  int var111 = var1.lyricsGlowColor >= 0 ? WorldSky.LYRIC_COLORS[Math.floorMod(var1.lyricsGlowColor, WorldSky.LYRIC_COLORS.length)] : var13;
                  int var112 = Gfx.lerpColor(9211040, var111, var34);
                  put(var11, var2, var109, var108, 0.7F, 0.9F, -0.5F, 328460, var33 * 0.55F, var17);
                  if (var14) {
                     float var113 = var33 * var34 * 0.2F * var1.lyricsGlowStrength * (var35 ? 1.3F : 0.8F);
                     if (var16 > 0) {
                        for (int var114 = 0; var114 < var16; var114++) {
                           double var115 = var114 * Math.PI * 2.0 / var16 + (Math.PI / 8);
                           put(
                              var11,
                              var2,
                              var109,
                              var108,
                              (float)Math.cos(var115) * 1.1F,
                              (float)Math.sin(var115) * 1.1F,
                              0.1F,
                              var112,
                              var113 * 6.0F / var16,
                              var17
                           );
                        }
                     } else {
                        put(var11, var2, var109, var108, 0.0F, 0.6F, 0.1F, var112, var113 * 1.6F, var17);
                     }
                  }

                  put(var11, var2, var109, var108, 0.0F, 0.0F, 0.3F, var119, var33, var17);
               }
            }
         }
      }
   }

   private static void put(
      TextRenderer var0, VertexConsumerProvider var1, Matrix4f var2, Text var3, float var4, float var5, float var6, int var7, float var8, TextLayerType var9
   ) {
      if (!(var8 <= 0.03F)) {
         Matrix4f var10 = SCR.set(var2).translate(var4, var5, var6);
         var0.draw(var3, 0.0F, 0.0F, Gfx.argbRaw(var7, var8), false, var10, var1, var9, 0, 15728880);
      }
   }

   private static final class Word {
      final String text;
      final double x;
      final double y;
      final double z;
      final double nx;
      final double nz;
      final float tilt;
      final float pitch;
      final float roll;
      final float size;
      final float halfW;
      final float halfH;
      final long born;
      long gone = 0L;

      Word(
         String var1,
         double var2,
         double var4,
         double var6,
         double var8,
         double var10,
         float var12,
         float var13,
         float var14,
         float var15,
         float var16,
         float var17,
         long var18
      ) {
         this.text = var1;
         this.x = var2;
         this.y = var4;
         this.z = var6;
         this.nx = var8;
         this.nz = var10;
         this.tilt = var12;
         this.pitch = var13;
         this.roll = var14;
         this.size = var15;
         this.halfW = var16;
         this.halfH = var17;
         this.born = var18;
      }
   }
}
