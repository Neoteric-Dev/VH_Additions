package tv.alterNERD.VaultModTweaks.integration;

import iskallia.vault.VaultMod;
import iskallia.vault.gear.attribute.VaultGearAttribute;
import iskallia.vault.gear.attribute.config.ConfigurableAttributeGenerator;
import iskallia.vault.gear.attribute.type.VaultGearAttributeType;
import iskallia.vault.gear.comparator.VaultGearAttributeComparator;
import iskallia.vault.gear.reader.VaultGearModifierReader;
import iskallia.vault.init.ModGearAttributeGenerators;
import iskallia.vault.init.ModGearAttributeReaders;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nullable;

/**
 * Adds additional {@link VaultGearAttribute} Vault Gear Attributes.
 */
public final class AdditionGearAttributes
{
    //region Fields

    /**
     * An attribute for getting the mana cost to use a Wand.
     */
    public final static VaultGearAttribute<Float> MANA_COST = CreateAttribute("wand_mana_cost", VaultGearAttributeType.floatType(), ModGearAttributeGenerators.floatRange(), ModGearAttributeReaders.addedDecimalReader("Mana Cost", 3236013), VaultGearAttributeComparator.floatComparator());

    /**
     * An attribute for determining the damage dealt by a Wand.
     */
    public final static VaultGearAttribute<Float> MAGIC_POWER = CreateAttribute("wand_magic_power", VaultGearAttributeType.floatType(), ModGearAttributeGenerators.floatRange(), ModGearAttributeReaders.addedDecimalReader("Ability Power Percentage", 6762925), VaultGearAttributeComparator.floatComparator());

    //endregion

    //region Initialisation

    /**
     * Initialises the {@link VaultGearAttribute} Vault Gear Attributes.
     * @param event The {@link RegistryEvent} registry event for registering the {@link VaultGearAttribute} Vault Gear Attributes.
     */
    public static void Initialise(RegistryEvent.Register<VaultGearAttribute<?>> event)
    {
        IForgeRegistry<VaultGearAttribute<?>> registry = event.getRegistry();

        registry.register(MANA_COST);
        registry.register(MAGIC_POWER);
    }

    //endregion

    //region Helpers

    /**
     * A helper method for creating {@link VaultGearAttribute} Vault Gear Attributes.
     * A direct copy of {@link iskallia.vault.init.ModGearAttributes#attr(String, VaultGearAttributeType, ConfigurableAttributeGenerator, VaultGearModifierReader)}.
     * @param name The name of the attribute.
     * @param type The specified type of the value for the attribute.
     * @param generator The generator for creating the attribute value.
     * @param reader The reader for displaying the attribute value.
     * @return Returns the created {@link VaultGearAttribute} Vault Gear Attribute.
     * @param <T> The type of T is the value type of the attribute.
     */
    private static <T> VaultGearAttribute<T> CreateAttribute(String name, VaultGearAttributeType<T> type, ConfigurableAttributeGenerator<T, ?> generator, VaultGearModifierReader<T> reader)
    {
        return CreateAttribute(name, type, generator, reader, null);
    }

    /**
     * A helper method for creating {@link VaultGearAttribute} Vault Gear Attributes.
     * A direct copy of {@link iskallia.vault.init.ModGearAttributes#attr(String, VaultGearAttributeType, ConfigurableAttributeGenerator, VaultGearModifierReader, VaultGearAttributeComparator)}.
     * @param name The name of the attribute.
     * @param type The specified type of the value for the attribute.
     * @param generator The generator for creating the attribute value.
     * @param reader The reader for displaying the attribute value.
     * @param comparator The comparator for comparing the attribute value.
     * @return Returns the created {@link VaultGearAttribute} Vault Gear Attribute.
     * @param <T> The type of T is the value type of the attribute.
     */
    private static <T> VaultGearAttribute<T> CreateAttribute(String name, VaultGearAttributeType<T> type, ConfigurableAttributeGenerator<T, ?> generator, VaultGearModifierReader<T> reader, @Nullable VaultGearAttributeComparator<T> comparator)
    {
        return new VaultGearAttribute(VaultMod.id(name), type, generator, reader, comparator);
    }

    //endregion
}