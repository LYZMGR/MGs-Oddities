package github.mgrlyz.mgsoddities.common.content.network.transmitter;

import github.mgrlyz.mgsoddities.common.tier.transmitter.TTier;
import github.mgrlyz.mgsoddities.common.tile.transmitter.MGsOdditiesTileEntityTransmitter;
import mekanism.api.Action;
import mekanism.api.AutomationType;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.IChemicalHandler;
import mekanism.api.chemical.IChemicalTank;
import mekanism.common.content.network.ChemicalNetwork;
import mekanism.common.content.network.transmitter.PressurizedTube;
import mekanism.common.lib.transmitter.ConnectionType;
import mekanism.common.lib.transmitter.acceptor.AcceptorCache;
import mekanism.common.upgrade.transmitter.PressurizedTubeUpgradeData;
import mekanism.common.upgrade.transmitter.TransmitterUpgradeData;
import mekanism.common.util.EnumUtils;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MGsOdditiesPressurizedTube extends PressurizedTube {
    public MGsOdditiesPressurizedTube(Holder<Block> blockProvider, MGsOdditiesTileEntityTransmitter tile) {
        super(blockProvider, tile);
    }

    public void pullFromAcceptors() {
        if (this.hasPullSide && this.getAvailablePull() > 0L) {
            AcceptorCache<IChemicalHandler> acceptorCache = this.getAcceptorCache();

            for(Direction side : EnumUtils.DIRECTIONS) {
                if (this.isConnectionType(side, ConnectionType.PULL)) {
                    IChemicalHandler connectedAcceptor = (IChemicalHandler)acceptorCache.getConnectedAcceptor(side);
                    if (connectedAcceptor != null) {
                        ChemicalStack bufferWithFallback = this.getBufferWithFallback();
                        this.pullFromAcceptor(connectedAcceptor, bufferWithFallback, bufferWithFallback.isEmpty());
                    }
                }
            }

        }
    }

    private boolean pullFromAcceptor(IChemicalHandler connectedAcceptor, ChemicalStack bufferWithFallback, boolean bufferIsEmpty) {
        if (connectedAcceptor == null) {
            return false;
        } else {
            long availablePull = this.getAvailablePull();
            ChemicalStack received;
            if (bufferIsEmpty) {
                received = connectedAcceptor.extractChemical(availablePull, Action.SIMULATE);
            } else {
                received = connectedAcceptor.extractChemical(bufferWithFallback.copyWithAmount(availablePull), Action.SIMULATE);
            }

            if (!received.isEmpty() && this.takeChemical(received, Action.SIMULATE).isEmpty()) {
                this.takeChemical(connectedAcceptor.extractChemical(received, Action.EXECUTE), Action.EXECUTE);
                return true;
            } else {
                return false;
            }
        }
    }

    private long getAvailablePull() {
        return this.hasTransmitterNetwork() ? Math.min(this.tier.getTubePullAmount(), ((ChemicalNetwork)this.getTransmitterNetwork()).chemicalTank.getNeeded()) : Math.min(this.tier.getTubePullAmount(), this.chemicalTank.getNeeded());
    }

    public @Nullable PressurizedTubeUpgradeData getUpgradeData() {
        return super.getUpgradeData();
    }

    public boolean dataTypeMatches(@NotNull TransmitterUpgradeData data) {
        return data instanceof PressurizedTubeUpgradeData;
    }

    public void parseUpgradeData(@NotNull PressurizedTubeUpgradeData data) {
        this.redstoneReactive = data.redstoneReactive;
        this.setConnectionTypesRaw(data.connectionTypes);
        this.takeChemical(data.contents, Action.EXECUTE);
    }

    public long getCapacity() {
        return TTier.getTubeCapacity(this.tier);
    }

    private ChemicalStack takeChemical(ChemicalStack stack, Action action) {
        IChemicalTank tank;
        if (this.hasTransmitterNetwork()) {
            tank = ((ChemicalNetwork)this.getTransmitterNetwork()).chemicalTank;
        } else {
            tank = this.chemicalTank;
        }

        return tank.insert(stack, action, AutomationType.INTERNAL);
    }
}