package tv.alterNERD.VaultModTweaks.integration.mixin;

import iskallia.vault.block.entity.VaultArtisanStationTileEntity;
import iskallia.vault.container.VaultArtisanStationContainer;
import iskallia.vault.container.oversized.OverSizedSlotContainer;
import iskallia.vault.container.oversized.OverSizedTabSlot;
import iskallia.vault.container.slot.TabSlot;
import iskallia.vault.gear.item.VaultGearItem;
import iskallia.vault.gear.modification.GearModification;
import iskallia.vault.gear.modification.GearModificationAction;
import iskallia.vault.init.*;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.*;
import tv.alterNERD.VaultModTweaks.integration.ICoinSlots;

import java.util.ArrayList;
import java.util.List;

/**
 * Modifies the {@link VaultArtisanStationContainer} to modify the slot placement.
 */
@Mixin(VaultArtisanStationContainer.class)
@Implements(@Interface(iface = ICoinSlots.class, prefix = "coinSlot$"))
public abstract class MixinVaultArtisanStationContainer extends OverSizedSlotContainer
{
    /**
     * Shadows the {@link List<GearModificationAction>} modificationActions field in the {@link VaultArtisanStationContainer} to be accessed locally.
     */
    @Final
    @Shadow
    private final List<GearModificationAction> modificationActions = new ArrayList();

    /**
     * Shadows the {@link VaultArtisanStationTileEntity} tileEntity field in the {@link VaultArtisanStationContainer} to be accessed locally.
     */
    @Shadow
    private final VaultArtisanStationTileEntity tileEntity;

    /**
     * Shadows the {@link VaultArtisanStationContainer#addModSlot(OverSizedTabSlot, GearModification, boolean)} member in the {@link VaultArtisanStationContainer} to be accessed locally.
     * @param slot The {@link OverSizedTabSlot} slot argument shadowed.
     * @param modification The {@link GearModification} modification argument shadowed.
     * @param rightSide The boolean rightside argument shadowed.
     */
    @Shadow
    private void addModSlot(OverSizedTabSlot slot, GearModification modification, boolean rightSide){}

    /**
     * A constructor for the mixin to initialise the {@link MixinVaultArtisanStationContainer#tileEntity} and handle the super.
     * @param tileEntity The tile entity of the artisan station.
     * @param windowId An ID for a window to be passed to the super.
     * @param playerInventory The {@link Inventory} of the interacting player to be passed to the super.
     */
    public MixinVaultArtisanStationContainer(VaultArtisanStationTileEntity tileEntity, int windowId, Inventory playerInventory)
    {
        super(ModContainers.VAULT_ARTISAN_STATION_CONTAINER, windowId, playerInventory.player);

        this.tileEntity = tileEntity;
    }

    /**
     * Get the slot for plating.
     * @return Returns the slot for plating.
     */
    public Slot getPlatingSlot()
    {
        return this.slots.get(36);
    }

    /**
     * Shadows the {@link VaultArtisanStationContainer#getBronzeSlot()} member in the {@link VaultArtisanStationContainer} to be accessed locally.
     * @return The {@link Slot} return for the shadowed member.
     */
    @Shadow
    public abstract Slot getBronzeSlot();

    /**
     * Intrinsically adds the {@link VaultArtisanStationContainer#getBronzeSlot()} member.
     * @return The {@link Slot} return for the intrinsic member.
     */
    @Intrinsic(displace = true)
    public Slot coinSlot$getBronzeSlot()
    {
        return this.getBronzeSlot();
    }

    /**
     * Get the slot for the silver coin.
     * @return Returns the slot for the silver coin.
     */
    public Slot coinSlot$getSilverSlot()
    {
        return this.slots.get(38);
    }

    /**
     * Get the slot for the gold coin.
     * @return Returns the slot for the gold coin.
     */
    public Slot coinSlot$getGoldSlot()
    {
        return this.slots.get(39);
    }

    /**
     * Get the slot for the platinum coin.
     * @return Returns the slot for the platinum coin.
     */
    public Slot coinSlot$getPlatinumSlot()
    {
        return this.slots.get(40);
    }

