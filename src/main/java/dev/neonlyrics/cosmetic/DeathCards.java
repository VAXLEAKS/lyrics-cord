package dev.neonlyrics.cosmetic;

import dev.neonlyrics.NeonLyrics;
import dev.neonlyrics.sky.SkyTextures;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Random;
import javax.imageio.ImageIO;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

final class DeathCards {
   static final float T_END = 4.2F;
   static final float[] TT = new float[9001];
   private static final int N = 8;
   private static final float DASH0 = 1.15F;
   private static final float GAP = 0.1F;
   private static final float DUR = 0.14F;
   private static final float TS = 2.05F;
   private static final float FIN0 = 2.25F;
   private static final float FIN1 = 2.75F;
   private static final float DY = 2.45F;
   private static final int[] SLASH_PX;
   private static final int[][] SLICES;
   private static final float[][] PARTS;
   private static final float[] A;
   private static final float[] B;
   private static final float[] S0;
   private static final float[][] LAND;
   private static final float[] C;
   private static final float[] C2;
   private static final float CW = 0.21F;
   private static final float CH = 0.29F;
   private static final float CT = 0.006F;
   static final Identifier CARDS;
   private static final int AW = 512;
   private static final int AH = 256;
   private static final int TW = 80;
   private static final int TH = 112;
   private static boolean cardsReady;
   private static boolean cardsFailed;

   private DeathCards() {
   }

   static Object create(DeathFx.Fx var0, Random var1) {
      DeathCards.State var2 = new DeathCards.State();
      var2.face[0] = 1;
      var2.face[1] = 2;

      for (int var3 = 2; var3 < 8; var3++) {
         var2.face[var3] = 3 + var1.nextInt(4);
      }

      for (int var11 = 0; var11 < SLICES.length; var11++) {
         double var4 = var1.nextDouble() * Math.PI * 2.0;
         float var6 = var11 == 0 ? 0.3F + var1.nextFloat() * 0.3F : 0.9F + var1.nextFloat() * 1.3F;
         float var7 = var1.nextFloat() - 0.5F;
         float var8 = var1.nextFloat() - 0.5F;
         float var9 = var1.nextFloat() - 0.5F;
         float var10 = (float)Math.sqrt(var7 * var7 + var8 * var8 + var9 * var9) + 1.0E-4F;
         var2.pieces[var11] = new float[]{
            (float)Math.cos(var4) * var6,
            var11 == 0 ? 0.4F : 1.4F + var11 * 0.45F + var1.nextFloat() * 0.8F,
            (float)Math.sin(var4) * var6,
            var7 / var10,
            var8 / var10,
            var9 / var10,
            var11 == 0 ? 2.5F : 5.0F + var1.nextFloat() * 5.0F
         };
      }

      for (int var12 = 0; var12 < var2.hits.length; var12++) {
         var2.hits[var12] = new float[]{
            (float)var1.nextGaussian() * 0.5F,
            0.4F + var1.nextFloat() * 1.2F,
            (float)var1.nextGaussian() * 0.5F,
            1.8F + var1.nextFloat() * 2.4F,
            0.04F + var1.nextFloat() * 0.06F,
            var1.nextFloat() < 0.4F ? 1.0F : 0.0F
         };
      }

      for (int var13 = 0; var13 < var2.burst.length; var13++) {
         double var15 = var1.nextDouble() * Math.PI * 2.0;
         float var19 = 1.5F + var1.nextFloat() * 3.0F;
         var2.burst[var13] = new float[]{
            (float)Math.cos(var15) * var19,
            1.0F + var1.nextFloat() * 3.5F,
            (float)Math.sin(var15) * var19,
            0.04F + var1.nextFloat() * 0.08F,
            var1.nextFloat() * 14.0F,
            var1.nextFloat() < 0.35F ? 1.0F : 0.0F
         };
      }

      int[] var14 = new int[]{13111342, 1381653, 15921906, 1986504, 2067002};

      for (int var16 = 0; var16 < var2.chips.length; var16++) {
         double var5 = var1.nextDouble() * Math.PI * 2.0;
         float var20 = 1.6F + var1.nextFloat() * 2.4F;
         var2.chips[var16] = new float[]{
            (float)Math.cos(var5) * var20,
            2.5F + var1.nextFloat() * 3.0F,
            (float)Math.sin(var5) * var20,
            6.0F + var1.nextFloat() * 14.0F,
            Float.intBitsToFloat(var14[var1.nextInt(var14.length)])
         };
      }

      for (int var17 = 0; var17 < var2.sparks.length; var17++) {
         var2.sparks[var17] = new float[]{
            var17 < 12 ? 0.1F + var1.nextFloat() * 0.15F : 2.75F + var1.nextFloat() * 0.1F,
            var1.nextFloat() * 6.283F,
            0.6F + var1.nextFloat() * 1.8F,
            0.3F + var1.nextFloat() * 1.5F,
            0.05F + var1.nextFloat() * 0.08F
         };
      }

      for (int var18 = 0; var18 < var2.embers.length; var18++) {
         var2.embers[var18] = new float[]{
            2.95F + var1.nextFloat() * 0.5F, (var1.nextFloat() - 0.5F) * 0.4F, (var1.nextFloat() - 0.5F) * 0.4F, 0.6F + var1.nextFloat() * 1.2F
         };
      }

      return var2;
   }

   private static float omega(float var0) {
      float var1 = Math.max(0.0F, var0 - 0.6F);
      return 1.3F * var0 + 6.0F * var1 * var1;
   }

   private static void orbit(int var0, float var1, boolean var2, float[] var3) {
      double var4 = var0 * Math.PI * 2.0 / 8.0 + omega(var1) + (var2 ? Math.PI : 0.0);
      float var6 = 1.6F - 0.15F * DeathFx.sm(0.9F, 1.2F, var1);
      var3[0] = (float)Math.cos(var4) * var6;
      var3[1] = 1.05F + 0.2F * (float)Math.sin(var4 * 2.0 + var1 * 3.0F);
      var3[2] = (float)Math.sin(var4) * var6;
   }

   private static float appear(int var0) {
      return 0.2F + var0 * 0.04F;
   }

