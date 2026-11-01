package dev.neonlyrics.config;

import dev.neonlyrics.hud.Gfx;
import dev.neonlyrics.sky.SkyPreset;
import dev.neonlyrics.sky.WorldSky;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Config {
   public int accent = 0;
   public boolean rgbAccent = false;
   public boolean blur = true;
   public boolean particles = false;
   public boolean ripples = false;
   public boolean sounds = true;
   public int uiFont = 0;
   public int lyricsFont = 0;
   public boolean fxMenuAnim = true;
   public int menuMode = 0;
   public String licenseCode = "";
   public Map<String, String> usedCodes = new HashMap<>();
   public int menuModeVer = 0;
   public boolean menuCustom = true;
   public int menuTime = 0;
   public int menuCam = 0;
   public boolean menuOwnBg = false;
   public int sky = 0;
   public int lastSky = 0;
   public float skyStrength = 0.8F;
   public float weatherAmount = 1.0F;
   public boolean realWeather = true;
   public boolean hideClouds = true;
   public String cloudsBefore = "";
   public boolean skyDay = true;
   public boolean fxDayNight = false;
   public boolean fxConstellations = true;
   public boolean fxSkyMusic = true;
   public float skySpeed = 1.0F;
   public int skyPalette = -1;
   public int hat = 0;
   public boolean hatGlow = true;
   public int crosshair = 0;
   public int trail = 0;
   public boolean aura = false;
   public int deathFx = 1;
   public boolean deathOthers = false;
   public boolean beatGlow = false;
   public boolean skyShuffle = false;
   public float skyPattern = 1.0F;
   public float skyLively = 0.6F;
   public float skyBreath = 1.0F;
   public int customTop = 656920;
   public int customHorizon = 2758216;
   public int customAurora1 = 10719743;
   public int customAurora2 = 16777215;
   public int customWeather = 2;
   public boolean customAurora = true;
   public boolean customStars = true;
   public boolean customMoon = false;
   public boolean customShooting = true;
   public float customStarAmount = 1.3F;
   public float customStrength = 0.85F;
   public int customMoonColor = 15920861;
   public String customImage = "";
   public List<String> customSaved = new ArrayList<>();
   public boolean lyricsSky = true;
   public boolean lyricsIsland = true;
   public boolean lyricsFollow = true;
   public int lyricsStyle = 0;
   public boolean lyricsViewLock = false;
   public float lyricsSize = 1.6F;
   public float lyricsOpacity = 0.9F;
   public boolean lyricsGlow = true;
   public float lyricsGlowStrength = 1.0F;
   public int lyricsGlowColor = -1;
   public int lyricsColor = -1;
   public int lyricsAnim = 0;
   public boolean lyricsUnderground = false;
   public boolean lyricsThroughBlocks = false;
   public int djStyle = 0;
   public boolean titleMenu = false;
   public int titlePlace = 0;
   public int titleTime = 4;
   public int titleCamera = 0;
   public String titleImage = "";
   public int titleCursor = 0;
   public int titleButtons = 3;
   public int titleWeather = 0;
   public int titleDesign = 0;
   public boolean titleParticles = true;
   public boolean titleClock = true;
   public int titleAnim = 2;
   public int titleParticleLevel = 2;
   public boolean titleRays = true;
   public boolean titleMusic = true;
   public boolean titleSplash = true;
   public boolean titleAnimals = true;
   public int lyricsOffsetMs = 0;
   public boolean fxBeatPulse = true;
   public boolean fxSongIntro = true;
   public boolean translate = false;
   public String translateTo = "de";
   public boolean islandEnabled = true;
   public boolean showClock = true;
   public boolean showPing = true;
   public boolean showFps = false;
   public boolean albumCover = true;
   public boolean glassHotbar = true;
   public boolean fxStartup = true;
   public int quality = 1;
   public boolean fpsBoost = false;
   public String boostBefore = "";
   public boolean autoPerf = true;
   public int targetFps = 0;
   public boolean fxWeather = true;
   public boolean fxAurora = true;
   public boolean fxStars = true;
   public boolean fxBeams = true;
   public boolean fxSkyColor = true;
   public int profile = 0;
   public boolean demoLyrics = false;
   public String spotifyClientId = "";
   public int spotifyMode = 0;
   public boolean spotifyTutorialSeen = false;
   public int styleVersion = 0;

   public void clamp() {
      this.deathFx = Math.max(0, Math.min(7, this.deathFx));
      if (this.styleVersion < 2 && this.spotifyClientId != null && !this.spotifyClientId.isBlank()) {
         this.spotifyMode = 1;
      }

      if (this.styleVersion < 3 && System.getProperty("os.name", "").toLowerCase(Locale.ROOT).contains("win")) {
         this.spotifyMode = 0;
      }

      if (this.styleVersion < 7) {
         this.fxDayNight = false;
         this.skyDay = true;
      }

      if (this.styleVersion < 8) {
         this.styleVersion = 8;
      }

      if (this.styleVersion < 9) {
         this.autoPerf = true;
         this.targetFps = 0;
         this.styleVersion = 9;
      }

      this.spotifyMode = Math.max(0, Math.min(1, this.spotifyMode));
      this.rgbAccent = false;
      int var1 = Gfx.FONT_IDS.length;
      this.uiFont = Math.max(0, Math.min(var1, this.uiFont));
      this.lyricsFont = Math.max(0, Math.min(var1 - 1, this.lyricsFont));
      this.sky = Math.max(-1, Math.min(SkyPreset.values().length - 1, this.sky));
      this.skyStrength = Math.max(0.1F, Math.min(1.0F, this.skyStrength));
      this.weatherAmount = Math.max(0.0F, Math.min(2.0F, this.weatherAmount));
      this.lyricsSize = Math.max(0.6F, Math.min(4.0F, this.lyricsSize));
      this.lyricsOpacity = Math.max(0.2F, Math.min(1.0F, this.lyricsOpacity));
      this.lyricsGlowStrength = Math.max(0.2F, Math.min(2.5F, this.lyricsGlowStrength));
      this.lyricsAnim = Math.floorMod(this.lyricsAnim, 3);
      this.quality = Math.max(0, Math.min(2, this.quality));
      this.djStyle = Math.floorMod(this.djStyle, 5);
      this.titlePlace = Math.floorMod(this.titlePlace, 4);
      this.titleTime = Math.floorMod(this.titleTime, 5);
      this.titleCamera = Math.floorMod(this.titleCamera, 3);
      this.titleCursor = Math.floorMod(this.titleCursor, 4);
      if (this.customSaved == null) {
         this.customSaved = new ArrayList<>();
      }

      if (this.customImage == null) {
         this.customImage = "";
      }

      while (this.customSaved.size() > 8) {
         this.customSaved.remove(this.customSaved.size() - 1);
      }

      this.customStarAmount = Math.max(0.0F, Math.min(2.0F, this.customStarAmount));
      this.customStrength = Math.max(0.4F, Math.min(1.0F, this.customStrength));
      if (this.titleDesign < 1) {
         this.titleDesign = 1;
         this.titleButtons = 3;
      }

      if (this.titleDesign < 2) {
         this.titleDesign = 2;
         this.titleMenu = false;
      }

      this.titleButtons = Math.floorMod(this.titleButtons, 4);
      this.titleWeather = Math.floorMod(this.titleWeather, 3);
      this.titleAnim = Math.floorMod(this.titleAnim, 3);
      this.titleParticleLevel = Math.floorMod(this.titleParticleLevel, 3);
      if (this.titleImage == null) {
         this.titleImage = "";
      }

      if (this.targetFps != 0) {
         this.targetFps = Math.max(30, Math.min(240, this.targetFps));
      }

      this.skySpeed = Math.max(0.25F, Math.min(2.0F, this.skySpeed));
      this.skyPattern = Math.max(0.2F, Math.min(1.5F, this.skyPattern));
      this.skyLively = Math.max(0.0F, Math.min(1.0F, this.skyLively));
      this.skyBreath = Math.max(0.0F, Math.min(2.0F, this.skyBreath));
      this.skyPalette = Math.max(-1, Math.min(WorldSky.PALETTES.length - 1, this.skyPalette));
      this.lyricsOffsetMs = Math.max(-5000, Math.min(5000, this.lyricsOffsetMs));
      this.lyricsStyle = Math.floorMod(this.lyricsStyle, 3);
      if (this.spotifyClientId == null) {
         this.spotifyClientId = "";
      }

      if (this.cloudsBefore == null) {
         this.cloudsBefore = "";
      }

      if (this.boostBefore == null) {
         this.boostBefore = "";
      }

      if (this.translateTo == null) {
         this.translateTo = "de";
      }

      if (this.licenseCode == null) {
         this.licenseCode = "";
      }

      if (this.usedCodes == null) {
         this.usedCodes = new HashMap<>();
      }

      if (this.sky >= 20 && this.sky < 29) {
         this.sky = SkyPreset.INFERNO.ordinal();
      }

      if (this.menuModeVer < 2) {
         this.menuMode = 0;
         this.menuModeVer = 2;
      }

      if (this.sky < 0) {
         this.sky = Math.max(0, this.lastSky);
      }

      this.skyDay = true;
      this.fxDayNight = false;
      if (this.sky >= 0) {
         this.lastSky = this.sky;
      }
   }
}
