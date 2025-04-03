package github.mgrlyz.mgsoddities.common.tier.transmitter;

import github.mgrlyz.mgsoddities.common.config.MGsOdditiesConfig;
import mekanism.common.tier.TubeTier;

public class TTier {
    public static long getTubePullAmount(TubeTier tier) {
        return switch (tier) {
            case BASIC, ADVANCED, ULTIMATE -> MGsOdditiesConfig.mgsodditiesTierConfig.paragonPressurizedTubePullAmount.get();
            case ELITE -> MGsOdditiesConfig.mgsodditiesTierConfig.apotheosisPressurizedTubePullAmount.get();
        };
    }

    public static long getTubeCapacity(TubeTier tier) {
        return switch (tier) {
            case BASIC, ADVANCED, ULTIMATE -> MGsOdditiesConfig.mgsodditiesTierConfig.paragonPressurizedTubeCapacity.get();
            case ELITE -> MGsOdditiesConfig.mgsodditiesTierConfig.apotheosisPressurizedTubeCapacity.get();
        };
    }
}
