package dev.neonlyrics.cosmetic;

import dev.neonlyrics.NeonLyrics;
import dev.neonlyrics.config.Config;
import dev.neonlyrics.lyrics.NowPlaying;
import dev.neonlyrics.lyrics.SpotifyClient;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import javax.imageio.ImageIO;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LightType;
import org.joml.Matrix4f;

public final class Accessories {
   public static final String[] HATS = new String[]{
      "none",
      "headphones",
      "cone",
      "crown",
      "halo",
      "pumpkin",
      "flame_crown",
      "saturn",
      "storm_cloud",
      "unicorn",
      "disco_ball",
      "rubber_duck",
      "octopus",
      "cat_ears",
      "devil_horns",
      "top_hat",
      "cap",
      "party_hat",
      "viking",
      "bunny_ears",
      "note_ring",
      "flower_crown",
      "beanie",
      "visor",
      "witch_hat",
      "antennae",
      "mushroom"
   };
   public static final Identifier WHITE = Identifier.of("neonlyrics", "cosmetic/white");
   private static boolean whiteReady;
   private static boolean whiteFailed;
   private static final float[][] MESH = new float[HATS.length][];
   private static final boolean[] TRIED = new boolean[HATS.length];
   private static final int STRIDE = 14;

   private Accessories() {
   }

   private static float[] mesh(int var0) {
      if (TRIED[var0]) {
         return MESH[var0];
      }

      TRIED[var0] = true;

      try (InputStream var1 = Accessories.class.getResourceAsStream("/assets/neonlyrics/models/" + HATS[var0] + ".bin")) {
         if (var1 == null) {
            return null;
         }

         ByteBuffer var2 = ByteBuffer.wrap(var1.readAllBytes()).order(ByteOrder.LITTLE_ENDIAN);
         if (var2.getInt() != 827149390) {
            return null;
         }

         int var3 = var2.getInt();
         float[] var4 = new float[var3 * 14];

         for (int var5 = 0; var5 < var3; var5++) {
            for (int var6 = 0; var6 < 12; var6++) {
               var4[var5 * 14 + var6] = var2.getFloat();
            }

            var4[var5 * 14 + 12] = Float.intBitsToFloat(var2.getInt());
            var4[var5 * 14 + 13] = var2.getInt();
         }

         MESH[var0] = var4;
      } catch (Throwable var9) {
         NeonLyrics.LOG.warn("Hut-Modell fehlt: " + HATS[var0], var9);
      }

      return MESH[var0];
   }

   public static boolean whiteTex() {
      return white();
   }

   private static boolean white() {
      if (whiteReady) {
         return true;
      }

      if (whiteFailed) {
         return false;
      }

      try {
         BufferedImage var0 = new BufferedImage(4, 4, 2);

         for (int var1 = 0; var1 < 4; var1++) {
            for (int var2 = 0; var2 < 4; var2++) {
               var0.setRGB(var2, var1, -1);
            }
         }

         ByteArrayOutputStream var4 = new ByteArrayOutputStream();
         ImageIO.write(var0, "png", var4);
         MinecraftClient.getInstance()
            .getTextureManager()
            .registerTexture(WHITE, new NativeImageBackedTexture(() -> "neonlyrics_white", NativeImage.read(var4.toByteArray())));
         whiteReady = true;
      } catch (Throwable var3) {
         whiteFailed = true;
      }

      return whiteReady;
   }

