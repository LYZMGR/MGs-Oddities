package github.mgrlyz.mgsoddities.registries;

import github.mgrlyz.mgsoddities.Item.ItemAlloy;
import github.mgrlyz.mgsoddities.MGsOddities;
import github.mgrlyz.mgsoddities.api.tier.AlloyTier;
import github.mgrlyz.mgsoddities.api.tier.BaseTier;
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

    private MGsOdditiesItems() {
    }

    public static final ItemDeferredRegister ITEMS = new ItemDeferredRegister(MGsOddities.MODID);
    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
    //注册控制电路
    public static final ItemRegistryObject<Item> PARAGON_CONTROL_CIRCUIT = registerCircuit(BaseTier.PARAGON);
    public static final ItemRegistryObject<Item> APOTHEOSIS_CONTROL_CIRCUIT = registerCircuit(BaseTier.APOTHEOSIS);

    //注册合金
    public static final ItemRegistryObject<ItemAlloy> PRIMORDIAL_ALLOY = registerAlloy(AlloyTier.PRIMORDIAL, Rarity.EPIC);
    public static final ItemRegistryObject<ItemAlloy> AETHER_ALLOY = registerAlloy(AlloyTier.AETHER, Rarity.EPIC);

    //精华
    public static final ItemRegistryObject<Item> NETHER_ESSENCE = ITEMS.register("nether_essence");
    public static final ItemRegistryObject<Item> AETHER_ESSENCE = ITEMS.register("aether_essence");
    public static final ItemRegistryObject<Item> DARK_MATTER = ITEMS.register("dark_matter");
    public static final ItemRegistryObject<Item> ASTRAL_THREADS = ITEMS.register("astral_threads");

    private static ItemRegistryObject<Item> registerCircuit(BaseTier tier) {
        return ITEMS.registerItem(tier.getLowerName() + "_control_circuit", properties -> new Item(properties) {
            @NotNull
            @Override
            public Component getName(@NotNull ItemStack stack) {
                return TextComponentUtil.build(tier.getColor(), super.getName(stack));
            }
        });
    }
    private static ItemRegistryObject<ItemAlloy> registerAlloy(AlloyTier tier, Rarity rarity) {
        return ITEMS.registerItem("alloy_" + tier.getName(), properties -> new ItemAlloy(tier, properties.rarity(rarity)));
    }
}