    /**
     * Overrides the {@link VaultArtisanStationContainer#initSlots(Inventory)} member to add additional slots.
     * @param playerInventory The {@link Inventory} of the interacting player.
     */
    private void initSlots(Inventory playerInventory)
    {
        VaultArtisanStationContainer vaultArtisanStationContainer = (VaultArtisanStationContainer)(Object)this;
        int hotbarSlot;

        for(hotbarSlot = 0; hotbarSlot < 3; ++hotbarSlot)
        {
            for(int column = 0; column < 9; ++column)
            {
                vaultArtisanStationContainer.addSlot(new TabSlot(playerInventory, column + hotbarSlot * 9 + 9, 8 + column * 18, 148 + hotbarSlot * 18));
            }
        }

        for(hotbarSlot = 0; hotbarSlot < 9; ++hotbarSlot)
        {
            vaultArtisanStationContainer.addSlot(new TabSlot(playerInventory, hotbarSlot, 8 + hotbarSlot * 18, 206));
        }

        // Modified slot for plating.
        Container invContainer = tileEntity.getInventory();
        vaultArtisanStationContainer.addSlot((new OverSizedTabSlot(invContainer, 0, 79, 60)).setFilter((stack) ->
        {
            return stack.is(ModItems.VAULT_PLATING);
        }).setBackground(InventoryMenu.BLOCK_ATLAS, ModSlotIcons.PLATING_NO_ITEM));

        // Modified slot for bronze coins.
        vaultArtisanStationContainer.addSlot((new OverSizedTabSlot(invContainer, 1, 69, 20)).setFilter((stack) ->
        {
            return stack.is(ModBlocks.VAULT_BRONZE);
        }).setBackground(InventoryMenu.BLOCK_ATLAS, ModSlotIcons.COINS_NO_ITEM));

        // Added slot for silver coins.
        vaultArtisanStationContainer.addSlot((new OverSizedTabSlot(invContainer, 2, 89, 20)).setFilter((stack) ->
        {
            return stack.is(ModBlocks.VAULT_SILVER);
        }).setBackground(InventoryMenu.BLOCK_ATLAS, ModSlotIcons.COINS_NO_ITEM));

        // Added slot for gold coins.
        vaultArtisanStationContainer.addSlot((new OverSizedTabSlot(invContainer, 3, 69, 40)).setFilter((stack) ->
        {
            return stack.is(ModBlocks.VAULT_GOLD);
        }).setBackground(InventoryMenu.BLOCK_ATLAS, ModSlotIcons.COINS_NO_ITEM));

        // Added slot for platinum coins.
        vaultArtisanStationContainer.addSlot((new OverSizedTabSlot(invContainer, 4, 89, 40)).setFilter((stack) ->
        {
            return stack.is(ModBlocks.VAULT_PLATINUM);
        }).setBackground(InventoryMenu.BLOCK_ATLAS, ModSlotIcons.COINS_NO_ITEM));

        addModSlot(new OverSizedTabSlot(invContainer, 5, 8, 20), ModGearModifications.REFORGE_ALL_MODIFIERS, true);
        addModSlot(new OverSizedTabSlot(invContainer, 6, 8, 44), ModGearModifications.ADD_MODIFIER, true);
        addModSlot(new OverSizedTabSlot(invContainer, 7, 8, 68), ModGearModifications.REMOVE_MODIFIER, true);
        addModSlot(new OverSizedTabSlot(invContainer, 8, 8, 92), ModGearModifications.REFORGE_ALL_ADD_TAG, true);
        addModSlot(new OverSizedTabSlot(invContainer, 9, 150, 20), ModGearModifications.RESET_POTENTIAL, false);
        addModSlot(new OverSizedTabSlot(invContainer, 10, 150, 44), ModGearModifications.REFORGE_REPAIR_SLOTS, false);
        addModSlot(new OverSizedTabSlot(invContainer, 11, 150, 68), ModGearModifications.REFORGE_ALL_IMPLICITS, false);
        addModSlot(new OverSizedTabSlot(invContainer, 12, 150, 92), ModGearModifications.REFORGE_RANDOM_TIER, false);
        addModSlot(new OverSizedTabSlot(invContainer, 13, 8, 116), ModGearModifications.REFORGE_PREFIXES, true);
        addModSlot(new OverSizedTabSlot(invContainer, 14, 150, 116), ModGearModifications.REFORGE_SUFFIXES, false);

        Container inputContainer = this.tileEntity.getGearInput();

        vaultArtisanStationContainer.addSlot(new TabSlot(inputContainer, 0, 79, 90)
        {
            public boolean mayPlace(ItemStack stack)
            {
                return stack.getItem() instanceof VaultGearItem && stack.getItem() != ModItems.JEWEL;
            }
        });
    }
}