   private static int flyPos(int var0, float var1, float[] var2) {
      float var3 = 1.15F + var0 * 0.1F;
      if (var1 < appear(var0)) {
         var2[0] = 0.0F;
         var2[1] = 2.45F + var0 * 0.012F;
         var2[2] = 0.0F;
         return 0;
      } else if (var1 < var3) {
         orbit(var0, var1, false, var2);
         float var9 = DeathFx.eo((var1 - appear(var0)) / 0.45F);
         var2[0] = DeathFx.lerp(0.0F, var2[0], var9);
         var2[1] = DeathFx.lerp(2.45F + var0 * 0.012F, var2[1], var9);
         var2[2] = DeathFx.lerp(0.0F, var2[2], var9);
         return 1;
      } else {
         orbit(var0, var3, false, A);
         float var4 = SLASH_PX[var0] * 0.05859375F;
         if (var1 < var3 + 0.14F) {
            float var10 = (var1 - var3) / 0.14F * 2.0F;
            var2[0] = A[0] + (0.0F - A[0]) * var10;
            var2[1] = A[1] + (var4 - A[1]) * var10;
            var2[2] = A[2] + (0.0F - A[2]) * var10;
            return 2;
         } else {
            float var5 = -A[0];
            float var6 = 2.0F * var4 - A[1];
            float var7 = -A[2];
            orbit(var0, var1, true, B);
            float var8 = DeathFx.eo((var1 - var3 - 0.14F) / 0.3F);
            var2[0] = DeathFx.lerp(var5, B[0], var8);
            var2[1] = DeathFx.lerp(var6, B[1], var8);
            var2[2] = DeathFx.lerp(var7, B[2], var8);
            return 1;
         }
      }
   }

   private static boolean card(int var0, float var1, float[] var2) {
      if (var1 < 2.25F) {
         int var10 = flyPos(var0, var1, var2);
         var2[3] = var10 == 0 ? 0.3F * var0 : var1 * (var10 == 2 ? 45.0F : 28.0F) + var0 * 1.3F;
         var2[4] = var10 == 0 ? 0.0F : 0.25F * (float)Math.sin(var0 * 1.7F + var1 * 2.0F);
         var2[5] = var10 == 0 ? DeathFx.sm(0.08F, 0.22F, var1) : 1.0F;
         return true;
      } else {
         flyPos(var0, 2.249F, S0);
         float var3 = var1 - 2.25F;
         if (var0 < 2) {
            float[] var11 = LAND[var0];
            float var12 = DeathFx.cl(var3 / 0.5F);
            float var13 = DeathFx.sm(0.0F, 1.0F, var12);
            var2[0] = DeathFx.lerp(S0[0], var11[0], var13);
            var2[2] = DeathFx.lerp(S0[2], var11[2], var13);
            var2[1] = DeathFx.lerp(S0[1], var11[1], var12 * var12) + (float)Math.sin(Math.PI * var12) * 0.7F * (1.0F - var12 * 0.3F);
            var2[3] = var11[3] + (1.0F - var12) * (1.0F - var12) * 14.0F;
            var2[4] = 0.0F;
            var2[5] = 1.0F - DeathFx.sm(3.7F, 4.1F, var1);
            return var2[5] > 0.01F;
         } else {
            float var4 = (float)Math.sqrt(S0[0] * S0[0] + S0[2] * S0[2]) + 1.0E-4F;
            float var5 = S0[0] / var4 * 3.2F;
            float var6 = S0[2] / var4 * 3.2F;
            float var7 = 2.2F + var0 * 0.15F;
            float var8 = (var7 + (float)Math.sqrt(var7 * var7 + 18.0F * Math.max(0.0F, S0[1] - 0.012F))) / 9.0F;
            float var9 = Math.min(var3, var8);
            var2[0] = S0[0] + var5 * var9;
            var2[2] = S0[2] + var6 * var9;
            var2[1] = Math.max(0.012F + var0 * 0.002F, S0[1] + var7 * var9 - 4.5F * var9 * var9);
            var2[3] = 63.0F + var0 * 1.3F + var9 * 20.0F;
            var2[4] = var3 < var8 ? (float)Math.sin(var3 * 9.0F + var0) * 0.6F : 0.0F;
            var2[5] = 1.0F - DeathFx.sm(3.2F, 3.7F, var1);
            return var2[5] > 0.01F;
         }
      }
   }

