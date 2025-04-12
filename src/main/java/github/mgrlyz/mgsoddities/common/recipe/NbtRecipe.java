package github.mgrlyz.mgsoddities.common.recipe;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public class NbtRecipe implements Recipe<NbtInput> {
    private final NonNullList<Ingredient> inputItems;
    private final ItemStack output;

    public NbtRecipe(NonNullList<Ingredient> inputItems,ItemStack output) {
        this.inputItems = inputItems;
        this.output = output;
    }

    @Override
    public boolean matches(NbtInput input, Level level) {
        if (input.size() != inputItems.size()) {
            return false;
        }

        for (int i = 0; i< inputItems.size(); i++) {
            if (!inputItems.get(i).test(input.getItem(i))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return inputItems;
    }

    @Override
    public ItemStack assemble(NbtInput input, HolderLookup.Provider registries) {
        return output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return output;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return MGsOdditiesRecipeSerializer.NBT_RECIPE_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return MGsOdditiesRecipe.NBT_RECIPE.get();
    }

    public NonNullList<Ingredient> getInputItems() {
        return inputItems;
    }

    public ItemStack getOutput() {
        return output;
    }
}