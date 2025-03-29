package github.mgrlyz.mgsoddities.common.tile.transmitter;

import github.mgrlyz.mgsoddities.api.tier.AdvanceTier;
import github.mgrlyz.mgsoddities.common.content.network.transmitter.MGsOdditiesThermodynamicConductor;
import github.mgrlyz.mgsoddities.common.registries.block.MGsOdditiesBlocks;
import mekanism.api.heat.IHeatCapacitor;
import mekanism.api.heat.IMekanismHeatHandler;
import mekanism.common.block.states.BlockStateHelper;
import mekanism.common.block.states.TransmitterType;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.capabilities.resolver.manager.HeatHandlerManager;
import mekanism.common.lib.transmitter.ConnectionType;
import mekanism.common.registration.impl.BlockRegistryObject;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class MGsOdditiesTileEntityThermodynamicConductor extends MGsOdditiesTileEntityTransmitter {
    private final HeatHandlerManager heatHandlerManager;

    public MGsOdditiesTileEntityThermodynamicConductor(Holder<Block> blockProvider, BlockPos pos, BlockState state) {
        super(blockProvider, pos, state);
        this.addCapabilityResolver(this.heatHandlerManager = new HeatHandlerManager((direction) -> {
            MGsOdditiesThermodynamicConductor conductor = this.getTransmitter();
            return (direction == null || conductor.getConnectionTypeRaw(direction) != ConnectionType.NONE) && !conductor.isRedstoneActivated() ? conductor.getHeatCapacitors(direction) : Collections.emptyList();
        }, new IMekanismHeatHandler() {
            public @NotNull List<IHeatCapacitor> getHeatCapacitors(@Nullable Direction side) {
                return MGsOdditiesTileEntityThermodynamicConductor.this.heatHandlerManager.getContainers(side);
            }

            public void onContentsChanged() {
            }
        }));
    }

    protected MGsOdditiesThermodynamicConductor createTransmitter(Holder<Block> blockProvider) {
        return new MGsOdditiesThermodynamicConductor(blockProvider, this);
    }

    public MGsOdditiesThermodynamicConductor getTransmitter() {
        return (MGsOdditiesThermodynamicConductor)super.getTransmitter();
    }

    public TransmitterType getTransmitterType() {
        return TransmitterType.THERMODYNAMIC_CONDUCTOR;
    }

    protected @NotNull BlockState upgradeResult(@NotNull BlockState current, @NotNull AdvanceTier tier) {
        BlockRegistryObject var10001;
        switch (tier) {
            case PARAGON -> var10001 = MGsOdditiesBlocks.PARAGON_THERMODYNAMIC_CONDUCTOR;
            case APOTHEOSIS -> var10001 = MGsOdditiesBlocks.APOTHEOSIS_THERMODYNAMIC_CONDUCTOR;
            default -> throw new MatchException((String)null, (Throwable)null);
        }

        return BlockStateHelper.copyStateData(current, var10001);
    }

    public void sideChanged(@NotNull Direction side, @NotNull ConnectionType old, @NotNull ConnectionType type) {
        super.sideChanged(side, old, type);
        if (type == ConnectionType.NONE) {
            this.invalidateCapability(Capabilities.HEAT, side);
        } else if (old == ConnectionType.NONE) {
            this.invalidateCapabilities();
        }

    }

    public void redstoneChanged(boolean powered) {
        super.redstoneChanged(powered);
        if (powered) {
            this.invalidateCapabilityAll(Capabilities.HEAT);
        } else {
            this.invalidateCapabilities();
        }

    }
}