package dev.neonlyrics.cosmetic;

import dev.neonlyrics.NeonLyrics;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.zip.GZIPInputStream;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryUtil;

final class CarModel {
   private static final byte M_GLASS = 3;
   private static final byte M_GLOW = 4;
   private static final float FAR = 9.0F;
   private static final int SLICES = 12;
   private static CarModel.Mesh NEAR;
   private static CarModel.Mesh FARM;
   private static int frame;
   private static boolean tried;
   static final float[][] DRL = new float[2][];
   static final float[][] TAIL = new float[2][];
   private static boolean hazard;
   private static boolean blinkOn;
   private static boolean flash;
   private static boolean visibleNow;
   private static CarModel.Mesh current;

   private CarModel() {
   }

   static void lights(boolean var0, boolean var1, boolean var2) {
      hazard = var0;
      blinkOn = var1;
      flash = var2;
   }

   static synchronized boolean ready() {
      if (!tried) {
         tried = true;

         try {
            NEAR = load("/assets/neonlyrics/models/m3.bin.gz", true);
            FARM = load("/assets/neonlyrics/models/m3_far.bin.gz", false);
         } catch (Throwable var1) {
            NeonLyrics.LOG.warn("Auto-Modell nicht geladen", var1);
            NEAR = null;
         }
      }

      return NEAR != null;
   }

   private static CarModel.Mesh load(String var0, boolean var1) throws Exception {
      try (InputStream var2 = CarModel.class.getResourceAsStream(var0)) {
         if (var2 == null) {
            return null;
         }

         DataInputStream var3 = new DataInputStream(new BufferedInputStream(new GZIPInputStream(var2), 65536));
         byte[] var4 = new byte[4];
         var3.readFully(var4);
         if (var4[0] != 78 || var4[1] != 76 || var4[2] != 67) {
            throw new IllegalStateException("kein Auto-Modell");
         }

         boolean var5 = var4[3] == 50;
         int var6 = var3.readInt();
         int var7 = var3.readInt();
         CarModel.Mesh var8 = new CarModel.Mesh();
         float[] var9 = new float[var6 * 6];
         int[] var10 = new int[var6];
         byte[] var11 = new byte[var6];
         boolean[] var12 = new boolean[var6];

         for (int var13 = 0; var13 < var6; var13++) {
            for (int var14 = 0; var14 < 6; var14++) {
               var9[var13 * 6 + var14] = var3.readFloat();
            }

            var10[var13] = var3.readInt();
            var11[var13] = var3.readByte();
            var12[var13] = var3.readByte() != 0;
         }

         int[] var29 = new int[var7 * 4];

         for (int var30 = 0; var30 < var7; var30++) {
            var29[var30 * 4] = var3.readInt();
            var29[var30 * 4 + 1] = var3.readInt();
            var29[var30 * 4 + 2] = var3.readInt();
            var29[var30 * 4 + 3] = var5 ? var3.readInt() : var29[var30 * 4 + 2];
         }

         float[] var31 = new float[var7 * 3];
         boolean[] var15 = new boolean[var7];

         for (int var16 = 0; var16 < var7; var16++) {
            int var17 = var29[var16 * 4] * 6;
            int var18 = var29[var16 * 4 + 1] * 6;
            int var19 = var29[var16 * 4 + 2] * 6;
            float var20 = var9[var18] - var9[var17];
            float var21 = var9[var18 + 1] - var9[var17 + 1];
            float var22 = var9[var18 + 2] - var9[var17 + 2];
            float var23 = var9[var19] - var9[var17];
            float var24 = var9[var19 + 1] - var9[var17 + 1];
            float var25 = var9[var19 + 2] - var9[var17 + 2];
            var31[var16 * 3] = var21 * var25 - var22 * var24;
            var31[var16 * 3 + 1] = var22 * var23 - var20 * var25;
            var31[var16 * 3 + 2] = var20 * var24 - var21 * var23;
            var15[var16] = var12[var29[var16 * 4]] || var11[var29[var16 * 4]] == 3 || var11[var29[var16 * 4]] == 4;
         }

         if (var1) {
            float[][] var32 = new float[4][4];

            for (int var35 = 0; var35 < var6; var35++) {
               if (var11[var35] == 4) {
                  int var39 = (var9[var35 * 6 + 2] > 0.0F ? 0 : 2) + (var9[var35 * 6] > 0.0F ? 0 : 1);

                  for (int var43 = 0; var43 < 3; var43++) {
                     var32[var39][var43] = var32[var39][var43] + var9[var35 * 6 + var43];
                  }

                  var32[var39][3]++;
               }
            }

            for (int var36 = 0; var36 < 4; var36++) {
               float var40 = Math.max(1.0F, var32[var36][3]);
               float[] var44 = new float[]{var32[var36][0] / var40, var32[var36][1] / var40, var32[var36][2] / var40};
               if (var36 < 2) {
                  DRL[var36] = var32[var36][3] > 0.0F ? var44 : null;
               } else {
                  TAIL[var36 - 2] = var32[var36][3] > 0.0F ? var44 : null;
               }
            }
         }

         var8.p = var9;
         var8.col = var10;
         var8.mat = var11;
         var8.two = var15;
         var8.q = var29;
         var8.fn = var31;
         var8.sh = new float[var6 * 4];
         var8.stamp = new int[var6];
         int var33 = 0;
         int var37 = 0;
         int var41 = 0;

         for (int var45 = 0; var45 < var7; var45++) {
            byte var48 = var11[var29[var45 * 4]];
            if (var48 == 3) {
               var37++;
            } else if (var48 == 4) {
               var41++;
            } else {
               var33++;
            }
         }

         var8.bodyQ = new int[var33];
         var8.glassQ = new int[var37];
         var8.glowQ = new int[var41];
         var41 = 0;
         var37 = 0;
         var33 = 0;

         for (int var46 = 0; var46 < var7; var46++) {
            byte var49 = var11[var29[var46 * 4]];
            if (var49 == 3) {
               var8.glassQ[var37++] = var46;
            } else if (var49 == 4) {
               var8.glowQ[var41++] = var46;
            } else {
               var8.bodyQ[var33++] = var46;
            }
         }

         NeonLyrics.LOG.info("Auto-Modell geladen ({}): {} Ecken, {} Flaechen", new Object[]{var0, var6, var7});
         return var8;
      }
   }

