package github.mgrlyz.mgsoddities.registration.impl;

import com.google.common.base.Preconditions;
import github.mgrlyz.mgsoddities.block.attribute.Attribute;
import github.mgrlyz.mgsoddities.block.attribute.Attributes;
import github.mgrlyz.mgsoddities.capabilities.Capabilities;
import github.mgrlyz.mgsoddities.registration.MGsOdditiesDeferredRegister;
import github.mgrlyz.mgsoddities.tile.base.CapabilityTileEntity;
import github.mgrlyz.mgsoddities.tile.base.TileEntityMGsOddities;
import mekanism.api.functions.ConstantPredicates;
import mekanism.api.security.IBlockSecurityUtils;
import mekanism.common.integration.energy.EnergyCompatUtils;
import mekanism.common.registration.impl.BlockRegistryObject;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.function.Supplier;

public class TileEntityTypeDeferredRegister extends MGsOdditiesDeferredRegister<BlockEntityType<?>> {

    public TileEntityTypeDeferredRegister(String modid) {
        super(Registries.BLOCK_ENTITY_TYPE, modid, TileEntityTypeRegistryObject::new);
    }

    public <BE extends TileEntityMGsOddities> TileEntityTypeDeferredRegister.BlockEntityTypeBuilder<BE> mekBuilder(BlockRegistryObject<?, ?> block, BlockEntityType.BlockEntitySupplier<? extends BE> factory) {
        BooleanSupplier hasSecurity = () -> Attribute.has(block.getBlock(), Attributes.AttributeSecurity.class);
        TileEntityTypeDeferredRegister.BlockEntityTypeBuilder<BE> builder = this.<BE>builder(block, factory)
                .withSimple(IBlockSecurityUtils.INSTANCE.ownerCapability(), hasSecurity)
                .withSimple(IBlockSecurityUtils.INSTANCE.securityCapability(), hasSecurity)
                .with(Capabilities.CHEMICAL.block(), CapabilityTileEntity.CHEMICAL_HANDLER_PROVIDER)
                .with(Capabilities.HEAT, CapabilityTileEntity.HEAT_HANDLER_PROVIDER)
                .with(Capabilities.ITEM.block(), CapabilityTileEntity.ITEM_HANDLER_PROVIDER)
                .with(Capabilities.FLUID.block(), CapabilityTileEntity.FLUID_HANDLER_PROVIDER);
        EnergyCompatUtils.addBlockCapabilities(builder);
        return builder;
    }

    public <BE extends BlockEntity> TileEntityTypeDeferredRegister.BlockEntityTypeBuilder<BE> builder(BlockRegistryObject<?, ?> block, BlockEntityType.BlockEntitySupplier<? extends BE> factory) {
        return new TileEntityTypeDeferredRegister.BlockEntityTypeBuilder<>(block, factory);
    }

    @SuppressWarnings("unchecked")
    private <BE extends BlockEntity> TileEntityTypeRegistryObject<BE> registerMek(String name, Supplier<? extends BlockEntityType<BE>> sup) {
        return (TileEntityTypeRegistryObject<BE>) super.register(name, sup);
    }

    @Override
    public void register(@NotNull IEventBus bus) {
        super.register(bus);
        bus.addListener(this::registerCapabilities);
    }

    private void registerCapabilities(RegisterCapabilitiesEvent event) {
        for (DeferredHolder<BlockEntityType<?>, ? extends BlockEntityType<?>> entry : getEntries()) {
            if (entry instanceof TileEntityTypeRegistryObject<?> tileRO) {
                tileRO.registerCapabilityProviders(event);
            } else if (!FMLEnvironment.production) {
                throw new IllegalStateException("Expected entry to be a TileEntityTypeRegistryObject");
            }
        }
    }

    public class BlockEntityTypeBuilder<BE extends BlockEntity> {

        private final BlockRegistryObject<?, ?> block;
        private final BlockEntityType.BlockEntitySupplier<? extends BE> factory;
        private final List<TileEntityTypeRegistryObject.CapabilityData<BE, ?, ?>> capabilityProviders = new ArrayList<>();
        @Nullable
        private BlockEntityTicker<BE> clientTicker;
        @Nullable
        private BlockEntityTicker<BE> serverTicker;

