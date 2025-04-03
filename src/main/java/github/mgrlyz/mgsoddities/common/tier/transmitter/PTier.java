package github.mgrlyz.mgsoddities.common.tier.transmitter;

import github.mgrlyz.mgsoddities.common.config.MGsOdditiesConfig;
import mekanism.common.tier.PipeTier;

public class PTier {

    public static int getPipePullAmount(PipeTier tier) {
        return switch (tier) {
            case BASIC, ADVANCED, ULTIMATE -> (int)MGsOdditiesConfig.mgsodditiesTierConfig.paragonMechanicalPipePullAmount.get();
            case ELITE -> (int)MGsOdditiesConfig.mgsodditiesTierConfig.apotheosisMechanicalPipePullAmount.get();
        };
    }

    public static long getPipeCapacity(PipeTier tier) {
        return switch (tier) {
            case BASIC, ADVANCED, ULTIMATE -> MGsOdditiesConfig.mgsodditiesTierConfig.paragonMechanicalPipeCapacity.get();
            case ELITE -> MGsOdditiesConfig.mgsodditiesTierConfig.apotheosisMechanicalPipeCapacity.get();
        };
    }
}