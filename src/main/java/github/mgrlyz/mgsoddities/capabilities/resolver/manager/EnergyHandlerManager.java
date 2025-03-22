package github.mgrlyz.mgsoddities.capabilities.resolver.manager;

import github.mgrlyz.mgsoddities.capabilities.holder.energy.IEnergyContainerHolder;
import github.mgrlyz.mgsoddities.capabilities.proxy.ProxyStrictEnergyHandler;
import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.energy.IEnergyContainer;
import mekanism.api.energy.ISidedStrictEnergyHandler;
import mekanism.api.energy.IStrictEnergyHandler;
import mekanism.common.integration.energy.EnergyCompatUtils;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.capabilities.BlockCapability;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@NothingNullByDefault
public class EnergyHandlerManager implements ICapabilityHandlerManager<IEnergyContainer> {

    private final Map<Direction, Map<BlockCapability<?, @Nullable Direction>, Object>> cachedCapabilities;
    private final Map<BlockCapability<?, @Nullable Direction>, Object> cachedReadOnlyCapabilities;
    private final Map<Direction, IStrictEnergyHandler> handlers;
    private final ISidedStrictEnergyHandler baseHandler;
    private final boolean canHandle;
    @Nullable
    private IStrictEnergyHandler readOnlyHandler;
    @Nullable
    private final IEnergyContainerHolder holder;

    public EnergyHandlerManager(@Nullable IEnergyContainerHolder holder, ISidedStrictEnergyHandler baseHandler) {
        this.holder = holder;
        this.canHandle = this.holder != null;
        this.baseHandler = baseHandler;
        if (this.canHandle) {
            handlers = new EnumMap<>(Direction.class);
            cachedCapabilities = new EnumMap<>(Direction.class);
            cachedReadOnlyCapabilities = new IdentityHashMap<>();
        } else {
            handlers = Collections.emptyMap();
            cachedCapabilities = Collections.emptyMap();
            cachedReadOnlyCapabilities = Collections.emptyMap();
        }
    }

    @Override
    public boolean canHandle() {
        return canHandle;
    }

    @Override
    public List<IEnergyContainer> getContainers(@Nullable Direction side) {
        return canHandle() ? holder.getEnergyContainers(side) : Collections.emptyList();
    }

    @Override
    public List<BlockCapability<?, @Nullable Direction>> getSupportedCapabilities() {
        return EnergyCompatUtils.getLoadedEnergyCapabilities();
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public <T> T resolve(BlockCapability<T, @Nullable Direction> capability, @Nullable Direction side) {
        if (getContainers(side).isEmpty()) {
            return null;
        }
        if (side == null) {
            Object result = cachedReadOnlyCapabilities.get(capability);
            if (result == null) {
                if (readOnlyHandler == null) {
                    readOnlyHandler = new ProxyStrictEnergyHandler(baseHandler, null, holder);
                }
                result = EnergyCompatUtils.wrapStrictEnergyHandler(capability, readOnlyHandler);
                cachedReadOnlyCapabilities.put(capability, result);
            }
            return (T) result;
        }
        Map<BlockCapability<?, @Nullable Direction>, Object> cache = cachedCapabilities.computeIfAbsent(side, key -> new IdentityHashMap<>());
        Object result = cache.get(capability);
        if (result == null) {
            IStrictEnergyHandler handler = handlers.get(side);
            if (handler == null) {
                handler = new ProxyStrictEnergyHandler(baseHandler, side, holder);
                handlers.put(side, handler);
            }
            result = EnergyCompatUtils.wrapStrictEnergyHandler(capability, handler);
            cache.put(capability, result);
        }
        return (T) result;
    }

    @Override
    public void invalidate(BlockCapability<?, @Nullable Direction> capability, @Nullable Direction side) {
        if (side == null) {
            cachedReadOnlyCapabilities.remove(capability);
        } else {
            Map<BlockCapability<?, @Nullable Direction>, ?> cachedSide = cachedCapabilities.get(side);
            if (cachedSide != null) {
                cachedSide.remove(capability);
            }
        }
    }

    @Override
    public void invalidateAll() {
        cachedCapabilities.clear();
        cachedReadOnlyCapabilities.clear();
    }
}