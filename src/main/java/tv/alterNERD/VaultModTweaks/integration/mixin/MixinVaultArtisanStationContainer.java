package tv.alterNERD.VaultModTweaks.integration.mixin;

import iskallia.vault.container.VaultArtisanStationContainer;
import iskallia.vault.container.oversized.OverSizedSlotContainer;
import iskallia.vault.container.oversized.OverSizedTabSlot;
import iskallia.vault.init.*;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import tv.alterNERD.VaultModTweaks.integration.ICoinSlots;

/**
 * Modifies the {@link VaultArtisanStationContainer} to modify the slot placement.
 */
@Mixin(VaultArtisanStationContainer.class)
@Implements(@Interface(iface = ICoinSlots.class, prefix = "coinSlot$"))
public abstract class MixinVaultArtisanStationContainer extends OverSizedSlotContainer
{
    //region Shadow Members

    /**
     * Shadows the {@link VaultArtisanStationContainer#getBronzeSlot()} member in the {@link VaultArtisanStationContainer} to be accessed locally.
     * @return The {@link Slot} return for the shadowed member.
     */
    @Shadow
    public abstract Slot getBronzeSlot();

    //endregion

    //region Getters

    /**
     * Intrinsically adds the {@link VaultArtisanStationContainer#getBronzeSlot()} member.
     * @return The {@link Slot} return for the intrinsic member.
     */
    @Intrinsic(displace = true)
    public Slot coinSlot$getBronzeSlot()
    {
        return getBronzeSlot();
    }

    /**
     * Get the slot for the silver coin.
     * @return Returns the slot for the silver coin.
     */
    public Slot coinSlot$getSilverSlot()
    {
        return slots.get(38);
    }

    /**
     * Get the slot for the gold coin.
     * @return Returns the slot for the gold coin.
     */
    public Slot coinSlot$getGoldSlot()
    {
        return slots.get(39);
    }

    /**
     * Get the slot for the platinum coin.
     * @return Returns the slot for the platinum coin.
     */
    public Slot coinSlot$getPlatinumSlot()
    {
        return slots.get(40);
    }

    //endregion

    //region Initialisation

    /**
     * The constructor for {@link VaultArtisanStationContainer}.
     * @param windowID Passing through the {@link Integer} windowId as an argument.
     * @param playerInventory Passing through the {@link Inventory} playerInventory as an argument.
     */
    public MixinVaultArtisanStationContainer(int windowID, Inventory playerInventory)
    {
        super(ModContainers.VAULT_ARTISAN_STATION_CONTAINER, windowID, playerInventory.player);
    }

    //endregion

    //region Mixins

    /**
     * Modifies the {@link Integer} X argument of the {@link OverSizedTabSlot} used for plating in the {@link VaultArtisanStationContainer#initSlots(Inventory)} member.
     * @param x The {@link Integer} X to modify.
     * @return Returns the modified {@link Integer} X.
     */
    @ModifyArg(method = "initSlots", at = @At(value = "INVOKE", target = "Liskallia/vault/container/oversized/OverSizedTabSlot;<init>(Lnet/minecraft/world/Container;III)V", ordinal = 0), index = 2)
    int ModifyPlatingSlotX(int x)
    {
        return 79;
    }

    /**
     * Modifies the {@link Integer} Y argument of the {@link OverSizedTabSlot} used for plating in the {@link VaultArtisanStationContainer#initSlots(Inventory)} member.
     * @param y The {@link Integer} Y to modify.
     * @return Returns the modified {@link Integer} Y.
     */
    @ModifyArg(method = "initSlots", at = @At(value = "INVOKE", target = "Liskallia/vault/container/oversized/OverSizedTabSlot;<init>(Lnet/minecraft/world/Container;III)V", ordinal = 0), index = 3)
    int ModifyPlatingSlotY(int y)
    {
        return 60;
    }

    /**
     * Modifies the {@link Integer} X argument of the {@link OverSizedTabSlot} used for bronze in the {@link VaultArtisanStationContainer#initSlots(Inventory)} member.
     * @param x The {@link Integer} X to modify.
     * @return Returns the modified {@link Integer} X.
     */
    @ModifyArg(method = "initSlots", at = @At(value = "INVOKE", target = "Liskallia/vault/container/oversized/OverSizedTabSlot;<init>(Lnet/minecraft/world/Container;III)V", ordinal = 1), index = 2)
    int ModifyBronzeSlotX(int x)
    {
        return 69;
    }

