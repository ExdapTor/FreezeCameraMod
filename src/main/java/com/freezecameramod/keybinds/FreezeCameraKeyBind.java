package com.freezecameramod.keybinds;

import category.FREEZE;
import com.freezecameramod.FreezeCameraClient;
import com.freezecameramod.comun.CameraUtils;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.impl.client.keymapping.KeyMappingRegistryImpl;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

import static com.freezecameramod.comun.FormatText.varDefault;

public class FreezeCameraKeyBind {
    public static KeyMapping freezeKey;

    public static void register() {
        System.out.println("Ejecutandose el registro de la tecla para congelar");
        freezeKey = KeyMappingHelper.registerKeyMapping(
                new KeyMapping(
                        "key.freezecamera.toggle",
                        InputConstants.Type.KEYSYM,
                        GLFW.GLFW_KEY_WORLD_2,
                        FREEZE.FREEZE
                )
        );

        //TEMPORAL
        System.out.println(freezeKey.getName());
        System.out.println(Component.translatable(freezeKey.getName()).getString());
        //FIN TEMPORAL

        // Activar/Desactivar movimiento de camara
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (freezeKey.consumeClick()) {
                FreezeCameraClient.setFrozen(!FreezeCameraClient.isFrozen());

                if (FreezeCameraClient.isFrozen() && client.player != null) {
                    Float yaw = CameraUtils.getYaw();
                    Float pitch = CameraUtils.getPitch();

                    Component yawText = varDefault(yaw.toString());
                    Component pitchText = varDefault(pitch.toString());

                    FreezeCameraClient.setYaw(yaw);
                    FreezeCameraClient.setPitch(pitch);

                    client.player.sendSystemMessage(Component.translatable(
                            "message.freezecamera.frozen",
                            yawText,
                            pitchText));
                } else if (client.player != null) {
                    client.player.sendSystemMessage(
                            Component.translatable("message.freezecamera.unfrozen")
                    );
                }
            }

            //SEGURIDAD: Si está congelado y consigue mover la camara
            if (FreezeCameraClient.isFrozen() && client.player != null) {
                client.player.setYRot(FreezeCameraClient.getFrozenYaw());
                client.player.setXRot(FreezeCameraClient.getFrozenPitch());
            }
        });
    }
}
