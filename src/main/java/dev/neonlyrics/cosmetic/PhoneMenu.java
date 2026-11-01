package dev.neonlyrics.cosmetic;

import dev.neonlyrics.NeonLyrics;
import dev.neonlyrics.gui.LyricsScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import org.joml.Matrix4f;

public final class PhoneMenu {
   private static WorldPanel panel;

   private PhoneMenu() {
   }

   public static boolean interactive() {
      return NeonLyrics.cfg().menuMode == 1;
   }

   public static void open(MinecraftClient var0) {
      if (panel != null) {
         if (!panel.closing()) {
            panel.close();
         }
      } else if (interactive() && var0.world != null && var0.player != null) {
         panel = new WorldPanel(var0);
      } else {
         var0.setScreen(new LyricsScreen());
      }
   }

   public static void closeNow() {
      panel = null;
   }

   public static void menuClosed() {
   }

   public static boolean hidesHand() {
      return false;
   }

   public static void tick(MinecraftClient var0) {
      if (panel != null) {
         if (var0.world != null && var0.player != null && !panel.finished() && !(var0.player.getEntityPos().distanceTo(panel.pos()) > 32.0)) {
            panel.tick(var0);
         } else {
            panel = null;
         }
      }
   }

   public static void startTick(MinecraftClient var0) {
      if (panel != null && var0.currentScreen == null && panel.hovered()) {
         while (var0.options.attackKey.wasPressed()) {
            panel.click();
         }

         while (var0.options.useKey.wasPressed()) {
            panel.click();
         }
      }
   }

   public static void render(MinecraftClient var0, VertexConsumerProvider var1, Matrix4f var2, float var3) {
      if (panel != null && var0.player != null) {
         panel.render(var0, var1, var2);
      }
   }
}
