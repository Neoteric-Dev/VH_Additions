package tv.alterNERD.VaultModTweaks.integration.mixin;

import iskallia.vault.block.ModifierWorkbenchBlock;
import iskallia.vault.block.entity.ModifierWorkbenchTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ModifierWorkbenchTileEntity.class)
public abstract class MixinModifierWorkbenchTileEntity extends BlockEntity
{
    @Shadow
    private final SimpleContainer inventory = new SimpleContainer(4)
    {
        public void setChanged()
        {
            super.setChanged();
            MixinModifierWorkbenchTileEntity.this.setChanged();
        }
    };

    public MixinModifierWorkbenchTileEntity(BlockEntityType<?> p_155228_, BlockPos p_155229_, BlockState p_155230_)
    {
        super(p_155228_, p_155229_, p_155230_);
    }
}