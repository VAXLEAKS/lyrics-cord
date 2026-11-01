package dev.neonlyrics.gui;

import dev.neonlyrics.NeonLyrics;
import dev.neonlyrics.cosmetic.Emotes;
import dev.neonlyrics.hud.Anim;
import dev.neonlyrics.hud.Gfx;
import dev.neonlyrics.util.Fx;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.input.KeyInput;
import net.minecraft.text.Style;
import net.minecraft.text.StyleSpriteSource;
import net.minecraft.text.Text;
import net.minecraft.text.StyleSpriteSource.Font;
import net.minecraft.util.Identifier;

public class EmoteScreen extends Screen {
   private static final int GLASS = 1118484;
   private static final int TEXT = 15658737;
   private static final int SUB = 8618892;
   private static final int INK = 1118484;
   private static final StyleSpriteSource BOLD = new Font(Identifier.of("neonlyrics", "menu_b"));
   private static final StyleSpriteSource SMALL = new Font(Identifier.of("neonlyrics", "menu_s"));
   private static final StyleSpriteSource TITLE = new Font(Identifier.of("neonlyrics", "menu_t"));
   private final long openAt = System.currentTimeMillis();
   private final float[] hoverK = new float[Emotes.IDS.length];
   private int hover = -1;
   private long lastNs = System.nanoTime();
   private float sel = -1.0F;

   public EmoteScreen() {
      super(Text.literal("Emotes"));
   }

   public boolean shouldPause() {
      return false;
   }

   public void renderBackground(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
      float var5 = Anim.clamp((float)(System.currentTimeMillis() - this.openAt) / 200.0F);
      Gfx.rect(context, 0, 0, this.width, this.height, 263176, 0.38F * var5);
   }

   public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
      super.render(context, mouseX, mouseY, deltaTicks);
      long var5 = System.nanoTime();
      float var7 = Math.min(0.05F, (float)(var5 - this.lastNs) / 1.0E9F);
      this.lastNs = var5;
      float var8 = Anim.outCubic(Anim.clamp((float)(System.currentTimeMillis() - this.openAt) / 240.0F));
      int var9 = Emotes.IDS.length;
      float var10 = this.width / 2.0F;
      float var11 = this.height / 2.0F;
      float var12 = Math.min(118.0F, Math.min(this.width, this.height) * 0.32F) * (0.85F + 0.15F * var8);
      double var13 = mouseX - var10;
      double var15 = mouseY - var11;
      this.hover = Math.hypot(var13, var15) > 34.0 ? Math.floorMod(Math.round(Math.atan2(var13, -var15) / (Math.PI * 2) * var9), var9) : -1;
      Gfx.alpha = var8;
      Gfx.aaCircle(context, var10, var11, var12 + 34.0F, 16777215, 0.08F);
      Gfx.aaCircle(context, var10, var11, var12 + 33.0F, 1118484, 0.9F);
      Gfx.aaCircle(context, var10, var11, var12 - 33.0F, 16777215, 0.05F);
      Gfx.aaCircle(context, var10, var11, var12 - 34.0F, 1118484, 1.0F);
      if (this.hover >= 0) {
         float var17 = this.hover;
         if (this.sel < 0.0F) {
            this.sel = var17;
         }

         float var18 = ((var17 - this.sel) % var9 + var9 * 1.5F) % var9 - var9 / 2.0F;
         this.sel = this.sel + var18 * (1.0F - (float)Math.exp(-18.0F * var7));
         double var19 = this.sel * Math.PI * 2.0 / var9;
         Gfx.aaCircle(context, var10 + (float)Math.sin(var19) * (var12 - 44.0F), var11 - (float)Math.cos(var19) * (var12 - 44.0F), 2.5F, 16777215, 0.9F);
      }

      for (int var29 = 0; var29 < var9; var29++) {
         this.hoverK[var29] = this.hoverK[var29] + ((var29 == this.hover ? 1.0F : 0.0F) - this.hoverK[var29]) * (1.0F - (float)Math.exp(-16.0F * var7));
         float var31 = this.hoverK[var29];
         double var32 = var29 * Math.PI * 2.0 / var9;
         float var21 = Anim.outBack(Anim.clamp((float)(System.currentTimeMillis() - this.openAt - var29 * 18) / 260.0F));
         float var22 = var12 * (0.7F + 0.3F * var21);
         float var23 = var10 + (float)Math.sin(var32) * var22;
         float var24 = var11 - (float)Math.cos(var32) * var22;
         String var25 = Fx.t("neonlyrics.emote." + Emotes.IDS[var29]);
         int var26 = this.tw(var25, var31 > 0.5F ? BOLD : SMALL);
         float var27 = Math.max(46, var26 + 20) + 6.0F * var31;
         float var28 = 20.0F + 2.0F * var31;
         Gfx.aaRound(context, var23 - var27 / 2.0F, var24 - var28 / 2.0F, var27, var28, var28 / 2.0F, 16777215, 0.07F + 0.93F * var31);
         this.txtC(context, var25, var23, var24 - 3.5F, Gfx.lerpColor(15658737, 1118484, var31), 1.0F, var31 > 0.5F ? BOLD : SMALL);
      }

      String var30 = this.hover >= 0 ? Fx.t("neonlyrics.emote." + Emotes.IDS[this.hover]) : Fx.t("neonlyrics.emote.pick");
      this.txtC(context, var30, var10, var11 - 8.0F, 15658737, 1.0F, TITLE);
      this.txtC(context, Fx.t(this.hover >= 0 ? "neonlyrics.emote.click" : "neonlyrics.emote.point"), var10, var11 + 6.0F, 8618892, 1.0F, SMALL);
      this.txtC(context, Fx.t("neonlyrics.emote.hint"), var10, var11 + var12 + 46.0F, 8618892, 0.8F, SMALL);
      Gfx.alpha = 1.0F;
   }

   private Text comp(String var1, StyleSpriteSource var2) {
      return Text.literal(var1).fillStyle(Style.EMPTY.withFont(var2));
   }

   private int tw(String var1, StyleSpriteSource var2) {
      return this.textRenderer.getWidth(this.comp(var1, var2));
   }

   private void txtC(DrawContext var1, String var2, float var3, float var4, int var5, float var6, StyleSpriteSource var7) {
      var1.drawText(
         this.textRenderer, this.comp(var2, var7), Math.round(var3 - this.tw(var2, var7) / 2.0F), Math.round(var4), Gfx.argb(var5, var6 * Gfx.alpha), false
      );
   }

   private void pick(int var1) {
      Fx.sound(Fx.Sfx.CLICK);
      this.close();
      Emotes.play(var1);
   }

   public boolean mouseClicked(Click click, boolean doubled) {
      if (this.hover >= 0) {
         this.pick(this.hover);
      } else {
         this.close();
      }

      return true;
   }

   public boolean keyPressed(KeyInput input) {
      int var2 = input.key();
      if (var2 >= 49 && var2 <= 57 && var2 - 49 < Emotes.IDS.length) {
         this.pick(var2 - 49);
         return true;
      } else if (var2 == 48 && Emotes.IDS.length >= 10) {
         this.pick(9);
         return true;
      } else if (KeyBindingHelper.getBoundKeyOf(NeonLyrics.keyEmote).getCode() == var2) {
         this.close();
         return true;
      } else {
         return super.keyPressed(input);
      }
   }
}
