package github.mgrlyz.mgsoddities.common.tile;

import github.mgrlyz.mgsoddities.common.block.attribute.MGsOdditiesAttribute;
import github.mgrlyz.mgsoddities.common.capabilities.energy.MGsOdditiesEnergyCubeEnergyContainer;
import github.mgrlyz.mgsoddities.common.tier.ECtier;
import mekanism.api.IContentsListener;
import mekanism.api.RelativeSide;
import mekanism.common.attachments.containers.ContainerType;
import mekanism.common.capabilities.holder.energy.EnergyContainerHelper;
import mekanism.common.capabilities.holder.energy.IEnergyContainerHolder;
import mekanism.common.capabilities.holder.slot.IInventorySlotHolder;
import mekanism.common.capabilities.holder.slot.InventorySlotHelper;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper;
import mekanism.common.integration.computer.annotation.WrappingComputerMethod;
import mekanism.common.inventory.container.slot.SlotOverlay;
import mekanism.common.inventory.slot.EnergyInventorySlot;
import mekanism.common.lib.transmitter.TransmissionType;
import mekanism.common.tile.component.ITileComponent;
import mekanism.common.tile.component.TileComponentEjector;
import mekanism.common.tile.component.config.ConfigInfo;
import mekanism.common.tile.component.config.DataType;
import mekanism.common.tile.component.config.slot.ISlotInfo;
import mekanism.common.tile.prefab.TileEntityConfigurableMachine;
import mekanism.common.upgrade.EnergyCubeUpgradeData;
import mekanism.common.upgrade.IUpgradeData;
import mekanism.common.util.EnumUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.NBTUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelProperty;
import org.jetbrains.annotations.NotNull;

public class MGsOdditiesTileEntityEnergyCube extends TileEntityConfigurableMachine {
    public static final ModelProperty<CubeSideState[]> SIDE_STATE_PROPERTY = new ModelProperty();
    private ECtier tier;
    private float prevScale;
    private MGsOdditiesEnergyCubeEnergyContainer energyContainer;
    @WrappingComputerMethod(
            wrapper = SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper.class,
            methodNames = {"getChargeItem"},
            docPlaceholder = "charge slot"
    )
    EnergyInventorySlot chargeSlot;
    @WrappingComputerMethod(
            wrapper = SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper.class,
            methodNames = {"getDischargeItem"},
            docPlaceholder = "discharge slot"
    )
    EnergyInventorySlot dischargeSlot;

    public MGsOdditiesTileEntityEnergyCube(Holder<Block> blockProvider, BlockPos pos, BlockState state) {
        super(blockProvider, pos, state);
        this.configComponent.setupIOConfig(TransmissionType.ITEM, this.chargeSlot, this.dischargeSlot, RelativeSide.FRONT, true).setCanEject(false);
        this.configComponent.setupIOConfig(TransmissionType.ENERGY, this.energyContainer, RelativeSide.FRONT).setEjecting(true);
        this.ejectorComponent = new TileComponentEjector(this, () -> this.tier.getOutput());
        this.ejectorComponent.setOutputData(this.configComponent, new TransmissionType[]{TransmissionType.ENERGY}).setCanEject((type) -> this.canFunction());
    }

    protected void presetVariables() {
        super.presetVariables();
        this.tier = (ECtier) MGsOdditiesAttribute.getAdvanceTier(this.getBlockHolder(), ECtier.class);
    }

    protected @NotNull IEnergyContainerHolder getInitialEnergyContainers(IContentsListener listener) {
        EnergyContainerHelper builder = EnergyContainerHelper.forSideWithConfig(this);
        builder.addContainer(this.energyContainer = MGsOdditiesEnergyCubeEnergyContainer.create(this.tier, listener));
        return builder.build();
    }

    protected @NotNull IInventorySlotHolder getInitialInventory(IContentsListener listener) {
        InventorySlotHelper builder = InventorySlotHelper.forSideWithConfig(this);
        builder.addSlot(this.dischargeSlot = EnergyInventorySlot.fillOrConvert(this.energyContainer, this::getLevel, listener, 17, 35));
        builder.addSlot(this.chargeSlot = EnergyInventorySlot.drain(this.energyContainer, listener, 143, 35));
        this.dischargeSlot.setSlotOverlay(SlotOverlay.MINUS);
        this.chargeSlot.setSlotOverlay(SlotOverlay.PLUS);
        return builder.build();
    }

