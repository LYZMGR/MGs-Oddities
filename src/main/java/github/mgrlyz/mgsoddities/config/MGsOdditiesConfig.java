package github.mgrlyz.mgsoddities.config;

import github.mgrlyz.mgsoddities.MGsOddities;
import net.neoforged.fml.config.IConfigSpec;
import net.neoforged.fml.event.config.ModConfigEvent;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MGsOdditiesConfig {

    private MGsOdditiesConfig() {
    }
    private static final Map<IConfigSpec, IMGsOdditiesConfig> KNOWN_CONFIGS = new HashMap<>();
//    public static final ClientConfig client = new ClientConfig();
    public static final CommonConfig common = new CommonConfig();
    public static final GeneralConfig general = new GeneralConfig();
//    public static final GearConfig gear = new GearConfig();
//    public static final MekanismStartupConfig startup = new MekanismStartupConfig();
    public static final StorageConfig storage = new StorageConfig();
//    public static final TierConfig tiers = new TierConfig();
    public static final UsageConfig usage = new UsageConfig();
//    public static final WorldConfig world = new WorldConfig();
//
//    public static void registerConfigs(ModContainer modContainer) {
//        MGsOdditiesConfigHelper.registerConfig(KNOWN_CONFIGS, modContainer, client);
//        MGsOdditiesConfigHelper.registerConfig(KNOWN_CONFIGS, modContainer, common);
//        MGsOdditiesConfigHelper.registerConfig(KNOWN_CONFIGS, modContainer, general);
//        MGsOdditiesConfigHelper.registerConfig(KNOWN_CONFIGS, modContainer, gear);
//        MGsOdditiesConfigHelper.registerConfig(KNOWN_CONFIGS, modContainer, startup);
//        MGsOdditiesConfigHelper.registerConfig(KNOWN_CONFIGS, modContainer, storage);
//        MGsOdditiesConfigHelper.registerConfig(KNOWN_CONFIGS, modContainer, tiers);
//        MGsOdditiesConfigHelper.registerConfig(KNOWN_CONFIGS, modContainer, usage);
//        MGsOdditiesConfigHelper.registerConfig(KNOWN_CONFIGS, modContainer, world);
//    }

    public static void onConfigLoad(ModConfigEvent configEvent) {
        MGsOdditiesConfigHelper.onConfigLoad(configEvent, MGsOddities.MODID, KNOWN_CONFIGS);
    }

    public static Collection<IMGsOdditiesConfig> getConfigs() {
        return Collections.unmodifiableCollection(KNOWN_CONFIGS.values());
    }
}