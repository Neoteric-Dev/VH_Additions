package tv.alterNERD.VaultModTweaks.integration;

import iskallia.vault.container.oversized.OverSizedInventory;

/**
 * Add a method to retrieve an {@link OverSizedInventory}.
 */
public interface IOverSizedInventory
{
    //region Getters

    /**
     * Get the {@link OverSizedInventory}.
     * @return Returns the {@link OverSizedInventory}.
     */
    OverSizedInventory getOverSizedInventory();

    //endregion
}