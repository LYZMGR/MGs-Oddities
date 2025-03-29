package github.mgrlyz.mgsoddities.client.render.transmitter;

import com.mojang.blaze3d.vertex.PoseStack;
import github.mgrlyz.mgsoddities.common.content.network.transmitter.MGsOdditiesThermodynamicConductor;
import github.mgrlyz.mgsoddities.common.tile.transmitter.MGsOdditiesTileEntityThermodynamicConductor;
import mekanism.client.render.MekanismRenderer;
import mekanism.client.render.transmitter.RenderTransmitterBase;
import mekanism.common.base.ProfilerConstants;
import mekanism.common.util.HeatUtils;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;

public class MGsOdditiesRenderThermodynamicConductor extends RenderTransmitterBase<MGsOdditiesTileEntityThermodynamicConductor> {
    public MGsOdditiesRenderThermodynamicConductor(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void render(@NotNull MGsOdditiesTileEntityThermodynamicConductor tile, float partialTick, PoseStack matrix, MultiBufferSource renderer, int light, int overlayLight, @NotNull ProfilerFiller profiler) {
        matrix.pushPose();
        matrix.translate(0.5, 0.5, 0.5);
        MGsOdditiesThermodynamicConductor conductor = tile.getTransmitter();
        int argb = HeatUtils.getColorFromTemp(conductor.getTotalTemperature(), conductor.getBaseColor()).argb();
        renderModel(tile, matrix, renderer.getBuffer(Sheets.translucentCullBlockSheet()), argb, MekanismRenderer.getAlpha(argb), LightTexture.FULL_BRIGHT,
                overlayLight, MekanismRenderer.heatIcon);
        matrix.popPose();
    }

    @Override
    protected @NotNull String getProfilerSection() {
        return ProfilerConstants.THERMODYNAMIC_CONDUCTOR;
    }
}