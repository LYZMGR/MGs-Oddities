package github.mgrlyz.mgsoddities.common.config;

import mekanism.common.config.IMekanismConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.IConfigSpec;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.fml.loading.FMLPaths;

import java.nio.file.Path;
import java.util.Map;

public class MGsOdditiesConfigHelper {
    public static final Path CONFIG_DIR;

    private MGsOdditiesConfigHelper() {
    }

    public static void registerConfig(Map<IConfigSpec, IMekanismConfig> knownConfigs, ModContainer modContainer, IMekanismConfig config) {
        modContainer.registerConfig(config.getConfigType(), config.getConfigSpec(), "Mekanism-Extras/" + config.getFileName() + ".toml");
        knownConfigs.put(config.getConfigSpec(), config);
    }

    public static void onConfigLoad(ModConfigEvent event, String modid, Map<IConfigSpec, IMekanismConfig> knownConfigs) {
        ModConfig config = event.getConfig();
        if (config.getModId().equals(modid)) {
            IMekanismConfig mekanismConfig = (IMekanismConfig)knownConfigs.get(config.getSpec());
            if (mekanismConfig != null) {
                mekanismConfig.clearCache(event instanceof ModConfigEvent.Unloading);
            }
        }

    }

    static {
        CONFIG_DIR = FMLPaths.getOrCreateGameRelativePath(FMLPaths.CONFIGDIR.get().resolve("Mekanism-Extras"));
    }
}