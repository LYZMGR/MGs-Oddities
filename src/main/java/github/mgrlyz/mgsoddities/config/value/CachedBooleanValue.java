package github.mgrlyz.mgsoddities.config.value;

import github.mgrlyz.mgsoddities.config.IMGsOdditiesConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.function.BooleanSupplier;

public class CachedBooleanValue extends CachedValue<Boolean> implements BooleanSupplier {

    private boolean resolved;
    private boolean cachedValue;

    private CachedBooleanValue(IMGsOdditiesConfig config, ModConfigSpec.ConfigValue<Boolean> internal) {
        super(config, internal);
    }

    public static CachedBooleanValue wrap(IMGsOdditiesConfig config, ModConfigSpec.ConfigValue<Boolean> internal) {
        return new CachedBooleanValue(config, internal);
    }

    public boolean getOrDefault() {
        if (resolved || isLoaded()) {
            return get();
        }
        return internal.getDefault();
    }

    public boolean get() {
        if (!resolved) {
            cachedValue = internal.get();
            resolved = true;
        }
        return cachedValue;
    }

    @Override
    public boolean getAsBoolean() {
        return get();
    }

    public void set(boolean value) {
        internal.set(value);
        cachedValue = value;
    }

    @Override
    protected boolean clearCachedValue(boolean checkChanged) {
        if (!resolved) {
            return false;
        }
        boolean oldCachedValue = cachedValue;
        resolved = false;
        return checkChanged && oldCachedValue != get();
    }
}