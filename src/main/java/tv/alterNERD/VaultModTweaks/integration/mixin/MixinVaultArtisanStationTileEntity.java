package tv.alterNERD.VaultModTweaks.integration.mixin;

import iskallia.vault.block.entity.VaultArtisanStationTileEntity;
import iskallia.vault.container.oversized.OverSizedInventory;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.*;

/**
 * Modifies the {@link VaultArtisanStationTileEntity} class to allow silver, gold and platinum coins to be used in the artisan station.
 */
@Mixin(VaultArtisanStationTileEntity.class)
public abstract class MixinVaultArtisanStationTileEntity
{
    /**
     * Shadows the {@link OverSizedInventory} inventory field in the {@link VaultArtisanStationTileEntity} to give it '15' inventory spaces instead of the default '12'.
     * This will allow silver, gold and platinum coins to have slots available.
     */
    @Final
    @Shadow
    private final OverSizedInventory inventory = new OverSizedInventory(15, (BlockEntity)(Object)this);
}