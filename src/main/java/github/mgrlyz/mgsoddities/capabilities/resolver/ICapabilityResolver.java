package github.mgrlyz.mgsoddities.capabilities.resolver;

import mekanism.api.annotations.NothingNullByDefault;
import net.neoforged.neoforge.capabilities.BlockCapability;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.List;

@NothingNullByDefault
public interface ICapabilityResolver<CONTEXT> {

    List<BlockCapability<?, CONTEXT>> getSupportedCapabilities();

    @Nullable
    <T> T resolve(BlockCapability<T, CONTEXT> capability, @UnknownNullability CONTEXT side);

    void invalidate(BlockCapability<?, CONTEXT> capability, @UnknownNullability CONTEXT context);

    void invalidateAll();
}