   static void draw(MinecraftClient var0, VertexConsumerProvider var1, Matrix4f var2, Vec3d var3, DeathFx.Fx var4, float var5) {
      if (var4.extra instanceof DeathCards.State var6 && Accessories.whiteTex() && cardsTex()) {
         Matrix4f var34 = new Matrix4f(var2).translate((float)(var4.ox - var3.x), (float)(var4.oy - var3.y), (float)(var4.oz - var3.z)).rotateY(-var4.yaw);
         int var8 = DeathFx.light(var0, var4.ox, var4.oy + 1.0, var4.oz);
         boolean var9 = var4.self && var0.options.getPerspective().isFirstPerson();
         VertexConsumer var10 = var1.getBuffer(RenderLayers.debugQuads());

         for (int var11 = 0; var11 < 8; var11++) {
            if (!(var5 < appear(var11) + 0.05F) && !(var5 > 2.75F)) {
               boolean var12 = var5 >= 1.15F + var11 * 0.1F && var5 < 1.15F + var11 * 0.1F + 0.14F;
               int var13 = var12 ? 16734780 : 16765802;
               card(var11, var5, C);

               for (int var14 = 1; var14 <= 6; var14++) {
                  card(var11, var5 - var14 * 0.014F, C2);
                  float var15 = C2[0] - C[0];
                  float var16 = C2[2] - C[2];
                  float var17 = (float)Math.sqrt(var15 * var15 + var16 * var16) / 0.014F;
                  float var18 = (var12 ? 0.75F : 0.4F) * (1.0F - var14 / 7.0F) * DeathFx.sm(2.0F, 7.0F, var17);
                  float var19 = 0.2F * (1.0F - (var14 - 1) / 7.0F);
                  float var20 = 0.2F * (1.0F - var14 / 7.0F);
                  ribbon(var10, var34, C[0], C[1], C[2], C2[0], C2[1], C2[2], var19, var20, var13, var18, var18 * 0.6F);
                  System.arraycopy(C2, 0, C, 0, 3);
               }
            }
         }

         if (var5 < 2.2F && !var9) {
            for (int var35 = 0; var35 < 8; var35++) {
               float var37 = 1.15F + var35 * 0.1F + 0.07F;
               float var41 = var5 - var37;
               if (!(var41 < 0.0F) && !(var41 > 0.6F)) {
                  orbit(var35, 1.15F + var35 * 0.1F, false, A);
                  float var43 = (float)Math.sqrt(A[0] * A[0] + A[2] * A[2]) + 1.0E-4F;
                  float var47 = -A[0] / var43;
                  float var53 = -A[2] / var43;
                  float var61 = SLASH_PX[var35] * 0.05859375F;
                  float var69 = (1.0F - var41 / 0.6F) * (var5 > 2.05F ? 1.0F - DeathFx.sm(2.05F, 2.2F, var5) : 1.0F);
                  float var77 = 0.42F;
                  float var85 = 0.045F * (1.0F - var41);
                  cross(var10, var34, -var47 * var77, var61, -var53 * var77, var47 * var77, var61, var53 * var77, var85 * 3.5F, 16719936, var69 * 0.35F);
                  cross(var10, var34, -var47 * var77, var61, -var53 * var77, var47 * var77, var61, var53 * var77, var85, 16773320, var69);
               }
            }
         }

         float var36 = (var5 - 2.75F) / 0.6F;
         if (var36 > 0.0F && var36 < 1.0F) {
            float var38 = 0.2F + DeathFx.eo(var36) * 1.6F;
            ringFlat(var10, var34, 0.0F, 0.03F, 0.0F, var38 * 0.85F, var38, 16765802, 0.9F * (1.0F - var36));
         }

         if (var5 > 2.7F) {
            float var39 = DeathFx.sm(2.7F, 2.85F, var5) * (1.0F - DeathFx.sm(3.7F, 4.1F, var5)) * (0.32F + 0.06F * (float)Math.sin(var5 * 6.0F));
            DeathFx.disc(var10, var34, 0.0F, 0.015F, 0.0F, 0.0F, 0.75F, 16762960, var39, 16762960, 0.0F, 28);
         }

         float var40 = (var5 - 2.05F) / 0.35F;
         if (var40 > 0.0F && var40 < 1.0F) {
            ringFlat(var10, var34, 0.0F, 1.1F, 0.0F, 0.2F + DeathFx.eo(var40) * 1.6F, 0.3F + DeathFx.eo(var40) * 1.75F, 16724048, 0.85F * (1.0F - var40));
         }

         VertexConsumer var42 = var1.getBuffer(RenderLayers.entityCutoutNoCull(Accessories.WHITE));
         if (var5 >= 2.05F && !var9) {
            for (int var44 = 0; var44 < SLICES.length; var44++) {
               Matrix4f var48 = pieceMatrix(var34, var6, var44, var5);
               if (var48 != null) {
                  float var54 = 0.75F + 0.25F * (float)Math.sin(var5 * 14.0F + var44);
                  int var62 = DeathFx.mix(7997206, 16723274, var54);

                  for (float[] var21 : PARTS) {
                     caps(var42, var48, var21, SLICES[var44][0], SLICES[var44][1], var62);
                  }
               }
            }
         }

         for (int var45 = 0; var45 < 8; var45++) {
            float var49 = 1.15F + var45 * 0.1F + 0.07F;
            float var55 = var5 - var49;
            if (!(var55 <= 0.0F) && !(var55 > 0.8F) && !var9) {
               orbit(var45, 1.15F + var45 * 0.1F, false, A);
               float var63 = (float)Math.sqrt(A[0] * A[0] + A[2] * A[2]) + 1.0E-4F;
               float var71 = -A[0] / var63;
               float var79 = -A[2] / var63;
               float var87 = SLASH_PX[var45] * 0.05859375F;

               for (int var93 = 0; var93 < 7; var93++) {
                  float[] var22 = var6.hits[var45 * 7 + var93];
                  float var23 = var71 * var22[3] + var22[0];
                  float var24 = var22[1];
                  float var25 = var79 * var22[3] + var22[2];
                  float var26 = var23 * var55;
                  float var27 = var87 + var24 * var55 - 6.0F * var55 * var55;
                  float var28 = var25 * var55;
                  if (!(var27 < 0.0F)) {
                     float var29 = var22[4] * (1.0F - var55 / 0.8F * 0.5F);
                     boolean var30 = var22[5] > 0.5F;
                     DeathFx.cube(
                        var42,
                        new Matrix4f(var34).translate(var26, var27, var28).rotateXYZ(var55 * 9.0F, var55 * 7.0F, 0.0F),
                        -var29 / 2.0F,
                        -var29 / 2.0F,
                        -var29 / 2.0F,
                        var29 / 2.0F,
                        var29 / 2.0F,
                        var29 / 2.0F,
                        var30 ? -54715 : -12974576,
                        var30 ? 15728880 : var8
                     );
                  }
               }
            }
         }

         float var46 = var5 - 2.05F;
         if (var46 > 0.0F && var46 < 1.6F) {
            for (float[] var72 : var6.burst) {
               float var80 = var72[0] * var46;
               float var88 = 1.1F + var72[1] * var46 - 6.0F * var46 * var46;
               float var94 = var72[2] * var46;
               if (!(var88 < 0.0F)) {
                  float var100 = var72[3] * (1.0F - var46 / 1.6F * 0.5F);
                  boolean var106 = var72[5] > 0.5F;
                  DeathFx.cube(
                     var42,
                     new Matrix4f(var34).translate(var80, var88, var94).rotateXYZ(var72[4] * var46, var72[4] * var46 * 0.6F, 0.0F),
                     -var100 / 2.0F,
                     -var100 / 2.0F,
                     -var100 / 2.0F,
                     var100 / 2.0F,
                     var100 / 2.0F,
                     var100 / 2.0F,
                     var106 ? -54715 : -12974576,
                     var106 ? 15728880 : var8
                  );
               }
            }
         }

         if (var46 > 0.0F && var5 < 3.8F) {
            for (float[] var73 : var6.chips) {
               float var81 = var73[1];
               float var89 = (var81 + (float)Math.sqrt(var81 * var81 + 19.44F)) / 9.0F;
               float var95 = Math.min(var46, var89);
               float var101 = var73[0] * var95;
               float var107 = var73[2] * var95;
               float var112 = Math.max(0.02F, 1.1F + var81 * var95 - 4.5F * var95 * var95);
               float var120 = 1.0F - DeathFx.sm(3.3F, 3.8F, var5);
               Matrix4f var126 = new Matrix4f(var34)
                  .translate(var101, var112, var107)
                  .rotateXYZ(var46 < var89 ? var73[3] * var95 : 0.0F, var73[3] * var95 * 0.5F, var46 < var89 ? var73[3] * var95 * 0.3F : 0.0F)
                  .scale(var120);
               chip(var42, var126, Float.floatToRawIntBits(var73[4]) | 0xFF000000, var8);
            }
         }

         VertexConsumer var52 = var1.getBuffer(RenderLayers.entityCutoutNoCull(CARDS));

         for (int var58 = 0; var58 < 8; var58++) {
            if (card(var58, var5, C) && !(C[5] <= 0.01F)) {
               Matrix4f var66 = new Matrix4f(var34).translate(C[0], C[1], C[2]).rotateY(C[3]).rotateX(C[4]).scale(C[5]);
               cardQuad(var52, var66, var6.face[var58]);
            }
         }

         if (!var9 && var4.skin != null) {
            VertexConsumer var59 = var1.getBuffer(RenderLayers.entityCutoutNoCull(var4.skin));
            int var67 = DeathFx.light(var0, var4.ox, var4.oy + 1.2, var4.oz);
            if (var5 < 2.05F) {
               float var74 = (float)(0.8 * Math.exp(-var5 * 10.0F));
               float var82 = 0.0F;
               float var90 = -0.12F * (float)Math.sin(DeathFx.cl(var5 / 0.35F) * Math.PI);

               for (int var96 = 0; var96 < 8; var96++) {
                  float var102 = 1.15F + var96 * 0.1F + 0.07F;
                  if (!(var5 < var102)) {
                     float var108 = (float)Math.exp(-(var5 - var102) * 12.0F);
                     var74 = Math.max(var74, var108);
                     var82 += ((var96 & 1) == 0 ? 0.22F : -0.22F) * var108;
                     var90 += 0.07F * var108;
                  }
               }

               int var97 = tint(Math.min(1.0F, var74));
               float var103 = 1.0546875F;
               Matrix4f var109 = new Matrix4f(var34).translate(0.0F, var103, 0.0F).rotateY(var82).rotateX(var90).translate(0.0F, -var103, 0.0F);

               for (int var113 = 0; var113 < SLICES.length; var113++) {
                  for (float[] var132 : PARTS) {
                     slice(var59, var109, var132, SLICES[var113][0], SLICES[var113][1], var97, var67);
                  }
               }
            } else {
               for (int var75 = 0; var75 < SLICES.length; var75++) {
                  Matrix4f var83 = pieceMatrix(var34, var6, var75, var5);
                  if (var83 != null) {
                     float var91 = DeathFx.sm(2.95F, 3.45F, var5);
                     int var98 = tint(Math.min(1.0F, 0.35F * (float)Math.exp(-(var5 - 2.05F) * 5.0F) + var91));

                     for (float[] var122 : PARTS) {
                        slice(var59, var83, var122, SLICES[var75][0], SLICES[var75][1], var98, var67);
                     }
                  }
               }
            }
         }

         if (SkyTextures.spritesReady()) {
            VertexConsumer var60 = var1.getBuffer(RenderLayers.text(SkyTextures.SPRITES));
            float var68 = DeathFx.AX[0];
            float var76 = DeathFx.AX[1];
            float var84 = DeathFx.AX[2];
            float var92 = DeathFx.AX[3];
            float var99 = DeathFx.AX[4];
            float var105 = DeathFx.AX[5];
            float var111 = (var5 - 0.05F) / 0.5F;
            if (var111 > 0.0F && var111 < 1.0F) {
               DeathFx.sprite(
                  var60,
                  var2,
                  var4,
                  var3,
                  0.0F,
                  2.45F,
                  0.0F,
                  1.6F * DeathFx.eo(var111),
                  2,
                  16765802,
                  (1.0F - var111) * 0.9F,
                  var68,
                  var76,
                  var84,
                  var92,
                  var99,
                  var105
               );
            }

            for (int var115 = 0; var115 < 8; var115++) {
               if (card(var115, var5, C) && !(C[5] <= 0.01F)) {
                  DeathFx.sprite(
                     var60,
                     var2,
                     var4,
                     var3,
                     C[0],
                     C[1],
                     C[2],
                     0.75F * C[5],
                     2,
                     var115 < 2 && var5 > 2.75F ? 16769162 : 16762960,
                     0.3F * C[5],
                     var68,
                     var76,
                     var84,
                     var92,
                     var99,
                     var105
                  );
               }
            }

            for (int var116 = 0; var116 < 8; var116++) {
               float var123 = var5 - (1.15F + var116 * 0.1F + 0.07F);
               if (!(var123 < 0.0F) && !(var123 > 0.25F) && !var9) {
                  DeathFx.sprite(
                     var60,
                     var2,
                     var4,
                     var3,
                     0.0F,
                     SLASH_PX[var116] * 0.05859375F,
                     0.0F,
                     0.6F + var123 * 4.0F,
                     2,
                     16773320,
                     1.0F - var123 / 0.25F,
                     var68,
                     var76,
                     var84,
                     var92,
                     var99,
                     var105
                  );
               }
            }

            if (var5 >= 2.05F && var5 < 2.45F) {
               float var117 = (var5 - 2.05F) / 0.4F;
               DeathFx.sprite(
                  var60,
                  var2,
                  var4,
                  var3,
                  0.0F,
                  1.1F,
                  0.0F,
                  1.2F + var117 * 3.0F,
                  2,
                  16726613,
                  0.9F * (1.0F - var117),
                  var68,
                  var76,
                  var84,
                  var92,
                  var99,
                  var105
               );
            }

            for (float[] var131 : var6.sparks) {
               float var133 = (var5 - var131[0]) / 0.9F;
               if (!(var133 <= 0.0F) && !(var133 >= 1.0F)) {
                  boolean var134 = var131[0] < 1.0F;
                  float var135 = 0.0F;
                  float var31 = var134 ? 2.45F : 0.05F;
                  float var32 = 0.0F;
                  float var33 = var131[2] * DeathFx.eo(var133) * (var134 ? 0.8F : 1.0F);
                  DeathFx.sprite(
                     var60,
                     var2,
                     var4,
                     var3,
                     var135 + (float)Math.cos(var131[1]) * var33,
                     var31 + var131[3] * var133 * (var134 ? 0.6F : 1.0F),
                     var32 + (float)Math.sin(var131[1]) * var33,
                     var131[4] * 2.4F,
                     2,
                     16765802,
                     (float)Math.sin(var133 * Math.PI),
                     var68,
                     var76,
                     var84,
                     var92,
                     var99,
                     var105
                  );
               }
            }

            if (!var9) {
               for (int var119 = 0; var119 < var6.embers.length; var119++) {
                  float[] var125 = var6.embers[var119];
                  float var129 = (var5 - var125[0]) / 0.9F;
                  if (!(var129 <= 0.0F) && !(var129 >= 1.0F)) {
                     piecePos(var6, var119 % SLICES.length, Math.min(var5, 3.45F), A);
                     DeathFx.sprite(
                        var60,
                        var2,
                        var4,
                        var3,
                        A[0] + var125[1],
                        A[1] + var125[3] * var129,
                        A[2] + var125[2],
                        0.14F * (1.0F - var129 * 0.5F),
                        2,
                        (var119 & 1) == 0 ? 16720960 : 16756800,
                        (float)Math.sin(var129 * Math.PI),
                        var68,
                        var76,
                        var84,
                        var92,
                        var99,
                        var105
                     );
                  }
               }
            }
         }
      }
   }

