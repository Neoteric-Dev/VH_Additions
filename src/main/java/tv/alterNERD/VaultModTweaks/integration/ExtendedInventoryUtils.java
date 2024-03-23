package tv.alterNERD.VaultModTweaks.integration;

import iskallia.vault.container.oversized.OverSizedInventory;
import iskallia.vault.container.oversized.OverSizedItemStack;
import iskallia.vault.util.InventoryUtil;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Additional helper methods for similar to the helper methods found in {@link InventoryUtil}.
 */
public final class ExtendedInventoryUtils
{
    //region Helpers

    /**
     * A reimplementation of the {@link InventoryUtil#isEqualCrafting(ItemStack, ItemStack)} method in {@link InventoryUtil}.
     * @param thisStack The first {@link ItemStack} to compare.
     * @param thatStack The second {@link ItemStack} to compare.
     * @return Returns whether the two {@link ItemStack} are equal.
     */
    static boolean IsEqualCrafting(ItemStack thisStack, ItemStack thatStack)
    {
        return thisStack.getItem() == thatStack.getItem() && thisStack.getDamageValue() == thatStack.getDamageValue() && (thisStack.getTag() == null || thisStack.areShareTagsEqual(thatStack));
    }

    /**
     * A reimplementation of the {@link InventoryUtil#getMissingInputs(List, Inventory, OverSizedInventory)} method in {@link InventoryUtil}.
     * @param recipeInputs The required inputs of the recipe.
     * @param containerInventory The {@link OverSizedInventory} inventory of the container.
     * @return Returns the missing inputs for calculating the remaining cost of the recipe.
     */
    public static List<ItemStack> GetMissingInputs(List<ItemStack> recipeInputs, OverSizedInventory containerInventory)
    {
        List<ItemStack> missing = new ArrayList<>();

        for (ItemStack input : recipeInputs)
        {
            int neededCount = input.getCount();

            for (OverSizedItemStack overSized : containerInventory.getOverSizedContents())
            {
                if (IsEqualCrafting(input, overSized.stack()))
                {
                    neededCount -= overSized.amount();
                }
            }

            if (neededCount > 0)
            {
                missing.add(input);
            }
        }

        return missing;
    }

    //endregion
}