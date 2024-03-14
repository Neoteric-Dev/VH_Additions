package tv.alterNERD.VaultModTweaks.integration.mixin;

import iskallia.vault.container.TransmogTableContainer;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.network.message.transmog.TransmogButtonMessage;
import iskallia.vault.world.data.DiscoveredModelsData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import tv.alterNERD.VaultModTweaks.VaultModTweaks;

import java.util.Set;

/**
 * Modifies the {@link TransmogButtonMessage} to use Vault Silver, Gold and Platinum for transmogrification.
 */
@Mixin(TransmogButtonMessage.class)
public class MixinTransmogButtonMessage
{
    //region Mixins

    /**
     * Injects the use of Vault Silver, Gold and Platinum into the {@link TransmogButtonMessage#handle(ServerPlayer, TransmogTableContainer, Slot, Slot, Slot, int, DiscoveredModelsData, Set, ItemStack, VaultGearData)} method.
     * @param context Passing through the {@link NetworkEvent.Context} context for the injection.
     * @param callbackInformation  The {@link CallbackInfo} for the injection.
     * @param player Passing through the {@link ServerPlayer} player for the injection.
     * @param container Passing through the {@link TransmogTableContainer} container for the injection.
     * @param gearSlot Passing through the {@link Slot} gearSlot for the injection.
     * @param bronzeSlot Passing through the {@link Slot} bronzeSlot for the injection.
     * @param outputSlot Passing through the {@link Slot} outputSlot for the injection.
     * @param totalBronzeCostRemaining Passing through the {@link int} totalBronzeCostRemaining for the injection.
     * @param discoveredModelsData Passing through the {@link DiscoveredModelsData} discoveredModelsData for the injection.
     * @param discoveredModels Passing through the {@link Set} discoveredModels for the injection.
     * @param resultingStack Passing through the {@link ItemStack} resultingStack for the injection.
     * @param gearData Passing through the {@link VaultGearData} gearData for the injection.
     */
    @Inject(method = "lambda$handle$0", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;copy()Lnet/minecraft/world/item/ItemStack;", ordinal = 1), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
    private static void InjectCoinCost(NetworkEvent.Context context, CallbackInfo callbackInformation, ServerPlayer player, TransmogTableContainer container, Slot gearSlot, Slot bronzeSlot, Slot outputSlot, int totalBronzeCostRemaining, DiscoveredModelsData discoveredModelsData, Set<ResourceLocation> discoveredModels, ItemStack resultingStack, VaultGearData gearData)
    {
        Slot silverSlot = container.getSlot(container.getInternalInventoryIndexRange().getContainerIndex(2));
        Slot goldSlot = container.getSlot(container.getInternalInventoryIndexRange().getContainerIndex(3));
        Slot platinumSlot = container.getSlot(container.getInternalInventoryIndexRange().getContainerIndex(4));

        ItemStack bronze = bronzeSlot.getItem().copy();
        ItemStack silver = silverSlot.getItem().copy();
        ItemStack gold = goldSlot.getItem().copy();
        ItemStack platinum = platinumSlot.getItem().copy();

        if (totalBronzeCostRemaining - bronze.getCount() <= 0)
        {
            bronze.shrink(totalBronzeCostRemaining);
            VaultModTweaks.LOGGER.error("BO Cost: " + totalBronzeCostRemaining);
        }

        else
        {
            totalBronzeCostRemaining -= bronze.getCount();

            if (totalBronzeCostRemaining - (silver.getCount() * 9) <= 0)
            {
                bronze = new ItemStack(ModBlocks.VAULT_BRONZE);
                bronze.setCount(9 - (totalBronzeCostRemaining % 9));

                silver.shrink((int)(Math.ceil((double)totalBronzeCostRemaining / 9)));
                VaultModTweaks.LOGGER.error("BS Cost: " + totalBronzeCostRemaining);
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
                    VaultModTweaks.LOGGER.error("BSG Cost: " + totalBronzeCostRemaining);
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
                    VaultModTweaks.LOGGER.error("BSGP Cost: " + totalBronzeCostRemaining);
                }
            }
        }

        container.getInternalInventory().setItem(1, bronze);
        container.getInternalInventory().setItem(2, silver);
        container.getInternalInventory().setItem(3, gold);
        container.getInternalInventory().setItem(4, platinum);

        outputSlot.set(resultingStack);

        container.broadcastChanges();

        callbackInformation.cancel();
    }

    //endregion
}