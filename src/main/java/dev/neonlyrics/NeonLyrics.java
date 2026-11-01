package dev.neonlyrics;

import dev.neonlyrics.config.Config;
import dev.neonlyrics.config.Storage;
import dev.neonlyrics.cosmetic.DeathFx;
import dev.neonlyrics.cosmetic.Emotes;
import dev.neonlyrics.cosmetic.PhoneMenu;
import dev.neonlyrics.gui.EmoteScreen;
import dev.neonlyrics.gui.LicenseScreen;
import dev.neonlyrics.hud.BeatGlow;
import dev.neonlyrics.hud.BookLyrics;
import dev.neonlyrics.hud.Crosshair;
import dev.neonlyrics.hud.GlassHotbar;
import dev.neonlyrics.hud.Island;
import dev.neonlyrics.hud.StartupIntro;
import dev.neonlyrics.lyrics.Favorites;
import dev.neonlyrics.lyrics.SpotifyClient;
import dev.neonlyrics.sky.SkyRenderer;
import dev.neonlyrics.sky.WorldSky;
import dev.neonlyrics.util.AutoPerf;
import dev.neonlyrics.util.Fx;
import dev.neonlyrics.util.Profiles;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.option.KeyBinding.Category;
import net.minecraft.client.util.InputUtil.Type;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class NeonLyrics implements ClientModInitializer {
   public static final String MOD_ID = "neonlyrics";
   public static final Logger LOG = LoggerFactory.getLogger("NeonLyrics");
   private static Config config = new Config();
   public static KeyBinding keyMenu;
   public static KeyBinding keySkip;
   public static KeyBinding keyPrev;
   public static KeyBinding keyPause;
   public static KeyBinding keyLyrics;
   public static KeyBinding keySync;
   public static KeyBinding keyFav;
   public static KeyBinding keyClip;
   public static KeyBinding keyProfile;
   public static KeyBinding keyEmote;
   private static boolean licenseHint;

   public static Config cfg() {
      return config;
   }

   public static void saveConfig() {
      config.clamp();
      Storage.save("config.json", config);
   }

   public void onInitializeClient() {
      Storage.migrateFromAuction();
      config = Storage.load("config.json", Config.class, Config::new);
      config.clamp();
      saveConfig();
      Category var1 = Category.create(Identifier.of("neonlyrics", "main"));
      keyMenu = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.neonlyrics.openmenu", Type.KEYSYM, 74, var1));
      keySkip = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.neonlyrics.skip", Type.KEYSYM, 298, var1));
      keyPrev = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.neonlyrics.prev", Type.KEYSYM, -1, var1));
      keyPause = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.neonlyrics.pause", Type.KEYSYM, -1, var1));
      keyFav = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.neonlyrics.fav", Type.KEYSYM, 78, var1));
      keyClip = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.neonlyrics.clip", Type.KEYSYM, 299, var1));
      keyProfile = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.neonlyrics.profile", Type.KEYSYM, 297, var1));
      keySync = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.neonlyrics.sync", Type.KEYSYM, -1, var1));
      keyLyrics = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.neonlyrics.lyrics", Type.KEYSYM, -1, var1));
      keyEmote = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.neonlyrics.emote", Type.KEYSYM, 66, var1));
      HudElementRegistry.addFirst(Identifier.of("neonlyrics", "sky"), SkyRenderer::render);
      HudElementRegistry.addLast(Identifier.of("neonlyrics", "island"), Island::render);
      HudElementRegistry.replaceElement(VanillaHudElements.HOTBAR, GlassHotbar::wrap);
      HudElementRegistry.replaceElement(VanillaHudElements.CROSSHAIR, Crosshair::wrap);
      HudElementRegistry.addLast(Identifier.of("neonlyrics", "beatglow"), BeatGlow::render);
      HudElementRegistry.addLast(Identifier.of("neonlyrics", "book"), BookLyrics::render);
      WorldRenderEvents.AFTER_ENTITIES.register(WorldSky::render);
      SpotifyClient.init();
      StartupIntro.register();
      AttackEntityCallback.EVENT.register((AttackEntityCallback)(var0, var1x, var2, var3, var4) -> {
         DeathFx.onAttack(var3);
         return ActionResult.PASS;
      });
      ClientTickEvents.END_CLIENT_TICK.register(this::tick);
      ClientTickEvents.START_CLIENT_TICK.register(PhoneMenu::startTick);
      LOG.info("Lyrics Cord geladen");
   }

   private void tick(MinecraftClient var1) {
      if (!Beat.live()) {
         while (keyMenu.wasPressed()) {
            if (var1.currentScreen == null) {
               var1.setScreen(new LicenseScreen());
            }
         }

         for (KeyBinding var5 : new KeyBinding[]{keySkip, keyPrev, keyPause, keySync, keyFav, keyClip, keyProfile, keyLyrics, keyEmote}) {
            while (var5.wasPressed()) {
            }
         }

         if (!licenseHint && var1.player != null) {
            licenseHint = true;
            var1.inGameHud.getChatHud().addMessage(Text.translatable("neonlyrics.lic.chat"));
         }
      } else {
         AutoPerf.tick(var1);
         PhoneMenu.tick(var1);
         DeathFx.tick(var1);
         SkyRenderer.tick(var1);
         Emotes.tick(var1);

         while (keyEmote.wasPressed()) {
            if (Emotes.active()) {
               Emotes.stop();
            } else if (var1.currentScreen == null && var1.player != null) {
               var1.setScreen(new EmoteScreen());
            }
         }

         while (keyMenu.wasPressed()) {
            if (var1.currentScreen == null) {
               PhoneMenu.open(var1);
            }
         }

         while (keySkip.wasPressed()) {
            SpotifyClient.control(SpotifyClient.Cmd.NEXT);
         }

         while (keyPrev.wasPressed()) {
            SpotifyClient.control(SpotifyClient.Cmd.PREVIOUS);
         }

         while (keyPause.wasPressed()) {
            SpotifyClient.control(SpotifyClient.Cmd.PLAYPAUSE);
         }

         while (keySync.wasPressed()) {
            SpotifyClient.syncHere();
         }

         while (keyFav.wasPressed()) {
            Favorites.saveCurrent();
         }

         while (keyClip.wasPressed()) {
            var1.options.hudHidden = !var1.options.hudHidden;
         }

         while (keyProfile.wasPressed()) {
            Profiles.toggle();
         }

         while (keyLyrics.wasPressed()) {
            config.lyricsSky = !config.lyricsSky;
            saveConfig();
            Island.notify(Fx.t(config.lyricsSky ? "neonlyrics.media.lyricson" : "neonlyrics.media.lyricsoff"), config.lyricsSky);
         }
      }
   }
}
