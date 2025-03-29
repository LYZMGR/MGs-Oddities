package github.mgrlyz.mgsoddities.common.tile.transmitter;

import github.mgrlyz.mgsoddities.api.tier.AdvanceTier;
import github.mgrlyz.mgsoddities.common.content.network.transmitter.MGsOdditiesMechanicalPipe;
import github.mgrlyz.mgsoddities.common.registries.block.MGsOdditiesBlocks;
import mekanism.api.IContentsListener;
import mekanism.api.fluid.IExtendedFluidTank;
import mekanism.common.block.states.BlockStateHelper;
import mekanism.common.block.states.TransmitterType;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.capabilities.fluid.DynamicFluidHandler;
import mekanism.common.capabilities.resolver.manager.FluidHandlerManager;
import mekanism.common.content.network.FluidNetwork;
import mekanism.common.integration.computer.IComputerTile;
import mekanism.common.integration.computer.annotation.ComputerMethod;
import mekanism.common.lib.transmitter.ConnectionType;
import mekanism.common.registration.impl.BlockRegistryObject;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class MGsOdditiesTileEntityMechanicalPipe extends MGsOdditiesTileEntityTransmitter implements IComputerTile {
    private final FluidHandlerManager fluidHandlerManager;

    public MGsOdditiesTileEntityMechanicalPipe(Holder<Block> blockProvider, BlockPos pos, BlockState state) {
        super(blockProvider, pos, state);
        this.addCapabilityResolver(this.fluidHandlerManager = new FluidHandlerManager((direction) -> {
            MGsOdditiesMechanicalPipe pipe = this.getTransmitter();
            return (direction == null || pipe.getConnectionTypeRaw(direction) != ConnectionType.NONE) && !pipe.isRedstoneActivated() ? pipe.getFluidTanks(direction) : Collections.emptyList();
        }, new DynamicFluidHandler(this::getFluidTanks, this.getExtractPredicate(), this.getInsertPredicate(), (IContentsListener)null)));
    }

    protected MGsOdditiesMechanicalPipe createTransmitter(Holder<Block> blockProvider) {
        return new MGsOdditiesMechanicalPipe(blockProvider, this);
    }

    public MGsOdditiesMechanicalPipe getTransmitter() {
        return (MGsOdditiesMechanicalPipe)super.getTransmitter();
    }

    protected void onUpdateServer() {
        this.getTransmitter().pullFromAcceptors();
        super.onUpdateServer();
    }

    public TransmitterType getTransmitterType() {
        return TransmitterType.MECHANICAL_PIPE;
    }

    protected @NotNull BlockState upgradeResult(@NotNull BlockState current, @NotNull AdvanceTier tier) {
        BlockRegistryObject var10001;
        switch (tier) {
            case PARAGON -> var10001 = MGsOdditiesBlocks.PARAGON_UNIVERSAL_CABLE;
            case APOTHEOSIS -> var10001 = MGsOdditiesBlocks.APOTHEOSIS_UNIVERSAL_CABLE;
            default -> throw new MatchException((String)null, (Throwable)null);
        }

        return BlockStateHelper.copyStateData(current, var10001);
    }

    public @NotNull CompoundTag getUpdateTag(HolderLookup.@NotNull Provider provider) {
        CompoundTag updateTag = super.getUpdateTag(provider);
        if (this.getTransmitter().hasTransmitterNetwork()) {
            FluidNetwork network = (FluidNetwork)this.getTransmitter().getTransmitterNetwork();
            updateTag.put("fluid", network.lastFluid.saveOptional(provider));
            updateTag.putFloat("scale", network.currentScale);
        }

        return updateTag;
    }

    private List<IExtendedFluidTank> getFluidTanks(@Nullable Direction side) {
        return this.fluidHandlerManager.getContainers(side);
    }

    public void sideChanged(@NotNull Direction side, @NotNull ConnectionType old, @NotNull ConnectionType type) {
        super.sideChanged(side, old, type);
        if (type == ConnectionType.NONE) {
            this.invalidateCapability(Capabilities.FLUID.block(), side);
        } else if (old == ConnectionType.NONE) {
            this.invalidateCapabilities();
        }

    }

    public void redstoneChanged(boolean powered) {
        super.redstoneChanged(powered);
        if (powered) {
            this.invalidateCapabilityAll(Capabilities.FLUID.block());
        } else {
            this.invalidateCapabilities();
        }

    }

    public String getComputerName() {
        return this.getTransmitter().getTier().getBaseTier().getLowerName() + "MechanicalPipe";
    }

    @ComputerMethod
    FluidStack getBuffer() {
        return this.getTransmitter().getBufferWithFallback();
    }

    @ComputerMethod
    long getCapacity() {
        MGsOdditiesMechanicalPipe pipe = this.getTransmitter();
        return pipe.hasTransmitterNetwork() ? ((FluidNetwork)pipe.getTransmitterNetwork()).getCapacity() : pipe.getCapacity();
    }

    @ComputerMethod
    long getNeeded() {
        return this.getCapacity() - (long)this.getBuffer().getAmount();
    }

    @ComputerMethod
    double getFilledPercentage() {
        return (double)this.getBuffer().getAmount() / (double)this.getCapacity();
    }
}