   private static int tint(float var0) {
      int var1 = Math.round(255.0F * (1.0F - 0.65F * var0));
      return -65536 | var1 << 8 | var1;
   }

   private static float restY(int var0) {
      return (SLICES[var0][0] + SLICES[var0][1]) / 2.0F * 0.05859375F;
   }

   private static void piecePos(DeathCards.State var0, int var1, float var2, float[] var3) {
      float[] var4 = var0.pieces[var1];
      float var5 = Math.max(0.0F, var2 - 2.05F);
      float var6 = restY(var1);
      float var7 = 0.14F;
      float var8 = (var4[1] + (float)Math.sqrt(var4[1] * var4[1] + 18.0F * Math.max(0.0F, var6 - var7))) / 9.0F;
      float var9 = Math.min(var5, var8);
      float var10 = var5 > var8 ? (1.0F - (float)Math.exp(-(var5 - var8) * 4.0F)) / 4.0F * 0.35F : 0.0F;
      var3[0] = var4[0] * (var9 + var10);
      var3[2] = var4[2] * (var9 + var10);
      var3[1] = var5 < var8 ? var6 + var4[1] * var9 - 4.5F * var9 * var9 : var7;
   }

   private static Matrix4f pieceMatrix(Matrix4f var0, DeathCards.State var1, int var2, float var3) {
      float var4 = 1.0F - DeathFx.sm(3.0F, 3.5F, var3);
      if (var4 <= 0.01F) {
         return null;
      }

      float[] var5 = var1.pieces[var2];
      piecePos(var1, var2, var3, B);
      float var6 = var3 - 2.05F;
      float var7 = restY(var2);
      float var8 = (var5[1] + (float)Math.sqrt(var5[1] * var5[1] + 18.0F * Math.max(0.0F, var7 - 0.14F))) / 9.0F;
      float var9 = var5[6] * Math.min(var6, var8) + var5[6] * 0.15F * (var6 > var8 ? 1.0F - (float)Math.exp(-(var6 - var8) * 5.0F) : 0.0F);
      return new Matrix4f(var0).translate(B[0], B[1], B[2]).rotate(var9, var5[3], var5[4], var5[5]).scale(var4).translate(0.0F, -var7, 0.0F);
   }

