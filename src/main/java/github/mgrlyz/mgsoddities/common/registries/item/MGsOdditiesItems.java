package github.mgrlyz.mgsoddities.common.registries.item;

import github.mgrlyz.mgsoddities.api.tier.AdvanceTier;
import github.mgrlyz.mgsoddities.api.tier.MGsOdditiesAlloyTier;
import mekanism.api.text.TextComponentUtil;
import mekanism.common.registration.impl.ItemDeferredRegister;
import mekanism.common.registration.impl.ItemRegistryObject;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.neoforged.bus.api.IEventBus;
import org.jetbrains.annotations.NotNull;

public class MGsOdditiesItems {
    public static final ItemDeferredRegister MGSODDITIES_ITEMS = new ItemDeferredRegister("mgsoddities");
    public static final ItemRegistryObject<Item> PARAGON_CONTROL_CIRCUIT;
    public static final ItemRegistryObject<Item> APOTHEOSIS_CONTROL_CIRCUIT;
    public static final ItemRegistryObject<Item> NETHER_ESSENCE;
    public static final ItemRegistryObject<Item> AETHER_ESSENCE;
    public static final ItemRegistryObject<MGsOdditiesItemAlloy> ALLOY_PRIMORDIAL;
    public static final ItemRegistryObject<MGsOdditiesItemAlloy> ALLOY_AETHER;
    public static final ItemRegistryObject<Item> ASTRAL_THREADS;
    public static final ItemRegistryObject<Item> DARK_MATTER;

    public MGsOdditiesItems() {
    }
    
    private static ItemRegistryObject<Item> registerCircuit(AdvanceTier tier) {
        return MGSODDITIES_ITEMS.registerItem(tier.getLowerName() + "_control_circuit", (properties) -> new Item(properties) {
            public @NotNull Component getName(@NotNull ItemStack stack) {
                return TextComponentUtil.build(new Object[]{tier.getColor(), super.getName(stack)});
            }
        });
    }

    private static ItemRegistryObject<Item> registerEssence(String name) {
        return MGSODDITIES_ITEMS.registerItem(name, (properties) -> new Item(properties));
    }

    private static ItemRegistryObject<MGsOdditiesItemAlloy> registerAlloy(MGsOdditiesAlloyTier tier, Rarity rarity) {
        return MGSODDITIES_ITEMS.registerItem("alloy_" + tier.getName(), (properties) -> new MGsOdditiesItemAlloy(tier, properties.rarity(rarity)));
    }

    public static void register(IEventBus eventBus) {
        MGSODDITIES_ITEMS.register(eventBus);
    }

    static {
        PARAGON_CONTROL_CIRCUIT = registerCircuit(AdvanceTier.PARAGON);
        APOTHEOSIS_CONTROL_CIRCUIT = registerCircuit(AdvanceTier.APOTHEOSIS);
        NETHER_ESSENCE = registerEssence("nether_essence");
        AETHER_ESSENCE = registerEssence("aether_essence");
        ALLOY_PRIMORDIAL = registerAlloy(MGsOdditiesAlloyTier.PRIMORDIAL, Rarity.EPIC);
        ALLOY_AETHER = registerAlloy(MGsOdditiesAlloyTier.AETHER, Rarity.EPIC);
        ASTRAL_THREADS = MGSODDITIES_ITEMS.register("astral_threads");
        DARK_MATTER = MGSODDITIES_ITEMS.register("dark_matter");
    }



}