    /**
     * Modifies the {@link Integer} Y argument of the {@link OverSizedTabSlot} used for bronze in the {@link VaultArtisanStationContainer#initSlots(Inventory)} member.
     * @param y The {@link Integer} Y to modify.
     * @return Returns the modified {@link Integer} Y.
     */
    @ModifyArg(method = "initSlots", at = @At(value = "INVOKE", target = "Liskallia/vault/container/oversized/OverSizedTabSlot;<init>(Lnet/minecraft/world/Container;III)V", ordinal = 1), index = 3)
    int ModifyBronzeSlotY(int y)
    {
        return 20;
    }

    /**
     * Modifies the {@link Integer} index argument of the {@link OverSizedTabSlot} used for the reforge all modifiers in the {@link VaultArtisanStationContainer#initSlots(Inventory)} member.
     * @param index The {@link Integer} index to modify.
     * @return Returns the modified {@link Integer} index.
     */
    @ModifyArg(method = "initSlots", at = @At(value = "INVOKE", target = "Liskallia/vault/container/oversized/OverSizedTabSlot;<init>(Lnet/minecraft/world/Container;III)V", ordinal = 2), index = 1)
    int ModifyReforgeAllModifiersSlot(int index)
    {
        return 5;
    }

    /**
     * Modifies the {@link Integer} index argument of the {@link OverSizedTabSlot} used for the add modifier in the {@link VaultArtisanStationContainer#initSlots(Inventory)} member.
     * @param index The {@link Integer} index to modify.
     * @return Returns the modified {@link Integer} index.
     */
    @ModifyArg(method = "initSlots", at = @At(value = "INVOKE", target = "Liskallia/vault/container/oversized/OverSizedTabSlot;<init>(Lnet/minecraft/world/Container;III)V", ordinal = 3), index = 1)
    int ModifyAddModifierSlot(int index)
    {
        return 6;
    }

    /**
     * Modifies the {@link Integer} index argument of the {@link OverSizedTabSlot} used for the remove modifier in the {@link VaultArtisanStationContainer#initSlots(Inventory)} member.
     * @param index The {@link Integer} index to modify.
     * @return Returns the modified {@link Integer} index.
     */
    @ModifyArg(method = "initSlots", at = @At(value = "INVOKE", target = "Liskallia/vault/container/oversized/OverSizedTabSlot;<init>(Lnet/minecraft/world/Container;III)V", ordinal = 4), index = 1)
    int ModifyRemoveModifierSlot(int index)
    {
        return 7;
    }

    /**
     * Modifies the {@link Integer} index argument of the {@link OverSizedTabSlot} used for the reforge all add tag in the {@link VaultArtisanStationContainer#initSlots(Inventory)} member.
     * @param index The {@link Integer} index to modify.
     * @return Returns the modified {@link Integer} index.
     */
    @ModifyArg(method = "initSlots", at = @At(value = "INVOKE", target = "Liskallia/vault/container/oversized/OverSizedTabSlot;<init>(Lnet/minecraft/world/Container;III)V", ordinal = 5), index = 1)
    int ModifyReforgeAllAddTagSlot(int index)
    {
        return 8;
    }

    /**
     * Modifies the {@link Integer} index argument of the {@link OverSizedTabSlot} used for the reforge potential in the {@link VaultArtisanStationContainer#initSlots(Inventory)} member.
     * @param index The {@link Integer} index to modify.
     * @return Returns the modified {@link Integer} index.
     */
    @ModifyArg(method = "initSlots", at = @At(value = "INVOKE", target = "Liskallia/vault/container/oversized/OverSizedTabSlot;<init>(Lnet/minecraft/world/Container;III)V", ordinal = 6), index = 1)
    int ModifyReforgePotentialSlot(int index)
    {
        return 9;
    }

    /**
     * Modifies the {@link Integer} index argument of the {@link OverSizedTabSlot} used for the reforge repair slots in the {@link VaultArtisanStationContainer#initSlots(Inventory)} member.
     * @param index The {@link Integer} index to modify.
     * @return Returns the modified {@link Integer} index.
     */
    @ModifyArg(method = "initSlots", at = @At(value = "INVOKE", target = "Liskallia/vault/container/oversized/OverSizedTabSlot;<init>(Lnet/minecraft/world/Container;III)V", ordinal = 7), index = 1)
    int ModifyReforgeRepairSlotsSlot(int index)
    {
        return 10;
    }

