package dev.neonlyrics.gui;

import dev.neonlyrics.NeonLyrics;
import dev.neonlyrics.hud.Anim;
import dev.neonlyrics.hud.Gfx;
import dev.neonlyrics.hud.Icons;
import dev.neonlyrics.hud.SkyThumbs;
import dev.neonlyrics.sky.SkyPreset;
import dev.neonlyrics.sky.SkyPreview;
import dev.neonlyrics.util.Fx;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.input.KeyInput;
import net.minecraft.text.Style;
import net.minecraft.text.StyleSpriteSource;
import net.minecraft.text.Text;
import net.minecraft.text.StyleSpriteSource.Font;
import net.minecraft.util.Identifier;

public class SkyPreviewScreen extends Screen {
   private static final int GLASS = 921106;
   private static final int TEXT = 15658737;
   private static final int SUB = 10132131;
   private static final StyleSpriteSource BOLD = new Font(Identifier.of("neonlyrics", "menu_b"));
   private static final StyleSpriteSource SMALL = new Font(Identifier.of("neonlyrics", "menu_s"));
   private final Screen parent;
   private final SkyPreset[] skies = LyricsScreen.skyOrder();
   private final List<SkyPreviewScreen.Hit> hits = new ArrayList<>();
   private int index;
   private float strip;
   private float tray;
   private boolean auto;
   private long changedAt = System.currentTimeMillis();
   private long lastNs = System.nanoTime();
   private final long openAt = System.currentTimeMillis();

   public SkyPreviewScreen(Screen var1, int var2) {
      super(Text.literal("Vorschau"));
      this.parent = var1;
      this.index = Math.floorMod(var2, this.skies.length);
      this.strip = this.index;
      SkyPreview.set(this.skies[this.index]);
   }

   public static SkyPreviewScreen current(Screen var0) {
      int var1 = NeonLyrics.cfg().sky >= 0 ? NeonLyrics.cfg().sky : NeonLyrics.cfg().lastSky;
      int var2 = 0;
      SkyPreset[] var3 = LyricsScreen.skyOrder();

      for (int var4 = 0; var4 < var3.length; var4++) {
         if (var3[var4].ordinal() == var1) {
            var2 = var4;
         }
      }

      return new SkyPreviewScreen(var0, var2);
   }

   private void select(int var1) {
      var1 = Math.floorMod(var1, this.skies.length);
      if (var1 != this.index) {
         this.index = var1;
         this.changedAt = System.currentTimeMillis();
         SkyPreview.set(this.skies[this.index]);
         Fx.sound(Fx.Sfx.CLICK);
      }
   }

   private void applyAndClose() {
      NeonLyrics.cfg().sky = this.skies[this.index].ordinal();
      NeonLyrics.cfg().lastSky = this.skies[this.index].ordinal();
      NeonLyrics.saveConfig();
      Fx.sound(Fx.Sfx.CLICK);
      this.close();
   }

   public boolean shouldPause() {
      return false;
   }

   public void close() {
      this.client.setScreen(this.parent);
   }

   public void removed() {
      super.removed();
      SkyPreview.clear();
   }

   public void renderBackground(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
   }

   public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
      long var5 = System.nanoTime();
      float var7 = Math.min(0.1F, (float)(var5 - this.lastNs) / 1.0E9F);
      this.lastNs = var5;
      if (this.auto && System.currentTimeMillis() - this.changedAt > 4500L) {
         this.select(this.index + 1);
      }

