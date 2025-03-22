package github.mgrlyz.mgsoddities.config;


import github.mgrlyz.mgsoddities.config.value.CachedBooleanValue;
import github.mgrlyz.mgsoddities.config.value.CachedDoubleValue;
import net.neoforged.neoforge.common.ModConfigSpec;

public class GeneralConfig {

    //Energy Conversion
    public final CachedBooleanValue blacklistForge;
    public final CachedDoubleValue forgeConversionRate;
//    public final CachedBooleanValue blacklistFluxNetworks;
//    public final CachedBooleanValue blacklistGrandPower;
//    public final CachedLongValue FROM_H2;
//    public final CachedLongValue maxEnergyPerSteam;
//    public final CachedDoubleValue forgeConversionRate;

    GeneralConfig() {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        blacklistForge = CachedBooleanValue.wrap(this, MGsOdditiesConfigTranslations.GENERAL_ENERGY_CONVERSION_BLACKLIST_FE.applyToBuilder(builder)
                .worldRestart()
                .define("blacklistForge", false));


        forgeConversionRate = CachedDoubleValue.wrap(this, MGsOdditiesConfigTranslations.GENERAL_ENERGY_CONVERSION_FE.applyToBuilder(builder)
                .defineInRange("feConversionRate", 2.5, 0.0001, 10_000));
    }
}
