package dev.neonlyrics.cosmetic;

import dev.neonlyrics.sky.SkyTextures;
import java.util.Random;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

final class DeathFresh {
   static final int FIRST = 4;
   static final float[] END = new float[]{4.3F, 4.4F, 4.6F, 4.6F};
   static final int[] HUES = new int[]{16727464, 3073791, 16765501, 8191851, 11824127, 16738877};

   private DeathFresh() {
   }

   static Object create(DeathFx.Fx var0, Random var1) {
      DeathFresh.X var2 = new DeathFresh.X();
      int var3 = var0.kind - 4;
      if (var3 == 0) {
         var2.a = new float[110][];

         for (int var4 = 0; var4 < var2.a.length; var4++) {
            var2.a[var4] = new float[]{
               (var1.nextFloat() - 0.5F) * 0.55F,
               0.05F + var1.nextFloat() * 1.8F,
               (var1.nextFloat() - 0.5F) * 0.3F,
               0.05F + var1.nextFloat() * 0.09F,
               1.32F + var1.nextFloat() * 0.35F,
               (var1.nextFloat() - 0.5F) * 14.0F,
               0.6F + var1.nextFloat() * 1.4F
            };
         }
      } else if (var3 == 1) {
         var2.a = new float[16][];

         for (int var11 = 0; var11 < var2.a.length; var11++) {
            var2.a[var11] = new float[]{
               (float)(var11 / 16.0 * Math.PI * 2.0 + var1.nextFloat() * 0.3),
               0.25F + var1.nextFloat() * 0.6F,
               0.5F + var1.nextFloat() * 1.3F,
               0.15F + var1.nextFloat() * 0.45F,
               var1.nextFloat() * 0.35F
            };
         }

         var2.b = new float[80][];

         for (int var12 = 0; var12 < var2.b.length; var12++) {
            double var5 = var1.nextDouble() * Math.PI * 2.0;
            float var7 = 1.5F + var1.nextFloat() * 4.5F;
            var2.b[var12] = new float[]{
               (var1.nextFloat() - 0.5F) * 0.6F,
               0.1F + var1.nextFloat() * 1.8F,
               (var1.nextFloat() - 0.5F) * 0.4F,
               (float)Math.cos(var5) * var7,
               1.5F + var1.nextFloat() * 5.0F,
               (float)Math.sin(var5) * var7,
               0.07F + var1.nextFloat() * 0.17F,
               var1.nextFloat() * 6.0F,
               (var1.nextFloat() - 0.5F) * 16.0F,
               var1.nextFloat()
            };
         }
      } else if (var3 == 2) {
         var2.a = new float[36][];

         for (int var13 = 0; var13 < var2.a.length; var13++) {
            var2.a[var13] = new float[]{
               var1.nextFloat() * 6.283F, 0.8F + var1.nextFloat() * 3.2F, 0.12F + var1.nextFloat() * 0.22F, 0.3F + var1.nextFloat() * 1.8F
            };
         }
      } else {
         var2.a = new float[180][];

         for (int var14 = 0; var14 < var2.a.length; var14++) {
            double var15 = var1.nextDouble() * 2.0 - 1.0;
            double var16 = var1.nextDouble() * Math.PI * 2.0;
            double var9 = Math.sqrt(1.0 - var15 * var15);
            var2.a[var14] = new float[]{
               (float)(var9 * Math.cos(var16)),
               (float)var15,
               (float)(var9 * Math.sin(var16)),
               5.5F + var1.nextFloat() * 3.5F,
               var1.nextInt(3),
               var1.nextFloat() * 6.28F
            };
         }

         var2.hues = new int[]{HUES[var1.nextInt(HUES.length)], HUES[var1.nextInt(HUES.length)], 16774872};
      }

      return var2;
   }

   private static void once(MinecraftClient var0, DeathFx.Fx var1, SoundEvent var2, float var3, float var4) {
      try {
         var0.world.playSoundClient(var1.ox, var1.oy + 1.0, var1.oz, var2, SoundCategory.PLAYERS, var3, var4, false);
      } catch (Throwable var6) {
      }
   }

