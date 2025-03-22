package github.mgrlyz.mgsoddities.config.value;

import github.mgrlyz.mgsoddities.config.IMGsOdditiesConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.*;
import java.util.function.Consumer;

public abstract class CachedMapConfigValue<KEY, VALUE> extends CachedResolvableConfigValue<Map<KEY, VALUE>, List<? extends String>> {

    protected CachedMapConfigValue(IMGsOdditiesConfig config, ModConfigSpec.ConfigValue<List<? extends String>> internal) {
        super(config, internal);
    }

    protected abstract void resolve(String encoded, Map<KEY, VALUE> resolved);

    protected abstract void encode(KEY key, VALUE value, Consumer<String> adder);

    @Override
    protected final Map<KEY, VALUE> resolve(List<? extends String> encoded) {
        Map<KEY, VALUE> resolved = new HashMap<>(encoded.size());
        for (String s : encoded) {
            resolve(s, resolved);
        }
        return resolved;
    }

    @Override
    protected final List<? extends String> encode(Map<KEY, VALUE> values) {
        return encodeStatic(values, this::encode);
    }

    protected static <KEY, VALUE> List<? extends String> encodeStatic(Map<KEY, VALUE> values, ValueEncoder<KEY, VALUE> encoder) {
        List<String> encoded = new ArrayList<>(values.size());
        for (Map.Entry<KEY, VALUE> entry : values.entrySet()) {
            encoder.encode(entry.getKey(), entry.getValue(), encoded::add);
        }
        Collections.sort(encoded);
        return encoded;
    }

    protected interface ValueEncoder<KEY, VALUE> {

        void encode(KEY key, VALUE value, Consumer<String> adder);
    }
}