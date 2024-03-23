package tv.alterNERD.VaultModTweaks.integration.mixin;

import iskallia.vault.block.entity.TransmogTableTileEntity;
import iskallia.vault.container.TransmogTableContainer;
import iskallia.vault.container.inventory.TransmogTableInventory;
import iskallia.vault.container.oversized.OverSizedSlotContainer;
import iskallia.vault.container.oversized.OverSizedTabSlot;
import iskallia.vault.container.slot.TabSlot;
import iskallia.vault.container.spi.AbstractElementContainer;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModSlotIcons;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

/**
 * Modifies the {@link TransmogTableContainer} to allow Vault Silver, Gold and Platinum to be used in the Transmogrification Table.
 */
@Mixin(TransmogTableContainer.class)
public abstract class MixinTransmogTableContainer extends OverSizedSlotContainer
{
    //region Shadow Fields

    /**
     * Shadows the {@link TransmogTableInventory} inventory field in the {@link TransmogTableTileEntity}.
     */
    @Shadow
    protected TransmogTableInventory internalInventory;

    /**
     * Shadows the {@link AbstractElementContainer.SlotIndexRange} internalInventoryIndexRange field in the {@link TransmogTableContainer}.
     */
    @Shadow
    protected AbstractElementContainer.SlotIndexRange internalInventoryIndexRange;

    //endregion

    //region Shadow Methods

    /**
     * Shadows the {@link TransmogTableContainer#copperCost()} method in the {@link TransmogTableContainer}.
     * @return Returns the copper cost of transmogrification.
     */
    @Shadow
    public int copperCost()
    {
        return 0;
    }

    //endregion

    //region Initialisation

    /**
     * A constructor for the {@link MixinTransmogTableContainer}.
     * @param menuType Passing through the {@link MenuType} menuType as an argument.
     * @param id Passing through the {@link Integer} id as an argument.
     * @param player Passing through the {@link Player} player as an argument.
     */
    protected MixinTransmogTableContainer(MenuType<?> menuType, int id, Player player)
    {
        super(menuType, id, player);
    }

    //endregion

    //region Mixins

    /**
     * Modify the inventory slot Y position to allow space for Vault Silver, Gold and Platinum.
     * @param container Passing through the {@link TransmogTableContainer} container as an argument.
     * @param index Passing through the {@link Integer} index as an argument.
     * @param x Passing through the {@link Integer} x as an argument.
     * @param y Passing through the {@link Integer} y as an argument.
     * @return Returns the modified {@link TabSlot} inventory slots.
     */
    @Redirect(method = "initInventory", at = @At(value = "NEW", target = "(Lnet/minecraft/world/Container;III)Liskallia/vault/container/slot/TabSlot;", ordinal = 0), remap = false)
    TabSlot ModifyInventoryTabSlotsYPosition(Container container, int index, int x, int y)
    {
        return new TabSlot(container, index, x, y + 23);
    }

    /**
     * Modify the hotbar slot Y position to allow space for Vault Silver, Gold and Platinum.
     * @param container Passing through the {@link TransmogTableContainer} container as an argument.
     * @param index Passing through the {@link Integer} index as an argument.
     * @param x Passing through the {@link Integer} x as an argument.
     * @param y Passing through the {@link Integer} y as an argument.
     * @return Returns the modified {@link TabSlot} hotbar slots.
     */
    @Redirect(method = "initInventory", at = @At(value = "NEW", target = "(Lnet/minecraft/world/Container;III)Liskallia/vault/container/slot/TabSlot;", ordinal = 1), remap = false)
    TabSlot ModifyHotbarTabSlotsYPosition(Container container, int index, int x, int y)
    {
        return new TabSlot(container, index, x, y + 23);
    }

