package com.diffusehyperion.inertiaanticheat.common.util;

import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InertiaAntiCheatConstants {
    public static final Identifier ANTICHEAT_DETAILS_ID = Identifier.fromNamespaceAndPath("inertiaanticheat", "anticheat_details");

    public static final Identifier CHECK_CONNECTION = Identifier.fromNamespaceAndPath("inertiaanticheat", "check_connection");
    public static final Identifier INITIATE_E2EE = Identifier.fromNamespaceAndPath("inertiaanticheat", "initiate_e2ee");
    public static final Identifier SET_ADAPTOR = Identifier.fromNamespaceAndPath("inertiaanticheat", "set_adapter");
    public static final Identifier SEND_MOD = Identifier.fromNamespaceAndPath("inertiaanticheat", "send_mod");

    public static final Logger MODLOGGER = LoggerFactory.getLogger("InertiaAntiCheat");
    public static final String MODID = "inertiaanticheat";

    public static final long CURRENT_SERVER_CONFIG_VERSION = 9;
    public static final long CURRENT_CLIENT_CONFIG_VERSION = 2;
}
