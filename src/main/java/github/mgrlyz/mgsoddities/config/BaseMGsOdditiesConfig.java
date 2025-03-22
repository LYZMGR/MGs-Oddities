package github.mgrlyz.mgsoddities.config;

import github.mgrlyz.mgsoddities.MGsOddities;
import github.mgrlyz.mgsoddities.config.value.CachedValue;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class BaseMGsOdditiesConfig implements IMGsOdditiesConfig {

    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();

    private final List<CachedValue<?>> cachedConfigValues = new ArrayList<>();

    @Override
    public void clearCache(boolean unloading) {
        for (CachedValue<?> cachedConfigValue : cachedConfigValues) {
            cachedConfigValue.clearCache(unloading);
        }
    }

    @Override
    public void addCachedValue(CachedValue<?> configValue) {
        cachedConfigValues.add(configValue);
    }

    @Override
    public void save() {
        EXECUTOR.submit(new BaseMGsOdditiesConfig.ConfigSaver(getConfigSpec()));
    }

    private static class ConfigSaver implements Runnable {

        private final ModConfigSpec configSpec;
        private int retries = 0;

        private ConfigSaver(ModConfigSpec configSpec) {
            this.configSpec = configSpec;
        }

        @Override
        public void run() {
            try {
                configSpec.save();
            } catch (Exception e) {
                MGsOddities.logger.error("Failed to save config", e);
                if (retries++ < 3) {
                    EXECUTOR.submit(this);
                } else {
                    MGsOddities.logger.error("Giving up");
                }
            }
        }
    }
}