   static void draw(VertexConsumer var0, boolean var1, Matrix4f var2, Matrix4f var3, int var4, List<float[]> var5, Matrix4f var6) {
      float var7 = 0.32F + 0.68F * Math.max(var4 >> 4 & 15, var4 >> 20 & 15) / 15.0F;
      Vector3f var8 = new Matrix4f(var3).invert().transformPosition(new Vector3f());
      boolean var9 = CarGpu.ok();
      if (!var1) {
         visibleNow = inView(var3);
         current = !var9 && FARM != null && var8.length() > 9.0F ? FARM : NEAR;
      }

      if (visibleNow && current != null) {
         CarModel.Mesh var10 = current;
         Vector3f var11 = new Vector3f();
         Vector3f var12 = new Vector3f();
         int[] var13;
         if (var1) {
            var13 = var10.glassQ;
         } else if (var9) {
            try {
               long var14 = System.currentTimeMillis();
               long var16 = System.nanoTime();
               if (var10.inst != null && !(var10.bakeBl < 0.0F)) {
                  float var18 = var8.distance(var10.bakeCam);
                  if (var10.stage == 0 && (var18 > 0.5F || Math.abs(var7 - var10.bakeBl) > 0.03F) && var14 - var10.bakeAt > 200L) {
                     var10.stage = 1;
                     var10.bakeCam.set(var8);
                     var10.bakeBl = var7;
                  }

                  if (var10.stage > 0) {
                     int var19 = var10.bodyV.length;
                     int var20 = var10.stage - 1;
                     if (var20 < 12) {
                        shadeSlice(var10, var3, var7, var19 * var20 / 12, var19 * (var20 + 1) / 12);
                        writeColorSlice(var10, var19 * var20 / 12, var19 * (var20 + 1) / 12);
                        var10.stage++;
                     } else {
                        CarGpu.upload(var10.inst.length);
                        var10.stage = 0;
                        var10.bakeAt = var14;
                     }
                  }
               } else {
                  if (var10.inst == null) {
                     buildInst(var10, CarGpu.culls());
                  }

                  shadeSlice(var10, var3, var7, 0, var10.bodyV.length);
                  writeAll(var10);
                  var10.bakeCam.set(var8);
                  var10.bakeBl = var7;
                  var10.bakeAt = var14;
               }

               if (Boolean.getBoolean("neonlyrics.selftest") && System.nanoTime() - var16 > 4000000L) {
                  NeonLyrics.LOG.info("SELFTEST Auto Puffer: {} ms", (System.nanoTime() - var16) / 1000000.0);
               }

               CarGpu.draw(var2);
               var13 = var10.glowQ;
            } catch (Throwable var31) {
               CarGpu.fail(var31);
               var13 = null;
            }

            if (var13 == null) {
               var13 = allNonGlass(var10);
            }
         } else {
            var13 = allNonGlass(var10);
         }

         if (!var1) {
            var10.reuse = var10.shadedOnce && var8.distanceSquared(var10.lastCam) < 1.0E-5F && Math.abs(var7 - var10.lastBl) < 0.001F;
            var10.lastCam.set(var8);
            var10.lastBl = var7;
            var10.shadedOnce = true;
         }

         int var32 = ++frame;
         float[] var15 = var10.p;
         float[] var33 = var10.fn;
         int[] var17 = var10.q;
         boolean[] var34 = var10.two;

         for (int var22 : var13) {
            int var23 = var17[var22 * 4];
            int var24 = var17[var22 * 4 + 1];
            int var25 = var17[var22 * 4 + 2];
            int var26 = var17[var22 * 4 + 3];
            float var27 = var8.x - var15[var23 * 6];
            float var28 = var8.y - var15[var23 * 6 + 1];
            float var29 = var8.z - var15[var23 * 6 + 2];
            boolean var30 = var33[var22 * 3] * var27 + var33[var22 * 3 + 1] * var28 + var33[var22 * 3 + 2] * var29 > 0.0F;
            if (var30 || var34[var22]) {
               shadeVertex(var10, var23, var32, var3, var11, var12, var7);
               shadeVertex(var10, var24, var32, var3, var11, var12, var7);
               shadeVertex(var10, var25, var32, var3, var11, var12, var7);
               if (var26 != var25) {
                  shadeVertex(var10, var26, var32, var3, var11, var12, var7);
               }

               emit(var0, var2, var10, var23);
               emit(var0, var2, var10, var30 ? var24 : var26);
               emit(var0, var2, var10, var25);
               emit(var0, var2, var10, var30 ? var26 : var24);
            }
         }

         if (!var1) {
            float var36 = frontLevel();
            float var38 = rearLevel();

            for (float[] var54 : DRL) {
               if (var54 != null) {
                  glow(var5, var6, var54, 0.06F, 0.42F, 16761370, 0.45F * var36);
               }
            }

            for (float[] var55 : TAIL) {
               if (var55 != null) {
                  glow(var5, var6, var55, -0.06F, 0.34F, 16720438, 0.35F * var38);
               }
            }

            if (hazard && blinkOn) {
               for (float[] var56 : DRL) {
                  if (var56 != null) {
                     glow(var5, var6, new float[]{var56[0] * 1.12F, var56[1], var56[2]}, 0.08F, 0.62F, 16751120, 0.85F);
                     glow(var5, var6, new float[]{Math.signum(var56[0]) * 1.2F, var56[1] + 0.02F, 1.35F}, 0.0F, 0.28F, 16751120, 0.8F);
                  }
               }

               for (float[] var57 : TAIL) {
                  if (var57 != null) {
                     glow(var5, var6, new float[]{var57[0] * 1.1F, var57[1], var57[2]}, -0.08F, 0.58F, 16751120, 0.85F);
                  }
               }
            }

            if (flash) {
               for (float[] var58 : DRL) {
                  if (var58 != null) {
                     glow(var5, var6, new float[]{var58[0] * 0.85F, var58[1] - 0.02F, var58[2]}, 0.14F, 1.25F, 16774880, 0.9F);
                  }
               }
            }
         }
      }
   }

