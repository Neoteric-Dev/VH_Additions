package tv.alterNERD.VaultModTweaks.integration.mixin;

import iskallia.vault.block.entity.ModifierWorkbenchTileEntity;
import iskallia.vault.config.gear.VaultGearWorkbenchConfig;
import iskallia.vault.container.ModifierWorkbenchContainer;
import iskallia.vault.container.VaultArtisanStationContainer;
import iskallia.vault.container.oversized.OverSizedInventory;
import iskallia.vault.container.oversized.OverSizedTabSlot;
import iskallia.vault.container.spi.AbstractElementContainer;
import iskallia.vault.gear.VaultGearState;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModContainers;
import iskallia.vault.init.ModItems;
import iskallia.vault.init.ModSlotIcons;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tv.alterNERD.VaultModTweaks.VaultModTweaks;
import tv.alterNERD.VaultModTweaks.integration.IOverSizedInventory;

import javax.annotation.Nullable;

/**
 * Modifies the {@link ModifierWorkbenchContainer} to add slots for Vault Gold, Nullifying Focus and Amplifying Focus.
 */
@Mixin(ModifierWorkbenchContainer.class)
public abstract class MixinModifierWorkbenchContainer extends AbstractElementContainer
{
    //region Shadow Fields

    /**
     * Shadow in the {@link ModifierWorkbenchTileEntity} tileEntity field to be accessed locally.
     */
    @Shadow
    private final ModifierWorkbenchTileEntity tileEntity;

    //endregion

    //region Initialisation

    /**
     * A constructor for the {@link MixinModifierWorkbenchContainer}.
     * @param menuType Passing through the {@link MenuType} menuType as an argument.
     * @param windowID Passing through the {@link Integer} windowID as an argument.
     * @param player Passing through the {@link Player} player as an argument.
     */
    public MixinModifierWorkbenchContainer(MenuType<?> menuType, int windowID, Player player)
    {
        super(ModContainers.MODIFIER_WORKBENCH_CONTAINER, windowID, player);

        tileEntity = ((ModifierWorkbenchContainer)(Object)this).getTileEntity();
    }

    //endregion

    //region Mixins

    /**
     * Inject new {@link OverSizedTabSlot} slots for Silver Coins, Nullifying Focus and Amplifying Focus into the {@link VaultArtisanStationContainer#initSlots(Inventory)} member.
     * @param callbackInformation The {@link CallbackInfo} for the injection.
     */
    @Inject(method = "initSlots", at = @At(value = "INVOKE", target = "Liskallia/vault/container/ModifierWorkbenchContainer;addSlot(Lnet/minecraft/world/inventory/Slot;)Lnet/minecraft/world/inventory/Slot;", ordinal = 2, shift = At.Shift.AFTER), remap = false)
    void InjectAdditionalSlots(CallbackInfo callbackInformation)
    {
        OverSizedInventory overSizedInventory = ((IOverSizedInventory)tileEntity).getOverSizedInventory();

        // Added slot for Gold Coins.
        addSlot(new OverSizedTabSlot(overSizedInventory, 0, 143, 20).setFilter((stack) -> stack.is(ModBlocks.VAULT_GOLD)).setBackground(InventoryMenu.BLOCK_ATLAS, ModSlotIcons.COINS_NO_ITEM));

        // Added slot for Nullifying Focus.
        addSlot(new OverSizedTabSlot(overSizedInventory, 1, 143, 40).setFilter((stack) -> stack.is(ModItems.NULLIFYING_FOCUS)));

        // Added slot for Amplifying Focus.
        addSlot(new OverSizedTabSlot(overSizedInventory, 2, 143, 60).setFilter((stack) -> stack.is(ModItems.AMPLIFYING_FOCUS)));
    }

    /**
     * Modifies an argument of the {@link Slot} used for the gear slot in the {@link VaultArtisanStationContainer#initSlots(Inventory)} member.
     * @param slot The {@link Slot} gear slot to modify.
     * @return Returns the modified {@link Slot} gear slot.
     */
    @ModifyArg(method = "initSlots", at = @At(value = "INVOKE", target = "Liskallia/vault/container/ModifierWorkbenchContainer;addSlot(Lnet/minecraft/world/inventory/Slot;)Lnet/minecraft/world/inventory/Slot;", ordinal = 2), index = 0)
    Slot ModifySlot(Slot slot)
    {
        return new Slot(tileEntity.getInventory(), 0, 143, 80)
        {
            public boolean mayPlace(ItemStack stack)
            {
                if (stack.isEmpty())
                {
                    return false;
                }

                else if (VaultGearWorkbenchConfig.getConfig(stack.getItem()).isEmpty())
                {
                    return false;
                }

                VaultGearData data = VaultGearData.read(stack);

                return data.getState() == VaultGearState.IDENTIFIED;
            }
        };
    }

