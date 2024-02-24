package tv.alterNERD.VaultModTweaks.integration.mixin;

import iskallia.vault.client.gui.framework.ScreenRenderers;
import iskallia.vault.client.gui.framework.element.ButtonElement;
import iskallia.vault.client.gui.framework.element.TextInputElement;
import iskallia.vault.client.gui.framework.element.WorkbenchCraftSelectorElement;
import iskallia.vault.client.gui.framework.element.spi.IElement;
import iskallia.vault.client.gui.framework.render.ScreenTooltipRenderer;
import iskallia.vault.client.gui.framework.render.spi.IElementRenderer;
import iskallia.vault.client.gui.framework.render.spi.ITooltipRendererFactory;
import iskallia.vault.client.gui.framework.screen.AbstractElementContainerScreen;
import iskallia.vault.client.gui.framework.spatial.Spatials;
import iskallia.vault.client.gui.framework.spatial.spi.IMutableSpatial;
import iskallia.vault.client.gui.screen.block.ModifierWorkbenchScreen;
import iskallia.vault.container.ModifierWorkbenchContainer;
import iskallia.vault.container.VaultArtisanStationContainer;
import iskallia.vault.container.oversized.OverSizedInventory;
import iskallia.vault.gear.crafting.ModifierWorkbenchHelper;
import iskallia.vault.util.function.ObservableSupplier;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import tv.alterNERD.VaultModTweaks.integration.ExtendedInventoryUtils;
import tv.alterNERD.VaultModTweaks.integration.ExtendedWorkbenchCraftSelectorElement;
import tv.alterNERD.VaultModTweaks.integration.IOverSizedInventory;

import java.util.List;

/**
 * Modifies the {@link ModifierWorkbenchScreen} to use Vault Gold, Nullifying Focus and Amplifying Focus from the slots instead of the player inventory.
 */
@Mixin(ModifierWorkbenchScreen.class)
public abstract class MixinModifierWorkbenchScreen extends AbstractElementContainerScreen<ModifierWorkbenchContainer>
{
    //region Fields

    /**
     * An instance of {@link ExtendedWorkbenchCraftSelectorElement} rather than the default {@link WorkbenchCraftSelectorElement}.
     */
    ExtendedWorkbenchCraftSelectorElement<?, ?> extendedSelectorElement;

    //endregion

    //region Shadow Fields

    /**
     * Shadows the {@link TextInputElement} searchInput field in the {@link ModifierWorkbenchScreen} to be accessed locally.
     */
    @Shadow
    final TextInputElement<?> searchInput;

    /**
     * Shadows the {@link TextInputElement} selectedOption field in the {@link ModifierWorkbenchScreen} to be accessed locally.
     */
    @Shadow
    ModifierWorkbenchHelper.CraftingOption selectedOption;

    //endregion

    //region Initialisation

    /**
     * A constructor for the {@link MixinModifierWorkbenchScreen}.
     * @param container Passing through the {@link ModifierWorkbenchContainer} container as an argument.
     * @param inventory Passing through the {@link Inventory} inventory as an argument.
     * @param title Passing through the {@link Component} title as an argument.
     * @param elementRenderer Passing through the {@link IElementRenderer} elementRenderer as an argument.
     * @param tooltipRendererFactory Passing through the {@link ITooltipRendererFactory} tooltipRendererFactory as an argument.
     */
    public MixinModifierWorkbenchScreen(ModifierWorkbenchContainer container, Inventory inventory, Component title, IElementRenderer elementRenderer, ITooltipRendererFactory<AbstractElementContainerScreen<ModifierWorkbenchContainer>> tooltipRendererFactory)
    {
        super(container, inventory, title, ScreenRenderers.getImmediate(), ScreenTooltipRenderer::create);

        this.searchInput = null;
    }

    //endregion

    //region Mixins

    /**
     * Injects the {@link ExtendedWorkbenchCraftSelectorElement} into the {@link ModifierWorkbenchScreen}.
     * @param container Passing through the {@link ModifierWorkbenchContainer} container as an argument.
     * @param inventory Passing through the {@link Inventory} inventory as an argument.
     * @param title Passing through the {@link Component} title as an argument.
     * @param callbackInformation The {@link CallbackInfo} for the injection.
     * @param inventoryName Passing through the {@link IMutableSpatial} inventoryName as an argument.
     * @param craftButton Passing through the {@link ButtonElement} craftButton as an argument.
     */
    @Inject(method = "<init>", at = @At(value = "RETURN"), locals = LocalCapture.CAPTURE_FAILHARD, remap = false)
    void InjectSelectorElement(ModifierWorkbenchContainer container, Inventory inventory, Component title, CallbackInfo callbackInformation, MutableComponent inventoryName, ButtonElement craftButton)
    {
        this.addElement(extendedSelectorElement = (ExtendedWorkbenchCraftSelectorElement)(new ExtendedWorkbenchCraftSelectorElement(Spatials.positionXY(8, 19).height(97), ObservableSupplier.ofIdentity(() -> this.getMenu().getInput()), ((TextInputElement) this.searchInput)::getInput, (IOverSizedInventory)getMenu().getTileEntity()).layout((screen, gui, parent, world) -> world.translateXY(gui))));

        extendedSelectorElement.onSelect((option) -> this.selectedOption = option);
        this.searchInput.onTextChanged((text) -> extendedSelectorElement.refreshElements());
    }

