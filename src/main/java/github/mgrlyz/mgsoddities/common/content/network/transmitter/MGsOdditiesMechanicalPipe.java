package github.mgrlyz.mgsoddities.common.content.network.transmitter;

import github.mgrlyz.mgsoddities.common.tier.transmitter.PTier;
import github.mgrlyz.mgsoddities.common.tile.transmitter.MGsOdditiesTileEntityTransmitter;
import github.mgrlyz.mgsoddities.common.util.IMGsOdditiesUpgradeableTransmitter;
import mekanism.api.Action;
import mekanism.api.fluid.IMekanismFluidHandler;
import mekanism.common.content.network.FluidNetwork;
import mekanism.common.content.network.transmitter.MechanicalPipe;
import mekanism.common.lib.transmitter.ConnectionType;
import mekanism.common.lib.transmitter.acceptor.AcceptorCache;
import mekanism.common.upgrade.transmitter.MechanicalPipeUpgradeData;
import mekanism.common.upgrade.transmitter.TransmitterUpgradeData;
import mekanism.common.util.EnumUtils;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MGsOdditiesMechanicalPipe extends MechanicalPipe implements IMekanismFluidHandler, IMGsOdditiesUpgradeableTransmitter<MechanicalPipeUpgradeData> {
    public MGsOdditiesMechanicalPipe(Holder<Block> blockProvider, MGsOdditiesTileEntityTransmitter tile) {
        super(blockProvider, tile);
    }

    public void pullFromAcceptors() {
        if (this.hasPullSide && this.getAvailablePull() > 0) {
            AcceptorCache<IFluidHandler> acceptorCache = this.getAcceptorCache();

            for(Direction side : EnumUtils.DIRECTIONS) {
                if (this.isConnectionType(side, ConnectionType.PULL)) {
                    IFluidHandler connectedAcceptor = (IFluidHandler)acceptorCache.getConnectedAcceptor(side);
                    if (connectedAcceptor != null) {
                        FluidStack bufferWithFallback = this.getBufferWithFallback();
                        FluidStack received;
                        if (bufferWithFallback.isEmpty()) {
                            received = connectedAcceptor.drain(this.getAvailablePull(), FluidAction.SIMULATE);
                        } else {
                            received = connectedAcceptor.drain(bufferWithFallback.copyWithAmount(this.getAvailablePull()), FluidAction.SIMULATE);
                        }

                        if (!received.isEmpty() && this.takeFluid(received, Action.SIMULATE).isEmpty()) {
                            this.takeFluid(connectedAcceptor.drain(received, FluidAction.EXECUTE), Action.EXECUTE);
                        }
                    }
                }
            }

        }
    }

    private int getAvailablePull() {
        return this.hasTransmitterNetwork() ? Math.min(PTier.getPipePullAmount(this.tier), ((FluidNetwork)this.getTransmitterNetwork()).fluidTank.getNeeded()) : Math.min(PTier.getPipePullAmount(this.tier), this.buffer.getNeeded());
    }

    public long getCapacity() {
        return PTier.getPipeCapacity(this.tier);
    }

    public @Nullable MechanicalPipeUpgradeData getUpgradeData() {
        return super.getUpgradeData();
    }

    public boolean dataTypeMatches(@NotNull TransmitterUpgradeData data) {
        return super.dataTypeMatches(data);
    }

    public void parseUpgradeData(@NotNull MechanicalPipeUpgradeData data) {
        super.parseUpgradeData(data);
    }
}