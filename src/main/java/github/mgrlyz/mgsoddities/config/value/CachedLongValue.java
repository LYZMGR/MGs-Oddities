package github.mgrlyz.mgsoddities.config.value;

import github.mgrlyz.mgsoddities.config.IConfigTranslation;
import github.mgrlyz.mgsoddities.config.IMGsOdditiesConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.function.LongSupplier;

public class CachedLongValue extends CachedValue<Long> implements LongSupplier {

    private boolean resolved;
    private long cachedValue;

    private CachedLongValue(IMGsOdditiesConfig config, ModConfigSpec.ConfigValue<Long> internal) {
        super(config, internal);
    }

    public static CachedLongValue wrap(IMGsOdditiesConfig config, ModConfigSpec.ConfigValue<Long> internal) {
        return new CachedLongValue(config, internal);
    }

    public static CachedLongValue definePositive(IMGsOdditiesConfig config, ModConfigSpec.Builder builder, IConfigTranslation comment, String path, long defaultValue) {
        return define(config, builder, comment, path, defaultValue, 0, Long.MAX_VALUE);
    }

    public static CachedLongValue definedMin(IMGsOdditiesConfig config, ModConfigSpec.Builder builder, IConfigTranslation comment, String path, long defaultValue, long min) {
        return define(config, builder, comment, path, defaultValue, min, Long.MAX_VALUE);
    }

    public static CachedLongValue define(IMGsOdditiesConfig config, ModConfigSpec.Builder builder, IConfigTranslation comment, String path, long defaultValue, long min, long max) {
        return CachedLongValue.wrap(config, comment.applyToBuilder(builder).defineInRange(path, defaultValue, min, max));
    }

    public long getOrDefault() {
        if (resolved || isLoaded()) {
            return get();
        }
        return internal.getDefault();
    }

    public long get() {
        if (!resolved) {
            cachedValue = internal.get();
            resolved = true;
        }
        return cachedValue;
    }

    @Override
    public long getAsLong() {
        return get();
    }

    public void set(long value) {
        internal.set(value);
        cachedValue = value;
    }

    @Override
    protected boolean clearCachedValue(boolean checkChanged) {
        if (!resolved) {
            return false;
        }
        long oldCachedValue = cachedValue;
        resolved = false;
        return checkChanged && oldCachedValue != get();
    }
}