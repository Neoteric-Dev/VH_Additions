package tv.alterNERD.VaultModTweaks.integration.mixin;

import iskallia.vault.gear.attribute.VaultGearAttribute;
import iskallia.vault.gear.attribute.config.ConfigurableAttributeGenerator;
import iskallia.vault.gear.attribute.type.VaultGearAttributeType;
import iskallia.vault.gear.comparator.VaultGearAttributeComparator;
import iskallia.vault.gear.reader.VaultGearModifierReader;
import iskallia.vault.init.ModGearAttributeGenerators;
import iskallia.vault.init.ModGearAttributeReaders;
import iskallia.vault.init.ModGearAttributes;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.IForgeRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import javax.annotation.Nullable;

@Mixin(ModGearAttributes.class)
public abstract class MixinModGearAttributes
{
    //region Fields

    private static VaultGearAttribute<Float> MANA_COST = attr("mana_cost", VaultGearAttributeType.floatType(), ModGearAttributeGenerators.floatRange(), ModGearAttributeReaders.addedDecimalReader("Mana Cost", 3236013), VaultGearAttributeComparator.floatComparator());
    private static VaultGearAttribute<Float> ABILITY_POWER_PERCENTAGE = attr("ability_power_percentage", VaultGearAttributeType.floatType(), ModGearAttributeGenerators.floatRange(), ModGearAttributeReaders.addedDecimalReader("Ability Power Percentage", 6762925), VaultGearAttributeComparator.floatComparator());

    @Shadow
    private static <T> VaultGearAttribute<T> attr(String name, VaultGearAttributeType<T> type, ConfigurableAttributeGenerator<T, ?> generator, VaultGearModifierReader<T> reader, @Nullable VaultGearAttributeComparator<T> comparator)
    {
        return null;
    }

    public VaultGearAttribute<Float> GetManaCost()
    {
        return MANA_COST;
    }

    public VaultGearAttribute<Float> GetAbilityPowerPercentage()
    {
        return ABILITY_POWER_PERCENTAGE;
    }

    @Inject(method = "init", at = @At("RETURN"), locals = LocalCapture.CAPTURE_FAILHARD, remap = false)
    private static void InjectModGearAttributes(RegistryEvent.Register<VaultGearAttribute<?>> event, CallbackInfo callbackInformation, IForgeRegistry<VaultGearAttribute<?>> registry)
    {
        registry.register(MANA_COST);
        registry.register(ABILITY_POWER_PERCENTAGE);
    }
}