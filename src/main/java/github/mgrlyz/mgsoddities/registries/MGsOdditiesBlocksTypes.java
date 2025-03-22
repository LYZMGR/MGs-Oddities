package github.mgrlyz.mgsoddities.registries;

import github.mgrlyz.mgsoddities.MGsOdditiesLang;
import github.mgrlyz.mgsoddities.api.tier.ITier;
import github.mgrlyz.mgsoddities.block.attribute.AttributeSideConfig;
import github.mgrlyz.mgsoddities.block.attribute.AttributeTier;
import github.mgrlyz.mgsoddities.config.MGsOdditiesConfig;
import github.mgrlyz.mgsoddities.content.blocktype.BlockTypeTile;
import github.mgrlyz.mgsoddities.registration.impl.TileEntityTypeRegistryObject;
import github.mgrlyz.mgsoddities.tier.PipeTier;
import github.mgrlyz.mgsoddities.tile.transmitter.TileEntityMechanicalPipe;
import mekanism.common.config.IMekanismConfig;
import mekanism.common.config.MekanismConfig;
import mekanism.common.content.blocktype.FactoryType;
import mekanism.common.content.blocktype.Machine;
import mekanism.common.registries.MekanismSounds;
import mekanism.common.tile.machine.TileEntityEnergizedSmelter;
import mekanism.common.tile.transmitter.TileEntityTransmitter;

import java.util.function.Supplier;

public class MGsOdditiesBlocksTypes {
    public static final BlockTypeTile<TileEntityMechanicalPipe> PARAGON_MECHANICAL_PIPE = createPipe(PipeTier.PARAGON, () -> MGsOdditiesBlocksTypes.PARAGON_MECHANICAL_PIPE);
    public static final BlockTypeTile<TileEntityMechanicalPipe> APOTHEOSIS_MECHANICAL_PIPE = createPipe(PipeTier.PARAGON, () -> MGsOdditiesBlocksTypes.APOTHEOSIS_MECHANICAL_PIPE);

    // Energized Smelter
    public static final Machine.FactoryMachine<TileEntityEnergizedSmelter> ENERGIZED_SMELTER = Machine.MachineBuilder
            .createFactoryMachine(() -> MGsOdditiesTileEntityTypes.ENERGIZED_SMELTER, MGsOdditiesLang.DESCRIPTION_ENERGIZED_SMELTER, FactoryType.SMELTING)
            .withGui(() -> MGsOdditiesContainerTypes.ENERGIZED_SMELTER)
            .withSound(MekanismSounds.ENERGIZED_SMELTER)
            .withEnergyConfig(MGsOdditiesConfig.usage.energizedSmelter, MGsOdditiesConfig.storage.energizedSmelter)
            .with(AttributeSideConfig.ELECTRIC_MACHINE)
            .withComputerSupport("energizedSmelter")
            .build();


    private static BlockTypeTile<TileEntityMechanicalPipe> createPipe(PipeTier tier, Supplier<TileEntityTypeRegistryObject<TileEntityMechanicalPipe>> tile) {
        return createTransmitter(tier, tile, MGsOdditiesLang.DESCRIPTION_PIPE);
    }

    private static <TILE extends TileEntityTransmitter> BlockTypeTile<TILE> createTransmitter(ITier tier, Supplier<TileEntityTypeRegistryObject<TILE>> tile, ILangEntry description) {
        return BlockTypeTile.BlockTileBuilder.createBlock(tile, description)
                .with(new AttributeTier<>(tier))
                .build();
    }
}