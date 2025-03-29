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
//    public static final DeferredRegister<CreativeModeTab> CREATIVE_TAB =
//            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MGsOddities.MODID);
//
//    public static final Supplier<CreativeModeTab> MGsOddities_ITEMS_TAB = CREATIVE_TAB.register("mgsoddities_items_tab",
//            () -> CreativeModeTab.builder().icon(() -> new ItemStack(MGsOdditiesItems.APOTHEOSIS_CONTROL_CIRCUIT.get()))
//                    .title(Component.translatable("creativetab.mgsoddities.items"))
//                    .displayItems((itemDisplayParameters,output) -> {
//                        output.accept(MGsOdditiesItems.PARAGON_CONTROL_CIRCUIT);
//                        output.accept(MGsOdditiesItems.APOTHEOSIS_CONTROL_CIRCUIT);
//                        output.accept(MGsOdditiesItems.ALLOY_PRIMORDIAL);
//                        output.accept(MGsOdditiesItems.ALLOY_AETHER);
//                        output.accept(MGsOdditiesItems.DARK_MATTER);
//                        output.accept(MGsOdditiesItems.AETHER_ESSENCE);
//                        output.accept(MGsOdditiesItems.NETHER_ESSENCE);
//                        output.accept(MGsOdditiesItems.ASTRAL_THREADS);
//                    }).build());
//
//    public static final Supplier<CreativeModeTab> MGsOddities_BLOCKS_TAB = CREATIVE_TAB.register("mgsoddities_blocks_tab",
//            () -> CreativeModeTab.builder().icon(() -> new ItemStack(MGsOdditiesBlocks.STELLAR_MATTER_O.get()))
//                    .title(Component.translatable("creativetab.mgsoddities.blocks"))
//                    .displayItems((itemDisplayParameters,output) -> {
//                        output.accept(MGsOdditiesBlocks.DYSON_SPHERE_FRAME);
//                        output.accept(MGsOdditiesBlocks.DYSON_SPHERE_SHELL);
//                        output.accept(MGsOdditiesBlocks.STELLAR_MATTER_O);
//                        output.accept(MGsOdditiesBlocks.STELLAR_MATTER_F);
//                        output.accept(MGsOdditiesBlocks.STELLAR_MATTER_M);
//                        output.accept(MGsOdditiesBlocks.PARAGON_UNIVERSAL_CABLE);
//                        output.accept(MGsOdditiesBlocks.STELLAR_MATTER_M);
//                        output.accept(MGsOdditiesBlocks.STELLAR_MATTER_M);
//                    }).build());
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
