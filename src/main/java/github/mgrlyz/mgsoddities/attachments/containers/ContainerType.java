package github.mgrlyz.mgsoddities.attachments.containers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import github.mgrlyz.mgsoddities.config.IMGsOdditiesConfig;
import github.mgrlyz.mgsoddities.registries.MGsOdditiesDataComponents;
import github.mgrlyz.mgsoddities.tile.base.TileEntityMGsOddities;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import mekanism.api.DataHandlerUtils;
import mekanism.api.SerializationConstants;
import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.chemical.IChemicalTank;
import mekanism.api.energy.IEnergyContainer;
import mekanism.api.energy.IStrictEnergyHandler;
import mekanism.api.fluid.IExtendedFluidTank;
import mekanism.api.heat.IHeatCapacitor;
import mekanism.api.inventory.IInventorySlot;
import mekanism.common.Mekanism;
import mekanism.common.attachments.containers.ComponentBackedHandler;
import mekanism.common.attachments.containers.IAttachedContainers;
import mekanism.common.attachments.containers.chemical.AttachedChemicals;
import mekanism.common.attachments.containers.chemical.ComponentBackedChemicalHandler;
import mekanism.common.attachments.containers.creator.IContainerCreator;
import mekanism.common.attachments.containers.energy.AttachedEnergy;
import mekanism.common.attachments.containers.energy.ComponentBackedEnergyHandler;
import mekanism.common.attachments.containers.fluid.AttachedFluids;
import mekanism.common.attachments.containers.fluid.ComponentBackedFluidHandler;
import mekanism.common.attachments.containers.heat.AttachedHeat;
import mekanism.common.attachments.containers.heat.ComponentBackedHeatHandler;
import mekanism.common.attachments.containers.item.AttachedItems;
import mekanism.common.attachments.containers.item.ComponentBackedItemHandler;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.capabilities.IMultiTypeCapability;
import mekanism.common.config.IMekanismConfig;
import mekanism.common.integration.energy.EnergyCompatUtils;
import mekanism.common.registries.MekanismDataComponents;
import mekanism.common.tile.base.TileEntityMekanism;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.capabilities.ItemCapability;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;

