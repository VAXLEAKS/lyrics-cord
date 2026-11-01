package dev.neonlyrics.cosmetic;

import dev.neonlyrics.NeonLyrics;
import dev.neonlyrics.config.Config;
import dev.neonlyrics.gui.LyricsScreen;
import dev.neonlyrics.hud.Gfx;
import dev.neonlyrics.hud.SkyThumbs;
import dev.neonlyrics.lyrics.NowPlaying;
import dev.neonlyrics.lyrics.SpotifyClient;
import dev.neonlyrics.sky.SkyPreset;
import dev.neonlyrics.util.Fx;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.font.TextRenderer.TextLayerType;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.RenderSetup;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector3fc;

final class WorldPanel {
   static final int W = 380;
   static final int H = 222;
   static final float K = 0.0105F;
   private static final float RCX = 98.0F;
   private static final float RCY = 104.0F;
   private static final float RR = 78.0F;
   private static final float BX = 62.0F;
   private static final float CX0 = 206.0F;
   private static final float CX1 = 366.0F;
   private static final int ACCENT = 10321407;
   private static final int ACCENT2 = 14471167;
   private static final int DEEP = 5914576;
   private static final int TOP = 1709620;
   private static final int BOTTOM = 723479;
   private static final int LIGHT = 15728880;
   private static RenderLayer flat;
   private final Vec3d pos;
   private final float rx;
   private final float rz;
   private final float nx;
   private final float nz;
   private final List<WorldPanel.Hit> hits = new ArrayList<>();
   private final Map<String, Float> hov = new HashMap<>();
   private int page;
   private int skyPage;
   private int hatPage;
   private final long born = System.currentTimeMillis();
   private long closing = -1L;
   private long pageTime = 0L;
   private long lastNs = System.nanoTime();
   private float dt;
   private float spin;
   private boolean hovered;
   private boolean sndLand;
   private boolean sndClose;
   private float hx;
   private float hy;
   private static final String[] TABS = new String[]{"sky", "lyrics", "style", "more"};
   private Matrix4f m;
   private float ox;
   private float oy;
   private VertexConsumer q;
   private final List<Runnable> late = new ArrayList<>();

   private static RenderLayer flat() {
      if (flat == null) {
         flat = RenderLayer.of("neonlyrics_panel", RenderSetup.builder(RenderPipelines.DEBUG_QUADS).build());
      }

      return flat;
   }

   private static String tr(String var0) {
      return Text.translatable("neonlyrics.wp." + var0).getString();
   }

   WorldPanel(MinecraftClient var1) {
      Camera var2 = var1.gameRenderer.getCamera();
      Vector3fc var3 = var2.getHorizontalPlane();
      double var4 = Math.sqrt(var3.x() * var3.x() + var3.z() * var3.z());
      float var6 = var4 < 1.0E-4 ? 0.0F : (float)(var3.x() / var4);
      float var7 = var4 < 1.0E-4 ? 1.0F : (float)(var3.z() / var4);
      Vec3d var8 = var2.getCameraPos();
      this.pos = new Vec3d(var8.x + var6 * 3.2, var8.y - 0.05, var8.z + var7 * 3.2);
      this.nx = -var6;
      this.nz = -var7;
      this.rx = -var7;
      this.rz = var6;
      play(var1, SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME, 1.3F, 0.8F);
   }

   Vec3d pos() {
      return this.pos;
   }

   boolean hovered() {
      return this.hovered;
   }

   boolean closing() {
      return this.closing >= 0L;
   }

   void close() {
      if (this.closing < 0L) {
         this.closing = System.currentTimeMillis();
      }
   }

   boolean finished() {
      return this.closing >= 0L && System.currentTimeMillis() - this.closing > 900L;
   }

   private float age() {
      return (float)(System.currentTimeMillis() - this.born);
   }

   private float closeAge() {
      return this.closing < 0L ? -1.0F : (float)(System.currentTimeMillis() - this.closing);
   }

   void tick(MinecraftClient var1) {
      if (!this.sndLand && this.age() > 860.0F) {
         this.sndLand = true;
         play(var1, SoundEvents.BLOCK_BEACON_POWER_SELECT, 1.8F, 0.3F);
      }

      if (!this.sndClose && this.closing >= 0L && this.closeAge() > 360.0F) {
         this.sndClose = true;
         play(var1, SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME, 1.0F, 0.7F);
      }
   }

   private static void play(MinecraftClient var0, SoundEvent var1, float var2, float var3) {
      try {
         var0.getSoundManager().play(PositionedSoundInstance.ui(var1, var2, var3));
      } catch (Throwable var5) {
      }
   }

