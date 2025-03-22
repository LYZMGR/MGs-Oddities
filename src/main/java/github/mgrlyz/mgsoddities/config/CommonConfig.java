package github.mgrlyz.mgsoddities.config;

import github.mgrlyz.mgsoddities.config.value.CachedBooleanValue;
import github.mgrlyz.mgsoddities.config.value.CachedEnumValue;
import github.mgrlyz.mgsoddities.util.UnitDisplayUtils;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

public class CommonConfig extends BaseMGsOdditiesConfig {

    private final ModConfigSpec configSpec;

    public final CachedEnumValue<UnitDisplayUtils.EnergyUnit> energyUnit;
    public final CachedEnumValue<UnitDisplayUtils.TemperatureUnit> tempUnit;
    public final CachedBooleanValue enableDecayTimers;
    public final CachedBooleanValue copyBlockData;
    public final CachedBooleanValue holidays;

    CommonConfig() {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        energyUnit = CachedEnumValue.wrap(this, MGsOdditiesConfigTranslations.COMMON_UNIT_ENERGY.applyToBuilder(builder)
                .defineEnum("energyType", UnitDisplayUtils.EnergyUnit.FORGE_ENERGY));
        tempUnit = CachedEnumValue.wrap(this, MGsOdditiesConfigTranslations.COMMON_UNIT_TEMPERATURE.applyToBuilder(builder)
                .defineEnum("temperatureUnit", UnitDisplayUtils.TemperatureUnit.KELVIN));
        enableDecayTimers = CachedBooleanValue.wrap(this, MGsOdditiesConfigTranslations.COMMON_DECAY_TIMERS.applyToBuilder(builder)
                .define("enableDecayTimers", true));
        copyBlockData = CachedBooleanValue.wrap(this, MGsOdditiesConfigTranslations.COMMON_COPY_BLOCK_DATA.applyToBuilder(builder)
                .define("copyBlockData", true));
        holidays = CachedBooleanValue.wrap(this, MGsOdditiesConfigTranslations.COMMON_HOLIDAYS.applyToBuilder(builder)
                .define("holidays", true));

        configSpec = builder.build();
    }

    @Override
    public String getFileName() {
        return "common";
    }

    @Override
    public String getTranslation() {
        return "Common Config";
    }

    @Override
    public ModConfigSpec getConfigSpec() {
        return configSpec;
    }

    @Override
    public ModConfig.Type getConfigType() {
        return ModConfig.Type.COMMON;
    }
}