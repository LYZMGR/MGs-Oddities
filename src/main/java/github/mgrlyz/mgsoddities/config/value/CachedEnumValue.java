package github.mgrlyz.mgsoddities.config.value;

import github.mgrlyz.mgsoddities.config.IMGsOdditiesConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.TranslatableEnum;

public class CachedEnumValue<T extends Enum<T>> extends CachedConfigValue<T> {

    private CachedEnumValue(IMGsOdditiesConfig config, ModConfigSpec.EnumValue<T> internal) {
        super(config, internal);
    }

    public static <T extends Enum<T> & TranslatableEnum> CachedEnumValue<T> wrap(IMGsOdditiesConfig config, ModConfigSpec.EnumValue<T> internal) {
        return new CachedEnumValue<>(config, internal);
    }
}