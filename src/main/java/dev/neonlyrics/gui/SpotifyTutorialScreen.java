package dev.neonlyrics.gui;

import dev.neonlyrics.NeonLyrics;
import dev.neonlyrics.config.Config;
import dev.neonlyrics.hud.Anim;
import dev.neonlyrics.hud.Gfx;
import dev.neonlyrics.hud.Island;
import dev.neonlyrics.hud.Theme;
import dev.neonlyrics.lyrics.LocalMedia;
import dev.neonlyrics.lyrics.NowPlaying;
import dev.neonlyrics.lyrics.SpotifyClient;
import dev.neonlyrics.util.Fx;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.input.KeyInput;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;

public class SpotifyTutorialScreen extends Screen {
   private static final int STEPS = 6;
   private final Screen parent;
   private final long opened = System.currentTimeMillis();
   private long stepTime = System.currentTimeMillis();
   private int step = 0;
   private int prevStep = -1;
   private int dir = 1;
   private final List<SpotifyTutorialScreen.Hit> hits = new ArrayList<>();
   private final Anim.Pool pool = new Anim.Pool(14.0);
   private final Anim dots = new Anim(0.0, 14.0);
   private int mx;
   private int my;
   private int px;
   private int py;
   private int pw;
   private int ph;
   private TextFieldWidget idBox;

   public SpotifyTutorialScreen(Screen var1) {
      super(Text.literal("Spotify Tutorial"));
      this.parent = var1;
   }

   protected void init() {
      this.pw = Math.min(430, this.width - 16);
      this.ph = Math.min(250, this.height - 16);
      this.px = (this.width - this.pw) / 2;
      this.py = (this.height - this.ph) / 2;
      this.idBox = new TextFieldWidget(this.textRenderer, this.px + 22, this.py + 118, 170, 12, Text.literal(""));
      this.idBox.setDrawsBackground(false);
      this.idBox.setMaxLength(64);
      this.idBox.setEditableColor(-790529);
      this.idBox.setText(NeonLyrics.cfg().spotifyClientId);
      this.idBox.setPlaceholder(Text.literal(Fx.t("neonlyrics.hint.clientid")).formatted(Formatting.DARK_GRAY));
      this.addDrawableChild(this.idBox);
   }

   private void go(int var1) {
      var1 = Math.max(0, Math.min(5, var1));
      if (var1 != this.step) {
         this.dir = var1 > this.step ? 1 : -1;
         this.prevStep = this.step;
         this.step = var1;
         this.stepTime = System.currentTimeMillis();
         this.setFocused(null);
         Fx.sound(Fx.Sfx.CLICK);
      }
   }

   public void renderBackground(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
      if (NeonLyrics.cfg().blur && this.client != null && this.client.world != null) {
         super.renderBackground(context, mouseX, mouseY, deltaTicks);
      }

      float var5 = Anim.outCubic(Anim.since(this.opened, 250L));
      Gfx.alpha = 1.0F;
      Gfx.vGradient(context, 0, 0, this.width, this.height, 460044, 986135, 0.55F * var5, 0.75F * var5);
   }

   public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
      this.mx = mouseX;
      this.my = mouseY;
      this.hits.clear();
      Config var5 = NeonLyrics.cfg();
      int var6 = Theme.accent();
      float var7 = Anim.outCubic(Anim.since(this.opened, 300L));
      Gfx.alpha = var7;
      context.getMatrices().pushMatrix();
      float var8 = 0.94F + 0.06F * Anim.outBack(Anim.since(this.opened, 380L));
      context.getMatrices().translate(this.px + this.pw / 2.0F, this.py + this.ph / 2.0F);
      context.getMatrices().scale(var8, var8);
      context.getMatrices().translate(-(this.px + this.pw / 2.0F), -(this.py + this.ph / 2.0F));
      Gfx.glow(context, this.px, this.py, this.pw, this.ph, 10, var6, 0.7F, 10);
      Gfx.round(context, this.px, this.py, this.pw, this.ph, 10, 986135, 0.97F);
      Gfx.vGradient(context, this.px + 1, this.py + 1, this.pw - 2, 50, var6, 986135, 0.08F, 0.0F);
      Gfx.flowBorder(context, this.px + 4, this.py, this.pw - 8, this.ph, var6, Theme.accent2(), 0.8F, 0.0F, Anim.outCubic(Anim.since(this.opened, 700L)));
      context.getMatrices().pushMatrix();
      context.getMatrices().translate(this.px + 22, this.py + 18);
      context.getMatrices().scale(1.2F, 1.2F);
      context.drawItem(new ItemStack(Items.JUKEBOX), -8, -8);
      context.getMatrices().popMatrix();
      Gfx.text(context, Fx.t("neonlyrics.tut.title"), this.px + 36, this.py + 10, 16777215, 1.0F, true);
      Gfx.text(context, Fx.t("neonlyrics.tut.stepof", this.step + 1, 6), this.px + 36, this.py + 21, 10326968, 1.0F, false);
      float var9 = this.dots.target(this.step).get();
      byte var10 = 72;
      int var11 = this.px + this.pw - 16 - var10;

