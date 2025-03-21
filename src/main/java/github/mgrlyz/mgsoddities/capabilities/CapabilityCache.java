package github.mgrlyz.mgsoddities.capabilities;

import github.mgrlyz.mgsoddities.MGsOddities;
import github.mgrlyz.mgsoddities.capabilities.resolver.ICapabilityResolver;
import mekanism.api.annotations.NothingNullByDefault;
import mekanism.common.tile.component.TileComponentConfig;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.capabilities.BlockCapability;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@NothingNullByDefault
public class CapabilityCache {

    private final Map<BlockCapability<?, @Nullable Direction>, ICapabilityResolver<@Nullable Direction>> capabilityResolvers = new IdentityHashMap<>();
    private final List<ICapabilityResolver<?>> uniqueResolvers = new ArrayList<>();
    @Nullable
    private TileComponentConfig config;

    public void addCapabilityResolver(ICapabilityResolver<@Nullable Direction> resolver) {
        uniqueResolvers.add(resolver);
        List<BlockCapability<?, @Nullable Direction>> supportedCapabilities = resolver.getSupportedCapabilities();
        for (BlockCapability<?, @Nullable Direction> supportedCapability : supportedCapabilities) {
            if (capabilityResolvers.put(supportedCapability, resolver) != null) {
                MGsOddities.logger.warn("Multiple capability resolvers registered for {}. Overriding", supportedCapability.name(), new Exception());
            }
        }
    }

    public void addConfigComponent(TileComponentConfig config) {
        if (this.config != null) {
            MGsOddities.logger.warn("Config component already registered. Overriding", new Exception());
        }
        this.config = config;
    }

    public boolean isCapabilityDisabled(BlockCapability<?, @Nullable Direction> capability, @Nullable Direction side) {
        return config != null && config.isCapabilityDisabled(capability, side);
    }

    @Nullable
    public ICapabilityResolver<@Nullable Direction> getResolver(BlockCapability<?, @Nullable Direction> capability) {
        return capabilityResolvers.get(capability);
    }

    public ICapabilityResolver<@Nullable Direction> getResolver(BlockCapability<?, @Nullable Direction> capability,
                                                                Supplier<ICapabilityResolver<@Nullable Direction>> resolver) {
        ICapabilityResolver<@Nullable Direction> knownResolver = getResolver(capability);
        if (knownResolver == null) {
            knownResolver = resolver.get();
            addCapabilityResolver(knownResolver);
        }
        return knownResolver;
    }

    public void invalidate(BlockCapability<?, @Nullable Direction> capability, @Nullable Direction side) {
        ICapabilityResolver<@Nullable Direction> capabilityResolver = capabilityResolvers.get(capability);
        if (capabilityResolver != null) {
            capabilityResolver.invalidate(capability, side);
        }
    }

    public void invalidateAll(BlockCapability<?, @Nullable Direction> capability) {
        ICapabilityResolver<@Nullable Direction> capabilityResolver = capabilityResolvers.get(capability);
        if (capabilityResolver != null) {
            capabilityResolver.invalidateAll();
        }
    }

    public void invalidateAll() {
        uniqueResolvers.forEach(ICapabilityResolver::invalidateAll);
    }
}