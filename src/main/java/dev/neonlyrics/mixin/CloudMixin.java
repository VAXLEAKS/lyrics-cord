package dev.neonlyrics.mixin;

import dev.neonlyrics.sky.WorldSky;
import net.minecraft.client.render.CloudRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CloudRenderer.class)
public abstract class CloudMixin {
   @Inject(method = {"method_62168(ILnet/minecraft/class_4063;FLnet/minecraft/class_243;JF)V", "renderOit"}, at = @At("HEAD"), cancellable = true, require = 0)
   private void neonlyrics$hideClouds(CallbackInfo var1) {
      if (WorldSky.hidesClouds()) {
         var1.cancel();
      }
   }
}
