package com.diffusehyperion.inertiaanticheat.mixins.client;

import com.diffusehyperion.inertiaanticheat.client.interfaces.UpgradedClientCollection;
import com.diffusehyperion.inertiaanticheat.client.networking.packets.UpgradedClientQueryNetworkHandler;
import com.diffusehyperion.inertiaanticheat.common.interfaces.UpgradedServerInfo;
import com.diffusehyperion.inertiaanticheat.common.networking.packets.UpgradedClientQueryPacketListener;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.client.multiplayer.ServerStatusPinger;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.Connection;
import net.minecraft.server.network.EventLoopGroupHolder;
import net.minecraft.network.protocol.status.ClientStatusPacketListener;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.InetSocketAddress;

@Mixin(ServerStatusPinger.class)
public abstract class ServerStatusPingerMixin {
    @Shadow
    void onPingFailed(Component error, ServerData info) {}
    @Shadow
    void pingLegacyServer(InetSocketAddress socketAddress, ServerAddress address, ServerData serverInfo, EventLoopGroupHolder backend) {}

    @Inject(method = "pingServer",
            at = @At(value = "HEAD"))
    private void setUpgradedServerPingRefs(ServerData entry, Runnable saver, Runnable pingCallback, EventLoopGroupHolder backend, CallbackInfo ci,
                                           @Share("serverInfo") LocalRef<ServerData> serverDataLocalRef,
                                           @Share("saver") LocalRef<Runnable> saverLocalRef,
                                           @Share("pingCallback") LocalRef<Runnable> pingCallbackLocalRef,
                                           @Share("backend") LocalRef<EventLoopGroupHolder> backendLocalRef) {
        serverDataLocalRef.set(entry);
        saverLocalRef.set(saver);
        pingCallbackLocalRef.set(pingCallback);
        backendLocalRef.set(backend);
    }

    @Redirect(method = "pingServer",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/network/Connection;initiateServerboundStatusConnection(Ljava/lang/String;ILnet/minecraft/network/protocol/status/ClientStatusPacketListener;)V"))
    private void upgradeServerPing(
            Connection connection, String host, int port, ClientStatusPacketListener clientQueryPacketListener,
            @Share("serverInfo") LocalRef<ServerData> serverDataLocalRef,
            @Share("saver") LocalRef<Runnable> runnableLocalRef,
            @Share("pingCallback") LocalRef<Runnable> pingCallbackLocalRef,
            @Share("backend") LocalRef<EventLoopGroupHolder> backendLocalRef,
            @Local InetSocketAddress inetSocketAddress,
            @Local ServerAddress serverAddress) {
        ServerData serverInfo = serverDataLocalRef.get();
        Runnable saver = runnableLocalRef.get();
        Runnable pingCallback = pingCallbackLocalRef.get();
        EventLoopGroupHolder backend = backendLocalRef.get();

        UpgradedClientQueryPacketListener listener =
                new UpgradedClientQueryNetworkHandler(serverInfo, saver, pingCallback, backend,
                        connection, inetSocketAddress, serverAddress,
                this::onPingFailed,
                this::pingLegacyServer);

        ((UpgradedServerInfo) serverInfo).inertiaAntiCheat$setInertiaInstalled(null);
        ((UpgradedServerInfo) serverInfo).inertiaAntiCheat$setAnticheatDetails(null);
        ((UpgradedClientCollection) connection).inertiaAntiCheat$connect(host, port, listener);
    }
}