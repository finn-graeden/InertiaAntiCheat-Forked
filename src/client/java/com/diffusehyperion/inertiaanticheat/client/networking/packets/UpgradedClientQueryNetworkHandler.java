package com.diffusehyperion.inertiaanticheat.client.networking.packets;

import com.diffusehyperion.inertiaanticheat.common.interfaces.UpgradedServerInfo;
import com.diffusehyperion.inertiaanticheat.common.networking.packets.S2C.AnticheatDetailsS2CPacket;
import com.diffusehyperion.inertiaanticheat.common.networking.packets.UpgradedClientQueryPacketListener;
import com.diffusehyperion.inertiaanticheat.utils.QuadConsumer;
import net.minecraft.client.multiplayer.ServerStatusPinger;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.Connection;
import net.minecraft.network.DisconnectionDetails;
import net.minecraft.server.network.EventLoopGroupHolder;
import net.minecraft.network.protocol.ping.ServerboundPingRequestPacket;
import net.minecraft.network.protocol.ping.ClientboundPongResponsePacket;
import net.minecraft.network.protocol.status.ClientboundStatusResponsePacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.players.NameAndId;
import net.minecraft.network.protocol.status.ServerStatus;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.util.Util;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;

public class UpgradedClientQueryNetworkHandler implements UpgradedClientQueryPacketListener {
    /* ---------- vanilla fields ----------*/

    private final ServerData entry;
    private final Runnable saver;
    private final Runnable pingCallback;
    private final EventLoopGroupHolder backend;

    private final Connection clientConnection;

    private final InetSocketAddress inetSocketAddress;
    private final ServerAddress serverAddress;

    private final BiConsumer<Component, ServerData> showErrorMethod;
    private final QuadConsumer<InetSocketAddress, ServerAddress, ServerData, EventLoopGroupHolder> pingMethod;

    private boolean sentQuery;
    private boolean received;
    private long startTime;

    public UpgradedClientQueryNetworkHandler(ServerData entry, Runnable saver, Runnable pingCallback, EventLoopGroupHolder backend,
                                             Connection clientConnection,
                                             InetSocketAddress inetSocketAddress, ServerAddress serverAddress,
                                             BiConsumer<Component, ServerData> showErrorMethod,
                                             QuadConsumer<InetSocketAddress, ServerAddress, ServerData, EventLoopGroupHolder> pingMethod) {
        /* ---------- vanilla fields ----------*/

        this.entry = entry;
        this.saver = saver;
        this.pingCallback = pingCallback;
        this.backend = backend;

        this.clientConnection = clientConnection;

        this.inetSocketAddress = inetSocketAddress;
        this.serverAddress = serverAddress;

        this.showErrorMethod = showErrorMethod;
        this.pingMethod = pingMethod;
    }

    @Override
    public void onReceiveAnticheatDetails(AnticheatDetailsS2CPacket var1) {
        ((UpgradedServerInfo) entry).inertiaAntiCheat$setInertiaInstalled(true);
        ((UpgradedServerInfo) entry).inertiaAntiCheat$setAnticheatDetails(var1.details());
    }


    /* ---------- (Mostly) vanilla stuff below ----------*/

    @Override
    public void handleStatusResponse(ClientboundStatusResponsePacket packet) {
        if (this.received) {
            clientConnection.disconnect(Component.translatable("multiplayer.status.unrequested"));
        } else {
            this.received = true;
            ServerStatus serverMetadata = packet.status();
            entry.motd = serverMetadata.description();
            serverMetadata.version().ifPresentOrElse(version -> {
                entry.version = Component.literal(version.name());
                entry.protocol = version.protocol();
            }, () -> {
                entry.version = Component.translatable("multiplayer.status.old");
                entry.protocol = 0;
            });
            serverMetadata.players().ifPresentOrElse(players -> {
                entry.status = ServerStatusPinger.formatPlayerCount(players.online(), players.max());
                entry.players = players;
                if (!players.sample().isEmpty()) {
                    List<Component> list = new ArrayList<>(players.sample().size());

                    for (NameAndId playerConfigEntry : players.sample()) {
                        Component text;
                        if (playerConfigEntry.equals(MinecraftServer.ANONYMOUS_PLAYER_PROFILE)) {
                            text = Component.translatable("multiplayer.status.anonymous_player");
                        } else {
                            text = Component.literal(playerConfigEntry.name());
                        }

                        list.add(text);
                    }

                    if (players.sample().size() < players.online()) {
                        list.add(Component.translatable("multiplayer.status.and_more", players.online() - players.sample().size()));
                    }

                    entry.playerList = list;
                } else {
                    entry.playerList = List.of();
                }
            }, () -> entry.status = Component.translatable("multiplayer.status.unknown").withStyle(ChatFormatting.DARK_GRAY));
            serverMetadata.favicon().ifPresent(favicon -> {
                if (!Arrays.equals(favicon.iconBytes(), entry.getIconBytes())) {
                    entry.setIconBytes(ServerData.validateIcon(favicon.iconBytes()));
                    saver.run();
                }
            });
            this.startTime = Util.getMillis();
            clientConnection.send(new ServerboundPingRequestPacket(this.startTime));
            this.sentQuery = true;
        }
    }

    @Override
    public void handlePongResponse(ClientboundPongResponsePacket packet) {
        long l = this.startTime;
        long m = Util.getMillis();
        entry.ping = m - l;
        this.clientConnection.disconnect(Component.translatable("multiplayer.status.finished"));
        this.pingCallback.run();
    }

    @Override
    public void onDisconnect(DisconnectionDetails info) {
        if (!this.sentQuery) {
            showErrorMethod.accept(info.reason(), entry);
            pingMethod.accept(inetSocketAddress, serverAddress, entry, backend);
        }
    }

    @Override
    public boolean isAcceptingMessages() {
        return this.clientConnection.isConnected();
    }
}
