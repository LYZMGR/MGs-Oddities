package github.mgrlyz.mgsoddities.registries;

import github.mgrlyz.mgsoddities.MGsOddities;
import github.mgrlyz.mgsoddities.attachments.component.UpgradeAware;
import github.mgrlyz.mgsoddities.registration.MGsOdditiesDeferredHolder;
import github.mgrlyz.mgsoddities.registration.impl.DataComponentDeferredRegister;
import net.minecraft.core.component.DataComponentType;

public class MGsOdditiesDataComponents {
    private MGsOdditiesDataComponents() {
    }
    public static final DataComponentDeferredRegister DATA_COMPONENTS = new DataComponentDeferredRegister(MGsOddities.MODID);

    public static final MGsOdditiesDeferredHolder<DataComponentType<?>, DataComponentType<UpgradeAware>> UPGRADES = DATA_COMPONENTS.simple("upgrades",
            builder -> builder.persistent(UpgradeAware.CODEC)
                    .networkSynchronized(UpgradeAware.STREAM_CODEC)
                    .cacheEncoding()
    );
}
