package github.mgrlyz.mgsoddities.common.tile.transmitter;

import com.mojang.serialization.DataResult;
import github.mgrlyz.mgsoddities.api.tier.AdvanceTier;
import github.mgrlyz.mgsoddities.common.content.network.transmitter.MGsOdditiesPressurizedTube;
import github.mgrlyz.mgsoddities.common.registries.block.MGsOdditiesBlocks;
import mekanism.api.IContentsListener;
import mekanism.api.MekanismAPI;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.IChemicalTank;
import mekanism.api.math.MathUtils;
import mekanism.api.radiation.IRadiationManager;
import mekanism.common.Mekanism;
import mekanism.common.block.states.BlockStateHelper;
import mekanism.common.block.states.TransmitterType;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.capabilities.chemical.DynamicChemicalHandler;
import mekanism.common.capabilities.resolver.manager.ChemicalHandlerManager;
import mekanism.common.content.network.ChemicalNetwork;
import mekanism.common.content.network.transmitter.PressurizedTube;
import mekanism.common.integration.computer.IComputerTile;
import mekanism.common.integration.computer.annotation.ComputerMethod;
import mekanism.common.lib.transmitter.ConnectionType;
import mekanism.common.registration.impl.BlockRegistryObject;
import mekanism.common.tile.interfaces.ITileRadioactive;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

public class MGsOdditiesTileEntityPressurizedTube extends MGsOdditiesTileEntityTransmitter implements IComputerTile, ITileRadioactive {
    private final ChemicalHandlerManager chemicalHandlerManager;

    public MGsOdditiesTileEntityPressurizedTube(Holder<Block> blockProvider, BlockPos pos, BlockState state) {
        super(blockProvider, pos, state);
        Predicate<Direction> canExtract = this.getExtractPredicate();
        Predicate<Direction> canInsert = this.getInsertPredicate();
        this.addCapabilityResolver(this.chemicalHandlerManager = new ChemicalHandlerManager((direction) -> {
            MGsOdditiesPressurizedTube tube = this.getTransmitter();
            return (direction == null || tube.getConnectionTypeRaw(direction) != ConnectionType.NONE) && !tube.isRedstoneActivated() ? tube.getChemicalTanks(direction) : Collections.emptyList();
        }, new DynamicChemicalHandler(this::getChemicalTanks, canExtract, canInsert, (IContentsListener)null)));
    }

    protected MGsOdditiesPressurizedTube createTransmitter(Holder<Block> blockProvider) {
        return new MGsOdditiesPressurizedTube(blockProvider, this);
    }

    public MGsOdditiesPressurizedTube getTransmitter() {
        return (MGsOdditiesPressurizedTube)super.getTransmitter();
    }

    protected void onUpdateServer() {
        this.getTransmitter().pullFromAcceptors();
        super.onUpdateServer();
    }

    public TransmitterType getTransmitterType() {
        return TransmitterType.PRESSURIZED_TUBE;
    }

    protected @NotNull BlockState upgradeResult(@NotNull BlockState current, @NotNull AdvanceTier tier) {
        BlockRegistryObject var10001;
        switch (tier) {
            case PARAGON -> var10001 = MGsOdditiesBlocks.PARAGON_PRESSURIZED_TUBE;
            case APOTHEOSIS -> var10001 = MGsOdditiesBlocks.APOTHEOSIS_PRESSURIZED_TUBE;
            default -> throw new MatchException((String)null, (Throwable)null);
        }

        return BlockStateHelper.copyStateData(current, var10001);
    }

    public @NotNull CompoundTag getUpdateTag(HolderLookup.@NotNull Provider provider) {
        CompoundTag updateTag = super.getUpdateTag(provider);
        if (this.getTransmitter().hasTransmitterNetwork()) {
            ChemicalNetwork network = (ChemicalNetwork)this.getTransmitter().getTransmitterNetwork();
            if (!network.lastChemical.is(MekanismAPI.EMPTY_CHEMICAL_KEY)) {
                DataResult<Tag> encoded = Chemical.HOLDER_CODEC.encodeStart(provider.createSerializationContext(NbtOps.INSTANCE), network.lastChemical);
                if (encoded.isSuccess()) {
                    updateTag.put("chemical", (Tag)encoded.getOrThrow());
                } else {
                    encoded.ifError((error) -> Mekanism.logger.warn("Failed to encode last chemical: {}", error.message()));
                }
            }

            updateTag.putFloat("scale", network.currentScale);
        }

        return updateTag;
    }

    public float getRadiationScale() {
        if (IRadiationManager.INSTANCE.isRadiationEnabled()) {
            PressurizedTube tube = this.getTransmitter();
            if (this.isRemote()) {
                if (tube.hasTransmitterNetwork()) {
                    ChemicalNetwork network = (ChemicalNetwork)tube.getTransmitterNetwork();
                    if (!network.lastChemical.is(MekanismAPI.EMPTY_CHEMICAL_KEY) && !network.getChemicalTank().isEmpty() && ((Chemical)network.lastChemical.value()).isRadioactive()) {
                        return network.currentScale;
                    }
                }
            } else {
                IChemicalTank gasTank = tube.getChemicalTank();
                if (!gasTank.isEmpty() && gasTank.getStack().isRadioactive()) {
                    return (float)gasTank.getStored() / (float)gasTank.getCapacity();
                }
            }
        }

        return 0.0F;
    }

    public int getRadiationParticleCount() {
        return MathUtils.clampToInt((double)(3.0F * this.getRadiationScale()));
    }

    private List<IChemicalTank> getChemicalTanks(@Nullable Direction side) {
        return this.chemicalHandlerManager.getContainers(side);
    }

    public void sideChanged(@NotNull Direction side, @NotNull ConnectionType old, @NotNull ConnectionType type) {
        super.sideChanged(side, old, type);
        if (type == ConnectionType.NONE) {
            this.invalidateCapability(Capabilities.CHEMICAL.block(), side);
        } else if (old == ConnectionType.NONE) {
            this.invalidateCapabilities();
        }

    }

    public void redstoneChanged(boolean powered) {
        super.redstoneChanged(powered);
        if (powered) {
            this.invalidateCapabilityAll(Capabilities.CHEMICAL.block());
        } else {
            this.invalidateCapabilities();
        }

    }

    public String getComputerName() {
        return this.getTransmitter().getTier().getBaseTier().getLowerName() + "PressurizedTube";
    }

    @ComputerMethod
    ChemicalStack getBuffer() {
        return this.getTransmitter().getBufferWithFallback();
    }

    @ComputerMethod
    long getCapacity() {
        MGsOdditiesPressurizedTube tube = this.getTransmitter();
        return tube.hasTransmitterNetwork() ? ((ChemicalNetwork)tube.getTransmitterNetwork()).getCapacity() : tube.getCapacity();
    }

    @ComputerMethod
    long getNeeded() {
        return this.getCapacity() - this.getBuffer().getAmount();
    }

    @ComputerMethod
    double getFilledPercentage() {
        return (double)this.getBuffer().getAmount() / (double)this.getCapacity();
    }
}