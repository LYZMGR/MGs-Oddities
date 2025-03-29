package github.mgrlyz.mgsoddities.client.render.transmitter;

import com.mojang.blaze3d.vertex.PoseStack;
import github.mgrlyz.mgsoddities.common.tile.transmitter.MGsOdditiesTileEntityUniversalCable;
import mekanism.api.annotations.NothingNullByDefault;
import mekanism.client.render.MekanismRenderer;
import mekanism.client.render.transmitter.RenderTransmitterBase;
import mekanism.common.base.ProfilerConstants;
import mekanism.common.content.network.EnergyNetwork;
import mekanism.common.content.network.transmitter.UniversalCable;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.phys.Vec3;

@NothingNullByDefault
public class MGsOdditiesRenderUniversalCable extends RenderTransmitterBase<MGsOdditiesTileEntityUniversalCable> {
    public MGsOdditiesRenderUniversalCable(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void render(MGsOdditiesTileEntityUniversalCable tile, float partialTick, PoseStack matrix, MultiBufferSource renderer, int light, int overlayLight, ProfilerFiller profiler) {
        EnergyNetwork network = tile.getTransmitter().getTransmitterNetwork();
        matrix.pushPose();
        matrix.translate(0.5, 0.5, 0.5);
        renderModel(tile, matrix, renderer.getBuffer(Sheets.translucentCullBlockSheet()), 0xFFFFFF, network.currentScale, LightTexture.FULL_BRIGHT,
                overlayLight, MekanismRenderer.energyIcon);
        matrix.popPose();
    }

    @Override
    protected String getProfilerSection() {
        return ProfilerConstants.UNIVERSAL_CABLE;
    }

    @Override
    protected boolean shouldRenderTransmitter(MGsOdditiesTileEntityUniversalCable tile, Vec3 camera) {
        if (super.shouldRenderTransmitter(tile, camera)) {
            UniversalCable cable = tile.getTransmitter();
            if (cable.hasTransmitterNetwork()) {
                EnergyNetwork network = cable.getTransmitterNetwork();
                return network.currentScale > 0;
            }
        }
        return false;
    }
}