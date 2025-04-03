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
        addCapabilityResolver(new MGsOdditiesTileEntityLogisticalTransporterBase.TransporterCapabilityResolver());
    }

    @Override
    protected abstract LogisticalTransporterBase createTransmitter(Holder<Block> blockProvider);

    @Override
    public LogisticalTransporterBase getTransmitter() {
        return (LogisticalTransporterBase) super.getTransmitter();
    }

    public static void tickClient(Level level, BlockPos pos, BlockState state, MGsOdditiesTileEntityLogisticalTransporterBase transmitter) {
        transmitter.getTransmitter().onUpdateClient();
    }

    @Override
    public void onUpdateServer() {
        super.onUpdateServer();
        getTransmitter().onUpdateServer();
    }

    @Override
    public void blockRemoved() {
        super.blockRemoved();
        if (!isRemote()) {
            LogisticalTransporterBase transporter = getTransmitter();
            if (!transporter.isUpgrading()) {
                for (TransporterStack stack : transporter.getTransit()) {
                    TransporterUtils.drop(transporter, stack);
                }
            }
        }
    }

    @Override
    public void sideChanged(@NotNull Direction side, @NotNull ConnectionType old, @NotNull ConnectionType type) {
        super.sideChanged(side, old, type);
        if (type == ConnectionType.NONE && old != ConnectionType.PUSH || type == ConnectionType.PUSH && old != ConnectionType.NONE) {
            invalidateCapability(Capabilities.ITEM.block(), side);
        } else if (old == ConnectionType.NONE && type != ConnectionType.PUSH || old == ConnectionType.PUSH && type != ConnectionType.NONE) {
            invalidateCapabilities();
        }
    }

    @NothingNullByDefault
    private class TransporterCapabilityResolver implements ICapabilityResolver<@Nullable Direction> {

        private static final List<BlockCapability<?, @Nullable Direction>> SUPPORTED_CAPABILITY = Collections.singletonList(Capabilities.ITEM.block());

        private final Map<Direction, CursedTransporterItemHandler> cursedHandlers = new EnumMap<>(Direction.class);
        private final Map<Direction, IItemHandler> handlers = new EnumMap<>(Direction.class);

        @Override
        public List<BlockCapability<?, @Nullable Direction>> getSupportedCapabilities() {
            return SUPPORTED_CAPABILITY;
        }

        @Nullable
        @Override
        public <T> T resolve(BlockCapability<T, @Nullable Direction> capability, @Nullable Direction side) {
            if (side == null) {
                return null;
            }
            IItemHandler cachedCapability = handlers.get(side);
            if (cachedCapability == null) {
                LogisticalTransporterBase transporter = getTransmitter();
                if (transporter.exposesInsertCap(side)) {
                    CursedTransporterItemHandler cached = cursedHandlers.get(side);
                    if (cached == null) {
                        cached = new CursedTransporterItemHandler(transporter, WorldUtils.relativePos(getWorldPositionLong(), side), () -> level == null ? -1 : level.getGameTime());
                        cursedHandlers.put(side, cached);
                    }
                    handlers.put(side, cached);
                    return (T) cached;
                }
            }
            return (T) cachedCapability;
        }

        @Override
        public void invalidate(BlockCapability<?, @Nullable Direction> capability, @Nullable Direction side) {
            if (side != null) {
                handlers.remove(side);
            }
        }

        @Override
        public void invalidateAll() {
            handlers.clear();
        }
    }
}