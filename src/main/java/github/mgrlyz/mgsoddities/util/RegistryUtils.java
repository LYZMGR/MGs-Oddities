package github.mgrlyz.mgsoddities.util;

import mekanism.api.SerializationConstants;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class RegistryUtils {

    private RegistryUtils() {
    }

    public static Holder<BlockEntityType<?>> getBEHolder(BlockEntityType<?> type) {
        Holder<BlockEntityType<?>> holder = type.builtInRegistryHolder();
        if (holder == null) {
            return BuiltInRegistries.BLOCK_ENTITY_TYPE.wrapAsHolder(type);
        }
        return holder;
    }

    public static <R> Optional<R> getById(CompoundTag nbt, Registry<R> registry) {
        return Optional.ofNullable(nbt)
                .filter(tag -> tag.contains(SerializationConstants.ID, Tag.TAG_STRING))
                .map(tag -> tag.getString(SerializationConstants.ID))
                .map(ResourceLocation::tryParse)
                .flatMap(registry::getOptional);
    }

    public static ResourceLocation getName(MenuType<?> element) {
        return BuiltInRegistries.MENU.getKey(element);
    }

    public static ResourceLocation getName(ParticleType<?> element) {
        return BuiltInRegistries.PARTICLE_TYPE.getKey(element);
    }

    public static ResourceLocation getName(Item element) {
        return BuiltInRegistries.ITEM.getKey(element);
    }

    public static String getPath(Item element) {
        return getName(element).getPath();
    }

    public static ResourceLocation getName(Block element) {
        return BuiltInRegistries.BLOCK.getKey(element);
    }

    public static String getNamespace(Block element) {
        return getName(element).getNamespace();
    }

    public static String getPath(Block element) {
        return getName(element).getPath();
    }

    public static ResourceLocation getName(Fluid element) {
        return BuiltInRegistries.FLUID.getKey(element);
    }

    public static ResourceLocation getName(BlockEntityType<?> element) {
        return BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(element);
    }

    public static ResourceLocation getName(EntityType<?> element) {
        return BuiltInRegistries.ENTITY_TYPE.getKey(element);
    }

    @Nullable
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static ResourceLocation getNameGeneric(Object element) {
        for (Registry<?> registry : BuiltInRegistries.REGISTRY) {
            Optional<ResourceKey<?>> resourceKey = ((Registry) registry).getResourceKey(element);
            if (resourceKey.isPresent()) {
                return resourceKey.get().location();
            }
        }
        return null;
    }
}