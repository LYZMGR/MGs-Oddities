package github.mgrlyz.mgsoddities.registries;

import github.mgrlyz.mgsoddities.MGsOddities;
import github.mgrlyz.mgsoddities.MGsOdditiesLang;
import github.mgrlyz.mgsoddities.registration.MGsOdditiesDeferredHolder;
import github.mgrlyz.mgsoddities.registration.impl.CreativeTabDeferredRegister;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

public class MGsOdditiesCreativeTabs {
    public static final CreativeTabDeferredRegister CREATIVE_TABS = new CreativeTabDeferredRegister(MGsOddities.MODID,MGsOdditiesCreativeTabs::addToExistingTabs);

    public static final MGsOdditiesDeferredHolder<CreativeModeTab,CreativeModeTab> MGSODDITIES = CREATIVE_TABS.registerMain(MGsOdditiesLang.MGSODDITIES, MGsOdditiesItems.APOTHEOSIS_CONTROL_CIRCUIT, builder ->
            builder.withSearchBar()
                    .displayItems((displayParameters, output) -> {
                        CreativeTabDeferredRegister.addToDisplay(MGsOdditiesItems.ITEMS, output);
                    })
    );

    public static void addToExistingTabs(BuildCreativeModeTabContentsEvent event) {
        ResourceKey<CreativeModeTab> tabKey = event.getTabKey();
//        if (tabKey == CreativeModeTabs.BUILDING_BLOCKS) {
//            mekanism.common.registration.impl.CreativeTabDeferredRegister.addToDisplay(event, MekanismBlocks.SALT_BLOCK, MekanismBlocks.BRONZE_BLOCK, MekanismBlocks.STEEL_BLOCK,
//                    MekanismBlocks.CHARCOAL_BLOCK, MekanismBlocks.REFINED_OBSIDIAN_BLOCK, MekanismBlocks.REFINED_GLOWSTONE_BLOCK);
//        } else
        if (tabKey ==CreativeModeTabs.INGREDIENTS) {
            CreativeTabDeferredRegister.addToDisplay(event,
                    MGsOdditiesItems.PARAGON_CONTROL_CIRCUIT,MGsOdditiesItems.APOTHEOSIS_CONTROL_CIRCUIT,
                    MGsOdditiesItems.PRIMORDIAL_ALLOY,MGsOdditiesItems.AETHER_ALLOY,
                    MGsOdditiesItems.NETHER_ESSENCE,MGsOdditiesItems.AETHER_ESSENCE,
                    MGsOdditiesItems.DARK_MATTER,
                    MGsOdditiesItems.ASTRAL_THREADS
            );
        }
    }
}