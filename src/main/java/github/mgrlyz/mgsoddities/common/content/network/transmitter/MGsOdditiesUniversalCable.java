package github.mgrlyz.mgsoddities.common.content.network.transmitter;

import github.mgrlyz.mgsoddities.common.tier.transmitter.CTier;
import github.mgrlyz.mgsoddities.common.tile.transmitter.MGsOdditiesTileEntityTransmitter;
import github.mgrlyz.mgsoddities.common.util.IMGsOdditiesUpgradeableTransmitter;
import mekanism.api.Action;
import mekanism.api.AutomationType;
import mekanism.api.energy.IMekanismStrictEnergyHandler;
import mekanism.api.energy.IStrictEnergyHandler;
import mekanism.common.capabilities.energy.VariableCapacityEnergyContainer;
import mekanism.common.content.network.EnergyNetwork;
import mekanism.common.content.network.transmitter.UniversalCable;
import mekanism.common.lib.transmitter.ConnectionType;
import mekanism.common.lib.transmitter.acceptor.EnergyAcceptorCache;
import mekanism.common.upgrade.transmitter.TransmitterUpgradeData;
import mekanism.common.upgrade.transmitter.UniversalCableUpgradeData;
import mekanism.common.util.EnumUtils;
import mekanism.common.util.NBTUtils;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class MGsOdditiesUniversalCable extends UniversalCable implements IMekanismStrictEnergyHandler, IMGsOdditiesUpgradeableTransmitter<UniversalCableUpgradeData> {
    public MGsOdditiesUniversalCable(Holder<Block> blockProvider, MGsOdditiesTileEntityTransmitter tile) {
        super(blockProvider, tile);
    }

    public void pullFromAcceptors() {
        if (this.hasPullSide && this.getAvailablePull() > 0L) {
            EnergyAcceptorCache acceptorCache = this.getAcceptorCache();

            for(Direction side : EnumUtils.DIRECTIONS) {
                if (this.isConnectionType(side, ConnectionType.PULL)) {
                    IStrictEnergyHandler connectedAcceptor = (IStrictEnergyHandler)acceptorCache.getConnectedAcceptor(side);
                    if (connectedAcceptor != null) {
                        long received = connectedAcceptor.extractEnergy(this.getAvailablePull(), Action.SIMULATE);
                        if (received > 0L && this.takeEnergy(received, Action.SIMULATE) == 0L) {
                            long remainder = this.takeEnergy(received, Action.EXECUTE);
                            connectedAcceptor.extractEnergy(received - remainder, Action.EXECUTE);
                        }
                    }
                }
            }

        }
    }

    private long getAvailablePull() {
        return this.hasTransmitterNetwork() ? Math.min(this.getCapacity(), ((EnergyNetwork)this.getTransmitterNetwork()).energyContainer.getNeeded()) : Math.min(this.getCapacity(), this.buffer.getNeeded());
    }

    public @NotNull long getCapacityAsFloatingLong() {
        return CTier.getCapacityAsLong(this.tier);
    }

    public long getCapacity() {
        return CTier.getCapacityAsLong(this.tier);
    }

    private long takeEnergy(long amount, Action action) {
        return this.hasTransmitterNetwork() ? ((EnergyNetwork)this.getTransmitterNetwork()).energyContainer.insert(amount, action, AutomationType.INTERNAL) : this.buffer.insert(amount, action, AutomationType.INTERNAL);
    }

    protected void handleContentsUpdateTag(@NotNull EnergyNetwork network, @NotNull CompoundTag tag, @NotNull HolderLookup.@NotNull Provider provider) {
        super.handleContentsUpdateTag(network, tag, provider);
        VariableCapacityEnergyContainer var10002 = network.energyContainer;
        Objects.requireNonNull(var10002);
        NBTUtils.setLegacyEnergyIfPresent(tag, "energy", var10002::setEnergy);
        NBTUtils.setFloatIfPresent(tag, "scale", (scale) -> network.currentScale = scale);
    }

    public @Nullable UniversalCableUpgradeData getUpgradeData() {
        return super.getUpgradeData();
    }

    public boolean dataTypeMatches(@NotNull TransmitterUpgradeData data) {
        return super.dataTypeMatches(data);
    }

    public void parseUpgradeData(@NotNull UniversalCableUpgradeData data) {
        super.parseUpgradeData(data);
    }
}