    /**
     * Modifies the {@link Integer} index argument of the {@link OverSizedTabSlot} used for the reforge all implicits in the {@link VaultArtisanStationContainer#initSlots(Inventory)} member.
     * @param index The {@link Integer} index to modify.
     * @return Returns the modified {@link Integer} index.
     */
    @ModifyArg(method = "initSlots", at = @At(value = "INVOKE", target = "Liskallia/vault/container/oversized/OverSizedTabSlot;<init>(Lnet/minecraft/world/Container;III)V", ordinal = 8), index = 1)
    int ModifyReforgeAllImplicitsSlot(int index)
    {
        return 11;
    }

    /**
     * Modifies the {@link Integer} index argument of the {@link OverSizedTabSlot} used for the reforge random tier in the {@link VaultArtisanStationContainer#initSlots(Inventory)} member.
     * @param index The {@link Integer} index to modify.
     * @return Returns the modified {@link Integer} index.
     */
    @ModifyArg(method = "initSlots", at = @At(value = "INVOKE", target = "Liskallia/vault/container/oversized/OverSizedTabSlot;<init>(Lnet/minecraft/world/Container;III)V", ordinal = 9), index = 1)
    int ModifyReforgeRandomTierSlot(int index)
    {
        return 12;
    }

    /**
     * Modifies the {@link Integer} index argument of the {@link OverSizedTabSlot} used for the reforge prefixes in the {@link VaultArtisanStationContainer#initSlots(Inventory)} member.
     * @param index The {@link Integer} index to modify.
     * @return Returns the modified {@link Integer} index.
     */
    @ModifyArg(method = "initSlots", at = @At(value = "INVOKE", target = "Liskallia/vault/container/oversized/OverSizedTabSlot;<init>(Lnet/minecraft/world/Container;III)V", ordinal = 10), index = 1)
    int ModifyReforgePrefixesSlot(int index)
    {
        return 13;
    }

    /**
     * Modifies the {@link Integer} index argument of the {@link OverSizedTabSlot} used for the reforge suffixes in the {@link VaultArtisanStationContainer#initSlots(Inventory)} member.
     * @param index The {@link Integer} index to modify.
     * @return Returns the modified {@link Integer} index.
     */
    @ModifyArg(method = "initSlots", at = @At(value = "INVOKE", target = "Liskallia/vault/container/oversized/OverSizedTabSlot;<init>(Lnet/minecraft/world/Container;III)V", ordinal = 11), index = 1)
    int ModifyReforgeSuffixesSlot(int index)
    {
        return 14;
    }

    /**
     * Modifies the {@link Integer} Y argument of the {@link OverSizedTabSlot} used for the gear in the {@link VaultArtisanStationContainer#initSlots(Inventory)} member.
     * @param y The {@link Integer} Y to modify.
     * @return Returns the modified {@link Integer} Y.
     */
    @ModifyConstant(method = "initSlots", constant = @Constant(intValue = 72, ordinal = 0), remap = false)
    int ModifyGearSlotY(int y)
    {
        return 90;
    }

    /**
     * Inject new {@link OverSizedTabSlot} slots for Silver, Gold and Platinum Coins into the {@link VaultArtisanStationContainer#initSlots(Inventory)} member.
     * @param callbackInformation The {@link CallbackInfo} for the injection.
     * @param invContainer Passing in the local variable {@link Container} invContainer as an argument.
     */
    @Inject(method = "initSlots", at = @At(value = "INVOKE", target = "Liskallia/vault/container/VaultArtisanStationContainer;addModSlot(Liskallia/vault/container/oversized/OverSizedTabSlot;Liskallia/vault/gear/modification/GearModification;Z)V", ordinal = 0), locals = LocalCapture.CAPTURE_FAILHARD, remap = false)
    void InjectAdditionalCoinSlots(Inventory playerInventory, CallbackInfo callbackInformation, Container invContainer)
    {
        // Added slot for Silver Coins.
        addSlot((new OverSizedTabSlot(invContainer, 2, 89, 20)).setFilter((stack) -> stack.is(ModBlocks.VAULT_SILVER)).setBackground(InventoryMenu.BLOCK_ATLAS, ModSlotIcons.COINS_NO_ITEM));

        // Added slot for Gold Coins.
        addSlot((new OverSizedTabSlot(invContainer, 3, 69, 40)).setFilter((stack) -> stack.is(ModBlocks.VAULT_GOLD)).setBackground(InventoryMenu.BLOCK_ATLAS, ModSlotIcons.COINS_NO_ITEM));

        // Added slot for Platinum Coins.
        addSlot((new OverSizedTabSlot(invContainer, 4, 89, 40)).setFilter((stack) -> stack.is(ModBlocks.VAULT_PLATINUM)).setBackground(InventoryMenu.BLOCK_ATLAS, ModSlotIcons.COINS_NO_ITEM));
    }

    //endregion
}
