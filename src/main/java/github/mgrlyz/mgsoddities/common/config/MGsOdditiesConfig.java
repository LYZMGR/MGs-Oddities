package github.mgrlyz.mgsoddities.common.config;

import mekanism.common.config.IMekanismConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.IConfigSpec;
import net.neoforged.fml.event.config.ModConfigEvent;

import java.util.HashMap;
import java.util.Map;

public class MGsOdditiesConfig {

    private MGsOdditiesConfig() {

    }

    private static final Map<IConfigSpec, IMekanismConfig> KNOWN_CONFIGS = new HashMap<>();
    public static final MGsOdditiesTierConfig mgsodditiesTierConfig = new MGsOdditiesTierConfig();
    public static final MGsOdditiesGeneralConfig mgsodditiesGeneralConfig = new MGsOdditiesGeneralConfig();

    public static void registerConfigs(ModContainer modContainer) {
        MGsOdditiesConfigHelper.registerConfig(KNOWN_CONFIGS, modContainer, mgsodditiesTierConfig);
        MGsOdditiesConfigHelper.registerConfig(KNOWN_CONFIGS, modContainer, mgsodditiesGeneralConfig);
    }

    public static void onConfigLoad(ModConfigEvent configEvent) {
        MGsOdditiesConfigHelper.onConfigLoad(configEvent, "mgsoddities", KNOWN_CONFIGS);
    }
}
