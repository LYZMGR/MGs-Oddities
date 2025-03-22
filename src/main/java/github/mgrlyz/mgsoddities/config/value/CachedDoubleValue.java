package github.mgrlyz.mgsoddities.config.value;

import github.mgrlyz.mgsoddities.config.IMGsOdditiesConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.function.DoubleSupplier;

public class CachedDoubleValue extends CachedValue<Double> implements DoubleSupplier {

    private boolean resolved;
    private double cachedValue;

    private CachedDoubleValue(IMGsOdditiesConfig config, ModConfigSpec.ConfigValue<Double> internal) {
        super(config, internal);
    }

    public static CachedDoubleValue wrap(IMGsOdditiesConfig config, ModConfigSpec.ConfigValue<Double> internal) {
        return new CachedDoubleValue(config, internal);
    }

    public double getOrDefault() {
        if (resolved || isLoaded()) {
            return get();
        }
        return internal.getDefault();
    }

    public double get() {
        if (!resolved) {
            cachedValue = internal.get();
            resolved = true;
        }
        return cachedValue;
    }

    @Override
    public double getAsDouble() {
        return get();
    }

    public void set(double value) {
        internal.set(value);
        cachedValue = value;
    }

    @Override
    protected boolean clearCachedValue(boolean checkChanged) {
        if (!resolved) {
            return false;
        }
        double oldCachedValue = cachedValue;
        resolved = false;
        return checkChanged && oldCachedValue != get();
    }
}