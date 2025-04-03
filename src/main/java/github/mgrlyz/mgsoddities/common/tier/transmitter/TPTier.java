package github.mgrlyz.mgsoddities.common.tier.transmitter;

import github.mgrlyz.mgsoddities.common.config.MGsOdditiesConfig;
import mekanism.common.tier.TransporterTier;

public class TPTier {

    public static int getSpeed(TransporterTier tier) {
        return switch (tier) {
            case BASIC, ADVANCED, ULTIMATE -> (int) MGsOdditiesConfig.mgsodditiesTierConfig.paragonLogisticalTransporterSpeed.get();
            case ELITE -> (int) MGsOdditiesConfig.mgsodditiesTierConfig.apotheosisLogisticalTransporterSpeed.get();
        };
    }
    public static int getPullAmount(TransporterTier tier) {
        return switch (tier) {
            case BASIC, ADVANCED, ULTIMATE -> (int) MGsOdditiesConfig.mgsodditiesTierConfig.paragonLogisticalTransporterPullAmount.get();
            case ELITE -> (int) MGsOdditiesConfig.mgsodditiesTierConfig.apotheosisLogisticalTransporterPullAmount.get();
        };
    }
}