   private void aim(Camera var1) {
      this.hovered = false;
      if (!(this.age() < 1200.0F) && this.closing < 0L) {
         Vec3d var2 = var1.getCameraPos();
         Vector3fc var3 = var1.getHorizontalPlane();
         double var4 = var3.x() * this.nx + var3.z() * this.nz;
         if (!(var4 > -1.0E-4)) {
            double var6 = ((this.pos.x - var2.x) * this.nx + (this.pos.z - var2.z) * this.nz) / var4;
            if (!(var6 <= 0.0) && !(var6 > 14.0)) {
               double var8 = var2.x + var3.x() * var6 - this.pos.x;
               double var10 = var2.y + var3.y() * var6 - this.pos.y;
               double var12 = var2.z + var3.z() * var6 - this.pos.z;
               float var14 = (float)((var8 * this.rx + var12 * this.rz) / 0.0105F) + 190.0F;
               float var15 = 111.0F - (float)(var10 / 0.0105F);
               if (!(var14 < 14.0F) && !(var15 < 0.0F) && !(var14 > 380.0F) && !(var15 > 222.0F)) {
                  this.hovered = true;
                  this.hx = var14;
                  this.hy = var15;
               }
            }
         }
      }
   }

   private boolean over(float var1, float var2, float var3, float var4) {
      return this.hovered && this.hx >= var1 && this.hy >= var2 && this.hx < var1 + var3 && this.hy < var2 + var4;
   }

   private float hv(String var1, boolean var2) {
      float var3 = this.hov.getOrDefault(var1, 0.0F);
      var3 += ((var2 ? 1.0F : 0.0F) - var3) * Math.min(1.0F, this.dt * 14.0F);
      this.hov.put(var1, var3);
      return var3;
   }

   void click() {
      if (this.hovered && this.closing < 0L) {
         for (int var1 = this.hits.size() - 1; var1 >= 0; var1--) {
            WorldPanel.Hit var2 = this.hits.get(var1);
            if (this.hx >= var2.x() && this.hy >= var2.y() && this.hx < var2.x() + var2.w() && this.hy < var2.y() + var2.h()) {
               Fx.sound(Fx.Sfx.CLICK);
               var2.run().run();
               NeonLyrics.saveConfig();
               return;
            }
         }
      }
   }

   private void go(int var1) {
      if (var1 != this.page) {
         this.page = var1;
         this.pageTime = System.currentTimeMillis();
      }
   }

   void render(MinecraftClient var1, VertexConsumerProvider var2, Matrix4f var3) {
      Camera var4 = var1.gameRenderer.getCamera();
      this.aim(var4);
      long var5 = System.nanoTime();
      this.dt = Math.min(0.1F, (float)(var5 - this.lastNs) / 1.0E9F);
      this.lastNs = var5;
      float var7 = this.age();
      float var8 = this.closeAge();
      float var9;
      float var10;
      float var11;
      float var12;
      if (var8 < 0.0F) {
         var9 = inOut(clamp(var7 / 900.0F));
         var10 = outCubic(clamp((var7 - 820.0F) / 460.0F));
         var11 = outCubic(clamp((var7 - 1100.0F) / 300.0F));
         var12 = inOut(clamp((var7 - 1150.0F) / 400.0F));
      } else {
         var12 = 1.0F - inOut(clamp(var8 / 220.0F));
         var11 = 1.0F - clamp(var8 / 160.0F);
         var10 = 1.0F - inOut(clamp((var8 - 120.0F) / 300.0F));
         var9 = 1.0F - inOut(clamp((var8 - 380.0F) / 480.0F));
      }

      float var13 = 3.5F + 16.5F * (float)Math.exp(-var7 / 420.0F) + (var8 >= 0.0F ? 10.0F * clamp(var8 / 600.0F) : 0.0F);
      this.spin = this.spin + var13 * this.dt;
      Vec3d var14 = var4.getCameraPos();
      float var15 = (float)Math.sin(System.currentTimeMillis() / 1200.0) * 0.012F * var10;
      Matrix4f var16 = new Matrix4f(this.rx, 0.0F, this.rz, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, this.nx, 0.0F, this.nz, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F);
      Matrix4f var17 = new Matrix4f(var3)
         .translate((float)(this.pos.x - var14.x), (float)(this.pos.y - var14.y) + var15, (float)(this.pos.z - var14.z))
         .mul(var16);
      this.q = var2.getBuffer(flat());
      this.hits.clear();
      this.late.clear();
      Config var18 = NeonLyrics.cfg();
      NowPlaying var19 = SpotifyClient.now();
      if (var10 > 0.01F) {
         float var20 = -0.96599996F;
         this.m = new Matrix4f(var17).translate(var20, 0.0F, 0.0F).scale(var10, 1.0F, 1.0F).translate(-var20, 0.0F, 0.0F);
         this.ox = 190.0F;
         this.oy = 111.0F;
         float var21 = Math.min(1.0F, var10 * 1.5F);

         for (int var22 = 4; var22 >= 1; var22--) {
            this.round(
               62.0F - var22 * 2.5F, -var22 * 2.5F, 318.0F + var22 * 5.0F, 222.0F + var22 * 5.0F, 14.0F + var22 * 2.5F, 10321407, 10321407, 0.05F * var21, 0.0F
            );
         }

         this.round(61.2F, -0.8F, 319.6F, 223.6F, 14.8F, 12167664, 7231688, 0.35F * var21, 0.0F);
         this.round(62.0F, 0.0F, 318.0F, 222.0F, 14.0F, 1709620, 723479, 0.96F * var21, 0.0F);
         this.round(63.0F, 0.6F, 316.0F, 1.2F, 1.0F, 16777215, 16777215, 0.1F * var21, 0.0F);
         if (var11 > 0.01F) {
            this.body(var1, var18, var19, var11);
         }
      }

      float var32 = -0.96599996F;
      float var33 = 0.0735F;
      Vector3f var34 = new Vector3f(this.rx * var32, var33 + var15, this.rz * var32)
         .add((float)(this.pos.x - var14.x), (float)(this.pos.y - var14.y), (float)(this.pos.z - var14.z));
      Vector3fc var23 = var4.getHorizontalPlane();
      Vector3fc var24 = var4.getVerticalPlane();
      Vector3f var25 = new Vector3f(var23).mul(0.9F).add(new Vector3f(var24).mul(-0.3F));
      Vector3f var26 = new Vector3f(var25).lerp(var34, var9).add(0.0F, (float)Math.sin(Math.PI * var9) * 0.45F, 0.0F);
      float var27 = 0.12F + 0.88F * outCubic(var9);
      float var28 = (1.0F - var9) * 70.0F;
      float var29 = Math.min(1.0F, (var8 < 0.0F ? var7 : 900.0F - var8) / 150.0F);
      if (var29 > 0.01F) {
         this.m = new Matrix4f(var3).translate(var26).mul(var16).rotateX((float)Math.toRadians(-var28)).scale(var27).translate(0.0F, 0.0F, 0.006F);
         this.ox = 0.0F;
         this.oy = 0.0F;
         this.record(var19, var29, var12);
      }

      if (this.hovered) {
         this.m = new Matrix4f(var17);
         this.ox = 190.0F;
         this.oy = 111.0F;
         this.ring(this.hx, this.hy, 0.0F, 2.2F, 0.0F, 360.0F, 16777215, 0.95F, 16777215, 0.95F, 0.02F, 12);
         this.ring(this.hx, this.hy, 2.2F, 5.5F, 0.0F, 360.0F, 10321407, 0.35F, 10321407, 0.0F, 0.02F, 12);
      }

      if (var2 instanceof Immediate var30) {
         var30.draw(flat());
      }

      for (Runnable var31 : this.late) {
         var31.run();
      }
   }

