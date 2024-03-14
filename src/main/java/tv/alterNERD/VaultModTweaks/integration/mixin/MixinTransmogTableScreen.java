package tv.alterNERD.VaultModTweaks.integration.mixin;

import iskallia.vault.client.gui.framework.element.NineSliceElement;
import iskallia.vault.client.gui.framework.render.spi.IElementRenderer;
import iskallia.vault.client.gui.framework.render.spi.ITooltipRendererFactory;
import iskallia.vault.client.gui.framework.screen.AbstractElementContainerScreen;
import iskallia.vault.client.gui.screen.block.TransmogTableScreen;
import iskallia.vault.container.TransmogTableContainer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Modifies the {@link TransmogTableScreen} to increase the height of the {@link NineSliceElement} to allow space for the Silver, Gold and Platinum coin slots.
 */
@Mixin(TransmogTableScreen.class)
public abstract class MixinTransmogTableScreen extends AbstractElementContainerScreen<TransmogTableContainer>
{
    //region Initialisation

    /**
     * A constructor for the {@link MixinTransmogTableScreen}.
     * @param container Passing through the {@link TransmogTableContainer} container as an argument.
     * @param inventory Passing through the {@link Inventory} inventory as an argument.
     * @param title Passing through the {@link Component} title as an argument.
     * @param elementRenderer Passing through the {@link IElementRenderer} elementRenderer as an argument.
     * @param tooltipRendererFactory Passing through the {@link ITooltipRendererFactory} tooltipRendererFactory as an argument.
     */
    public MixinTransmogTableScreen(TransmogTableContainer container, Inventory inventory, Component title, IElementRenderer elementRenderer, ITooltipRendererFactory<AbstractElementContainerScreen<TransmogTableContainer>> tooltipRendererFactory)
    {
        super(container, inventory, title, elementRenderer, tooltipRendererFactory);
    }

    //endregion

    //region Mixins

    ///**
    // * Modifies the 'Y' position of the {@link NineSliceElement} when creating the {@link TransmogTableScreen} screen.
    // * @param spatial Passing through the {@link ISpatial} spatial as an argument.
    // * @param textureRegion Passing through the {@link NineSlice.TextureRegion} textureRegion as an argument.
    // * @return Returns the modified {@link NineSliceElement}.
    // */
    //@Redirect(method = "<init>", at = @At(value = "NEW", target = "(Liskallia/vault/client/gui/framework/spatial/spi/ISpatial;Liskallia/vault/client/gui/framework/render/NineSlice$TextureRegion;)Liskallia/vault/client/gui/framework/element/NineSliceElement;"))
    //NineSliceElement ModifyNineSliceYPosition(ISpatial spatial, NineSlice.TextureRegion textureRegion)
    //{
    //    return new NineSliceElement(Spatials.positionXY(0, -33).size(0, 10), textureRegion);
    //}

    /**
     * Modifies the height of the {@link NineSliceElement} when creating the {@link TransmogTableScreen} screen.
     * @param height Passing through the {@link Integer} height as an argument.
     * @return Returns the modified {@link Integer} height.
     */
    @ModifyArg(method = "lambda$new$0", at = @At(value = "INVOKE", target = "Liskallia/vault/client/gui/framework/spatial/Spatials;size(II)Liskallia/vault/client/gui/framework/spatial/spi/IMutableSpatial;", ordinal = 0), index = 1)
    private static int ModifyNineSliceHeight(int height)
    {
        return 33;
    }

    //endregion
}