package com.diffusehyperion.inertiaanticheat.mixins.client;

import com.diffusehyperion.inertiaanticheat.common.interfaces.UpgradedClientLoginNetworkHandler;
import net.minecraft.client.multiplayer.ClientHandshakePacketListenerImpl;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.function.Consumer;

@Mixin(ClientHandshakePacketListenerImpl.class)
public abstract class ClientHandshakePacketListenerImplMixin implements UpgradedClientLoginNetworkHandler {
    @Unique
    private Consumer<Component> secondaryStatusConsumer = (Component text) -> {
        throw new RuntimeException("Tried setting secondary status to " + text + " when it was uninitialized");
    };

    @Override
    public void inertiaAntiCheat$setSecondaryStatusConsumer(Consumer<Component> consumer) {
        this.secondaryStatusConsumer = consumer;
    }

    @Override
    public Consumer<Component> inertiaAntiCheat$getSecondaryStatusConsumer() {
        return this.secondaryStatusConsumer;
    }
}
