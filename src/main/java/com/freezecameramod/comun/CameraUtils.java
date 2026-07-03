package com.freezecameramod.comun;

import com.freezecameramod.FreezeCameraClient;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public class CameraUtils {
    public static int setCameraAngles(float yaw, float pitch) {
        Minecraft client = Minecraft.getInstance();

        if (client.player != null) {
            client.player.setYRot(yaw);
            client.player.setXRot(pitch);
            FreezeCameraClient.setYaw(yaw);
            FreezeCameraClient.setPitch(pitch);
            FreezeCameraClient.setFrozen(true);
            client.player.sendSystemMessage(Component.translatable(
                    "message.freezecamera.setFrozenCamera",
                    yaw,
                    pitch));

            return 1;
        } else {
            return 0;
        }
    }
    public static float getYaw() {
        Minecraft client = Minecraft.getInstance();
        return client.player.getYRot();
    }
    public static float getPitch() {
        Minecraft client = Minecraft.getInstance();
        return client.player.getXRot();
    }
}
