package tv.alterNERD.VaultModTweaks.integration.mixin;

import iskallia.vault.client.gui.framework.element.ButtonElement;
import iskallia.vault.client.gui.framework.element.ModificationButtonElement;
import iskallia.vault.client.gui.framework.render.Tooltips;
import iskallia.vault.client.gui.framework.spatial.spi.IPosition;
import iskallia.vault.container.VaultArtisanStationContainer;
import iskallia.vault.gear.data.AttributeGearData;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.gear.modification.GearModification;
import iskallia.vault.gear.modification.GearModificationAction;
import iskallia.vault.gear.modification.GearModificationCost;
import iskallia.vault.gear.tooltip.GearTooltip;
import iskallia.vault.gear.tooltip.VaultGearTooltipItem;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.init.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import tv.alterNERD.VaultModTweaks.integration.ICoinSlots;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@Mixin (ModificationButtonElement.class)
public abstract class MixinModificationButtonElement extends ButtonElement
{
    /**
     * Shadow in the {@link Random} random field in the {@link ModificationButtonElement}.
     */
    @Shadow
    private static final Random rand = new Random();

    /**
     * The constructor for the {@link MixinModificationButtonElement}.
     * @param position Passing through the {@link IPosition} position argument.
     * @param textures Passing through the {@link ButtonTextures} textures argument.
     * @param onClick Passing through the {@link Runnable} onClick argument.
     */
    public MixinModificationButtonElement(IPosition position, ButtonTextures textures, Runnable onClick)
    {
        super(position, textures, onClick);
    }

    /**
     * Modifies the tooltip in the {@link ModificationButtonElement#ModificationButtonElement(IPosition, Runnable, VaultArtisanStationContainer, GearModification)} (right after the tooltip is set).
     * This is a nasty hack that is entirely necessary as the target class has all the code in the constructor, and it is all in a lambda.
     * The lesson is, move complex initialisation to a separate method, and do not use lambdas in complex initialisations.
     * I am not going to move this into methods like I suggest to above as it will not improve the code for anyone else.
     * @param position Passing through the {@link IPosition} position argument.
     * @param onClick Passing through the {@link Runnable} onClick argument.
     * @param container Passing through the {@link VaultArtisanStationContainer} container argument.
     * @param modification Passing through the {@link GearModification} modification argument.
     * @param callbackInfo The {@link CallbackInfo} for the injection.
     */
    @Inject(method = "<init>", at = @At(value = "RETURN"), locals = LocalCapture.CAPTURE_FAILHARD)
    void ModifyTooltip(IPosition position, Runnable onClick, VaultArtisanStationContainer container, GearModification modification, CallbackInfo callbackInfo)
    {
        this.tooltip(Tooltips.multi(() ->
        {
            GearModificationAction action = container.getModificationAction(modification);

            if (action == null)
            {
                return Collections.emptyList();
            }

            else
            {
                ItemStack inputItem = ItemStack.EMPTY;
                Slot inputSlot = action.getCorrespondingSlot(container);

                if (inputSlot != null && !inputSlot.getItem().isEmpty())
                {
                    inputItem = inputSlot.getItem();
                }

                ItemStack gearStack = container.getGearInputSlot().getItem();
                AttributeGearData itemData = AttributeGearData.read(gearStack);
                int potential = itemData.getFirstValue(ModGearAttributes.CRAFTING_POTENTIAL).orElse(Integer.MIN_VALUE);
                boolean hasInput = !gearStack.isEmpty() && potential != Integer.MIN_VALUE;
                boolean failedModification = false;
                List<Component> tooltip = new ArrayList<>(modification.getDescription(inputItem));

                if (hasInput && !itemData.isModifiable())
                {
                    return List.of((new TranslatableComponent("the_vault.gear_modification.unmodifiable")).withStyle(ChatFormatting.RED));
                }

                else
                {
                    if (hasInput && !inputItem.isEmpty() && !action.modification().canApply(gearStack, inputItem, container.getPlayer(), rand))
                    {
                        tooltip.add(action.modification().getInvalidDescription(inputItem));
                        failedModification = true;
                    }

                    if (!failedModification && hasInput)
                    {
                        MutableComponent focusCmp;

                        if (!inputItem.isEmpty())
                        {
                            focusCmp = (new TextComponent("- ")).append(modification.getDisplayStack().getHoverName()).append(" x1").append(" [%s]".formatted(inputItem.getCount()));
                        }

                        else
                        {
                            focusCmp = (new TextComponent("Requires ")).append(modification.getDisplayStack().getHoverName());
                        }

                        focusCmp.withStyle(inputItem.isEmpty() ? ChatFormatting.RED : ChatFormatting.GREEN);
                        tooltip.add(focusCmp);
                    }

                    if (hasInput)
                    {
                        if (!failedModification && !inputItem.isEmpty())
                        {
                            VaultGearData data = VaultGearData.read(gearStack);
                            GearModificationCost cost = GearModificationCost.getCost(data.getRarity(), data.getItemLevel(), potential, modification);
                            ItemStack plating = container.getPlatingSlot().getItem();
                            int bronzeTotal = ((ICoinSlots)container).getBronzeSlot().getItem().getCount() + (((ICoinSlots)container).getSilverSlot().getItem().getCount() * 9) + (((ICoinSlots)container).getGoldSlot().getItem().getCount() * 81) + (((ICoinSlots)container).getPlatinumSlot().getItem().getCount() * 729);
                            MutableComponent var10001 = (new TextComponent("- ")).append((new ItemStack(ModItems.VAULT_PLATING)).getHoverName());
                            int var10002 = cost.costPlating();
                            tooltip.add(var10001.append(" x" + var10002).append(" [%s]".formatted(plating.getCount())).withStyle(cost.costPlating() > plating.getCount() ? ChatFormatting.RED : ChatFormatting.GREEN));
                            var10001 = (new TextComponent("- ")).append((new ItemStack(ModBlocks.VAULT_BRONZE)).getHoverName());
                            var10002 = cost.costBronze();
                            tooltip.add(var10001.append(" x" + var10002).append(" [%s]".formatted(bronzeTotal)).withStyle(cost.costBronze() > bronzeTotal ? ChatFormatting.RED : ChatFormatting.GREEN));
                        }

                        tooltip.add(TextComponent.EMPTY);
                        tooltip.add(gearStack.getHoverName());
                        Item patt5433$temp = gearStack.getItem();

                        if (patt5433$temp instanceof VaultGearTooltipItem gearTooltipItem)
                        {
                            tooltip.addAll(gearTooltipItem.createTooltip(gearStack, GearTooltip.craftingView()));
                        }
                    }

                    return tooltip;
                }
            }
        }));
    }
}