   private static int[] allNonGlass(CarModel.Mesh var0) {
      if (var0.glowQ.length == 0) {
         return var0.bodyQ;
      }

      int[] var1 = Arrays.copyOf(var0.bodyQ, var0.bodyQ.length + var0.glowQ.length);
      System.arraycopy(var0.glowQ, 0, var1, var0.bodyQ.length, var0.glowQ.length);
      return var1;
   }

   private static void buildInst(CarModel.Mesh var0, boolean var1) {
      int[] var2 = var0.q;
      int var3 = 0;

      for (int var7 : var0.bodyQ) {
         var3 += var1 && var0.two[var7] ? 8 : 4;
      }

      int[] var24 = new int[var3];
      boolean[] var25 = new boolean[var0.col.length];
      int var26 = 0;
      var3 = 0;

      for (int var10 : var0.bodyQ) {
         int var11 = var2[var10 * 4];
         int var12 = var2[var10 * 4 + 1];
         int var13 = var2[var10 * 4 + 2];
         int var14 = var2[var10 * 4 + 3];
         var24[var3++] = var11;
         var24[var3++] = var12;
         var24[var3++] = var13;
         var24[var3++] = var14;
         if (var1 && var0.two[var10]) {
            var24[var3++] = var11;
            var24[var3++] = var14;
            var24[var3++] = var13;
            var24[var3++] = var12;
         }

         for (int var15 = 0; var15 < 4; var15++) {
            int var16 = var2[var10 * 4 + var15];
            if (!var25[var16]) {
               var25[var16] = true;
               var26++;
            }
         }
      }

      int[] var29 = new int[var26];
      var26 = 0;

      for (int var30 = 0; var30 < var25.length; var30++) {
         if (var25[var30]) {
            var29[var26++] = var30;
         }
      }

      int[] var31 = new int[var25.length + 1];

      for (int var39 : var24) {
         var31[var39 + 1]++;
      }

      for (int var33 = 0; var33 < var25.length; var33++) {
         var31[var33 + 1] = var31[var33 + 1] + var31[var33];
      }

      int[] var34 = new int[var24.length];
      int[] var36 = (int[])var31.clone();
      int var38 = 0;

      while (var38 < var24.length) {
         var34[var36[var24[var38]]++] = var38++;
      }

      var0.inst = var24;
      var0.bodyV = var29;
      var0.instStart = var31;
      var0.instAt = var34;
   }

