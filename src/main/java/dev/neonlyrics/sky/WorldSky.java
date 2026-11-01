package dev.neonlyrics.sky;

import dev.neonlyrics.Beat;
import dev.neonlyrics.NeonLyrics;
import dev.neonlyrics.config.Config;
import dev.neonlyrics.cosmetic.Accessories;
import dev.neonlyrics.cosmetic.DeathFx;
import dev.neonlyrics.cosmetic.Emotes;
import dev.neonlyrics.cosmetic.PhoneMenu;
import dev.neonlyrics.hud.Anim;
import dev.neonlyrics.hud.Gfx;
import dev.neonlyrics.hud.Theme;
import dev.neonlyrics.lyrics.NowPlaying;
import dev.neonlyrics.lyrics.SpotifyClient;
import dev.neonlyrics.util.AutoPerf;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.font.TextRenderer.TextLayerType;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Matrix4f;
import org.joml.Vector4f;

public final class WorldSky {
   private static final Random RND = new Random();
   private static float[][] stars;
   private static final List<float[]> shooting = new ArrayList<>();
   private static long nextShooting = 0L;
   private static final long T0 = System.currentTimeMillis();
   private static double[][] parts = new double[0][];
   private static SkyPreset.Weather partType = null;
   private static long lastNanos = System.nanoTime();
   private static final Anim visible = new Anim(0.0, 3.0);
   private static final Anim outside = new Anim(0.0, 2.0);
   private static final Anim musicCalm = new Anim(1.0, 1.5);
   private static double fx;
   private static double fy;
   private static double fz;
   private static double rx;
   private static double ry;
   private static double rz;
   private static double ux;
   private static double uy;
   private static double uz;
   private static float camYaw;
   private static float camPitch;
   private static double dist;
   private static double texDist;
   private static double skyD;
   private static boolean skyOpen = true;
   private static Matrix4f base;
   private static double skyTime = 1000.0;
   private static double breathT = 0.0;
   private static final float[] Q_STARS = new float[]{0.4F, 0.65F, 1.0F};
   private static final double[] Q_AURORA_STEP = new double[]{7.0, 4.5, 3.0};
   private static final float[] Q_WEATHER = new float[]{0.3F, 0.55F, 0.85F};
   private static final int[] Q_DOME = new int[]{36, 30, 24};
   private static final int[] Q_MOON = new int[]{10, 14, 20};
   private static final int[] Q_GLOW = new int[]{0, 2, 4};
   private static VertexConsumer quads;
   private static float djFrontA;
   private static float djFrontB;
   private static boolean djShaderNow;
   private static final double[][] RING = new double[16][3];
   private static int ringPos = 0;
   private static final float[] DAY_AL = new float[]{0.0F, 0.55F, 0.9F, 0.97F, 0.985F, 0.99F, 0.99F};
   private static final double[] DOME_EL = new double[]{-6.0, 0.0, 8.0, 20.0, 38.0, 62.0, 90.0};
   private static final float[] DOME_AL = new float[]{0.0F, 0.3F, 0.42F, 0.52F, 0.6F, 0.64F, 0.64F};
   private static final float[] DOME_A = new float[DOME_EL.length];
   private static final int[] DOME_C = new int[DOME_EL.length];
   private static double[][][] domeGridCache;
   private static int domeGridStep = -1;
   private static double flashNext = 0.0;
   private static double flashT0 = -10.0;
   private static double[][] bolt = new double[0][0];
   public static final int[][] PALETTES = new int[][]{
      {10719743, 16184063}, {16740036, 16769266}, {3073750, 10155775}, {4652976, 8186111}, {16757596, 16739950}, {10475775, 16777215}, {13716479, 4970751}
   };
   public static final String[] PALETTE_KEYS = new String[]{"purple", "pink", "ocean", "mint", "sunset", "ice", "neon"};
   private static final double[][][] CONST = new double[][][]{
      {{30.0, 48.0}, {36.0, 50.0}, {42.0, 49.0}, {47.0, 46.0}, {50.0, 41.0}, {57.0, 42.0}, {56.0, 47.0}},
      {{160.0, 38.0}, {165.0, 44.0}, {171.0, 40.0}, {177.0, 46.0}, {183.0, 41.0}},
      {{250.0, 30.0}, {254.0, 36.0}, {258.0, 30.0}, {251.0, 25.0}, {257.0, 24.0}, {254.0, 36.0}, {254.0, 43.0}},
      {{320.0, 55.0}, {326.0, 58.0}, {331.0, 55.0}, {326.0, 52.0}, {320.0, 55.0}}
   };
   private static String introTrack = "";
   private static long introStart = 0L;
   private static double introAz = 0.0;
   private static double introEl = 0.0;
   private static final int PF = 11;
   private static final double[] TEX_EL = new double[]{-8.0, -3.0, 2.0, 8.0, 15.0, 24.0, 35.0, 48.0, 62.0, 76.0, 90.0};
   private static final float MESH_EL0 = -4.0F;
   private static final float MESH_EL1 = 90.0F;
   private static int meshVer = -1;
   private static int meshNA;
   private static int meshNE;
   private static double meshStep = 0.0;
   private static float[] mX;
   private static float[] mY;
   private static float[] mZ;
   private static float[] mAz;
   private static float[] mEl;
   private static int[] mBase;
   private static int[] mFrame;
   private static int[] mStamp;
   private static byte[] mDetail;
   private static int frameNo = 0;
   private static double liveStep = 0.0;
   private static int liveNA;
   private static int liveNE;
   private static float[] lX;
   private static float[] lY;
   private static float[] lZ;
   private static float[] lEl;
   private static int[] lCol;
   private static int[] lStamp;
   private static SkyPreset lastLive;
   private static long liveSince;
   private static float fogR;
   private static float fogG;
   private static float fogB;
   private static float fogMix;
   private static final Map<String, RenderLayer> PANO = new HashMap<>();
   private static boolean noFogBroken;
   private static final int[] SUB_COL = new int[16];
   private static final float[][] SUB_P = new float[16][3];
   private static final ArrayDeque<double[]> TRAIL = new ArrayDeque<>();
   private static long lastTrail = 0L;
   private static double lastTx;
   private static double lastTz;
   private static double lastSide = 0.0;
   private static WorldSky.SkyLine active;
   private static final List<WorldSky.SkyLine> exiting = new ArrayList<>();
   private static int activeIdx = -2;
   private static String activeTrack = "";
   private static final Matrix4f M0 = new Matrix4f();
   private static final Matrix4f TMP = new Matrix4f();
   private static final Matrix4f WM = new Matrix4f();
   private static final Matrix4f SCR = new Matrix4f();
   private static final Map<String, Text> COMP = new HashMap<>();
   private static int compFont = -1;
   public static final int[] LYRIC_COLORS = new int[]{13219327, 16777215, 16747224, 9098495, 9240520, 16765562, 16743034, 11635967};

   private WorldSky() {
   }

   private static int q() {
      return AutoPerf.quality();
   }

   public static void render(WorldRenderContext var0) {
      if (Beat.live() && Beat.sync()) {
         AutoPerf.frame();
         AutoPerf.begin();

         try {
            renderSky(var0);
         } finally {
            AutoPerf.end();
         }
      }
   }

   public static boolean skyWorld(World var0) {
      return var0 != null && var0.getRegistryKey() != World.NETHER && var0.getRegistryKey() != World.END;
   }

   public static boolean hidesClouds() {
      try {
         MinecraftClient var0 = MinecraftClient.getInstance();
         return NeonLyrics.cfg().hideClouds && Beat.live() && SkyPreview.sky() >= 0 && var0.world != null && skyWorld(var0.world);
      } catch (Throwable var1) {
         return false;
      }
   }

   private static void renderSky(WorldRenderContext var0) {
      fogMix = 0.0F;
      djFrontA = 0.0F;
      MinecraftClient var1 = MinecraftClient.getInstance();
      if (var1.player != null && var1.world != null) {
         Config var2 = NeonLyrics.cfg();
         SkyPreset var3 = SkyPreset.get(SkyPreview.sky());
         long var4 = System.nanoTime();
         float var6 = Math.min(0.1F, (float)(var4 - lastNanos) / 1.0E9F);
         lastNanos = var4;
         float var7 = var1.getRenderTickCounter().getTickProgress(false);
         Camera var8 = var1.gameRenderer.getCamera();
         camYaw = var8.getYaw();
         camPitch = var8.getPitch();
         double var9 = Math.toRadians(camYaw);
         double var11 = Math.toRadians(camPitch);
         fx = -Math.sin(var9) * Math.cos(var11);
         fy = -Math.sin(var11);
         fz = Math.cos(var9) * Math.cos(var11);
         rx = -Math.cos(var9);
         ry = 0.0;
         rz = -Math.sin(var9);
         ux = ry * fz - rz * fy;
         uy = rz * fx - rx * fz;
         uz = rx * fy - ry * fx;
         dist = Math.max(40.0, Math.min(180.0, var1.options.getClampedViewDistance() * 16 * 0.7));
         texDist = Math.max(dist * 0.975, var1.options.getClampedViewDistance() * 16 * 0.8);
         skyD = Math.max(dist, var1.options.getClampedViewDistance() * 16 * 1.04 + 8.0);
         boolean var13 = skyWorld(var1.world);
         float var14 = visible.target(var3 != null && var13 ? 1.0 : 0.0).get();
         skyOpen = var1.world.isSkyVisible(var1.player.getBlockPos().up());
         float var15 = outside.target(skyOpen ? 1.0 : 0.0).get();
         skyTime = skyTime + var6 * Math.max(0.1F, Math.min(3.0F, var2.skySpeed));
         breathT = breathT + var6 * var2.skyBreath * 1.3;
         double var16 = skyTime;
         MatrixStack var18 = var0.matrices();
         base = new Matrix4f(var18.peek().getPositionMatrix());
         VertexConsumerProvider var19 = var0.consumers();
         NowPlaying var20 = SpotifyClient.now();
         float var21 = 0.0F;
         float var22 = 1.0F;
         if (var2.fxSkyMusic && var20.hasTrack() && var20.playing) {
            long var23 = var20.progressNow(var2.lyricsOffsetMs);
            var21 = var20.beatPulse(var23);
            int var25 = var20.lineIndex(var23);
            boolean var26 = var25 >= 0 && var23 <= var20.lineEnd(var25) + 300L;
            var22 = musicCalm.target(var26 ? 1.0 : 0.7F).get();
         }

         long var36 = Math.floorMod(var1.world.getTimeOfDay(), 24000L);
         if (var36 >= 13000L && var36 <= 23000L) {
            float var47 = 1.0F;
         } else if (var36 > 12000L && var36 < 13000L) {
            float var46 = (float)(var36 - 12000L) / 1000.0F;
         } else if (var36 > 23000L) {
            float var10000 = 1.0F - (float)(var36 - 23000L) / 1000.0F;
         } else {
            float var45 = 0.0F;
         }

         float var37 = 1.0F;
         float var27 = 1.0F;
         if (var3 != null && var14 > 0.01F) {
            SkyTextures.update();
            float var28 = var3.strength * var2.skyStrength * var14 * var27;
            quads = var19.getBuffer(RenderLayers.debugQuads());
            djShaderNow = var3 == SkyPreset.DJ && LiveSkyShader.readyFx();
            boolean var29 = var3.ordinal() >= 29 && !djShaderNow;
            if ((var2.fxSkyColor || var37 > 0.01F) && !var29) {
               drawDome(var3, var28, var37 * var14, var1);
            }

            LiveSky.MAIN.prepare(var3, aurora1(var3), aurora2(var3), var16, var2);
            float var30 = var14 * liveFade(var3);
            float var31 = var27 * (0.92F + 0.08F * var22) * (1.0F + 0.12F * var21);
            updateFog(var30, var31);
            boolean var32 = var3 == SkyPreset.DJ;
            RenderLayer var33 = var3 == SkyPreset.CUSTOM ? SkyImages.type(var2.customImage) : null;
            if (var33 != null) {
               drawImageSky(var33, var30, var19, var1);
            } else if (var29) {
               drawPano(var3, var30, var19, var1);
               if (var32) {
                  djFrontA = var30;
               }
            } else if (!drawLiveShader(var3, var30, var31, var16, var2, var19, var1, var32 ? 1 : 0, skyD)) {
               drawLiveSky(var30, var31, var1);
            } else if (var32) {
               djFrontA = var30;
               djFrontB = var31;
            }

            quads = null;
            quads = var19.getBuffer(RenderLayers.debugQuads());
            if (var3.stars > 0.0F && var2.fxStars && AutoPerf.allow(5)) {
               drawStars(var3, var14 * var27 * (0.85F + 0.3F * var21), var16);
            }

            if (var3.stars > 0.0F && var2.fxStars && var2.fxConstellations && AutoPerf.allow(2)) {
               drawConstellations(var14 * var27);
            }

            if (var3 == SkyPreset.STORM && AutoPerf.allow(2)) {
               drawStorm(var14, var16);
            }

            if ((var3.shootingStars || var3.stars >= 0.8F) && AutoPerf.allow(3)) {
               drawShooting(var14, var6);
            }

            drawBody(var3, var14, var16);
            quads = null;
            if (var2.fxWeather
               && SkyTextures.spritesReady()
               && AutoPerf.allow(4)
               && var13
               && var2.weatherAmount > 0.01F
               && var3.weather != SkyPreset.Weather.NONE
               && var15 > 0.01F) {
               Vec3d var34 = var0.worldState().cameraRenderState.pos;
               VertexConsumer var35 = var19.getBuffer(RenderLayers.text(SkyTextures.SPRITES));
               drawWeather(var35, var3.weather, var2.weatherAmount * (AutoPerf.allow(3) ? 1.0F : 0.5F), var15, var6, var16, var34);
            }

            if (var3 == SkyPreset.RAIN && var13 && SkyTextures.spritesReady() && Accessories.whiteTex()) {
               RainFx.render(var1, var19, base, var0.worldState().cameraRenderState.pos, var14, var6, fx, fy, fz, rx, ry, rz, ux, uy, uz);
            }
         }

         int var38 = var2.lyricsColor >= 0
            ? LYRIC_COLORS[Math.floorMod(var2.lyricsColor, LYRIC_COLORS.length)]
            : (var3 == null ? Theme.accent() : var3.lyricColor);
         if (var2.lyricsSky && var2.lyricsStyle == 0) {
            drawLyrics(var1, var2, var19, var38, var6);
         }

         if (var2.lyricsStyle == 1) {
            boolean var10006 = var2.lyricsThroughBlocks || !skyOpen || var2.lyricsUnderground;
            FloatWords.renderWorld(var1, var2, var19, base, var0.worldState().cameraRenderState.pos, var7, var10006);
         }

         if (var2.fxSongIntro) {
            drawSongIntro(var1, var2, var19, var38);
         }

         if (djFrontA > 0.01F && var19 instanceof Immediate var39) {
            var39.draw();
            if (!djShaderNow) {
               NowPlaying var41 = SpotifyClient.now();
               boolean var42 = var41.hasTrack() && var41.playing;
               double var43 = var42 ? var41.beatPos(var41.progressNow(var2.lyricsOffsetMs)) : skyTime * 100.0 / 60.0;
               RenderLayer var44 = RenderLayers.text(Accessories.WHITE);
               if (Accessories.whiteTex()) {
                  DjModel.draw(var19.getBuffer(var44), base, texDist * 0.93, djFrontA, var43, var42, skyTime, Math.floorMod(var2.djStyle, 5));
                  var39.draw(var44);
               }
            } else {
               drawLiveShader(var3, djFrontA, djFrontB, var16, var2, var19, var1, 2, Math.max(24.0, dist * 0.8));
            }
         }

         Vec3d var40 = var0.worldState().cameraRenderState.pos;
         if (var2.trail > 0 || var2.aura) {
            SkyTextures.update();
            drawPlayerFx(var1, var2, var19, var40, var7, var16, var3);
         }

         Accessories.render(var1, var19, base, var40, var7);
         DeathFx.render(var1, var19, base, var40, var7);
         Emotes.render(var1, var19, base, var40, var7);
         PhoneMenu.render(var1, var19, base, var7);
         base = null;
      }
   }

