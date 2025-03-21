package github.mgrlyz.mgsoddities.registration;

import mekanism.api.annotations.NothingNullByDefault;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Function;
import java.util.function.Supplier;

@NothingNullByDefault
public class MGsOdditiesDeferredRegister<T> extends DeferredRegister<T> {
    private final Function<ResourceKey<T>, ? extends MGsOdditiesDeferredHolder<T, ?>> holderCreator;

    public MGsOdditiesDeferredRegister(ResourceKey<? extends Registry<T>> registryKey, String namespace) {
        this(registryKey, namespace, MGsOdditiesDeferredHolder::new);
    }

    public MGsOdditiesDeferredRegister(ResourceKey<? extends Registry<T>> registryKey, String namespace,
                                    Function<ResourceKey<T>, ? extends MGsOdditiesDeferredHolder<T, ? extends T>> holderCreator) {
        super(registryKey, namespace);
        this.holderCreator = holderCreator;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <I extends T> MGsOdditiesDeferredHolder<T, I> register(String name, Function<ResourceLocation, ? extends I> func) {
        return (MGsOdditiesDeferredHolder<T, I>) super.register(name, func);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <I extends T> MGsOdditiesDeferredHolder<T, I> register(String name, Supplier<? extends I> sup) {
        return (MGsOdditiesDeferredHolder<T, I>) super.register(name, sup);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected <I extends T> MGsOdditiesDeferredHolder<T, I> createHolder(ResourceKey<? extends Registry<T>> registryKey, ResourceLocation key) {
        return (MGsOdditiesDeferredHolder<T, I>) holderCreator.apply(ResourceKey.create(registryKey, key));
    }
}