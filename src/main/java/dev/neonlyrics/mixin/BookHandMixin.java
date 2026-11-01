package dev.neonlyrics.mixin;

import dev.neonlyrics.hud.BookHand;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeldItemRenderer.class)
public abstract class BookHandMixin {
   @Shadow
   protected abstract void method_3231(MatrixStack var1, OrderedRenderCommandQueue var2, int var3, float var4, float var5, float var6);

   @Inject(
      method = "method_3228(Lnet/minecraft/class_742;FFLnet/minecraft/class_1268;FLnet/minecraft/class_1799;FLnet/minecraft/class_4587;Lnet/minecraft/class_11659;I)V",
      at = @At("HEAD"),
      cancellable = true,
      require = 0
   )
   private void neonlyrics$book(
      AbstractClientPlayerEntity var1,
      float var2,
      float var3,
      Hand var4,
      float var5,
      ItemStack var6,
      float var7,
      MatrixStack var8,
      OrderedRenderCommandQueue var9,
      int var10,
      CallbackInfo var11
   ) {
      if (BookHand.wants()) {
         if (var4 == Hand.MAIN_HAND) {
            var8.push();
            BookHand.drawing = true;

            try {
               this.method_3231(var8, var9, var10, Math.min(90.0F, var3 + 32.0F), var7, var5);
            } finally {
               BookHand.drawing = false;
               var8.pop();
            }
         }

         var11.cancel();
      }
   }

   @Inject(
      method = "method_3223(Lnet/minecraft/class_4587;Lnet/minecraft/class_11659;ILnet/minecraft/class_1799;)V",
      at = @At("HEAD"),
      cancellable = true,
      require = 0
   )
   private void neonlyrics$bookInsteadOfMap(MatrixStack var1, OrderedRenderCommandQueue var2, int var3, ItemStack var4, CallbackInfo var5) {
      if (BookHand.drawing) {
         BookHand.render(var1, var2, var3);
         var5.cancel();
      }
   }
}
