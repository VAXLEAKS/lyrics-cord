package dev.neonlyrics.gui;

import dev.neonlyrics.NeonLyrics;
import dev.neonlyrics.config.Config;
import dev.neonlyrics.hud.Anim;
import dev.neonlyrics.hud.Gfx;
import dev.neonlyrics.hud.Theme;
import dev.neonlyrics.sky.SkyImages;
import dev.neonlyrics.sky.SkyPreset;
import dev.neonlyrics.util.Fx;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Random;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.input.KeyInput;
import net.minecraft.text.Style;
import net.minecraft.text.StyleSpriteSource;
import net.minecraft.text.Text;
import net.minecraft.text.StyleSpriteSource.Font;
import net.minecraft.util.Identifier;

public class SkyStudioScreen extends Screen {
   private static final StyleSpriteSource BOLD = fd("menu_b");
   private static final StyleSpriteSource SMALL = fd("menu_s");
   private static final StyleSpriteSource TITLE = fd("menu_t");
   private static final int GLASS = 986644;
   private static final int TEXT = 15658738;
   private static final int SUB = 9210008;
   private static final int CHIP = 1907493;
   private static final String[] SLOTS_EN = new String[]{"Top", "Horizon", "Aurora 1", "Aurora 2", "Moon"};
   private static final String[] SLOTS_DE = new String[]{"Oben", "Horizont", "Polarlicht 1", "Polarlicht 2", "Mond"};
   private final Screen parent;
   private final List<SkyStudioScreen.Hit> hits = new ArrayList<>();
   private final long openAt = System.currentTimeMillis();
   private int slot;
   private float hue;
   private float sat;
   private float val;
   private int drag = -1;
   private float bx;
   private float bw;
   private final float[] barY = new float[5];
   private String toast = "";
   private float sc = 1.0F;
   private long toastAt;

   public SkyStudioScreen(Screen var1) {
      super(Text.literal("Sky Studio"));
      this.parent = var1;
   }

   private static StyleSpriteSource fd(String var0) {
      return new Font(Identifier.of("neonlyrics", var0));
   }

   private boolean de() {
      return this.client != null && this.client.options.language.startsWith("de");
   }

   private String t(String var1, String var2) {
      return this.de() ? var2 : var1;
   }

   protected void init() {
      Config var1 = NeonLyrics.cfg();
      var1.sky = SkyPreset.CUSTOM.ordinal();
      var1.lastSky = var1.sky;
      SkyPreset.applyCustom();
      this.loadSlot();
   }

   public boolean shouldPause() {
      return false;
   }

   public void renderBackground(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
   }

   public void close() {
      NeonLyrics.saveConfig();
      this.client.setScreen(this.parent);
   }

   private int slotColor(int var1) {
      Config var2 = NeonLyrics.cfg();

      return switch (var1) {
         case 0 -> var2.customTop;
         case 1 -> var2.customHorizon;
         case 2 -> var2.customAurora1;
         case 3 -> var2.customAurora2;
         default -> var2.customMoonColor;
      };
   }

   private void setSlotColor(int var1, int var2) {
      Config var3 = NeonLyrics.cfg();
      switch (var1) {
         case 0:
            var3.customTop = var2;
            break;
         case 1:
            var3.customHorizon = var2;
            break;
         case 2:
            var3.customAurora1 = var2;
            break;
         case 3:
            var3.customAurora2 = var2;
            break;
         default:
            var3.customMoonColor = var2;
      }

      this.changed();
   }

   private void loadSlot() {
      int var1 = this.slotColor(this.slot);
      float[] var2 = Color.RGBtoHSB(var1 >> 16 & 0xFF, var1 >> 8 & 0xFF, var1 & 0xFF, null);
      this.hue = var2[0];
      this.sat = var2[1];
      this.val = var2[2];
   }

   private static int hsv(float var0, float var1, float var2) {
      return Color.HSBtoRGB((var0 % 1.0F + 1.0F) % 1.0F, Math.max(0.0F, Math.min(1.0F, var1)), Math.max(0.0F, Math.min(1.0F, var2))) & 16777215;
   }

