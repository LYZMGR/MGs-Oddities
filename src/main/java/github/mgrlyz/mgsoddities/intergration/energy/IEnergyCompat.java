package github.mgrlyz.mgsoddities.intergration.energy;

import github.mgrlyz.mgsoddities.capabilities.MultiTypeCapability;
import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.energy.IStrictEnergyHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import org.jetbrains.annotations.Nullable;

@NothingNullByDefault
public interface IEnergyCompat {

    boolean isUsable();

    default boolean capabilityExists() {
        return true;
    }

    MultiTypeCapability<?> getCapability();

    <OBJECT, CONTEXT> ICapabilityProvider<OBJECT, CONTEXT, ?> getProviderAs(ICapabilityProvider<OBJECT, CONTEXT, IStrictEnergyHandler> provider);

    Object wrapStrictEnergyHandler(IStrictEnergyHandler handler);

    @Nullable
    IStrictEnergyHandler wrapAsStrictEnergyHandler(Object handler);

    @Nullable
    default IStrictEnergyHandler getAsStrictEnergyHandler(Level level, BlockPos pos, @Nullable BlockState state, @Nullable BlockEntity tile, @Nullable Direction context) {
        Object capability = getCapability().getCapabilityIfLoaded(level, pos, state, tile, context);
        return capability == null ? null : wrapAsStrictEnergyHandler(capability);
    }

    @Nullable
    default IStrictEnergyHandler getStrictEnergyHandler(ItemStack stack) {
        Object capability = getCapability().getCapability(stack);
        return capability == null ? null : wrapAsStrictEnergyHandler(capability);
    }

    @Nullable
    default IStrictEnergyHandler getStrictEnergyHandler(Entity entity) {
        Object capability = getCapability().getCapability(entity);
        return capability == null ? null : wrapAsStrictEnergyHandler(capability);
    }
}