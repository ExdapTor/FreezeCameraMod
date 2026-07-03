package com.freezecameramod.keybinds;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;
import com.mojang.blaze3d.platform.InputConstants;
import java.lang.reflect.Constructor;

public class FreezeSlotKeyBind {
    public static KeyMapping lockSlotKey;
    public static boolean slotLocked = false;
    public static int lockedSlot = 0;

    public static void register() {
        lockSlotKey = KeyMappingHelper.registerKeyMapping(createCompatibleKeyBinding(
                "key.freezecameramod.lock_slot",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_0, // Código 48
                "key.categories.misc"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (lockSlotKey.consumeClick()) {
                slotLocked = !slotLocked;
//                if (slotLocked) {
//                    lockedSlot = ((PlayerInventoryAccessor) client.player.getInventory()).getSelectedSlot();
//                }
                client.player.sendSystemMessage(Component.translatable(slotLocked ? "message.freezecamera.frozenslot" : "message.freezecamera.unfrozenslot"));
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