   private void changed() {
      Config var1 = NeonLyrics.cfg();
      var1.sky = SkyPreset.CUSTOM.ordinal();
      SkyPreset.applyCustom();
   }

   private void say(String var1) {
      this.toast = var1;
      this.toastAt = System.currentTimeMillis();
   }

   private void randomize() {
      Config var1 = NeonLyrics.cfg();
      Random var2 = new Random();
      float var3 = var2.nextFloat();
      boolean var4 = var2.nextFloat() < 0.25F;
      var1.customTop = var4 ? hsv(var3, 0.55F, 0.55F + var2.nextFloat() * 0.2F) : hsv(var3, 0.75F, 0.06F + var2.nextFloat() * 0.12F);
      var1.customHorizon = hsv(var3 + 0.05F + var2.nextFloat() * 0.12F, 0.55F + var2.nextFloat() * 0.3F, var4 ? 0.95F : 0.3F + var2.nextFloat() * 0.35F);
      float var5 = var3 + (var2.nextBoolean() ? 0.33F : 0.5F) + var2.nextFloat() * 0.08F;
      var1.customAurora1 = hsv(var5, 0.65F, 1.0F);
      var1.customAurora2 = hsv(var5 + 0.12F + var2.nextFloat() * 0.15F, 0.45F, 1.0F);
      var1.customAurora = !var4 && var2.nextFloat() < 0.7F;
      var1.customStars = !var4;
      var1.customStarAmount = 0.6F + var2.nextFloat() * 1.2F;
      var1.customMoon = !var4 && var2.nextFloat() < 0.45F;
      var1.customMoonColor = var2.nextFloat() < 0.6F ? 15920861 : hsv(var5, 0.25F, 1.0F);
      var1.customShooting = !var4 && var2.nextFloat() < 0.6F;
      int[] var6 = new int[]{0, 0, 0, 0, 1, 5, 7};
      var1.customWeather = var6[var2.nextInt(var6.length)];
      var1.customStrength = 0.75F + var2.nextFloat() * 0.25F;
      this.changed();
      this.loadSlot();
   }

   private static String encode(Config var0) {
      byte[] var1 = new byte[21];
      int var2 = 0;
      var1[var2++] = 1;

      for (int var6 : new int[]{var0.customTop, var0.customHorizon, var0.customAurora1, var0.customAurora2, var0.customMoonColor}) {
         var1[var2++] = (byte)(var6 >> 16);
         var1[var2++] = (byte)(var6 >> 8);
         var1[var2++] = (byte)var6;
      }

      var1[var2++] = (byte)((var0.customAurora ? 1 : 0) | (var0.customStars ? 2 : 0) | (var0.customMoon ? 4 : 0) | (var0.customShooting ? 8 : 0));
      var1[var2++] = (byte)Math.round(var0.customStarAmount * 100.0F);
      var1[var2++] = (byte)var0.customWeather;
      var1[var2] = (byte)Math.round(var0.customStrength * 100.0F);
      return "LCSKY-" + Base64.getUrlEncoder().withoutPadding().encodeToString(var1);
   }

   private static boolean decode(String var0, Config var1) {
      try {
         String var2 = var0.trim();
         if (!var2.startsWith("LCSKY-")) {
            return false;
         }

         byte[] var3 = Base64.getUrlDecoder().decode(var2.substring(6));
         if (var3.length >= 21 && var3[0] == 1) {
            int[] var4 = new int[5];

            for (int var5 = 0; var5 < 5; var5++) {
               var4[var5] = (var3[1 + var5 * 3] & 255) << 16 | (var3[2 + var5 * 3] & 255) << 8 | var3[3 + var5 * 3] & 255;
            }

            var1.customTop = var4[0];
            var1.customHorizon = var4[1];
            var1.customAurora1 = var4[2];
            var1.customAurora2 = var4[3];
            var1.customMoonColor = var4[4];
            byte var7 = var3[16];
            var1.customAurora = (var7 & 1) != 0;
            var1.customStars = (var7 & 2) != 0;
            var1.customMoon = (var7 & 4) != 0;
            var1.customShooting = (var7 & 8) != 0;
            var1.customStarAmount = (var3[17] & 0xFF) / 100.0F;
            var1.customWeather = var3[18];
            var1.customStrength = (var3[19] & 0xFF) / 100.0F;
            var1.clamp();
            return true;
         } else {
            return false;
         }
      } catch (Exception var6) {
         return false;
      }
   }

