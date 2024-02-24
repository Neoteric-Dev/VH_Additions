package tv.alterNERD.VaultModTweaks.integration.mixin;

import iskallia.vault.container.VaultArtisanStationContainer;
import iskallia.vault.gear.attribute.type.VaultGearAttributeTypeMerger;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.gear.modification.GearModification;
import iskallia.vault.gear.modification.GearModificationAction;
import iskallia.vault.gear.modification.GearModificationCost;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModGearAttributes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import tv.alterNERD.VaultModTweaks.integration.ICoinSlots;

import java.util.Optional;
import java.util.Random;

/**
 * Modifies the {@link GearModificationAction} to accept Silver, Gold and Platinum Coins when applying modifications.
 */
@Mixin(GearModificationAction.class)
public abstract class MixinGearModificationAction
{
    //region Shadow Fields

    /**
     * Shadow the {@link Random} instance in the {@link GearModificationAction} to be accessed locally.
     */
    @Shadow
    private static final Random rand = new Random();

    //endregion

    //region Shadow Members

    /**
     * Shadows the {@link VaultArtisanStationContainer#getGearInputSlot()} member to be accessed locally.
     * @return The {@link GearModificationAction} return for the shadowed member.
     */
    @Shadow
    public abstract GearModification modification();

    //endregion

    //region Mixins

