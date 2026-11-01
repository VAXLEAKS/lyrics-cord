package dev.neonlyrics.sky;

import dev.neonlyrics.Beat;
import dev.neonlyrics.NeonLyrics;
import dev.neonlyrics.config.Config;
import dev.neonlyrics.hud.Anim;
import dev.neonlyrics.hud.Gfx;
import dev.neonlyrics.hud.Island;
import dev.neonlyrics.util.Fx;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.option.CloudRenderMode;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.client.render.RenderTickCounter;

public final class SkyRenderer {
   private static float sw;
   private static float sh;
   private static float focal;
   private static double rx;
   private static double ry;
   private static double rz;
   private static double ux;
   private static double uy;
   private static double uz;
   private static double fx;
   private static double fy;
   private static double fz;
   private static float camYaw;
   private static float camPitch;
   private static final float[] P = new float[3];
   private static final Anim visible = new Anim(0.0, 4.0);
   private static final Random RND = new Random();
   private static float[][] stars;
   private static final List<float[]> shooting = new ArrayList<>();
   private static long nextShooting = 0L;
   private static double[][] parts = new double[0][];
   private static SkyPreset.Weather partType = null;
   private static long lastNanos = System.nanoTime();
   private static long flashAt = 0L;
   private static long nextFlash = 0L;
   private static boolean forcedRain = false;
   private static int lastSky = -99;

   private SkyRenderer() {
   }

   private static void setupCamera(DrawContext var0, MinecraftClient var1, float var2) {
      sw = var0.getScaledWindowWidth();
      sh = var0.getScaledWindowHeight();
      double var3 = Math.toRadians(((Integer)var1.options.getFov().getValue()).intValue());
      focal = (float)(sh / 2.0 / Math.tan(var3 / 2.0));
      camYaw = var1.player.getYaw(var2);
      camPitch = var1.player.getPitch(var2);
      double var5 = Math.toRadians(camYaw);
      double var7 = Math.toRadians(camPitch);
      fx = -Math.sin(var5) * Math.cos(var7);
      fy = -Math.sin(var7);
      fz = Math.cos(var5) * Math.cos(var7);
      rx = -Math.cos(var5);
      ry = 0.0;
      rz = -Math.sin(var5);
      ux = ry * fz - rz * fy;
      uy = rz * fx - rx * fz;
      uz = rx * fy - ry * fx;
   }

   private static boolean project(double var0, double var2, double var4) {
      double var6 = var0 * fx + var2 * fy + var4 * fz;
      if (var6 < 0.02) {
         return false;
      }

      double var8 = var0 * rx + var2 * ry + var4 * rz;
      double var10 = var0 * ux + var2 * uy + var4 * uz;
      P[0] = (float)(sw / 2.0F + focal * var8 / var6);
      P[1] = (float)(sh / 2.0F - focal * var10 / var6);
      P[2] = (float)var6;
      return true;
   }

   private static boolean projectDir(double var0, double var2) {
      double var4 = Math.toRadians(var0);
      double var6 = Math.toRadians(var2);
      return project(-Math.sin(var4) * Math.cos(var6), Math.sin(var6), Math.cos(var4) * Math.cos(var6));
   }

   private static void initStars() {
      Random var0 = new Random(1337L);
      stars = new float[520][5];

      for (int var1 = 0; var1 < stars.length; var1++) {
         float[] var2 = stars[var1];
         var2[0] = var0.nextFloat() * 360.0F;
         var2[1] = (float)Math.toDegrees(Math.asin(0.05 + var0.nextFloat() * 0.95));
         var2[2] = var0.nextFloat() < 0.12F ? 2.0F : 1.0F;
         var2[3] = 0.35F + var0.nextFloat() * 0.65F;
         var2[4] = var0.nextFloat() * 6.28F;
      }
   }

   public static void tick(MinecraftClient var0) {
      Config var1 = NeonLyrics.cfg();
      if (var1.sky != lastSky) {
         if (lastSky != -99 && var0.player != null) {
            SkyPreset var2 = SkyPreset.get(var1.sky);
            Island.notify(var2 == null ? Fx.t("neonlyrics.sky.off") : Fx.t("neonlyrics.sky.enabled", Fx.t(var2.key())), var2 != null);
         }

         lastSky = var1.sky;
      }

      if (var0.world == null) {
         forcedRain = false;
      } else {
         updateClouds(var0, var1);
         SkyPreset var4 = SkyPreset.get(var1.sky);
         boolean var3 = var4 != null
            && (var1.realWeather || var4 == SkyPreset.RAIN)
            && (var4.weather == SkyPreset.Weather.RAIN || var4.weather == SkyPreset.Weather.THUNDER)
            && WorldSky.skyWorld(var0.world);
         if (var3) {
            var0.world.setRainGradient(1.0F);
            var0.world.setThunderGradient(var4.weather == SkyPreset.Weather.THUNDER ? 1.0F : 0.0F);
            forcedRain = true;
         } else if (forcedRain) {
            var0.world.setRainGradient(0.0F);
            var0.world.setThunderGradient(0.0F);
            forcedRain = false;
         }
      }
   }

   private static void updateClouds(MinecraftClient var0, Config var1) {
      boolean var2 = var1.hideClouds && var1.sky >= 0;
      SimpleOption var3 = var0.options.getCloudRenderMode();
      if (var2 && var3.getValue() != CloudRenderMode.OFF) {
         var1.cloudsBefore = ((CloudRenderMode)var3.getValue()).name();
         var3.setValue(CloudRenderMode.OFF);
         NeonLyrics.saveConfig();
      } else if (!var2 && !var1.cloudsBefore.isEmpty()) {
         try {
            var3.setValue(CloudRenderMode.valueOf(var1.cloudsBefore));
         } catch (Exception var5) {
         }

         var1.cloudsBefore = "";
         NeonLyrics.saveConfig();
      }
   }

   public static void render(DrawContext var0, RenderTickCounter var1) {
      if (Beat.live() && Beat.sync()) {
         MinecraftClient var2 = MinecraftClient.getInstance();
         if (!var2.options.hudHidden && var2.player != null && var2.world != null) {
            Config var3 = NeonLyrics.cfg();
            long var4 = System.nanoTime();
            float var6 = Math.min(0.1F, (float)(var4 - lastNanos) / 1.0E9F);
            lastNanos = var4;
            float var7 = var1.getTickProgress(false);
            setupCamera(var0, var2, var7);
            SkyPreset var8 = SkyPreset.get(SkyPreview.sky());
            float var9 = Gfx.alpha;
            Gfx.alpha = 1.0F;
            if (var8 != null && var8.weather == SkyPreset.Weather.THUNDER && WorldSky.skyWorld(var2.world)) {
               drawLightning(var0);
            }

            Gfx.alpha = var9;
         }
      }
   }

   private static void drawLightning(DrawContext var0) {
      long var1 = System.currentTimeMillis();
      if (var1 > nextFlash) {
         flashAt = var1;
         nextFlash = var1 + 5000L + RND.nextInt(9000);
      }

      long var3 = var1 - flashAt;
      if (var3 < 600L) {
         float var5 = var3 < 90L ? 0.32F : (var3 < 180L ? 0.05F : (var3 < 260L ? 0.22F : 0.22F * (1.0F - (float)(var3 - 260L) / 340.0F)));
         Gfx.rect(var0, 0, 0, (int)sw, (int)sh, 15266047, Math.max(0.0F, var5));
      }
   }
}