   private static void writeColorSlice(CarModel.Mesh var0, int var1, int var2) {
      long var3 = MemoryUtil.memAddress(CarGpu.staging());
      int var5 = CarGpu.stride();
      int var6 = CarGpu.offsets()[1];
      float[] var7 = var0.sh;

      for (int var8 = var1; var8 < var2; var8++) {
         int var9 = var0.bodyV[var8];
         int var10 = var9 * 4;
         int var11 = (int)(Math.min(1.0F, var7[var10]) * 255.0F + 0.5F);
         int var12 = (int)(Math.min(1.0F, var7[var10 + 1]) * 255.0F + 0.5F);
         int var13 = (int)(Math.min(1.0F, var7[var10 + 2]) * 255.0F + 0.5F);
         int var14 = (int)(Math.min(1.0F, var7[var10 + 3]) * 255.0F + 0.5F);
         int var15 = var11 | var12 << 8 | var13 << 16 | var14 << 24;

         for (int var16 = var0.instStart[var9]; var16 < var0.instStart[var9 + 1]; var16++) {
            MemoryUtil.memPutInt(var3 + (long)var0.instAt[var16] * var5 + var6, var15);
         }
      }
   }

   private static void shadeSlice(CarModel.Mesh var0, Matrix4f var1, float var2, int var3, int var4) {
      Vector3f var5 = new Vector3f();
      Vector3f var6 = new Vector3f();
      float[] var7 = var0.p;

      for (int var8 = var3; var8 < var4; var8++) {
         int var9 = var0.bodyV[var8];
         int var10 = var9 * 6;
         int var11 = var0.col[var9];
         var1.transformPosition(var5.set(var7[var10], var7[var10 + 1], var7[var10 + 2]));
         var1.transformDirection(var6.set(var7[var10 + 3], var7[var10 + 4], var7[var10 + 5])).normalize();
         SportCar.shade(
            var0.mat[var9], (var11 >> 16 & 0xFF) / 255.0F, (var11 >> 8 & 0xFF) / 255.0F, (var11 & 0xFF) / 255.0F, var6, var5, var2, var0.sh, var9 * 4
         );
      }
   }

