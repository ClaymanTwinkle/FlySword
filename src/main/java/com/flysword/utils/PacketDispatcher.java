package com.flysword.utils;

import com.flysword.network.server.SpawnSwordBeamPacket;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class PacketDispatcher {
    private static byte packetId = 0;

    public static final String CHANNEL = "dsschannel";

    private static final SimpleNetworkWrapper dispatcher = NetworkRegistry.INSTANCE.newSimpleChannel(CHANNEL);

    /**
     *  Registers all packets and handlers - call this during {@link FMLPreInitializationEvent}
     */
    public static final void initialize() {
        PacketDispatcher.dispatcher.registerMessage(SpawnSwordBeamPacket.class, SpawnSwordBeamPacket.class, packetId++, Side.SERVER);
    }

    public static void sendToServer(IMessage message) {
        PacketDispatcher.dispatcher.sendToServer(message);
    }
}
