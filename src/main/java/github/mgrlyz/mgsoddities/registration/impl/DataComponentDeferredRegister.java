package github.mgrlyz.mgsoddities.registration.impl;

import github.mgrlyz.mgsoddities.registration.MGsOdditiesDeferredHolder;
import github.mgrlyz.mgsoddities.registration.MGsOdditiesDeferredRegister;
import mekanism.api.annotations.NothingNullByDefault;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;

import java.util.function.UnaryOperator;

@NothingNullByDefault
public class DataComponentDeferredRegister extends MGsOdditiesDeferredRegister<DataComponentType<?>> {
    public DataComponentDeferredRegister(String namespace) {
        super(Registries.DATA_COMPONENT_TYPE, namespace);
    }

    public <TYPE> MGsOdditiesDeferredHolder<DataComponentType<?>, DataComponentType<TYPE>> simple(String name, UnaryOperator<DataComponentType.Builder<TYPE>> operator) {
        return register(name, () -> operator.apply(DataComponentType.builder()).build());
    }

}
