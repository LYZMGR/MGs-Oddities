package github.mgrlyz.mgsoddities.common.content.network.transmitter;

import github.mgrlyz.mgsoddities.common.capabilities.heat.MGsOdditiesVariableHeatCapacitor;
import github.mgrlyz.mgsoddities.common.tier.transmitter.TCTier;
import github.mgrlyz.mgsoddities.common.tile.transmitter.MGsOdditiesTileEntityTransmitter;
import github.mgrlyz.mgsoddities.common.util.IMGsOdditiesUpgradeableTransmitter;
import mekanism.api.SerializationConstants;
import mekanism.api.heat.IHeatCapacitor;
import mekanism.api.heat.IHeatHandler;
import mekanism.common.attachments.containers.ContainerType;
import mekanism.common.block.attribute.Attribute;
import mekanism.common.capabilities.heat.CachedAmbientTemperature;
import mekanism.common.capabilities.heat.ITileHeatHandler;
import mekanism.common.content.network.HeatNetwork;
import mekanism.common.content.network.transmitter.ThermodynamicConductor;
import mekanism.common.lib.transmitter.acceptor.AbstractAcceptorCache;
import mekanism.common.lib.transmitter.acceptor.AcceptorCache;
import mekanism.common.tier.ConductorTier;
import mekanism.common.upgrade.transmitter.ThermodynamicConductorUpgradeData;
import mekanism.common.upgrade.transmitter.TransmitterUpgradeData;
import mekanism.common.util.NBTUtils;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class MGsOdditiesThermodynamicConductor extends ThermodynamicConductor implements ITileHeatHandler,
        IMGsOdditiesUpgradeableTransmitter<ThermodynamicConductorUpgradeData> {
    private final CachedAmbientTemperature ambientTemperature = new CachedAmbientTemperature(this::getLevel, this::getBlockPos);
    public final ConductorTier tier;
    private double clientTemperature = -1;
    private final List<IHeatCapacitor> capacitors;
    public final MGsOdditiesVariableHeatCapacitor buffer;
    public MGsOdditiesThermodynamicConductor(Holder<Block> blockProvider, MGsOdditiesTileEntityTransmitter tile) {
        super(blockProvider, tile);
        this.tier = Attribute.getTier(blockProvider, ConductorTier.class);
        buffer = MGsOdditiesVariableHeatCapacitor.create(TCTier.getHeatCapacity(tier), TCTier.getConduction(tier), TCTier.getConductionInsulation(tier), ambientTemperature, this);
        capacitors = Collections.singletonList(buffer);
    }

    @Override
    protected AbstractAcceptorCache<IHeatHandler, ?> createAcceptorCache() {
        return super.createAcceptorCache();
    }

    @Override
    public AcceptorCache<IHeatHandler> getAcceptorCache() {
        return super.getAcceptorCache();
    }

    @Override
    public void takeShare() {
        super.takeShare();
    }

    @Override
    protected boolean isValidAcceptor(@Nullable BlockEntity tile, Direction side) {
        return getAcceptorCache().getConnectedAcceptor(side) != null;
    }

    @NotNull
    @Override
    public CompoundTag write(HolderLookup.Provider provider, @NotNull CompoundTag tag) {
        super.write(provider, tag);
        ContainerType.HEAT.saveTo(provider, tag, getHeatCapacitors(null));
        return tag;
    }

    @Override
    public void read(HolderLookup.Provider provider, @NotNull CompoundTag tag) {
        super.read(provider, tag);
        ContainerType.HEAT.readFrom(provider, tag, getHeatCapacitors(null));
    }

    @NotNull
    @Override
    public CompoundTag getReducedUpdateTag(HolderLookup.@NotNull Provider provider, CompoundTag updateTag) {
        updateTag = super.getReducedUpdateTag(provider, updateTag);
        updateTag.putDouble(SerializationConstants.TEMPERATURE, buffer.getHeat());
        return updateTag;
    }

    @NotNull
    @Override
    public List<IHeatCapacitor> getHeatCapacitors(Direction side) {
        return capacitors;
    }

    @Override
    public boolean handleUpdateTag(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider provider) {
        boolean refreshModelData= super.handleUpdateTag(tag, provider);
        NBTUtils.setDoubleIfPresent(tag, SerializationConstants.TEMPERATURE, buffer::setHeat);
        return refreshModelData;
    }

    @Override
    public void onContentsChanged() {
        if (!isRemote()) {
            if (clientTemperature == -1) {
                clientTemperature = ambientTemperature.getAsDouble();
            }
            if (Math.abs(buffer.getTemperature() - clientTemperature) > (buffer.getTemperature() / 20)) {
                clientTemperature = buffer.getTemperature();
                getTransmitterTile().sendUpdatePacket();
            }
        }
        getTransmitterTile().setChanged();
    }

    @Override
    public double getAmbientTemperature(@NotNull Direction side) {
        return ambientTemperature.getTemperature(side);
    }

    @Nullable
    @Override
    public IHeatHandler getAdjacent(@NotNull Direction side) {
        if (connectionMapContainsSide(getAllCurrentConnections(), side)) {
            return getAcceptorCache().getConnectedAcceptor(side);
        }
        return null;
    }

    @Override
    public double incrementAdjacentTransfer(double currentAdjacentTransfer, double tempToTransfer, @NotNull Direction side) {
        if (tempToTransfer > 0 && hasTransmitterNetwork()) {
            HeatNetwork transmitterNetwork = getTransmitterNetwork();
            ThermodynamicConductor adjacent = transmitterNetwork.getTransmitter(getBlockPos().relative(side));
            if (adjacent != null) {
                return currentAdjacentTransfer;
            }
        }
        return super.incrementAdjacentTransfer(currentAdjacentTransfer, tempToTransfer, side);
    }

    @Nullable
    @Override
    public ThermodynamicConductorUpgradeData getUpgradeData() {
        return super.getUpgradeData();
    }

    @Override
    public boolean dataTypeMatches(@NotNull TransmitterUpgradeData data) {
        return super.dataTypeMatches(data);
    }

    @Override
    public void parseUpgradeData(@NotNull ThermodynamicConductorUpgradeData data) {
        super.parseUpgradeData(data);
    }
}