   private static void slice(VertexConsumer var0, Matrix4f var1, float[] var2, int var3, int var4, int var5, int var6) {
      float var7 = var2[1];
      float var8 = var2[1] + var2[3];
      float var9 = Math.max(var3, var7);
      float var10 = Math.min(var4, var8);
      if (!(var9 >= var10)) {
         Matrix4f var11 = new Matrix4f(var1).translate(var2[0] * 0.05859375F, (var7 + var2[3] / 2.0F) * 0.05859375F, 0.0F);
         box(var0, var11, var2[2], var2[3], var2[4], 0.0F, (int)var2[5], (int)var2[6], var9 - var7, var10 - var7, var5, var6);
         box(var0, var11, var2[2], var2[3], var2[4], var2[9], (int)var2[7], (int)var2[8], var9 - var7, var10 - var7, var5, var6);
      }
   }

   private static void caps(VertexConsumer var0, Matrix4f var1, float[] var2, int var3, int var4, int var5) {
      float var6 = var2[1];
      float var7 = var2[1] + var2[3];
      float var8 = Math.max(var3, var6);
      float var9 = Math.min(var4, var7);
      if (!(var8 >= var9)) {
         float var10 = var2[2] / 2.0F * 0.05859375F * 0.97F;
         float var11 = var2[4] / 2.0F * 0.05859375F * 0.97F;
         float var12 = var2[0] * 0.05859375F;
         if (var9 < var7) {
            float var13 = var9 * 0.05859375F;
            DeathFx.face(
               var0,
               var1,
               var12 - var10,
               var13,
               -var11,
               var12 + var10,
               var13,
               -var11,
               var12 + var10,
               var13,
               var11,
               var12 - var10,
               var13,
               var11,
               0.5F,
               0.5F,
               0.5F,
               0.5F,
               0.0F,
               1.0F,
               0.0F,
               var5,
               15728880
            );
         }

         if (var8 > var6) {
            float var14 = var8 * 0.05859375F;
            DeathFx.face(
               var0,
               var1,
               var12 - var10,
               var14,
               var11,
               var12 + var10,
               var14,
               var11,
               var12 + var10,
               var14,
               -var11,
               var12 - var10,
               var14,
               -var11,
               0.5F,
               0.5F,
               0.5F,
               0.5F,
               0.0F,
               -1.0F,
               0.0F,
               var5,
               15728880
            );
         }
      }
   }

