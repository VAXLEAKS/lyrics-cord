package dev.neonlyrics.sky;

import dev.neonlyrics.NeonLyrics;
import dev.neonlyrics.config.Config;
import dev.neonlyrics.hud.Gfx;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public enum SkyPreset {
   SYNTHWAVE(721956, 5905006, 0.9F, 0.8F, false, 16732120, 4973567, SkyPreset.Weather.NONE, 0, true, 16759024),
   AURORA(133652, 731699, 0.85F, 1.0F, true, 4652976, 10124543, SkyPreset.Weather.NONE, 0, true, 10289108),
   NEBULA(459546, 1968698, 0.85F, 1.0F, false, 16736184, 7240959, SkyPreset.Weather.NONE, 0, true, 15780095),
   BLACK_HOLE(65541, 722456, 0.9F, 1.4F, false, 16757850, 16734766, SkyPreset.Weather.NONE, 0, false, 16766632),
   COSMOS(197132, 1182252, 0.9F, 1.7F, false, 11824127, 6015231, SkyPreset.Weather.NONE, 0, true, 14207231),
   NEON_CITY(329750, 2756170, 0.85F, 0.3F, false, 16727464, 3073791, SkyPreset.Weather.RAIN, 0, false, 16751320),
   GOLDEN_HOUR(2761552, 16751196, 0.7F, 0.15F, false, 16761466, 16743052, SkyPreset.Weather.FIREFLIES, 0, false, 16769208),
   COTTON_CANDY(5214453, 16763108, 0.85F, 0.0F, false, 16777215, 16766700, SkyPreset.Weather.NONE, 0, false, 16777215),
   SAKURA(3807820, 16757721, 0.6F, 0.3F, false, 16762598, 13144319, SkyPreset.Weather.PETALS, 0, false, 16762598),
   FROST(662064, 6264008, 0.75F, 0.8F, true, 12120319, 15137023, SkyPreset.Weather.SNOW, 15398655, false, 14677247),
   MIDNIGHT(131850, 1055290, 0.85F, 1.8F, false, 0, 0, SkyPreset.Weather.NONE, 15920861, true, 13161215),
   DEEP_SEA(136734, 739930, 0.8F, 0.3F, false, 6025958, 12124150, SkyPreset.Weather.NONE, 0, false, 11075572),
   BLOOD_MOON(1377030, 4852242, 0.85F, 0.6F, false, 16734794, 3803664, SkyPreset.Weather.EMBERS, 16726843, false, 16747146),
   STORM(921880, 3028034, 0.8F, 0.0F, false, 0, 0, SkyPreset.Weather.THUNDER, 0, false, 13161712),
   CUSTOM(656920, 2758216, 0.85F, 1.2F, true, 10719743, 16777215, SkyPreset.Weather.BLIZZARD, 0, true, 13219327),
   INFERNO(1704962, 8002566, 0.85F, 0.2F, false, 16747038, 16769658, SkyPreset.Weather.EMBERS, 0, false, 16757867),
   MATRIX(2052, 138252, 0.9F, 0.0F, false, 4652922, 14221280, SkyPreset.Weather.NONE, 0, false, 8191898),
   PRISM(459796, 1708086, 0.9F, 1.2F, false, 11824127, 4973567, SkyPreset.Weather.NONE, 0, true, 15126783),
   PULSAR(131852, 660528, 0.9F, 1.6F, false, 4973567, 11824127, SkyPreset.Weather.NONE, 0, true, 12580095),
   AQUARELL(15124712, 16770258, 0.8F, 0.0F, false, 16752329, 10475775, SkyPreset.Weather.PETALS, 0, false, 16777215),
   POLAR(1551, 670280, 0.9F, 0.0F, false, 3076288, 4951295, SkyPreset.Weather.NONE, 0, true, 10289128),
   GALAXY(131088, 1378360, 0.9F, 0.0F, false, 11824127, 6015231, SkyPreset.Weather.NONE, 0, true, 14207231),
   OCEAN(340843, 2932966, 0.9F, 0.0F, false, 6025958, 12124150, SkyPreset.Weather.NONE, 0, false, 12124159),
   ICE(662096, 6261990, 0.9F, 0.0F, false, 12120319, 15137023, SkyPreset.Weather.SNOW, 0, false, 14677247),
   PURPLE(656160, 4199518, 0.9F, 0.0F, false, 11824127, 14725375, SkyPreset.Weather.NONE, 0, true, 14729471),
   SUNSET(3021401, 16741158, 0.9F, 0.0F, false, 16757867, 16739950, SkyPreset.Weather.FIREFLIES, 0, false, 16765088),
   CHERRY(2034227, 9188224, 0.9F, 0.0F, false, 16762598, 13144319, SkyPreset.Weather.PETALS, 0, false, 16762598),
   CRIMSON(655362, 4195077, 0.9F, 0.0F, false, 16726826, 16747114, SkyPreset.Weather.EMBERS, 0, false, 16747146),
   THUNDER(198676, 1320010, 0.9F, 0.0F, false, 9416959, 13161727, SkyPreset.Weather.RAIN, 0, false, 13161712),
   HOLO(12170976, 15919349, 0.9F, 0.0F, false, 16752358, 9427199, SkyPreset.Weather.NONE, 0, false, 16777215),
   PAPER(6394075, 16765357, 0.9F, 0.0F, false, 16777215, 16765327, SkyPreset.Weather.NONE, 0, false, 16777215),
   RINGWORLD(1317445, 9732049, 0.9F, 0.0F, false, 15906949, 9221119, SkyPreset.Weather.NONE, 0, false, 16767160),
   TWIN_SUNS(1725035, 16754795, 0.9F, 0.0F, false, 16747584, 15135487, SkyPreset.Weather.NONE, 0, false, 16770760),
   CRYSTAL(5396408, 14273791, 0.9F, 0.0F, false, 12110079, 16777215, SkyPreset.Weather.NONE, 0, false, 15261951),
   LUNA(857144, 5730488, 0.9F, 0.0F, false, 14542079, 9217766, SkyPreset.Weather.NONE, 0, false, 14542079),
   DAWN(5080555, 16764088, 0.9F, 0.0F, false, 16777215, 16760972, SkyPreset.Weather.NONE, 0, false, 16777215),
   PIXEL(3681914, 16754821, 0.9F, 0.0F, false, 16747635, 7553184, SkyPreset.Weather.NONE, 0, false, 16767176),
   SPACE_WAR(526362, 3808840, 0.9F, 0.0F, false, 5880063, 16736320, SkyPreset.Weather.NONE, 0, false, 16765104),
   DJ(525330, 3804730, 0.9F, 0.0F, false, 12607743, 4259728, SkyPreset.Weather.NONE, 0, false, 15780095),
   RAIN(526606, 2501688, 0.9F, 0.0F, false, 10466520, 13161712, SkyPreset.Weather.RAIN, 0, false, 13161712);

   public static final int PAINTED = 20;
   public static final int FRESH = 29;
   public int top;
   public int horizon;
   public float strength;
   public float stars;
   public boolean aurora;
   public int aurora1;
   public int aurora2;
   public SkyPreset.Weather weather;
   public int moon;
   public boolean shootingStars;
   public int lyricColor;

   public boolean listed() {
      return this.ordinal() < 20 || this.ordinal() >= 29;
   }

   public boolean painted() {
      return this.ordinal() >= 20;
   }

   SkyPreset(
      int nullxx,
      int nullxxx,
      float nullxxxx,
      float nullxxxxx,
      boolean nullxxxxxx,
      int nullxxxxxxx,
      int nullxxxxxxxx,
      SkyPreset.Weather nullxxxxxxxxx,
      int nullxxxxxxxxxx,
      boolean nullxxxxxxxxxxx,
      int nullxxxxxxxxxxxx
   ) {
      this.top = nullxx;
      this.horizon = nullxxx;
      this.strength = nullxxxx;
      this.stars = nullxxxxx;
      this.aurora = nullxxxxxx;
      this.aurora1 = nullxxxxxxx;
      this.aurora2 = nullxxxxxxxx;
      this.weather = nullxxxxxxxxx;
      this.moon = nullxxxxxxxxxx;
      this.shootingStars = nullxxxxxxxxxxx;
      this.lyricColor = nullxxxxxxxxxxxx;
   }

   public boolean clouds() {
      return this == COTTON_CANDY;
   }

   public String key() {
      return "neonlyrics.sky." + this.name().toLowerCase();
   }

   public String[] features() {
      ArrayList<String> var1 = new ArrayList();
      switch (this) {
         case SYNTHWAVE:
            var1.add("neonlyrics.feat.synth");
         case AURORA:
         case SAKURA:
         case FROST:
         case MIDNIGHT:
         case BLOOD_MOON:
         case STORM:
         case CUSTOM:
         case POLAR:
         case GALAXY:
         case OCEAN:
         case ICE:
         case PURPLE:
         case SUNSET:
         case CHERRY:
         case CRIMSON:
         case THUNDER:
         case HOLO:
         case PAPER:
         case RINGWORLD:
         case TWIN_SUNS:
         case CRYSTAL:
         case LUNA:
         case DAWN:
         case PIXEL:
         case SPACE_WAR:
         case DJ:
         default:
            break;
         case NEBULA:
            var1.add("neonlyrics.feat.nebula");
            break;
         case BLACK_HOLE:
            var1.add("neonlyrics.feat.blackhole");
            break;
         case COSMOS:
            var1.add("neonlyrics.feat.galaxy");
            break;
         case NEON_CITY:
            var1.add("neonlyrics.feat.beams");
            break;
         case GOLDEN_HOUR:
         case COTTON_CANDY:
            var1.add("neonlyrics.feat.clouds");
            break;
         case DEEP_SEA:
            var1.add("neonlyrics.feat.lagoon");
            break;
         case INFERNO:
            var1.add("neonlyrics.feat.lava");
            break;
         case MATRIX:
            var1.add("neonlyrics.feat.code");
            break;
         case PRISM:
         case PULSAR:
            var1.add("neonlyrics.feat.beams");
            break;
         case AQUARELL:
            var1.add("neonlyrics.feat.paint");
            break;
         case RAIN:
            var1.add("neonlyrics.feat.wet");
      }

      if (this.aurora) {
         var1.add("neonlyrics.feat.aurora");
      }

      if (this.stars >= 1.0F) {
         var1.add("neonlyrics.feat.stars");
      }

      if (this.shootingStars) {
         var1.add("neonlyrics.feat.shooting");
      }

      if (this.moon != 0) {
         var1.add("neonlyrics.feat.moon");
      }

      if (this.weather != SkyPreset.Weather.NONE) {
         var1.add("neonlyrics.feat." + this.weather.name().toLowerCase());
      }

      return var1.toArray(new String[0]);
   }

   public static SkyPreset get(int var0) {
      SkyPreset[] var1 = values();
      SkyPreset var2 = var0 >= 0 && var0 < var1.length ? var1[var0] : null;
      if (var2 == CUSTOM) {
         applyCustom();
      }

      return var2;
   }

   public static void shuffle() {
      Config var0 = NeonLyrics.cfg();
      SkyPreset[] var1 = values();

      int var2;
      do {
         var2 = ThreadLocalRandom.current().nextInt(var1.length);
      } while (var1[var2] == CUSTOM || var2 == var0.sky || !var1[var2].listed());

      var0.sky = var2;
      var0.lastSky = var2;
      NeonLyrics.saveConfig();
   }

   public static void applyCustom() {
      Config var0 = NeonLyrics.cfg();
      CUSTOM.top = var0.customTop;
      CUSTOM.horizon = var0.customHorizon;
      CUSTOM.aurora = var0.customAurora;
      CUSTOM.aurora1 = var0.customAurora1;
      CUSTOM.aurora2 = var0.customAurora2;
      CUSTOM.stars = var0.customStars ? Math.max(0.1F, Math.min(2.0F, var0.customStarAmount)) : 0.0F;
      CUSTOM.moon = var0.customMoon ? (var0.customMoonColor == 0 ? 15920861 : var0.customMoonColor) : 0;
      CUSTOM.strength = Math.max(0.4F, Math.min(1.0F, var0.customStrength));
      CUSTOM.shootingStars = var0.customShooting;
      SkyPreset.Weather[] var1 = SkyPreset.Weather.values();
      CUSTOM.weather = var1[Math.floorMod(var0.customWeather, var1.length)];
      CUSTOM.lyricColor = Gfx.lerpColor(var0.customAurora1, 16777215, 0.35F);
   }

   public enum Weather {
      NONE,
      SNOW,
      BLIZZARD,
      RAIN,
      THUNDER,
      PETALS,
      EMBERS,
      FIREFLIES;
   }
}
