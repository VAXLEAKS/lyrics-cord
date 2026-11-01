package dev.neonlyrics.hud;

import java.util.HashMap;
import java.util.Map;

public final class Anim {
   private double value;
   private double target;
   private final double speed;
   private long last = System.nanoTime();

   public Anim(double var1, double var3) {
      this.value = var1;
      this.target = var1;
      this.speed = var3;
   }

   public Anim target(double var1) {
      this.target = var1;
      return this;
   }

   public float get() {
      long var1 = System.nanoTime();
      double var3 = Math.min(0.1, (var1 - this.last) / 1.0E9);
      this.last = var1;
      this.value = this.value + (this.target - this.value) * (1.0 - Math.exp(-this.speed * var3));
      if (Math.abs(this.target - this.value) < 1.0E-4) {
         this.value = this.target;
      }

      return (float)this.value;
   }

   public void snap(double var1) {
      this.value = var1;
      this.target = var1;
   }

   public double target() {
      return this.target;
   }

   public static float outCubic(float var0) {
      var0 = clamp(var0);
      float var1 = 1.0F - var0;
      return 1.0F - var1 * var1 * var1;
   }

   public static float outBack(float var0) {
      var0 = clamp(var0);
      float var1 = 1.70158F;
      float var2 = var1 + 1.0F;
      return 1.0F + var2 * (float)Math.pow(var0 - 1.0F, 3.0) + var1 * (float)Math.pow(var0 - 1.0F, 2.0);
   }

   public static float outElastic(float var0) {
      var0 = clamp(var0);
      return var0 != 0.0F && var0 != 1.0F ? (float)(Math.pow(2.0, -10.0F * var0) * Math.sin((var0 * 10.0F - 0.75) * (Math.PI * 2.0 / 3.0)) + 1.0) : var0;
   }

   public static float clamp(float var0) {
      return Math.max(0.0F, Math.min(1.0F, var0));
   }

   public static float since(long var0, long var2) {
      return clamp((float)(System.currentTimeMillis() - var0) / (float)var2);
   }

   public static float pulse(double var0) {
      return (float)(0.5 + 0.5 * Math.sin(System.currentTimeMillis() / var0 * Math.PI * 2.0));
   }

   public static final class Pool {
      private final Map<String, Anim> map = new HashMap<>();
      private final double speed;

      public Pool(double var1) {
         this.speed = var1;
      }

      public float to(String var1, double var2) {
         return this.map.computeIfAbsent(var1, var3 -> new Anim(var2, this.speed)).target(var2).get();
      }

      public float toFrom(String var1, double var2, double var4) {
         return this.map.computeIfAbsent(var1, var3 -> new Anim(var2, this.speed)).target(var4).get();
      }

      public void reset(String var1) {
         this.map.keySet().removeIf(var1x -> var1x.startsWith(var1));
      }
   }
}
