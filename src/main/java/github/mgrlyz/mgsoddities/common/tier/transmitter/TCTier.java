package github.mgrlyz.mgsoddities.common.tier.transmitter;

import github.mgrlyz.mgsoddities.common.config.MGsOdditiesConfig;
import mekanism.common.tier.ConductorTier;

public class TCTier {
    public TCTier() {
    }

    public static double getConduction(ConductorTier tier) {
        return switch (tier) {
            case BASIC, ADVANCED, ELITE -> (double) MGsOdditiesConfig.mgsodditiesTierConfig.paragonThermodynamicConductorConduction.get();
            case ULTIMATE -> (double)MGsOdditiesConfig.mgsodditiesTierConfig.apotheosisThermodynamicConductorConduction.get();
        };
    }

    public static double getHeatCapacity(ConductorTier tier) {
        return switch (tier) {
            case BASIC, ADVANCED, ELITE -> (double)MGsOdditiesConfig.mgsodditiesTierConfig.paragonThermodynamicConductornCapacity.get();
            case ULTIMATE -> (double)MGsOdditiesConfig.mgsodditiesTierConfig.apotheosisThermodynamicConductornCapacity.get();
        };
    }

    public static double getConductionInsulation(ConductorTier tier) {
        return switch (tier) {
            case BASIC, ADVANCED, ELITE -> (double)MGsOdditiesConfig.mgsodditiesTierConfig.paragonThermodynamicConductornInsulation.get();
            case ULTIMATE -> (double)MGsOdditiesConfig.mgsodditiesTierConfig.apotheosisThermodynamicConductornInsulation.get();
        };
    }
}