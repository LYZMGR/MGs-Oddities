package github.mgrlyz.mgsoddities.registration.impl;

import github.mgrlyz.mgsoddities.registration.MGsOdditiesDeferredHolder;
import github.mgrlyz.mgsoddities.registration.MGsOdditiesDeferredRegister;
import mekanism.api.providers.IItemProvider;
import mekanism.api.text.ILangEntry;
import mekanism.client.SpecialColors;
import mekanism.common.registration.impl.ItemDeferredRegister;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.UnaryOperator;

public class CreativeTabDeferredRegister extends MGsOdditiesDeferredRegister<CreativeModeTab> {

    private final Consumer<BuildCreativeModeTabContentsEvent> addToExistingTabs;

    public CreativeTabDeferredRegister(String modid) {
        this(modid, event -> {
        });
    }

    public CreativeTabDeferredRegister(String modid, Consumer<BuildCreativeModeTabContentsEvent> addToExistingTabs) {
        super(Registries.CREATIVE_MODE_TAB, modid);
        this.addToExistingTabs = addToExistingTabs;
    }

    @Override
    public void register(@NotNull IEventBus bus) {
        super.register(bus);
        bus.addListener(addToExistingTabs);
    }

    public MGsOdditiesDeferredHolder<CreativeModeTab, CreativeModeTab> registerMain(ILangEntry title, IItemProvider icon, UnaryOperator<CreativeModeTab.Builder> operator) {
        return register(getNamespace(), title, icon, operator);
    }

    public MGsOdditiesDeferredHolder<CreativeModeTab, CreativeModeTab> register(String name, ILangEntry title, IItemProvider icon, UnaryOperator<CreativeModeTab.Builder> operator) {
        return register(name, () -> {
            CreativeModeTab.Builder builder = CreativeModeTab.builder()
                    .title(title.translate())
                    .icon(icon::getItemStack)
                    .withTabFactory(MGsOdditiesCreativeTab::new);
            return operator.apply(builder).build();
        });
    }

    public static void addToDisplay(CreativeModeTab.Output output, ItemLike... items) {
        for (ItemLike item : items) {
            addToDisplay(output, item);
        }
    }

    public static void addToDisplay(CreativeModeTab.Output output, ItemLike itemLike) {
        CreativeModeTab.TabVisibility visibility;
        if (output instanceof BuildCreativeModeTabContentsEvent) {
            visibility = CreativeModeTab.TabVisibility.PARENT_TAB_ONLY;
        } else {
            visibility = CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS;
        }
        if (itemLike.asItem() instanceof ICustomCreativeTabContents contents) {
            if (contents.addDefault()) {
                output.accept(itemLike, visibility);
            }
            contents.addItems(stack -> output.accept(stack, visibility));
        } else {
            output.accept(itemLike, visibility);
        }
    }

    public static void addToDisplay(ItemDeferredRegister register, CreativeModeTab.Output output) {
        for (Holder<Item> itemProvider : register.getEntries()) {
            addToDisplay(output, itemProvider.value());
        }
    }

    public interface ICustomCreativeTabContents {

        void addItems(Consumer<ItemStack> addToTab);

        default boolean addDefault() {
            return true;
        }
    }

    public static class MGsOdditiesCreativeTab extends CreativeModeTab {

        protected MGsOdditiesCreativeTab(CreativeModeTab.Builder builder) {
            super(builder);
        }

        @Override
        public int getLabelColor() {
            return SpecialColors.TEXT_TITLE.argb();
        }
    }
}