package github.mgrlyz.mgsoddities.common.tile.transmitter;

import com.mojang.serialization.DataResult;
import github.mgrlyz.mgsoddities.api.tier.AdvanceTier;
import github.mgrlyz.mgsoddities.common.content.network.transmitter.MGsOdditiesPressurizedTube;
import github.mgrlyz.mgsoddities.common.registries.block.MGsOdditiesBlocks;
import mekanism.api.MekanismAPI;
import mekanism.api.SerializationConstants;
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
        Predicate<@Nullable Direction> canExtract = getExtractPredicate();
        Predicate<@Nullable Direction> canInsert = getInsertPredicate();
        addCapabilityResolver(chemicalHandlerManager = new ChemicalHandlerManager(direction -> {
            MGsOdditiesPressurizedTube tube = getTransmitter();
            if (direction != null && (tube.getConnectionTypeRaw(direction) == ConnectionType.NONE) || tube.isRedstoneActivated()) {
                return Collections.emptyList();
            }
            return tube.getChemicalTanks(direction);
        }, new DynamicChemicalHandler(this::getChemicalTanks, canExtract, canInsert, null)));
    }

    @Override
    protected MGsOdditiesPressurizedTube createTransmitter(Holder<Block> blockProvider) {
        return new MGsOdditiesPressurizedTube(blockProvider, this);
    }

    @Override
    public MGsOdditiesPressurizedTube getTransmitter() {
        return (MGsOdditiesPressurizedTube) super.getTransmitter();
    }

    @Override
    protected void onUpdateServer() {
        getTransmitter().pullFromAcceptors();
        super.onUpdateServer();
    }

    @Override
    public TransmitterType getTransmitterType() {
        return TransmitterType.PRESSURIZED_TUBE;
    }

    @NotNull
    @Override
    protected BlockState upgradeResult(@NotNull BlockState current, @NotNull AdvanceTier tier) {
        return BlockStateHelper.copyStateData(current, switch (tier) {
            case PARAGON -> MGsOdditiesBlocks.PARAGON_PRESSURIZED_TUBE;
            case APOTHEOSIS -> MGsOdditiesBlocks.APOTHEOSIS_PRESSURIZED_TUBE;
        });
    }

    @NotNull
    @Override
    public CompoundTag getUpdateTag(HolderLookup.@NotNull Provider provider) {
        CompoundTag updateTag = super.getUpdateTag(provider);
        if (getTransmitter().hasTransmitterNetwork()) {
            ChemicalNetwork network = getTransmitter().getTransmitterNetwork();
            if (!network.lastChemical.is(MekanismAPI.EMPTY_CHEMICAL_KEY)) {
                DataResult<Tag> encoded = Chemical.HOLDER_CODEC.encodeStart(provider.createSerializationContext(NbtOps.INSTANCE), network.lastChemical);
                if (encoded.isSuccess()) {
                    updateTag.put(SerializationConstants.CHEMICAL, encoded.getOrThrow());
                } else {
                    encoded.ifError(error -> Mekanism.logger.warn("Failed to encode last chemical: {}", error.message()));
                }
            }
            updateTag.putFloat(SerializationConstants.SCALE, network.currentScale);
        }
        return updateTag;
    }

    @Override
    public float getRadiationScale() {
        if (IRadiationManager.INSTANCE.isRadiationEnabled()) {
            PressurizedTube tube = getTransmitter();
            if (isRemote()) {
                if (tube.hasTransmitterNetwork()) {
                    ChemicalNetwork network = tube.getTransmitterNetwork();
                    if (!network.lastChemical.is(MekanismAPI.EMPTY_CHEMICAL_KEY) && !network.getChemicalTank().isEmpty() && network.lastChemical.value().isRadioactive()) {
                        return network.currentScale;
                    }
                }
            } else {
                IChemicalTank gasTank = tube.getChemicalTank();
                if (!gasTank.isEmpty() && gasTank.getStack().isRadioactive()) {
                    return gasTank.getStored() / (float) gasTank.getCapacity();
                }
            }
        }
        return 0;
    }

    @Override
    public int getRadiationParticleCount() {
        return MathUtils.clampToInt(3 * getRadiationScale());
    }

    private List<IChemicalTank> getChemicalTanks(@Nullable Direction side) {
        return chemicalHandlerManager.getContainers(side);
    }

    @Override
    public void sideChanged(@NotNull Direction side, @NotNull ConnectionType old, @NotNull ConnectionType type) {
        super.sideChanged(side, old, type);
        if (type == ConnectionType.NONE) {
            invalidateCapability(Capabilities.CHEMICAL.block(), side);
        } else if (old == ConnectionType.NONE) {
            invalidateCapabilities();
        }
    }

    @Override
    public void redstoneChanged(boolean powered) {
        super.redstoneChanged(powered);
        if (powered) {
            invalidateCapabilityAll(Capabilities.CHEMICAL.block());
        } else {
            invalidateCapabilities();
        }
    }

    @Override
    public String getComputerName() {
        return getTransmitter().getTier().getBaseTier().getLowerName() + "PressurizedTube";
    }

    @ComputerMethod
    ChemicalStack getBuffer() {
        return getTransmitter().getBufferWithFallback();
    }

    @ComputerMethod
    long getCapacity() {
        MGsOdditiesPressurizedTube tube = getTransmitter();
        return tube.hasTransmitterNetwork() ? tube.getTransmitterNetwork().getCapacity() : tube.getCapacity();
    }

    @ComputerMethod
    long getNeeded() {
        return getCapacity() - getBuffer().getAmount();
    }

    @ComputerMethod
    double getFilledPercentage() {
        return getBuffer().getAmount() / (double) getCapacity();
    }
}