   private void record(NowPlaying var1, float var2, float var3) {
      float var4 = 78.0F;
      this.ring(0.0F, 0.0F, var4, var4 + 14.0F, 0.0F, 360.0F, 10321407, 0.32F * var2, 10321407, 0.0F, 0.0F, 48);
      byte var5 = 10;

      for (int var6 = 0; var6 < var5; var6++) {
         float var7 = var4 * (0.38F + 0.62F * var6 / var5);
         float var8 = var4 * (0.38F + 0.62F * (var6 + 1) / var5);
         int var9 = var6 % 2 == 0 ? 986900 : 1513246;
         this.ring(0.0F, 0.0F, var7, var8, 0.0F, 360.0F, var9, var2, var9, var2, 0.001F, 48);
      }

      this.ring(0.0F, 0.0F, var4 - 1.4F, var4, 0.0F, 360.0F, 3421247, var2, 3421247, var2, 0.002F, 48);
      float var19 = (float)Math.toDegrees(this.spin);

      for (int var20 = 0; var20 < 4; var20++) {
         float var22 = var4 * (0.48F + 0.12F * var20);
         float var24 = var19 + var20 * 97;
         this.ring(0.0F, 0.0F, var22, var22 + 1.1F, var24, var24 + 38.0F, 4539738, var2, 4539738, var2, 0.002F, 8);
      }

      this.ring(0.0F, 0.0F, var4 * 0.4F, var4 * 0.98F, 300.0F, 330.0F, 16777215, 0.02F * var2, 16777215, 0.11F * var2, 0.003F, 6);
      this.ring(0.0F, 0.0F, var4 * 0.4F, var4 * 0.98F, 120.0F, 150.0F, 16777215, 0.02F * var2, 16777215, 0.08F * var2, 0.003F, 6);
      this.ring(0.0F, 0.0F, 0.0F, var4 * 0.36F, 0.0F, 360.0F, 14471167, var2, 5914576, var2, 0.004F, 40);
      this.ring(0.0F, 0.0F, var4 * 0.33F, var4 * 0.36F, 0.0F, 360.0F, 5914576, var2, 5914576, var2, 0.005F, 40);
      this.ring(0.0F, 0.0F, 0.0F, var4 * 0.04F, 0.0F, 360.0F, 526348, var2, 526348, var2, 0.006F, 16);
      Matrix4f var21 = new Matrix4f(this.m).rotateZ(-this.spin);
      this.textAt(var21, "LYRICS CORD", 0.0F, -var4 * 0.2F, 0.5F, 16777215, var2, true);
      String var23 = var1.hasTrack() ? var1.artist : tr("nomusic");
      this.textAt(var21, ellip(var23, 70.0F, 0.42F), 0.0F, var4 * 0.1F, 0.42F, 16777215, 0.85F * var2, true);
      float var25 = var4 * 0.98F;
      float var10 = -var4 * 0.92F;
      float var11 = (float)Math.toRadians(88.0F + 15.0F * var3);
      float var12 = var4 * 1.05F;
      float var13 = (float)Math.cos(var11);
      float var14 = (float)Math.sin(var11);
      float var15 = -var14 * 1.7F;
      float var16 = var13 * 1.7F;
      float var17 = var25 + var13 * var12;
      float var18 = var10 + var14 * var12;
      this.poly(var25 + var15, var10 + var16, var17 + var15, var18 + var16, var17 - var15, var18 - var16, var25 - var15, var10 - var16, 14211298, var2, 0.008F);
      this.poly(
         var17 - 4.0F * var13 + 3.5F * -var14,
         var18 - 4.0F * var14 + 3.5F * var13,
         var17 + 5.0F * var13 + 3.5F * -var14,
         var18 + 5.0F * var14 + 3.5F * var13,
         var17 + 5.0F * var13 - 3.5F * -var14,
         var18 + 5.0F * var14 - 3.5F * var13,
         var17 - 4.0F * var13 - 3.5F * -var14,
         var18 - 4.0F * var14 - 3.5F * var13,
         11053238,
         var2,
         0.009F
      );
      this.ring(var25, var10, 0.0F, 8.0F, 0.0F, 360.0F, 5921384, var2, 2894902, var2, 0.009F, 20);
      this.ring(var25, var10, 0.0F, 3.0F, 0.0F, 360.0F, 14737642, var2, 14737642, var2, 0.01F, 12);
   }

