package github.mgrlyz.mgsoddities.config.value;

import github.mgrlyz.mgsoddities.config.IMGsOdditiesConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

public class CachedConfigValue<T> extends CachedResolvableConfigValue<T, T> {

    protected CachedConfigValue(IMGsOdditiesConfig config, ModConfigSpec.ConfigValue<T> internal) {
        super(config, internal);
    }

    public static <T> CachedConfigValue<T> wrap(IMGsOdditiesConfig config, ModConfigSpec.ConfigValue<T> internal) {
        return new CachedConfigValue<>(config, internal);
    }

    @Override
    protected T resolve(T encoded) {
        return encoded;
    }

    @Override
    protected T encode(T value) {
        return value;
    }
}