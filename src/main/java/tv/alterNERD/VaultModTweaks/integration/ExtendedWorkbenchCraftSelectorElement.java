package tv.alterNERD.VaultModTweaks.integration;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import iskallia.vault.client.gui.framework.ScreenTextures;
import iskallia.vault.client.gui.framework.element.ScrollableListSelectorElement;
import iskallia.vault.client.gui.framework.element.SelectableButtonElement;
import iskallia.vault.client.gui.framework.element.WorkbenchCraftSelectorElement;
import iskallia.vault.client.gui.framework.render.TooltipDirection;
import iskallia.vault.client.gui.framework.render.spi.IElementRenderer;
import iskallia.vault.client.gui.framework.spatial.Spatials;
import iskallia.vault.client.gui.framework.spatial.spi.IPosition;
import iskallia.vault.client.gui.framework.spatial.spi.ISpatial;
import iskallia.vault.client.gui.framework.text.LabelTextStyle;
import iskallia.vault.client.gui.framework.text.TextAlign;
import iskallia.vault.client.gui.framework.text.TextWrap;
import iskallia.vault.client.gui.helper.LightmapHelper;
import iskallia.vault.config.gear.VaultGearWorkbenchConfig;
import iskallia.vault.container.oversized.OverSizedInventory;
import iskallia.vault.gear.VaultGearModifierHelper;
import iskallia.vault.gear.attribute.VaultGearModifier;
import iskallia.vault.gear.crafting.ModifierWorkbenchHelper;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.util.SidedHelper;
import iskallia.vault.util.function.ObservableSupplier;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * This class is a copy of {@link WorkbenchCraftSelectorElement} with minor changes to pass in an {@link IOverSizedInventory} to use with {@link ExtendedInventoryUtils#GetMissingInputs(List, OverSizedInventory)} instead of the player inventory.
 * @param <E> The type of the {@link ExtendedWorkbenchCraftSelectorElement}.
 * @param <V> The type of the {@link ExtendedWorkbenchCraftSelectorElement.ExtendedWorkbenchListElement}.
 */
public class ExtendedWorkbenchCraftSelectorElement<E extends ExtendedWorkbenchCraftSelectorElement<E, V>, V extends ExtendedWorkbenchCraftSelectorElement.ExtendedWorkbenchListElement<V>> extends ScrollableListSelectorElement<E, VaultGearWorkbenchConfig.CraftableModifierConfig, V>
{
    final ObservableSupplier<ItemStack> inputSupplier;

    public ExtendedWorkbenchCraftSelectorElement(ISpatial spatial, ObservableSupplier<ItemStack> inputSupplier, Supplier<String> searchFilter, IOverSizedInventory overSizedInventory)
    {
        super(Spatials.copy(spatial).width(ScreenTextures.BUTTON_WORKBENCH_MODIFIER_TEXTURES.button().width()), new ExtendedWorkbenchCraftSelectorElement.ExtendedWorkbenchCraftSelectorModel<>(inputSupplier, searchFilter, overSizedInventory));

        this.inputSupplier = inputSupplier;
    }

    public void onSelect(Consumer<ModifierWorkbenchHelper.CraftingOption> fn)
    {
        SelectorModel<V, VaultGearWorkbenchConfig.CraftableModifierConfig> var3 = this.getSelectorModel();

        if (var3 instanceof ExtendedWorkbenchCraftSelectorElement.ExtendedWorkbenchCraftSelectorModel<?> selModel)
        {
            selModel.whenSelected((cfg) ->
            {
                ModifierWorkbenchHelper.CraftingOption option = selModel.getSelectedCraftingOption();

                if (option != null)
                {
                    fn.accept(option);
                }

            });
        }
    }

    public void render(IElementRenderer renderer, @Nonnull PoseStack poseStack, int mouseX, int mouseY, float partialTick)
    {
        super.render(renderer, poseStack, mouseX, mouseY, partialTick);

        this.inputSupplier.ifChanged((change) -> this.refreshElements());
    }

    public static class ExtendedWorkbenchCraftSelectorModel<E extends ExtendedWorkbenchCraftSelectorElement.ExtendedWorkbenchListElement<E>> extends ScrollableListSelectorElement.SelectorModel<E, VaultGearWorkbenchConfig.CraftableModifierConfig>
    {
        final ObservableSupplier<ItemStack> inputSupplier;
        final Supplier<String> searchFilter;

        /**
         * This instance is to work with {@link ExtendedWorkbenchCraftSelectorModel#createSelectable(ISpatial, VaultGearWorkbenchConfig.CraftableModifierConfig)}.
         */
        IOverSizedInventory overSizedInventory;

        public ExtendedWorkbenchCraftSelectorModel(ObservableSupplier<ItemStack> inputSupplier, Supplier<String> searchFilter, IOverSizedInventory overSizedInventory)
        {
            this.inputSupplier = inputSupplier;
            this.searchFilter = searchFilter;
            this.overSizedInventory = overSizedInventory;
        }

        public List<VaultGearWorkbenchConfig.CraftableModifierConfig> getEntries()
        {
            ItemStack input = this.inputSupplier.get();

            if (input.isEmpty())
            {
                return Collections.emptyList();
            }

            else
            {
                Player player = Minecraft.getInstance().player;

                if (player == null)
                {
                    return Collections.emptyList();
                }

                else
                {
                    String searchTerm = (this.searchFilter.get()).toLowerCase(Locale.ROOT);
                    List<VaultGearWorkbenchConfig.CraftableModifierConfig> out = new ArrayList<>();
                    String locRemove = (new TranslatableComponent("the_vault.gear_workbench.remove_crafted_modifiers")).getString();

                    if (searchTerm.isEmpty() || locRemove.toLowerCase(Locale.ROOT).contains(searchTerm))
                    {
                        out.add(null);
                    }

                    VaultGearWorkbenchConfig.getConfig(input.getItem()).map(VaultGearWorkbenchConfig::getAllCraftableModifiers).ifPresent((craftingConfigs) -> craftingConfigs.forEach((cfg) -> cfg.createModifier().flatMap((modifier) -> modifier.getConfigDisplay(input)).ifPresent((display) ->
                    {
                        String locDisplay = display.getString().toLowerCase(Locale.ROOT);

                        if (searchTerm.isEmpty() || locDisplay.contains(searchTerm))
                        {
                            out.add(cfg);
                        }
                    })));

                    int playerLevel = SidedHelper.getVaultLevel(player);
                    out.removeIf((cfg) -> cfg != null && cfg.getUnlockCategory() == VaultGearWorkbenchConfig.UnlockCategory.VAULT_DISCOVERY && cfg.getMinLevel() > playerLevel);

                    return out;
                }
            }
        }

        public E createSelectable(ISpatial spatial, VaultGearWorkbenchConfig.CraftableModifierConfig entry)
        {
            return (E)(entry == null ? new ExtendedWorkbenchRemoveCraftElement(spatial, this.inputSupplier.get(), overSizedInventory) : new ExtendedWorkbenchCraftElement(spatial, this.inputSupplier.get(), entry, overSizedInventory));
        }

        @Nullable
        protected ModifierWorkbenchHelper.CraftingOption getSelectedCraftingOption()
        {
            E element = this.getSelectedElement();

            if (element == null)
            {
                return null;
            }

            else if (element instanceof ExtendedWorkbenchCraftSelectorElement.ExtendedWorkbenchCraftElement)
            {
                ExtendedWorkbenchCraftSelectorElement.ExtendedWorkbenchCraftElement<?> craftElement = (ExtendedWorkbenchCraftSelectorElement.ExtendedWorkbenchCraftElement)element;

                return new ModifierWorkbenchHelper.CraftingOption(craftElement.getModifier());
            }

            else
            {
                return new ModifierWorkbenchHelper.CraftingOption(null);
            }
        }
    }

    public static class ExtendedWorkbenchCraftElement<E extends ExtendedWorkbenchCraftSelectorElement.ExtendedWorkbenchCraftElement<E>> extends ExtendedWorkbenchCraftSelectorElement.ExtendedWorkbenchListElement<E>
    {
        final ItemStack gearStack;
        final VaultGearWorkbenchConfig.CraftableModifierConfig modifier;
        final LabelTextStyle textStyle;
        final VaultGearModifier<?> displayModifier;

        public ExtendedWorkbenchCraftElement(IPosition position, ItemStack gearStack, VaultGearWorkbenchConfig.CraftableModifierConfig modifier, IOverSizedInventory overSizedInventory)
        {
            super(position, overSizedInventory);

            this.gearStack = gearStack;
            this.modifier = modifier;
            this.textStyle = LabelTextStyle.defaultStyle().shadow().build();
            this.displayModifier = this.modifier.createModifier().orElse(null);

            this.tooltip((tooltipRenderer, poseStack, mouseX, mouseY, tooltipFlag) ->
            {
                if (this.canCraft())
                {
                    return false;
                }

                else
                {
                    Player player = Minecraft.getInstance().player;

                    if (player == null)
                    {
                        return false;
                    }

                    else
                    {
                        String affix;

                        if (!this.modifier.hasPrerequisites(player))
                        {
                            affix = this.getModifier().getUnlockCategory().formatDisplay(this.modifier.getMinLevel());
                            tooltipRenderer.renderTooltip(poseStack, (new TextComponent(affix)).withStyle(ChatFormatting.RED), mouseX, mouseY, TooltipDirection.RIGHT);
                        }

                        else
                        {
                            MutableComponent cmpx;

                            if (!this.gearStack.isEmpty())
                            {
                                int minLevel = this.modifier.getMinLevel();

                                if (VaultGearData.read(this.gearStack).getItemLevel() < minLevel)
                                {
                                    cmpx = (new TextComponent("Item Level required: " + minLevel)).withStyle(ChatFormatting.RED);
                                    tooltipRenderer.renderTooltip(poseStack, cmpx, mouseX, mouseY, TooltipDirection.RIGHT);

                                    return true;
                                }

                                ItemStack gearCopy = this.gearStack.copy();
                                ModifierWorkbenchHelper.removeCraftedModifiers(gearCopy);
                                VaultGearData data = VaultGearData.read(gearCopy);
                                Set<String> groups = data.getExistingModifierGroups(VaultGearData.Type.EXPLICIT_MODIFIERS);

                                if (this.displayModifier != null && groups.contains(this.displayModifier.getModifierGroup()))
                                {
                                    MutableComponent cmp = (new TextComponent("Item already has a modifier of this group.")).withStyle(ChatFormatting.RED);
                                    tooltipRenderer.renderTooltip(poseStack, cmp, mouseX, mouseY, TooltipDirection.RIGHT);

                                    return true;
                                }
                            }

                            affix = this.modifier.getAffixGroup().getTargetAffixType().getSingular();
                            cmpx = (new TextComponent("Item has no open " + affix)).withStyle(ChatFormatting.RED);
                            tooltipRenderer.renderTooltip(poseStack, cmpx, mouseX, mouseY, TooltipDirection.RIGHT);
                        }

                        return true;
                    }
                }
            });
        }

        public VaultGearWorkbenchConfig.CraftableModifierConfig getModifier()
        {
            return this.modifier;
        }

        public void render(IElementRenderer renderer, @NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTick)
        {
            this.setDisabled(!this.canCraft());

            super.render(renderer, poseStack, mouseX, mouseY, partialTick);

            poseStack.pushPose();
            poseStack.translate(0.0, 0.0, 1.0);

            if (this.displayModifier != null)
            {
                this.getCraftedModifierDescription().ifPresent((cfgDisplay) -> this.textStyle.textBorder().render(renderer, poseStack, cfgDisplay, TextWrap.wrap(), TextAlign.LEFT, this.worldSpatial.x() + 4, this.worldSpatial.y() + 3, this.worldSpatial.z(), this.worldSpatial.width()));
            }

            poseStack.popPose();
        }

        protected List<ItemStack> createNeededInputs()
        {
            return this.modifier.createCraftingCost(this.gearStack);
        }

        public Optional<MutableComponent> getCraftedModifierDescription()
        {
            return this.displayModifier.getConfigDisplay(this.gearStack);
        }

        boolean hasAffixSpace()
        {
            ItemStack inputCopy = this.gearStack.copy();
            ModifierWorkbenchHelper.removeCraftedModifiers(inputCopy);
            VaultGearModifier.AffixType affixType = this.modifier.getAffixGroup().getTargetAffixType();

            return affixType == VaultGearModifier.AffixType.PREFIX ? VaultGearModifierHelper.hasOpenPrefix(inputCopy) : VaultGearModifierHelper.hasOpenSuffix(inputCopy);
        }

        boolean hasGroupApplied()
        {
            if (this.gearStack.isEmpty())
            {
                return false;
            }

            else
            {
                ItemStack gearCopy = this.gearStack.copy();
                ModifierWorkbenchHelper.removeCraftedModifiers(gearCopy);
                VaultGearData data = VaultGearData.read(gearCopy);
                Set<String> groups = data.getExistingModifierGroups(VaultGearData.Type.EXPLICIT_MODIFIERS);

                return this.displayModifier != null && groups.contains(this.displayModifier.getModifierGroup());
            }
        }

        boolean canCraft()
        {
            Player player = Minecraft.getInstance().player;
            return player != null && !this.gearStack.isEmpty() && this.modifier.hasPrerequisites(player) && VaultGearData.read(this.gearStack).getItemLevel() >= this.modifier.getMinLevel() && !this.hasGroupApplied() && this.hasAffixSpace();
        }
    }

    public static class ExtendedWorkbenchRemoveCraftElement<E extends ExtendedWorkbenchCraftSelectorElement.ExtendedWorkbenchRemoveCraftElement<E>> extends ExtendedWorkbenchCraftSelectorElement.ExtendedWorkbenchListElement<E>
    {
        final ItemStack gearStack;
        final LabelTextStyle textStyle;

        public ExtendedWorkbenchRemoveCraftElement(IPosition position, ItemStack gearStack, IOverSizedInventory overSizedInventory)
        {
            super(position, overSizedInventory);

            this.gearStack = gearStack;
            this.textStyle = LabelTextStyle.defaultStyle().shadow().build();

            this.tooltip((tooltipRenderer, poseStack, mouseX, mouseY, tooltipFlag) ->
            {
                if (this.canCraft())
                {
                    return false;
                }

                else if (this.gearStack.isEmpty())
                {
                    return false;
                }

                else
                {
                    Component cmp = (new TranslatableComponent("the_vault.gear_workbench.remove_crafted_modifiers.no_modifier")).withStyle(ChatFormatting.RED);
                    tooltipRenderer.renderTooltip(poseStack, cmp, mouseX, mouseY, TooltipDirection.RIGHT);

                    return true;
                }
            });
        }

        public void render(IElementRenderer renderer, @NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTick)
        {
            this.setDisabled(!this.canCraft());

            super.render(renderer, poseStack, mouseX, mouseY, partialTick);

            poseStack.pushPose();
            poseStack.translate(0.0, 0.0, 1.0);
            this.textStyle.textBorder().render(renderer, poseStack, new TranslatableComponent("the_vault.gear_workbench.remove_crafted_modifiers"), TextWrap.wrap(), TextAlign.LEFT, this.worldSpatial.x() + 4, this.worldSpatial.y() + 3, this.worldSpatial.z(), this.worldSpatial.width());
            poseStack.popPose();
        }

        protected List<ItemStack> createNeededInputs()
        {
            return this.gearStack.isEmpty() ? Collections.emptyList() : VaultGearWorkbenchConfig.getConfig(this.gearStack.getItem()).map(VaultGearWorkbenchConfig::getCostRemoveCraftedModifiers).orElse(Collections.emptyList());
        }

        boolean canCraft()
        {
            return !this.gearStack.isEmpty() && ModifierWorkbenchHelper.hasCraftedModifier(this.gearStack);
        }
    }

    public abstract static class ExtendedWorkbenchListElement<E extends ExtendedWorkbenchCraftSelectorElement.ExtendedWorkbenchListElement<E>> extends SelectableButtonElement<E>
    {
        List<ItemStack> inputs;

        /**
         * This is the additional container for determining missing inputs.
         * This entire code base exists for a reference to this one container.
         */
        IOverSizedInventory overSizedInventory;

        public ExtendedWorkbenchListElement(IPosition position, IOverSizedInventory overSizedInventory)
        {
            super(position, ScreenTextures.BUTTON_WORKBENCH_MODIFIER_TEXTURES, () -> {});

            this.overSizedInventory = overSizedInventory;
        }

        public void render(IElementRenderer renderer, @NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTick)
        {
            super.render(renderer, poseStack, mouseX, mouseY, partialTick);

            ItemRenderer ir = Minecraft.getInstance().getItemRenderer();
            Font font = Minecraft.getInstance().font;
            int offsetX = this.worldSpatial.x() + this.worldSpatial.width() - 18;
            int offsetY = this.worldSpatial.y() + this.worldSpatial.height() - 18;
            List<ItemStack> inputs = this.getInputs();
            List<ItemStack> missingInputs = new ArrayList<>();

            if (Minecraft.getInstance().player != null)
            {
                missingInputs = ExtendedInventoryUtils.GetMissingInputs(inputs, overSizedInventory.getOverSizedInventory());
            }

            for(Iterator<ItemStack> var12 = inputs.iterator(); var12.hasNext(); offsetX -= 17)
            {
                ItemStack stack = var12.next();
                ir.renderGuiItem(stack, offsetX, offsetY);
                MutableComponent text = new TextComponent(String.valueOf(stack.getCount()));

                if ((missingInputs).contains(stack))
                {
                    text.withStyle(ChatFormatting.RED);
                }

                poseStack.pushPose();
                poseStack.translate(0.0, 0.0, 200.0);
                MultiBufferSource.BufferSource buffers = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
                font.drawInBatch(text, (float)(offsetX + 17 - font.width(text)), (float)(offsetY + 9), 16777215, true, poseStack.last().pose(), buffers, false, 0, LightmapHelper.getPackedFullbrightCoords());
                buffers.endBatch();
                poseStack.popPose();
            }

        }

        protected List<ItemStack> getInputs()
        {
            if (this.inputs == null)
            {
                this.inputs = this.createNeededInputs();
            }

            return this.inputs;
        }

        protected abstract List<ItemStack> createNeededInputs();
    }
}