   private void body(MinecraftClient var1, Config var2, NowPlaying var3, float var4) {
      float var5 = 197.0F;
      this.roundBtn("prev", 72.0F, var5, 9.0F, "⏮", var4, () -> SpotifyClient.control(SpotifyClient.Cmd.PREVIOUS));
      this.roundBtn("play", 98.0F, var5, 10.5F, var3.playing ? "⏸" : "▶", var4, () -> SpotifyClient.control(SpotifyClient.Cmd.PLAYPAUSE));
      this.roundBtn("next", 124.0F, var5, 9.0F, "⏭", var4, () -> SpotifyClient.control(SpotifyClient.Cmd.NEXT));
      this.hits.add(new WorldPanel.Hit(43.4F, 49.4F, 109.2F, 109.2F, () -> SpotifyClient.control(SpotifyClient.Cmd.PLAYPAUSE)));
      TextRenderer var6 = MinecraftClient.getInstance().textRenderer;
      this.text("Lyrics Cord", 206.0F, 10.0F, 0.85F, 16777215, var4);
      float var7 = this.hv("x", this.over(351.0F, 6.0F, 15.0F, 15.0F));
      this.ring(
         358.5F,
         13.5F,
         0.0F,
         7.5F,
         0.0F,
         360.0F,
         Gfx.lerpColor(16777215, 16734842, var7),
         (0.08F + 0.55F * var7) * var4,
         Gfx.lerpColor(16777215, 16734842, var7),
         (0.08F + 0.55F * var7) * var4,
         0.002F,
         16
      );
      this.text("✕", 355.5F, 10.5F, 0.7F, 16777215, var4);
      this.hits.add(new WorldPanel.Hit(351.0F, 6.0F, 15.0F, 15.0F, this::close));
      String var8 = tr("normal");
      float var9 = var6.getWidth(var8) * 0.62F + 12.0F;
      float var10 = this.hv("nrm", this.over(346.0F - var9, 6.0F, var9, 15.0F));
      this.round(346.0F - var9, 6.0F, var9, 15.0F, 7.5F, 16777215, 16777215, (0.07F + 0.12F * var10) * var4, 0.002F);
      this.text(var8, 352.0F - var9, 10.5F, 0.62F, 16777215, (0.8F + 0.2F * var10) * var4);
      this.hits.add(new WorldPanel.Hit(346.0F - var9, 6.0F, var9, 15.0F, () -> {
         var2.menuMode = 0;
         PhoneMenu.closeNow();
         var1.setScreen(new LyricsScreen());
      }));
      float var11 = 37.0F;

      for (int var12 = 0; var12 < TABS.length; var12++) {
         float var13 = 206.0F + var12 * (var11 + 4.0F);
         boolean var14 = this.page == var12;
         float var15 = this.hv("tab" + var12, this.over(var13, 26.0F, var11, 14.0F));
         int var16 = var12;
         if (var14) {
            this.round(var13, 26.0F, var11, 14.0F, 7.0F, 10321407, 8018664, 0.9F * var4, 0.002F);
         } else {
            this.round(var13, 26.0F, var11, 14.0F, 7.0F, 16777215, 16777215, (0.05F + 0.08F * var15) * var4, 0.002F);
         }

         String var17 = ellip(tr(TABS[var12]), var11 - 6.0F, 0.62F);
         this.text(var17, var13 + (var11 - var6.getWidth(var17) * 0.62F) / 2.0F, 30.0F, 0.62F, 16777215, (var14 ? 1.0F : 0.7F + 0.3F * var15) * var4);
         this.hits.add(new WorldPanel.Hit(var13, 26.0F, var11, 14.0F, () -> this.go(var16)));
      }

      String var18 = var3.hasTrack() ? "♪  " + var3.title + "  ·  " + var3.artist : "♪  " + tr("nomusic");
      this.text(ellip(var18, 160.0F, 0.62F), 206.0F, 208.0F, 0.62F, 14471167, 0.75F * var4);
      float var19 = var4 * outCubic(clamp((float)(System.currentTimeMillis() - this.pageTime) / 220.0F));
      switch (this.page) {
         case 0:
            this.skies(var2, var19);
            break;
         case 1:
            this.lyrics(var2, var19);
            break;
         case 2:
            this.style(var2, var19);
            break;
         default:
            this.more(var1, var2, var19);
      }
   }

