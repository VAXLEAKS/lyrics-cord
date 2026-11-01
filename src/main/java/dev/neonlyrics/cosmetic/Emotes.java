package dev.neonlyrics.cosmetic;

import dev.neonlyrics.NeonLyrics;
import dev.neonlyrics.lyrics.NowPlaying;
import dev.neonlyrics.lyrics.SpotifyClient;
import dev.neonlyrics.sky.SkyTextures;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.entity.Entity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public final class Emotes {
   public static final String[] IDS = new String[]{"lowrider", "money", "floss", "dab", "backflip", "wave", "guitar", "throne", "auto", "autowalk"};
   private static final float[] DURATION = new float[]{0.0F, 0.0F, 0.0F, 2.4F, 1.5F, 2.6F, 0.0F, 0.0F, 0.0F, 0.0F};
   private static int active = -1;
   private static long start;
   private static Perspective camBefore;
   private static SoundInstance music;
   private static float yaw;
   private static long lastNs;
   private static int lastBeat = -1;
   private static boolean landed;
   private static boolean lastBlink;
   private static final Random RND = new Random();
   private static final List<float[]> BILLS = new ArrayList<>();
   private static final List<float[]> SPARKS = new ArrayList<>();
   private static final int PARKED = 9;
   private static int profN;
   private static double carX;
   private static double carY;
   private static double carZ;
   private static float carYaw;
   private static World carLevel;
   private static final Emotes.Limbs L = new Emotes.Limbs();

   private Emotes() {
   }

   public static boolean active() {
      return active >= 0;
   }

   public static boolean hides(Entity var0) {
      return active >= 0 && active != 9 && var0 == MinecraftClient.getInstance().player;
   }

   public static void play(int var0) {
      MinecraftClient var1 = MinecraftClient.getInstance();
      ClientPlayerEntity var2 = var1.player;
      if (var2 != null && var0 >= 0 && var0 < IDS.length) {
         stop();
         active = var0;
         start = System.nanoTime();
         lastNs = start;
         lastBeat = -1;
         landed = false;
         yaw = (float)Math.toRadians(var2.bodyYaw);
         BILLS.clear();
         SPARKS.clear();
         if (var0 == 9) {
            double var5 = Math.toRadians(var2.getYaw());
            carX = var2.getX() - Math.sin(var5) * 3.4;
            carZ = var2.getZ() + Math.cos(var5) * 3.4;
            carY = ground(var1, carX, var2.getY() + 2.5, carZ, var2.getY());
            carYaw = (float)(var5 + (Math.PI / 2));
            carLevel = var1.world;
            sound(var1, SoundEvents.BLOCK_IRON_DOOR_CLOSE, 0.25F, 1.6F);
         } else {
            if (var1.options.getPerspective().isFirstPerson()) {
               camBefore = var1.options.getPerspective();
               var1.options.setPerspective(Perspective.THIRD_PERSON_BACK);
            }

            NowPlaying var3 = SpotifyClient.now();
            if (var0 == 0 && (!var3.hasTrack() || !var3.playing)) {
               music = PositionedSoundInstance.ui((SoundEvent)SoundEvents.MUSIC_DISC_PIGSTEP.value(), 1.0F, 0.8F);
               var1.getSoundManager().play(music);
            }

            if (var0 == 4) {
               sound(var1, SoundEvents.ENTITY_FIREWORK_ROCKET_LAUNCH, 0.6F, 1.4F);
            }
         }
      }
   }

   public static void stop() {
      MinecraftClient var0 = MinecraftClient.getInstance();
      if (music != null) {
         var0.getSoundManager().stop(music);
         music = null;
      }

      if (camBefore != null && var0.options.getPerspective() == Perspective.THIRD_PERSON_BACK) {
         var0.options.setPerspective(camBefore);
      }

      camBefore = null;
      active = -1;
   }

   private static void sound(MinecraftClient var0, SoundEvent var1, float var2, float var3) {
      try {
         var0.getSoundManager().play(PositionedSoundInstance.ui(var1, var3, var2));
      } catch (Throwable var5) {
      }
   }

   private static float time() {
      return (float)(System.nanoTime() - start) / 1.0E9F;
   }

   private static double beatPos(float var0) {
      NowPlaying var1 = SpotifyClient.now();
      return var1.hasTrack() && var1.playing ? var1.beatPos(var1.progressNow(NeonLyrics.cfg().lyricsOffsetMs)) : var0 * 85.0 / 60.0;
   }

   public static void tick(MinecraftClient var0) {
      if (active >= 0) {
         ClientPlayerEntity var1 = var0.player;
         if (var1 == null || var0.world == null || var1.isDead()) {
            stop();
         } else if (active == 9) {
            if (var0.world != carLevel || var1.squaredDistanceTo(carX, carY, carZ) > 4096.0) {
               stop();
            }
         } else {
            Vec2f var2 = var1.input.getMovementInput();
            boolean var3 = var2.x * var2.x + var2.y * var2.y > 0.01F || var1.input.playerInput.jump() || var1.hurtTime > 0 || var1.hasVehicle();
            float var4 = DURATION[active];
            if (var3 || var4 > 0.0F && time() > var4) {
               stop();
            }
         }
      }
   }

   static Matrix4f armR(Matrix4f var0, Emotes.Limbs var1) {
      return new Matrix4f(var0).translate(-0.3515625F, 0.234375F, 0.0F).rotateZ(var1.rArmZ).rotateX(var1.rArmX).translate(0.0F, -0.234375F, 0.0F);
   }

   static Matrix4f armL(Matrix4f var0, Emotes.Limbs var1) {
      return new Matrix4f(var0).translate(0.3515625F, 0.234375F, 0.0F).rotateZ(var1.lArmZ).rotateX(var1.lArmX).translate(0.0F, -0.234375F, 0.0F);
   }

   static void figure(VertexConsumer var0, Matrix4f var1, Emotes.Limbs var2, int var3, int var4) {
      DeathFx.skinBox(var0, var1, 8.0F, 12.0F, 4.0F, 0.0F, 16, 16, var3, var4);
      DeathFx.skinBox(var0, var1, 8.0F, 12.0F, 4.0F, 0.25F, 16, 32, var3, var4);
      Matrix4f var5 = new Matrix4f(var1)
         .translate(0.0F, 0.3515625F, 0.0F)
         .rotateY(var2.headY)
         .rotateX(var2.headX)
         .rotateZ(var2.headZ)
         .translate(0.0F, 0.234375F, 0.0F);
      DeathFx.skinBox(var0, var5, 8.0F, 8.0F, 8.0F, 0.0F, 0, 0, var3, var4);
      DeathFx.skinBox(var0, var5, 8.0F, 8.0F, 8.0F, 0.5F, 32, 0, var3, var4);
      Matrix4f var6 = armR(var1, var2);
      DeathFx.skinBox(var0, var6, 4.0F, 12.0F, 4.0F, 0.0F, 40, 16, var3, var4);
      DeathFx.skinBox(var0, var6, 4.0F, 12.0F, 4.0F, 0.25F, 40, 32, var3, var4);
      Matrix4f var7 = armL(var1, var2);
      DeathFx.skinBox(var0, var7, 4.0F, 12.0F, 4.0F, 0.0F, 32, 48, var3, var4);
      DeathFx.skinBox(var0, var7, 4.0F, 12.0F, 4.0F, 0.25F, 48, 48, var3, var4);
      Matrix4f var8 = new Matrix4f(var1).translate(-0.1171875F, -0.3515625F, 0.0F).rotateZ(var2.rLegZ).rotateX(var2.rLegX).translate(0.0F, -0.3515625F, 0.0F);
      DeathFx.skinBox(var0, var8, 4.0F, 12.0F, 4.0F, 0.0F, 0, 16, var3, var4);
      DeathFx.skinBox(var0, var8, 4.0F, 12.0F, 4.0F, 0.25F, 0, 32, var3, var4);
      Matrix4f var9 = new Matrix4f(var1).translate(0.1171875F, -0.3515625F, 0.0F).rotateZ(var2.lLegZ).rotateX(var2.lLegX).translate(0.0F, -0.3515625F, 0.0F);
      DeathFx.skinBox(var0, var9, 4.0F, 12.0F, 4.0F, 0.0F, 16, 48, var3, var4);
      DeathFx.skinBox(var0, var9, 4.0F, 12.0F, 4.0F, 0.25F, 0, 48, var3, var4);
   }

   public static void render(MinecraftClient var0, VertexConsumerProvider var1, Matrix4f var2, Vec3d var3, float var4) {
      if (active >= 0 && var0.player != null && Accessories.whiteTex()) {
         try {
            draw(var0, var1, var2, var3, var4);
         } catch (Throwable var6) {
            NeonLyrics.LOG.warn("Emote abgebrochen", var6);
            stop();
         }
      }
   }

   private static double ground(MinecraftClient var0, double var1, double var3, double var5, double var7) {
      Mutable var9 = new Mutable();
      int var10 = (int)Math.floor(var1);
      int var11 = (int)Math.floor(var5);
      int var12 = (int)Math.floor(var3);

      for (int var13 = var12; var13 > var12 - 24; var13--) {
         var9.set(var10, var13, var11);
         VoxelShape var14 = var0.world.getBlockState(var9).getCollisionShape(var0.world, var9);
         if (!var14.isEmpty()) {
            return var13 + var14.getMax(Axis.Y);
         }
      }

      return var7;
   }

   private static void drawParked(MinecraftClient var0, VertexConsumerProvider var1, Matrix4f var2, Vec3d var3, float var4) {
      float var5 = time();
      Matrix4f var6 = new Matrix4f().translate((float)(carX - var3.x), (float)(carY - var3.y), (float)(carZ - var3.z)).rotateY(-carYaw);
      Matrix4f var7 = new Matrix4f(var2).mul(var6);
      int var8 = DeathFx.light(var0, carX, carY + 1.0, carZ);
      boolean var9 = ((int)(var5 * 2.6F) & 1) == 0;
      if (var9 != lastBlink) {
         lastBlink = var9;
         double var10 = Math.sqrt(var0.player.squaredDistanceTo(carX, carY, carZ));
         float var12 = 0.22F * (float)Math.max(0.0, 1.0 - var10 / 14.0);
         if (var12 > 0.01F) {
            sound(var0, (SoundEvent)SoundEvents.BLOCK_NOTE_BLOCK_HAT.value(), var12, var9 ? 1.7F : 1.35F);
         }
      }

      CarModel.lights(true, var9, false);
      ArrayList<float[]> var16 = new ArrayList();
      Matrix4f var11 = new Matrix4f();
      SportCar.draw(var1.getBuffer(RenderLayers.entityCutoutNoCull(Accessories.WHITE)), false, var7, var6, var8, var16, var11);
      SportCar.draw(var1.getBuffer(RenderLayers.text(Accessories.WHITE)), true, var7, var6, var8, var16, var11);
      if (!var16.isEmpty() && SkyTextures.spritesReady()) {
         DeathFx.cameraAxes(var0, var2, var4);
         VertexConsumer var17 = var1.getBuffer(RenderLayers.text(SkyTextures.SPRITES));

         for (float[] var14 : var16) {
            Vector3f var15 = var6.transformPosition(new Vector3f(var14[0], var14[1], var14[2]));
            billboard(var17, var2, var15.x, var15.y, var15.z, var14[3], Float.floatToRawIntBits(var14[4]), var14[5]);
         }
      }
   }

   private static void draw(MinecraftClient var0, VertexConsumerProvider var1, Matrix4f var2, Vec3d var3, float var4) {
      if (active == 9) {
         long var35 = System.nanoTime();
         drawParked(var0, var1, var2, var3, var4);
         if (Boolean.getBoolean("neonlyrics.selftest") && ++profN % 40 == 0) {
            NeonLyrics.LOG.info("SELFTEST Auto je Bild: {} ms", (System.nanoTime() - var35) / 1000000.0);
         }
      } else {
         ClientPlayerEntity var5 = var0.player;
         long var6 = System.nanoTime();
         float var8 = Math.min(0.05F, (float)(var6 - lastNs) / 1.0E9F);
         lastNs = var6;
         float var9 = time();
         double var10 = beatPos(var9);
         int var12 = (int)Math.floor(var10);
         float var13 = (float)(var10 - Math.floor(var10));
         float var14 = (float)Math.exp(-var13 * 6.0F);
         boolean var15 = var12 != lastBeat;
         lastBeat = var12;
         Vec3d var16 = var5.getLerpedPos(var4);
         Matrix4f var17 = new Matrix4f(var2).translate((float)(var16.x - var3.x), (float)(var16.y - var3.y), (float)(var16.z - var3.z)).rotateY(-yaw);
         int var18 = DeathFx.light(var0, var16.x, var16.y + 1.0, var16.z);
         L.reset();
         Matrix4f var19 = new Matrix4f().translate(0.0F, 1.0546875F, 0.0F);
         VertexConsumer var20 = var1.getBuffer(RenderLayers.entityCutoutNoCull(Accessories.WHITE));
         ArrayList<float[]> var21 = new ArrayList();
         Matrix4f var22 = null;
         Matrix4f var23 = null;
         Matrix4f var24 = null;
         int var25 = active;
         switch (var25) {
            case 0:
               boolean var42 = (var12 & 1) == 0;
               float var49 = (float)Math.pow(Math.sin(var13 * Math.PI), 0.7) * (var42 ? 0.3F : 0.12F);
               float var58 = (var12 >> 3 & 1) == 1 ? 0.06F * (float)Math.sin(var10 * Math.PI) : 0.0F;
               float var64 = (var42 ? 0.1F : 0.03F) * (float)Math.sin(var13 * Math.PI);
               Matrix4f var67 = new Matrix4f()
                  .translate(0.0F, var64, 0.0F)
                  .translate(0.0F, 0.39F, -1.51F)
                  .rotateX(-var49)
                  .rotateZ(var58)
                  .translate(0.0F, -0.39F, 1.51F);
               var22 = new Matrix4f(var17).mul(var67);
               var23 = new Matrix4f().translate((float)(var16.x - var3.x), (float)(var16.y - var3.y), (float)(var16.z - var3.z)).rotateY(-yaw).mul(var67);
               var24 = var67;
               CarModel.lights(false, false, false);
               SportCar.draw(var1.getBuffer(RenderLayers.entityCutoutNoCull(Accessories.WHITE)), false, var22, var23, var18, var21, var67);
               var20 = var1.getBuffer(RenderLayers.entityCutoutNoCull(Accessories.WHITE));
               var19 = new Matrix4f(var67).translate(0.44F, 0.7115625F, -0.12F);
               L.rLegX = L.lLegX = -1.45F;
               L.rLegZ = 0.06F;
               L.lLegZ = -0.06F;
               L.rArmX = -1.15F;
               L.rArmZ = 0.3F;
               float var31 = (float)Math.sin(var13 * Math.PI * 2.0);
               L.lArmX = -0.35F - 0.25F * var31;
               L.lArmZ = 2.15F + 0.2F * var31;
               L.headX = 0.12F * var14;
               L.headY = 0.25F * (float)Math.sin(var10 * 0.5);
               if (var15) {
                  throwBills(armL(new Matrix4f(var19), L), var42 ? 7 : 4, 0.9F);
               }
               break;
            case 1:
               boolean var41 = (var12 & 1) == 0;
               float var48 = (float)Math.sin(var13 * Math.PI);
               L.rArmX = var41 ? -2.6F + 1.3F * (1.0F - var48) : -0.6F;
               L.lArmX = var41 ? -0.6F : -2.6F + 1.3F * (1.0F - var48);
               L.rArmZ = -0.25F;
               L.lArmZ = 0.25F;
               L.headX = -0.2F + 0.15F * var14;
               var19 = new Matrix4f().translate(0.0F, 1.0546875F + 0.03F * (float)Math.sin(var13 * Math.PI), 0.0F);
               if (var15) {
                  Matrix4f var56 = new Matrix4f(var19);
                  throwBills(var41 ? armR(var56, L) : armL(var56, L), 9, 1.3F);
               }

               if (RND.nextFloat() < var8 * 14.0F) {
                  BILLS.add(bill((RND.nextFloat() - 0.5F) * 6.0F, 5.0F + RND.nextFloat() * 2.0F, (RND.nextFloat() - 0.5F) * 6.0F, 0.0F, -0.5F, 0.0F));
               }

               Matrix4f var57 = new Matrix4f(var17).mul(var19).translate(0.0F, 0.234375F, 0.12890625F);

               for (int var63 = 0; var63 < 10; var63++) {
                  double var66 = Math.PI * (0.15 + 0.7 * var63 / 9.0);
                  float var32 = (float)Math.cos(var66) * 3.4F * 0.05859375F;
                  float var33 = -((float)Math.sin(var66)) * 3.0F * 0.05859375F;
                  DeathFx.cube(var20, var57, var32 - 0.022F, var33 - 0.022F, -0.02F, var32 + 0.022F, var33 + 0.022F, 0.02F, -868018, 15728880);
               }
               break;
            case 2:
               float var40 = (float)Math.sin(var10 * Math.PI);
               float var47 = 0.12F * var40;
               var19 = new Matrix4f().translate(var47, 1.0546875F, 0.0F).rotateZ(-var47 * 0.8F);
               float var55 = (var12 & 1) == 0 ? 1.0F : -1.0F;
               L.rArmZ = -0.35F * var40 - 0.1F;
               L.lArmZ = -0.35F * var40 + 0.1F;
               L.rArmX = 0.45F * var55;
               L.lArmX = 0.45F * var55;
               L.rLegZ = -var47 * 0.6F;
               L.lLegZ = -var47 * 0.6F;
               L.headZ = var47;
               break;
            case 3:
               float var39 = DeathFx.eo(var9 / 0.25F) * (1.0F - DeathFx.sm(1.9F, 2.3F, var9));
               L.rArmX = -1.55F * var39;
               L.rArmZ = 1.0F * var39;
               L.lArmX = -2.4F * var39;
               L.lArmZ = 1.25F * var39;
               L.headX = 0.55F * var39;
               L.headY = -0.5F * var39;
               L.headZ = 0.25F * var39;
               var19 = new Matrix4f().translate(0.0F, 1.0546875F, 0.0F).rotateX(0.15F * var39);
               if (var9 > 0.25F && var9 < 0.3F && SPARKS.size() < 40) {
                  for (int var46 = 0; var46 < 30; var46++) {
                     spark(-0.9F, 2.0F, 0.2F, 3.0F, 7074815);
                  }
               }
               break;
            case 4:
               float var38 = DeathFx.cl(var9 / 1.1F);
               float var45 = (float)Math.sin(var38 * Math.PI) * 1.5F;
               float var54 = (float) (-Math.PI * 2) * DeathFx.sm(0.08F, 0.92F, var38);
               float var62 = (float)Math.sin(var38 * Math.PI);
               var19 = new Matrix4f().translate(0.0F, 1.0546875F + var45, 0.0F).rotateX(var54);
               L.rLegX = L.lLegX = -1.7F * var62;
               L.rArmX = L.lArmX = -2.5F * (1.0F - var62) - 0.8F * var62;
               L.headX = 0.3F * var62;
               if (var38 >= 1.0F && !landed) {
                  landed = true;

                  for (int var30 = 0; var30 < 26; var30++) {
                     spark(0.0F, 0.05F, 0.0F, 2.2F, 14209216);
                  }
               }
               break;
            case 5:
               float var37 = DeathFx.eo(var9 / 0.3F) * (1.0F - DeathFx.sm(2.2F, 2.6F, var9));
               L.rArmX = -2.75F * var37;
               L.rArmZ = (-0.3F + 0.4F * (float)Math.sin(var9 * 10.0F)) * var37;
               L.headZ = 0.1F * var37;
               L.headX = -0.1F * var37;
               break;
            case 6:
               L.rArmX = -0.75F + 0.25F * (float)Math.sin(var9 * 22.0F);
               L.rArmZ = 0.35F;
               L.lArmX = -1.2F;
               L.lArmZ = 0.65F;
               L.headX = 0.45F * var14 - 0.1F;
               L.rLegZ = 0.15F;
               L.lLegZ = -0.15F;
               var19 = new Matrix4f().translate(0.0F, 1.0546875F - 0.05F * var14, 0.0F).rotateX(0.12F * var14);
               guitar(var20, new Matrix4f(var17).mul(var19), var9, var14, var21, new Matrix4f(var19));
               if (var15) {
                  for (int var36 = 0; var36 < 12; var36++) {
                     spark(0.05F, 0.85F, 0.35F, 2.5F, (var12 & 1) == 0 ? 16727464 : 3073791);
                  }
               }
               break;
            case 7:
            default:
               throne(var20, var17, var18, var9, var21);
               var19 = new Matrix4f().translate(0.0F, 1.1015625F, -0.15F).rotateX(-0.12F);
               L.rLegX = L.lLegX = -1.5F;
               L.rLegZ = 0.18F;
               L.lLegZ = -0.18F;
               L.rArmX = L.lArmX = -0.5F;
               L.rArmZ = -0.35F;
               L.lArmZ = 0.35F;
               L.headX = -0.15F + 0.1F * var14;
               L.headY = 0.3F * (float)Math.sin(var9 * 0.6);
               break;
            case 8:
               Matrix4f var26 = new Matrix4f();
               boolean var27 = ((int)(var9 * 2.6F) & 1) == 0;
               float var28 = var9 % 2.4F;
               boolean var29 = var28 < 0.12F || var28 > 0.26F && var28 < 0.38F;
               if (var27 != lastBlink) {
                  lastBlink = var27;
                  sound(var0, (SoundEvent)SoundEvents.BLOCK_NOTE_BLOCK_HAT.value(), 0.22F, var27 ? 1.7F : 1.35F);
               }

               CarModel.lights(true, var27, var29);
               var22 = new Matrix4f(var17).mul(var26);
               var23 = new Matrix4f().translate((float)(var16.x - var3.x), (float)(var16.y - var3.y), (float)(var16.z - var3.z)).rotateY(-yaw).mul(var26);
               var24 = var26;
               SportCar.draw(var1.getBuffer(RenderLayers.entityCutoutNoCull(Accessories.WHITE)), false, var22, var23, var18, var21, var26);
               var20 = var1.getBuffer(RenderLayers.entityCutoutNoCull(Accessories.WHITE));
               var19 = new Matrix4f(var26).translate(0.44F, 0.7115625F, -0.12F);
               L.rLegX = L.lLegX = -1.45F;
               L.rLegZ = 0.06F;
               L.lLegZ = -0.06F;
               L.rArmX = -1.15F;
               L.rArmZ = 0.3F;
               L.lArmX = -1.15F;
               L.lArmZ = -0.3F;
               L.headY = 0.35F * (float)Math.sin(var9 * 0.7);
               L.headX = 0.05F;
         }

         for (int var43 = BILLS.size() - 1; var43 >= 0; var43--) {
            float[] var50 = BILLS.get(var43);
            var50[8] += var8;
            if (var50[9] < 0.5F) {
               var50[3] *= 1.0F - 1.6F * var8;
               var50[5] *= 1.0F - 1.6F * var8;
               var50[4] -= 4.5F * var8;
               var50[4] = Math.max(var50[4], -1.4F);
               var50[0] += var50[3] * var8 + (float)Math.sin(var50[8] * 5.0F + var50[10]) * 0.6F * var8;
               var50[1] += var50[4] * var8;
               var50[2] += var50[5] * var8 + (float)Math.cos(var50[8] * 4.0F + var50[10]) * 0.6F * var8;
               var50[6] += var50[7] * var8;
               if (var50[1] <= 0.01F) {
                  var50[1] = 0.01F;
                  var50[9] = 1.0F;
               }
            }

            if (!(var50[8] > 9.0F) && (BILLS.size() <= 260 || var43 >= 30)) {
               Matrix4f var59 = new Matrix4f(var17).translate(var50[0], var50[1], var50[2]).rotateY(var50[6]);
               if (var50[9] < 0.5F) {
                  var59.rotateX((float)Math.sin(var50[8] * 7.0F + var50[10]) * 0.9F).rotateZ((float)Math.cos(var50[8] * 6.0F + var50[10]) * 0.5F);
               }

               DeathFx.cube(var20, var59, -0.15F, -0.003F, -0.07F, 0.15F, 0.003F, 0.07F, -12674225, var18);
               DeathFx.cube(var20, var59, -0.05F, 0.0031F, -0.035F, 0.05F, 0.0035F, 0.035F, -7350628, var18);
            } else {
               BILLS.remove(var43);
            }
         }

         Identifier var44 = null;

         try {
            var44 = var5.getSkin().body().texturePath();
         } catch (Throwable var34) {
         }

         if (var44 != null) {
            VertexConsumer var51 = var1.getBuffer(RenderLayers.entityCutoutNoCull(var44));
            figure(var51, new Matrix4f(var17).mul(var19), L, -1, var18);
         }

         if (var22 != null) {
            SportCar.draw(var1.getBuffer(RenderLayers.text(Accessories.WHITE)), true, var22, var23, var18, var21, var24);
         }

         for (int var52 = SPARKS.size() - 1; var52 >= 0; var52--) {
            float[] var60 = SPARKS.get(var52);
            var60[6] += var8;
            if (var60[6] > var60[7]) {
               SPARKS.remove(var52);
            } else {
               var60[4] -= 6.0F * var8;
               var60[0] += var60[3] * var8;
               var60[1] += var60[4] * var8;
               var60[2] += var60[5] * var8;
               if (var60[1] < 0.02F) {
                  var60[1] = 0.02F;
                  var60[4] *= -0.3F;
               }

               var21.add(new float[]{var60[0], var60[1], var60[2], 0.12F, var60[8], 1.0F - var60[6] / var60[7]});
            }
         }

         if (!var21.isEmpty() && SkyTextures.spritesReady()) {
            DeathFx.cameraAxes(var0, var2, var4);
            VertexConsumer var53 = var1.getBuffer(RenderLayers.text(SkyTextures.SPRITES));
            Matrix4f var61 = new Matrix4f().translate((float)(var16.x - var3.x), (float)(var16.y - var3.y), (float)(var16.z - var3.z)).rotateY(-yaw);

            for (float[] var68 : var21) {
               Vector3f var69 = var61.transformPosition(new Vector3f(var68[0], var68[1], var68[2]));
               billboard(var53, var2, var69.x, var69.y, var69.z, var68[3], Float.floatToRawIntBits(var68[4]), var68[5]);
            }
         }
      }
   }

   private static void guitar(VertexConsumer var0, Matrix4f var1, float var2, float var3, List<float[]> var4, Matrix4f var5) {
      Matrix4f var6 = new Matrix4f(var1).translate(0.05F, -0.2F, 0.25F).rotateZ(0.55F);
      int var7 = -15395556;
      int var8 = -13703425;
      int var9 = -49752;
      DeathFx.cube(var0, var6, -0.28F, -0.3F, -0.05F, 0.28F, 0.1F, 0.05F, var7, 15728880);
      DeathFx.cube(var0, var6, -0.2F, 0.1F, -0.05F, 0.2F, 0.25F, 0.05F, var7, 15728880);
      DeathFx.cube(var0, var6, -0.3F, -0.32F, -0.06F, 0.3F, -0.29F, 0.06F, var8, 15728880);
      DeathFx.cube(var0, var6, -0.3F, -0.32F, -0.06F, -0.27F, 0.12F, 0.06F, var9, 15728880);
      DeathFx.cube(var0, var6, 0.27F, -0.32F, -0.06F, 0.3F, 0.12F, 0.06F, var9, 15728880);
      DeathFx.cube(var0, var6, -0.04F, 0.25F, -0.03F, 0.04F, 1.05F, 0.03F, -14013901, 15728880);
      DeathFx.cube(var0, var6, -0.07F, 1.05F, -0.04F, 0.07F, 1.22F, 0.04F, var8, 15728880);

      for (int var10 = 0; var10 < 4; var10++) {
         DeathFx.cube(var0, var6, -0.03F + var10 * 0.02F, -0.2F, 0.05F, -0.025F + var10 * 0.02F, 1.05F, 0.055F, -2039584, 15728880);
      }

      Vector3f var11 = new Matrix4f(var5).translate(0.05F, -0.2F, 0.25F).rotateZ(0.55F).transformPosition(new Vector3f(0.0F, -0.1F, 0.1F));
      var4.add(new float[]{var11.x, var11.y, var11.z, 1.1F + 0.6F * var3, Float.intBitsToFloat(3073791), 0.25F + 0.35F * var3});
   }

   private static void throne(VertexConsumer var0, Matrix4f var1, int var2, float var3, List<float[]> var4) {
      int var5 = -1527238;
      short var6 = -10133;
      int var7 = -6418392;
      DeathFx.cube(var0, var1, -0.75F, 0.0F, -0.6F, 0.75F, 0.12F, 0.6F, var5, var2);
      DeathFx.cube(var0, var1, -0.6F, 0.12F, -0.45F, 0.6F, 0.62F, 0.45F, var5, var2);
      DeathFx.cube(var0, var1, -0.55F, 0.62F, -0.4F, 0.55F, 0.75F, 0.42F, var7, var2);
      DeathFx.cube(var0, var1, -0.65F, 0.62F, -0.6F, 0.65F, 2.3F, -0.42F, var5, var2);
      DeathFx.cube(var0, var1, -0.5F, 0.8F, -0.43F, 0.5F, 2.1F, -0.4F, var7, var2);
      DeathFx.cube(var0, var1, -0.75F, 0.62F, -0.6F, -0.6F, 1.15F, 0.45F, var6, var2);
      DeathFx.cube(var0, var1, 0.6F, 0.62F, -0.6F, 0.75F, 1.15F, 0.45F, var6, var2);

      for (int var8 = 0; var8 < 5; var8++) {
         float var9 = -0.52F + var8 * 0.26F;
         float var10 = var8 % 2 == 0 ? 0.35F : 0.22F;
         DeathFx.cube(var0, var1, var9 - 0.05F, 2.3F, -0.56F, var9 + 0.05F, 2.3F + var10, -0.46F, var6, var2);
         DeathFx.cube(var0, var1, var9 - 0.045F, 2.3F + var10, -0.55F, var9 + 0.045F, 2.39F + var10, -0.47F, -54454, 15728880);
      }

      for (int var13 = 0; var13 < 14; var13++) {
         double var14 = var13 * 0.45 + var3 * 0.6;
         float var11 = 1.1F + 0.2F * (float)Math.sin(var3 + var13);
         float var12 = 0.5F + (var3 * 0.4F + var13 * 0.17F) % 2.4F;
         var4.add(new float[]{(float)Math.cos(var14) * var11, var12, (float)Math.sin(var14) * var11, 0.18F, Float.intBitsToFloat(16767083), 0.7F});
      }
   }

   private static float[] bill(float var0, float var1, float var2, float var3, float var4, float var5) {
      return new float[]{var0, var1, var2, var3, var4, var5, RND.nextFloat() * 6.28F, (RND.nextFloat() - 0.5F) * 8.0F, 0.0F, 0.0F, RND.nextFloat() * 10.0F};
   }

   private static void throwBills(Matrix4f var0, int var1, float var2) {
      Vector3f var3 = var0.transformPosition(new Vector3f(0.0F, -0.3515625F, 0.0F));

      for (int var4 = 0; var4 < var1; var4++) {
         float var5 = (RND.nextFloat() - 0.5F) * 2.2F;
         float var6 = (1.5F + RND.nextFloat() * 2.2F) * var2;
         BILLS.add(
            bill(var3.x, var3.y, var3.z, (float)Math.sin(var5) * var6 * 0.7F, 2.2F + RND.nextFloat() * 2.4F * var2, (float)Math.cos(var5) * var6 * 0.4F + 0.6F)
         );
      }

      if (BILLS.size() > 260) {
         BILLS.subList(0, BILLS.size() - 260).clear();
      }
   }

   private static void spark(float var0, float var1, float var2, float var3, int var4) {
      double var5 = RND.nextDouble() * Math.PI * 2.0;
      double var7 = RND.nextDouble() * 1.2;
      float var9 = var3 * (0.4F + RND.nextFloat() * 0.8F);
      SPARKS.add(
         new float[]{
            var0,
            var1,
            var2,
            (float)(Math.cos(var5) * Math.cos(var7)) * var9,
            (float)Math.sin(var7) * var9 + 1.5F,
            (float)(Math.sin(var5) * Math.cos(var7)) * var9,
            0.0F,
            0.5F + RND.nextFloat() * 0.6F,
            Float.intBitsToFloat(var4)
         }
      );
   }

   private static void billboard(VertexConsumer var0, Matrix4f var1, float var2, float var3, float var4, float var5, int var6, float var7) {
      if (!(var7 <= 0.02F)) {
         float var8 = DeathFx.AX[0];
         float var9 = DeathFx.AX[1];
         float var10 = DeathFx.AX[2];
         float var11 = DeathFx.AX[3];
         float var12 = DeathFx.AX[4];
         float var13 = DeathFx.AX[5];
         float var14 = var5 / 2.0F;
         int var15 = DeathFx.argb(var6, var7);
         float[] var16 = new float[]{
            var2 - var8 * var14 - var11 * var14,
            var3 - var9 * var14 - var12 * var14,
            var4 - var10 * var14 - var13 * var14,
            var2 + var8 * var14 - var11 * var14,
            var3 + var9 * var14 - var12 * var14,
            var4 + var10 * var14 - var13 * var14,
            var2 + var8 * var14 + var11 * var14,
            var3 + var9 * var14 + var12 * var14,
            var4 + var10 * var14 + var13 * var14,
            var2 - var8 * var14 + var11 * var14,
            var3 - var9 * var14 + var12 * var14,
            var4 - var10 * var14 + var13 * var14
         };
         float var17 = 0.5F;
         float var18 = 0.75F;
         float[][] var19 = new float[][]{{var17, 1.0F}, {var18, 1.0F}, {var18, 0.0F}, {var17, 0.0F}};

         for (int var20 = 0; var20 < 8; var20++) {
            int var21 = var20 < 4 ? var20 : 7 - var20;
            var0.vertex(var1, var16[var21 * 3], var16[var21 * 3 + 1], var16[var21 * 3 + 2])
               .color(var15)
               .texture(var19[var21][0], var19[var21][1])
               .overlay(OverlayTexture.DEFAULT_UV)
               .light(15728880)
               .normal(0.0F, 1.0F, 0.0F);
         }
      }
   }

   static final class Limbs {
      float rArmX;
      float rArmZ;
      float lArmX;
      float lArmZ;
      float rLegX;
      float lLegX;
      float rLegZ;
      float lLegZ;
      float headX;
      float headY;
      float headZ;

      void reset() {
         this.rArmX = this.rArmZ = this.lArmX = this.lArmZ = this.rLegX = this.lLegX = this.rLegZ = this.lLegZ = this.headX = this.headY = this.headZ = 0.0F;
      }
   }
}
