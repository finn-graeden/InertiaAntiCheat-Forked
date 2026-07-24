package com.diffusehyperion.inertiaanticheat.client.networking.method.name;

import com.diffusehyperion.inertiaanticheat.client.InertiaAntiCheatClient;
import com.diffusehyperion.inertiaanticheat.client.networking.method.TransferHandler;
import com.diffusehyperion.inertiaanticheat.common.util.InertiaAntiCheatConstants;
import io.netty.channel.ChannelFutureListener;
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientHandshakePacketListenerImpl;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import static com.diffusehyperion.inertiaanticheat.client.InertiaAntiCheatClient.debugInfo;
import static com.diffusehyperion.inertiaanticheat.client.InertiaAntiCheatClient.debugLine;

public class ClientNameTransferHandler extends TransferHandler {
    private final int maxIndex;
    private int currentIndex;

    public ClientNameTransferHandler(PublicKey publicKey, Identifier modTransferID, Consumer<Component> secondaryStatusConsumer) {
        super(publicKey, modTransferID, secondaryStatusConsumer, InertiaAntiCheatClient.allModNames.size());

        debugInfo("Creating name transfer handler");

        this.maxIndex = InertiaAntiCheatClient.allModNames.size();
        this.currentIndex = 0;
    }

    @Override
    public CompletableFuture<FriendlyByteBuf> transferMod(Minecraft client, ClientHandshakePacketListenerImpl handler, FriendlyByteBuf buf, Consumer<ChannelFutureListener> callbacksConsumer) {
        debugInfo("Sending mod " + this.currentIndex);

        if (this.currentIndex >= this.maxIndex) {
            // All files have been sent, returning null to signify goodbye
            debugInfo("Sending final packet");
            debugLine();

            this.setCompleteTransferStatus();

            ClientLoginNetworking.unregisterGlobalReceiver(InertiaAntiCheatConstants.SEND_MOD);
            return CompletableFuture.completedFuture(null);
        }
        FriendlyByteBuf responseBuf = this.preparePacket(InertiaAntiCheatClient.allModNames.get(currentIndex).getBytes(StandardCharsets.UTF_8));

        this.increaseSentModsStatus();
        this.currentIndex++;

        debugLine();
        return CompletableFuture.completedFuture(responseBuf);
    }
}
