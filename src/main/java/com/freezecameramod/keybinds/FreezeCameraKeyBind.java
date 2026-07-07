package com.freezecameramod.keybinds;

import com.freezecameramod.FreezeCameraClient;
import com.freezecameramod.comun.CameraUtils;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import static com.freezecameramod.comun.FormatText.varDefault;

public class FreezeCameraKeyBind {
    private static KeyBinding freezeKey;

    public static void register() {
        freezeKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.freezecamera.toggle",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_WORLD_2,
                "category.freezecamera"
        ));

        // Activar/Desactivar movimiento de camara
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (freezeKey.wasPressed()) {
                FreezeCameraClient.setFrozen(!FreezeCameraClient.isFrozen());
                if (FreezeCameraClient.isFrozen() && client.player != null) {
                    Float yaw = CameraUtils.getYaw();
                    Float pitch = CameraUtils.getPitch();
                    Text yawText = varDefault(yaw.toString());
                    Text pitchText = varDefault(pitch.toString());
                    FreezeCameraClient.setYaw(yaw);
                    FreezeCameraClient.setPitch(pitch);
                    client.player.sendMessage(Text.translatable(
                            "message.freezecamera.frozen",
                            yawText,
                            pitchText), false);
                } else if (client.player != null) {
                    client.player.sendMessage(Text.translatable("message.freezecamera.unfrozen"), false);
                }
            }

            //SEGURIDAD: Si está congelado y consigue mover la camara
            if (FreezeCameraClient.isFrozen() && client.player != null) {
                client.player.setYaw(FreezeCameraClient.getFrozenYaw());
                client.player.setPitch(FreezeCameraClient.getFrozenPitch());
            }
        });
    }

}
