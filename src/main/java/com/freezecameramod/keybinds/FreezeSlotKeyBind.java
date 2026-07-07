package com.freezecameramod.keybinds;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class FreezeSlotKeyBind {
    public static KeyBinding lockSlotKey;
    public static boolean slotLocked = false;
    public static int lockedSlot = 0;

    public static void register() {
        lockSlotKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.freezecameramod.lock_slot",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_0, // Código 48
                "category.freezecamera"
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
}
