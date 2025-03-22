package github.mgrlyz.mgsoddities.config;

import github.mgrlyz.mgsoddities.config.value.CachedValue;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

public interface IMGsOdditiesConfig {

    String getFileName();

    String getTranslation();

    ModConfigSpec getConfigSpec();

    default boolean isLoaded() {
        return getConfigSpec().isLoaded();
    }

    ModConfig.Type getConfigType();

    void save();

    void clearCache(boolean unloading);

    void addCachedValue(CachedValue<?> configValue);
}