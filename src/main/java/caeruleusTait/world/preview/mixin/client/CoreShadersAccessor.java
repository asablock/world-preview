package caeruleusTait.world.preview.mixin.client;

import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.CoreShaders;
import net.minecraft.client.renderer.ShaderProgram;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(CoreShaders.class)
public interface CoreShadersAccessor {
    @Invoker
    static ShaderProgram invokeRegister(String string, VertexFormat vertexFormat) {
        throw new AssertionError();
    }
}