      this.hits.clear();
      long var8 = System.currentTimeMillis();
      float var10 = Anim.outCubic(Anim.clamp((float)(var8 - this.openAt) / 300.0F));
      Gfx.alpha = var10;
      this.strip = this.strip + (this.index - this.strip) * (1.0F - (float)Math.exp(-12.0F * var7));
      boolean var11 = mouseY > this.height * 0.7F;
      this.tray = this.tray + ((var11 ? 1.0F : 0.0F) - this.tray) * (1.0F - (float)Math.exp(-10.0F * var7));
      float var12 = Anim.outCubic(Anim.clamp(this.tray));
      SkyPreset var13 = this.skies[this.index];
      float var14 = 1.0F - Anim.outCubic(Anim.clamp((float)(var8 - this.changedAt) / 260.0F));
      String var15 = Fx.t(var13.key());
      int var16 = this.tw(var15, BOLD) + 70;
      byte var17 = 22;
      int var18 = Math.min(this.width - 24, 520);
      byte var19 = 70;
      int var20 = this.height - 10 - Math.round(var19 * var12);
      int var21 = (this.width - var16) / 2;
      int var22 = var20 - var17 - 8 + Math.round((1.0F - var10) * 20.0F);
      Gfx.aaRound(context, var21, var22, var16, var17, var17 / 2.0F, 921106, 0.62F);
      this.txtC(context, var15, this.width / 2.0F, var22 + 7 - var14 * 2.0F, 15658737, 1.0F, BOLD);
      this.arrow(context, Icons.Icon.BACK, var21 + 13, var22 + var17 / 2, mouseX, mouseY, () -> this.select(this.index - 1));
      this.arrow(context, Icons.Icon.CHEVRON, var21 + var16 - 13, var22 + var17 / 2, mouseX, mouseY, () -> this.select(this.index + 1));
      float var23 = 1.0F - Anim.clamp((float)(var8 - this.changedAt - 2200L) / 600.0F);
      if (var23 > 0.01F) {
         this.txtC(
            context,
            this.ell(Fx.t("neonlyrics.skydesc." + var13.name().toLowerCase(Locale.ROOT)), Math.min(this.width - 40, 460)),
            this.width / 2.0F,
            var22 - 12,
            10132131,
            var23,
            SMALL
         );
      }

      float var24 = 1.0F - Anim.clamp((float)(var8 - this.openAt - 3500L) / 700.0F);
      if (var24 > 0.01F) {
         String var25 = Fx.t("neonlyrics.preview.hint");
         int var26 = this.tw(var25, SMALL) + 20;
         Gfx.aaRound(context, (this.width - var26) / 2.0F, 8.0F, var26, 16.0F, 8.0F, 921106, 0.5F * var24);
         this.txtC(context, var25, this.width / 2.0F, 12.0F, 10132131, var24, SMALL);
      }