      for (int var12 = 0; var12 < 6; var12++) {
         boolean var13 = var12 < this.step;
         Gfx.round(context, var11 + var12 * 12, this.py + 15, 8, 4, 2, var13 ? var6 : 2761272, var13 ? 0.7F : 1.0F);
         this.hit(var11 + var12 * 12 - 2, this.py + 11, 12, 12, () -> {}, var12);
      }

      Gfx.round(context, var11 + Math.round(var9 * 12.0F), this.py + 14, 8, 6, 3, var6, 1.0F);
      Gfx.rect(context, this.px + 14, this.py + 34, this.pw - 28, 1, 3813456, 0.8F);
      float var16 = Anim.outCubic(Anim.since(this.stepTime, 300L));
      context.enableScissor(this.px + 4, this.py + 36, this.px + this.pw - 4, this.py + this.ph - 30);
      if (this.prevStep >= 0 && var16 < 1.0F) {
         float var17 = Gfx.alpha;
         Gfx.alpha = var7 * (1.0F - var16);
         context.getMatrices().pushMatrix();
         context.getMatrices().translate(-this.dir * var16 * 30.0F, 0.0F);
         this.content(context, this.prevStep, false);
         context.getMatrices().popMatrix();
         Gfx.alpha = var17;
      }

      float var18 = Gfx.alpha;
      Gfx.alpha = var7 * var16;
      context.getMatrices().pushMatrix();
      context.getMatrices().translate(this.prevStep >= 0 ? this.dir * (1.0F - var16) * 30.0F : 0.0F, this.prevStep < 0 ? (1.0F - var16) * 8.0F : 0.0F);
      this.content(context, this.step, true);
      context.getMatrices().popMatrix();
      Gfx.alpha = var18;
      context.disableScissor();
      int var14 = this.py + this.ph - 24;
      Gfx.rect(context, this.px + 14, var14 - 6, this.pw - 28, 1, 3813456, 0.8F);
      this.button(context, "close", this.px + 14, var14, 80, 16, Fx.t("neonlyrics.tut.close"), 10326968, false, this::close);
      if (this.step > 0) {
         this.button(
            context, "back", this.px + this.pw - 14 - 80 - 6 - 80, var14, 80, 16, Fx.t("neonlyrics.tut.back"), 10326968, false, () -> this.go(this.step - 1)
         );
      }

      boolean var15 = this.step == 5;
      this.button(
         context, "next", this.px + this.pw - 14 - 80, var14, 80, 16, var15 ? Fx.t("neonlyrics.tut.finish") : Fx.t("neonlyrics.tut.next"), var6, true, () -> {
            if (var15) {
               this.close();
            } else {
               this.go(this.step + 1);
            }
         }
      );
      context.getMatrices().popMatrix();
      this.idBox.visible = this.step == 4 && var16 > 0.9F;
      this.idBox.active = this.idBox.visible;
      if (!this.idBox.visible && this.idBox.isFocused()) {
         this.setFocused(null);
      }

      if (this.step == 4) {
         var5.spotifyClientId = this.idBox.getText().trim();
      }

