package github.mgrlyz.mgsoddities.common.tier.transmitter;

import github.mgrlyz.mgsoddities.common.config.MGsOdditiesConfig;
import mekanism.common.tier.ConductorTier;

public class TCTier {

    public static double getConduction(ConductorTier tier) {
        return switch (tier) {
            case BASIC, ADVANCED, ULTIMATE -> (double) MGsOdditiesConfig.mgsodditiesTierConfig.paragonThermodynamicConductorConduction.get();
            case ELITE -> (double)MGsOdditiesConfig.mgsodditiesTierConfig.apotheosisThermodynamicConductorConduction.get();
        };
    }

    public static double getHeatCapacity(ConductorTier tier) {
        return switch (tier) {
            case BASIC, ADVANCED, ULTIMATE -> (double)MGsOdditiesConfig.mgsodditiesTierConfig.paragonThermodynamicConductornCapacity.get();
            case ELITE -> (double)MGsOdditiesConfig.mgsodditiesTierConfig.apotheosisThermodynamicConductornCapacity.get();
        };
    }

    public static double getConductionInsulation(ConductorTier tier) {
        return switch (tier) {
            case BASIC, ADVANCED, ULTIMATE -> (double)MGsOdditiesConfig.mgsodditiesTierConfig.paragonThermodynamicConductornInsulation.get();
            case ELITE -> (double)MGsOdditiesConfig.mgsodditiesTierConfig.apotheosisThermodynamicConductornInsulation.get();
        };
    }
}