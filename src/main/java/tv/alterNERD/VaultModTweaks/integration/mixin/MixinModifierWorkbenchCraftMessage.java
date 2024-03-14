package tv.alterNERD.VaultModTweaks.integration.mixin;

import iskallia.vault.config.gear.VaultGearWorkbenchConfig;
import iskallia.vault.container.ModifierWorkbenchContainer;
import iskallia.vault.container.oversized.OverSizedInventory;
import iskallia.vault.container.oversized.OverSizedItemStack;
import iskallia.vault.gear.attribute.VaultGearModifier;
import iskallia.vault.gear.crafting.ModifierWorkbenchHelper;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.network.message.ModifierWorkbenchCraftMessage;
import iskallia.vault.util.InventoryUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import tv.alterNERD.VaultModTweaks.integration.ExtendedInventoryUtils;
import tv.alterNERD.VaultModTweaks.integration.IOverSizedInventory;

import java.util.ArrayList;
import java.util.List;

/**
 * Modifies the {@link ModifierWorkbenchCraftMessage} to use the {@link IOverSizedInventory} inventory slots instead of the {@link OverSizedInventory}.
 */
@Mixin(ModifierWorkbenchCraftMessage.class)
public class MixinModifierWorkbenchCraftMessage
{
    //region Fields

    /**
     * A local copy of the {@link BlockPos} localPosition from the {@link ModifierWorkbenchCraftMessage}.
     */
    private static BlockPos localPosition;

    //endregion

    //region Mixins

    /**
     * Injects to grab a local copy of the {@link BlockPos} localPosition from the {@link ModifierWorkbenchCraftMessage}.
     * @param buffer Passing in the {@link FriendlyByteBuf} buffer as an argument.
     * @param callbackInformationReturnable The {@link CallbackInfoReturnable} for the injection.
     */
    @Inject(method = "decode", at = @At("HEAD"), remap = false)
    private static void InjectLocalPosition(FriendlyByteBuf buffer, CallbackInfoReturnable<ModifierWorkbenchCraftMessage> callbackInformationReturnable)
    {
        localPosition = new FriendlyByteBuf(buffer.duplicate()).readBlockPos();
    }

    /**
     * Inject a new consumption using the slots in the {@link ModifierWorkbenchContainer} instead of the player inventory.
     * @param input Passing in the {@link ItemStack} input as an argument.
     * @param message Passing in the {@link ModifierWorkbenchCraftMessage} message as an argument.
     * @param player Passing in the {@link ServerPlayer} player as an argument.
     * @param tile Passing in the {@link BlockEntity} tile as an argument.
     * @param cfg Passing in the {@link VaultGearWorkbenchConfig} cfg as an argument.
     * @param callbackInformation The {@link CallbackInfo} for the injection.
     * @param inputCopy Passing in the {@link ItemStack} inputCopy as an argument.
     * @param targetAffix Passing in the {@link VaultGearModifier.AffixType} targetAffix as an argument.
     * @param createdModifier Passing in the {@link VaultGearModifier} createdModifier as an argument.
     * @param cost Passing in the {@link List} cost as an argument.
     * @param missing Passing in the {@link List} missing as an argument.
     */
    @Inject(method = "lambda$handle$0", at = @At(value = "INVOKE", target = "Liskallia/vault/util/InventoryUtil;consumeInputs(Ljava/util/List;Lnet/minecraft/world/entity/player/Inventory;Z)Z", ordinal = 0), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD, remap = false)
    private static void InjectConsumption(ItemStack input, ModifierWorkbenchCraftMessage message, ServerPlayer player, BlockEntity tile, VaultGearWorkbenchConfig cfg, CallbackInfo callbackInformation, ItemStack inputCopy, VaultGearModifier.AffixType targetAffix, VaultGearModifier createdModifier, List cost, List missing)
    {
        if (ConsumeInputs(cost, ((IOverSizedInventory)tile).getOverSizedInventory(), true, new ArrayList<>()))
        {
            if (ConsumeInputs(cost, ((IOverSizedInventory)tile).getOverSizedInventory(), false, new ArrayList<>()))
            {
                if (createdModifier == null)
                {
                    ModifierWorkbenchHelper.removeCraftedModifiers(input);
                }

                else
                {
                    createdModifier.setCategory(VaultGearModifier.AffixCategory.CRAFTED);
                    createdModifier.setGameTimeAdded(player.getLevel().getGameTime());
                    ModifierWorkbenchHelper.removeCraftedModifiers(input);
                    VaultGearData datax = VaultGearData.read(input);
                    datax.addModifier(targetAffix, createdModifier);
                    datax.write(input);
                }

                player.getLevel().levelEvent(1030, tile.getBlockPos(), 0);
            }

        }

        callbackInformation.cancel();
    }

