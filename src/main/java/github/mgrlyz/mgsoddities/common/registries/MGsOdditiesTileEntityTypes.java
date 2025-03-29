package github.mgrlyz.mgsoddities.common.registries;

import github.mgrlyz.mgsoddities.common.capabilities.MGsOdditiesCapabilities;
import github.mgrlyz.mgsoddities.common.registries.block.MGsOdditiesBlocks;
import github.mgrlyz.mgsoddities.common.tile.transmitter.*;
import mekanism.api.functions.ConstantPredicates;
import mekanism.common.Mekanism;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.integration.computer.ComputerCapabilityHelper;
import mekanism.common.integration.energy.EnergyCompatUtils;
import mekanism.common.registration.impl.BlockRegistryObject;
import mekanism.common.registration.impl.TileEntityTypeDeferredRegister;
import mekanism.common.registration.impl.TileEntityTypeRegistryObject;
import mekanism.common.tile.base.CapabilityTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class MGsOdditiesTileEntityTypes {
    public static final TileEntityTypeDeferredRegister MGSODDITIES_TILE_ENTITY_TYPES = new TileEntityTypeDeferredRegister("mgsoddities");
    
    public static final TileEntityTypeRegistryObject<MGsOdditiesTileEntityUniversalCable> PARAGON_UNIVERSAL_CABLE;
    public static final TileEntityTypeRegistryObject<MGsOdditiesTileEntityUniversalCable> APOTHEOSIS_UNIVERSAL_CABLE;
    public static final TileEntityTypeRegistryObject<MGsOdditiesTileEntityMechanicalPipe> PARAGON_MECHANICAL_PIPE;
    public static final TileEntityTypeRegistryObject<MGsOdditiesTileEntityMechanicalPipe> APOTHEOSIS_MECHANICAL_PIPE;
    public static final TileEntityTypeRegistryObject<MGsOdditiesTileEntityPressurizedTube> PARAGON_PRESSURIZED_TUBE;
    public static final TileEntityTypeRegistryObject<MGsOdditiesTileEntityPressurizedTube> APOTHEOSIS_PRESSURIZED_TUBE;
    public static final TileEntityTypeRegistryObject<MGsOdditiesTileEntityLogisticalTransporter> PARAGON_LOGISTICAL_TRANSPORTER;
    public static final TileEntityTypeRegistryObject<MGsOdditiesTileEntityLogisticalTransporter> APOTHEOSIS_LOGISTICAL_TRANSPORTER;
    public static final TileEntityTypeRegistryObject<MGsOdditiesTileEntityThermodynamicConductor> PARAGON_THERMODYNAMIC_CONDUCTOR;
    public static final TileEntityTypeRegistryObject<MGsOdditiesTileEntityThermodynamicConductor> APOTHEOSIS_THERMODYNAMIC_CONDUCTOR;

    public MGsOdditiesTileEntityTypes() {
    }

    private static TileEntityTypeRegistryObject<MGsOdditiesTileEntityUniversalCable> registerCable(BlockRegistryObject<?, ?> block) {
        TileEntityTypeDeferredRegister.BlockEntityTypeBuilder<MGsOdditiesTileEntityUniversalCable> builder = transmitterBuilder(block, MGsOdditiesTileEntityUniversalCable::new);
        EnergyCompatUtils.addBlockCapabilities(builder);
        if (Mekanism.hooks.computerCompatEnabled()) {
            ComputerCapabilityHelper.addComputerCapabilities(builder, ConstantPredicates.ALWAYS_TRUE);
        }
        return builder.build();
    }

    private static TileEntityTypeRegistryObject<MGsOdditiesTileEntityMechanicalPipe> registerPipe(BlockRegistryObject<?, ?> block) {
        TileEntityTypeDeferredRegister.BlockEntityTypeBuilder<MGsOdditiesTileEntityMechanicalPipe> builder = transmitterBuilder(block, MGsOdditiesTileEntityMechanicalPipe::new)
                .with(Capabilities.FLUID.block(), CapabilityTileEntity.FLUID_HANDLER_PROVIDER);
        if (Mekanism.hooks.computerCompatEnabled()) {
            ComputerCapabilityHelper.addComputerCapabilities(builder, ConstantPredicates.ALWAYS_TRUE);
        }
        return builder.build();
    }

    private static TileEntityTypeRegistryObject<MGsOdditiesTileEntityPressurizedTube> registerTube(BlockRegistryObject<?, ?> block) {
        TileEntityTypeDeferredRegister.BlockEntityTypeBuilder<MGsOdditiesTileEntityPressurizedTube> builder = transmitterBuilder(block, MGsOdditiesTileEntityPressurizedTube::new)
                .with(Capabilities.CHEMICAL.block(), CapabilityTileEntity.CHEMICAL_HANDLER_PROVIDER);
        if (Mekanism.hooks.computerCompatEnabled()) {
            ComputerCapabilityHelper.addComputerCapabilities(builder, ConstantPredicates.ALWAYS_TRUE);
        }
        return builder.build();
    }

    private static <BE extends MGsOdditiesTileEntityLogisticalTransporterBase> TileEntityTypeRegistryObject<BE> registerTransporter(BlockRegistryObject<?, ?> block, BlockEntityFactory<BE> factory) {
        return transporterBuilder(block, factory).build();
    }

    private static <BE extends MGsOdditiesTileEntityLogisticalTransporterBase> TileEntityTypeDeferredRegister.BlockEntityTypeBuilder<BE> transporterBuilder(BlockRegistryObject<?, ?> block, BlockEntityFactory<BE> factory) {
        return transmitterBuilder(block, factory)
                .clientTicker(MGsOdditiesTileEntityLogisticalTransporterBase::tickClient)
                .with(Capabilities.ITEM.block(), CapabilityTileEntity.ITEM_HANDLER_PROVIDER);
    }

    private static TileEntityTypeRegistryObject<MGsOdditiesTileEntityThermodynamicConductor> registerConductor(BlockRegistryObject<?, ?> block) {
        return transmitterBuilder(block, MGsOdditiesTileEntityThermodynamicConductor::new)
                .with(Capabilities.HEAT, CapabilityTileEntity.HEAT_HANDLER_PROVIDER)
                .build();
    }

    private static <BE extends MGsOdditiesTileEntityTransmitter> TileEntityTypeDeferredRegister.BlockEntityTypeBuilder<BE> transmitterBuilder(BlockRegistryObject<?, ?> block, BlockEntityFactory<BE> factory) {
        return MGSODDITIES_TILE_ENTITY_TYPES.builder(block, (pos, state) -> factory.create(block, pos, state))
                .serverTicker(MGsOdditiesTileEntityTransmitter::tickServer)
                .withSimple(MGsOdditiesCapabilities.MGSODDITIES_ALLOY_INTERACTION)
                .with(Capabilities.CONFIGURABLE, MGsOdditiesTileEntityTransmitter.CONFIGURABLE_PROVIDER);
    }

    static {
        PARAGON_UNIVERSAL_CABLE = registerCable(MGsOdditiesBlocks.PARAGON_UNIVERSAL_CABLE);
        APOTHEOSIS_UNIVERSAL_CABLE = registerCable(MGsOdditiesBlocks.APOTHEOSIS_UNIVERSAL_CABLE);
        PARAGON_MECHANICAL_PIPE = registerPipe(MGsOdditiesBlocks.PARAGON_MECHANICAL_PIPE);
        APOTHEOSIS_MECHANICAL_PIPE = registerPipe(MGsOdditiesBlocks.APOTHEOSIS_MECHANICAL_PIPE);
        PARAGON_PRESSURIZED_TUBE = registerTube(MGsOdditiesBlocks.PARAGON_PRESSURIZED_TUBE);
        APOTHEOSIS_PRESSURIZED_TUBE = registerTube(MGsOdditiesBlocks.APOTHEOSIS_PRESSURIZED_TUBE);
        PARAGON_LOGISTICAL_TRANSPORTER = registerTransporter(MGsOdditiesBlocks.PARAGON_LOGISTICAL_TRANSPORTER, MGsOdditiesTileEntityLogisticalTransporter::new);
        APOTHEOSIS_LOGISTICAL_TRANSPORTER = registerTransporter(MGsOdditiesBlocks.APOTHEOSIS_LOGISTICAL_TRANSPORTER, MGsOdditiesTileEntityLogisticalTransporter::new);
        PARAGON_THERMODYNAMIC_CONDUCTOR = registerConductor(MGsOdditiesBlocks.PARAGON_THERMODYNAMIC_CONDUCTOR);
        APOTHEOSIS_THERMODYNAMIC_CONDUCTOR = registerConductor(MGsOdditiesBlocks.APOTHEOSIS_THERMODYNAMIC_CONDUCTOR);

    }
    @FunctionalInterface
    private interface BlockEntityFactory<BE extends BlockEntity> {
        BE create(Holder<Block> var1, BlockPos var2, BlockState var3);
    }
}