        BlockEntityTypeBuilder(BlockRegistryObject<?, ?> block, BlockEntityType.BlockEntitySupplier<? extends BE> factory) {
            this.block = block;
            this.factory = factory;
        }

        public <CAP, CONTEXT> TileEntityTypeDeferredRegister.BlockEntityTypeBuilder<BE> withSimple(BlockCapability<CAP, CONTEXT> capability) {
            return withSimple(capability, ConstantPredicates.ALWAYS_TRUE);
        }

        @SuppressWarnings("unchecked")
        public <CAP, CONTEXT> TileEntityTypeDeferredRegister.BlockEntityTypeBuilder<BE> withSimple(BlockCapability<CAP, CONTEXT> capability, BooleanSupplier shouldApply) {
            return with(capability, (ICapabilityProvider<? super BE, CONTEXT, CAP>) Capabilities.SIMPLE_PROVIDER, shouldApply);
        }

        public <CAP, CONTEXT> TileEntityTypeDeferredRegister.BlockEntityTypeBuilder<BE> with(BlockCapability<CAP, CONTEXT> capability,
                                                                                                                               Function<BlockCapability<CAP, CONTEXT>, ICapabilityProvider<? super BE, CONTEXT, CAP>> provider) {
            return with(capability, provider.apply(capability));
        }

        public <CAP, CONTEXT> TileEntityTypeDeferredRegister.BlockEntityTypeBuilder<BE> with(BlockCapability<CAP, CONTEXT> capability, ICapabilityProvider<? super BE, CONTEXT, CAP> provider) {
            return with(capability, provider, ConstantPredicates.ALWAYS_TRUE);
        }

        public <CAP, CONTEXT> TileEntityTypeDeferredRegister.BlockEntityTypeBuilder<BE> with(BlockCapability<CAP, CONTEXT> capability, ICapabilityProvider<? super BE, CONTEXT, CAP> provider,
                                                                                                                               BooleanSupplier shouldApply) {
            capabilityProviders.add(new TileEntityTypeRegistryObject.CapabilityData<>(capability, provider, shouldApply));
            return this;
        }

        public TileEntityTypeDeferredRegister.BlockEntityTypeBuilder<BE> without(BlockCapability<?, ?>... capabilities) {
            for (BlockCapability<?, ?> capability : capabilities) {
                for (Iterator<TileEntityTypeRegistryObject.CapabilityData<BE, ?, ?>> iterator = capabilityProviders.iterator(); iterator.hasNext(); ) {
                    if (iterator.next().capability() == capability) {
                        iterator.remove();
                    }
                }
            }
            return this;
        }

        public TileEntityTypeDeferredRegister.BlockEntityTypeBuilder<BE> without(Collection<? extends BlockCapability<?, ?>> capabilities) {
            for (Iterator<TileEntityTypeRegistryObject.CapabilityData<BE, ?, ?>> iterator = capabilityProviders.iterator(); iterator.hasNext(); ) {
                if (capabilities.contains(iterator.next().capability())) {
                    iterator.remove();
                }
            }
            return this;
        }

        public TileEntityTypeDeferredRegister.BlockEntityTypeBuilder<BE> clientTicker(BlockEntityTicker<BE> ticker) {
            Preconditions.checkState(clientTicker == null, "Client ticker may only be set once.");
            clientTicker = ticker;
            return this;
        }

        public TileEntityTypeDeferredRegister.BlockEntityTypeBuilder<BE> serverTicker(BlockEntityTicker<BE> ticker) {
            Preconditions.checkState(serverTicker == null, "Server ticker may only be set once.");
            serverTicker = ticker;
            return this;
        }

        public TileEntityTypeDeferredRegister.BlockEntityTypeBuilder<BE> commonTicker(BlockEntityTicker<BE> ticker) {
            return clientTicker(ticker)
                    .serverTicker(ticker);
        }

        @SuppressWarnings("ConstantConditions")
        public TileEntityTypeRegistryObject<BE> build() {
            TileEntityTypeRegistryObject<BE> holder = registerMek(block.getName(), () -> BlockEntityType.Builder.<BE>of(factory, block.getBlock()).build(null));
            holder.tickers(clientTicker, serverTicker);
            holder.capabilities(capabilityProviders.isEmpty() ? null : capabilityProviders);
            return holder;
        }
    }
}