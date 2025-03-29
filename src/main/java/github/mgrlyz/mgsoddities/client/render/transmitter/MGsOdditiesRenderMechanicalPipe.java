package github.mgrlyz.mgsoddities.client.render.transmitter;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import github.mgrlyz.mgsoddities.common.content.network.transmitter.MGsOdditiesMechanicalPipe;
import github.mgrlyz.mgsoddities.common.tile.transmitter.MGsOdditiesTileEntityMechanicalPipe;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import mekanism.api.annotations.NothingNullByDefault;
import mekanism.client.render.MekanismRenderer;
import mekanism.client.render.ModelRenderer;
import mekanism.client.render.RenderResizableCuboid;
import mekanism.client.render.transmitter.RenderTransmitterBase;
import mekanism.common.base.ProfilerConstants;
import mekanism.common.content.network.FluidNetwork;
import mekanism.common.lib.transmitter.ConnectionType;
import mekanism.common.util.EnumUtils;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@NothingNullByDefault
public class MGsOdditiesRenderMechanicalPipe extends RenderTransmitterBase<MGsOdditiesTileEntityMechanicalPipe> {

    private static final int stages = 100;
    private static final float height = 0.45F;
    private static final float offset = 0.02F;
    private static final Int2ObjectMap<Map<FluidStack, Int2ObjectMap<MekanismRenderer.Model3D>>> cachedLiquids = new Int2ObjectArrayMap<>(8);