   static void draw(MinecraftClient var0, VertexConsumerProvider var1, Matrix4f var2, Vec3d var3, DeathFx.Fx var4, float var5) {
      if (Accessories.whiteTex()) {
         DeathFresh.X var6 = (DeathFresh.X)var4.extra;
         int var7 = var4.kind - 4;
         Matrix4f var8 = new Matrix4f(var2).translate((float)(var4.ox - var3.x), (float)(var4.oy - var3.y), (float)(var4.oz - var3.z)).rotateY(-var4.yaw);
         int var9 = DeathFx.light(var0, var4.ox, var4.oy + 0.5, var4.oz);
         boolean var10 = var4.self && var0.options.getPerspective().isFirstPerson();
         switch (var7) {
            case 0:
               orbital(var0, var1, var2, var3, var4, var6, var8, var9, var5, var10);
               break;
            case 1:
               frost(var0, var1, var2, var3, var4, var6, var8, var9, var5, var10);
               break;
            case 2:
               blackHole(var0, var1, var2, var3, var4, var6, var8, var9, var5, var10);
               break;
            default:
               firework(var0, var1, var2, var3, var4, var6, var8, var9, var5, var10);
         }
      }
   }

   private static void body(VertexConsumerProvider var0, DeathFx.Fx var1, Matrix4f var2, DeathFx.Pose var3, int var4, int var5, Matrix4f var6) {
      if (var1.skin != null) {
         VertexConsumer var7 = var0.getBuffer(RenderLayers.entityCutoutNoCull(var1.skin));
         Matrix4f var8 = new Matrix4f(var2).translate(var3.p[0], var3.p[1], var3.p[2]).rotateY(var3.ry).rotateX(var3.rx).rotateZ(var3.rz);
         if (var6 != null) {
            var8.mul(var6);
         }

         DeathFx.body(var7, var8, var3, var4, var5);
      }
   }

   private static DeathFx.Pose stand(float var0) {
      DeathFx.Pose var1 = new DeathFx.Pose();
      var1.p[0] = 0.0F;
      var1.p[1] = 1.055F + var0;
      var1.p[2] = 0.0F;
      return var1;
   }

   private static int tint(int var0) {
      return 0xFF000000 | var0;
   }

