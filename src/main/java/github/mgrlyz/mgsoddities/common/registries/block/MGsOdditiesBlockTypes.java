package github.mgrlyz.mgsoddities.common.registries.block;

import github.mgrlyz.mgsoddities.common.registries.MGsOdditiesTileEntityTypes;
import github.mgrlyz.mgsoddities.common.tile.transmitter.*;
import mekanism.api.text.ILangEntry;
import mekanism.api.tier.ITier;
import mekanism.common.MekanismLang;
import mekanism.common.block.attribute.*;
import mekanism.common.content.blocktype.BlockTypeTile;
import mekanism.common.registration.impl.TileEntityTypeRegistryObject;
import mekanism.common.tier.*;

import java.util.function.Supplier;

public class MGsOdditiesBlockTypes {
    public static final BlockTypeTile<MGsOdditiesTileEntityUniversalCable> PARAGON_UNIVERSAL_CABLE;
    public static final BlockTypeTile<MGsOdditiesTileEntityUniversalCable> APOTHEOSIS_UNIVERSAL_CABLE;
    public static final BlockTypeTile<MGsOdditiesTileEntityMechanicalPipe> PARAGON_MECHANICAL_PIPE;
    public static final BlockTypeTile<MGsOdditiesTileEntityMechanicalPipe> APOTHEOSIS_MECHANICAL_PIPE;
    public static final BlockTypeTile<MGsOdditiesTileEntityPressurizedTube> PARAGON_PRESSURIZED_TUBE;
    public static final BlockTypeTile<MGsOdditiesTileEntityPressurizedTube> APOTHEOSIS_PRESSURIZED_TUBE;
    public static final BlockTypeTile<MGsOdditiesTileEntityLogisticalTransporter> PARAGON_LOGISTICAL_TRANSPORTER;
    public static final BlockTypeTile<MGsOdditiesTileEntityLogisticalTransporter> APOTHEOSIS_LOGISTICAL_TRANSPORTER;
    public static final BlockTypeTile<MGsOdditiesTileEntityThermodynamicConductor> PARAGON_THERMODYNAMIC_CONDUCTOR;
    public static final BlockTypeTile<MGsOdditiesTileEntityThermodynamicConductor> APOTHEOSIS_THERMODYNAMIC_CONDUCTOR;

    public MGsOdditiesBlockTypes() {
    }

    private static BlockTypeTile<MGsOdditiesTileEntityUniversalCable> createCable(CableTier tier, Supplier<TileEntityTypeRegistryObject<MGsOdditiesTileEntityUniversalCable>> tile) {
        return createTransmitter(tier, tile, MekanismLang.DESCRIPTION_CABLE);
    }

    private static BlockTypeTile<MGsOdditiesTileEntityMechanicalPipe> createPipe(PipeTier tier, Supplier<TileEntityTypeRegistryObject<MGsOdditiesTileEntityMechanicalPipe>> tile) {
        return createTransmitter(tier, tile, MekanismLang.DESCRIPTION_PIPE);
    }

    private static BlockTypeTile<MGsOdditiesTileEntityPressurizedTube> createTube(TubeTier
                                                                                          tier, Supplier<TileEntityTypeRegistryObject<MGsOdditiesTileEntityPressurizedTube>> tile) {
        return createTransmitter(tier, tile, MekanismLang.DESCRIPTION_TUBE);
    }

    private static BlockTypeTile<MGsOdditiesTileEntityLogisticalTransporter> createTransporter(TransporterTier
                                                                                                       tier, Supplier<TileEntityTypeRegistryObject<MGsOdditiesTileEntityLogisticalTransporter>> tile) {
        return createTransmitter(tier, tile, MekanismLang.DESCRIPTION_TRANSPORTER);
    }

    private static BlockTypeTile<MGsOdditiesTileEntityThermodynamicConductor> createConductor(ConductorTier
                                                                                                      tier, Supplier<TileEntityTypeRegistryObject<MGsOdditiesTileEntityThermodynamicConductor>> tile) {
        return createTransmitter(tier, tile, MekanismLang.DESCRIPTION_CONDUCTOR);
    }

    private static <TILE extends MGsOdditiesTileEntityTransmitter> BlockTypeTile<TILE> createTransmitter(ITier tier, Supplier<TileEntityTypeRegistryObject<TILE>> tile, ILangEntry description) {
        return (BlockTypeTile)((BlockTypeTile.BlockTileBuilder) BlockTypeTile.BlockTileBuilder.createBlock(tile, description).with(new Attribute[]{new AttributeTier(tier)})).build();
    }

    static {
        PARAGON_UNIVERSAL_CABLE = createCable(CableTier.ADVANCED, () -> MGsOdditiesTileEntityTypes.PARAGON_UNIVERSAL_CABLE);
        APOTHEOSIS_UNIVERSAL_CABLE = createCable(CableTier.ELITE, () -> MGsOdditiesTileEntityTypes.APOTHEOSIS_UNIVERSAL_CABLE);
        PARAGON_MECHANICAL_PIPE = createPipe(PipeTier.ADVANCED, () -> MGsOdditiesTileEntityTypes.PARAGON_MECHANICAL_PIPE);
        APOTHEOSIS_MECHANICAL_PIPE = createPipe(PipeTier.ELITE, () -> MGsOdditiesTileEntityTypes.APOTHEOSIS_MECHANICAL_PIPE);
        PARAGON_PRESSURIZED_TUBE = createTube(TubeTier.ADVANCED, () -> MGsOdditiesTileEntityTypes.PARAGON_PRESSURIZED_TUBE);
        APOTHEOSIS_PRESSURIZED_TUBE = createTube(TubeTier.ELITE, () -> MGsOdditiesTileEntityTypes.APOTHEOSIS_PRESSURIZED_TUBE);
        PARAGON_LOGISTICAL_TRANSPORTER = createTransporter(TransporterTier.ADVANCED, () -> MGsOdditiesTileEntityTypes.PARAGON_LOGISTICAL_TRANSPORTER);
        APOTHEOSIS_LOGISTICAL_TRANSPORTER = createTransporter(TransporterTier.ELITE, () -> MGsOdditiesTileEntityTypes.APOTHEOSIS_LOGISTICAL_TRANSPORTER);
        PARAGON_THERMODYNAMIC_CONDUCTOR = createConductor(ConductorTier.ADVANCED, () -> MGsOdditiesTileEntityTypes.PARAGON_THERMODYNAMIC_CONDUCTOR);
        APOTHEOSIS_THERMODYNAMIC_CONDUCTOR = createConductor(ConductorTier.ELITE, () -> MGsOdditiesTileEntityTypes.APOTHEOSIS_THERMODYNAMIC_CONDUCTOR);
    }
}    