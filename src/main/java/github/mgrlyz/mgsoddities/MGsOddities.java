package github.mgrlyz.mgsoddities;

import github.mgrlyz.mgsoddities.common.capabilities.MGsOdditiesCapabilities;
import github.mgrlyz.mgsoddities.common.config.MGsOdditiesConfig;
import github.mgrlyz.mgsoddities.common.registries.MGsOdditiesCreativeTabs;
import github.mgrlyz.mgsoddities.common.registries.block.MGsOdditiesBlocks;
import github.mgrlyz.mgsoddities.common.registries.item.MGsOdditiesItems;


import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

@Mod(MGsOddities.MODID)
public class MGsOddities {
    public static final String MODID = "mgsoddities";

    public MGsOddities(ModContainer modContainer, IEventBus modEventBus)
    {
        NeoForge.EVENT_BUS.register(this);
        modEventBus.addListener(this::commonSetup);

        MGsOdditiesConfig.registerConfigs(modContainer);

        MGsOdditiesItems.register(modEventBus);
        MGsOdditiesBlocks.register(modEventBus);
        MGsOdditiesCreativeTabs.register(modEventBus);

        modEventBus.addListener(this::addCreative);
        modEventBus.addListener(MGsOdditiesConfig::onConfigLoad);
        modEventBus.addListener(MGsOdditiesCapabilities::registerCapabilities);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
    }

    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
        }
    }

    public static ResourceLocation rl(String path) {
        return ResourceLocation.fromNamespaceAndPath(MGsOddities.MODID, path);
    }
}