   private static void orbital(
      MinecraftClient var0,
      VertexConsumerProvider var1,
      Matrix4f var2,
      Vec3d var3,
      DeathFx.Fx var4,
      DeathFresh.X var5,
      Matrix4f var6,
      int var7,
      float var8,
      boolean var9
   ) {
      if (var8 >= 1.25F && !var5.s1) {
         var5.s1 = true;
         once(var0, var4, SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER, 1.2F, 1.4F);
         once(var0, var4, SoundEvents.BLOCK_BEACON_ACTIVATE, 1.5F, 0.6F);
      }

      VertexConsumer var10 = var1.getBuffer(RenderLayers.debugQuads());
      float var11 = 0.03F;
      if (var8 < 1.45F) {
         float var12 = DeathFx.sm(0.0F, 0.15F, var8) * (1.0F - DeathFx.sm(1.25F, 1.45F, var8));
         float var13 = 2.4F - 1.4F * DeathFx.eo(var8 / 1.1F);
         float var14 = var8 > 0.9F ? 0.6F + 0.4F * (float)Math.sin(var8 * 40.0F) : 1.0F;
         DeathFx.disc(var10, var6, 0.0F, var11, 0.0F, var13 * 0.93F, var13, 16720960, 0.95F * var12 * var14, 16720960, 0.95F * var12 * var14, 48);
         DeathFx.disc(var10, var6, 0.0F, var11, 0.0F, var13 * 0.5F, var13 * 0.54F, 16720960, 0.7F * var12, 16720960, 0.7F * var12, 40);
         DeathFx.disc(var10, var6, 0.0F, var11, 0.0F, 0.0F, var13 * 0.1F, 16734832, 0.9F * var12, 16720960, 0.5F * var12, 16);

         for (int var15 = 0; var15 < 4; var15++) {
            double var16 = var15 * Math.PI / 2.0 + var8 * 1.5;
            float var18 = (float)Math.cos(var16);
            float var19 = (float)Math.sin(var16);
            DeathFx.strip(
               var10,
               var6,
               var18 * var13 * 0.6F,
               var19 * var13 * 0.6F,
               var18 * var13 * 1.25F,
               var19 * var13 * 1.25F,
               var11 + 0.004F,
               0.06F,
               16720960,
               0.9F * var12
            );
         }

         column(
            var10, var6, 0.0F, 60.0F, 0.03F + 0.02F * (float)Math.sin(var8 * 30.0F), 16730726, 0.5F * var12 * DeathFx.sm(0.4F, 0.9F, var8), 16730726, 0.0F, 8
         );
      }

      if (var8 >= 1.25F) {
         float var26 = var8 - 1.25F;
         float var29 = 1.35F * DeathFx.eo(var26 / 0.12F) * (1.0F - DeathFx.sm(1.1F, 1.7F, var26)) * (1.0F + 0.06F * (float)Math.sin(var8 * 50.0F));
         if (var29 > 0.01F) {
            column(var10, var6, 0.0F, 90.0F, var29, 7071999, 0.35F, 7071999, 0.0F, 20);
            column(var10, var6, 0.0F, 90.0F, var29 * 0.55F, 13170943, 0.75F, 13170943, 0.25F, 16);
            column(var10, var6, 0.0F, 90.0F, var29 * 0.25F, 16777215, 1.0F, 16777215, 0.9F, 12);
         }

         float var33 = var26 / 0.9F;
         if (var33 < 1.0F) {
            float var37 = 0.6F + DeathFx.eo(var33) * 7.0F;
            DeathFx.disc(var10, var6, 0.0F, var11 + 0.02F, 0.0F, var37 * 0.88F, var37, 7071999, 0.0F, 7071999, 0.9F * (1.0F - var33), 48);
            DeathFx.disc(var10, var6, 0.0F, var11 + 0.02F, 0.0F, var37, var37 * 1.05F, 16777215, 0.8F * (1.0F - var33), 16777215, 0.0F, 48);
         }

         float var38 = DeathFx.sm(0.05F, 0.3F, var26) * (1.0F - DeathFx.sm(2.4F, 3.0F, var26));
         DeathFx.disc(var10, var6, 0.0F, var11 + 0.01F, 0.0F, 0.0F, 1.6F, 656902, 0.92F * var38, 1706502, 0.7F * var38, 32);
         DeathFx.disc(
            var10, var6, 0.0F, var11 + 0.012F, 0.0F, 1.45F, 1.9F, 16742942, 0.85F * var38 * (0.75F + 0.25F * (float)Math.sin(var8 * 13.0F)), 16726544, 0.0F, 32
         );
      }

      if (var8 < 1.6F && !var9) {
         DeathFx.Pose var27 = stand(0.35F * DeathFx.sm(0.5F, 1.25F, var8));
         var27.arm = 0.35F * DeathFx.sm(0.4F, 1.0F, var8) + 0.05F * (float)Math.sin(var8 * 9.0F);
         var27.head = -0.3F * DeathFx.sm(0.6F, 1.2F, var8);
         var27.rz = 0.03F * (float)Math.sin(var8 * 30.0F) * DeathFx.sm(0.8F, 1.25F, var8);
         float var30 = 0.5F + 0.5F * (float)Math.sin(var8 * 18.0F);
         int var34 = var8 < 1.25F ? DeathFx.mix(16777215, 16738922, 0.5F * var30 * DeathFx.sm(0.3F, 0.8F, var8)) : DeathFx.mix(16777215, 13170943, 1.0F);
         body(var1, var4, var6, var27, tint(var34), var8 < 1.25F ? var7 : 15728880, null);
      }

      VertexConsumer var28 = var1.getBuffer(RenderLayers.entityCutoutNoCull(Accessories.WHITE));

      for (float[] var41 : var5.a) {
         float var17 = var8 - var41[4];
         if (!(var17 <= 0.0F) && !(var17 > 2.6F)) {
            float var43 = var41[6];
            float var45 = var17 * 4.0F * var43;
            float var20 = 0.25F + var17 * 0.35F;
            float var21 = var41[0] + (float)Math.cos(var45) * var20 * 0.6F;
            float var22 = var41[2] + (float)Math.sin(var45) * var20 * 0.6F;
            float var23 = 0.35F + var41[1] + var17 * var17 * 4.5F * var43;
            float var24 = var41[3] * (1.0F - var17 / 2.6F);
            Matrix4f var25 = new Matrix4f(var6).translate(var21, var23, var22).rotateXYZ(var41[5] * var17, var41[5] * var17 * 0.7F, 0.0F);
            DeathFx.cube(
               var28, var25, -var24 / 2.0F, -var24 / 2.0F, -var24 / 2.0F, var24 / 2.0F, var24 / 2.0F, var24 / 2.0F, var17 < 0.25F ? -1 : -9705217, 15728880
            );
         }
      }

      if (SkyTextures.spritesReady()) {
         VertexConsumer var32 = var1.getBuffer(RenderLayers.text(SkyTextures.SPRITES));

         for (int var36 = 0; var36 < 26; var36++) {
            float var40 = (var8 - 1.25F - var36 * 0.03F) / 1.6F;
            if (!(var40 <= 0.0F) && !(var40 >= 1.0F)) {
               double var42 = var36 * 2.4;
               float var44 = 0.4F + var40 * 2.2F;
               DeathFx.sprite(
                  var32,
                  var2,
                  var4,
                  var3,
                  (float)Math.cos(var42) * var44,
                  0.1F + var40 * 2.5F,
                  (float)Math.sin(var42) * var44,
                  0.25F + var40 * 0.4F,
                  2,
                  var36 % 2 == 0 ? 16747056 : 7071999,
                  (float)Math.sin(var40 * Math.PI),
                  DeathFx.AX[0],
                  DeathFx.AX[1],
                  DeathFx.AX[2],
                  DeathFx.AX[3],
                  DeathFx.AX[4],
                  DeathFx.AX[5]
               );
            }
         }
      }
   }

