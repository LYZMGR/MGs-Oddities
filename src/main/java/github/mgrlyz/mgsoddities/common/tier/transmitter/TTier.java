package github.mgrlyz.mgsoddities.common.tier.transmitter;

import github.mgrlyz.mgsoddities.common.config.MGsOdditiesConfig;
import mekanism.common.tier.TubeTier;

public class TTier {
    public static long getTubePullAmount(TubeTier tier) {
        return switch (tier) {
            case BASIC, ADVANCED, ELITE -> MGsOdditiesConfig.mgsodditiesTierConfig.paragonPressurizedTubePullAmount.get();
            case ULTIMATE -> MGsOdditiesConfig.mgsodditiesTierConfig.apotheosisPressurizedTubePullAmount.get();
        };
    }

    public static long getTubeCapacity(TubeTier tier) {
        long var10000;
        return switch (tier) {
            case BASIC, ADVANCED, ELITE -> MGsOdditiesConfig.mgsodditiesTierConfig.paragonPressurizedTubeCapacity.get();
            case ULTIMATE -> MGsOdditiesConfig.mgsodditiesTierConfig.apotheosisPressurizedTubeCapacity.get();
        };
    }
}