      Gfx.alpha = 1.0F;
      super.render(context, mouseX, mouseY, deltaTicks);
   }

   private void content(DrawContext var1, int var2, boolean var3) {
      int var4 = Theme.accent();
      int var5 = this.px + 18;
      int var6 = this.py + 44;
      int var7 = var2 == 5 ? this.pw - 36 : 196;
      int var8 = this.px + 224;
      int var9 = this.py + 44;
      int var10 = this.pw - 224 - 16;
      int var11 = this.ph - 44 - 34;
      String var12 = Fx.t("neonlyrics.tut.s" + var2 + ".title");
      Gfx.text(var1, var12, var5, var6, 16777215, 1.0F, true);
      int var13 = var6 + 14;

      for (String var15 : Gfx.wrap(Fx.t("neonlyrics.tut.s" + var2 + ".text"), var7)) {
         Gfx.text(var1, var15, var5, var13, 15986687, 0.88F, false);
         var13 += 10;
      }

      var13 += 6;
      switch (var2) {
         case 0:
            String[] var22 = new String[]{Fx.t("neonlyrics.tut.need1"), Fx.t("neonlyrics.tut.need2"), Fx.t("neonlyrics.tut.need3")};

            for (String var18 : var22) {
               Gfx.circle(var1, var5 + 3, var13 + 3, 2.0F, var4, 1.0F);
               Gfx.text(var1, var18, var5 + 10, var13, 15986687, 0.9F, false);
               var13 += 11;
            }

            if (LocalMedia.WINDOWS) {
               this.button(var1, "useauto" + var3, var5, var13 + 6, 150, 16, Fx.t("neonlyrics.tut.useauto"), 6022299, true, () -> {
                  SpotifyClient.setMode(0);
                  this.close();
               }, var3);
            }

            this.drawWelcome(var1, var8, var9, var10, var11);
            break;
         case 1:
            this.button(
               var1,
               "dash" + var3,
               var5,
               var13,
               120,
               16,
               Fx.t("neonlyrics.spotify.dashboard"),
               6022299,
               true,
               () -> Util.getOperatingSystem().open("https://developer.spotify.com/dashboard"),
               var3
            );
            this.drawBrowser(var1, var8, var9, var10, var11, 1);
            break;
         case 2:
            Gfx.round(var1, var5, var13 - 2, var7, 13, 4, 1972265, 0.95F);
            Gfx.text(var1, "http://127.0.0.1:8789/callback", var5 + 5, var13 + 1, 16762460, 1.0F, false);
            var13 += 16;
            this.button(var1, "copy" + var3, var5, var13, 120, 16, Fx.t("neonlyrics.tut.copyuri"), 6022299, true, () -> {
               Fx.clipboard("http://127.0.0.1:8789/callback");
               Island.notify(Fx.t("neonlyrics.toast.copiedres"), true);
            }, var3);
            this.drawBrowser(var1, var8, var9, var10, var11, 2);
            break;
         case 3:
            this.drawBrowser(var1, var8, var9, var10, var11, 3);
            break;
         case 4:
            int var21 = this.py + 114;
            Gfx.text(var1, Fx.t("neonlyrics.spotify.clientid").toUpperCase(), var5, var21 - 12, 10326968, 1.0F, false);
            boolean var23 = this.idBox.isFocused();
            Gfx.glow(var1, var5, var21, 180, 18, 5, var4, var23 ? 0.8F : 0.0F, 4);
            Gfx.round(var1, var5, var21, 180, 18, 5, var23 ? 2761272 : 1972265, 0.95F);
            SpotifyClient.Status var16 = SpotifyClient.status();
            boolean var17 = var16 == SpotifyClient.Status.CONNECTED;
            this.button(
               var1,
               "connect" + var3,
               var5,
               var21 + 24,
               120,
               16,
               var17 ? Fx.t("neonlyrics.spotify.st.on") : Fx.t("neonlyrics.spotify.connect"),
               var17 ? 6022299 : var4,
               !var17,
               () -> {
                  NeonLyrics.cfg().spotifyClientId = this.idBox.getText().trim();
                  NeonLyrics.saveConfig();
                  SpotifyClient.setMode(1);
                  if (SpotifyClient.status() != SpotifyClient.Status.CONNECTED) {
                     SpotifyClient.login();
                  }
               },
               var3
            );
            this.drawStatus(var1, var8, var9, var10, var11);
            break;
         default:
            this.drawHelp(var1, var5, var13, this.pw - 36, this.py + this.ph - 34 - var13);
      }
   }

   private void drawWelcome(DrawContext var1, int var2, int var3, int var4, int var5) {
      int var6 = Theme.accent();
      Gfx.round(var1, var2, var3, var4, var5, 8, 722962, 0.95F);
      Gfx.vGradient(var1, var2 + 6, var3 + 6, var4 - 12, var5 - 12, 788246, 2758213, 1.0F, 1.0F);
      Random var7 = new Random(4L);
      double var8 = System.currentTimeMillis() / 1000.0;

      for (int var10 = 0; var10 < 40; var10++) {
         float var11 = (float)(0.5 + 0.5 * Math.sin(var8 * 2.0 + var10));
         Gfx.rect(var1, var2 + 8 + var7.nextInt(var4 - 16), var3 + 8 + var7.nextInt(var5 - 40), 1, 1, 16777215, 0.3F + 0.6F * var11);
      }

      for (int var13 = 0; var13 < var4 - 12; var13 += 2) {
         float var15 = (float)(14.0 + 8.0 * Math.sin(var13 * 0.12 + var8));
         int var12 = Gfx.lerpColor(10976255, 15129855, (float)(0.5 + 0.5 * Math.sin(var13 * 0.1 + var8)));
         var1.fillGradient(var2 + 6 + var13, var3 + 26, var2 + 8 + var13, (int)(var3 + 26 + var15), Gfx.argb(var12, 0.0F), Gfx.argb(var12, 0.4F));
      }

      Gfx.round(var1, var2 + var4 / 2 - 44, var3 + 10, 88, 11, 5, 1183260, 0.9F);
      Gfx.round(var1, var2 + var4 / 2 - 20, var3 + 12, 22, 7, 2, var6, 0.6F);
      var1.getMatrices().pushMatrix();
      var1.getMatrices().translate(var2 + var4 / 2.0F, var3 + var5 / 2.0F + 8.0F);
      var1.getMatrices().rotate(-0.08F);
      var1.getMatrices().scale(1.8F, 1.8F);
      String var14 = Fx.t("neonlyrics.tut.preview");
      Gfx.text(var1, var14, -Gfx.w(var14) / 2, 0, 13215999, 0.95F, true);
      var1.getMatrices().popMatrix();
      Gfx.rect(var1, var2 + 6, var3 + var5 - 14, var4 - 12, 8, 3037738, 0.95F);
   }

   private void drawBrowser(DrawContext var1, int var2, int var3, int var4, int var5, int var6) {
      int var7 = Theme.accent();
      float var8 = Anim.pulse(1100.0);
      Gfx.round(var1, var2, var3, var4, var5, 8, 15592946, 0.98F);
      Gfx.round(var1, var2, var3, var4, 14, 8, 14079710, 1.0F);
      Gfx.rect(var1, var2, var3 + 8, var4, 6, 14079710, 1.0F);

      for (int var9 = 0; var9 < 3; var9++) {
         Gfx.circle(var1, var2 + 8 + var9 * 6, var3 + 7, 2.0F, new int[]{16736087, 16694318, 2672704}[var9], 1.0F);
      }

      Gfx.round(var1, var2 + 30, var3 + 3, var4 - 38, 8, 3, 16777215, 1.0F);
      Gfx.text(var1, Gfx.ellipsize("developer.spotify.com/dashboard", var4 - 44), var2 + 34, var3 + 3, 5592422, 0.9F, false);
      int var16 = var3 + 20;
      int var10 = 1710626;
      int var11 = 9079449;
      int var12 = 1947988;
      switch (var6) {
         case 1:
            Gfx.text(var1, "Dashboard", var2 + 8, var16, var10, 1.0F, false);
            var16 += 16;

            for (int var24 = 0; var24 < 3; var24++) {
               Gfx.round(var1, var2 + 8, var16 + var24 * 14, var4 - 16, 10, 3, 14474468, 1.0F);
            }

            int var25 = var2 + var4 - 66;
            int var26 = var3 + 18;
            Gfx.glow(var1, var25, var26, 58, 13, 6, var12, 0.6F + 0.6F * var8, 5);
            Gfx.round(var1, var25, var26, 58, 13, 6, var12, 1.0F);
            Gfx.center(var1, "Create app", var25 + 29, var26 + 3, 16777215, 1.0F, false);
            this.arrow(var1, var25 - 14, var26 + 6, var8);
            break;
         case 2:
            Gfx.text(var1, "Create app", var2 + 8, var16, var10, 1.0F, false);
            var16 += 13;
            String[][] var13 = new String[][]{
               {"App name", "Minecraft Lyrics"}, {"App description", "Lyrics im Spiel"}, {"Redirect URIs", "http://127.0.0.1:8789/…"}
            };

            for (int var14 = 0; var14 < var13.length; var14++) {
               boolean var15 = var14 == 2;
               Gfx.text(var1, var13[var14][0], var2 + 8, var16, var11, 1.0F, false);
               var16 += 9;
               if (var15) {
                  Gfx.glow(var1, var2 + 8, var16, var4 - 16, 11, 3, var7, 0.6F + 0.6F * var8, 4);
               }

               Gfx.round(var1, var2 + 8, var16, var4 - 16, 11, 3, 16777215, 1.0F);
               Gfx.round(var1, var2 + 8, var16, var4 - 16, 1, 1, var15 ? var7 : 13158610, 1.0F);
               Gfx.text(var1, Gfx.ellipsize(var13[var14][1], var4 - 24), var2 + 11, var16 + 2, var15 ? var7 : var10, 1.0F, false);
               if (var15) {
                  Gfx.round(var1, var2 + var4 - 30, var16 + 1, 20, 9, 3, 15000812, 1.0F);
               }

               if (var15) {
                  Gfx.center(var1, "Add", var2 + var4 - 20, var16 + 2, var10, 0.8F, false);
               }

               var16 += 14;
            }

            Gfx.round(var1, var2 + 8, var16, 8, 8, 2, var12, 1.0F);
            Gfx.rect(var1, var2 + 10, var16 + 4, 2, 1, 16777215, 1.0F);
            Gfx.rect(var1, var2 + 11, var16 + 5, 1, 1, 16777215, 1.0F);
            Gfx.rect(var1, var2 + 12, var16 + 3, 1, 2, 16777215, 1.0F);
            Gfx.rect(var1, var2 + 13, var16 + 2, 1, 1, 16777215, 1.0F);
            Gfx.text(var1, "Web API", var2 + 20, var16, var10, 1.0F, false);
            Gfx.round(var1, var2 + var4 - 44, var16 - 2, 36, 12, 6, var12, 1.0F);
            Gfx.center(var1, "Save", var2 + var4 - 26, var16 + 1, 16777215, 1.0F, false);
            break;
         case 3:
            Gfx.text(var1, "Minecraft Lyrics", var2 + 8, var16, var10, 1.0F, false);
            Gfx.round(var1, var2 + var4 - 52, var16 - 2, 44, 12, 6, 14474468, 1.0F);
            Gfx.center(var1, "Settings", var2 + var4 - 30, var16 + 1, var10, 1.0F, false);
            var16 += 16;
            Gfx.text(var1, "Basic Information", var2 + 8, var16, var11, 1.0F, false);
            var16 += 12;
            Gfx.text(var1, "Client ID", var2 + 8, var16, var10, 1.0F, false);
            var16 += 10;
            Gfx.glow(var1, var2 + 8, var16, var4 - 16, 12, 3, var7, 0.6F + 0.6F * var8, 4);
            Gfx.round(var1, var2 + 8, var16, var4 - 16, 12, 3, 16777215, 1.0F);
            Gfx.text(var1, Gfx.ellipsize("3f9a1c7e2b8d4e60a5c1…", var4 - 40), var2 + 11, var16 + 2, var10, 1.0F, false);
            Gfx.round(var1, var2 + var4 - 20, var16 + 2, 8, 8, 2, var7, 1.0F);
            Gfx.round(var1, var2 + var4 - 18, var16 + 4, 4, 4, 1, 16777215, 1.0F);
            var16 += 18;
            Gfx.text(var1, "Client secret", var2 + 8, var16, var11, 1.0F, false);
            Gfx.text(var1, Fx.t("neonlyrics.tut.nosecret"), var2 + 8, var16 + 10, 12597547, 1.0F, false);
      }
   }

   private void arrow(DrawContext var1, int var2, int var3, float var4) {
      int var5 = (int)(var4 * 3.0F);
      int var6 = Theme.accent();
      Gfx.rect(var1, var2 - 8 + var5, var3, 8, 2, var6, 1.0F);
      Gfx.rect(var1, var2 - 2 + var5, var3 - 2, 2, 6, var6, 1.0F);
      Gfx.rect(var1, var2 + var5, var3 - 1, 2, 4, var6, 1.0F);
   }

   private void drawStatus(DrawContext var1, int var2, int var3, int var4, int var5) {
      int var6 = Theme.accent();
      SpotifyClient.Status var7 = SpotifyClient.status();
      NowPlaying var8 = SpotifyClient.now();
      Gfx.round(var1, var2, var3, var4, var5, 8, 1972265, 0.95F);

      int var9 = switch (var7) {
         case CONNECTED -> 6022299;
         case ERROR -> 16735354;
         case WAITING_LOGIN -> 16762460;
         default -> 10326968;
      };
      Gfx.text(var1, Fx.t("neonlyrics.tut.status"), var2 + 10, var3 + 8, 10326968, 1.0F, false);
      float var10 = var7 == SpotifyClient.Status.WAITING_LOGIN ? Anim.pulse(700.0) : 1.0F;
      Gfx.circle(var1, var2 + 13, var3 + 26, 4.0F, var9, 0.4F + 0.6F * var10);

      String var11 = switch (var7) {
         case CONNECTED -> Fx.t("neonlyrics.spotify.st.on");
         case ERROR -> Fx.t("neonlyrics.spotify.st.error", SpotifyClient.lastError());
         case WAITING_LOGIN -> Fx.t("neonlyrics.spotify.st.wait");
         case NOT_CONFIGURED -> Fx.t("neonlyrics.spotify.st.setup");
         case DISCONNECTED -> Fx.t("neonlyrics.spotify.st.off");
      };
      int var12 = var3 + 22;

      for (String var14 : Gfx.wrap(var11, var4 - 30)) {
         Gfx.text(var1, var14, var2 + 22, var12, var9, 1.0F, false);
         var12 += 10;
      }

      var12 += 6;
      if (var7 == SpotifyClient.Status.CONNECTED) {
         String var17 = var8.hasTrack() ? "♪ " + var8.title : Fx.t("neonlyrics.tut.playsong");

         for (String var15 : Gfx.wrap(var17, var4 - 20)) {
            Gfx.text(var1, var15, var2 + 10, var12, 15986687, 0.9F, false);
            var12 += 10;
         }
      } else if (var7 == SpotifyClient.Status.WAITING_LOGIN) {
         for (String var20 : Gfx.wrap(Fx.t("neonlyrics.tut.browserhint"), var4 - 20)) {
            Gfx.text(var1, var20, var2 + 10, var12, 10326968, 0.9F, false);
            var12 += 10;
         }
      }

      Gfx.rect(var1, var2 + 10, var3 + var5 - 20, var4 - 20, 1, 3813456, 0.8F);
      Gfx.text(var1, Gfx.ellipsize(Fx.t("neonlyrics.tut.local"), var4 - 20), var2 + 10, var3 + var5 - 14, 10326968, 0.85F, false);
      if (var7 == SpotifyClient.Status.CONNECTED) {
         Gfx.round(var1, var2, var3, 3, var5, 2, var6, 1.0F);
      }
   }

   private void drawHelp(DrawContext var1, int var2, int var3, int var4, int var5) {
      int var6 = (var4 - 6) / 2;
      int var7 = (var5 - 6) / 2;

      for (int var8 = 0; var8 < 4; var8++) {
         int var9 = var2 + var8 % 2 * (var6 + 6);
         int var10 = var3 + var8 / 2 * (var7 + 6);
         Gfx.round(var1, var9, var10, var6, var7, 6, 1972265, 0.95F);
         Gfx.round(var1, var9, var10 + 5, 2, var7 - 10, 1, 16762460, 1.0F);
         Gfx.text(var1, Gfx.ellipsize(Fx.t("neonlyrics.tut.err" + (var8 + 1)), var6 - 14), var9 + 8, var10 + 5, 16762460, 1.0F, false);
         int var11 = var10 + 16;

         for (String var13 : Gfx.wrap(Fx.t("neonlyrics.tut.fix" + (var8 + 1)), var6 - 14)) {
            if (var11 > var10 + var7 - 9) {
               break;
            }

            Gfx.text(var1, var13, var9 + 8, var11, 15986687, 0.85F, false);
            var11 += 9;
         }
      }
   }

   private void button(DrawContext var1, String var2, int var3, int var4, int var5, int var6, String var7, int var8, boolean var9, Runnable var10) {
      this.button(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, true);
   }

   private void button(
      DrawContext var1, String var2, int var3, int var4, int var5, int var6, String var7, int var8, boolean var9, Runnable var10, boolean var11
   ) {
      boolean var12 = var11 && this.mx >= var3 && this.my >= var4 && this.mx < var3 + var5 && this.my < var4 + var6;
      float var13 = this.pool.to("b" + var2, var12 ? 1.0 : 0.0);
      Gfx.glow(var1, var3, var4, var5, var6, 5, var8, var13 * 0.8F, 4);
      Gfx.round(var1, var3, var4, var5, var6, 5, var9 ? var8 : Gfx.lerpColor(1972265, var8, 0.12F + 0.2F * var13), var9 ? 0.3F + 0.25F * var13 : 0.95F);
      Gfx.center(
         var1,
         Gfx.ellipsize(var7, var5 - 6),
         var3 + var5 / 2,
         var4 + (var6 - 8) / 2,
         Gfx.lerpColor(Gfx.lerpColor(15986687, var8, 0.3F), 16777215, var13 * 0.6F),
         1.0F,
         false
      );
      if (var11) {
         this.hits.add(new SpotifyTutorialScreen.Hit(var3, var4, var5, var6, () -> {
            Fx.sound(Fx.Sfx.CLICK);
            var10.run();
         }));
      }
   }

   private void hit(int var1, int var2, int var3, int var4, Runnable var5, int var6) {
      this.hits.add(new SpotifyTutorialScreen.Hit(var1, var2, var3, var4, () -> this.go(var6)));
   }

   public boolean mouseClicked(Click click, boolean doubled) {
      if (super.mouseClicked(click, doubled)) {
         return true;
      }

      if (click.button() != 0) {
         return false;
      }

      for (int var3 = this.hits.size() - 1; var3 >= 0; var3--) {
         SpotifyTutorialScreen.Hit var4 = this.hits.get(var3);
         if (click.x() >= var4.x() && click.y() >= var4.y() && click.x() < var4.x() + var4.w() && click.y() < var4.y() + var4.h()) {
            this.setFocused(null);
            var4.r().run();
            return true;
         }
      }

      this.setFocused(null);
      return false;
   }

   public boolean keyPressed(KeyInput input) {
      int var2 = input.key();
      if (this.getFocused() == null) {
         if (var2 == 262 || var2 == 257 || var2 == 335) {
            if (this.step == 5) {
               this.close();
            } else {
               this.go(this.step + 1);
            }

            return true;
         }

         if (var2 == 263) {
            this.go(this.step - 1);
            return true;
         }
      }

      return super.keyPressed(input);
   }

   public void close() {
      Config var1 = NeonLyrics.cfg();
      var1.spotifyClientId = this.idBox.getText().trim();
      var1.spotifyTutorialSeen = true;
      NeonLyrics.saveConfig();
      this.client.setScreen(this.parent);
   }

   public boolean shouldPause() {
      return false;
   }

   private record Hit(int x, int y, int w, int h, Runnable r) {
   }
}
