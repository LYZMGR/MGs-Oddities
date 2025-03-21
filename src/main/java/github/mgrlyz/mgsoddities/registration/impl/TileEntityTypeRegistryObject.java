package github.mgrlyz.mgsoddities.registration.impl;

import github.mgrlyz.mgsoddities.registration.MGsOdditiesDeferredHolder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.BooleanSupplier;

public class TileEntityTypeRegistryObject<BE extends BlockEntity> extends MGsOdditiesDeferredHolder<BlockEntityType<?>, BlockEntityType<BE>> {

    @Nullable
    private List<TileEntityTypeRegistryObject.CapabilityData<BE, ?, ?>> capabilityProviders;
    @Nullable
    private BlockEntityTicker<BE> clientTicker;
    @Nullable
    private BlockEntityTicker<BE> serverTicker;

    public TileEntityTypeRegistryObject(ResourceKey<BlockEntityType<?>> key) {
        super(key);
    }

    @Nullable
    public BlockEntityTicker<BE> getTicker(boolean isClient) {
        return isClient ? clientTicker : serverTicker;
    }

    @ApiStatus.Internal
    void tickers(@Nullable BlockEntityTicker<BE> clientTicker, @Nullable BlockEntityTicker<BE> serverTicker) {
        this.clientTicker = clientTicker;
        this.serverTicker = serverTicker;
    }

    @ApiStatus.Internal
    void capabilities(@Nullable List<TileEntityTypeRegistryObject.CapabilityData<BE, ?, ?>> capabilityProviders) {
        this.capabilityProviders = capabilityProviders;
    }

    @ApiStatus.Internal
    void registerCapabilityProviders(RegisterCapabilitiesEvent event) {
        if (capabilityProviders != null) {
            for (TileEntityTypeRegistryObject.CapabilityData<BE, ?, ?> capabilityProvider : capabilityProviders) {
                capabilityProvider.registerProvider(event, value());
            }
        }
    }

    record CapabilityData<BE extends BlockEntity, CAP, CONTEXT>(BlockCapability<CAP, CONTEXT> capability, ICapabilityProvider<? super BE, CONTEXT, CAP> provider,
                                                                BooleanSupplier shouldApply) {

        private void registerProvider(RegisterCapabilitiesEvent event, BlockEntityType<BE> type) {
            if (shouldApply.getAsBoolean()) {
                event.registerBlockEntity(capability, type, provider);
            }
        }
    }
}