   public static void render(MinecraftClient var0, VertexConsumerProvider var1, Matrix4f var2, Vec3d var3, float var4) {
      Config var5 = NeonLyrics.cfg();
      int var6 = var5.hat;
      if (var6 > 0 && var6 < HATS.length) {
         ClientPlayerEntity var7 = var0.player;
         if (var7 != null && !var7.isInvisible() && !var7.isSleeping() && !var7.isSwimming() && !var7.isGliding()) {
            if (!var0.options.getPerspective().isFirstPerson() && !Emotes.active()) {
               float[] var8 = mesh(var6);
               if (var8 != null && white()) {
                  Vec3d var9 = var7.getLerpedPos(var4);
                  double var10 = var9.y + var7.getStandingEyeHeight() - 0.22;
                  float var12 = (float)Math.toRadians(MathHelper.lerpAngleDegrees(var4, var7.lastHeadYaw, var7.headYaw));
                  float var13 = (float)Math.toRadians(var7.getPitch(var4));
                  float var14 = (float)Math.cos(-var12);
                  float var15 = (float)Math.sin(-var12);
                  float var16 = (float)Math.cos(var13);
                  float var17 = (float)Math.sin(var13);
                  float var18 = 0.060937498F;
                  double var19 = var9.x - var3.x;
                  double var21 = var10 - var3.y;
                  double var23 = var9.z - var3.z;

                  int var25;
                  try {
                     BlockPos var26 = BlockPos.ofFloored(var9.x, var10 + 0.3, var9.z);
                     var25 = LightmapTextureManager.pack(var0.world.getLightLevel(LightType.BLOCK, var26), var0.world.getLightLevel(LightType.SKY, var26));
                  } catch (Throwable var49) {
                     var25 = 15728880;
                  }

                  NowPlaying var50 = SpotifyClient.now();
                  float var27 = var50.playing ? var50.beatPulse(var50.progressNow(var5.lyricsOffsetMs)) : 0.0F;
                  float var28 = var5.hatGlow ? 0.75F + 0.25F * var27 : 0.55F;
                  VertexConsumer var29 = var1.getBuffer(RenderLayers.entityCutoutNoCull(WHITE));
                  int var30 = var8.length / 14;
                  float[] var31 = new float[9];

                  for (int var32 = 0; var32 < var30; var32++) {
                     int var33 = var32 * 14;
                     float var34 = var8[var33 + 9];
                     float var35 = var8[var33 + 10];
                     float var36 = var8[var33 + 11];
                     float var37 = var35 * var16 - var36 * var17;
                     float var38 = var35 * var17 + var36 * var16;
                     float var39 = var34 * var14 + var38 * var15;
                     float var40 = -var34 * var15 + var38 * var14;

                     for (int var41 = 0; var41 < 3; var41++) {
                        float var42 = var8[var33 + var41 * 3] * var18;
                        float var43 = (var8[var33 + var41 * 3 + 1] + 4.0F) * var18;
                        float var44 = var8[var33 + var41 * 3 + 2] * var18;
                        float var45 = var43 * var16 - var44 * var17;
                        float var46 = var43 * var17 + var44 * var16;
                        var31[var41 * 3] = var42 * var14 + var46 * var15;
                        var31[var41 * 3 + 1] = var45;
                        var31[var41 * 3 + 2] = -var42 * var15 + var46 * var14;
                     }

                     int var51 = Float.floatToRawIntBits(var8[var33 + 12]);
                     boolean var52 = var8[var33 + 13] > 0.5F;
                     int var54 = var25;
                     int var53;
                     if (var52) {
                        float var55 = var28;
                        int var57 = Math.min(255, Math.round((var51 >> 16 & 0xFF) * (0.6F + 0.6F * var55)));
                        int var47 = Math.min(255, Math.round((var51 >> 8 & 0xFF) * (0.6F + 0.6F * var55)));
                        int var48 = Math.min(255, Math.round((var51 & 0xFF) * (0.6F + 0.6F * var55)));
                        var53 = 0xFF000000 | var57 << 16 | var47 << 8 | var48;
                        var54 = 15728880;
                     } else {
                        var53 = 0xFF000000 | var51;
                     }

                     for (int var56 = 0; var56 < 4; var56++) {
                        int var58 = Math.min(var56, 2);
                        var29.vertex(var2, (float)(var19 + var31[var58 * 3]), (float)(var21 + var31[var58 * 3 + 1]), (float)(var23 + var31[var58 * 3 + 2]))
                           .color(var53)
                           .texture(0.5F, 0.5F)
                           .overlay(OverlayTexture.DEFAULT_UV)
                           .light(var54)
                           .normal(var39, var37, var40);
                     }
                  }
               }
            }
         }
      }
   }
}
