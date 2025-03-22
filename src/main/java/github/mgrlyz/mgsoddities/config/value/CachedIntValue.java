package github.mgrlyz.mgsoddities.config.value;

import github.mgrlyz.mgsoddities.config.IMGsOdditiesConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.function.IntSupplier;
import java.util.function.LongSupplier;

public class CachedIntValue extends CachedValue<Integer> implements IntSupplier, LongSupplier {

    private boolean resolved;
    private int cachedValue;

    private CachedIntValue(IMGsOdditiesConfig config, ModConfigSpec.ConfigValue<Integer> internal) {
        super(config, internal);
    }

    public static CachedIntValue wrap(IMGsOdditiesConfig config, ModConfigSpec.ConfigValue<Integer> internal) {
        return new CachedIntValue(config, internal);
    }

    public int getOrDefault() {
        if (resolved || isLoaded()) {
            return get();
        }
        return internal.getDefault();
    }

    public int get() {
        if (!resolved) {
            cachedValue = internal.get();
            resolved = true;
        }
        return cachedValue;
    }

    @Override
    public int getAsInt() {
        return get();
    }

    @Override
    public long getAsLong() {
        return get();
    }

    public void set(int value) {
        internal.set(value);
        cachedValue = value;
    }

    @Override
    protected boolean clearCachedValue(boolean checkChanged) {
        if (!resolved) {
            return false;
        }
        int oldCachedValue = cachedValue;
        resolved = false;
        return checkChanged && oldCachedValue != get();
    }
}