    /**
     * Removes {@link ModifierWorkbenchScreen#addElement(IElement)} for the {@link iskallia.vault.client.gui.framework.element.WorkbenchCraftSelectorElement} so that {@link ExtendedWorkbenchCraftSelectorElement} can be used instead.
     * @param instance Passing through the {@link ModifierWorkbenchScreen} instance as an argument.
     * @param iElement Passing through the {@link IElement} iElement as an argument.
     * @return Returns null.
     */
    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Liskallia/vault/client/gui/screen/block/ModifierWorkbenchScreen;addElement(Liskallia/vault/client/gui/framework/element/spi/IElement;)Liskallia/vault/client/gui/framework/element/spi/IElement;", ordinal = 5), remap = false)
    IElement RemoveAddElement(ModifierWorkbenchScreen instance, IElement iElement)
    {
        return null;
    }

    /**
     * Modifies the {@link Integer} X argument of the {@link ButtonElement} used for crafting in the {@link ModifierWorkbenchScreen}.
     * @param x The {@link Integer} X to modify.
     * @return Returns the modified {@link Integer} X.
     */
    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Liskallia/vault/client/gui/framework/spatial/Spatials;positionXY(II)Liskallia/vault/client/gui/framework/spatial/spi/IMutableSpatial;", ordinal = 4), index = 0, remap = false)
    int ModifyCraftButtonX(int x)
    {
        return 142;
    }

    /**
     * Modifies the {@link Integer} Y argument of the {@link ButtonElement} used for crafting in the {@link ModifierWorkbenchScreen}.
     * @param y The {@link Integer} Y to modify.
     * @return Returns the modified {@link Integer} Y.
     */
    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Liskallia/vault/client/gui/framework/spatial/Spatials;positionXY(II)Liskallia/vault/client/gui/framework/spatial/spi/IMutableSpatial;", ordinal = 4), index = 1, remap = false)
    int ModifyCraftButtonY(int y)
    {
        return 105;
    }

    /**
     * Modifies the tooltip of the {@link ButtonElement} craftButton in the {@link ModifierWorkbenchScreen} to use the {@link ExtendedInventoryUtils#GetMissingInputs(List, OverSizedInventory)} helper, ignoring the {@link Inventory} playerInventory.
     * @param inputs Passing through the {@link List<ItemStack>} inputs as an argument.
     * @param playerInventory Passing through the {@link Inventory} playerInventory as an argument.
     * @return Returns the modified outcome.
     */
    @Redirect(method = "lambda$new$8", at = @At(value = "INVOKE", target = "Liskallia/vault/util/InventoryUtil;getMissingInputs(Ljava/util/List;Lnet/minecraft/world/entity/player/Inventory;)Ljava/util/List;", ordinal = 0), remap = false)
    List<ItemStack> ModifyCraftButtonTooltip(List<ItemStack> inputs, Inventory playerInventory)
    {
        return ExtendedInventoryUtils.GetMissingInputs(inputs, ((IOverSizedInventory)getMenu().getTileEntity()).getOverSizedInventory());
    }

    /**
     * Modifies the active state of the {@link ButtonElement} craftButton in the {@link ModifierWorkbenchScreen} to use the {@link ExtendedInventoryUtils#GetMissingInputs(List, OverSizedInventory)} helper, ignoring the {@link Inventory} playerInventory.
     * @param inputs Passing through the {@link List<ItemStack>} inputs as an argument.
     * @param playerInventory Passing through the {@link Inventory} playerInventory as an argument.
     * @return Returns the modified outcome.
     */
    @Redirect(method = "lambda$new$9", at = @At(value = "INVOKE", target = "Liskallia/vault/util/InventoryUtil;getMissingInputs(Ljava/util/List;Lnet/minecraft/world/entity/player/Inventory;)Ljava/util/List;", ordinal = 0), remap = false)
    List<ItemStack> ModifyCraftButtonSetDisabled(List<ItemStack> inputs, Inventory playerInventory)
    {
        return ExtendedInventoryUtils.GetMissingInputs(inputs, ((IOverSizedInventory)getMenu().getTileEntity()).getOverSizedInventory());
    }

    /**
     * Modifies the {@link ModifierWorkbenchScreen#tryCraft()} method to use the {@link ExtendedInventoryUtils#GetMissingInputs(List, OverSizedInventory)} helper, ignoring the {@link Inventory} playerInventory.
     * @param inputs Passing through the {@link List<ItemStack>} inputs as an argument.
     * @param playerInventory Passing through the {@link Inventory} playerInventory as an argument.
     * @return Returns the modified outcome.
     */
    @Redirect(method = "tryCraft", at = @At(value = "INVOKE", target = "Liskallia/vault/util/InventoryUtil;getMissingInputs(Ljava/util/List;Lnet/minecraft/world/entity/player/Inventory;)Ljava/util/List;", ordinal = 0), remap = false)
    List<ItemStack> ModifyTryCraft(List<ItemStack> inputs, Inventory playerInventory)
    {
        return ExtendedInventoryUtils.GetMissingInputs(inputs, ((IOverSizedInventory)getMenu().getTileEntity()).getOverSizedInventory());
    }

    //endregion
}