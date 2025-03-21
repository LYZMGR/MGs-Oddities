package github.mgrlyz.mgsoddities.api.providers;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

@MethodsReturnNonnullByDefault
public interface IBlockProvider extends IItemProvider {

    Block getBlock();
    @NotNull
    default BlockState defaultState() {
        return getBlock().defaultBlockState();
    }

    @Override
    default ResourceLocation getRegistryName() {
        return BuiltInRegistries.BLOCK.getKey(getBlock());
    }

    @Override
    default String getTranslationKey() {
        return getBlock().getDescriptionId();
    }
}