   private void skies(Config var1, float var2) {
      SkyPreset[] var3 = LyricsScreen.skyOrder();
      int var4 = var3.length + 1;
      byte var5 = 6;
      int var6 = (var4 + var5 - 1) / var5;
      this.skyPage = Math.floorMod(this.skyPage, var6);
      float var7 = 48.0F;
      float var8 = 27.0F;

      for (int var9 = 0; var9 < var5; var9++) {
         int var10 = this.skyPage * var5 + var9;
         if (var10 >= var4) {
            break;
         }

         SkyPreset var11 = var10 == 0 ? null : var3[var10 - 1];
         float var12 = 206.0F + var9 % 3 * (var7 + 8.0F);
         float var13 = 47.0F + var9 / 3 * (var8 + 14.0F);
         boolean var14 = var11 == null ? var1.sky < 0 : var1.sky == var11.ordinal();
         float var15 = this.hv("sky" + var10, this.over(var12 - 1.0F, var13 - 1.0F, var7 + 2.0F, var8 + 12.0F));
         float var16 = var15 * 1.2F;
         if (var14) {
            this.round(var12 - 2.0F, var13 - 2.0F - var16, var7 + 4.0F, var8 + 4.0F, 6.0F, 10321407, 14471167, var2, 0.002F);
         } else if (var15 > 0.01F) {
            this.round(var12 - 1.5F, var13 - 1.5F - var16, var7 + 3.0F, var8 + 3.0F, 5.5F, 16777215, 16777215, 0.5F * var15 * var2, 0.002F);
         }

         if (var11 == null) {
            this.round(var12, var13 - var16, var7, var8, 4.0F, 7907327, 12901375, var2, 0.003F);
            this.text(
               Text.translatable("neonlyrics.ui.sky.off").getString(),
               var12 + 1.0F,
               var13 + var8 + 3.0F,
               0.56F,
               16777215,
               var2 * (var14 ? 1.0F : 0.75F + 0.25F * var15)
            );
            this.hits.add(new WorldPanel.Hit(var12 - 1.0F, var13 - 1.0F, var7 + 2.0F, var8 + 12.0F, () -> {
               if (var1.sky >= 0) {
                  var1.lastSky = var1.sky;
               }

               var1.sky = -1;
            }));
         } else {
            Identifier var17 = SkyThumbs.texture(var11);
            if (var17 != null) {
               this.image(var17, var12, var13 - var16, var7, var8, var2);
            } else {
               this.round(var12, var13 - var16, var7, var8, 4.0F, var11.top, var11.horizon, var2, 0.003F);
            }

            this.text(
               ellip(Text.translatable(var11.key()).getString(), var7, 0.56F),
               var12 + 1.0F,
               var13 + var8 + 3.0F,
               0.56F,
               16777215,
               var2 * (var14 ? 1.0F : 0.75F + 0.25F * var15)
            );
            this.hits.add(new WorldPanel.Hit(var12 - 1.0F, var13 - 1.0F, var7 + 2.0F, var8 + 12.0F, () -> {
               var1.sky = var11.ordinal();
               var1.lastSky = var1.sky;
            }));
         }
      }

      this.pager("sk", 128.0F, this.skyPage, var6, var1x -> this.skyPage += var1x, var2);
      this.slider("str", tr("strength"), 148.0F, (var1.skyStrength - 0.2F) / 1.3F, var1x -> var1.skyStrength = 0.2F + 1.3F * var1x, var2);
      this.slider("spd", tr("speed"), 167.0F, (var1.skySpeed - 0.25F) / 1.75F, var1x -> var1.skySpeed = 0.25F + 1.75F * var1x, var2);
      this.slider("pat", tr("pattern"), 186.0F, (var1.skyPattern - 0.2F) / 1.3F, var1x -> var1.skyPattern = 0.2F + 1.3F * var1x, var2);
   }

   private void lyrics(Config var1, float var2) {
      this.toggle(0, tr("lyricssky"), var1.lyricsSky, () -> var1.lyricsSky = !var1.lyricsSky, var2);
      this.toggle(1, tr("floating"), var1.lyricsStyle == 1, () -> var1.lyricsStyle = var1.lyricsStyle == 1 ? 0 : 1, var2);
      this.toggle(2, tr("songintro"), var1.fxSongIntro, () -> var1.fxSongIntro = !var1.fxSongIntro, var2);
      this.toggle(3, tr("beat"), var1.fxBeatPulse, () -> var1.fxBeatPulse = !var1.fxBeatPulse, var2);
      this.toggle(4, tr("skymusic"), var1.fxSkyMusic, () -> var1.fxSkyMusic = !var1.fxSkyMusic, var2);
      this.slider("lsz", tr("size"), 160.0F, (var1.lyricsSize - 0.8F) / 1.8F, var1x -> var1.lyricsSize = 0.8F + 1.8F * var1x, var2);
   }

