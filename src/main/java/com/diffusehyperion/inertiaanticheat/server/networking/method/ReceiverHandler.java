package com.diffusehyperion.inertiaanticheat.server.networking.method;

import com.diffusehyperion.inertiaanticheat.common.util.InertiaAntiCheatConstants;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerLoginNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;
import net.minecraft.resources.Identifier;

import java.security.KeyPair;

public abstract class ReceiverHandler {
    protected final KeyPair keyPair;
    protected final Identifier modTransferID;

    public ReceiverHandler(KeyPair keyPair, Identifier modTransferID, ServerLoginPacketListenerImpl handler) {
        this.keyPair = keyPair;
        this.modTransferID = modTransferID;

        ServerLoginNetworking.registerReceiver(handler, InertiaAntiCheatConstants.SEND_MOD, this::receiveMod);
    }

    protected abstract void receiveMod(MinecraftServer minecraftServer, ServerLoginPacketListenerImpl serverLoginNetworkHandler, boolean b, FriendlyByteBuf buf, ServerLoginNetworking.LoginSynchronizer synchronizer, PacketSender packetSender);
}
