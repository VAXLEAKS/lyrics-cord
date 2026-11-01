package dev.neonlyrics.hud;

import dev.neonlyrics.Beat;
import dev.neonlyrics.NeonLyrics;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;

public final class GlassHotbar {
   private static final Anim sel = new Anim(0.0, 22.0);

   private GlassHotbar() {
   }

   public static HudElement wrap(HudElement var0) {
      return (var1, var2) -> {
         MinecraftClient var3 = MinecraftClient.getInstance();
         if (NeonLyrics.cfg().glassHotbar && Beat.live() && var3.player != null && !var3.player.isSpectator()) {
            render(var1, var3);
         } else {
            var0.render(var1, var2);
         }
      };
   }

   private static void render(DrawContext var0, MinecraftClient var1) {
      float var2 = Gfx.alpha;
      Gfx.alpha = 1.0F;
      int var3 = Theme.accent();
      short var4 = 182;
      byte var5 = 22;
      int var6 = var0.getScaledWindowWidth() / 2 - var4 / 2;
      int var7 = var0.getScaledWindowHeight() - var5 - 1;
      PlayerInventory var8 = var1.player.getInventory();
      Gfx.round(var0, var6, var7, var4, var5, 6, 1183260, 0.72F);
      Gfx.round(var0, var6, var7, var4, 1, 1, 16777215, 0.07F);
      int var9 = var8.getSelectedSlot();
      float var10 = sel.target(var9).get();
      int var11 = var6 + 1 + Math.round(var10 * 20.0F);
      Gfx.glow(var0, var11, var7 + 1, 20, 20, 5, var3, 0.9F, 4);
      Gfx.round(var0, var11, var7 + 1, 20, 20, 5, var3, 0.22F);
      outline(var0, var11, var7 + 1, 20, 20, var3);

      for (int var12 = 0; var12 < 9; var12++) {
         ItemStack var13 = var8.getStack(var12);
         if (!var13.isEmpty()) {
            int var14 = var6 + 3 + var12 * 20;
            int var15 = var7 + 3;
            var0.drawItem(var13, var14, var15);
            var0.drawStackOverlay(var1.textRenderer, var13, var14, var15);
         }
      }

      ItemStack var16 = var1.player.getOffHandStack();
      if (!var16.isEmpty()) {
         int var17 = var6 - 26;
         Gfx.round(var0, var17, var7, 22, 22, 6, 1183260, 0.72F);
         var0.drawItem(var16, var17 + 3, var7 + 3);
         var0.drawStackOverlay(var1.textRenderer, var16, var17 + 3, var7 + 3);
      }

      Gfx.alpha = var2;
   }

   private static void outline(DrawContext var0, int var1, int var2, int var3, int var4, int var5) {
      Gfx.rect(var0, var1 + 3, var2, var3 - 6, 1, var5, 0.95F);
      Gfx.rect(var0, var1 + 3, var2 + var4 - 1, var3 - 6, 1, var5, 0.95F);
      Gfx.rect(var0, var1, var2 + 3, 1, var4 - 6, var5, 0.95F);
      Gfx.rect(var0, var1 + var3 - 1, var2 + 3, 1, var4 - 6, var5, 0.95F);
      Gfx.rect(var0, var1 + 1, var2 + 1, 2, 1, var5, 0.95F);
      Gfx.rect(var0, var1 + 1, var2 + 2, 1, 1, var5, 0.95F);
      Gfx.rect(var0, var1 + var3 - 3, var2 + 1, 2, 1, var5, 0.95F);
      Gfx.rect(var0, var1 + var3 - 2, var2 + 2, 1, 1, var5, 0.95F);
      Gfx.rect(var0, var1 + 1, var2 + var4 - 2, 2, 1, var5, 0.95F);
      Gfx.rect(var0, var1 + 1, var2 + var4 - 3, 1, 1, var5, 0.95F);
      Gfx.rect(var0, var1 + var3 - 3, var2 + var4 - 2, 2, 1, var5, 0.95F);
      Gfx.rect(var0, var1 + var3 - 2, var2 + var4 - 3, 1, 1, var5, 0.95F);
   }
}
