package dev.neonlyrics.gui;

import dev.neonlyrics.Beat;
import dev.neonlyrics.NeonLyrics;
import dev.neonlyrics.config.Config;
import dev.neonlyrics.cosmetic.Accessories;
import dev.neonlyrics.cosmetic.DeathFx;
import dev.neonlyrics.cosmetic.PhoneMenu;
import dev.neonlyrics.hud.Anim;
import dev.neonlyrics.hud.Cover;
import dev.neonlyrics.hud.Gfx;
import dev.neonlyrics.hud.Icons;
import dev.neonlyrics.hud.Island;
import dev.neonlyrics.hud.SkyThumbs;
import dev.neonlyrics.hud.Theme;
import dev.neonlyrics.lyrics.Favorites;
import dev.neonlyrics.lyrics.LocalMedia;
import dev.neonlyrics.lyrics.NowPlaying;
import dev.neonlyrics.lyrics.SpotifyClient;
import dev.neonlyrics.sky.SkyPreset;
import dev.neonlyrics.sky.SkyPreview;
import dev.neonlyrics.sky.WorldSky;
import dev.neonlyrics.util.AutoPerf;
import dev.neonlyrics.util.FpsBoost;
import dev.neonlyrics.util.Fx;
import dev.neonlyrics.util.Profiles;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.input.KeyInput;
import net.minecraft.text.Style;
import net.minecraft.text.StyleSpriteSource;
import net.minecraft.text.Text;
import net.minecraft.text.StyleSpriteSource.Font;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.joml.Matrix3x2f;
import org.joml.Vector2f;

public class LyricsScreen extends Screen {
   private static final int COL_BG = 1118484;
   private static final int GROUP = 1710623;
   private static final int GROUP_HI = 2236968;
   private static final int LINE = 2500140;
   private static final int TEXT = 15658737;
   private static final int SUB = 8618892;
   private static final int DIM = 5000276;
   private static final int TRACK = 3421243;
   private static final int KNOB_OFF = 9079442;
   private static final int INK = 1118484;
   private static final Map<LyricsScreen.T, StyleSpriteSource> FONTS = new EnumMap<>(LyricsScreen.T.class);
   private final Map<LyricsScreen.T, Map<String, Text>> comps = new EnumMap<>(LyricsScreen.T.class);
   private final Map<LyricsScreen.T, Map<String, Integer>> widths = new EnumMap<>(LyricsScreen.T.class);
   private static final SkyPreset[] SKY_ORDER = new SkyPreset[]{
      SkyPreset.RAIN,
      SkyPreset.DJ,
      SkyPreset.SPACE_WAR,
      SkyPreset.HOLO,
      SkyPreset.PAPER,
      SkyPreset.RINGWORLD,
      SkyPreset.TWIN_SUNS,
      SkyPreset.CRYSTAL,
      SkyPreset.LUNA,
      SkyPreset.DAWN,
      SkyPreset.PIXEL,
      SkyPreset.INFERNO,
      SkyPreset.MATRIX,
      SkyPreset.PRISM,
      SkyPreset.PULSAR,
      SkyPreset.AQUARELL,
      SkyPreset.SYNTHWAVE,
      SkyPreset.AURORA,
      SkyPreset.NEBULA,
      SkyPreset.BLACK_HOLE,
      SkyPreset.COSMOS,
      SkyPreset.NEON_CITY,
      SkyPreset.GOLDEN_HOUR,
      SkyPreset.COTTON_CANDY,
      SkyPreset.SAKURA,
      SkyPreset.FROST,
      SkyPreset.MIDNIGHT,
      SkyPreset.DEEP_SEA,
      SkyPreset.BLOOD_MOON,
      SkyPreset.STORM,
      SkyPreset.CUSTOM
   };
   private static final int[] DARK_COLS = new int[]{131848, 460058, 656920, 1378328, 201240, 1053982, 1839672, 2758216, 4068404, 668480, 1784412, 4852242};
   private static final int[] BRIGHT = new int[]{
      10719743, 13716479, 16743112, 16739950, 16757596, 16769658, 4652976, 3073750, 8177919, 4942847, 16184063, 16777215
   };
   private static final String[] LANGS = new String[]{"de", "en", "es", "fr", "tr", "pl", "it", "ru"};
   private static final int[] TARGETS = new int[]{0, 60, 90, 120, 144, 240};
   private static final int RAIL = 56;
   private static final int ROW = 38;
   private static LyricsScreen.Page lastPage = LyricsScreen.Page.SKY;
   private LyricsScreen.Page page;
   private long pageTime;
   private final long openTime = System.currentTimeMillis();
   private long closeTime = -1L;
   private int dir = 1;
   private final Anim.Pool pool = new Anim.Pool(12.0);
   private final Map<String, Long> pressed = new HashMap<>();
   private final List<LyricsScreen.Hit> hits = new ArrayList<>();
   private final List<LyricsScreen.Slider> sliders = new ArrayList<>();
   private LyricsScreen.Slider activeSlider;
   private int mx;
   private int my;
   private float S = 1.0F;
   private int W;
   private int H;
   private int colX;
   private int colY;
   private int colW;
   private int colH;
   private int vx;
   private int vy;
   private int vw;
   private int vh;
   private float stageK = 0.0F;
   private float scroll = 0.0F;
   private float scrollTarget = 0.0F;
   private int contentH = 0;
   private int ly;
   private int rowIdx;
   private float rowA = 1.0F;
   private boolean inContent;
   private boolean firstInGroup;
   private long lastNs = System.nanoTime();
   private DrawContext gfx;
   private TextFieldWidget idBox;
   private int idX;
   private int idY;
   private int idW;
   private boolean idWanted;
   private String lastTrack = "";
   private long trackAt = 0L;
   private float spin = 0.0F;
   private float vizLevel = 0.0F;
   private float frameDt = 0.016F;
   private final Map<String, float[]> springs = new HashMap<>();
   private final Map<String, LyricsScreen.Ripple> ripples = new HashMap<>();
   private final Map<Integer, Long> seen = new HashMap<>();
   private String curRid;
   private boolean floating;
   private final Matrix3x2f floatInv = new Matrix3x2f();
   private SkyPreset skyHover;
   private SkyPreset skyHoverLast;
   private long skyHoverAt;

   private static StyleSpriteSource fd(String var0) {
      return new Font(Identifier.of("neonlyrics", var0));
   }

   public static SkyPreset[] skyOrder() {
      return (SkyPreset[])SKY_ORDER.clone();
   }

   public LyricsScreen() {
      this(lastPage);
   }

   public LyricsScreen(boolean var1) {
      this(lastPage);
      this.floating = var1;
   }

   public LyricsScreen(LyricsScreen.Page var1) {
      super(Text.literal("Lyrics Cord"));
      this.page = var1;
      this.pageTime = System.currentTimeMillis() + 520L;
   }

   private static Config c() {
      return NeonLyrics.cfg();
   }

   private static String t(String var0, Object... var1) {
      return Fx.t("neonlyrics." + var0, var1);
   }

   protected void init() {
      this.layout(SpotifyClient.now().hasTrack() ? 1.0F : 0.0F);
      this.stageK = SpotifyClient.now().hasTrack() ? 1.0F : 0.0F;
      this.idBox = new TextFieldWidget(this.textRenderer, 0, -200, 100, 12, Text.literal(""));
      this.idBox.setDrawsBackground(false);
      this.idBox.setMaxLength(64);
      this.idBox.setEditableColor(-1118479);
      this.idBox.setText(c().spotifyClientId);
      this.idBox.setPlaceholder(Text.literal(t("hint.clientid")).formatted(Formatting.DARK_GRAY));
      this.addDrawableChild(this.idBox);
      show(this.idBox, false);
   }

   private void layout(float var1) {
      this.S = Math.min(1.0F, Math.min(this.width / 640.0F, this.height / 350.0F));
      this.W = Math.round(this.width / this.S);
      this.H = Math.round(this.height / this.S);
      int var2 = Math.max(330, Math.min(470, Math.round(this.W * 0.46F)));
      int var3 = Math.min(this.W - 60, 640);
      this.colW = Math.round(var3 + (var2 - var3) * var1);
      int var4 = this.W - var2 - 16;
      int var5 = (this.W - var3) / 2;
      this.colX = Math.round(var5 + (var4 - var5) * var1);
      this.colY = 12;
      this.colH = this.H - 24;
      this.vx = this.colX + 56 + 18;
      this.vw = this.colW - 56 - 18 - 16;
      this.vy = this.colY + 50;
      this.vh = this.colH - 50 - 10;
   }

   private static void show(TextFieldWidget var0, boolean var1) {
      var0.visible = var1;
      var0.active = var1;
      if (!var1 && var0.isFocused()) {
         var0.setFocused(false);
      }
   }

   private void go(LyricsScreen.Page var1) {
      if (var1 != this.page) {
         this.dir = var1.ordinal() >= this.page.ordinal() ? 1 : -1;
         this.page = var1;
         lastPage = var1;
         this.pageTime = System.currentTimeMillis();
         this.scroll = this.scrollTarget = 0.0F;
         this.seen.clear();
         this.setFocused(null);
         Fx.sound(Fx.Sfx.CLICK);
      }
   }

   private boolean anim() {
      return c().fxMenuAnim;
   }

   private float since() {
      return this.anim() ? (float)(System.currentTimeMillis() - this.openTime) / 1000.0F : 9.0F;
   }

   private float closeK() {
      if (this.closeTime < 0L) {
         return 1.0F;
      } else {
         return this.anim() ? 1.0F - Anim.outCubic(Anim.since(this.closeTime, 160L)) : 0.0F;
      }
   }

   private static float oc(float var0) {
      return Anim.outCubic(Anim.clamp(var0));
   }

   private static float ob(float var0) {
      return Anim.outBack(Anim.clamp(var0));
   }

   public void renderBackground(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
      if (!this.floating) {
         float var5 = oc(this.since() / 0.3F) * this.closeK();
         boolean var6 = c().blur;
         if (var6 && this.client != null && this.client.world != null) {
            super.renderBackground(context, mouseX, mouseY, deltaTicks);
         }

         Gfx.alpha = 1.0F;
         byte var7 = 24;

         for (int var8 = 0; var8 < var7; var8++) {
            float var9 = var8 / (var7 - 1.0F);
            float var10 = var9 < 0.55F ? 0.86F + -0.31F * (var9 / 0.55F) : 0.55F + 0.25F * ((var9 - 0.55F) / 0.45F);
            if (this.stageK < 0.99F) {
               var10 += (0.62F - var10) * (1.0F - this.stageK);
            }

            int var11 = Math.round((float)(this.width * var8) / var7);
            int var12 = Math.round((float)(this.width * (var8 + 1)) / var7);
            Gfx.rect(context, var11, 0, var12 - var11, this.height, 263176, var10 * var5);
         }

         float var13 = Math.max(0.0F, 1.0F - Math.abs(this.since() - 0.12F) / 0.12F);
         if (var13 > 0.01F) {
            Gfx.rect(context, 0, 0, this.width, this.height, 16777215, 0.1F * var13);
         }
      }
   }