   private static double[] p(double var0, double var2, double var4) {
      double[] var6 = RING[ringPos];
      ringPos = ringPos + 1 & 15;
      var6[0] = var0;
      var6[1] = var2;
      var6[2] = var4;
      return var6;
   }

   private static double[] dir(double var0, double var2) {
      double var4 = Math.toRadians(var0);
      double var6 = Math.toRadians(var2);
      double var8 = Math.cos(var6);
      return p(-Math.sin(var4) * var8, Math.sin(var6), Math.cos(var4) * var8);
   }

   private static void v(double var0, double var2, double var4, int var6, float var7) {
      quads.vertex(base, (float)var0, (float)var2, (float)var4).color(Gfx.argbRaw(var6, var7));
   }

   private static void quad(
      double[] var0, double[] var1, double[] var2, double[] var3, int var4, float var5, int var6, float var7, int var8, float var9, int var10, float var11
   ) {
      if (!(var5 <= 0.003F) || !(var7 <= 0.003F) || !(var9 <= 0.003F) || !(var11 <= 0.003F)) {
         v(var0[0], var0[1], var0[2], var4, var5);
         v(var1[0], var1[1], var1[2], var6, var7);
         v(var2[0], var2[1], var2[2], var8, var9);
         v(var3[0], var3[1], var3[2], var10, var11);
         v(var3[0], var3[1], var3[2], var10, var11);
         v(var2[0], var2[1], var2[2], var8, var9);
         v(var1[0], var1[1], var1[2], var6, var7);
         v(var0[0], var0[1], var0[2], var4, var5);
      }
   }

   private static double[] at(double var0, double var2, double var4) {
      double[] var6 = dir(var0, var2);
      var6[0] *= var4;
      var6[1] *= var4;
      var6[2] *= var4;
      return var6;
   }

   private static void billboard(double var0, double var2, double var4, double var6, int var8, float var9) {
      double var10 = var6 / 2.0;
      double[] var12 = p(var0 - rx * var10 - ux * var10, var2 - ry * var10 - uy * var10, var4 - rz * var10 - uz * var10);
      double[] var13 = p(var0 + rx * var10 - ux * var10, var2 + ry * var10 - uy * var10, var4 + rz * var10 - uz * var10);
      double[] var14 = p(var0 + rx * var10 + ux * var10, var2 + ry * var10 + uy * var10, var4 + rz * var10 + uz * var10);
      double[] var15 = p(var0 - rx * var10 + ux * var10, var2 - ry * var10 + uy * var10, var4 - rz * var10 + uz * var10);
      cquad(var12, var13, var14, var15, var8, var9, var8, var9, var8, var9, var8, var9);
   }

   private static void streak(double var0, double var2, double var4, double var6, double var8, double var10, double var12, int var14, float var15, float var16) {
      double var17 = var6 - var0;
      double var19 = var8 - var2;
      double var21 = var10 - var4;
      double var23 = (var0 + var6) / 2.0;
      double var25 = (var2 + var8) / 2.0;
      double var27 = (var4 + var10) / 2.0;
      double var29 = var19 * var27 - var21 * var25;
      double var31 = var21 * var23 - var17 * var27;
      double var33 = var17 * var25 - var19 * var23;
      double var35 = Math.sqrt(var29 * var29 + var31 * var31 + var33 * var33);
      if (!(var35 < 1.0E-6)) {
         double var37 = var12 / 2.0 / var35;
         var29 *= var37;
         var31 *= var37;
         var33 *= var37;
         quad(
            p(var0 - var29, var2 - var31, var4 - var33),
            p(var0 + var29, var2 + var31, var4 + var33),
            p(var6 + var29, var8 + var31, var10 + var33),
            p(var6 - var29, var8 - var31, var10 - var33),
            var14,
            var15,
            var14,
            var15,
            var14,
            var16,
            var14,
            var16
         );
      }
   }

   private static double[][][] domeGrid(int var0) {
      if (domeGridCache != null && domeGridStep == var0) {
         return domeGridCache;
      }

      int var1 = 360 / var0;
      double[][][] var2 = new double[var1 + 1][DOME_EL.length][3];

      for (int var3 = 0; var3 <= var1; var3++) {
         double var4 = Math.toRadians(var3 * var0);

         for (int var6 = 0; var6 < DOME_EL.length; var6++) {
            double var7 = Math.toRadians(DOME_EL[var6]);
            var2[var3][var6][0] = -Math.sin(var4) * Math.cos(var7);
            var2[var3][var6][1] = Math.sin(var7);
            var2[var3][var6][2] = Math.cos(var4) * Math.cos(var7);
         }
      }

      domeGridCache = var2;
      domeGridStep = var0;
      return var2;
   }

   private static void drawDome(SkyPreset var0, float var1, float var2, MinecraftClient var3) {
      double var4 = skyD * 1.25;
      int var6 = Q_DOME[q()];
      double[][][] var7 = domeGrid(var6);
      float[] var8 = DOME_A;
      int[] var9 = DOME_C;

      for (int var10 = 0; var10 < DOME_EL.length; var10++) {
         var8[var10] = DOME_AL[var10] * var1 + (DAY_AL[var10] - DOME_AL[var10] * var1) * var2;
         var9[var10] = Gfx.lerpColor(var0.horizon, var0.top, (float)Math.max(0.0, DOME_EL[var10] / 45.0));
      }

      int var18 = var7.length - 1;

      for (int var11 = 0; var11 < var18; var11++) {
         double[] var12 = var7[var11][3];
         if (!(var12[0] * fx + var12[2] * fz < -0.55) || !(fy > -0.6) || !(fy < 0.6)) {
            for (int var13 = 0; var13 < DOME_EL.length - 1; var13++) {
               double[] var14 = var7[var11][var13];
               double[] var15 = var7[var11 + 1][var13];
               double[] var16 = var7[var11 + 1][var13 + 1];
               double[] var17 = var7[var11][var13 + 1];
               quad(
                  p(var14[0] * var4, var14[1] * var4, var14[2] * var4),
                  p(var15[0] * var4, var15[1] * var4, var15[2] * var4),
                  p(var16[0] * var4, var16[1] * var4, var16[2] * var4),
                  p(var17[0] * var4, var17[1] * var4, var17[2] * var4),
                  var9[var13],
                  var8[var13],
                  var9[var13],
                  var8[var13],
                  var9[var13 + 1],
                  var8[var13 + 1],
                  var9[var13 + 1],
                  var8[var13 + 1]
               );
            }
         }
      }
   }

   private static void initStars() {
      Random var0 = new Random(1337L);
      stars = new float[1300][8];

      for (float[] var4 : stars) {
         var4[0] = var0.nextFloat() * 360.0F;
         var4[1] = (float)Math.toDegrees(Math.asin(0.04 + var0.nextFloat() * 0.96));
         var4[2] = var0.nextFloat() < 0.1F ? 1.8F : 1.0F;
         var4[3] = 0.35F + var0.nextFloat() * 0.65F;
         var4[4] = var0.nextFloat() * 6.28F;
         double var5 = Math.toRadians(var4[0]);
         double var7 = Math.toRadians(var4[1]);
         var4[5] = (float)(-Math.sin(var5) * Math.cos(var7));
         var4[6] = (float)Math.sin(var7);
         var4[7] = (float)(Math.cos(var5) * Math.cos(var7));
      }
   }

   private static void drawStars(SkyPreset var0, float var1, double var2) {
      if (stars == null) {
         initStars();
      }

      int var4 = (int)Math.min(stars.length, 520.0F * var0.stars * Q_STARS[q()]);
      double var5 = skyD * 0.98;
      double var7 = var5 * 0.0016;

      for (int var9 = 0; var9 < var4; var9++) {
         float[] var10 = stars[var9];
         if (!(var10[5] * fx + var10[6] * fy + var10[7] * fz < 0.05)) {
            float var11 = (float)(0.75 + 0.25 * Math.sin(var2 * (0.8 + var10[4] * 0.2) + var10[4]));
            float var12 = var10[3] * var11 * var1 * Math.min(1.0F, var10[1] / 10.0F);
            float var13 = NeonLyrics.cfg().skyLively;
            float var14 = var13 * (float)Math.pow(0.5 + 0.5 * Math.sin(var2 * (1.3 + var10[4] * 0.4) + var10[4] * 3.0F), 3.0) + (1.0F - var13) * 0.5F;
            billboard(var10[5] * var5, var10[6] * var5, var10[7] * var5, var7 * 1.7 * var10[2], 16052479, var12 * (0.25F + 0.75F * var14));
         }
      }
   }

   private static float flashLevel(double var0) {
      if (var0 >= flashNext) {
         flashT0 = var0;
         flashNext = var0 + 0.5 + RND.nextDouble() * 2.0;
         bolt = makeBolt();
      }

      double var2 = var0 - flashT0;
      if (var2 < 0.05) {
         return 1.0F;
      } else if (var2 < 0.12) {
         return 0.25F;
      } else if (var2 < 0.18) {
         return 0.9F;
      } else {
         return var2 < 0.6 ? (float)(0.8 * Math.exp(-(var2 - 0.18) * 9.0)) : 0.0F;
      }
   }

   private static double[][] makeBolt() {
      byte var0 = 10;
      double[][] var1 = new double[var0][2];
      double var2 = RND.nextDouble() * 360.0;

      for (int var4 = 0; var4 < var0; var4++) {
         double var5 = (double)var4 / (var0 - 1);
         var1[var4][0] = var2 + (RND.nextDouble() - 0.5) * 9.0;
         var1[var4][1] = 58.0 - var5 * 48.0 + (RND.nextDouble() - 0.5) * 4.0;
         var2 += (RND.nextDouble() - 0.5) * 6.0;
      }

      return var1;
   }

   private static void drawStorm(float var0, double var1) {
      float var3 = flashLevel(var1);
      double var4 = skyD * 0.97;
      if (var3 > 0.02F && bolt.length > 1) {
         double[] var6 = at(0.0, 70.0, var4);
         billboard(var6[0], var6[1], var6[2], var4 * 1.2, 13161727, 0.35F * var3 * var0);

         for (int var7 = 0; var7 + 1 < bolt.length; var7++) {
            double[] var8 = at(bolt[var7][0], bolt[var7][1], var4);
            double[] var9 = at(bolt[var7 + 1][0], bolt[var7 + 1][1], var4);
            streak(var8[0], var8[1], var8[2], var9[0], var9[1], var9[2], var4 * 0.006, 12110079, var3 * var0, var3 * var0);
            streak(var8[0], var8[1], var8[2], var9[0], var9[1], var9[2], var4 * 0.0025, 16777215, var3 * var0, var3 * var0);
         }
      }
   }

   public static int aurora1(SkyPreset var0) {
      int var1 = NeonLyrics.cfg().skyPalette;
      if (var1 >= 0 && var1 < PALETTES.length) {
         return PALETTES[var1][0];
      } else {
         return var0.aurora1 != 0 ? var0.aurora1 : var0.lyricColor;
      }
   }

   public static int aurora2(SkyPreset var0) {
      int var1 = NeonLyrics.cfg().skyPalette;
      if (var1 >= 0 && var1 < PALETTES.length) {
         return PALETTES[var1][1];
      } else {
         return var0.aurora2 != 0 ? var0.aurora2 : 16777215;
      }
   }