   public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
      super.render(context, mouseX, mouseY, deltaTicks);
      this.hits.clear();
      int var5 = this.width;
      int var6 = this.height;
      this.sc = Math.min(1.0F, (var6 - 8) / 472.0F);
      this.width = Math.round(var5 / this.sc);
      this.height = Math.round(var6 / this.sc);
      mouseX = Math.round(mouseX / this.sc);
      mouseY = Math.round(mouseY / this.sc);
      context.getMatrices().pushMatrix();
      context.getMatrices().scale(this.sc, this.sc);

      try {
         this.drawPanel(context, mouseX, mouseY);
      } finally {
         context.getMatrices().popMatrix();
         this.width = var5;
         this.height = var6;
      }
   }

   private void drawPanel(DrawContext var1, int var2, int var3) {
      Config var4 = NeonLyrics.cfg();
      int var5 = Theme.accent();
      float var6 = Anim.outCubic(Anim.clamp((float)(System.currentTimeMillis() - this.openAt) / 260.0F));
      Gfx.alpha = var6;
      float var7 = 236.0F;
      float var8 = this.width - var7 - 12.0F + (1.0F - var6) * 40.0F;
      float var9 = 10.0F;
      float var10 = this.height - 20;
      Gfx.aaRound(var1, var8, var9, var7, var10, 16.0F, 16777215, 0.08F);
      Gfx.aaRound(var1, var8 + 1.0F, var9 + 1.0F, var7 - 2.0F, var10 - 2.0F, 15.0F, 986644, 0.9F);
      this.hits.add(new SkyStudioScreen.Hit(var8, var9, var7, var10, () -> {}));
      float var11 = var8 + 14.0F;
      float var12 = var7 - 28.0F;
      float var13 = var9 + 14.0F;
      this.txt(var1, "Sky Studio", var11, var13, 15658738, TITLE);
      this.txt(var1, this.t("Look up – changes show live", "Schau nach oben – alles live"), var11, var13 + 14.0F, 9210008, SMALL);
      var13 += 34.0F;
      List var14 = SkyImages.list();
      int var15 = var14.indexOf(var4.customImage);
      String var16 = var15 < 0 ? this.t("No image (colors)", "Kein Bild (Farben)") : (String)var14.get(var15);
      if (var16.length() > 22) {
         var16 = var16.substring(0, 20) + "…";
      }

      float var17 = 58.0F;
      Gfx.aaRound(var1, var11, var13, var12 - var17 - 6.0F, 22.0F, 11.0F, var15 >= 0 ? var5 : 1907493, 1.0F);
      this.txtC(var1, "‹  " + var16 + "  ›", var11 + (var12 - var17 - 6.0F) / 2.0F, var13 + 7.0F, 16777215, SMALL);
      Runnable var18 = () -> {
         List var2x = SkyImages.list();
         if (var2x.isEmpty()) {
            SkyImages.openFolder();
            this.say(this.t("Put PNG/JPG images in this folder", "Leg PNG/JPG-Bilder in diesen Ordner"));
         } else {
            int var3x = var2x.indexOf(var4.customImage) + 1;
            var4.customImage = var3x >= var2x.size() ? "" : (String)var2x.get(var3x);
            this.changed();
         }
      };
      this.hits.add(new SkyStudioScreen.Hit(var11, var13, var12 - var17 - 6.0F, 22.0F, var18));
      boolean var19 = var2 >= var11 + var12 - var17 && var2 < var11 + var12 && var3 >= var13 && var3 < var13 + 22.0F;
      Gfx.aaRound(var1, var11 + var12 - var17, var13, var17, 22.0F, 11.0F, var19 ? 2762547 : 1907493, 1.0F);
      this.txtC(var1, this.t("Folder", "Ordner"), var11 + var12 - var17 / 2.0F, var13 + 7.0F, 15658738, SMALL);
      this.hits.add(new SkyStudioScreen.Hit(var11 + var12 - var17, var13, var17, 22.0F, () -> {
         SkyImages.openFolder();
         this.say(this.t("Put PNG/JPG images in this folder", "Leg PNG/JPG-Bilder in diesen Ordner"));
      }));
      var13 += 32.0F;
      String[] var20 = this.de() ? SLOTS_DE : SLOTS_EN;
      float var21 = var12 / 5.0F;

      for (int var22 = 0; var22 < 5; var22++) {
         float var23 = var11 + var21 * var22 + var21 / 2.0F;
         boolean var24 = var22 == this.slot;
         boolean var25 = var22 < 2 || (var22 < 4 ? var4.customAurora : var4.customMoon);
         if (var24) {
            Gfx.aaCircle(var1, var23, var13 + 10.0F, 12.5F, 16777215, 1.0F);
         }

         Gfx.aaCircle(var1, var23, var13 + 10.0F, var24 ? 10.5F : 10.0F, 0, 0.6F);
         Gfx.aaCircle(var1, var23, var13 + 10.0F, 9.5F, this.slotColor(var22), var25 ? 1.0F : 0.35F);
         this.txtC(var1, var20[var22], var23, var13 + 25.0F, var24 ? 15658738 : 9210008, SMALL);
         int var26 = var22;
         this.hits.add(new SkyStudioScreen.Hit(var23 - var21 / 2.0F, var13 - 2.0F, var21, 36.0F, () -> {
            this.slot = var26;
            this.loadSlot();
         }));
      }

      var13 += 44.0F;
      this.bx = var11;
      this.bw = var12;
      var13 = this.colorBar(var1, 0, this.t("Hue", "Farbton"), var13, var2, var3);
      var13 = this.colorBar(var1, 1, this.t("Saturation", "Sättigung"), var13, var2, var3);
      var13 = this.colorBar(var1, 2, this.t("Brightness", "Helligkeit"), var13, var2, var3);
      var13 += 4.0F;
      String[] var47 = new String[]{this.t("Aurora", "Polarlicht"), this.t("Moon", "Mond"), this.t("Stars", "Sterne"), this.t("Comets", "Schnuppen")};
      boolean[] var48 = new boolean[]{var4.customAurora, var4.customMoon, var4.customStars, var4.customShooting};
      float var49 = (var12 - 18.0F) / 4.0F;

      for (int var50 = 0; var50 < 4; var50++) {
         float var52 = var11 + var50 * (var49 + 6.0F);
         Gfx.aaRound(var1, var52, var13, var49, 20.0F, 10.0F, var48[var50] ? var5 : 1907493, 1.0F);
         this.txtC(var1, var47[var50], var52 + var49 / 2.0F, var13 + 6.0F, var48[var50] ? 16777215 : 9210008, SMALL);
         int var27 = var50;
         this.hits.add(new SkyStudioScreen.Hit(var52, var13, var49, 20.0F, () -> {
            switch (var27) {
               case 0:
                  var4.customAurora = !var4.customAurora;
                  break;
               case 1:
                  var4.customMoon = !var4.customMoon;
                  break;
               case 2:
                  var4.customStars = !var4.customStars;
                  break;
               default:
                  var4.customShooting = !var4.customShooting;
            }

            this.changed();
         }));
      }

      var13 += 30.0F;
      var13 = this.slider(var1, 3, this.t("Star amount", "Sternen-Menge"), var4.customStarAmount / 2.0F, var13, var5);
      var13 = this.slider(var1, 4, this.t("Glow", "Leuchtkraft"), (var4.customStrength - 0.4F) / 0.6F, var13, var5);
      String[] var51 = this.de()
         ? new String[]{"Klar", "Schnee", "Schneesturm", "Regen", "Gewitter", "Blüten", "Glut", "Glühwürmchen"}
         : new String[]{"Clear", "Snow", "Blizzard", "Rain", "Thunder", "Petals", "Embers", "Fireflies"};
      int var53 = Math.floorMod(var4.customWeather, var51.length);
      Gfx.aaRound(var1, var11, var13, var12, 22.0F, 11.0F, 1907493, 1.0F);
      this.txt(var1, this.t("Weather", "Wetter"), var11 + 10.0F, var13 + 7.0F, 9210008, SMALL);
      this.txtC(var1, "‹  " + var51[var53] + "  ›", var11 + var12 - 60.0F, var13 + 7.0F, 15658738, BOLD);
      this.hits.add(new SkyStudioScreen.Hit(var11 + var12 - 120.0F, var13, 60.0F, 22.0F, () -> {
         var4.customWeather = Math.floorMod(var4.customWeather - 1, var51.length);
         this.changed();
      }));
      this.hits.add(new SkyStudioScreen.Hit(var11 + var12 - 60.0F, var13, 60.0F, 22.0F, () -> {
         var4.customWeather = Math.floorMod(var4.customWeather + 1, var51.length);
         this.changed();
      }));
      var13 += 32.0F;
      String[] var54 = new String[]{
         this.t("Random", "Zufall"), this.t("Save", "Speichern"), this.t("Copy code", "Code kopieren"), this.t("Paste code", "Code einfügen")
      };
      float var28 = (var12 - 6.0F) / 2.0F;

      for (int var29 = 0; var29 < 4; var29++) {
         float var30 = var11 + var29 % 2 * (var28 + 6.0F);
         float var31 = var13 + var29 / 2 * 26;
         boolean var32 = var2 >= var30 && var2 < var30 + var28 && var3 >= var31 && var3 < var31 + 20.0F;
         Gfx.aaRound(var1, var30, var31, var28, 20.0F, 10.0F, var29 == 0 ? var5 : (var32 ? 2762547 : 1907493), 1.0F);
         this.txtC(var1, var54[var29], var30 + var28 / 2.0F, var31 + 6.0F, 16777215, var29 == 0 ? BOLD : SMALL);
         int var33 = var29;
         this.hits.add(new SkyStudioScreen.Hit(var30, var31, var28, 20.0F, () -> this.button(var33)));
      }

      var13 += 58.0F;
      this.txt(var1, this.t("Saved skies", "Gespeicherte Himmel") + "  " + var4.customSaved.size() + "/8", var11, var13, 9210008, SMALL);
      this.txt(
         var1,
         this.t("right-click = delete", "Rechtsklick = löschen"),
         var11 + var12 - this.tw(this.t("right-click = delete", "Rechtsklick = löschen"), SMALL),
         var13,
         9210008,
         SMALL
      );
      var13 += 14.0F;

      for (int var55 = 0; var55 < 8; var55++) {
         float var57 = var11 + var55 * (var12 / 8.0F) + var12 / 16.0F;
         float var58 = var13 + 11.0F;
         if (var55 < var4.customSaved.size()) {
            Config var60 = new Config();
            if (decode(var4.customSaved.get(var55), var60)) {
               Gfx.aaCircle(var1, var57, var58, 10.5F, 16777215, encode(var4).equals(var4.customSaved.get(var55)) ? 1.0F : 0.15F);
               Gfx.aaCircle(var1, var57, var58, 9.5F, var60.customHorizon, 1.0F);
               Gfx.aaRound(var1, var57 - 9.5F, var58 - 9.5F, 19.0F, 11.0F, 6.0F, var60.customTop, 1.0F);
               if (var60.customAurora) {
                  Gfx.aaCircle(var1, var57 + 3.0F, var58 - 3.0F, 2.6F, var60.customAurora1, 1.0F);
               }
            }

            int var62 = var55;
            this.hits.add(new SkyStudioScreen.Hit(var57 - 11.0F, var58 - 11.0F, 22.0F, 22.0F, () -> {
               if (decode(var4.customSaved.get(var62), var4)) {
                  this.changed();
                  this.loadSlot();
               }
            }));
         } else {
            Gfx.aaCircle(var1, var57, var58, 9.5F, 16777215, 0.06F);
         }
      }

      long var56 = System.currentTimeMillis() - this.toastAt;
      if (!this.toast.isEmpty() && var56 < 1800L) {
         float var59 = Math.min(1.0F, (float)(1800L - var56) / 300.0F);
         int var61 = this.tw(this.toast, BOLD) + 24;
         Gfx.alpha = var59;
         Gfx.aaRound(var1, (this.width - var7) / 2.0F - var61 / 2.0F, this.height - 44, var61, 22.0F, 11.0F, 986644, 0.9F);
         this.txtC(var1, this.toast, (this.width - var7) / 2.0F, this.height - 37, 15658738, BOLD);
      }

      Gfx.alpha = 1.0F;
   }

   private float colorBar(DrawContext var1, int var2, String var3, float var4, int var5, int var6) {
      this.txt(var1, var3, this.bx, var4, 9210008, SMALL);
      var4 += 11.0F;
      byte var7 = 48;
      float var8 = this.bw / var7;

      for (int var9 = 0; var9 < var7; var9++) {
         float var10 = (var9 + 0.5F) / var7;
         int var11 = var2 == 0 ? hsv(var10, 1.0F, 1.0F) : (var2 == 1 ? hsv(this.hue, var10, Math.max(0.35F, this.val)) : hsv(this.hue, this.sat, var10));
         Gfx.rect(
            var1,
            Math.round(this.bx + var9 * var8),
            Math.round(var4),
            Math.max(1, Math.round(this.bx + (var9 + 1) * var8) - Math.round(this.bx + var9 * var8)),
            10,
            var11,
            1.0F
         );
      }

      float var13 = var2 == 0 ? this.hue : (var2 == 1 ? this.sat : this.val);
      float var14 = this.bx + var13 * this.bw;
      Gfx.aaCircle(var1, var14, var4 + 5.0F, 7.0F, 16777215, 1.0F);
      Gfx.aaCircle(var1, var14, var4 + 5.0F, 5.0F, hsv(this.hue, this.sat, this.val), 1.0F);
      this.barY[var2] = var4;
      this.hits.add(new SkyStudioScreen.Hit(this.bx - 4.0F, var4 - 4.0F, this.bw + 8.0F, 18.0F, () -> this.drag = var2));
      return var4 + 20.0F;
   }

   private float slider(DrawContext var1, int var2, String var3, float var4, float var5, int var6) {
      var4 = Math.max(0.0F, Math.min(1.0F, var4));
      this.txt(var1, var3, this.bx, var5, 9210008, SMALL);
      var5 += 12.0F;
      Gfx.aaRound(var1, this.bx, var5 + 3.0F, this.bw, 4.0F, 2.0F, 1907493, 1.0F);
      Gfx.aaRound(var1, this.bx, var5 + 3.0F, this.bw * var4, 4.0F, 2.0F, var6, 1.0F);
      Gfx.aaCircle(var1, this.bx + this.bw * var4, var5 + 5.0F, 6.0F, 16777215, 1.0F);
      this.barY[var2] = var5;
      this.hits.add(new SkyStudioScreen.Hit(this.bx - 4.0F, var5 - 4.0F, this.bw + 8.0F, 18.0F, () -> this.drag = var2));
      return var5 + 18.0F;
   }

   private void setFromMouse(double var1) {
      float var3 = (float)Math.max(0.0, Math.min(1.0, (var1 - this.bx) / this.bw));
      Config var4 = NeonLyrics.cfg();
      switch (this.drag) {
         case 0:
            this.hue = Math.min(0.999F, var3);
            break;
         case 1:
            this.sat = var3;
            break;
         case 2:
            this.val = var3;
            break;
         case 3:
            var4.customStarAmount = var3 * 2.0F;
            break;
         case 4:
            var4.customStrength = 0.4F + var3 * 0.6F;
      }

      if (this.drag <= 2) {
         this.setSlotColor(this.slot, hsv(this.hue, this.sat, this.val));
      } else {
         this.changed();
      }
   }

   private void button(int var1) {
      Config var2 = NeonLyrics.cfg();
      switch (var1) {
         case 0:
            this.randomize();
            break;
         case 1:
            String var3 = encode(var2);
            if (var2.customSaved.contains(var3)) {
               this.say(this.t("Already saved", "Schon gespeichert"));
            } else {
               if (var2.customSaved.size() >= 8) {
                  var2.customSaved.remove(0);
               }

               var2.customSaved.add(var3);
               NeonLyrics.saveConfig();
               this.say(this.t("Sky saved", "Himmel gespeichert"));
            }
            break;
         case 2:
            this.client.keyboard.setClipboard(encode(var2));
            this.say(this.t("Code copied – share it!", "Code kopiert – teil ihn!"));
            break;
         default:
            if (decode(this.client.keyboard.getClipboard(), var2)) {
               this.changed();
               this.loadSlot();
               this.say(this.t("Sky loaded", "Himmel geladen"));
            } else {
               this.say(this.t("No sky code in clipboard", "Kein Himmel-Code kopiert"));
            }
      }
   }

   public boolean mouseClicked(Click click, boolean doubled) {
      double var3 = click.x() / this.sc;
      double var5 = click.y() / this.sc;
      if (click.button() == 1) {
         Config var11 = NeonLyrics.cfg();

         for (int var12 = this.hits.size() - 1; var12 >= 0; var12--) {
            SkyStudioScreen.Hit var9 = this.hits.get(var12);
            if (var9.w == 22.0F && var9.h == 22.0F && var3 >= var9.x && var3 < var9.x + var9.w && var5 >= var9.y && var5 < var9.y + var9.h) {
               int var10 = this.savedIndexAt(var3);
               if (var10 >= 0 && var10 < var11.customSaved.size()) {
                  var11.customSaved.remove(var10);
                  NeonLyrics.saveConfig();
                  this.say(this.t("Deleted", "Gelöscht"));
               }

               return true;
            }
         }

         return false;
      } else {
         for (int var7 = this.hits.size() - 1; var7 >= 0; var7--) {
            SkyStudioScreen.Hit var8 = this.hits.get(var7);
            if (var3 >= var8.x && var3 < var8.x + var8.w && var5 >= var8.y && var5 < var8.y + var8.h) {
               Fx.sound(Fx.Sfx.CLICK);
               var8.run.run();
               if (this.drag >= 0) {
                  this.setFromMouse(var3);
               }

               return true;
            }
         }

         return super.mouseClicked(click, doubled);
      }
   }

   private int savedIndexAt(double var1) {
      return (int)Math.floor((var1 - this.bx) / (this.bw / 8.0F));
   }

   public boolean mouseDragged(Click click, double offsetX, double offsetY) {
      if (this.drag >= 0) {
         this.setFromMouse(click.x() / this.sc);
         return true;
      } else {
         return super.mouseDragged(click, offsetX, offsetY);
      }
   }

   public boolean mouseReleased(Click click) {
      if (this.drag >= 0) {
         this.drag = -1;
         NeonLyrics.saveConfig();
         return true;
      } else {
         return super.mouseReleased(click);
      }
   }

   public boolean keyPressed(KeyInput input) {
      if (input.key() == 82) {
         this.randomize();
         return true;
      } else {
         return super.keyPressed(input);
      }
   }

   private Text comp(String var1, StyleSpriteSource var2) {
      return Text.literal(var1).fillStyle(Style.EMPTY.withFont(var2));
   }

   private int tw(String var1, StyleSpriteSource var2) {
      return this.textRenderer.getWidth(this.comp(var1, var2));
   }

   private void txt(DrawContext var1, String var2, float var3, float var4, int var5, StyleSpriteSource var6) {
      var1.drawText(this.textRenderer, this.comp(var2, var6), Math.round(var3), Math.round(var4), Gfx.argb(var5, Gfx.alpha), false);
   }

   private void txtC(DrawContext var1, String var2, float var3, float var4, int var5, StyleSpriteSource var6) {
      var1.drawText(this.textRenderer, this.comp(var2, var6), Math.round(var3 - this.tw(var2, var6) / 2.0F), Math.round(var4), Gfx.argb(var5, Gfx.alpha), false);
   }

   private record Hit(float x, float y, float w, float h, Runnable run) {
   }
}