    public ECtier getAdvanceTier() {
        return this.tier;
    }

    protected boolean onUpdateServer() {
        boolean sendUpdatePacket = super.onUpdateServer();
        this.chargeSlot.drainContainer();
        this.dischargeSlot.fillContainerOrConvert();
        float newScale = MekanismUtils.getScale(this.prevScale, this.energyContainer);
        if (newScale != this.prevScale) {
            this.prevScale = newScale;
            sendUpdatePacket = true;
        }

        return sendUpdatePacket;
    }

    public int getRedstoneLevel() {
        return MekanismUtils.redstoneLevelFromContents(this.energyContainer.getEnergy(), this.energyContainer.getMaxEnergy());
    }

    protected boolean makesComparatorDirty(ContainerType<?, ?, ?> type) {
        return type == ContainerType.ENERGY;
    }

    public void parseUpgradeData(HolderLookup.Provider provider, @NotNull IUpgradeData upgradeData) {
        if (upgradeData instanceof EnergyCubeUpgradeData data) {
            this.redstone = data.redstone;
            this.setControlType(data.controlType);
            this.getEnergyContainer().setEnergy(data.energyContainer.getEnergy());
            this.chargeSlot.setStack(data.chargeSlot.getStack());
            this.dischargeSlot.deserializeNBT(provider, data.dischargeSlot.serializeNBT(provider));

            for(ITileComponent component : this.getComponents()) {
                component.read(data.components, provider);
            }
        } else {
            super.parseUpgradeData(provider, upgradeData);
        }

    }

    public MGsOdditiesEnergyCubeEnergyContainer getEnergyContainer() {
        return this.energyContainer;
    }

    public @NotNull EnergyCubeUpgradeData getUpgradeData(HolderLookup.Provider provider) {
        return new EnergyCubeUpgradeData(provider, this.redstone, this.getControlType(), this.getEnergyContainer(), this.chargeSlot, this.dischargeSlot, this.getComponents());
    }

    public float getEnergyScale() {
        return this.prevScale;
    }

    public @NotNull CompoundTag getReducedUpdateTag(@NotNull HolderLookup.@NotNull Provider provider) {
        CompoundTag updateTag = super.getReducedUpdateTag(provider);
        updateTag.putFloat("scale", this.prevScale);
        return updateTag;
    }

    public void handleUpdateTag(@NotNull CompoundTag tag, @NotNull HolderLookup.@NotNull Provider provider) {
        ConfigInfo config = this.getConfig().getConfig(TransmissionType.ENERGY);
        DataType[] currentConfig = new DataType[EnumUtils.SIDES.length];
        if (config != null) {
            for(RelativeSide side : EnumUtils.SIDES) {
                currentConfig[side.ordinal()] = config.getDataType(side);
            }
        }

        super.handleUpdateTag(tag, provider);
        NBTUtils.setFloatIfPresent(tag, "scale", (scale) -> this.prevScale = scale);
        if (config != null) {
            for(RelativeSide side : EnumUtils.SIDES) {
                if (currentConfig[side.ordinal()] != config.getDataType(side)) {
                    this.updateModelData();
                    break;
                }
            }
        }

    }

    public @NotNull ModelData getModelData() {
        ConfigInfo config = this.getConfig().getConfig(TransmissionType.ENERGY);
        if (config == null) {
            return super.getModelData();
        } else {
            CubeSideState[] sideStates = new CubeSideState[EnumUtils.SIDES.length];

            for(RelativeSide side : EnumUtils.SIDES) {
                CubeSideState state = MGsOdditiesTileEntityEnergyCube.CubeSideState.INACTIVE;
                ISlotInfo slotInfo = config.getSlotInfo(side);
                if (slotInfo != null) {
                    if (slotInfo.canOutput()) {
                        state = MGsOdditiesTileEntityEnergyCube.CubeSideState.ACTIVE_LIT;
                    } else if (slotInfo.canInput()) {
                        state = MGsOdditiesTileEntityEnergyCube.CubeSideState.ACTIVE_UNLIT;
                    }
                }

                sideStates[side.ordinal()] = state;
            }
            return ModelData.builder().with(SIDE_STATE_PROPERTY, sideStates).build();
        }
    }

    public static enum CubeSideState {
        ACTIVE_LIT,
        ACTIVE_UNLIT,
        INACTIVE;

        private CubeSideState() {
        }
    }
}