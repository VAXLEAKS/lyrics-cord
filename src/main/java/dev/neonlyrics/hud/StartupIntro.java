package dev.neonlyrics.hud;

import dev.neonlyrics.NeonLyrics;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents.AfterInit;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents.AfterRender;
import net.minecraft.client.gui.screen.TitleScreen;

public final class StartupIntro {
   private static long start = -1L;
   private static boolean done = false;

   private StartupIntro() {
   }

   public static void register() {
      ScreenEvents.AFTER_INIT.register((AfterInit)(var0, var1, var2, var3) -> {
         if (!done && var1 instanceof TitleScreen && NeonLyrics.cfg().fxStartup) {
            if (start < 0L) {
               start = System.currentTimeMillis();
            }

            ScreenEvents.afterRender(var1).register((AfterRender)(var0x, var1x, var2x, var3x, var4) -> {
               if (!done) {
                  float var5 = (float)(System.currentTimeMillis() - start) / 1000.0F;
                  if (var5 > 2.4F) {
                     done = true;
                  } else {
                     int var6 = var1x.getScaledWindowWidth();
                     int var7 = var1x.getScaledWindowHeight();
                     float var8 = Gfx.alpha;
                     float var9 = var5 > 1.7F ? 1.0F - (var5 - 1.7F) / 0.7F : 1.0F;
                     Gfx.alpha = 1.0F;
                     Gfx.rect(var1x, 0, 0, var6, var7, 460046, 0.96F * var9);
                     float var10 = Anim.outBack(Math.min(1.0F, var5 / 0.6F));
                     float var11 = (float)(0.5 + 0.5 * Math.sin(var5 * 4.0F));
                     int var12 = Theme.accent();
                     var1x.getMatrices().pushMatrix();
                     var1x.getMatrices().translate(var6 / 2.0F, var7 / 2.0F - 6.0F);
                     float var13 = 2.6F * (0.7F + 0.3F * var10);
                     var1x.getMatrices().scale(var13, var13);
                     String var14 = "NEON";
                     int var15 = Gfx.w(var14, Gfx.BOLD);
                     Gfx.alpha = var9 * Math.min(1.0F, var5 / 0.3F);
                     Gfx.aaRound(var1x, -var15 / 2.0F - 8.0F, -7.0F, var15 + 16, 17.0F, 8.5F, var12, 0.18F + 0.12F * var11);
                     Gfx.textBold(var1x, var14, -var15 / 2, -3, 16777215, 1.0F, false);
                     var1x.getMatrices().popMatrix();
                     String var16 = "Lyrics";
                     int var17 = Math.max(0, Math.min(var16.length(), (int)((var5 - 0.5F) / 0.05F)));
                     Gfx.center(var1x, var16.substring(0, var17), var6 / 2, var7 / 2 + 22, 10326968, var9, false);
                     float var18 = Math.min(1.0F, var5 / 1.6F);
                     Gfx.aaRound(var1x, var6 / 2.0F - 40.0F, var7 / 2.0F + 36.0F, 80.0F, 2.0F, 1.0F, 16777215, 0.12F * var9);
                     Gfx.aaRound(var1x, var6 / 2.0F - 40.0F, var7 / 2.0F + 36.0F, 80.0F * var18, 2.0F, 1.0F, var12, var9);
                     Gfx.alpha = var8;
                  }
               }
            });
         }
      });
   }
}
