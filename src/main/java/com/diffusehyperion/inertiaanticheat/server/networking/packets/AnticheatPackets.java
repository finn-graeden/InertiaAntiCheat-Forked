package com.diffusehyperion.inertiaanticheat.server.networking.packets;

import com.diffusehyperion.inertiaanticheat.common.networking.packets.S2C.AnticheatDetailsS2CPacket;
import com.diffusehyperion.inertiaanticheat.common.util.InertiaAntiCheatConstants;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.PacketType;

public class AnticheatPackets {
    public static final PacketType<AnticheatDetailsS2CPacket> DETAILS_RESPONSE = new PacketType<>(PacketFlow.CLIENTBOUND, InertiaAntiCheatConstants.ANTICHEAT_DETAILS_ID);
}