   private static void drawConstellations(float var0) {
      double var1 = skyD * 0.975;
      double var3 = var1 * 0.0016;

      for (double[][] var8 : CONST) {
         for (int var9 = 0; var9 < var8.length; var9++) {
            double[] var10 = at(var8[var9][0], var8[var9][1], var1);
            double var11 = var10[0];
            double var13 = var10[1];
            double var15 = var10[2];
            billboard(var11, var13, var15, var3 * 2.6, 16777215, 0.9F * var0);
            if (var9 + 1 < var8.length) {
               double[] var17 = at(var8[var9 + 1][0], var8[var9 + 1][1], var1);
               streak(var11, var13, var15, var17[0], var17[1], var17[2], var3 * 0.7, 14208255, 0.22F * var0, 0.22F * var0);
            }
         }
      }
   }

   private static void drawSongIntro(MinecraftClient var0, Config var1, VertexConsumerProvider var2, int var3) {
      NowPlaying var4 = SpotifyClient.now();
      if (var4.hasTrack()) {
         String var5 = String.valueOf(var4.trackId);
         long var6 = System.currentTimeMillis();
         if (!var5.equals(introTrack)) {
            introTrack = var5;
            introStart = var6;
            introAz = camYaw;
            introEl = Math.max(8.0F, Math.min(50.0F, -camPitch + 10.0F));
         }

         long var8 = var6 - introStart;
         if (var8 <= 3800L) {
            float var10 = Math.min(1.0F, (float)var8 / 500.0F) * (var8 > 3000L ? 1.0F - (float)(var8 - 3000L) / 800.0F : 1.0F) * var1.lyricsOpacity;
            if (!(var10 <= 0.03F)) {
               double var11 = dist * 0.88;
               double var13 = degPerPx(var0, var1);
               float var15 = (float)(Math.toRadians(var13) * var11);
               TextRenderer var16 = var0.textRenderer;
               String var17 = var4.title.length() > 32 ? var4.title.substring(0, 31) + "…" : var4.title;
               drawFlat(var16, var2, var17, introAz, introEl + 2.0 + (1.0F - Math.min(1.0F, (float)var8 / 700.0F)) * 2.0F, var11, var15 * 1.6F, var3, var10);
               if (!var4.artist.isBlank()) {
                  drawFlat(var16, var2, var4.artist, introAz, introEl - 4.0, var11, var15 * 0.9F, 16777215, var10 * 0.75F);
               }
            }
         }
      }
   }

   private static void drawFlat(
      TextRenderer var0, VertexConsumerProvider var1, String var2, double var3, double var5, double var7, float var9, int var10, float var11
   ) {
      double var12 = Math.toRadians(var3);
      double var14 = Math.toRadians(var5);
      double var16 = -Math.sin(var12) * Math.cos(var14);
      double var18 = Math.sin(var14);
      double var20 = Math.cos(var12) * Math.cos(var14);
      double var22 = -Math.cos(var12);
      double var24 = -Math.sin(var12);
      double var26 = -var24 * var18;
      double var28 = var24 * var16 - var22 * var20;
      double var30 = var22 * var18;
      Matrix4f var32 = new Matrix4f(base)
         .mul(
            new Matrix4f(
               (float)var22 * var9,
               0.0F,
               (float)var24 * var9,
               0.0F,
               (float)(-var26) * var9,
               (float)(-var28) * var9,
               (float)(-var30) * var9,
               0.0F,
               (float)(-var16) * var9,
               (float)(-var18) * var9,
               (float)(-var20) * var9,
               0.0F,
               (float)(var16 * var7),
               (float)(var18 * var7),
               (float)(var20 * var7),
               1.0F
            )
         );
      int var33 = Gfx.w(var2, Gfx.LYRICS);
      var32.translate(-var33 / 2.0F, -4.5F, 0.0F);
      word(var0, var1, var32, var2, 0.0F, 1.0F, 0.0F, Gfx.lerpColor(var10, 656916, 0.7F), var11 * 0.6F, false);
      word(var0, var1, var32, var2, 0.0F, 0.0F, 0.1F, var10, var11, false);
   }

   private static void drawShooting(float var0, float var1) {
      long var2 = System.currentTimeMillis();
      if (var2 > nextShooting) {
         nextShooting = var2 + 1200L + RND.nextInt(3500);
         shooting.add(
            new float[]{camYaw + (RND.nextFloat() - 0.5F) * 80.0F, 30.0F + RND.nextFloat() * 30.0F, (float)(var2 - T0), RND.nextBoolean() ? 1.0F : -1.0F}
         );
      }

      shooting.removeIf(var2x -> var2 - (T0 + (long)var2x[2]) > 900L || var2 < T0 + (long)var2x[2] - 1000L);
      double var4 = skyD * 0.97;

      for (float[] var7 : shooting) {
         float var8 = Anim.clamp((float)(var2 - (T0 + (long)var7[2])) / 900.0F);
         double var9 = var7[0] + var7[3] * var8 * 30.0F;
         double var11 = var7[1] - var8 * 12.0F;
         double var13 = var9 - var7[3] * 6.0F;
         double var15 = var11 + 2.4;
         double[] var17 = at(var9, var11, var4);
         double var18 = var17[0];
         double var20 = var17[1];
         double var22 = var17[2];
         double[] var24 = at(var13, var15, var4);
         float var25 = var0 * (1.0F - var8);
         streak(var24[0], var24[1], var24[2], var18, var20, var22, var4 * 0.004, 16777215, 0.0F, var25);
      }
   }

   private static void drawWeather(VertexConsumer var0, SkyPreset.Weather var1, float var2, float var3, float var4, double var5, Vec3d var7) {
      int var8 = switch (var1) {
         case BLIZZARD -> 240;
         case SNOW -> 200;
         case RAIN, THUNDER -> 220;
         case PETALS -> 150;
         case EMBERS -> 80;
         case FIREFLIES -> 45;
         default -> 0;
      };
      var8 = (int)(var8 * var2 * Q_WEATHER[q()]);
      double var9 = 14.0;
      double var11 = 9.0;
      if (partType != var1 || parts.length != var8) {
         partType = var1;
         parts = new double[var8][11];

         for (double[] var16 : parts) {
            var16[0] = var7.x + (RND.nextDouble() * 2.0 - 1.0) * var9;
            var16[1] = var7.y + (RND.nextDouble() * 2.0 - 1.0) * var11;
            var16[2] = var7.z + (RND.nextDouble() * 2.0 - 1.0) * var9;
            var16[3] = RND.nextDouble() * 6.28;
            var16[4] = 0.6 + RND.nextDouble() * 0.8;
            double var17 = RND.nextGaussian();
            double var19 = RND.nextGaussian() * 0.6;
            double var21 = RND.nextGaussian();
            double var23 = Math.sqrt(var17 * var17 + var19 * var19 + var21 * var21) + 1.0E-9;
            var16[5] = var17 / var23;
            var16[6] = var19 / var23;
            var16[7] = var21 / var23;
            var16[8] = (RND.nextBoolean() ? 1 : -1) * (1.2 + RND.nextDouble() * 2.6);
            var16[9] = 0.75 + RND.nextDouble() * 0.6;
            var16[10] = RND.nextDouble();
         }
      }

      for (double[] var44 : parts) {
         double var45 = var44[3];
         double var46 = var44[4];
         double var25;
         double var47;
         double var48;
         switch (var1) {
            case BLIZZARD:
               var47 = -8.5 * var46;
               var48 = -4.0 * var46;
               var25 = 2.8 * var46;
               break;
            case SNOW:
               var47 = 0.4 + 0.5 * Math.sin(var5 * 0.8 + var45);
               var48 = -1.3 * var46;
               var25 = 0.3 * Math.cos(var5 * 0.7 + var45);
               break;
            case RAIN:
            case THUNDER:
               var47 = 0.6;
               var48 = -17.0 * var46;
               var25 = 0.2;
               break;
            case PETALS:
               var47 = 0.7 + 0.8 * Math.sin(var5 * 0.9 + var45);
               var48 = -0.55 * var46 - 0.25 * Math.sin(var5 * 2.1 + var45 * 2.0);
               var25 = 0.45 * Math.cos(var5 * 0.7 + var45);
               break;
            case EMBERS:
               var47 = 0.3 * Math.sin(var5 * 1.3 + var45);
               var48 = 0.9 * var46;
               var25 = 0.3 * Math.cos(var5 + var45);
               break;
            default:
               var47 = 0.4 * Math.sin(var5 * 0.5 + var45);
               var48 = 0.25 * Math.sin(var5 * 0.7 + var45 * 2.0);
               var25 = 0.4 * Math.cos(var5 * 0.45 + var45);
         }

         var44[0] += var47 * var4;
         var44[1] += var48 * var4;
         var44[2] += var25 * var4;
         double var27 = wrap(var44[0] - var7.x, var9);
         double var29 = wrap(var44[1] - var7.y, var11);
         double var31 = wrap(var44[2] - var7.z, var9);
         var44[0] = var7.x + var27;
         var44[1] = var7.y + var29;
         var44[2] = var7.z + var31;
         if (!(var27 * fx + var29 * fy + var31 * fz < 0.3)) {
            double var33 = Math.sqrt(var27 * var27 + var29 * var29 + var31 * var31);
            float var35 = (float)Math.min(1.0, 5.0 / Math.max(0.5, var33));
            float var36 = (float)(1.0 - Math.max(0.0, Math.min(1.0, (var33 - 9.0) / 6.0)));
            switch (var1) {
               case BLIZZARD:
                  spriteStreak(
                     var0,
                     var27 - var47 * 0.06,
                     var29 - var48 * 0.06,
                     var31 - var25 * 0.06,
                     var27,
                     var29,
                     var31,
                     0.07,
                     16052991,
                     var3 * var36 * (0.45F + 0.4F * var35)
                  );
                  break;
               case SNOW:
                  sprite(var0, var27, var29, var31, 0.11 * var44[9], 1, 16777215, var3 * var36 * (0.55F + 0.4F * var35));
                  break;
               case RAIN:
               case THUNDER:
                  spriteStreak(
                     var0,
                     var27 - var47 * 0.035,
                     var29 - var48 * 0.035,
                     var31 - var25 * 0.035,
                     var27,
                     var29,
                     var31,
                     0.05,
                     12767468,
                     var3 * var36 * (0.3F + 0.35F * var35)
                  );
                  break;
               case PETALS:
                  double var50 = var44[3] + var5 * var44[8];
                  int var39 = Gfx.lerpColor(16777215, 16765670, (float)var44[10]);
                  petal(var0, var27, var29, var31, 0.16 * var44[9], var44[5], var44[6], var44[7], var50, var39, var3 * var36 * 0.95F);
                  break;
               case EMBERS:
                  float var49 = (float)(0.5 + 0.5 * Math.sin(var5 * 9.0 + var45 * 3.0));
                  sprite(var0, var27, var29, var31, 0.22 * var44[9], 2, Gfx.lerpColor(16726815, 16760906, var49), var3 * var36 * (0.5F + 0.5F * var49));
                  break;
               default:
                  float var37 = (float)Math.pow(0.5 + 0.5 * Math.sin(var5 * 2.2 + var45 * 4.0), 2.0);
                  sprite(var0, var27, var29, var31, 0.6, 2, 14090090, var3 * var36 * (0.15F + 0.85F * var37));
            }
         }
      }
   }

   private static void tv(VertexConsumer var0, double var1, double var3, double var5, float var7, float var8, int var9) {
      var0.vertex(base, (float)var1, (float)var3, (float)var5)
         .color(var9)
         .texture(var7, var8)
         .overlay(OverlayTexture.DEFAULT_UV)
         .light(15728880)
         .normal(0.0F, 1.0F, 0.0F);
   }

   private static void tquad(
      VertexConsumer var0, double[] var1, double[] var2, double[] var3, double[] var4, float var5, float var6, float var7, float var8, int var9
   ) {
      double var10 = var2[0] - var1[0];
      double var12 = var2[1] - var1[1];
      double var14 = var2[2] - var1[2];
      double var16 = var3[0] - var1[0];
      double var18 = var3[1] - var1[1];
      double var20 = var3[2] - var1[2];
      double var22 = var12 * var20 - var14 * var18;
      double var24 = var14 * var16 - var10 * var20;
      double var26 = var10 * var18 - var12 * var16;
      if (var22 * var1[0] + var24 * var1[1] + var26 * var1[2] < 0.0) {
         tv(var0, var1[0], var1[1], var1[2], var5, var8, var9);
         tv(var0, var2[0], var2[1], var2[2], var7, var8, var9);
         tv(var0, var3[0], var3[1], var3[2], var7, var6, var9);
         tv(var0, var4[0], var4[1], var4[2], var5, var6, var9);
      } else {
         tv(var0, var4[0], var4[1], var4[2], var5, var6, var9);
         tv(var0, var3[0], var3[1], var3[2], var7, var6, var9);
         tv(var0, var2[0], var2[1], var2[2], var7, var8, var9);
         tv(var0, var1[0], var1[1], var1[2], var5, var8, var9);
      }
   }

   private static void sprite(VertexConsumer var0, double var1, double var3, double var5, double var7, int var9, int var10, float var11) {
      if (!(var11 <= 0.02F)) {
         double var12 = var7 / 2.0;
         int var14 = Gfx.argbRaw(var10, var11);
         float var15 = var9 * 0.25F;
         float var16 = var15 + 0.25F;
         tquad(
            var0,
            p(var1 - rx * var12 - ux * var12, var3 - ry * var12 - uy * var12, var5 - rz * var12 - uz * var12),
            p(var1 + rx * var12 - ux * var12, var3 + ry * var12 - uy * var12, var5 + rz * var12 - uz * var12),
            p(var1 + rx * var12 + ux * var12, var3 + ry * var12 + uy * var12, var5 + rz * var12 + uz * var12),
            p(var1 - rx * var12 + ux * var12, var3 - ry * var12 + uy * var12, var5 - rz * var12 + uz * var12),
            var15,
            0.0F,
            var16,
            1.0F,
            var14
         );
      }
   }

