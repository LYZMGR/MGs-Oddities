package github.mgrlyz.mgsoddities.registries;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import github.mgrlyz.mgsoddities.MGsOddities;
import github.mgrlyz.mgsoddities.capabilities.Capabilities;
import github.mgrlyz.mgsoddities.registration.impl.TileEntityTypeRegistryObject;
import mekanism.common.content.blocktype.FactoryType;
import mekanism.common.registration.impl.BlockRegistryObject;
import mekanism.common.registration.impl.TileEntityTypeDeferredRegister;
import mekanism.common.tier.FactoryTier;
import mekanism.common.tile.base.CapabilityTileEntity;
import mekanism.common.tile.factory.TileEntityFactory;
import mekanism.common.tile.transmitter.TileEntityMechanicalPipe;
import mekanism.common.tile.transmitter.TileEntityTransmitter;

public class MGsOdditiesTileEntityTypes {
    private MGsOdditiesTileEntityTypes() {
    }

    public static final TileEntityTypeDeferredRegister TILE_ENTITY_TYPES = new TileEntityTypeDeferredRegister(MGsOddities.MODID);

    private static final Table<FactoryTier, FactoryType, TileEntityTypeRegistryObject<? extends TileEntityFactory<?>>> FACTORIES = HashBasedTable.create();



    public static final TileEntityTypeRegistryObject<TileEntityMechanicalPipe> PARAGON_MECHANICAL_PIPE = registerPipe(MGsOdditiesBlocks.PARAGON_MECHANICAL_PIPE);

    private static <BE extends TileEntityTransmitter> TileEntityTypeDeferredRegister.BlockEntityTypeBuilder<BE> transmitterBuilder(BlockRegistryObject<?, ?> block, MekanismTileEntityTypes.BlockEntityFactory<BE> factory) {
        return TILE_ENTITY_TYPES.builder(block, (pos, state) -> factory.create(block, pos, state))
                .serverTicker(TileEntityTransmitter::tickServer)
                .withSimple(Capabilities.ALLOY_INTERACTION)
                .with(Capabilities.CONFIGURABLE, TileEntityTransmitter.CONFIGURABLE_PROVIDER);
    }

    private static TileEntityTypeRegistryObject<TileEntityMechanicalPipe> registerPipe(BlockRegistryObject<?, ?> block) {
        TileEntityTypeDeferredRegister.BlockEntityTypeBuilder<TileEntityMechanicalPipe> builder = transmitterBuilder(block, TileEntityMechanicalPipe::new)
                .with(Capabilities.FLUID.block(), CapabilityTileEntity.FLUID_HANDLER_PROVIDER);
        return builder.build();
    }
}
