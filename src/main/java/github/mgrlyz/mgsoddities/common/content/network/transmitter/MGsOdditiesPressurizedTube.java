package github.mgrlyz.mgsoddities.common.content.network.transmitter;

import github.mgrlyz.mgsoddities.common.tier.transmitter.TTier;
import github.mgrlyz.mgsoddities.common.tile.transmitter.MGsOdditiesTileEntityTransmitter;
import mekanism.api.Action;
import mekanism.api.AutomationType;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.IChemicalHandler;
import mekanism.api.chemical.IChemicalTank;
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

    @Override
    public void pullFromAcceptors() {
        if (!hasPullSide || getAvailablePull() <= 0) {
            return;
        }
        AcceptorCache<IChemicalHandler> acceptorCache = getAcceptorCache();
        for (Direction side : EnumUtils.DIRECTIONS) {
            if (!isConnectionType(side, ConnectionType.PULL)) {
                continue;
            }
            IChemicalHandler connectedAcceptor = acceptorCache.getConnectedAcceptor(side);
            if (connectedAcceptor != null) {
                ChemicalStack bufferWithFallback = getBufferWithFallback();
                pullFromAcceptor(connectedAcceptor, bufferWithFallback, bufferWithFallback.isEmpty());
            }
        }
    }

    private boolean pullFromAcceptor(IChemicalHandler connectedAcceptor, ChemicalStack bufferWithFallback, boolean bufferIsEmpty) {
        if (connectedAcceptor == null) {
            return false;
        }
        long availablePull = getAvailablePull();
        ChemicalStack received;
        if (bufferIsEmpty) {
            received = connectedAcceptor.extractChemical(availablePull, Action.SIMULATE);
        } else {
            received = connectedAcceptor.extractChemical(bufferWithFallback.copyWithAmount(availablePull), Action.SIMULATE);
        }
        if (!received.isEmpty() && takeChemical(received, Action.SIMULATE).isEmpty()) {
            takeChemical(connectedAcceptor.extractChemical(received, Action.EXECUTE), Action.EXECUTE);
            return true;
        }
        return false;
    }

    private long getAvailablePull() {
        if (hasTransmitterNetwork()) {
            return Math.min(tier.getTubePullAmount(), getTransmitterNetwork().chemicalTank.getNeeded());
        }
        return Math.min(tier.getTubePullAmount(), chemicalTank.getNeeded());
    }

    @Nullable
    @Override
    public PressurizedTubeUpgradeData getUpgradeData() {
        return super.getUpgradeData();
    }

    @Override
    public boolean dataTypeMatches(@NotNull TransmitterUpgradeData data) {
        return data instanceof PressurizedTubeUpgradeData;
    }

    @Override
    public void parseUpgradeData(@NotNull PressurizedTubeUpgradeData data) {
        redstoneReactive = data.redstoneReactive;
        setConnectionTypesRaw(data.connectionTypes);
        takeChemical(data.contents, Action.EXECUTE);
    }

    @Override
    public long getCapacity() {
        return TTier.getTubeCapacity(tier);
    }

    private ChemicalStack takeChemical(ChemicalStack stack, Action action) {
        IChemicalTank tank;
        if (hasTransmitterNetwork()) {
            tank = getTransmitterNetwork().chemicalTank;
        } else {
            tank = chemicalTank;
        }
        return tank.insert(stack, action, AutomationType.INTERNAL);
    }
}