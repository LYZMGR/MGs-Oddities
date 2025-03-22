package github.mgrlyz.mgsoddities.config.value;

import github.mgrlyz.mgsoddities.config.IMGsOdditiesConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public abstract class CachedResolvableConfigValue<TYPE, REAL> extends CachedValue<REAL> implements Supplier<TYPE> {

    @Nullable
    private TYPE cachedValue;

    protected CachedResolvableConfigValue(IMGsOdditiesConfig config, ModConfigSpec.ConfigValue<REAL> internal) {
        super(config, internal);
    }

    protected abstract TYPE resolve(REAL encoded);

    protected abstract REAL encode(TYPE value);

    @NotNull
    public TYPE getOrDefault() {
        if (cachedValue != null || isLoaded()) {
            return get();
        }
        return resolve(internal.getDefault());
    }

    @NotNull
    @Override
    public TYPE get() {
        if (cachedValue == null) {
            cachedValue = resolve(internal.get());
        }
        return cachedValue;
    }

    public void set(TYPE value) {
        internal.set(encode(value));
        cachedValue = value;
    }

    @Override
    protected boolean clearCachedValue(boolean checkChanged) {
        if (cachedValue == null) {
            return false;
        }
        TYPE oldCachedValue = cachedValue;
        cachedValue = null;
        return checkChanged && !oldCachedValue.equals(get());
    }
}