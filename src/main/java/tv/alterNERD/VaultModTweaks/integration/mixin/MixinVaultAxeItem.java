package tv.alterNERD.VaultModTweaks.integration.mixin;

import iskallia.vault.item.gear.VaultAxeItem;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Modifies the {@link VaultAxeItem} to be used in the offhand instead of mainhand.
 */
@Mixin(VaultAxeItem.class)
public abstract class MixinVaultAxeItem
{
    //region Mixins

    /**
     * Inject new the alternate hand into the {@link VaultAxeItem#getIntendedSlot(ItemStack)} method.
     * @param stack Passing through the {@link ItemStack} stack as an argument.
     * @param callbackInformationReturnable The {@link CallbackInfoReturnable} for the injection.
     */
    @Inject(method = "getIntendedSlot", at = @At(value = "HEAD"), cancellable = true, remap = false)
    public void InjectMainhandIntendedSlot(ItemStack stack, CallbackInfoReturnable<EquipmentSlot> callbackInformationReturnable)
    {
        callbackInformationReturnable.setReturnValue(EquipmentSlot.OFFHAND);
        callbackInformationReturnable.cancel();
    }

    /**
     * Sets the axe to not damage the {@link LivingEntity} target.
     * @param stack Passing through the {@link ItemStack} stack as an argument.
     * @param target Passing through the {@link LivingEntity} target as an argument.
     * @param attacker Passing through the {@link LivingEntity} attacker as an argument.
     * @param callbackInformationReturnable The {@link CallbackInfoReturnable} for the injection.
     */
    @Inject(method = "hurtEnemy", at = @At("HEAD"), cancellable = true)
    public void InjectHurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker, CallbackInfoReturnable<Boolean> callbackInformationReturnable)
    {
        callbackInformationReturnable.setReturnValue(false);
        callbackInformationReturnable.cancel();
    }

    //endregion
}