    /**
     * Removes the bronze shrink to allow it to be replaced entirely with {@link MixinGearModificationAction#InjectSpendCoins(VaultArtisanStationContainer, ServerPlayer, CallbackInfo, ItemStack, VaultGearData, Optional, Slot, ItemStack, ItemStack, String, GearModificationCost, ItemStack)} )}
     * @param instance The {@link ItemStack} instance to be ignored.
     * @param bronzeCost The {@link Integer} bronzeCost to be ignored.
     */
    @Redirect(method = "apply", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;shrink(I)V", ordinal = 1))
    void RemoveBronzeShrink(ItemStack instance, int bronzeCost) {}

    /**
     * Injects new method to handle spending of coins, including Silver, Gold and Platinum with Bronze for a gear modification.
     * @param container Passing in the calling method argument {@link VaultArtisanStationContainer} container as an argument.
     * @param player Passing in the calling method argument {@link ServerPlayer} player as an argument.
     * @param callbackInformation The {@link CallbackInfo} for the injection.
     * @param gear Passing in the local variable {@link ItemStack} gear as an argument.
     * @param data Passing in the local variable {@link VaultGearData} data as an argument.
     * @param potential Passing in the local variable {@link Optional} potential as an argument.
     * @param inSlot Passing in the local variable {@link Slot} inSlot as an argument.
     * @param input Passing in the local variable {@link ItemStack} input as an argument.
     * @param material Passing in the local variable {@link ItemStack} material as an argument.
     * @param cost Passing in the local variable {@link GearModificationCost} cost as an argument.
     * @param bronze Passing in the local variable {@link ItemStack} bronze as an argument.
     */
    @Inject(method = "apply", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;shrink(I)V", ordinal = 1), remap = false, locals = LocalCapture.CAPTURE_FAILHARD)
    void InjectSpendCoins(VaultArtisanStationContainer container, ServerPlayer player, CallbackInfo callbackInformation, ItemStack gear, VaultGearData data, Optional potential, Slot inSlot, ItemStack input, ItemStack material, String rollType, GearModificationCost cost, ItemStack bronze)
    {
        ItemStack silver = ((ICoinSlots)container).getSilverSlot().getItem();
        ItemStack gold = ((ICoinSlots)container).getGoldSlot().getItem();
        ItemStack platinum = ((ICoinSlots)container).getPlatinumSlot().getItem();

        int totalBronzeCostRemaining = cost.costBronze();

        if (totalBronzeCostRemaining - bronze.getCount() <= 0)
        {
            bronze.shrink(totalBronzeCostRemaining);
        }

        else
        {
            totalBronzeCostRemaining -= bronze.getCount();

            if (totalBronzeCostRemaining - (silver.getCount() * 9) <= 0)
            {
                bronze = new ItemStack(ModBlocks.VAULT_BRONZE);
                bronze.setCount(9 - (totalBronzeCostRemaining % 9));

                silver.shrink((int)(Math.ceil((double)totalBronzeCostRemaining / 9)));
            }

            else
            {
                totalBronzeCostRemaining -= silver.getCount() * 9;

                if (totalBronzeCostRemaining - (gold.getCount() * 81) <= 0)
                {
                    bronze = new ItemStack(ModBlocks.VAULT_BRONZE);
                    bronze.setCount(9 - ((totalBronzeCostRemaining % 81) % 9));

                    silver = new ItemStack(ModBlocks.VAULT_SILVER);
                    silver.setCount((81 - (totalBronzeCostRemaining % 81)) / 9);

                    gold.shrink((int)(Math.ceil((double)totalBronzeCostRemaining / 81)));
                }

                else
                {
                    totalBronzeCostRemaining -= gold.getCount() * 81;

                    bronze = new ItemStack(ModBlocks.VAULT_BRONZE);
                    bronze.setCount(9 - (((totalBronzeCostRemaining % 729) % 81) % 9));

                    silver = new ItemStack(ModBlocks.VAULT_SILVER);
                    silver.setCount((81 - ((totalBronzeCostRemaining % 729) % 81)) / 9);

                    gold = new ItemStack(ModBlocks.VAULT_GOLD);
                    gold.setCount((729 - (totalBronzeCostRemaining % 729)) / 81);

                    platinum.shrink((int)(Math.ceil((double)totalBronzeCostRemaining / 729)));
                }
            }
        }

        ((ICoinSlots)container).getSilverSlot().set(silver);
        ((ICoinSlots)container).getGoldSlot().set(gold);
        ((ICoinSlots)container).getPlatinumSlot().set(platinum);
    }

    /**
     *
     * @param container Passing in the calling method argument {@link VaultArtisanStationContainer} container as an argument.
     * @param player Passing in the calling method argument {@link ServerPlayer} player as an argument.
     * @param callbackInformationReturnable The {@link CallbackInfoReturnable} for the injection and return.
     * @param inSlot Passing in the local variable {@link Slot} inSlot as an argument.
     * @param gear Passing in the local variable {@link ItemStack} gear as an argument.
     * @param in Passing in the local variable {@link ItemStack} in as an argument.
     */
    @Inject(method = "canApply", at = @At(value = "RETURN", ordinal = 2), cancellable = true, remap = false, locals = LocalCapture.CAPTURE_FAILHARD)
    void InjectCanSpendCoins(VaultArtisanStationContainer container, Player player, CallbackInfoReturnable<Boolean> callbackInformationReturnable, Slot inSlot, ItemStack gear, ItemStack in)
    {
        VaultGearData data = VaultGearData.read(gear);
        Optional<Integer> potential = data.getFirstValue(ModGearAttributes.CRAFTING_POTENTIAL);
        String rollType = data.get(ModGearAttributes.GEAR_ROLL_TYPE, VaultGearAttributeTypeMerger.firstNonNull());
        GearModificationCost cost = GearModificationCost.getCost(data.getRarity(), rollType, data.getItemLevel(), potential.orElse(0), this.modification());

        callbackInformationReturnable.setReturnValue(container.getPlatingSlot().getItem().getCount() >= cost.costPlating() && (((ICoinSlots)container).getBronzeSlot().getItem().getCount() + (((ICoinSlots)container).getSilverSlot().getItem().getCount() * 9) + (((ICoinSlots)container).getGoldSlot().getItem().getCount() * 81) + (((ICoinSlots)container).getPlatinumSlot().getItem().getCount() * 729) >= cost.costBronze()) && this.modification().canApply(gear, in, player, rand));
    }

    //endregion
}