   private static void frost(
      MinecraftClient var0,
      VertexConsumerProvider var1,
      Matrix4f var2,
      Vec3d var3,
      DeathFx.Fx var4,
      DeathFresh.X var5,
      Matrix4f var6,
      int var7,
      float var8,
      boolean var9
   ) {
      if (var8 > 0.05F && !var5.s1) {
         var5.s1 = true;
         once(var0, var4, SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME, 1.2F, 0.5F);
      }

      if (var8 >= 1.45F && !var5.s2) {
         var5.s2 = true;
         once(var0, var4, SoundEvents.BLOCK_GLASS_BREAK, 1.5F, 0.7F);
         once(var0, var4, SoundEvents.BLOCK_GLASS_BREAK, 1.2F, 1.2F);
      }

      VertexConsumer var10 = var1.getBuffer(RenderLayers.debugQuads());
      float var11 = 1.0F - DeathFx.sm(3.6F, 4.4F, var8);
      float var12 = 2.6F * DeathFx.eo(var8 / 1.1F);
      DeathFx.disc(var10, var6, 0.0F, 0.025F, 0.0F, 0.0F, var12, 14546687, 0.55F * var11, 10475775, 0.0F, 40);
      DeathFx.disc(var10, var6, 0.0F, 0.027F, 0.0F, var12 * 0.9F, var12, 16777215, 0.6F * var11, 16777215, 0.0F, 40);
      if (var8 >= 1.45F) {
         float var13 = (var8 - 1.45F) / 0.7F;
         if (var13 < 1.0F) {
            DeathFx.disc(
               var10,
               var6,
               0.0F,
               0.03F,
               0.0F,
               0.5F + DeathFx.eo(var13) * 5.0F,
               0.6F + DeathFx.eo(var13) * 5.4F,
               16777215,
               0.85F * (1.0F - var13),
               10475775,
               0.0F,
               48
            );
         }
      }

      VertexConsumer var27 = var1.getBuffer(RenderLayers.entityCutoutNoCull(Accessories.WHITE));

      for (float[] var17 : var5.a) {
         float var18 = DeathFx.sm(0.05F + var17[4], 0.95F, var8) * (1.0F - DeathFx.sm(1.45F, 1.7F, var8));
         if (!(var18 <= 0.01F)) {
            float var19 = var17[2] * var18;
            Matrix4f var20 = new Matrix4f(var6)
               .translate((float)Math.cos(var17[0]) * var17[1], 0.0F, (float)Math.sin(var17[0]) * var17[1])
               .rotateY(-var17[0])
               .rotateZ(-var17[3]);
            DeathFx.cube(var27, var20, -0.07F, 0.0F, -0.07F, 0.07F, var19, 0.07F, -4658433, var7);
            DeathFx.cube(var27, var20, -0.035F, var19, -0.035F, 0.035F, var19 + 0.18F * var18, 0.035F, -1509121, var7);
         }
      }

      if (var8 < 1.45F && !var9) {
         DeathFx.Pose var29 = stand(0.0F);
         float var32 = DeathFx.sm(0.1F, 1.0F, var8);
         var29.arm = 0.6F * var32;
         var29.head = -0.25F * var32;
         var29.leg = 0.1F * var32;
         var29.rz = 0.025F * (float)Math.sin(var8 * 60.0F) * DeathFx.sm(1.0F, 1.2F, var8);
         body(var1, var4, var6, var29, tint(DeathFx.mix(16777215, 9228287, var32)), var7, null);
      }

      if (var8 >= 1.45F) {
         var27 = var1.getBuffer(RenderLayers.entityCutoutNoCull(Accessories.WHITE));

         for (float[] var37 : var5.b) {
            float var39 = var8 - 1.45F;
            float var40 = var37[0] + var37[3] * var39 * 0.85F;
            float var42 = var37[2] + var37[5] * var39 * 0.85F;
            float var21 = var37[4];
            float var22 = var37[1];
            float var23 = var39;

            for (int var24 = 0; var24 < 3; var24++) {
               float var25 = (var21 + (float)Math.sqrt(var21 * var21 + 28.0F * Math.max(0.01F, var22))) / 14.0F;
               if (var23 < var25) {
                  var22 = var22 + var21 * var23 - 7.0F * var23 * var23;
                  break;
               }

               var23 -= var25;
               var22 = 0.0F;
               var21 = -(var21 - 14.0F * var25) * 0.35F;
               if (var24 == 2) {
                  var22 = 0.0F;
               }
            }

            var22 = Math.max(0.01F, var22);
            float var44 = var37[6] * (1.0F - DeathFx.sm(3.4F, 4.3F, var8));
            if (!(var44 <= 0.005F)) {
               Matrix4f var45 = new Matrix4f(var6)
                  .translate(var40, var22 + var44 / 2.0F, var42)
                  .rotateXYZ(var37[7] + var37[8] * Math.min(var39, 1.2F), var37[8] * Math.min(var39, 1.2F) * 0.6F, 0.0F);
               int var26 = var37[9] < 0.4F ? -4658433 : (var37[9] < 0.75F ? -1509121 : -7548929);
               DeathFx.cube(var27, var45, -var44 / 2.0F, -var44 / 2.0F, -var44 / 2.0F, var44 / 2.0F, var44 / 2.0F, var44 / 2.0F, var26, var7);
            }
         }
      }

      if (SkyTextures.spritesReady()) {
         VertexConsumer var31 = var1.getBuffer(RenderLayers.text(SkyTextures.SPRITES));

         for (int var34 = 0; var34 < 30; var34++) {
            float var36 = (var8 - 1.45F - var34 * 0.01F) / 1.2F;
            if (!(var36 <= 0.0F) && !(var36 >= 1.0F)) {
               double var38 = var34 * 2.1;
               float var41 = 0.3F + var36 * 2.6F;
               DeathFx.sprite(
                  var31,
                  var2,
                  var4,
                  var3,
                  (float)Math.cos(var38) * var41,
                  0.6F + (float)Math.sin(var34) * 0.6F + var36,
                  (float)Math.sin(var38) * var41,
                  0.5F + var36,
                  1,
                  15268095,
                  0.7F * (1.0F - var36),
                  DeathFx.AX[0],
                  DeathFx.AX[1],
                  DeathFx.AX[2],
                  DeathFx.AX[3],
                  DeathFx.AX[4],
                  DeathFx.AX[5]
               );
            }
         }
      }
   }

