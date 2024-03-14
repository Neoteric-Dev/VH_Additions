package tv.alterNERD.VaultModTweaks.integration;

import net.minecraft.world.inventory.Slot;

/**
 * Add methods to retrieve the Vault Bronze, Silver, Gold and Platinum slots.
 */
public interface ICoinSlots
{
    //region Getters

    /**
     * Get the Vault Bronze slot.
     * @return Returns the Vault Bronze slot.
     */
    Slot getBronzeSlot();

    /**
     * Get the Vault Silver slot.
     * @return Returns the Vault Silver slot.
     */
    Slot getSilverSlot();

    /**
     * Get the Vault Gold slot.
     * @return Returns the Vault Gold slot.
     */
    Slot getGoldSlot();

    /**
     * Get the Vault Platinum slot.
     * @return Returns the Vault Platinum slot.
     */
    Slot getPlatinumSlot();

    //endregion
}