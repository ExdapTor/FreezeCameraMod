package com.freezecameramod.keybinds;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.lang.reflect.Constructor;

public class FreezeSlotKeyBind {
    public static KeyBinding lockSlotKey;
    public static boolean slotLocked = false;
    public static int lockedSlot = 0;

    public static void register() {
        lockSlotKey = KeyBindingHelper.registerKeyBinding(createCompatibleKeyBinding(
                "key.freezecameramod.lock_slot",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_0, // Código 48
                "key.categories.misc"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (lockSlotKey.wasPressed()) {
                slotLocked = !slotLocked;
//                if (slotLocked) {
//                    lockedSlot = ((PlayerInventoryAccessor) client.player.getInventory()).getSelectedSlot();
//                }
                client.player.sendMessage(Text.translatable(slotLocked ? "message.freezecamera.frozenslot" : "message.freezecamera.unfrozenslot"), true);
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