   private void style(Config var1, float var2) {
      String[] var3 = Accessories.HATS;
      byte var4 = 6;
      int var5 = (var3.length + var4 - 1) / var4;
      this.hatPage = Math.floorMod(this.hatPage, var5);
      this.text(tr("hat"), 206.0F, 45.0F, 0.62F, 16777215, 0.55F * var2);

      for (int var6 = 0; var6 < var4; var6++) {
         int var7 = this.hatPage * var4 + var6;
         if (var7 >= var3.length) {
            break;
         }

         this.option(
            "hat" + var7,
            206.0F + var6 % 2 * 82,
            54 + var6 / 2 * 18,
            78.0F,
            15.0F,
            Text.translatable("neonlyrics.ui.hat." + var3[var7]).getString(),
            var1.hat == var7,
            () -> var1.hat = var7,
            var2
         );
      }

      this.pager("ht", 108.0F, this.hatPage, var5, var1x -> this.hatPage += var1x, var2);
      this.text(tr("death"), 206.0F, 125.0F, 0.62F, 16777215, 0.55F * var2);

      for (int var8 = 0; var8 < 8; var8++) {
         int var9 = var8;
         this.option(
            "de" + var8,
            206.0F + var8 % 2 * 82,
            134 + var8 / 2 * 18,
            78.0F,
            15.0F,
            Text.translatable("neonlyrics.ui.death." + var8).getString(),
            var1.deathFx == var8,
            () -> var1.deathFx = var9,
            var2
         );
      }

      this.toggle2("aur", 206.0F, 174.0F, 78.0F, tr("aura"), var1.aura, () -> var1.aura = !var1.aura, var2);
      this.toggle2("hgl", 288.0F, 174.0F, 78.0F, tr("hatglow"), var1.hatGlow, () -> var1.hatGlow = !var1.hatGlow, var2);
   }

   private void more(MinecraftClient var1, Config var2, float var3) {
      this.toggle(0, tr("hotbar"), var2.glassHotbar, () -> var2.glassHotbar = !var2.glassHotbar, var3);
      this.toggle(1, tr("island"), var2.islandEnabled, () -> var2.islandEnabled = !var2.islandEnabled, var3);
      this.toggle(2, tr("clock"), var2.showClock, () -> var2.showClock = !var2.showClock, var3);
      this.option("m0", 206.0F, 114.0F, 160.0F, 15.0F, "↗  " + tr("bigmenu"), false, () -> {
         PhoneMenu.closeNow();
         var1.setScreen(new LyricsScreen());
      }, var3);
      this.option("m1", 206.0F, 132.0F, 160.0F, 15.0F, "☄  " + tr("random"), false, SkyPreset::shuffle, var3);
      this.option("m2", 206.0F, 150.0F, 160.0F, 15.0F, "↺  " + tr("backnormal"), false, () -> {
         var2.menuMode = 0;
         this.close();
      }, var3);
      this.option("m3", 206.0F, 168.0F, 160.0F, 15.0F, "✕  " + tr("close"), false, this::close, var3);
   }

   private void toggle(int var1, String var2, boolean var3, Runnable var4, float var5) {
      this.toggle2("tg" + var1, 206.0F, 47 + var1 * 20, 160.0F, var2, var3, var4, var5);
   }

   private void toggle2(String var1, float var2, float var3, float var4, String var5, boolean var6, Runnable var7, float var8) {
      float var9 = 16.0F;
      float var10 = this.hv(var1, this.over(var2, var3, var4, var9));
      this.round(var2, var3, var4, var9, 6.0F, 16777215, 16777215, (0.035F + 0.05F * var10) * var8, 0.002F);
      this.text(ellip(var5, var4 - 34.0F, 0.68F), var2 + 7.0F, var3 + 5.0F, 0.68F, 16777215, 0.92F * var8);
      float var11 = var2 + var4 - 25.0F;
      float var12 = var3 + 4.0F;
      this.round(var11, var12, 19.0F, 8.0F, 4.0F, var6 ? 10321407 : 16777215, var6 ? 8018664 : 16777215, (var6 ? 0.95F : 0.16F) * var8, 0.003F);
      this.ring(var6 ? var11 + 15.0F : var11 + 4.0F, var12 + 4.0F, 0.0F, 3.2F, 0.0F, 360.0F, 16777215, var8, 15262966, var8, 0.004F, 12);
      this.hits.add(new WorldPanel.Hit(var2, var3, var4, var9, var7));
   }

   private void option(String var1, float var2, float var3, float var4, float var5, String var6, boolean var7, Runnable var8, float var9) {
      float var10 = this.hv(var1, this.over(var2, var3, var4, var5));
      if (var7) {
         this.round(var2, var3, var4, var5, 6.0F, 10321407, 8018664, 0.88F * var9, 0.002F);
      } else {
         this.round(var2, var3, var4, var5, 6.0F, 16777215, 16777215, (0.035F + 0.06F * var10) * var9, 0.002F);
      }

      this.text(ellip(var6, var4 - 12.0F, 0.66F), var2 + 7.0F, var3 + (var5 - 6.0F) / 2.0F, 0.66F, 16777215, (var7 ? 1.0F : 0.8F + 0.2F * var10) * var9);
      this.hits.add(new WorldPanel.Hit(var2, var3, var4, var5, var8));
   }

