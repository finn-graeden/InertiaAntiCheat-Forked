package com.diffusehyperion.inertiaanticheat.common.interfaces;

import com.mojang.authlib.GameProfile;
import net.minecraft.network.Connection;

public interface UpgradedServerLoginNetworkHandler {
    Connection inertiaAntiCheat$getConnection();
    GameProfile inertiaAntiCheat$getGameProfile();
}