   private static void spriteStreak(
      VertexConsumer var0, double var1, double var3, double var5, double var7, double var9, double var11, double var13, int var15, float var16
   ) {
      if (!(var16 <= 0.02F)) {
         double var17 = var7 - var1;
         double var19 = var9 - var3;
         double var21 = var11 - var5;
         double var23 = (var1 + var7) / 2.0;
         double var25 = (var3 + var9) / 2.0;
         double var27 = (var5 + var11) / 2.0;
         double var29 = var19 * var27 - var21 * var25;
         double var31 = var21 * var23 - var17 * var27;
         double var33 = var17 * var25 - var19 * var23;
         double var35 = Math.sqrt(var29 * var29 + var31 * var31 + var33 * var33);
         if (!(var35 < 1.0E-6)) {
            double var37 = var13 / 2.0 / var35;
            var29 *= var37;
            var31 *= var37;
            var33 *= var37;
            int var39 = Gfx.argbRaw(var15, var16);
            tquad(
               var0,
               p(var7 - var29, var9 - var31, var11 - var33),
               p(var7 + var29, var9 + var31, var11 + var33),
               p(var1 + var29, var3 + var31, var5 + var33),
               p(var1 - var29, var3 - var31, var5 - var33),
               0.75F,
               0.0F,
               1.0F,
               1.0F,
               var39
            );
         }
      }
   }

   private static void petal(
      VertexConsumer var0, double var1, double var3, double var5, double var7, double var9, double var11, double var13, double var15, int var17, float var18
   ) {
      if (!(var18 <= 0.02F)) {
         double var19 = Math.cos(var15);
         double var21 = Math.sin(var15);
         double var23 = 1.0 - var19;
         double var25 = var19 + var9 * var9 * var23;
         double var27 = var11 * var9 * var23 + var13 * var21;
         double var29 = var13 * var9 * var23 - var11 * var21;
         double var31 = var9 * var13 * var23 + var11 * var21;
         double var33 = var11 * var13 * var23 - var9 * var21;
         double var35 = var19 + var13 * var13 * var23;
         double var37 = var7 / 2.0;
         int var39 = Gfx.argbRaw(var17, var18);
         tquad(
            var0,
            p(var1 - var25 * var37 - var31 * var37, var3 - var27 * var37 - var33 * var37, var5 - var29 * var37 - var35 * var37),
            p(var1 + var25 * var37 - var31 * var37, var3 + var27 * var37 - var33 * var37, var5 + var29 * var37 - var35 * var37),
            p(var1 + var25 * var37 + var31 * var37, var3 + var27 * var37 + var33 * var37, var5 + var29 * var37 + var35 * var37),
            p(var1 - var25 * var37 + var31 * var37, var3 - var27 * var37 + var33 * var37, var5 - var29 * var37 + var35 * var37),
            0.0F,
            0.0F,
            0.25F,
            1.0F,
            var39
         );
      }
   }

   private static void texDome(VertexConsumer var0, float var1, float var2, float var3, double var4) {
      texDome(var0, var1, 1.0F, texDist, var2, var3, var4);
   }

   private static void texDome(VertexConsumer var0, float var1, float var2, double var3, float var5, float var6, double var7) {
      if (!(var1 <= 0.01F) && !(var2 <= 0.01F)) {
         int var9 = Math.round(Math.min(1.0F, var1) * 255.0F);
         int var10 = Math.round(Math.min(1.0F, var2) * 255.0F) << 24 | var9 << 16 | var9 << 8 | var9;
         int var11 = q() == 0 ? 12 : 8;

         for (int var12 = 0; var12 < 360; var12 += var11) {
            double var13 = Math.toRadians(var12 + var11 / 2.0);
            if (!(-Math.sin(var13) * fx + Math.cos(var13) * fz < -0.55) || !(fy > -0.6) || !(fy < 0.6)) {
               for (int var15 = 0; var15 < TEX_EL.length - 1; var15++) {
                  texQuad(var0, var12, var12 + var11, TEX_EL[var15], TEX_EL[var15 + 1], var3, var5, var6, var7, var10);
               }
            }
         }
      }
   }

   private static void texQuad(
      VertexConsumer var0, double var1, double var3, double var5, double var7, double var9, float var11, float var12, double var13, int var15
   ) {
      double var16 = var1 / 360.0 + var11;
      var16 -= Math.floor(var16);
      double var18 = var16 + (var3 - var1) / 360.0;
      if (var18 > 1.0000001) {
         double var20 = var1 + (1.0 - var16) * 360.0;
         texPart(var0, var1, var20, var16, 1.0, var5, var7, var9, var12, var13, var15);
         texPart(var0, var20, var3, 0.0, var18 - 1.0, var5, var7, var9, var12, var13, var15);
      } else {
         texPart(var0, var1, var3, var16, var18, var5, var7, var9, var12, var13, var15);
      }
   }

   private static void texPart(
      VertexConsumer var0, double var1, double var3, double var5, double var7, double var9, double var11, double var13, float var15, double var16, int var18
   ) {
      float var19 = (float)((90.0 - var11) / 98.0);
      float var20 = (float)((90.0 - var9) / 98.0);
      double var21 = waveAt(var1, var9, var16) * var15;
      double var23 = waveAt(var3, var9, var16) * var15;
      double var25 = waveAt(var3, var11, var16) * var15;
      double var27 = waveAt(var1, var11, var16) * var15;
      double[] var29 = at(var1, var9 + var21, var13);
      double var30 = var29[0];
      double var32 = var29[1];
      double var34 = var29[2];
      double[] var36 = at(var3, var9 + var23, var13);
      double var37 = var36[0];
      double var39 = var36[1];
      double var41 = var36[2];
      double[] var43 = at(var3, var11 + var25, var13);
      double var44 = var43[0];
      double var46 = var43[1];
      double var48 = var43[2];
      double[] var50 = at(var1, var11 + var27, var13);
      tquad(var0, p(var30, var32, var34), p(var37, var39, var41), p(var44, var46, var48), var50, (float)var5, var19, (float)var7, var20, var18);
   }

   private static double waveAt(double var0, double var2, double var4) {
      double var6 = Math.toRadians(var0);
      double var8 = Math.max(0.0, Math.min(1.0, (var2 - 2.0) / 10.0)) * Math.max(0.0, Math.min(1.0, (80.0 - var2) / 20.0));
      return (Math.sin(var6 * 5.0 + var4 * 0.55) * 0.6 + Math.sin(var6 * 11.0 - var4 * 0.8) * 0.4) * var8;
   }

   private static double wrap(double var0, double var2) {
      double var4 = var2 * 2.0;
      var0 = (var0 + var2) % var4;
      if (var0 < 0.0) {
         var0 += var4;
      }

      return var0 - var2;
   }

   private static int mIdx(int var0, int var1) {
      return var1 * meshNA + Math.floorMod(var0, meshNA);
   }

   private static int samplePano(int[] var0, double var1, double var3) {
      short var5 = 2048;
      short var6 = 512;
      double var7 = var1 / 360.0 * var5 - 0.5;
      double var9 = (90.0 - var3) / 98.0 * var6 - 0.5;
      var7 -= Math.floor(var7 / var5) * var5;
      if (var9 < 0.0) {
         var9 = 0.0;
      }

      if (var9 > var6 - 1) {
         var9 = var6 - 1;
      }

      int var11 = (int)var7;
      int var12 = (int)var9;
      int var13 = (var11 + 1) % var5;
      int var14 = Math.min(var6 - 1, var12 + 1);
      float var15 = (float)(var7 - var11);
      float var16 = (float)(var9 - var12);
      int var17 = var0[var12 * var5 + var11];
      int var18 = var0[var12 * var5 + var13];
      int var19 = var0[var14 * var5 + var11];
      int var20 = var0[var14 * var5 + var13];
      int var21 = Math.round(bil(var17 >> 16 & 0xFF, var18 >> 16 & 0xFF, var19 >> 16 & 0xFF, var20 >> 16 & 0xFF, var15, var16));
      int var22 = Math.round(bil(var17 >> 8 & 0xFF, var18 >> 8 & 0xFF, var19 >> 8 & 0xFF, var20 >> 8 & 0xFF, var15, var16));
      int var23 = Math.round(bil(var17 & 0xFF, var18 & 0xFF, var19 & 0xFF, var20 & 0xFF, var15, var16));
      return var21 << 16 | var22 << 8 | var23;
   }

   private static float bil(int var0, int var1, int var2, int var3, float var4, float var5) {
      float var6 = var0 + (var1 - var0) * var4;
      float var7 = var2 + (var3 - var2) * var4;
      return var6 + (var7 - var6) * var5;
   }

   private static float lum(int var0) {
      return ((var0 >> 16 & 0xFF) * 0.3F + (var0 >> 8 & 0xFF) * 0.55F + (var0 & 0xFF) * 0.15F) / 255.0F;
   }

   private static void buildMesh(double var0) {
      int[] var2 = SkyTextures.base();
      meshStep = var0;
      meshVer = SkyTextures.version();
      meshNA = (int)Math.round(360.0 / var0);
      meshNE = (int)Math.ceil(94.0 / var0);
      int var3 = meshNA * (meshNE + 1);
      mX = new float[var3];
      mY = new float[var3];
      mZ = new float[var3];
      mAz = new float[var3];
      mEl = new float[var3];
      mBase = new int[var3];
      mFrame = new int[var3];
      mStamp = new int[var3];

      for (int var4 = 0; var4 <= meshNE; var4++) {
         float var5 = -4.0F + var4 * 94.0F / meshNE;
         double var6 = Math.toRadians(var5);
         double var8 = Math.cos(var6);

         for (int var10 = 0; var10 < meshNA; var10++) {
            float var11 = var10 * 360.0F / meshNA;
            double var12 = Math.toRadians(var11);
            int var14 = var4 * meshNA + var10;
            mX[var14] = (float)(-Math.sin(var12) * var8);
            mY[var14] = (float)Math.sin(var6);
            mZ[var14] = (float)(Math.cos(var12) * var8);
            mAz[var14] = var11;
            mEl[var14] = var5;
            mBase[var14] = samplePano(var2, var11, var5);
         }
      }

      mDetail = new byte[meshNA * meshNE];

      for (int var15 = 0; var15 < meshNE; var15++) {
         for (int var16 = 0; var16 < meshNA; var16++) {
            int var17 = mIdx(var16, var15);
            float var7 = 9.0F;
            float var18 = -9.0F;
            float var9 = mAz[var17];
            float var19 = mEl[var17];

            for (int var20 = 0; var20 <= 1; var20++) {
               for (int var21 = 0; var21 <= 1; var21++) {
                  float var13 = lum(samplePano(var2, var9 + (var21 + 0.5) * var0 / 2.0, var19 + (var20 + 0.5) * var0 / 2.0));
                  var7 = Math.min(var7, var13);
                  var18 = Math.max(var18, var13);
               }
            }

            mDetail[var15 * meshNA + var16] = (byte)(var18 - var7 > 0.1F ? 1 : 0);
         }
      }
   }

   private static float horizonA(float var0) {
      float var1 = Anim.clamp((var0 + 3.0F) / 6.0F);
      return var1 * var1 * (3.0F - 2.0F * var1);
   }

   private static int meshColor(int var0, double var1, double var3, float var5, int[] var6, float var7, double var8) {
      int var10 = var0 >> 16 & 0xFF;
      int var11 = var0 >> 8 & 0xFF;
      int var12 = var0 & 0xFF;
      if (var5 < 0.999F) {
         var10 = Math.round(var10 * var5);
         var11 = Math.round(var11 * var5);
         var12 = Math.round(var12 * var5);
      }

      if (var6 != null && var7 > 0.01F) {
         int var13 = samplePano(var6, var1 - var8, var3);
         var10 = Math.min(255, var10 + Math.round((var13 >> 16 & 0xFF) * var7));
         var11 = Math.min(255, var11 + Math.round((var13 >> 8 & 0xFF) * var7));
         var12 = Math.min(255, var12 + Math.round((var13 & 0xFF) * var7));
      }

      return var10 << 16 | var11 << 8 | var12;
   }

   private static int vertColor(int var0, float var1, int[] var2, float var3, double var4) {
      if (mStamp[var0] == frameNo) {
         return mFrame[var0];
      }

      int var6 = meshColor(mBase[var0], mAz[var0], mEl[var0], var1, var2, var3, var4);
      mFrame[var0] = var6;
      mStamp[var0] = frameNo;
      return var6;
   }

   private static void cquad(
      double[] var0, double[] var1, double[] var2, double[] var3, int var4, float var5, int var6, float var7, int var8, float var9, int var10, float var11
   ) {
      double var12 = var1[0] - var0[0];
      double var14 = var1[1] - var0[1];
      double var16 = var1[2] - var0[2];
      double var18 = var2[0] - var0[0];
      double var20 = var2[1] - var0[1];
      double var22 = var2[2] - var0[2];
      double var24 = var14 * var22 - var16 * var20;
      double var26 = var16 * var18 - var12 * var22;
      double var28 = var12 * var20 - var14 * var18;
      if (var24 * var0[0] + var26 * var0[1] + var28 * var0[2] < 0.0) {
         v(var0[0], var0[1], var0[2], var4, var5);
         v(var1[0], var1[1], var1[2], var6, var7);
         v(var2[0], var2[1], var2[2], var8, var9);
         v(var3[0], var3[1], var3[2], var10, var11);
      } else {
         v(var3[0], var3[1], var3[2], var10, var11);
         v(var2[0], var2[1], var2[2], var8, var9);
         v(var1[0], var1[1], var1[2], var6, var7);
         v(var0[0], var0[1], var0[2], var4, var5);
      }
   }

