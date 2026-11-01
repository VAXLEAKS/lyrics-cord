package dev.neonlyrics.gui;

import dev.neonlyrics.Beat;
import dev.neonlyrics.NeonLyrics;
import dev.neonlyrics.config.Config;
import dev.neonlyrics.config.Storage;
import dev.neonlyrics.hud.Gfx;
import java.nio.file.Files;
import java.time.LocalDateTime;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.input.KeyInput;
import net.minecraft.text.Text;

public class LicenseScreen extends Screen {
   private static final int ACCENT = 10321407;
   private TextFieldWidget box;
   private String status = "";
   private int statusCol = 16777215;
   private long okTime = -1L;
   private int bx;
   private int by;
   private int bw;
   private int bh;
   private int px;
   private int py;
   private int pw;
   private int ph;

   public LicenseScreen() {
      super(Text.literal("Lyrics Cord"));
   }

   private static String tr(String var0) {
      return Text.translatable("neonlyrics.lic." + var0).getString();
   }

   protected void init() {
      int var1 = Math.min(320, this.width - 40);
      this.box = new TextFieldWidget(this.textRenderer, this.width / 2 - var1 / 2 + 12, this.height / 2 + 4, var1 - 24, 16, Text.literal(""));
      this.box.setMaxLength(200);
      this.box.setPlaceholder(Text.literal("LCORD-XXXX-XXXX-XXXX"));
      String var2 = NeonLyrics.cfg().licenseCode;
      this.box.setText(var2.startsWith("R1:") ? "" : var2);
      this.box.setChangedListener(var1x -> {
         if (this.okTime < 0L && Beat.isKey(var1x.trim())) {
            this.tryUnlock();
         }
      });
      this.addDrawableChild(this.box);
      this.setInitialFocus(this.box);
   }

   public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
      int var5 = Math.min(320, this.width - 40);
      short var6 = 150;
      int var7 = this.width / 2 - var5 / 2;
      int var8 = this.height / 2 - 70;
      Gfx.aaRound(context, var7, var8, var5, var6, 12.0F, 1380895, 0.96F);
      Gfx.aaRound(context, var7, var8, var5, 2.0F, 1.0F, 10321407, 0.9F);
      Gfx.center(context, tr("title"), this.width / 2, var8 + 14, 16777215, 1.0F, true);
      Gfx.center(context, tr("account") + " " + Beat.name(), this.width / 2, var8 + 32, 13219327, 1.0F, false);
      Gfx.center(context, tr("hint"), this.width / 2, var8 + 48, 11184821, 1.0F, false);
      Gfx.aaRound(context, var7 + 10, this.height / 2, var5 - 20, 24.0F, 6.0F, 0, 0.5F);
      super.render(context, mouseX, mouseY, deltaTicks);
      this.ph = 18;
      this.pw = (var5 - 30) / 2;
      this.px = var7 + 10;
      this.py = var8 + var6 - 34;
      this.bx = this.px + this.pw + 10;
      this.by = this.py;
      this.bw = this.pw;
      this.bh = this.ph;
      boolean var9 = mouseX >= this.px && mouseY >= this.py && mouseX < this.px + this.pw && mouseY < this.py + this.ph;
      boolean var10 = mouseX >= this.bx && mouseY >= this.by && mouseX < this.bx + this.bw && mouseY < this.by + this.bh;
      Gfx.aaRound(context, this.px, this.py, this.pw, this.ph, 9.0F, 16777215, var9 ? 0.18F : 0.09F);
      Gfx.center(context, tr("paste"), this.px + this.pw / 2, this.py + 5, 16777215, 1.0F, false);
      Gfx.aaRound(context, this.bx, this.by, this.bw, this.bh, 9.0F, 10321407, var10 ? 1.0F : 0.8F);
      Gfx.center(context, tr("unlock"), this.bx + this.bw / 2, this.by + 5, 16777215, 1.0F, true);
      if (!this.status.isEmpty()) {
         Gfx.center(context, this.status, this.width / 2, var8 + var6 + 8, this.statusCol, 1.0F, true);
      }

      if (this.okTime > 0L && System.currentTimeMillis() - this.okTime > 900L) {
         this.close();
      }
   }

   public boolean mouseClicked(Click click, boolean doubled) {
      if (click.button() == 0) {
         double var3 = click.x();
         double var5 = click.y();
         if (var3 >= this.px && var5 >= this.py && var3 < this.px + this.pw && var5 < this.py + this.ph) {
            this.box.setText(this.client.keyboard.getClipboard().trim());
            return true;
         }

         if (var3 >= this.bx && var5 >= this.by && var3 < this.bx + this.bw && var5 < this.by + this.bh) {
            this.tryUnlock();
            return true;
         }
      }

      return super.mouseClicked(click, doubled);
   }

   public void enterCode(String var1) {
      this.box.setText(var1);
   }

   public boolean keyPressed(KeyInput input) {
      if (input.key() != 257 && input.key() != 335) {
         return super.keyPressed(input);
      }

      this.tryUnlock();
      return true;
   }

   private void unlocked() {
      NeonLyrics.saveConfig();
      this.status = tr("ok");
      this.statusCol = 8257434;
      this.okTime = System.currentTimeMillis();
   }

   private void tryUnlock() {
      String var1 = this.box.getText().trim();
      String var2 = Beat.uuid();
      Config var3 = NeonLyrics.cfg();
      if (Beat.isKey(var1)) {
         String var4 = Beat.normalize(var1);
         String var5 = var3.usedCodes.get(var4);
         if (var5 != null && !var5.equals(var2)) {
            this.status = tr("used");
            this.statusCol = 16738922;
            return;
         }

         var3.usedCodes.put(var4, var2);
         var3.licenseCode = "R1:" + var4 + ":" + var2;
         this.unlocked();
      } else if (Beat.eval(var2, var1)) {
         var3.licenseCode = var1;
         this.unlocked();
      } else {
         String var7 = Beat.normalize(var1);
         this.status = tr("bad") + (var7.isEmpty() ? "" : "  (" + (var7.length() > 22 ? var7.substring(0, 22) + "..." : var7) + ")");
         this.statusCol = 16738922;
      }

      try {
         Files.writeString(
            Storage.dir().resolve("license-status.txt"),
            LocalDateTime.now()
               + "\nAccount: "
               + Beat.name()
               + " "
               + var2
               + "\nEingabe-Laenge: "
               + var1.length()
               + "\nGelesen: "
               + Beat.normalize(var1)
               + "\nErgebnis: "
               + this.status
               + "\n"
         );
      } catch (Throwable var6) {
      }
   }
}
