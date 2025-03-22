package github.mgrlyz.mgsoddities.intergration.energy;

import mekanism.api.energy.IStrictEnergyHandler;
import mekanism.common.integration.energy.fluxnetworks.FNEnergyCompat;
import mekanism.common.integration.energy.forgeenergy.ForgeEnergyCompat;
import mekanism.common.integration.energy.grandpower.GPEnergyCompat;
import mekanism.common.registration.impl.TileEntityTypeDeferredRegister;
import mekanism.common.tile.base.CapabilityTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.BiFunction;

public class EnergyCompatUtils {

    private EnergyCompatUtils() {
    }

    private static final List<IEnergyCompat> energyCompats = List.of(
            new StrictEnergyCompat(),
            new GPEnergyCompat(),
            new FNEnergyCompat(),
            new ForgeEnergyCompat()
    );

    private static List<BlockCapability<?, @Nullable Direction>> LOADED_ENERGY_CAPS = List.of(Capabilities.STRICT_ENERGY.block(), Capabilities.ENERGY.block());

    public static void initLoadedCache() {
        LOADED_ENERGY_CAPS = energyCompats.stream().filter(IEnergyCompat::capabilityExists).<BlockCapability<?, @Nullable Direction>>map(compat -> compat.getCapability().block()).toList();
    }

    public static List<IEnergyCompat> getCompats() {
        return energyCompats;
    }

    public static boolean isEnergyCapability(@NotNull BlockCapability<?, @Nullable Direction> capability) {
        for (IEnergyCompat energyCompat : energyCompats) {
            if (energyCompat.capabilityExists() && energyCompat.getCapability().block() == capability) {
                return energyCompat.isUsable();
            }
        }
        return false;
    }

    public static List<BlockCapability<?, @Nullable Direction>> getLoadedEnergyCapabilities() {
        return LOADED_ENERGY_CAPS;
    }

    public static void registerItemCapabilities(RegisterCapabilitiesEvent event, Item item, ICapabilityProvider<ItemStack, Void, IStrictEnergyHandler> mekProvider) {
        for (IEnergyCompat energyCompat : energyCompats) {
            if (energyCompat.capabilityExists()) {
                register(event, energyCompat.getCapability().item(), energyCompat.getProviderAs(mekProvider), item);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static <CAP> void register(RegisterCapabilitiesEvent event, ItemCapability<CAP, Void> capability, ICapabilityProvider<ItemStack, Void, ?> provider, Item item) {
        event.registerItem(capability, (ICapabilityProvider<ItemStack, Void, CAP>) provider, item);
    }

    public static <ENTITY extends Entity> void registerEntityCapabilities(RegisterCapabilitiesEvent event, EntityType<ENTITY> entity,
                                                                          ICapabilityProvider<? super ENTITY, ?, IStrictEnergyHandler> mekProvider) {
        for (IEnergyCompat energyCompat : energyCompats) {
            if (energyCompat.capabilityExists()) {
                register(event, energyCompat.getCapability().entity(), entity, energyCompat.getProviderAs(mekProvider));
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static <ENTITY extends Entity, CAP, CONTEXT> void register(RegisterCapabilitiesEvent event, EntityCapability<CAP, CONTEXT> capability, EntityType<ENTITY> entity,
                                                                       ICapabilityProvider<? super ENTITY, ?, ?> provider) {
        event.registerEntity(capability, entity, (ICapabilityProvider<? super ENTITY, CONTEXT, CAP>) provider);
    }

    public static void addBlockCapabilities(TileEntityTypeDeferredRegister.BlockEntityTypeBuilder<? extends CapabilityTileEntity> builder) {
        for (IEnergyCompat energyCompat : energyCompats) {
            if (energyCompat.capabilityExists()) {
                builder.with(energyCompat.getCapability().block(), CapabilityTileEntity::basicCapabilityProvider);
            }
        }
    }

    @Nullable
    public static Object wrapStrictEnergyHandler(BlockCapability<?, @Nullable Direction> capability, IStrictEnergyHandler handler) {
        for (IEnergyCompat energyCompat : energyCompats) {
            if (energyCompat.isUsable() && energyCompat.getCapability().block() == capability) {
                return energyCompat.wrapStrictEnergyHandler(handler);
            }
        }
        return null;
    }

    public static boolean hasStrictEnergyHandler(@NotNull ItemStack stack) {
        return getStrictEnergyHandler(stack) != null;
    }

    @Nullable
    public static IStrictEnergyHandler getStrictEnergyHandler(@NotNull ItemStack stack) {
        if (!stack.isEmpty()) {
            return getStrictEnergyHandler(stack, IEnergyCompat::getStrictEnergyHandler);
        }
        return null;
    }

    @Nullable
    public static IStrictEnergyHandler getStrictEnergyHandler(@Nullable Entity entity) {
        if (entity != null) {
            return getStrictEnergyHandler(entity, IEnergyCompat::getStrictEnergyHandler);
        }
        return null;
    }

    @Nullable
    private static <OBJECT> IStrictEnergyHandler getStrictEnergyHandler(OBJECT object, BiFunction<IEnergyCompat, OBJECT, IStrictEnergyHandler> getter) {
        for (IEnergyCompat energyCompat : energyCompats) {
            if (energyCompat.isUsable()) {
                IStrictEnergyHandler handler = getter.apply(energyCompat, object);
                if (handler != null) {
                    return handler;
                }
            }
        }
        return null;
    }

    @Nullable
    public static IStrictEnergyHandler getStrictEnergyHandler(Level level, BlockPos pos, @Nullable BlockState state, @Nullable BlockEntity tile, Direction side) {
        for (IEnergyCompat energyCompat : energyCompats) {
            if (energyCompat.isUsable()) {
                IStrictEnergyHandler handler = energyCompat.getAsStrictEnergyHandler(level, pos, state, tile, side);
                if (handler != null) {
                    return handler;
                }
            }
        }
        return null;
    }
}