   private static void blackHole(
      MinecraftClient var0,
      VertexConsumerProvider var1,
      Matrix4f var2,
      Vec3d var3,
      DeathFx.Fx var4,
      DeathFresh.X var5,
      Matrix4f var6,
      int var7,
      float var8,
      boolean var9
   ) {
      if (var8 > 0.05F && !var5.s1) {
         var5.s1 = true;
         once(var0, var4, SoundEvents.BLOCK_PORTAL_TRAVEL, 0.5F, 1.8F);
      }

      if (var8 >= 2.75F && !var5.s2) {
         var5.s2 = true;
         once(var0, var4, SoundEvents.ENTITY_WARDEN_SONIC_BOOM, 1.3F, 0.7F);
      }

      VertexConsumer var10 = var1.getBuffer(RenderLayers.debugQuads());
      float var11 = 0.75F * DeathFx.eo(var8 / 0.5F) * (1.0F - DeathFx.sm(2.5F, 2.75F, var8))
         + 0.15F * DeathFx.sm(1.5F, 2.4F, var8) * (1.0F - DeathFx.sm(2.45F, 2.75F, var8));
      if (var11 > 0.01F) {
         for (int var12 = 0; var12 < 4; var12++) {
            float var13 = 0.35F + 0.1F * var12;
            float var14 = var8 * (1.5F + var12 * 0.4F) + var12;
            float var15 = (float)Math.sin(var14) * var13;
            float var16 = (float)Math.cos(var14) * var13;
            int var17 = var12 % 2 == 0 ? 11824127 : 16743129;
            DeathFx.ringAround(
               var10, var6, 0.0F, 3.4F, 0.0F, var15, 1.0F, var16, var11 * (1.6F + var12 * 0.45F), var11 * (0.35F + 0.1F * var12), var17, 0.75F - var12 * 0.12F
            );
         }

         float var24 = (float)(var4.ox - var3.x);
         float var27 = (float)(var4.oy + 3.4F - var3.y);
         float var31 = (float)(var4.oz - var3.z);
         camDisc(var10, var2, var24, var27, var31, 0.0F, var11, 0, 1.0F, 0, 1.0F, 32);
         camDisc(var10, var2, var24, var27, var31, var11, var11 * 1.35F, 11824127, 0.9F, 11824127, 0.0F, 32);
      }

      if (var8 >= 2.75F) {
         float var25 = (var8 - 2.75F) / 0.9F;
         if (var25 < 1.0F) {
            float var28 = (float)(var4.ox - var3.x);
            float var32 = (float)(var4.oy + 3.4F - var3.y);
            float var35 = (float)(var4.oz - var3.z);
            camDisc(var10, var2, var28, var32, var35, 0.0F, 0.5F + 3.5F * DeathFx.eo(var25), 16777215, 0.9F * (1.0F - var25), 11824127, 0.0F, 32);
            DeathFx.disc(
               var10,
               var6,
               0.0F,
               3.4F,
               0.0F,
               0.5F + DeathFx.eo(var25) * 8.0F,
               0.7F + DeathFx.eo(var25) * 8.6F,
               16743129,
               0.8F * (1.0F - var25),
               11824127,
               0.0F,
               48
            );
         }
      }

      VertexConsumer var26 = var1.getBuffer(RenderLayers.entityCutoutNoCull(Accessories.WHITE));

      for (float[] var38 : var5.a) {
         float var40 = (var8 - var38[3]) / 1.1F;
         if (!(var40 <= 0.0F) && !(var40 >= 1.0F) && !(var8 > 2.75F)) {
            float var18 = DeathFx.ei(var40);
            float var19 = var38[0] + var18 * 9.0F;
            float var20 = var38[1] * (1.0F - var18);
            float var21 = var18 * 3.4F;
            float var22 = var38[2] * (1.0F - var18 * 0.7F);
            Matrix4f var23 = new Matrix4f(var6)
               .translate((float)Math.cos(var19) * var20, var21 + 0.05F, (float)Math.sin(var19) * var20)
               .rotateXYZ(var40 * 9.0F, var40 * 7.0F, 0.0F);
            DeathFx.cube(
               var26,
               var23,
               -var22 / 2.0F,
               -var22 / 2.0F,
               -var22 / 2.0F,
               var22 / 2.0F,
               var22 / 2.0F,
               var22 / 2.0F,
               0xFF000000 | var4.groundCol & 16777215,
               var7
            );
         }
      }

      if (var8 < 2.75F && !var9) {
         float var30 = DeathFx.ei(DeathFx.cl((var8 - 0.35F) / 2.3F));
         DeathFx.Pose var34 = new DeathFx.Pose();
         float var37 = var30 * 14.0F;
         float var39 = (1.0F - var30) * 0.6F * DeathFx.sm(0.35F, 0.9F, var8);
         var34.p[0] = (float)Math.cos(var37) * var39;
         var34.p[1] = 1.055F + 2.3450003F * var30 + 0.3F * DeathFx.sm(0.1F, 0.6F, var8) * (1.0F - var30);
         var34.p[2] = (float)Math.sin(var37) * var39;
         var34.ry = var37 * 1.3F;
         var34.rx = -0.6F * var30;
         var34.arm = 1.2F + 0.4F * (float)Math.sin(var8 * 12.0F);
         var34.leg = 0.4F * (float)Math.sin(var8 * 10.0F);
         var34.head = -0.6F;
         float var41 = 1.0F - var30 * 0.85F;
         Matrix4f var42 = new Matrix4f().scale(var41 * (1.0F - var30 * 0.4F), var41 * (1.0F + var30 * 1.6F), var41 * (1.0F - var30 * 0.4F));
         body(var1, var4, var6, var34, tint(DeathFx.mix(16777215, 13148415, var30)), var7, var42);
      }
   }

