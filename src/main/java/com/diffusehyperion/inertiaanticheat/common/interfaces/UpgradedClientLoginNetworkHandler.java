package com.diffusehyperion.inertiaanticheat.common.interfaces;

import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

public interface UpgradedClientLoginNetworkHandler {
    void inertiaAntiCheat$setSecondaryStatusConsumer(Consumer<Component> consumer);

    Consumer<Component> inertiaAntiCheat$getSecondaryStatusConsumer();
}
