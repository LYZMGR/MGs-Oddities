package github.mgrlyz.mgsoddities;

import github.mgrlyz.mgsoddities.registries.MGsOdditiesCreativeTabs;
import github.mgrlyz.mgsoddities.registries.MGsOdditiesItems;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;


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
    private static final Logger OGGER = LogUtils.getLogger();
    public static final Logger logger = LogUtils.getLogger();
    public static final String MOD_NAME = "MGsOddities";

    public MGsOddities(ModContainer modContainer, IEventBus modEventBus)
    {
        modEventBus.addListener(this::commonSetup);
        NeoForge.EVENT_BUS.register(this);
        MGsOdditiesItems.register(modEventBus);
        modEventBus.addListener(this::addCreative);
        MGsOdditiesCreativeTabs.CREATIVE_TABS.register(modEventBus);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
//        if(event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
//            //控制电路
//            event.accept(MGsOdditiesItems.PARAGON_CONTROL_CIRCUIT);
//            event.accept(MGsOdditiesItems.APOTHEOSIS_CONTROL_CIRCUIT);
//            //对应电路的合金
//            event.accept(MGsOdditiesItems.ALLOY_PRIMORDIAL);
//            event.accept(MGsOdditiesItems.ALLOY_AETHER);
//
//            event.accept(MGsOdditiesItems.NETHER_ESSENCE);
//            event.accept(MGsOdditiesItems.DARK_MATTER);
//            event.accept(MGsOdditiesItems.AETHER_ESSENCE);
//            event.accept(MGsOdditiesItems.ASTRAL_THREADS);
//        }
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
