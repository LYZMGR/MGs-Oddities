package github.mgrlyz.mgsoddities.block.attribute.base;

import github.mgrlyz.mgsoddities.api.providers.IBlockProvider;
import github.mgrlyz.mgsoddities.block.attribute.*;
import mekanism.api.Action;
import mekanism.api.IConfigCardAccess;
import mekanism.api.IContentsListener;
import mekanism.api.SerializationConstants;
import mekanism.api.chemical.IChemicalTank;
import mekanism.api.chemical.IMekanismChemicalHandler;
import mekanism.api.energy.IEnergyContainer;
import mekanism.api.energy.IMekanismStrictEnergyHandler;
import mekanism.api.fluid.IExtendedFluidTank;
import mekanism.api.fluid.IMekanismFluidHandler;
import mekanism.api.heat.IHeatCapacitor;
import mekanism.api.heat.IHeatHandler;
import mekanism.api.inventory.IInventorySlot;
import mekanism.api.inventory.IMekanismInventory;
import mekanism.api.math.MathUtils;
import mekanism.api.radiation.IRadiationManager;
import mekanism.api.security.IBlockSecurityUtils;
import mekanism.api.security.SecurityMode;
import mekanism.api.text.TextComponentUtil;
import mekanism.client.sound.SoundHandler;
import mekanism.common.Mekanism;
import mekanism.common.attachments.FilterAware;
import mekanism.common.attachments.containers.ContainerType;
import mekanism.common.block.interfaces.IHasTileEntity;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.capabilities.energy.MachineEnergyContainer;
import mekanism.common.capabilities.heat.BasicHeatCapacitor;
import mekanism.common.capabilities.heat.CachedAmbientTemperature;
import mekanism.common.capabilities.heat.ITileHeatHandler;
import mekanism.common.capabilities.holder.chemical.IChemicalTankHolder;
import mekanism.common.capabilities.holder.energy.IEnergyContainerHolder;
import mekanism.common.capabilities.holder.fluid.IFluidTankHolder;
import mekanism.common.capabilities.holder.heat.IHeatCapacitorHolder;
import mekanism.common.capabilities.holder.slot.IInventorySlotHolder;
import mekanism.common.capabilities.resolver.manager.*;
import mekanism.common.config.MekanismConfig;
import mekanism.common.content.filter.FilterManager;
import mekanism.common.integration.computer.*;
import mekanism.common.integration.computer.annotation.ComputerMethod;
import mekanism.common.inventory.container.ITrackableContainer;
import mekanism.common.inventory.container.MekanismContainer;
import mekanism.common.inventory.container.sync.*;
import mekanism.common.inventory.container.sync.chemical.SyncableChemicalStack;
import mekanism.common.inventory.container.sync.dynamic.SyncMapper;
import mekanism.common.item.ItemConfigurationCard;
import mekanism.common.item.ItemConfigurator;
import mekanism.common.lib.LastEnergyTracker;
import mekanism.common.lib.chunkloading.IChunkLoader;
import mekanism.common.lib.frequency.IFrequencyHandler;
import mekanism.common.lib.frequency.TileComponentFrequency;
import mekanism.common.lib.security.BlockSecurityUtils;
import mekanism.common.lib.security.ISecurityTile;
import mekanism.common.registries.MekanismDataComponents;
import mekanism.common.tile.base.CapabilityTileEntity;
import mekanism.common.tile.base.WrenchResult;
import mekanism.common.tile.component.ITileComponent;
import mekanism.common.tile.component.TileComponentSecurity;
import mekanism.common.tile.component.TileComponentUpgrade;
import mekanism.common.tile.interfaces.*;
import mekanism.common.util.NBTUtils;
import mekanism.common.util.WorldUtils;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import java.util.function.ToLongFunction;

