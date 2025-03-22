package github.mgrlyz.mgsoddities.registration.impl;

import github.mgrlyz.mgsoddities.api.providers.IItemProvider;
import github.mgrlyz.mgsoddities.capabilities.ICapabilityAware;
import github.mgrlyz.mgsoddities.config.IMGsOdditiesConfig;
import github.mgrlyz.mgsoddities.registration.MGsOdditiesDeferredHolder;
import mekanism.common.attachments.containers.ContainerType;
import mekanism.common.attachments.containers.creator.IContainerCreator;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ItemRegistryObject<ITEM extends Item> extends MGsOdditiesDeferredHolder<Item, ITEM> implements IItemProvider {

    @Nullable
    private Map<ContainerType<?, ?, ?>, Supplier<? extends IContainerCreator<?, ?>>> defaultCreators;
    @Nullable
    private List<Consumer<RegisterCapabilitiesEvent>> containerCapabilities;

    public ItemRegistryObject(ResourceKey<Item> key) {
        super(key);
    }

    @NotNull
    @Override
    public ITEM asItem() {
        return value();
    }

    @ApiStatus.Internal
    public <CONTAINER extends INBTSerializable<CompoundTag>> ItemRegistryObject<ITEM> addAttachmentOnlyContainers(ContainerType<CONTAINER, ?, ?> containerType,
                                                                                                                                                    Supplier<IContainerCreator<? extends CONTAINER, ?>> defaultCreator) {
        if (defaultCreators == null) {
            defaultCreators = new LinkedHashMap<>();
        }
        if (defaultCreators.put(containerType, defaultCreator) != null) {
            throw new IllegalStateException("Duplicate attachments added for container type: " + containerType.getComponentName());
        }
        return this;
    }

    @ApiStatus.Internal
    public <CONTAINER extends INBTSerializable<CompoundTag>> ItemRegistryObject<ITEM> addAttachedContainerCapabilities(ContainerType<CONTAINER, ?, ?> containerType,
                                                                                                                                                         Supplier<IContainerCreator<? extends CONTAINER, ?>> defaultCreator, IMekanismConfig... requiredConfigs) {
        addAttachmentOnlyContainers(containerType, defaultCreator);
        return addContainerCapability(containerType, requiredConfigs);
    }

    @ApiStatus.Internal
    private ItemRegistryObject<ITEM> addContainerCapability(ContainerType<?, ?, ?> containerType, IMGsOdditiesConfig... requiredConfigs) {
        if (containerCapabilities == null) {
            containerCapabilities = new ArrayList<>();
        }
        containerCapabilities.add(event -> containerType.registerItemCapabilities(event, asItem(), false, requiredConfigs));
        return this;
    }

    @ApiStatus.Internal
    void registerCapabilities(RegisterCapabilitiesEvent event) {
        if (asItem() instanceof ICapabilityAware capabilityAware) {
            capabilityAware.attachCapabilities(event);
        }
        if (containerCapabilities != null) {
            for (Consumer<RegisterCapabilitiesEvent> consumer : containerCapabilities) {
                consumer.accept(event);
            }
            containerCapabilities = null;
        }
    }

    @ApiStatus.Internal
    @SuppressWarnings({"unchecked", "rawtypes"})
    void attachDefaultContainers(IEventBus eventBus) {
        ITEM item = asItem();
        if (item instanceof IAttachmentAware attachmentAware) {
            attachmentAware.attachAttachments(eventBus);
        }
        if (defaultCreators != null) {
            for (Map.Entry<ContainerType<?, ?, ?>, Supplier<? extends IContainerCreator<?, ?>>> entry : defaultCreators.entrySet()) {
                entry.getKey().addDefaultCreators(null, item, (Supplier) entry.getValue());
            }
            defaultCreators = null;
        }
    }
}