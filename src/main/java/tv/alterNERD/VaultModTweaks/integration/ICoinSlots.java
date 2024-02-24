package tv.alterNERD.VaultModTweaks.integration;

import net.minecraft.world.inventory.Slot;

/**
 * Add methods to retrieve the Bronze, Silver, Gold and Platinum slots.
 */
public interface ICoinSlots
{
    //region Getters

    /**
     * Get the Bronze slot.
     * @return Returns the Bronze slot.
     */
    Slot getBronzeSlot();

    /**
     * Get the Silver slot.
     * @return Returns the Silver slot.
     */
    Slot getSilverSlot();

    /**
     * Get the Gold slot.
     * @return Returns the Gold slot.
     */
    Slot getGoldSlot();

    /**
     * Get the Platinum slot.
     * @return Returns the Platinum slot.
     */
    Slot getPlatinumSlot();

    //endregion
}