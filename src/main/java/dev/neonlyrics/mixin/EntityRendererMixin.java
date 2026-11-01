package dev.neonlyrics.mixin;

import dev.neonlyrics.cosmetic.DeathFx;
import dev.neonlyrics.cosmetic.Emotes;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin {
   @Inject(method = "method_3933(Lnet/minecraft/class_1297;Lnet/minecraft/class_4604;DDD)Z", at = @At("HEAD"), cancellable = true, require = 0)
   private void neonlyrics$hideForDeathFx(Entity var1, Frustum var2, double var3, double var5, double var7, CallbackInfoReturnable<Boolean> var9) {
      if (DeathFx.hides(var1) || Emotes.hides(var1)) {
         var9.setReturnValue(false);
      }
   }
}
