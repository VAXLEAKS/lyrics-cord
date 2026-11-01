package dev.neonlyrics.mixin;

import dev.neonlyrics.sky.WorldSky;
import net.minecraft.block.enums.CameraSubmersionType;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.fog.FogRenderer;
import net.minecraft.client.world.ClientWorld;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FogRenderer.class)
public abstract class FogRendererMixin {
   @Inject(
      method = "method_62185(Lnet/minecraft/class_4184;FLnet/minecraft/class_638;IF)Lorg/joml/Vector4f;",
      at = @At("RETURN"),
      cancellable = true,
      require = 0
   )
   private void neonlyrics$skyFog(Camera var1, float var2, ClientWorld var3, int var4, float var5, CallbackInfoReturnable<Vector4f> var6) {
      if (var1.getSubmersionType() == CameraSubmersionType.NONE) {
         Vector4f var7 = (Vector4f)var6.getReturnValue();
         if (var7 != null && WorldSky.tintFog(var7)) {
            var6.setReturnValue(var7);
         }
      }
   }
}
