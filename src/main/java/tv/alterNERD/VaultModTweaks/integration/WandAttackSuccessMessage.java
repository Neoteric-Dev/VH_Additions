package tv.alterNERD.VaultModTweaks.integration;

import iskallia.vault.gear.attribute.type.VaultGearAttributeTypeMerger;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.mana.Mana;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * A message to send to the server when a wand attack is successful.
 * Successful refers to it NOT hitting a block, rather than hitting an entity.
 */
public class WandAttackSuccessMessage
{
    //region Initialisation

    /**
     * A constructor for the {@link WandAttackSuccessMessage}.
     */
    public WandAttackSuccessMessage() {}

    //endregion

    //region Methods

    /**
     * Encodes the message into a {@link FriendlyByteBuf} buffer.
     * @param buffer The container for the encoded message.
     */
    public void Encode(FriendlyByteBuf buffer) {}

    /**
     * Decodes the message from a {@link FriendlyByteBuf} buffer.
     * @param buffer The container for the encoded message.
     */
    public static WandAttackSuccessMessage Decode(FriendlyByteBuf buffer)
    {
        return new WandAttackSuccessMessage();
    }

    /**
     * Handles the message on the server side.
     * @param contextSupplier The context of the message.
     * @return Returns true if the message was successfully handled.
     */
    public boolean Handle(Supplier<NetworkEvent.Context> contextSupplier)
    {
        if (contextSupplier.get().getSender() instanceof ServerPlayer)
        {
            ServerPlayer player = contextSupplier.get().getSender();

            if (player != null)
            {
                Mana.decrease(player, VaultGearData.read(player.getMainHandItem()).get(ModGearAttributes.ABILITY_POWER, VaultGearAttributeTypeMerger.floatSum()));

                contextSupplier.get().setPacketHandled(true);

                return true;
            }
        }

        return false;
    }

    //endregion
}