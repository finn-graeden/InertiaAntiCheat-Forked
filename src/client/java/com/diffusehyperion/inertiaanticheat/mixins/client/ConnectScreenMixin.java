package com.diffusehyperion.inertiaanticheat.mixins.client;

import com.diffusehyperion.inertiaanticheat.common.interfaces.UpgradedConnectScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(ConnectScreen.class)
public abstract class ConnectScreenMixin extends Screen implements UpgradedConnectScreen {
    @Unique
    private @Nullable Component secondaryStatus;

    protected ConnectScreenMixin(Component title) {
        super(title);
    }

    @Unique
    @Override
    public void inertiaAntiCheat$setSecondaryStatus(@Nullable Component secondaryStatus) {
        this.secondaryStatus = secondaryStatus;
    }

    @Inject(method = "render", at = @At(value = "TAIL"))
    private void render(GuiGraphics context, int mouseX, int mouseY, float deltaTicks, CallbackInfo ci) {
        if (Objects.nonNull(this.secondaryStatus)) {
            context.drawCenteredString(this.font, this.secondaryStatus, this.width / 2, this.height / 2 - 35, 16777215);
        }
    }
}
