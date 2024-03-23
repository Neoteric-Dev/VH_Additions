package tv.alterNERD.VaultModTweaks.integration.mixin;

import iskallia.vault.block.entity.TransmogTableTileEntity;
import iskallia.vault.container.inventory.TransmogTableInventory;
import iskallia.vault.container.spi.RecipeInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/**
 * Modifies the {@link TransmogTableInventory} to allow Vault Silver, Gold and Platinum to be used in the Vault Artisan Station.
 */
@Mixin(TransmogTableInventory.class)
public abstract class MixinTransmogTableInventory extends RecipeInventory
{
    //region Initialisation

    /**
     * A constructor for the {@link MixinTransmogTableInventory}.
     * @param tileEntity Passing through the {@link TransmogTableTileEntity} tileEntity as an argument.
     */
    public MixinTransmogTableInventory(TransmogTableTileEntity tileEntity)
    {
        super(5, tileEntity);
    }

    //endregion

    //region Mixins

    /**
     * Modifies the {@link TransmogTableInventory} inventory to give it '6' inventory spaces instead of the default '3'.
     * This will allow Vault Silver, Gold and Platinum to have slots available.
     * @param size Passing through the {@link Integer} size as an argument.
     * @return Returns the modified inventory size.
     */
    @ModifyConstant(method = "<init>", constant = @Constant(intValue = 2))
    private static int ModifyInventorySize(int size)
    {
        return 5;
    }

    //endregion
}