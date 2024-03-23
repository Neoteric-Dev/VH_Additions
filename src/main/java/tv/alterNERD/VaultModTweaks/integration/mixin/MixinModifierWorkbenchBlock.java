package tv.alterNERD.VaultModTweaks.integration.mixin;

import iskallia.vault.block.ModifierWorkbenchBlock;
import iskallia.vault.block.base.FacedBlock;
import iskallia.vault.block.entity.ModifierWorkbenchTileEntity;
import iskallia.vault.container.oversized.OverSizedInventory;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Containers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import tv.alterNERD.VaultModTweaks.integration.IOverSizedInventory;

/**
 * Modifies the {@link ModifierWorkbenchBlock} to drop items from the {@link OverSizedInventory} when the block is broken.
 */
@Mixin(ModifierWorkbenchBlock.class)
public abstract class MixinModifierWorkbenchBlock extends FacedBlock implements EntityBlock
{
    //region Initialisation

    /**
     * A constructor for the {@link MixinModifierWorkbenchBlock}.
     * @param properties Passing through the {@link Properties} as an argument.
     */
    public MixinModifierWorkbenchBlock(Properties properties)
    {
        super(properties);
    }

    //endregion

    //region Mixins

    /**
     * Injects the {@link OverSizedInventory} into the {@link ModifierWorkbenchTileEntity} when the {@link ModifierWorkbenchBlock} block is broken in world.
     * @param state Passing through the {@link BlockState} state as an argument.
     * @param level Passing through the {@link Level} level as an argument.
     * @param pos Passing through the {@link BlockPos} pos as an argument.
     * @param newState Passing through the {@link BlockState} newState as an argument.
     * @param isMoving Passing through the {@link Boolean} isMoving as an argument.
     * @param callbackInformation The {@link CallbackInfo} for the injection.
     * @param tile Passing through the {@link BlockEntity} tile as an argument.
     * @param workbench Passing through the {@link ModifierWorkbenchTileEntity} workbench as an argument.
     */
    @Inject(method = "onRemove", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/Containers;dropContents(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/Container;)V", ordinal = 0), locals = LocalCapture.CAPTURE_FAILHARD)
    void ModifyOnRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving, CallbackInfo callbackInformation, BlockEntity tile, ModifierWorkbenchTileEntity workbench)
    {
        OverSizedInventory overSizedInventory = ((IOverSizedInventory)workbench).getOverSizedInventory();
        Containers.dropContents(level, pos, overSizedInventory);
        overSizedInventory.clearContent();
    }

    //endregion
}