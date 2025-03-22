package github.mgrlyz.mgsoddities.config.value;

import github.mgrlyz.mgsoddities.MGsOddities;
import github.mgrlyz.mgsoddities.config.IMGsOdditiesConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.function.BiPredicate;

public abstract class CachedValue<T> {

    private final IMGsOdditiesConfig config;
    protected final ModConfigSpec.ConfigValue<T> internal;
    private Set<CachedValue.IConfigValueInvalidationListener> invalidationListeners;

    protected CachedValue(IMGsOdditiesConfig config, ModConfigSpec.ConfigValue<T> internal) {
        this.config = config;
        this.internal = internal;
        this.config.addCachedValue(this);
    }

    public boolean hasInvalidationListeners() {
        return invalidationListeners != null && !invalidationListeners.isEmpty();
    }

    public void addInvalidationListener(CachedValue.IConfigValueInvalidationListener listener) {
        if (invalidationListeners == null) {
            invalidationListeners = new HashSet<>();
        }
        if (!invalidationListeners.add(listener)) {
            MGsOddities.logger.warn("Duplicate invalidation listener added");
        }
    }

    public void removeInvalidationListener(CachedValue.IConfigValueInvalidationListener listener) {
        if (invalidationListeners == null) {
            MGsOddities.logger.warn("Unable to remove specified invalidation listener, no invalidation listeners have been added.");
        } else if (!invalidationListeners.remove(listener)) {
            MGsOddities.logger.warn("Unable to remove specified invalidation listener.");
        }
    }

    public <DATA> void removeInvalidationListenersMatching(DATA data, BiPredicate<CachedValue.IConfigValueInvalidationListener, DATA> checker) {
        if (invalidationListeners != null && !invalidationListeners.isEmpty()) {
            for (Iterator<CachedValue.IConfigValueInvalidationListener> iter = invalidationListeners.iterator(); iter.hasNext(); ) {
                CachedValue.IConfigValueInvalidationListener listener = iter.next();
                if (checker.test(listener, data)) {
                    iter.remove();
                }
            }
        }
    }

    protected abstract boolean clearCachedValue(boolean checkChanged);

    public final void clearCache(boolean unloading) {
        if (hasInvalidationListeners()) {
            if (!unloading && isLoaded() && clearCachedValue(true)) {
                invalidationListeners.forEach(CachedValue.IConfigValueInvalidationListener::run);
            }
        } else {
            clearCachedValue(false);
        }
    }

    protected boolean isLoaded() {
        return config.isLoaded();
    }

    @FunctionalInterface
    public interface IConfigValueInvalidationListener extends Runnable {
    }
}