   private static void drawSkyMesh(float var0, float var1, float var2, double var3, double var5, MinecraftClient var7) {
      if (!(var0 <= 0.01F)) {
         double var8 = q() != 0 && AutoPerf.allow(3) ? (q() == 1 ? 1.25 : 1.0) : 2.0;
         if (meshVer != SkyTextures.version() || var8 != meshStep || mX == null) {
            buildMesh(var8);
         }

         int[] var10 = var2 > 0.01F ? SkyTextures.fx() : null;
         int[] var11 = SkyTextures.base();
         frameNo++;
         double var12 = 70.0;

         try {
            var12 = ((Integer)var7.options.getFov().getValue()).intValue();
         } catch (Throwable var82) {
         }

         double var14 = (double)var7.getWindow().getFramebufferWidth() / Math.max(1, var7.getWindow().getFramebufferHeight());
         double var16 = Math.toDegrees(Math.atan(Math.tan(Math.toRadians(var12 / 2.0)) * Math.sqrt(1.0 + var14 * var14)));
         double var18 = Math.toRadians(Math.min(95.0, var16 + 4.0 + var8));
         double var20 = Math.cos(var18);
         boolean var22 = q() > 0 && AutoPerf.allow(2);
         float var23 = (float)var5;
         int var24 = q() >= 2 ? 1800 : 900;

         for (int var25 = 0; var25 < meshNE; var25++) {
            for (int var26 = 0; var26 < meshNA; var26++) {
               int var27 = mIdx(var26, var25);
               int var28 = mIdx(var26 + 1, var25);
               int var29 = mIdx(var26 + 1, var25 + 1);
               int var30 = mIdx(var26, var25 + 1);
               double var31 = mX[var27] + mX[var28] + mX[var29] + mX[var30];
               double var33 = mY[var27] + mY[var28] + mY[var29] + mY[var30];
               double var35 = mZ[var27] + mZ[var28] + mZ[var29] + mZ[var30];
               double var37 = Math.sqrt(var31 * var31 + var33 * var33 + var35 * var35);
               if (!(var37 < 1.0E-6) && !((var31 * fx + var33 * fy + var35 * fz) / var37 < var20)) {
                  float var39 = var0 * horizonA(mEl[var27]);
                  float var40 = var0 * horizonA(mEl[var30]);
                  if (!(var39 <= 0.003F) || !(var40 <= 0.003F)) {
                     if (var22 && var24 > 0 && mDetail[var25 * meshNA + Math.floorMod(var26, meshNA)] != 0) {
                        var24--;
                        float var83 = mAz[var27];
                        float var84 = mEl[var27];
                        float var85 = (float)(meshStep / 2.0);

                        for (int var86 = 0; var86 < 2; var86++) {
                           for (int var45 = 0; var45 < 2; var45++) {
                              double var46 = var83 + var45 * var85;
                              double var48 = var46 + var85;
                              double var50 = var84 + var86 * var85;
                              double var52 = var50 + var85;
                              int var54 = meshColor(samplePano(var11, var46, var50), var46, var50, var1, var10, var2, var3);
                              int var55 = meshColor(samplePano(var11, var48, var50), var48, var50, var1, var10, var2, var3);
                              int var56 = meshColor(samplePano(var11, var48, var52), var48, var52, var1, var10, var2, var3);
                              int var57 = meshColor(samplePano(var11, var46, var52), var46, var52, var1, var10, var2, var3);
                              float var58 = var0 * horizonA((float)var50);
                              float var59 = var0 * horizonA((float)var52);
                              double[] var60 = at(var46, var50, var23);
                              double var61 = var60[0];
                              double var63 = var60[1];
                              double var65 = var60[2];
                              double[] var67 = at(var48, var50, var23);
                              double var68 = var67[0];
                              double var70 = var67[1];
                              double var72 = var67[2];
                              double[] var74 = at(var48, var52, var23);
                              double var75 = var74[0];
                              double var77 = var74[1];
                              double var79 = var74[2];
                              double[] var81 = at(var46, var52, var23);
                              cquad(
                                 p(var61, var63, var65),
                                 p(var68, var70, var72),
                                 p(var75, var77, var79),
                                 var81,
                                 var54,
                                 var58,
                                 var55,
                                 var58,
                                 var56,
                                 var59,
                                 var57,
                                 var59
                              );
                           }
                        }
                     } else {
                        int var41 = vertColor(var27, var1, var10, var2, var3);
                        int var42 = vertColor(var28, var1, var10, var2, var3);
                        int var43 = vertColor(var29, var1, var10, var2, var3);
                        int var44 = vertColor(var30, var1, var10, var2, var3);
                        cquad(
                           p(mX[var27] * var5, mY[var27] * var5, mZ[var27] * var5),
                           p(mX[var28] * var5, mY[var28] * var5, mZ[var28] * var5),
                           p(mX[var29] * var5, mY[var29] * var5, mZ[var29] * var5),
                           p(mX[var30] * var5, mY[var30] * var5, mZ[var30] * var5),
                           var41,
                           var39,
                           var42,
                           var39,
                           var43,
                           var40,
                           var44,
                           var40
                        );
                     }
                  }
               }
            }
         }
      }
   }

   private static float liveFade(SkyPreset var0) {
      if (var0 != lastLive) {
         lastLive = var0;
         liveSince = System.currentTimeMillis();
      }

      return Anim.outCubic(Anim.since(liveSince, 900L));
   }

   private static void buildLive(double var0) {
      liveStep = var0;
      liveNA = (int)Math.round(360.0 / var0);
      liveNE = (int)Math.ceil(94.0 / var0);
      int var2 = liveNA * (liveNE + 1);
      lX = new float[var2];
      lY = new float[var2];
      lZ = new float[var2];
      lEl = new float[var2];
      lCol = new int[var2];
      lStamp = new int[var2];

      for (int var3 = 0; var3 <= liveNE; var3++) {
         float var4 = -4.0F + var3 * 94.0F / liveNE;
         double var5 = Math.toRadians(var4);
         double var7 = Math.cos(var5);

         for (int var9 = 0; var9 < liveNA; var9++) {
            double var10 = Math.toRadians(var9 * 360.0 / liveNA);
            int var12 = var3 * liveNA + var9;
            lX[var12] = (float)(-Math.sin(var10) * var7);
            lY[var12] = (float)Math.sin(var5);
            lZ[var12] = (float)(Math.cos(var10) * var7);
            lEl[var12] = var4;
         }
      }
   }

   private static int liveCol(int var0, float var1) {
      if (lStamp[var0] == frameNo) {
         return lCol[var0];
      }

      int var2 = LiveSky.MAIN.color(lX[var0], lY[var0], lZ[var0], lEl[var0]);
      if (var1 < 0.999F || var1 > 1.001F) {
         int var3 = Math.min(255, Math.round((var2 >> 16 & 0xFF) * var1));
         int var4 = Math.min(255, Math.round((var2 >> 8 & 0xFF) * var1));
         int var5 = Math.min(255, Math.round((var2 & 0xFF) * var1));
         var2 = var3 << 16 | var4 << 8 | var5;
      }

      lCol[var0] = var2;
      lStamp[var0] = frameNo;
      return var2;
   }

   private static void drawLiveSky(float var0, float var1, MinecraftClient var2) {
      if (!(var0 <= 0.01F)) {
         double var3 = q() != 0 && AutoPerf.allow(3) ? (q() == 1 ? 2.0 : 1.5) : 3.0;
         if (lX == null || var3 != liveStep) {
            buildLive(var3);
         }

         frameNo++;
         double var5 = 70.0;

         try {
            var5 = ((Integer)var2.options.getFov().getValue()).intValue();
         } catch (Throwable var39) {
         }

         double var7 = (double)var2.getWindow().getFramebufferWidth() / Math.max(1, var2.getWindow().getFramebufferHeight());
         double var9 = Math.toDegrees(Math.atan(Math.tan(Math.toRadians(var5 / 2.0)) * Math.sqrt(1.0 + var7 * var7)));
         double var11 = Math.cos(Math.toRadians(Math.min(95.0, var9 + 4.0 + var3)));
         double var13 = skyD;
         int var15 = q() >= 2 ? 14000 : (q() == 1 ? 7000 : 3000);
         if (!AutoPerf.allow(5)) {
            var15 = 0;
         }

         for (int var16 = 0; var16 < liveNE; var16++) {
            float var17 = var0 * horizonA(lEl[var16 * liveNA]);
            float var18 = var0 * horizonA(lEl[(var16 + 1) * liveNA]);
            if (!(var17 <= 0.003F) || !(var18 <= 0.003F)) {
               for (int var19 = 0; var19 < liveNA; var19++) {
                  int var20 = var16 * liveNA + var19;
                  int var21 = var16 * liveNA + (var19 + 1) % liveNA;
                  int var22 = var20 + liveNA;
                  int var23 = var21 + liveNA;
                  double var24 = lX[var20] + lX[var21] + lX[var23] + lX[var22];
                  double var26 = lY[var20] + lY[var21] + lY[var23] + lY[var22];
                  double var28 = lZ[var20] + lZ[var21] + lZ[var23] + lZ[var22];
                  double var30 = Math.sqrt(var24 * var24 + var26 * var26 + var28 * var28);
                  if (!(var30 < 1.0E-6) && !((var24 * fx + var26 * fy + var28 * fz) / var30 < var11)) {
                     int var32 = liveCol(var20, var1);
                     int var33 = liveCol(var21, var1);
                     int var34 = liveCol(var23, var1);
                     int var35 = liveCol(var22, var1);
                     if (var15 > 0) {
                        float var36 = Math.min(Math.min(lum(var32), lum(var33)), Math.min(lum(var35), lum(var34)));
                        float var37 = Math.max(Math.max(lum(var32), lum(var33)), Math.max(lum(var35), lum(var34)));
                        int var38 = var37 - var36 > 0.3F ? 3 : (var37 - var36 > 0.1F ? 2 : 1);
                        if (var38 > 1) {
                           var15 -= (var38 + 1) * (var38 + 1) - 4;
                           liveSub(var19, var16, var38, var32, var33, var34, var35, var0, var1, var13);
                           continue;
                        }
                     }

                     cquad(
                        p(lX[var20] * var13, lY[var20] * var13, lZ[var20] * var13),
                        p(lX[var21] * var13, lY[var21] * var13, lZ[var21] * var13),
                        p(lX[var23] * var13, lY[var23] * var13, lZ[var23] * var13),
                        p(lX[var22] * var13, lY[var22] * var13, lZ[var22] * var13),
                        var32,
                        var17,
                        var33,
                        var17,
                        var34,
                        var18,
                        var35,
                        var18
                     );
                  }
               }
            }
         }
      }
   }

   public static boolean tintFog(Vector4f var0) {
      if (fogMix <= 0.01F) {
         return false;
      }

      var0.x = var0.x + (fogR - var0.x) * fogMix;
      var0.y = var0.y + (fogG - var0.y) * fogMix;
      var0.z = var0.z + (fogB - var0.z) * fogMix;
      return true;
   }

   private static void updateFog(float var0, float var1) {
      float var2 = 0.0F;
      float var3 = 0.0F;
      float var4 = 0.0F;

      for (int var5 = -2; var5 <= 2; var5++) {
         double[] var6 = dir(camYaw + var5 * 35, 1.5);
         int var7 = LiveSky.MAIN.color(var6[0], var6[1], var6[2], 1.5F);
         var2 += var7 >> 16 & 0xFF;
         var3 += var7 >> 8 & 0xFF;
         var4 += var7 & 0xFF;
      }

      fogR = Math.min(1.0F, var2 / 5.0F / 255.0F * var1);
      fogG = Math.min(1.0F, var3 / 5.0F / 255.0F * var1);
      fogB = Math.min(1.0F, var4 / 5.0F / 255.0F * var1);
      float var8 = (fogR + fogG + fogB) / 3.0F;
      fogR = fogR + (var8 - fogR) * 0.45F;
      fogG = fogG + (var8 - fogG) * 0.45F;
      fogB = fogB + (var8 - fogB) * 0.45F;
      int var9 = MinecraftClient.getInstance().options.getClampedViewDistance();
      float var10 = Math.max(0.2F, Math.min(0.55F, (var9 - 3) / 14.0F));
      fogMix = Math.max(0.0F, Math.min(var10, var0 * var10));
   }

   private static RenderLayer panoType(String var0) {
      return PANO.computeIfAbsent(var0, var0x -> noFog(Identifier.of("neonlyrics", "textures/sky/pano/" + var0x + ".png")));
   }

   public static RenderLayer noFog(Identifier var0) {
      if (!noFogBroken) {
         RenderLayer var1 = LiveSkyShader.noFogType(var0);
         if (var1 != null) {
            return var1;
         }

         noFogBroken = true;
      }

      return RenderLayers.text(var0);
   }

   public static double panoDist() {
      return noFogBroken ? texDist : skyD * 0.99;
   }

