package github.mgrlyz.mgsoddities.capabilities.resolver.manager;

import github.mgrlyz.mgsoddities.capabilities.resolver.ICapabilityResolver;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@MethodsReturnNonnullByDefault
public interface ICapabilityHandlerManager<CONTAINER> extends ICapabilityResolver<@Nullable Direction> {

    boolean canHandle();

    List<CONTAINER> getContainers(@Nullable Direction side);
}