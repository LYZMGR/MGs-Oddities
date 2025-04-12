package github.mgrlyz.mgsoddities.common.recipe;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

public record NbtInput(NonNullList<ItemStack> stacks) implements RecipeInput {
    @Override
    public ItemStack getItem(int slot) {
        if (slot < 0 || slot >= stacks.size()) {
            throw new IllegalArgumentException("No item for index " + slot);
        }
        return stacks.get(slot);
    }

    @Override
    public int size() {
        return stacks.size();
    }
}
