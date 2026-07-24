package com.diffusehyperion.inertiaanticheat.client.networking.method;

import com.diffusehyperion.inertiaanticheat.common.InertiaAntiCheat;
import com.diffusehyperion.inertiaanticheat.common.util.InertiaAntiCheatConstants;
import io.netty.channel.ChannelFutureListener;
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginNetworking;
import net.fabricmc.fabric.api.networking.v1.FriendlyByteBufs;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientHandshakePacketListenerImpl;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import javax.crypto.SecretKey;
import java.security.PublicKey;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public abstract class TransferHandler {
    protected final PublicKey publicKey;
    protected final Identifier modTransferID;
    protected final Consumer<Component> secondaryStatusConsumer;

    private int sentMods;
    private final int totalMods;
    
    public TransferHandler(PublicKey publicKey, Identifier modTransferID, Consumer<Component> secondaryStatusConsumer, int totalMods) {
        this.publicKey = publicKey;
        this.modTransferID = modTransferID;
        this.secondaryStatusConsumer = secondaryStatusConsumer;

        this.sentMods = 0;
        this.totalMods = totalMods;
        this.updateSecondaryStatus("Sent 0/" + totalMods + " mods");

        ClientLoginNetworking.registerReceiver(InertiaAntiCheatConstants.SEND_MOD, this::transferMod);
    }

    protected abstract CompletableFuture<FriendlyByteBuf> transferMod(Minecraft client, ClientHandshakePacketListenerImpl handler, FriendlyByteBuf buf, Consumer<ChannelFutureListener> callbacksConsumer);

    public void onDisconnect(ClientHandshakePacketListenerImpl ignored1, Minecraft ignored2) {
        ClientLoginNetworking.unregisterReceiver(this.modTransferID);
    }

    protected FriendlyByteBuf preparePacket(byte[] data) {
        FriendlyByteBuf buf = FriendlyByteBufs.create();

        return this.preparePacket(buf, data);
    }

    protected FriendlyByteBuf preparePacket(FriendlyByteBuf buf, byte[] data) {
        SecretKey secretKey = InertiaAntiCheat.createAESKey();

        byte[] encryptedRSASecretKey = InertiaAntiCheat.encryptRSABytes(secretKey.getEncoded(), this.publicKey);
        byte[] encryptedAESNameData = InertiaAntiCheat.encryptAESBytes(data, secretKey);
        buf.writeInt(encryptedRSASecretKey.length);
        buf.writeBytes(encryptedRSASecretKey);
        buf.writeBytes(encryptedAESNameData);

        return buf;
    }

    protected void setCompleteTransferStatus() {
        this.secondaryStatusConsumer.accept(Component.nullToEmpty("Waiting for validation..."));
    }

    protected void increaseSentModsStatus() {
        this.sentMods++;
        this.updateSecondaryStatus("Sent " + this.sentMods + "/" + this.totalMods + " mods");
    }

    private void updateSecondaryStatus(String message) {
        this.secondaryStatusConsumer.accept(Component.nullToEmpty(message));
    }
}