   private static void box(
      VertexConsumer var0, Matrix4f var1, float var2, float var3, float var4, float var5, int var6, int var7, float var8, float var9, int var10, int var11
   ) {
      float var12 = (var2 / 2.0F + var5) * 0.05859375F;
      float var13 = (var4 / 2.0F + var5) * 0.05859375F;
      float var14 = (var8 - var3 / 2.0F) * 0.05859375F - (var8 <= 0.0F ? var5 * 0.05859375F : 0.0F);
      float var15 = (var9 - var3 / 2.0F) * 0.05859375F + (var9 >= var3 ? var5 * 0.05859375F : 0.0F);
      float var16 = 0.015625F;
      float var17 = (var7 + var4 + (var3 - var9)) * var16;
      float var18 = (var7 + var4 + (var3 - var8)) * var16;
      DeathFx.face(
         var0,
         var1,
         -var12,
         var15,
         var13,
         var12,
         var15,
         var13,
         var12,
         var14,
         var13,
         -var12,
         var14,
         var13,
         (var6 + var4) * var16,
         var17,
         (var6 + var4 + var2) * var16,
         var18,
         0.0F,
         0.0F,
         1.0F,
         var10,
         var11
      );
      DeathFx.face(
         var0,
         var1,
         var12,
         var15,
         -var13,
         -var12,
         var15,
         -var13,
         -var12,
         var14,
         -var13,
         var12,
         var14,
         -var13,
         (var6 + 2.0F * var4 + var2) * var16,
         var17,
         (var6 + 2.0F * var4 + 2.0F * var2) * var16,
         var18,
         0.0F,
         0.0F,
         -1.0F,
         var10,
         var11
      );
      DeathFx.face(
         var0,
         var1,
         -var12,
         var15,
         -var13,
         -var12,
         var15,
         var13,
         -var12,
         var14,
         var13,
         -var12,
         var14,
         -var13,
         var6 * var16,
         var17,
         (var6 + var4) * var16,
         var18,
         -1.0F,
         0.0F,
         0.0F,
         var10,
         var11
      );
      DeathFx.face(
         var0,
         var1,
         var12,
         var15,
         var13,
         var12,
         var15,
         -var13,
         var12,
         var14,
         -var13,
         var12,
         var14,
         var13,
         (var6 + var4 + var2) * var16,
         var17,
         (var6 + 2.0F * var4 + var2) * var16,
         var18,
         1.0F,
         0.0F,
         0.0F,
         var10,
         var11
      );
      if (var9 >= var3) {
         DeathFx.face(
            var0,
            var1,
            -var12,
            var15,
            -var13,
            var12,
            var15,
            -var13,
            var12,
            var15,
            var13,
            -var12,
            var15,
            var13,
            (var6 + var4) * var16,
            var7 * var16,
            (var6 + var4 + var2) * var16,
            (var7 + var4) * var16,
            0.0F,
            1.0F,
            0.0F,
            var10,
            var11
         );
      }

      if (var8 <= 0.0F) {
         DeathFx.face(
            var0,
            var1,
            -var12,
            var14,
            var13,
            var12,
            var14,
            var13,
            var12,
            var14,
            -var13,
            -var12,
            var14,
            -var13,
            (var6 + var4 + var2) * var16,
            var7 * var16,
            (var6 + var4 + 2.0F * var2) * var16,
            (var7 + var4) * var16,
            0.0F,
            -1.0F,
            0.0F,
            var10,
            var11
         );
      }
   }

   private static void cardQuad(VertexConsumer var0, Matrix4f var1, int var2) {
      float[] var3 = uv(var2);
      float[] var4 = uv(0);
      DeathFx.face(
         var0,
         var1,
         -0.21F,
         0.006F,
         -0.29F,
         0.21F,
         0.006F,
         -0.29F,
         0.21F,
         0.006F,
         0.29F,
         -0.21F,
         0.006F,
         0.29F,
         var3[0],
         var3[1],
         var3[2],
         var3[3],
         0.0F,
         1.0F,
         0.0F,
         -1,
         15728880
      );
      DeathFx.face(
         var0,
         var1,
         0.21F,
         -0.006F,
         -0.29F,
         -0.21F,
         -0.006F,
         -0.29F,
         -0.21F,
         -0.006F,
         0.29F,
         0.21F,
         -0.006F,
         0.29F,
         var4[0],
         var4[1],
         var4[2],
         var4[3],
         0.0F,
         -1.0F,
         0.0F,
         -1,
         15728880
      );
   }

   private static void chip(VertexConsumer var0, Matrix4f var1, int var2, int var3) {
      float var4 = 0.075F;
      float var5 = 0.014F;
      int var6 = DeathFx.mix(var2 & 16777215, 16777215, 0.55F);

      for (int var7 = 0; var7 < 8; var7++) {
         double var8 = var7 * Math.PI / 4.0;
         double var10 = (var7 + 1) * Math.PI / 4.0;
         float var12 = (float)Math.cos(var8) * var4;
         float var13 = (float)Math.sin(var8) * var4;
         float var14 = (float)Math.cos(var10) * var4;
         float var15 = (float)Math.sin(var10) * var4;
         int var16 = (var7 & 1) == 0 ? var6 : var2;
         float var17 = (float)Math.cos((var8 + var10) / 2.0);
         float var18 = (float)Math.sin((var8 + var10) / 2.0);
         DeathFx.face(
            var0,
            var1,
            var12,
            var5,
            var13,
            var14,
            var5,
            var15,
            var14,
            -var5,
            var15,
            var12,
            -var5,
            var13,
            0.5F,
            0.5F,
            0.5F,
            0.5F,
            var17,
            0.0F,
            var18,
            var16,
            var3
         );
         DeathFx.tri(
            var0, var1, 0.0F, var5, 0.0F, var14, var5, var15, var12, var5, var13, var7 % 2 == 0 ? var2 : DeathFx.mix(var2 & 16777215, 16777215, 0.15F), var3
         );
         DeathFx.tri(var0, var1, 0.0F, -var5, 0.0F, var12, -var5, var13, var14, -var5, var15, var2, var3);
      }
   }

