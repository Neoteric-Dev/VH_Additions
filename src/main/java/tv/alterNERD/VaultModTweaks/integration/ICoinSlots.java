package tv.alterNERD.VaultModTweaks.integration;

import net.minecraft.world.inventory.Slot;

/**
 * Add methods to retrieve the silver, gold and platinum slots.
 */
public interface ICoinSlots
{
    /**
     * Get the bronze slot.
     * @return Returns the bronze slot.
     */
    public Slot getBronzeSlot();

    /**
     * Get the silver slot.
     * @return Returns the silver slot.
     */
    public Slot getSilverSlot();

    /**
     * Get the gold slot.
     * @return Returns the silver slot.
     */
    public Slot getGoldSlot();

    /**
     * Get the platinum slot.
     * @return Returns the silver slot.
     */
    public Slot getPlatinumSlot();
}