   private void slider(String var1, String var2, float var3, float var4, Consumer<Float> var5, float var6) {
      var4 = clamp(var4);
      float var7 = 258.0F;
      float var8 = 366.0F - var7;
      float var9 = this.hv(var1, this.over(206.0F, var3 - 4.0F, 160.0F, 14.0F));
      this.text(var2, 206.0F, var3, 0.66F, 16777215, (0.7F + 0.3F * var9) * var6);
      this.round(var7, var3 + 1.5F, var8, 4.0F, 2.0F, 16777215, 16777215, (0.12F + 0.06F * var9) * var6, 0.002F);
      this.round(var7, var3 + 1.5F, Math.max(4.0F, var8 * var4), 4.0F, 2.0F, 14471167, 10321407, var6, 0.003F);
      this.ring(var7 + var8 * var4, var3 + 3.5F, 0.0F, 4.5F + var9, 0.0F, 360.0F, 16777215, var6, 15262966, var6, 0.004F, 14);
      this.hits.add(new WorldPanel.Hit(var7 - 4.0F, var3 - 4.0F, var8 + 8.0F, 14.0F, () -> var5.accept(clamp((this.hx - var7) / var8))));
   }

   private void roundBtn(String var1, float var2, float var3, float var4, String var5, float var6, Runnable var7) {
      float var8 = this.hv(var1, this.over(var2 - var4, var3 - var4, 2.0F * var4, 2.0F * var4));
      this.ring(var2, var3, 0.0F, var4, 0.0F, 360.0F, Gfx.lerpColor(2762309, 10321407, var8), var6, Gfx.lerpColor(1841460, 8018664, var8), var6, 0.002F, 20);
      float var9 = MinecraftClient.getInstance().textRenderer.getWidth(var5) * 0.75F;
      this.text(var5, var2 - var9 / 2.0F, var3 - 3.0F, 0.75F, 16777215, var6);
      this.hits.add(new WorldPanel.Hit(var2 - var4, var3 - var4, 2.0F * var4, 2.0F * var4, var7));
   }

   private void pager(String var1, float var2, int var3, int var4, IntConsumer var5, float var6) {
      if (var4 > 1) {
         float var7 = this.hv(var1 + "l", this.over(320.0F, var2, 14.0F, 12.0F));
         float var8 = this.hv(var1 + "r", this.over(352.0F, var2, 14.0F, 12.0F));
         this.round(320.0F, var2, 14.0F, 12.0F, 6.0F, 16777215, 16777215, (0.08F + 0.2F * var7) * var6, 0.002F);
         this.round(352.0F, var2, 14.0F, 12.0F, 6.0F, 16777215, 16777215, (0.08F + 0.2F * var8) * var6, 0.002F);
         this.text("‹", 325.0F, var2 + 2.5F, 0.75F, 16777215, var6);
         this.text("›", 357.0F, var2 + 2.5F, 0.75F, 16777215, var6);
         this.text(var3 + 1 + "/" + var4, 336.0F, var2 + 3.0F, 0.6F, 16777215, 0.7F * var6);
         this.hits.add(new WorldPanel.Hit(320.0F, var2, 14.0F, 12.0F, () -> var5.accept(-1)));
         this.hits.add(new WorldPanel.Hit(352.0F, var2, 14.0F, 12.0F, () -> var5.accept(1)));
      }
   }

   private float lx(float var1) {
      return (var1 - this.ox) * 0.0105F;
   }

   private float ly(float var1) {
      return (this.oy - var1) * 0.0105F;
   }

   private void vert(float var1, float var2, float var3, int var4) {
      this.q.vertex(this.m, this.lx(var1), this.ly(var2), var3).color(var4);
   }

   private void quad(float var1, float var2, float var3, float var4, int var5, float var6, int var7, float var8, float var9) {
      int var10 = Gfx.argbRaw(var5, var6);
      int var11 = Gfx.argbRaw(var7, var8);
      this.vert(var1, var2, var9, var10);
      this.vert(var1 + var3, var2, var9, var10);
      this.vert(var1 + var3, var2 + var4, var9, var11);
      this.vert(var1, var2 + var4, var9, var11);
   }

   private void poly(float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, int var9, float var10, float var11) {
      int var12 = Gfx.argbRaw(var9, var10);
      this.vert(var1, var2, var11, var12);
      this.vert(var3, var4, var11, var12);
      this.vert(var5, var6, var11, var12);
      this.vert(var7, var8, var11, var12);
   }

   private void ring(
      float var1, float var2, float var3, float var4, float var5, float var6, int var7, float var8, int var9, float var10, float var11, int var12
   ) {
      if (!(var8 <= 0.003F) || !(var10 <= 0.003F)) {
         int var13 = Gfx.argbRaw(var7, var8);
         int var14 = Gfx.argbRaw(var9, var10);

         for (int var15 = 0; var15 < var12; var15++) {
            double var16 = Math.toRadians(var5 + (var6 - var5) * var15 / var12);
            double var18 = Math.toRadians(var5 + (var6 - var5) * (var15 + 1) / var12);
            float var20 = (float)Math.cos(var16);
            float var21 = (float)Math.sin(var16);
            float var22 = (float)Math.cos(var18);
            float var23 = (float)Math.sin(var18);
            this.vert(var1 + var20 * var3, var2 + var21 * var3, var11, var13);
            this.vert(var1 + var20 * var4, var2 + var21 * var4, var11, var14);
            this.vert(var1 + var22 * var4, var2 + var23 * var4, var11, var14);
            this.vert(var1 + var22 * var3, var2 + var23 * var3, var11, var13);
         }
      }
   }