   public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
      if (!this.floating) {
         this.renderBody(context, mouseX, mouseY, deltaTicks);
      } else {
         float var5 = this.since();
         float var6 = this.closeK();
         float var7 = this.anim() ? oc(var5 / 0.22F) : 1.0F;
         float var8 = this.anim() ? oc((var5 - 0.12F) / 0.38F) : 1.0F;
         float var9 = 0.08F + 0.92F * var7;
         float var10 = (0.03F + 0.97F * var8) * (0.03F + 0.97F * var6);
         float var11 = 0.84F;
         float var12 = (float)Math.sin(System.currentTimeMillis() / 900.0) * this.height * 0.005F;
         Matrix3x2f var13 = new Matrix3x2f()
            .translate(this.width / 2.0F, this.height / 2.0F + var12)
            .mul(new Matrix3x2f(1.0F, -0.035F, 0.0F, 1.0F, 0.0F, 0.0F))
            .scale(var11 * var9, var11 * var10)
            .translate(-this.width / 2.0F, -this.height / 2.0F);
         var13.invert(this.floatInv);
         Vector2f var14 = this.floatInv.transformPosition(mouseX, mouseY, new Vector2f());
         context.getMatrices().pushMatrix();
         context.getMatrices().mul(var13);
         float var15 = Math.min(1.0F, var5 / 0.15F) * var6;
         Gfx.glow(context, 0, 0, this.width, this.height, 22, 10321407, 0.55F * var15, 18);
         Gfx.aaRound(context, 0.0F, 0.0F, this.width, this.height, 22.0F, 920352, 0.86F * var15);
         Gfx.aaRound(context, 1.0F, 1.0F, this.width - 2, this.height / 2.0F, 21.0F, 16777215, 0.025F * var15);
         this.renderBody(context, Math.round(var14.x), Math.round(var14.y), deltaTicks);
         context.getMatrices().popMatrix();
      }
   }

   private Click fm(Click var1) {
      if (!this.floating) {
         return var1;
      }

      Vector2f var2 = this.floatInv.transformPosition((float)var1.x(), (float)var1.y(), new Vector2f());
      return new Click(var2.x, var2.y, var1.buttonInfo());
   }

   private void updateSkyHover() {
      long var1 = System.currentTimeMillis();
      if (this.skyHover != this.skyHoverLast) {
         this.skyHoverLast = this.skyHover;
         this.skyHoverAt = var1;
      }

      if (this.skyHover != null && var1 - this.skyHoverAt > 280L) {
         SkyPreview.set(this.skyHover);
      } else if (this.skyHover == null) {
         SkyPreview.clear();
      }
   }

   public void removed() {
      SkyPreview.clear();
      super.removed();
      if (this.floating) {
         PhoneMenu.menuClosed();
      }
   }

   private void renderBody(DrawContext var1, int var2, int var3, float var4) {
      if (this.closeTime < 0L || this.anim() && System.currentTimeMillis() - this.closeTime <= 170L) {
         long var5 = System.nanoTime();
         float var7 = Math.min(0.1F, (float)(var5 - this.lastNs) / 1.0E9F);
         this.lastNs = var5;
         this.frameDt = var7;
         NowPlaying var8 = SpotifyClient.now();
         this.stageK = this.pool.to("stage", var8.hasTrack() ? 1.0 : 0.0);
         if (!this.anim()) {
            this.stageK = var8.hasTrack() ? 1.0F : 0.0F;
         }

         this.layout(this.stageK);
         this.mx = Math.round(var2 / this.S);
         this.my = Math.round(var3 / this.S);
         this.gfx = var1;
         Gfx.revealOn = false;
         if (this.activeSlider != null) {
            if (!this.client.mouse.wasLeftButtonClicked()) {
               this.activeSlider = null;
               NeonLyrics.saveConfig();
            } else {
               this.activeSlider.set().accept(Anim.clamp((float)(this.mx - this.activeSlider.x()) / this.activeSlider.w()));
            }
         }

         this.hits.clear();
         this.sliders.clear();
         this.idWanted = false;
         float var9 = this.since();
         float var10 = this.closeK();
         var1.getMatrices().pushMatrix();
         var1.getMatrices().scale(this.S, this.S);
         Gfx.alpha = var10;
         if (this.stageK > 0.01F) {
            this.stage(var1, var8, var9, var7);
         }

         float var11 = this.anim() ? ob((var9 - 0.3F) / 0.5F) : 1.0F;
         float var12 = (1.0F - var11) * (this.W - this.colX + 20) + (1.0F - var10) * 30.0F;
         var1.getMatrices().pushMatrix();
         var1.getMatrices().translate(var12, 0.0F);
         if (var11 < 0.999F || var10 < 0.999F) {
            float var13 = 0.92F + 0.08F * Math.min(var11, 1.02F) - 0.04F * (1.0F - var10);
            float var14 = this.colX + this.colW / 2.0F;
            float var15 = this.colY + this.colH / 2.0F;
            var1.getMatrices().translate(var14, var15);
            var1.getMatrices().rotate((1.0F - Anim.clamp(var11)) * 0.06F);
            var1.getMatrices().scale(var13, var13);
            var1.getMatrices().translate(-var14, -var15);
         }

         this.skyHover = null;
         this.column(var1, var7);
         this.updateSkyHover();
         var1.getMatrices().popMatrix();
         var1.getMatrices().popMatrix();
         Gfx.alpha = 1.0F;
         float var16 = this.anim() ? Anim.outCubic(Anim.since(this.pageTime, 260L)) : 1.0F;
         boolean var17 = this.idWanted && this.closeTime < 0L && var11 > 0.98F && var16 > 0.95F && this.idY >= this.vy && this.idY + 12 <= this.vy + this.vh;
         show(this.idBox, var17);
         if (var17) {
            this.idBox.setX(Math.round(this.idX * this.S));
            this.idBox.setY(Math.round(this.idY * this.S));
            this.idBox.setWidth(Math.max(20, Math.round(this.idW * this.S)));
            c().spotifyClientId = this.idBox.getText().trim();
         }

         super.render(var1, var2, var3, var4);
      } else {
         super.close();
      }
   }

   private void stage(DrawContext var1, NowPlaying var2, float var3, float var4) {
      Config var5 = c();
      long var6 = System.currentTimeMillis();
      String var8 = String.valueOf(var2.trackId);
      if (!var8.equals(this.lastTrack)) {
         this.lastTrack = var8;
         this.trackAt = var6;
      }

      float var9 = Gfx.alpha;
      float var10 = this.stageK;
      int var11 = Math.round((1.0F - var10) * -40.0F);
      int var12 = 40 + var11;
      int var13 = this.colX - 34;
      int var14 = Math.max(120, var13 - var12);
      int var15 = Math.round(Math.min(150.0F, Math.min(this.H * 0.38F, var14 * 0.46F)));
      int var16 = Math.round(this.H * 0.12F);
      if (var2.playing) {
         this.spin += var4 * 70.0F;
      }

      float var17 = this.anim() ? oc((var3 - 0.4F) / 0.5F) : 1.0F;
      float var18 = var15 * 0.47F;
      float var19 = var12 + var15 / 2.0F + var17 * var15 * 0.6F;
      float var20 = var16 + var15 / 2.0F;
      if (var17 > 0.01F) {
         Gfx.alpha = var9 * var10 * Anim.clamp((var3 - 0.4F) / 0.1F);
         Gfx.aaCircle(var1, var19, var20, var18, 789518, 1.0F);

         for (int var21 = 1; var21 <= 7; var21++) {
            Gfx.aaCircle(var1, var19, var20, var18 * (1.0F - 0.075F * var21), (var21 & 1) == 1 ? 1513243 : 789518, 1.0F);
         }

         float var47 = var18 * 0.62F;
         var1.getMatrices().pushMatrix();
         var1.getMatrices().translate(var19, var20);
         var1.getMatrices().rotate((float)Math.toRadians(this.spin));
         Gfx.aaRound(var1, -var18 * 0.92F, -0.75F, var18 * 1.84F, 1.5F, 0.75F, 16777215, 0.05F);
         Gfx.aaRound(var1, -0.75F, -var18 * 0.92F, 1.5F, var18 * 1.84F, 0.75F, 16777215, 0.03F);
         if (!Cover.draw(var1, Math.round(-var47 / 2.0F), Math.round(-var47 / 2.0F), Math.round(var47), 1.0F)) {
            Gfx.aaCircle(var1, 0.0F, 0.0F, var47 / 2.0F, 2763315, 1.0F);
            Gfx.aaCircle(var1, var47 * 0.18F, -var47 * 0.1F, 2.0F, 16777215, 0.6F);
         }

         Gfx.aaCircle(var1, 0.0F, 0.0F, 2.2F, 789518, 1.0F);
         var1.getMatrices().popMatrix();
      }

      float var48 = this.anim() ? ob((var3 - 0.05F) / 0.45F) : 1.0F;
      float var22 = this.anim() ? oc((float)(var6 - this.trackAt) / 450.0F) : 1.0F;
      Gfx.alpha = var9 * var10 * Anim.clamp((var3 - 0.05F) / 0.2F);
      var1.getMatrices().pushMatrix();
      var1.getMatrices().translate(var12 + var15 / 2.0F, var16 + var15 / 2.0F + (1.0F - var48) * 60.0F);
      var1.getMatrices().rotate((float)Math.toRadians((1.0F - var48) * -14.0F));
      float var23 = (0.94F + 0.06F * var22) * (0.8F + 0.2F * var48);
      var1.getMatrices().scale(var23, var23);
      Gfx.aaRound(var1, -var15 / 2.0F - 2.0F, -var15 / 2.0F + 4.0F, var15 + 4, var15 + 4, 10.0F, 0, 0.35F);
      if (!Cover.drawSquare(var1, -var15 / 2, -var15 / 2, var15, 1.0F)) {
         Gfx.aaRound(var1, -var15 / 2.0F, -var15 / 2.0F, var15, var15, 8.0F, 1973796, 1.0F);
         Icons.draw(var1, Icons.Icon.LYRICS, 0.0F, 0.0F, var15 * 0.26F, 4539725, 1.0F);
      }

      var1.getMatrices().popMatrix();
      int var24 = var16 + var15 + 20;
      float var25 = this.anim() ? oc((var3 - 0.55F) / 0.4F) : 1.0F;
      Gfx.alpha = var9 * var10 * var25 * var22;
      int var26 = Math.round((1.0F - var25 * var22) * 14.0F);
      float var27 = this.anim() ? Anim.outCubic(Anim.clamp((var3 - 0.55F) / 0.5F)) * var22 : 1.0F;
      String var28 = this.ell(var2.title, var14, LyricsScreen.T.XL);
      if (var27 < 0.999F) {
         var1.enableScissor(var12 - 2, var24 - 4, var12 + Math.round((this.tw(var28, LyricsScreen.T.XL) + 6) * var27), var24 + 22);
      }

      this.txt(var28, var12, var24 + var26, 15658737, 1.0F, LyricsScreen.T.XL);
      if (var27 < 0.999F) {
         var1.disableScissor();
      }

      this.txt(this.ell(var2.artist, var14, LyricsScreen.T.M), var12, var24 + 24 + var26, 8618892, 1.0F, LyricsScreen.T.M);
      float var29 = this.anim() ? oc((var3 - 0.7F) / 0.4F) : 1.0F;
      Gfx.alpha = var9 * var10 * var29;
      int var30 = var24 + 46 + Math.round((1.0F - var29) * 14.0F);
      if (var2.lyricsState == NowPlaying.LyricsState.SYNCED) {
         long var31 = var2.progressNow(var5.lyricsOffsetMs);
         int var33 = var2.lineIndex(var31);
         if (var33 >= 0 && var33 < var2.lines.size() && !var2.lines.get(var33).text().isBlank()) {
            NowPlaying.Line var34 = var2.lines.get(var33);
            long var35 = var33 + 1 < var2.lines.size() ? var2.lines.get(var33 + 1).timeMs() : var34.timeMs() + 4000L;
            float var37 = Anim.clamp((float)(var31 - var34.timeMs()) / (float)Math.max(400L, Math.min(var35 - var34.timeMs(), 6000L) - 200L));
            String[] var38 = var34.text().split(" ");
            int var39 = (int)Math.ceil(var37 * var38.length);
            if (var34.wordTimes() != null && var34.wordTimes().length == var38.length) {
               var39 = 0;

               for (long var43 : var34.wordTimes()) {
                  if (var43 <= var31) {
                     var39++;
                  }
               }
            }

            float var56 = Anim.clamp(this.pool.toFrom("ln" + var33 + var8, 0.0, 1.0));
            int var57 = var30 + Math.round((1.0F - var56) * 8.0F);
            float var60 = var12;
            int var63 = 0;
            int var44 = this.tw(" ", LyricsScreen.T.TITLE);

            for (int var45 = 0; var45 < var38.length && var63 < 2; var45++) {
               int var46 = this.tw(var38[var45], LyricsScreen.T.TITLE);
               if (var60 + var46 > var12 + var14 && var60 > var12) {
                  var60 = var12;
                  var57 += 15;
                  if (++var63 >= 2) {
                     break;
                  }
               }

               this.txt(var38[var45], var60, var57, var45 < var39 ? 15658737 : 5921378, var56, LyricsScreen.T.TITLE);
               var60 += var46 + var44;
            }

            if (var33 + 1 < var2.lines.size()) {
               this.txt(this.ell(var2.lines.get(var33 + 1).text(), var14, LyricsScreen.T.M), var12, var57 + 19, 5921378, var56, LyricsScreen.T.M);
            }
         }
      } else {
         String var49 = switch (var2.lyricsState) {
            case LOADING -> t("spotify.ly.loading");
            case NOT_FOUND -> t("spotify.ly.none");
            case ERROR -> t("spotify.ly.error");
            default -> "";
         };
         this.txt(this.ell(var49, var14, LyricsScreen.T.M), var12, var30, 8618892, 1.0F, LyricsScreen.T.M);
      }

      byte var50 = 28;
      byte var32 = 3;
      byte var51 = 3;
      byte var52 = 34;
      int var53 = this.H - 26;
      long var36 = var2.progressNow(var5.lyricsOffsetMs);
      float var54 = var2.playing ? var2.beatPulse(var36) : 0.0F;
      this.vizLevel = this.vizLevel + ((var2.playing ? 1.0F : 0.12F) - this.vizLevel) * (1.0F - (float)Math.exp(-6.0F * var4));
      double var55 = var6 / 1000.0;

      for (int var58 = 0; var58 < var50; var58++) {
         float var61 = this.anim() ? oc((var3 - 0.5F - var58 * 0.012F) / 0.3F) : 1.0F;
         double var64 = Math.abs(Math.sin(var58 * 0.7 + var55 * 6.0) * Math.sin(var58 * 0.23 + var55 * 3.1));
         float var67 = 3.0F + (var52 - 3) * (float)var64 * this.vizLevel * (0.55F + 0.6F * var54) * var61;
         Gfx.alpha = var9 * var10 * (0.3F + 0.6F * var61);
         Gfx.aaRound(var1, var12 + var58 * (var32 + var51), var53 - var67, var32, var67, 1.5F, 16777215, 0.85F);
      }

      Gfx.alpha = var9 * var10 * (this.anim() ? oc((var3 - 0.75F) / 0.3F) : 1.0F);
      int var59 = var12 + var50 * (var32 + var51) + 24;
      int var62 = var53 - 14;
      this.iconBtn(var1, "prev", var59, var62 - 10, 20, Icons.Icon.PREV, 8.0F, () -> SpotifyClient.control(SpotifyClient.Cmd.PREVIOUS));
      float var65 = this.hover(var59 + 28, var62 - 14, 28, 28, "play");
      float var66 = 1.0F - Anim.outCubic(Anim.since(this.pressed.getOrDefault("play", 0L), 220L));
      Gfx.aaCircle(var1, var59 + 42, var62, 14.0F * (1.0F + 0.05F * var65) * (1.0F - 0.08F * var66), 16777215, 1.0F);
      Icons.draw(var1, var2.playing ? Icons.Icon.PAUSE : Icons.Icon.PLAY, var59 + 42 + (var2.playing ? 0.0F : 0.8F), var62, 10.0F, 1118484, 1.0F);
      this.hit(var59 + 28, var62 - 14, 28, 28, () -> {
         this.pressed.put("play", System.currentTimeMillis());
         SpotifyClient.control(SpotifyClient.Cmd.PLAYPAUSE);
      });
      this.iconBtn(var1, "next", var59 + 64, var62 - 10, 20, Icons.Icon.NEXT, 8.0F, () -> SpotifyClient.control(SpotifyClient.Cmd.NEXT));
      this.iconBtn(var1, "fav", var59 + 92, var62 - 10, 20, Icons.Icon.FAV, 8.0F, Favorites::saveCurrent);
      Gfx.alpha = var9;
   }

   private Icons.Icon icon(LyricsScreen.Page var1) {
      return switch (var1) {
         case SKY -> Icons.Icon.SKY;
         case LYRICS -> Icons.Icon.TEXT;
         case STYLE -> Icons.Icon.HAT;
         case SPOTIFY -> Icons.Icon.SPOTIFY;
         case DISPLAY -> Icons.Icon.DISPLAY;
         case MENU -> Icons.Icon.SETTINGS;
         case PERF -> Icons.Icon.PERF;
         case FAV -> Icons.Icon.FAV;
      };
   }

   private String title(LyricsScreen.Page var1) {
      return t("ui.page." + var1.name().toLowerCase(Locale.ROOT));
   }

   private String subtitle(LyricsScreen.Page var1) {
      return var1 == LyricsScreen.Page.SKY ? t("ui.page.sky.sub", SKY_ORDER.length) : t("ui.page." + var1.name().toLowerCase(Locale.ROOT) + ".sub");
   }

   private void column(DrawContext var1, float var2) {
      Gfx.aaRound(var1, this.colX - 1, this.colY - 1, this.colW + 2, this.colH + 2, 15.0F, 16777215, 0.1F);
      Gfx.aaRound(var1, this.colX, this.colY, this.colW, this.colH, 14.0F, 1118484, 0.94F);
      Gfx.vGradient(var1, this.colX + 10, this.colY + 1, this.colW - 20, 40, 16777215, 16777215, 0.05F, 0.0F);
      Gfx.rect(var1, this.colX + 56, this.colY + 12, 1, this.colH - 24, 2500140, 1.0F);
      int var3 = this.colY + 10;
      int var4 = Math.min(44, (this.colH - 20) / LyricsScreen.Page.values().length - 2);
      float var5 = this.spring("railsel", this.page.ordinal());
      float var6 = this.springs.get("railsel")[1];
      float var7 = Math.min(10.0F, Math.abs(var6) * 0.9F);
      Gfx.aaRound(var1, this.colX + 6, var3 + var5 * (var4 + 2) - var7 / 2.0F, 44.0F, var4 + var7, 10.0F, 16777215, 1.0F);

      for (LyricsScreen.Page var11 : LyricsScreen.Page.values()) {
         int var12 = var3 + var11.ordinal() * (var4 + 2);
         float var13 = Anim.clamp(1.0F - Math.abs(var5 - var11.ordinal()));
         float var14 = this.hover(this.colX + 6, var12, 44, var4, "rail" + var11.name());
         if (var11 != this.page && var14 > 0.01F) {
            Gfx.aaRound(var1, this.colX + 6, var12, 44.0F, var4, 10.0F, 16777215, 0.05F * var14);
         }

         int var15 = Gfx.lerpColor(Gfx.lerpColor(8618892, 13158606, var14), 1118484, var13);
         float var16 = this.anim() ? Anim.outBack(Anim.clamp((this.since() - 0.55F - var11.ordinal() * 0.06F) / 0.35F)) : 1.0F;
         float var17 = (1.0F + 0.18F * (1.0F - Anim.outCubic(Anim.since(this.pressed.getOrDefault("rail" + var11.name(), 0L), 380L))) + 0.08F * var14)
            * Math.max(0.01F, var16);
         Icons.draw(var1, this.icon(var11), this.colX + 28.0F, var12 + var4 / 2.0F - 5.0F - var14, 10.0F * var17, var15, 1.0F);
         this.txtC(this.ell(this.title(var11), 42, LyricsScreen.T.S), this.colX + 28.0F, var12 + var4 / 2.0F + 5.0F, var15, 1.0F, LyricsScreen.T.S);
         LyricsScreen.Page var18 = var11;
         this.hit(this.colX + 6, var12, 44, var4, () -> {
            this.pressed.put("rail" + var18.name(), System.currentTimeMillis());
            this.go(var18);
         });
      }

      int var22 = this.colX + this.colW - 28;
      int var23 = this.colY + 12;
      float var24 = this.hover(var22, var23, 18, 18, "close");
      Gfx.aaCircle(var1, var22 + 9, var23 + 9, 9.0F, 16777215, 0.05F + 0.07F * var24);
      var1.getMatrices().pushMatrix();
      var1.getMatrices().translate(var22 + 9, var23 + 9);
      var1.getMatrices().rotate((float)(var24 * Math.PI / 2.0));
      Icons.draw(var1, Icons.Icon.CLOSE, 0.0F, 0.0F, 6.5F, Gfx.lerpColor(8618892, 15658737, var24), 1.0F);
      var1.getMatrices().popMatrix();
      this.hit(var22, var23, 18, 18, this::close);
      float var25 = this.anim() ? Anim.outCubic(Anim.since(this.pageTime, 260L)) : 1.0F;
      int var26 = this.vx + Math.round((1.0F - var25) * 8.0F * this.dir);
      float var27 = this.anim() ? Anim.outCubic(Anim.since(this.pageTime, 420L)) : 1.0F;
      int var28 = this.tw(this.title(this.page), LyricsScreen.T.TITLE);
      if (var27 < 0.999F) {
         var1.enableScissor(var26 - 2, this.colY + 8, var26 + Math.round((var28 + 6) * var27), this.colY + 30);
      }

      this.txt(this.title(this.page), var26, this.colY + 15, 15658737, var25, LyricsScreen.T.TITLE);
      if (var27 < 0.999F) {
         var1.disableScissor();
         Gfx.aaRound(var1, var26 + (var28 + 4) * var27, this.colY + 13, 1.5F, 12.0F, 0.75F, 16777215, 0.8F * (1.0F - var27));
      }

      float var29 = this.anim() ? Anim.outCubic(Anim.since(this.pageTime + 120L, 300L)) : 1.0F;
      this.txt(
         this.ell(this.subtitle(this.page), this.vw - 30, LyricsScreen.T.S),
         var26,
         this.colY + 31 + Math.round((1.0F - var29) * 5.0F),
         8618892,
         var29,
         LyricsScreen.T.S
      );
      float var30 = Math.max(0, this.contentH - this.vh + 6);
      float var31 = 36.0F;
      this.scrollTarget = Math.max(-var31, Math.min(var30 + var31, this.scrollTarget));
      if (this.scrollTarget < 0.0F) {
         this.scrollTarget = this.scrollTarget + (0.0F - this.scrollTarget) * (1.0F - (float)Math.exp(-9.0F * var2));
      }

      if (this.scrollTarget > var30) {
         this.scrollTarget = this.scrollTarget + (var30 - this.scrollTarget) * (1.0F - (float)Math.exp(-9.0F * var2));
      }

      this.scroll = this.scroll + (this.scrollTarget - this.scroll) * (1.0F - (float)Math.exp(-16.0F * var2));
      var1.enableScissor(this.vx - 4, this.vy, this.vx + this.vw + 4, this.vy + this.vh);
      this.ly = 0;
      this.rowIdx = 0;
      float var32 = Gfx.alpha;
      Gfx.alpha = var32 * Math.max(0.0F, var25);
      this.inContent = true;
      switch (this.page) {
         case SKY:
            this.sky();
            break;
         case LYRICS:
            this.lyrics();
            break;
         case STYLE:
            this.style();
            break;
         case SPOTIFY:
            this.spotify();
            break;
         case DISPLAY:
            this.display();
            break;
         case MENU:
            this.menuPage();
            break;
         case PERF:
            this.perf();
            break;
         case FAV:
            this.fav();
      }

      this.inContent = false;
      this.contentH = this.ly + 4;
      Gfx.alpha = var32;
      var1.disableScissor();
      if (this.scroll > 1.0F) {
         Gfx.vGradient(var1, this.vx - 4, this.vy, this.vw + 8, 10, 1118484, 1118484, 0.94F * var32, 0.0F);
      }

      if (this.scroll < var30 - 1.0F) {
         Gfx.vGradient(var1, this.vx - 4, this.vy + this.vh - 10, this.vw + 8, 10, 1118484, 1118484, 0.0F, 0.94F * var32);
      }

      if (var30 > 0.0F) {
         int var19 = Math.max(16, Math.round(this.vh * ((float)this.vh / this.contentH)));
         int var20 = this.vy + Math.round((this.vh - var19) * (this.scroll / var30));
         float var21 = this.pool.to("scrollact", Math.abs(this.scrollTarget - this.scroll) > 0.5F ? 1.0 : 0.0);
         Gfx.aaRound(var1, this.colX + this.colW - 7 - var21, var20, 2.0F + var21 * 1.5F, var19, 1.0F + var21, 16777215, 0.12F + 0.25F * var21);
      }
   }

   private void setSky(SkyPreset var1) {
      c().sky = var1.ordinal();
      c().lastSky = var1.ordinal();
   }

   private void sky() {
      Config var1 = c();
      this.gStart(t("ui.group.general"), null);
      this.toggleRow("skyon", t("ui.sky.on"), t("ui.sky.on.sub"), var1.sky >= 0, var1x -> {
         if (!var1x && var1.sky >= 0) {
            var1.lastSky = var1.sky;
         }

         var1.sky = var1x ? Math.max(0, var1.lastSky) : -1;
      });
      this.toggleRow("shuf", t("ui.sky.shuffle"), t("ui.sky.shuffle.sub"), var1.skyShuffle, var1x -> var1.skyShuffle = var1x);
      this.linkRow(t("ui.sky.random"), null, SkyPreset::shuffle);
      this.linkRow(t("ui.sky.studio"), t("ui.sky.studio.sub"), () -> this.client.setScreen(new SkyStudioScreen(new LyricsScreen(LyricsScreen.Page.SKY))));
      this.linkRow(t("ui.sky.preview"), t("ui.sky.preview.sub"), () -> this.client.setScreen(SkyPreviewScreen.current(new LyricsScreen(LyricsScreen.Page.SKY))));
      this.gEnd();
      this.label(t("ui.sky.style"), t("ui.sky.count", SKY_ORDER.length));
      this.skyGrid();
      SkyPreset var2 = SkyPreset.get(var1.sky >= 0 ? var1.sky : var1.lastSky);
      if (var2 != null) {
         this.note(t("skydesc." + var2.name().toLowerCase(Locale.ROOT)));
      }

      if (var2 == SkyPreset.DJ) {
         this.gStart(t("ui.dj.title"), null);
         byte var3 = 5;
         this.pickerRow(
            "djs",
            t("ui.dj.style"),
            t("ui.dj.style." + var1.djStyle + ".sub"),
            t("ui.dj.style." + var1.djStyle),
            -1,
            () -> var1.djStyle = Math.floorMod(var1.djStyle - 1, var3),
            () -> var1.djStyle = Math.floorMod(var1.djStyle + 1, var3)
         );
         this.gEnd();
         this.note(t("ui.dj.note"));
      }

      if (var2 == SkyPreset.CUSTOM && var1.sky >= 0) {
         this.gStart(t("skyed.title"), null);
         this.swatchRow("ctop", t("skyed.top"), DARK_COLS, var1.customTop, false, var1x -> {
            var1.customTop = var1x;
            SkyPreset.applyCustom();
         });
         this.swatchRow("chor", t("skyed.horizon"), DARK_COLS, var1.customHorizon, false, var1x -> {
            var1.customHorizon = var1x;
            SkyPreset.applyCustom();
         });
         this.swatchRow("ca1", t("skyed.aurora1"), BRIGHT, var1.customAurora1, false, var1x -> {
            var1.customAurora1 = var1x;
            SkyPreset.applyCustom();
         });
         this.swatchRow("ca2", t("skyed.aurora2"), BRIGHT, var1.customAurora2, false, var1x -> {
            var1.customAurora2 = var1x;
            SkyPreset.applyCustom();
         });
         this.toggleRow("cau", t("feat.aurora"), null, var1.customAurora, var1x -> {
            var1.customAurora = var1x;
            SkyPreset.applyCustom();
         });
         this.toggleRow("cst", t("feat.stars"), null, var1.customStars, var1x -> {
            var1.customStars = var1x;
            SkyPreset.applyCustom();
         });
         this.toggleRow("cmo", t("feat.moon"), null, var1.customMoon, var1x -> {
            var1.customMoon = var1x;
            SkyPreset.applyCustom();
         });
         this.toggleRow("csh", t("feat.shooting"), null, var1.customShooting, var1x -> {
            var1.customShooting = var1x;
            SkyPreset.applyCustom();
         });
         SkyPreset.Weather[] var5 = SkyPreset.Weather.values();
         SkyPreset.Weather var4 = var5[Math.floorMod(var1.customWeather, var5.length)];
         this.pickerRow(
            "cwe",
            t("skyed.weather"),
            null,
            var4 == SkyPreset.Weather.NONE ? t("skyed.noweather") : t("feat." + var4.name().toLowerCase(Locale.ROOT)),
            -1,
            () -> {
               var1.customWeather = Math.floorMod(var1.customWeather - 1, var5.length);
               SkyPreset.applyCustom();
            },
            () -> {
               var1.customWeather = Math.floorMod(var1.customWeather + 1, var5.length);
               SkyPreset.applyCustom();
            }
         );
         this.gEnd();
      }

      this.gStart(t("ui.sky.tune"), null);
      String var6 = var1.skyPalette < 0 ? t("ui.palette.original") : t("ui.palette." + WorldSky.PALETTE_KEYS[var1.skyPalette]);
      int var7 = WorldSky.PALETTES.length + 1;
      this.pickerRow(
         "pal",
         t("ui.sky.palette"),
         t("ui.sky.palette.sub"),
         var6,
         -1,
         () -> var1.skyPalette = Math.floorMod(var1.skyPalette, var7) - 1,
         () -> var1.skyPalette = Math.floorMod(var1.skyPalette + 2, var7) - 1
      );
      this.sliderRow(
         "spd",
         t("ui.sky.speed"),
         (var1.skySpeed - 0.25F) / 1.75F,
         String.format(Locale.ROOT, "%.1f×", var1.skySpeed),
         var1x -> var1.skySpeed = 0.25F + var1x * 1.75F
      );
      this.sliderRow("liv", t("ui.sky.lively"), var1.skyLively, Math.round(var1.skyLively * 100.0F) + " %", var1x -> var1.skyLively = var1x);
      this.sliderRow(
         "brt",
         t("ui.sky.breath"),
         var1.skyBreath / 2.0F,
         var1.skyBreath < 0.02F ? t("ui.off") : String.format(Locale.ROOT, "%.1f×", var1.skyBreath),
         var1x -> var1.skyBreath = var1x < 0.03F ? 0.0F : var1x * 2.0F
      );
      this.sliderRow(
         "pat",
         t("ui.sky.pattern"),
         (var1.skyPattern - 0.2F) / 1.3F,
         Math.round(var1.skyPattern * 100.0F) + " %",
         var1x -> var1.skyPattern = 0.2F + var1x * 1.3F
      );
      this.sliderRow(
         "str",
         t("opt.skystrength"),
         (var1.skyStrength - 0.1F) / 0.9F,
         Math.round(var1.skyStrength * 100.0F) + " %",
         var1x -> var1.skyStrength = 0.1F + var1x * 0.9F
      );
      this.sliderRow(
         "wea", t("opt.weatheramount"), var1.weatherAmount / 2.0F, Math.round(var1.weatherAmount * 100.0F) + " %", var1x -> var1.weatherAmount = var1x * 2.0F
      );
      this.gEnd();
      this.gStart(t("ui.sky.behaviour"), null);
      this.toggleRow("rw", t("opt.realweather"), t("ui.sky.realweather.sub"), var1.realWeather, var1x -> var1.realWeather = var1x);
      this.toggleRow("hc", t("opt.hideclouds"), t("ui.sky.hideclouds.sub"), var1.hideClouds, var1x -> var1.hideClouds = var1x);
      this.toggleRow("con", t("extras.const"), t("ui.sky.const.sub"), var1.fxConstellations, var1x -> var1.fxConstellations = var1x);
      this.toggleRow("skm", t("extras.skymusic"), t("ui.sky.music.sub"), var1.fxSkyMusic, var1x -> var1.fxSkyMusic = var1x);
      this.gEnd();
   }

   private void lyrics() {
      Config var1 = c();
      this.gStart(t("ui.group.general"), null);
      this.toggleRow("lyron", t("ui.lyrics.show"), t("ui.lyrics.show.sub"), var1.lyricsSky, var1x -> var1.lyricsSky = var1x);
      String[] var2 = Beat.owner()
         ? new String[]{t("ui.style.sky"), t("ui.style.float"), t("ui.style.book")}
         : new String[]{t("ui.style.sky"), t("ui.style.float")};
      this.segRow("kind", t("ui.lyrics.kind"), null, var2, Math.min(var1.lyricsStyle, var2.length - 1), var1x -> var1.lyricsStyle = var1x);
      this.toggleRow("fol", t("opt.lyricsfollow"), t("ui.lyrics.follow.sub"), var1.lyricsFollow, var1x -> var1.lyricsFollow = var1x);
      this.toggleRow("lock", t("ui.lyrics.lock"), t("ui.lyrics.lock.sub"), var1.lyricsViewLock, var1x -> var1.lyricsViewLock = var1x);
      this.toggleRow("und", t("style.underground"), t("ui.lyrics.under.sub"), var1.lyricsUnderground, var1x -> var1.lyricsUnderground = var1x);
      this.toggleRow("thru", t("ui.lyrics.through"), t("ui.lyrics.through.sub"), var1.lyricsThroughBlocks, var1x -> var1.lyricsThroughBlocks = var1x);
      this.gEnd();
      this.gStart(t("ui.lyrics.look"), null);
      this.swatchRow(
         "lcol",
         t("ui.lyrics.color"),
         WorldSky.LYRIC_COLORS,
         var1.lyricsColor < 0 ? -1 : WorldSky.LYRIC_COLORS[Math.floorMod(var1.lyricsColor, WorldSky.LYRIC_COLORS.length)],
         true,
         var1x -> var1.lyricsColor = indexOf(WorldSky.LYRIC_COLORS, var1x)
      );
      this.glowRow();
      this.pairRow(
         "look1",
         34,
         this.sliderCell(
            "size",
            t("opt.lyricssize"),
            (var1.lyricsSize - 0.6F) / 3.4F,
            String.format(Locale.ROOT, "%.1f×", var1.lyricsSize),
            var1x -> var1.lyricsSize = 0.6F + var1x * 3.4F
         ),
         this.sliderCell(
            "opa",
            t("opt.lyricsopacity"),
            (var1.lyricsOpacity - 0.2F) / 0.8F,
            Math.round(var1.lyricsOpacity * 100.0F) + " %",
            var1x -> var1.lyricsOpacity = 0.2F + var1x * 0.8F
         )
      );
      String[] var3 = new String[]{t("style.anim0"), t("style.anim1"), t("style.anim2")};
      this.pairRow(
         "look2",
         34,
         var1.lyricsGlow
            ? this.sliderCell(
               "gstr",
               t("style.glowstrength"),
               (var1.lyricsGlowStrength - 0.2F) / 2.3F,
               Math.round(var1.lyricsGlowStrength * 100.0F) + " %",
               var1x -> var1.lyricsGlowStrength = 0.2F + var1x * 2.3F
            )
            : null,
         this.pickerCell(
            "anim",
            t("style.anim"),
            var3[Math.floorMod(var1.lyricsAnim, 3)],
            -1,
            () -> var1.lyricsAnim = Math.floorMod(var1.lyricsAnim - 1, 3),
            () -> var1.lyricsAnim = Math.floorMod(var1.lyricsAnim + 1, 3)
         )
      );
      int var4 = Gfx.FONT_IDS.length;
      this.pickerRow("font", t("opt.lyricsfont"), null, Gfx.FONT_NAMES[var1.lyricsFont], var1.lyricsFont, () -> {
         var1.lyricsFont = Math.floorMod(var1.lyricsFont - 1, var4);
         Gfx.clearWidths();
      }, () -> {
         var1.lyricsFont = Math.floorMod(var1.lyricsFont + 1, var4);
         Gfx.clearWidths();
      });
      this.gEnd();
      this.gStart(t("ui.lyrics.timing"), null);
      this.pickerRow(
         "off",
         t("opt.lyricsoffset"),
         t("ui.lyrics.offset.sub"),
         (var1.lyricsOffsetMs > 0 ? "+" : "") + var1.lyricsOffsetMs + " ms",
         -1,
         () -> var1.lyricsOffsetMs = Math.max(-5000, var1.lyricsOffsetMs - 100),
         () -> var1.lyricsOffsetMs = Math.min(5000, var1.lyricsOffsetMs + 100)
      );
      this.toggleRow("beat", t("extras.beat"), t("ui.lyrics.beat.sub"), var1.fxBeatPulse, var1x -> var1.fxBeatPulse = var1x);
      this.toggleRow("intro", t("extras.intro"), t("ui.lyrics.intro.sub"), var1.fxSongIntro, var1x -> var1.fxSongIntro = var1x);
      this.gEnd();
      this.gStart(t("ui.lyrics.translate"), null);
      this.toggleRow("tr", t("extras.translate"), t("ui.lyrics.translate.sub"), var1.translate, var1x -> var1.translate = var1x);
      int var5 = Math.max(0, Arrays.asList(LANGS).indexOf(var1.translateTo));
      this.pickerRow(
         "lang",
         t("ui.lyrics.lang"),
         null,
         LANGS[var5].toUpperCase(Locale.ROOT),
         -1,
         () -> var1.translateTo = LANGS[Math.floorMod(var5 - 1, LANGS.length)],
         () -> var1.translateTo = LANGS[Math.floorMod(var5 + 1, LANGS.length)]
      );
      this.gEnd();
   }

   private void style() {
      Config var1 = c();
      this.label(t("ui.style.hat"), null);
      this.hatGrid();
      this.note(t("ui.style.hat.note"));
      this.gStart(t("ui.style.hatopts"), null);
      this.toggleRow("hglow", t("ui.style.hatglow"), t("ui.style.hatglow.sub"), var1.hatGlow, var1x -> var1.hatGlow = var1x);
      this.gEnd();
      this.gStart(t("ui.style.crosshair"), null);
      String[] var2 = new String[]{t("ui.cross.0"), t("ui.cross.1"), t("ui.cross.2"), t("ui.cross.3"), t("ui.cross.4")};
      this.pickerRow(
         "cross",
         t("ui.style.crosshair"),
         t("ui.style.crosshair.sub"),
         var2[Math.floorMod(var1.crosshair, var2.length)],
         -1,
         () -> var1.crosshair = Math.floorMod(var1.crosshair - 1, var2.length),
         () -> var1.crosshair = Math.floorMod(var1.crosshair + 1, var2.length)
      );
      this.gEnd();
      this.gStart(t("ui.style.effects"), null);
      String[] var3 = new String[]{t("ui.trail.0"), t("ui.trail.1"), t("ui.trail.2"), t("ui.trail.3")};
      this.pickerRow(
         "trail",
         t("ui.style.trail"),
         t("ui.style.trail.sub"),
         var3[Math.floorMod(var1.trail, var3.length)],
         -1,
         () -> var1.trail = Math.floorMod(var1.trail - 1, var3.length),
         () -> var1.trail = Math.floorMod(var1.trail + 1, var3.length)
      );
      this.toggleRow("aura", t("ui.style.aura"), t("ui.style.aura.sub"), var1.aura, var1x -> var1.aura = var1x);
      this.toggleRow("bglow", t("ui.style.beatglow"), t("ui.style.beatglow.sub"), var1.beatGlow, var1x -> var1.beatGlow = var1x);
      this.gEnd();
      this.gStart(t("ui.style.death"), null);
      String[] var4 = new String[8];

      for (int var5 = 0; var5 < var4.length; var5++) {
         var4[var5] = t("ui.death." + var5);
      }

      this.pickerRow(
         "death",
         t("ui.style.death"),
         t("ui.style.death.sub"),
         var4[Math.floorMod(var1.deathFx, var4.length)],
         -1,
         () -> var1.deathFx = Math.floorMod(var1.deathFx - 1, var4.length),
         () -> var1.deathFx = Math.floorMod(var1.deathFx + 1, var4.length)
      );
      if (var1.deathFx > 0) {
         this.toggleRow("dothers", t("ui.style.deathothers"), t("ui.style.deathothers.sub"), var1.deathOthers, var1x -> var1.deathOthers = var1x);
         this.linkRow(t("ui.style.deathtest"), t("ui.style.deathtest.sub"), () -> {
            this.close();
            DeathFx.test();
         });
      }

      this.gEnd();
   }

   private void hatGrid() {
      Config var1 = c();
      String[] var2 = Accessories.HATS;
      byte var3 = 3;
      byte var4 = 8;
      int var5 = (this.vw - var4 * (var3 - 1)) / var3;
      int var6 = Math.round(var5 * 0.625F);
      int var7 = (var2.length + var3 - 1) / var3;

      for (int var8 = 0; var8 < var7; var8++) {
         int var9 = this.rowStart(var6 + 24);
         if (var9 != Integer.MIN_VALUE) {
            for (int var10 = 0; var10 < var3; var10++) {
               int var11 = var8 * var3 + var10;
               if (var11 >= var2.length) {
                  break;
               }

               int var12 = this.vx + var10 * (var5 + var4);
               int var13 = var9 + 2;
               boolean var14 = var1.hat == var11;
               float var15 = this.hover(var12, var13, var5, var6 + 16, "hat" + var11);
               float var16 = this.pool.to("hatsel" + var11, var14 ? 1.0 : 0.0);
               float var17 = 1.0F - Anim.outCubic(Anim.since(this.pressed.getOrDefault("hat" + var11, 0L), 300L));
               float var18 = this.spring("hatsc" + var11, 1.0F + 0.05F * var15) - 0.05F * var17;
               this.gfx.getMatrices().pushMatrix();
               this.gfx.getMatrices().translate(var12 + var5 / 2.0F, var13 + var6 / 2.0F);
               this.gfx.getMatrices().scale(var18, var18);
               this.gfx.getMatrices().translate(-(var12 + var5 / 2.0F), -(var13 + var6 / 2.0F));
               if (var16 > 0.01F) {
                  float var19 = 2.0F + 2.0F * (1.0F - Anim.outBack(var16));
                  Gfx.aaRound(this.gfx, var12 - var19, var13 - var19, var5 + var19 * 2.0F, var6 + var19 * 2.0F, 9.0F + var19, 16777215, var16);
               }

               Gfx.aaRound(this.gfx, var12, var13, var5, var6, 9.0F, Gfx.lerpColor(1710623, 2236968, var15), 1.0F);
               if (var11 == 0) {
                  Icons.draw(
                     this.gfx,
                     Icons.Icon.CLOSE,
                     var12 + var5 / 2.0F,
                     var13 + var6 / 2.0F,
                     10.0F,
                     Gfx.lerpColor(5000276, 15658737, Math.max(var15, var16)),
                     1.0F
                  );
               } else {
                  float var23 = (float)Math.sin(System.currentTimeMillis() / 400.0 + var11) * 1.5F * Math.max(var15, var16);
                  Identifier var20 = Identifier.of("neonlyrics", "textures/gui/hats/" + var2[var11] + ".png");

                  try {
                     this.gfx
                        .drawTexture(
                           RenderPipelines.GUI_TEXTURED,
                           var20,
                           var12 + 2,
                           Math.round(var13 + 2 + var23),
                           0.0F,
                           0.0F,
                           var5 - 4,
                           var6 - 4,
                           var5 - 4,
                           var6 - 4,
                           Gfx.argb(16777215, 1.0F)
                        );
                  } catch (Throwable var22) {
                  }
               }

               this.gfx.getMatrices().popMatrix();
               this.txt(
                  this.ell(t("ui.hat." + var2[var11]), var5, LyricsScreen.T.S),
                  var12,
                  var13 + var6 + 6,
                  Gfx.lerpColor(Gfx.lerpColor(8618892, 13158606, var15), 15658737, var16),
                  1.0F,
                  var14 ? LyricsScreen.T.B : LyricsScreen.T.S
               );
               int var24 = var11;
               this.hit(var12, var13, var5, var6 + 16, () -> {
                  this.pressed.put("hat" + var24, System.currentTimeMillis());
                  var1.hat = var24;
                  Fx.sound(Fx.Sfx.CLICK);
                  NeonLyrics.saveConfig();
               });
            }
         }

         this.rowEnd();
      }
   }

   private void spotify() {
      Config var1 = c();
      this.gStart(t("ui.group.status"), null);
      this.statusRow();
      this.gEnd();
      this.gStart(t("ui.spotify.connection"), null);
      boolean var2 = SpotifyClient.autoMode();
      this.segRow("mode", t("ui.spotify.mode"), null, new String[]{t("spotify.mode.auto"), t("spotify.mode.api")}, var2 ? 0 : 1, var1x -> {
         SpotifyClient.setMode(var1x);
         this.idBox.setText(c().spotifyClientId);
      });
      if (var2) {
         this.linkRow(t("spotify.auto.retry"), null, () -> {
            LocalMedia.restart();
            Island.notify(t("spotify.auto.retrying"), true);
         });
      } else {
         this.fieldRow(t("spotify.clientid"), t("ui.spotify.clientid.sub"));
         boolean var3 = SpotifyClient.status() == SpotifyClient.Status.CONNECTED;
         this.linkRow(var3 ? t("spotify.disconnect") : t("spotify.connect"), null, () -> {
            var1.spotifyClientId = this.idBox.getText().trim();
            NeonLyrics.saveConfig();
            if (SpotifyClient.status() == SpotifyClient.Status.CONNECTED) {
               SpotifyClient.logout();
            } else {
               SpotifyClient.login();
            }
         });
         this.linkRow(t("tut.open"), null, this::openTutorial);
         this.copyRow(t("ui.spotify.redirect"), "http://127.0.0.1:8789/callback");
         this.linkRow(t("spotify.dashboard"), null, () -> Util.getOperatingSystem().open("https://developer.spotify.com/dashboard"));
      }

      this.gEnd();
      if (var2) {
         this.note(t("spotify.auto.p1"));
         this.note(t("spotify.auto.p2"));
         this.note(t("spotify.auto.p4"));
      }

      this.gStart(t("ui.spotify.playback"), null);
      this.toggleRow("demo", t("ui.spotify.demo"), t("ui.spotify.demo.sub"), var1.demoLyrics, var1x -> {
         var1.demoLyrics = var1x;
         SpotifyClient.restartDemo();
      });
      this.linkRow(t("ui.spotify.sync"), null, SpotifyClient::syncHere);
      this.gEnd();
   }

   private void display() {
      Config var1 = c();
      this.gStart(t("ui.display.bar"), null);
      this.toggleRow("isl", t("ui.display.island"), t("ui.display.island.sub"), var1.islandEnabled, var1x -> var1.islandEnabled = var1x);
      this.toggleRow("lisl", t("opt.lyricsisland"), t("ui.display.lyricsisland.sub"), var1.lyricsIsland, var1x -> var1.lyricsIsland = var1x);
      this.toggleRow("cov", t("extras.cover"), t("ui.display.cover.sub"), var1.albumCover, var1x -> var1.albumCover = var1x);
      this.toggleRow("clk", t("opt.clock"), null, var1.showClock, var1x -> var1.showClock = var1x);
      this.toggleRow("png", t("opt.ping"), null, var1.showPing, var1x -> var1.showPing = var1x);
      this.toggleRow("fps", t("opt.fps"), null, var1.showFps, var1x -> var1.showFps = var1x);
      int var2 = Gfx.FONT_NAMES.length;
      this.pickerRow(
         "ufont",
         t("ui.display.barfont"),
         t("ui.display.barfont.sub"),
         Gfx.FONT_NAMES[var1.uiFont],
         var1.uiFont < Gfx.FONT_IDS.length ? var1.uiFont : -1,
         () -> {
            var1.uiFont = Math.floorMod(var1.uiFont - 1, var2);
            Gfx.clearWidths();
         },
         () -> {
            var1.uiFont = Math.floorMod(var1.uiFont + 1, var2);
            Gfx.clearWidths();
         }
      );
      this.gEnd();
      this.gStart(t("ui.display.hud"), null);
      this.toggleRow("hot", t("opt.glasshotbar"), t("ui.display.hotbar.sub"), var1.glassHotbar, var1x -> var1.glassHotbar = var1x);
      this.toggleRow("intro", t("extras.startup"), null, var1.fxStartup, var1x -> var1.fxStartup = var1x);
      this.gEnd();
   }

   private void menuPage() {
      Config var1 = c();
      this.gStart(t("ui.menu.mode"), null);
      this.segRow(
         "mmode",
         t("ui.menu.open"),
         t(var1.menuMode == 1 ? "ui.menu.world.sub" : "ui.menu.normal.sub"),
         new String[]{t("ui.menu.normal"), t("ui.menu.world")},
         var1.menuMode,
         var1x -> var1.menuMode = var1x
      );
      this.linkRow(t("ui.menu.reset"), t("ui.menu.reset.sub"), () -> {
         var1.menuMode = 0;
         var1.accent = 0;
         var1.blur = true;
         var1.fxMenuAnim = true;
         var1.sounds = true;
         PhoneMenu.closeNow();
         NeonLyrics.saveConfig();
      });
      this.gEnd();
      this.gStart(t("ui.display.menu"), null);
      this.accentRow();
      this.toggleRow("blur", t("opt.blur"), null, var1.blur, var1x -> var1.blur = var1x);
      this.toggleRow("anim", t("perf.menuanim"), null, var1.fxMenuAnim, var1x -> var1.fxMenuAnim = var1x);
      this.toggleRow("snd", t("opt.sounds"), null, var1.sounds, var1x -> var1.sounds = var1x);
      this.gEnd();
   }

   private void perf() {
      Config var1 = c();
      this.gStart(t("ui.group.now"), null);
      this.fpsRow();
      this.gEnd();
      this.gStart(t("ui.group.auto"), null);
      this.toggleRow("auto", t("perf.auto"), t("ui.perf.auto.sub"), var1.autoPerf, var1x -> {
         var1.autoPerf = var1x;
         AutoPerf.reset();
      });
      int var2 = 0;

      for (int var3 = 0; var3 < TARGETS.length; var3++) {
         if (TARGETS[var3] == var1.targetFps) {
            var2 = var3;
         }
      }

      int var4 = var2;
      this.pickerRow(
         "target",
         t("perf.target"),
         t("ui.perf.target.sub"),
         var1.targetFps <= 0 ? t("ui.perf.target.auto", AutoPerf.target()) : var1.targetFps + " FPS",
         -1,
         () -> {
            var1.targetFps = TARGETS[Math.floorMod(var4 - 1, TARGETS.length)];
            AutoPerf.reset();
         },
         () -> {
            var1.targetFps = TARGETS[Math.floorMod(var4 + 1, TARGETS.length)];
            AutoPerf.reset();
         }
      );
      this.segRow("qual", t("quality"), null, new String[]{t("quality.0"), t("quality.1"), t("quality.2")}, var1.quality, var1x -> var1.quality = var1x);
      this.segRow("prof", t("ui.perf.profile"), null, new String[]{"Chill", "PvP"}, var1.profile, Profiles::set);
      this.toggleRow("boost", t("opt.fpsboost"), t("ui.perf.boost.sub"), var1.fpsBoost, FpsBoost::apply);
      this.gEnd();
      this.gStart(t("ui.perf.effects"), null);
      this.toggleRow("e_col", t("perf.skycolor"), null, var1.fxSkyColor, var1x -> var1.fxSkyColor = var1x);
      this.toggleRow("e_wea", t("perf.weather"), null, var1.fxWeather, var1x -> var1.fxWeather = var1x);
      this.toggleRow("e_aur", t("perf.aurora"), null, var1.fxAurora, var1x -> var1.fxAurora = var1x);
      this.toggleRow("e_sta", t("perf.stars"), null, var1.fxStars, var1x -> var1.fxStars = var1x);
      this.toggleRow("e_glo", t("style.glow"), t("ui.perf.glow.sub"), var1.lyricsGlow, var1x -> var1.lyricsGlow = var1x);
      this.linkRow(t("perf.alloff"), null, () -> this.setAllEffects(false));
      this.linkRow(t("perf.allon"), null, () -> this.setAllEffects(true));
      this.gEnd();
   }

   private void fav() {
      List var1 = Favorites.all();
      if (var1.isEmpty()) {
         this.label(t("ui.fav.lines"), null);
         this.note(t("fav.empty"));
      } else {
         this.gStart(t("ui.fav.lines"), Integer.toString(var1.size()));

         for (int var2 = 0; var2 < var1.size(); var2++) {
            Favorites.Entry var3 = (Favorites.Entry)var1.get(var2);
            int var4 = this.rowStart(38);
            if (var4 != Integer.MIN_VALUE) {
               int var5 = this.vx + 12;
               int var6 = this.vw - 24;
               this.rowBg(var4, 38, "fav" + var2);
               this.txt(this.ell("„" + var3.line + "“", var6 - 26, LyricsScreen.T.B), var5, var4 + 9, 15658737, 1.0F, LyricsScreen.T.B);
               this.txt(
                  this.ell(var3.title + (var3.artist.isBlank() ? "" : " · " + var3.artist), var6 - 26, LyricsScreen.T.S),
                  var5,
                  var4 + 21,
                  8618892,
                  1.0F,
                  LyricsScreen.T.S
               );
               this.iconBtn(this.gfx, "favdel" + var2, var5 + var6 - 16, var4 + 10, 18, Icons.Icon.CLOSE, 6.0F, () -> Favorites.remove(var3));
            }

            this.rowEnd();
         }

         this.gEnd();
      }

      List var11 = SpotifyClient.history();
      if (var11.isEmpty()) {
         this.label(t("ui.fav.history"), null);
         this.note(t("ui.fav.history.empty"));
      } else {
         this.gStart(t("ui.fav.history"), Integer.toString(var11.size()));
         long var12 = System.currentTimeMillis();

         for (int var13 = 0; var13 < var11.size(); var13++) {
            SpotifyClient.Played var14 = (SpotifyClient.Played)var11.get(var13);
            int var7 = this.rowStart(38);
            if (var7 != Integer.MIN_VALUE) {
               int var8 = this.vx + 12;
               int var9 = this.vw - 24;
               this.rowBg(var7, 38, "his" + var13);
               String var10 = ago(var12 - var14.at());
               this.txt(
                  this.ell(var14.title(), var9 - 14 - this.tw(var10, LyricsScreen.T.S), LyricsScreen.T.B), var8, var7 + 9, 15658737, 1.0F, LyricsScreen.T.B
               );
               this.txt(this.ell(var14.artist(), var9, LyricsScreen.T.S), var8, var7 + 21, 8618892, 1.0F, LyricsScreen.T.S);
               this.txtR(var10, var8 + var9, var7 + 10, 8618892, 1.0F, LyricsScreen.T.S);
            }

            this.rowEnd();
         }

         this.gEnd();
      }
   }

   private int rowStart(int var1) {
      int var2 = this.vy + this.ly - Math.round(this.scroll);
      int var3 = this.rowIdx++;
      this.ly += var1;
      this.rowA = Gfx.alpha;
      if (var2 + var1 < this.vy - 2 || var2 > this.vy + this.vh + 2) {
         return Integer.MIN_VALUE;
      }

      if (!this.anim()) {
         return var2;
      }

      long var4 = System.currentTimeMillis();
      long var6 = this.seen.computeIfAbsent(var3, var4x -> Math.max(var4, this.pageTime + Math.min(var3, 14) * 35L));
      float var8 = Anim.outCubic(Anim.since(var6, 340L));
      float var9 = Anim.outBack(Anim.since(var6, 420L));
      Gfx.alpha = this.rowA * var8;
      return var2 + Math.round((1.0F - var9) * 14.0F);
   }

   private void rowEnd() {
      Gfx.alpha = this.rowA;
   }

   private void gStart(String var1, String var2) {
      this.label(var1, var2);
      int var3 = this.rowStart(5);
      if (var3 != Integer.MIN_VALUE) {
         Gfx.aaRound(this.gfx, this.vx, var3, this.vw, 16.0F, 9.0F, 1710623, 1.0F);
      }

      this.rowEnd();
      this.firstInGroup = true;
   }

   private void gEnd() {
      int var1 = this.rowStart(13);
      if (var1 != Integer.MIN_VALUE) {
         Gfx.aaRound(this.gfx, this.vx, var1 - 11, this.vw, 16.0F, 9.0F, 1710623, 1.0F);
      }

      this.rowEnd();
      this.firstInGroup = false;
   }

   private void label(String var1, String var2) {
      int var3 = this.rowStart(20);
      if (var3 != Integer.MIN_VALUE) {
         this.txt(var1.toUpperCase(Locale.ROOT), this.vx + 3, var3 + 8, 8618892, 1.0F, LyricsScreen.T.S);
         if (var2 != null) {
            this.txtR(var2, this.vx + this.vw - 3, var3 + 8, 8618892, 1.0F, LyricsScreen.T.S);
         }
      }

      this.rowEnd();
   }

   private void rowBg(int var1, int var2, String var3) {
      float var4 = this.hover(this.vx, var1, this.vw, var2, var3);
      Gfx.rect(this.gfx, this.vx, var1, this.vw, var2, Gfx.lerpColor(1710623, 2236968, var4), 1.0F);
      if (var4 > 0.01F) {
         Gfx.aaRound(this.gfx, this.vx + 3, var1 + var2 / 2.0F - (var2 - 14) * var4 / 2.0F, 2.0F, (var2 - 14) * var4, 1.0F, 16777215, 0.5F * var4);
      }

      if (!this.firstInGroup) {
         Gfx.rect(this.gfx, this.vx + 12, var1, this.vw - 24, 1, 2500140, 1.0F);
      }

      this.firstInGroup = false;
      this.ripple(var3, this.vx, var1, this.vw, var2);
      this.curRid = var3;
   }

   private void labels(int var1, int var2, String var3, String var4, int var5) {
      int var6 = this.vx + 12;
      if (var4 == null) {
         this.txt(this.ell(var3, var5, LyricsScreen.T.B), var6, var1 + var2 / 2 - 4, 15658737, 1.0F, LyricsScreen.T.B);
      } else {
         this.txt(this.ell(var3, var5, LyricsScreen.T.B), var6, var1 + var2 / 2 - 9, 15658737, 1.0F, LyricsScreen.T.B);
         this.txt(this.ell(var4, var5, LyricsScreen.T.S), var6, var1 + var2 / 2 + 3, 8618892, 1.0F, LyricsScreen.T.S);
      }
   }

   private int rowH(String var1) {
      return var1 == null ? 30 : 38;
   }

   private void note(String var1) {
      List var2 = this.wrap(var1, this.vw - 6, LyricsScreen.T.S);
      int var3 = this.rowStart(6 + var2.size() * 9);
      if (var3 != Integer.MIN_VALUE) {
         for (int var4 = 0; var4 < var2.size(); var4++) {
            this.txt((String)var2.get(var4), this.vx + 3, var3 + 3 + var4 * 9, 8618892, 1.0F, LyricsScreen.T.S);
         }
      }

      this.rowEnd();
   }

   private void toggleRow(String var1, String var2, String var3, boolean var4, Consumer<Boolean> var5) {
      int var6 = this.rowH(var3);
      int var7 = this.rowStart(var6);
      if (var7 != Integer.MIN_VALUE) {
         this.rowBg(var7, var6, "tr" + var1);
         this.labels(var7, var6, var2, var3, this.vw - 70);
         byte var8 = 24;
         byte var9 = 14;
         int var10 = this.vx + this.vw - 12 - var8;
         int var11 = var7 + var6 / 2 - var9 / 2;
         float var12 = this.spring("tg" + var1, var4 ? 1.0F : 0.0F);
         float var13 = Anim.clamp(var12);
         float var14 = 1.0F - Anim.outCubic(Anim.since(this.pressed.getOrDefault("tg" + var1, 0L), 450L));
         if (var14 > 0.01F) {
            Gfx.aaRound(
               this.gfx,
               var10 - 4.0F * (1.0F - var14) - 1.0F,
               var11 - 4.0F * (1.0F - var14) - 1.0F,
               var8 + 8.0F * (1.0F - var14) + 2.0F,
               var9 + 8.0F * (1.0F - var14) + 2.0F,
               var9 / 2.0F + 4.0F,
               16777215,
               0.25F * var14
            );
         }

         Gfx.aaRound(this.gfx, var10, var11, var8, var9, var9 / 2.0F, Gfx.lerpColor(3421243, 16777215, var13), 1.0F);
         float var15 = Math.min(5.0F, Math.abs(this.springs.get("tg" + var1)[1]) * 0.35F);
         Gfx.aaRound(
            this.gfx, var10 + 2 + var12 * 10.0F - (var4 ? var15 : 0.0F), var11 + 2, 10.0F + var15, 10.0F, 5.0F, Gfx.lerpColor(9079442, 1118484, var13), 1.0F
         );
         this.hit(this.vx, var7, this.vw, var6, () -> {
            this.pressed.put("tg" + var1, System.currentTimeMillis());
            var5.accept(!var4);
            Fx.sound(Fx.Sfx.CLICK);
            NeonLyrics.saveConfig();
         }, "tr" + var1);
      }

      this.rowEnd();
   }

   private void sliderRow(String var1, String var2, float var3, String var4, Consumer<Float> var5) {
      byte var6 = 42;
      int var7 = this.rowStart(var6);
      if (var7 != Integer.MIN_VALUE) {
         this.rowBg(var7, var6, "sr" + var1);
         int var8 = this.vx + 12;
         int var9 = this.vw - 24;
         this.txt(this.ell(var2, var9 - 50, LyricsScreen.T.B), var8, var7 + 9, 15658737, 1.0F, LyricsScreen.T.B);
         var3 = Anim.clamp(var3);
         boolean var10 = this.activeSlider != null && this.activeSlider.id().equals(var1);
         float var11 = this.pool.to("sd" + var1, var10 ? 1.0 : 0.0);
         this.txtR(
            var4, var8 + var9, var7 + 9 - Math.round(var11 * 1.5F), Gfx.lerpColor(12105920, 16777215, var11), 1.0F, var10 ? LyricsScreen.T.B : LyricsScreen.T.S
         );
         float var12 = var10 ? var3 : this.spring("sv" + var1, var3);
         int var13 = var7 + 29;
         float var14 = this.pool.to("sh" + var1, !var10 && !this.in(var8 - 6, var13 - 8, var9 + 12, 16) ? 0.0 : 1.0);
         Gfx.aaRound(this.gfx, var8, var13 - 1.5F, var9, 3.0F, 1.5F, 3421243, 1.0F);
         Gfx.aaRound(this.gfx, var8, var13 - 1.5F, Math.max(3.0F, var9 * Anim.clamp(var12)), 3.0F, 1.5F, 16777215, 1.0F);
         if (var14 > 0.01F) {
            Gfx.aaCircle(this.gfx, var8 + var9 * Anim.clamp(var12), var13, 8.0F + 3.0F * var11, 16777215, 0.12F * var14);
         }

         Gfx.aaCircle(this.gfx, var8 + var9 * Anim.clamp(var12), var13, 4.5F + 0.8F * var14 + 1.5F * var11, 16777215, 1.0F);
         if (this.closeTime < 0L) {
            int var15 = Math.max(var13 - 9, this.vy);
            int var16 = Math.min(var13 + 9, this.vy + this.vh);
            this.sliders.add(new LyricsScreen.Slider(var1, var8, var15, var9, var16 - var15, var5));
         }
      }

      this.rowEnd();
   }

   private void pickerRow(String var1, String var2, String var3, String var4, int var5, Runnable var6, Runnable var7) {
      int var8 = this.rowH(var3);
      int var9 = this.rowStart(var8);
      if (var9 != Integer.MIN_VALUE) {
         this.rowBg(var9, var8, "pr" + var1);
         int var10 = Math.min(150, this.vw / 2);
         int var11 = this.vx + this.vw - 12 - var10;
         this.labels(var9, var8, var2, var3, var11 - this.vx - 20);
         int var12 = var9 + var8 / 2;
         boolean var13 = this.in(var11, var9, var10 / 2, var8);
         boolean var14 = this.in(var11 + var10 / 2, var9, var10 - var10 / 2, var8);
         float var15 = 1.0F - Anim.outCubic(Anim.since(this.pressed.getOrDefault("pk" + var1, 0L), 240L));
         int var16 = this.vx + this.vw - 24;
         int var17;
         if (var5 >= 0) {
            var17 = Math.min(var10 - 30, Gfx.wIn(var4, var5, false));
            Gfx.textIn(this.gfx, var4, var16 - var17, var12 - 4 - Math.round(var15 * 1.5F), 13684950, 1.0F, var5, false);
         } else {
            String var18 = this.ell(var4, var10 - 30, LyricsScreen.T.M);
            var17 = this.tw(var18, LyricsScreen.T.M);
            this.txtR(var18, var16, var12 - 4 - var15 * 1.5F, 13684950, 1.0F, LyricsScreen.T.M);
         }

         float var20 = this.pool.to("hl" + var1, var13 ? 1.0 : 0.0);
         float var19 = this.pool.to("hr" + var1, var14 ? 1.0 : 0.0);
         Icons.draw(this.gfx, Icons.Icon.BACK, var16 - var17 - 10 - var20 * 2.0F, var12, 6.0F + var20, Gfx.lerpColor(5000276, 15658737, var20), 1.0F);
         Icons.draw(this.gfx, Icons.Icon.CHEVRON, this.vx + this.vw - 15 + var19 * 2.0F, var12, 6.0F + var19, Gfx.lerpColor(5000276, 15658737, var19), 1.0F);
         this.hit(var11, var9, var10 / 2, var8, () -> {
            this.pressed.put("pk" + var1, System.currentTimeMillis());
            var7.run();
            Fx.sound(Fx.Sfx.CLICK);
            NeonLyrics.saveConfig();
         }, "pr" + var1);
         this.hit(var11 + var10 / 2, var9, var10 - var10 / 2, var8, () -> {
            this.pressed.put("pk" + var1, System.currentTimeMillis());
            var7.run();
            Fx.sound(Fx.Sfx.CLICK);
            NeonLyrics.saveConfig();
         }, "pr" + var1);
      }

      this.rowEnd();
   }

   private void segRow(String var1, String var2, String var3, String[] var4, int var5, IntConsumer var6) {
      int var7 = this.rowH(var3);
      int var8 = this.rowStart(var7);
      if (var8 != Integer.MIN_VALUE) {
         this.rowBg(var8, var7, "sg" + var1);
         int var9 = var4.length;
         int var10 = 0;

         for (String var14 : var4) {
            var10 = Math.max(var10, Math.max(this.tw(var14, LyricsScreen.T.S), this.tw(var14, LyricsScreen.T.B)) + 18);
         }

         int var19 = var10 * var9 + 4;
         byte var20 = 18;
         int var21 = this.vx + this.vw - 12 - var19;
         int var22 = var8 + var7 / 2 - var20 / 2;
         this.labels(var8, var7, var2, var3, var21 - this.vx - 20);
         Gfx.aaRound(this.gfx, var21, var22, var19, var20, 9.0F, 921105, 1.0F);
         float var15 = this.spring("seg" + var1, var5);
         Gfx.aaRound(this.gfx, var21 + 2 + var15 * var10, var22 + 2, var10, var20 - 4, 7.0F, 16777215, 1.0F);

         for (int var16 = 0; var16 < var9; var16++) {
            float var17 = Anim.clamp(1.0F - Math.abs(var15 - var16));
            this.txtC(
               var4[var16],
               var21 + 2 + var16 * var10 + var10 / 2.0F,
               var22 + 5,
               Gfx.lerpColor(8618892, 1118484, var17),
               1.0F,
               var17 > 0.5F ? LyricsScreen.T.B : LyricsScreen.T.S
            );
            int var18 = var16;
            this.hit(var21 + 2 + var16 * var10, var22, var10, var20, () -> {
               if (var18 != var5) {
                  var6.accept(var18);
                  Fx.sound(Fx.Sfx.CLICK);
                  NeonLyrics.saveConfig();
               }
            });
         }
      }

      this.rowEnd();
   }

   private void swatchRow(String var1, String var2, int[] var3, int var4, boolean var5, IntConsumer var6) {
      byte var7 = 30;
      int var8 = this.rowStart(var7);
      if (var8 != Integer.MIN_VALUE) {
         this.rowBg(var8, var7, "sw" + var1);
         int var9 = var8 + var7 / 2;
         int var10 = var3.length + (var5 ? 1 : 0);
         byte var11 = 14;
         int var12 = this.vx + this.vw - 17 - (var10 - 1) * var11;
         this.labels(var8, var7, var2, null, var12 - this.vx - 24);

         for (int var13 = 0; var13 < var10; var13++) {
            boolean var14 = var5 && var13 == 0;
            int var15 = var14 ? 0 : var3[var13 - (var5 ? 1 : 0)];
            boolean var16 = var14 ? var4 < 0 : var4 == var15;
            float var17 = var12 + var13 * var11;
            float var18 = this.pool.to(var1 + "s" + var13, var16 ? 1.0 : 0.0);
            if (var18 > 0.01F) {
               Gfx.aaCircle(this.gfx, var17, var9, 6.5F, 16777215, var18);
               Gfx.aaCircle(this.gfx, var17, var9, 5.2F, 1710623, 1.0F);
            }

            if (var14) {
               Gfx.aaCircle(this.gfx, var17, var9, 4.2F, 3421243, 1.0F);
               this.txtC("A", var17, var9 - 3, 8618892, 1.0F, LyricsScreen.T.S);
            } else {
               Gfx.aaCircle(this.gfx, var17, var9, 4.2F, var15, 1.0F);
            }

            int var19 = var14 ? -1 : var15;
            this.hit(Math.round(var17 - 7.0F), var8, 14, var7, () -> {
               var6.accept(var19);
               Fx.sound(Fx.Sfx.CLICK);
               NeonLyrics.saveConfig();
            });
         }
      }

      this.rowEnd();
   }

   private void glowRow() {
      Config var1 = c();
      byte var2 = 30;
      int var3 = this.rowStart(var2);
      if (var3 != Integer.MIN_VALUE) {
         this.rowBg(var3, var2, "glowrow");
         int var4 = var3 + var2 / 2;
         int[] var5 = WorldSky.LYRIC_COLORS;
         int var6 = var1.lyricsGlowColor < 0 ? -1 : var5[Math.floorMod(var1.lyricsGlowColor, var5.length)];
         byte var7 = 24;
         byte var8 = 14;
         int var9 = this.vx + this.vw - 12 - var7;
         int var10 = var4 - var8 / 2;
         int var11 = var5.length + 1;
         byte var12 = 14;
         int var13 = var9 - 14 - (var11 - 1) * var12;
         this.labels(var3, var2, t("style.glow"), null, var13 - this.vx - 24);
         float var14 = this.pool.to("glowon", var1.lyricsGlow ? 1.0 : 0.0);
         float var15 = Gfx.alpha;
         Gfx.alpha = var15 * (0.35F + 0.65F * var14);

         for (int var16 = 0; var16 < var11; var16++) {
            boolean var17 = var16 == 0;
            int var18 = var17 ? 0 : var5[var16 - 1];
            boolean var19 = var17 ? var6 < 0 : var6 == var18;
            float var20 = var13 + var16 * var12;
            float var21 = this.pool.to("gcs" + var16, var19 ? 1.0 : 0.0);
            if (var21 > 0.01F) {
               Gfx.aaCircle(this.gfx, var20, var4, 6.5F, 16777215, var21);
               Gfx.aaCircle(this.gfx, var20, var4, 5.2F, 1710623, 1.0F);
            }

            if (var17) {
               Gfx.aaCircle(this.gfx, var20, var4, 4.2F, 3421243, 1.0F);
               this.txtC("A", var20, var4 - 3, 8618892, 1.0F, LyricsScreen.T.S);
            } else {
               Gfx.aaCircle(this.gfx, var20, var4, 4.2F, var18, 1.0F);
            }

            int var22 = var17 ? -1 : var18;
            this.hit(Math.round(var20 - 7.0F), var3, 14, var2, () -> {
               var1.lyricsGlowColor = indexOf(var5, var22);
               var1.lyricsGlow = true;
               Fx.sound(Fx.Sfx.CLICK);
               NeonLyrics.saveConfig();
            });
         }

         Gfx.alpha = var15;
         float var23 = this.spring("tgglow", var1.lyricsGlow ? 1.0F : 0.0F);
         float var24 = Anim.clamp(var23);
         Gfx.aaRound(this.gfx, var9, var10, var7, var8, var8 / 2.0F, Gfx.lerpColor(3421243, 16777215, var24), 1.0F);
         Gfx.aaRound(this.gfx, var9 + 2 + var23 * 10.0F, var10 + 2, 10.0F, 10.0F, 5.0F, Gfx.lerpColor(9079442, 1118484, var24), 1.0F);
         this.hit(var9 - 6, var3, var7 + 18, var2, () -> {
            var1.lyricsGlow = !var1.lyricsGlow;
            Fx.sound(Fx.Sfx.CLICK);
            NeonLyrics.saveConfig();
         });
      }

      this.rowEnd();
   }

   private void pairRow(String var1, int var2, LyricsScreen.Cell var3, LyricsScreen.Cell var4) {
      int var5 = this.rowStart(var2);
      if (var5 != Integer.MIN_VALUE) {
         this.rowBg(var5, var2, "pair" + var1);
         int var6 = this.vw / 2;
         Gfx.rect(this.gfx, this.vx + var6, var5 + 7, 1, var2 - 14, 2500140, 1.0F);
         if (var3 != null) {
            var3.draw(this.vx, var5, var6, var2);
         }

         if (var4 != null) {
            var4.draw(this.vx + var6, var5, this.vw - var6, var2);
         }
      }

      this.rowEnd();
   }

   private LyricsScreen.Cell sliderCell(String var1, String var2, float var3, String var4, Consumer<Float> var5) {
      return (var6, var7, var8, var9) -> {
         int var10 = var6 + 12;
         int var11 = var8 - 24;
         float var12 = Anim.clamp(var3);
         boolean var13 = this.activeSlider != null && this.activeSlider.id().equals(var1);
         float var14 = this.pool.to("sd" + var1, var13 ? 1.0 : 0.0);
         this.txt(this.ell(var2, var11 - this.tw(var4, LyricsScreen.T.S) - 6, LyricsScreen.T.S), var10, var7 + 7, 15658737, 1.0F, LyricsScreen.T.S);
         this.txtR(var4, var10 + var11, var7 + 7, Gfx.lerpColor(12105920, 16777215, var14), 1.0F, var13 ? LyricsScreen.T.B : LyricsScreen.T.S);
         float var15 = var13 ? var12 : this.spring("sv" + var1, var12);
         int var16 = var7 + var9 - 10;
         float var17 = this.pool.to("sh" + var1, !var13 && !this.in(var10 - 6, var16 - 8, var11 + 12, 16) ? 0.0 : 1.0);
         Gfx.aaRound(this.gfx, var10, var16 - 1.5F, var11, 3.0F, 1.5F, 3421243, 1.0F);
         Gfx.aaRound(this.gfx, var10, var16 - 1.5F, Math.max(3.0F, var11 * Anim.clamp(var15)), 3.0F, 1.5F, 16777215, 1.0F);
         if (var17 > 0.01F) {
            Gfx.aaCircle(this.gfx, var10 + var11 * Anim.clamp(var15), var16, 7.0F + 2.0F * var14, 16777215, 0.12F * var17);
         }

         Gfx.aaCircle(this.gfx, var10 + var11 * Anim.clamp(var15), var16, 4.0F + 0.7F * var17 + 1.2F * var14, 16777215, 1.0F);
         if (this.closeTime < 0L) {
            int var18 = Math.max(var16 - 9, this.vy);
            int var19 = Math.min(var16 + 9, this.vy + this.vh);
            this.sliders.add(new LyricsScreen.Slider(var1, var10, var18, var11, var19 - var18, var5));
         }
      };
   }

   private LyricsScreen.Cell pickerCell(String var1, String var2, String var3, int var4, Runnable var5, Runnable var6) {
      return (var6x, var7, var8, var9) -> {
         this.txt(this.ell(var2, var8 - 24, LyricsScreen.T.S), var6x + 12, var7 + 7, 15658737, 1.0F, LyricsScreen.T.S);
         int var10 = var7 + var9 - 10;
         float var11 = 1.0F - Anim.outCubic(Anim.since(this.pressed.getOrDefault("pk" + var1, 0L), 240L));
         boolean var12 = this.in(var6x, var7, var8 / 2, var9);
         boolean var13 = this.in(var6x + var8 / 2, var7, var8 - var8 / 2, var9);
         float var14 = this.pool.to("hl" + var1, var12 ? 1.0 : 0.0);
         float var15 = this.pool.to("hr" + var1, var13 ? 1.0 : 0.0);
         String var16 = this.ell(var3, var8 - 44, LyricsScreen.T.S);
         this.txtC(var16, var6x + var8 / 2.0F, var10 - 4 - var11 * 1.5F, 13684950, 1.0F, LyricsScreen.T.S);
         Icons.draw(this.gfx, Icons.Icon.BACK, var6x + 16 - var14 * 2.0F, var10, 5.0F + var14, Gfx.lerpColor(5000276, 15658737, var14), 1.0F);
         Icons.draw(this.gfx, Icons.Icon.CHEVRON, var6x + var8 - 16 + var15 * 2.0F, var10, 5.0F + var15, Gfx.lerpColor(5000276, 15658737, var15), 1.0F);
         this.hit(var6x, var7, var8 / 2, var9, () -> {
            this.pressed.put("pk" + var1, System.currentTimeMillis());
            var5.run();
            Fx.sound(Fx.Sfx.CLICK);
            NeonLyrics.saveConfig();
         }, "pair" + var1);
         this.hit(var6x + var8 / 2, var7, var8 - var8 / 2, var9, () -> {
            this.pressed.put("pk" + var1, System.currentTimeMillis());
            var5.run();
            Fx.sound(Fx.Sfx.CLICK);
            NeonLyrics.saveConfig();
         }, "pair" + var1);
      };
   }

   private void accentRow() {
      Config var1 = c();
      byte var2 = 30;
      int var3 = this.rowStart(var2);
      if (var3 != Integer.MIN_VALUE) {
         this.rowBg(var3, var2, "accent");
         int var4 = var3 + var2 / 2;
         int var5 = Theme.ACCENTS.length;
         byte var6 = 14;
         int var7 = this.vx + this.vw - 17 - (var5 - 1) * var6;
         this.labels(var3, var2, t("ui.display.accent"), null, var7 - this.vx - 24);

         for (int var8 = 0; var8 < var5; var8++) {
            float var9 = var7 + var8 * var6;
            float var10 = this.pool.to("accs" + var8, var1.accent == var8 ? 1.0 : 0.0);
            if (var10 > 0.01F) {
               Gfx.aaCircle(this.gfx, var9, var4, 6.5F, 16777215, var10);
               Gfx.aaCircle(this.gfx, var9, var4, 5.2F, 1710623, 1.0F);
            }

            Gfx.aaCircle(this.gfx, var9, var4, 4.2F, Theme.ACCENTS[var8], 1.0F);
            int var11 = var8;
            this.hit(Math.round(var9 - 7.0F), var3, 14, var2, () -> {
               var1.accent = var11;
               var1.rgbAccent = false;
               Fx.sound(Fx.Sfx.CLICK);
               NeonLyrics.saveConfig();
            });
         }
      }

      this.rowEnd();
   }

   private void linkRow(String var1, String var2, Runnable var3) {
      int var4 = this.rowH(var2);
      int var5 = this.rowStart(var4);
      if (var5 != Integer.MIN_VALUE) {
         String var6 = "lk" + var1;
         this.rowBg(var5, var4, var6);
         float var7 = this.hover(this.vx, var5, this.vw, var4, var6);
         float var8 = 1.0F - Anim.outCubic(Anim.since(this.pressed.getOrDefault(var6, 0L), 260L));
         this.labels(var5 + Math.round(var8), var4, var1, var2, this.vw - 40);
         Icons.draw(
            this.gfx,
            Icons.Icon.CHEVRON,
            this.vx + this.vw - 15 + var7 * 3.0F + var8 * 3.0F,
            var5 + var4 / 2.0F,
            6.0F + var7,
            Gfx.lerpColor(5000276, 15658737, var7),
            1.0F
         );
         this.hit(this.vx, var5, this.vw, var4, () -> {
            this.pressed.put(var6, System.currentTimeMillis());
            Fx.sound(Fx.Sfx.CLICK);
            var3.run();
         }, var6);
      }

      this.rowEnd();
   }

   private void fieldRow(String var1, String var2) {
      byte var3 = 58;
      int var4 = this.rowStart(var3);
      if (var4 != Integer.MIN_VALUE) {
         this.rowBg(var4, var3, "field");
         int var5 = this.vx + 12;
         int var6 = this.vw - 24;
         this.txt(var1, var5, var4 + 8, 15658737, 1.0F, LyricsScreen.T.B);
         this.txt(this.ell(var2, var6, LyricsScreen.T.S), var5, var4 + 19, 8618892, 1.0F, LyricsScreen.T.S);
         float var7 = this.pool.to("idfocus", this.idBox.isFocused() ? 1.0 : 0.0);
         Gfx.aaRound(this.gfx, var5, var4 + 31, var6, 20.0F, 6.0F, Gfx.lerpColor(2500140, 4868690, var7), 1.0F);
         Gfx.aaRound(this.gfx, var5 + 1, var4 + 32, var6 - 2, 18.0F, 5.0F, 921105, 1.0F);
         this.idWanted = true;
         this.idX = var5 + 7;
         this.idY = var4 + 37;
         this.idW = var6 - 14;
         this.hit(var5, var4 + 31, var6, 20, () -> this.setFocused(this.idBox));
      }

      this.rowEnd();
   }

   private void copyRow(String var1, String var2) {
      byte var3 = 38;
      int var4 = this.rowStart(var3);
      if (var4 != Integer.MIN_VALUE) {
         this.rowBg(var4, var3, "copy");
         String var5 = t("btn.copy");
         int var6 = this.tw(var5, LyricsScreen.T.S) + 16;
         this.labels(var4, var3, var1, var2, this.vw - var6 - 40);
         int var7 = this.vx + this.vw - 12 - var6;
         int var8 = var4 + var3 / 2 - 9;
         float var9 = this.hover(var7, var8, var6, 18, "copyb");
         Gfx.aaRound(this.gfx, var7, var8, var6, 18.0F, 9.0F, Gfx.lerpColor(2763313, 3816002, var9), 1.0F);
         this.txtC(var5, var7 + var6 / 2.0F, var8 + 5, 15658737, 1.0F, LyricsScreen.T.S);
         this.hit(var7, var8, var6, 18, () -> {
            Fx.clipboard(var2);
            Fx.sound(Fx.Sfx.CLICK);
            Island.notify(t("toast.copiedres"), true);
         });
      }

      this.rowEnd();
   }

   private void statusRow() {
      Config var1 = c();
      byte var2 = 38;
      int var3 = this.rowStart(var2);
      if (var3 != Integer.MIN_VALUE) {
         this.rowBg(var3, var2, "status");
         NowPlaying var4 = SpotifyClient.now();
         boolean var5 = var1.demoLyrics
            || (SpotifyClient.autoMode() ? LocalMedia.status() == LocalMedia.Status.PLAYING : SpotifyClient.status() == SpotifyClient.Status.CONNECTED);
         boolean var6 = SpotifyClient.autoMode()
            ? LocalMedia.status() == LocalMedia.Status.FAILED || LocalMedia.status() == LocalMedia.Status.UNSUPPORTED
            : SpotifyClient.status() == SpotifyClient.Status.ERROR;
         int var7 = var5 ? 6022299 : (var6 ? 16735354 : 16762460);
         float var8 = var5 ? 0.5F + 0.5F * Anim.pulse(1600.0) : 1.0F;
         Gfx.aaCircle(this.gfx, this.vx + this.vw - 20, var3 + var2 / 2.0F, 7.0F, var7, 0.15F * var8);
         Gfx.aaCircle(this.gfx, this.vx + this.vw - 20, var3 + var2 / 2.0F, 3.0F, var7, 1.0F);
         String var9 = var4.hasTrack() ? var4.title + (var4.artist.isBlank() ? "" : " · " + var4.artist) : t("spotify.nothing");
         this.labels(var3, var2, this.spotifyStatus(), var9, this.vw - 50);
      }

      this.rowEnd();
   }

   private String spotifyStatus() {
      Config var1 = c();
      if (var1.demoLyrics) {
         return t("spotify.st.demo");
      }

      if (SpotifyClient.autoMode()) {
         return switch (LocalMedia.status()) {
            case UNSUPPORTED -> t("spotify.auto.unsupported");
            case STARTING -> t("spotify.auto.starting");
            case NO_PLAYER -> t("spotify.auto.noplayer");
            case PLAYING -> t("spotify.auto.on", LocalMedia.app());
            case FAILED -> t("spotify.auto.failed");
         };
      } else {
         return switch (SpotifyClient.status()) {
            case NOT_CONFIGURED -> t("spotify.st.setup");
            case DISCONNECTED -> t("spotify.st.off");
            case WAITING_LOGIN -> t("spotify.st.wait");
            case CONNECTED -> t("spotify.st.on");
            case ERROR -> t("spotify.st.error", SpotifyClient.lastError());
         };
      }
   }

   private void fpsRow() {
      Config var1 = c();
      byte var2 = 50;
      int var3 = this.rowStart(var2);
      if (var3 != Integer.MIN_VALUE) {
         this.rowBg(var3, var2, "fpsrow");
         int var4 = this.vx + 12;
         int var5 = this.vw - 24;
         String var6 = Integer.toString(Math.round(this.pool.to("fpsnum", AutoPerf.fps())));
         this.txt(var6, var4, var3 + 12, 15658737, 1.0F, LyricsScreen.T.XL);
         this.txt("FPS", var4 + this.tw(var6, LyricsScreen.T.XL) + 5, var3 + 24, 8618892, 1.0F, LyricsScreen.T.M);
         int var7 = AutoPerf.level();
         String var8 = !var1.autoPerf ? t("perf.auto.off") : (var7 == 0 ? t("perf.auto.full") : t("perf.auto.level", var7, 6));
         this.txtR(t("ui.perf.cost", String.format(Locale.ROOT, "%.1f", AutoPerf.modMs())), var4 + var5, var3 + 15, 8618892, 1.0F, LyricsScreen.T.S);
         this.txtR(var8, var4 + var5, var3 + 26, 8618892, 1.0F, LyricsScreen.T.S);
      }

      this.rowEnd();
   }

   private void skyGrid() {
      Config var1 = c();
      int var2 = this.vw >= 330 ? 3 : 2;
      byte var3 = 8;
      int var4 = (this.vw - var3 * (var2 - 1)) / var2;
      int var5 = Math.round(var4 * 0.5F);
      int var6 = var5 + 22;
      int var7 = (SKY_ORDER.length + var2 - 1) / var2;
      int var8 = var1.sky;

      for (int var9 = 0; var9 < var7; var9++) {
         int var10 = this.rowStart(var6);
         if (var10 != Integer.MIN_VALUE) {
            for (int var11 = 0; var11 < var2; var11++) {
               int var12 = var9 * var2 + var11;
               if (var12 >= SKY_ORDER.length) {
                  break;
               }

               SkyPreset var13 = SKY_ORDER[var12];
               int var14 = this.vx + var11 * (var4 + var3);
               int var15 = var10 + 2;
               boolean var16 = var8 == var13.ordinal();
               float var17 = this.hover(var14, var15, var4, var5 + 16, "sky" + var12);
               if (this.closeTime < 0L && this.in(var14, var15, var4, var5 + 16) && this.my >= this.vy && this.my < this.vy + this.vh) {
                  this.skyHover = var13;
               }

               float var18 = this.pool.to("skysel" + var12, var16 ? 1.0 : 0.0);
               float var19 = 1.0F - Anim.outCubic(Anim.since(this.pressed.getOrDefault("sky" + var12, 0L), 300L));
               float var20 = this.spring("skysc" + var12, 1.0F + 0.05F * var17) - 0.05F * var19;
               int var21 = Math.round(var15 - var17 * 2.0F);
               this.gfx.getMatrices().pushMatrix();
               this.gfx.getMatrices().translate(var14 + var4 / 2.0F, var21 + var5 / 2.0F);
               this.gfx.getMatrices().scale(var20, var20);
               this.gfx.getMatrices().translate(-(var14 + var4 / 2.0F), -(var21 + var5 / 2.0F));
               if (var17 > 0.01F) {
                  Gfx.aaRound(this.gfx, var14, var21 + 5, var4, var5, 8.0F, 0, 0.4F * var17);
               }

               if (var18 > 0.01F) {
                  float var22 = 2.0F + 2.0F * (1.0F - Anim.outBack(var18));
                  Gfx.aaRound(this.gfx, var14 - var22, var21 - var22, var4 + var22 * 2.0F, var5 + var22 * 2.0F, 7.0F + var22, 16777215, var18);
                  Gfx.aaRound(this.gfx, var14 - 0.75F, var21 - 0.75F, var4 + 1.5F, var5 + 1.5F, 7.5F, 1118484, 1.0F);
               }

               float var25 = var8 < 0 ? 0.45F : 0.8F + 0.2F * Math.max(var17, var18);
               if (!SkyThumbs.draw(this.gfx, var13, var14, var21, var4, var5, var25)) {
                  Gfx.aaRound(this.gfx, var14, var21, var4, var5, 7.0F, var13.top, var25);
               }

               this.gfx.getMatrices().popMatrix();
               this.txt(
                  this.ell(Fx.t(var13.key()), var4, LyricsScreen.T.S),
                  var14,
                  var15 + var5 + 6,
                  Gfx.lerpColor(Gfx.lerpColor(8618892, 13158606, var17), 15658737, var18),
                  1.0F,
                  var16 ? LyricsScreen.T.B : LyricsScreen.T.S
               );
               SkyPreset var23 = var13;
               int var24 = var12;
               this.hit(var14, var15, var4, var5 + 16, () -> {
                  this.pressed.put("sky" + var24, System.currentTimeMillis());
                  this.setSky(var23);
                  Fx.sound(Fx.Sfx.CLICK);
                  NeonLyrics.saveConfig();
               });
            }
         }

         this.rowEnd();
      }
   }

   private Text comp(String var1, LyricsScreen.T var2) {
      Map<String, Text> var3 = this.comps.computeIfAbsent(var2, var0 -> new HashMap<>());
      if (var3.size() > 600) {
         var3.clear();
      }

      return var3.computeIfAbsent(var1, var1x -> Text.literal(var1x).fillStyle(Style.EMPTY.withFont(FONTS.get(var2))));
   }

   private int tw(String var1, LyricsScreen.T var2) {
      if (var1 != null && !var1.isEmpty()) {
         Map<String, Integer> var3 = this.widths.computeIfAbsent(var2, var0 -> new HashMap<>());
         if (var3.size() > 600) {
            var3.clear();
         }

         Integer var4 = (Integer)var3.get(var1);
         if (var4 == null) {
            var4 = this.textRenderer.getWidth(this.comp(var1, var2));
            var3.put(var1, var4);
         }

         return var4;
      } else {
         return 0;
      }
   }

   private void txt(String var1, float var2, float var3, int var4, float var5, LyricsScreen.T var6) {
      if (var1 != null && !var1.isEmpty() && !(var5 * Gfx.alpha < 0.02F)) {
         this.gfx.drawText(this.textRenderer, this.comp(var1, var6), Math.round(var2), Math.round(var3), Gfx.argb(var4, var5), false);
      }
   }

   private void txtR(String var1, float var2, float var3, int var4, float var5, LyricsScreen.T var6) {
      this.txt(var1, var2 - this.tw(var1, var6), var3, var4, var5, var6);
   }

   private void txtC(String var1, float var2, float var3, int var4, float var5, LyricsScreen.T var6) {
      this.txt(var1, var2 - this.tw(var1, var6) / 2.0F, var3, var4, var5, var6);
   }

   private String ell(String var1, int var2, LyricsScreen.T var3) {
      if (var1 == null) {
         return "";
      }

      if (this.tw(var1, var3) <= var2) {
         return var1;
      }

      while (var1.length() > 1 && this.tw(var1 + "…", var3) > var2) {
         var1 = var1.substring(0, var1.length() - 1);
      }

      return var1 + "…";
   }

   private List<String> wrap(String var1, int var2, LyricsScreen.T var3) {
      ArrayList var4 = new ArrayList();
      if (var1 != null && !var1.isEmpty()) {
         StringBuilder var5 = new StringBuilder();

         for (String var9 : var1.split(" ")) {
            String var10 = var5.length() == 0 ? var9 : var5 + " " + var9;
            if (this.tw(var10, var3) > var2 && var5.length() > 0) {
               var4.add(var5.toString());
               var5 = new StringBuilder(var9);
            } else {
               var5 = new StringBuilder(var10);
            }
         }

         if (var5.length() > 0) {
            var4.add(var5.toString());
         }

         return var4;
      } else {
         return var4;
      }
   }

   private float spring(String var1, float var2) {
      float[] var3 = this.springs.computeIfAbsent(var1, var1x -> new float[]{var2, 0.0F});
      if (!this.anim()) {
         var3[0] = var2;
         var3[1] = 0.0F;
         return var2;
      }

      float var4 = Math.min(this.frameDt, 0.05F) / 3.0F;

      for (int var5 = 0; var5 < 3; var5++) {
         float var6 = 320.0F * (var2 - var3[0]) - 24.0F * var3[1];
         var3[1] += var6 * var4;
         var3[0] += var3[1] * var4;
      }

      return var3[0];
   }

   private void ripple(String var1, int var2, int var3, int var4, int var5) {
      LyricsScreen.Ripple var6 = this.ripples.get(var1);
      if (var6 != null) {
         float var7 = (float)(System.currentTimeMillis() - var6.at()) / 520.0F;
         if (var7 >= 1.0F) {
            this.ripples.remove(var1);
         } else {
            float var8 = Anim.outCubic(var7);
            float var9 = Math.max(var2, Math.min(var2 + var4, var6.x()));
            float var10 = var9 - (var9 - var2) * var8;
            float var11 = var9 + (var2 + var4 - var9) * var8;
            float var12 = 0.16F * (1.0F - var7);
            int var13 = this.inContent ? Math.max(var3, this.vy) : var3;
            int var14 = this.inContent ? Math.min(var3 + var5, this.vy + this.vh) : var3 + var5;
            if (var14 > var13 && !(var12 < 0.005F)) {
               Gfx.aaRound(this.gfx, var10, var13, Math.max(2.0F, var11 - var10), var14 - var13, Math.min(6.0F, (var14 - var13) / 2.0F), 16777215, var12);
            }
         }
      }
   }

   private void iconBtn(DrawContext var1, String var2, int var3, int var4, int var5, Icons.Icon var6, float var7, Runnable var8) {
      float var9 = this.hover(var3, var4, var5, var5, "ib" + var2);
      float var10 = 1.0F - Anim.outCubic(Anim.since(this.pressed.getOrDefault(var2, 0L), 220L));
      float var11 = var3 + var5 / 2.0F;
      float var12 = var4 + var5 / 2.0F;
      if (var9 > 0.01F) {
         Gfx.aaCircle(var1, var11, var12, var5 / 2.0F * (0.8F + 0.2F * var9), 16777215, 0.07F * var9);
      }

      if (var10 > 0.01F) {
         Gfx.aaCircle(var1, var11, var12, var5 / 2.0F + 6.0F * (1.0F - var10), 16777215, 0.14F * var10);
      }

      Icons.draw(var1, var6, var11, var12, var7 * (1.0F - 0.15F * var10 + 0.08F * var9), Gfx.lerpColor(13158606, 16777215, var9), 1.0F);
      this.hit(var3, var4, var5, var5, () -> {
         this.pressed.put(var2, System.currentTimeMillis());
         Fx.sound(Fx.Sfx.CLICK);
         var8.run();
      });
   }

   private float hover(int var1, int var2, int var3, int var4, String var5) {
      boolean var6 = this.closeTime < 0L && this.in(var1, var2, var3, var4) && (!this.inContent || this.my >= this.vy && this.my < this.vy + this.vh);
      return this.pool.to("hv" + var5, var6 ? 1.0 : 0.0);
   }

   private boolean in(int var1, int var2, int var3, int var4) {
      return this.mx >= var1 && this.my >= var2 && this.mx < var1 + var3 && this.my < var2 + var4;
   }

   private void hit(int var1, int var2, int var3, int var4, Runnable var5) {
      this.hit(var1, var2, var3, var4, var5, null);
   }

   private void hit(int var1, int var2, int var3, int var4, Runnable var5, String var6) {
      if (this.closeTime < 0L) {
         if (this.inContent) {
            int var7 = Math.max(var2, this.vy);
            int var8 = Math.min(var2 + var4, this.vy + this.vh);
            if (var8 > var7) {
               this.hits.add(new LyricsScreen.Hit(var1, var7, var3, var8 - var7, var5, var6, true));
            }
         } else {
            this.hits.add(new LyricsScreen.Hit(var1, var2, var3, var4, var5, var6, false));
         }
      }
   }

   private static String ago(long var0) {
      long var2 = var0 / 60000L;
      if (var2 < 1L) {
         return t("ui.ago.now");
      } else {
         return var2 < 60L ? t("ui.ago.min", var2) : t("ui.ago.h", var2 / 60L);
      }
   }

   private static int indexOf(int[] var0, int var1) {
      for (int var2 = 0; var2 < var0.length; var2++) {
         if (var0[var2] == var1) {
            return var2;
         }
      }

      return -1;
   }

   private void setAllEffects(boolean var1) {
      Config var2 = c();
      if (!var1 && var2.sky >= 0) {
         var2.lastSky = var2.sky;
      }

      var2.sky = var1 ? Math.max(0, var2.lastSky) : -1;
      var2.fxSkyColor = var1;
      var2.fxWeather = var1;
      var2.fxAurora = var1;
      var2.fxStars = var1;
      var2.fxBeams = var1;
      var2.lyricsGlow = var1;
      var2.glassHotbar = var1;
      var2.blur = var1;
      var2.fxMenuAnim = var1;
      var2.quality = var1 ? 1 : 0;
      if (!var1 && !var2.fpsBoost) {
         FpsBoost.apply(true);
      }

      if (var1 && var2.fpsBoost) {
         FpsBoost.apply(false);
      }

      NeonLyrics.saveConfig();
      Island.notify(t(var1 ? "perf.allon.done" : "perf.alloff.done"), true);
   }

   private void openTutorial() {
      c().spotifyClientId = this.idBox.getText().trim();
      NeonLyrics.saveConfig();
      this.client.setScreen(new SpotifyTutorialScreen(new LyricsScreen(LyricsScreen.Page.SPOTIFY)));
   }

   public boolean mouseClicked(Click click, boolean doubled) {
      click = this.fm(click);
      if (super.mouseClicked(click, doubled)) {
         return true;
      }

      if (this.closeTime < 0L && click.button() == 0) {
         double var3 = click.x() / this.S;
         double var5 = click.y() / this.S;

         for (LyricsScreen.Slider var8 : this.sliders) {
            if (var8.h() > 0 && var3 >= var8.x() - 6 && var3 <= var8.x() + var8.w() + 6 && var5 >= var8.y() && var5 <= var8.y() + var8.h()) {
               this.activeSlider = var8;
               var8.set().accept(Anim.clamp((float)((var3 - var8.x()) / var8.w())));
               return true;
            }
         }

         for (int var10 = this.hits.size() - 1; var10 >= 0; var10--) {
            LyricsScreen.Hit var11 = this.hits.get(var10);
            if (var3 >= var11.x() && var5 >= var11.y() && var3 < var11.x() + var11.w() && var5 < var11.y() + var11.h()) {
               this.setFocused(null);
               if (var11.rid() != null) {
                  this.ripples
                     .put(
                        var11.rid(),
                        new LyricsScreen.Ripple(
                           (float)var3, (float)(var11.content() ? var5 - this.vy + this.scroll : var5), var11.content(), System.currentTimeMillis()
                        )
                     );
               }

               var11.run().run();
               return true;
            }
         }

         this.setFocused(null);
         return false;
      } else {
         return false;
      }
   }

   public boolean mouseDragged(Click click, double offsetX, double offsetY) {
      click = this.fm(click);
      if (this.activeSlider != null) {
         this.activeSlider.set().accept(Anim.clamp((float)((click.x() / this.S - this.activeSlider.x()) / this.activeSlider.w())));
         return true;
      } else {
         return super.mouseDragged(click, offsetX, offsetY);
      }
   }

   public boolean mouseReleased(Click click) {
      click = this.fm(click);
      if (this.activeSlider != null) {
         this.activeSlider = null;
         NeonLyrics.saveConfig();
         return true;
      } else {
         return super.mouseReleased(click);
      }
   }

   public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
      this.scrollTarget -= (float)verticalAmount * 30.0F;
      return true;
   }

   public boolean keyPressed(KeyInput input) {
      int var2 = input.key();
      boolean var3 = this.getFocused() == this.idBox;
      if (!var3 && var2 >= 49 && var2 < 49 + LyricsScreen.Page.values().length) {
         this.go(LyricsScreen.Page.values()[var2 - 49]);
         return true;
      }

      if (!var3 && var2 == 258) {
         this.go(LyricsScreen.Page.values()[(this.page.ordinal() + 1) % LyricsScreen.Page.values().length]);
         return true;
      }

      if (var3 || var2 != 264 && var2 != 267) {
         if (var3 || var2 != 265 && var2 != 266) {
            if (!var3 && KeyBindingHelper.getBoundKeyOf(NeonLyrics.keyMenu).getCode() == var2) {
               this.close();
               return true;
            } else {
               return super.keyPressed(input);
            }
         } else {
            this.scrollTarget = this.scrollTarget - (var2 == 265 ? 30.0F : this.vh * 0.8F);
            return true;
         }
      } else {
         this.scrollTarget = this.scrollTarget + (var2 == 264 ? 30.0F : this.vh * 0.8F);
         return true;
      }
   }

   public void close() {
      if (this.closeTime < 0L) {
         this.closeTime = System.currentTimeMillis();
         c().spotifyClientId = this.idBox.getText().trim();
         NeonLyrics.saveConfig();
         show(this.idBox, false);
      }
   }

   public boolean shouldPause() {
      return false;
   }

   static {
      FONTS.put(LyricsScreen.T.S, fd("menu_s"));
      FONTS.put(LyricsScreen.T.M, fd("menu_m"));
      FONTS.put(LyricsScreen.T.B, fd("menu_b"));
      FONTS.put(LyricsScreen.T.TITLE, fd("menu_t"));
      FONTS.put(LyricsScreen.T.XL, fd("menu_xl"));
   }

   private interface Cell {
      void draw(int var1, int var2, int var3, int var4);
   }

   private record Hit(int x, int y, int w, int h, Runnable run, String rid, boolean content) {
   }

   public enum Page {
      SKY,
      LYRICS,
      STYLE,
      SPOTIFY,
      DISPLAY,
      MENU,
      PERF,
      FAV;
   }

   private record Ripple(float x, float y, boolean content, long at) {
   }

   private record Slider(String id, int x, int y, int w, int h, Consumer<Float> set) {
   }

   private enum T {
      S,
      M,
      B,
      TITLE,
      XL;
   }
}