    /**
     * Adds the Vault Silver, Gold and Platinum to the {@link TransmogTableContainer}.
     * @param callbackInformation The {@link CallbackInfo} for the injection.
     * @param offsetX Passing through the {@link Integer} offsetX as an argument.
     * @param offsetY Passing through the {@link Integer} offsetY as an argument.
     * @param containerSlotIndex Passing through the {@link Integer} containerSlotIndex as an argument.
     */
    @Inject(method = "initInventory", at = @At(value = "INVOKE", target = "Liskallia/vault/container/TransmogTableContainer;addSlot(Lnet/minecraft/world/inventory/Slot;)Lnet/minecraft/world/inventory/Slot;", ordinal = 3, shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
    void InjectAdditionalCoinSlots(CallbackInfo callbackInformation, int offsetX, int offsetY, int containerSlotIndex)
    {
        addSlot((new OverSizedTabSlot(internalInventory, 2, 72, 84) {
            public void setChanged()
            {
                super.setChanged();

                slotsChanged(container);
            }
        }).setFilter((itemStack) -> itemStack.getItem() == ModBlocks.VAULT_SILVER).setBackground(InventoryMenu.BLOCK_ATLAS, ModSlotIcons.COINS_NO_ITEM));

        ++containerSlotIndex;

        addSlot((new OverSizedTabSlot(internalInventory, 3, 92, 84) {
            public void setChanged()
            {
                super.setChanged();

                slotsChanged(container);
            }
        }).setFilter((itemStack) -> itemStack.getItem() == ModBlocks.VAULT_GOLD).setBackground(InventoryMenu.BLOCK_ATLAS, ModSlotIcons.COINS_NO_ITEM));

        ++containerSlotIndex;

        addSlot((new OverSizedTabSlot(internalInventory, 4, 112, 84) {
            public void setChanged()
            {
                super.setChanged();

                slotsChanged(container);
            }
        }).setFilter((itemStack) -> itemStack.getItem() == ModBlocks.VAULT_PLATINUM).setBackground(InventoryMenu.BLOCK_ATLAS, ModSlotIcons.COINS_NO_ITEM));

        ++containerSlotIndex;
    }

    /**
     * Modifies the {@link TransmogTableContainer} to have slots for Vault Silver, Gold and Platinum.
     * @param hotbarRangeEnd Passing through the {@link Integer} hotbarRangeEnd as an argument.
     * @param containerSlotIndex Passing through the {@link Integer} containerSlotIndex as an argument.
     * @return Returns the modified {@link AbstractElementContainer.SlotIndexRange} with the new slots.
     */
    @Redirect(method = "initInventory", at = @At(value = "NEW", target = "(II)Liskallia/vault/container/spi/AbstractElementContainer$SlotIndexRange;", ordinal = 2), remap = false)
    AbstractElementContainer.SlotIndexRange ModifySlotIndexRange(int hotbarRangeEnd, int containerSlotIndex)
    {
        return new AbstractElementContainer.SlotIndexRange(hotbarRangeEnd, containerSlotIndex + 3);
    }

    /**
     * Injects a check to see if the price has been fulfilled when considering Vault Bronze, Silver, Gold and Platinum.
     * @param callbackInformationReturnable The {@link CallbackInfoReturnable} for the injection.
     */
    @Inject(method = "priceFulfilled", at = @At("HEAD"), cancellable = true, remap = false)
    void InjectPriceFulfilled(CallbackInfoReturnable<Boolean> callbackInformationReturnable)
    {
        Slot bronzeSlot = getSlot(internalInventoryIndexRange.getContainerIndex(1));
        Slot silverSlot = getSlot(internalInventoryIndexRange.getContainerIndex(2));
        Slot goldSlot = getSlot(internalInventoryIndexRange.getContainerIndex(3));
        Slot platinumSlot = getSlot(internalInventoryIndexRange.getContainerIndex(4));

        int bronzeCount = (bronzeSlot.hasItem() && bronzeSlot.getItem().is(ModBlocks.VAULT_BRONZE)) ? bronzeSlot.getItem().getCount() : 0;
        int silverCount = (silverSlot.hasItem() && silverSlot.getItem().is(ModBlocks.VAULT_SILVER)) ? silverSlot.getItem().getCount() : 0;
        int goldCount = (goldSlot.hasItem() && goldSlot.getItem().is(ModBlocks.VAULT_GOLD)) ? goldSlot.getItem().getCount() : 0;
        int platinumCount = (platinumSlot.hasItem() && platinumSlot.getItem().is(ModBlocks.VAULT_PLATINUM)) ? platinumSlot.getItem().getCount() : 0;

        callbackInformationReturnable.setReturnValue((bronzeCount + (silverCount * 9) + (goldCount * 81) + (platinumCount * 729)) >= copperCost());
        callbackInformationReturnable.cancel();
    }

    //endregion
}