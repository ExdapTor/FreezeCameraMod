package com.freezecameramod.mixin;

import com.freezecameramod.keybinds.FreezeSlotKeyBind;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.freezecameramod.FreezeCameraClient;

@Mixin(MouseHandler.class)
public class MouseMixin {
    // Cancela el movimiento de cámara si está congelada
    @Inject(method = "turnPlayer", at = @At("HEAD"), cancellable = true)
    private void freezeCameraOnMouseMove(CallbackInfo ci) {
        if (FreezeCameraClient.isFrozen()) {
            ci.cancel(); // Cancela el movimiento de cámara
        }
    }

    // Cancela el scroll del ratón si el slot está bloqueado
    @Inject(method = "onScroll", at = @At("HEAD"), cancellable = true)
    private void blockScrollIfSlotLocked(long window, double horizontal, double vertical, CallbackInfo ci) {
        if (FreezeSlotKeyBind.slotLocked) {
            ci.cancel();
        }
    }
}