@NothingNullByDefault
public class ContainerType<CONTAINER extends INBTSerializable<CompoundTag>, ATTACHED extends IAttachedContainers<?, ATTACHED>,
        HANDLER extends ComponentBackedHandler<?, CONTAINER, ATTACHED>> {

    private static final List<ContainerType<?, ?, ?>> TYPES_INTERNAL = new ArrayList<>();
    public static final List<ContainerType<?, ?, ?>> TYPES = Collections.unmodifiableList(TYPES_INTERNAL);

    public static final ContainerType<IEnergyContainer, AttachedEnergy, ComponentBackedEnergyHandler> ENERGY = new ContainerType<>(MGsOdditiesDataComponents.ATTACHED_ENERGY,
            SerializationConstants.ENERGY_CONTAINERS, SerializationConstants.CONTAINER, ComponentBackedEnergyHandler::new, Capabilities.STRICT_ENERGY, AttachedEnergy.EMPTY,
            TileEntityMGsOddities::getEnergyContainers, TileEntityMGsOddities::collectEnergyContainers, TileEntityMGsOddities::applyEnergyContainers, TileEntityMekanism::canHandleEnergy) {
        @Override
        @SuppressWarnings("unchecked")
        public void registerItemCapabilities(RegisterCapabilitiesEvent event, Item item, boolean exposeWhenStacked, IMekanismConfig... requiredConfigs) {
            EnergyCompatUtils.registerItemCapabilities(event, item, (ICapabilityProvider<ItemStack, Void, IStrictEnergyHandler>) getCapabilityProvider(exposeWhenStacked, requiredConfigs));
        }
    };
            SerializationConstants.ITEMS, SerializationConstants.SLOT, ComponentBackedItemHandler::new, Capabilities.ITEM, AttachedItems.EMPTY,
            TileEntityMGsOddities::getInventorySlots, TileEntityMGsOddities::collectInventorySlots, TileEntityMGsOddities::applyInventorySlots, TileEntityMGsOddities::hasInventory);
    public static final mekanism.common.attachments.containers.ContainerType<IExtendedFluidTank, AttachedFluids, ComponentBackedFluidHandler> FLUID = new ContainerType<>(MGsOdditiesDataComponents.ATTACHED_FLUIDS,
            SerializationConstants.FLUID_TANKS, SerializationConstants.TANK, ComponentBackedFluidHandler::new, Capabilities.FLUID, AttachedFluids.EMPTY,
            TileEntityMGsOddities::getFluidTanks, TileEntityMGsOddities::collectFluidTanks, TileEntityMGsOddities::applyFluidTanks, TileEntityMGsOddities::canHandleFluid);

    public static final ContainerType<IChemicalTank, AttachedChemicals, ComponentBackedChemicalHandler> CHEMICAL = new ContainerType<>(MGsOdditiesDataComponents.ATTACHED_CHEMICALS,
            SerializationConstants.CHEMICAL_TANKS, SerializationConstants.TANK, ComponentBackedChemicalHandler::new, Capabilities.CHEMICAL, AttachedChemicals.EMPTY,
            TileEntityMGsOddities::getChemicalTanks, TileEntityMGsOddities::collectChemicalTanks, TileEntityMGsOddities::applyChemicalTanks, TileEntityMGsOddities::canHandleChemicals) {
        @Override
        public void readFrom(HolderLookup.Provider provider, CompoundTag tag, TileEntityMGsOddities tile) {
            if (tag.contains(getTag(), Tag.TAG_LIST)) {
                super.readFrom(provider, tag, getContainers(tile));
            } else {
                if (tag.contains(SerializationConstants.GAS_TANKS)) {
                    read(provider, tile.getLegacyGasTanks(), tag.getList(SerializationConstants.GAS_TANKS, Tag.TAG_COMPOUND));
                }
                if (tag.contains(SerializationConstants.INFUSION_TANKS)) {
                    read(provider, tile.getLegacyInfuseTanks(), tag.getList(SerializationConstants.INFUSION_TANKS, Tag.TAG_COMPOUND));
                }
                if (tag.contains(SerializationConstants.PIGMENT_TANKS)) {
                    read(provider, tile.getLegacyPigmentTanks(), tag.getList(SerializationConstants.PIGMENT_TANKS, Tag.TAG_COMPOUND));
                }
                if (tag.contains(SerializationConstants.SLURRY_TANKS)) {
                    read(provider, tile.getLegacySlurryTanks(), tag.getList(SerializationConstants.SLURRY_TANKS, Tag.TAG_COMPOUND));
                }
            }
        }

        @Override
        public void readFrom(HolderLookup.Provider provider, CompoundTag tag, List<IChemicalTank> containers) {
            if (tag.contains(getTag(), Tag.TAG_LIST)) {
                super.readFrom(provider, tag, containers);
            } else {
                read(provider, containers, tag.getList(SerializationConstants.GAS_TANKS, Tag.TAG_COMPOUND));
                read(provider, containers, tag.getList(SerializationConstants.INFUSION_TANKS, Tag.TAG_COMPOUND));
                read(provider, containers, tag.getList(SerializationConstants.PIGMENT_TANKS, Tag.TAG_COMPOUND));
                read(provider, containers, tag.getList(SerializationConstants.SLURRY_TANKS, Tag.TAG_COMPOUND));
            }
        }
    };

    public static final ContainerType<IHeatCapacitor, AttachedHeat, ComponentBackedHeatHandler> HEAT = new ContainerType<>(MGsOdditiesDataComponents.ATTACHED_HEAT,
            SerializationConstants.HEAT_CAPACITORS, SerializationConstants.CONTAINER, ComponentBackedHeatHandler::new, null, AttachedHeat.EMPTY,
            TileEntityMGsOddities::getHeatCapacitors, TileEntityMGsOddities::collectHeatCapacitors, TileEntityMGsOddities::applyHeatCapacitors, TileEntityMGsOddities::canHandleHeat);

    public static final Codec<ContainerType<?, ?, ?>> CODEC = BuiltInRegistries.DATA_COMPONENT_TYPE.byNameCodec().comapFlatMap(componentType -> {
        for (ContainerType<?, ?, ?> type : TYPES) {
            if (type.component.value() == componentType) {
                return DataResult.success(type);
            }
        }
        return DataResult.error(() -> "Data Component type " + BuiltInRegistries.DATA_COMPONENT_TYPE.getKey(componentType) + " does not have a corresponding container type");
    }, containerType -> containerType.component.get());

    private final Map<Item, Lazy<? extends IContainerCreator<? extends CONTAINER, ATTACHED>>> knownDefaultCreators = new Reference2ObjectOpenHashMap<>();
    private final ContainerType.HandlerConstructor<HANDLER> handlerConstructor;
    private final BiFunction<TileEntityMekanism, @Nullable Direction, List<CONTAINER>> containersFromTile;
    private final ContainerType.CopyFromTile<CONTAINER, ATTACHED> copyFromTile;
    private final ContainerType.CopyToTile<CONTAINER, ATTACHED> copyToTile;
    private final DeferredHolder<DataComponentType<?>, DataComponentType<ATTACHED>> component;
    @Nullable
    private final IMultiTypeCapability<? super HANDLER, ?> capability;
    private final Predicate<TileEntityMekanism> canHandle;
    private final ATTACHED emptyAttachment;
    private final String containerTag;
    protected final String containerKey;

    private ContainerType(DeferredHolder<DataComponentType<?>, DataComponentType<ATTACHED>> component, String containerTag, String containerKey,
                          ContainerType.HandlerConstructor<HANDLER> handlerConstructor, @Nullable IMultiTypeCapability<? super HANDLER, ?> capability, ATTACHED emptyAttachment,
                          BiFunction<TileEntityMGsOddities, @Nullable Direction, List<CONTAINER>> containersFromTile, ContainerType.CopyFromTile<CONTAINER, ATTACHED> copyFromTile,
                          ContainerType.CopyToTile<CONTAINER, ATTACHED> copyToTile, Predicate<TileEntityMGsOddities> canHandle) {
        TYPES_INTERNAL.add(this);
        this.component = component;
        this.containerTag = containerTag;
        this.containerKey = containerKey;
        this.emptyAttachment = emptyAttachment;
        this.handlerConstructor = handlerConstructor;
        this.containersFromTile = containersFromTile;
        this.copyFromTile = copyFromTile;
        this.copyToTile = copyToTile;
        this.capability = capability;
        this.canHandle = canHandle;
    }

    public DeferredHolder<DataComponentType<?>, DataComponentType<ATTACHED>> getComponentType() {
        return component;
    }

    @Nullable
    public ResourceLocation getComponentName() {
        return BuiltInRegistries.DATA_COMPONENT_TYPE.getKey(component.get());
    }

    public String getTag() {
        return containerTag;
    }

    public void addDefaultCreators(@Nullable IEventBus eventBus, Item item, Supplier<? extends IContainerCreator<? extends CONTAINER, ATTACHED>> defaultCreator,
                                   IMekanismConfig... requiredConfigs) {
        knownDefaultCreators.put(item, Lazy.of(defaultCreator));
        if (eventBus != null && capability != null) {
            eventBus.addListener(RegisterCapabilitiesEvent.class, event -> registerItemCapabilities(event, item, false, requiredConfigs));
        }
    }

    public void registerItemCapabilities(RegisterCapabilitiesEvent event, Item item, boolean exposeWhenStacked, IMekanismConfig... requiredConfigs) {
        if (capability != null) {
            event.registerItem((ItemCapability) capability.item(), getCapabilityProvider(exposeWhenStacked, requiredConfigs), item);
        }
    }

    public List<CONTAINER> getAttachmentContainersIfPresent(ItemStack stack) {
        HANDLER handler = createHandlerIfData(stack);
        return handler == null ? Collections.emptyList() : handler.getContainers();
    }

    public int getContainerCount(ItemStack stack) {
        ATTACHED attached = getOrEmpty(stack);
        if (attached.isEmpty()) {
            Lazy<? extends IContainerCreator<? extends CONTAINER, ATTACHED>> containerCreator = knownDefaultCreators.get(stack.getItem());
            return containerCreator == null ? 0 : containerCreator.get().totalContainers();
        }
        return attached.size();
    }

    @Nullable
    public HANDLER createHandlerIfData(ItemStack stack) {
        ATTACHED attached = getOrEmpty(stack);
        return attached.isEmpty() ? null : handlerConstructor.create(stack, attached.size());
    }

    @Nullable
    public HANDLER createHandler(ItemStack stack) {
        int count = getContainerCount(stack);
        if (count == 0) {
            return null;
        }
        return handlerConstructor.create(stack, count);
    }

    public ATTACHED createNewAttachment(ItemStack stack) {
        Lazy<? extends IContainerCreator<? extends CONTAINER, ATTACHED>> lazy = knownDefaultCreators.get(stack.getItem());
        if (lazy == null) {
            return emptyAttachment;
        }
        IContainerCreator<? extends CONTAINER, ATTACHED> containerCreator = lazy.get();
        int count = containerCreator.totalContainers();
        if (count == 0) {
            return emptyAttachment;
        }
        return containerCreator.initStorage(count);
    }

    public ATTACHED getOrEmpty(ItemStack stack) {
        return stack.getOrDefault(component, emptyAttachment);
    }

    public CONTAINER createContainer(ItemStack attachedTo, int containerIndex) {
        Lazy<? extends IContainerCreator<? extends CONTAINER, ATTACHED>> creator = knownDefaultCreators.get(attachedTo.getItem());
        if (creator != null) {
            return creator.get().create(this, attachedTo, containerIndex);
        }
        throw new IllegalArgumentException("No known containers for item " + attachedTo.getItem());
    }

    protected ICapabilityProvider<ItemStack, Void, ? super HANDLER> getCapabilityProvider(boolean exposeWhenStacked, IMekanismConfig... requiredConfigs) {
        if (exposeWhenStacked) {
            return getCapabilityProvider(requiredConfigs);
        } else if (requiredConfigs.length == 0) {
            return (stack, context) -> stack.getCount() == 1 ? createHandler(stack) : null;
        }
        return (stack, context) -> stack.getCount() == 1 && hasRequiredConfigs(requiredConfigs) ? createHandler(stack) : null;
    }

    protected ICapabilityProvider<ItemStack, Void, ? super HANDLER> getCapabilityProvider(IMekanismConfig... requiredConfigs) {
        if (requiredConfigs.length == 0) {
            return (stack, context) -> createHandler(stack);
        }
        return (stack, context) -> hasRequiredConfigs(requiredConfigs) ? createHandler(stack) : null;
    }

    private static boolean hasRequiredConfigs(IMekanismConfig... requiredConfigs) {
        for (IMGsOdditiesConfig requiredConfig : requiredConfigs) {
            if (!requiredConfig.isLoaded()) {
                return false;
            }
        }
        return true;
    }

    public boolean supports(ItemStack stack) {
        return stack.has(component) || knownDefaultCreators.containsKey(stack.getItem());
    }

    public void addDefault(ItemLike item, DataComponentPatch.Builder builder) {
        Lazy<? extends IContainerCreator<? extends CONTAINER, ATTACHED>> lazy = knownDefaultCreators.get(item);
        if (lazy != null) {
            IContainerCreator<? extends CONTAINER, ATTACHED> containerCreator = lazy.get();
            int count = containerCreator.totalContainers();
            if (count > 0) {
                builder.set(component.get(), containerCreator.initStorage(count));
            }
        }
    }

    public static boolean anySupports(ItemLike itemLike) {
        for (ContainerType<?, ?, ?> type : TYPES) {
            if (type.knownDefaultCreators.containsKey(itemLike.asItem())) {
                return true;
            }
        }
        return false;
    }

    private ListTag save(HolderLookup.Provider provider, List<CONTAINER> containers) {
        return DataHandlerUtils.writeContents(provider, containers, containerKey);
    }

    protected void read(HolderLookup.Provider provider, List<CONTAINER> containers, @Nullable ListTag storedContainers) {
        if (storedContainers != null) {
            DataHandlerUtils.readContents(provider, containers, storedContainers, containerKey);
        }
    }

    public void saveTo(HolderLookup.Provider provider, CompoundTag tag, TileEntityMekanism tile) {
        saveTo(provider, tag, getContainers(tile));
    }

    public void saveTo(HolderLookup.Provider provider, CompoundTag tag, List<CONTAINER> containers) {
        ListTag serialized = save(provider, containers);
        if (!serialized.isEmpty()) {
            tag.put(containerTag, serialized);
        }
    }

    public void readFrom(HolderLookup.Provider provider, CompoundTag tag, TileEntityMekanism tile) {
        readFrom(provider, tag, getContainers(tile));
    }

    public void readFrom(HolderLookup.Provider provider, CompoundTag tag, List<CONTAINER> containers) {
        read(provider, containers, tag.getList(containerTag, Tag.TAG_COMPOUND));
    }

    public void copyToStack(HolderLookup.Provider provider, List<CONTAINER> containers, ItemStack stack) {
        HANDLER handler = createHandler(stack);
        if (handler != null) {
            read(provider, handler.getContainers(), save(provider, containers));
            stack.set(component, handler.getAttached());
            if (stack.getCount() > 1) {
                Mekanism.logger.error("Copied {} to a stack ({}). This might lead to duplication of data.", getComponentName(), stack);
            }
        }
    }

    public void copyToTile(TileEntityMekanism tile, BlockEntity.DataComponentInput input) {
        ATTACHED attachedData = input.get(component);
        if (attachedData != null) {
            copyToTile.copy(tile, input, getContainers(tile), attachedData);
        }
    }

    public void copyFromStack(HolderLookup.Provider provider, ItemStack stack, List<CONTAINER> containers) {
        HANDLER handler = createHandler(stack);
        if (handler != null) {
            read(provider, containers, save(provider, handler.getContainers()));
        }
    }

    public void copyFromTile(TileEntityMekanism tile, DataComponentMap.Builder builder) {
        List<CONTAINER> containers = getContainers(tile);
        if (!containers.isEmpty()) {
            ATTACHED attachedData = copyFromTile.copy(tile, builder, containers);
            if (attachedData != null) {
                builder.set(component, attachedData);
            }
        }
    }

    public boolean canHandle(TileEntityMekanism tile) {
        return canHandle.test(tile);
    }

    public List<CONTAINER> getContainers(TileEntityMekanism tile) {
        return containersFromTile.apply(tile, null);
    }

    @FunctionalInterface
    private interface HandlerConstructor<HANDLER extends ComponentBackedHandler<?, ?, ?>> {

        HANDLER create(ItemStack attachedTo, int totalContainers);
    }

    @FunctionalInterface
    public interface CopyToTile<CONTAINER extends INBTSerializable<CompoundTag>, ATTACHED extends IAttachedContainers<?, ATTACHED>> {

        void copy(TileEntityMGsOddities tile, BlockEntity.DataComponentInput input, List<CONTAINER> containers, ATTACHED attachedData);
    }

    @FunctionalInterface
    public interface CopyFromTile<CONTAINER extends INBTSerializable<CompoundTag>, ATTACHED extends IAttachedContainers<?, ATTACHED>> {

        @Nullable
        ATTACHED copy(TileEntityMGsOddities tile, DataComponentMap.Builder builder, List<CONTAINER> containers);
    }
}