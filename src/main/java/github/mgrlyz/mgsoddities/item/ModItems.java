package github.mgrlyz.mgsoddities.item;

import github.mgrlyz.mgsoddities.MGsOddities;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MGsOddities.MODID);

    public static final DeferredItem<Item> PARAGON_CONTROL_CIRCUIT = ITEMS.register("paragon_control_circuit",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> APOTHEOSIS_CONTROL_CIRCUIT = ITEMS.register("apotheosis_control_circuit",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> ALLOY_PRIMORDIAL = ITEMS.register("alloy_primordial",
            () -> new Item(new Item.Properties()));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
