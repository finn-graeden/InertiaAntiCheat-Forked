package com.diffusehyperion.inertiaanticheat.mixins.client;

import com.diffusehyperion.inertiaanticheat.common.interfaces.UpgradedServerInfo;
import com.diffusehyperion.inertiaanticheat.common.util.AnticheatDetails;
import com.diffusehyperion.inertiaanticheat.common.util.GroupAnticheatDetails;
import com.diffusehyperion.inertiaanticheat.common.util.IndividualAnticheatDetails;
import com.diffusehyperion.inertiaanticheat.common.util.ValidationMethod;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.multiplayer.ServerSelectionList;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

import static com.diffusehyperion.inertiaanticheat.common.util.InertiaAntiCheatConstants.MODID;

@Mixin(ServerSelectionList.OnlineServerEntry.class)
public abstract class MultiplayerServerListWidgetServerEntryMixin extends ServerSelectionList.Entry {
    @Shadow @Final private ServerData serverData;

    @Unique
    private static final Identifier ICON_ENABLED = Identifier.fromNamespaceAndPath(MODID, "textures/gui/enabled.png");
    @Unique
    private static final Identifier ICON_WHITELIST = Identifier.fromNamespaceAndPath(MODID, "textures/gui/whitelist.png");
    @Unique
    private static final Identifier ICON_BLACKLIST = Identifier.fromNamespaceAndPath(MODID, "textures/gui/blacklist.png");
    @Unique
    private static final Identifier ICON_MODPACK = Identifier.fromNamespaceAndPath(MODID, "textures/gui/modpack.png");

    @Inject(
            method = "renderContent",
            at = @At(value = "TAIL")
    )
    private void render(GuiGraphics context, int mouseX, int mouseY, boolean hovered, float deltaTicks, CallbackInfo ci) {
        UpgradedServerInfo upgradedServerInfo = ((UpgradedServerInfo) serverData);
        Boolean installed = upgradedServerInfo.inertiaAntiCheat$isInertiaInstalled();
        AnticheatDetails anticheatDetails = upgradedServerInfo.inertiaAntiCheat$getAnticheatDetails();
        if (Objects.nonNull(installed) && installed.equals(true) && anticheatDetails.showInstalled()) {
            int iconX = this.getContentRight() - 10 - 5;
            int iconY = this.getContentY() + 8 + 2;
            context.blit(RenderPipelines.GUI_TEXTURED, ICON_ENABLED, iconX, iconY, 0.0f, 0.0f, 10, 10, 10, 10);
            if (mouseX > iconX && mouseX < iconX + 10 && mouseY > iconY && mouseY < iconY + 10) {
                context.setTooltipForNextFrame(Component.nullToEmpty("InertiaAntiCheat installed"), mouseX, mouseY);
            }
        }
        if (Objects.nonNull(anticheatDetails)) {
            if (anticheatDetails.getValidationMethod() == ValidationMethod.INDIVIDUAL) {
                IndividualAnticheatDetails details = (IndividualAnticheatDetails) anticheatDetails;

                if ((details.getWhitelistedMods().size() == 1 && !Objects.equals(details.getWhitelistedMods().getFirst(), "")) || details.getWhitelistedMods().size() >= 2) {
                    int whitelistIconX = this.getContentRight() - 10 - 5 - 10;
                    int whitelistIconY = this.getContentY() + 8 + 2 + 10 + 2;
                    context.blit(RenderPipelines.GUI_TEXTURED, ICON_WHITELIST, whitelistIconX, whitelistIconY, 0.0f, 0.0f, 10, 10, 10, 10);
                    if (mouseX > whitelistIconX && mouseX < whitelistIconX + 10 && mouseY > whitelistIconY && mouseY < whitelistIconY + 10) {
                        context.setTooltipForNextFrame(details.getWhitelistedMods().stream().map(Component::nullToEmpty).map(Component::getVisualOrderText).toList(), mouseX, mouseY);
                    }
                }

                if ((details.getBlacklistedMods().size() == 1 && !Objects.equals(details.getBlacklistedMods().getFirst(), "")) || details.getBlacklistedMods().size() >= 2) {
                    int blacklistIconX = this.getContentRight() - 10 - 5;
                    int blacklistIconY = this.getContentY() + 8 + 2 + 10 + 2;
                    context.blit(RenderPipelines.GUI_TEXTURED, ICON_BLACKLIST, blacklistIconX, blacklistIconY, 0.0f, 0.0f, 10, 10, 10, 10);
                    if (mouseX > blacklistIconX && mouseX < blacklistIconX + 10 && mouseY > blacklistIconY && mouseY < blacklistIconY + 10) {
                        context.setTooltipForNextFrame(details.getBlacklistedMods().stream().map(Component::nullToEmpty).map(Component::getVisualOrderText).toList(), mouseX, mouseY);
                    }
                }
            } else {
                GroupAnticheatDetails details = (GroupAnticheatDetails) anticheatDetails;
                if ((details.getModpackDetails().size() == 1 && !Objects.equals(details.getModpackDetails().getFirst(), "")) || details.getModpackDetails().size() >= 2) {
                    int blacklistIconX = this.getContentRight() - 10 - 5;
                    int blacklistIconY = this.getContentY() + 8 + 2 + 10 + 2;
                    context.blit(RenderPipelines.GUI_TEXTURED, ICON_MODPACK, blacklistIconX, blacklistIconY, 0.0f, 0.0f, 10, 10, 10, 10);
                    if (mouseX > blacklistIconX && mouseX < blacklistIconX + 10 && mouseY > blacklistIconY && mouseY < blacklistIconY + 10) {
                        context.setTooltipForNextFrame(details.getModpackDetails().stream().map(Component::nullToEmpty).map(Component::getVisualOrderText).toList(), mouseX, mouseY);
                    }
                }
            }
        }
    }
}
