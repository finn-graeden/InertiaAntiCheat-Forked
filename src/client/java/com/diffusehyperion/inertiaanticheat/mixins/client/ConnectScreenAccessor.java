package com.diffusehyperion.inertiaanticheat.mixins.client;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ConnectScreen.class)
public interface ConnectScreenAccessor {
    @Accessor("connection")
    Connection getConnection();

    @Accessor("parent")
    Screen getParent();

    @Invoker("updateStatus")
    void invokeUpdateStatus(Component status);
}