   private void round(float var1, float var2, float var3, float var4, float var5, int var6, int var7, float var8, float var9) {
      if (!(var8 <= 0.003F)) {
         var5 = Math.min(var5, Math.min(var3, var4) / 2.0F);
         int var10 = var5 > 3.0F ? 5 : 2;
         float var11 = var5 / var10;

         for (int var12 = 0; var12 < var10; var12++) {
            float var13 = var5 - (var12 + 0.5F) * var11;
            float var14 = var5 - (float)Math.sqrt(Math.max(0.0F, var5 * var5 - var13 * var13));
            this.band(var1 + var14, var2 + var12 * var11, var3 - 2.0F * var14, var11, var2, var4, var6, var7, var8, var9);
            this.band(var1 + var14, var2 + var4 - (var12 + 1) * var11, var3 - 2.0F * var14, var11, var2, var4, var6, var7, var8, var9);
         }

         this.band(var1, var2 + var5, var3, var4 - 2.0F * var5, var2, var4, var6, var7, var8, var9);
      }
   }

   private void band(float var1, float var2, float var3, float var4, float var5, float var6, int var7, int var8, float var9, float var10) {
      if (!(var4 <= 0.0F) && !(var3 <= 0.0F)) {
         this.quad(
            var1, var2, var3, var4, Gfx.lerpColor(var7, var8, (var2 - var5) / var6), var9, Gfx.lerpColor(var7, var8, (var2 + var4 - var5) / var6), var9, var10
         );
      }
   }

   private void image(Identifier var1, float var2, float var3, float var4, float var5, float var6) {
      Matrix4f var7 = new Matrix4f(this.m);
      float var8 = this.lx(var2);
      float var9 = this.lx(var2 + var4);
      float var10 = this.ly(var3);
      float var11 = this.ly(var3 + var5);
      float var12 = 0.005F;
      int var13 = Gfx.argbRaw(16777215, var6);
      this.late.add(() -> {
         VertexConsumer var8x = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers().getBuffer(RenderLayers.text(var1));
         var8x.vertex(var7, var8, var10, var12).color(var13).texture(0.0F, 0.0F).light(15728880);
         var8x.vertex(var7, var8, var11, var12).color(var13).texture(0.0F, 1.0F).light(15728880);
         var8x.vertex(var7, var9, var11, var12).color(var13).texture(1.0F, 1.0F).light(15728880);
         var8x.vertex(var7, var9, var10, var12).color(var13).texture(1.0F, 0.0F).light(15728880);
      });
   }

   private void text(String var1, float var2, float var3, float var4, int var5, float var6) {
      this.textAt(this.m, var1, this.lx(var2) / 0.0105F, -this.ly(var3) / 0.0105F, var4, var5, var6, false);
   }

   private void textAt(Matrix4f var1, String var2, float var3, float var4, float var5, int var6, float var7, boolean var8) {
      if (!(var7 <= 0.01F) && !var2.isEmpty()) {
         TextRenderer var9 = MinecraftClient.getInstance().textRenderer;
         float var10 = var8 ? var3 - var9.getWidth(var2) * var5 / 2.0F : var3;
         Matrix4f var11 = new Matrix4f(var1).translate(var10 * 0.0105F, -var4 * 0.0105F, 0.012F).scale(0.0105F * var5, -0.0105F * var5, 0.0105F);
         int var12 = Gfx.argbRaw(var6, var7);
         this.late
            .add(
               () -> {
                  MinecraftClient var3x = MinecraftClient.getInstance();
                  var3x.textRenderer
                     .draw(
                        Text.literal(var2),
                        0.0F,
                        0.0F,
                        var12,
                        false,
                        var11,
                        var3x.getBufferBuilders().getEntityVertexConsumers(),
                        TextLayerType.NORMAL,
                        0,
                        15728880
                     );
               }
            );
      }
   }

   private static String ellip(String var0, float var1, float var2) {
      TextRenderer var3 = MinecraftClient.getInstance().textRenderer;
      if (var3.getWidth(var0) * var2 <= var1) {
         return var0;
      }

      String var4 = var0;

      while (var4.length() > 1 && var3.getWidth(var4 + "...") * var2 > var1) {
         var4 = var4.substring(0, var4.length() - 1);
      }

      return var4 + "...";
   }

   private static float clamp(float var0) {
      return Math.max(0.0F, Math.min(1.0F, var0));
   }

   private static float outCubic(float var0) {
      float var1 = 1.0F - var0;
      return 1.0F - var1 * var1 * var1;
   }

   private static float inOut(float var0) {
      return var0 < 0.5F ? 4.0F * var0 * var0 * var0 : 1.0F - (float)Math.pow(-2.0F * var0 + 2.0F, 3.0) / 2.0F;
   }

   private record Hit(float x, float y, float w, float h, Runnable run) {
   }
}
