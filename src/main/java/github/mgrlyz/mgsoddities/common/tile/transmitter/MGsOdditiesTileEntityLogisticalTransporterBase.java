package github.mgrlyz.mgsoddities.common.tile.transmitter;

import mekanism.api.annotations.NothingNullByDefault;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.capabilities.item.CursedTransporterItemHandler;
import mekanism.common.capabilities.resolver.ICapabilityResolver;
import mekanism.common.content.network.transmitter.LogisticalTransporterBase;
import mekanism.common.content.transporter.TransporterStack;
import mekanism.common.lib.transmitter.ConnectionType;
import mekanism.common.util.TransporterUtils;
import mekanism.common.util.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public abstract class MGsOdditiesTileEntityLogisticalTransporterBase extends MGsOdditiesTileEntityTransmitter {
    public MGsOdditiesTileEntityLogisticalTransporterBase(Holder<Block> blockProvider, BlockPos pos, BlockState state) {
        super(blockProvider, pos, state);
        this.addCapabilityResolver(new TransporterCapabilityResolver());
    }

    protected abstract LogisticalTransporterBase createTransmitter(Holder<Block> var1);

    public LogisticalTransporterBase getTransmitter() {
        return (LogisticalTransporterBase)super.getTransmitter();
    }

    public static void tickClient(Level level, BlockPos pos, BlockState state, MGsOdditiesTileEntityLogisticalTransporterBase transmitter) {
        transmitter.getTransmitter().onUpdateClient();
    }

    public void onUpdateServer() {
        super.onUpdateServer();
        this.getTransmitter().onUpdateServer();
    }

    public void blockRemoved() {
        super.blockRemoved();
        if (!this.isRemote()) {
            LogisticalTransporterBase transporter = this.getTransmitter();
            if (!transporter.isUpgrading()) {
                for(TransporterStack stack : transporter.getTransit()) {
                    TransporterUtils.drop(transporter, stack);
                }
            }
        }

    }

    public void sideChanged(@NotNull Direction side, @NotNull ConnectionType old, @NotNull ConnectionType type) {
        super.sideChanged(side, old, type);
        if ((type != ConnectionType.NONE || old == ConnectionType.PUSH) && (type != ConnectionType.PUSH || old == ConnectionType.NONE)) {
            if (old == ConnectionType.NONE && type != ConnectionType.PUSH || old == ConnectionType.PUSH && type != ConnectionType.NONE) {
                this.invalidateCapabilities();
            }
        } else {
            this.invalidateCapability(Capabilities.ITEM.block(), side);
        }

    }

    @NothingNullByDefault
    private class TransporterCapabilityResolver implements ICapabilityResolver<@Nullable Direction> {
        private static final List<BlockCapability<?, @Nullable Direction>> SUPPORTED_CAPABILITY;
        private final Map<Direction, CursedTransporterItemHandler> cursedHandlers = new EnumMap(Direction.class);
        private final Map<Direction, IItemHandler> handlers = new EnumMap(Direction.class);

        private TransporterCapabilityResolver() {
        }

        public List<BlockCapability<?, @Nullable Direction>> getSupportedCapabilities() {
            return SUPPORTED_CAPABILITY;
        }

        public <T> @Nullable T resolve(BlockCapability<T, @Nullable Direction> capability, @Nullable Direction side) {
            if (side == null) {
                return null;
            } else {
                IItemHandler cachedCapability = (IItemHandler)this.handlers.get(side);
                if (cachedCapability == null) {
                    LogisticalTransporterBase transporter = MGsOdditiesTileEntityLogisticalTransporterBase.this.getTransmitter();
                    if (transporter.exposesInsertCap(side)) {
                        CursedTransporterItemHandler cached = (CursedTransporterItemHandler)this.cursedHandlers.get(side);
                        if (cached == null) {
                            cached = new CursedTransporterItemHandler(transporter, WorldUtils.relativePos(MGsOdditiesTileEntityLogisticalTransporterBase.this.getWorldPositionLong(), side), () -> MGsOdditiesTileEntityLogisticalTransporterBase.this.level == null ? -1L : MGsOdditiesTileEntityLogisticalTransporterBase.this.level.getGameTime());
                            this.cursedHandlers.put(side, cached);
                        }

                        this.handlers.put(side, cached);
                        return (T)cached;
                    }
                }

                return (T)cachedCapability;
            }
        }

        public void invalidate(BlockCapability<?, @Nullable Direction> capability, @Nullable Direction side) {
            if (side != null) {
                this.handlers.remove(side);
            }

        }

        public void invalidateAll() {
            this.handlers.clear();
        }

        static {
            SUPPORTED_CAPABILITY = Collections.singletonList(Capabilities.ITEM.block());
        }
    }
}