    /**
     * Modifies the {@link ModifierWorkbenchContainer#moveItemStackTo(ItemStack, int, int, boolean)} member to use the {@link MixinModifierWorkbenchContainer#MoveOverSizedItemStackTo(ItemStack, int, int, boolean)} member.
     * In this instance the end index is set to the size of the {@link ModifierWorkbenchContainer#slots} slots collection.
     * @param instance Passing through the {@link ModifierWorkbenchContainer} instance as an argument.
     * @param sourceStack Passing through the {@link ItemStack} sourceStack as an argument.
     * @param startIndex Passing through the {@link Integer} startIndex as an argument.
     * @param endIndex Passing through the {@link Integer} endIndex as an argument.
     * @param reverseDirection Passing through the {@link Boolean} reverseDirection as an argument.
     * @return Returns the modified outcome.
     */
    @Redirect(method = "quickMoveStack", at = @At(value = "INVOKE", target = "Liskallia/vault/container/ModifierWorkbenchContainer;moveItemStackTo(Lnet/minecraft/world/item/ItemStack;IIZ)Z", ordinal = 0))
    boolean InjectMoveOverSizedItemStackToOrdinalZero(ModifierWorkbenchContainer instance, ItemStack sourceStack, int startIndex, int endIndex, boolean reverseDirection)
    {
        return MoveOverSizedItemStackTo(sourceStack, startIndex, slots.size(), reverseDirection);
    }

    /**
     * Modifies the {@link ModifierWorkbenchContainer#moveItemStackTo(ItemStack, int, int, boolean)} member to use the {@link MixinModifierWorkbenchContainer#MoveOverSizedItemStackTo(ItemStack, int, int, boolean)} member.
     * @param instance Passing through the {@link ModifierWorkbenchContainer} instance as an argument.
     * @param sourceStack Passing through the {@link ItemStack} sourceStack as an argument.
     * @param startIndex Passing through the {@link Integer} startIndex as an argument.
     * @param endIndex Passing through the {@link Integer} endIndex as an argument.
     * @param reverseDirection Passing through the {@link Boolean} reverseDirection as an argument.
     * @return Returns the modified outcome.
     */
    @Redirect(method = "quickMoveStack", at = @At(value = "INVOKE", target = "Liskallia/vault/container/ModifierWorkbenchContainer;moveItemStackTo(Lnet/minecraft/world/item/ItemStack;IIZ)Z"), slice = @Slice(from = @At(value = "INVOKE", target = "Liskallia/vault/container/ModifierWorkbenchContainer;moveItemStackTo(Lnet/minecraft/world/item/ItemStack;IIZ)Z", ordinal = 1), to = @At(value = "INVOKE", target = "Liskallia/vault/container/ModifierWorkbenchContainer;moveItemStackTo(Lnet/minecraft/world/item/ItemStack;IIZ)Z", ordinal = 3)))
    boolean ModifyMoveOverSizedItemStack(ModifierWorkbenchContainer instance, ItemStack sourceStack, int startIndex, int endIndex, boolean reverseDirection)
    {
        return MoveOverSizedItemStackTo(sourceStack, startIndex, endIndex, reverseDirection);
    }

    //endregion

    //region Helpers

    /**
     * A (better) reimplementation of the MoveOverSizedItemStackTo method found in other work stations.
     * @param sourceStack The source stack being moved.
     * @param startIndex The start index for searching for a free slot.
     * @param endIndex The end index for searching for a free slot.
     * @param reverseDirection A flag to determine whether to search the indexes in reverse.
     * @return Returns whether the move was successful.
     */
    boolean MoveOverSizedItemStackTo(ItemStack sourceStack, int startIndex, int endIndex, boolean reverseDirection)
    {
        boolean flag = false;

        for (int i = reverseDirection ? endIndex : startIndex; reverseDirection ? i > startIndex : i < endIndex; i += (reverseDirection ? -1 : 1))
        {
            if (sourceStack.isEmpty())
            {
                break;
            }

            Slot currentSlot = slots.get(i);
            ItemStack currentSlotStack = currentSlot.getItem();

            if (!currentSlotStack.isEmpty() && currentSlotStack.getItem() == sourceStack.getItem() && ItemStack.tagMatches(currentSlotStack, sourceStack) && currentSlot.mayPlace(sourceStack))
            {
                int maximumStackSize;

                if (currentSlot instanceof OverSizedTabSlot)
                {
                    maximumStackSize = currentSlotStack.getCount() + sourceStack.getCount();
                }

                else
                {
                    maximumStackSize = currentSlot.getMaxStackSize(currentSlotStack);
                }

                currentSlotStack.grow(sourceStack.split(maximumStackSize - currentSlotStack.getCount()).getCount());
                currentSlot.set(currentSlotStack);
                currentSlot.setChanged();

                flag = true;
            }
        }

        for (int i = reverseDirection ? endIndex : startIndex; reverseDirection ? i > startIndex : i < endIndex; i += (reverseDirection ? -1 : 1))
        {
            if (sourceStack.isEmpty())
            {
                break;
            }

            Slot currentSlot = slots.get(i);
            ItemStack currentSlotStack = currentSlot.getItem();

            if (currentSlotStack.isEmpty() && currentSlot.mayPlace(sourceStack))
            {
                currentSlot.set(sourceStack.split(currentSlotStack.getMaxStackSize()));
                currentSlot.setChanged();

                flag = true;
            }
        }

        return flag;
    }

    //endregion
}