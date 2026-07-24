package com.diffusehyperion.inertiaanticheat.server.networking.method.id.handlers;

import com.diffusehyperion.inertiaanticheat.server.networking.method.ReceiverHandler;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;
import net.minecraft.resources.Identifier;

import java.security.KeyPair;

public abstract class IdReceiverHandler extends ReceiverHandler {
    protected final IdValidationHandler validator;

    public IdReceiverHandler(KeyPair keyPair, Identifier modTransferID, ServerLoginPacketListenerImpl handler, IdValidationHandler validator) {
        super(keyPair, modTransferID, handler);
        this.validator = validator;
    }
}