   private static void firework(
      MinecraftClient var0,
      VertexConsumerProvider var1,
      Matrix4f var2,
      Vec3d var3,
      DeathFx.Fx var4,
      DeathFresh.X var5,
      Matrix4f var6,
      int var7,
      float var8,
      boolean var9
   ) {
      if (var8 >= 0.6F && !var5.s1) {
         var5.s1 = true;
         once(var0, var4, SoundEvents.ENTITY_FIREWORK_ROCKET_LAUNCH, 2.0F, 0.8F);
      }

      if (var8 >= 1.7F && !var5.s2) {
         var5.s2 = true;
         once(var0, var4, (SoundEvent)SoundEvents.ENTITY_GENERIC_EXPLODE.value(), 1.4F, 1.3F);
      }

      float var10 = var8 < 0.6F ? 0.0F : DeathFx.ei(DeathFx.cl((var8 - 0.6F) / 1.1F));
      float var11 = 1.055F - 0.12F * DeathFx.sm(0.0F, 0.6F, var8) * (1.0F - DeathFx.sm(0.6F, 0.70000005F, var8)) + var10 * 16.0F;
      if (var8 < 1.7F && !var9) {
         DeathFx.Pose var12 = new DeathFx.Pose();
         var12.p[1] = var11;
         var12.ry = var10 * 18.0F;
         var12.arm = var8 < 0.6F ? 0.3F : -0.2F;
         var12.leg = 0.0F;
         var12.head = 0.0F;
         var12.rz = 0.03F * (float)Math.sin(var8 * 50.0F) * DeathFx.sm(0.1F, 0.6F, var8) * (1.0F - DeathFx.sm(0.6F, 0.8F, var8));
         body(var1, var4, var6, var12, tint(var8 < 0.6F ? 16777215 : DeathFx.mix(16777215, 16767136, var10)), var8 < 0.6F ? var7 : 15728880, null);
      }

      VertexConsumer var36 = var1.getBuffer(RenderLayers.debugQuads());
      if (var8 >= 0.6F && var8 < 1.4000001F) {
         float var13 = 1.0F - (var8 - 0.6F) / 0.8F;
         DeathFx.disc(var36, var6, 0.0F, 0.03F, 0.0F, 0.0F, 1.2F + (1.0F - var13) * 1.5F, 16756816, 0.8F * var13, 16732192, 0.0F, 32);
      }

      if (SkyTextures.spritesReady()) {
         VertexConsumer var37 = var1.getBuffer(RenderLayers.text(SkyTextures.SPRITES));
         float var14 = DeathFx.AX[0];
         float var15 = DeathFx.AX[1];
         float var16 = DeathFx.AX[2];
         float var17 = DeathFx.AX[3];
         float var18 = DeathFx.AX[4];
         float var19 = DeathFx.AX[5];
         if (var8 < 0.70000005F) {
            for (int var20 = 0; var20 < 14; var20++) {
               float var21 = (var8 * 3.0F + var20 * 0.137F) % 1.0F;
               double var22 = var20 * 2.7 + var8 * 5.0F;
               DeathFx.sprite(
                  var37,
                  var2,
                  var4,
                  var3,
                  (float)Math.cos(var22) * var21 * 0.6F,
                  0.05F + var21 * 0.5F,
                  (float)Math.sin(var22) * var21 * 0.6F,
                  0.12F,
                  2,
                  16762976,
                  1.0F - var21,
                  var14,
                  var15,
                  var16,
                  var17,
                  var18,
                  var19
               );
            }
         }

         if (var8 >= 0.6F) {
            for (int var38 = 0; var38 < 28; var38++) {
               float var40 = var8 - var38 * 0.035F;
               if (!(var40 < 0.6F) && !(var40 > 1.7F)) {
                  float var42 = DeathFx.ei(DeathFx.cl((var40 - 0.6F) / 1.1F));
                  float var23 = 1.055F + var42 * 16.0F - 0.9F;
                  float var24 = var8 - var40;
                  DeathFx.sprite(
                     var37,
                     var2,
                     var4,
                     var3,
                     (float)Math.sin(var38 * 1.7) * var24 * 0.5F,
                     var23 - var24 * 0.3F,
                     (float)Math.cos(var38 * 1.3) * var24 * 0.5F,
                     0.5F + var24 * 1.6F,
                     1,
                     9079442,
                     0.45F * (1.0F - var24 / 1.0F),
                     var14,
                     var15,
                     var16,
                     var17,
                     var18,
                     var19
                  );
                  if (var38 < 10) {
                     DeathFx.sprite(
                        var37, var2, var4, var3, 0.0F, var23 + 0.2F, 0.0F, 0.3F, 2, 16756816, 1.0F - var38 / 10.0F, var14, var15, var16, var17, var18, var19
                     );
                  }
               }
            }
         }

         if (var8 >= 1.7F) {
            float var39 = var8 - 1.7F;
            float var41 = 0.0F;
            float var43 = 17.055F;
            float var44 = 0.0F;

            for (float[] var27 : var5.a) {
               float var28 = var27[3] * (1.0F - (float)Math.exp(-var39 * 2.4)) / 2.4F * 2.2F;
               float var29 = var41 + var27[0] * var28;
               float var30 = var43 + var27[1] * var28 - var39 * var39 * 1.6F;
               float var31 = var44 + var27[2] * var28;
               float var32 = 1.0F - DeathFx.sm(1.6F, 2.8F, var39);
               float var33 = 0.6F + 0.4F * (float)Math.sin(var39 * 25.0F + var27[5]);
               int var34 = var5.hues[(int)var27[4]];
               DeathFx.sprite(var37, var2, var4, var3, var29, var30, var31, 0.42F, 2, var34, var32 * var33, var14, var15, var16, var17, var18, var19);
               float var35 = var28 * 0.85F;
               DeathFx.sprite(
                  var37,
                  var2,
                  var4,
                  var3,
                  var41 + var27[0] * var35,
                  var43 + var27[1] * var35 - var39 * var39 * 1.4F,
                  var44 + var27[2] * var35,
                  0.26F,
                  2,
                  var34,
                  var32 * 0.4F,
                  var14,
                  var15,
                  var16,
                  var17,
                  var18,
                  var19
               );
            }

            if (var39 < 0.4F) {
               DeathFx.sprite(
                  var37,
                  var2,
                  var4,
                  var3,
                  var41,
                  var43,
                  var44,
                  4.0F * (1.0F - var39 / 0.4F) + 1.0F,
                  2,
                  16774872,
                  1.0F - var39 / 0.4F,
                  var14,
                  var15,
                  var16,
                  var17,
                  var18,
                  var19
               );
            }
         }
      }
   }