   private static void writeAll(CarModel.Mesh var0) {
      ByteBuffer var1 = CarGpu.begin(var0.inst.length);
      long var2 = MemoryUtil.memAddress(var1);
      int var4 = CarGpu.stride();
      int[] var5 = CarGpu.offsets();
      float[] var6 = var0.p;

      for (int var7 = 0; var7 < var0.inst.length; var7++) {
         long var8 = var2 + (long)var7 * var4;
         int var10 = var0.inst[var7] * 6;
         MemoryUtil.memPutFloat(var8 + var5[0], var6[var10]);
         MemoryUtil.memPutFloat(var8 + var5[0] + 4L, var6[var10 + 1]);
         MemoryUtil.memPutFloat(var8 + var5[0] + 8L, var6[var10 + 2]);
         MemoryUtil.memPutFloat(var8 + var5[2], 0.5F);
         MemoryUtil.memPutFloat(var8 + var5[2] + 4L, 0.5F);
         MemoryUtil.memPutShort(var8 + var5[3], (short)240);
         MemoryUtil.memPutShort(var8 + var5[3] + 2L, (short)240);
      }

      writeColors(var0);
   }

   private static void writeColors(CarModel.Mesh var0) {
      ByteBuffer var1 = CarGpu.staging();
      long var2 = MemoryUtil.memAddress(var1);
      int var4 = CarGpu.stride();
      int var5 = CarGpu.offsets()[1];
      float[] var6 = var0.sh;
      int[] var7 = new int[var0.col.length];

      for (int var11 : var0.bodyV) {
         int var12 = var11 * 4;
         int var13 = (int)(Math.min(1.0F, var6[var12]) * 255.0F + 0.5F);
         int var14 = (int)(Math.min(1.0F, var6[var12 + 1]) * 255.0F + 0.5F);
         int var15 = (int)(Math.min(1.0F, var6[var12 + 2]) * 255.0F + 0.5F);
         int var16 = (int)(Math.min(1.0F, var6[var12 + 3]) * 255.0F + 0.5F);
         var7[var11] = var13 | var14 << 8 | var15 << 16 | var16 << 24;
      }

      for (int var17 = 0; var17 < var0.inst.length; var17++) {
         MemoryUtil.memPutInt(var2 + (long)var17 * var4 + var5, var7[var0.inst[var17]]);
      }

      CarGpu.upload(var0.inst.length);
   }