   private static void drawImageSky(RenderLayer var0, float var1, VertexConsumerProvider var2, MinecraftClient var3) {
      if (!(var1 <= 0.01F) && var2 instanceof Immediate var4) {
         byte var5 = 72;
         byte var6 = 18;
         double var7 = panoDist();
         VertexConsumer var9 = var2.getBuffer(var0);

         for (int var10 = 0; var10 < var6; var10++) {
            double var11 = var10 * 90.0 / var6;
            double var13 = (var10 + 1) * 90.0 / var6;
            float var15 = (float)Math.min(1.0, var11 / 75.0);
            float var16 = (float)Math.min(1.0, var13 / 75.0);
            int var17 = Math.round(var1 * 255.0F * (float)Math.min(1.0, var11 / 3.0)) << 24 | 16777215;
            int var18 = Math.round(var1 * 255.0F * (float)Math.min(1.0, var13 / 3.0)) << 24 | 16777215;

            for (int var19 = 0; var19 < var5; var19++) {
               double var20 = var19 * 360.0 / var5;
               double var22 = (var19 + 1) * 360.0 / var5;
               double[] var24 = dir((var20 + var22) / 2.0, (var11 + var13) / 2.0);
               if (!(var24[0] * fx + var24[1] * fy + var24[2] * fz < -0.2)) {
                  float var25 = (float)(var20 <= 180.0 ? var20 / 180.0 : 2.0 - var20 / 180.0);
                  float var26 = (float)(var22 <= 180.0 ? var22 / 180.0 : 2.0 - var22 / 180.0);
                  double[] var27 = dir(var20, var11);
                  double[] var28 = dir(var22, var11);
                  double[] var29 = dir(var22, var13);
                  double[] var30 = dir(var20, var13);

                  for (int var31 = 0; var31 < 2; var31++) {
                     double[][] var32 = var31 == 0 ? new double[][]{var27, var28, var29, var30} : new double[][]{var30, var29, var28, var27};
                     float[][] var33 = var31 == 0
                        ? new float[][]{{var25, var15}, {var26, var15}, {var26, var16}, {var25, var16}}
                        : new float[][]{{var25, var16}, {var26, var16}, {var26, var15}, {var25, var15}};

                     for (int var34 = 0; var34 < 4; var34++) {
                        float var35 = (float)(var32[var34][0] * var7);
                        float var36 = (float)(var32[var34][1] * var7);
                        float var37 = (float)(var32[var34][2] * var7);
                        boolean var38 = var31 == 0 == var34 < 2;
                        var9.vertex(base, var35, var36, var37).color(var38 ? var17 : var18).texture(var33[var34][0], var33[var34][1]).light(15728880);
                     }
                  }
               }
            }
         }

         var4.draw(var0);
      }
   }

   private static void drawPano(SkyPreset var0, float var1, VertexConsumerProvider var2, MinecraftClient var3) {
      if (!(var1 <= 0.01F) && var2 instanceof Immediate var4) {
         String var5 = var0 == SkyPreset.DJ ? "dj_bg" : var0.name().toLowerCase(Locale.ROOT);
         double var6 = 70.0;

         try {
            var6 = ((Integer)var3.options.getFov().getValue()).intValue();
         } catch (Throwable var49) {
         }

         double var8 = (double)var3.getWindow().getFramebufferWidth() / Math.max(1, var3.getWindow().getFramebufferHeight());
         double var10 = Math.toDegrees(Math.atan(Math.tan(Math.toRadians(var6 / 2.0)) * Math.sqrt(1.0 + var8 * var8)));
         double var12 = Math.cos(Math.toRadians(Math.min(100.0, var10 + 14.0)));
         byte var14 = 60;
         byte var15 = 16;
         RenderLayer var16 = panoType(var5);
         double var17 = panoDist();
         VertexConsumer var19 = var2.getBuffer(var16);

         for (int var20 = 0; var20 < var15; var20++) {
            double var21 = var20 * 90.0 / var15;
            double var23 = (var20 + 1) * 90.0 / var15;
            float var25 = (float)((90.0 - var21) / 120.0);
            float var26 = (float)((90.0 - var23) / 120.0);
            int var27 = Math.round(var1 * 255.0F * (float)Math.min(1.0, var21 / 3.0)) << 24 | 16777215;
            int var28 = Math.round(var1 * 255.0F * (float)Math.min(1.0, var23 / 3.0)) << 24 | 16777215;

            for (int var29 = 0; var29 < var14; var29++) {
               double var30 = var29 * 360.0 / var14;
               double var32 = (var29 + 1) * 360.0 / var14;
               double[] var34 = dir((var30 + var32) / 2.0, (var21 + var23) / 2.0);
               if (!(var34[0] * fx + var34[1] * fy + var34[2] * fz < var12)) {
                  float var35 = (float)(var30 / 360.0);
                  float var36 = (float)(var32 / 360.0);
                  double[] var37 = dir(var30, var21);
                  double[] var38 = dir(var32, var21);
                  double[] var39 = dir(var32, var23);
                  double[] var40 = dir(var30, var23);

                  for (int var41 = 0; var41 < 2; var41++) {
                     double[][] var42 = var41 == 0 ? new double[][]{var37, var38, var39, var40} : new double[][]{var40, var39, var38, var37};
                     float[][] var43 = var41 == 0
                        ? new float[][]{{var35, var25}, {var36, var25}, {var36, var26}, {var35, var26}}
                        : new float[][]{{var35, var26}, {var36, var26}, {var36, var25}, {var35, var25}};

                     for (int var44 = 0; var44 < 4; var44++) {
                        float var45 = (float)(var42[var44][0] * var17);
                        float var46 = (float)(var42[var44][1] * var17);
                        float var47 = (float)(var42[var44][2] * var17);
                        boolean var48 = var41 == 0 == var44 < 2;
                        var19.vertex(base, var45, var46, var47).color(var48 ? var27 : var28).texture(var43[var44][0], var43[var44][1]).light(15728880);
                     }
                  }
               }
            }
         }

         var4.draw(var16);
         if (var0 == SkyPreset.DJ) {
            NowPlaying var51 = SpotifyClient.now();
            if (var51.hasTrack() && var51.playing) {
               double var52 = var51.beatPos(var51.progressNow(NeonLyrics.cfg().lyricsOffsetMs));
               double var22 = var52 - Math.floor(var52);
               var17 = Math.floorMod((long)Math.floor(var52), 4) * 6 + var22 * 6.0;
            } else {
               var17 = 0.0;
            }
         } else {
            var17 = skyTime / (var0 == SkyPreset.SPACE_WAR ? 0.45 : 1.6);
         }

         SkyAnim.draw(var4, base, var5, var17, var1, panoDist() * 0.995, fx, fy, fz, var12);
      }
   }

   private static boolean drawLiveShader(
      SkyPreset var0, float var1, float var2, double var3, Config var5, VertexConsumerProvider var6, MinecraftClient var7, int var8, double var9
   ) {
      if (!(var6 instanceof Immediate var11 && LiveSkyShader.ready())) {
         return false;
      } else {
         if (var1 <= 0.01F) {
            return true;
         }

         try {
            var11.draw(RenderLayers.debugQuads());
            LiveSkyShader.params(var0, aurora1(var0), aurora2(var0), var3, var5, base);
            RenderLayer var12 = LiveSkyShader.type(var0);
            VertexConsumer var13 = var6.getBuffer(var12);
            float var14 = (float)(var3 * 2.2);
            int var15 = Math.max(0, Math.min(255, Math.round(var2 / 2.0F * 255.0F)));
            int var16 = Math.max(0, Math.min(255, Math.round(var1 * 255.0F)));
            double var17 = 70.0;

            try {
               var17 = ((Integer)var7.options.getFov().getValue()).intValue();
            } catch (Throwable var57) {
            }

            double var19 = (double)var7.getWindow().getFramebufferWidth() / Math.max(1, var7.getWindow().getFramebufferHeight());
            double var21 = Math.toDegrees(Math.atan(Math.tan(Math.toRadians(var17 / 2.0)) * Math.sqrt(1.0 + var19 * var19)));
            double var23 = Math.cos(Math.toRadians(Math.min(100.0, var21 + 14.0)));
            double var25 = var9;
            int var27 = var8 == 0 ? 0 : (var8 == 1 ? 128 : 255);
            byte var28 = 36;
            byte var29 = 20;

            for (int var30 = 0; var30 < var29; var30++) {
               double var31 = -30.0 + var30 * 120.0 / var29;
               double var33 = -30.0 + (var30 + 1) * 120.0 / var29;

               for (int var35 = 0; var35 < var28; var35++) {
                  double var36 = var35 * 360.0 / var28;
                  double var38 = (var35 + 1) * 360.0 / var28;
                  double[] var40 = dir((var36 + var38) / 2.0, (var31 + var33) / 2.0);
                  if (!(var40[0] * fx + var40[1] * fy + var40[2] * fz < var23)) {
                     double[] var41 = dir(var36, var31);
                     float var42 = (float)(var41[0] * var25);
                     float var43 = (float)(var41[1] * var25);
                     float var44 = (float)(var41[2] * var25);
                     double[] var45 = dir(var38, var31);
                     float var46 = (float)(var45[0] * var25);
                     float var47 = (float)(var45[1] * var25);
                     float var48 = (float)(var45[2] * var25);
                     double[] var49 = dir(var38, var33);
                     float var50 = (float)(var49[0] * var25);
                     float var51 = (float)(var49[1] * var25);
                     float var52 = (float)(var49[2] * var25);
                     double[] var53 = dir(var36, var33);
                     float var54 = (float)(var53[0] * var25);
                     float var55 = (float)(var53[1] * var25);
                     float var56 = (float)(var53[2] * var25);
                     var13.vertex(base, var42, var43, var44).texture(var14, 0.0F).color(var15, var27, 0, var16);
                     var13.vertex(base, var46, var47, var48).texture(var14, 0.0F).color(var15, var27, 0, var16);
                     var13.vertex(base, var50, var51, var52).texture(var14, 0.0F).color(var15, var27, 0, var16);
                     var13.vertex(base, var54, var55, var56).texture(var14, 0.0F).color(var15, var27, 0, var16);
                  }
               }
            }

            var11.draw(var12);
            return true;
         } catch (Throwable var58) {
            LiveSkyShader.disable(var58);
            return false;
         }
      }
   }

   private static void liveSub(int var0, int var1, int var2, int var3, int var4, int var5, int var6, float var7, float var8, double var9) {
      double var11 = var0 * 360.0 / liveNA;
      double var13 = (var0 + 1) * 360.0 / liveNA;
      float var15 = lEl[var1 * liveNA];
      float var16 = lEl[(var1 + 1) * liveNA];

      for (int var17 = 0; var17 <= var2; var17++) {
         float var18 = var15 + (var16 - var15) * var17 / var2;
         double var19 = Math.toRadians(var18);
         double var21 = Math.cos(var19);
         double var23 = Math.sin(var19);

         for (int var25 = 0; var25 <= var2; var25++) {
            int var26 = var17 * (var2 + 1) + var25;
            double var27 = Math.toRadians(var11 + (var13 - var11) * var25 / var2);
            float var29 = (float)(-Math.sin(var27) * var21);
            float var30 = (float)var23;
            float var31 = (float)(Math.cos(var27) * var21);
            SUB_P[var26][0] = var29;
            SUB_P[var26][1] = var30;
            SUB_P[var26][2] = var31;
            int var32;
            if (var25 == 0 && var17 == 0) {
               var32 = var3;
            } else if (var25 == var2 && var17 == 0) {
               var32 = var4;
            } else if (var25 == var2 && var17 == var2) {
               var32 = var5;
            } else if (var25 == 0 && var17 == var2) {
               var32 = var6;
            } else {
               var32 = LiveSky.MAIN.color(var29, var30, var31, var18);
               if (var8 < 0.999F || var8 > 1.001F) {
                  int var33 = Math.min(255, Math.round((var32 >> 16 & 0xFF) * var8));
                  int var34 = Math.min(255, Math.round((var32 >> 8 & 0xFF) * var8));
                  int var35 = Math.min(255, Math.round((var32 & 0xFF) * var8));
                  var32 = var33 << 16 | var34 << 8 | var35;
               }
            }

            SUB_COL[var26] = var32;
         }
      }

      for (int var36 = 0; var36 < var2; var36++) {
         float var37 = var7 * horizonA(var15 + (var16 - var15) * var36 / var2);
         float var38 = var7 * horizonA(var15 + (var16 - var15) * (var36 + 1) / var2);

         for (int var20 = 0; var20 < var2; var20++) {
            int var39 = var36 * (var2 + 1) + var20;
            int var22 = var39 + 1;
            int var40 = var39 + var2 + 1;
            int var24 = var40 + 1;
            cquad(
               p(SUB_P[var39][0] * var9, SUB_P[var39][1] * var9, SUB_P[var39][2] * var9),
               p(SUB_P[var22][0] * var9, SUB_P[var22][1] * var9, SUB_P[var22][2] * var9),
               p(SUB_P[var24][0] * var9, SUB_P[var24][1] * var9, SUB_P[var24][2] * var9),
               p(SUB_P[var40][0] * var9, SUB_P[var40][1] * var9, SUB_P[var40][2] * var9),
               SUB_COL[var39],
               var37,
               SUB_COL[var22],
               var37,
               SUB_COL[var24],
               var38,
               SUB_COL[var40],
               var38
            );
         }
      }
   }

