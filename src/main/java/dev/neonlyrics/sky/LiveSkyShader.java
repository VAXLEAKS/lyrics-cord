package dev.neonlyrics.sky;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderPipeline.Snippet;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexFormat.DrawMode;
import dev.neonlyrics.NeonLyrics;
import dev.neonlyrics.config.Config;
import dev.neonlyrics.config.Storage;
import dev.neonlyrics.lyrics.NowPlaying;
import dev.neonlyrics.lyrics.SpotifyClient;
import dev.neonlyrics.util.AutoPerf;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.Locale;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.UniformType;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderSetup;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public final class LiveSkyShader {
   private static final Identifier TEX = Identifier.of("neonlyrics", "live_sky_params");
   private static final RenderPipeline PIPE = pipe("pipeline/live_sky", "core/live_sky");
   private static final RenderPipeline PIPE_FX = pipe("pipeline/live_sky_fx", "core/live_sky_fx");
   private static RenderLayer type;
   private static RenderLayer typeFx;
   private static NativeImageBackedTexture tex;
   private static int state;
   private static int fxState;
   private static final int[] last = new int[16];
   private static final int[] next = new int[16];
   private static float music;
   private static double beatPos;
   private static final RenderPipeline NOFOG = pipe(
      "pipeline/sky_nofog", Identifier.ofVanilla("core/position_tex_color"), Identifier.ofVanilla("core/position_tex_color")
   );
   private static int nofogState;

   private LiveSkyShader() {
   }

   private static RenderPipeline pipe(String var0, String var1) {
      return pipe(var0, Identifier.of("neonlyrics", "core/live_sky"), Identifier.of("neonlyrics", var1));
   }

   public static RenderLayer noFogType(Identifier var0) {
      if (nofogState == 0) {
         try {
            nofogState = RenderSystem.getDevice().precompilePipeline(NOFOG).isValid() ? 1 : -1;
         } catch (Throwable var2) {
            nofogState = -1;
         }

         if (nofogState < 0) {
            NeonLyrics.LOG.warn("Himmel ohne Nebel geht hier nicht - nehme den alten Weg");
         }
      }

      return nofogState < 0 ? null : RenderLayer.of("neonlyrics_sky_nofog", RenderSetup.builder(NOFOG).texture("Sampler0", var0).build());
   }

   private static RenderPipeline pipe(String var0, Identifier var1, Identifier var2) {
      return RenderPipeline.builder(new Snippet[0])
         .withLocation(Identifier.of("neonlyrics", var0))
         .withUniform("DynamicTransforms", UniformType.UNIFORM_BUFFER)
         .withUniform("Projection", UniformType.UNIFORM_BUFFER)
         .withVertexShader(var1)
         .withFragmentShader(var2)
         .withSampler("Sampler0")
         .withBlend(BlendFunction.TRANSLUCENT)
         .withDepthWrite(false)
         .withCull(false)
         .withVertexFormat(VertexFormats.POSITION_TEXTURE_COLOR, DrawMode.QUADS)
         .build();
   }

   private static boolean badGpu() {
      try {
         String var0 = (RenderSystem.getDevice().getRenderer() + " " + RenderSystem.getDevice().getVendor()).toLowerCase(Locale.ROOT);
         return var0.contains("amd") || var0.contains("radeon") || var0.contains("ati technologies");
      } catch (Throwable var1) {
         return false;
      }
   }

   public static boolean ready() {
      if (state != 0) {
         return state > 0;
      }

      try {
         if (badGpu()) {
            NeonLyrics.LOG.info("Himmel-Shader auf dieser Grafikkarte aus (AMD) - nehme das Buehnenbild");
            state = -1;
         } else if (!RenderSystem.getDevice().precompilePipeline(PIPE).isValid()) {
            NeonLyrics.LOG.warn("Himmel-Shader laesst sich nicht kompilieren - nehme das Buehnenbild");
            state = -1;
         } else {
            tex = new NativeImageBackedTexture(() -> "neonlyrics_live_sky", new NativeImage(16, 1, false));
            MinecraftClient.getInstance().getTextureManager().registerTexture(TEX, tex);
            type = RenderLayer.of("neonlyrics_live_sky", RenderSetup.builder(PIPE).texture("Sampler0", TEX).build());
            state = 1;
            NeonLyrics.LOG.info("Himmel-Shader aktiv");
         }
      } catch (Throwable var1) {
         NeonLyrics.LOG.warn("Himmel-Shader nicht verfuegbar - nehme das Buehnenbild", var1);
         state = -1;
      }

      status(state > 0 ? "aktiv" : "aus (Buehnenbild)");
      return state > 0;
   }

   public static boolean readyFx() {
      if (!ready()) {
         return false;
      }

      if (fxState != 0) {
         return fxState > 0;
      }

      try {
         if (RenderSystem.getDevice().precompilePipeline(PIPE_FX).isValid()) {
            typeFx = RenderLayer.of("neonlyrics_live_sky_fx", RenderSetup.builder(PIPE_FX).texture("Sampler0", TEX).build());
            fxState = 1;
            NeonLyrics.LOG.info("Effekt-Himmel-Shader aktiv");
         } else {
            fxState = -1;
            NeonLyrics.LOG.warn("Effekt-Himmel-Shader laesst sich nicht kompilieren");
         }
      } catch (Throwable var1) {
         fxState = -1;
         NeonLyrics.LOG.warn("Effekt-Himmel-Shader nicht verfuegbar", var1);
      }

      status(fxState > 0 ? "aktiv (+ Effekt-Shader)" : "aktiv (Effekt-Shader aus)");
      return fxState > 0;
   }

   private static void status(String var0) {
      try {
         String var1 = "";

         try {
            var1 = RenderSystem.getDevice().getRenderer() + " / " + RenderSystem.getDevice().getVersion();
         } catch (Throwable var3) {
         }

         Files.writeString(Storage.dir().resolve("sky-status.txt"), LocalDateTime.now() + "\nHimmel-Shader: " + var0 + "\nGrafik: " + var1 + "\n");
      } catch (Throwable var4) {
      }
   }

   public static void disable(Throwable var0) {
      NeonLyrics.LOG.warn("Himmel-Shader abgeschaltet", var0);
      state = -1;
      status("abgeschaltet nach Fehler: " + var0);
   }

   public static RenderLayer type() {
      return type;
   }

   public static RenderLayer type(SkyPreset var0) {
      return (var0 == SkyPreset.DJ || var0 == SkyPreset.SPACE_WAR) && readyFx() ? typeFx : type;
   }

   public static void params(SkyPreset var0, int var1, int var2, double var3, Config var5, Matrix4f var6) {
      float var7 = 1.0F;
      float var8 = 0.6F;
      float var9 = 1.0F;
      float var10 = 1.0F;
      if (var5 != null) {
         var7 = Math.max(0.2F, Math.min(1.5F, var5.skyPattern));
         var8 = Math.max(0.0F, Math.min(1.0F, var5.skyLively));
         var9 = var5.skyBreath <= 0.01F ? 1.0F : 0.85F + 0.15F * (float)Math.sin(var3 * 0.45 * Math.max(0.2F, var5.skyBreath));
         var10 = var5.fxAurora ? 1.0F : 0.0F;
      }

      next[0] = rgba(var0.top, 255);
      next[1] = rgba(var0.horizon, 255);
      next[2] = rgba(var1, 255);
      next[3] = rgba(var2, 255);
      LiveSky.Body var11 = LiveSky.body(var0);
      next[4] = var11 == null ? 0 : rgba(var11.color(), var11.kind() + 1);
      next[5] = bytes(b8(var7 / 1.5F), b8(var8), b8(var9), b8(var10));
      next[6] = var11 == null ? 0 : pair(var11.az() * 100.0, (var11.el() + 90.0) * 100.0);
      next[7] = (var11 == null ? 0 : pair(var11.radius() * 1000.0, 0.0) & -65536) | var0.ordinal() << 8;
      Matrix3f var12 = new Matrix3f().set(var6).invert();
      float[] var13 = new float[]{var12.m00, var12.m01, var12.m02, var12.m10, var12.m11, var12.m12, var12.m20, var12.m21, var12.m22, 0.0F};

      for (int var14 = 0; var14 < 5; var14++) {
         next[8 + var14] = pair(enc(var13[var14 * 2]), enc(var13[var14 * 2 + 1]));
      }

      float var23 = 0.0F;
      boolean var15 = false;

      try {
         NowPlaying var16 = SpotifyClient.now();
         var15 = var16.hasTrack() && var16.playing;
         if (var15) {
            long var17 = var16.progressNow(var5 == null ? 0 : var5.lyricsOffsetMs);
            var23 = var16.beatPulse(var17);
            beatPos = var16.beatPos(var17);
         }
      } catch (Throwable var22) {
      }

      music = music + ((var15 ? 1.0F : 0.0F) - music) * 0.04F;
      double var24 = beatPos - Math.floor(beatPos);
      int var18 = (int)Math.floorMod((long)Math.floor(beatPos), 32L);
      next[13] = bytes(b8(music), b8(var23), b8((float)var24), var18 * 8);
      next[14] = bytes(AutoPerf.allow(2) ? 0 : 255, (var5 == null ? 0 : var5.djStyle) * 40, 0, 255);
      boolean var19 = false;

      for (int var20 = 0; var20 < 16; var20++) {
         if (next[var20] != last[var20]) {
            last[var20] = next[var20];
            var19 = true;
            int var21 = next[var20];
            tex.getImage().setColorArgb(var20, 0, (var21 & 0xFF) << 24 | var21 >>> 8);
         }
      }

      if (var19) {
         tex.upload();
      }
   }

   private static int pair(double var0, double var2) {
      int var4 = (int)Math.max(0L, Math.min(65535L, Math.round(var0)));
      int var5 = (int)Math.max(0L, Math.min(65535L, Math.round(var2)));
      return var4 << 16 | var5;
   }

   private static double enc(float var0) {
      return (Math.max(-2.0F, Math.min(2.0F, var0)) + 2.0) / 4.0 * 65535.0;
   }

   private static int b8(float var0) {
      return Math.max(0, Math.min(255, Math.round(var0 * 255.0F)));
   }

   private static int rgba(int var0, int var1) {
      return (var0 & 16777215) << 8 | var1 & 0xFF;
   }

   private static int bytes(int var0, int var1, int var2, int var3) {
      return var0 << 24 | var1 << 16 | var2 << 8 | var3;
   }
}
