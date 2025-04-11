package github.mgrlyz.mgsoddities.common.registries;

import github.mgrlyz.mgsoddities.common.MGsOdditiesLang;
import github.mgrlyz.mgsoddities.common.registries.block.MGsOdditiesBlocks;
import github.mgrlyz.mgsoddities.common.registries.item.MGsOdditiesItems;
import mekanism.common.registration.MekanismDeferredHolder;
import mekanism.common.registration.impl.CreativeTabDeferredRegister;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

public class MGsOdditiesCreativeTabs {
    public static final CreativeTabDeferredRegister MGSODDITIES_CREATIVE_TABS = new CreativeTabDeferredRegister("mgsoddities", MGsOdditiesCreativeTabs::addToExistingTabs);
    public static final MekanismDeferredHolder<CreativeModeTab, CreativeModeTab> MGSODDITIES;

    public MGsOdditiesCreativeTabs() {
    }

    private static void addToExistingTabs(BuildCreativeModeTabContentsEvent buildCreativeModeTabContentsEvent) {
    }

    public static void register(IEventBus eventBus) {
        MGSODDITIES_CREATIVE_TABS.register(eventBus);
    }

    static {
        MGSODDITIES = MGSODDITIES_CREATIVE_TABS.registerMain(MGsOdditiesLang.MGSODDITIES, MGsOdditiesItems.APOTHEOSIS_CONTROL_CIRCUIT, (builder) -> builder.displayItems((displayParameters, output) -> {
            CreativeTabDeferredRegister.addToDisplay(MGsOdditiesItems.MGSODDITIES_ITEMS, output);
            CreativeTabDeferredRegister.addToDisplay(MGsOdditiesBlocks.MGSODDITIES_BLOCKS, output);
        }));
    }
}
