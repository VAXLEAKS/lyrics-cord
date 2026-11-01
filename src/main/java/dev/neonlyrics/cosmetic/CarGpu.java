package dev.neonlyrics.cosmetic;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.systems.RenderSystem.ShapeIndexBuffer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import com.mojang.blaze3d.vertex.VertexFormat.DrawMode;
import dev.neonlyrics.NeonLyrics;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.Map.Entry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.RenderSetup;
import net.minecraft.client.render.RenderSetup.Texture;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryUtil;

final class CarGpu {
   private static boolean failed;
   private static GpuBuffer vbuf;
   private static ByteBuffer staging;
   private static int vcount;
   private static Field setupField;

   private CarGpu() {
   }

   static boolean ok() {
      return !failed;
   }

   static void fail(Throwable var0) {
      if (!failed) {
         NeonLyrics.LOG.warn("Auto auf der Grafikkarte geht nicht, zeichne wie frueher", var0);
      }

      failed = true;
   }

   private static RenderLayer type() {
      return RenderLayers.text(Accessories.WHITE);
   }

   static ByteBuffer begin(int var0) {
      int var1 = type().getVertexFormat().getVertexSize();
      int var2 = var0 * var1;
      if (staging == null || staging.capacity() < var2) {
         if (staging != null) {
            MemoryUtil.memFree(staging);
         }

         staging = MemoryUtil.memAlloc(var2 + (var2 >> 3));
      }

      staging.clear();
      return staging;
   }

   static ByteBuffer staging() {
      return staging;
   }

   static int stride() {
      return type().getVertexFormat().getVertexSize();
   }

   static int[] offsets() {
      VertexFormat var0 = type().getVertexFormat();
      return new int[]{
         var0.getOffset(VertexFormatElement.POSITION),
         var0.getOffset(VertexFormatElement.COLOR),
         var0.getOffset(VertexFormatElement.UV0),
         var0.getOffset(VertexFormatElement.UV2)
      };
   }

   static boolean culls() {
      return type().getRenderPipeline().isCull();
   }

   static void upload(int var0) {
      int var1 = var0 * stride();
      staging.limit(var1);
      staging.position(0);
      if (vbuf != null && !vbuf.isClosed() && vbuf.size() >= var1) {
         RenderSystem.getDevice().createCommandEncoder().writeToBuffer(vbuf.slice(0L, var1), staging);
      } else {
         if (vbuf != null && !vbuf.isClosed()) {
            vbuf.close();
         }

         vbuf = RenderSystem.getDevice().createBuffer(() -> "neonlyrics car", 40, staging);
      }

      vcount = var0;
   }

   static void draw(Matrix4f var0) {
      if (vbuf != null && vcount != 0) {
         RenderLayer var1 = type();
         RenderPipeline var2 = var1.getRenderPipeline();
         Matrix4fStack var3 = RenderSystem.getModelViewStack();
         var3.pushMatrix();
         var3.mul(var0);

         GpuBufferSlice var4;
         try {
            var4 = RenderSystem.getDynamicUniforms()
               .write(RenderSystem.getModelViewMatrix(), new Vector4f(1.0F, 1.0F, 1.0F, 1.0F), new Vector3f(), new Matrix4f());
         } finally {
            var3.popMatrix();
         }

         int var5 = vcount / 4 * 6;
         ShapeIndexBuffer var6 = RenderSystem.getSequentialBuffer(DrawMode.QUADS);
         GpuBuffer var7 = var6.getIndexBuffer(var5);
         Framebuffer var8 = MinecraftClient.getInstance().getFramebuffer();
         Map<String, Texture> var9 = setup(var1).resolveTextures();
         RenderPass var10 = RenderSystem.getDevice()
            .createCommandEncoder()
            .createRenderPass(() -> "neonlyrics car", var8.getColorAttachmentView(), OptionalInt.empty(), var8.getDepthAttachmentView(), OptionalDouble.empty());

         try {
            var10.setPipeline(var2);
            RenderSystem.bindDefaultUniforms(var10);
            var10.setUniform("DynamicTransforms", var4);
            var10.setVertexBuffer(0, vbuf);

            for (Entry<String, Texture> var12 : var9.entrySet()) {
               var10.bindTexture((String)var12.getKey(), ((Texture)var12.getValue()).textureView(), ((Texture)var12.getValue()).sampler());
            }

            var10.setIndexBuffer(var7, var6.getIndexType());
            var10.drawIndexed(0, 0, var5, 1);
         } catch (Throwable var18) {
            if (var10 != null) {
               try {
                  var10.close();
               } catch (Throwable var16) {
                  var18.addSuppressed(var16);
               }
            }

            throw var18;
         }

         if (var10 != null) {
            var10.close();
         }
      }
   }

   private static RenderSetup setup(RenderLayer var0) {
      try {
         if (setupField == null) {
            for (Field var4 : RenderLayer.class.getDeclaredFields()) {
               if (var4.getType() == RenderSetup.class) {
                  var4.setAccessible(true);
                  setupField = var4;
               }
            }
         }

         return (RenderSetup)setupField.get(var0);
      } catch (Exception var5) {
         throw new IllegalStateException(var5);
      }
   }
}
