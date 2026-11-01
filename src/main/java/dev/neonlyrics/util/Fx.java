package dev.neonlyrics.util;

import dev.neonlyrics.NeonLyrics;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public final class Fx {
   private Fx() {
   }

   public static String t(String var0, Object... var1) {
      return I18n.translate(var0, var1);
   }

   public static void sound(Fx.Sfx var0) {
      if (NeonLyrics.cfg().sounds) {
         MinecraftClient var1 = MinecraftClient.getInstance();
         if (var1.getSoundManager() != null) {
            switch (var0) {
               case CLICK:
                  var1.getSoundManager().play(PositionedSoundInstance.ui((SoundEvent)SoundEvents.UI_BUTTON_CLICK.value(), 1.4F, 0.35F));
                  break;
               case WIN:
                  var1.getSoundManager().play(PositionedSoundInstance.ui(SoundEvents.ENTITY_PLAYER_LEVELUP, 1.0F, 0.6F));
                  break;
               case ERROR:
                  var1.getSoundManager().play(PositionedSoundInstance.ui((SoundEvent)SoundEvents.BLOCK_NOTE_BLOCK_BASS.value(), 0.6F, 0.7F));
                  break;
               case TICK:
                  var1.getSoundManager().play(PositionedSoundInstance.ui((SoundEvent)SoundEvents.BLOCK_NOTE_BLOCK_HAT.value(), 1.8F, 0.4F));
            }
         }
      }
   }

   public static void info(String var0) {
      MinecraftClient var1 = MinecraftClient.getInstance();
      if (var1.inGameHud != null) {
         var1.inGameHud
            .getChatHud()
            .addMessage(Text.literal("[Lyrics Cord] ").formatted(Formatting.AQUA).append(Text.literal(var0).formatted(Formatting.WHITE)));
      }
   }

   public static void error(String var0) {
      MinecraftClient var1 = MinecraftClient.getInstance();
      if (var1.inGameHud != null) {
         var1.inGameHud
            .getChatHud()
            .addMessage(Text.literal("[Lyrics Cord] ").formatted(Formatting.LIGHT_PURPLE).append(Text.literal(var0).formatted(Formatting.RED)));
         sound(Fx.Sfx.ERROR);
      }
   }

   public static void clipboard(String var0) {
      MinecraftClient.getInstance().keyboard.setClipboard(var0);
   }

   public static String myName() {
      return MinecraftClient.getInstance().getSession().getUsername();
   }

   public enum Sfx {
      CLICK,
      WIN,
      ERROR,
      TICK;
   }
}