   private static void ribbon(
      VertexConsumer var0,
      Matrix4f var1,
      float var2,
      float var3,
      float var4,
      float var5,
      float var6,
      float var7,
      float var8,
      float var9,
      int var10,
      float var11,
      float var12
   ) {
      float var13 = var5 - var2;
      float var14 = var7 - var4;
      float var15 = (float)Math.sqrt(var13 * var13 + var14 * var14);
      if (!(var15 < 1.0E-5F) && (!(var11 <= 0.003F) || !(var12 <= 0.003F))) {
         float var16 = -var14 / var15;
         float var17 = var13 / var15;
         int var18 = DeathFx.argb(var10, var11);
         int var19 = DeathFx.argb(var10, var12);
         DeathFx.GP[0] = var2 + var16 * var8 / 2.0F;
         DeathFx.GP[1] = var3;
         DeathFx.GP[2] = var4 + var17 * var8 / 2.0F;
         DeathFx.GP[3] = var5 + var16 * var9 / 2.0F;
         DeathFx.GP[4] = var6;
         DeathFx.GP[5] = var7 + var17 * var9 / 2.0F;
         DeathFx.GP[6] = var5 - var16 * var9 / 2.0F;
         DeathFx.GP[7] = var6;
         DeathFx.GP[8] = var7 - var17 * var9 / 2.0F;
         DeathFx.GP[9] = var2 - var16 * var8 / 2.0F;
         DeathFx.GP[10] = var3;
         DeathFx.GP[11] = var4 - var17 * var8 / 2.0F;
         DeathFx.gquad(var0, var1, DeathFx.GP, var18, var19, var19, var18);
         DeathFx.GP[0] = var2;
         DeathFx.GP[1] = var3 + var8 * 0.25F;
         DeathFx.GP[2] = var4;
         DeathFx.GP[3] = var5;
         DeathFx.GP[4] = var6 + var9 * 0.25F;
         DeathFx.GP[5] = var7;
         DeathFx.GP[6] = var5;
         DeathFx.GP[7] = var6 - var9 * 0.25F;
         DeathFx.GP[8] = var7;
         DeathFx.GP[9] = var2;
         DeathFx.GP[10] = var3 - var8 * 0.25F;
         DeathFx.GP[11] = var4;
         DeathFx.gquad(var0, var1, DeathFx.GP, var18, var19, var19, var18);
      }
   }

   private static void cross(
      VertexConsumer var0, Matrix4f var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, int var9, float var10
   ) {
      ribbon(var0, var1, var2, var3, var4, var5, var6, var7, var8, var8, var9, var10, var10);
      float var11 = var5 - var2;
      float var12 = var7 - var4;
      float var13 = (float)Math.sqrt(var11 * var11 + var12 * var12);
      if (!(var13 < 1.0E-5F)) {
         int var14 = DeathFx.argb(var9, var10);
         DeathFx.GP[0] = var2;
         DeathFx.GP[1] = var3 + var8 / 2.0F;
         DeathFx.GP[2] = var4;
         DeathFx.GP[3] = var5;
         DeathFx.GP[4] = var6 + var8 / 2.0F;
         DeathFx.GP[5] = var7;
         DeathFx.GP[6] = var5;
         DeathFx.GP[7] = var6 - var8 / 2.0F;
         DeathFx.GP[8] = var7;
         DeathFx.GP[9] = var2;
         DeathFx.GP[10] = var3 - var8 / 2.0F;
         DeathFx.GP[11] = var4;
         DeathFx.gquad(var0, var1, DeathFx.GP, var14, var14, var14, var14);
      }
   }

   private static void ringFlat(VertexConsumer var0, Matrix4f var1, float var2, float var3, float var4, float var5, float var6, int var7, float var8) {
      DeathFx.disc(var0, var1, var2, var3, var4, var5, var6, var7, 0.0F, var7, var8, 40);
      DeathFx.disc(var0, var1, var2, var3, var4, var6, var6 * 1.05F, var7, var8, var7, 0.0F, 40);
   }

   private static float[] uv(int var0) {
      int var1 = var0 % 6;
      int var2 = var0 / 6;
      float var3 = 2 + var1 * 84;
      float var4 = 4 + var2 * 124;
      return new float[]{var3 / 512.0F, var4 / 256.0F, (var3 + 80.0F) / 512.0F, (var4 + 112.0F) / 256.0F};
   }

   private static boolean cardsTex() {
      if (cardsReady) {
         return true;
      }

      if (cardsFailed) {
         return false;
      }

      try {
         BufferedImage var0 = new BufferedImage(512, 256, 2);
         Graphics2D var1 = var0.createGraphics();
         var1.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
         var1.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
         String[] var2 = new String[]{"", "A", "K", "Q", "J", "10", "7"};
         int[] var3 = new int[]{0, 0, 1, 2, 3, 1, 0};

         for (int var4 = 0; var4 < 7; var4++) {
            int var5 = var4 % 6;
            int var6 = var4 / 6;
            int var7 = 2 + var5 * 84;
            int var8 = 4 + var6 * 124;
            if (var4 == 0) {
               back(var1, var7, var8);
            } else {
               front(var1, var7, var8, var2[var4], var3[var4]);
            }
         }

         var1.dispose();
         ByteArrayOutputStream var10 = new ByteArrayOutputStream();
         ImageIO.write(var0, "png", var10);
         MinecraftClient.getInstance()
            .getTextureManager()
            .registerTexture(CARDS, new NativeImageBackedTexture(() -> "neonlyrics_cards", NativeImage.read(var10.toByteArray())));
         cardsReady = true;
      } catch (Throwable var9) {
         NeonLyrics.LOG.warn("Karten-Bild konnte nicht gemalt werden", var9);
         cardsFailed = true;
      }

      return cardsReady;
   }

   private static void back(Graphics2D var0, int var1, int var2) {
      java.awt.geom.RoundRectangle2D.Float var3 = new java.awt.geom.RoundRectangle2D.Float(var1, var2, 80.0F, 112.0F, 12.0F, 12.0F);
      var0.setColor(new Color(9309986));
      var0.fill(var3);
      java.awt.geom.RoundRectangle2D.Float var4 = new java.awt.geom.RoundRectangle2D.Float(var1 + 6, var2 + 6, 68.0F, 100.0F, 8.0F, 8.0F);
      Shape var5 = var0.getClip();
      var0.setClip(var4);
      var0.setColor(new Color(217, 180, 74, 70));
      var0.setStroke(new BasicStroke(1.4F));

      for (int var6 = -112; var6 < 192; var6 += 9) {
         var0.drawLine(var1 + var6, var2, var1 + var6 + 112, var2 + 112);
         var0.drawLine(var1 + var6, var2 + 112, var1 + var6 + 112, var2);
      }

      var0.setClip(var5);
      var0.setColor(new Color(14267466));
      var0.setStroke(new BasicStroke(2.6F));
      var0.draw(var4);
      float var8 = var1 + 40.0F;
      float var7 = var2 + 56.0F;
      var0.fill(new java.awt.geom.Ellipse2D.Float(var8 - 17.0F, var7 - 17.0F, 34.0F, 34.0F));
      var0.setColor(new Color(1380376));
      var0.fill(suitShape(0, var8, var7, 11.0F));
   }

