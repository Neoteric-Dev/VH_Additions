package tv.alterNERD.VaultModTweaks.integration.mixin;

import iskallia.vault.event.RegistryEvents;
import iskallia.vault.gear.attribute.VaultGearAttribute;
import net.minecraftforge.event.RegistryEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tv.alterNERD.VaultModTweaks.integration.AdditionGearAttributes;

/**
 * Adds additional {@link VaultGearAttribute} Vault Gear Attributes.
 */
@Mixin(RegistryEvents.class)
public class MixinRegistryEvents
{
    //region Mixins

    /**
     * Injects an addition group of {@link VaultGearAttribute} Vault Gear Attributes.
     * @param event Passing through the {@link RegistryEvent} for the injection.
     * @param callbackInformation The {@link CallbackInfo} for the injection.
     */
    @Inject(method = "onGearAttributeRegistry", at = @At(value = "INVOKE", target = "Liskallia/vault/init/ModGearAttributes;registerVanillaAssociations()V", ordinal = 0), remap = false)
    private static void InjectVaultGearAttributes(RegistryEvent.Register<VaultGearAttribute<?>> event, CallbackInfo callbackInformation)
    {
        AdditionGearAttributes.Initialise(event);
    }
    //endregion
}