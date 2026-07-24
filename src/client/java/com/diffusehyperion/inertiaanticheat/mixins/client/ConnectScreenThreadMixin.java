package com.diffusehyperion.inertiaanticheat.mixins.client;

import com.diffusehyperion.inertiaanticheat.common.interfaces.UpgradedClientLoginNetworkHandler;
import com.diffusehyperion.inertiaanticheat.common.interfaces.UpgradedConnectScreen;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.multiplayer.ClientHandshakePacketListenerImpl;
import net.minecraft.client.multiplayer.TransferState;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.LevelLoadTracker;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

// targets anonymous thread class in ConnectScreen.connect(client, address, ...)
@Mixin(targets = "net.minecraft.client.gui.screens.ConnectScreen$1")
public class ConnectScreenThreadMixin {
    @Shadow
    @Final
    Minecraft val$minecraft;

    @Shadow
    @Final
    ServerData val$server;

    @Shadow
    @Final
    TransferState val$transferState;

    @Shadow
    @Final
    ConnectScreen this$0;

    @Inject(
            method = "run",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/Connection;initiateServerboundPlayConnection(Ljava/lang/String;ILnet/minecraft/network/ProtocolInfo;Lnet/minecraft/network/ProtocolInfo;Lnet/minecraft/network/ClientboundPacketListener;Z)V"
            )
    )
    private void createUpgradedLoginNetworkHandler(
            CallbackInfo ci,
            @Share("loginNetworkHandler") LocalRef<ClientHandshakePacketListenerImpl> loginNetworkHandlerLocalRef
    ) {
        ConnectScreenAccessor accessor = (ConnectScreenAccessor) this$0;
        ClientHandshakePacketListenerImpl handler = new ClientHandshakePacketListenerImpl(
                accessor.getConnection(),
                val$minecraft,
                val$server,
                accessor.getParent(),
                false,
                null,
                accessor::invokeUpdateStatus,
                new LevelLoadTracker(),
                val$transferState
        );

        UpgradedClientLoginNetworkHandler upgradedHandler = (UpgradedClientLoginNetworkHandler) handler;
        UpgradedConnectScreen upgradedScreen = (UpgradedConnectScreen) this$0;

        upgradedHandler.inertiaAntiCheat$setSecondaryStatusConsumer(upgradedScreen::inertiaAntiCheat$setSecondaryStatus);

        loginNetworkHandlerLocalRef.set(handler);
    }

    // ModifyArg had weird generic issues, this will work
    @ModifyArgs(
            method = "run",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/Connection;initiateServerboundPlayConnection(Ljava/lang/String;ILnet/minecraft/network/ProtocolInfo;Lnet/minecraft/network/ProtocolInfo;Lnet/minecraft/network/ClientboundPacketListener;Z)V"
            )
    )
    private void replaceLoginNetworkHandler(Args args, @Share("loginNetworkHandler") LocalRef<ClientHandshakePacketListenerImpl> loginNetworkHandlerLocalRef) {
        args.set(4, loginNetworkHandlerLocalRef.get());
    }
}