   static void column(VertexConsumer var0, Matrix4f var1, float var2, float var3, float var4, int var5, float var6, int var7, float var8, int var9) {
      if (!(var4 <= 0.001F)) {
         int var10 = DeathFx.argb(var5, var6);
         int var11 = DeathFx.argb(var7, var8);

         for (int var12 = 0; var12 < var9; var12++) {
            double var13 = var12 * Math.PI * 2.0 / var9;
            double var15 = (var12 + 1) * Math.PI * 2.0 / var9;
            float var17 = (float)Math.cos(var13) * var4;
            float var18 = (float)Math.sin(var13) * var4;
            float var19 = (float)Math.cos(var15) * var4;
            float var20 = (float)Math.sin(var15) * var4;
            DeathFx.GP[0] = var17;
            DeathFx.GP[1] = var2;
            DeathFx.GP[2] = var18;
            DeathFx.GP[3] = var19;
            DeathFx.GP[4] = var2;
            DeathFx.GP[5] = var20;
            DeathFx.GP[6] = var19;
            DeathFx.GP[7] = var3;
            DeathFx.GP[8] = var20;
            DeathFx.GP[9] = var17;
            DeathFx.GP[10] = var3;
            DeathFx.GP[11] = var18;
            DeathFx.gquad(var0, var1, DeathFx.GP, var10, var10, var11, var11);
         }
      }
   }

