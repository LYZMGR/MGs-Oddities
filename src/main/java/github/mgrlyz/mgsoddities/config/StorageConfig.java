package github.mgrlyz.mgsoddities.config;

import github.mgrlyz.mgsoddities.config.value.CachedLongValue;
import net.neoforged.neoforge.common.ModConfigSpec;

public class StorageConfig {

    public final CachedLongValue energizedSmelter;

    StorageConfig() {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        energizedSmelter = CachedLongValue.definedMin(this, builder, MGsOdditiesConfigTranslations.ENERGY_STORAGE_SMELTER, "energizedSmelter",
                20_000L, 1);
    }
}