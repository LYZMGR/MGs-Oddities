package github.mgrlyz.mgsoddities.common.content.network.transmitter;

import github.mgrlyz.mgsoddities.common.capabilities.heat.MGsOdditiesVariableHeatCapacitor;
import github.mgrlyz.mgsoddities.common.tier.transmitter.TCTier;
import github.mgrlyz.mgsoddities.common.tile.transmitter.MGsOdditiesTileEntityTransmitter;
import github.mgrlyz.mgsoddities.common.util.IMGsOdditiesUpgradeableTransmitter;
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
import java.util.Objects;

public class MGsOdditiesThermodynamicConductor extends ThermodynamicConductor implements ITileHeatHandler, IMGsOdditiesUpgradeableTransmitter<ThermodynamicConductorUpgradeData> {
    private final CachedAmbientTemperature ambientTemperature = new CachedAmbientTemperature(this::getLevel, this::getBlockPos);
    public final ConductorTier tier;
    private double clientTemperature = (double)-1.0F;
    private final List<IHeatCapacitor> capacitors;
    public final MGsOdditiesVariableHeatCapacitor buffer;

    public MGsOdditiesThermodynamicConductor(Holder<Block> blockProvider, MGsOdditiesTileEntityTransmitter tile) {
        super(blockProvider, tile);
        this.tier = (ConductorTier) Attribute.getTier(blockProvider, ConductorTier.class);
        this.buffer = MGsOdditiesVariableHeatCapacitor.create(TCTier.getHeatCapacity(this.tier), TCTier.getConduction(this.tier), TCTier.getConductionInsulation(this.tier), this.ambientTemperature, this);
        this.capacitors = Collections.singletonList(this.buffer);
    }

    protected AbstractAcceptorCache<IHeatHandler, ?> createAcceptorCache() {
        return super.createAcceptorCache();
    }

    public AcceptorCache<IHeatHandler> getAcceptorCache() {
        return super.getAcceptorCache();
    }

    public void takeShare() {
        super.takeShare();
    }

    protected boolean isValidAcceptor(@Nullable BlockEntity tile, Direction side) {
        return this.getAcceptorCache().getConnectedAcceptor(side) != null;
    }

    public @NotNull CompoundTag write(HolderLookup.Provider provider, @NotNull CompoundTag tag) {
        super.write(provider, tag);
        ContainerType.HEAT.saveTo(provider, tag, this.getHeatCapacitors((Direction)null));
        return tag;
    }

    public void read(HolderLookup.Provider provider, @NotNull CompoundTag tag) {
        super.read(provider, tag);
        ContainerType.HEAT.readFrom(provider, tag, this.getHeatCapacitors((Direction)null));
    }

    public @NotNull CompoundTag getReducedUpdateTag(HolderLookup.@NotNull Provider provider, CompoundTag updateTag) {
        updateTag = super.getReducedUpdateTag(provider, updateTag);
        updateTag.putDouble("temperature", this.buffer.getHeat());
        return updateTag;
    }

    public @NotNull List<IHeatCapacitor> getHeatCapacitors(Direction side) {
        return this.capacitors;
    }

    public boolean handleUpdateTag(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider provider) {
        boolean refreshModelData = super.handleUpdateTag(tag, provider);
        MGsOdditiesVariableHeatCapacitor var10002 = this.buffer;
        Objects.requireNonNull(var10002);
        NBTUtils.setDoubleIfPresent(tag, "temperature", var10002::setHeat);
        return refreshModelData;
    }

    public void onContentsChanged() {
        if (!this.isRemote()) {
            if (this.clientTemperature == (double)-1.0F) {
                this.clientTemperature = this.ambientTemperature.getAsDouble();
            }

            if (Math.abs(this.buffer.getTemperature() - this.clientTemperature) > this.buffer.getTemperature() / (double)20.0F) {
                this.clientTemperature = this.buffer.getTemperature();
                this.getTransmitterTile().sendUpdatePacket();
            }
        }

        this.getTransmitterTile().setChanged();
    }

    public double getAmbientTemperature(@NotNull Direction side) {
        return this.ambientTemperature.getTemperature(side);
    }

    public @Nullable IHeatHandler getAdjacent(@NotNull Direction side) {
        return connectionMapContainsSide(this.getAllCurrentConnections(), side) ? (IHeatHandler)this.getAcceptorCache().getConnectedAcceptor(side) : null;
    }

    public double incrementAdjacentTransfer(double currentAdjacentTransfer, double tempToTransfer, @NotNull Direction side) {
        if (tempToTransfer > (double)0.0F && this.hasTransmitterNetwork()) {
            HeatNetwork transmitterNetwork = (HeatNetwork)this.getTransmitterNetwork();
            ThermodynamicConductor adjacent = (ThermodynamicConductor)transmitterNetwork.getTransmitter(this.getBlockPos().relative(side));
            if (adjacent != null) {
                return currentAdjacentTransfer;
            }
        }

        return super.incrementAdjacentTransfer(currentAdjacentTransfer, tempToTransfer, side);
    }

    public @Nullable ThermodynamicConductorUpgradeData getUpgradeData() {
        return super.getUpgradeData();
    }

    public boolean dataTypeMatches(@NotNull TransmitterUpgradeData data) {
        return super.dataTypeMatches(data);
    }

    public void parseUpgradeData(@NotNull ThermodynamicConductorUpgradeData data) {
        super.parseUpgradeData(data);
    }
}
