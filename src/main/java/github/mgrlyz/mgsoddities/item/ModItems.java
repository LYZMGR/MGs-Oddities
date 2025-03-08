package github.mgrlyz.mgsoddities.item;

import github.mgrlyz.mgsoddities.MGsOddities;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MGsOddities.MODID);

    //注册控制电路
    public static final DeferredItem<Item> PARAGON_CONTROL_CIRCUIT = ITEMS.register("paragon_control_circuit",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> APOTHEOSIS_CONTROL_CIRCUIT = ITEMS.register("apotheosis_control_circuit",
            () -> new Item(new Item.Properties()));

    //注册对应电路的合金
    public static final DeferredItem<Item> ALLOY_PRIMORDIAL = ITEMS.register("alloy_primordial",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> ALLOY_AETHER = ITEMS.register("alloy_aether",
            () -> new Item((new Item.Properties())));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
