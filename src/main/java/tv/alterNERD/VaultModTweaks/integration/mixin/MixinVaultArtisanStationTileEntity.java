package tv.alterNERD.VaultModTweaks.integration.mixin;

import iskallia.vault.block.entity.VaultArtisanStationTileEntity;
import iskallia.vault.container.oversized.OverSizedInventory;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.*;

/**
 * Modifies the {@link VaultArtisanStationTileEntity} class to allow Silver, Gold and Platinum Coins to be used in the Vault Artisan Station.
 */
@Mixin(VaultArtisanStationTileEntity.class)
public abstract class MixinVaultArtisanStationTileEntity extends BlockEntity
{
    //region Shadow Fields

    /**
     * Shadows the {@link OverSizedInventory} inventory field in the {@link VaultArtisanStationTileEntity} to give it '15' inventory spaces instead of the default '12'.
     * This will allow Silver, Gold and Platinum coins to have slots available.
     */
    @Shadow
    private final OverSizedInventory inventory = new OverSizedInventory(15, this);

    //endregion

    //region Initialisation

    /**
     * A constructor for the {@link MixinVaultArtisanStationTileEntity}
     * @param p_155228_ Passing through the {@link BlockEntityType} as an argument.
     * @param p_155229_ Passing through the {@link BlockPos} as an argument.
     * @param p_155230_ Passing through the {@link BlockState} as an argument.
     */
    public MixinVaultArtisanStationTileEntity(BlockEntityType<?> p_155228_, BlockPos p_155229_, BlockState p_155230_)
    {
        super(p_155228_, p_155229_, p_155230_);
    }

    //endregion
}