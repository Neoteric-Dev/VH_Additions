package tv.alterNERD.VaultModTweaks.integration;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import tv.alterNERD.VaultModTweaks.VaultModTweaks;

/**
 * Handles the sending and receiving of packets across the network.
 */
public final class PacketHandler
{
    //region Fields

    /**
     * An instance of a channel for sending packets.
     */
    static final SimpleChannel Instance = NetworkRegistry.ChannelBuilder.named(new ResourceLocation(VaultModTweaks.MOD_ID, "main")).clientAcceptedVersions((version) -> true).serverAcceptedVersions((version) -> true).networkProtocolVersion(() -> String.valueOf(1)).simpleChannel();

    //endregion

    //region Initialisation

    /**
     * Register the packet handler to deal with packets across the network.
     */
    public static void Register()
    {
        Instance.messageBuilder(WandAttackSuccessMessage.class, NetworkDirection.PLAY_TO_SERVER.ordinal()).encoder(WandAttackSuccessMessage::Encode).decoder(WandAttackSuccessMessage::Decode).consumer(WandAttackSuccessMessage::Handle).add();
    }

    //endregion

    //region Methods

    /**
     * Sends a packet to the server across the network.
     * @param message The content of the packet.
     */
    public static void sendToServer(Object message)
    {
        Instance.send(PacketDistributor.SERVER.noArg(), message);
    }

    //endregion
}