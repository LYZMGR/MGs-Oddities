package github.mgrlyz.mgsoddities.registries;

import github.mgrlyz.mgsoddities.MGsOddities;
import github.mgrlyz.mgsoddities.attachments.component.UpgradeAware;
import github.mgrlyz.mgsoddities.attachments.containers.chemical.AttachedChemicals;
import github.mgrlyz.mgsoddities.attachments.containers.heat.AttachedHeat;
import github.mgrlyz.mgsoddities.registration.MGsOdditiesDeferredHolder;
import github.mgrlyz.mgsoddities.registration.impl.DataComponentDeferredRegister;
import mekanism.common.attachments.containers.energy.AttachedEnergy;
import mekanism.common.attachments.containers.fluid.AttachedFluids;
import mekanism.common.attachments.containers.item.AttachedItems;
import mekanism.common.registration.MekanismDeferredHolder;
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

    public static final MGsOdditiesDeferredHolder<DataComponentType<?>, DataComponentType<AttachedEnergy>> ATTACHED_ENERGY = DATA_COMPONENTS.simple("energy",
            builder -> builder.persistent(AttachedEnergy.CODEC)
                    .networkSynchronized(AttachedEnergy.STREAM_CODEC)
                    .cacheEncoding()
    );
    public static final MGsOdditiesDeferredHolder<DataComponentType<?>, DataComponentType<AttachedItems>> ATTACHED_ITEMS = DATA_COMPONENTS.simple("items",
            builder -> builder.persistent(AttachedItems.CODEC)
                    .networkSynchronized(AttachedItems.STREAM_CODEC)
                    .cacheEncoding()
    );
    public static final MGsOdditiesDeferredHolder<DataComponentType<?>, DataComponentType<AttachedFluids>> ATTACHED_FLUIDS = DATA_COMPONENTS.simple("fluids",
            builder -> builder.persistent(AttachedFluids.CODEC)
                    .networkSynchronized(AttachedFluids.STREAM_CODEC)
                    .cacheEncoding()
    );

    public static final MGsOdditiesDeferredHolder<DataComponentType<?>, DataComponentType<AttachedChemicals>> ATTACHED_CHEMICALS = DATA_COMPONENTS.simple("chemicals",
            builder -> builder.persistent(AttachedChemicals.CODEC)
                    .networkSynchronized(AttachedChemicals.STREAM_CODEC)
                    .cacheEncoding()
    );

    static {
        DATA_COMPONENTS.addAlias(MGsOddities.rl("gases"), MGsOddities.rl(ATTACHED_CHEMICALS.getName()));
        DATA_COMPONENTS.addAlias(MGsOddities.rl("infuse_types"), MGsOddities.rl(ATTACHED_CHEMICALS.getName()));
        DATA_COMPONENTS.addAlias(MGsOddities.rl("pigments"), MGsOddities.rl(ATTACHED_CHEMICALS.getName()));
        DATA_COMPONENTS.addAlias(MGsOddities.rl("slurries"), MGsOddities.rl(ATTACHED_CHEMICALS.getName()));
    }

    public static final MGsOdditiesDeferredHolder<DataComponentType<?>, DataComponentType<AttachedHeat>> ATTACHED_HEAT = DATA_COMPONENTS.simple("heat_data",
            builder -> builder.persistent(AttachedHeat.CODEC)
                    .networkSynchronized(AttachedHeat.STREAM_CODEC)
                    .cacheEncoding()
    );
}
