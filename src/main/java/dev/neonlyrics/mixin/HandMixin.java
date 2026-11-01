package dev.neonlyrics.mixin;

import dev.neonlyrics.cosmetic.PhoneMenu;
import net.minecraft.client.render.GameRenderer;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class HandMixin {
   @Inject(method = "method_3172(FZLorg/joml/Matrix4f;)V", at = @At("HEAD"), cancellable = true, require = 0)
   private void neonlyrics$hideHand(float var1, boolean var2, Matrix4f var3, CallbackInfo var4) {
      if (PhoneMenu.hidesHand()) {
         var4.cancel();
      }
   }
}