public abstract class TileEntityMGsOddities extends CapabilityTileEntity implements IFrequencyHandler, ITileDirectional, IConfigCardAccess, ITileActive, ITileSound,
        ITileRedstone, ISecurityTile, IMekanismInventory, ITileUpgradable, ITierUpgradable, IComparatorSupport, ITrackableContainer, IMekanismFluidHandler,
        IMekanismStrictEnergyHandler, ITileHeatHandler, IMekanismChemicalHandler, IComputerTile, ITileRadioactive, Nameable {

    public final Set<Player> playersUsing = new HashSet<>();

    private final List<ITileComponent> components = new ArrayList<>();

    protected final IBlockProvider blockProvider;

    private boolean supportsComparator;
    private boolean supportsComputers;
    private boolean supportsUpgrades;
    private boolean supportsRedstone;
    private boolean canBeUpgraded;
    private boolean isDirectional;
    private boolean isActivatable;
    private AttributeStateActive activeAttribute;
    private boolean hasSecurity;
    private boolean hasSound;
    private boolean hasGui;
    private boolean nameable;

    @Nullable
    private Component customName;
    @Nullable
    private String containerDescription;

    @Nullable
    private Direction cachedDirection;


    protected boolean redstone = false;
    private boolean redstoneLastTick = false;
    private RedstoneControl controlType = RedstoneControl.DISABLED;

    private int currentRedstoneLevel;

    protected TileComponentUpgrade upgradeComponent;

    protected final TileComponentFrequency frequencyComponent;

    @Nullable
    protected final ItemHandlerManager itemHandlerManager;

    @Nullable
    private final ChemicalHandlerManager chemicalHandlerManager;
    private float radiationScale;

    @Nullable
    private final FluidHandlerManager fluidHandlerManager;

    @Nullable
    private final EnergyHandlerManager energyHandlerManager;
    private final LastEnergyTracker lastEnergyTracker = new LastEnergyTracker();

    protected final Map<Direction, BlockCapabilityCache<IHeatHandler, @Nullable Direction>> adjacentHeatCaps;
    protected final CachedAmbientTemperature ambientTemperature;
    @Nullable
    protected final HeatHandlerManager heatHandlerManager;

    private TileComponentSecurity securityComponent;

    private boolean currentActive;
    private int updateDelay;
    protected IntSupplier delaySupplier = MekanismConfig.general.blockDeactivationDelay;

    @Nullable
    protected final Supplier<SoundEvent> soundEvent;
    @Nullable
    protected SoundEvent lastSoundEvent;

    private SoundInstance activeSound;
    private int playSoundCooldown = 0;

    public TileEntityMekanism(IBlockProvider blockProvider, BlockPos pos, BlockState state) {
        super(((IHasTileEntity<? extends BlockEntity>) blockProvider.getBlock()).getTileType(), pos, state);
        this.blockProvider = blockProvider;
        Block block = this.blockProvider.getBlock();
        setSupportedTypes(block);
        presetVariables();
        IContentsListener saveOnlyListener = this::markForSave;

        List<ICapabilityHandlerManager<?>> capabilityHandlerManagers = new ArrayList<>();

        IChemicalTankHolder initialChemicalTanks = getInitialChemicalTanks(getListener(ContainerType.CHEMICAL, saveOnlyListener));
        if (initialChemicalTanks != null) {
            capabilityHandlerManagers.add(chemicalHandlerManager = new ChemicalHandlerManager(initialChemicalTanks, this));
        } else {
            chemicalHandlerManager = null;
        }

        IFluidTankHolder initialFluidTanks = getInitialFluidTanks(getListener(ContainerType.FLUID, saveOnlyListener));
        if (initialFluidTanks != null) {
            capabilityHandlerManagers.add(fluidHandlerManager = new FluidHandlerManager(initialFluidTanks, this));
        } else {
            fluidHandlerManager = null;
        }

        IEnergyContainerHolder initialEnergyContainers = getInitialEnergyContainers(getListener(ContainerType.ENERGY, saveOnlyListener));
        if (initialEnergyContainers != null) {
            capabilityHandlerManagers.add(energyHandlerManager = new EnergyHandlerManager(initialEnergyContainers, this));
        } else {
            energyHandlerManager = null;
        }

        IInventorySlotHolder initialInventory = getInitialInventory(getListener(ContainerType.ITEM, saveOnlyListener));
        if (initialInventory != null) {
            capabilityHandlerManagers.add(itemHandlerManager = new ItemHandlerManager(initialInventory, this));
        } else {
            itemHandlerManager = null;
        }

        CachedAmbientTemperature ambientTemperature = new CachedAmbientTemperature(this::getLevel, this::getBlockPos);
        IHeatCapacitorHolder initialHeatCapacitors = getInitialHeatCapacitors(getListener(ContainerType.HEAT, saveOnlyListener), ambientTemperature);
        if (initialHeatCapacitors != null) {
            capabilityHandlerManagers.add(heatHandlerManager = new HeatHandlerManager(initialHeatCapacitors, this));
        } else {
            heatHandlerManager = null;
        }
        if (canHandleHeat()) {
            adjacentHeatCaps = new EnumMap<>(Direction.class);
            this.ambientTemperature = ambientTemperature;
        } else {
            adjacentHeatCaps = Collections.emptyMap();
            this.ambientTemperature = null;
        }

        addCapabilityResolvers(capabilityHandlerManagers);
        frequencyComponent = new TileComponentFrequency(this);
        if (supportsUpgrades()) {
            upgradeComponent = new TileComponentUpgrade(this);
        }
        if (hasSecurity()) {
            securityComponent = new TileComponentSecurity(this);
        }
        soundEvent = hasSound() ? Attribute.getOrThrow(block, AttributeSound.class).getSound() : null;
    }

    private void setSupportedTypes(Block block) {
        supportsUpgrades = Attribute.has(block, AttributeUpgradeSupport.class);
        canBeUpgraded = Attribute.has(block, AttributeUpgradeable.class);
        isDirectional = Attribute.has(block, AttributeStateFacing.class);
        supportsRedstone = Attribute.has(block, Attributes.AttributeRedstone.class);
        hasSound = Attribute.has(block, AttributeSound.class);
        hasGui = Attribute.has(block, AttributeGui.class);
        hasSecurity = Attribute.has(block, Attributes.AttributeSecurity.class);
        activeAttribute = Attribute.get(block, AttributeStateActive.class);
        isActivatable = hasSound || activeAttribute != null;
        supportsComparator = Attribute.has(block, Attributes.AttributeComparator.class);
        supportsComputers = Mekanism.hooks.computerCompatEnabled() && Attribute.has(block, Attributes.AttributeComputerIntegration.class);
        nameable = hasGui() && !Attribute.getOrThrow(getBlockType(), AttributeGui.class).hasCustomName();
    }

    protected void presetVariables() {
    }

    public Block getBlockType() {
        return blockProvider.getBlock();
    }

    public ResourceLocation getBlockTypeRegistryName() {
        return blockProvider.getRegistryName();
    }

    public boolean persists(ContainerType<?, ?, ?> type) {
        return type.canHandle(this);
    }

    public boolean persistsToItem(ContainerType<?, ?, ?> type) {
        return persists(type);
    }

    public boolean syncs(ContainerType<?, ?, ?> type) {
        return persists(type);
    }

    @Override
    public final boolean supportsUpgrades() {
        return supportsUpgrades;
    }

    @Override
    public final boolean supportsComparator() {
        return supportsComparator;
    }

    @Override
    public final boolean canBeUpgraded() {
        return canBeUpgraded;
    }

    @Override
    public final boolean isDirectional() {
        return isDirectional;
    }

    @Override
    public final boolean supportsRedstone() {
        return supportsRedstone;
    }

    @Override
    public final boolean hasSound() {
        return hasSound;
    }

    public final boolean hasGui() {
        return hasGui;
    }

    @Override
    public final boolean hasSecurity() {
        return hasSecurity;
    }

    @Override
    public final boolean isActivatable() {
        return isActivatable;
    }

    @Override
    public final boolean hasComputerSupport() {
        return supportsComputers;
    }

    @Override
    public final boolean hasInventory() {
        return itemHandlerManager != null && itemHandlerManager.canHandle();
    }

    @Override
    public boolean canHandleChemicals() {
        return chemicalHandlerManager != null && chemicalHandlerManager.canHandle();
    }

    @Override
    public final boolean canHandleFluid() {
        return fluidHandlerManager != null && fluidHandlerManager.canHandle();
    }

    @Override
    public final boolean canHandleEnergy() {
        return energyHandlerManager != null && energyHandlerManager.canHandle();
    }

    @Override
    public final boolean canHandleHeat() {
        return heatHandlerManager != null && heatHandlerManager.canHandle();
    }


    @NotNull
    @Override
    @SuppressWarnings("ConstantConditions")
    public Component getName() {
        return hasCustomName() ? getCustomName() : TextComponentUtil.build(getBlockType());
    }

    @NotNull
    @Override
    @SuppressWarnings("ConstantConditions")
    public Component getDisplayName() {
        if (isNameable()) {
            return hasCustomName() ? getCustomName() : TextComponentUtil.translate(getContainerDescription());
        }
        return TextComponentUtil.build(getBlockType());
    }

    private String getContainerDescription() {
        if (containerDescription == null) {
            containerDescription = Util.makeDescriptionId("container", getBlockTypeRegistryName());
        }
        return containerDescription;
    }

    @Nullable
    @Override
    public Component getCustomName() {
        return isNameable() ? customName : null;
    }

    public void setCustomName(@Nullable Component name) {
        if (isNameable()) {
            this.customName = name;
        }
    }

    public boolean isNameable() {
        return nameable;
    }

    @Override
    public void markDirtyComparator() {
        if (supportsComparator()) {
            updateComparators = true;
        }
    }

    protected WrenchResult tryWrenchDismantle(BlockState state, Player player, ItemStack stack) {
        if (player.isShiftKeyDown()) {
            if (IRadiationManager.INSTANCE.isRadiationEnabled() && getRadiationScale() > 0) {
                return WrenchResult.RADIOACTIVE;
            }
            WorldUtils.dismantleBlock(state, getLevel(), worldPosition, this, player, stack);
            return WrenchResult.DISMANTLED;
        }
        return WrenchResult.PASS;
    }

    protected WrenchResult tryWrenchRotate(BlockState state, Player player, ItemStack stack) {
        if (isDirectional()) {
            AttributeStateFacing attribute = Attribute.getOrThrow(getBlockType(), AttributeStateFacing.class);
            if (attribute.canRotate()) {
                setFacing(MekanismUtils.rotate(getDirection(), attribute.getFacingProperty() == BlockStateProperties.FACING));
                return WrenchResult.SUCCESS;
            }
        }
        return WrenchResult.PASS;
    }


    public InteractionResult openGui(Player player) {
        if (hasGui() && !isRemote() && !player.isShiftKeyDown()) {
            if (hasSecurity() && !IBlockSecurityUtils.INSTANCE.canAccessOrDisplayError(player, player.level(), worldPosition, this)) {
                return InteractionResult.FAIL;
            }
            ItemStack stack = player.getMainHandItem();
            if (isDirectional() && !stack.isEmpty() && stack.getItem() instanceof ItemConfigurator configurator) {
                if (configurator.getMode(stack) == ItemConfigurator.ConfiguratorMode.ROTATE) {
                    return InteractionResult.PASS;
                }
            }
            if (!stack.isEmpty() && stack.getItem() instanceof ItemConfigurationCard &&
                    WorldUtils.getCapability(level, Capabilities.CONFIG_CARD, worldPosition, null, this, null) != null) {
                return InteractionResult.PASS;
            }

            player.openMenu(Attribute.getOrThrow(getBlockType(), AttributeGui.class).getProvider(this, true), buffer -> {
                buffer.writeBlockPos(worldPosition);
                encodeExtraContainerData(buffer);
            });
            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }

    public void encodeExtraContainerData(RegistryFriendlyByteBuf buffer) {
    }

    public void open(Player player) {
        playersUsing.add(player);
    }

    public void close(Player player) {
        playersUsing.remove(player);
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        for (ITileComponent component : components) {
            component.invalidate();
        }
        if (isRemote() && hasSound()) {
            updateSound();
        }
    }

    @Override
    public void blockRemoved() {
        super.blockRemoved();
        for (ITileComponent component : components) {
            component.removed();
        }
        if (!isRemote() && IRadiationManager.INSTANCE.isRadiationEnabled() && shouldDumpRadiation()) {
            IRadiationManager.INSTANCE.dumpRadiation(getTileGlobalPos(), getChemicalTanks(null), false);
        }
    }

    @Override
    @Deprecated
    public void setBlockState(@NotNull BlockState newState) {
        super.setBlockState(newState);
        if (isDirectional()) {
            Direction newDirection = Attribute.getFacing(newState);
            if (cachedDirection != newDirection) {
                invalidateDirectionCaches(newDirection);
            }
        }
    }

    @Override
    public void loadAdditional(@NotNull CompoundTag nbt, @NotNull HolderLookup.Provider provider) {
        super.loadAdditional(nbt, provider);
        NBTUtils.setBooleanIfPresent(nbt, SerializationConstants.REDSTONE, value -> redstone = value);
        for (ITileComponent component : components) {
            component.read(nbt, provider);
        }
        readSustainedData(provider, nbt);
        for (ContainerType<?, ?, ?> type : ContainerType.TYPES) {
            if (type.canHandle(this) && persists(type)) {
                type.readFrom(provider, nbt, this);
            }
        }
        if (isActivatable()) {
            NBTUtils.setBooleanIfPresent(nbt, SerializationConstants.ACTIVE_STATE, value -> currentActive = value);
            NBTUtils.setIntIfPresent(nbt, SerializationConstants.UPDATE_DELAY, value -> updateDelay = value);
        }
        if (supportsComparator()) {
            NBTUtils.setIntIfPresent(nbt, SerializationConstants.CURRENT_REDSTONE, value -> currentRedstoneLevel = value);
        }
        if (isNameable()) {
            NBTUtils.setStringIfPresent(nbt, SerializationConstants.CUSTOM_NAME, value -> customName = Component.Serializer.fromJson(value, provider));
        }
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag nbtTags, @NotNull HolderLookup.Provider provider) {
        super.saveAdditional(nbtTags, provider);
        nbtTags.putBoolean(SerializationConstants.REDSTONE, redstone);
        for (ITileComponent component : components) {
            component.write(nbtTags, provider);
        }
        writeSustainedData(provider, nbtTags);

        for (ContainerType<?, ?, ?> type : ContainerType.TYPES) {
            if (type.canHandle(this) && persists(type)) {
                type.saveTo(provider, nbtTags, this);
            }
        }

        if (isActivatable()) {
            nbtTags.putBoolean(SerializationConstants.ACTIVE_STATE, currentActive);
            nbtTags.putInt(SerializationConstants.UPDATE_DELAY, updateDelay);
        }
        if (supportsComparator()) {
            nbtTags.putInt(SerializationConstants.CURRENT_REDSTONE, currentRedstoneLevel);
        }

        if (this.customName != null && isNameable()) {
            nbtTags.putString(SerializationConstants.CUSTOM_NAME, Component.Serializer.toJson(this.customName, provider));
        }
    }

    public void writeSustainedData(HolderLookup.Provider provider, CompoundTag data) {
        if (supportsRedstone()) {
            NBTUtils.writeEnum(data, SerializationConstants.CONTROL_TYPE, controlType);
        }
    }

    public void readSustainedData(HolderLookup.Provider provider, CompoundTag data) {
        if (supportsRedstone()) {
            NBTUtils.setEnumIfPresent(data, SerializationConstants.CONTROL_TYPE, RedstoneControl.BY_ID, type -> controlType = supportedOrNextType(type));
        }
    }

    @Override
    protected void applyImplicitComponents(@NotNull BlockEntity.DataComponentInput input) {
        super.applyImplicitComponents(input);
        if (isNameable()) {
            setCustomName(input.get(DataComponents.CUSTOM_NAME));
        }

        for (ITileComponent component : components) {
            component.applyImplicitComponents(input);
        }
        if (supportsUpgrades()) {
            for (Upgrade upgrade : getSupportedUpgrade()) {
                recalculateUpgrades(upgrade);
            }
        }

        for (ContainerType<?, ?, ?> type : ContainerType.TYPES) {
            if (persistsToItem(type)) {
                type.copyToTile(this, input);
            }
        }
        if (this instanceof ITileFilterHolder<?> filterHolder) {
            FilterAware filterAware = input.get(MekanismDataComponents.FILTER_AWARE);
            if (filterAware != null) {
                filterHolder.getFilterManager().trySetFilters(filterAware.filters());
            }
        }
        if (supportsRedstone()) {
            setControlType(input.getOrDefault(MekanismDataComponents.REDSTONE_CONTROL, getControlType()));
        }
    }

    @Override
    public List<DataComponentType<?>> getRemapEntries() {
        List<DataComponentType<?>> remapEntries = super.getRemapEntries();
        for (ITileComponent component : components) {
            component.addRemapEntries(remapEntries);
        }
        for (ContainerType<?, ?, ?> type : ContainerType.TYPES) {
            if (persistsToItem(type) && !remapEntries.contains(type.getComponentType().get())) {
                remapEntries.add(type.getComponentType().get());
            }
        }
        if (this instanceof ITileFilterHolder<?> && !remapEntries.contains(MekanismDataComponents.FILTER_AWARE.get())) {
            remapEntries.add(MekanismDataComponents.FILTER_AWARE.get());
        }
        return remapEntries;
    }

    @Override
    @Deprecated
    public void removeComponentsFromTag(@NotNull CompoundTag tag) {
        super.removeComponentsFromTag(tag);
        for (ITileComponent component : components) {
            tag.remove(component.getComponentKey());
        }
        tag.remove(SerializationConstants.REDSTONE);
        if (supportsComparator()) {
            tag.remove(SerializationConstants.CURRENT_REDSTONE);
        }
        if (isActivatable()) {
            tag.remove(SerializationConstants.ACTIVE_STATE);
            tag.remove(SerializationConstants.UPDATE_DELAY);
        }
        if (supportsRedstone()) {
            tag.remove(SerializationConstants.CONTROL_TYPE);
        }
    }

    @Override
    protected void collectImplicitComponents(@NotNull DataComponentMap.Builder builder) {
        super.collectImplicitComponents(builder);
        for (ITileComponent component : components) {
            component.collectImplicitComponents(builder);
        }
        for (ContainerType<?, ?, ?> type : ContainerType.TYPES) {
            if (persistsToItem(type)) {
                type.copyFromTile(this, builder);
            }
        }
        if (this instanceof ITileFilterHolder<?> filterHolder) {
            FilterManager<?> filterManager = filterHolder.getFilterManager();
            if (!filterManager.getFilters().isEmpty()) {
                builder.set(MekanismDataComponents.FILTER_AWARE, new FilterAware(List.copyOf(filterManager.getFilters())));
            }
        }
        if (supportsRedstone()) {
            builder.set(MekanismDataComponents.REDSTONE_CONTROL, controlType);
        }
    }

    @Override
    public void addContainerTrackers(MekanismContainer container) {
        SyncMapper.INSTANCE.setup(container, getClass(), () -> this);

        for (ITileComponent component : components) {
            component.trackForMainContainer(container);
        }
        if (supportsRedstone()) {
            container.track(SyncableEnum.create(RedstoneControl.BY_ID, RedstoneControl.DISABLED, () -> controlType, value -> controlType = value));
            container.track(SyncableBoolean.create(this::isPowered, value -> redstone = value));
            container.track(SyncableBoolean.create(this::wasPowered, value -> redstoneLastTick = value));
        }
        boolean isClient = isRemote();
        if (canHandleChemicals() && syncs(ContainerType.CHEMICAL)) {
            List<IChemicalTank> chemicalTanks = getChemicalTanks(null);
            for (IChemicalTank chemicalTank : chemicalTanks) {
                container.track(SyncableChemicalStack.create(chemicalTank, isClient));
            }
        }
        if (canHandleFluid() && syncs(ContainerType.FLUID)) {
            List<IExtendedFluidTank> fluidTanks = getFluidTanks(null);
            for (IExtendedFluidTank fluidTank : fluidTanks) {
                container.track(SyncableFluidStack.create(fluidTank, isClient));
            }
        }
        if (canHandleHeat() && syncs(ContainerType.HEAT)) {
            List<IHeatCapacitor> heatCapacitors = getHeatCapacitors(null);
            for (IHeatCapacitor capacitor : heatCapacitors) {
                container.track(SyncableDouble.create(capacitor::getHeat, capacitor::setHeat));
                if (capacitor instanceof BasicHeatCapacitor heatCapacitor) {
                    container.track(SyncableDouble.create(capacitor::getHeatCapacity, capacity -> heatCapacitor.setHeatCapacity(capacity, false)));
                }
            }
        }
        if (canHandleEnergy() && syncs(ContainerType.ENERGY)) {
            trackLastEnergy(container);
            List<IEnergyContainer> energyContainers = getEnergyContainers(null);
            for (IEnergyContainer energyContainer : energyContainers) {
                if (energyContainer instanceof MachineEnergyContainer<?> machineEnergy) {
                    if (supportsUpgrades() || machineEnergy.adjustableRates()) {
                        container.track(SyncableLong.create(machineEnergy::getMaxEnergy, machineEnergy::setMaxEnergy));
                        container.track(SyncableLong.create(machineEnergy::getEnergyPerTick, machineEnergy::setEnergyPerTick));
                    }
                }
                container.track(SyncableLong.create(energyContainer::getEnergy, energyContainer::setEnergy));
            }
        }
    }

    protected void trackLastEnergy(MekanismContainer container) {
        container.track(SyncableLong.create(lastEnergyTracker::getLastEnergyReceived, lastEnergyTracker::setLastEnergyReceived));
    }

    @NotNull
    @Override
    public CompoundTag getReducedUpdateTag(@NotNull HolderLookup.Provider provider) {
        CompoundTag updateTag = super.getReducedUpdateTag(provider);
        for (ITileComponent component : components) {
            component.addToUpdateTag(updateTag);
        }
        updateTag.putFloat(SerializationConstants.RADIATION, radiationScale);
        return updateTag;
    }

    @Override
    public void handleUpdateTag(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider provider) {
        super.handleUpdateTag(tag, provider);
        for (ITileComponent component : components) {
            component.readFromUpdateTag(tag);
        }
        radiationScale = tag.getFloat(SerializationConstants.RADIATION);
    }

    @Override
    public void onAdded() {
        super.onAdded();
        updatePower();
        if (getClientActive()) {
            currentActive = true;
        }
    }

    @Override
    public TileComponentFrequency getFrequencyComponent() {
        return frequencyComponent;
    }

    @NotNull
    @Override
    @ComputerMethod(restriction = MethodRestriction.DIRECTIONAL)
    public final Direction getDirection() {
        if (isDirectional()) {
            if (cachedDirection != null) {
                return cachedDirection;
            }
            BlockState state = getBlockState();
            cachedDirection = Attribute.getFacing(state);
            if (cachedDirection != null) {
                return cachedDirection;
            } else if (!getType().isValid(state)) {
                Mekanism.logger.warn("Error invalid block for tile {} at {} in {}. Unable to get direction, falling back to north, "
                        + "things will probably not work correctly. This is almost certainly due to another mod incorrectly "
                        + "trying to move this tile and not properly updating the position.", RegistryUtils.getName(getType()), worldPosition, level);
            }
        }
        return Direction.NORTH;
    }

    protected void invalidateDirectionCaches(Direction newDirection) {
        cachedDirection = newDirection;
    }

    @Override
    public void setFacing(@NotNull Direction direction) {
        setFacing(direction, true);
    }

    public void setFacing(@NotNull Direction direction, boolean notifyCaps) {
        if (isDirectional() && direction != cachedDirection && level != null) {
            invalidateDirectionCaches(direction);
            BlockState state = Attribute.setFacing(getBlockState(), direction);
            if (state != null) {
                level.setBlockAndUpdate(worldPosition, state);
                if (notifyCaps) {
                    invalidateCapabilitiesFull();
                }
            }
        }
    }

    @Override
    @ComputerMethod(nameOverride = "getRedstoneMode", restriction = MethodRestriction.REDSTONE_CONTROL)
    public RedstoneControl getControlType() {
        return controlType;
    }

    @Override
    public void setControlType(@NotNull RedstoneControl type) {
        if (supportsRedstone()) {
            type = supportedOrNextType(type);
            if (type != controlType) {
                controlType = type;
                markForSave();
            }
        }
    }

    private RedstoneControl supportedOrNextType(@NotNull RedstoneControl type) {
        Objects.requireNonNull(type);
        if (!supportsMode(type)) {
            type = type.getNext(this::supportsMode);
        }
        return type;
    }

    @Override
    public boolean isPowered() {
        return supportsRedstone() && redstone;
    }

    @Override
    public final boolean wasPowered() {
        return supportsRedstone() && redstoneLastTick;
    }

    public final void updatePower() {
        if (supportsRedstone()) {
            boolean power = level.hasNeighborSignal(getBlockPos());
            if (redstone != power) {
                redstone = power;
                onPowerChange();
            }
        }
    }

    public final boolean isRedstoneActivated() {
        return !supportsRedstone() ||
                switch (controlType) {
                    case DISABLED -> true;
                    case HIGH -> isPowered();
                    case LOW -> !isPowered();
                    case PULSE -> isPowered() && !redstoneLastTick;
                };
    }

    public boolean canFunction() {
        return isRedstoneActivated();
    }

    protected boolean makesComparatorDirty(ContainerType<?, ?, ?> type) {
        return type == ContainerType.ITEM;
    }

    protected final IContentsListener getListener(ContainerType<?, ?, ?> type, IContentsListener saveOnlyListener) {
        return !supportsComparator() || makesComparatorDirty(type) ? this : saveOnlyListener;
    }

    @Override
    @ComputerMethod(nameOverride = "getComparatorLevel", restriction = MethodRestriction.COMPARATOR)
    public int getCurrentRedstoneLevel() {
        return currentRedstoneLevel;
    }

    @NotNull
    @Override
    public Set<Upgrade> getSupportedUpgrade() {
        if (supportsUpgrades()) {
            return Attribute.getOrThrow(getBlockType(), AttributeUpgradeSupport.class).supportedUpgrades();
        }
        return Collections.emptySet();
    }

    @Override
    public TileComponentUpgrade getComponent() {
        return upgradeComponent;
    }

    @Override
    public void recalculateUpgrades(Upgrade upgrade) {
        if (upgrade == Upgrade.SPEED) {
            for (IEnergyContainer energyContainer : getEnergyContainers(null)) {
                if (energyContainer instanceof MachineEnergyContainer<?> machineEnergy) {
                    machineEnergy.updateEnergyPerTick();
                }
            }
        } else if (upgrade == Upgrade.ENERGY) {
            for (IEnergyContainer energyContainer : getEnergyContainers(null)) {
                if (energyContainer instanceof MachineEnergyContainer<?> machineEnergy) {
                    machineEnergy.updateMaxEnergy();
                    machineEnergy.updateEnergyPerTick();
                }
            }
        }
    }

    @Nullable
    protected IInventorySlotHolder getInitialInventory(IContentsListener listener) {
        return null;
    }

    @NotNull
    @Override
    public final List<IInventorySlot> getInventorySlots(@Nullable Direction side) {
        return itemHandlerManager != null ? itemHandlerManager.getContainers(side) : Collections.emptyList();
    }

    @Override
    public void onContentsChanged() {
        setChanged();
    }

    public boolean shouldDumpRadiation() {
        return canHandleChemicals();
    }

    @Override
    public float getRadiationScale() {
        return IRadiationManager.INSTANCE.isRadiationEnabled() ? radiationScale : 0;
    }

    @Nullable
    public IChemicalTankHolder getInitialChemicalTanks(IContentsListener listener) {
        return null;
    }

    @NotNull
    @Override
    public List<IChemicalTank> getChemicalTanks(@Nullable Direction side) {
        return chemicalHandlerManager == null ? Collections.emptyList() : chemicalHandlerManager.getContainers(side);
    }

    @Deprecated(forRemoval = true)
    public List<IChemicalTank> getLegacyGasTanks() {
        return getChemicalTanks(null);
    }

    @Deprecated(forRemoval = true)
    public List<IChemicalTank> getLegacyInfuseTanks() {
        return getChemicalTanks(null);
    }

    @Deprecated(forRemoval = true)
    public List<IChemicalTank> getLegacyPigmentTanks() {
        return getChemicalTanks(null);
    }

    @Deprecated(forRemoval = true)
    public List<IChemicalTank> getLegacySlurryTanks() {
        return getChemicalTanks(null);
    }

    @Nullable
    protected IFluidTankHolder getInitialFluidTanks(IContentsListener listener) {
        return null;
    }

    @NotNull
    @Override
    public final List<IExtendedFluidTank> getFluidTanks(@Nullable Direction side) {
        return fluidHandlerManager != null ? fluidHandlerManager.getContainers(side) : Collections.emptyList();
    }

    @Nullable
    protected IEnergyContainerHolder getInitialEnergyContainers(IContentsListener listener) {
        return null;
    }

    @NotNull
    @Override
    public final List<IEnergyContainer> getEnergyContainers(@Nullable Direction side) {
        return energyHandlerManager != null ? energyHandlerManager.getContainers(side) : Collections.emptyList();
    }

    @Override
    public long insertEnergy(int container, long amount, @Nullable Direction side, @NotNull Action action) {
        return trackLastEnergy(amount, action, IMekanismStrictEnergyHandler.super.insertEnergy(container, amount, side, action));
    }

    @Override
    public long insertEnergy(long amount, @Nullable Direction side, @NotNull Action action) {
        return trackLastEnergy(amount, action, IMekanismStrictEnergyHandler.super.insertEnergy(amount, side, action));
    }

    private long trackLastEnergy(long amount, @NotNull Action action, long remainder) {
        if (action.execute()) {
            lastEnergyTracker.received(level == null ? 0 : level.getGameTime(), amount - remainder);
        }
        return remainder;
    }

    @Nullable
    protected IHeatCapacitorHolder getInitialHeatCapacitors(IContentsListener listener, CachedAmbientTemperature ambientTemperature) {
        return null;
    }

    @Override
    public double getAmbientTemperature(@NotNull Direction side) {
        if (canHandleHeat() && ambientTemperature != null) {
            return ambientTemperature.getTemperature(side);
        }
        return ITileHeatHandler.super.getAmbientTemperature(side);
    }

    @Nullable
    @Override
    public IHeatHandler getAdjacent(@NotNull Direction side) {
        if (canHandleHeat() && getHeatCapacitorCount(side) > 0) {
            return getAdjacentUnchecked(side);
        }
        return null;
    }

    @Nullable
    protected IHeatHandler getAdjacentUnchecked(@NotNull Direction side) {
        BlockCapabilityCache<IHeatHandler, @Nullable Direction> cache = adjacentHeatCaps.get(side);
        if (cache == null) {
            cache = BlockCapabilityCache.create(Capabilities.HEAT, (ServerLevel) level, worldPosition.relative(side), side.getOpposite());
            adjacentHeatCaps.put(side, cache);
        }
        return cache.getCapability();
    }

    @NotNull
    @Override
    public final List<IHeatCapacitor> getHeatCapacitors(@Nullable Direction side) {
        return heatHandlerManager != null ? heatHandlerManager.getContainers(side) : Collections.emptyList();
    }

    @Override
    public CompoundTag getConfigurationData(HolderLookup.Provider provider, Player player) {
        CompoundTag data = new CompoundTag();
        writeSustainedData(provider, data);
        getFrequencyComponent().writeConfiguredFrequencies(provider, data);
        return data;
    }

    @Override
    public void setConfigurationData(HolderLookup.Provider provider, Player player, CompoundTag data) {
        readSustainedData(provider, data);
        getFrequencyComponent().readConfiguredFrequencies(provider, player, data);
    }

    @Override
    public Block getConfigurationDataType() {
        return getBlockType();
    }

    @Override
    public void configurationDataSet() {
        setChanged();
        invalidateCapabilitiesFull();
        sendUpdatePacket();
        WorldUtils.notifyLoadedNeighborsOfTileChange(getLevel(), this.getBlockPos());
    }

    @Override
    public TileComponentSecurity getSecurity() {
        return securityComponent;
    }

    @Override
    public void onSecurityChanged(@NotNull SecurityMode old, @NotNull SecurityMode mode) {
        if (!isRemote() && hasGui() && level != null) {
            BlockSecurityUtils.get().securityChanged(playersUsing, level, worldPosition, this, old, mode);
        }
    }

    @Override
    public boolean getActive() {
        return isRemote() ? getClientActive() : currentActive;
    }

    private boolean getClientActive() {
        return activeAttribute != null && activeAttribute.isActive(getBlockState());
    }

    @Override
    public void setActive(boolean active) {
        if (isActivatable() && active != currentActive) {
            BlockState state = getBlockState();
            if (activeAttribute != null) {
                currentActive = active;
                if (getClientActive() != active) {
                    if (active) {
                        level.setBlockAndUpdate(worldPosition, activeAttribute.setActive(state, true));
                    } else {
                        if (updateDelay == 0) {
                            level.setBlockAndUpdate(worldPosition, activeAttribute.setActive(state, currentActive));
                        }
                        updateDelay = delaySupplier.getAsInt();
                    }
                }
            }
        }
    }

    protected boolean canPlaySound() {
        return getActive();
    }

    private void updateSound() {
        if (!hasSound() || !MekanismConfig.client.enableMachineSounds.get() || soundEvent == null) {
            return;
        }
        if (canPlaySound() && !isRemoved()) {
            if (--playSoundCooldown > 0) {
                return;
            }
            SoundEvent sound = soundEvent.get();
            if (sound != lastSoundEvent) {
                if (activeSound != null) {
                    SoundHandler.stopTileSound(getSoundPos());
                    activeSound = null;
                }
                lastSoundEvent = sound;
            }

            if (!isFullyMuffled() && (activeSound == null || !Minecraft.getInstance().getSoundManager().isActive(activeSound))) {
                activeSound = SoundHandler.startTileSound(lastSoundEvent, getSoundCategory(), getInitialVolume(), level.getRandom(), getSoundPos());
            }
            playSoundCooldown = SharedConstants.TICKS_PER_SECOND;
        } else if (activeSound != null) {
            SoundHandler.stopTileSound(getSoundPos());
            activeSound = null;
            playSoundCooldown = 0;
        }
    }

    protected boolean isFullyMuffled() {
        if (hasSound() && supportsUpgrade(Upgrade.MUFFLING)) {
            return getComponent().getUpgrades(Upgrade.MUFFLING) >= Upgrade.MUFFLING.getMax();
        }
        return false;
    }

    @Override
    public String getComputerName() {
        if (hasComputerSupport()) {
            return Attribute.getOrThrow(getBlockType(), AttributeComputerIntegration.class).name();
        }
        return "";
    }

    public void validateSecurityIsPublic() throws ComputerException {
        if (hasSecurity() && IBlockSecurityUtils.INSTANCE.getSecurityMode(getWorldNN(), worldPosition, this) != SecurityMode.PUBLIC) {
            throw new ComputerException("Setter not available due to machine security not being public.");
        }
    }

    @Override
    public void getComputerMethods(BoundMethodHolder holder) {
        IComputerTile.super.getComputerMethods(holder);
        for (ITileComponent component : components) {
            FactoryRegistry.bindTo(holder, component);
        }
    }

    private long getTotalEnergy(ToLongFunction<IEnergyContainer> getter) {
        long total = 0;
        List<IEnergyContainer> energyContainers = getEnergyContainers(null);
        for (IEnergyContainer energyContainer : energyContainers) {
            total = MathUtils.addClamped(total, getter.applyAsLong(energyContainer));
        }
        return total;
    }
}