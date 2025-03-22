package github.mgrlyz.mgsoddities.config;

import github.mgrlyz.mgsoddities.MGsOddities;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.IConfigSpec;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.fml.loading.FMLPaths;

import java.nio.file.Path;
import java.util.Map;

public class MGsOdditiesConfigHelper {

    private MGsOdditiesConfigHelper() {
    }

    public static final Path CONFIG_DIR = FMLPaths.getOrCreateGameRelativePath(FMLPaths.CONFIGDIR.get().resolve(MGsOddities.MOD_NAME));

    public static void registerConfig(Map<IConfigSpec, IMGsOdditiesConfig> knownConfigs, ModContainer modContainer, IMGsOdditiesConfig config) {
        modContainer.registerConfig(config.getConfigType(), config.getConfigSpec(), MGsOddities.MOD_NAME + "/" + config.getFileName() + ".toml");
        knownConfigs.put(config.getConfigSpec(), config);
    }

    public static void onConfigLoad(ModConfigEvent event, String modid, Map<IConfigSpec, IMGsOdditiesConfig> knownConfigs) {
        ModConfig config = event.getConfig();
        if (config.getModId().equals(modid)) {
            IMGsOdditiesConfig MGsOdditiesConfig = knownConfigs.get(config.getSpec());
            if (MGsOdditiesConfig != null) {
                MGsOdditiesConfig.clearCache(event instanceof ModConfigEvent.Unloading);
            }
        }
    }
}