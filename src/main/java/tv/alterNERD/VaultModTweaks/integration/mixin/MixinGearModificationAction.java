package tv.alterNERD.VaultModTweaks.integration.mixin;

import iskallia.vault.container.VaultArtisanStationContainer;
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
import tv.alterNERD.VaultModTweaks.integration.ICoinSlots;

import java.util.Optional;
import java.util.Random;

/**
 * Modifies the {@link GearModificationAction} to allow silver, gold and platinum coins to be used.
 */
@Mixin(GearModificationAction.class)
public abstract class MixinGearModificationAction
{
    /**
     * Shadow the {@link Random} instance in the {@link GearModificationAction} to be accessed locally.
     */
    @Shadow
    private static final Random rand = new Random();

    /**
     * Shadows the VaultArtisanStationContainer#getCorrespondingSlot(VaultArtisanStationContainer) member in the {@link GearModificationAction} to be accessed locally.
     * @param container The {@link VaultArtisanStationContainer} container argument shadowed.
     * @return The {@link Slot} return for the shadowed member.
     */
    @Shadow
    public abstract Slot getCorrespondingSlot(VaultArtisanStationContainer container);

    /**
     * Shadows the {@link VaultArtisanStationContainer#getGearInputSlot()} member in the {@link GearModificationAction} to be accessed locally.
     * @return The {@link GearModificationAction} return for the shadowed member.
     */
    @Shadow
    public abstract GearModification modification();

    /**
     * Overrides the {@link GearModificationAction#apply(VaultArtisanStationContainer, ServerPlayer)} member in the {@link GearModificationAction} to allow silver, gold and platinum coins to be used.
     * @param container The {@link VaultArtisanStationContainer} to access the coins from.
     * @param player The {@link ServerPlayer} using the coins.
     */
    public void apply(VaultArtisanStationContainer container, ServerPlayer player)
    {
        if (this.canApply(container, player))
        {
            ItemStack gear = container.getGearInputSlot().getItem();
            VaultGearData data = VaultGearData.read(gear);
            Optional<Integer> potential = data.getFirstValue(ModGearAttributes.CRAFTING_POTENTIAL);

            if (!potential.isEmpty())
            {
                Slot inSlot = this.getCorrespondingSlot(container);

                if (inSlot != null)
                {
                    ItemStack input = inSlot.getItem();
                    ItemStack material = input.copy();
                    input.shrink(1);
                    inSlot.set(input);
                    GearModificationCost cost = GearModificationCost.getCost(data.getRarity(), data.getItemLevel(), (Integer)potential.get(), this.modification());
                    ItemStack bronze = ((ICoinSlots)container).getBronzeSlot().getItem();
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

                    ((ICoinSlots)container).getBronzeSlot().set(bronze);
                    ((ICoinSlots)container).getSilverSlot().set(silver);
                    ((ICoinSlots)container).getGoldSlot().set(gold);
                    ((ICoinSlots)container).getPlatinumSlot().set(platinum);

                    ItemStack plating = container.getPlatingSlot().getItem();
                    plating.shrink(cost.costPlating());
                    container.getPlatingSlot().set(plating);
                    this.modification().apply(gear, material, player, rand);
                }
            }
        }
    }

    /**
     * Overrides the {@link GearModificationAction#canApply(VaultArtisanStationContainer, Player)} member in the {@link GearModificationAction} to allow silver, gold and platinum coins to be used.
     * @param container The {@link VaultArtisanStationContainer} to access the coins from.
     * @param player The {@link ServerPlayer} using the coins.
     * @return Returns whether the crafting action can be complete.
     */
    public boolean canApply(VaultArtisanStationContainer container, Player player)
    {
        Slot inSlot = this.getCorrespondingSlot(container);

        if (inSlot == null)
        {
            return false;
        }

        else
        {
            ItemStack gear = container.getGearInputSlot().getItem();
            ItemStack in = inSlot.getItem();

            if (!in.isEmpty() && !gear.isEmpty())
            {
                VaultGearData data = VaultGearData.read(gear);
                Optional<Integer> potential = data.getFirstValue(ModGearAttributes.CRAFTING_POTENTIAL);

                if (potential.isEmpty())
                {
                    return false;
                }

                else
                {
                    GearModificationCost cost = GearModificationCost.getCost(data.getRarity(), data.getItemLevel(), (Integer)potential.get(), this.modification());
                    ItemStack bronze = ((ICoinSlots)container).getBronzeSlot().getItem();
                    ItemStack silver = ((ICoinSlots)container).getSilverSlot().getItem();
                    ItemStack gold = ((ICoinSlots)container).getGoldSlot().getItem();
                    ItemStack platinum = ((ICoinSlots)container).getPlatinumSlot().getItem();
                    ItemStack plating = container.getPlatingSlot().getItem();

                    return plating.getCount() >= cost.costPlating() && (bronze.getCount() + (silver.getCount() * 9) + (gold.getCount() * 81) + (platinum.getCount() * 729) >= cost.costBronze()) && this.modification().canApply(gear, in, player, rand);
                }
            }

            else
            {
                return false;
            }
        }
    }
}