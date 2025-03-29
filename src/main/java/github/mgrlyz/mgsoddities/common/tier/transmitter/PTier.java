package github.mgrlyz.mgsoddities.common.tier.transmitter;

import github.mgrlyz.mgsoddities.common.config.MGsOdditiesConfig;
import mekanism.common.tier.PipeTier;

public class PTier {
    public PTier() {
    }

    public static int getPipePullAmount(PipeTier tier) {
        return switch (tier) {
            case BASIC, ADVANCED, ELITE -> (int)MGsOdditiesConfig.mgsodditiesTierConfig.paragonMechanicalPipePullAmount.get();
            case ULTIMATE -> (int)MGsOdditiesConfig.mgsodditiesTierConfig.apotheosisMechanicalPipePullAmount.get();
        };
    }

    public static long getPipeCapacity(PipeTier tier) {
        return switch (tier) {
            case BASIC, ADVANCED, ELITE -> MGsOdditiesConfig.mgsodditiesTierConfig.paragonMechanicalPipeCapacity.get();
            case ULTIMATE -> MGsOdditiesConfig.mgsodditiesTierConfig.apotheosisMechanicalPipeCapacity.get();
        };
    }
}