package github.mgrlyz.mgsoddities.config.value;

import github.mgrlyz.mgsoddities.config.IMGsOdditiesConfig;
import mekanism.api.functions.FloatSupplier;
import net.neoforged.neoforge.common.ModConfigSpec;

public class CachedFloatValue extends CachedValue<Double> implements FloatSupplier {

    private boolean resolved;
    private float cachedValue;

    private CachedFloatValue(IMGsOdditiesConfig config, ModConfigSpec.ConfigValue<Double> internal) {
        super(config, internal);
    }

    public static CachedFloatValue wrap(IMGsOdditiesConfig config, ModConfigSpec.ConfigValue<Double> internal) {
        return new CachedFloatValue(config, internal);
    }

    public float getOrDefault() {
        if (resolved || isLoaded()) {
            return get();
        }
        return clampInternal(internal.getDefault());
    }

    public float get() {
        if (!resolved) {
            cachedValue = clampInternal(internal.get());
            resolved = true;
        }
        return cachedValue;
    }

    private float clampInternal(Double val) {
        if (val == null) {
            return 0;
        } else if (val > Float.MAX_VALUE) {
            return Float.MAX_VALUE;
        } else if (val < -Float.MAX_VALUE) {
            return -Float.MAX_VALUE;
        }
        return val.floatValue();
    }

    @Override
    public float getAsFloat() {
        return get();
    }

    public void set(float value) {
        internal.set((double) value);
        cachedValue = value;
    }

    public void set(double value) {
        internal.set(value);
        cachedValue = (float) value;
    }

    @Override
    protected boolean clearCachedValue(boolean checkChanged) {
        if (!resolved) {
            return false;
        }
        float oldCachedValue = cachedValue;
        resolved = false;
        return checkChanged && oldCachedValue != get();
    }
}