   private static void front(Graphics2D var0, int var1, int var2, String var3, int var4) {
      java.awt.geom.RoundRectangle2D.Float var5 = new java.awt.geom.RoundRectangle2D.Float(var1, var2, 80.0F, 112.0F, 12.0F, 12.0F);
      var0.setColor(new Color(16183783));
      var0.fill(var5);
      var0.setColor(new Color(13616820));
      var0.setStroke(new BasicStroke(2.0F));
      var0.draw(new java.awt.geom.RoundRectangle2D.Float(var1 + 1, var2 + 1, 78.0F, 110.0F, 11.0F, 11.0F));
      Color var6 = var4 != 1 && var4 != 2 ? new Color(1447452) : new Color(13111342);
      var0.setColor(var6);
      var0.setFont(new Font("SansSerif", 1, var3.length() > 1 ? 17 : 20));
      var0.drawString(var3, var1 + 7, var2 + 22);
      var0.fill(suitShape(var4, var1 + 14, var2 + 34, 6.0F));
      AffineTransform var7 = var0.getTransform();
      var0.rotate(Math.PI, var1 + 40.0, var2 + 56.0);
      var0.drawString(var3, var1 + 7, var2 + 22);
      var0.fill(suitShape(var4, var1 + 14, var2 + 34, 6.0F));
      var0.setTransform(var7);
      var0.fill(suitShape(var4, var1 + 40.0F, var2 + 56.0F, var4 == 0 && var3.equals("A") ? 22.0F : 17.0F));
   }

   private static Shape suitShape(int var0, float var1, float var2, float var3) {
      java.awt.geom.Path2D.Float var4 = new java.awt.geom.Path2D.Float();
      switch (var0) {
         case 1:
            var4.moveTo(var1, var2 + var3 * 0.9F);
            var4.curveTo(var1 - var3 * 1.25F, var2 + var3 * 0.05F, var1 - var3 * 0.65F, var2 - var3 * 1.0F, var1, var2 - var3 * 0.35F);
            var4.curveTo(var1 + var3 * 0.65F, var2 - var3 * 1.0F, var1 + var3 * 1.25F, var2 + var3 * 0.05F, var1, var2 + var3 * 0.9F);
            break;
         case 2:
            var4.moveTo(var1, var2 - var3);
            var4.lineTo(var1 + var3 * 0.72F, var2);
            var4.lineTo(var1, var2 + var3);
            var4.lineTo(var1 - var3 * 0.72F, var2);
            break;
         case 3:
            var4.append(new java.awt.geom.Ellipse2D.Float(var1 - var3 * 0.42F, var2 - var3 * 0.95F, var3 * 0.84F, var3 * 0.84F), false);
            var4.append(new java.awt.geom.Ellipse2D.Float(var1 - var3 * 0.92F, var2 - var3 * 0.3F, var3 * 0.84F, var3 * 0.84F), false);
            var4.append(new java.awt.geom.Ellipse2D.Float(var1 + var3 * 0.08F, var2 - var3 * 0.3F, var3 * 0.84F, var3 * 0.84F), false);
            var4.moveTo(var1, var2);
            var4.lineTo(var1 - var3 * 0.32F, var2 + var3 * 0.95F);
            var4.lineTo(var1 + var3 * 0.32F, var2 + var3 * 0.95F);
            break;
         default:
            var4.moveTo(var1, var2 - var3 * 0.95F);
            var4.curveTo(var1 + var3 * 1.25F, var2 - var3 * 0.1F, var1 + var3 * 0.65F, var2 + var3 * 0.8F, var1, var2 + var3 * 0.25F);
            var4.curveTo(var1 - var3 * 0.65F, var2 + var3 * 0.8F, var1 - var3 * 1.25F, var2 - var3 * 0.1F, var1, var2 - var3 * 0.95F);
            var4.moveTo(var1, var2 + var3 * 0.1F);
            var4.lineTo(var1 - var3 * 0.36F, var2 + var3 * 0.95F);
            var4.lineTo(var1 + var3 * 0.36F, var2 + var3 * 0.95F);
      }

      var4.closePath();
      return var4;
   }

   static {
      float var0 = 0.0F;

      for (int var1 = 0; var1 < TT.length; var1++) {
         TT[var1] = var0;
         float var2 = var1 / 1000.0F;
         var0 += (1.0F - 0.72F * DeathFx.sm(1.97F, 2.07F, var2) * (1.0F - DeathFx.sm(2.55F, 2.8F, var2))) / 1000.0F;
      }

      SLASH_PX = new int[]{24, 12, 18, 6, 18, 24, 6, 12};
      SLICES = new int[][]{{0, 6}, {6, 12}, {12, 18}, {18, 24}, {24, 32}};
      PARTS = new float[][]{
         {-2.0F, 0.0F, 4.0F, 12.0F, 4.0F, 0.0F, 16.0F, 0.0F, 32.0F, 0.25F},
         {2.0F, 0.0F, 4.0F, 12.0F, 4.0F, 16.0F, 48.0F, 0.0F, 48.0F, 0.25F},
         {0.0F, 12.0F, 8.0F, 12.0F, 4.0F, 16.0F, 16.0F, 16.0F, 32.0F, 0.25F},
         {-6.0F, 12.0F, 4.0F, 12.0F, 4.0F, 40.0F, 16.0F, 40.0F, 32.0F, 0.25F},
         {6.0F, 12.0F, 4.0F, 12.0F, 4.0F, 32.0F, 48.0F, 48.0F, 48.0F, 0.25F},
         {0.0F, 24.0F, 8.0F, 8.0F, 8.0F, 0.0F, 0.0F, 32.0F, 0.0F, 0.5F}
      };
      A = new float[3];
      B = new float[3];
      S0 = new float[3];
      LAND = new float[][]{{-0.17F, 0.02F, 0.05F, 0.35F}, {0.16F, 0.028F, -0.05F, -0.28F}};
      C = new float[6];
      C2 = new float[6];
      CARDS = Identifier.of("neonlyrics", "cosmetic/cards");
   }

   private static final class State {
      int[] face = new int[8];
      float[][] pieces = new float[DeathCards.SLICES.length][];
      float[][] hits = new float[56][];
      float[][] burst = new float[30][];
      float[][] chips = new float[16][];
      float[][] sparks = new float[40][];
      float[][] embers = new float[DeathCards.SLICES.length * 8][];
   }
}
