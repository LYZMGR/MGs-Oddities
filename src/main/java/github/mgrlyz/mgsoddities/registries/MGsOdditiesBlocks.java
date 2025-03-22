package github.mgrlyz.mgsoddities.registries;


import github.mgrlyz.mgsoddities.Item.block.transmitter.ItemBlockMechanicalPipe;
import github.mgrlyz.mgsoddities.block.attribute.AttributeTier;
import github.mgrlyz.mgsoddities.content.blocktype.BlockType;
import github.mgrlyz.mgsoddities.content.blocktype.BlockTypeTile;
import github.mgrlyz.mgsoddities.registration.impl.BlockRegistryObject;
import github.mgrlyz.mgsoddities.tile.transmitter.BlockLargeTransmitter;
import github.mgrlyz.mgsoddities.tile.transmitter.TileEntityMechanicalPipe;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public class MGsOdditiesBlocks {
    public static final BlockRegistryObject<BlockLargeTransmitter<TileEntityMechanicalPipe>, ItemBlockMechanicalPipe> PARAGON_MECHANICAL_PIPE = registerMechanicalPipe(MGsOdditiesBlocksTypes.PARAGON_MECHANICAL_PIPE);
    public static final BlockRegistryObject<BlockLargeTransmitter<TileEntityMechanicalPipe>, ItemBlockMechanicalPipe> APOTHEOSIS_MECHANICAL_PIPE = registerMechanicalPipe(MGsOdditiesBlocksTypes.APOTHEOSIS_MECHANICAL_PIPE);

    private static <BLOCK extends Block, ITEM extends BlockItem> BlockRegistryObject<BLOCK, ITEM> registerTieredBlock(BlockType type, String suffix,
                                                                                                                      Supplier<? extends BLOCK> blockSupplier, BiFunction<BLOCK, Item.Properties, ITEM> itemCreator) {
        return registerTieredBlock(type.get(AttributeTier.class).tier(), suffix, blockSupplier, itemCreator);
    }

    private static BlockRegistryObject<BlockLargeTransmitter<TileEntityMechanicalPipe>, ItemBlockMechanicalPipe> registerMechanicalPipe(
            BlockTypeTile<TileEntityMechanicalPipe> type) {
        return registerTieredBlock(type, "_mechanical_pipe", () -> new BlockLargeTransmitter<>(type), ItemBlockMechanicalPipe::new);
    }
}