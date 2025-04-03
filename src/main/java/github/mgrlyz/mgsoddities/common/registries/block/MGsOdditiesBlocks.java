package github.mgrlyz.mgsoddities.common.registries.block;

import github.mgrlyz.mgsoddities.MGsOddities;
import github.mgrlyz.mgsoddities.api.tier.IAdvanceTier;
import github.mgrlyz.mgsoddities.common.item.block.transmitter.*;
import github.mgrlyz.mgsoddities.common.tile.transmitter.*;
import mekanism.common.block.interfaces.IHasDescription;
import mekanism.common.block.transmitter.BlockLargeTransmitter;
import mekanism.common.block.transmitter.BlockSmallTransmitter;
import mekanism.common.content.blocktype.BlockTypeTile;
import mekanism.common.item.block.ItemBlockTooltip;
import mekanism.common.registration.impl.BlockDeferredRegister;
import mekanism.common.registration.impl.BlockRegistryObject;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public class MGsOdditiesBlocks {
    public static final BlockDeferredRegister MGSODDITIES_BLOCKS = new BlockDeferredRegister(MGsOddities.MODID);
    
    public static final BlockRegistryObject<Block,BlockItem> STELLAR_MATTER_O;
    public static final BlockRegistryObject<Block,BlockItem> STELLAR_MATTER_F;
    public static final BlockRegistryObject<Block,BlockItem> STELLAR_MATTER_M;
    public static final BlockRegistryObject<Block,BlockItem> DYSON_SPHERE_FRAME;
    public static final BlockRegistryObject<Block,BlockItem> DYSON_SPHERE_SHELL;

    public MGsOdditiesBlocks() {
    }

    private static <BLOCK extends Block, ITEM extends BlockItem> BlockRegistryObject<BLOCK, ITEM> registerTieredBlock(IAdvanceTier tier, String suffix, Function<MapColor, ? extends BLOCK> blockSupplier, BiFunction<BLOCK, Item.Properties, ITEM> itemCreator) {
        return registerTieredBlock(tier, suffix, () -> blockSupplier.apply(tier.getAdvanceTier().getMapColor()), itemCreator);
    }

    private static <BLOCK extends Block, ITEM extends BlockItem> BlockRegistryObject<BLOCK, ITEM> registerTieredBlock(IAdvanceTier tier, String suffix, Supplier<? extends BLOCK> blockSupplier, BiFunction<BLOCK, Item.Properties, ITEM> itemCreator) {
        return MGSODDITIES_BLOCKS.register(tier.getAdvanceTier().getLowerName() + suffix, blockSupplier, itemCreator);
    }

    private static <BLOCK extends Block, ITEM extends BlockItem> BlockRegistryObject<BLOCK, ITEM> registerTieredBlock(String registerName, Supplier<? extends BLOCK> blockSupplier, BiFunction<BLOCK, Item.Properties, ITEM> itemCreator) {
        return MGSODDITIES_BLOCKS.register(registerName, blockSupplier, itemCreator);
    }

    private static <BLOCK extends Block & IHasDescription> BlockRegistryObject<BLOCK, ItemBlockTooltip<BLOCK>> registerBlock(String name, Supplier<? extends BLOCK> blockSupplier) {
        return MGSODDITIES_BLOCKS.register(name, blockSupplier, ItemBlockTooltip::new);
    }

    private static BlockRegistryObject<BlockSmallTransmitter<MGsOdditiesTileEntityUniversalCable>, MGsOdditiesItemBlockUniversalCable> registerUniversalCable(
            String nameTier, BlockTypeTile<MGsOdditiesTileEntityUniversalCable> type) {
        return registerTieredBlock(nameTier + "_universal_cable", () -> new BlockSmallTransmitter<>(type), MGsOdditiesItemBlockUniversalCable::new);
    }

    private static BlockRegistryObject<BlockLargeTransmitter<MGsOdditiesTileEntityMechanicalPipe>, MGsOdditiesItemBlockMechanicalPipe> registerMechanicalPipe(
            String nameTier, BlockTypeTile<MGsOdditiesTileEntityMechanicalPipe> type) {
        return registerTieredBlock(nameTier + "_mechanical_pipe", () -> new BlockLargeTransmitter<>(type), MGsOdditiesItemBlockMechanicalPipe::new);
    }

    private static BlockRegistryObject<BlockSmallTransmitter<MGsOdditiesTileEntityPressurizedTube>, MGsOdditiesItemBlockPressurizedTube> registerPressurizedTube(
            String nameTier, BlockTypeTile<MGsOdditiesTileEntityPressurizedTube> type) {
        return registerTieredBlock(nameTier + "_pressurized_tube", () -> new BlockSmallTransmitter<>(type), MGsOdditiesItemBlockPressurizedTube::new);
    }

    private static BlockRegistryObject<BlockLargeTransmitter<MGsOdditiesTileEntityLogisticalTransporter>, MGsOdditiesItemBlockLogisticalTransporter> registerLogisticalTransporter(
            String nameTier, BlockTypeTile<MGsOdditiesTileEntityLogisticalTransporter> type) {
        return registerTieredBlock(nameTier + "_logistical_transporter", () -> new BlockLargeTransmitter<>(type), MGsOdditiesItemBlockLogisticalTransporter::new);
    }

    private static BlockRegistryObject<BlockSmallTransmitter<MGsOdditiesTileEntityThermodynamicConductor>, MGsOdditiesItemBlockThermodynamicConductor> registerThermodynamicConductor(
            String nameTier, BlockTypeTile<MGsOdditiesTileEntityThermodynamicConductor> type) {
        return registerTieredBlock(nameTier + "_thermodynamic_conductor", () -> new BlockSmallTransmitter<>(type), MGsOdditiesItemBlockThermodynamicConductor::new);
    }

    private static BlockRegistryObject<Block, BlockItem> registerStellarMatter(String nameTier) {
        return MGSODDITIES_BLOCKS.register("stellar_matter_" + nameTier,() -> new Block(BlockBehaviour.Properties.of()
                .strength(4f)
                .requiresCorrectToolForDrops()
                .sound(SoundType.ANCIENT_DEBRIS)
                .lightLevel(state -> 15)));
    }

    public static void register(IEventBus eventBus) {
        MGSODDITIES_BLOCKS.register(eventBus);
    }

    public static final BlockRegistryObject<BlockSmallTransmitter<MGsOdditiesTileEntityUniversalCable>, MGsOdditiesItemBlockUniversalCable> PARAGON_UNIVERSAL_CABLE = registerUniversalCable("paragon", MGsOdditiesBlockTypes.PARAGON_UNIVERSAL_CABLE);
    public static final BlockRegistryObject<BlockSmallTransmitter<MGsOdditiesTileEntityUniversalCable>, MGsOdditiesItemBlockUniversalCable> APOTHEOSIS_UNIVERSAL_CABLE = registerUniversalCable("apotheosis", MGsOdditiesBlockTypes.APOTHEOSIS_UNIVERSAL_CABLE);

    public static final BlockRegistryObject<BlockLargeTransmitter<MGsOdditiesTileEntityMechanicalPipe>, MGsOdditiesItemBlockMechanicalPipe> PARAGON_MECHANICAL_PIPE = registerMechanicalPipe("paragon", MGsOdditiesBlockTypes.PARAGON_MECHANICAL_PIPE);
    public static final BlockRegistryObject<BlockLargeTransmitter<MGsOdditiesTileEntityMechanicalPipe>, MGsOdditiesItemBlockMechanicalPipe> APOTHEOSIS_MECHANICAL_PIPE = registerMechanicalPipe("apotheosis", MGsOdditiesBlockTypes.APOTHEOSIS_MECHANICAL_PIPE);

    public static final BlockRegistryObject<BlockSmallTransmitter<MGsOdditiesTileEntityPressurizedTube>, MGsOdditiesItemBlockPressurizedTube> PARAGON_PRESSURIZED_TUBE = registerPressurizedTube("paragon", MGsOdditiesBlockTypes.PARAGON_PRESSURIZED_TUBE);
    public static final BlockRegistryObject<BlockSmallTransmitter<MGsOdditiesTileEntityPressurizedTube>, MGsOdditiesItemBlockPressurizedTube> APOTHEOSIS_PRESSURIZED_TUBE = registerPressurizedTube("apotheosis", MGsOdditiesBlockTypes.APOTHEOSIS_PRESSURIZED_TUBE);

    public static final BlockRegistryObject<BlockLargeTransmitter<MGsOdditiesTileEntityLogisticalTransporter>, MGsOdditiesItemBlockLogisticalTransporter> PARAGON_LOGISTICAL_TRANSPORTER = registerLogisticalTransporter("paragon", MGsOdditiesBlockTypes.PARAGON_LOGISTICAL_TRANSPORTER);
    public static final BlockRegistryObject<BlockLargeTransmitter<MGsOdditiesTileEntityLogisticalTransporter>, MGsOdditiesItemBlockLogisticalTransporter> APOTHEOSIS_LOGISTICAL_TRANSPORTER = registerLogisticalTransporter("apotheosis", MGsOdditiesBlockTypes.APOTHEOSIS_LOGISTICAL_TRANSPORTER);

    public static final BlockRegistryObject<BlockSmallTransmitter<MGsOdditiesTileEntityThermodynamicConductor>, MGsOdditiesItemBlockThermodynamicConductor> PARAGON_THERMODYNAMIC_CONDUCTOR = registerThermodynamicConductor("paragon", MGsOdditiesBlockTypes.PARAGON_THERMODYNAMIC_CONDUCTOR);
    public static final BlockRegistryObject<BlockSmallTransmitter<MGsOdditiesTileEntityThermodynamicConductor>, MGsOdditiesItemBlockThermodynamicConductor> APOTHEOSIS_THERMODYNAMIC_CONDUCTOR = registerThermodynamicConductor("apotheosis", MGsOdditiesBlockTypes.APOTHEOSIS_THERMODYNAMIC_CONDUCTOR);
    
    static {
        STELLAR_MATTER_O = registerStellarMatter("o");
        STELLAR_MATTER_F = registerStellarMatter("f");
        STELLAR_MATTER_M = registerStellarMatter("m");
        DYSON_SPHERE_SHELL = MGSODDITIES_BLOCKS.register("dyson_sphere_shell",() ->new Block(BlockBehaviour.Properties.of().strength(4f).requiresCorrectToolForDrops().sound(SoundType.METAL)));
        DYSON_SPHERE_FRAME = MGSODDITIES_BLOCKS.register("dyson_sphere_frame",() ->new Block(BlockBehaviour.Properties.of().strength(4f).requiresCorrectToolForDrops().sound(SoundType.METAL)));
    }
}