      if (var12 < 0.02F) {
         Gfx.alpha = 1.0F;
      } else {
         float var43 = Gfx.alpha;
         Gfx.alpha = var10 * var12;
         int var44 = (this.width - var18) / 2;
         int var27 = var20;
         Gfx.aaRound(context, var44, var27, var18, var19, 12.0F, 921106, 0.72F);
         byte var28 = 52;
         byte var29 = 28;
         byte var30 = 6;
         int var31 = var27 + 8;
         context.enableScissor(var44 + 8, var31 - 3, var44 + var18 - 8, var31 + var29 + 3);
         float var32 = var44 + var18 / 2.0F - var28 / 2.0F - this.strip * (var28 + var30);

         for (int var33 = 0; var33 < this.skies.length; var33++) {
            int var34 = Math.round(var32 + var33 * (var28 + var30));
            if (var34 + var28 >= var44 && var34 <= var44 + var18) {
               boolean var35 = var33 == this.index;
               boolean var36 = mouseX >= var34 && mouseX < var34 + var28 && mouseY >= var31 && mouseY < var31 + var29;
               if (var35) {
                  Gfx.aaRound(context, var34 - 2, var31 - 2, var28 + 4, var29 + 4, 7.0F, 16777215, 1.0F);
               }

               float var37 = var35 ? 1.0F : (var36 ? 0.9F : 0.55F);
               if (!SkyThumbs.draw(context, this.skies[var33], var34, var31, var28, var29, var37)) {
                  Gfx.aaRound(context, var34, var31, var28, var29, 5.0F, this.skies[var33].top, var37);
               }

               int var38 = var33;
               int var39 = Math.max(var34, var44 + 8);
               int var40 = Math.min(var34 + var28, var44 + var18 - 8);
               if (var40 > var39) {
                  this.hits.add(new SkyPreviewScreen.Hit(var39, var31, var40 - var39, var29, () -> this.select(var38)));
               }
            }
         }

         context.disableScissor();
         int var45 = var27 + var19 - 24;
         byte var46 = 16;
         String var47 = Fx.t("neonlyrics.preview.apply");
         String var48 = Fx.t("neonlyrics.preview.back");
         String var49 = Fx.t(this.auto ? "neonlyrics.preview.auto.on" : "neonlyrics.preview.auto");
         int var50 = this.tw(var47, BOLD) + 24;
         int var51 = this.tw(var48, SMALL) + 20;
         int var52 = this.tw(var49, SMALL) + 20;
         int var41 = var50 + var51 + var52 + 12;
         int var42 = var44 + (var18 - var41) / 2;
         this.button(context, var48, var42, var45, var51, var46, false, mouseX, mouseY, this::close);
         this.button(context, var49, var42 + var51 + 6, var45, var52, var46, false, mouseX, mouseY, () -> {
            this.auto = !this.auto;
            this.changedAt = System.currentTimeMillis();
            Fx.sound(Fx.Sfx.CLICK);
         });
         this.button(context, var47, var42 + var51 + var52 + 12, var45, var50, var46, true, mouseX, mouseY, this::applyAndClose);
         this.txtC(context, this.index + 1 + " / " + this.skies.length, var44 + var18 - 30, var45 + 4, 10132131, 0.8F, SMALL);
         Gfx.alpha = var43;
         Gfx.alpha = 1.0F;
      }
   }

   private void arrow(DrawContext var1, Icons.Icon var2, int var3, int var4, int var5, int var6, Runnable var7) {
      boolean var8 = Math.abs(var5 - var3) < 11 && Math.abs(var6 - var4) < 11;
      if (var8) {
         Gfx.aaCircle(var1, var3, var4, 8.0F, 16777215, 0.12F);
      }

      Icons.draw(var1, var2, var3, var4, 6.5F, var8 ? 16777215 : 10132131, 1.0F);
      this.hits.add(new SkyPreviewScreen.Hit(var3 - 11, var4 - 11, 22, 22, var7));
   }

   private void button(DrawContext var1, String var2, int var3, int var4, int var5, int var6, boolean var7, int var8, int var9, Runnable var10) {
      boolean var11 = var8 >= var3 && var8 < var3 + var5 && var9 >= var4 && var9 < var4 + var6;
      int var12 = var7 ? 16777215 : (var11 ? 3421243 : 2500140);
      Gfx.aaRound(var1, var3, var4, var5, var6, var6 / 2.0F, var12, var7 ? (var11 ? 1.0F : 0.92F) : 1.0F);
      this.txtC(var1, var2, var3 + var5 / 2.0F, var4 + 5, var7 ? 1118484 : 15658737, 1.0F, var7 ? BOLD : SMALL);
      this.hits.add(new SkyPreviewScreen.Hit(var3, var4, var5, var6, var10));
   }

   private Text comp(String var1, StyleSpriteSource var2) {
      return Text.literal(var1).fillStyle(Style.EMPTY.withFont(var2));
   }

   private int tw(String var1, StyleSpriteSource var2) {
      return this.textRenderer.getWidth(this.comp(var1, var2));
   }

   private void txtC(DrawContext var1, String var2, float var3, float var4, int var5, float var6, StyleSpriteSource var7) {
      if (var2 != null && !var2.isEmpty()) {
         var1.drawText(this.textRenderer, this.comp(var2, var7), Math.round(var3 - this.tw(var2, var7) / 2.0F), Math.round(var4), Gfx.argb(var5, var6), false);
      }
   }

   private String ell(String var1, int var2) {
      if (var1 == null) {
         return "";
      }

      if (this.tw(var1, SMALL) <= var2) {
         return var1;
      }

      while (var1.length() > 1 && this.tw(var1 + "…", SMALL) > var2) {
         var1 = var1.substring(0, var1.length() - 1);
      }

      return var1 + "…";
   }

   public boolean mouseClicked(Click click, boolean doubled) {
      if (click.button() == 0) {
         for (int var3 = this.hits.size() - 1; var3 >= 0; var3--) {
            SkyPreviewScreen.Hit var4 = this.hits.get(var3);
            if (click.x() >= var4.x() && click.y() >= var4.y() && click.x() < var4.x() + var4.w() && click.y() < var4.y() + var4.h()) {
               var4.run().run();
               return true;
            }
         }
      }

      return super.mouseClicked(click, doubled);
   }

   public boolean mouseDragged(Click click, double offsetX, double offsetY) {
      if (this.client.player != null) {
         this.client.player.changeLookDirection(offsetX * 2.5, offsetY * 2.5);
      }

      return true;
   }

   public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
      if (verticalAmount != 0.0) {
         this.select(this.index + (verticalAmount < 0.0 ? 1 : -1));
      }

      return true;
   }

   public boolean keyPressed(KeyInput input) {
      int var2 = input.key();
      if (var2 == 262 || var2 == 68) {
         this.select(this.index + 1);
         return true;
      } else if (var2 == 263 || var2 == 65) {
         this.select(this.index - 1);
         return true;
      } else if (var2 == 257 || var2 == 335) {
         this.applyAndClose();
         return true;
      } else if (var2 == 32) {
         this.auto = !this.auto;
         this.changedAt = System.currentTimeMillis();
         return true;
      } else {
         return super.keyPressed(input);
      }
   }

   private record Hit(int x, int y, int w, int h, Runnable run) {
   }
}