   private static void drawBody(SkyPreset var0, float var1, double var2) {
      LiveSky.Body var4 = LiveSky.body(var0);
      if (var4 != null && !(var1 <= 0.01F)) {
         double var5 = skyD * 0.995;
         double var7 = Math.toRadians(var4.az());
         double var9 = Math.toRadians(var4.el());
         double var11 = -Math.sin(var7) * Math.cos(var9);
         double var13 = Math.sin(var9);
         double var15 = Math.cos(var7) * Math.cos(var9);
         if (!(var11 * fx + var13 * fy + var15 * fz < 0.2)) {
            double var17 = Math.cos(var7);
            double var19 = 0.0;
            double var21 = Math.sin(var7);
            double var23 = var13 * var21 - var15 * var19;
            double var25 = var15 * var17 - var11 * var21;
            double var27 = var11 * var19 - var13 * var17;
            double[] var29 = new double[]{var11, var13, var15, var17, var19, var21, var23, var25, var27};
            double var30 = Math.toRadians(var4.radius());
            int var32 = var4.color();
            if (var4.kind() == 0) {
               bodyRing(var29, var5, 0.0, var30 * 0.8, 16777215, var1, Gfx.lerpColor(var32, 16777215, 0.6F), var1, 40, 0.0, var2);
               bodyRing(var29, var5, var30 * 0.8, var30, Gfx.lerpColor(var32, 16777215, 0.6F), var1, var32, var1, 40, 0.0, var2);
               bodyRing(var29, var5, var30, var30 * 1.25, var32, var1, var32, 0.0F, 40, 0.0, var2);
               bodyRing(var29, var5, var30 * 1.25, var30 * 3.2, var32, var1 * 0.3F, var32, 0.0F, 40, 0.0, var2);
            } else if (var4.kind() == 1) {
               int var33 = Gfx.lerpColor(var32, 16777215, 0.35F);
               int var34 = Gfx.lerpColor(var32, 0, 0.18F);
               bodyRing(var29, var5, 0.0, var30 * 0.7, var33, var1, var32, var1, 40, 0.0, var2);
               bodyRing(var29, var5, var30 * 0.7, var30, var32, var1, var34, var1, 40, 0.0, var2);
               bodyRing(var29, var5, var30, var30 * 1.08, var34, var1, var34, 0.0F, 40, 0.0, var2);
               bodyRing(var29, var5, var30 * 1.08, var30 * 3.4, var32, var1 * 0.16F, var32, 0.0F, 40, 0.0, var2);
               int var35 = Gfx.lerpColor(var32, 0, 0.22F);
               double[][] var36 = new double[][]{{-0.3, 0.25, 0.28}, {0.28, 0.05, 0.22}, {0.05, -0.35, 0.18}};

               for (double[] var40 : var36) {
                  double var41 = var40[0] * Math.sin(var30);
                  double var43 = var40[1] * Math.sin(var30);
                  double[] var45 = new double[]{
                     var11 * Math.cos(var30 * 0.3) + var17 * var41 + var23 * var43,
                     var13 * Math.cos(var30 * 0.3) + var19 * var41 + var25 * var43,
                     var15 * Math.cos(var30 * 0.3) + var21 * var41 + var27 * var43,
                     var17,
                     var19,
                     var21,
                     var23,
                     var25,
                     var27
                  };
                  bodyRing(var45, var5 * 0.999, 0.0, var30 * var40[2], var35, var1 * 0.35F, var35, 0.0F, 20, 0.0, var2);
               }
            } else if (var4.kind() == 3) {
               int var48 = aurora1(var0);
               bodyRing(var29, var5, var30, var30 * 1.6, var48, var1 * 0.45F, var48, 0.0F, 48, 0.0, var2);
               byte var50 = 64;

               for (int var52 = 0; var52 < var50; var52++) {
                  double var53 = -var30 + 2.0 * var30 * var52 / var50;
                  double var54 = var53 + 2.0 * var30 / var50;
                  double var55 = (var53 + var54) / 2.0 / var30;
                  if (!LiveSky.synthGap(var55, var2)) {
                     double var42 = Math.sqrt(Math.max(0.0, var30 * var30 - var53 * var53));
                     double var44 = Math.sqrt(Math.max(0.0, var30 * var30 - var54 * var54));
                     int var46 = LiveSky.synthColor(var53 / var30);
                     int var47 = LiveSky.synthColor(var54 / var30);
                     quad(
                        disc(var29, -var42, var53, var5),
                        disc(var29, var42, var53, var5),
                        disc(var29, var44, var54, var5),
                        disc(var29, -var44, var54, var5),
                        var46,
                        var1,
                        var46,
                        var1,
                        var47,
                        var1,
                        var47,
                        var1
                     );
                  }
               }
            } else {
               int var49 = aurora1(var0);
               int var51 = aurora2(var0);
               bodyRing(var29, var5, 0.0, var30, 262664, var1, 262664, var1, 48, 0.0, var2);
               bodyRing(var29, var5, var30, var30 * 1.035, 16774364, var1, 16774364, var1, 64, 0.25, var2);
               bodyRing(var29, var5, var30 * 1.035, var30 * 1.2, Gfx.lerpColor(var49, 16777215, 0.4F), var1 * 0.95F, var49, 0.0F, 64, 0.5, var2);
               bodyRing(var29, var5, var30 * 1.2, var30 * 2.4, var51, var1 * 0.25F, var51, 0.0F, 64, 0.8, var2);
            }
         }
      }
   }

   private static double[] disc(double[] var0, double var1, double var3, double var5) {
      double var7 = var0[0] + var0[3] * var1 + var0[6] * var3;
      double var9 = var0[1] + var0[4] * var1 + var0[7] * var3;
      double var11 = var0[2] + var0[5] * var1 + var0[8] * var3;
      double var13 = Math.sqrt(var7 * var7 + var9 * var9 + var11 * var11);
      return p(var7 / var13 * var5, var9 / var13 * var5, var11 / var13 * var5);
   }

   private static void bodyRing(
      double[] var0, double var1, double var3, double var5, int var7, float var8, int var9, float var10, int var11, double var12, double var14
   ) {
      double var16 = Math.sin(var3);
      double var18 = Math.cos(var3);
      double var20 = Math.sin(var5);
      double var22 = Math.cos(var5);

      for (int var24 = 0; var24 < var11; var24++) {
         double var25 = var24 * Math.PI * 2.0 / var11;
         double var27 = (var24 + 1) * Math.PI * 2.0 / var11;
         float var29 = var12 > 0.0
            ? (float)(1.0 - var12 + var12 * (0.5 + 0.5 * Math.sin(var25 * 7.0 + var14 * 0.9) * Math.sin(var25 * 3.0 - var14 * 0.5)))
            : 1.0F;
         float var30 = var12 > 0.0
            ? (float)(1.0 - var12 + var12 * (0.5 + 0.5 * Math.sin(var27 * 7.0 + var14 * 0.9) * Math.sin(var27 * 3.0 - var14 * 0.5)))
            : 1.0F;
         double var31 = Math.cos(var25);
         double var33 = Math.sin(var25);
         double var35 = Math.cos(var27);
         double var37 = Math.sin(var27);
         quad(
            p(
               (var0[0] * var18 + (var0[3] * var31 + var0[6] * var33) * var16) * var1,
               (var0[1] * var18 + (var0[4] * var31 + var0[7] * var33) * var16) * var1,
               (var0[2] * var18 + (var0[5] * var31 + var0[8] * var33) * var16) * var1
            ),
            p(
               (var0[0] * var18 + (var0[3] * var35 + var0[6] * var37) * var16) * var1,
               (var0[1] * var18 + (var0[4] * var35 + var0[7] * var37) * var16) * var1,
               (var0[2] * var18 + (var0[5] * var35 + var0[8] * var37) * var16) * var1
            ),
            p(
               (var0[0] * var22 + (var0[3] * var35 + var0[6] * var37) * var20) * var1,
               (var0[1] * var22 + (var0[4] * var35 + var0[7] * var37) * var20) * var1,
               (var0[2] * var22 + (var0[5] * var35 + var0[8] * var37) * var20) * var1
            ),
            p(
               (var0[0] * var22 + (var0[3] * var31 + var0[6] * var33) * var20) * var1,
               (var0[1] * var22 + (var0[4] * var31 + var0[7] * var33) * var20) * var1,
               (var0[2] * var22 + (var0[5] * var31 + var0[8] * var33) * var20) * var1
            ),
            var7,
            var8 * var29,
            var7,
            var8 * var30,
            var9,
            var10 * var30,
            var9,
            var10 * var29
         );
      }
   }

   private static void drawPlayerFx(MinecraftClient var0, Config var1, VertexConsumerProvider var2, Vec3d var3, float var4, double var5, SkyPreset var7) {
      ClientPlayerEntity var8 = var0.player;
      if (var8 != null && !var8.isInvisible()) {
         Vec3d var9 = var8.getLerpedPos(var4);
         long var10 = System.currentTimeMillis();
         NowPlaying var12 = SpotifyClient.now();
         float var13 = var12.playing ? var12.beatPulse(var12.progressNow(var1.lyricsOffsetMs)) : 0.0F;
         int var14 = Theme.accent();
         int var15 = var7 != null ? aurora1(var7) : var14;
         if (var1.trail > 0 && SkyTextures.spritesReady()) {
            double var16 = var9.x - lastTx;
            double var18 = var9.z - lastTz;
            if (var10 - lastTrail > 35L && var16 * var16 + var18 * var18 > 9.0E-4) {
               lastTrail = var10;
               lastTx = var9.x;
               lastTz = var9.z;
               double var20 = (Math.random() - 0.5) * 0.35;
               double var22 = (Math.random() - 0.5) * 0.35;
               TRAIL.addLast(new double[]{var9.x + var20, var9.y + 0.12 + Math.random() * 0.25, var9.z + var22, var10, var10 / 1400.0 % 1.0});
            }

            while (!TRAIL.isEmpty() && var10 - ((double[])TRAIL.peekFirst())[3] > 1300.0) {
               TRAIL.pollFirst();
            }

            if (!TRAIL.isEmpty()) {
               VertexConsumer var38 = var2.getBuffer(RenderLayers.text(SkyTextures.SPRITES));

               for (double[] var40 : TRAIL) {
                  float var23 = (float)((var10 - var40[3]) / 1300.0);
                  int var24 = var1.trail == 2 ? Gfx.hsv((float)var40[4], 0.65F, 1.0F) : (var1.trail == 3 ? var15 : var14);
                  double var25 = var23 * 0.35;
                  sprite(
                     var38,
                     var40[0] - var3.x,
                     var40[1] + var25 - var3.y,
                     var40[2] - var3.z,
                     0.32 * (1.0 - var23 * 0.6) * (1.0 + 0.4 * var13),
                     2,
                     var24,
                     (1.0F - var23) * 0.9F
                  );
               }
            }
         } else if (!TRAIL.isEmpty()) {
            TRAIL.clear();
         }

         if (var1.aura) {
            quads = var2.getBuffer(RenderLayers.debugQuads());
            double var36 = var9.x - var3.x;
            double var37 = var9.y + 0.03 - var3.y;
            double var39 = var9.z - var3.z;
            byte var41 = 40;
            float var42 = 0.35F + 0.65F * var13;
            double var43 = 0.55 + 0.08 * var13;
            double var26 = var43 + 0.16;

            for (int var28 = 0; var28 < var41; var28++) {
               double var29 = var28 * Math.PI * 2.0 / var41 + var5 * 0.6;
               double var31 = (var28 + 1) * Math.PI * 2.0 / var41 + var5 * 0.6;
               float var33 = (float)(0.5 + 0.5 * Math.cos(var29 * 3.0 - var5 * 2.4));
               float var34 = (0.18F + 0.45F * var33) * var42;
               int var35 = var1.trail == 2 ? Gfx.hsv((float)(((double)var28 / var41 + var5 * 0.1) % 1.0), 0.6F, 1.0F) : var14;
               quad(
                  p(var36 + Math.cos(var29) * var43, var37, var39 + Math.sin(var29) * var43),
                  p(var36 + Math.cos(var31) * var43, var37, var39 + Math.sin(var31) * var43),
                  p(var36 + Math.cos(var31) * var26, var37, var39 + Math.sin(var31) * var26),
                  p(var36 + Math.cos(var29) * var26, var37, var39 + Math.sin(var29) * var26),
                  var35,
                  var34,
                  var35,
                  var34,
                  var35,
                  0.0F,
                  var35,
                  0.0F
               );
            }

            quads = null;
         }
      }
   }

   private static double skyEl() {
      double var0 = -camPitch + 3.0F;
      return NeonLyrics.cfg().lyricsUnderground && !skyOpen ? var0 : Math.max(-6.0, Math.min(60.0, var0));
   }

   private static double degPerPx(MinecraftClient var0, Config var1) {
      double var2 = var0.getWindow().getScaledHeight();
      double var4 = Math.max(1.0, var2) / 2.0 / Math.tan(Math.toRadians(35.0));
      return Math.toDegrees(var1.lyricsSize / var4);
   }

   private static void drawLyrics(MinecraftClient var0, Config var1, VertexConsumerProvider var2, int var3, float var4) {
      NowPlaying var5 = SpotifyClient.now();
      long var6 = System.currentTimeMillis();
      int var8 = -1;
      long var9 = 0L;
      if (var5.hasTrack() && var5.lyricsState == NowPlaying.LyricsState.SYNCED && var5.playing) {
         var9 = var5.progressNow(var1.lyricsOffsetMs + 280);
         var8 = var5.lineIndex(var9);
         if (var8 >= 0 && var9 > var5.lineEnd(var8) + 400L) {
            var8 = -1;
         }
      }

      double var11 = degPerPx(var0, var1);
      if (var8 != activeIdx || !String.valueOf(var5.trackId).equals(activeTrack)) {
         if (active != null) {
            active.exitAt = var6;
            exiting.add(active);
         }

         active = null;
         activeIdx = var8;
         activeTrack = String.valueOf(var5.trackId);
         if (var8 >= 0 && !var5.lines.get(var8).text().isBlank()) {
            active = new WorldSky.SkyLine(var5.lines.get(var8), camYaw, skyEl(), var11);
         }
      }

      exiting.removeIf(var2x -> var6 - var2x.exitAt > 520L);

      while (exiting.size() > 1) {
         exiting.remove(0);
      }

      for (WorldSky.SkyLine var14 : exiting) {
         drawLine(var0, var1, var2, var14, var3, -1.0F, var6, var4, var11);
      }

      if (active != null) {
         drawLine(var0, var1, var2, active, var3, var5.wordPosition(var8, var9), var6, var4, var11);
      }
   }

