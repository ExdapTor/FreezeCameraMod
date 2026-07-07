package com.freezecameramod.keybinds;

import category.FREEZE;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;
import com.mojang.blaze3d.platform.InputConstants;
import java.lang.reflect.Constructor;
import net.minecraft.client.Options;

import static com.freezecameramod.keybinds.FreezeCameraKeyBind.freezeKey;


public class FreezeSlotKeyBind {
    public static KeyMapping lockSlotKey;
    public static boolean slotLocked = false;
    public static int lockedSlot = 0;

    public static void register() {
        lockSlotKey = KeyMappingHelper.registerKeyMapping(
                new KeyMapping(
                        "key.freezecameramod.lock_slot",
                        InputConstants.Type.KEYSYM,
                        GLFW.GLFW_KEY_0,
                        FREEZE.FREEZE                )
        );

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (lockSlotKey.consumeClick()) {
                slotLocked = !slotLocked;
//                if (slotLocked) {
//                    lockedSlot = ((PlayerInventoryAccessor) client.player.getInventory()).getSelectedSlot();
//                }
                client.player.sendSystemMessage(Component.translatable(slotLocked ? "message.freezecamera.frozenslot" : "message.freezecamera.unfrozenslot"));
            }
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (lockSlotKey.consumeClick()) {
                slotLocked = !slotLocked;

                if (client.player != null) {
                    client.player.sendSystemMessage(
                            Component.translatable(
                                    slotLocked
                                            ? "message.freezecamera.frozenslot"
                                            : "message.freezecamera.unfrozenslot"
                            )
                    );
                }
            }
        });
    }
}
