package github.mgrlyz.mgsoddities.config.value;

import github.mgrlyz.mgsoddities.config.IMGsOdditiesConfig;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class CachedOredictionificatorConfigValue extends CachedMapConfigValue<String, List<String>> {

    private CachedOredictionificatorConfigValue(IMGsOdditiesConfig config, ModConfigSpec.ConfigValue<List<? extends String>> internal) {
        super(config, internal);
    }

    public static CachedOredictionificatorConfigValue define(IMGsOdditiesConfig config, ModConfigSpec.Builder builder, String path,
                                                                                          Supplier<Map<String, List<String>>> defaults) {
        return new CachedOredictionificatorConfigValue(config, builder.defineListAllowEmpty(path,
                () -> encodeStatic(defaults.get(), CachedOredictionificatorConfigValue::encodeStatic),
                () -> "c:ingots/",
                o -> o instanceof String string && ResourceLocation.tryParse(string.toLowerCase(Locale.ROOT)) != null));
    }

    @Override
    protected void resolve(String encoded, Map<String, List<String>> resolved) {
        ResourceLocation rl = ResourceLocation.tryParse(encoded.toLowerCase(Locale.ROOT));
        if (rl != null) {
            resolved.computeIfAbsent(rl.getNamespace(), r -> new ArrayList<>()).add(rl.getPath());
        }
    }

    @Override
    protected void encode(String key, List<String> values, Consumer<String> adder) {
        encodeStatic(key, values, adder);
    }

    private static void encodeStatic(String key, List<String> values, Consumer<String> adder) {
        String namespace = key.toLowerCase(Locale.ROOT);
        for (String path : values) {
            ResourceLocation rl = ResourceLocation.tryBuild(namespace, path.toLowerCase(Locale.ROOT));
            if (rl != null) {
                adder.accept(rl.toString());
            }
        }
    }
}