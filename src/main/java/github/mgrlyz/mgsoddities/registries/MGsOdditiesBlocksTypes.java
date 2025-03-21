package github.mgrlyz.mgsoddities.registries;

import github.mgrlyz.mgsoddities.MGsOdditiesLang;
import github.mgrlyz.mgsoddities.api.tier.ITier;
import github.mgrlyz.mgsoddities.block.attribute.AttributeTier;
import github.mgrlyz.mgsoddities.content.blocktype.BlockTypeTile;
import github.mgrlyz.mgsoddities.registration.impl.TileEntityTypeRegistryObject;
import github.mgrlyz.mgsoddities.tier.PipeTier;
import github.mgrlyz.mgsoddities.tile.transmitter.TileEntityMechanicalPipe;
import mekanism.api.text.ILangEntry;
import mekanism.common.tile.transmitter.TileEntityTransmitter;

import java.util.function.Supplier;

public class MGsOdditiesBlocksTypes {
    public static final BlockTypeTile<TileEntityMechanicalPipe> PARAGON_MECHANICAL_PIPE = createPipe(PipeTier.PARAGON, () -> MGsOdditiesBlocksTypes.PARAGON_MECHANICAL_PIPE);

    private static BlockTypeTile<TileEntityMechanicalPipe> createPipe(PipeTier tier, Supplier<TileEntityTypeRegistryObject<TileEntityMechanicalPipe>> tile) {
        return createTransmitter(tier, tile, MGsOdditiesLang.DESCRIPTION_PIPE);
    }

    private static <TILE extends TileEntityTransmitter> BlockTypeTile<TILE> createTransmitter(ITier tier, Supplier<TileEntityTypeRegistryObject<TILE>> tile, ILangEntry description) {
        return BlockTypeTile.BlockTileBuilder.createBlock(tile, description)
                .with(new AttributeTier<>(tier))
                .build();
    }
}