   private static boolean inView(Matrix4f var0) {
      try {
         Camera var1 = MinecraftClient.getInstance().gameRenderer.getCamera();
         double var2 = Math.toRadians(var1.getYaw());
         double var4 = Math.toRadians(var1.getPitch());
         float var6 = (float)(-Math.sin(var2) * Math.cos(var4));
         float var7 = (float)(-Math.sin(var4));
         float var8 = (float)(Math.cos(var2) * Math.cos(var4));
         Vector3f var9 = var0.transformPosition(new Vector3f(0.0F, 0.7F, 0.0F));
         float var10 = var9.length();
         return var10 < 3.5F ? true : (var9.x * var6 + var9.y * var7 + var9.z * var8) / var10 > 0.05F - 3.2F / var10;
      } catch (Throwable var11) {
         return true;
      }
   }

   private static float frontLevel() {
      return !hazard ? 1.0F : (blinkOn ? 1.0F : 0.12F) + (flash ? 1.0F : 0.0F);
   }

   private static float rearLevel() {
      return !hazard ? 1.0F : (blinkOn ? 1.0F : 0.35F);
   }

   private static void glow(List<float[]> var0, Matrix4f var1, float[] var2, float var3, float var4, int var5, float var6) {
      Vector3f var7 = var1.transformPosition(new Vector3f(var2[0], var2[1], var2[2] + var3));
      var0.add(new float[]{var7.x, var7.y, var7.z, var4, Float.intBitsToFloat(var5), var6});
   }

   private static void shadeVertex(CarModel.Mesh var0, int var1, int var2, Matrix4f var3, Vector3f var4, Vector3f var5, float var6) {
      if (var0.stamp[var1] != var2) {
         var0.stamp[var1] = var2;
         if (!var0.reuse || var0.mat[var1] == 4) {
            float[] var7 = var0.p;
            float[] var8 = var0.sh;
            int var9 = var1 * 6;
            int var10 = var0.col[var1];
            var3.transformPosition(var4.set(var7[var9], var7[var9 + 1], var7[var9 + 2]));
            var3.transformDirection(var5.set(var7[var9 + 3], var7[var9 + 4], var7[var9 + 5])).normalize();
            SportCar.shade(
               var0.mat[var1], (var10 >> 16 & 0xFF) / 255.0F, (var10 >> 8 & 0xFF) / 255.0F, (var10 & 0xFF) / 255.0F, var5, var4, var6, var8, var1 * 4
            );
            if (var0.mat[var1] == 4 && hazard) {
               boolean var11 = var7[var9 + 2] > 0.0F;
               float var12 = Math.min(1.0F, var11 ? frontLevel() : rearLevel());
               float var13 = var11 && flash ? 1.0F : 0.0F;

               for (int var14 = 0; var14 < 3; var14++) {
                  var8[var1 * 4 + var14] = Math.min(1.0F, var8[var1 * 4 + var14] * var12 + var13);
               }
            }
         }
      }
   }

   private static void emit(VertexConsumer var0, Matrix4f var1, CarModel.Mesh var2, int var3) {
      float[] var4 = var2.p;
      float[] var5 = var2.sh;
      int var6 = var3 * 6;
      int var7 = var3 * 4;
      var0.vertex(var1, var4[var6], var4[var6 + 1], var4[var6 + 2])
         .color(var5[var7], var5[var7 + 1], var5[var7 + 2], var5[var7 + 3])
         .texture(0.5F, 0.5F)
         .overlay(OverlayTexture.DEFAULT_UV)
         .light(15728880)
         .normal(0.0F, 1.0F, 0.0F);
   }

   private static final class Mesh {
      float[] p;
      int[] col;
      byte[] mat;
      boolean[] two;
      int[] q;
      float[] fn;
      float[] sh;
      int[] stamp;
      boolean reuse;
      boolean shadedOnce;
      final Vector3f lastCam = new Vector3f(1.0E9F, 0.0F, 0.0F);
      float lastBl = -1.0F;
      int[] bodyQ;
      int[] glassQ;
      int[] glowQ;
      final Vector3f bakeCam = new Vector3f(1.0E9F, 0.0F, 0.0F);
      float bakeBl = -1.0F;
      long bakeAt;
      int[] inst;
      int[] bodyV;
      int stage;
      int[] instStart;
      int[] instAt;
   }
}
