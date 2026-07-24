package com.diffusehyperion.inertiaanticheat.mixins.server;

import com.diffusehyperion.inertiaanticheat.common.interfaces.UpgradedServerLoginNetworkHandler;
import com.mojang.authlib.GameProfile;
import net.minecraft.network.Connection;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ServerLoginPacketListenerImpl.class)
public abstract class ServerLoginPacketListenerImplMixin implements UpgradedServerLoginNetworkHandler {
    @Shadow @Final
    Connection connection;

    @Shadow private @Nullable GameProfile authenticatedProfile;

    @Override
    public Connection inertiaAntiCheat$getConnection() {
        return this.connection;
    }

    @Override
    public GameProfile inertiaAntiCheat$getGameProfile() {
        return this.authenticatedProfile;
    }
}