    public MGsOdditiesRenderMechanicalPipe(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    public static void onStitch() {
        cachedLiquids.clear();
    }

    @Override
    protected void render(MGsOdditiesTileEntityMechanicalPipe tile, float partialTick, @NotNull PoseStack matrix, @NotNull MultiBufferSource renderer, int light, int overlayLight,
                          @NotNull ProfilerFiller profiler) {
        MGsOdditiesMechanicalPipe pipe = tile.getTransmitter();
        FluidNetwork network = pipe.getTransmitterNetwork();
        FluidStack fluidStack = network.lastFluid;
        if (fluidStack.isEmpty()) {
            return;
        }
        float fluidScale = network.currentScale;
        int stage = Math.max(3, ModelRenderer.getStage(fluidStack, stages, fluidScale));
        int glow = MekanismRenderer.calculateGlowLight(light, fluidStack);
        int color = MekanismRenderer.getColorARGB(fluidStack, fluidScale);
        List<String> connectionContents = new ArrayList<>();
        boolean[] renderSides = new boolean[6];
        boolean hasHorizontalSide = false;
        int verticalSides = 0;
        VertexConsumer buffer = renderer.getBuffer(Sheets.translucentCullBlockSheet());
        Camera camera = getCamera();
        for (Direction side : EnumUtils.DIRECTIONS) {
            ConnectionType connectionType = pipe.getConnectionType(side);
            if (connectionType == ConnectionType.NORMAL) {
                MekanismRenderer.renderObject(getModel(side, fluidStack, stage), matrix, buffer, color, glow, overlayLight, RenderResizableCuboid.FaceDisplay.FRONT, camera, tile.getBlockPos());
            } else if (connectionType != ConnectionType.NONE) {
                connectionContents.add(side.getSerializedName() + connectionType.getSerializedName().toUpperCase(Locale.ROOT));
            }
            renderSides[side.ordinal()] = connectionType != ConnectionType.NORMAL;
            if (connectionType != ConnectionType.NONE) {
                if (side.getAxis().isHorizontal()) {
                    hasHorizontalSide = true;
                } else {
                    verticalSides++;
                }
            }
        }
        boolean renderBase = hasHorizontalSide || verticalSides < 2;
        MekanismRenderer.Model3D model = getModel(fluidStack, stage, renderBase);
        for (Direction side : EnumUtils.DIRECTIONS) {
            model.setSideRender(side, renderSides[side.ordinal()] || (side.getAxis().isVertical() && renderBase && stage != stages - 1));
        }
        MekanismRenderer.renderObject(model, matrix, buffer, color, glow, overlayLight, RenderResizableCuboid.FaceDisplay.FRONT, camera, tile.getBlockPos());
        if (!connectionContents.isEmpty()) {
            matrix.pushPose();
            matrix.translate(0.5, 0.5, 0.5);
            renderModel(tile, matrix, buffer, MekanismRenderer.getRed(color), MekanismRenderer.getGreen(color), MekanismRenderer.getBlue(color),
                    MekanismRenderer.getAlpha(color), glow, overlayLight, MekanismRenderer.getFluidTexture(fluidStack, MekanismRenderer.FluidTextureType.STILL), connectionContents);
            matrix.popPose();
        }
    }

    @Override
    protected @NotNull String getProfilerSection() {
        return ProfilerConstants.MECHANICAL_PIPE;
    }

    @Override
    protected boolean shouldRenderTransmitter(MGsOdditiesTileEntityMechanicalPipe tile, Vec3 camera) {
        if (super.shouldRenderTransmitter(tile, camera)) {
            MGsOdditiesMechanicalPipe pipe = tile.getTransmitter();
            if (pipe.hasTransmitterNetwork()) {
                FluidNetwork network = pipe.getTransmitterNetwork();
                return !network.lastFluid.isEmpty() && !network.fluidTank.isEmpty() && network.currentScale > 0;
            }
        }
        return false;
    }

    private MekanismRenderer.Model3D getModel(FluidStack fluid, int stage, boolean hasSides) {
        return getModel(null, fluid, stage, hasSides);
    }

    private MekanismRenderer.Model3D getModel(Direction side, FluidStack fluid, int stage) {
        return getModel(side, fluid, stage, false);
    }

    private MekanismRenderer.Model3D getModel(@Nullable Direction side, FluidStack fluid, int stage, boolean renderBase) {
        int sideOrdinal;
        if (side == null) {
            sideOrdinal = renderBase ? 7 : 6;
        } else {
            sideOrdinal = side.ordinal();
        }
        Int2ObjectMap<MekanismRenderer.Model3D> modelMap = cachedLiquids.computeIfAbsent(sideOrdinal, s -> new HashMap<>())
                .computeIfAbsent(fluid, f -> new Int2ObjectOpenHashMap<>());
        MekanismRenderer.Model3D model = modelMap.get(stage);
        if (model == null) {
            model = new MekanismRenderer.Model3D().setTexture(MekanismRenderer.getFluidTexture(fluid, MekanismRenderer.FluidTextureType.STILL));
            float stageRatio = (stage / (float) stages) * height;
            if (side == null) {
                float min;
                float max;
                if (renderBase) {
                    min = 0.25F + offset;
                    max = 0.75F - offset;
                } else {
                    min = 0.5F - stageRatio / 2;
                    max = 0.5F + stageRatio / 2;
                }
                return model.xBounds(min, max)
                        .yBounds(0.25F + offset, 0.25F + offset + stageRatio)
                        .zBounds(min, max);
            }
            model.setSideRender(side, false)
                    .setSideRender(side.getOpposite(), false);
            if (side.getAxis().isHorizontal()) {
                model.yBounds(0.25F + offset, 0.25F + offset + stageRatio);
                if (side.getAxis() == Direction.Axis.Z) {
                    return setHorizontalBounds(side, model::xBounds, model::zBounds);
                }
                return setHorizontalBounds(side, model::zBounds, model::xBounds);
            }
            float min = 0.5F - stageRatio / 2;
            float max = 0.5F + stageRatio / 2;
            model.xBounds(min, max)
                    .zBounds(min, max);
            if (side == Direction.DOWN) {
                model.yBounds(0, 0.25F + offset);
            } else {
                model.yBounds(0.25F + offset + stageRatio, 1);
            }
            modelMap.put(stage, model);
        }
        return model;
    }

    private static MekanismRenderer.Model3D setHorizontalBounds(Direction horizontal, MekanismRenderer.Model3D.ModelBoundsSetter axisBased, MekanismRenderer.Model3D.ModelBoundsSetter directionBased) {
        axisBased.set(0.25F + offset, 0.75F - offset);
        if (horizontal.getAxisDirection() == Direction.AxisDirection.POSITIVE) {
            return directionBased.set(0.75F - offset, 1);
        }
        return directionBased.set(0, 0.25F + offset);
    }
}