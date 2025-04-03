package github.mgrlyz.mgsoddities.client.events;

import github.mgrlyz.mgsoddities.common.tier.TierColor;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;

public class ClientTick {
    @SubscribeEvent
    public void onTickClientTick(ClientTickEvent.Pre event) {
        TierColor.tick();
    }
}