   static void camDisc(
      VertexConsumer var0, Matrix4f var1, float var2, float var3, float var4, float var5, float var6, int var7, float var8, int var9, float var10, int var11
   ) {
      int var12 = DeathFx.argb(var7, var8);
      int var13 = DeathFx.argb(var9, var10);
      float var14 = DeathFx.AX[0];
      float var15 = DeathFx.AX[1];
      float var16 = DeathFx.AX[2];
      float var17 = DeathFx.AX[3];
      float var18 = DeathFx.AX[4];
      float var19 = DeathFx.AX[5];

      for (int var20 = 0; var20 < var11; var20++) {
         double var21 = var20 * Math.PI * 2.0 / var11;
         double var23 = (var20 + 1) * Math.PI * 2.0 / var11;
         float var25 = (float)Math.cos(var21);
         float var26 = (float)Math.sin(var21);
         float var27 = (float)Math.cos(var23);
         float var28 = (float)Math.sin(var23);
         DeathFx.GP[0] = var2 + (var14 * var25 + var17 * var26) * var5;
         DeathFx.GP[1] = var3 + (var15 * var25 + var18 * var26) * var5;
         DeathFx.GP[2] = var4 + (var16 * var25 + var19 * var26) * var5;
         DeathFx.GP[3] = var2 + (var14 * var27 + var17 * var28) * var5;
         DeathFx.GP[4] = var3 + (var15 * var27 + var18 * var28) * var5;
         DeathFx.GP[5] = var4 + (var16 * var27 + var19 * var28) * var5;
         DeathFx.GP[6] = var2 + (var14 * var27 + var17 * var28) * var6;
         DeathFx.GP[7] = var3 + (var15 * var27 + var18 * var28) * var6;
         DeathFx.GP[8] = var4 + (var16 * var27 + var19 * var28) * var6;
         DeathFx.GP[9] = var2 + (var14 * var25 + var17 * var26) * var6;
         DeathFx.GP[10] = var3 + (var15 * var25 + var18 * var26) * var6;
         DeathFx.GP[11] = var4 + (var16 * var25 + var19 * var26) * var6;
         DeathFx.gquad(var0, var1, DeathFx.GP, var12, var12, var13, var13);
      }
   }

   static final class X {
      boolean s1;
      boolean s2;
      boolean s3;
      float[][] a;
      float[][] b;
      int[] hues;
   }
}
