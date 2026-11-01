package dev.neonlyrics.sky;

import dev.neonlyrics.NeonLyrics;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;

final class SkyAnim {
   private static Map<String, SkyAnim.Anim> anims;

   private SkyAnim() {
   }

   private static synchronized Map<String, SkyAnim.Anim> index() {
      if (anims != null) {
         return anims;
      }

      anims = new HashMap<>();

      try (InputStream var0 = SkyAnim.class.getResourceAsStream("/assets/neonlyrics/textures/sky/pano/anim.txt")) {
         if (var0 == null) {
            return anims;
         }

         BufferedReader var1 = new BufferedReader(new InputStreamReader(var0, StandardCharsets.UTF_8));

         String var2;
         while ((var2 = var1.readLine()) != null) {
            String[] var3 = var2.trim().split(" ");
            if (var3.length >= 8) {
               int var4 = Integer.parseInt(var3[5]);
               int[] var5 = new int[var4];
               int[] var6 = new int[var4];

               for (int var7 = 0; var7 < var4; var7++) {
                  String[] var8 = var3[8 + var7].split(",");
                  var5[var7] = Integer.parseInt(var8[0]);
                  var6[var7] = Integer.parseInt(var8[1]);
               }

               RenderLayer var13 = WorldSky.noFog(Identifier.of("neonlyrics", "textures/sky/pano/anim/" + var3[0] + ".png"));
               anims.put(
                  var3[0],
                  new SkyAnim.Anim(
                     Integer.parseInt(var3[1]),
                     Integer.parseInt(var3[2]),
                     Integer.parseInt(var3[3]),
                     Integer.parseInt(var3[4]),
                     var4,
                     Integer.parseInt(var3[6]),
                     Integer.parseInt(var3[7]),
                     var5,
                     var6,
                     var13
                  )
               );
            }
         }
      } catch (Exception var11) {
         NeonLyrics.LOG.warn("Himmel-Animation nicht geladen", var11);
      }

      return anims;
   }

   static void draw(Immediate var0, Matrix4f var1, String var2, double var3, float var5, double var6, double var8, double var10, double var12, double var14) {
      SkyAnim.Anim var16 = index().get(var2);
      if (var16 != null && !(var5 <= 0.01F)) {
         double var17 = (var3 % var16.frames + var16.frames) % var16.frames;
         int var19 = (int)Math.floor(var17);
         int var20 = (var19 + 1) % var16.frames;
         float var21 = (float)(var17 - var19);
         var21 = var21 * var21 * (3.0F - 2.0F * var21);
         VertexConsumer var22 = var0.getBuffer(var16.type);
         layer(var22, var1, var16, var19, var5, var6 * 0.996, var8, var10, var12, var14);
         if (var21 > 0.02F) {
            layer(var22, var1, var16, var20, var5 * var21, var6 * 0.995, var8, var10, var12, var14);
         }

         var0.draw(var16.type);
      }
   }

   private static void layer(
      VertexConsumer var0, Matrix4f var1, SkyAnim.Anim var2, int var3, float var4, double var5, double var7, double var9, double var11, double var13
   ) {
      float var15 = var2.cols * var2.tile;
      float var16 = var2.rows * var2.tile;
      int var17 = Math.round(Math.min(1.0F, var4) * 255.0F) << 24 | 16777215;

      for (int var18 = 0; var18 < var2.n; var18++) {
         double var19 = var2.tc[var18] * var2.tile * 360.0 / var2.w;
         double var21 = (var2.tc[var18] + 1) * var2.tile * 360.0 / var2.w;
         double var23 = 90.0 - (var2.tr[var18] + 1) * var2.tile * 120.0 / var2.h;
         double var25 = 90.0 - var2.tr[var18] * var2.tile * 120.0 / var2.h;
         if (!(var25 <= 0.0)) {
            double[] var27 = dir((var19 + var21) / 2.0, (var23 + var25) / 2.0);
            if (!(var27[0] * var7 + var27[1] * var9 + var27[2] * var11 < var13)) {
               int var28 = var3 * var2.n + var18;
               float var29 = (var28 % var2.cols * var2.tile + 0.5F) / var15;
               float var30 = (var28 / var2.cols * var2.tile + 0.5F) / var16;
               float var31 = var29 + (var2.tile - 1) / var15;
               float var32 = var30 + (var2.tile - 1) / var16;
               int var33 = var23 < 3.0 ? Math.round(var4 * 255.0F * (float)Math.max(0.0, var23 / 3.0)) << 24 | 16777215 : var17;
               double[] var34 = dir(var19, var23);
               double[] var35 = dir(var21, var23);
               double[] var36 = dir(var21, var25);
               double[] var37 = dir(var19, var25);
               v(var0, var1, var34, var5, var29, var32, var33);
               v(var0, var1, var35, var5, var31, var32, var33);
               v(var0, var1, var36, var5, var31, var30, var17);
               v(var0, var1, var37, var5, var29, var30, var17);
               v(var0, var1, var37, var5, var29, var30, var17);
               v(var0, var1, var36, var5, var31, var30, var17);
               v(var0, var1, var35, var5, var31, var32, var33);
               v(var0, var1, var34, var5, var29, var32, var33);
            }
         }
      }
   }

   private static void v(VertexConsumer var0, Matrix4f var1, double[] var2, double var3, float var5, float var6, int var7) {
      var0.vertex(var1, (float)(var2[0] * var3), (float)(var2[1] * var3), (float)(var2[2] * var3)).color(var7).texture(var5, var6).light(15728880);
   }

   private static double[] dir(double var0, double var2) {
      double var4 = Math.toRadians(var0);
      double var6 = Math.toRadians(var2);
      double var8 = Math.cos(var6);
      return new double[]{-Math.sin(var4) * var8, Math.sin(var6), Math.cos(var4) * var8};
   }

   private record Anim(int w, int h, int tile, int frames, int n, int cols, int rows, int[] tc, int[] tr, RenderLayer type) {
   }
}
