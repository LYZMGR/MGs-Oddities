package github.mgrlyz.mgsoddities.mixin;

import github.mgrlyz.mgsoddities.client.render.transmitter.MGsOdditiesRenderMechanicalPipe;
import mekanism.client.render.MekanismRenderer;
import net.neoforged.neoforge.client.event.TextureAtlasStitchedEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MekanismRenderer.class, remap = false)
public class MixinMekanismRenderer {
    @Inject(method = "onStitch", at = @At(value = "INVOKE", target = "Lmekanism/client/render/transmitter/RenderMechanicalPipe;onStitch()V"))
    private static void onMGsOdditiesStitch(TextureAtlasStitchedEvent event, CallbackInfo ci) {
        MGsOdditiesRenderMechanicalPipe.onStitch();
    }
}