   private static double angDiff(double var0, double var2) {
      return ((var0 - var2) % 360.0 + 540.0) % 360.0 - 180.0;
   }

   private static void drawLine(
      MinecraftClient var0, Config var1, VertexConsumerProvider var2, WorldSky.SkyLine var3, int var4, float var5, long var6, float var8, double var9
   ) {
      if (var1.lyricsFollow && !var1.lyricsViewLock && var3.exitAt == 0L) {
         double var11 = angDiff(camYaw, var3.baseAz);
         double var13 = 1.0 - Math.exp(-3.0 * var8);
         if (Math.abs(var11) > 26.0) {
            var3.baseAz = var3.baseAz + (var11 - Math.signum(var11) * 26.0) * var13;
         }

         double var15 = skyEl() - var3.baseEl;
         double var17 = 1.0 - Math.exp(-(Math.abs(var15) > 6.0 ? 4.0 : 1.5) * var8);
         var3.baseEl += var15 * var17;
      }

      float var100 = var3.exitAt == 0L ? 0.0F : Anim.clamp((float)(var6 - var3.exitAt) / 480.0F);
      float var12 = (1.0F - Anim.outCubic(var100)) * var1.lyricsOpacity;
      if (!(var12 <= 0.01F)) {
         String[] var101 = var3.line.words();
         int var14 = var5 < 0.0F ? var101.length : (int)Math.floor(var5);
         TextRenderer var102 = var0.textRenderer;
         double var16 = dist * 0.9;
         float var18 = (float)(Math.toRadians(var9) * var16);
         int var19 = Gfx.w(" ", Gfx.LYRICS);
         int var20 = Gfx.lerpColor(var4, 656916, 0.75F);
         long var21 = !exiting.isEmpty() && var3.exitAt == 0L ? 420L : 0L;
         NowPlaying var23 = SpotifyClient.now();
         float var24 = var1.fxBeatPulse ? var23.beatPulse(var23.progressNow(var1.lyricsOffsetMs)) : 0.0F;
         boolean var25 = var1.lyricsThroughBlocks || var1.lyricsUnderground && !skyOpen;
         int var26 = var1.lyricsGlowColor < 0 ? var4 : LYRIC_COLORS[Math.floorMod(var1.lyricsGlowColor, LYRIC_COLORS.length)];

         for (WorldSky.Chunk var28 : var3.chunks) {
            if (var3.exitAt != 0L || var28.from <= var14) {
               float var29 = var18 * var28.size;
               double var30 = Math.toRadians(var3.baseAz + var28.offAz);
               double var32 = Math.toRadians(var3.baseEl + var28.offEl + Anim.outCubic(var100) * 3.0F);
               double var34 = -Math.sin(var30) * Math.cos(var32);
               double var36 = Math.sin(var32);
               double var38 = Math.cos(var30) * Math.cos(var32);
               double var40 = -Math.cos(var30);
               double var42 = -Math.sin(var30);
               double var44 = -var42 * var36;
               double var46 = var42 * var34 - var40 * var38;
               double var48 = var40 * var36;
               double var50 = Math.cos(var28.yawTilt);
               double var52 = Math.sin(var28.yawTilt);
               double var54 = var40 * var50 + var34 * var52;
               double var56 = var36 * var52;
               double var58 = var42 * var50 + var38 * var52;
               double var60 = Math.cos(var28.roll);
               double var62 = Math.sin(var28.roll);
               double var64 = var54 * var60 + var44 * var62;
               double var66 = var56 * var60 + var46 * var62;
               double var68 = var58 * var60 + var48 * var62;
               double var70 = var44 * var60 - var54 * var62;
               double var72 = var46 * var60 - var56 * var62;
               double var74 = var48 * var60 - var58 * var62;
               double var76 = var66 * var74 - var68 * var72;
               double var78 = var68 * var70 - var64 * var74;
               double var80 = var64 * var72 - var66 * var70;
               if (var76 * var34 + var78 * var36 + var80 * var38 > 0.0) {
                  var76 = -var76;
                  var78 = -var78;
                  var80 = -var80;
               }

               Matrix4f var82 = M0.set(base)
                  .mul(
                     TMP.set(
                        (float)var64 * var29,
                        (float)var66 * var29,
                        (float)var68 * var29,
                        0.0F,
                        (float)(-var70) * var29,
                        (float)(-var72) * var29,
                        (float)(-var74) * var29,
                        0.0F,
                        (float)var76 * var29,
                        (float)var78 * var29,
                        (float)var80 * var29,
                        0.0F,
                        (float)(var34 * var16),
                        (float)(var36 * var16),
                        (float)(var38 * var16),
                        1.0F
                     )
                  );
               float var83 = -var28.widthPx / 2.0F;

               for (int var84 = var28.from; var84 < var28.to; var84++) {
                  String var85 = var101[var84];
                  int var86 = Gfx.w(var85, Gfx.LYRICS);
                  boolean var87 = var3.exitAt != 0L || var84 <= var14;
                  if (var87 && var3.shown[var84] == 0L) {
                     var3.shown[var84] = Math.max(var6, var3.born + var21);
                  }

                  if (!var87) {
                     var83 += var86 + var19;
                  } else {
                     float var88 = Anim.since(var3.shown[var84], 450L);
                     float var89 = Anim.outCubic(var88);
                     float var90 = var89 * var12;
                     boolean var91 = var3.exitAt == 0L && var84 == var14;
                     Matrix4f var92 = WM.set(var82);
                     if (var91 && var24 > 0.01F) {
                        float var93 = 1.0F + 0.07F * var24;
                        var92.translate(var83 + var86 / 2.0F, 0.0F, 0.0F).scale(var93, var93, 1.0F).translate(-(var83 + var86 / 2.0F), 0.0F, 0.0F);
                     }

                     switch (var1.lyricsAnim) {
                        case 1:
                           float var103 = 1.3F - 0.3F * Anim.outBack(var88);
                           var92.translate(var83 + var86 / 2.0F, 0.0F, 0.0F).scale(var103, var103, 1.0F).translate(-var86 / 2.0F, -4.5F, 0.0F);
                           break;
                        case 2:
                           var92.translate(var83 - (1.0F - var89) * 10.0F, -4.5F, 0.0F);
                           break;
                        default:
                           var92.translate(var83, -4.5F + (1.0F - var89) * 3.0F, 0.0F);
                     }

                     int var104 = Q_GLOW[q()];
                     boolean var94 = var1.lyricsGlow && AutoPerf.allow(3);
                     if (var94 && var104 > 0) {
                        float var95 = var1.lyricsGlowStrength * (var91 ? 1.25F : 1.0F) * (8.0F / var104) * 0.75F;

                        for (int var96 = 0; var96 < var104; var96++) {
                           double var97 = var96 * Math.PI * 2.0 / var104 + (Math.PI / 8);
                           float var99 = 1.0F;
                           word(
                              var102,
                              var2,
                              var92,
                              var85,
                              (float)Math.cos(var97) * var99,
                              (float)Math.sin(var97) * var99,
                              0.04F,
                              var26,
                              var90 * 0.16F * var95,
                              var25
                           );
                        }
                     } else if (var94) {
                        word(var102, var2, var92, var85, 0.0F, 0.6F, 0.04F, var26, var90 * 0.45F * var1.lyricsGlowStrength, var25);
                     } else {
                        word(var102, var2, var92, var85, 0.0F, 0.9F, 0.0F, var20, var90 * 0.55F, var25);
                     }

                     int var105 = var91 ? Gfx.lerpColor(var4, 16777215, 0.6F) : Gfx.lerpColor(var4, 16777215, 0.2F);
                     word(var102, var2, var92, var85, 0.0F, 0.0F, 0.12F, var105, var90, var25);
                     var83 += var86 + var19;
                  }
               }
            }
         }
      }
   }

   private static Text lyricComp(String var0) {
      int var1 = NeonLyrics.cfg().lyricsFont;
      if (var1 != compFont || COMP.size() > 400) {
         COMP.clear();
         compFont = var1;
      }

      return COMP.computeIfAbsent(var0, var0x -> Gfx.comp(var0x, Gfx.LYRICS));
   }

   private static void word(
      TextRenderer var0, VertexConsumerProvider var1, Matrix4f var2, String var3, float var4, float var5, float var6, int var7, float var8, boolean var9
   ) {
      if (!(var8 <= 0.03F)) {
         Matrix4f var10 = SCR.set(var2).translate(var4, var5, var6);
         var0.draw(
            lyricComp(var3), 0.0F, 0.0F, Gfx.argbRaw(var7, var8), false, var10, var1, var9 ? TextLayerType.SEE_THROUGH : TextLayerType.NORMAL, 0, 15728880
         );
      }
   }

   private static final class Chunk {
      int from;
      int to;
      int widthPx;
      double offAz;
      double offEl;
      float yawTilt;
      float roll;
      float size = 1.0F;
   }

   private static final class SkyLine {
      final NowPlaying.Line line;
      final long[] shown;
      final List<WorldSky.Chunk> chunks = new ArrayList<>();
      double baseAz;
      double baseEl;
      final long born = System.currentTimeMillis();
      long exitAt = 0L;

      SkyLine(NowPlaying.Line var1, double var2, double var4, double var6) {
         this.line = var1;
         this.shown = new long[var1.words().length];
         this.baseAz = var2;
         this.baseEl = var4;
         String[] var8 = var1.words();
         int var9 = Gfx.w(" ", Gfx.LYRICS);
         int var10 = 0;

         while (var10 < var8.length) {
            int var11 = var10 + 1;

            for (int var12 = var8[var10].length();
               var11 < var8.length && var11 - var10 < 3 && var12 + var8[var11].length() <= (var11 - var10 == 1 ? 12 : 9);
               var11++
            ) {
               var12 += var8[var11].length();
            }

            WorldSky.Chunk var13 = new WorldSky.Chunk();
            var13.from = var10;
            var13.to = var11;

            for (int var14 = var10; var14 < var11; var14++) {
               var13.widthPx = var13.widthPx + Gfx.w(var8[var14], Gfx.LYRICS) + (var14 > var10 ? var9 : 0);
            }

            this.chunks.add(var13);
            var10 = var11;
         }

         double var42 = var9 * 2.4 * var6;
         int var43 = this.chunks.size();
         int var44 = var43 <= 2 ? 1 : (var43 >= 6 && WorldSky.RND.nextFloat() < 0.35F ? 3 : 2);
         if (var43 == 3 && WorldSky.RND.nextFloat() < 0.35F) {
            var44 = 1;
         }

         ArrayList<List<WorldSky.Chunk>> var15 = new ArrayList();
         int var16 = 0;

         for (int var17 = 0; var17 < var44; var17++) {
            int var18 = var43 - var16;
            int var19 = var44 - var17;
            int var20 = var19 == 1
               ? var18
               : Math.max(1, Math.min(var18 - (var19 - 1), Math.round((float)var18 / var19 + (WorldSky.RND.nextFloat() - 0.5F) * 1.2F)));
            var15.add(new ArrayList<>(this.chunks.subList(var16, var16 + var20)));
            var16 += var20;
         }

         int var45 = WorldSky.RND.nextInt(var43);

         for (int var46 = 0; var46 < var43; var46++) {
            this.chunks.get(var46).size = (var46 == var45 ? 1.22F : 0.86F) + WorldSky.RND.nextFloat() * 0.2F;
         }

         double var47 = 17.0 * var6;
         double var48 = (WorldSky.RND.nextDouble() * 2.0 - 1.0) * 20.0;
         if (Math.signum(var48) == WorldSky.lastSide && WorldSky.RND.nextFloat() < 0.65F) {
            var48 = -var48;
         }

         WorldSky.lastSide = Math.signum(var48);
         double var22 = 2.5 + WorldSky.RND.nextDouble() * 6.0;
         double var24 = (WorldSky.RND.nextBoolean() ? 1 : -1) * (5.0 + WorldSky.RND.nextDouble() * 8.0);

         for (int var26 = 0; var26 < var15.size(); var26++) {
            List<WorldSky.Chunk> var27 = var15.get(var26);
            double var28 = -var42;

            for (WorldSky.Chunk var31 : var27) {
               var28 += var42 + var31.widthPx * var6 * var31.size;
            }

            if (var28 > 58.0) {
               float var49 = (float)(58.0 / var28);

               for (WorldSky.Chunk var32 : var27) {
                  var32.size *= var49;
               }

               var28 = 58.0;
            }

            double var50 = var48 + var26 * var24 + (WorldSky.RND.nextDouble() - 0.5) * 4.0;
            double var53 = Math.max(0.0, 46.0 - var28 / 2.0);
            var50 = Math.max(-var53, Math.min(var53, var50));
            double var34 = var22 + (var15.size() - 1 - var26) * var47 + (WorldSky.RND.nextDouble() - 0.5) * var47 * 0.15;
            double var36 = var50 - var28 / 2.0;

            for (int var38 = 0; var38 < var27.size(); var38++) {
               WorldSky.Chunk var39 = (WorldSky.Chunk)var27.get(var38);
               double var40 = var39.widthPx * var6 * var39.size;
               var39.offAz = var36 + var40 / 2.0;
               var39.offEl = var34 + (var38 % 2 == 0 ? 0.8 : -0.6) + (WorldSky.RND.nextDouble() - 0.5) * 0.8;
               var36 += var40 + var42;
               var39.yawTilt = (float)Math.toRadians((WorldSky.RND.nextFloat() < 0.5F ? -1 : 1) * (5.0F + WorldSky.RND.nextFloat() * 14.0F));
               var39.roll = (float)Math.toRadians((WorldSky.RND.nextFloat() - 0.5F) * 9.0F);
            }
         }
      }
   }
}
