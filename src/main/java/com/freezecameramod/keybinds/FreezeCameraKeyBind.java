package com.freezecameramod.keybinds;

import com.freezecameramod.FreezeCameraClient;
import com.freezecameramod.comun.CameraUtils;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

import java.lang.reflect.Constructor;

import static com.freezecameramod.comun.FormatText.varDefault;

public class FreezeCameraKeyBind {
    public static KeyMapping freezeKey;

    public static void register() {
        freezeKey = KeyBindingHelper.registerKeyBinding(createCompatibleKeyBinding(
                "key.freezecamera.toggle",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_WORLD_2,
                "key.categories.misc"
        ));

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
                    client.player.displayClientMessage(Component.translatable(
                            "message.freezecamera.frozen",
                            yawText,
                            pitchText), false);
                } else if (client.player != null) {
                    client.player.displayClientMessage(Component.translatable("message.freezecamera.unfrozen"), false);
                }
            }

            //SEGURIDAD: Si está congelado y consigue mover la camara
            if (FreezeCameraClient.isFrozen() && client.player != null) {
                client.player.setYRot(FreezeCameraClient.getFrozenYaw());
                client.player.setXRot(FreezeCameraClient.getFrozenPitch());
            }
        });
    }

    /**
     * Crea un KeyBinding compatible con versiones que usan Category (enum)
     * o con las antiguas que usan String.
     */
    private static KeyMapping createCompatibleKeyBinding(String translationKey, InputConstants.Type type, int keyCode, String categoryName) {
        try {
            // Buscar la clase interna KeyBinding$Category si existe
            for (Constructor<?> constructor : KeyMapping.class.getConstructors()) {
                Class<?>[] params = constructor.getParameterTypes();
                // Si el último parámetro NO es String, asumimos que es el nuevo tipo de categoría
                if (params.length == 4 && !params[3].equals(String.class)) {
                    // Crear una instancia usando null como categoría o una categoría genérica
                    Object miscCategory = null;
                    try {
                        Class<?> categoryClass = params[3];
                        miscCategory = Enum.valueOf((Class<Enum>) categoryClass.asSubclass(Enum.class), "MISC");
                    } catch (Exception ignored) {}

                    return (KeyMapping) constructor.newInstance(translationKey, type, keyCode, miscCategory);
                }
            }

            // Si no se encontró ninguno compatible, usa el constructor antiguo con String
            Constructor<KeyMapping> oldConstructor = KeyMapping.class.getConstructor(String.class, InputConstants.Type.class, int.class, String.class);
            return oldConstructor.newInstance(translationKey, type, keyCode, categoryName);

        } catch (Exception e) {
            throw new RuntimeException("No se pudo crear el KeyBinding compatible (ajuste de categoría)", e);
        }
    }
}
