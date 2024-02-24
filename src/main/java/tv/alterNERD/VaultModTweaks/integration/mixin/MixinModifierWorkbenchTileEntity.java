package tv.alterNERD.VaultModTweaks.integration.mixin;

import iskallia.vault.block.entity.ModifierWorkbenchTileEntity;
import iskallia.vault.container.oversized.OverSizedInventory;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tv.alterNERD.VaultModTweaks.integration.IOverSizedInventory;

/**
 * Modifies the {@link ModifierWorkbenchTileEntity} to add slots for Vault Gold, Nullifying Focus and Amplifying Focus.
 */
@Mixin(ModifierWorkbenchTileEntity.class)
public abstract class MixinModifierWorkbenchTileEntity extends BlockEntity implements IOverSizedInventory
{
    //region Shadow Fields

    /**
     * Adds an {@link OverSizedInventory} to the {@link ModifierWorkbenchTileEntity}.
     * This allows Gold Coins, Nullifying Focus and Amplifying Focus to be used in the Modifier Workbench.
     */
    final OverSizedInventory overSizedInventory = new OverSizedInventory(3, this);

    //endregion

    //region Getters

    /**
     * Gets the {@link OverSizedInventory} from the {@link ModifierWorkbenchTileEntity}.
     * @return Returns the {@link OverSizedInventory}.
     */
    public OverSizedInventory getOverSizedInventory()
    {
        return overSizedInventory;
    }

    //endregion

    //region Initialisation

    /**
     * A constructor for the {@link MixinModifierWorkbenchTileEntity}.
     * @param p_155228_ Passing through the {@link BlockEntityType} as an argument.
     * @param p_155229_ Passing through the {@link BlockPos} as an argument.
     * @param p_155230_ Passing through the {@link BlockState} as an argument.
     */
    public MixinModifierWorkbenchTileEntity(BlockEntityType<?> p_155228_, BlockPos p_155229_, BlockState p_155230_)
    {
        super(p_155228_, p_155229_, p_155230_);
    }

    //endregion

    //region Mixins

    /**
     * Inject new load information into the {@link ModifierWorkbenchTileEntity#load(CompoundTag)} method.
     * @param tag Passing through the {@link CompoundTag} tag as an argument.
     * @param callbackInformation The {@link CallbackInfo} for the injection.
     */
    @Inject(method = "load", at = @At(value = "RETURN"))
    public void load(CompoundTag tag, CallbackInfo callbackInformation)
    {
        overSizedInventory.load(tag);
    }

    /**
     * Inject new load information into the {@link ModifierWorkbenchTileEntity#saveAdditional(CompoundTag)} method.
     * @param tag Passing through the {@link CompoundTag} tag as an argument.
     * @param callbackInformation The {@link CallbackInfo} for the injection.
     */
    @Inject(method = "saveAdditional", at = @At(value = "RETURN"))
    protected void saveAdditional(CompoundTag tag, CallbackInfo callbackInformation)
    {
        overSizedInventory.save(tag);
    }

    //endregion
}