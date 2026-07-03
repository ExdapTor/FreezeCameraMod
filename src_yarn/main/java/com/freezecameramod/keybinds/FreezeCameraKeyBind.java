package com.freezecameramod.keybinds;

import com.freezecameramod.FreezeCameraClient;
import com.freezecameramod.comun.CameraUtils;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.world.GameRules;
import org.lwjgl.glfw.GLFW;

import java.lang.reflect.Constructor;

import static com.freezecameramod.comun.FormatText.varDefault;

public class FreezeCameraKeyBind {
    public static KeyBinding freezeKey;

    public static void register() {
        freezeKey = KeyBindingHelper.registerKeyBinding(createCompatibleKeyBinding(
                "key.freezecamera.toggle",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_WORLD_2,
                "key.categories.misc"
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

    /**
     * Crea un KeyBinding compatible con versiones que usan Category (enum)
     * o con las antiguas que usan String.
     */
    private static KeyBinding createCompatibleKeyBinding(String translationKey, InputUtil.Type type, int keyCode, String categoryName) {
        try {
            // Buscar la clase interna KeyBinding$Category si existe
            for (Constructor<?> constructor : KeyBinding.class.getConstructors()) {
                Class<?>[] params = constructor.getParameterTypes();
                // Si el último parámetro NO es String, asumimos que es el nuevo tipo de categoría
                if (params.length == 4 && !params[3].equals(String.class)) {
                    // Crear una instancia usando null como categoría o una categoría genérica
                    Object miscCategory = null;
                    try {
                        Class<?> categoryClass = params[3];
                        miscCategory = Enum.valueOf((Class<Enum>) categoryClass.asSubclass(Enum.class), "MISC");
                    } catch (Exception ignored) {}

                    return (KeyBinding) constructor.newInstance(translationKey, type, keyCode, miscCategory);
                }
            }

            // Si no se encontró ninguno compatible, usa el constructor antiguo con String
            Constructor<KeyBinding> oldConstructor = KeyBinding.class.getConstructor(String.class, InputUtil.Type.class, int.class, String.class);
            return oldConstructor.newInstance(translationKey, type, keyCode, categoryName);

        } catch (Exception e) {
            throw new RuntimeException("No se pudo crear el KeyBinding compatible (ajuste de categoría)", e);
        }
    }
}