    /**
     * Modifies the {@link InventoryUtil#getMissingInputs(List, Inventory)} to use the {@link IOverSizedInventory} inventory slots instead of the player inventory.
     * @param recipeInputs Passing in the {@link List} recipeInputs as an argument.
     * @param playerInventory Passing in the {@link Inventory} playerInventory as an argument.
     * @return Returns the modified result.
     */
    @Redirect(method = "lambda$handle$0", at = @At(value = "INVOKE", target = "Liskallia/vault/util/InventoryUtil;getMissingInputs(Ljava/util/List;Lnet/minecraft/world/entity/player/Inventory;)Ljava/util/List;", ordinal = 0), remap = false)
    private static List<ItemStack> ModifyGetMissingInputs(List<ItemStack> recipeInputs, Inventory playerInventory)
    {
        BlockEntity tile = playerInventory.player.getLevel().getBlockEntity(localPosition);

        if (tile == null)
        {
            return recipeInputs;
        }

        return ExtendedInventoryUtils.GetMissingInputs(recipeInputs, ((IOverSizedInventory)tile).getOverSizedInventory());
    }

    //endregion

    //region Helpers

    /**
     * A direct copy of {@link InventoryUtil#isEqualCrafting(ItemStack, ItemStack)} for local use.
     * @param thisStack The first {@link ItemStack} to compare.
     * @param thatStack The second {@link ItemStack} to compare.
     * @return Returns if the two {@link ItemStack} are equal.
     */
    private static boolean IsEqualCrafting(ItemStack thisStack, ItemStack thatStack)
    {
        return thisStack.getItem() == thatStack.getItem() && thisStack.getDamageValue() == thatStack.getDamageValue() && (thisStack.getTag() == null || thisStack.areShareTagsEqual(thatStack));
    }

    /**
     * A modified version of {@link InventoryUtil#consumeInputs(List, Inventory, OverSizedInventory, boolean, List)} that removes the player inventory.
     * @param recipeInputs Passing in the {@link List} recipeInputs as an argument.
     * @param tileInv Passing in the {@link OverSizedInventory} tileInv as an argument.
     * @param simulate Passing in the {@link boolean} simulate as an argument.
     * @param consumed Passing in the {@link List} consumed as an argument.
     * @return Returns if the consumption was successful.
     */
    private static boolean ConsumeInputs(List<ItemStack> recipeInputs, OverSizedInventory tileInv, boolean simulate, List<OverSizedItemStack> consumed)
    {
        boolean success = true;

        for (ItemStack input : recipeInputs)
        {
            int neededCount = input.getCount();
            NonNullList<OverSizedItemStack> overSizedContents = tileInv.getOverSizedContents();

            for (int slot = 0; slot < overSizedContents.size(); ++slot)
            {
                OverSizedItemStack overSized = overSizedContents.get(slot);

                if (neededCount <= 0)
                {
                    break;
                }

                if (IsEqualCrafting(input, overSized.stack()))
                {
                    int deductedAmount = Math.min(neededCount, overSized.amount());

                    if (!simulate)
                    {
                        tileInv.setOverSizedStack(slot, overSized.addCopy(-deductedAmount));
                        consumed.add(overSized.copyAmount(deductedAmount));
                    }

                    neededCount -= overSized.amount();
                }
            }

            if (neededCount > 0)
            {
                success = false;
            }
        }

        return success;
    }

    //endregion
}