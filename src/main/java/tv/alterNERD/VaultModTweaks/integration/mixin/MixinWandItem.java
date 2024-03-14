package tv.alterNERD.VaultModTweaks.integration.mixin;

import iskallia.vault.item.BasicItem;
import iskallia.vault.item.gear.WandItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tv.alterNERD.VaultModTweaks.VaultModTweaks;

/**
 * Modifies the {@link WandItem} to be used in the mainhand instead of offhand.
 */
@Mixin(WandItem.class)
public abstract class MixinWandItem extends BasicItem
{
    //region Initialisation

    /**
     * A constructor for the {@link MixinWandItem}.
     * @param id Passing through the {@link ResourceLocation} id as an argument.
     */
    public MixinWandItem(ResourceLocation id)
    {
        super(id);
    }

    //endregion

    //region Mixins

    /**
     * Inject new the alternate hand into the {@link WandItem#getIntendedSlot(ItemStack)} method.
     * @param stack Passing through the {@link ItemStack} stack as an argument.
     * @param callbackInformationReturnable The {@link CallbackInfoReturnable} for the injection.
     */
    @Inject(method = "getIntendedSlot", at = @At(value = "HEAD"), cancellable = true, remap = false)
    public void InjectMainhandIntendedSlot(ItemStack stack, CallbackInfoReturnable<EquipmentSlot> callbackInformationReturnable)
    {
        callbackInformationReturnable.setReturnValue(EquipmentSlot.MAINHAND);
        callbackInformationReturnable.cancel();
    }

    /**
     * Sets the wand to damage the {@link LivingEntity} target.
     * @param stack Passing through the {@link ItemStack} stack as an argument.
     * @param target Passing through the {@link LivingEntity} target as an argument.
     * @param attacker Passing through the {@link LivingEntity} attacker as an argument.
     */
    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker)
    {
        stack.hurtAndBreak(1, attacker, (targetEntity) -> targetEntity.broadcastBreakEvent(EquipmentSlot.MAINHAND));

        return true;
    }

    //endregion
}